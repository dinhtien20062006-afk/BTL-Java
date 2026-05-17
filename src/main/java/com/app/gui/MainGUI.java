package com.app.gui;

import javax.swing.*;

import com.app.service.CompileService;
import com.app.service.FileService;
import com.app.service.GeminiService;
import com.app.service.OCRService;

import java.awt.*;
import java.io.File;

public class MainGUI extends JFrame {
    private JTextArea txtDeBai;
    private JTextArea txtBaoCao;
    private JButton btnNhapAnh, btnPhanTich, btnTestCase, btnCode, btnRun;

    public MainGUI() {
        // Thiết lập khung cửa sổ
        setTitle("AI Programming Contest Helper");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // 1. Vùng nhập liệu (Bên trái)
        JPanel pnlLeft = new JPanel(new BorderLayout());
        txtDeBai = new JTextArea();
        txtDeBai.setLineWrap(true);
        pnlLeft.add(new JScrollPane(txtDeBai), BorderLayout.CENTER);
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Nội dung đề bài (Text)"));

        // 2. Vùng báo cáo (Bên phải)
        JPanel pnlRight = new JPanel(new BorderLayout());
        txtBaoCao = new JTextArea(10, 50);
        txtBaoCao.setEditable(false);
        txtBaoCao.setBackground(new Color(240, 240, 240));
        pnlRight.add(new JScrollPane(txtBaoCao), BorderLayout.CENTER);
        pnlRight.setBorder(BorderFactory.createTitledBorder("Kết quả"));

        // 3. Thanh công cụ (Bên dưới)
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNhapAnh = new JButton("Nạp đề từ Ảnh");
        btnPhanTich = new JButton("Phân tích đề");
        btnTestCase = new JButton("Tạo Test Case");
        btnCode = new JButton("Sinh Code AC/WA/TLE");
        btnRun = new JButton("Chạy Code");

        pnlTools.add(btnNhapAnh);
        pnlTools.add(btnPhanTich);
        pnlTools.add(btnTestCase);
        pnlTools.add(btnCode);
        pnlTools.add(btnRun);

        // Thêm vào cửa sổ chính
        add(pnlLeft, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.EAST);
        add(pnlTools, BorderLayout.SOUTH);

        // Sự kiện cho nút "Nạp đề từ Ảnh"
            btnNhapAnh.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Hiển thị trạng thái đang xử lý
                txtBaoCao.setText("Đang quét ảnh, vui lòng đợi...");
                
                // Chạy OCR (Nên chạy trong luồng phụ để không treo giao diện)
                new Thread(() -> {
                    OCRService ocr = new OCRService();
                    String text = ocr.extractTextFromImage(selectedFile.getAbsolutePath());
                    
                    // Cập nhật kết quả lên ô nhập liệu
                    SwingUtilities.invokeLater(() -> {
                        txtDeBai.setText(cleanMarkdown(text));
                        txtBaoCao.setText("Đã nạp đề bài từ ảnh thành công!");
                    });
                }).start();
            }
        });

            btnPhanTich.addActionListener(e -> {
            String deBai = txtDeBai.getText();
            if (deBai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đề bài!");
                return;
            }

            txtBaoCao.setText("AI đang phân tích cấu trúc đề bài...");
            
            new Thread(() -> {
                GeminiService gemini = new GeminiService();
                // Prompt tối ưu để AI trả về thông tin tóm tắt
                String prompt = "Hãy phân tích đề bài sau và tóm tắt theo các mục: "
                        + "1. Tóm tắt yêu cầu bài toán. "
                        + "2. Định dạng Input, Output. "   
                        + "3. Các ràng buộc (Constraints) quan trọng. "
                        + "4. Đề xuất ý tưởng, thuật toán giải quyết. "
                        + "\n\n Đề bài: \n" + deBai;

                String ketQua = gemini.callGemini(prompt);

                SwingUtilities.invokeLater(() -> {
                    txtBaoCao.setText("--- KẾT QUẢ PHÂN TÍCH ---\n" + cleanMarkdown(ketQua));
                });
            }).start();
        });

            btnTestCase.addActionListener(e -> {
            String deBai = txtDeBai.getText();
            if (deBai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đề trước!");
                return;
            }

            txtBaoCao.setText("AI đang thiết kế các bộ test (Input/Output)...");

            String problemName = getProblemName();

            new Thread(() -> {
                GeminiService gemini = new GeminiService();
                FileService fileService = new FileService();
                
                String response = gemini.generateTestCases(deBai);

              // Logic bóc tách từng test
            String[] tests = response.split("\\[TEST_START\\]");
            int count = 1;
            for (String t : tests) {
                if (t.contains("[IN]") && t.contains("[OUT]")) {
                    String input = t.split("\\[IN\\]")[1].split("\\[OUT\\]")[0].trim();
                    String output = t.split("\\[OUT\\]")[1].split("\\[TEST_END\\]")[0].trim();
                    
                    fileService.saveFile(problemName, "testcases", "test" + count + ".in", input);
                    fileService.saveFile(problemName, "testcases", "test" + count + ".out", output);
                    count++;
                }
            }
            SwingUtilities.invokeLater(() -> {
                txtBaoCao.setText("--- ĐÃ SINH XONG TEST CASE ---\n"
                    + cleanMarkdown(response)
                    + "\n\nĐã lưu test case vào /resources/" + problemName + "/testcases/");
            });
                
            }).start();
        });

            btnCode.addActionListener(e -> {
            String deBai = txtDeBai.getText();
            if (deBai.isEmpty()) return;
            String problemName = getProblemName();
            txtBaoCao.setText("AI đang viết các phiên bản code mẫu...");

            new Thread(() -> {
                GeminiService gemini = new GeminiService();
                FileService fileService = new FileService();

                String response = gemini.generateSourceCode(deBai);

                SwingUtilities.invokeLater(() -> {
                    txtBaoCao.setText("--- ĐÃ SINH XONG MÃ NGUỒN ---\n"
                        + cleanMarkdown(response)
                        + "\n\nĐã lưu mã nguồn vào /resources/" + problemName + "/codes/");
                });
                
                // Logic bóc tách code AC/WA/TLE
                if (response.contains("[AC_CODE]")) {
                String ac = response.split("\\[AC_CODE\\]")[1].split("\\[END_AC\\]")[0].trim();
                fileService.saveFile(problemName, "codes", problemName + "_AC.cpp", ac);
                }
                if (response.contains("[WA_CODE]")) {
                String wa = response.split("\\[WA_CODE\\]")[1].split("\\[END_WA\\]")[0].trim();
                fileService.saveFile(problemName, "codes", problemName + "_WA.cpp", wa);
                }
                if (response.contains("[TLE_CODE]")) {
                String tle = response.split("\\[TLE_CODE\\]")[1].split("\\[END_TLE\\]")[0].trim();
                fileService.saveFile(problemName, "codes", problemName + "_TLE.cpp", tle);
                }

            }).start();
        });

            btnRun.addActionListener(e -> {
            String problemName = getProblemName(); // Lấy từ dòng 1 của TextArea
            txtBaoCao.setText("--- BẮT ĐẦU BIÊN DỊCH VÀ CHẠY: " + problemName + "_AC.cpp" + " ---\n");

            new Thread(() -> {
                CompileService compiler = new CompileService();
                String codePath = "resources/" + problemName + "/codes/" + problemName + "_AC.cpp";
                File testFolder = new File("resources/" + problemName + "/testcases");
                
                // Lấy danh sách file đầu vào .in
                File[] inputs = testFolder.listFiles((dir, name) -> name.endsWith(".in"));
                
                if (inputs == null || inputs.length == 0) {
                    SwingUtilities.invokeLater(() -> txtBaoCao.append("Không tìm thấy testcase nào!"));
                    return;
                }

                for (File inFile : inputs) {
                    String testName = inFile.getName().replace(".in", "");
                    String expectedOutPath = inFile.getAbsolutePath().replace(".in", ".out");
                    
                    // Gọi hàm chấm
                    String status = compiler.compileAndRun(problemName, codePath, inFile.getAbsolutePath(), expectedOutPath);
                    
                    SwingUtilities.invokeLater(() -> {
                        txtBaoCao.append(testName + ": " + status + "\n");
                    });
                }
                SwingUtilities.invokeLater(() -> txtBaoCao.append("\n--- HOÀN THÀNH ---"));
            }).start();
        });
    }

    // Hàm hỗ trợ lấy tên Problem từ dòng đầu của JTextArea
    private String getProblemName() {
        String text = txtDeBai.getText().trim();
        if (text.isEmpty()) return "UnknownProblem";
        return text.split("\n")[0].replaceAll("[^a-zA-Z0-9]", ""); // Lấy dòng 1, bỏ ký tự đặc biệt
    }

    private String cleanMarkdown(String text) {
    if (text == null) return "";

    return text
        .replaceAll("(?m)^#{1,6}\\s*", "")
        .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
        .replaceAll("\\*(.*?)\\*", "$1")
        .replaceAll("_(.*?)_", "$1")
        .replaceAll("(?m)^\\s*[-*]\\s+", "• ")
        .replaceAll("\\$([^$]+)\\$", "$1")
        .replaceAll("```[\\s\\S]*?```", "")
        .replaceAll("\\[([^\\]]+)\\]\\(([^)]+)\\)", "$1")
        .replaceAll("(?m)^\\s+", "")
        .trim();
    }

    public static void main(String[] args) {
        // Chạy giao diện
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}