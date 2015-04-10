package com.ak.vdrApp.service;

import com.ak.vdrApp.controller.BaseController;
import com.ak.vdrApp.model.Document;
import com.ak.vdrApp.model.Project;
import com.ak.vdrApp.service.exceptions.ProjectNotFoundException;
import com.ak.vdrApp.util.Constants;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Objects;


@ManagedBean(name = "documentManager")
@ApplicationScoped
public class DocumentManager extends BaseController {

    static final Logger log = LoggerFactory.getLogger(DocumentManager.class);

    @EJB
    private ProjectManagerLocal projectManager;

    private Project currentProject;

    private LinkedHashMap<String, TreeNode> mapDataToTreeNode = new LinkedHashMap<>();

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public LinkedHashMap<String, TreeNode> getMapDataToTreeNode() {
        return mapDataToTreeNode;
    }

    public TreeNode createDocuments() {

        this.mapDataToTreeNode  = (LinkedHashMap<String, TreeNode>) currentProject.getDocMap();
        TreeNode root = mapDataToTreeNode.get("root");
        assert root !=null;

        if (mapDataToTreeNode.keySet().contains("Documents")) {
            return root;
        }
        createFolder(root, "Documents");
        return root;
    }

    public TreeNode createFolder(TreeNode relativeRoot, String folderName) {

        if (mapDataToTreeNode.keySet().contains(folderName)) {
            FacesMessage message = new FacesMessage("Folder already exists.", "Folder names must be unique.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }

        Document data = new Document(folderName, "-", "Folder");
        TreeNode newFolder = new DefaultTreeNode(data, relativeRoot);
        mapDataToTreeNode.put(data.getName(), newFolder);
        try {
            projectManager.updateProject(currentProject.getId(), mapDataToTreeNode);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
        }
        return newFolder;
    }

    public TreeNode createDocument(TreeNode relativeRoot, String docName, UploadedFile file) {
        Document data = new Document(docName, "-", "Document");
        if (file.getSize() >0) {
            String prefix = FilenameUtils.getBaseName(file.getFileName());
            String suffix = FilenameUtils.getExtension(file.getFileName());
            File savedFile = null;
            try {
                savedFile = File.createTempFile(prefix + "-", "." + suffix, new File(Constants.STORAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream is = null;
            try {
                is = file.getInputstream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                assert savedFile != null;
                out = new FileOutputStream(savedFile);
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

            if (Objects.equals(data.getName(), "")) {
            data.setName(file.getFileName());
            }
            data.setLink(savedFile.getAbsolutePath());
            data.setSize(String.valueOf(file.getSize()/1000 + " kb" ));

                String shortType;
                switch (file.getContentType()) {
                    case "application/pdf": shortType="pdf"; break;
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": shortType="document"; break;
                    case "application/msword": shortType="doc"; break;
                    case "application/vnd.openxmlformats-officedocument.presentationml.presentation": shortType="presentation"; break;
                    case "application/vnd.ms-powerpoint": shortType="ppt"; break;
                    case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": shortType="sheet"; break;
                    case "application/vnd.ms-excel": shortType="xls"; break;

                    case "application/octet-stream": shortType ="text/plain";
                        String  oldName = data.getName();
                        data.setName(oldName+".txt");
                        break;

                    case "image/jpeg":
                    case "image/png":
                    case "image/tiff":
                        shortType= "image";
                        break;
                    default: shortType=file.getContentType(); break;
                }
            data.setType(shortType);
        }

        TreeNode newDoc = new DefaultTreeNode("Document", data, relativeRoot);
        mapDataToTreeNode.put(data.getName(), newDoc);
        try {
            projectManager.updateProject(currentProject.getId(), this.mapDataToTreeNode);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
        }
        return newDoc;
    }

    public void deleteNode(String docName) {
        TreeNode node = mapDataToTreeNode.get(docName);
        node.getChildren().clear();
        node.getParent().getChildren().remove(node);
        node.setParent(null);
//        node = null;
        mapDataToTreeNode.remove(docName);
        try {
            projectManager.updateProject(currentProject.getId(), this.mapDataToTreeNode);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
        }
    }


}