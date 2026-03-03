document.addEventListener('DOMContentLoaded', function() {
	const csrfToken = document.querySelector('meta[name="_csrf"]').content;
	const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

	// 회원정보 수정
	const profileUpdateForm = document.getElementById('profileUpdateForm');

	if (profileUpdateForm) {
		profileUpdateForm.addEventListener('submit', function(e) {
			e.preventDefault();

			const userId = document.getElementById('currentUserId').value;
			const newUsername = document.getElementById('newUsername').value;
			const newEmail = document.getElementById('newEmail').value;

			const params = new URLSearchParams();
			params.append('userId', userId);
			params.append('newUsername', newUsername);
			params.append('newEmail', newEmail);

			const submitBtn = this.querySelector('button[type=\"submit\"]');
			submitBtn.disabled = true;
			submitBtn.innerText = '처리 중...';

			// 메일 발송
			fetch('/user/updateEmailSend', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					[csrfHeader]: csrfToken
				},
				body: params,
			})
				.then((res) => {
					if (res.ok) {
						alert('인증 메일이 발송되었습니다. 확인을 위해 로그아웃됩니다.');
						
						// POST 요청을 보내기 위한 임시 폼
						const form = document.createElement('form');
						form.method = 'POST';
						form.action = '/user/logout';

						const hiddenInput = document.createElement('input');
						hiddenInput.type = 'hidden';
						hiddenInput.name = '_csrf';
						hiddenInput.value = csrfToken;

						form.appendChild(hiddenInput);
						document.body.appendChild(form);
						form.submit();
					} else {
						alert('요청 실패. 다시 시도해주세요.');
					}
				})
				.catch((e) => {
					console.error(e);
					alert('통신 오류가 발생했습니다.');
				})
				.finally(() => {
					submitBtn.disabled = false;
					submitBtn.innerText = '정보 수정';
				});
		});
	}

	// 비밀번호 재설정
	const sendResetEmailBtn = document.getElementById('sendResetEmailBtn');
	if (sendResetEmailBtn) {
		sendResetEmailBtn.addEventListener('click', function() {
			const userIdElement = document.getElementById('currentUserId');
			if (!userIdElement) {
				alert('사용자 정보를 찾을 수 없습니다.');
				return;
			}
			const userId = userIdElement.value;

			const originalText = this.innerText;
			this.disabled = true;
			this.innerText = '발송 중...';

			// URLSearchParams를 사용하여 @RequestParam 데이터 전달
			const params = new URLSearchParams();
			params.append('userId', userId);

			fetch('/user/pwResetSend', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					[csrfHeader]: csrfToken
				},
				body: params,
			})
				.then((res) => {
					if (res.redirected || res.ok) {
						alert('재설정 메일이 발송되었습니다. 유효 시간은 5분입니다.');
					} else {
						alert('메일 발송 실패: 사용자 정보를 확인하세요.');
					}
				})
				.catch((e) => {
					console.error('Error:', e);
					alert('통신 중 오류가 발생했습니다.');
				})
				.finally(() => {
					const modalEl = document.getElementById('confirmPasswordResetModal');
					const modal = coreui.Modal.getInstance(modalEl);
					if (modal) {
						modal.hide();
					}
					this.disabled = false;
					this.innerText = originalText;
				});
		});
	}
});
