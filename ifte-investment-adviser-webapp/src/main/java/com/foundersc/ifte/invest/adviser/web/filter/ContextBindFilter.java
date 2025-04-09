package com.foundersc.ifte.invest.adviser.web.filter;

import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.*;

/**
 * 用户获取设备信息、版本号及操作系统等信息
 *
 * @author wangfuwei
 * @date 2022/10/28
 */
@Slf4j
public class ContextBindFilter extends OncePerRequestFilter {

    private final static String VER_PREFIX = "Ver:";

    public static final String VER = "Ver";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String opStation = request.getHeader(OP_STATION);
        log.info("opStation={}", opStation);
        String deviceId = getDeviceId(request);
        Map<String, String> opStationItems = getOpStationItems(opStation);
        String os = getOs(request);
        String appVersion = getAppVersion(request, opStationItems);
        ContextHolder.AppInfo appInfo = new ContextHolder.AppInfo(deviceId, os, appVersion);
        ContextHolder.setAppInfo(appInfo);
        log.info("uri={}, bind appInfo={}", request.getRequestURI(), appInfo);
        filterChain.doFilter(request, response);
        ContextHolder.removeAppInfo();
    }

    /**
     * 获取app版本号
     *
     * @param request
     * @return
     */
    private String getAppVersion(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT);
        String version = "7.0.0";
        if (StringUtils.isNotBlank(userAgent) && isApp(userAgent)) {
            String terminal = userAgent.split("\\s+")[0];
            String[] terSplit = terminal.split("/");
            if (terSplit.length > 1) {
                version = terSplit[1];
            } else if (terSplit.length == 1) {
                version = terminal.replace("xf", "");
            } else {
                log.info("user default version={}", version);
            }
        }
        return version;
    }

    /**
     * 获取app版本号
     *
     * @param request
     * @param opStationItems
     * @return
     */
    private String getAppVersion(HttpServletRequest request, Map<String, String> opStationItems) {
        String userAgent = request.getHeader(USER_AGENT);
        String opStation = request.getHeader(OP_STATION);
        String version = "7.0.0";
        String ver = opStationItems.get(VER);
        if (StringUtils.isNotBlank(ver)) {
            return ver;
        }
        if (StringUtils.isNotBlank(userAgent) && isApp(userAgent)) {
            String terminal = userAgent.split("\\s+")[0];
            String[] terSplit = terminal.split("/");
            if (terSplit.length > 1) {
                version = terSplit[1];
            } else if (terSplit.length == 1) {
                version = terminal.replace("xf", "");
            } else {
                log.info("user default version={}", version);
            }
        } else if (StringUtils.isNotBlank(opStation)) {
            String[] splits = opStation.split(",");
            for (String split : splits) {
                if (split.startsWith(VER_PREFIX)) {
                    version = split.substring(VER_PREFIX.length());
                    break;
                }
            }
        }
        return version;
    }

    /**
     * 设备号
     *
     * @param request
     * @return
     */
    private String getDeviceId(HttpServletRequest request) {
        return request.getHeader(DEVICE_ID);
    }

    /**
     * 是否为app
     *
     * @param userAgent
     * @return
     */
    private boolean isApp(String userAgent) {
        return StringUtils.isNotBlank(getOs(userAgent));
    }

    /**
     * 操作系统
     *
     * @param userAgent
     * @return
     */
    private String getOs(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return StringUtils.EMPTY;
        }
        String lowerCaseUserAgent = userAgent.toLowerCase();
        if (lowerCaseUserAgent.contains(IOS)) {
            return IOS;
        } else if (lowerCaseUserAgent.contains(ANDROID)) {
            return ANDROID;
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 操作系统
     *
     * @param request
     * @return
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT);
        return getOs(userAgent);
    }

    /**
     * 从op-station中提取key-value
     *
     * @param opStation
     * @return
     */
    private Map<String, String> getOpStationItems(String opStation) {
        Map<String, String> map = new HashMap<>(16);
        if (StringUtils.isBlank(opStation)) {
            return map;
        }
        try {
            String[] opStationsItems = opStation.split(",");
            for (String opStationsItem : opStationsItems) {
                int index = opStationsItem.indexOf(":");
                if (index > 0 && index < opStationsItem.length() - 1) {
                    map.put(opStationsItem.substring(0, index), opStationsItem.substring(index + 1));
                }
            }
        } catch (Exception e) {
            log.warn("解析op-station失败");
        }
        return map;
    }
}
