package com.foundersc.ifte.invest.adviser.web.util;

import com.foundersc.ifc.common.databind.DataBind;
import com.foundersc.ifc.common.databind.DataBindManager;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于保存一次http请求的上下文参数
 */
@Data
public class ContextHolder {
    /**
     * 用于保存不同请求时的客户号登录信息
     */
    private static DataBind<SimpleAccount> simpleAccountDataBind = DataBindManager.getInstance().getDataBind(DataBindTypeEnum.SIMPLE_ACCOUNT);
    /**
     * 用于保存不同请求时的手机号登录信息
     */
    private static DataBind<MobileLoginInfo> mobileLoginInfoDataBind = DataBindManager.getInstance().getDataBind(DataBindTypeEnum.MOBILE_LOGIN_INFO);
    /**
     * 用于保存app相关信息
     */
    private static DataBind<AppInfo> appInfoDataBind = DataBindManager.getInstance().getDataBind(DataBindTypeEnum.APP_INFO);

    /**
     * 设置当前请求的客户号登录信息
     *
     * @param simpleAccount
     */
    public static void setSimpleAccount(SimpleAccount simpleAccount) {
        simpleAccountDataBind.put(simpleAccount);
    }

    /**
     * 获取当前请求的客户号登录信息
     *
     * @return
     */
    public static SimpleAccount getSimpleAccount() {
        return simpleAccountDataBind.get();
    }

    /**
     * 清除当前请求的客户号登录信息
     */
    public static void removeSimpleAccount() {
        simpleAccountDataBind.remove();
    }

    /**
     * 设置当前请求的手机号登录信息
     *
     * @param mobileLoginInfo
     */
    public static void setMobileLoginInfo(MobileLoginInfo mobileLoginInfo) {
        mobileLoginInfoDataBind.put(mobileLoginInfo);
    }

    /**
     * 获取当前请求的手机号登录信息
     *
     * @return
     */
    public static MobileLoginInfo getMobileLoginInfo() {
        return mobileLoginInfoDataBind.get();
    }

    /**
     * 清除当前请求的手机号登录信息
     */
    public static void removeMobileLoginInfo() {
        mobileLoginInfoDataBind.remove();
    }

    /**
     * 设置appInfo
     *
     * @param appInfo
     */
    public static void setAppInfo(AppInfo appInfo) {
        appInfoDataBind.put(appInfo);
    }

    /**
     * 获取AppInfo
     *
     * @return
     */
    public static AppInfo getAppInfo() {
        return appInfoDataBind.get();
    }

    /**
     * 清除appInfo
     */
    public static void removeAppInfo() {
        appInfoDataBind.remove();
    }


    /**
     * 清除当前请求的所有信息
     */
    public static void clear() {
        simpleAccountDataBind.remove();
        mobileLoginInfoDataBind.remove();
        appInfoDataBind.remove();
    }

    /**
     * 绑定类型
     */
    public enum DataBindTypeEnum {
        /**
         * 手机号登录后的信息
         */
        MOBILE_LOGIN_INFO,
        /**
         * 客户号登录后的相关信息
         */
        SIMPLE_ACCOUNT,
        /**
         * app相关信息，包括设备号，操作系统，app版本号等
         */
        APP_INFO
    }

    /**
     * 手机号登录之后的信息
     */
    @Data
    @AllArgsConstructor
    public static class MobileLoginInfo {
        /**
         * 用户唯一标识（如果可以获取到，比如微博uid）
         */
        String uid;

        /**
         * 登录的手机号
         */
        String mobile;
    }

    /**
     * app相关信息
     */
    @Data
    @AllArgsConstructor
    public static class AppInfo {
        /**
         * 设备号
         */
        private String deviceId;

        /**
         * 操作系统
         */
        private String os;

        /**
         * app版本号
         */
        private String version;
    }
}
