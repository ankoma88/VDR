package com.ak.vdrApp.controller;

import com.ak.vdrApp.model.Document;
import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.model.User;
import com.ak.vdrApp.service.ConverterToPdf;
import com.ak.vdrApp.service.DocumentManager;
import com.ak.vdrApp.service.ProjectManagerLocal;
import com.ak.vdrApp.service.Watermarker;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@ManagedBean(name="projectController")
@ViewScoped
public class ProjectController extends BaseController implements Serializable {

    static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @EJB
    private ProjectManagerLocal projectManager;

    @ManagedProperty(value = "#{documentManager}")
    private DocumentManager documentManager;

    private Project currentProject = new Project();

    private User currentUser = new User();

    private TreeNode root;

    private UploadedFile file;

    private String docName;

    private String fName;

    private String dName;

    private String comment;

    private Map<String[], LinkedList<String>> comments = new HashMap<>();

    private boolean isMayDownload;

    public ProjectManagerLocal getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProjectManagerLocal projectManager) {
        this.projectManager = projectManager;
    }

    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public void setDocumentManager (DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
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

    public Map<String[], LinkedList<String>> getComments() {
        return comments;
    }

    public void setComments(Map<String[], LinkedList<String>> comments) {
        this.comments = comments;
    }

    public boolean isIsMayDownload() {
        return isMayDownload;
    }

    public void setIsMayDownload(boolean isMayDownload) {
        this.isMayDownload = isMayDownload;
    }

    public ProjectController() {
    }


    public void deleteNode(ActionEvent event) {
        docName = (String) event.getComponent().getAttributes().get("docName");
        documentManager.deleteNode(docName);
    }

    public void openCreateFolderDialog(ActionEvent event) {
        docName = (String) event.getComponent().getAttributes().get("docName");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('createFolderDialog').show();");
    }

    public void createFolder() {
        TreeNode relativeRoot = documentManager.getMapDataToTreeNode().get(docName);
        documentManager.createFolder(relativeRoot, fName);
        fName=null;
    }

    public void openCreateDocumentDialog(ActionEvent event) {
        docName = (String) event.getComponent().getAttributes().get("docName");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('createDocumentDialog').show();");
    }

    public void createDocument() {
        TreeNode relativeRoot = documentManager.getMapDataToTreeNode().get(docName);
        documentManager.createDocument(relativeRoot, dName, file);
        dName=null;

    }

    public void upload() {

        if(file.getSize() != 0) {
            FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            createDocument();
            return;
        }
        FacesMessage message = new FacesMessage("Choose file!");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }



    public void download() throws IOException {

        Document doc = (Document) evaluateEL("#{document}", Document.class);

        if (doc.getType().equals("Folder")) {
            return;
        }

        String downloadLink = doc.getLink();

        String[] tokensLink = downloadLink.split("\\.(?=[^\\.]+$)");
        String[] tokensName = doc.getName().split("\\.(?=[^\\.]+$)");

        String fileName;

        if (tokensName.length > 1) {
          fileName = doc.getName(); //file name already has extension (user left name field blank when creating and default name applied)
        }
        else {
            fileName = doc.getName() + "." + tokensLink[1]; //adding extension to file name (user named file without extension while creating)
        }

        File file = new File(downloadLink);

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("application/octet-stream");
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");


        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream out = null;
        try {
            out = ec.getResponseOutputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte buf[] = new byte[1024];
        int len;
        try {
            assert is != null;
            while ((len = is.read(buf)) > 0) {
                assert out != null;
                out.write(buf, 0, len);
            }
            is.close();
            assert out != null;
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fc.responseComplete();

    }


    public String open() throws IOException {
        Document doc = (Document) evaluateEL("#{document}", Document.class);
        if(doc.getType().equals("Folder")){
            return null;
        }
        String downloadLink = doc.getLink();
// Converting to pdf and adding watermarks
        switch (doc.getType()) {
            case "pdf":
                downloadLink = Watermarker.addWatermark(downloadLink);
                break;
            case "doc":
            case "ppt":
            case "xls":
            case "text/plain":
                downloadLink = ConverterToPdf.convertToPdf(downloadLink);
                downloadLink = Watermarker.addWatermark(downloadLink);
                prepareStreamedBytes(downloadLink);
                return "viewerPdf?faces-redirect=true";
            case "image":
                prepareStreamedBytes(downloadLink);
                return "viewerImage?faces-redirect=true";
            default:
                downloadLink = FacesContext.getCurrentInstance().getExternalContext().getRealPath(File.separator)+"/resources/img/unsupported_format.pdf";
                prepareStreamedBytes(downloadLink);
                return "viewerPdf?faces-redirect=true";
        }
        prepareStreamedBytes(downloadLink);
        return "viewerPdf?faces-redirect=true";
    }

    public void prepareStreamedBytes(String downloadLink) throws IOException {
        InputStream is = new FileInputStream(downloadLink);
        byte[] streamedBytes = IOUtils.toByteArray(is);
        getSession().setAttribute("streamedBytes", streamedBytes);
    }

    public void openCommentDialog(ActionEvent event) {
        docName = (String) event.getComponent().getAttributes().get("docName");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('commentDialog').show();");
    }

    public void saveComment() {
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


    public boolean renderIfIsFolder() {
        Document doc = (Document) evaluateEL("#{document}", Document.class);
        return doc.getType().equals("Folder");
    }

    public boolean renderComponentDelete() {
        Document doc = (Document) evaluateEL("#{document}", Document.class);
        return !doc.getName().equals("Documents");
    }




    @PostConstruct
    public void setUp () {
        this.currentUser = ((LoginController) getSession().getAttribute("loginBean")).getCurrentUser();
        Integer chosenProjectId = (Integer) getSession().getAttribute("chosenProjectId");
        this.currentProject = projectManager.find(chosenProjectId);
        documentManager.setCurrentProject(this.currentProject);
        this.comments = (Map<String[], LinkedList<String>>) currentProject.getComments();
        this.isMayDownload = currentUser.isMayDownload();
        this.root = documentManager.createDocuments();

    }

}