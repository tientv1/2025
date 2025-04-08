package com.assignment.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @SuppressWarnings("rawtypes")
    public String uploadImage(MultipartFile file) throws IOException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("folder", "products");
            params.put("resource_type", "auto");

            Map result = cloudinary.uploader().upload(file.getBytes(), params);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Error uploading file to Cloudinary", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "image"));
        return uploadResult.get("secure_url").toString();
    }
}