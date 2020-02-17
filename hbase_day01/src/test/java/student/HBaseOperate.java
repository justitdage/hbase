package student;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class HBaseOperate {

    private Connection connection ;
    private final String TABLE_NAME = "myuser";
    private Table table ;

    @Test
    public void createTable() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "39.105.138.31:2181,47.93.53.1:2181,47.93.220.66:2181");
//        configuration.set("zookeeper.znode.parent", "/HBase");
        //获得连接
        Connection connection = ConnectionFactory.createConnection(configuration);
        //得管理员
        Admin admin = connection.getAdmin();
        //创建表
        HTableDescriptor myuser = new HTableDescriptor(TableName.valueOf("myuser2"));
        //添加列族
        myuser.addFamily(new HColumnDescriptor("f1"));
        myuser.addFamily(new HColumnDescriptor("f2"));
        admin.createTable(myuser);
        //关闭连接
        admin.close();
        connection.close();
    }


//    @Test
//    public void createTable() throws IOException {
//        Configuration configuration= HBaseConfiguration.create();
//        configuration.set("hbase.zookeeper.quorum","node01:2181,node02:2181,node03:2181");
//        Connection connection= ConnectionFactory.createConnection(configuration);
//        Admin admin=connection.getAdmin();
//        HTableDescriptor myuser=new HTableDescriptor(TableName.valueOf("myuser"));
//        myuser.addFamily(new HColumnDescriptor("f1"));
//        myuser.addFamily(new HColumnDescriptor("f2"));
//        admin.createTable(myuser);
//        admin.close();
//        connection.close();
//    }
//    @BeforeTest
//    public void initTable() throws IOException{
//        Configuration configuration=HBaseConfiguration.create();
//        configuration.set("HBase.zookeeper.quorum","node01:2181,node02:2181");
//        connection= ConnectionFactory.createConnection(configuration);
//        table = connection.getTable(TableName.valueOf(TABLE_NAME));
//    }
//    @AfterTest
//    public void close() throws IOException {
//        table.close();
//        connection.close();
//    }
//    @Test
//    public void addData() throws IOException {
//        //获取表
//        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
//        Put put = new Put("0001".getBytes());//创建put对象，并指定rowkey值
//        put.addColumn("f1".getBytes(),"name".getBytes(),"zhangsan".getBytes());
//        put.addColumn("f1".getBytes(),"age".getBytes(), Bytes.toBytes(18));
//        put.addColumn("f1".getBytes(),"id".getBytes(), Bytes.toBytes(25));
//        put.addColumn("f1".getBytes(),"address".getBytes(), Bytes.toBytes("地球人"));
//        table.put(put);
//        table.close();
//    }
}
