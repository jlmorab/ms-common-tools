package com.jlmorab.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages = "com.jlmorab.ms")
public class SpringApplicationCommon {
	
	public void init(String[] args) { 
	    SpringApplication.run(SpringApplicationCommon.class, args);
	}//end init()
	
}
