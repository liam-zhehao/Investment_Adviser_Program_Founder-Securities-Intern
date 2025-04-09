package com.foundersc.ifte.invest.adviser.dubbo.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author wangfuwei
 * @date 2023/10/24
 */
@Slf4j
public class AppVersionUtil {
    /**
     * 小于前缀
     */
    private static final String LESS_THAN_PREFIX = "lt_";

    /**
     * 小于等于前缀
     */
    private static final String LESS_EQUAL_THAN_PREFIX = "le_";

    /**
     * 等于前缀
     */
    private static final String EQUAL_PREFIX = "eq_";

    /**
     * 大于等于前缀
     */
    private static final String GREATER_EQUAL_THAN_PREFIX = "ge_";

    /**
     * 大于前缀
     */
    private static final String GREATER_THAN_PREFIX = "gt_";

    /**
     * 小于某版本号
     *
     * @param version
     * @return
     */
    public static VersionRule lessThanVersion(String version) {
        checkVersion(version);
        return new VersionRule(LESS_THAN_PREFIX + version);
    }

    /**
     * 小于等于某版本号
     *
     * @param version
     * @return
     */
    public static VersionRule lessThanOrEqualToVersion(String version) {
        checkVersion(version);
        return new VersionRule(LESS_EQUAL_THAN_PREFIX + version);
    }

    /**
     * 等于某版本号
     *
     * @param version
     * @return
     */
    public static VersionRule equalToVersion(String version) {
        checkVersion(version);
        return new VersionRule(EQUAL_PREFIX + version);
    }

    /**
     * 大于等于某版本号
     *
     * @param version
     * @return
     */
    public static VersionRule greaterThanOrEqualToVersion(String version) {
        checkVersion(version);
        return new VersionRule(GREATER_EQUAL_THAN_PREFIX + version);
    }

    /**
     * 大于某版本号
     *
     * @param version
     * @return
     */
    public static VersionRule greaterThanVersion(String version) {
        checkVersion(version);
        return new VersionRule(GREATER_THAN_PREFIX + version);
    }

    /**
     * 是否为有效版本号
     *
     * @param version
     */
    private static void checkVersion(String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("app版本号不能为空");
        }
    }

    /**
     * 版本是否匹配
     *
     * @param versionRule
     * @param version
     * @return
     */
    public static boolean isVersionMatch(VersionRule versionRule, String version) {
        if (versionRule == null) {
            return true;
        }
        if (versionRule.getRuleDesc().startsWith(LESS_THAN_PREFIX)) {
            // 判断version是否小于指定版本号
            return compare(version, versionRule.getRuleDesc().substring(LESS_THAN_PREFIX.length())) < 0;
        }
        if (versionRule.getRuleDesc().startsWith(LESS_EQUAL_THAN_PREFIX)) {
            // 判断version是否小于等于指定版本号
            return compare(version, versionRule.getRuleDesc().substring(LESS_EQUAL_THAN_PREFIX.length())) <= 0;
        }
        if (versionRule.getRuleDesc().startsWith(EQUAL_PREFIX)) {
            // 判断version是否等于指定版本号
            return compare(version, versionRule.getRuleDesc().substring(EQUAL_PREFIX.length())) == 0;
        }
        if (versionRule.getRuleDesc().startsWith(GREATER_EQUAL_THAN_PREFIX)) {
            // 判断version是否大于等于指定版本号
            return compare(version, versionRule.getRuleDesc().substring(GREATER_EQUAL_THAN_PREFIX.length())) >= 0;
        }
        if (versionRule.getRuleDesc().startsWith(GREATER_THAN_PREFIX)) {
            // 判断version是否大于指定版本号
            return compare(version, versionRule.getRuleDesc().substring(GREATER_THAN_PREFIX.length())) > 0;
        }
        return false;
    }

    /**
     * 版本规则
     */
    @Data
    public static class VersionRule {
        /**
         * 规则描述，规则必须以某个前缀开头，且
         */
        private String ruleDesc;

        protected VersionRule(String ruleDesc) {
            this.ruleDesc = ruleDesc;
        }
    }

    /**
     * app版本号比较器
     *
     * @param versionA
     * @param versionB
     * @return
     */
    public static int compare(String versionA, String versionB) {
        try {
            if (StringUtils.isEmpty(versionA) || StringUtils.isEmpty(versionB)) {
                log.info("传入版本号为空");
                return -1;
            }
            String[] versionAs = versionA.split("\\.");
            String[] versionBs = versionB.split("\\.");
            if (versionAs.length != versionBs.length || versionAs.length == 0) {
                log.error("传入版本号格式错误");
                return -1;
            }
            for (int i = 0; i < versionAs.length; i++) {
                if (!Objects.equals(Integer.valueOf(versionAs[i]), Integer.valueOf(versionBs[i]))) {
                    return Integer.valueOf(versionAs[i]) - Integer.valueOf(versionBs[i]);
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("版本比较异常，versionA={}, versionB={}", versionA, versionB, e);
            return -1;
        }
    }
}
