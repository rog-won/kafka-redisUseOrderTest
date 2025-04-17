/**
 * 회원가입 페이지 JavaScript 코드
 */

document.addEventListener('DOMContentLoaded', function() {
    // 이미 로그인되어 있는지 확인하고 리다이렉션
    redirectIfLoggedIn();
    
    // 회원가입 관련 이벤트 설정
    setupSignupPage();
});

// 회원가입 관련 이벤트 설정
function setupSignupPage() {
    // 아이디 중복 체크 버튼
    setupUsernameCheck();
    
    // 비밀번호 확인 입력 체크
    setupPasswordConfirmCheck();
    
    // 회원가입 폼 제출
    setupSignupForm();
}

// 아이디 중복 체크
function setupUsernameCheck() {
    const checkUsernameBtn = document.getElementById('checkUsernameBtn');
    if (checkUsernameBtn) {
        checkUsernameBtn.addEventListener('click', function() {
            const username = document.getElementById('username').value;
            if (!username) {
                showUsernameMessage('아이디를 입력해주세요.', false);
                return;
            }
            
            fetch(`/api/auth/check-username?username=${username}`)
                .then(response => response.json())
                .then(data => {
                    showUsernameMessage(data.message, data.available);
                })
                .catch(error => {
                    showUsernameMessage('중복 확인 중 오류가 발생했습니다.', false);
                });
        });
    }
}

// 아이디 중복 체크 메시지 표시
function showUsernameMessage(message, isAvailable) {
    const messageElement = document.getElementById('usernameMessage');
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.className = isAvailable ? 'success-message' : 'error-message';
    }
}

// 비밀번호 확인 체크 설정
function setupPasswordConfirmCheck() {
    const confirmPassword = document.getElementById('confirmPassword');
    if (confirmPassword) {
        confirmPassword.addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            const messageElement = document.getElementById('passwordMatchMessage');
            
            if (password && confirmPassword) {
                if (password === confirmPassword) {
                    messageElement.textContent = '비밀번호가 일치합니다.';
                    messageElement.className = 'success-message';
                } else {
                    messageElement.textContent = '비밀번호가 일치하지 않습니다.';
                    messageElement.className = 'error-message';
                }
            } else {
                messageElement.textContent = '';
            }
        });
    }
}

// 회원가입 폼 설정
function setupSignupForm() {
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }
            
            const userData = {
                username: document.getElementById('username').value,
                password: password,
                name: document.getElementById('name').value,
                email: document.getElementById('email').value
            };
            
            fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(data => {
                const successMessage = document.getElementById('successMessage');
                if (successMessage) {
                    successMessage.style.display = 'block';
                }
                signupForm.reset();
                window.scrollTo(0, 0);
            })
            .catch(error => {
                alert('회원가입 실패: ' + error.message);
            });
        });
    }
} 