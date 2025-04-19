package cnpm.edu.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Role {
    private int id;
    private String nameRole;
    private String description;
    private LocalDateTime createDate;

  
    public Role(int id, String nameRole, String description) {
        this.id = id;
        this.nameRole = nameRole;
        this.description = description;
        this.createDate = LocalDateTime.now();
    }

    public Role(int id, String nameRole, String description, LocalDateTime createDate) {
        this.id = id;
        this.nameRole = nameRole;
        this.description = description;
        this.createDate = createDate;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameRole() {
        return nameRole;
    }

    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    private LocalDate assignmentDate;

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

}