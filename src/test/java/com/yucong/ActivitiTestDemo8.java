package com.yucong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class ActivitiTestDemo8 {

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

    @Test
    public void test1() {
        // 获取模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel("policy:7:535016");
        Process process = bpmnModel.getMainProcess();
        List<Map<String, Object>> list = getNames(process, "usertask2");

        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }

    private List<Map<String, Object>> getNames(Process process, String flowElement) {
        FlowElement element = process.getFlowElement(flowElement);
        String name = element.getName();
        String outgoingFlows = JSONObject.parseObject(JSON.toJSONString(element)).getString("outgoingFlows");
        List<JSONObject> list = JSONObject.parseArray(outgoingFlows, JSONObject.class);

        List<Map<String, Object>> names = new ArrayList<>();
        if (list.size() == 1) {
            JSONObject object = list.get(0);
            String target = object.getString("targetRef");
            if (target.contains("exclusivegateway")) {
                names = getNames(process, target);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put(name, "");
                names.add(map);
            }
        } else {
            for (JSONObject object : list) {
                String exp = object.getString("conditionExpression");
                if (StringUtils.isNotEmpty(exp)) {
                    int a = exp.lastIndexOf("=");
                    int b = exp.lastIndexOf("}");
                    Map<String, Object> map = new HashMap<>();
                    map.put(object.getString("name"), exp.substring(a + 1, b).trim());
                    names.add(map);
                }
            }
        }
        return names;
    }

    /**
     * 动态部署 https://blog.csdn.net/qq_30739519/article/details/51166993
     */
    // 开始任务
    @Test
    public void start() {
        // 实例化BpmnModel对象
        BpmnModel bpmnModel = new BpmnModel();
        // 开始节点的属性
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start1shareniu");
        startEvent.setName("start1shareniu");
        // 普通的UserTask节点
        UserTask userTask = new UserTask();
        userTask.setId("userTask1shareniu");
        userTask.setName("userTask1shareniu");
        // 结束节点属性
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEventshareniu");
        endEvent.setName("endEventshareniu");
        // 连线信息
        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();
        List<SequenceFlow> toEnd = new ArrayList<SequenceFlow>();
        SequenceFlow s1 = new SequenceFlow();
        s1.setId("starttouserTask");
        s1.setName("starttouserTask");
        s1.setSourceRef("start1shareniu");
        s1.setTargetRef("userTask1shareniu");
        sequenceFlows.add(s1);
        SequenceFlow s2 = new SequenceFlow();
        s2.setId("userTasktoend");
        s2.setName("userTasktoend");
        s2.setSourceRef("userTask1shareniu");
        s2.setTargetRef("endEventshareniu");
        toEnd.add(s2);
        startEvent.setOutgoingFlows(sequenceFlows);
        userTask.setOutgoingFlows(toEnd);
        userTask.setIncomingFlows(sequenceFlows);
        endEvent.setIncomingFlows(toEnd);
        // Process对象
        Process process = new Process();
        process.setId("process1");
        process.addFlowElement(startEvent);
        process.addFlowElement(s1);
        process.addFlowElement(userTask);
        process.addFlowElement(s2);
        process.addFlowElement(endEvent);
        bpmnModel.addProcess(process);


        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String bytes = new String(convertToXML);
        System.out.println(bytes);

        // ValidationError封装的是验证信息，如果size为0说明，bpmnmodel正确，大于0,说明自定义的bpmnmodel是错误的，不可以使用的
        // 验证bpmnModel 是否是正确的bpmn xml文件
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
        // 验证失败信息的封装ValidationError
        List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);
        System.out.println("================:" + validate.size());
    }


    // 删除部署的某个流程
    @Test
    public void deleteDeployment() {

        processEngine.getRepositoryService().deleteDeployment("485001", true);
    }


    /**
     * https://www.jianshu.com/p/f1b654f52dd9
     */
    @Test
    public void genarateBpmnModel() {
        // 创建BpmnModel对象
        BpmnModel bpmnModel = new BpmnModel();

        /**
         * 创建process节点 并添加至process <process id="myProcess" name="My process" isExecutable="true">
         */
        Process process = new Process();
        // executabl内置默认为true
        process.setId("myProcess");
        process.setName("My process");


        /**
         * 创建开始结束节点 并添加至process <startEvent id="startevent1" name="Start"></startEvent>
         * <endEvent id="endevent2" name="End"></endEvent>
         */
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startevent1");
        startEvent.setName("Start");

        EndEvent endEvent = new EndEvent();
        endEvent.setId("endevent2");
        endEvent.setName("End");

        process.addFlowElement(startEvent);
        process.addFlowElement(endEvent);

        /**
         * 创建任务节点 并添加至process <userTask id="usertask1" name="提交审批"></userTask>
         */
        UserTask userTask1 = genarateUserTask("usertask1", "提交审批");
        UserTask userTask2 = genarateUserTask("usertask2", "总经理审批");
        UserTask userTask3 = genarateUserTask("usertask3", "部门经理审批");

        process.addFlowElement(userTask1);
        process.addFlowElement(userTask2);
        process.addFlowElement(userTask3);
        /**
         * 创建排他网关 并添加至process
         * <exclusiveGateway id="exclusivegateway1" name="Exclusive Gateway"></exclusiveGateway>
         */
        ExclusiveGateway exclusiveGateway = genarateExclusiveGateway("exclusivegateway1", "Exclusive Gateway");

        process.addFlowElement(exclusiveGateway);

        /**
         * 创建连线 并添加至process
         * <sequenceFlow id="flow1" sourceRef="startevent1" targetRef="usertask1"></sequenceFlow>
         */
        SequenceFlow sequenceFlow1 = genarateSequenceFlow("flow1", "", "startevent1", "usertask1", "", process);
        process.addFlowElement(sequenceFlow1);
        /**
         * 创建连线 并添加至process <sequenceFlow id="flow2" sourceRef="usertask1" targetRef=
         * "exclusivegateway1"></sequenceFlow>
         */
        SequenceFlow sequenceFlow2 = genarateSequenceFlow("flow2", "", "usertask1", "exclusivegateway1", "", process);
        process.addFlowElement(sequenceFlow2);

        /**
         * 创建连线 并添加至process
         * <sequenceFlow id="flow3" name="大于三天" sourceRef="exclusivegateway1" targetRef="usertask2">
         * <conditionExpression xsi:type=
         * "tFormalExpression"><![CDATA[${day>3}]]></conditionExpression> </sequenceFlow>
         */
        SequenceFlow sequenceFlow3 = genarateSequenceFlow("flow3", "大于三天", "exclusivegateway1", "usertask2", "${day>3}", process);
        process.addFlowElement(sequenceFlow3);

        /**
         * 创建连线 并添加至process
         * <sequenceFlow id="flow4" name="小于三天" sourceRef="exclusivegateway1" targetRef="usertask3">
         * <conditionExpression xsi:type=
         * "tFormalExpression"><![CDATA[${day<=3}]]></conditionExpression> </sequenceFlow>
         */
        SequenceFlow sequenceFlow4 = genarateSequenceFlow("flow4", "小于三天", "exclusivegateway1", "usertask3", "${day<=3}", process);
        process.addFlowElement(sequenceFlow4);

        /**
         * 创建连线 并添加至process
         * <sequenceFlow id="flow6" sourceRef="usertask2" targetRef="endevent2"></sequenceFlow>
         * <sequenceFlow id="flow7" sourceRef="usertask3" targetRef="endevent2"></sequenceFlow>
         */
        SequenceFlow sequenceFlow6 = genarateSequenceFlow("flow6", "", "usertask2", "endevent2", "", process);
        SequenceFlow sequenceFlow7 = genarateSequenceFlow("flow7", "", "usertask3", "endevent2", "", process);
        process.addFlowElement(sequenceFlow6);
        process.addFlowElement(sequenceFlow7);

        /**
         * 为排他网关添加出线
         */
        List<SequenceFlow> outgoingFlows = new ArrayList<>();
        outgoingFlows.add(sequenceFlow3);
        outgoingFlows.add(sequenceFlow4);
        exclusiveGateway.setOutgoingFlows(outgoingFlows);

        /**
         * 将process封装至模型对象
         */
        bpmnModel.addProcess(process);

        /**
         * 模型正确性校验
         */
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator processValidator = processValidatorFactory.createDefaultProcessValidator();
        List<ValidationError> validate = processValidator.validate(bpmnModel);
        if (validate.size() >= 1) {
            for (ValidationError validationError : validate) {
                System.out.println(validationError.getProblem());
                System.out.println(validationError.isWarning());
            }
        } else {
            System.out.println("=============模型创建成功==============");
        }

        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);

        System.out.println(new String(bytes));

        Deployment deploy = repositoryService.createDeployment().addBpmnModel("test.bpmn", bpmnModel).deploy();
    }

    /**
     * 创建连线
     */
    private SequenceFlow genarateSequenceFlow(String id, String name, String sourceRef, String tartgetRef, String conditionExpression, Process process) {
        SequenceFlow sequenceFlow = new SequenceFlow(sourceRef, tartgetRef);
        sequenceFlow.setId(id);
        if (name != null && name != "") {
            sequenceFlow.setName(name);
        }
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(tartgetRef);
        if (conditionExpression != null && conditionExpression != "") {
            sequenceFlow.setConditionExpression(conditionExpression);
        }
        return sequenceFlow;
    }

    /**
     * 创建排他网关
     */
    private ExclusiveGateway genarateExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        return exclusiveGateway;
    }

    /**
     * 创建用户人物节点
     */
    private UserTask genarateUserTask(String id, String name) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        return userTask;
    }

}
