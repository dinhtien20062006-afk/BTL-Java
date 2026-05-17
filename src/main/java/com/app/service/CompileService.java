package com.app.service;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class CompileService {

    public String compileAndRun(String problemName, String codeFileName, String inputFilePath, String expectedOutputFilePath) {
        String baseDir = "resources/" + problemName;
        String exePath = baseDir + "/program.exe";
        
        try {
            // 1. BIÊN DỊCH (Compile)
            ProcessBuilder compileBuilder = new ProcessBuilder("g++", codeFileName, "-o", exePath);
            compileBuilder.directory(new File(".")); // Chạy lệnh tại thư mục gốc project
            Process compileProcess = compileBuilder.start();
            
            // Đợi biên dịch xong (tối đa 10 giây)
            if (!compileProcess.waitFor(10, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
                return "LỖI BIÊN DỊCH";
            }

            // 2. THỰC THI (Execute)
            ProcessBuilder runBuilder = new ProcessBuilder(new File(exePath).getAbsolutePath());
            runBuilder.redirectInput(new File(inputFilePath)); // Nạp file .in
            
            long startTime = System.currentTimeMillis();
            Process runProcess = runBuilder.start();
            
            // Đọc kết quả đầu ra
            StringBuilder actualOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    actualOutput.append(line.trim()).append("\n");
                }
            }

            // Giới hạn thời gian chạy (ví dụ 2 giây cho TLE)
            if (!runProcess.waitFor(2, TimeUnit.SECONDS)) {
                runProcess.destroyForcibly();
                return "TLE (Time Limit Exceeded)";
            }
            long duration = System.currentTimeMillis() - startTime;

            // 3. KIỂM TRA KẾT QUẢ (Check Answer)
            String expected = new String(Files.readAllBytes(Paths.get(expectedOutputFilePath))).trim();
            String actual = actualOutput.toString().trim();

            if (actual.equals(expected)) {
                return "AC (" + duration + "ms)";
            } else {
                return "WA (Wrong Answer)";
            }

        } catch (Exception e) {
            return "LỖI HỆ THỐNG: " + e.getMessage();
        }
    }
}