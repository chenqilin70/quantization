<%--
  Created by IntelliJ IDEA.
  User: aierxuan
  Date: 2019-01-30
  Time: 16:41
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
    <script>
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



            var getOption=function(index1,index2,data){
                // 指定图表的配置项和数据
                data = data.map(function (item) {
                    return [item[1], item[0], item[2] || '-'];
                });

                var option = option = {
                    xAxis: [{
                        type: 'category',
                        data: eval(redis('rectangleLabelStr')),
                        axisLabel:{
                            rotate:40
                        }
                    },
                    {
                        type: 'category',
                        data: eval(redis('kernalLebelStr'))
                    },
                    {
                        type: 'value',
                        position:'bottom',
                        offset:100
                    }],
                    yAxis: [
                        {
                            type: 'value',
                            name: '个数',
                            axisLabel: {
                                formatter: '{value} 个'
                            }
                        },
                        {
                            type: 'value',
                            name: '估计',
                            axisLabel: {
                                formatter: '{value} '
                            }
                        }
                    ],
                    series: [{
                            data: eval(redis('rectangleDataStr')),
                            type: 'bar',
                            yAxisIndex: 0,
                            xAxisIndex:0
                        },
                        {
                            data: eval(redis('densitiesStr')),
                            type: 'line',
                            yAxisIndex: 1,
                            xAxisIndex:1,
                            symbol: "none"
                        },
                        {
                            data: eval(redis('cdfDataStr')),
                            type: 'line',
                            yAxisIndex: 1,
                            xAxisIndex:2,
                            symbol: "none"
                        }],
                    grid:{//直角坐标系内绘图网格
                        show:true,//是否显示直角坐标系网格。[ default: false ]
                        left:"20%",//grid 组件离容器左侧的距离。
                        right:"30px",
                        borderColor:"#c45455",//网格的边框颜色
                        bottom:"20%" //
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        }
                    }
                };
                return option
            }

            $.ajax({
                url:$('#contextPath').val()+"/index/corr_index_data",
                async:true,
                dataType:'json',
                contentType: "application/json;charset=utf-8",
                success:function(result){
//                    result=$.parseJSON( result );
                    console.log(result)
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(getOption(result['index1'],result['index2'],result['data']));
                },
                error:function(e){
                    console.error(e)
                }
            })

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
