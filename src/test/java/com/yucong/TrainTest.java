package com.yucong;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class TrainTest {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;


    // 开始流程
    @Test
    public void startByProcessInstanceByKey() {

        // 开启流程
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("userId", "111111111111");
        variables.put("username", "张三");
        variables.put("subject", "Java基础");
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("train", variables);

        // 根据procInstId查询进行中的task
        Task task = taskService.createTaskQuery()//
                .processInstanceId(procInst.getProcessInstanceId())//
                .singleResult();

        // 申请人提交申请
        taskService.complete(task.getId(), null);

    }


    // 正常执行任务
    @Test
    public void completeTaskVariablesTest3() {
        processEngine.getTaskService().complete("82519"); // act_ru_task表的主键 ID_


        AttachmentEntity aEntity = new AttachmentEntity();

    }

    // 添加参数执行任务
    @Test
    public void completeTaskVariablesTest2() {
        TaskService taskService = processEngine.getTaskService();

        JSONObject json = new JSONObject();
        json.put("name", "Tom");
        json.put("age", "11");

        taskService.setVariables("7508", json);

        taskService.complete("7508"); // act_ru_task表的主键 ID_
    }

    // 条件执行任务
    @Test
    public void completeByCondition1() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", true);
        processEngine.getTaskService().complete("30005", variables);

    }

    // 条件执行任务
    @Test
    public void completeByCondition2() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", "dsds");
        processEngine.getTaskService().complete("110010", variables);
    }

    /**
     * 获取流程变量数据
     */
    @Test
    public void getVariableValues() {
        TaskService taskService = processEngine.getTaskService();// 获取任务
        String name = (String) taskService.getVariable("75019", "subject");// 获取请假天数
        String age = (String) taskService.getVariable("77506", "subject");// 请假原因

        System.out.println("name:" + name);
        System.out.println("age：" + age);
    }

    @Test
    public void findTask() {
        TaskService taskService = processEngine.getTaskService();// 获取任务

        List<Task> list = taskService.createTaskQuery().taskAssignee("a").listPage(0, 10);
        for (Task task : list) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
            System.out.println(task.getOwner());
            System.out.println("==============");
        }
    }

    // 获取流程图片
    @Test
    public void downloadPNG() throws Exception {
        String deploymentId = "14";
        List<String> query = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);

        String resourceName = null;
        if (!CollectionUtils.isEmpty(query)) {
            for (String string : query) {
                if (string.contains(".png")) {
                    resourceName = string;
                }
            }
        }

        if (!StringUtils.isEmpty(resourceName)) {
            InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(deploymentId, resourceName);

            File file = new File("D:/" + resourceName);
            FileUtils.copyInputStreamToFile(inputStream, file);
        }
    }

    // 查询部署表，在部署流程时，不同的bpmn文件用不同的部署名称，同一个bpmn文件产生的部署名称最好相同
    @Test
    public void queryDeployment() {
        List<Deployment> list = processEngine.getRepositoryService().createDeploymentQuery()//
                // .deploymentId(deploymentId)
                // .deploymentName(name)
                // .deploymentNameLike(nameLike)
                // .count()
                // .list()
                // .orderByDeploymenTime()
                .processDefinitionKey("并行审批").list(); // processDefinitionKey是act_re_procdef表中的key_

        for (Deployment deployment : list) {
            System.out.println(deployment.getName());
        }
    }

    // 删除部署的某个流程
    @Test
    public void deleteDeployment() {
        processEngine.getRepositoryService()//
                // .deleteDeployment(deploymentId) //只能删除没有启动的流程
                .deleteDeployment("95001", true); // 级联删除启动的流程
    }

    // 查询流程表
    @Test
    public void queryProcDef() {
        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery()//
                // .count()
                // .deploymentId(deploymentId)
                // .deploymentIds(deploymentIds)
                // .latestVersion()
                // .listPage(firstResult, maxResults)
                // .processDefinitionName(processDefinitionName)
                // .processDefinitionKey(processDefinitionKey)
                // .processDefinitionName(processDefinitionName)
                .processDefinitionKey("并行审批").list();

        for (ProcessDefinition processDefinition : list) {
            System.out.println(processDefinition.getId());
        }
    }

    // 根据用户ID查询和我相关的流程，从历史活动表查，
    @Test
    public void findHisActByUserId() {
        List<HistoricActivityInstance> list = processEngine.getHistoryService()//
                .createHistoricActivityInstanceQuery()//
                .taskAssignee("11111")//
                .list();

        for (HistoricActivityInstance his : list) {
            System.out.println(his.getId() + "\t" + his.getProcessDefinitionId());
        }
    }

    // 根据用户ID查询和我相关的流程，从任务表差
    @Test
    public void findHisTByaskUserId() {
        List<HistoricTaskInstance> list = processEngine.getHistoryService()//
                .createHistoricTaskInstanceQuery()//
                .taskAssignee("11111")//
                .list();

        for (HistoricTaskInstance his : list) {
            System.out.println(his.getId() + "\t" + his.getProcessDefinitionId());
        }
    }

    // 根据用户ID查询和我相关的流程，从任务表差
    @Test
    public void findHisVariables() {
        List<HistoricVariableInstance> list = processEngine.getHistoryService()//
                .createHistoricVariableInstanceQuery()//
                // .executionId("2501")//
                .processInstanceId("")
                .list();

        for (HistoricVariableInstance his : list) {
            System.out.println(his.getId() + "\t" + his.getVariableName() + "\t" + his.getValue());
        }
    }

    // 设置参数
    @Test
    public void setParam() {
        TaskService taskService = processEngine.getTaskService();

        String param1 = "hello";
        String taskId1 = "52505";
        taskService.setVariable(taskId1, "param1", param1);

        String param2 = "world";
        String taskId2 = "52510";
        taskService.setVariableLocal(taskId2, "param2", param2);

        taskService.complete(taskId1);
        taskService.complete(taskId2);
    }

    // 获取参数
    @Test
    public void getParam() {
        TaskService taskService = processEngine.getTaskService();
        String taskId = "55006";

        String param1 = (String) taskService.getVariable(taskId, "param1");
        String param2 = (String) taskService.getVariable(taskId, "param2");

        if (param1 != null) {
            System.out.println(param1);
        } else {
            System.out.println("param1 == null");
        }
        if (param2 != null) {
            System.out.println(param2);
        } else {
            System.out.println("param2 == null");
        }

        System.out.println("================================");

        taskService.complete(taskId);
    }

    // 获取当前任务的角色
    @Test
    public void getGroup() {

        TaskService taskService = processEngine.getTaskService();
        List<IdentityLink> list = taskService.getIdentityLinksForTask("12510");
        for (IdentityLink identityLink : list) {
            System.out.println("taskId:" + identityLink.getTaskId() //
                    + "\t" + "groupId:" + identityLink.getGroupId()//
                    + "\t" + "type:" + identityLink.getType());
        }
    }

    // 删除任务
    @Test
    public void deleteTask() {

        processEngine.getRuntimeService()//
                .deleteProcessInstance("11", "不相参加");
    }

}
