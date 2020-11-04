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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

	<script type="text/javascript">

	$(function(){

		//导入时间控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//为按钮添加点击事件
		$("#addBtn").click(function () {

			//读取数据库user对象，将读取的user加到所有者的下拉列表中
			$.ajax({

				url : "workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function(result){

					//result {{用户1}，{用户2}}

					var html = "<option></option>";
					//每一个n就是一个用户对象
					$.each(result,function(i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})

					$("#create-owner").html(html);

					//下拉列表默认选中登录用户
					var id = "${user.id}";
					$("#create-owner").val(id);
				}

			})

			//当下拉列表完成后，打开模态窗口
			$("#createActivityModal").modal("show");

		})

		//为保存按钮添加事件，执行添加操作
		$("#saveBtn").click(function () {
			$.ajax({

				url : "workbench/activity/save.do",
				data : {

					"owner": $.trim($("#create-owner").val()),
					"name": $.trim($("#create-name").val()),
					"startDate": $.trim($("#create-startDate").val()),
					"endDate": $.trim($("#create-endDate").val()),
					"cost": $.trim($("#create-cost").val()),
					"description": $.trim($("#create-description").val())

				},
				type : "post",
				dataType : "json",
				success : function(result){

					//result {"success": true/false}

					//添加成功
					if(result.success){

						//刷新市场活动信息列表
						/*
						$("#activityPage").bs_pagination('getOption', 'currentPage'): 操作后停留在当前页
						$("#activityPage").bs_pagination('getOption', 'rowsPerPage')： 操作后维持已经修改好的每页记录数

						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						*/
						pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


						//清空模态窗口数据
						$("#saveForm")[0].reset();

						//关闭模态窗口
						$("#createActivityModal").modal("hide");

					//添加失败
					}else{
						alert("市场活动添加失败");
					}

				}

			})

		})

		//页面加载完毕，执行方法pageList方法
		pageList(1,2);

		//为查询按钮绑定事件，触发pageList方法
		$("#searchBtn").click(function (){

			//将查询条件赋给隐藏域
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,3);
		})

        //为删除按钮绑定事件，执行删除操作
        $("#deleteBtn").click(function () {

        	if(confirm("确定要删除吗？")){

				var $ids = $("input[name=selectBtn]:checked");
				var param = "";
				for (i=0; i<$ids.length; i++){

					param += "id="+$($ids[i]).val();

					if (i < $ids.length-1){
						param += "&";
					}
				}
				//alert(param);

				$.ajax({

					url:"workbench/activity/delete.do",
					data: param,
					type: "post",
					dataType: "json",
					success: function (result){

						//result {"success" : true/false}
						if (result.success){

							//删除成功，刷新页面
							pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						}else{

							alert("删除市场活动失败")
						}
					}
				})
			}
        })

		//为修改按钮绑定事件，打开修改模态窗口
		$("#editBtn").click(function () {

			var $ids = $("input[name=selectBtn]:checked");
			if ($ids.length == 0){

				alert("请选择需要修改的市场活动");
			}else if ($ids.length > 1){

				alert("只能选择一条市场活动");
			}else {

				var id = $ids.val();
				//alert(id)
				$.ajax({

					url: "workbench/activity/getUserListAndActivity.do",
					data: {"id" : id},
					type: "get",
					dataType: "json",
					success: function (result) {

						/*
						 result
						 	uList:[{user1}, {2}, {3}]
						 	"activity" : activity
						result:{"uList":[{user1}, {2}, {3}], "activity" : activity}
						*/
						var html = "<option></option>";

						//每一个n就是一个User对象
						$.each(result.uList, function (i, n) {

							html += "<option value='"+n.id+"'>"+n.name+"</option>";
						})

						$("#edit-owner").html(html);

						var activity = result.activity;

                        $("#edit-id").val(activity.id);
                        $("#edit-owner").val(activity.owner);
						$("#edit-name").val(activity.name);
                        $("#edit-startDate").val(activity.startDate);
                        $("#edit-endDate").val(activity.endDate);
                        $("#edit-cost").val(activity.cost);
                        $("#edit-description").val(activity.description);

                        //打开模态窗口
                        $("#editActivityModal").modal("show");

					}
				})
			}
		})

		//为更新按钮绑定事件，更新市场活动信息
		$("#updateBtn").click(function () {

			$.ajax({

				url : "workbench/activity/update.do",
				data : {

					"id": $.trim($("#edit-id").val()),
					"owner": $.trim($("#edit-owner").val()),
					"name": $.trim($("#edit-name").val()),
					"startDate": $.trim($("#edit-startDate").val()),
					"endDate": $.trim($("#edit-endDate").val()),
					"cost": $.trim($("#edit-cost").val()),
					"description": $.trim($("#edit-description").val())

				},
				type : "post",
				dataType : "json",
				success : function(result){

					//result {"success": true/false}

					//添加成功
					if(result.success){

						//刷新市场活动信息列表
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage'),$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//清空模态窗口数据
						$("#saveForm")[0].reset();

						//关闭模态窗口
						$("#editActivityModal").modal("hide");

						//添加失败
					}else{
						alert("市场活动修改失败");
					}

				}

			})

		})

    });

	/*
		pageNo:页码
		pageSize:每页展现的记录数

		pageList方法调用情况：
			1、点击市场活动
			2、添加修改删除后
			3、点击查询按钮后
			4、点击分页组件时
	*/

	//刷新市场活动列表方法
	function pageList(pageNo,pageSize){

	    //取消全选复选框的√
        $("#selectAllBtn").prop("checked",false);

		//将隐藏域的值重新赋给查询条件框
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url: "workbench/activity/pageList.do",
			data: {
				 "pageNo": pageNo,
				 "pageSize": pageSize,
				 "name": $.trim($("#search-name").val()),
				 "owner": $.trim($("#search-owner").val()),
				 "startDate": $.trim($("#search-startDate").val()),
				 "endDate": $.trim($("#search-endDate").val())
			},
			type: "get",
			dataType: "json",
			success: function(result){
				//alert("123");
				/*result
					[{市场活动1}， {2}， {3}}]
					查询的总记录数{"total": 100}

				组合成
					{"total": 100, "aList": [{市场活动1}， {2}， {3}}]}
				*/

				var html = "";

				//每一个n就是一个市场活动
				$.each(result.list, function (i,n) {
					//alert(n.name);
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="selectBtn" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';

				})

				$("#activityBody").html(html);

				var totalPages = result.total%pageSize==0 ? result.total/pageSize : parseInt(result.total/pageSize)+1;
				//插入分页插件，进行分页查询
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: result.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});


			}
		})

		//为全选复选框绑定事件，触发全选事件
		$("#selectAllBtn").click(function () {

			$("input[name=selectBtn]").prop("checked", this.checked)

		})

		/*
		错误！！！
		动态生成的元素不能一普通绑定事件的形式来进行操作
		$("input[name=selectBtn]").click(function () {
			alert("123")
		})
		*/

		//动态生成的元素，需以on方法的形式来触发事件
		//语法：$(需要绑定元素的有效外层元素).on(绑定事件的方式，需绑定的元素的jquery对象，回调函数)
		$("#activityBody").on("click", $("input[name=selectBtn]"), function () {

			//alert(123);
			$("#selectAllBtn").prop("checked", $("input[name=selectBtn]").length==$("input[name=selectBtn]:checked").length);

		})
	}
	
</script>
</head>
<body>

	<input type="hidden" id="hidden-name" />
	<input type="hidden" id="hidden-owner" />
	<input type="hidden" id="hidden-startDate" />
	<input type="hidden" id="hidden-endDate" />

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="saveForm" class="form-horizontal" role="form">

                        <input type="hidden" id="edit-id" />

						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" >
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" >
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLa bel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">


								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="selectAllBtn" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">

				</div>
			</div>
			
		</div>
		
	</div>
</body>
</html>