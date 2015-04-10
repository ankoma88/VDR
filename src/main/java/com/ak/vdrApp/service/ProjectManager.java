package com.ak.vdrApp.service;

import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.exceptions.ProjectAlreadyExistsException;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import com.ak.vdrApp.service.exceptions.UserNotFoundException;
import org.primefaces.model.TreeNode;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

@Stateless
@SuppressWarnings("unchecked")
public class ProjectManager implements ProjectManagerLocal {

    @PersistenceContext(unitName = "vdrUnit")
    public EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }


    @Override
    public List<Project> getProjectsCreatedByAdminUser(User admin) {
        Query query = em.createQuery("SELECT project FROM Project project WHERE project.projectAdmin = :projectAdmin");
        query.setParameter("projectAdmin", admin);
        List<Project> result = query.getResultList();
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<Project> getAllProjects() {
        Query query = em.createQuery("SELECT project FROM Project project", Project.class);
        List<Project> result = query.getResultList();
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public Project getProjectByProjectId(int projectId) throws ProjectNotFoundException {
        Query query = em.createQuery("SELECT project FROM Project project WHERE project.id =:projectId");
        query.setParameter("projectId", projectId);
        Project project;
        try {
            project = (Project) query.getSingleResult();
        } catch (NoResultException e) {
            throw new ProjectNotFoundException();
        }
        return project;
    }

    @Override
    public Project getProjectByProjectName(String projectName) throws ProjectNotFoundException {
        Query query = em.createQuery("SELECT project FROM Project project WHERE project.projectName =:projectName");
        query.setParameter("projectName", projectName);
        Project project;
        try {
            project = (Project) query.getSingleResult();
        } catch (NoResultException e) {
            throw new ProjectNotFoundException();
        }
        return project;
    }

    @Override
    public void createProjectByAdminUser(Project newProject, User admin) throws ProjectAlreadyExistsException, ProjectNotFoundException, UserNotFoundException {
        admin = em.find(User.class, admin.getId());
        if (admin == null) {
            throw new UserNotFoundException();
        }
        Query query = em.createQuery("SELECT project FROM Project project WHERE project.projectName = :projectName");
        query.setParameter("projectName", newProject.getProjectName());
        try {
            query.getSingleResult();
            throw new ProjectAlreadyExistsException();
        } catch (NoResultException e) {
          newProject.setProjectAdmin(admin);
          admin.getOwnedProjects().add(newProject);
          em.persist(newProject);
          em.merge(admin);
          em.flush();
            assignProjectToUser(newProject,admin);
        }
    }

    @Override
    public void removeProjectByProjectId(int projectId) throws ProjectNotFoundException{
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new ProjectNotFoundException();
        }

        project.getUsers().clear();
        em.remove(project);
        em.flush();
    }

    @Override
    public void updateProject(Project project, String projectName, String description) throws ProjectNotFoundException {
        project = em.find(Project.class, project.getId());

        if (project==null) {
            throw new ProjectNotFoundException();
        }
        if (projectName!=null) {
            project.setProjectName(projectName);
        }
        if (description!=null) {
            project.setDescription(description);
        }

        em.merge(project);
        em.flush();
    }

    @Override
    public void assignProjectToUser(Project project, User user) throws ProjectNotFoundException, UserNotFoundException {
        project = em.find(Project.class, project.getId());
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        user = em.find(User.class, user.getId());
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (project.getUsers() == null){
            project.setUsers(new ArrayList<User>());
        }
        if (user.getProjects() == null){
            user.setProjects(new ArrayList<Project>());
        }

        if(!project.getUsers().contains(user) || !user.getProjects().contains(project)) {

            project.getUsers().add(user);
            user.getProjects().add(project);
            em.merge(project);
            em.merge(user);
            em.flush();
        }
    }

    @Override
    public List<Project> getProjectsOfUser(User user) {
        Query query = em.createQuery("SELECT project FROM Project project JOIN project.users user WHERE user =:user");
        query.setParameter("user", user);
        List<Project> result = query.getResultList();
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }


    @Override
    public void unassignProjectFromUser(Project project, User user)  throws ProjectNotFoundException, UserNotFoundException {
        project = em.find(Project.class, project.getId());
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        user = em.find(User.class, user.getId());
        if (user == null) {
            throw new UserNotFoundException();
        }
       if (!user.isAdmin() && (project.getUsers().contains(user) || user.getProjects().contains(project) )) {
            project.getUsers().remove(user);
            user.getProjects().remove(project);
            em.merge(project);
            em.merge(user);
            em.flush();
        }
    }

    @Override
    public Project find(int id) {
        return em.find(Project.class, id);
    }

    @Override
    public void updateProject(int id, LinkedHashMap<String, TreeNode> currentDocMap) throws ProjectNotFoundException {
        Project project = em.find(Project.class, id);


        if (project==null) {
            throw new ProjectNotFoundException();
        }

        if (project.getDocMap()!=null) {
            project.setDocMap(currentDocMap);
            em.merge(project);
            em.flush();
        }
    }

    @Override
    public void updateProject(int id, Map<String[], LinkedList<String>> comments) throws ProjectNotFoundException {
        Project project = em.find(Project.class, id);
        if (project==null) {
            throw new ProjectNotFoundException();
        }
        if (comments!=null) {
            project.setComments((Serializable) comments);
        }
        em.merge(project);
        em.flush();
    }


}
