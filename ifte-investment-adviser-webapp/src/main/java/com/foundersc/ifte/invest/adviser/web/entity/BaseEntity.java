package com.foundersc.ifte.invest.adviser.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 基类
 */
@Data
public abstract class BaseEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_NULL)
    private Date createDate;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE, update = "sysdate")
    private Date updateDate;

}
