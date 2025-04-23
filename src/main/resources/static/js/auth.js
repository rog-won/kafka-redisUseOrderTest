/**
 * 인증 관련 JavaScript 함수
 */

// 토큰 확인 및 상태 처리
function checkAuthStatus() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const name = localStorage.getItem('name');
    const role = localStorage.getItem('role');
    
    // 로그인 상태에 따라 UI 요소 처리
    if (token && username) {
        return {
            isLoggedIn: true,
            username: username,
            name: name || username,
            role: role || 'ROLE_USER'
        };
    } else {
        return {
            isLoggedIn: false,
            username: null,
            name: null,
            role: null
        };
    }
}

// 토큰 저장
function saveAuthToken(token, username, name, role) {
    localStorage.setItem('token', token);
    localStorage.setItem('username', username);
    localStorage.setItem('name', name || username);
    localStorage.setItem('role', role || 'ROLE_USER');
}

// 토큰 삭제 (로그아웃)
function clearAuthToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('name');
    localStorage.removeItem('role');
}

// 로그인 요청
function loginUser(loginData) {
    return fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
        }
        return response.json();
    })
    .then(data => {
        // JWT 토큰 저장
        saveAuthToken(data.token, data.username, data.name, data.role);
        return data;
    });
}

// API 요청에 인증 토큰 추가
function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('token');
    if (!token) {
        return Promise.reject(new Error('인증 토큰이 없습니다.'));
    }
    
    // 기본 옵션에 헤더 추가
    const defaultOptions = {
        headers: {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        }
    };
    
    // 옵션 병합
    const mergedOptions = {
        ...options,
        ...defaultOptions,
        headers: {
            ...options.headers,
            ...defaultOptions.headers
        }
    };
    
    return fetch(url, mergedOptions);
}

// 로그인 페이지에서 토큰 확인 후 리다이렉션
function redirectIfLoggedIn() {
    const authStatus = checkAuthStatus();
    if (authStatus.isLoggedIn) {
        window.location.href = '/index';
    }
}

// 보호된 페이지에서 토큰 확인 후 리다이렉션
function redirectIfNotLoggedIn() {
    const authStatus = checkAuthStatus();
    if (!authStatus.isLoggedIn) {
        window.location.href = '/view/auth/login';
    }
}

// 사용자 역할 이름을 한글로 변환
function getRoleDisplayName(role) {
    switch(role) {
        case 'ROLE_SUPER_ADMIN':
            return '슈퍼관리자';
        case 'ROLE_ADMIN':
            return '관리자';
        case 'ROLE_USER':
            return '일반사용자';
        default:
            return '사용자';
    }
} 