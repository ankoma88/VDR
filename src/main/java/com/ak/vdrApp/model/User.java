package com.ak.vdrApp.model;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name= "VDR_USER")
@Named
@RequestScoped
public class User implements Serializable{

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "USER_NAME")
    private String userName;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "PASSWORD")
    private String password;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "LAST_NAME")
    private String lastName;


    @Column(name = "ISADMIN")
    private boolean isAdmin;

    @Column(name = "DOWNLOADPERMISSION")
    private boolean isMayDownload;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "COMPANY")
    private String company;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "PHONE")
    private String phone;

    @OneToMany(mappedBy = "projectAdmin")
    private List<Project> ownedProjects;

    @ManyToMany
    @JoinTable(name = "PROJECT_VDR_USER")
    private List<Project> projects;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.firstName = "n/a";
        this.lastName = "n/a";
        this.phone = "n/a";
        this.company = "n/a";
        this.isAdmin = false;
    }

    public User(String userName, String password, String firstName, String lastName, String company, String phone, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.company = company;
        this.phone = phone;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public boolean isMayDownload() {
        return isMayDownload;
    }

    public void setMayDownload(boolean isMayDownload) {
        this.isMayDownload = isMayDownload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id && isAdmin == user.isAdmin && company.equals(user.company) && firstName.equals(user.firstName) && lastName.equals(user.lastName) && password.equals(user.password) && phone.equals(user.phone) && userName.equals(user.userName);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userName.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + company.hashCode();
        result = 31 * result + phone.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return firstName + " " +lastName + " ";
    }
}

