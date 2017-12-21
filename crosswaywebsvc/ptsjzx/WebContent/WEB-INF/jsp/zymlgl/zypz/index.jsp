<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<base target="_self">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="/common/meta.jsp"/>
<link href="${ctx}/skins/blue/css/sjsjzx.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx }/tiles/scripts/jquery-1.8.0.min.js"></script> 
<script type="text/javascript" src="${ctx }/tiles/scripts/md5.js"></script>
<!-- 表单验证组件 --> 
 <script type="text/javascript" src="${ctx}/tiles/Validform/js/Validform_v5.3.2.js"></script>
 <link rel="stylesheet" type="text/css" href="${ctx}/tiles/Validform/css/style.css"/>
<title>新增模板</title>
<style type="text/css">
.button{
	position: relative; 
    overflow: visible; 
    display: inline-block; 
    padding: 0.5em 1em; 
    border: 1px solid #6495ED; 
    margin: 0;
    width:120px;
    text-decoration: none; 
    text-shadow: 1px 1px 0 #fff; 
    font-family:"Microsoft YaHei", "微软雅黑", "宋体", Arial, Helvetica, sans-serif;
	font-size: 16px;
    color: #333; 
    font-weight:bold;
    white-space: nowrap; 
    cursor: pointer; 
    outline: none; 
    background-color: #fff;
    -webkit-background-clip: padding;
    -moz-background-clip: padding;
    -o-background-clip: padding-box; 
    /*background-clip: padding-box;*/ /* commented out due to Opera 11.10 bug */
    -webkit-border-radius: 0.2em; 
    -moz-border-radius: 0.2em; 
    border-radius: 0.2em; 
    /* IE hacks */
    zoom: 1; 
    *display: inline; 
}
.dfinput{width:345px; height:25px; line-height:25px; border-top:solid 1px #a7b5bc; border-left:solid 1px #a7b5bc; border-right:solid 1px #ced9df; border-bottom:solid 1px #ced9df; background:url(../images/inputbg.gif) repeat-x; text-indent:10px;}
</style>
</head>
<body>
<p style="text-align:center;font-size:20px;padding-bottom: 15px;padding-top: 10px"><b>资源项配置</b></p>
<form id="mainForm" name="mainForm" action="${ctx }/zypz/savePz" method="post"   enctype="multipart/form-data">
	<input type="hidden" name="resourceid" id="resourceid" value="${obj.re.id }">
	<table width="80%" class="table_multilist" align="center">
		<tr>
			<td width="30%" class="label_1">资源名</td>
			<td width="50%" class="label_2">${obj.re.resourcename }</td>
		</tr>
		<tr>
			<td width="30%" class="label_1">资源ID</td>
			<td width="50%" class="label_2">${obj.re.id }</td>
		</tr>
		<tr>
			<td width="30%" class="label_1">模板文件</td>
			<td width="50%" class="label_2"><input type="file" class="dfinput" style="width:95%" id="summary" name="file" value="" accept=".xls"/></td>
		</tr>
		<tr>
			<td width="30%" class="label_1">所属部门</td>
			<td width="50%" class="label_2"><wd:dicvalue dicId="201701" dicCode="${obj.re.providedept}"/></td>
		</tr>
		<tr>
			<td width="30%" class="label_1">数据项所在行</td>
			<td width="50%" class="label_2">
				<select class="dfinput" name="orderno">
					<option value=0 >第一行</option>
					<option value=1 >第二行</option>
					<option value=2 >第三行</option>
				</select>
			</td>
		</tr>
		<tr>
			<td width="30%" class="label_1">数据项开始列</td>
			<td width="50%" class="label_2">
				<select class="dfinput" name="rowno">
					<option value=0 >第一列</option>
					<option value=1 >第二列</option>
					<option value=2 >第三列</option>
				</select>
			</td>
		</tr>
	</table>
	<div style="text-align: center;margin-top: 20px;padding-bottom: 10px">
		<input type="button" id="button"  class="button" value="返回" onclick="back();" /> 
		&nbsp;<input type="button" id="button"  class="button" value="下一步" onclick="next();" /> 
	</div>
</form>
</body>
<script type="text/javascript">
function next(){
	if($("#summary").val()==null||$("#summary").val()==''){
		alert("请选择文件！");
		return;
	}
	$("#mainForm").submit();

}
function back(){
	window.history.back(-1); 
}
</script>
</html>