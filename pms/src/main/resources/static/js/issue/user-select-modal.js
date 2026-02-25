/*
        모달창에서 DOMContentLoaded 사용하면 읽지 못한다
        document.addEventListener("DOMContentLoaded", function () {});
        
		userModal 변수에 userSelectModal의 id를 가진 태그를 가져온다
		managerInput 변수에 managerId이라는 id를 가진 태그를 가져온다

		modalElement변수에 .user-item클래스 태그 전부 가져온다 
		그리고 forEach 함수로 가져온 리스트에 클릭 event를 다 준다
		그 클릭 event는 managerInput의 값에 빈 공간을 없애고 가져온다
		this는 클릭 event가 발생한 자기자신
		*/
// 일감 담당자 선택 모달
const userModal = document.getElementById("userSelectModal");
const managerInput = document.getElementById("managerId");
const managerName = document.getElementById("managerName");

userModal.querySelectorAll(".user-item").forEach((item) => {
  item.addEventListener("click", function () {
    managerName.value = this.textContent.trim();
    managerInput.value = this.dataset.id;
    // 이미 열려있는 modal 인스턴스를 가져와서 닫는
    // Bootstrap 공식 방식
    const modal = bootstrap.Modal.getInstance(userModal);
    modal.hide();
  });
});
