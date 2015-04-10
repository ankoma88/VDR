package com.ak.vdrApp.controller;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class BaseController {

    public static final String SYSTEM_ERROR = "System error ...";

    protected FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    protected Map getRequestMap() {
        return getContext().getExternalContext().getRequestMap();
    }

    protected HttpServletRequest getRequest() {
        return getRequest();
    }

    protected HttpSession getSession() {
        return (HttpSession) getContext().getExternalContext().getSession(false);
    }

    protected Object evaluateEL(String elExpression, Class beanClazz) {
        return getContext().getApplication().evaluateExpressionGet(getContext(), elExpression,
                beanClazz);
    }



}
