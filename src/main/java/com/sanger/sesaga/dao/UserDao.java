package com.sanger.sesaga.dao;

import java.util.List;

import com.sanger.sesaga.entity.User;

public interface UserDao {

    public void updateNotPasswordNotRole(User user);

    public void updateNotPassword(User user);

    public void updatePassword(User user);

    public void validate(Long id);

    public List<User> findAll();

}
