package com.example.kafkaorder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 에러 로그를 파일에 저장하는 서비스
 */
@Slf4j
@Service
public class ErrorLogService {

    @Value("${app.error-log.directory:./logs/errors}")
    private String errorLogDirectory;

    private final ObjectMapper objectMapper;

    public ErrorLogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // errorLogDirectory가 null이면 기본값 설정
        if (errorLogDirectory == null || errorLogDirectory.trim().isEmpty()) {
            errorLogDirectory = "./logs/errors";
        }
        createErrorLogDirectory();
    }

    /**
     * 에러 로그를 파일에 저장합니다.
     */
    public void logError(String requestUrl, String method, String errorType, String errorMessage, 
                        String stackTrace, String userAgent, String clientIp) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String fileName = "error-" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
            Path filePath = Paths.get(errorLogDirectory, fileName);

            // 에러 로그 데이터 구성
            Map<String, Object> errorLog = new HashMap<>();
            errorLog.put("timestamp", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            errorLog.put("requestUrl", requestUrl);
            errorLog.put("httpMethod", method);
            errorLog.put("errorType", errorType);
            errorLog.put("errorMessage", errorMessage);
            errorLog.put("stackTrace", stackTrace);
            errorLog.put("userAgent", userAgent);
            errorLog.put("clientIp", clientIp);

            // JSON 형태로 파일에 추가
            String jsonLog = objectMapper.writeValueAsString(errorLog);
            
            // 파일에 로그 추가 (한 줄씩 JSON 객체 저장)
            try (FileWriter writer = new FileWriter(filePath.toFile(), true)) {
                writer.write(jsonLog + System.lineSeparator());
            }

            log.debug("에러 로그가 파일에 저장되었습니다: {}", filePath);
            
        } catch (IOException e) {
            log.error("에러 로그 파일 저장 중 오류 발생", e);
        }
    }

    /**
     * 특정 날짜의 에러 로그 파일을 읽어옵니다.
     */
    public String getErrorLogsByDate(String date) {
        try {
            String fileName = "error-" + date + ".json";
            Path filePath = Paths.get(errorLogDirectory, fileName);
            
            if (Files.exists(filePath)) {
                return Files.readString(filePath);
            } else {
                return "해당 날짜의 에러 로그가 없습니다.";
            }
        } catch (IOException e) {
            log.error("에러 로그 파일 읽기 중 오류 발생", e);
            return "에러 로그 파일을 읽는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /**
     * 에러 로그 디렉토리 내의 모든 파일 목록을 반환합니다.
     */
    public String[] getErrorLogFileList() {
        try {
            File directory = new File(errorLogDirectory);
            if (directory.exists() && directory.isDirectory()) {
                return directory.list((dir, name) -> name.startsWith("error-") && name.endsWith(".json"));
            }
            return new String[0];
        } catch (Exception e) {
            log.error("에러 로그 파일 목록 조회 중 오류 발생", e);
            return new String[0];
        }
    }

    /**
     * 에러 로그 디렉토리가 없으면 생성합니다.
     */
    private void createErrorLogDirectory() {
        try {
            Path path = Paths.get(errorLogDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("에러 로그 디렉토리가 생성되었습니다: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("에러 로그 디렉토리 생성 중 오류 발생", e);
        }
    }

    /**
     * 에러 로그 디렉토리 경로를 반환합니다.
     */
    public String getErrorLogDirectory() {
        return new File(errorLogDirectory).getAbsolutePath();
    }
} 