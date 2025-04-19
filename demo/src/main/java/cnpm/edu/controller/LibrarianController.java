package cnpm.edu.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import cnpm.edu.model.Librarian;
import cnpm.edu.model.LibrarianModel;
import cnpm.edu.model.Role;
import cnpm.edu.view.LibrarianDetailDialog;
import cnpm.edu.view.LibrarianFormDialog;
import cnpm.edu.view.LibrarianView;
import cnpm.edu.view.RoleFormDialog;

public class LibrarianController {
    private LibrarianModel model;
    private LibrarianView view;

    public LibrarianController(LibrarianModel model, LibrarianView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
        model.initializeData();
        updateView();
    }

    public void addLibrarian() {

        LibrarianFormDialog dialog = new LibrarianFormDialog(view, new ArrayList<>(model.getRoles()), model);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                String fullName = dialog.getNameField();
                double salary = dialog.getSalary();
                LocalDate birthDate = dialog.getBirthDate();
                String gender = dialog.getGender();
                String avatarUrl = dialog.getAvatarField();
                String email = dialog.getEmailField();
                String phoneNumber = dialog.getPhoneField();
                String address = dialog.getAddressField();
                List<String> roleNames = dialog.getSelectedRoles();
                List<Role> selectedRoles = new ArrayList<>();

                for (String roleName : roleNames) {
                    Role role = model.getRoles().stream()
                            .filter(r -> r.getNameRole().equals(roleName))
                            .findFirst().orElse(null);
                    if (role != null) {
                        selectedRoles.add(role);
                    }
                }

                model.addLibrarianWithRoles(fullName, salary, birthDate, gender, avatarUrl,
                        selectedRoles, email, phoneNumber, address);
                updateView();
                JOptionPane.showMessageDialog(view, "Thêm thủ thư thành công!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Dữ liệu không hợp lệ: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {

            System.out.println("Đã hủy thêm thủ thư mới");
        }
    }

    public void updateLibrarian() {
        int id = view.getSelectedLibrarianId();
        System.out.println("Đang sửa thủ thư với ID: " + id);

        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thủ thư để sửa.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Librarian librarian = model.getLibrarians().stream()
                .filter(l -> l.getId() == id)
                .findFirst().orElse(null);

        if (librarian == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy thủ thư với id " + id,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Đã tìm thấy thủ thư: " + librarian.getFullName());

        LibrarianFormDialog dialog = new LibrarianFormDialog(view, librarian, new ArrayList<>(model.getRoles()), model);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                String fullName = dialog.getNameField();
                double salary = dialog.getSalary();
                LocalDate birthDate = dialog.getBirthDate();
                String gender = dialog.getGender();
                String avatarUrl = dialog.getAvatarField();
                String email = dialog.getEmailField();
                String phoneNumber = dialog.getPhoneField();
                String address = dialog.getAddressField();

                // Kiểm tra trùng lặp email (loại trừ chính ID đang sửa)
                if (model.isEmailExists(email, id)) {
                    JOptionPane.showMessageDialog(view,
                            "Email này đã tồn tại với thủ thư khác trong hệ thống. Vui lòng sử dụng email khác.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra trùng lặp số điện thoại (loại trừ chính ID đang sửa)
                if (model.isPhoneExists(phoneNumber, id)) {
                    JOptionPane.showMessageDialog(view,
                            "Số điện thoại này đã tồn tại với thủ thư khác trong hệ thống. Vui lòng sử dụng số khác.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra trùng lặp thông tin thủ thư (loại trừ chính ID đang sửa)
                if (model.isLibrarianExists(fullName, birthDate, gender, id)) {
                    JOptionPane.showMessageDialog(view,
                            "Thủ thư có thông tin tương tự đã tồn tại trong hệ thống.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy danh sách vai trò được chọn
                List<String> roleNames = dialog.getSelectedRoles();
                List<Role> selectedRoles = new ArrayList<>();
                for (String roleName : roleNames) {
                    Role role = model.getRoles().stream()
                            .filter(r -> r.getNameRole().equals(roleName))
                            .findFirst().orElse(null);
                    if (role != null) {
                        selectedRoles.add(role);
                    }
                }

                // Cập nhật thủ thư với lương lấy từ form
                model.updateLibrarianWithRoles(id, fullName, salary, birthDate, gender,
                        avatarUrl, selectedRoles, email, phoneNumber, address);
                updateView();
                JOptionPane.showMessageDialog(view, "Cập nhật thủ thư thành công!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Dữ liệu không hợp lệ: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            // Người dùng đã hủy, không làm gì thêm
            System.out.println("Đã hủy cập nhật thủ thư");
        }
    }

    public void deleteLibrarian() {
        int id = view.getSelectedLibrarianId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thủ thư để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn xóa thủ thư này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            model.deleteLibrarian(id);
            updateView();
        }
    }

    // Cập nhật các phương thức hiện có để sử dụng combinedSearch
    public void searchLibrarian() {
        combinedSearch();
    }

    public void sortLibrarians() {
        combinedSearch();
    }

    public void filterLibrarians() {
        combinedSearch();
    }

    public void assignRole() {
        String selectedLibrarianStr = view.getSelectedLibrarianForAssign();
        String selectedRoleName = view.getSelectedRoleForAssign();
        if (selectedLibrarianStr == null || selectedRoleName == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn thủ thư và vai trò.");
            return;
        }
        int librarianId = Integer.parseInt(selectedLibrarianStr.split("ID: ")[1].replace(")", ""));
        Librarian librarian = model.getLibrarians().stream()
                .filter(l -> l.getId() == librarianId)
                .findFirst().orElse(null);
        Role role = model.getRoles().stream()
                .filter(r -> r.getNameRole().equals(selectedRoleName))
                .findFirst().orElse(null);
        if (librarian != null && role != null) {
            model.assignRoleToLibrarian(librarian, role);
            updateView();
            JOptionPane.showMessageDialog(view, "Gán vai trò thành công!");
        }
    }

    // Cập nhật phương thức updateView
    private void updateView() {
        model.getLibrarians();
        List<Librarian> librariansCopy = new ArrayList<>(model.getLibrarians());
        List<Role> rolesCopy = new ArrayList<>(model.getRoles());

        librariansCopy.sort((l1, l2) -> l1.getId() - l2.getId());

        view.updateTable(librariansCopy);
        view.updateRoleCombo(rolesCopy);
        view.updateLibrarianCombo(librariansCopy);
        view.updateRoleTable(rolesCopy);
    }

    public LibrarianModel getModel() {
        return model;
    }

    public void combinedSearch() {
        String searchText = view.getSearchField();
        String gender = view.getFilterGender();
        String ageRange = view.getFilterAge();
        String salaryRange = view.getFilterSalary();
        String sortCriteria = view.getSortCriteria();
        boolean sortAscending = view.isSortAscending();
        List<Librarian> filteredList = model.filter(searchText, gender, ageRange, salaryRange);

        sortLibrarians(filteredList, sortCriteria, sortAscending);

        view.updateTable(filteredList);
    }

    // Cập nhật phương thức sortLibrarians để sử dụng lương trực tiếp
    private void sortLibrarians(List<Librarian> librarians, String criteria, boolean ascending) {
        switch (criteria) {
            case "Mã":
                librarians.sort((l1, l2) -> ascending ? l1.getId() - l2.getId() : l2.getId() - l1.getId());
                break;
            case "Tên":
                librarians.sort((l1, l2) -> {
                    String lastName1 = getLastName(l1.getFullName());
                    String lastName2 = getLastName(l2.getFullName());
                    return ascending ? lastName1.compareTo(lastName2) : lastName2.compareTo(lastName1);
                });
                break;
            case "Tuổi":
                librarians.sort((l1, l2) -> ascending ? l1.getAge() - l2.getAge() : l2.getAge() - l1.getAge());
                break;
            case "Lương":
                // Sắp xếp trực tiếp theo lương từ đối tượng thủ thư
                librarians.sort((l1, l2) -> {
                    double salary1 = l1.getSalary();
                    double salary2 = l2.getSalary();
                    return ascending ? Double.compare(salary1, salary2) : Double.compare(salary2, salary1);
                });
                break;
        }
    }

    // Trích xuất tên cuối cùng từ họ tên
    private String getLastName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    // Đặt lại tất cả các điều kiện lọc về mặc định
    public void resetFilters() {
        view.resetFilters();
        updateView();
    }

    public void addRole() {
        RoleFormDialog dialog = new RoleFormDialog(view);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String roleName = dialog.getRoleName();
            String description = dialog.getRoleDescription();
            List<Integer> permissionIds = dialog.getSelectedPermissionIds();

            System.out.println("DEBUG: Thêm vai trò mới - Tên: " + roleName +
                    ", Mô tả: " + description +
                    ", Số quyền: " + permissionIds.size());

            // Kiểm tra tên vai trò trùng lặp ở đây thay vì trong phương thức addRole()
            if (model.isRoleNameExists(roleName, -1)) {
                JOptionPane.showMessageDialog(view,
                        "Tên vai trò này đã tồn tại trong hệ thống. Vui lòng chọn tên khác.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                // Mở lại dialog để người dùng có thể sửa tên
                addRole();
                return;
            }

            // Nếu không trùng mới gọi phương thức addRole
            addRole(roleName, description, permissionIds);
        }
    }

    public void addRole(String roleName, String description, List<Integer> permissionIds) {
        // Thêm kiểm tra tên vai trò trùng lặp
        if (model.isRoleNameExists(roleName, -1)) {
            JOptionPane.showMessageDialog(view,
                    "Tên vai trò này đã tồn tại trong hệ thống. Vui lòng chọn tên khác.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roleId = model.addRole(roleName, description);
        if (roleId > 0) {
            boolean permissionsAdded = model.updateRolePermissions(roleId, permissionIds);

            if (permissionsAdded) {
                JOptionPane.showMessageDialog(view,
                        "Đã thêm vai trò mới thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                updateView();
            } else {
                // Nếu thêm quyền thất bại, xóa vai trò đã thêm
                model.deleteRole(roleId);
                JOptionPane.showMessageDialog(view,
                        "Không thể thêm quyền cho vai trò. Thao tác đã bị hủy.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view,
                    "Không thể thêm vai trò mới. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRole() {
        int roleId = view.getSelectedRoleId();
        if (roleId == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn vai trò cần cập nhật.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tìm vai trò theo ID
        Role selectedRole = model.getRoles().stream()
                .filter(r -> r.getId() == roleId)
                .findFirst()
                .orElse(null);

        if (selectedRole == null) {
            JOptionPane.showMessageDialog(view,
                    "Không tìm thấy thông tin vai trò.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean editingCompleted = false;
        while (!editingCompleted) {
            RoleFormDialog dialog = new RoleFormDialog(view, selectedRole);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String roleName = dialog.getRoleName();
                String description = dialog.getRoleDescription();
                List<Integer> permissionIds = dialog.getSelectedPermissionIds();

                // Kiểm tra trùng lặp tên vai trò
                if (!roleName.equals(selectedRole.getNameRole()) && model.isRoleNameExists(roleName, roleId)) {
                    JOptionPane.showMessageDialog(view,
                            "Tên vai trò \"" + roleName + "\" đã tồn tại trong hệ thống.\nVui lòng chọn tên khác.",
                            "Lỗi - Tên vai trò trùng lặp", JOptionPane.ERROR_MESSAGE);
                    // Tiếp tục vòng lặp để hiển thị lại dialog
                    continue;
                }

                // Cập nhật vai trò và quyền
                boolean success = model.updateRole(roleId, roleName, description);
                if (success) {
                    // Cập nhật quyền cho vai trò
                    boolean permissionsUpdated = model.updateRolePermissions(roleId, permissionIds);
                    if (permissionsUpdated) {
                        JOptionPane.showMessageDialog(view,
                                "Đã cập nhật vai trò thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        updateView();
                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Cập nhật vai trò thành công nhưng không thể cập nhật quyền.\nHãy thử cập nhật quyền lại sau.",
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        updateView();
                    }
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Không thể cập nhật vai trò. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                editingCompleted = true;
            } else {
                // Người dùng đã hủy
                editingCompleted = true;
            }
        }
    }

    public void deleteRole() {
        int roleId = view.getSelectedRoleId();
        if (roleId == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn vai trò cần xóa.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        deleteRole(roleId);
    }

    // Phương thức thêm vai trò mới
    public void addRole(String roleName, String description) {
        if (model.isRoleNameExists(roleName, -1)) {
            JOptionPane.showMessageDialog(view,
                    "Tên vai trò này đã tồn tại trong hệ thống. Vui lòng chọn tên khác.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Thực hiện thêm vai trò mới
        int roleId = model.addRole(roleName, description); // Đã đổi từ boolean thành int
        if (roleId > 0) { // Kiểm tra nếu roleId lớn hơn 0 thay vì kiểm tra boolean
            JOptionPane.showMessageDialog(view,
                    "Đã thêm vai trò mới thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            updateView();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Không thể thêm vai trò mới. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức cập nhật vai trò
    public void updateRole(int roleId, String roleName, String description) {
        // Kiểm tra xem tên vai trò đã tồn tại chưa (loại trừ chính ID đang sửa)
        if (model.isRoleNameExists(roleName, roleId)) {
            JOptionPane.showMessageDialog(view,
                    "Tên vai trò này đã tồn tại trong hệ thống. Vui lòng chọn tên khác.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Thực hiện cập nhật vai trò
        boolean success = model.updateRole(roleId, roleName, description);
        if (success) {
            JOptionPane.showMessageDialog(view,
                    "Đã cập nhật vai trò thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            updateView();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Không thể cập nhật vai trò. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức xóa vai trò
    // Phương thức xóa vai trò
    public void deleteRole(int roleId) {
        // Hiển thị hộp thoại xác nhận
        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn xóa vai trò này?\n" +
                        "Lưu ý: Tất cả thủ thư có vai trò này sẽ bị gỡ vai trò này.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = model.deleteRole(roleId);
            if (success) {
                JOptionPane.showMessageDialog(view,
                        "Đã xóa vai trò thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                updateView();
            } else {
                JOptionPane.showMessageDialog(view,
                        "Không thể xóa vai trò. Vui lòng thử lại sau.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Thêm phương thức mới vào LibrarianController
    public void viewLibrarianDetails() {
        int id = view.getSelectedLibrarianId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn thủ thư để xem chi tiết.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tìm thủ thư cần xem chi tiết
        Librarian librarian = model.getLibrarians().stream()
                .filter(l -> l.getId() == id)
                .findFirst().orElse(null);

        if (librarian == null) {
            JOptionPane.showMessageDialog(view,
                    "Không tìm thấy thủ thư với ID " + id,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hiển thị dialog chi tiết
        LibrarianDetailDialog dialog = new LibrarianDetailDialog(view, librarian, model);
        dialog.setVisible(true);
    }
}