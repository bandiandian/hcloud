package com.haier.interconn.hcloud.mybatis;

import com.github.pagehelper.PageHelper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * DataSource、SqlSessionFactory和Transaction Manager 配置
 * @author bandd
 *
 */
@Configuration
@ConditionalOnClass(TransactionManagementConfigurer.class)
@EnableTransactionManagement
@EnableConfigurationProperties({MyDataSourceConfigProperties.class,MyBatisConfigProperties.class})
public class MyBatisConfig implements TransactionManagementConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);

    @Autowired
    private MyDataSourceConfigProperties myDataSourceConfigProperties;

    @Autowired
    private MyBatisConfigProperties myBatisConfigProperties;

    /**
     * 配置dataSource，使用Hikari连接池
     */
    @Bean(destroyMethod = "close")
    @Primary
    public DataSource dataSource1(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(myDataSourceConfigProperties.getDriverClassName());
        config.setJdbcUrl(myDataSourceConfigProperties.getJdbcUrl());
        config.setUsername(myDataSourceConfigProperties.getUsername());
        config.setPassword(myDataSourceConfigProperties.getPassword());
        config.setConnectionTimeout(myDataSourceConfigProperties.getConnectionTimeout());
        config.setIdleTimeout(myDataSourceConfigProperties.getIdleTimeout());
        config.setMaxLifetime(myDataSourceConfigProperties.getMaxLifetime());
        config.setMaximumPoolSize(myDataSourceConfigProperties.getMaximumPoolSize());
        config.setConnectionTestQuery("SELECT 1");

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }



    /**
     * 配置SqlSessionFactory：
     * - 创建SqlSessionFactoryBean，并指定一个dataSource；
     * - 设置这个分页插件：https://github.com/pagehelper/Mybatis-PageHelper；
     * - 指定mapper文件的路径；
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory1() {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource1());

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("dialect", myBatisConfigProperties.getDialect());
        properties.setProperty("reasonable", myBatisConfigProperties.getReasonable());
        properties.setProperty("pageSizeZero", myBatisConfigProperties.getPageSizeZero());
        pageHelper.setProperties(properties);
        bean.setPlugins(new Interceptor[]{pageHelper});

        try {
            //指定mapper xml目录
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            bean.setMapperLocations(resolver.getResources(myBatisConfigProperties.getMapperLocations()));
            return bean.getObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }



    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager dtm1 = new DataSourceTransactionManager(dataSource1());
        return dtm1;
    }



}