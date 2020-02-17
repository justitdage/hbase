package student;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class HBase01Test {
    private Connection connection;
    private Table table;

    @Test
    public void createTable() throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        Connection connection = ConnectionFactory.createConnection(config);

        Admin admin = connection.getAdmin();

        HTableDescriptor myuser = new HTableDescriptor(TableName.valueOf("myuser-st1"));
        myuser.addFamily(new HColumnDescriptor("f1"));
        myuser.addFamily(new HColumnDescriptor("f2"));

        admin.createTable(myuser);

        admin.close();
        connection.close();
    }

    @Test
    public void putData() throws IOException {
        Put put = new Put("0002".getBytes());//创建put对象，指定rowkey值
        put.addColumn("f1".getBytes(), "name".getBytes(), "zhangsan".getBytes());
        put.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(18));
        put.addColumn("f1".getBytes(),"id".getBytes(), Bytes.toBytes(25));
        put.addColumn("f1".getBytes(),"address".getBytes(), Bytes.toBytes("地球人"));
        table.put(put);
    }


    @BeforeTest
    public void init() throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        connection = ConnectionFactory.createConnection(config);
        table = connection.getTable(TableName.valueOf("myuser"));
    }
    @AfterTest
    public void close() throws IOException {
        table.close();
        connection.close();
    }
}
