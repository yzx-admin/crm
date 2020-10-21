<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">
	<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script>

		$(function(){

			if(window.top!=window){
				window.top.location = window.location;
			}

			//清除用户名文本框内容
			$("#loginAct").val("");

			//用户名文本框自动获取焦点
			$("#loginAct").focus();

			//给提交按钮绑定事件
			$("#submitBtn").click(function () {

				login();

			})

			//给当前登入窗口绑定敲键盘事件
			$(window).keydown(function(event){
				//alert(event.keyCode);

				//回车键的编码是13
				if(event.keyCode==13){

					login();

				}

			})

		})

		function login(){
			//alert("123");

			//验证用户名是否为空，若为空显示错误信息
			//去除用户名与密码的空格
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());

			if(loginAct=="" || loginPwd=="null"){

				//输出账户密码不能为空
				$("#msg").html("账户密码不能为空");

				//程序到此，无需往下验证,终止方法
				return false;

			}

			//程序执行到此，验证其他信息
			$.ajax({
				url : "settings/user/login.do",
				data : {
					"loginAct" : loginAct,
					"loginPwd" : loginPwd
				},
				type : "post",
				dataType : "json",
				success : function (result) {

					//result { "success" : true/false , "msg" : "哪错了"}

					if(result.success){

						//登入成功，跳转到工作台首页
						window.location.href = "workbench/index.jsp";

					}else{

						//alert("123");

						//登入失败，输出错误信息
						$("#msg").html(result.msg);
					}

				}
			})
		}

	</script>

</head>
<body>
<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
	<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
</div>
<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
	<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
</div>

<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
	<div style="position: absolute; top: 0px; right: 60px;">
		<div class="page-header">
			<h1>登录</h1>
		</div>
		<form action="workbench/index.jsp" class="form-horizontal" role="form">
			<div class="form-group form-group-lg">
				<div style="width: 350px;">
					<input class="form-control" type="text" placeholder="用户名" id="loginAct">
				</div>
				<div style="width: 350px; position: relative;top: 20px;">
					<input class="form-control" type="password" placeholder="密码" id="loginPwd">
				</div>
				<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">

					<span id="msg" style="color: #ff0000"></span>

				</div>

				<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
			</div>
		</form>
	</div>
</div>
</body>
</html>