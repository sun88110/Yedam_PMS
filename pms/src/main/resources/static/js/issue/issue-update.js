document.addEventListener("DOMContentLoaded", function () {
  // html의 id 태그를 가져오는 변수
  const projectStart = document.getElementById("projectStartDate").value;
  const projectEnd = document.getElementById("projectEndDate").value;

  // jQuery를 이용한 datepicker 라이브러리 사용
  $(".datepicker").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
    startDate: projectStart,
    endDate: projectEnd,
  });
  // 일감의 시작일을 선택하면 마감일이 자동으로 적용
  $("#startDate").on("changeDate", function (event) {
    $("#endDate").datepicker("setStartDate", event.date);
  });

  $("#endDate").on("changeDate", function (event) {
    $("#startDate").datepicker("setEndDate", event.date);
  });

  const updateForm = document.getElementById("issueUpdate");
  const confirmUpdateButton = document.getElementById("updateSubmit");
  // 첨부파일
  const fileInput = document.getElementById("files");
  const fileList = document.getElementById("fileList");
  // 에러 메세지
  const commentInput = updateForm.querySelector("input[name='changeComment']");
  const errorMessage = document.getElementById("commentError");
  // Bootstrap 5 모달 인스턴스
  const modalElement = document.getElementById("issueUpdateModal");
  const updateModal = bootstrap.Modal.getOrCreateInstance(modalElement);
  // toast 인스턴스
  const toastElement = document.getElementById("fileToast");
  const toastMessage = document.getElementById("toastMessage");
  const fileToast = bootstrap.Toast.getOrCreateInstance(toastElement);

  // DataTransfer
  // 파일들을 누적으로 담아둘 객체
  const globalDataTransfer = new DataTransfer();

  // 모달 내 '수정' 확인 버튼 클릭 시
  if (confirmUpdateButton) {
    confirmUpdateButton.addEventListener("click", function () {
      const commentValue = commentInput.value.trim();

      //  유효성 검사: 수정 사유가 비어있는지 확인
      if (!commentValue) {
        updateModal.hide();
        // 수정사유를 입력하지 않았을 시
        // 경고 문구 표시 (d-none 제거)
        if (errorMessage) errorMessage.classList.remove("d-none");

        // 입력창에 빨간 테두리 추가 (Bootstrap 클래스)
        commentInput.classList.add("is-invalid");
        // 수정사유를 입력하세요 ! 라고 강조
        // 입력창으로 포커스 이동
        commentInput.focus();

        // 함수 종료 (submit 방지)
        return;
      }

      // 2. 값이 정상적으로 입력되었다면 경고 제거 후 제출
      if (errorMessage) errorMessage.classList.add("d-none");
      commentInput.classList.remove("is-invalid");

      // 만약에 globalDataTransfer여기에 저장된 파일이 있으면 그대로 다시 전송
      fileInput.files = globalDataTransfer.files;

      updateForm.submit();
    });
  }

  // 새로운 파일 선택 시 (누적 로직 추가) + 기존의 첨부파일 목록 불러오기
  fileInput.addEventListener("change", function () {
    // 현재 선택된 파일들 Controller에서 쏴주는 파일목록들
    const newSelectedFiles = Array.from(fileInput.files);

    newSelectedFiles.forEach((file) => {
      const fileName = file.name;

      // 중복 체크: 현재 화면에 표시된 파일 리스트(기존+신규) 중 이름이 같은 게 있는지 확인
      // globalDataTransfer 여기서 비교
      const isDuplicate = Array.from(globalDataTransfer.files).some(
        (file) => file.name === fileName,
      );

      // 만약 중복된 파일이 아니라면
      if (!isDuplicate) {
        // 바구니에 추가
        globalDataTransfer.items.add(file);

        // UI 생성
        const li = document.createElement("li");
        li.className =
          "list-group-item d-flex shadow-sm justify-content-between align-items-center";
        li.dataset.name = fileName;
        li.innerHTML = `
	            <span>${fileName} <span class="btn btn-primary">추가된 파일</span></span>
	            <button type="button" class="btn btn-sm btn-danger new-file-delete">X</button>
	        `;

        // 신규 파일 삭제 버튼 이벤트
        li.querySelector(".new-file-delete").addEventListener(
          "click",
          function () {
            // 새로 등록한 파일 삭제 이벤트
            li.remove();

            // 다시 필터링
            const newDataTransfer = new DataTransfer();
            Array.from(globalDataTransfer.files)
              .filter((file) => file.name !== fileName)
              .forEach((file) => newDataTransfer.items.add(file));
          },
        );
        fileList.appendChild(li);
      }
    });
    // 최종적으로 input.files를 누적된 바구니 내용으로 교체
    fileInput.files = globalDataTransfer.files;
  });

  //  기존/신규 파일 삭제 통합 처리 (이벤트 위임)
  fileList.addEventListener("click", function (event) {
    // 기존 서버 파일 삭제 버튼일 때
    // contains(실제 html에서의 클래스명과 일치해야 함)
    if (event.target.classList.contains("file-delete-btn")) {
      const li = event.target.closest("li");
      // html에서 th:data-no를 가져옴
      const fileNo = li.dataset.no;

      /* input type="file"은 MultipartFile 실제 파일 데이터
         input type="hidden"String/Integer 단순이 문자열 혹은 숫자를 보낼 수 있음
         서버에 fileNo 뭐를 지웠다고 알려줌
         html의 filesNo
      */
      const hiddenInput = document.createElement("input");
      hiddenInput.type = "hidden";
      // Controller 매개변수 명
      hiddenInput.name = "deleteFiles";
      // 삭제할 파일 no 무슨 파일의 번호를 삭제할 것인가 알려줘야함
      hiddenInput.value = fileNo;

      document.getElementById("issueUpdate").appendChild(hiddenInput);
      li.remove();
      // toast에 어떤 메세지를 보여줄지
      toastMessage.innerText = "기존 파일이 삭제 목록에 추가되었습니다.";
      // toast 호출
      fileToast.show();
    }
  });
});
