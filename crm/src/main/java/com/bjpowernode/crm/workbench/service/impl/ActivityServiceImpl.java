package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String, Object> map = new HashMap<String, Object>();

        //取得uList
        List<User> uList = userDao.getUserList();

        //根据id取得单条市场活动activity
        Activity activity = activityDao.getById(id);

        //将uList和activity封装进map
        map.put("uList", uList);
        map.put("activity", activity);

        return map;
    }

    public Boolean update(Activity a) {

        Boolean flag = true;
        int count = activityDao.update(a);

        if(count != 1){
            flag = false;
        }
        return flag;

    }

    public Activity detail(String id) {

        Activity activity = activityDao.detail(id);

        return activity;
    }

    public List<ActivityRemark> getRemarkListByAid(String aid) {

        List<ActivityRemark> rList = activityRemarkDao.getRemarkListByAid(aid);

        return rList;
    }

    public Boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteRemark(id);

        if (count != 1){

            flag = false;
        }
        return flag;
    }

    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);

        if (count != 1){

            flag = false;
        }
        return flag;
    }

    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);

        if (count != 1){

            flag = false;
        }
        return flag;
    }

    public Boolean delete(String[] ids) {
        Boolean flag = true;

        //查询出与市场活动相匹配的活动备注数量
        int count1 = activityRemarkDao.getActivityRemark(ids);

        //删除相应的备注
        int count2 = activityRemarkDao.deleteActivityRemark(ids);
        if (count1 != count2){

            flag = false;
        }

        //删除活动
        int count = activityDao.deleteActivity(ids);
        if (count != ids.length){

            flag = false;
        }
        return flag;
    }

    public Boolean save(Activity activity) {
        Boolean flag = true;
        int count = activityDao.save(activity);

        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVo<Activity> pageList(Map<String, Object> map) {

        //取得total
        int total = activityDao.getTotalByCondition(map);

        //取得list
        List<Activity> aList = activityDao.getActivityListByCondition(map);

        //封装vo
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setList(aList);

        return vo;
    }


}
