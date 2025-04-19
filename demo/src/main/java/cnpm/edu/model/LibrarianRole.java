package cnpm.edu.model;

import java.time.LocalDateTime;

public class LibrarianRole {
    private int librarianId;
    private int roleId;
    private LocalDateTime assignedDate;

    private Librarian librarian;
    private Role role;

    public LibrarianRole(int librarianId, int roleId, LocalDateTime assignedDate) {
        this.librarianId = librarianId;
        this.roleId = roleId;
        this.assignedDate = assignedDate;
    }

    public LibrarianRole(int librarianId, int roleId, LocalDateTime assignedDate,
            Librarian librarian, Role role) {
        this.librarianId = librarianId;
        this.roleId = roleId;
        this.assignedDate = assignedDate;

        this.librarian = librarian;
        this.role = role;
    }

    public LibrarianRole(Librarian librarian, Role role) {
        this.librarianId = librarian.getId();
        this.roleId = role.getId();
        this.assignedDate = LocalDateTime.now();
        this.librarian = librarian;
        this.role = role;
    }

    public LibrarianRole(Librarian librarian, Role role, LocalDateTime assignmentDate) {
        this.librarianId = librarian.getId();
        this.roleId = role.getId();
        this.assignedDate = assignmentDate != null ? assignmentDate : LocalDateTime.now();
        this.librarian = librarian;
        this.role = role;
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}