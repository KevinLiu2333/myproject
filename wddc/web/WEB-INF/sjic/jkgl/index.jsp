<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ include file="/cj/title_setting.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${sys_title}</title>
<%@ include file="/cj/meta.jsp"%>
<!-- Loading Bootstrap -->
<link href="${ctx}/wddc/tiles/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">
<!--self-->
<link rel="stylesheet" type="text/css"
	href="${ctx}/wddc/skins/css/wddc.css" />
<!--font-awesome-->
<link rel="stylesheet" type="text/css"
	href="${ctx}/wddc/tiles/awesome/css/font-awesome.min.css" />
<!-- Loading jquery -->
<script type="text/javascript"
	src="${ctx}/wddc/tiles/js/jquery-2.2.3.min.js"></script>
<link href="${ctx}/wddc/tiles/data-tables/css/demo_page.css"
	rel="stylesheet" />
<link href="${ctx}/wddc/tiles/data-tables/css/demo_table.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="${ctx}/wddc/tiles/data-tables/DT_bootstrap.css" />
</head>
<body>
	<input type="hidden" id="js_ctx" value="${ctx }">
	<jsp:include page="/cj/header.jsp" />
	<br />
	<div class="container">
		<div class="col-md-12">
			<h2 class="page-header" style="margin-top: 5px">
				接口管理 <span style="float: right; padding-right: 10px">
					<button type="button" class="btn btn-warning" style="width: 100px"
						onclick="add()">新增&nbsp;</button> &nbsp;
				</span>
			</h2>
			<div class="content">
				<div class="panel-body">
					<div class="adv-table">
						<table class="display table table-bordered table-striped"
							id="dynamic-table">
							<thead>
								<tr>
									<th>序号</th>
									<th>接口名称</th>
									<th>系统id</th>
									<th>接口类型</th>
									<th>总调用次数</th>
									<th>调用成功</th>
									<th>调用失败</th>
									<th>更新时间</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${obj.list}" var="bo" varStatus="bos">
									<tr>
										<td>${bo.info.id}</td>
										<td>${bo.info.name}</td>
										<td>${bo.info.joinId}</td>
										<td>${bo.info.type == 1 ? 'WSDL':'REST'}</td>
										<td>${bo.detail.transferSum}</td>
										<td>${bo.detail.transferSuccess}</td>
										<td>${bo.detail.transferFailed}</td>
										<td><fmt:formatDate value="${bo.info.updateTime}"
												pattern="yyyy-MM-dd HH:mm:ss" /></td>
										<td><a href="#" onclick="checkUrl('${bo.info.id }')">测试</a>
											<a href="#" onclick="edit('${bo.info.id }')">编辑</a> <a
											href="#" onclick="deleteinfo('${bo.info.id }')">失效</a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="/cj/foot.jsp" />
</body>
<!-- Loading Bootstrap js -->
<script type="text/javascript"
	src="${ctx}/wddc/tiles/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript"
	src="${ctx}/wddc/tiles/data-tables/jquery.dataTables.js"></script>
<script type="text/javascript"
	src="${ctx}/wddc/tiles/data-tables/DT_bootstrap.js"></script>
<!--dynamic table initialization -->
<script src="${ctx}/wddc/tiles/data-tables/dynamic_table_init.js"></script>
<script src="${ctx}/wddc/tiles/js/dic.js"></script>
<script type="text/javascript">
	function add() {
		window.location.href = "${ctx}/sjic/jkgl/edit";
	}

	function edit(id) {
		window.location.href = "${ctx}/sjic/jkgl/edit?id=" + id;
	}

	function deleteinfo(id) {
		if (confirm("确定失效吗？")) {
			$.post("${ctx}/sjic/jkgl/deleteInfo", {
				id : id
			}, function(data) {
				location.reload();
			});
		}
	}

	function view(id) {
		window.open("${ctx}/suite/data/df/viewInfo?id=" + id);
		location.reload();
	}

	function exportdata(id) {
		$('#myModal').modal({
			backdrop : 'static',
			keyboard : false
		});
		$.post("${ctx}/suite/data/df/cacheData", {
			id : id
		}, function(data) {
			if (data) {
				alert("缓存成功！");
				window.location.reload();
			} else {
				alert("缓存失败！");
				window.location.reload();
			}
		});
	}

	function checkUrl(id) {
		var params = prompt("请输入json类型参数,若无参数默认做连通测试,例:{id=123,name='abc'}注意string型参数加上单引号", "");
		if (params || params === "") {
			$.post("${ctx}/sjic/jkgl/checkUrl", {
				id : id,
				params : params
			}, function(data) {
				alert(data.detailmsg);
			});
		}

	}
</script>
</html>