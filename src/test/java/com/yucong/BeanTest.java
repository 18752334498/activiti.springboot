package com.yucong;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yucong.dao.UserDao;
import com.yucong.model.User;
import com.yucong.service.MyApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApp.class)
public class BeanTest {

    @Autowired
	private MyApplicationContext context;


    // 开始流程
    @Test
	@Transactional
    public void startByProcessInstanceByKey() {

//		UserDao bean = (UserDao) context.getBeanByName("userDao");
		UserDao bean = (UserDao) context.getBeanByClass(UserDao.class);

		User user = bean.getOne(3l);

		System.out.println(user.getName());
    }

}
