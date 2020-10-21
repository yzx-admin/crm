package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        if("/settings/user/login.do".equals(path) || "/login.jsp".equals(path)){
            chain.doFilter(request,response);
            return;

        }else{

            HttpSession session = request.getSession();

            User user = (User) session.getAttribute("user");

            //如果user不为null，说明登录过
            if(user!=null){

                chain.doFilter(req, resp);

                //没有登录过
            }else{

                response.sendRedirect(request.getContextPath() + "/login.jsp");

            }


        }

    }
}
