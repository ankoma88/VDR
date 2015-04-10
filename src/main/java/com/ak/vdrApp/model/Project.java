package com.ak.vdrApp.model;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name= "PROJECT")
@Named
@RequestScoped
public class Project implements Serializable {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PROJECT_ID")
    private int id;

    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "PROJECT_NAME")
    private String projectName;

    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "START_DATE")
    private String startDate;

    @Column(name = "EXPIRATION_DATE")
    private String expirationDate;

    @Lob
    @Basic(optional = false)
    @Column(name = "LOGO", columnDefinition="LONGBLOB")
    private byte[] logo = "".getBytes();

    @ManyToOne
    @JoinColumn(name = "PROJECT_ADMIN", referencedColumnName = "ID")
    private User projectAdmin;

    @ManyToMany(mappedBy = "projects", cascade = CascadeType.ALL)
    @JoinTable(name = "PROJECT_VDR_USER")
    private List<User> users;


    @Lob
    @Basic(optional = false)
    @Column(name = "DOCMAP", columnDefinition = "LONGBLOB")
    private Serializable docMap;

    @Lob
    @Basic(optional = false)
    @Column(name = "COMMENTS", columnDefinition = "LONGBLOB")
    private Serializable comments = new LinkedHashMap<String[], LinkedList<String>>();




    public Project() {
         setupDefaultDocMap();
    }

    public Project(String projectName, String description) {
        this.projectName = projectName;
        this.description = description;
    }

    public Project(String projectName, String description, String startDate, String expirationDate, byte[] logo) {
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(Date startDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        this.startDate = format.format(startDate);
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        this.expirationDate = format.format(expirationDate);
    }

    public Serializable getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public User getProjectAdmin() {
        return projectAdmin;
    }

    public void setProjectAdmin(User projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    public Serializable getDocMap() {
        return docMap;
    }

    public void setDocMap(Serializable docMap) {
        this.docMap = docMap;
    }

    public Serializable getComments() {
        return comments;
    }

    public void setComments(Serializable comments) {
        this.comments = comments;
    }

    public void setupDefaultDocMap() {
        LinkedHashMap<String, TreeNode> map = new LinkedHashMap<>();
        Document data = new Document("root", "-", "Folder");
        TreeNode root = new DefaultTreeNode(data, null);
        map.put(data.getName(), root);
        this.docMap = map;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return id == project.id && projectName.equals(project.projectName) && description.equals(project.description);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + projectName.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                '}';
    }
}
