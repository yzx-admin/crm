package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao =  SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        Map<String,String> map = new HashMap<String, String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        User user = userDao.login(map);

        //验证账号密码是否错误
        if(user==null){
            throw new LoginException("账号密码错误");
        }

        //验证账号是否锁定
        if("0".equals(user.getLockState())){
            throw new LoginException("账号已被锁定");
        }

        //验证账号是否失效
        String time = DateTimeUtil.getSysTime();
        if(time.compareTo(user.getExpireTime())>0){
            throw new LoginException("账号已失效");
        }

        //验证ip地址

        if(!user.getAllowIps().contains(ip)){
            throw new LoginException("ip地址无效");
        }

        return user;
    }

    public List<User> getUserList() {
        List<User> uList = userDao.getUserList();
        return uList;
    }
}
