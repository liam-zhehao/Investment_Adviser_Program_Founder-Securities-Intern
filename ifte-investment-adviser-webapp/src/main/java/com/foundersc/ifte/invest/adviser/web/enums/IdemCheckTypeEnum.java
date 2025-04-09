package com.foundersc.ifte.invest.adviser.web.enums;

import lombok.Getter;

/**
 * 幂等性校验业务类型
 *
 * @author wangfuwei
 * @date 2022/11/4
 */
@Getter
public enum IdemCheckTypeEnum {
    SIGN_EPAPER("sign_epaper", "签署电子合同", 5),
    PURCHASE("purchase", "转入", 5),
    REDEEM("redeem", "转出", 5),
    REVOKE("revoke", "撤单", 5)
    ;
    /**
     * 业务编码
     */
    private String code;

    /**
     * 业务描述
     */
    private String desc;

    /**
     * 过期时间
     */
    private int expireSeconds;

    IdemCheckTypeEnum(String code, String desc, int expireSeconds) {
        this.code = code;
        this.desc = desc;
        this.expireSeconds = expireSeconds;
    }
}
