package com.foundersc.ifte.invest.adviser.web.model.combine;

import lombok.Data;

/**
 * 组合标签
 *
 * @author wangfuwei
 * @date 2022/10/27
 */
@Data
public class TagVO {
    /**
     * 优先级
     */
    private Integer priorityLevel;

    /**
     * 标签id
     */
    private String tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型：0-KYC标签；1-运营标签
     */
    private String tagType;

    public TagVO() {
        this.priorityLevel = 0;
    }
}
