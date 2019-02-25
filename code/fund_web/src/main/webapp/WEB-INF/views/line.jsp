<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/2/25 0025
  Time: 09:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.css" >
    <script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/js/echarts.min.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/js/dataTool.js"  type="text/javascript"  ></script>
    <script  type="text/javascript"  >
        $(document).ready(function(){
            var myChart = echarts.init(document.getElementById('chartDiv'));
            var redis=function(key){
                var redisdata=""
                $.ajax({
                    url:$('#contextPath').val()+"/index/redis",
                    async:false,
                    data:{
                        'key':key
                    },
                    dataType:'json',
                    contentType: "application/json;charset=utf-8",
                    success:function(result){
                        console.log(result)
                        redisdata=result

                    },
                    error:function(e){
                        console.error(e)
                    }
                })
                return redisdata
            }


            var getOption=function(){

                var option =  {
                    xAxis: {
                        type: 'value'
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: eval(redis('lineSeries'))
                };
                return option
            }

            myChart.setOption(getOption());
        })

    </script>

    <style>
        .chartDiv{
            height: 1000px;
        }

    </style>
</head>
<body>
<div id="contextPath"><%=request.getContextPath()%></div>
<div class="row">
    <div id="chartDiv" class="chartDiv col-lg-offset-1 col-lg-10 col-md-offset-1  col-md-10  col-sm-offset-1 col-sm-10 col-xs-offset-1  col-xs-10">

    </div>
</div>
</body>
</html>
