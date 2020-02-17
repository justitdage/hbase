package com.kaikeba.hbase.demo03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

public class LoadData {
    public static void main(String[] args) throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01,node02,node03");
        //获取数据库连接
        Connection connection =  ConnectionFactory.createConnection(configuration);
        //获取表的管理器对象
        Admin admin = connection.getAdmin();
        //获取table对象
        TableName tableName = TableName.valueOf("myuser2");
        Table table = connection.getTable(tableName);
        //构建LoadIncrementalHFiles加载HFile文件
        LoadIncrementalHFiles load = new LoadIncrementalHFiles(configuration);
        load.doBulkLoad(new Path("hdfs://node01:8020/hbase/out_hfile"), admin,table,connection.getRegionLocator(tableName));
    }
}
