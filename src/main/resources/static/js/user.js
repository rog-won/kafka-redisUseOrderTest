/**
 * 사용자 관리 JavaScript 기능
 */

document.addEventListener('DOMContentLoaded', function() {
    // 로그인 상태 확인 및 권한 체크
    const authStatus = checkAuthStatus();
    
    // 권한 정보 확인을 위한 디버깅 코드
    console.log('현재 사용자 정보:', authStatus);
    console.log('권한:', authStatus.role);
    console.log('토큰 존재 여부:', !!localStorage.getItem('token'));
    console.log('토큰 값(앞부분):', localStorage.getItem('token')?.substring(0, 20) + '...');
    
    // 슈퍼 어드민이 아니면 메인 페이지로 리디렉션
    if (!authStatus.isLoggedIn || authStatus.role !== 'ROLE_SUPER_ADMIN') {
        console.log('슈퍼 어드민이 아니어서 리다이렉트합니다.');
        window.location.href = '/index';
        return;
    }
    
    // 사용자 목록 로드
    loadUserList();
    
    // 역할 변경 모달 저장 버튼 이벤트
    document.getElementById('saveRoleBtn').addEventListener('click', function() {
        const username = document.getElementById('modalUsername').value;
        const newRole = document.getElementById('roleSelect').value;
        
        updateUserRole(username, newRole);
    });
});

// 사용자 목록 로드
function loadUserList() {
    // 토큰 확인 
    const token = localStorage.getItem('token');
    console.log('사용자 목록 로드 시 토큰 확인:', token ? (token.substring(0, 20) + '...') : '없음');
    
    // 수동으로 API 요청 헤더 구성
    const headers = {
        'Authorization': `Bearer ${token}`
    };
    console.log('수동 요청 헤더:', headers);

    // fetchWithAuth를 사용하여 API 호출
    fetchWithAuth('/users/api/list')
        .then(response => {
            console.log('API 응답 상태:', response.status);
            if (!response.ok) {
                throw new Error('사용자 목록을 불러오는데 실패했습니다. 상태 코드: ' + response.status);
            }
            return response.json();
        })
        .then(users => {
            console.log('사용자 목록 로드 성공:', users.length + '명');
            renderUserList(users);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('사용자 목록을 불러오는데 실패했습니다: ' + error.message);
        });
}

// 사용자 목록 렌더링
function renderUserList(users) {
    const tableBody = document.getElementById('userTableBody');
    tableBody.innerHTML = '';
    
    users.forEach(user => {
        const row = document.createElement('tr');
        
        // 사용자 역할에 따른 배지 스타일 클래스 결정
        let roleClass = '';
        switch(user.role) {
            case 'ROLE_SUPER_ADMIN':
                roleClass = 'role-super-admin';
                break;
            case 'ROLE_ADMIN':
                roleClass = 'role-admin';
                break;
            default:
                roleClass = 'role-user';
        }
        
        // 날짜 형식 변환
        const createdDate = new Date(user.createdAt);
        const formattedDate = createdDate.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td><span class="user-role-badge ${roleClass}">${getRoleDisplayName(user.role)}</span></td>
            <td>${formattedDate}</td>
            <td>
                <button type="button" class="btn btn-sm btn-primary change-role-btn" 
                        data-username="${user.username}" data-role="${user.role}">
                    역할 변경
                </button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // 역할 변경 버튼 이벤트 추가
    document.querySelectorAll('.change-role-btn').forEach(button => {
        button.addEventListener('click', function() {
            const username = this.getAttribute('data-username');
            const currentRole = this.getAttribute('data-role');
            
            // 모달 정보 설정
            document.getElementById('modalUsername').value = username;
            document.getElementById('modalUsernameText').textContent = username;
            document.getElementById('roleSelect').value = currentRole;
            
            // 모달 표시
            const modal = new bootstrap.Modal(document.getElementById('changeRoleModal'));
            modal.show();
        });
    });
}

// 사용자 역할 업데이트
function updateUserRole(username, newRole) {
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('newRole', newRole);
    
    fetchWithAuth('/users/api/update-role', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || '역할 변경에 실패했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        // 모달 닫기
        const modal = bootstrap.Modal.getInstance(document.getElementById('changeRoleModal'));
        modal.hide();
        
        // 성공 메시지 표시
        alert(data.message || '역할이 성공적으로 변경되었습니다.');
        
        // 사용자 목록 새로고침
        loadUserList();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('역할 변경에 실패했습니다: ' + error.message);
    });
} 