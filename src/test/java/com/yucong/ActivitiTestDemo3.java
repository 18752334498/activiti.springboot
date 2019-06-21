package com.yucong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo3 {


    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;

    // 开始任务
    @Test
    public void start() {
        Map<String, Object> variables = new HashMap<String, Object>();
        String userId = "444555";
        variables.put("userId", userId);
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("demo3", "train_java", variables);
        runtimeService.setProcessInstanceName(procInst.getProcessInstanceId(), userId);

        System.out.println("启动成功....");
    }

    // 正常执行任务
    @Test
    public void completeTaskVariablesTest2() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", "1");

        taskService.complete("365003", variables);
    }

    // 条件执行任务
    @Test
    public void completeTaskVariablesTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", "1");

        taskService.complete("307503", variables);

        ((HistoryServiceImpl) historyService).getCommandExecutor().execute(new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                HistoricTaskInstanceEntity entity = commandContext.getDbSqlSession().selectById(HistoricTaskInstanceEntity.class, "307503");
                entity.setDeleteReason("I am a hero boom");
                return null;
            }
        });
    }

    @Test
    public void findTask2() {
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()//
                .taskId("255005").singleResult();
        String id = taskInstance.getId();
        
    }

    @Test
    public void setParam() {
        ((HistoryServiceImpl) historyService).getCommandExecutor().execute(new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                HistoricTaskInstanceEntity entity = commandContext.getDbSqlSession().selectById(HistoricTaskInstanceEntity.class, "255005");
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
                HistoricProcessInstanceEntity entity = commandContext.getDbSqlSession().selectById(HistoricProcessInstanceEntity.class, "242501");
                entity.setDeleteReason("I am a hero from proceInstHistory");
                return null;
            }
        });
    }


    @Test
    public void findTask() {
        List<Comment> commentsByType = taskService.getCommentsByType("变量");
        for (Comment co : commentsByType) {
            System.out.println(co.getId() + "\t" + co.getType() + "\t" + co.getFullMessage().length());
        }

        List<HistoricTaskInstance> list = historyService.createNativeHistoricTaskInstanceQuery()//
                .sql("select * from act_hi_taskinst t where t.category_ = #{category}")//
                .parameter("category", "nice")//
                .list();

        for (HistoricTaskInstance his : list) {
            System.out.println(his.getCategory() + "\t" + his.getId() + "\t" + his.getProcessInstanceId());
        }

        System.out.println("++++++++++++++++++++++++++");

        List<HistoricProcessInstance> list2 = historyService.createNativeHistoricProcessInstanceQuery()//
                .sql("select * from act_hi_procinst t where t.business_key_ = #{key} order by t.start_time_ desc")//
                .parameter("key", "train").list();
        for (HistoricProcessInstance his : list2) {
            System.out.println(his.getDurationInMillis() + "\t" + his.getId() + "\t" + his.getProcessDefinitionId() + "\t" + his.getName());
        }

        System.out.println("==========================");
        List<HistoricVariableInstance> list3 = historyService.createHistoricVariableInstanceQuery()//
                .variableName("userId").list();

        for (HistoricVariableInstance his : list3) {
            System.out.println(his.getVariableName() + "\t" + his.getProcessInstanceId() + "\t" + his.getId());
        }
    }

    // 删除部署的某个流程
    @Test
    public void deleteDeployment() {
        processEngine.getRepositoryService().deleteDeployment("312501", true);
        processEngine.getRepositoryService().deleteDeployment("322501", true);
        processEngine.getRepositoryService().deleteDeployment("330001", true);
        processEngine.getRepositoryService().deleteDeployment("350001", true);
    }
}
