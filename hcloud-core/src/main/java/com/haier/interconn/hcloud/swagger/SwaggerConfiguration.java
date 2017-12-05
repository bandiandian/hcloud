package com.haier.interconn.hcloud.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017/8/4  14:40
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties({SwaggerProperty.class})
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {


//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("webjars/springfox-swagger-ui/*")
//                .addResourceLocations("classpath:/META-INF/resources/webjars.springfox-swagger-ui/");
//    }


    @Resource
    private SwaggerProperty  swaggerProperty;
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .select()
                .apis(requestHandler -> {
                    String packageName = requestHandler.getHandlerMethod().getMethod()
                            .getDeclaringClass().getPackage().getName();

                    return packageName.startsWith(swaggerProperty.getScanPackage())&&requestHandler.getHandlerMethod().getMethod()
                          .isAnnotationPresent(ApiOperation.class);
                })
                .paths(PathSelectors.any())
                .build();

    }


    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title(swaggerProperty.getTitle())
                .description("")
//                .termsOfServiceUrl("http://localhost:0890/")
                .contact(swaggerProperty.getCreateBy())
                .version(swaggerProperty.getVersion())
                .build();

    }
}
