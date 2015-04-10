package com.ak.vdrApp.filters;

import com.ak.vdrApp.controller.LoginController;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        LoginController session = (LoginController) req.getSession().getAttribute("loginBean");
        String url = req.getRequestURI();

        /* - if request is for mainPage or logout and there is no session, redirect to login
           - if request is for register or login and there is a session, redirect to mainPage
           - if request is for logout and there is a session, update project, remove session and then redirect to login

        */

        if (session == null || !session.isLogged()) {
            if (url.contains("vdrMain.xhtml") || url.contains("logout.xhtml")) {
                resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if (url.contains("register.xhtml") || url.contains("login.xhtml")){
                resp.sendRedirect(req.getServletContext().getContextPath() + "/faces/protected/vdrMain.xhtml");
            }
            else if (url.contains("logout.xhtml")) {

                req.getSession().removeAttribute("loginBean");
                req.getSession().invalidate();
                resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
            }
            else {
                chain.doFilter(request, response);
            }
        }
    }


    @Override
    public void destroy() {

    }
}
