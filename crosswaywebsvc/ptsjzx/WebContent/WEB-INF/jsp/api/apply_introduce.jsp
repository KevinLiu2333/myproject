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
<title>API申请</title>
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
<script type="text/javascript">
function nextStep(){
	$('#MainForm').submit();
}
</script>
</head>
<body>
<form id="MainForm" action="${ctx}/api/toApiItemSelect" method="post">
<p style="text-align:left;font-size:20px;padding-bottom: 15px;padding-top: 10px;margin-left: 60px;"><b>API资源申请</b></p>
	<table width="60%" class="table_multilist" align="center" style="color: navy;">
		<tr>
			<td style="font-size: 15px">&nbsp;&nbsp;资源申请操作说明：</br>
			&nbsp;&nbsp;1.用户根据需求选择需要申请的资源类型。</br>
			&nbsp;&nbsp;2.勾选资源项名称，其中“普遍共享”和“按需共享”的资源项为当前可申请的资源，“不共享”的</br>
			&nbsp;&nbsp;资源项为当前不可申请的资源。</br>
			&nbsp;&nbsp;3.选择好需要的资源类型和资源项后，点击“下一步”。</br>
			&nbsp;&nbsp;4.填写申请信息，以及上传申请的材料（如果需要）；点击“下一步”。</br>
			&nbsp;&nbsp;5.阅读申请资源协议，无异议并勾选复选框，点击“提交审核”。</br>
			</br></br></br></br>
			</td>
		</tr>
	</table>
	<div style="text-align: center;margin-top: 20px;padding-bottom: 10px">
		<input type="button" id="button"  class="button" value="下一步" onclick="nextStep();" /> 
	</div>
</form>
</body>
</html>