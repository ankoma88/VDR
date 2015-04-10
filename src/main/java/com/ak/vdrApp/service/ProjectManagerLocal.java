package com.ak.vdrApp.service;

import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.exceptions.ProjectAlreadyExistsException;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;
import org.primefaces.model.TreeNode;

import javax.ejb.Local;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Local
public interface ProjectManagerLocal {

    public List<Project> getProjectsOfUser(User user);

    public List<Project> getProjectsCreatedByAdminUser(User user);

    public List<Project> getAllProjects();

    public Project getProjectByProjectId(int projectId) throws ProjectNotFoundException;

    public Project getProjectByProjectName(String projectName) throws ProjectNotFoundException;

    public void createProjectByAdminUser(Project newProject, User admin) throws ProjectAlreadyExistsException, ProjectNotFoundException, UserNotFoundException;

    public void updateProject(Project project, String projectName, String description) throws ProjectNotFoundException;

    public void assignProjectToUser(Project project, User user) throws ProjectNotFoundException, UserNotFoundException;

    public void removeProjectByProjectId(int projectId) throws ProjectNotFoundException;

    public void unassignProjectFromUser(Project project, User user) throws ProjectNotFoundException, UserNotFoundException;

    public Project find(int id);

    public void updateProject(int id, LinkedHashMap<String, TreeNode> currentDocMap) throws ProjectNotFoundException;

    public void updateProject(int id, Map<String[], LinkedList<String>> comments) throws ProjectNotFoundException;
}
