package com.foundersc.ifte.invest.adviser.web.service;


import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;
import com.foundersc.ifte.invest.adviser.web.model.combine.RecommendCombineVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.*;

import java.util.List;

public interface KycPaperInfoService {


    KycPaperVO getKycPaper(String investOrganNo);

    void delTestjourCache(SimpleAccount simpleAccount, String investOrganNo);

    TestjourInfo queryTestjour(SimpleAccount simpleAccount, String investOrganNo);

    /**
     * 获取问卷信息
     *
     * @param investOrganNo
     * @return
     */
    KycPaperVO getKycPaperInfo(String investOrganNo);

    /**
     * 用户是否答过问卷
     *
     * @param investOrganNo
     * @return
     */
    Boolean hasAnswered(String investOrganNo);

    /**
     * 提交问卷答案
     *
     * @return
     */
    Boolean submitPaper(SubmitPaperReq submitPaperReq);

    /**
     * @param investOrganNo
     * @return
     */
    List<RecommendCombineVO> queryRecommendCombines(String investOrganNo);

    /**
     * 查询用户投资需求（上次答题记录）
     *
     * @return
     */
    InvestDemandInfo queryInvestDemand(String investOrganNo);

    /**
     * 查询已上架的投顾机构列表
     *
     * @return
     */
    List<InvestOrganVO> queryInvestOrgans();

    /**
     * 查询投顾专区信息
     *
     * @return
     */
    InvestOrganPageVO queryInvestOrganInfo(String investOrganNo);


}
