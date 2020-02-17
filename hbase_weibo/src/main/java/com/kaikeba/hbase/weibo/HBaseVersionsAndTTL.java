package com.kaikeba.hbase.weibo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseVersionsAndTTL {
    public static void main(String[] args) throws IOException {
        //指定hbase集群连接的zk集群
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");
        Connection connection = ConnectionFactory.createConnection(configuration);

        Admin admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("version_hbase");

        //如果表不存在则创建
        if(!admin.tableExists(tableName)) {
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);

            HColumnDescriptor f1 = new HColumnDescriptor("f1");
            //最大版本、最小版本、TTL
            f1.setMinVersions(3);
            f1.setMaxVersions(5);
            //针对f1列族下边所有的列设置TTL time to live；单位秒
            f1.setTimeToLive(30);

            tableDescriptor.addFamily(f1);

            admin.createTable(tableDescriptor);
        }

        Table version_hbase = connection.getTable(tableName);

        //一、插入数据
       for(int i = 0; i < 6; i++) {
            Put put = new Put("001".getBytes());
            put.addColumn("f1".getBytes(), "name".getBytes(), ("zhangsan" + i).getBytes());
            version_hbase.put(put);
        }

        //二、查询验证
        Get get = new Get("001".getBytes());
        //获得cell的所有版本的值
        get.setMaxVersions();

        Result result = version_hbase.get(get);

//        List<Cell> cells = result.listCells();//查询没有过期的数据
        Cell[] cells = result.rawCells();

        for(Cell cell: cells) {
            System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
        }

        //关闭连接
        admin.close();
        version_hbase.close();
        connection.close();
    }

}
