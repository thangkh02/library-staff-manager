package cnpm.edu.utils;

/**
 * Lớp lưu trữ dữ liệu từ file CSV
 */
public class CSVData {
    private String fullName;
    private double salary;
    private String birthDate;
    private String gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private String error;
    private String avatarUrl;

    /**
     * Constructor với tất cả trường
     */
    public CSVData(String fullName, double salary, String birthDate, String gender,
            String email, String phoneNumber, String address, String role) {
        this.fullName = fullName;
        this.salary = salary;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.error = "";
        this.avatarUrl = "avatars/avatar-trang-1.jpg"; // Avatar mặc định
    }

    /**
     * Constructor rỗng
     */
    public CSVData() {
        this.error = "";
        this.avatarUrl = "avatars/avatar-trang-1.jpg"; // Avatar mặc định
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "CSVData{" +
                "fullName='" + fullName + '\'' +
                ", salary=" + salary +
                ", birthDate='" + birthDate + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}