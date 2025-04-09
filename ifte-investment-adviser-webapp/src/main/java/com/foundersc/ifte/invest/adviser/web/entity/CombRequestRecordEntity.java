package com.foundersc.ifte.invest.adviser.web.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 组合申请记录表
 *
 * @author wangfuwei
 * @since 2022-11-01
 */
@TableName("T_COMB_REQUEST_RECORD")
@KeySequence(value = "SEQ_COMB_REQUEST_RECORD")
@ApiModel(value = "CombRequestRecordEntity对象", description = "")
@Data
public class CombRequestRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户id")
    private String clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("激活手机号")
    private String mobile;

    @ApiModelProperty("组合代码")
    private String combCode;

    @ApiModelProperty("组合名称")
    private String combName;

    @ApiModelProperty("金额")
    private BigDecimal balance;

    @ApiModelProperty("资产账户")
    private String fundAccount;

    @ApiModelProperty("分支机构")
    private String branchNo;

    @ApiModelProperty("系统节点编号")
    private String sysNo;

    @ApiModelProperty("站点地址")
    private String opStation;

    @ApiModelProperty("操作结果")
    private String resp;

    @ApiModelProperty("操作编码：1签约 2 追加投资 3 减少投资 4 解约 5 撤单")
    private String operCode;

    @ApiModelProperty("操作说明")
    private String operDesc;

    @ApiModelProperty("执行操作的服务器ip")
    private String operIp;

    @ApiModelProperty("月份")
    private Integer createMonth;

    @ApiModelProperty("赎回比例")
    private BigDecimal redeemRatio;

    @ApiModelProperty("链路id")
    private String traceId;

    @ApiModelProperty("原组合转入申请编号")
    private String origCombRequestNo;

    @ApiModelProperty("组合申请编号")
    private String combRequestNo;

    @ApiModelProperty("错误编号")
    private String errorNo;

    @ApiModelProperty("错误信息")
    private String errorInfo;
}
