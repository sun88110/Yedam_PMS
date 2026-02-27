/**
 * DOMContentLoaded html파일을 다 만들고 나면 JavaScript 코드를 실행한다
 */
document.addEventListener("DOMContentLoaded", function () {
  $(".datepicker").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
  });
  /**
   *  보고서 검색 저장 기능 local storage session
   *  job, project, users, week, month
   *  선택하면 화면에 표시되어야 하는건
   * 검색조건 fragments이다
   * 그걸 화면에 표시
   */
  const setSearchItem = localStorage.getItem("searchForm");
  // 콤마 분리 split("여기 있는 무언가를 기준으로 분리")
  if (setSearchItem) {
    const removeComma = setSearchItem.split(",");

    // 반복문
    for (let i = 0; i < removeComma.length; i++) {
      // d-none 클래스 제거 앞에 field-가 붙음
      let key = removeComma[i];
      // html 요소가 있는지 확인
      let checkElement = document.getElementById(key);

      if (checkElement) {
        checkElement.classList.remove("d-none");
      } else {
        console.warn("이 페이지에 없는 id" + key);
      }
    }
  }

  // 검색조건 session에 저장하는 localStorage 기능
  document.querySelectorAll(".add-condition").forEach((menuItem) => {
    menuItem.addEventListener("click", function (event) {
      event.preventDefault();
      // this. 현재 event가 발생한 요소
      const targetId = this.getAttribute("data-target");
      const targetDiv = document.getElementById(targetId);

      if (targetDiv) {
        targetDiv.classList.remove("d-none"); // 숨김해제
        targetDiv.querySelector("input, select").focus(); // 나타나면 바로 입력할 수 있도록

        // 검색조건 추가시 localStorage에 저장 검색조건은 변경되니 let으로
        let currentData = localStorage.getItem("searchForm");
        if (currentData) {
          let arr = currentData.split(",");
          // 배열에 없으면 추가push  contains 함수는 java꺼였음 includes로
          if (!arr.includes(targetId)) {
            arr.push(targetId);
            localStorage.setItem("searchForm", arr.join(","));
          }
        } else {
          localStorage.setItem("searchForm", targetId);
        }
      }
    });
  });
  /* 닫기 버튼 클릭시 다시 숨긴다 d-none 활성화 
   btn-remove 클래스를 다 가져와서 각각에
   closeButton이라는 이름의 클릭 이벤트를 부여한다
   targetId는 data-target아이디이고
   targetDiv는 그 targetId의 아이디를 가져온다
   d-none이라는 class를 추가한다
   그리고 그 값도 초기화 한다
  */
  document.querySelectorAll(".btn-remove").forEach((closeButton) => {
    closeButton.addEventListener("click", function () {
      const targetId = this.getAttribute("data-target");
      const targetDiv = document.getElementById(targetId);

      if (targetDiv) {
        // 해당 id가 있으면
        targetDiv.classList.add("d-none");
        // 값 초기화 (querySelectorAll로 모두 가져온 뒤 for문 실행)
        const inputFields = targetDiv.querySelectorAll("input, select");
        // select 필드 초기화
        for (let j = 0; j < inputFields.length; j++) {
          let findSelect = inputFields[j];
          if (findSelect.tagName === "SELECT") {
            // html의 select 태그가 있다면
            findSelect.value = "0";
          } else {
            findSelect.value = ""; // 일반 input 초기화
          }
        }
        // targetId가 d-none이 추가되었다 event발생

        // remove 닫기 버튼 눌렀을 때 localStorage에서도 삭제
        let currentData = localStorage.getItem("searchForm");
        // 현재 currentDat를 가져온다 그리고 삭제
        if (currentData) {
          let arr = currentData.split(",");
          // filter 써서 x 버튼을 안 누른 Id만 가져옴 event가 발생하지 않은 부분만
          let filtered = arr.filter((id) => id !== targetId);
          localStorage.setItem("searchForm", filtered.join(","));
        }
      }
    });
  });

  // id가 reportSearchForm인 태그 가져옴
  const currentForm =
    document.getElementById("reportSearchForm") ||
    document.getElementById("searchForm");
  if (currentForm) {
    currentForm.addEventListener("submit", function () {
      // 선택이 안된 다른 input 태그의 값은 전송하지 않는다
      document.querySelectorAll(".condition-field.d-none").forEach((field) => {
        field.querySelectorAll("input, select").forEach((el) => {
          el.disabled = true;
        });
      });
    });
  }
});

function toggleMyWork(checkbox) {
  // 현재 URL 경로 가져옴
  const url = window.location.pathname;
  // 체크가 되어잇으면 ? showOnlyMe=Y 붙이고 아니면 기본 url로 이동
  if (checkbox.checked) {
    location.href = url + "?showOnlyMe=Y";
  } else {
    location.href = url;
  }
}
