package com.yucong.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Autowired
    private ActivitiMessageListener activitiMessageListener;

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        List<ActivitiEventListener> activitiEventListener = new ArrayList<ActivitiEventListener>();
        activitiEventListener.add(activitiMessageListener);// 配置全局监听器
        processEngineConfiguration.setEventListeners(activitiEventListener);
    }

}
