package cnpm.edu.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Librarian {
    private int id;
    private String fullName;
    private double salary;
    private LocalDate birthDate;
    private String gender;
    private String avatarUrl;
    private List<LibrarianRole> roleAssignments = new ArrayList<>();
    private String email;
    private String phoneNumber;
    private String address;

    public Librarian(int id, String fullName, double salary, LocalDate birthDate,
            String gender, String avatarUrl, List<Role> roles,
            String email, String phoneNumber, String address) {
        this.id = id;
        this.fullName = fullName;
        this.salary = salary;
        this.birthDate = birthDate;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.roleAssignments = roles != null ? roles.stream()
                .map(role -> new LibrarianRole(this, role))
                .collect(Collectors.toList()) : new ArrayList<>();
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Librarian(int id, String fullName, LocalDate birthDate,
            String gender, String avatarUrl, List<Role> roles,
            String email, String phoneNumber, String address) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.roleAssignments = roles != null ? roles.stream()
                .map(role -> new LibrarianRole(this, role))
                .collect(Collectors.toList()) : new ArrayList<>();
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Librarian() {
        this.roleAssignments = new ArrayList<>();
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Role> getRoles() {
        return roleAssignments.stream()
                .map(LibrarianRole::getRole)
                .collect(Collectors.toList());
    }

    public void setRoles(List<Role> roles) {
        this.roleAssignments = roles != null ? roles.stream()
                .map(role -> new LibrarianRole(this, role))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public void addRole(Role role) {
        if (roleAssignments.stream().noneMatch(r -> r.getRole().equals(role))) {
            roleAssignments.add(new LibrarianRole(this, role));
        }
    }

    public void removeRole(Role role) {
        roleAssignments.removeIf(r -> r.getRole().equals(role));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getPrimaryRole() {
        if (roleAssignments.isEmpty())
            return null;
        return roleAssignments.get(0).getRole();
    }

    public Role getRole() {
        return getPrimaryRole();
    }

    public void setRole(Role role) {
        if (role == null) {
            roleAssignments.clear();
        } else {
            if (roleAssignments.isEmpty()) {
                roleAssignments.add(new LibrarianRole(this, role));
            } else {
                roleAssignments.set(0, new LibrarianRole(this, role));
            }
        }
    }
}