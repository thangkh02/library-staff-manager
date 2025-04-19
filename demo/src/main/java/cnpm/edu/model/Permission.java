package cnpm.edu.model;

public class Permission {
    private int id;
    private String namePermission;
    private String description;

    public Permission(int id, String namePermission, String description) {
        this.id = id;
        this.namePermission = namePermission;
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamePermission() {
        return namePermission;
    }

    public void setNamePermission(String namePermission) {
        this.namePermission = namePermission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}