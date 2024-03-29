package com.muud.global.util;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoManager {

    String upload(MultipartFile file, String workingDirectory);

    void delete(String originalImageUrl, String workingDirectory);
}
