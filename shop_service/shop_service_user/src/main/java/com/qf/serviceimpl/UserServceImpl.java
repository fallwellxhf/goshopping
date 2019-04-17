package com.qf.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qf.dao.userMapper;
import com.qf.entity.User;
import com.qf.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class UserServceImpl implements IUserService {
    @Autowired
    private userMapper userMapper;
    @Override
    public int insertUser(User user) {
        //对密码进行MD5加密
        user.setPassword(MD5Util.md5(user.getPassword()));
        System.out.println("加密后的密码是："+user.getPassword());
        return userMapper.insert(user);
    }

    @Override
    public User loginUser(String username, String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("username",username);
       queryWrapper.eq("password",MD5Util.md5(password));
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    public int jihuoUser(String username) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        //把状态设置为登入状态
        user.setStatus(1);
        //修改用户信息
        userMapper.updateById(user);
        return 1;
    }
}
