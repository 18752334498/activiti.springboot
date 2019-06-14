package com.yucong;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo4 {


    @Autowired
    private ProcessEngine processEngine;

    // 发布任务
    @Test
    public void deploy() {
        // 获取部署相关service，这些都是activiti封装好的api接口，还有很多，下面也会用到很多
        Deployment deployment = processEngine.getRepositoryService()
                // 创建部署
                .createDeployment()
                // 加载流程图资源文件
                .addClasspathResource("processes/demo4.bpmn")
                // 加载流程图片
                .addClasspathResource("processes/demo4.png")
                // 流程名称
                .name("三选一流程new")
                // 部署流程
                .deploy();
        System.out.println("流程部署的ID: " + deployment.getId());
        System.out.println("流程部署的Name: " + deployment.getName());
    }

    // 开始任务
    @Test
    public void start() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("inputUser", "11111"); // 放入的value会与act_id_user中的 ID_ 对应

        // 运行启动流程的servicee
        ProcessInstance pi = processEngine.getRuntimeService()
                // 定义流程表的KEY字段值,key值是我们前面定义好的key，可在act_re_procdef表中的key_字段中找到，
                .startProcessInstanceByKey("请假流程三选一", variables);
        System.out.println(pi.getId());
        System.out.println(pi.getProcessDefinitionId());
    }

    // 正常执行任务
    @Test
    public void completeTaskVariablesTest2() {
        processEngine.getTaskService().complete("32504"); // act_ru_task表的主键 ID_
    }

    // 条件执行任务
    @Test
    public void completeTaskVariablesTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("days", 5);
        processEngine.getTaskService().complete("30005", variables); // act_ru_task表的主键 ID_
    }

}
