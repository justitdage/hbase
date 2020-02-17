package com.kaikeba.hbase.demo03;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

//四个泛型中后两个，分别对应rowkey及put
public class BulkLoadMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
    /**
     * 0007	zhangsan	18
     * 0008	lisi	25
     * 0009	wangwu	20
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split("\t");
        //封装输出的rowkey类型
        ImmutableBytesWritable immutableBytesWritable = new ImmutableBytesWritable(split[0].getBytes());

        //构建put对象
        Put put = new Put(split[0].getBytes());
        put.addColumn("f1".getBytes(), "name".getBytes(), split[1].getBytes());
        put.addColumn("f1".getBytes(), "age".getBytes(), split[2].getBytes());

        context.write(immutableBytesWritable, put);
    }
}
