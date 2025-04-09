package com.foundersc.ifte.invest.adviser.web.controller;

import com.foundersc.ifte.invest.adviser.web.enums.AlarmErrorEnum;
import com.foundersc.ifte.invest.adviser.web.enums.ListTypeEnum;
import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineReq;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineStrategyVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.TargetKycInfo;
import com.foundersc.ifte.invest.adviser.web.model.trade.ClientTargetCombModeVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.FlagAndTipVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.ModifyTargetCombModeReq;
import com.foundersc.ifte.invest.adviser.web.service.TargetCombineModeService;
import com.foundersc.ifte.invest.adviser.web.service.TargetCombineService;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/investAdviser/clientAuth/target")
@Validated
@Api(tags = {"目标盈策略列表"})
public class TargetCombController {

    @Autowired
    private TargetCombineService targetCombineService;

    @Autowired
    private TargetCombineModeService targetCombineModeService;

    /**
     * @param listType 0-参与期 1-往期
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("目标盈组合列表")
    public Response<List<CombineStrategyVO>> combineList(@ApiParam(value = "列表类型", required = true) @RequestParam("listType") Integer listType) {
        if (!ListTypeEnum.allListType.contains(listType)) {
            return Response.failed(AlarmErrorEnum.PARAM_ERROR.getDesc());
        }
        List<CombineStrategyVO> combineStrategyVOs = new ArrayList<>();
        if (ListTypeEnum.CURR_LIST.getTypeId().equals(listType)) {
            combineStrategyVOs = targetCombineService.getOnSaleCombineList(DateUtil.intDate());
        } else {
            combineStrategyVOs = targetCombineService.getHisCombineList(DateUtil.intDate());
        }
        return Response.ok(combineStrategyVOs);
    }

    /**
     * 获取当前用户的当前目标盈组合的续期方式
     *
     * @param combineCode
     * @return
     */
    @GetMapping("/mode")
    @ApiOperation("续期方式查询")
    public Response<ClientTargetCombModeVO> mode(@ApiParam(value = "combineCode", required = true) @RequestParam("combineCode") String combineCode) {
        return Response.ok(targetCombineModeService.queryModeInfo(combineCode));
    }

    @PostMapping("/changeMode")
    @ApiOperation("续期方式修改")
    public Response<FlagAndTipVO> modifyMode(@RequestBody ModifyTargetCombModeReq req) {
        return Response.ok(targetCombineModeService.modifyMode(req.getCombineCode(), req.getContinueMode()));
    }


    @GetMapping("/showKycInfo")
    @ApiOperation("KYC展示信息")
    public Response<TargetKycInfo> showKycInfo(@ApiParam(value = "combineCode", required = true) @RequestParam("combineCode") String combineCode) {
        return Response.ok(targetCombineService.queryTargetKycInfo(combineCode));
    }

    @PostMapping("/commitKyc")
    @ApiOperation("KYC提交")
    public Response<TargetKycInfo> commitKyc(@RequestBody CombineReq req) {
        return Response.ok(targetCombineService.commitTargetKyc(req.getCombineCode()));
    }

}
