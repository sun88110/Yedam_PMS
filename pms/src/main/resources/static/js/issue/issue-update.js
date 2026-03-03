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
  const commentInput = updateForm.querySelector("input[name='changeComment']");
  const errorMessage = document.getElementById("commentError");
  // Bootstrap 5 모달 인스턴스
  const modalElement = document.getElementById('issueUpdateModal');
  const updateModal = bootstrap.Modal.getOrCreateInstance(modalElement);
  
  // DataTransfer 
  // 파일들을 누적으로 담아둘 객체
  const globalDataTransfer = new DataTransfer();

  // 모달 내 '수정' 확인 버튼 클릭 시
  // 수정사유를 입력하지 않았을 시
  if (confirmUpdateButton) {
      confirmUpdateButton.addEventListener("click", function () {
        const commentValue = commentInput.value.trim();

        //  유효성 검사: 수정 사유가 비어있는지 확인
        if (!commentValue) {
			updateModal.hide();
          // 경고 문구 표시 (d-none 제거)
          if (errorMessage) errorMessage.classList.remove("d-none");
          
          // 입력창에 빨간 테두리 추가 (Bootstrap 클래스)
          commentInput.classList.add("is-invalid");
          
          // 입력창으로 포커스 이동
          commentInput.focus();
          
          // 함수 종료 (submit 방지)
          return;
        }

        // 2. 값이 정상적으로 입력되었다면 경고 제거 후 제출
        if (errorMessage) errorMessage.classList.add("d-none");
        commentInput.classList.remove("is-invalid");
        
        updateForm.submit();
      });
    }
	
	// 1. 새로운 파일 선택 시 (누적 로직 추가) + 기존의 첨부파일 목록 불러오기
	  fileInput.addEventListener("change", function () {
	    const newSelectedFiles = Array.from(fileInput.files);

	    newSelectedFiles.forEach(file => {
	      const fileName = file.name;

	      // 중복 체크: 현재 화면에 표시된 파일 리스트(기존+신규) 중 이름이 같은 게 있는지 확인
	      const isDuplicate = Array.from(fileList.children).some(
	        (li) => li.dataset.name === fileName
	      );

	      if (!isDuplicate) {
	        // 바구니에 추가
	        globalDataTransfer.items.add(file);

	        // UI 생성
	        const li = document.createElement("li");
	        li.className = "list-group-item d-flex shadow-sm justify-content-between align-items-center";
	        li.dataset.name = fileName;
	        li.innerHTML = `
	            <span>${fileName} <span class="btn btn-primary">추가된 파일</span></span>
	            <button type="button" class="btn btn-sm btn-danger new-file-delete">X</button>
	        `;

	        // 신규 파일 삭제 버튼 이벤트
	        li.querySelector(".new-file-delete").addEventListener("click", function () {
	          li.remove();
	          
	          // 바구니 갱신 (선택한 파일 제외하고 다시 담기)
	          const tempTransfer = new DataTransfer();
	          Array.from(globalDataTransfer.files)
	            .filter(f => f.name !== fileName)
	            .forEach(f => tempTransfer.items.add(f));
	          
	          // globalDataTransfer 비우고 다시 채우기
	          while(globalDataTransfer.items.length > 0) globalDataTransfer.items.remove(0);
	          Array.from(tempTransfer.files).forEach(f => globalDataTransfer.items.add(f));

	          // 실제 input 동기화
	          fileInput.files = globalDataTransfer.files;
	        });

	        fileList.appendChild(li);
	      }
	    });

	    // ⭐ 최종적으로 input.files를 누적된 바구니 내용으로 교체
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
