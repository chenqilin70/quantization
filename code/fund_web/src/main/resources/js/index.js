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
                async:true,
                data:{
                    "fundcode":$fundcode
                },
                success:function(result){
                    // var json=JSON.parse(result)
                    if(result==null || result.length==0){
                        console.log("result is 0")
                    }else{
                        myChart.setOption(getOption($jjqc,result));
                    }
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
    var myChart = echarts.init(document.getElementById('chartDiv'));


    var getOption=function(jjqc,value){
        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '相关性'
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                x: 'center',
                data:[jjqc]
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
                    data: [
                        {
                            value: value,
                            name: jjqc
                        }
                    ]
                }
            ]
        };
        return option
    }


    // 使用刚指定的配置项和数据显示图表。
    // myChart.setOption(getOption('sdafasdf',[0.5,0.8 ,0.6,0.4,0.1,0.2, 0.9]));




});