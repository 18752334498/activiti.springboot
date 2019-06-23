package com.yucong.service;

import java.util.Random;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yucong.dao.UserDao;
import com.yucong.model.User;

@Service(value = "myTaskListener")
public class MyTaskListener implements TaskListener {

	private static final long serialVersionUID = -6828470940263038064L;

	@Autowired
	private UserDao userDao;

	@Override
	public void notify(DelegateTask delegateTask) {

		long i = new Random().nextInt(9) + 1;
		User user = userDao.findOne(i);
		System.out.println("=================== delegateTask，下一个执行人是: " + user.getName() + " ===================");
		delegateTask.setAssignee(user.getName());
	}
}
