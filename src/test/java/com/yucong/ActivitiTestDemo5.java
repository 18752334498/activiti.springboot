package com.yucong;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo5 {

    @Autowired
    private ProcessEngine processEngine;

    // 部署流程
    @Test
    public void deploy() {
        // 获取部署相关service，这些都是activiti封装好的api接口，还有很多，下面也会用到很多
        Deployment deployment = processEngine.getRepositoryService()
                // 创建部署
                .createDeployment()
                // 加载流程图资源文件
                .addClasspathResource("processes/demo5.bpmn")
                // 加载流程图片
                .addClasspathResource("processes/demo5.png")
                // 流程名称
                .name("并行审批-角色")
                // 部署流程
                .deploy();
        System.out.println("流程部署的ID: " + deployment.getId());
        System.out.println("流程部署的Name: " + deployment.getName());
    }

    // 开始流程
    @Test
    public void startByProcessInstanceByKey() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("inputUser", "2344444444444"); // 放入的value会与act_id_user中的 ID_ 对应
        variables.put("bianliang", "train_1"); // 放入的value会与act_id_user中的 ID_ 对应

        // 启动流程，指定是哪个bpmn文件对应的流程
        ProcessInstance pi = processEngine.getRuntimeService()
                // 定义流程表的KEY字段值,key值是我们前面定义好的key，可在act_re_procdef表中的key_字段中找到，
                .startProcessInstanceByKey("并行审批-角色", variables);
        System.out.println(pi.getId());
        System.out.println(pi.getProcessDefinitionId());

        processEngine.getRuntimeService()//
                .setProcessInstanceName(pi.getProcessInstanceId(), "aaasssddd");

    }

    // 开始流程
    @Test
    public void startByProcessInstanceByKey2() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("userId", "444444"); // 放入的value会与act_id_user中的 ID_ 对应
        variables.put("subject", "java");

        // 启动流程，指定是哪个bpmn文件对应的流程
        ProcessInstance pi = processEngine.getRuntimeService()
                .startProcessInstanceByKey("培训流程", variables);
        System.out.println(pi.getId());
        System.out.println(pi.getProcessDefinitionId());

        Task task = processEngine.getTaskService()//
                .createTaskQuery()//
                .processInstanceId(pi.getProcessInstanceId())//
                .singleResult();

        System.out.println(task.getAssignee() + "\t" + task.getId());

    }

    // 正常执行任务
    @Test
    public void completeTaskVariablesTest3() {
        TaskService taskService = processEngine.getTaskService();

        taskService.complete("225005"); // act_ru_task表的主键 ID_
        taskService.complete("225010"); // act_ru_task表的主键 ID_

    }

    // 添加参数执行任务
    @Test
    public void completeTaskVariablesTest2() {
        TaskService taskService = processEngine.getTaskService();
        taskService.addComment("192513", "190001", "train_1_190001", "I will be back");
        taskService.complete("192513");
    }

    // 条件执行任务
    @Test
    public void completeTaskVariablesTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("days", 5);
        processEngine.getTaskService().complete("30005", variables); // act_ru_task表的主键 ID_
    }

    /**
     * 获取流程变量数据
     */
    @Test
    public void getVariableValues() {
        TaskService taskService = processEngine.getTaskService();// 获取任务
        List<Comment> comments = taskService.getProcessInstanceComments("190001", "train_1_190001");
        for (Comment co : comments) {
            System.out.println(co.getFullMessage() + "\t" + co.getProcessInstanceId() + "\t" + co.getId());
        }

    }

    @Test
    public void findTask() {
        TaskService taskService = processEngine.getTaskService();// 获取任务
        Task result = taskService.createTaskQuery().processDefinitionName("并行审批-角色").singleResult();
        // Task result = taskService.createTaskQuery().processDefinitionId("培训流程").singleResult();
        System.out.println(result.getName());
        System.out.println(result.getProcessInstanceId());
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
        processEngine.getRepositoryService().deleteDeployment("207501", true);
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
