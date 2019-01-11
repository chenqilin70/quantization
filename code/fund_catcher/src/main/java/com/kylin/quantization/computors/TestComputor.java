package com.kylin.quantization.computors;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
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
        ImmutableBytesWritable a=new ImmutableBytesWritable(Bytes.toBytes("a"));
        ImmutableBytesWritable b=new ImmutableBytesWritable(Bytes.toBytes("b"));
        ImmutableBytesWritable c=new ImmutableBytesWritable(Bytes.toBytes("c"));
        ImmutableBytesWritable d=new ImmutableBytesWritable(Bytes.toBytes("d"));
        ImmutableBytesWritable e=new ImmutableBytesWritable(Bytes.toBytes("e"));
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        List<Tuple2<ImmutableBytesWritable ,String>> data = Arrays.asList(new Tuple2<ImmutableBytesWritable ,String>(a,"1"),new Tuple2<ImmutableBytesWritable ,String>(b,"2")
                ,new Tuple2<ImmutableBytesWritable ,String>(c,"3"),new Tuple2<ImmutableBytesWritable ,String>(d,"4"),new Tuple2<ImmutableBytesWritable ,String>(e,"5"));
        JavaRDD<Tuple2<ImmutableBytesWritable ,String>> rdd = context.parallelize(data);
        List<Tuple2<ImmutableBytesWritable, String>> collect = rdd.filter(new Function<Tuple2<ImmutableBytesWritable, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, String> v1) throws Exception {
                if(Integer.parseInt(v1._2)>2){
                    return true;
                }
                return false;
            }
        }).collect();
        collect.forEach(t->{
            System.out.println(Bytes.toString(t._1.get())+","+t._2);
        });
    }
}
