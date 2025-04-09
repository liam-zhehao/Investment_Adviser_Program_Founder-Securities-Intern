package com.foundersc.ifte.invest.adviser.web.common.serializer;

/**
 * 格式化小数点位数，保留6位
 */
public class DataFormatSerializer6Scale extends DataFormatSerializer {
    @Override
    protected String getPattern() {
        return "#0.000000";
    }
}
