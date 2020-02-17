package com.kaikeba.hbase.demo01;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * myuser f1: name&age => myuser2 f1
 */
public class HBaseReadMapper extends TableMapper<Text, Put> {
    /**
     *
     * @param key rowkey
     * @param value rowkey此行的数据 Result类型
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        //获得roweky的字节数组
        byte[] rowkey_bytes = key.get();
        String rowkeyStr = Bytes.toString(rowkey_bytes);
        Text text = new Text(rowkeyStr);

        //输出数据 -> 写数据 -> Put 构建Put对象
        Put put = new Put(rowkey_bytes);
        //获取一行中所有的Cell对象
        Cell[] cells = value.rawCells();
        //将f1 : name& age输出
        for(Cell cell: cells) {
            //当前cell是否是f1
            //列族
            byte[] family_bytes = CellUtil.cloneFamily(cell);
            String familyStr = Bytes.toString(family_bytes);
            if("f1".equals(familyStr)) {
                //在判断是否是name | age
                byte[] qualifier_bytes = CellUtil.cloneQualifier(cell);
                String qualifierStr = Bytes.toString(qualifier_bytes);
                if("name".equals(qualifierStr)) {
                    put.add(cell);
                }
                if("age".equals(qualifierStr)) {
                    put.add(cell);
                }
            }
        }

        //判断是否为空；不为空，才输出
        if(!put.isEmpty()){
            context.write(text, put);
        }
    }
}
