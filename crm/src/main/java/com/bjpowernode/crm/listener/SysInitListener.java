package com.bjpowernode.crm.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SysInitListener implements ServletContextListener {

    //event：能够监听的对象
    public void contextInitialized(ServletContextEvent event) {

        //System.out.println("上下文域已经创建");

        System.out.println("服务器缓存处理数据字典开始");

        ServletContext application = event.getServletContext();

        //取数据字典的数据
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());

        //将47条数据按7种类型分别存放，有7个List，将List封装进Map返回
        //例： map.put("appellationList", dv1List )
        Map<String, List<DicValue>> map = dicService.getAllList();

        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for (String key : set){

            application.setAttribute(key, map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");

    }
}
