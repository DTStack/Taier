package org.apache.parquet.hadoop;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.parquet.HadoopReadOptions;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.bytes.ByteBufferInputStream;
import org.apache.parquet.bytes.BytesInput;
import org.apache.parquet.bytes.BytesUtils;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.Encoding;
import org.apache.parquet.column.page.DataPage;
import org.apache.parquet.column.page.DataPageV1;
import org.apache.parquet.column.page.DataPageV2;
import org.apache.parquet.column.page.DictionaryPage;
import org.apache.parquet.column.page.DictionaryPageReadStore;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.compression.CompressionCodecFactory;
import org.apache.parquet.filter2.compat.FilterCompat;
import org.apache.parquet.filter2.compat.RowGroupFilter;
import org.apache.parquet.format.DataPageHeader;
import org.apache.parquet.format.DataPageHeaderV2;
import org.apache.parquet.format.DictionaryPageHeader;
import org.apache.parquet.format.PageHeader;
import org.apache.parquet.format.PageType;
import org.apache.parquet.format.Util;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.ColumnChunkMetaData;
import org.apache.parquet.hadoop.metadata.ColumnPath;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HiddenFileFilter;
import org.apache.parquet.hadoop.util.counters.BenchmarkCounter;
import org.apache.parquet.internal.column.columnindex.ColumnIndex;
import org.apache.parquet.internal.column.columnindex.OffsetIndex;
import org.apache.parquet.internal.filter2.columnindex.ColumnIndexFilter;
import org.apache.parquet.internal.filter2.columnindex.ColumnIndexStore;
import org.apache.parquet.internal.filter2.columnindex.RowRanges;
import org.apache.parquet.internal.hadoop.metadata.IndexReference;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.ParquetDecodingException;
import org.apache.parquet.io.SeekableInputStream;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.yetus.audience.InterfaceAudience.Private;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParquetFileReader implements Closeable {
  private static final Logger LOG = LoggerFactory.getLogger(ParquetFileReader.class);
  
  public static String PARQUET_READ_PARALLELISM = "parquet.metadata.read.parallelism";
  
  private final ParquetMetadataConverter converter;
  
  private final InputFile file;
  
  private final SeekableInputStream f;
  
  private final ParquetReadOptions options;
  
  @Deprecated
  public static List<Footer> readAllFootersInParallelUsingSummaryFiles(Configuration configuration, List<FileStatus> partFiles) throws IOException {
    return readAllFootersInParallelUsingSummaryFiles(configuration, partFiles, false);
  }
  
  private static ParquetMetadataConverter.MetadataFilter filter(boolean skipRowGroups) {
    return skipRowGroups ? ParquetMetadataConverter.SKIP_ROW_GROUPS : ParquetMetadataConverter.NO_FILTER;
  }
  
  @Deprecated
  public static List<Footer> readAllFootersInParallelUsingSummaryFiles(final Configuration configuration, final Collection<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
    Set<Path> parents = new HashSet<>();
    for (FileStatus part : partFiles)
      parents.add(part.getPath().getParent()); 
    List<Callable<Map<Path, Footer>>> summaries = new ArrayList<>();
    for (Path path : parents) {
      summaries.add(new Callable<Map<Path, Footer>>() {
            public Map<Path, Footer> call() throws Exception {
              ParquetMetadata mergedMetadata = ParquetFileReader.readSummaryMetadata(configuration, path, skipRowGroups);
              if (mergedMetadata != null) {
                List<Footer> footers;
                if (skipRowGroups) {
                  footers = new ArrayList<>();
                  for (FileStatus f : partFiles)
                    footers.add(new Footer(f.getPath(), mergedMetadata)); 
                } else {
                  footers = ParquetFileReader.footersFromSummaryFile(path, mergedMetadata);
                } 
                Map<Path, Footer> map = new HashMap<>();
                for (Footer footer : footers) {
                  footer = new Footer(new Path(path, footer.getFile().getName()), footer.getParquetMetadata());
                  map.put(footer.getFile(), footer);
                } 
                return map;
              } 
              return Collections.emptyMap();
            }
          });
    } 
    Map<Path, Footer> cache = new HashMap<>();
    try {
      List<Map<Path, Footer>> footersFromSummaries = runAllInParallel(configuration.getInt(PARQUET_READ_PARALLELISM, 5), summaries);
      for (Map<Path, Footer> footers : footersFromSummaries)
        cache.putAll(footers); 
    } catch (ExecutionException e) {
      throw new IOException("Error reading summaries", e);
    } 
    List<Footer> result = new ArrayList<>(partFiles.size());
    List<FileStatus> toRead = new ArrayList<>();
    for (FileStatus part : partFiles) {
      Footer f = cache.get(part.getPath());
      if (f != null) {
        result.add(f);
        continue;
      } 
      toRead.add(part);
    } 
    if (toRead.size() > 0) {
      LOG.info("reading another {} footers", Integer.valueOf(toRead.size()));
      result.addAll(readAllFootersInParallel(configuration, toRead, skipRowGroups));
    } 
    return result;
  }
  
  private static <T> List<T> runAllInParallel(int parallelism, List<Callable<T>> toRun) throws ExecutionException {
    LOG.info("Initiating action with parallelism: {}", Integer.valueOf(parallelism));
    ExecutorService threadPool = Executors.newFixedThreadPool(parallelism);
    try {
      List<Future<T>> futures = new ArrayList<>();
      for (Callable<T> callable : toRun)
        futures.add(threadPool.submit(callable)); 
      List<T> result = new ArrayList<>(toRun.size());
      for (Future<T> future : futures) {
        try {
          result.add(future.get());
        } catch (InterruptedException e) {
          throw new RuntimeException("The thread was interrupted", e);
        } 
      } 
      return result;
    } finally {
      threadPool.shutdownNow();
    } 
  }
  
  @Deprecated
  public static List<Footer> readAllFootersInParallel(Configuration configuration, List<FileStatus> partFiles) throws IOException {
    return readAllFootersInParallel(configuration, partFiles, false);
  }
  
  @Deprecated
  public static List<Footer> readAllFootersInParallel(final Configuration configuration, List<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
    List<Callable<Footer>> footers = new ArrayList<>();
    for (FileStatus currentFile : partFiles) {
      footers.add(new Callable<Footer>() {
            public Footer call() throws Exception {
              try {
                return new Footer(currentFile.getPath(), ParquetFileReader.readFooter(configuration, currentFile, ParquetFileReader.filter(skipRowGroups)));
              } catch (IOException e) {
                throw new IOException("Could not read footer for file " + currentFile, e);
              } 
            }
          });
    } 
    try {
      return runAllInParallel(configuration.getInt(PARQUET_READ_PARALLELISM, 5), footers);
    } catch (ExecutionException e) {
      throw new IOException("Could not read footer: " + e.getMessage(), e.getCause());
    } 
  }
  
  @Deprecated
  public static List<Footer> readAllFootersInParallel(Configuration configuration, FileStatus fileStatus, boolean skipRowGroups) throws IOException {
    List<FileStatus> statuses = listFiles(configuration, fileStatus);
    return readAllFootersInParallel(configuration, statuses, skipRowGroups);
  }
  
  @Deprecated
  public static List<Footer> readAllFootersInParallel(Configuration configuration, FileStatus fileStatus) throws IOException {
    return readAllFootersInParallel(configuration, fileStatus, false);
  }
  
  @Deprecated
  public static List<Footer> readFooters(Configuration configuration, Path path) throws IOException {
    return readFooters(configuration, status(configuration, path));
  }
  
  private static FileStatus status(Configuration configuration, Path path) throws IOException {
    return path.getFileSystem(configuration).getFileStatus(path);
  }
  
  @Deprecated
  public static List<Footer> readFooters(Configuration configuration, FileStatus pathStatus) throws IOException {
    return readFooters(configuration, pathStatus, false);
  }
  
  @Deprecated
  public static List<Footer> readFooters(Configuration configuration, FileStatus pathStatus, boolean skipRowGroups) throws IOException {
    List<FileStatus> files = listFiles(configuration, pathStatus);
    return readAllFootersInParallelUsingSummaryFiles(configuration, files, skipRowGroups);
  }
  
  private static List<FileStatus> listFiles(Configuration conf, FileStatus fileStatus) throws IOException {
    if (fileStatus.isDir()) {
      FileSystem fs = fileStatus.getPath().getFileSystem(conf);
      FileStatus[] list = fs.listStatus(fileStatus.getPath(), (PathFilter)HiddenFileFilter.INSTANCE);
      List<FileStatus> result = new ArrayList<>();
      for (FileStatus sub : list)
        result.addAll(listFiles(conf, sub)); 
      return result;
    } 
    return Arrays.asList(new FileStatus[] { fileStatus });
  }
  
  @Deprecated
  public static List<Footer> readSummaryFile(Configuration configuration, FileStatus summaryStatus) throws IOException {
    Path parent = summaryStatus.getPath().getParent();
    ParquetMetadata mergedFooters = readFooter(configuration, summaryStatus, filter(false));
    return footersFromSummaryFile(parent, mergedFooters);
  }
  
  static ParquetMetadata readSummaryMetadata(Configuration configuration, Path basePath, boolean skipRowGroups) throws IOException {
    /*Path metadataFile = new Path(basePath, "_metadata");
    Path commonMetaDataFile = new Path(basePath, "_common_metadata");
    FileSystem fileSystem = basePath.getFileSystem(configuration);
    if (skipRowGroups && fileSystem.exists(commonMetaDataFile)) {
      LOG.info("reading summary file: {}", commonMetaDataFile);
      return readFooter(configuration, commonMetaDataFile, filter(skipRowGroups));
    } 
    if (fileSystem.exists(metadataFile)) {
      LOG.info("reading summary file: {}", metadataFile);
      return readFooter(configuration, metadataFile, filter(skipRowGroups));
    }*/
    return null;
  }
  
  static List<Footer> footersFromSummaryFile(Path parent, ParquetMetadata mergedFooters) {
    Map<Path, ParquetMetadata> footers = new HashMap<>();
    List<BlockMetaData> blocks = mergedFooters.getBlocks();
    for (BlockMetaData block : blocks) {
      String path = block.getPath();
      Path fullPath = new Path(parent, path);
      ParquetMetadata current = footers.get(fullPath);
      if (current == null) {
        current = new ParquetMetadata(mergedFooters.getFileMetaData(), new ArrayList());
        footers.put(fullPath, current);
      } 
      current.getBlocks().add(block);
    } 
    List<Footer> result = new ArrayList<>();
    for (Map.Entry<Path, ParquetMetadata> entry : footers.entrySet())
      result.add(new Footer(entry.getKey(), entry.getValue())); 
    return result;
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(Configuration configuration, Path file) throws IOException {
    return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
  }
  
  public static ParquetMetadata readFooter(Configuration configuration, Path file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    return readFooter((InputFile)HadoopInputFile.fromPath(file, configuration), filter);
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(Configuration configuration, FileStatus file) throws IOException {
    return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(Configuration configuration, FileStatus file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    return readFooter((InputFile)HadoopInputFile.fromStatus(file, configuration), filter);
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(InputFile file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    ParquetReadOptions options;
    if (file instanceof HadoopInputFile) {
      options = HadoopReadOptions.builder(((HadoopInputFile)file).getConfiguration()).withMetadataFilter(filter).build();
    } else {
      options = ParquetReadOptions.builder().withMetadataFilter(filter).build();
    } 
    try (SeekableInputStream in = file.newStream()) {
      return readFooter(file, options, in);
    } 
  }
  
  private static final ParquetMetadata readFooter(InputFile file, ParquetReadOptions options, SeekableInputStream f) throws IOException {
    ParquetMetadataConverter converter = new ParquetMetadataConverter(options);
    return readFooter(file, options, f, converter);
  }
  
  private static final ParquetMetadata readFooter(InputFile file, ParquetReadOptions options, SeekableInputStream f, ParquetMetadataConverter converter) throws IOException {
    long fileLen = file.getLength();
    String filePath = file.toString();
    LOG.debug("File length {}", Long.valueOf(fileLen));
    int FOOTER_LENGTH_SIZE = 4;
    if (fileLen < (ParquetFileWriter.MAGIC.length + FOOTER_LENGTH_SIZE + ParquetFileWriter.MAGIC.length))
      throw new RuntimeException(filePath + " is not a Parquet file (too small length: " + fileLen + ")"); 
    long footerLengthIndex = fileLen - FOOTER_LENGTH_SIZE - ParquetFileWriter.MAGIC.length;
    LOG.debug("reading footer index at {}", Long.valueOf(footerLengthIndex));
    f.seek(footerLengthIndex);
    int footerLength = BytesUtils.readIntLittleEndian((InputStream)f);
    byte[] magic = new byte[ParquetFileWriter.MAGIC.length];
    f.readFully(magic);
    if (!Arrays.equals(ParquetFileWriter.MAGIC, magic))
      throw new RuntimeException(filePath + " is not a Parquet file. expected magic number at tail " + Arrays.toString(ParquetFileWriter.MAGIC) + " but found " + Arrays.toString(magic)); 
    long footerIndex = footerLengthIndex - footerLength;
    LOG.debug("read footer length: {}, footer index: {}", Integer.valueOf(footerLength), Long.valueOf(footerIndex));
    if (footerIndex < ParquetFileWriter.MAGIC.length || footerIndex >= footerLengthIndex)
      throw new RuntimeException("corrupted file: the footer index is not within the file: " + footerIndex); 
    f.seek(footerIndex);
    return converter.readParquetMetadata((InputStream)f, options.getMetadataFilter());
  }
  
  @Deprecated
  public static ParquetFileReader open(Configuration conf, Path file) throws IOException {
    return new ParquetFileReader((InputFile)HadoopInputFile.fromPath(file, conf), 
        HadoopReadOptions.builder(conf).build());
  }
  
  @Deprecated
  public static ParquetFileReader open(Configuration conf, Path file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    return open((InputFile)HadoopInputFile.fromPath(file, conf), 
        HadoopReadOptions.builder(conf).withMetadataFilter(filter).build());
  }
  
  @Deprecated
  public static ParquetFileReader open(Configuration conf, Path file, ParquetMetadata footer) throws IOException {
    return new ParquetFileReader(conf, file, footer);
  }
  
  public static ParquetFileReader open(InputFile file) throws IOException {
    return new ParquetFileReader(file, ParquetReadOptions.builder().build());
  }
  
  public static ParquetFileReader open(InputFile file, ParquetReadOptions options) throws IOException {
    return new ParquetFileReader(file, options);
  }
  
  private final Map<ColumnPath, ColumnDescriptor> paths = new HashMap<>();
  
  private final FileMetaData fileMetaData;
  
  private final List<BlockMetaData> blocks;
  
  private final List<ColumnIndexStore> blockIndexStores;
  
  private final List<RowRanges> blockRowRanges;
  
  private ParquetMetadata footer;
  
  private int currentBlock = 0;
  
  private ColumnChunkPageReadStore currentRowGroup = null;
  
  private DictionaryPageReader nextDictionaryReader = null;
  
  @Deprecated
  public ParquetFileReader(Configuration configuration, Path filePath, List<BlockMetaData> blocks, List<ColumnDescriptor> columns) throws IOException {
    this(configuration, null, filePath, blocks, columns);
  }
  
  @Deprecated
  public ParquetFileReader(Configuration configuration, FileMetaData fileMetaData, Path filePath, List<BlockMetaData> blocks, List<ColumnDescriptor> columns) throws IOException {
    this.converter = new ParquetMetadataConverter(configuration);
    this.file = (InputFile)HadoopInputFile.fromPath(filePath, configuration);
    this.fileMetaData = fileMetaData;
    this.f = this.file.newStream();
    this.options = HadoopReadOptions.builder(configuration).build();
    this.blocks = filterRowGroups(blocks);
    this.blockIndexStores = listWithNulls(this.blocks.size());
    this.blockRowRanges = listWithNulls(this.blocks.size());
    for (ColumnDescriptor col : columns)
      this.paths.put(ColumnPath.get(col.getPath()), col); 
  }
  
  @Deprecated
  public ParquetFileReader(Configuration conf, Path file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    this((InputFile)HadoopInputFile.fromPath(file, conf), 
        HadoopReadOptions.builder(conf).withMetadataFilter(filter).build());
  }
  
  @Deprecated
  public ParquetFileReader(Configuration conf, Path file, ParquetMetadata footer) throws IOException {
    this.converter = new ParquetMetadataConverter(conf);
    this.file = (InputFile)HadoopInputFile.fromPath(file, conf);
    this.f = this.file.newStream();
    this.options = HadoopReadOptions.builder(conf).build();
    this.footer = footer;
    this.fileMetaData = footer.getFileMetaData();
    this.blocks = filterRowGroups(footer.getBlocks());
    this.blockIndexStores = listWithNulls(this.blocks.size());
    this.blockRowRanges = listWithNulls(this.blocks.size());
    for (ColumnDescriptor col : footer.getFileMetaData().getSchema().getColumns())
      this.paths.put(ColumnPath.get(col.getPath()), col); 
  }
  
  public ParquetFileReader(InputFile file, ParquetReadOptions options) throws IOException {
    this.converter = new ParquetMetadataConverter(options);
    this.file = file;
    this.f = file.newStream();
    this.options = options;
    try {
      this.footer = readFooter(file, options, this.f, this.converter);
    } catch (Exception e) {
      this.f.close();
      throw e;
    } 
    this.fileMetaData = this.footer.getFileMetaData();
    this.blocks = filterRowGroups(this.footer.getBlocks());
    this.blockIndexStores = listWithNulls(this.blocks.size());
    this.blockRowRanges = listWithNulls(this.blocks.size());
    for (ColumnDescriptor col : this.footer.getFileMetaData().getSchema().getColumns())
      this.paths.put(ColumnPath.get(col.getPath()), col); 
  }
  
  private static <T> List<T> listWithNulls(int size) {
    return (List<T>)Stream.generate(() -> null).limit(size).collect(Collectors.toCollection(ArrayList::new));
  }
  
  public ParquetMetadata getFooter() {
    if (this.footer == null)
      try {
        this.footer = readFooter(this.file, this.options, this.f, this.converter);
      } catch (IOException e) {
        throw new ParquetDecodingException("Unable to read file footer", e);
      }  
    return this.footer;
  }
  
  public FileMetaData getFileMetaData() {
    if (this.fileMetaData != null)
      return this.fileMetaData; 
    return getFooter().getFileMetaData();
  }
  
  public long getRecordCount() {
    long total = 0L;
    for (BlockMetaData block : this.blocks)
      total += block.getRowCount(); 
    return total;
  }
  
  long getFilteredRecordCount() {
    if (!this.options.useColumnIndexFilter())
      return getRecordCount(); 
    long total = 0L;
    for (int i = 0, n = this.blocks.size(); i < n; i++)
      total += getRowRanges(i).rowCount(); 
    return total;
  }
  
  @Deprecated
  public Path getPath() {
    return new Path(this.file.toString());
  }
  
  public String getFile() {
    return this.file.toString();
  }
  
  private List<BlockMetaData> filterRowGroups(List<BlockMetaData> blocks) throws IOException {
    List<RowGroupFilter.FilterLevel> levels = new ArrayList<>();
    if (this.options.useStatsFilter())
      levels.add(RowGroupFilter.FilterLevel.STATISTICS); 
    if (this.options.useDictionaryFilter())
      levels.add(RowGroupFilter.FilterLevel.DICTIONARY); 
    FilterCompat.Filter recordFilter = this.options.getRecordFilter();
    if (recordFilter != null)
      return RowGroupFilter.filterRowGroups(levels, recordFilter, blocks, this); 
    return blocks;
  }
  
  public List<BlockMetaData> getRowGroups() {
    return this.blocks;
  }
  
  public void setRequestedSchema(MessageType projection) {
    this.paths.clear();
    for (ColumnDescriptor col : projection.getColumns())
      this.paths.put(ColumnPath.get(col.getPath()), col); 
  }
  
  public void appendTo(ParquetFileWriter writer) throws IOException {
    writer.appendRowGroups(this.f, this.blocks, true);
  }
  
  public PageReadStore readNextRowGroup() throws IOException {
    if (this.currentBlock == this.blocks.size())
      return null; 
    BlockMetaData block = this.blocks.get(this.currentBlock);
    if (block.getRowCount() == 0L)
      throw new RuntimeException("Illegal row group of 0 rows"); 
    this.currentRowGroup = new ColumnChunkPageReadStore(block.getRowCount());
    List<ConsecutivePartList> allParts = new ArrayList<>();
    ConsecutivePartList currentParts = null;
    for (ColumnChunkMetaData mc : block.getColumns()) {
      ColumnPath pathKey = mc.getPath();
      BenchmarkCounter.incrementTotalBytes(mc.getTotalSize());
      ColumnDescriptor columnDescriptor = this.paths.get(pathKey);
      if (columnDescriptor != null) {
        long startingPos = mc.getStartingPos();
        if (currentParts == null || currentParts.endPos() != startingPos) {
          currentParts = new ConsecutivePartList(startingPos);
          allParts.add(currentParts);
        } 
        currentParts.addChunk(new ChunkDescriptor(columnDescriptor, mc, startingPos, (int)mc.getTotalSize()));
      } 
    } 
    ChunkListBuilder builder = new ChunkListBuilder();
    for (ConsecutivePartList consecutiveChunks : allParts)
      consecutiveChunks.readAll(this.f, builder); 
    for (Chunk chunk : builder.build())
      this.currentRowGroup.addColumn(chunk.descriptor.col, chunk.readAllPages()); 
    if (this.nextDictionaryReader != null)
      this.nextDictionaryReader.setRowGroup(this.currentRowGroup); 
    advanceToNextBlock();
    return this.currentRowGroup;
  }
  
  public PageReadStore readNextFilteredRowGroup() throws IOException {
    if (this.currentBlock == this.blocks.size())
      return null; 
    if (!this.options.useColumnIndexFilter())
      return readNextRowGroup(); 
    BlockMetaData block = this.blocks.get(this.currentBlock);
    if (block.getRowCount() == 0L)
      throw new RuntimeException("Illegal row group of 0 rows"); 
    ColumnIndexStore ciStore = getColumnIndexStore(this.currentBlock);
    RowRanges rowRanges = getRowRanges(this.currentBlock);
    long rowCount = rowRanges.rowCount();
    if (rowCount == 0L) {
      advanceToNextBlock();
      return readNextFilteredRowGroup();
    } 
    if (rowCount == block.getRowCount())
      return readNextRowGroup(); 
    this.currentRowGroup = new ColumnChunkPageReadStore(rowRanges);
    ChunkListBuilder builder = new ChunkListBuilder();
    List<ConsecutivePartList> allParts = new ArrayList<>();
    ConsecutivePartList currentParts = null;
    for (ColumnChunkMetaData mc : block.getColumns()) {
      ColumnPath pathKey = mc.getPath();
      ColumnDescriptor columnDescriptor = this.paths.get(pathKey);
      if (columnDescriptor != null) {
        OffsetIndex offsetIndex = ciStore.getOffsetIndex(mc.getPath());
        OffsetIndex filteredOffsetIndex = ColumnIndexFilterUtils.filterOffsetIndex(offsetIndex, rowRanges, block
            .getRowCount());
        for (ColumnIndexFilterUtils.OffsetRange range : ColumnIndexFilterUtils.calculateOffsetRanges(filteredOffsetIndex, mc, offsetIndex.getOffset(0))) {
          BenchmarkCounter.incrementTotalBytes(range.getLength());
          long startingPos = range.getOffset();
          if (currentParts == null || currentParts.endPos() != startingPos) {
            currentParts = new ConsecutivePartList(startingPos);
            allParts.add(currentParts);
          } 
          ChunkDescriptor chunkDescriptor = new ChunkDescriptor(columnDescriptor, mc, startingPos, (int)range.getLength());
          currentParts.addChunk(chunkDescriptor);
          builder.setOffsetIndex(chunkDescriptor, filteredOffsetIndex);
        } 
      } 
    } 
    for (ConsecutivePartList consecutiveChunks : allParts)
      consecutiveChunks.readAll(this.f, builder); 
    for (Chunk chunk : builder.build())
      this.currentRowGroup.addColumn(chunk.descriptor.col, chunk.readAllPages()); 
    if (this.nextDictionaryReader != null)
      this.nextDictionaryReader.setRowGroup(this.currentRowGroup); 
    advanceToNextBlock();
    return this.currentRowGroup;
  }
  
  private ColumnIndexStore getColumnIndexStore(int blockIndex) {
    ColumnIndexStore ciStore = this.blockIndexStores.get(blockIndex);
    if (ciStore == null) {
      ciStore = ColumnIndexStoreImpl.create(this, this.blocks.get(blockIndex), this.paths.keySet());
      this.blockIndexStores.set(blockIndex, ciStore);
    } 
    return ciStore;
  }
  
  private RowRanges getRowRanges(int blockIndex) {
    RowRanges rowRanges = this.blockRowRanges.get(blockIndex);
    if (rowRanges == null) {
      rowRanges = ColumnIndexFilter.calculateRowRanges(this.options.getRecordFilter(), getColumnIndexStore(blockIndex), this.paths
          .keySet(), ((BlockMetaData)this.blocks.get(blockIndex)).getRowCount());
      this.blockRowRanges.set(blockIndex, rowRanges);
    } 
    return rowRanges;
  }
  
  public boolean skipNextRowGroup() {
    return advanceToNextBlock();
  }
  
  private boolean advanceToNextBlock() {
    if (this.currentBlock == this.blocks.size())
      return false; 
    this.currentBlock++;
    this.nextDictionaryReader = null;
    return true;
  }
  
  public DictionaryPageReadStore getNextDictionaryReader() {
    if (this.nextDictionaryReader == null && this.currentBlock < this.blocks.size())
      this.nextDictionaryReader = getDictionaryReader(this.blocks.get(this.currentBlock)); 
    return this.nextDictionaryReader;
  }
  
  public DictionaryPageReader getDictionaryReader(BlockMetaData block) {
    return new DictionaryPageReader(this, block);
  }
  
  DictionaryPage readDictionary(ColumnChunkMetaData meta) throws IOException {
    if (!meta.getEncodings().contains(Encoding.PLAIN_DICTIONARY) && 
      !meta.getEncodings().contains(Encoding.RLE_DICTIONARY))
      return null; 
    if (this.f.getPos() != meta.getStartingPos())
      this.f.seek(meta.getStartingPos()); 
    PageHeader pageHeader = Util.readPageHeader((InputStream)this.f);
    if (!pageHeader.isSetDictionary_page_header())
      return null; 
    DictionaryPage compressedPage = readCompressedDictionary(pageHeader, this.f);
    CompressionCodecFactory.BytesInputDecompressor decompressor = this.options.getCodecFactory().getDecompressor(meta.getCodec());
    return new DictionaryPage(decompressor
        .decompress(compressedPage.getBytes(), compressedPage.getUncompressedSize()), compressedPage
        .getDictionarySize(), compressedPage
        .getEncoding());
  }
  
  private DictionaryPage readCompressedDictionary(PageHeader pageHeader, SeekableInputStream fin) throws IOException {
    DictionaryPageHeader dictHeader = pageHeader.getDictionary_page_header();
    int uncompressedPageSize = pageHeader.getUncompressed_page_size();
    int compressedPageSize = pageHeader.getCompressed_page_size();
    byte[] dictPageBytes = new byte[compressedPageSize];
    fin.readFully(dictPageBytes);
    BytesInput bin = BytesInput.from(dictPageBytes);
    return new DictionaryPage(bin, uncompressedPageSize, dictHeader
        .getNum_values(), this.converter
        .getEncoding(dictHeader.getEncoding()));
  }
  
  @Private
  public ColumnIndex readColumnIndex(ColumnChunkMetaData column) throws IOException {
    IndexReference ref = column.getColumnIndexReference();
    if (ref == null)
      return null; 
    this.f.seek(ref.getOffset());
    return ParquetMetadataConverter.fromParquetColumnIndex(column.getPrimitiveType(), Util.readColumnIndex((InputStream)this.f));
  }
  
  @Private
  public OffsetIndex readOffsetIndex(ColumnChunkMetaData column) throws IOException {
    IndexReference ref = column.getOffsetIndexReference();
    if (ref == null)
      return null; 
    this.f.seek(ref.getOffset());
    return ParquetMetadataConverter.fromParquetOffsetIndex(Util.readOffsetIndex((InputStream)this.f));
  }
  
  public void close() throws IOException {
    try {
      if (this.f != null)
        this.f.close(); 
    } finally {
      this.options.getCodecFactory().release();
    } 
  }
  
  private class ChunkListBuilder {
    private class ChunkData {
      private ChunkData() {}
      
      final List<ByteBuffer> buffers = new ArrayList<>();
      
      OffsetIndex offsetIndex;
    }
    
    private final Map<ParquetFileReader.ChunkDescriptor, ChunkData> map = new HashMap<>();
    
    private ParquetFileReader.ChunkDescriptor lastDescriptor;
    
    private SeekableInputStream f;
    
    void add(ParquetFileReader.ChunkDescriptor descriptor, List<ByteBuffer> buffers, SeekableInputStream f) {
      ChunkData data = this.map.get(descriptor);
      if (data == null) {
        data = new ChunkData();
        this.map.put(descriptor, data);
      } 
      data.buffers.addAll(buffers);
      this.lastDescriptor = descriptor;
      this.f = f;
    }
    
    void setOffsetIndex(ParquetFileReader.ChunkDescriptor descriptor, OffsetIndex offsetIndex) {
      ChunkData data = this.map.get(descriptor);
      if (data == null) {
        data = new ChunkData();
        this.map.put(descriptor, data);
      } 
      data.offsetIndex = offsetIndex;
    }
    
    List<ParquetFileReader.Chunk> build() {
      List<ParquetFileReader.Chunk> chunks = new ArrayList<>();
      for (Map.Entry<ParquetFileReader.ChunkDescriptor, ChunkData> entry : this.map.entrySet()) {
        ParquetFileReader.ChunkDescriptor descriptor = entry.getKey();
        ChunkData data = entry.getValue();
        if (descriptor.equals(this.lastDescriptor)) {
          chunks.add(new ParquetFileReader.WorkaroundChunk(this.lastDescriptor, data.buffers, this.f, data.offsetIndex));
          continue;
        } 
        chunks.add(new ParquetFileReader.Chunk(descriptor, data.buffers, data.offsetIndex));
      } 
      return chunks;
    }
    
    private ChunkListBuilder() {}
  }
  
  private class Chunk {
    protected final ParquetFileReader.ChunkDescriptor descriptor;
    
    protected final ByteBufferInputStream stream;
    
    final OffsetIndex offsetIndex;
    
    public Chunk(ParquetFileReader.ChunkDescriptor descriptor, List<ByteBuffer> buffers, OffsetIndex offsetIndex) {
      this.descriptor = descriptor;
      this.stream = ByteBufferInputStream.wrap(buffers);
      this.offsetIndex = offsetIndex;
    }
    
    protected PageHeader readPageHeader() throws IOException {
      return Util.readPageHeader((InputStream)this.stream);
    }
    
    public ColumnChunkPageReadStore.ColumnChunkPageReader readAllPages() throws IOException {
      List<DataPage> pagesInChunk = new ArrayList<>();
      DictionaryPage dictionaryPage = null;
      PrimitiveType type = ParquetFileReader.this.getFileMetaData().getSchema().getType(this.descriptor.col.getPath()).asPrimitiveType();
      long valuesCountReadSoFar = 0L;
      int dataPageCountReadSoFar = 0;
      while (hasMorePages(valuesCountReadSoFar, dataPageCountReadSoFar)) {
        DictionaryPageHeader dicHeader;
        DataPageHeader dataHeaderV1;
        DataPageHeaderV2 dataHeaderV2;
        int dataSize;
        PageHeader pageHeader = readPageHeader();
        int uncompressedPageSize = pageHeader.getUncompressed_page_size();
        int compressedPageSize = pageHeader.getCompressed_page_size();
        switch (pageHeader.type) {
          case DICTIONARY_PAGE:
            if (dictionaryPage != null)
              throw new ParquetDecodingException("more than one dictionary page in column " + this.descriptor.col); 
            dicHeader = pageHeader.getDictionary_page_header();
            dictionaryPage = new DictionaryPage(readAsBytesInput(compressedPageSize), uncompressedPageSize, dicHeader.getNum_values(), ParquetFileReader.this.converter.getEncoding(dicHeader.getEncoding()));
            continue;
          case DATA_PAGE:
            dataHeaderV1 = pageHeader.getData_page_header();
            pagesInChunk.add(new DataPageV1(
                  
                  readAsBytesInput(compressedPageSize), dataHeaderV1
                  .getNum_values(), uncompressedPageSize, ParquetFileReader.this
                  
                  .converter.fromParquetStatistics(ParquetFileReader.this
                    .getFileMetaData().getCreatedBy(), dataHeaderV1
                    .getStatistics(), type), ParquetFileReader.this
                  
                  .converter.getEncoding(dataHeaderV1.getRepetition_level_encoding()), ParquetFileReader.this
                  .converter.getEncoding(dataHeaderV1.getDefinition_level_encoding()), ParquetFileReader.this
                  .converter.getEncoding(dataHeaderV1.getEncoding())));
            valuesCountReadSoFar += dataHeaderV1.getNum_values();
            dataPageCountReadSoFar++;
            continue;
          case DATA_PAGE_V2:
            dataHeaderV2 = pageHeader.getData_page_header_v2();
            dataSize = compressedPageSize - dataHeaderV2.getRepetition_levels_byte_length() - dataHeaderV2.getDefinition_levels_byte_length();
            pagesInChunk.add(new DataPageV2(dataHeaderV2
                  
                  .getNum_rows(), dataHeaderV2
                  .getNum_nulls(), dataHeaderV2
                  .getNum_values(), 
                  readAsBytesInput(dataHeaderV2.getRepetition_levels_byte_length()), 
                  readAsBytesInput(dataHeaderV2.getDefinition_levels_byte_length()), ParquetFileReader.this
                  .converter.getEncoding(dataHeaderV2.getEncoding()), 
                  readAsBytesInput(dataSize), uncompressedPageSize, ParquetFileReader.this
                  
                  .converter.fromParquetStatistics(ParquetFileReader.this
                    .getFileMetaData().getCreatedBy(), dataHeaderV2
                    .getStatistics(), type), dataHeaderV2
                  
                  .isIs_compressed()));
            valuesCountReadSoFar += dataHeaderV2.getNum_values();
            dataPageCountReadSoFar++;
            continue;
        } 
        ParquetFileReader.LOG.debug("skipping page of type {} of size {}", pageHeader.getType(), Integer.valueOf(compressedPageSize));
        this.stream.skipFully(compressedPageSize);
      } 
      if (this.offsetIndex == null && valuesCountReadSoFar != this.descriptor.metadata.getValueCount())
        throw new IOException("Expected " + this.descriptor
            .metadata.getValueCount() + " values in column chunk at " + ParquetFileReader.this
            .getPath() + " offset " + this.descriptor.metadata.getFirstDataPageOffset() + " but got " + valuesCountReadSoFar + " values instead over " + pagesInChunk
            .size() + " pages ending at file offset " + (this.descriptor
            .fileOffset + this.stream.position())); 
      CompressionCodecFactory.BytesInputDecompressor decompressor = ParquetFileReader.this.options.getCodecFactory().getDecompressor(this.descriptor.metadata.getCodec());
      return new ColumnChunkPageReadStore.ColumnChunkPageReader(decompressor, pagesInChunk, dictionaryPage, this.offsetIndex, (
          (BlockMetaData)ParquetFileReader.this.blocks.get(ParquetFileReader.this.currentBlock)).getRowCount());
    }
    
    private boolean hasMorePages(long valuesCountReadSoFar, int dataPageCountReadSoFar) {
      return (this.offsetIndex == null) ? ((valuesCountReadSoFar < this.descriptor.metadata.getValueCount())) : (
        (dataPageCountReadSoFar < this.offsetIndex.getPageCount()));
    }
    
    public BytesInput readAsBytesInput(int size) throws IOException {
      return BytesInput.from(this.stream.sliceBuffers(size));
    }
  }
  
  private class WorkaroundChunk extends Chunk {
    private final SeekableInputStream f;
    
    private WorkaroundChunk(ParquetFileReader.ChunkDescriptor descriptor, List<ByteBuffer> buffers, SeekableInputStream f, OffsetIndex offsetIndex) {
      super(descriptor, buffers, offsetIndex);
      this.f = f;
    }
    
    protected PageHeader readPageHeader() throws IOException {
      PageHeader pageHeader;
      this.stream.mark(8192);
      try {
        pageHeader = Util.readPageHeader((InputStream)this.stream);
      } catch (IOException e) {
        this.stream.reset();
        ParquetFileReader.LOG.info("completing the column chunk to read the page header");
        pageHeader = Util.readPageHeader(new SequenceInputStream((InputStream)this.stream, (InputStream)this.f));
      } 
      return pageHeader;
    }
    
    public BytesInput readAsBytesInput(int size) throws IOException {
      int available = this.stream.available();
      if (size > available) {
        int missingBytes = size - available;
        ParquetFileReader.LOG.info("completed the column chunk with {} bytes", Integer.valueOf(missingBytes));
        List<ByteBuffer> buffers = new ArrayList<>();
        buffers.addAll(this.stream.sliceBuffers(available));
        ByteBuffer lastBuffer = ByteBuffer.allocate(missingBytes);
        this.f.readFully(lastBuffer);
        buffers.add(lastBuffer);
        return BytesInput.from(buffers);
      } 
      return super.readAsBytesInput(size);
    }
  }
  
  private static class ChunkDescriptor {
    private final ColumnDescriptor col;
    
    private final ColumnChunkMetaData metadata;
    
    private final long fileOffset;
    
    private final int size;
    
    private ChunkDescriptor(ColumnDescriptor col, ColumnChunkMetaData metadata, long fileOffset, int size) {
      this.col = col;
      this.metadata = metadata;
      this.fileOffset = fileOffset;
      this.size = size;
    }
    
    public int hashCode() {
      return this.col.hashCode();
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj instanceof ChunkDescriptor)
        return this.col.equals(((ChunkDescriptor)obj).col); 
      return false;
    }
  }
  
  private class ConsecutivePartList {
    private final long offset;
    
    private int length;
    
    private final List<ParquetFileReader.ChunkDescriptor> chunks = new ArrayList<>();
    
    ConsecutivePartList(long offset) {
      this.offset = offset;
    }
    
    public void addChunk(ParquetFileReader.ChunkDescriptor descriptor) {
      this.chunks.add(descriptor);
      this.length += descriptor.size;
    }
    
    public void readAll(SeekableInputStream f, ParquetFileReader.ChunkListBuilder builder) throws IOException {
      List<ParquetFileReader.Chunk> result = new ArrayList<>(this.chunks.size());
      f.seek(this.offset);
      int fullAllocations = this.length / ParquetFileReader.this.options.getMaxAllocationSize();
      int lastAllocationSize = this.length % ParquetFileReader.this.options.getMaxAllocationSize();
      int numAllocations = fullAllocations + ((lastAllocationSize > 0) ? 1 : 0);
      List<ByteBuffer> buffers = new ArrayList<>(numAllocations);
      for (int i = 0; i < fullAllocations; i++)
        buffers.add(ParquetFileReader.this.options.getAllocator().allocate(ParquetFileReader.this.options.getMaxAllocationSize())); 
      if (lastAllocationSize > 0)
        buffers.add(ParquetFileReader.this.options.getAllocator().allocate(lastAllocationSize)); 
      for (ByteBuffer buffer : buffers) {
        f.readFully(buffer);
        buffer.flip();
      } 
      BenchmarkCounter.incrementBytesRead(this.length);
      ByteBufferInputStream stream = ByteBufferInputStream.wrap(buffers);
      for (int j = 0; j < this.chunks.size(); j++) {
        ParquetFileReader.ChunkDescriptor descriptor = this.chunks.get(j);
        builder.add(descriptor, stream.sliceBuffers(descriptor.size), f);
      } 
    }
    
    public long endPos() {
      return this.offset + this.length;
    }
  }
}
