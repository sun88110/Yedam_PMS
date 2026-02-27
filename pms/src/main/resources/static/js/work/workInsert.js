document.addEventListener("DOMContentLoaded", function () {
  // 달력 jQuery
  $(".datepicker")
    .datepicker({
      format: "yyyy-mm-dd",
      language: "ko",
      autoclose: true,
    })
    .on("changeDate", function () {
      document.getElementById("error-workDate").textContent = ""; // 날짜 선택 시 에러 삭제
    });

  // 소요시간 등록 폼
  const workForm = document.getElementById("workInsert");
  // 등록 버튼
  const preSubmitBtn = document.getElementById("preSubmitBtn");
  // 모달 안의 확인 버튼
  const confirmSubmit = document.getElementById("confirmSubmit");
  // 부트스트랩 모달 객체 생성
  const submitModal = new bootstrap.Modal(
    document.getElementById("workSubmitModal"),
  );

  //  일감 선택 시 담당자 자동 입력이 될 수 있도록
  const jobSelect = document.getElementById("jobNo");
  const workersInput = document.getElementById("workers");

  if (jobSelect) {
    jobSelect.addEventListener("change", function () {
      const selectedOption = this.options[this.selectedIndex];
      const manager = selectedOption.dataset.manager; // th:data-manager 값 가져오기

      if (manager) {
        workersInput.value = manager;
        document.getElementById("error-jobNo").textContent = ""; // 선택 시 에er 삭제
      } else {
        workersInput.value = "";
      }
    });
  }

  // 2. 유효성 검사 함수
  function validate() {
    let isValid = true;
    // 모든 에러 메시지 초기화
    document
      .querySelectorAll(".error-msg")
      .forEach((el) => (el.textContent = ""));

    // input의 값 id가져온다
    const jobNo = document.getElementById("jobNo").value;
    const workDate = document.getElementById("workDate").value;
    const workTime = document.getElementById("workTime").value;
    const workDetailsNo = document.querySelector(
      "select[name='workDetailsNo']",
    ).value;
    const workContent = document.getElementById("workContent").value.trim();

    // if 문
    if (!jobNo) {
      document.getElementById("error-jobNo").textContent =
        "일감을 선택해주세요!";
      isValid = false;
    }
    if (!workDate) {
      document.getElementById("error-workDate").textContent =
        "작업일자를 입력해주세요!";
      isValid = false;
    }
    if (!workTime || workTime <= 0) {
      document.getElementById("error-workTime").textContent =
        "근무 시간을 정확히 입력해주세요!";
      isValid = false;
    }
    if (!workDetailsNo) {
      document.getElementById("error-workDetailsNo").textContent =
        "작업분류를 선택해주세요!";
      isValid = false;
    }
    if (!workContent) {
      document.getElementById("error-workContent").textContent =
        "상세 설명을 입력해주세요!";
      isValid = false;
    }

    return isValid;
  }

  // [등록] 버튼 클릭 시 유효성 검사 후 모달 띄우기
  preSubmitBtn.addEventListener("click", function () {
    if (validate()) {
      submitModal.show(); // 검사 통과 시에만 모달 표시
    }
  });

  // 모달 안의 [확인] 버튼 클릭 시 실제 Submit 실행
  confirmSubmit.addEventListener("click", function () {
    workForm.submit();
  });
});
