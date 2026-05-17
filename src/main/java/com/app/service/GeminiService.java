package com.app.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiService {
    private static final String API_KEY = "AIzaSyCebrvrtdkwxNDlvnyuYcRMYpk0zFAW-UA";
    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;

    public String callGemini(String prompt) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Tạo JSON request
            JSONObject part = new JSONObject().put("text", prompt);
            JSONArray parts = new JSONArray().put(part);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);
            JSONObject body = new JSONObject().put("contents", contents);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes("UTF-8"));
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            if (status >= 200 && status < 300) {
                JSONObject json = new JSONObject(response.toString());
                JSONArray candidates = json.optJSONArray("candidates");
                if (candidates != null && candidates.length() > 0) {
                    JSONObject contentObj = candidates.getJSONObject(0).getJSONObject("content");
                    JSONArray partsArr = contentObj.getJSONArray("parts");
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < partsArr.length(); i++) {
                        result.append(partsArr.getJSONObject(i).optString("text")).append("\n");
                    }
                    return result.toString().trim();
                } else {
                    return "Không có phản hồi từ AI.";
                }
            } else {
                return "Lỗi HTTP " + status + ": " + response.toString();
            }

        } catch (Exception e) {
            return "Lỗi khi kết nối AI: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public String generateTestCases(String deBai) {
        String prompt = "Dựa vào đề bài, hãy sinh 10 bộ testcase bao quát đầy đủ trường hợp. Trả về đúng định dạng sau cho mỗi test:\n" + //
                        "[TEST_START]\n" + //
                        "[IN]\n" + //
                        "(nội dung input)\n" + //
                        "[OUT]\n" + //
                        "(nội dung output)\n" + //
                        "[TEST_END] "
                + "\n\n Đề bài: \n" + deBai;
        return callGemini(prompt);
    }

    public String generateSourceCode(String deBai) {
        String prompt = "Viết code C++ hoàn chỉnh cho bài toán này chỉ viết code không giải thích gì thêm. Trả về đúng định dạng:\n" + //
                        "[AC_CODE] (nội dung code đúng) [END_AC]\n" + //
                        "[WA_CODE] (nội dung code sai) [END_WA]\n" + //
                        "[TLE_CODE] (nội dung code chạy chậm) [END_TLE] "
                + "\n\n Đề bài: \n" + deBai;
        return callGemini(prompt);
    }
}
