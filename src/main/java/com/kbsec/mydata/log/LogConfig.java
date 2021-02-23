package com.kbsec.mydata.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LogConfig {

	@Pointcut("@annotation(LogAnnotation)")
	public void GetMapping(){
		log.error("!!!!!!");
	}

	//org.springframework.web.bind.annotation
	//@Before("@annotation(com.youandi.backend.config.LogAnnotation)")
	@Before("@annotation(LogAnnotation)")
	
	public void test(JoinPoint joinPoint) {
		joinPoint.getTarget();
		log.error("!!!!!!");
	}
	
	@Around("@annotation(LogAnnotation)") 
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable{ 
		log.info("Before Execute Method!!!"); 
		Object proceed = joinPoint.proceed(); 
		log.info("After Execute Method!!!!"); 
		return proceed; 
	}



}
