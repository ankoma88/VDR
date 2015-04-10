package com.ak.vdrApp.controller;

import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.UserManagerLocal;
import com.ak.vdrApp.service.exceptions.UserAlreadyExistsException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class RegisterController extends BaseController implements Serializable {

    @EJB
    private UserManagerLocal userManager;

    @Produces
    @Named
    @RequestScoped
    private User newUser = new User();

    private String infoMessage;

    private boolean isRegistered;

    public boolean isIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public void registerUser() {

        try {
            userManager.addUser(newUser);
            infoMessage = "User registered successfully. Please, login";
            isRegistered = true;
        } catch (UserAlreadyExistsException e) {
            setInfoMessage(e.MESSAGE);
        } catch (Exception e) {
            infoMessage = "An error occurs while registering user";
            getContext().addMessage(null, new FacesMessage("An error occurs while registering user"));
        }
    }
}
