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

  const jobSelect = document.getElementById("jobNo");
  const workersInput = document.getElementById("workers");
  
  // jobNo가 id인 값이 있으면 event 추가
  if (jobSelect) {
	jobSelect.addEventListener('change', function () {
		// 선택된 option 요소를 가져옴
		const selectedOption = this.options[this.selectedIndex];
		// this는 event 발생한 대상 select id ="jobNo" 태그 자체
		// 그 태그 안의 .options 태그를 [] 배열 형태로 
		// this의 몇번째 옵션인지
		const manager = selectedOption.dataset.manager;
		// thymeleaf dataset-manager속성 을 접근할 수 있게 함
		if (manager) {
			workersInput.value = manager;
		} else {
			// 등록된 일감의 담당자가 업을리는 없지만 일단 없다면 빈칸
			workersInput.value = "";
		}
	})
  }
  

  
  document.getElementById("confirmSubmit").addEventListener("click", function () {
    document.getElementById("workInsert").submit();
  });

});
