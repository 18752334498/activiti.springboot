package com.yucong.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationContext implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public <T> T getBeanByClass(Class<T> clazz) {
		T bean = applicationContext.getBean(clazz);
		return bean;
	}

	public Object getBeanByName(String name) {
		Object bean = applicationContext.getBean(name);
		return bean;
	}

}
