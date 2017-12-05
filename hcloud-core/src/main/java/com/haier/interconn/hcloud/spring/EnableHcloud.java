package com.haier.interconn.hcloud.spring;

import com.haier.interconn.hcloud.endpoint.EndPointAutoConfig;
import com.haier.interconn.hcloud.mybatis.MyBatisConfig;
import com.haier.interconn.hcloud.ribbon.CoreAutoConfiguration;
import com.haier.interconn.hcloud.swagger.SwaggerConfiguration;
import com.haier.interconn.hcloud.trace.CustomTraceRespositoryConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *启用hcloud框架注解
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-10-09  13:58
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ CustomTraceRespositoryConfiguration.class, EndPointAutoConfig.class,CoreAutoConfiguration.class,SwaggerConfiguration.class})
public @interface EnableHcloud {

}
