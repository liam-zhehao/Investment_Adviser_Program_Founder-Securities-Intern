package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.KycPaperInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;

import java.util.List;

/**
 * kyc问卷相关服务
 */
public interface KycRemoteService {

    /**
     * 问卷查询
     *
     * @param simpleAccount
     * @param investOrganNo
     * @return
     */
    KycPaperInfo queryKycPaper(SimpleAccount simpleAccount, String investOrganNo);

    /**
     * 客户答题记录查询
     *
     * @param simpleAccount
     * @param investOrganNo
     * @return
     */
    List<TestjourInfo> queryTestjour(SimpleAccount simpleAccount, String investOrganNo);

    /**
     * 提交问卷答案
     *
     * @return
     */
    boolean postPaperAnswer(SimpleAccount simpleAccount, String paperAnswer, String paperVersion, String investOrganNo);
}
