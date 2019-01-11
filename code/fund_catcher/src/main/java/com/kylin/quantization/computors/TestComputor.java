package com.kylin.quantization.computors;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName: TestComputor
 * Description:
 * Author: aierxuan
 * Date: 2019-01-11 16:54
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestComputor  extends BaseSparkMain{
    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        List<Tuple2<String ,String>> data = Arrays.asList(new Tuple2<String ,String>("a","1"),new Tuple2<String ,String>("b","2")
                ,new Tuple2<String ,String>("c","3"),new Tuple2<String ,String>("d","4"),new Tuple2<String ,String>("e","5"));
        JavaRDD<Tuple2<String ,String>> rdd = context.parallelize(data);
        List<Tuple2<String, String>> collect = rdd.filter(new Function<Tuple2<String, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, String> v1) throws Exception {
                if(Integer.parseInt(v1._2)>2){
                    return true;
                }
                return false;
            }
        }).collect();
        collect.forEach(t->{
            System.out.println(t._1+","+t._2);
        });
    }
}
