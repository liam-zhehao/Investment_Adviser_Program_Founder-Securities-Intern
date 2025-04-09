package com.foundersc.ifte.invest.adviser.web.service.impl;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.LongCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;

import javax.annotation.PostConstruct;

@Service
public class OtelService {

    private Meter meter;
    private LongCounter everydayCustomerCounter;
    private MeterRegistry meterRegistry;

    @Autowired
    private OpenTelemetry openTelemetry;


    // 初始化自定义指标
    @PostConstruct
    public void init() {
        this.meter = openTelemetry.getMeter("new-customer-metrics");
        // 初始化 Counter 用于记录总访问量
        everydayCustomerCounter = meter.counterBuilder("everyday_customer")
                .setDescription("Number of customers using the adviser service every day")
                .setUnit("people")
                .build();

    }

    public void incrementEverydayCustomerCounter(long count) {
        everydayCustomerCounter.add(count);
    }

}

