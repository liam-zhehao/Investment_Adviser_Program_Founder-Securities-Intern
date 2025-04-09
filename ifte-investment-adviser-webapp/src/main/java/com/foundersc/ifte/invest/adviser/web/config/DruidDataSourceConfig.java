package com.foundersc.ifte.invest.adviser.web.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.foundersc.ifc.common.config.updatable.db.FZDataSource;
import com.foundersc.ifte.invest.adviser.web.config.support.NodeKeyProperties;
import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据库源配置
 */
@Configuration
@EnableTransactionManagement
@Slf4j
@MapperScan(basePackages = {"com.foundersc.ifte.invest.adviser.web.mapper"}, sqlSessionTemplateRef = "")
public class DruidDataSourceConfig {

    public static final String PROD_TRADE = "trade";

    public static final String TEST_TRADE = "trade_dev";

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, GlobalConfig globalConfig) throws Exception {
        log.info("sqlSessionFactory load");
        // 构建MyBatis的拦截器对象
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器，并指定数据库类型为ORACLE
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.OCEAN_BASE));
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mappers/*Mapper.xml"));

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);

        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 事务管理器，多数据源时事务需要指定事务管理器
     *
     * @param dataSource
     * @return
     */
    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * JDBC事务操作配置
     */
    @Bean
    public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public GlobalConfig globalConfig(@Qualifier("mybatisColumnsHandler") MetaObjectHandler metaObjectHandler) {
        GlobalConfig conf = new GlobalConfig();
        List<IKeyGenerator> keyGenerators = new ArrayList<>();
        keyGenerators.add(new OracleKeyGenerator());
        conf.setDbConfig(new GlobalConfig.DbConfig()
                .setLogicDeleteField("deleted")
                .setKeyGenerators(keyGenerators)
                .setSchema(PROD_TRADE)
                .setInsertStrategy(FieldStrategy.NOT_NULL)
                .setUpdateStrategy(FieldStrategy.NOT_NULL));
        conf.setMetaObjectHandler(metaObjectHandler);
        return conf;
    }

    /**
     * Mybatis分页插件
     *
     * @return
     */
/*    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "oracle9i");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }*/
}
