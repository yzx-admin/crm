package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    Boolean save(Activity activity);

    PaginationVo<Activity> pageList(Map<String, Object> map);

    Boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    Boolean update(Activity a);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String aid);

    Boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityListByClueId(String cId);

    boolean deleteActivityByCarId(String id);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    boolean bund(String cId, String[] aIds);

    List<Activity> getActivityListByName(String aname);
}