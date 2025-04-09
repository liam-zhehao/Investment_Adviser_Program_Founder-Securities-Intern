package com.foundersc.ifte.invest.adviser.web.controller;

import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.model.trade.*;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 交易相关
 */
@RestController
@RequestMapping("/api/investAdviser/clientAuth/trade")
@Api(tags = {"交易相关逻辑"})
public class TradeController {
    @Autowired
    private TradeService tradeService;


    @GetMapping("/preCheck")
    @ApiOperation("交易前检查")
    public Response<TradePreCheckResultVO> preCheck(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.preCheck(combineCode));
    }

    @GetMapping("/newPreCheck")
    @ApiOperation("交易前检查")
    public Response<TradePreCheckResultVO> newPreCheck(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode,
                                                       @ApiParam(value = "交易类型", required = true) @RequestParam(value = "tradeType") Integer tradeType) {
        return Response.ok(tradeService.newPreCheck(combineCode, tradeType));
    }

    @GetMapping("/eligibility/check")
    @ApiOperation("适当性匹配")
    public Response<EligibilityCheckResultVO> checkEligibility(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.checkEligibility(combineCode));
    }

    @GetMapping("/epaper/list")
    @ApiOperation("获取电子协议列表")
    public Response<List<EpaperVO>> listEpaper(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.listEpaper(combineCode));
    }

    @PostMapping("/epaper/sign")
    @ApiOperation("签署电子协议")
    public Response<Boolean> signEpaper(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.signEpaper(combineCode));
    }

    @GetMapping("/info/purchase")
    @ApiOperation("转入页面的相关信息")
    public Response<PurchaseInfoVO> purchaseInfo(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.getPurchaseInfo(combineCode));
    }

    @GetMapping("/info/enableBalance")
    @ApiOperation("可用余额")
    public Response<EnableBalanceVO> getEnableBalance() {
        return Response.ok(tradeService.getEnableBalance());
    }

    @PostMapping("/purchase")
    @ApiOperation("购买")
    public Response<PurchaseResultVO> purchase(@Valid @RequestBody PurchaseReq purchaseReq) {
        return Response.ok(tradeService.purchase(purchaseReq));
    }

    @GetMapping("/time/check")
    @ApiOperation("校验交易时间")
    public Response<TimeCheckResultVO> checkTime(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "prodCode") String prodCode) {
        return Response.ok(tradeService.checkTime(prodCode));
    }


    @PostMapping("/revoke")
    @ApiOperation("撤单")
    public Response<RevokeResultVO> revoke(@Valid @RequestBody RevokeReq revokeReq) {
        return Response.ok(tradeService.revoke(revokeReq));
    }

    @GetMapping("/info/redeem")
    @ApiOperation("赎回页面的相关信息")
    public Response<RedeemInfoVO> redeemInfo(@ApiParam(value = "组合编号", required = true) @RequestParam(value = "combineCode") String combineCode) {
        return Response.ok(tradeService.getRedeemInfo(combineCode));
    }

    @PostMapping("/redeem")
    @ApiOperation("赎回")
    public Response<RedeemResultVO> redeem(@Valid @RequestBody RedeemReq redeemReq) {
        return Response.ok(tradeService.redeem(redeemReq));
    }
}
