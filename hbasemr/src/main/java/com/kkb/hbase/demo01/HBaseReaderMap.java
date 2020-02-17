package com.kkb.hbase.demo01;

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
 * kout rowkey
 * vout put
 */
public class HBaseReaderMap extends TableMapper<Text, Put> {
    /**
     * 0001                            column=f1:address, timestamp=1576849354579, value=\xE5\x9C\xB0\xE7\x90\x83\xE4\xBA\xBA
     *  0001                            column=f1:age, timestamp=1576849354579, value=\x00\x00\x00\x12
     *  0001                            column=f1:id, timestamp=1576849354579, value=\x00\x00\x00\x19
     *  0001                            column=f1:name, timestamp=1576849354579, value=zhangsan
     *  0002                            column=f1:address, timestamp=1577272871777, value=beijing
     *  0002                            column=f1:name, timestamp=1577266140070, value=lisi
     * @param key rowkey
     * @param value result
     * @param context
     * @throws IOException
     * @throws InterruptedException
     * 将myuser f1:name f1:age -> myuser2 f1下边
     */
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        byte[] rowkey = key.get();
        String rowkeyStr = Bytes.toString(rowkey);
        Text text = new Text(rowkeyStr);

        //put
        Put put = new Put(rowkey);

        Cell[] cells = value.rawCells();
        for (Cell cell : cells) {
            //判断是否是f1列族
            byte[] family_bytes = CellUtil.cloneFamily(cell);
            String family = Bytes.toString(family_bytes);
            if("f1".equals(family)) {
                //如果是，判断是否是name or age
                byte[] qualifier_bytes = CellUtil.cloneQualifier(cell);
                String qualifier = Bytes.toString(qualifier_bytes);
                if("name".equals(qualifier)) {
                    put.add(cell);
                }

                if("age".equals(qualifier)) {
                    put.add(cell);
                }
            }
        }

        if(!put.isEmpty()) {
            context.write(text, put);
        }
    }
}
