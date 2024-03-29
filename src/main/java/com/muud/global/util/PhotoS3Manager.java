package com.muud.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PhotoS3Manager implements PhotoManager {

    private static final String SLASH = "/";
    private static final String SYSTEM_PATH = System.getProperty("user.dir");

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.root-path}")
    private String rootPath;

    @Override
    public String upload(MultipartFile multipartFile, String workingDirectory) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다. multipartFile: " + null);
        }
        return uploadPhoto(multipartFile, workingDirectory);
    }

    private String uploadPhoto(MultipartFile multipartFile, String workingDirectory) {
        try {
            String fileName = PhotoNameGenerator.of(multipartFile.getOriginalFilename());
            File uploadDirectory = loadDirectory(getLocalDirectoryPath(workingDirectory));
            File uploadPath = new File(uploadDirectory, fileName);
            File file = uploadFileInLocal(multipartFile, uploadPath);
            s3Client.putObject(new PutObjectRequest(bucket + workingDirectory, fileName, file));
            file.delete();
            return rootPath + workingDirectory + SLASH + fileName;
        } catch (Exception e) {
            throw new IllegalStateException("파일 업로드를 실패했습니다.");
        }
    }

    private String getLocalDirectoryPath(String workingDirectory) {
        return SYSTEM_PATH + SLASH + workingDirectory;
    }

    private File loadDirectory(String directoryLocation) {
        File directory = new File(directoryLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File uploadFileInLocal(MultipartFile multipartFile, File uploadPath) {
        try {
            multipartFile.transferTo(uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("파일 변환이 실패했습니다.");
        }
        return uploadPath;
    }

    @Override
    public void delete(String originalImageUrl, String workingDirectory) {
        if (originalImageUrl.contains(SLASH + workingDirectory)) {
            String fileName = originalImageUrl.substring(rootPath.length() + 1);
            s3Client.deleteObject(bucket, fileName);
        }
    }
}
