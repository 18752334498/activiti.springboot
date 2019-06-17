package com.yucong.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.dao.UserDao;
import com.yucong.model.Param;
import com.yucong.model.User;

@Service
@Transactional
public class ActivitiService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserDao userDao;

    public void test1() {
        User user = new User();
        user.setName("Tom");
        user.setAge("11");
        userDao.save(user);
    }

    public void test2(HttpServletRequest request) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("inputUser", "11111"); // 放入的value会与act_id_user中的 ID_ 对应

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("并行审批", variables);

        System.out.println(pi.getId());
        System.out.println(pi.getProcessDefinitionId());
    }

    public void test3(HttpServletRequest request) {
        String taskId1 = request.getParameter("taskId1");
        String taskId2 = request.getParameter("taskId2");

        Param param1 = new Param().setName("张三").setAge("11");
        taskService.setVariable(taskId1, "param1", param1);

        Param param2 = new Param().setName("李四").setAge("22");
        taskService.setVariableLocal(taskId2, "param2", param2);

        taskService.complete(taskId1);
        taskService.complete(taskId2);

        /************/
        int size = userDao.findAll().size();
        User user = new User();
        user.setName("Tom");
        user.setAge((size + 1) + "");
        userDao.save(user);
    }

    public void test4(HttpServletRequest request) {
        String taskId1 = request.getParameter("taskId1");
        String taskId2 = request.getParameter("taskId2");

        Param param1 = new Param().setName("张三").setAge("11");
        taskService.setVariable(taskId1, "param1", param1);

        Param param2 = new Param().setName("李四").setAge("22");
        taskService.setVariableLocal(taskId2, "param2", param2);

        taskService.complete(taskId1);
        taskService.complete(taskId2);

        /************/
        int size = userDao.findAll().size();
        User user = new User();
        user.setName("Tom");
        user.setAge((size + 1) + "");
        userDao.save(user);

        throw new RuntimeException("异常。。。。。。。。。");
    }

    public void test5(HttpServletRequest request) {
        String taskId = request.getParameter("taskId");
        taskService.complete(taskId);
    }


    
}
