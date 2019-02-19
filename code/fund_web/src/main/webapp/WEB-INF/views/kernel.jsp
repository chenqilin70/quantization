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
                        redisdata=result['data']

                    },
                    error:function(e){
                        console.error(e)
                    }
                })
                alert("OK"+redisdata)
                return redisdata
            }
            alert(redis('k1'))

            var getOption=function(index1,index2,data){
                // 指定图表的配置项和数据
                data = data.map(function (item) {
                    return [item[1], item[0], item[2] || '-'];
                });

                var option = option = {
                    xAxis: [{
                        type: 'category',
                        data: ['0.0000-1.7089',' 1.7089-3.4178',' 3.4178-5.1266',' 5.1266-6.8355',' 6.8355-8.5444',' 8.5444-10.2533',' 10.2533-11.9621',' 11.9621-13.6710',' 13.6710-15.3799',' 15.3799-17.0888',' 17.0888-18.7976',' 18.7976-20.5065',' 20.5065-22.2154',' 22.2154-23.9243',' 23.9243-25.6331',' 25.6331-27.3420',' 27.3420-29.0509',' 29.0509-30.7598',' 30.7598-32.4686',' 32.4686-34.1775',' 34.1775-35.8864',' 35.8864-37.5953',' 37.5953-39.3041',' 39.3041-41.0130',' 41.0130-42.7219',' 42.7219-44.4308',' 44.4308-46.1396',' 46.1396-47.8485',' 47.8485-49.5574',' 49.5574-51.2663',' 51.2663-52.9751',' 52.9751-54.6840',' 54.6840-56.3929',' 56.3929-58.1018',' 58.1018-59.8106',' 59.8106-61.5195',' 61.5195-63.2284',' 63.2284-64.9373',' 64.9373-66.6461',' 66.6461-68.3550',' 68.3550-70.0639',' 70.0639-71.7728',' 71.7728-73.4816',' 73.4816-75.1905',' 75.1905-76.8994',' 76.8994-78.6083',' 78.6083-80.3171',' 80.3171-82.0260',' 82.0260-83.7349',' 83.7349-85.4438',' 85.4438-87.1526',' 87.1526-88.8615',' 88.8615-90.5704',' 90.5704-92.2793',' 92.2793-93.9881',' 93.9881-95.6970',' 95.6970-97.4059',' 97.4059-99.1148',' 99.1148-100.8236',' 100.8236-102.5325',' 102.5325-104.2414',' 104.2414-105.9503',' 105.9503-107.6591',' 107.6591-109.3680',' 109.3680-111.0769',' 111.0769-112.7858',' 112.7858-114.4946',' 114.4946-116.2035',' 116.2035-117.9124',' 117.9124-119.6213',' 119.6213-121.3301',' 121.3301-123.0390',' 123.0390-124.7479',' 124.7479-126.4568',' 126.4568-128.1656',' 128.1656-129.8745',' 129.8745-131.5834',' 131.5834-133.2923',' 133.2923-135.0011',' 135.0011-136.7100'],
                        axisLabel:{
                            rotate:40
                        }
                    },
                        {
                            type: 'category',
                            data: [0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0, 62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 68.0, 69.0, 70.0, 71.0, 72.0, 73.0, 74.0, 75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 89.0, 90.0, 91.0, 92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0, 111.0, 112.0, 113.0, 114.0, 115.0, 116.0, 117.0, 118.0, 119.0, 120.0, 121.0, 122.0, 123.0, 124.0, 125.0, 126.0, 127.0, 128.0, 129.0, 130.0, 131.0, 132.0, 133.0, 134.0, 135.0,200]
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
                        data: [182,46,29,21,11,14,14,5,2,3,3,2,1,2,1,4,1,3,1,2,2,2,0,1,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                        type: 'bar',
                        yAxisIndex: 0,
                        xAxisIndex:0
                    },
                    {
                        data: [0.03359088501045043, 0.034268954596836196, 0.034653457841416296, 0.03473757248879912, 0.0345227215448158, 0.03401841767555787, 0.03324174145247297, 0.03221649108519235, 0.030972060979466042, 0.02954212123069602, 0.02796317903111651, 0.026273105535552715, 0.024509708194926955, 0.022709419659468592, 0.020906161225705183, 0.019130422873581156, 0.017408584738584, 0.01576248786075953, 0.01420924653054636, 0.012761281504085677, 0.011426543436724761, 0.010208889374313463, 0.009108572008617678, 0.008122801332942006, 0.00724634081556687, 0.006472104614428674, 0.005791728021990431, 0.005196089620086681, 0.004675769987255566, 0.004221437795105092, 0.003824159441456713, 0.0034756328066184263, 0.003168349205942124, 0.002895690156832687, 0.0026519672562652238, 0.002432414390619965, 0.0022331418070657564, 0.0020510614003507377, 0.001883792035932918, 0.001729552947760185, 0.0015870523043437638, 0.001455376997995913, 0.001333888630113951, 0.0012221295778659525, 0.0011197419631024971, 0.0010264013258094932, 9.417658521906996E-4, 8.654411406584682E-4, 7.969597261384019E-4, 7.357739416012867E-4, 6.812601908169854E-4, 6.327323493610688E-4, 5.894618078690556E-4, 5.507016216718879E-4, 5.157123259998315E-4, 4.8378720006320207E-4, 4.542750937550153E-4, 4.265993392158585E-4, 4.002717242715047E-4, 3.7490097283730085E-4, 3.501956271058898E-4, 3.259616296305098E-4, 3.020952377723212E-4, 2.785721528168854E-4, 2.5543390343923326E-4, 2.3277258779849967E-4, 2.1071505702498948E-4, 1.8940752762213747E-4, 1.6900145787640138E-4, 1.496413326101758E-4, 1.3145479089671133E-4, 1.145453209086567E-4, 9.898755062479852E-5, 8.482499495025612E-5, 7.206998721082474E-5, 6.0705430178109364E-5, 5.068794917713636E-5, 4.195201459309766E-5, 3.441461792863733E-5, 2.7980127557685422E-5, 2.2545009816501844E-5, 1.8002170435148665E-5, 1.4244743575249541E-5, 1.1169225059531007E-5, 8.677908310141106E-6, 6.680633104104409E-6, 5.095896989259638E-6, 3.851406827953096E-6, 2.8841642047352526E-6, 2.1401848277975107E-6, 1.5739504856577826E-6, 1.147684675766369E-6, 8.305317386984249E-7, 5.977060390975003E-7, 4.296639452776595E-7, 3.113382167176658E-7, 2.3146267726545375E-7, 1.8200514206228803E-7, 1.5771858099816922E-7, 1.5581429644826552E-7, 1.7575614118487824E-7, 2.1917105929836661E-7, 2.898679988965485E-7, 3.939540234302724E-7, 5.400327996066215E-7, 7.394662344184337E-7, 1.0066747003368893E-6, 1.35944505646347E-6, 1.8192088117832784E-6, 2.4112458104231534E-6, 3.1647625326483675E-6, 4.112789514489229E-6, 5.2918406757070745E-6, 6.741279756899196E-6, 8.502346771417012E-6, 1.0616811295875385E-5, 1.31252400334836E-5, 1.6064893240084404E-5, 1.9467297376089782E-5, 2.3355577938651955E-5, 2.7741674165149488E-5, 3.262359275097772E-5, 3.7982886943053964E-5, 4.37825662487175E-5, 4.9965646729036596E-5, 5.6454539422335696E-5, 6.315144316780702E-5, 6.993985799206988E-5, 7.668726829512394E-5, 8.324896538511024E-5, 8.947289237774235E-5, 9.520530841715392E-5, 1.0029699164209719E-4, 1.0460963925939564E-4, 1.0802208542726023E-4, 1.1043594845615535E-4],
                        type: 'line',
                        yAxisIndex: 1,
                        xAxisIndex:1,
                        symbol: "none"
                    }]
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
            height: 800px;
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
