/**
 * 메인 대시보드 JavaScript 코드
 */

document.addEventListener('DOMContentLoaded', function() {
    // 인증 상태 확인 및 처리
    setupMainPage();
    
    // 로그아웃 버튼 이벤트 설정
    setupLogoutButton();
});

// 메인 페이지 초기화
function setupMainPage() {
    const authStatus = checkAuthStatus();
    
    if (authStatus.isLoggedIn) {
        // 로그인 된 상태
        document.getElementById('userInfo').style.display = 'block';
        document.getElementById('welcomeMessage').textContent = `${authStatus.name}님 환영합니다!`;
        
        if (document.getElementById('notLoggedInMessage')) {
            document.getElementById('notLoggedInMessage').style.display = 'none';
        }
    } else {
        // 로그인되지 않은 상태 - 로그인 페이지로 리다이렉트
        redirectIfNotLoggedIn();
    }
}

// 로그아웃 버튼 이벤트 설정
function setupLogoutButton() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            // 토큰 삭제
            clearAuthToken();
            
            // 로그인 페이지로 이동
            window.location.href = '/view/auth/login';
        });
    }
} 