package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombCodeStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombProfitTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombShelfStatusEnum;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.KycPaperInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.KycRemoteService;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.RecommendCombineVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.*;
import com.foundersc.ifte.invest.adviser.web.service.KycPaperInfoService;
import com.foundersc.ifte.invest.adviser.web.util.BeanUtil;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.config.InvestOrganConfig.INVEST_ORGAN_LIST;
import static com.foundersc.ifte.invest.adviser.web.config.InvestOrganConfig.INVEST_ORGAN_MAP;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.RedisConstants.KYC_PAPER;
import static com.foundersc.ifte.invest.adviser.web.constants.RedisConstants.KYC_TESTJOUR;
import static com.foundersc.ifte.invest.adviser.web.model.kyc.SubmitAnswerInfo.DEFAULT_SCORE;


@Slf4j
@Service("kycPaperInfoService")
public class KycPaperInfoImpl implements KycPaperInfoService {

    @Autowired
    private KycRemoteService kycRemoteService;

    @Autowired
    private KycPaperInfoService kycPaperInfoService;

    @Autowired
    private CombineRemoteService combineRemoteService;

    @Autowired
    private CombInfoServiceImpl combInfoService;

    @Autowired
    private InvestOrganInfos investOrganInfos;

    public static final String RATIO_TAG = "%";
    //试题类型 单选
    public static final String QUESTION_KIND_SINGLE = "0";

    @Override
    @Cacheable(cacheNames = KYC_PAPER, key = "'kyc:paper:' + #investOrganNo", unless = "#result == null")
    public KycPaperVO getKycPaper(String investOrganNo) {
        KycPaperInfo kycPaperInfo = kycRemoteService.queryKycPaper(ContextHolder.getSimpleAccount(), investOrganNo);
        log.info("kycPaperInfo:{}", kycPaperInfo);
        if (kycPaperInfo == null) {
            throw new BizException(BizErrorCodeEnum.QUERY_KYC_PAPER_ERROR);
        }
        KycPaperVO result = new KycPaperVO();
        List<KycQuestionVO> list = BeanUtil.copyToList(kycPaperInfo.getRows(), KycQuestionVO.class);
        //所有试题限制为单选
        list.stream().forEach(tmpKycQuestionVO -> {
            tmpKycQuestionVO.setQuestionKind(QUESTION_KIND_SINGLE);
        });
        result.setQuestionList(list);
        result.setPaperVersion(kycPaperInfo.getPaperVersion());
        return result;
    }

    @Override
    public KycPaperVO getKycPaperInfo(String investOrganNo) {
        // 最新问卷
        KycPaperVO kycPaperVO = kycPaperInfoService.getKycPaper(investOrganNo);
        // 用户答题记录
        SimpleAccount simpleAccount = ContextHolder.getSimpleAccount();
        TestjourInfo testjourInfo = kycPaperInfoService.queryTestjour(simpleAccount, investOrganNo);
        if (testjourInfo == null) {
            return kycPaperVO;
        }
        // 问卷有更新
        if (!(testjourInfo.getPaperVersion().equals(kycPaperVO.getPaperVersion()))) {
            return kycPaperVO;
        }
        List<KycQuestionVO> kycQuestionVOList = kycPaperVO.getQuestionList();
        // 标签模式：paperAnswer:1#10&2|2#0&1^2
        // 非标签模式：[{"score":"0","id":"4","selected":["2"]},{"score":"0","id":"5","selected":["2"]},{"score":"0","id":"6","selected":["2"]}]
        // 使用非标签模式
        String paperAnswer = testjourInfo.getPaperAnswer();
        List<SubmitAnswerInfo> answerInfos = JSONArray.parseArray(paperAnswer, SubmitAnswerInfo.class);
        for (KycQuestionVO kycQuestionVO : kycQuestionVOList) {
            for (SubmitAnswerInfo submitAnswerInfo : answerInfos) {
                if (submitAnswerInfo.getId().equals(kycQuestionVO.getQuestionNo())) {
                    kycQuestionVO.setLastAnswers(submitAnswerInfo.getSelected());
                }
            }
        }

        // 使用非标签模式
//        List<String> answerInfos = Arrays.asList(paperAnswer.split(KYC_QUESTION_SEPARATOR));
//        answerInfos.stream().forEach(tmpAnswerInfo -> {
//            String questionNo = tmpAnswerInfo.split(KYC_QUESTION_N0_SCORE_SEPARATOR)[0];
//            String answerNos = tmpAnswerInfo.split(KYC_SCORE_ANSWER_NO_SEPARATOR)[1];
//            List<String> answerNoList = Arrays.asList(answerNos.split(KYC_ANSWER_SEPARATOR));
//            for (KycQuestionVO kycQuestionVO : kycQuestionVOList) {
//                if (questionNo.equals(kycQuestionVO.getQuestionNo())) {
//                    kycQuestionVO.setLastAnswers(answerNoList);
//                }
//            }
//        });
        return kycPaperVO;
    }

    @Override
    public Boolean hasAnswered(String investOrganNo) {
        KycPaperVO kycPaperVO = kycPaperInfoService.getKycPaper(investOrganNo);
        SimpleAccount simpleAccount = ContextHolder.getSimpleAccount();
        TestjourInfo testjourInfo = kycPaperInfoService.queryTestjour(simpleAccount, investOrganNo);
        if (testjourInfo == null) {
            return false;
        }
        if (testjourInfo.getPaperVersion().equals(kycPaperVO.getPaperVersion())) {
            return true;
        }
        return false;
    }

    @Override
    public List<RecommendCombineVO> queryRecommendCombines(String investOrganNo) {
        List<CombineInfo> combineInfos = combineRemoteService.getCombinfoItemByKyc(ContextHolder.getSimpleAccount(), RECOMMEND_COMB_ORDER_DIRECTION, investOrganNo, EN_TAG_TYPE);
        if (CollectionUtils.isEmpty(combineInfos)) {
            return new ArrayList<>();
        }
        List<RecommendCombineVO> recommendCombineVOS = combineInfos.stream().filter(tmpCombineInfo -> filterStatus(tmpCombineInfo) && filterOrgan(tmpCombineInfo, investOrganNo))
                .map(tmpCombineInfo -> combineInfoConvert(tmpCombineInfo)).collect(Collectors.toList());
        return recommendCombineVOS.stream().sorted(Comparator.comparing(RecommendCombineVO::getInvestOrganName)).collect(Collectors.toList());

    }

    private boolean filterOrgan(CombineInfo tmpCombineInfo, String investOrganNo) {
        return investOrganNo.equals(tmpCombineInfo.getInvestOrganNo());
    }


    // 保留上架状态并且生效状态的组合
    private boolean filterStatus(CombineInfo combineInfo) {
        return CombShelfStatusEnum.ON_SHELF.getCode().equals(combineInfo.getCombShelfStatus()) &&
                CombCodeStatusEnum.VALID.getCode().equals(combineInfo.getCombCodeStatus());
    }

    private RecommendCombineVO combineInfoConvert(CombineInfo combineInfo) {
        RecommendCombineVO recommendCombineVO = new RecommendCombineVO();
        CombineInfoVO combineInfoVO = combInfoService.info(combineInfo.getCombineCode(), DateUtil.dateToInt(new Date()));
        BeanUtil.copyProperties(combineInfoVO, recommendCombineVO);
        recommendCombineVO.setComIncomeRatio(combineInfoVO.getComIncomeRatioDesc());
        recommendCombineVO.setComMaxRetreatRatio(combineInfoVO.getComMaxRetreatRatioDesc());
        return recommendCombineVO;
    }

    @Override
    /**
     * kyc问卷模式（1-标签模式（默认））：1#10&2|2#0&1^2| #前是题号，#和&之间是获得的分数，现在写死0，&后是选择的答案编号，如果存在多个答案用^分割，|表示一题的答案结束
     * kyc问卷模式（0-非标签模式）：答案格式为json串[{"id":"1","score":"0","selected":["1","2"]},{"id":"2","score":"0","selected":["1"]}]
     * 采用 1-标签模式
     */
    public InvestDemandInfo queryInvestDemand(String investOrganNo) {
        InvestDemandInfo investDemandInfo = new InvestDemandInfo();
        // 用户答题记录
        SimpleAccount simpleAccount = ContextHolder.getSimpleAccount();
        TestjourInfo testjourInfo = kycPaperInfoService.queryTestjour(simpleAccount, investOrganNo);
        if (testjourInfo == null) {
            return investDemandInfo;
        }
        // kyc问卷
        KycPaperVO kycPaperVO = kycPaperInfoService.getKycPaper(investOrganNo);
        if (!(testjourInfo.getPaperVersion().equals(kycPaperVO.getPaperVersion()))) {
            return investDemandInfo;
        }

        List<KycQuestionVO> questionList = kycPaperVO.getQuestionList();
        String paperAnswer = testjourInfo.getPaperAnswer();
        List<InvestDemandVO> resultList = new ArrayList<>();
        //使用非标签模式
        // answerInfoList: [{"score":"0","id":"4","selected":["2"]},{"score":"0","id":"5","selected":["2"]},{"score":"0","id":"6","selected":["2"]}]
        List<SubmitAnswerInfo> answerInfoList = JSONArray.parseArray(paperAnswer, SubmitAnswerInfo.class);
        for (KycQuestionVO kycQuestionVO : questionList) {
            for (SubmitAnswerInfo submitAnswerInfo : answerInfoList) {
                if (!(kycQuestionVO.getQuestionNo().equals(submitAnswerInfo.getId()))) {
                    continue;
                }
                InvestDemandVO investDemandVO = new InvestDemandVO();
                investDemandVO.setDesc(kycQuestionVO.getRemark());
                List<String> answerTextList = new ArrayList<>();
                for (String answerNo : submitAnswerInfo.getSelected()) {
                    for (KycAnswerVO kycAnswerVO : kycQuestionVO.getAnswerList()) {
                        if (kycAnswerVO.getAnswerNo().equals(answerNo)) {
                            answerTextList.add(kycAnswerVO.getAnswerContent());
                        }
                    }
                }
                investDemandVO.setItem(String.join(INVESTDEMAND_ANSWER_SEPARATOR, answerTextList));
                resultList.add(investDemandVO);
            }
        }
        if (resultList.size() == 1) {
            investDemandInfo.setOneDemandDesc(resultList.get(0).getItem());
            investDemandInfo.setInvestDemandVOS(new ArrayList<>());
            return investDemandInfo;
        }
        investDemandInfo.setInvestDemandVOS(resultList);
        return investDemandInfo;
        // 标签模式：paperAnswer:1#10&2|2#0&1^2
        // 非标签模式：[{"score":"0","id":"4","selected":["2"]},{"score":"0","id":"5","selected":["2"]},{"score":"0","id":"6","selected":["2"]}]
        // 使用标签标签模式
//        answerInfos.stream().forEach(tmpAnswerInfo -> {
//            // answerInfos：1#10&2|2#0&1^2
//            String questionNo = tmpAnswerInfo.split(KYC_QUESTION_N0_SCORE_SEPARATOR)[0];
//            String answerNos = tmpAnswerInfo.split(KYC_SCORE_ANSWER_NO_SEPARATOR)[1];
//            List<String> answerNoList = Arrays.asList(answerNos.split(KYC_ANSWER_SEPARATOR));
//            List<String> answerTextList = new ArrayList<>(answerNoList.size());
//            for (KycQuestionVO kycQuestionVO : questionList) {
//                if (!(kycQuestionVO.getQuestionNo().equals(questionNo))) {
//                    continue;
//                }
//                List<KycAnswerVO> answerList = kycQuestionVO.getAnswerList();
//                for (KycAnswerVO kycAnswerVO : answerList) {
//                    if (answerNoList.contains(kycAnswerVO.getAnswerNo())) {
//                        answerTextList.add(kycAnswerVO.getAnswerContent());
//                    }
//                }
//                InvestDemandVO investDemandVO = new InvestDemandVO();
//                investDemandVO.setDesc(kycQuestionVO.getRemark());
//                investDemandVO.setItem(String.join(INVESTDEMAND_ANSWER_SEPARATOR, answerTextList));
//                resultList.add(investDemandVO);
//            }
//        });
    }

    @Override
    public Boolean submitPaper(SubmitPaperReq submitPaperReq) {
        String paperAnswerInfo = getPaperAnswerInfoNoTag(submitPaperReq);
        SimpleAccount simpleAccount = ContextHolder.getSimpleAccount();
        boolean result = kycRemoteService.postPaperAnswer(simpleAccount, paperAnswerInfo, submitPaperReq.getPaperVersion(), submitPaperReq.getInvestOrganNo());
        if (result) {
            log.info("delete testure,clientId:{}", simpleAccount.getClientId());
            kycPaperInfoService.delTestjourCache(simpleAccount, submitPaperReq.getInvestOrganNo());
        }

        return result;
    }

    @Override
    public List<InvestOrganVO> queryInvestOrgans() {
        return INVEST_ORGAN_LIST;
    }

    @Override
    public InvestOrganPageVO queryInvestOrganInfo(String investOrganNo) {
        InvestOrganVO investOrganVO = INVEST_ORGAN_MAP.get(investOrganNo);
        if (investOrganVO == null) {
            log.error("query invest organ error,investOrganNo:{}", investOrganNo);
            return null;
        }
        return investOrganConvert(investOrganVO);
    }

    private InvestOrganPageVO investOrganConvert(InvestOrganVO investOrganVO) {
        InvestOrganPageVO investOrganPageVO = new InvestOrganPageVO();
        BeanUtil.copyProperties(investOrganVO, investOrganPageVO);
        return investOrganPageVO;
    }

    /**
     * kyc问卷模式（1-标签模式（默认））：1#10&2|2#0&1^2| #前是题号，#和&之间是获得的分数，现在写死0，&后是选择的答案编号，如果存在多个答案用^分割，|表示一题的答案结束
     *
     * @param submitPaperReq
     * @return
     */
    public String getPaperAnswerInfo(SubmitPaperReq submitPaperReq) {
        List<PaperAnswer> paperAnswers = submitPaperReq.getPaperAnswers();
        if (CollectionUtils.isEmpty(paperAnswers)) {
            throw new BizException(BizErrorCodeEnum.KYC_PAPER_ANSWER_ERROR);
        }
        List<String> answerinfos = new ArrayList<>();
        for (PaperAnswer paperAnswer : paperAnswers) {
            List<String> answers = paperAnswer.getAnswerNos();
            String answer = String.join(KYC_ANSWER_JOINER, answers);
            answerinfos.add(String.format(KYC_ANSWER_PATTERN, paperAnswer.getQuestionNo(), answer));
        }
        return String.join(KYC_QUESTION_JOIN, answerinfos);
    }

    /**
     * kyc问卷模式（0-非标签模式）：答案格式为json串[{"id":"1","score":"0","selected":["1","2"]},{"id":"2","score":"0","selected":["1"]}]
     *
     * @param submitPaperReq
     * @return
     */
    public String getPaperAnswerInfoNoTag(SubmitPaperReq submitPaperReq) {
        List<PaperAnswer> paperAnswers = submitPaperReq.getPaperAnswers();
        JSONArray jsonArray = new JSONArray();
        if (CollectionUtils.isEmpty(paperAnswers)) {
            throw new BizException(BizErrorCodeEnum.KYC_PAPER_ANSWER_ERROR);
        }
        for (PaperAnswer paperAnswer : paperAnswers) {
            SubmitAnswerInfo submitAnswerInfo = new SubmitAnswerInfo();
            submitAnswerInfo.setId(paperAnswer.getQuestionNo());
            submitAnswerInfo.setScore(DEFAULT_SCORE);
            submitAnswerInfo.setSelected(paperAnswer.getAnswerNos());
            jsonArray.add(JSONObject.toJSON(submitAnswerInfo));
        }
        return jsonArray.toJSONString();
    }


    @Cacheable(cacheNames = KYC_TESTJOUR, key = "'kyc:testjour:' + #simpleAccount.clientId + ':' + #investOrganNo", unless = "#result == null")
    public TestjourInfo queryTestjour(SimpleAccount simpleAccount, String investOrganNo) {
        log.info("query testjour from t2,clientId:{},investOrganNo:{}", simpleAccount.getClientId(), investOrganNo);
        List<TestjourInfo> testjourInfos = kycRemoteService.queryTestjour(simpleAccount, investOrganNo);
        List<TestjourInfo> newTestjourInfos = testjourInfos.stream().filter(testjourInfo -> testjourInfo.getDateClear() == 0).collect(Collectors.toList());
        return CollectionUtils.isEmpty(newTestjourInfos) ? null : newTestjourInfos.get(0);
    }

    @CacheEvict(cacheNames = KYC_TESTJOUR, key = "'kyc:testjour:' + #simpleAccount.clientId + ':' + #investOrganNo")
    public void delTestjourCache(SimpleAccount simpleAccount, String investOrganNo) {
        log.info("delete testjour cache,clientId:{},investOrganNo:{}", simpleAccount.getClientId(), investOrganNo);
    }

}
