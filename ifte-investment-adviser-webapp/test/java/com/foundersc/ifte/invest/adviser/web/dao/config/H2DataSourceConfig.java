package com.foundersc.ifte.invest.adviser.web.dao.config;

import cn.hutool.core.io.FileUtil;
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
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @date 2022/6/6
 * @since 1.0
 */
@Slf4j
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.foundersc.ifte.invest.adviser.web.service.impl.dao"})
public class H2DataSourceConfig {
    @Bean
    public JdbcConnectionPool dataSource() throws Exception {
        String schemaSql = FileUtil.readString(new ClassPathResource("h2/schema.sql").getFile(), Charset.forName("utf-8"));
        String dataSql = FileUtil.readString(new ClassPathResource("h2/data.sql").getFile(), Charset.forName("utf-8"));
        JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create("jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1", "root", "root");
        Connection connection = null;
        Statement statement = null;
        try {
            connection = jdbcConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(schemaSql);
            statement.execute(dataSql);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }


        return jdbcConnectionPool;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, GlobalConfig globalConfig) throws Exception {
        log.info("sqlSessionFactory load");
        // 构建MyBatis的拦截器对象
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器，并指定数据库类型为ORACLE
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE));
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor, pageInterceptor());
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
    public GlobalConfig globalConfig(@Qualifier("mybatisColumnsHandler") MetaObjectHandler metaObjectHandler) {
        GlobalConfig conf = new GlobalConfig();
        List<IKeyGenerator> keyGenerators = new ArrayList<>();
        keyGenerators.add(new OracleKeyGenerator());
        conf.setDbConfig(new GlobalConfig.DbConfig()
                .setLogicDeleteField("deleted")
                .setKeyGenerators(keyGenerators)
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
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "oracle9i");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }
}
