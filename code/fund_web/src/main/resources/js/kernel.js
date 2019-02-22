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

        var option = option = {
            xAxis: [{
                type: 'value',
                axisLabel:{
                    // rotate:40,
                    formatter:function(val,index){
                        return val+"左右"
                    },
                    interval:0
                }
            },
                {
                    type: 'value',
                    position:'bottom',
                    offset:30
                }],
            yAxis: [
                {
                    type: 'value',
                    position:'left',
                    axisLabel: {
                        formatter: '{value} 个'
                    }
                },
                {
                    type: 'value',
                    position:'right'
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
                    xAxisIndex:1,
                    symbol: "none"
                }],
            grid:{//直角坐标系内绘图网格
                show:true,//是否显示直角坐标系网格。[ default: false ]
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

    myChart.setOption(getOption());


    var getBoxOption=function(){

        var data = echarts.dataTool.prepareBoxplotData(eval(redis("boxData")));

        var option = option = {
            xAxis: {
                type: 'category',
                data: data.axisData,
                boundaryGap: true,
                nameGap: 30,
                splitArea: {
                    show: false
                },
                axisLabel: {
                    formatter: 'expr {value}'
                },
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                splitArea: {
                    show: true
                }
            },
            series: [
                {
                    name: 'boxplot',
                    type: 'boxplot',
                    data: data.boxData,
                    tooltip: {
                        formatter: function (param) {
                            return [
                                'Experiment ' + param.name + ': ',
                                'upper: ' + param.data[5],
                                'Q3: ' + param.data[4],
                                'median: ' + param.data[3],
                                'Q1: ' + param.data[2],
                                'lower: ' + param.data[1]
                            ].join('<br/>');
                        }
                    }
                },
                {
                    name: 'outlier',
                    type: 'scatter',
                    data: data.outliers
                }
            ],
            grid:{//直角坐标系内绘图网格
                show:true,//是否显示直角坐标系网格。[ default: false ]
                borderColor:"#c45455",//网格的边框颜色
                bottom:"20%" //
            },
            tooltip: {
                trigger: 'item',
                axisPointer: {
                    type: 'shadow'
                }
            },
        };
        return option
    }

    var myBoxChart = echarts.init(document.getElementById('boxChartDiv'));
    myBoxChart.setOption(getBoxOption());

})