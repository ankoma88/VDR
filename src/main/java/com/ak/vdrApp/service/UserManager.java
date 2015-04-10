package com.ak.vdrApp.service;

import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.exceptions.UserAlreadyExistsException;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


@Stateless
@SuppressWarnings("unchecked")
public class UserManager implements UserManagerLocal {

    @PersistenceContext (unitName = "vdrUnit")
    public EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Override
    public User getUser(String userName, String password) throws UserNotFoundException {
        Query query = em.createQuery("SELECT user FROM User user WHERE user.userName =:userName AND user.password = :password");
        query.setParameter("userName", userName);
        query.setParameter("password", password);
        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException();
        }
        return user;
    }


    @Override
    public void addUser(User newUser) throws UserAlreadyExistsException {
        Query query = em.createQuery("SELECT user FROM User user WHERE user.userName= :userName");
        query.setParameter("userName", newUser.getUserName());
        try {
            query.getSingleResult();
            throw new UserAlreadyExistsException();
        } catch (NoResultException e) {
            em.persist(newUser);
            em.flush();
        }

    }


    @Override
    public List<User> getUsers() {
        Query query = em.createQuery("SELECT user FROM User user", User.class);
        List<User> result = query.getResultList();
        if (result == null) {
            return new ArrayList<>();
        }
        return result;

    }

    @Override
    public User find(int id) {
        return em.find(User.class, id);
    }

    @Override
    public void setUsersDownloadPermissionTrue(int userId) {
        User updUser = find(userId);
        updUser.setMayDownload(true);
        em.merge(updUser);
        em.flush();
    }

    @Override
    public void setUsersDownloadPermissionFalse(int userId) {
        User updUser = find(userId);
        updUser.setMayDownload(false);
        em.merge(updUser);
        em.flush();
    }
}
