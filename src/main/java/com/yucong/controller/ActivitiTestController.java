package com.yucong.controller;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

// @RestController
// @RequestMapping("/activiti")
public class ActivitiTestController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @GetMapping("/test2")
    public void demo2() {

        // 根据bpmn文件部署流程
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("demo2.bpmn").deploy();
        // 获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        // 启动流程定义，返回流程实例
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());
        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);

        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        System.out.println("第一次执行前，任务名称：" + task.getName());
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        System.out.println("第二次执行前，任务名称：" + task.getName());
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        System.out.println("task为null，任务执行完毕：" + task);

        long count = historyService.createHistoricTaskInstanceQuery().finished().count();
        System.out.println("count: " + count);

    }

    @GetMapping("/test3")
    public String demo3() {

        Deployment deploy = repositoryService.createDeployment().addClasspathResource("demo3.bpmn").addClasspathResource("demo3.png").name("新的请假流程").deploy();
        System.out.println(deploy.getId() + "\t" + deploy.getName());



        return "success";
    }
}

