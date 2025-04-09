package com.foundersc.ifte.invest.adviser.web.enums;

import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.ALARM_PRE;

/**
 * 日志告警状态码
 */
@Getter
public enum AlarmErrorEnum {
    PARAM_ERROR("1001","参数有误"),
    SAVE_COMB_REQUEST_RECORD_ERROR("3001", "保存组合申请记录失败");


    private String code;
    private String desc;

    AlarmErrorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return StringUtils.join(ALARM_PRE, code, "【", desc, "】");
    }

    /**
     * 告警信息
     *
     * @param bizErrorCodeEnum
     * @return
     */
    public static String getAlarmInfo(BizErrorCodeEnum bizErrorCodeEnum) {
        return StringUtils.join(ALARM_PRE, bizErrorCodeEnum.getCode(), "【", bizErrorCodeEnum.getDesc(), "】");
    }

    /**
     * 告警信息
     *
     * @param bizException
     * @return
     */
    public static String getAlarmInfo(BizException bizException) {
        return StringUtils.join(ALARM_PRE, bizException.getCode(), "【", bizException.getMsg(), "】");
    }

    /**
     * 告警信息
     *
     * @param businessException
     * @return
     */
    public static String getAlarmInfo(BusinessException businessException) {
        return StringUtils.join(ALARM_PRE, businessException.getCode(), "【", businessException.getMessage(), "】");
    }

    /**
     * 告警信息
     *
     * @return
     */
    public String getAlarmInfo() {
        return StringUtils.join(ALARM_PRE, getCode(), "【", getDesc(), "】");
    }
}
