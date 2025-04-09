package com.foundersc.ifte.invest.adviser.web.util;

/**
 * Created by qiujiangxu on 2017/9/18.
 */
public class AppVersionUtil {

    public static boolean isAppVersionEnough(String productVersion, String appVersion) {
        if (productVersion == null || appVersion == null) {
            return false;
        }
        String[] pVersionStr = productVersion.split("\\.");
        String[] aVersionStr = appVersion.split("\\.");
        if (pVersionStr.length != aVersionStr.length) {
            return false;
        }
        for (int i = 0; i < pVersionStr.length; i++) {
            try {
                if (Integer.parseInt(pVersionStr[i]) == Integer.parseInt(aVersionStr[i])) {
                    continue;
                }
            } catch (Exception ignored) {
                return false;
            }
            return Integer.parseInt(pVersionStr[i]) < Integer.parseInt(aVersionStr[i]);
        }
        return true;
    }

}
