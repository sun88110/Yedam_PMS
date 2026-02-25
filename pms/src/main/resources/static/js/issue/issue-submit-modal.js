/**
 * 모달창에서 실제 id가 confirmSubmit인 확인 버튼의 이벤트를 감지
 * 확인 버튼을 누르면 id가 issueInsert인 태그를 가져와서 submit Method를 실행
 * submit Method가 POST방식임 하지만 issue_insert.html에서 form의 action도 post로 해놓아야 함
 * 일감 등록 모달에서 전송
 */
document.getElementById("confirmSubmit").addEventListener("click", function () {
	console.log("제출 버튼 클릭");
	
  document.getElementById("issueInsert").submit();
  

  
});
