package com.foundersc.ifte.invest.adviser.web.config.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * nodeKey配置
 */
@Component
@ConfigurationProperties(prefix = "node-key")
@Data
public class NodeKeyProperties {
    /**
     * oracle数据库的nodeKey
     */
    private String oracle;

    /**
     * xfapp数据库的nodeKey
     */
    private String redis;
}

