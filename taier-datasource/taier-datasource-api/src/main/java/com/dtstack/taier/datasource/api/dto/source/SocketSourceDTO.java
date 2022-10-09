package com.dtstack.taier.datasource.api.dto.source;


import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * socket数据源连接信息
 *
 * @author ：wangchuan
 * date：Created in 4:16 下午 2020/12/28
 * company: www.dtstack.com
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketSourceDTO extends AbstractSourceDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * socket ip和端口
     */
    private String hostPort;

    /**
     * 数据源类型
     */
    @Builder.Default
    protected Integer sourceType = DataSourceType.SOCKET.getVal();
}
