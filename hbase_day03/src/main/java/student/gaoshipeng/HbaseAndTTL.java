package student.gaoshipeng;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HbaseAndTTL {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum" , "node01,node02,node03");
        Connection connection = ConnectionFactory.createConnection(conf);

        Admin admin = connection.getAdmin();
        //如果表不存在，才创建
        if(!admin.tableExists(TableName.valueOf("TTL"))) {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf("TTL"));
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor("info");

            //设置列族的ttl
            hColumnDescriptor.setMinVersions(2);
            hColumnDescriptor.setMaxVersions(4);
            hColumnDescriptor.setTimeToLive(5000);  //这里面是秒

           // admin.addColumn(TableName.valueOf("TTL"), hColumnDescriptor);
            hTableDescriptor.addFamily(hColumnDescriptor);
            admin.createTable(hTableDescriptor);
        }

        //像表里面插入数据
        Table ttl = connection.getTable(TableName.valueOf("TTL"));

        //List<Put> list = new ArrayList();

        for(int i = 5 ; i < 10 ; i ++){
            Put put = new Put("rk01".getBytes());
            long timeStamp = System.currentTimeMillis();
            System.out.println("timestamp = " + timeStamp);
            //方式二
            put.addColumn("info".getBytes(), "name".getBytes(), timeStamp+i, ("zhangsan" + i).getBytes());

           //为什么上面一行的代码不行，下面一行代码可以呢？

            //方式一
//            put.addColumn("info".getBytes() , "name" .getBytes() ,("zhangsan" + i) .getBytes() );
//            System.out.println("timestamp = " + System.currentTimeMillis());
            //list.add(put);
            ttl.put(put);
        }

        //ttl.put(list);

        //获取对象
        Get get = new Get("rk01".getBytes());
        get.setMaxVersions();     //这里的这个设置是你返回的时候返回几个版本的数据，如果不设置返回最新版本的数据，如果设置全部返回

        Result result = ttl.get(get);

        Cell[] cells = result.rawCells();

        for(Cell cell : cells){
            System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
        }

        ttl.close();
        connection.close();
    }
}
