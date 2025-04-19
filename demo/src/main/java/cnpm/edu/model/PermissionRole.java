package cnpm.edu.model;

public class PermissionRole {
    private int permissionId;
    private int roleId;
    public PermissionRole(int permissionId, int roleId) {
        this.permissionId = permissionId;
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}