package com.dtstack.taier.common.source;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

/**
 * @author yuebai
 * @date 2022/10/11
 */
public interface SourceDTOLoader {
    ISourceDTO buildSourceDTO(Long datasourceId);
}
