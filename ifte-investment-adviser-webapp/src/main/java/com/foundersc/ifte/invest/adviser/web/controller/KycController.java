package com.foundersc.ifte.invest.adviser.web.controller;

import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.model.kyc.InvestDemandInfo;
import com.foundersc.ifte.invest.adviser.web.model.kyc.InvestOrganVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.KycPaperVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.SubmitPaperReq;
import com.foundersc.ifte.invest.adviser.web.service.KycPaperInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * KYC问卷相关
 */
@RestController
@RequestMapping("/api/investAdviser/clientAuth/kyc")
@Validated
@Api(tags = {"KYC问卷"})
public class KycController {

    @Autowired
    private KycPaperInfoService kycPaperInfoService;

    @GetMapping("/paper")
    @ApiOperation("展示问卷问题")
    public Response<KycPaperVO> paper(@ApiParam("投顾机构编号") @RequestParam(value = "investOrganNo") String investOrganNo) {
        return Response.ok(kycPaperInfoService.getKycPaperInfo(investOrganNo));
    }

    @GetMapping("/hasAnswered")
    @ApiOperation("是否答过问卷")
    public Response<Boolean> hasAnswered(@ApiParam("投顾机构编号") @RequestParam(value = "investOrganNo") String investOrganNo) {
        return Response.ok(kycPaperInfoService.hasAnswered(investOrganNo));
    }

    @PostMapping("/submit")
    @ApiOperation("提交问卷答案")
    public Response<Boolean> submit(@Valid @RequestBody SubmitPaperReq submitPaperReq) {
        return Response.ok(kycPaperInfoService.submitPaper(submitPaperReq));
    }

    @GetMapping("/match")
    @ApiOperation("根据kyc问卷答案推荐组合")
    public Response match(@ApiParam("投顾机构编号") @RequestParam(value = "investOrganNo") String investOrganNo) {
        return Response.ok(kycPaperInfoService.queryRecommendCombines(investOrganNo));
    }

    @GetMapping("/investDemand")
    @ApiOperation("投资需求")
    public Response<InvestDemandInfo> investDemand(@ApiParam("投顾机构编号") @RequestParam(value = "investOrganNo") String investOrganNo) {
        return Response.ok(kycPaperInfoService.queryInvestDemand(investOrganNo));
    }

    @GetMapping("/investOrgans")
    @ApiOperation("查询已上架的投顾机构")
    public Response<List<InvestOrganVO>> getInvestOrgans() {
        return Response.ok(kycPaperInfoService.queryInvestOrgans());
    }

    @GetMapping("/investPageInfo")
    @ApiOperation("投顾机构专区信息")
    public Response<List<InvestOrganVO>> getInvestPageInfo(@ApiParam("投顾机构编号") @RequestParam(value = "investOrganNo") String investOrganNo) {
        return Response.ok(kycPaperInfoService.queryInvestOrganInfo(investOrganNo));
    }

}
