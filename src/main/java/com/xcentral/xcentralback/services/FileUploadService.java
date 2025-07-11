
package com.xcentral.xcentralback.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {

    private final String uploadDir = "uploads/profile_images/";

    public String uploadProfileImage(MultipartFile file, String username) throws IOException {
        // Create directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Use username + original extension as filename
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String filename = username + extension;
        Path filePath = Paths.get(uploadDir, filename);

        // Save file
        Files.write(filePath, file.getBytes());

        // Return relative path or URL to be saved in DB
        return "/uploads/profile_images/" + filename;
    }
}
