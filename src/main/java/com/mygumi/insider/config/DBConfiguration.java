package com.mygumi.insider.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.mygumi.insider.mapper")
public class DBConfiguration {
	
}