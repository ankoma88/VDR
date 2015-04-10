package com.ak.vdrApp.controller;

import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.ProjectManagerLocal;
import com.ak.vdrApp.service.UserManagerLocal;
import com.ak.vdrApp.service.exceptions.ProjectAlreadyExistsException;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;



@Named
@RequestScoped
public class VdrMainController extends BaseController implements Serializable {

    static final Logger log = LoggerFactory.getLogger(VdrMainController.class);

    @EJB
    private UserManagerLocal userManager;

    @EJB
    private ProjectManagerLocal projectManager;

    @Produces
    @Named
    @RequestScoped
    private Project newProject = new Project();

    private User currentUser;

    private int usrId;

    private boolean isMayDownload;

    private int currentProjectId;

    private List<Project> usersProjects;


    private String infoMessage;

    private Date sDate = new Date();
    private Date eDate = new Date();

    private Part filePart;

    private List<User> allUsers;

    private Project manageableProject = new Project();

    private User editableUser = new User();

    public Part getFilePart() {
        return filePart;
    }
    public void setFilePart(Part filePart) {
        this.filePart = filePart;
    }

    public Date getsDate() {
        return sDate;
    }

    public void setsDate(Date sDate) {
        this.sDate = sDate;
    }

    public Date geteDate() {
        return eDate;
    }

    public void seteDate(Date eDate) {
        this.eDate = eDate;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public Project getNewProject() {
        return newProject;
    }

    public void setNewProject(Project newProject) {
        this.newProject = newProject;
    }


    public List<Project> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(List<Project> usersProjects) {
        this.usersProjects = usersProjects;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public Project getManageableProject() {
        return manageableProject;
    }

    public void setManageableProject(Project manageableProject) {
        this.manageableProject = manageableProject;
    }

    public int getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(int currentProjectId) {
        this.currentProjectId = currentProjectId;
    }

    public int getUsrId() {
        return usrId;
    }

    public void setUsrId(int usrId) {
        this.usrId = usrId;
    }

    public User getEditableUser() {
        return editableUser;
    }

    public void setEditableUser(User editableUser) {
        this.editableUser = editableUser;
    }

    public boolean isIsMayDownload() {
        return isMayDownload;
    }

    public void setIsMayDownload(boolean isMayDownload) {
        this.isMayDownload = isMayDownload;
    }

    public void createProject() {

        try {
            addLogo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        newProject.setStartDate(sDate);
        newProject.setExpirationDate(eDate);

        LoginController lB = (LoginController) getSession().getAttribute("loginBean");
        User curUser = lB.getCurrentUser();
        if (!curUser.isAdmin()) {
            infoMessage = "You don't have admin rights. Only admins can create new projects.";
            return;
        }

        try {
            projectManager.createProjectByAdminUser(newProject, curUser);
            infoMessage = "Project " + newProject.getProjectName() + " created successfully.";

        } catch (ProjectAlreadyExistsException e) {
            setInfoMessage(e.MESSAGE);
        } catch (ProjectNotFoundException e) {
            setInfoMessage(e.MESSAGE);
        }  catch (UserNotFoundException e) {
            setInfoMessage(e.MESSAGE);

        } catch (Exception e) {
            infoMessage = "An error occurs while creating project.";
//            getContext().addMessage(null, new FacesMessage("An error occurs while creating project."));
        }

    }



    public void addLogo() throws IOException {
        if (filePart!=null) {
            byte[] bytes = IOUtils.toByteArray(filePart.getInputStream());
            newProject.setLogo(bytes);
        }
    }


    public void assignUser() throws UserNotFoundException, ProjectNotFoundException {
        this.manageableProject = projectManager.find(currentProjectId);
        if (manageableProject != null) {
            if (isIsMayDownload()) {
                editableUser.setMayDownload(isMayDownload);
                userManager.setUsersDownloadPermissionTrue(editableUser.getId());
            }
            try {
                projectManager.assignProjectToUser(manageableProject, editableUser);
            } catch (ProjectNotFoundException | UserNotFoundException e) {
                e.printStackTrace();
            }

            String message = "User " + editableUser.getFirstName() + " " + editableUser.getLastName() + " is assigned to project " + manageableProject.getProjectName();
//            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
            infoMessage=message;

        }
    }

    public void unassignUser() {
        this.manageableProject = projectManager.find(currentProjectId);
        if (manageableProject != null) {

            if (editableUser.isAdmin()) {
                getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Admin cannot be unassigned."));
                return;
            }

            try {
                editableUser.setMayDownload(false);
                userManager.setUsersDownloadPermissionFalse(editableUser.getId());
                projectManager.unassignProjectFromUser(manageableProject, editableUser);
            } catch (ProjectNotFoundException | UserNotFoundException e) {
                e.printStackTrace();
            }

            String message = "User " + editableUser.getFirstName() + " " + editableUser.getLastName() + " is unassigned from project " + manageableProject.getProjectName();
//            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
            infoMessage=message;
        }
    }

    public void attrListener(ActionEvent event) {
        this.manageableProject = projectManager.find(currentProjectId);
        if (manageableProject != null) {
            Integer usrId = (Integer) event.getComponent().getAttributes().get("editableUser");
            this.editableUser = userManager.find(usrId);
        }
    }

    public void deleteProjectListener() {
        this.manageableProject = projectManager.find(currentProjectId);
        if (manageableProject != null) {
            try {
                projectManager.removeProjectByProjectId(manageableProject.getId());
            } catch (ProjectNotFoundException e) {
                e.printStackTrace();
            }

            String message = "Project " + manageableProject.getProjectName() + " was deleted.";
//            getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
            infoMessage=message;
        }
    }

    public void goToProjectListener(ActionEvent event) {
        Integer chosenProjectId = (Integer) event.getComponent().getAttributes().get("chosenProject");
        getSession().setAttribute("chosenProjectId",chosenProjectId);

    }

    public String goToProject() {
        return"project.xhtml";
    }



    @PostConstruct
    public void setUp () {
        this.currentUser = ((LoginController) getSession().getAttribute("loginBean")).getCurrentUser();
        this.usersProjects = projectManager.getProjectsOfUser(currentUser);
        this.allUsers = userManager.getUsers();
    }


}
