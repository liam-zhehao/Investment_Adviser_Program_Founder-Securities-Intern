package com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 问卷信息
 */
@Data
public class KycPaperInfo implements Serializable {
    /**
     * 问卷版本号
     */
    private String paperVersion;
    /**
     * 试题列表
     */
    private List<QuestionInfo> rows;
}
