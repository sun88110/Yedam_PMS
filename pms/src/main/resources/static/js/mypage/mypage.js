document.addEventListener('DOMContentLoaded', function () {
  // 1. 전역 변수 초기화 (CoreUI 객체)
  const confirmEl = document.getElementById('confirmModal');
  const pmsToastElement = document.getElementById('pmsToast');

  let confirmModal = confirmEl ? new coreui.Modal(confirmEl) : null;
  let pmsToast = pmsToastElement ? new coreui.Toast(pmsToastElement) : null;

  // --- [내부 유틸리티 함수] ---

  // 토스트 표시 함수
  function showToast(message, type = 'success') {
    if (!pmsToastElement) return;
    const header = $('#toastHeader');
    const title = $('#toastTitle');
    const body = $('#toastBody');

    header
      .removeClass('bg-success bg-danger')
      .addClass(type === 'success' ? 'bg-success' : 'bg-danger');
    title.text(type === 'success' ? '성공' : '알림');
    body.text(message);

    if (!pmsToast) pmsToast = new coreui.Toast(pmsToastElement);
    pmsToast.show();
  }

  // 새로고침 후 표시할 토스트 예약
  function reserveToast(message, type = 'success') {
    sessionStorage.setItem('pendingToast', JSON.stringify({ message, type }));
  }

  // CSRF 토큰 가져오기
  const getCsrf = () => {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    return token && header ? { token, header } : null;
  };

  // 📍 페이지 로드 시 예약된 토스트가 있으면 표시
  const pendingToast = sessionStorage.getItem('pendingToast');
  if (pendingToast) {
    const { message, type } = JSON.parse(pendingToast);
    showToast(message, type);
    sessionStorage.removeItem('pendingToast');
  }

  // --- [회원정보 수정 로직] ---
  const profileUpdateForm = document.getElementById('profileUpdateForm');
  if (profileUpdateForm) {
    profileUpdateForm.addEventListener('submit', function (e) {
      e.preventDefault();

      const userId = document.getElementById('currentUserId').value;
      const newUsername = document.getElementById('newUsername').value;
      const newEmail = document.getElementById('newEmail').value;

      const params = new URLSearchParams();
      params.append('userId', userId);
      params.append('newUsername', newUsername);
      params.append('newEmail', newEmail);

      const submitBtn = this.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      const originalText = submitBtn.innerText;
      submitBtn.innerText = '처리 중...';

      const csrf = getCsrf();
      const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
      if (csrf) headers[csrf.header] = csrf.token;

      fetch('/user/updateEmailSend', {
        method: 'POST',
        headers: headers,
        body: params,
      })
        .then((res) => {
          if (res.ok) {
            // ✅ alert 대신 토스트 예약 후 새로고침
            reserveToast('인증 메일이 발송되었습니다. (유효시간 5분)');

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/user/logout';

            const csrf = getCsrf();
            if (csrf) {
              const csrfInput = document.createElement('input');
              csrfInput.type = 'hidden';
              csrfInput.name = '_csrf';
              csrfInput.value = csrf.token;
              form.appendChild(csrfInput);
            }

            document.body.appendChild(form);
            form.submit();
          } else {
            showToast('요청에 실패했습니다. 다시 시도해주세요.', 'error');
          }
        })
        .catch((e) => {
          showToast('통신 오류가 발생했습니다.', 'error');
        })
        .finally(() => {
          submitBtn.disabled = false;
          submitBtn.innerText = originalText;
        });
    });
  }

  // --- [비밀번호 재설정 로직] ---
  const sendResetEmailBtn = document.getElementById('sendResetEmailBtn');
  if (sendResetEmailBtn) {
    sendResetEmailBtn.addEventListener('click', function () {
      const userIdElement = document.getElementById('currentUserId');
      if (!userIdElement) {
        showToast('사용자 정보를 찾을 수 없습니다.', 'error');
        return;
      }

      const originalText = this.innerText;
      this.disabled = true;
      this.innerText = '발송 중...';

      const params = new URLSearchParams();
      params.append('userId', userIdElement.value);

      const csrf = getCsrf();
      const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
      if (csrf) headers[csrf.header] = csrf.token;

      fetch('/user/pwResetSend', {
        method: 'POST',
        headers: headers,
        body: params,
      })
        .then((res) => {
          if (res.ok) {
            // ✅ 비밀번호는 페이지 리로드 없이 바로 토스트 표시
            reserveToast('재설정 메일이 발송되었습니다. (유효시간 5분)');

            const modalEl = document.getElementById(
              'confirmPasswordResetModal',
            );
            const modal = coreui.Modal.getInstance(modalEl);
            if (modal) modal.hide();

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/user/logout';

            const csrf = getCsrf();
            if (csrf) {
              const csrfInput = document.createElement('input');
              csrfInput.type = 'hidden';
              csrfInput.name = '_csrf';
              csrfInput.value = csrf.token;
              form.appendChild(csrfInput);
            }

            document.body.appendChild(form);
            form.submit();
          } else {
            showToast('메일 발송 실패: 사용자 정보를 확인하세요.', 'error');
          }
        })
        .catch((e) => {
          showToast('통신 중 오류가 발생했습니다.', 'error');
        })
        .finally(() => {
          this.disabled = false;
          this.innerText = originalText;
        });
    });
  }
});
