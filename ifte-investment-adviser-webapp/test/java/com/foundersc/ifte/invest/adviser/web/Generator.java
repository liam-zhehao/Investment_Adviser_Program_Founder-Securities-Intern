package com.foundersc.ifte.invest.adviser.web;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.OracleTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.OracleQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.foundersc.ifte.invest.adviser.web.entity.BaseEntity;

import java.util.Collections;

/**
 * 代码生成器
 */
public class Generator {

    public static void main(String[] args) {
        final String pkgDir = System.getProperty("user.dir") + "/src/main/java";
        final String resourceDir = System.getProperty("user.dir") + "/src/main/resources";
        final String parentPackage = "com.foundersc.ifte.invest.adviser.web";
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(
                "jdbc:oracle:thin:@//100.116.1.110:1531/zeldadb", "osoadata", "testpwd")
                .schema("OSOADATA")
                .typeConvert(new OracleTypeConvert())
                .dbQuery(new OracleQuery());

        FastAutoGenerator.create(dataSourceConfigBuilder)
                // 全局配置
                .globalConfig(builder -> {
                    builder.outputDir(pkgDir)
                            .author("wangfuwei")
                            .enableSwagger()
                            .dateType(DateType.ONLY_DATE)
                            .commentDate("yyyy-MM-dd")
                            .disableOpenDir()
                            .build();
                })
                // 包相关配置
                .packageConfig(builder -> {
                    builder.parent(parentPackage)
                            .controller("controller")
                            .serviceImpl("service.impl")
                            .service("service")
                            .entity("entity")
                            .mapper("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, resourceDir + "/mappers"))
                            .build();
                })
                // 策略配置
                .strategyConfig(builder -> {
                    //TODO 添加要生成的表
                    builder.addInclude("T_COMB_REQUEST_RECORD")
                            .addTablePrefix("T_")
                            .controllerBuilder().enableRestStyle()
                            .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl")
                            .entityBuilder().enableLombok().formatFileName("%sEntity").superClass(BaseEntity.class).addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
                            .mapperBuilder().enableMapperAnnotation().enableBaseResultMap().enableBaseColumnList()
                            .build();
                })
                // 注入配置
                .injectionConfig(builder -> {
                    builder.beforeOutputFile((tableInfo, objectMap) -> {
                        System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
                    }).build();
                })
                .execute();
    }
}
