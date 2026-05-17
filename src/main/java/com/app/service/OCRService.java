package com.app.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class OCRService {
    public String extractTextFromImage(String imagePath) {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath("tessdata"); 
            tesseract.setLanguage("vie+eng"); // Đọc cả tiếng Việt và tiếng Anh
            return tesseract.doOCR(new File(imagePath));
        } catch (TesseractException e) {
            return "Lỗi khi đọc ảnh: " + e.getMessage();
        }
    }
}
