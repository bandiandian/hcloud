package com.haier.interconn.hcloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;

import com.haier.interconn.hcloud.zuul.filter.AccessFilter;

@EnableZuulProxy
@SpringCloudApplication
public class Zserver {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Zserver.class).web(true).run(args);
	}
	
	
	@RefreshScope
	@ConfigurationProperties("zuul")
	public ZuulProperties zuulProperties(){
		return new ZuulProperties();
	}
	
	@Bean
	public AccessFilter accessFilter(){
		return new AccessFilter();
	}
}