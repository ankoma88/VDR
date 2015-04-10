package com.ak.vdrApp.controller;

import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.UserManagerLocal;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;


@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginController extends BaseController implements Serializable {

    @EJB
    private UserManagerLocal userManager;

    private String infoMessage;

    private User currentUser = new User();

    private String name;
    private String pass;
    private boolean isLogged = false;

    public LoginController() {
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isIsLogged() {
        return isLogged;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public String login() {

        try {
            currentUser = userManager.getUser(name, pass);
        } catch (UserNotFoundException e) {
            setInfoMessage(e.MESSAGE);
            return "login.xhtml";
        }
          catch (Exception e) {
            getContext().addMessage(null, new FacesMessage(SYSTEM_ERROR));
            return "login.xhtml";
        }
        isLogged = true;
        return "faces/protected/vdrMain.xhtml?faces-redirect=true";
    }

}
