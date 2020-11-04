package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {

    int save(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int deleteActivity(String[] ids);

    Activity getById(String id);

    int update(Activity a);

    Activity detail(String id);
}
