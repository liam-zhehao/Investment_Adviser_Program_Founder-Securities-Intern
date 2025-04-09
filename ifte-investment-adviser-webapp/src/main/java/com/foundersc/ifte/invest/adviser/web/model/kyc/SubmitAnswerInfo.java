package com.foundersc.ifte.invest.adviser.web.model.kyc;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerInfo {

    public static String DEFAULT_SCORE = "0";

    // 试题编号
    private String id;

    // 分数
    private String score;

    // 用户提交的答案
    private List<String> selected;
}
