<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>法人数据库</title>
<link rel="stylesheet" type="text/css" href="${ctx}/skins/jk/css/default.css" />
<script type="text/javascript" src="${ctx}/skins/jk/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctx}/skins/jk/js/nav.js"></script>
<script src="${ctx}/tiles/echarts/echarts.js" ></script>
</head>

<body>
 <div class="main" style="margin-left: 5px;">
 	<div class="all_squares">
 		<div class="square1">
 			<span id="jrbmnum">接入节点：3个</span>
 		</div>
 		<div class="square2">
 			<ul>
 				<li class="border_bottom green_font" id="sumrcvyear">总接收量：53767</li>
 				<li class="blue_font" id="sumsndyear">总发送量：53767</li>
 			</ul>
 		</div>
 		<div class="square3">
 			<ul>
 				 <li class="border_bottom green_font" id="sumrcvday">总接收量：0</li>
 				 <li class="blue_font" id="sumsndday">总发送量：0</li>
 			</ul>
 		</div>
 	</div>
 	<div class="reportAll">
 		<div class="subTitle titleIcon4">法人数据统计<span>更多>></span></div>
 		<div id="people" style="height: 300px"></div>
 	</div>
 	
 </div>
</body>
<script type="text/javascript">
require.config({
	paths: {
		echarts: '${ctx}/tiles/echarts'
	}
});
require([
			'echarts',
			'echarts/chart/bar'
		],function (ec){
	drawpeople(ec);
});
function drawpeople(ec){
	var myChart = ec.init(document.getElementById("people"));
	var option={
			tooltip : {
		        trigger: 'axis'
		    },xAxis:[{type : 'category',
		    	data:['质监局',"编办","工商局"]
		    }],yAxis : [
		                {
		                    type : 'value'
		                }
		            ],
		            series:[{
		            	type:'bar',
		            	barWidth:40,
		            	itemStyle:{
		            		 normal: {
		            			 color: function(params) {
		            				 var colorList=[ '#C1232B','#B5C334','#FCCE10'];
		            				 return colorList[params.dataIndex];
		            			 }
		            		 }
		            	},
		            	data:[36564,13545,7656]}
		            ]
	};
	myChart.setOption(option);
}
</script>
</html>