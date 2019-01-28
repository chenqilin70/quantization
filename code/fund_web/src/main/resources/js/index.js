/**
 * Created by aierxuan on 2019-01-25.
 */
$(document).ready(function(){
    var $searchInput= $('#searchInput');
    var $searchMenu=$("#searchMenu")
    $searchInput.on('input propertychange', function() {
        var inputParam = $(this).val();
        $.ajax({
            url:$('#contextPath').val()+"/index/searchTips",
            async:true,
            data:{
                "searchWord":inputParam
            },
            success:function(result){
                $searchMenu.html(result)
                if(result.trim().length!=0){
                    $searchMenu.css("display","block");
                }else{
                    $searchMenu.css("display","none");
                }
                registItemClick();

            }
        })
    });
    var registItemClick=function(){
        $(".searchItem").on('mousedown',function(){
            var $fundcode=$(this).attr("fundcode")
            var $jjqc=$(this).attr("jjqc")
            $.ajax({
                url:$('#contextPath').val()+"/index/corr_radar",
                // async:true,
                data:{
                    "fundcode":$fundcode
                },
                dataType:'json',
                contentType: "application/json;charset=utf-8",
                success:function(result){
                    if(result==null || result.length==0){
                        $(".chartDiv").hide()
                        $(".chartNoDataDiv").show()
                    }else{
                        $(".chartDiv").show()
                        $(".chartNoDataDiv").hide()
                        result=$.parseJSON( result );
                        var legend=['成立以来相关性','近1个月相关性','近3个月相关性','近6个月相关性','近1年相关性']
                        var corrdata=[]
                        corrdata[0]={
                            value:result['0'],
                            name:legend[0]
                        }
                        corrdata[1]={
                            value:result['1'],
                            name:legend[1]
                        }
                        corrdata[2]={
                            value:result['3'],
                            name:legend[2]
                        }
                        corrdata[3]={
                            value:result['6'],
                            name:legend[3]
                        }
                        corrdata[4]={
                            value:result['12'],
                            name:legend[4]
                        }
                        myChart.setOption(getOption($jjqc,corrdata,legend));

                    }
                },
                error:function(e){
                    console.log('error:'+e)
                    console.error(e)
                }
            })

        })
    }
    $searchInput.blur(function(){
        $searchMenu.css("display","none");
    })
    $searchInput.focus(function(){
        if($searchMenu.children("li").length!=0){
            $searchMenu.css("display","block")
        }else{
            $searchMenu.css("display","none")
        }
    })



    // 基于准备好的dom，初始化echarts实例
    /*var chartMap={}
    var chartDivs =document.getElementsByClassName('chartDiv')
    for (var i = 0; i < chartDivs.length; i++) {
        chartMap[$(chartDivs[i]).attr('id')]=echarts.init(chartDivs[i]);
    }*/

    var myChart = echarts.init(document.getElementById('chartDiv'));


    var getOption=function(jjqc,value,$legend){
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: jjqc+'与各指数的相关性'
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                x: 'center',
                data:$legend
            },
            radar: [
                {
                    indicator: [
                        {text: '上证指数',  max: 1,min:-1},
                        {text: '深证成指',  max: 1,min:-1},
                        {text: '沪深300',  max: 1,min:-1},
                        {text: '创业板指', max: 1,min:-1},
                        {text: '中证500', max: 1,min:-1},
                        {text: '深证100', max: 1,min:-1},
                        {text: '上证50', max: 1,min:-1},

                    ],
                    center: ['50%','50%'],
                    radius: 80
                }
            ],
            series: [
                {
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data: value
                }
            ]
        };
        return option
    }


    // 使用刚指定的配置项和数据显示图表。
    // myChart.setOption(getOption('sdafasdf',[0.5,0.8 ,0.6,0.4,0.1,0.2, 0.9]));




});