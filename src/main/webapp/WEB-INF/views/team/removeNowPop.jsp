<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta charset="UTF-8">
<title>🏀 당근농장</title>
<script src="https://code.jquery.com/jquery-3.6.3.min.js"></script>
<style>
	h4{
		float: left;		
		margin: 0 10 0 0;
	}
	input[type="button"]{
		position: relative;
        top: 10px;
        left: 200;
	}
</style>
</head>
<body>

		<input type="text" value="${userId}" name="userId" hidden/>
		<input type="text" value="${teamIdx}" name="teamIdx" hidden/>
		
		<h2>즉시강퇴</h2>
		<hr/>
		<p>즉시강퇴가 가능한 사유는 해킹 또는 광고성 계정이거나, 더 이상 팀 활동이 어렵다고 사려되는 경우입니다.</p>
		<p>즉시강퇴 사유가 발생할 경우 경고횟수와 상관없이 즉시강퇴 됩니다.</p>
		<p>강퇴된 회원은 재가입이 불가하니 신중하게 선택해주세요.</p>
		<br/>
		
		<h4>즉시강퇴 대상자</h4>
		<span>${userId}</span>
		<br/>
		<label>강퇴 사유 선택 </label>
			<select name="remove" id="remove" style=" width:300px; margin: 10px;">
				<option value="none">강퇴 사유 </option>
				<option value="해킹 계정">해킹 계정</option>
				<option value="광고성 계정">광고성 계정</option>
				<option value="기타">기타</option>
			</select>
		<br/>
		<input type="text" name="content" id="content" style="width: 450px;" placeholder="강퇴사유를 입력해 주세요." hidden="true"/>
		<br/>
		<input type="button" value="강퇴" onclick="subChk()" style="margin: 10px;" />	
	
</body>
<script>	
	
	var teamIdx = "${teamIdx}";
	var userId = "${userId}";
	
	$('#remove').change(function(){
		console.log($(this).val());
		if($('#remove').val() == '기타'){		
		    $('#content').attr('hidden',false);
		  } else {
		    $('#content').attr('hidden',true);
		  }
	});
	
	

	function subChk(){
		
		var remove = $('#remove').val();
		console.log(remove);
		var content = $('#content').val();
		console.log(content);
		
		if(remove == 'none'){
			alert('강퇴 사유를 선택해 주세요.');
			return false;
		}		
		
		if(remove == '기타'){
			
			if(content == ''){
				alert('강퇴 사유를 입력해 주세요.');
				return false;
			}			
		}	

		    $.ajax({
		        url: 'removeNow.ajax',
		        type: 'POST',
		        data: {
		            'teamIdx': teamIdx,
		            'userId':userId,
		            'remove':remove,
		            'content':content
		        },
				dataType:'json',
				success: function(data) {
					console.log("성공");
					if(data.success == 1){
						if (window.opener && !window.opener.closed) {
							  window.opener.location.reload(); // 부모창 새로고침
							  window.alert("강퇴되었습니다.");
							  window.close(); // 창 닫기
							}
					}					
		        },
				error:function(e){
					console.log(e);
				}
		    });
		}

</script>
</html>