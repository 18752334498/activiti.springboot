package com.yucong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yucong.model.Param;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo7 {

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
        variables.put("pass", 1);
        variables.put("applyId", 8888888);
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("policy", variables);
        runtimeService.setProcessInstanceName(procInst.getProcessInstanceId(), "俞聪");
        System.out.println("启动成功....");

    }


    @Test
    public void getActivitiBPMN() throws Exception {
        // ProcessDefinition processDefinition =
        // repositoryService.createProcessDefinitionQuery().processDefinitionId("policy:7:535016").singleResult();
        // String resourceName = processDefinition.getResourceName();
        // InputStream resourceAsStream =
        // repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        // BpmnXMLConverter converter = new BpmnXMLConverter();
        // XMLInputFactory factory = XMLInputFactory.newInstance();
        // XMLStreamReader reader = factory.createXMLStreamReader(resourceAsStream);
        // BpmnModel bpmnModel = converter.convertToBpmnModel(reader);

        BpmnModel bpmnModel = repositoryService.getBpmnModel("policy:7:535016");
        System.out.println(JSON.toJSONString(bpmnModel));

        JSONObject model = JSONObject.parseObject(JSON.toJSONString(bpmnModel));
        JSONObject mainProcess = model.getJSONObject("mainProcess");
        List<JSONObject> flowElements = JSONObject.parseArray(mainProcess.getString("flowElements"), JSONObject.class);
        List<Map<String, JSONArray>> tem = new ArrayList<>();
        for (JSONObject jsonObject : flowElements) {
            if (StringUtils.isEmpty(jsonObject.getString("id"))) {
                continue;
            }
            Map<String, JSONArray> map = new HashMap<>();
            map.put(jsonObject.getString("id"), jsonObject.getJSONArray("outgoingFlows"));
            tem.add(map);
        }

        System.out.println(JSON.toJSONString(tem));
    }


    // 正常执行任务
    @Test
    public void findNext() {
    }

    // 正常执行任务
    @Test
    public void completeTaskVariablesTest2() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", 4);
        taskService.complete("530006", variables);
    }

    // 条件执行任务
    @Test
    public void completeTaskVariablesTest() {
        HistoricVariableInstance instance = historyService.createHistoricVariableInstanceQuery()//
                .variableName("user")//
                .singleResult();
        Param param = (Param) instance.getValue();
        System.out.println(param);

        HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()//
                .processInstanceId("385023").singleResult();
        System.out.println(result.getEndActivityId());
    }

    // 审批
    @Test
    public void findTask2() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pass", "1");

        taskService.complete("197505", variables);

    }

    @Test
    public void getParam() {
        ProcessDefinition definition = processEngine.getRepositoryService().createProcessDefinitionQuery()//
                .processDefinitionKey("policy").singleResult();

        System.out.println(definition.getName());
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


    // 删除部署的某个流程
    @Test
    public void deleteDeployment() {
        processEngine.getRepositoryService().deleteDeployment("527501", true);
    }
}
