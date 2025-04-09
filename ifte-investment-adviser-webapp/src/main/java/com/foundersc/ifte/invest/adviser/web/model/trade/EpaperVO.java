package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子协议
 */
@Data
@ApiModel(value = "EpaperVO", description = "电子协议")
@NoArgsConstructor
public class EpaperVO {
    @ApiModelProperty("模板编码")
    private String templateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板类别")
    private String templateType;

    @ApiModelProperty("图片编号")
    private String imageNo;

    @ApiModelProperty("顺序号")
    private Integer orderNo;

    @ApiModelProperty("版本号")
    private String versionNo;

    @ApiModelProperty("base64编码的pdf文件")
    private String imageData;

    @ApiModelProperty("文件类别")
    private String fileType;

    public EpaperVO(EpaperDTO epaperDTO) {
        this.templateId = epaperDTO.getTemplate_id();
        this.templateName = epaperDTO.getTemplate_name();
        this.templateType = epaperDTO.getTemplate_type();
        this.imageNo = epaperDTO.getImage_no();
        this.orderNo = epaperDTO.getOrder_no();
        this.versionNo = epaperDTO.getVersion_no();
    }
}