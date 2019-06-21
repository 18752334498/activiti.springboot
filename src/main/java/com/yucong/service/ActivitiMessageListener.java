package com.yucong.service;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Component
public class ActivitiMessageListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case PROCESS_COMPLETED:
                System.out.println("======================= 任务结束 ==========================");
                System.out.println("=========================" + event.getProcessInstanceId() + "=======================");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
