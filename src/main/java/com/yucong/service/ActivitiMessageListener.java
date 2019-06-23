package com.yucong.service;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Component
public class ActivitiMessageListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
		case PROCESS_COMPLETED:// 流程已结束

			System.out.println("======================= 任务结束 ==========================");
			break;
		case ACTIVITY_COMPLETED:// 一个节点成功结束
			System.out.println("======================== 一个节点成功结束  =======================");
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
