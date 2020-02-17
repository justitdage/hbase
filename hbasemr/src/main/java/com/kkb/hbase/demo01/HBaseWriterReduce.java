package com.kkb.hbase.demo01;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class HBaseWriterReduce extends TableReducer<Text, Put, ImmutableBytesWritable> {
    @Override
    protected void reduce(Text key, Iterable<Put> values, Context context) throws IOException, InterruptedException {
        //最终结果如表
        //rowkey
        ImmutableBytesWritable immutableBytesWritable = new ImmutableBytesWritable();
        immutableBytesWritable.set(key.toString().getBytes());

        //put
        for (Put put : values) {
            context.write(immutableBytesWritable, put);
        }
    }
}
