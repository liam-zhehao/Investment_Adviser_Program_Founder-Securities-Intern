package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class CombineEntrustInfo implements Serializable {

    private CombineEntrust combineEntrust;

    private String errorNo;

    private String errorInfo;
}
