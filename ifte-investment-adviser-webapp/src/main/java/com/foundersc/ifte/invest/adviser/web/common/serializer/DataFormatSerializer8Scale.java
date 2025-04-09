package com.foundersc.ifte.invest.adviser.web.common.serializer;

/**
 * 格式化小数点位数，保留8位
 */
public class DataFormatSerializer8Scale extends DataFormatSerializer {
    @Override
    protected String getPattern() {
        return "#0.00000000";
    }
}
