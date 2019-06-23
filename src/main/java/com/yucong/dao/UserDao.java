package com.yucong.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yucong.model.User;

public interface UserDao extends JpaRepository<User, Long> {

}
