package com.app.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileService {

    // Hàm tiện ích để lấy đường dẫn gốc của problem
    public String getProblemPath(String problemName) {
        return "resources/" + problemName;
    }

    public void saveFile(String problemName, String subFolder, String fileName, String content) {
        try {
            // Tạo đường dẫn: data/ten_problem/subFolder
            String path = getProblemPath(problemName) + "/" + subFolder;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // Tạo toàn bộ phân cấp thư mục nếu chưa có
            }

            File file = new File(dir, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
        } catch (IOException e) {
            System.err.println("Lỗi lưu file: " + e.getMessage());
        }
    }
}