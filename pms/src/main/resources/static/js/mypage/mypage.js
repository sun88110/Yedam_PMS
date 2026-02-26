document.addEventListener('DOMContentLoaded', function() {
	// 회원정보 수정
	const profileUpdateForm = document.getElementById('profileUpdateForm');
	if (profileUpdateForm) {
		profileUpdateForm.addEventListener('submit', function(e) {
			e.preventDefault();

			alert('회원 정보 수정 로직을 구현하세요.');
		});
	}

	// 비밀번호 재설정 메일 발송
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

			// const token = document.querySelector('meta[name="_csrf"]').content;
			// const header = document.querySelector(
			// 	'meta[name="_csrf_header"]',
			// ).content;

			fetch('/user/pwResetSend', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					// [header]: token,
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
