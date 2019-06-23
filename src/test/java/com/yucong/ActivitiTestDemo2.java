package com.yucong;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo2 {

	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;

	// 开始任务
	@Test
	public void start() {
		Map<String, Object> variables = new HashMap<String, Object>();
		String userId = "777777";
		variables.put("userId", userId);
		ProcessInstance procInst = runtimeService.startProcessInstanceByKey("demo2", "train_java", variables);
		runtimeService.setProcessInstanceName(procInst.getProcessInstanceId(), userId);
		System.out.println("启动成功....");
	}

	// 正常执行任务
	@Test
	public void completeTaskVariablesTest2() {
		Map<String, Object> variables = new HashMap<String, Object>();

		taskService.setVariable("165003", "refuseReason", "what the fuck ..............");

		variables.put("pass", "1");
		taskService.complete("165003", null);
	}

	// 条件执行任务
	@Test
	public void completeTaskVariablesTest() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("pass", "1");

		taskService.complete("197505", variables);
	}

	// 审批
	@Test
	public void findTask2() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("pass", "1");

		taskService.complete("197505", variables);

	}

	@Test
	public void setParam() {
		((HistoryServiceImpl) historyService).getCommandExecutor().execute(new Command<String>() {
			@Override
			public String execute(CommandContext commandContext) {
				HistoricTaskInstanceEntity entity = commandContext.getDbSqlSession()
						.selectById(HistoricTaskInstanceEntity.class, "255005");
				entity.setDeleteReason("I am a hero 流程2");
				return null;
			}
		});
	}

	@Test
	public void setParam2() {
		((HistoryServiceImpl) historyService).getCommandExecutor().execute(new Command<String>() {
			@Override
			public String execute(CommandContext commandContext) {
				HistoricProcessInstanceEntity entity = commandContext.getDbSqlSession()
						.selectById(HistoricProcessInstanceEntity.class, "242501");
				entity.setDeleteReason("I am a hero from proceInstHistory");
				return null;
			}
		});
	}


	// 删除部署的某个流程
	@Test
	public void deleteDeployment() {
		processEngine.getRepositoryService().deleteDeployment("187501", true);
	}
}
