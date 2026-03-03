//  issue_insert.html의 캘린더, 첨부파일 관련
//  html css를 다 로드한 다음에 script문을 실행!
document.addEventListener("DOMContentLoaded", function () {
  /*	bootstrap-datepicker 플러그인을 적용한다
                  jQuery만 알아듣는다 해서 씀
                  class="datepicker"를 가진 input요소를 찾아서
                  datepicker() method를 실행하겠다
                  한국어 캘린더 사용을 위해 가져옴
               */

  $(".datepicker").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
  });

  /* $  html태그에서 id가 startDate인 태그 찾아서
    on addEventListener 이벤트 추가 changeDate 라는 함수 날짜 선택용
    id 가 endDate인 태그의 datepicker의 setStartDate 가져옴 
  */
  // 시작 일자를 선택하면 마감일자는 시작일자 보다 그 앞을 선택할 수 없다
  $("#startDate").on("changeDate", function (event) {
    // 시작일 선택 -> 마감일의 선택 가능한 날짜 최소화
    // setStartDate : 달력의 시작 지점을 설정 하는 함수
    // event.date startDate의 값
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

  /* 첨부파일 관련 
              변수명 fileInput에 id가 files 가져옴
              변수명 fileList에 id가 fileList 가져옴
              변수 fileInput에 이벤트를 추가 
               change는 사용자가 input의 값을 변경하였을 때 발생하는 이벤트
              */
  const fileInput = document.getElementById("files");
  const fileList = document.getElementById("fileList");
  
  // DataTransfer 
  // 파일들을 누적으로 담아둘 객체
  const globalDataTransfer = new DataTransfer();

  fileInput.addEventListener("change", function () {
      const newFiles = fileInput.files; // 새로 선택된 파일들

      for (let i = 0; i < newFiles.length; i++) {
        const file = newFiles[i];
        const fileName = file.name;

        // 1. 중복 체크
        const exists = Array.from(globalDataTransfer.files).some(
          (f) => f.name === fileName && f.size === file.size
        );

        if (!exists) {
          // 2. DataTransfer 에 파일 추가
          globalDataTransfer.items.add(file);

          // 3.  리스트 생성
          const li = document.createElement("li");
          li.className = "list-group-item shadow-sm d-flex justify-content-between align-items-center";
          li.dataset.name = fileName;
          li.textContent = fileName;

          const xButton = document.createElement("button");
          xButton.type = "button";
          xButton.className = "btn btn-sm btn-danger";
          xButton.textContent = "X";

          // 삭제 버튼 클릭 시 로직
          xButton.addEventListener("click", function () {
            li.remove(); // 화면에서 제거

            // DataTransfer에서도 해당 파일 제거
            const tempTransfer = new DataTransfer();
            Array.from(globalDataTransfer.files)
              .filter((f) => f.name !== fileName)
              .forEach((f) => tempTransfer.items.add(f));
            
            // 전역 DataTransfer 갱신
            // globalDataTransfer는 직접 삭제가 안 되므로 다시 채워넣는 방식 사용
            while(globalDataTransfer.items.length > 0) globalDataTransfer.items.remove(0);
            Array.from(tempTransfer.files).forEach(f => globalDataTransfer.items.add(f));

            // 실제 input 요소 동기화
            fileInput.files = globalDataTransfer.files;
          });

          li.appendChild(xButton);
          fileList.appendChild(li);
        }
      }

      // 5. ⭐ 중요: 새로 선택이 끝난 후, 인풋의 파일을 전체 누적된 파일로 교체합니다.
      fileInput.files = globalDataTransfer.files;
      
    });
});
