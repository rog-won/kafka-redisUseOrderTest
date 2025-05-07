package com.example.kafkaorder.controller;

import com.example.kafkaorder.entity.User;
import com.example.kafkaorder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 목록 페이지 (슈퍼 어드민만 접근 가능)
    @GetMapping
    public String getUserListPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("사용자 목록 페이지 접근 시도: " + userDetails.getUsername());
        System.out.println("현재 사용자 권한: " + userDetails.getAuthorities());
        
        // 슈퍼 어드민 역할 확인
        boolean isSuperAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
                
        System.out.println("슈퍼 어드민 여부: " + isSuperAdmin);
        
        if (!isSuperAdmin) {
            System.out.println("권한 부족으로 접근 거부 (메소드 레벨)");
            return "redirect:/";
        }
        
        model.addAttribute("activeMenu", "users");
        return "view/user/list";
    }

    // 사용자 목록 API (슈퍼 어드민만 접근 가능)
    @GetMapping("/api/list")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<User>> getUserList() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 사용자 역할 변경 API (슈퍼 어드민만 접근 가능)
    @PostMapping("/api/update-role")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @RequestParam String username,
            @RequestParam String newRole,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String currentUserRole = userService.getUserRole(userDetails.getUsername());
        boolean updated = userService.updateUserRole(username, newRole, currentUserRole);
        
        if (updated) {
            return ResponseEntity.ok().body(
                    new UserRoleUpdateResponse(true, "사용자 역할이 업데이트되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(
                    new UserRoleUpdateResponse(false, "사용자 역할 업데이트에 실패했습니다."));
        }
    }
    
    // 응답 클래스
    private static class UserRoleUpdateResponse {
        private final boolean success;
        private final String message;
        
        public UserRoleUpdateResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 