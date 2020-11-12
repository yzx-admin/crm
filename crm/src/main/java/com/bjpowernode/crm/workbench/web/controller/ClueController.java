package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClueController extends HttpServlet{

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入线索活动控制器");
        String path = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(path)){

            getUserList(request, response);

        }else if("/workbench/clue/save.do".equals(path)){

            save(request, response);

        }else if("/workbench/clue/pageList.do".equals(path)){

            pageList(request, response);

        }else if("/workbench/clue/delete.do".equals(path)){

            delete(request, response);

        }else if("/workbench/clue/detail.do".equals(path)){

            detail(request, response);

        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){

            getActivityListByClueId(request, response);

        }else if("/workbench/clue/deleteActivityByCarId.do".equals(path)){

            deleteActivityByCarId(request, response);

        }else if("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){

            getActivityListByNameAndNotByClueId(request, response);

        }else if("/workbench/clue/bund.do".equals(path)){

            bund(request, response);

        }else if("/workbench/clue/getActivityListByName.do".equals(path)){

            getActivityListByName(request, response);

        }else if("/workbench/clue/convert.do".equals(path)){

            convert(request, response);

        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("执行线索转换操作");

        String flag = request.getParameter("flag");
        String clueId = request.getParameter("clueId");

        Tran t = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getName();


        if ("a".equals(flag)) {

            //创建交易
            t = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t.setActivityId(activityId);
            t.setStage(stage);
            t.setName(name);
            t.setMoney(money);
            t.setExpectedDate(expectedDate);
            t.setId(id);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = clueService.convert(clueId, t, createBy);

        if (success){

            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行查询市场活动操作（模糊查询）");

        String aname = request.getParameter("aname");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = activityService.getActivityListByName(aname);

        PrintJson.printJsonObj(response, aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行关联市场活动操作");

        String cId = request.getParameter("cId");
        String[] aIds = request.getParameterValues("aId");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cId", cId);
        map.put("aIds", aIds);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean success = activityService.bund(cId, aIds);

        PrintJson.printJsonObj(response, map);
    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询市场活动操作（通过名称模糊查询+排除已经关联的活动）");

        String aname = request.getParameter("aname");
        String cId =request.getParameter("clueId");

        Map<String, String> map = new HashMap<String, String>();
        map.put("aname", aname);
        map.put("cId", cId);


        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = activityService.getActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(response, aList);
    }

    private void deleteActivityByCarId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("通过线索活动关系id删除关联活动");

        String id = request.getParameter("id");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean success = activityService.deleteActivityByCarId(id);

        PrintJson.printJsonFlag(response, success);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行根据线索id查询市场活动操作");

        String cId = request.getParameter("clueId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = activityService.getActivityListByClueId(cId);

        PrintJson.printJsonObj(response, aList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入线索活动详情列表");

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);

        request.setAttribute("clue", clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行删除线索操作");

        String[] ids = request.getParameterValues("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = clueService.delete(ids);

        PrintJson.printJsonFlag(response, success);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行页面刷新操作");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String fullname = request.getParameter("fullname");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String phone  = request.getParameter("phone");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo-1)*pageSize;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fullname", fullname);
        map.put("owner", owner);
        map.put("company", company);
        map.put("phone", phone);
        map.put("mphone", mphone);
        map.put("state", state);
        map.put("source", source);
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        PaginationVo<Clue> vo = clueService.pageList(map);

        PrintJson.printJsonObj(response, vo);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行新建线索活动操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");


        Clue clue = new Clue();
        clue.setId(id);
        clue.setAddress(address);
        clue.setWebsite(website);
        clue.setState(state);
        clue.setSource(source);
        clue.setPhone(phone);
        clue.setOwner(owner);
        clue.setNextContactTime(nextContactTime);
        clue.setMphone(mphone);
        clue.setJob(job);
        clue.setFullname(fullname);
        clue.setEmail(email);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setCompany(company);
        clue.setAppellation(appellation);
        clue.setCreateTime(createTime);
        clue.setCreateBy(createBy);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = clueService.save(clue);

        PrintJson.printJsonFlag(response, success);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行取得用户列表操作");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = userService.getUserList();

        PrintJson.printJsonObj(response, uList);
    }


}
