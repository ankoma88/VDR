package com.ak.vdrApp.controller;

import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.ProjectManagerLocal;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name="questionsAndAnswersController")
@ViewScoped
public class QuestionsAndAnswersController extends BaseController implements Serializable {

    static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @EJB
    private ProjectManagerLocal projectManager;

    private Project currentProject = new Project();

    private User currentUser = new User();

    private String docName;

    private String comment;

    Map<String[], LinkedList<String>> comments;

    List<String[]> qAkeyList = new ArrayList<>();
    private ArrayList<LinkedList<String>> qAvaluesList;

    public ProjectManagerLocal getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProjectManagerLocal projectManager) {
        this.projectManager = projectManager;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public Map<String[], LinkedList<String>> getComments() {
        return comments;
    }

    public void setComments(Map<String[], LinkedList<String>> comments) {
        this.comments = comments;
    }

    public List<String[]> getqAkeyList() {
        return qAkeyList;
    }

    public void setqAkeyList(List<String[]> qAkeyList) {
        this.qAkeyList = qAkeyList;
    }

    public ArrayList<LinkedList<String>> getqAvaluesList() {
        return qAvaluesList;
    }

    public void setqAvaluesList(ArrayList<LinkedList<String>> qAvaluesList) {
        this.qAvaluesList = qAvaluesList;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String showDoc() {
        String[] docAndUser = (String[]) evaluateEL("#{docAndUser}",String[].class );
        String doc = docAndUser[0];
        return doc;
    }

    public String showCommentator() {
        String[] docAndUser = (String[]) evaluateEL("#{docAndUser}",String[].class );
        String user = docAndUser[1];
        return user;
    }

    public String showComment() {
        String[] docAndUser = (String[]) evaluateEL("#{docAndUser}",String[].class );
        LinkedList<String> commentList = comments.get(docAndUser);

            return commentList.getFirst();

    }

    public void openAnswerDialog(ActionEvent event) {
        String[] docAndUser = (String[]) evaluateEL("#{docAndUser}",String[].class );
        docName = docAndUser[0];
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('answerDialog').show();");
    }

    public void saveAnswer() {
        LinkedList<String> commentList;
        comments = (Map<String[], LinkedList<String>>) currentProject.getComments();
        if (comments == null) {
            comments = new HashMap<>();
        }
        String[] docAndUser = {docName, currentUser.getFirstName()+" "+currentUser.getLastName()};
        if (!comments.containsKey(docAndUser)) {
            comments.put(docAndUser, new LinkedList<String>());
        }
        commentList = comments.get(docAndUser);
        if (comment!=null) {
            commentList.add(comment);
        }
        comments.put(docAndUser, commentList);
        try {
            projectManager.updateProject(currentProject.getId(), comments);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    @PostConstruct
    public void setUp () {
        this.currentUser = ((LoginController) getSession().getAttribute("loginBean")).getCurrentUser();
        Integer chosenProjectId = (Integer) getSession().getAttribute("chosenProjectId");
        this.currentProject = projectManager.find(chosenProjectId);
        this.comments = (Map<String[], LinkedList<String>>) currentProject.getComments();
        this.qAkeyList = new ArrayList<>(comments.keySet());
        this.qAvaluesList = new ArrayList<>(comments.values());
    }
}
