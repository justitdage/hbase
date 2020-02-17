package com.kkb.coprocessor;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;

import java.io.IOException;
import java.util.List;

public class MyProcessor extends BaseRegionObserver {
    /**
     *
     * @param e
     * @param put   插入到proc1表里面的数据，都是封装在put对象里面了，可以解析put对象，获取数据，获取到了数据之后，插入到proc2表里面去
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        //获取连接
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");
        Connection connection = ConnectionFactory.createConnection(configuration);

        //涉及到多个版本问题
        List<Cell> cells = put.get("info".getBytes(), "name".getBytes());
        Cell nameCell = cells.get(0);//获取最新的那个版本数据
        //Cell nameCell = put.get("info".getBytes(), "name".getBytes()).get(0);

        Table proc2 = connection.getTable(TableName.valueOf("proc2"));

        Put put1 = new Put(put.getRow());
        put1.add(nameCell);

        proc2.put(put1);
        //释放资源
        proc2.close();
        connection.close();
    }
}