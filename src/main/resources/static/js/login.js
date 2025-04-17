/**
 * 로그인 페이지 JavaScript 코드
 */

document.addEventListener('DOMContentLoaded', function() {
    // 이미 로그인되어 있는지 확인하고 리다이렉션
    redirectIfLoggedIn();
    
    // 로그인 폼 이벤트 설정
    setupLoginForm();
});

// 로그인 폼 이벤트 설정
function setupLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const loginData = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };
            
            // 로그인 요청
            loginUser(loginData)
                .then(() => {
                    // 인덱스 페이지로 이동
                    window.location.href = '/index';
                })
                .catch(error => {
                    // 오류 메시지 표시
                    const errorMessageElement = document.getElementById('errorMessage');
                    errorMessageElement.textContent = '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.';
                    errorMessageElement.style.display = 'block';
                });
        });
    }
} 