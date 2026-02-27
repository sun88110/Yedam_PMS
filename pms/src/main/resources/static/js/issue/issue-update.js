document.addEventListener("DOMContentLoaded", function () {
  // jQuery 달력
  $(".datepicker").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
  });
  // 시작 일자를 선택하면 마감일자는 시작일자 보다 그 앞을 선택할 수 없다
  $("#startDate").on("changeDate", function (event) {
    // 시작일 선택 -> 마감일의 선택 가능한 날짜 최소화
    $("#endDate").datepicker("setStartDate", event.date);
    // 마감일을 먼저 선택 -> 시작일보다 빠르다면 마감일과 시작일이 동일하게 변경
    const endDateValue = $("#endDate").datepicker("getDate");
    if (endDateValue && endDateValue < event.date) {
      $("#endDate").datepicker("update", event.date);
    }
  });
  // 마감일이 변경될 때 시작일 범위 제한
  $("#endDate").on("changeDate", function (event) {
    // 마감일 먼저 선택 그러면 시작 날짜는 마감일 뒤로 못 간다
    $("#startDate").datepicker("setEndDate", event.date);
  });

  const updateForm = document.getElementById("issueUpdate");
  const confirmUpdateButton = document.getElementById("updateSubmit");
  const fileInput = document.getElementById("files");
  const fileList = document.getElementById("fileList");

  // 모달 내 '수정' 확인 버튼 클릭 시
  // 수정사유를 입력하지 않았을 시
  if (confirmUpdateButton) {
    confirmUpdateButton.addEventListener("click", function () {
      const comment = updateForm.querySelector(
        "input[name='changeComment']",
      ).value;
      if (!comment || comment.trim() === "") {
        alert("수정 사유를 입력해주세요.");
        return;
      }
      updateForm.submit();
    });
  }

  // 새로운 파일 첨부 시 리스트 추가
  fileInput.addEventListener("change", function () {
    for (let i = 0; i < fileInput.files.length; i++) {
      const fileName = fileInput.files[i].name;
      /* html에서 id가 files인 속성을 가져온 fileInput인
      html에서 input type="file" 이라고 하면 
      FileList 객체를 가져옴 읽기 전용 (웹이 사용자의 컴퓨터 파일 마음대로 변환 불가)
      그렇기에 통째로 교체를 하는거임
        */
      // 중복 체크 (data-name 속성 기준)
      const exists = Array.from(fileList.children).some(
        (li) => li.dataset.name === fileName,
      );

      // 중복 체크용
      if (!exists) {
        const li = document.createElement("li");
        li.className =
          "list-group-item d-flex justify-content-between align-items-center";
        li.dataset.name = fileName;
        li.innerHTML = `
                    <span>${fileName}</span>
                    <button type="button" class="btn btn-sm btn-danger new-file-delete">X</button>
                `;

        /* 신규 파일 삭제 (DataTransfer 객체 생성 중요)
        브라우저에서 input type="file"는 보안상 사용자가 직접 수정 불가
        DataTransfer이라는 임시 데이터 보관소     
        */
        li.querySelector(".new-file-delete").addEventListener(
          "click",
          function () {
            // X button 누르면 화면에서만 일단 사라짐
            li.remove();
            // DataTransfer 생성
            /* fileInput의 파일명에서 다시 배열 생성 
            filter함 파일이름이 없으면 
            items를 추가함 */
            const dataTransfer = new DataTransfer();
            Array.from(fileInput.files)
              .filter((file) => file.name !== fileName)
              .forEach((file) => dataTransfer.items.add(file));
            // DataTransfer에서 files 속성은 파일 목록
            fileInput.files = dataTransfer.files;
          },
        );

        fileList.appendChild(li);
      }
    }
  });

  //  기존/신규 파일 삭제 통합 처리 (이벤트 위임)
  fileList.addEventListener("click", function (event) {
    // 기존 서버 파일 삭제 버튼일 때
    // contains(실제 html에서의 클래스명과 일치해야 함)
    if (event.target.classList.contains("file-delete-btn")) {
      const li = event.target.closest("li");
      // html에서 th:data-no를 가져옴
      const fileNo = li.dataset.no;

      if (confirm("기존 파일을 삭제하시겠습니까? (수정 버튼을 누르면 적용)")) {
        const hiddenInput = document.createElement("input");
        /* input type="file"은 MultipartFile 실제 파일 데이터
           input type="hidden"String/Integer 단순이 문자열 혹은 숫자를 보낼 수 있음
           서버에 fileNo 뭐를 지웠다고 알려줌
        */
        hiddenInput.type = "hidden";
        // Controller 매개변수 명
        hiddenInput.name = "deleteFileNo";
        // 삭제할 파일 no 무슨 파일의 번호를 삭제할 것인가 알려줘야함
        hiddenInput.value = fileNo;

        document.getElementById("issueUpdate").appendChild(hiddenInput);
        li.remove();
      }
    }
  });
});
