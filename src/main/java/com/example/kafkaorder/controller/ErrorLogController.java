package com.example.kafkaorder.controller;

import com.example.kafkaorder.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 에러 로그 관리 컨트롤러
 */
@Slf4j
@Controller
@Profile({"dev", "test", "prod"})
@RequestMapping("/admin/error-logs")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    /**
     * 에러 로그 관리 페이지
     */
    @GetMapping
    public String errorLogPage(Model model) {
        // 오늘 날짜를 기본값으로 설정
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        model.addAttribute("selectedDate", today);
        
        // 에러 로그 파일 목록 조회
        String[] logFiles = errorLogService.getErrorLogFileList();
        model.addAttribute("logFiles", logFiles);
        
        // 에러 로그 디렉토리 경로
        model.addAttribute("logDirectory", errorLogService.getErrorLogDirectory());
        
        return "admin/error-logs";
    }

    /**
     * 특정 날짜의 에러 로그 조회 (API)
     */
    @GetMapping("/api/{date}")
    @ResponseBody
    public ResponseEntity<String> getErrorLogsByDate(@PathVariable String date) {
        try {
            String logs = errorLogService.getErrorLogsByDate(date);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("에러 로그 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body("에러 로그를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 에러 로그 파일 목록 조회 (API)
     */
    @GetMapping("/api/files")
    @ResponseBody
    public ResponseEntity<String[]> getErrorLogFiles() {
        try {
            String[] files = errorLogService.getErrorLogFileList();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("에러 로그 파일 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(new String[0]);
        }
    }

    /**
     * 에러 로그 페이지 (날짜별)
     */
    @GetMapping("/{date}")
    public String errorLogByDate(@PathVariable String date, Model model) {
        model.addAttribute("selectedDate", date);
        
        // 에러 로그 파일 목록 조회
        String[] logFiles = errorLogService.getErrorLogFileList();
        model.addAttribute("logFiles", logFiles);
        
        // 선택된 날짜의 로그 조회
        String logs = errorLogService.getErrorLogsByDate(date);
        model.addAttribute("errorLogs", logs);
        
        // 에러 로그 디렉토리 경로
        model.addAttribute("logDirectory", errorLogService.getErrorLogDirectory());
        
        return "admin/error-logs";
    }
} 