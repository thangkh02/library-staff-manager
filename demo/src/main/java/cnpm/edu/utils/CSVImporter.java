package cnpm.edu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp xử lý việc đọc dữ liệu từ file CSV
 */
public class CSVImporter {

    /**
     * Đọc dữ liệu từ file CSV
     * 
     * @param file File CSV cần đọc
     * @return Danh sách dữ liệu thủ thư
     * @throws IOException              nếu có lỗi khi đọc file
     * @throws IllegalArgumentException nếu định dạng file không hợp lệ
     */
    public static List<CSVData> importFromCSV(File file) throws IOException, IllegalArgumentException {
        List<CSVData> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Đọc dòng tiêu đề
            if (line == null) {
                throw new IllegalArgumentException("File CSV trống");
            }

            // Kiểm tra định dạng tiêu đề (Cột có thể khác nhau về thứ tự, nhưng phải có đủ các trường bắt buộc)
            String[] headers = line.split(",");
            int fullNameIndex = -1;
            int salaryIndex = -1;
            int birthDateIndex = -1;
            int genderIndex = -1;
            int emailIndex = -1;
            int phoneNumberIndex = -1;
            int addressIndex = -1;
            int roleIndex = -1;

            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim().toLowerCase();
                switch (header) {
                    case "họ tên":
                    case "ho ten":
                    case "fullname":
                    case "hovaten":
                    case "hoten":
                        fullNameIndex = i;
                        break;
                    case "lương":
                    case "luong":
                    case "salary":
                        salaryIndex = i;
                        break;
                    case "ngày sinh":
                    case "ngay sinh":
                    case "birthdate":
                    case "ngaysinh":
                        birthDateIndex = i;
                        break;
                    case "giới tính":
                    case "gioi tinh":
                    case "gender":
                    case "gioitinh":
                        genderIndex = i;
                        break;
                    case "email":
                        emailIndex = i;
                        break;
                    case "số điện thoại":
                    case "so dien thoai":
                    case "phonenumber":
                    case "phone":
                    case "sdt":
                        phoneNumberIndex = i;
                        break;
                    case "địa chỉ":
                    case "dia chi":
                    case "address":
                    case "diachi":
                        addressIndex = i;
                        break;
                    case "vai trò":
                    case "vai tro":
                    case "role":
                    case "vaitro":
                        roleIndex = i;
                        break;
                }
            }

            // Kiểm tra đủ các cột cần thiết
            if (fullNameIndex == -1 || salaryIndex == -1 || birthDateIndex == -1 || 
                    genderIndex == -1 || emailIndex == -1 || phoneNumberIndex == -1 || addressIndex == -1) {
                throw new IllegalArgumentException("File CSV không có đủ các cột cần thiết");
            }

            // Đọc từng dòng dữ liệu
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = parseCsvLine(line);

                // Tìm chỉ số lớn nhất để kiểm tra đủ số cột
                int maxIndex = Math.max(
                    Math.max(
                        Math.max(fullNameIndex, salaryIndex), 
                        Math.max(birthDateIndex, genderIndex)
                    ),
                    Math.max(
                        Math.max(emailIndex, phoneNumberIndex), 
                                addressIndex));

                if (values.length <= maxIndex) {
                    continue; // Bỏ qua các dòng không đủ số cột
                }

                CSVData data = new CSVData();

                // Đọc các trường từ dòng dữ liệu
                data.setFullName(values[fullNameIndex]);

                try {
                    data.setSalary(Double.parseDouble(values[salaryIndex].replace(",", "")));
                } catch (NumberFormatException e) {
                    data.setSalary(0);
                    data.setError("Lương không hợp lệ");
                    dataList.add(data);
                    continue;
                }

                data.setBirthDate(values[birthDateIndex]);
                data.setGender(values[genderIndex]);
                data.setEmail(values[emailIndex]);
                data.setPhoneNumber(values[phoneNumberIndex]);
                data.setAddress(values[addressIndex]);

                if (roleIndex != -1 && roleIndex < values.length) {
                    data.setRole(values[roleIndex]);
                } else {
                    data.setRole("");
                }

                dataList.add(data);
            }
        }

        return dataList;
    }

    /**
     * Phân tích một dòng CSV, xử lý các trường hợp có giá trị chứa dấu phẩy trong
     * dấu ngoặc kép
     * 
     * @param line Dòng CSV cần phân tích
     * @return Mảng các giá trị đã được phân tách
     */
    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }

        values.add(sb.toString().trim());

        return values.toArray(new String[0]);
    }
}