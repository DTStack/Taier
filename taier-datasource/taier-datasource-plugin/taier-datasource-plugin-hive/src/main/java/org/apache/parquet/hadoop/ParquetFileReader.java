/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.parquet.hadoop;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.parquet.Log;
import org.apache.parquet.bytes.BytesInput;
import org.apache.parquet.bytes.BytesUtils;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.page.DataPage;
import org.apache.parquet.column.page.DataPageV1;
import org.apache.parquet.column.page.DataPageV2;
import org.apache.parquet.column.page.DictionaryPage;
import org.apache.parquet.column.page.PageReadStore;
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
import org.apache.parquet.hadoop.util.HiddenFileFilter;
import org.apache.parquet.hadoop.util.counters.BenchmarkCounter;
import org.apache.parquet.io.ParquetDecodingException;

public class ParquetFileReader implements Closeable {
  private static final Log LOG = Log.getLog(ParquetFileReader.class);
  
  public static String PARQUET_READ_PARALLELISM = "parquet.metadata.read.parallelism";
  
  private static ParquetMetadataConverter converter = new ParquetMetadataConverter();
  
  private final CodecFactory codecFactory;
  
  private final List<BlockMetaData> blocks;
  
  private final FSDataInputStream f;
  
  private final Path filePath;
  
  @Deprecated
  public static List<Footer> readAllFootersInParallelUsingSummaryFiles(Configuration configuration, List<FileStatus> partFiles) throws IOException {
    return readAllFootersInParallelUsingSummaryFiles(configuration, partFiles, false);
  }
  
  private static ParquetMetadataConverter.MetadataFilter filter(boolean skipRowGroups) {
    return skipRowGroups ? ParquetMetadataConverter.SKIP_ROW_GROUPS : ParquetMetadataConverter.NO_FILTER;
  }
  
  public static List<Footer> readAllFootersInParallelUsingSummaryFiles(final Configuration configuration, final Collection<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
    Set<Path> parents = new HashSet<Path>();
    for (FileStatus part : partFiles)
      parents.add(part.getPath().getParent()); 
    List<Callable<Map<Path, Footer>>> summaries = new ArrayList<Callable<Map<Path, Footer>>>();
    for (Path path : parents) {
      summaries.add(new Callable<Map<Path, Footer>>() {
            public Map<Path, Footer> call() throws Exception {
              ParquetMetadata mergedMetadata = ParquetFileReader.readSummaryMetadata(configuration, path, skipRowGroups);
              if (mergedMetadata != null) {
                List<Footer> footers;
                if (skipRowGroups) {
                  footers = new ArrayList<Footer>();
                  for (FileStatus f : partFiles)
                    footers.add(new Footer(f.getPath(), mergedMetadata)); 
                } else {
                  footers = ParquetFileReader.footersFromSummaryFile(path, mergedMetadata);
                } 
                Map<Path, Footer> map = new HashMap<Path, Footer>();
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
    Map<Path, Footer> cache = new HashMap<Path, Footer>();
    try {
      List<Map<Path, Footer>> footersFromSummaries = runAllInParallel(configuration.getInt(PARQUET_READ_PARALLELISM, 5), summaries);
      for (Map<Path, Footer> footers : footersFromSummaries)
        cache.putAll(footers); 
    } catch (ExecutionException e) {
      throw new IOException("Error reading summaries", e);
    } 
    List<Footer> result = new ArrayList<Footer>(partFiles.size());
    List<FileStatus> toRead = new ArrayList<FileStatus>();
    for (FileStatus part : partFiles) {
      Footer f = cache.get(part.getPath());
      if (f != null) {
        result.add(f);
        continue;
      } 
      toRead.add(part);
    } 
    if (toRead.size() > 0) {
      if (Log.INFO)
        LOG.info("reading another " + toRead.size() + " footers"); 
      result.addAll(readAllFootersInParallel(configuration, toRead, skipRowGroups));
    } 
    return result;
  }
  
  private static <T> List<T> runAllInParallel(int parallelism, List<Callable<T>> toRun) throws ExecutionException {
    LOG.info("Initiating action with parallelism: " + parallelism);
    ExecutorService threadPool = Executors.newFixedThreadPool(parallelism);
    try {
      List<Future<T>> futures = new ArrayList<Future<T>>();
      for (Callable<T> callable : toRun)
        futures.add(threadPool.submit(callable)); 
      List<T> result = new ArrayList<T>(toRun.size());
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
  
  public static List<Footer> readAllFootersInParallel(final Configuration configuration, List<FileStatus> partFiles, final boolean skipRowGroups) throws IOException {
    List<Callable<Footer>> footers = new ArrayList<Callable<Footer>>();
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
  
  public static List<Footer> readAllFootersInParallel(Configuration configuration, FileStatus fileStatus) throws IOException {
    List<FileStatus> statuses = listFiles(configuration, fileStatus);
    return readAllFootersInParallel(configuration, statuses, false);
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
  
  public static List<Footer> readFooters(Configuration configuration, FileStatus pathStatus, boolean skipRowGroups) throws IOException {
    List<FileStatus> files = listFiles(configuration, pathStatus);
    return readAllFootersInParallelUsingSummaryFiles(configuration, files, skipRowGroups);
  }
  
  private static List<FileStatus> listFiles(Configuration conf, FileStatus fileStatus) throws IOException {
    if (fileStatus.isDir()) {
      FileSystem fs = fileStatus.getPath().getFileSystem(conf);
      FileStatus[] list = fs.listStatus(fileStatus.getPath(), (PathFilter)HiddenFileFilter.INSTANCE);
      List<FileStatus> result = new ArrayList<FileStatus>();
      for (FileStatus sub : list)
        result.addAll(listFiles(conf, sub)); 
      return result;
    } 
    return Arrays.asList(new FileStatus[] { fileStatus });
  }
  
  public static List<Footer> readSummaryFile(Configuration configuration, FileStatus summaryStatus) throws IOException {
    Path parent = summaryStatus.getPath().getParent();
    ParquetMetadata mergedFooters = readFooter(configuration, summaryStatus, filter(false));
    return footersFromSummaryFile(parent, mergedFooters);
  }
  
  static ParquetMetadata readSummaryMetadata(Configuration configuration, Path basePath, boolean skipRowGroups) throws IOException {
    /*Path metadataFile = new Path(basePath, "_metadata");
    Path commonMetaDataFile = new Path(basePath, "_common_metadata");
    FileSystem fileSystem = basePath.getFileSystem(configuration);
    if (skipRowGroups && fileSystem.exists(commonMetaDataFile) && fileSystem.isFile(commonMetaDataFile)) {
      if (Log.INFO)
        LOG.info("reading summary file: " + commonMetaDataFile); 
      return readFooter(configuration, commonMetaDataFile, filter(skipRowGroups));
    } 
    if (fileSystem.exists(metadataFile) && fileSystem.isFile(metadataFile)) {
      if (Log.INFO)
        LOG.info("reading summary file: " + metadataFile); 
      return readFooter(configuration, metadataFile, filter(skipRowGroups));
    }*/
    return null;
  }
  
  static List<Footer> footersFromSummaryFile(Path parent, ParquetMetadata mergedFooters) {
    Map<Path, ParquetMetadata> footers = new HashMap<Path, ParquetMetadata>();
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
    List<Footer> result = new ArrayList<Footer>();
    for (Map.Entry<Path, ParquetMetadata> entry : footers.entrySet())
      result.add(new Footer(entry.getKey(), entry.getValue())); 
    return result;
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(Configuration configuration, Path file) throws IOException {
    return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
  }
  
  public static ParquetMetadata readFooter(Configuration configuration, Path file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    FileSystem fileSystem = file.getFileSystem(configuration);
    return readFooter(configuration, fileSystem.getFileStatus(file), filter);
  }
  
  @Deprecated
  public static final ParquetMetadata readFooter(Configuration configuration, FileStatus file) throws IOException {
    return readFooter(configuration, file, ParquetMetadataConverter.NO_FILTER);
  }
  
  public static final ParquetMetadata readFooter(Configuration configuration, FileStatus file, ParquetMetadataConverter.MetadataFilter filter) throws IOException {
    FileSystem fileSystem = file.getPath().getFileSystem(configuration);
    FSDataInputStream f = fileSystem.open(file.getPath());
    try {
      long l = file.getLen();
      if (Log.DEBUG)
        LOG.debug("File length " + l); 
      int FOOTER_LENGTH_SIZE = 4;
      if (l < (ParquetFileWriter.MAGIC.length + FOOTER_LENGTH_SIZE + ParquetFileWriter.MAGIC.length))
        throw new RuntimeException(file.getPath() + " is not a Parquet file (too small)"); 
      long footerLengthIndex = l - FOOTER_LENGTH_SIZE - ParquetFileWriter.MAGIC.length;
      if (Log.DEBUG)
        LOG.debug("reading footer index at " + footerLengthIndex); 
      f.seek(footerLengthIndex);
      int footerLength = BytesUtils.readIntLittleEndian((InputStream)f);
      byte[] magic = new byte[ParquetFileWriter.MAGIC.length];
      f.readFully(magic);
      if (!Arrays.equals(ParquetFileWriter.MAGIC, magic))
        throw new RuntimeException(file.getPath() + " is not a Parquet file. expected magic number at tail " + Arrays.toString(ParquetFileWriter.MAGIC) + " but found " + Arrays.toString(magic)); 
      long footerIndex = footerLengthIndex - footerLength;
      if (Log.DEBUG)
        LOG.debug("read footer length: " + footerLength + ", footer index: " + footerIndex); 
      if (footerIndex < ParquetFileWriter.MAGIC.length || footerIndex >= footerLengthIndex)
        throw new RuntimeException("corrupted file: the footer index is not within the file"); 
      f.seek(footerIndex);
      return converter.readParquetMetadata((InputStream)f, filter);
    } finally {
      f.close();
    } 
  }
  
  private final Map<ColumnPath, ColumnDescriptor> paths = new HashMap<ColumnPath, ColumnDescriptor>();
  
  private final FileMetaData fileMetaData;
  
  private final String createdBy;
  
  private int currentBlock = 0;
  
  public ParquetFileReader(Configuration configuration, Path filePath, List<BlockMetaData> blocks, List<ColumnDescriptor> columns) throws IOException {
    this(configuration, null, filePath, blocks, columns);
  }
  
  public ParquetFileReader(Configuration configuration, FileMetaData fileMetaData, Path filePath, List<BlockMetaData> blocks, List<ColumnDescriptor> columns) throws IOException {
    this.filePath = filePath;
    this.fileMetaData = fileMetaData;
    this.createdBy = (fileMetaData == null) ? null : fileMetaData.getCreatedBy();
    FileSystem fs = filePath.getFileSystem(configuration);
    this.f = fs.open(filePath);
    this.blocks = blocks;
    for (ColumnDescriptor col : columns)
      this.paths.put(ColumnPath.get(col.getPath()), col); 
    this.codecFactory = new CodecFactory(configuration);
  }
  
  public PageReadStore readNextRowGroup() throws IOException {
    if (this.currentBlock == this.blocks.size())
      return null; 
    BlockMetaData block = this.blocks.get(this.currentBlock);
    if (block.getRowCount() == 0L)
      throw new RuntimeException("Illegal row group of 0 rows"); 
    ColumnChunkPageReadStore columnChunkPageReadStore = new ColumnChunkPageReadStore(block.getRowCount());
    List<ConsecutiveChunkList> allChunks = new ArrayList<ConsecutiveChunkList>();
    ConsecutiveChunkList currentChunks = null;
    for (ColumnChunkMetaData mc : block.getColumns()) {
      ColumnPath pathKey = mc.getPath();
      BenchmarkCounter.incrementTotalBytes(mc.getTotalSize());
      ColumnDescriptor columnDescriptor = this.paths.get(pathKey);
      if (columnDescriptor != null) {
        long startingPos = mc.getStartingPos();
        if (currentChunks == null || currentChunks.endPos() != startingPos) {
          currentChunks = new ConsecutiveChunkList(startingPos);
          allChunks.add(currentChunks);
        } 
        currentChunks.addChunk(new ChunkDescriptor(columnDescriptor, mc, startingPos, (int)mc.getTotalSize()));
      } 
    } 
    for (ConsecutiveChunkList consecutiveChunks : allChunks) {
      List<Chunk> chunks = consecutiveChunks.readAll(this.f);
      for (Chunk chunk : chunks)
        columnChunkPageReadStore.addColumn(chunk.descriptor.col, chunk.readAllPages()); 
    } 
    this.currentBlock++;
    return columnChunkPageReadStore;
  }
  
  public void close() throws IOException {
    this.f.close();
    this.codecFactory.release();
  }
  
  private class Chunk extends ByteArrayInputStream {
    private final ParquetFileReader.ChunkDescriptor descriptor;
    
    public Chunk(ParquetFileReader.ChunkDescriptor descriptor, byte[] data, int offset) {
      super(data);
      this.descriptor = descriptor;
      this.pos = offset;
    }
    
    protected PageHeader readPageHeader() throws IOException {
      return Util.readPageHeader(this);
    }
    
    public ColumnChunkPageReadStore.ColumnChunkPageReader readAllPages() throws IOException {
      List<DataPage> pagesInChunk = new ArrayList<DataPage>();
      DictionaryPage dictionaryPage = null;
      long valuesCountReadSoFar = 0L;
      while (valuesCountReadSoFar < this.descriptor.metadata.getValueCount()) {
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
            dictionaryPage = new DictionaryPage(readAsBytesInput(compressedPageSize), uncompressedPageSize, dicHeader.getNum_values(), ParquetFileReader.converter.getEncoding(dicHeader.getEncoding()));
            continue;
          case DATA_PAGE:
            dataHeaderV1 = pageHeader.getData_page_header();
            pagesInChunk.add(new DataPageV1(readAsBytesInput(compressedPageSize), dataHeaderV1.getNum_values(), uncompressedPageSize, ParquetMetadataConverter.fromParquetStatistics(ParquetFileReader.this.createdBy, dataHeaderV1.getStatistics(), this.descriptor.col.getType()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getRepetition_level_encoding()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getDefinition_level_encoding()), ParquetFileReader.converter.getEncoding(dataHeaderV1.getEncoding())));
            valuesCountReadSoFar += dataHeaderV1.getNum_values();
            continue;
          case DATA_PAGE_V2:
            dataHeaderV2 = pageHeader.getData_page_header_v2();
            dataSize = compressedPageSize - dataHeaderV2.getRepetition_levels_byte_length() - dataHeaderV2.getDefinition_levels_byte_length();
            pagesInChunk.add(new DataPageV2(dataHeaderV2.getNum_rows(), dataHeaderV2.getNum_nulls(), dataHeaderV2.getNum_values(), readAsBytesInput(dataHeaderV2.getRepetition_levels_byte_length()), readAsBytesInput(dataHeaderV2.getDefinition_levels_byte_length()), ParquetFileReader.converter.getEncoding(dataHeaderV2.getEncoding()), readAsBytesInput(dataSize), uncompressedPageSize, ParquetMetadataConverter.fromParquetStatistics(ParquetFileReader.this.createdBy, dataHeaderV2.getStatistics(), this.descriptor.col.getType()), dataHeaderV2.isIs_compressed()));
            valuesCountReadSoFar += dataHeaderV2.getNum_values();
            continue;
        } 
        if (Log.DEBUG)
          ParquetFileReader.LOG.debug("skipping page of type " + pageHeader.getType() + " of size " + compressedPageSize); 
        skip(compressedPageSize);
      } 
      if (valuesCountReadSoFar != this.descriptor.metadata.getValueCount())
        throw new IOException("Expected " + this.descriptor.metadata.getValueCount() + " values in column chunk at " + ParquetFileReader.this.filePath + " offset " + this.descriptor.metadata.getFirstDataPageOffset() + " but got " + valuesCountReadSoFar + " values instead over " + pagesInChunk.size() + " pages ending at file offset " + (this.descriptor.fileOffset + pos())); 
      CodecFactory.BytesDecompressor decompressor = ParquetFileReader.this.codecFactory.getDecompressor(this.descriptor.metadata.getCodec());
      return new ColumnChunkPageReadStore.ColumnChunkPageReader(decompressor, pagesInChunk, dictionaryPage);
    }
    
    public int pos() {
      return this.pos;
    }
    
    public BytesInput readAsBytesInput(int size) throws IOException {
      BytesInput r = BytesInput.from(this.buf, this.pos, size);
      this.pos += size;
      return r;
    }
  }
  
  private class WorkaroundChunk extends Chunk {
    private final FSDataInputStream f;
    
    private WorkaroundChunk(ParquetFileReader.ChunkDescriptor descriptor, byte[] data, int offset, FSDataInputStream f) {
      super(descriptor, data, offset);
      this.f = f;
    }
    
    protected PageHeader readPageHeader() throws IOException {
      PageHeader pageHeader;
      int initialPos = this.pos;
      try {
        pageHeader = Util.readPageHeader(this);
      } catch (IOException e) {
        this.pos = initialPos;
        ParquetFileReader.LOG.info("completing the column chunk to read the page header");
        pageHeader = Util.readPageHeader(new SequenceInputStream(this, (InputStream)this.f));
      } 
      return pageHeader;
    }
    
    public BytesInput readAsBytesInput(int size) throws IOException {
      if (this.pos + size > this.count) {
        int l1 = this.count - this.pos;
        int l2 = size - l1;
        ParquetFileReader.LOG.info("completed the column chunk with " + l2 + " bytes");
        return BytesInput.concat(new BytesInput[] { super.readAsBytesInput(l1), BytesInput.copy(BytesInput.from((InputStream)this.f, l2)) });
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
  }
  
  private class ConsecutiveChunkList {
    private final long offset;
    
    private int length;
    
    private final List<ParquetFileReader.ChunkDescriptor> chunks = new ArrayList<ParquetFileReader.ChunkDescriptor>();
    
    ConsecutiveChunkList(long offset) {
      this.offset = offset;
    }
    
    public void addChunk(ParquetFileReader.ChunkDescriptor descriptor) {
      this.chunks.add(descriptor);
      this.length += descriptor.size;
    }
    
    public List<ParquetFileReader.Chunk> readAll(FSDataInputStream f) throws IOException {
      List<ParquetFileReader.Chunk> result = new ArrayList<ParquetFileReader.Chunk>(this.chunks.size());
      f.seek(this.offset);
      byte[] chunksBytes = new byte[this.length];
      f.readFully(chunksBytes);
      BenchmarkCounter.incrementBytesRead(this.length);
      int currentChunkOffset = 0;
      for (int i = 0; i < this.chunks.size(); i++) {
        ParquetFileReader.ChunkDescriptor descriptor = this.chunks.get(i);
        if (i < this.chunks.size() - 1) {
          result.add(new ParquetFileReader.Chunk(descriptor, chunksBytes, currentChunkOffset));
        } else {
          result.add(new ParquetFileReader.WorkaroundChunk(descriptor, chunksBytes, currentChunkOffset, f));
        } 
        currentChunkOffset += descriptor.size;
      } 
      return result;
    }
    
    public long endPos() {
      return this.offset + this.length;
    }
  }
}
