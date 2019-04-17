package com.qf.serviceimpl;

import com.qf.entity.User;

public interface IUserService {

    int insertUser(User user);
    User loginUser(String username,String password);
    int jihuoUser(String username);
}
