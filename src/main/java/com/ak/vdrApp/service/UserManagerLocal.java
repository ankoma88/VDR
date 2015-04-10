package com.ak.vdrApp.service;

import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.exceptions.UserAlreadyExistsException;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserManagerLocal {

    public User getUser(String name, String password) throws UserNotFoundException;
    public List<User> getUsers();
    public void addUser(User newUser) throws UserAlreadyExistsException;
    public User find(int id);
    public void setUsersDownloadPermissionTrue(int userId);

    void setUsersDownloadPermissionFalse(int userId);
}