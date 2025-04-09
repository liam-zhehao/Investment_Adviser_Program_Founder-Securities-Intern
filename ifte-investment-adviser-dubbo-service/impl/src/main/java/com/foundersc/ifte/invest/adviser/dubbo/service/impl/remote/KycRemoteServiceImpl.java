package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import com.foundersc.ifc.portfolio.t2.model.v2.kyc.TestjourExtDTO;
import com.foundersc.ifc.portfolio.t2.request.v2.kyc.PostPaperAnswerReq;
import com.foundersc.ifc.portfolio.t2.request.v2.kyc.QueryKycPaperReq;
import com.foundersc.ifc.portfolio.t2.response.v2.kyc.PostAnswerResp;
import com.foundersc.ifc.portfolio.t2.response.v2.kyc.QueryKycPaperResp;
import com.foundersc.ifc.portfolio.t2.response.v2.kyc.QueryTestjourResp;
import com.foundersc.ifc.portfolio.t2.service.v2.KycPaperService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.KycPaperInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.QuestionInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.KycRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.enums.PaperSubTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.*;

@Service("kycRemoteService")
@Slf4j
public class KycRemoteServiceImpl implements KycRemoteService {

    private static final String PAPER_TYPE_KYC = "1";

    @Autowired
    private KycPaperService kycPaperService;

    @Override
    public KycPaperInfo queryKycPaper(SimpleAccount simpleAccount, String investOrganNo) {
        QueryKycPaperReq queryKycPaperReq = new QueryKycPaperReq();
        queryKycPaperReq.setInvestOrganNo(investOrganNo);
        queryKycPaperReq.setPaperType(PAPER_TYPE_KYC);
        BaseResult<QueryKycPaperResp> baseResult = kycPaperService.queryKycPaper(simpleAccount, queryKycPaperReq);
        log.info("[queryKycPaper] simpleAccount:{},baseResult:{}", simpleAccount, baseResult);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_KYC_PAPER_ERROR + "[queryKycPaper] failed, investOrganNo = {},clientId = {}", investOrganNo, simpleAccount.getClientId());
            throw new BusinessException(QUERY_KYC_PAPER_ERROR);
        }
        if (baseResult.getData() == null || CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error(QUERY_KYC_PAPER_ERROR + "[queryKycPaper] failed,question list is empty. investOrganNo = {},clientId = {}", investOrganNo, simpleAccount.getClientId());
            throw new BusinessException(QUERY_KYC_PAPER_ERROR);
        }
        QueryKycPaperResp queryKycPaperResp = baseResult.getData();
        KycPaperInfo kycPaperInfo = new KycPaperInfo();
        kycPaperInfo.setPaperVersion(queryKycPaperResp.getPaper_version());
        List<QuestionInfo> questionInfoList = new ArrayList<>(queryKycPaperResp.getRows().size());
        ObjectCopyUtil.copyKycPaper(queryKycPaperResp.getRows(), questionInfoList);
        kycPaperInfo.setRows(questionInfoList);
        return kycPaperInfo;
    }

    @Override
    public List<TestjourInfo> queryTestjour(SimpleAccount simpleAccount, String investOrganNo) {
        BaseResult<QueryTestjourResp> baseResult = kycPaperService.queryTestjour(simpleAccount, investOrganNo);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_TESTJOUR_ERROR + "[queryTestjour] failed, clientId = {}", simpleAccount.getClientId());
            throw new BusinessException(QUERY_TESTJOUR_ERROR);
        }
        if (baseResult.getData() == null || CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.info("testjour list is empty,clientId = {}", simpleAccount.getClientId());
            return new ArrayList<>();
        }
        List<TestjourExtDTO> testjourExtDTOs = baseResult.getData().getRows();
        List<TestjourInfo> testjourInfos = new ArrayList<>(testjourExtDTOs.size());
        ObjectCopyUtil.copyTestjourInfos(testjourExtDTOs, testjourInfos);
        return testjourInfos;
    }

    @Override
    public boolean postPaperAnswer(SimpleAccount simpleAccount, String paperAnswer, String paperVersion, String investOrganNo) {
        PostPaperAnswerReq postPaperAnswerReq = new PostPaperAnswerReq();
        postPaperAnswerReq.setPaperAnswer(paperAnswer);
        postPaperAnswerReq.setPaperVersion(paperVersion);
        postPaperAnswerReq.setPaperType(PAPER_TYPE_KYC);
        postPaperAnswerReq.setInvestOrganNo(investOrganNo);
        BaseResult<PostAnswerResp> baseResult = kycPaperService.postPaperAnswer(simpleAccount, postPaperAnswerReq);
        if (!baseResult.isSuccess()) {
            log.error(POST_PAPER_ANSWER_ERROR + "[postPaperAnswer] failed, clientId = {} , postPaperAnswerReq = {}", simpleAccount.getClientId(), postPaperAnswerReq);
            if ("39138".equals(baseResult.getCode())) {
                // 问卷试题有更新，请重新提交问卷答案
                throw new BusinessException(ErrorCodeEnum.POST_PAPER_ANSWER_ERROR, baseResult.getErrorMsg());
            } else {
                throw new BusinessException(ErrorCodeEnum.POST_PAPER_ANSWER_ERROR);
            }

        }
        return true;
    }
}
