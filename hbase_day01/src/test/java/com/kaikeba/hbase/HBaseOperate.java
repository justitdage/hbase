package com.kaikeba.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseOperate {

    /**
     * 操作数据库  第一步：获取连接  第二步：获取客户端对象   第三步：操作数据库  第四步：关闭
     * 创建myuser表，有两个列族 f1 f2
     *
     * @throws IOException
     */
    @Test
    public void createTable() throws IOException {
        //获得连接
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        //创建连接对象
        Connection connection = ConnectionFactory.createConnection(configuration);

        //操作：建表、删除表、修改表 -> 管理员；创建管理员对象
        Admin admin = connection.getAdmin();

        //添加了表名信息
        HTableDescriptor myuser = new HTableDescriptor(TableName.valueOf("myuser"));

        //给表添加列族
        myuser.addFamily(new HColumnDescriptor("f1"));
        myuser.addFamily(new HColumnDescriptor("f2"));

        //创建表
        admin.createTable(myuser);

        //关闭连接
        admin.close();
        connection.close();
    }

    private Connection connection;
    private Table table;

    //建立连接
    @BeforeTest
    public void init() throws IOException {
        //获得连接
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");
        //configuration.set("zookeeper.znode.parent", "/HBase");
        connection = ConnectionFactory.createConnection(configuration);
        //获得表
        table = connection.getTable(TableName.valueOf("myuser"));
    }

    @AfterTest
    public void close() throws IOException {
        table.close();
        connection.close();
    }

    //向表中添加数据
    @Test
    public void putData() throws IOException {
        Put put = new Put("0001".getBytes());//创建Put对象，并指定rowkey值
        //添加cell值：列族名称+列名+值
        put.addColumn("f1".getBytes(), "name".getBytes(), "zhangsan".getBytes());
        put.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(18));
        put.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(25));
        put.addColumn("f1".getBytes(), "address".getBytes(), Bytes.toBytes("地球人"));

        table.put(put);
    }

    @Test
    public void batchInsert() throws IOException {
        //创建put对象，并指定rowkey
        Put put = new Put("0002".getBytes());
        //向f1列族添加数据
        put.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(1));
        put.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("曹操"));
        put.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(30));
        //向f2列族添加数据
        put.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("沛国谯县"));
        put.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("16888888888"));
        put.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("helloworld"));

        Put put2 = new Put("0003".getBytes());
        put2.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(2));
        put2.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("刘备"));
        put2.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(32));
        put2.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put2.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("幽州涿郡涿县"));
        put2.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("17888888888"));
        put2.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("talk is cheap , show me the code"));

        Put put3 = new Put("0004".getBytes());
        put3.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(3));
        put3.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("孙权"));
        put3.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(35));
        put3.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put3.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("下邳"));
        put3.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("12888888888"));
        put3.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("what are you 弄啥嘞！"));

        Put put4 = new Put("0005".getBytes());
        put4.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(4));
        put4.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("诸葛亮"));
        put4.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(28));
        put4.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put4.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("四川隆中"));
        put4.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("14888888888"));
        put4.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("出师表你背了嘛"));

        Put put5 = new Put("0006".getBytes());
        put5.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(5));
        put5.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("司马懿"));
        put5.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(27));
        put5.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put5.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("哪里人有待考究"));
        put5.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("15888888888"));
        put5.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("跟诸葛亮死掐"));


        Put put6 = new Put("0007".getBytes());
        put6.addColumn("f1".getBytes(), "id".getBytes(), Bytes.toBytes(5));
        put6.addColumn("f1".getBytes(), "name".getBytes(), Bytes.toBytes("xiaobubu—吕布"));
        put6.addColumn("f1".getBytes(), "age".getBytes(), Bytes.toBytes(28));
        put6.addColumn("f2".getBytes(), "sex".getBytes(), Bytes.toBytes("1"));
        put6.addColumn("f2".getBytes(), "address".getBytes(), Bytes.toBytes("内蒙人"));
        put6.addColumn("f2".getBytes(), "phone".getBytes(), Bytes.toBytes("15788888888"));
        put6.addColumn("f2".getBytes(), "say".getBytes(), Bytes.toBytes("貂蝉去哪了"));

        List<Put> listPut = new ArrayList<Put>();
        listPut.add(put);
        listPut.add(put2);
        listPut.add(put3);
        listPut.add(put4);
        listPut.add(put5);
        listPut.add(put6);

        table.put(listPut);
    }

    /**
     * 查询rowkey为0003、f1列族下、f2列族下say列的数据
     * Get
     * table.get(get)
     * result: 对一行数据的结果做个抽象（包含了许多cell）
     * CellUtil工具列获得行键、列族、列、cell值
     * @throws IOException
     */
    @Test
    public void getDataByRowkey() throws IOException {
        //通过get对象，指定rowkey
        Get get = new Get("0003".getBytes());

        //获取某列族
        get.addFamily("f1".getBytes());//限定查询f1列族下面所有列的值
        get.addColumn("f2".getBytes(), "say".getBytes());//查询f2列族say列的值

        //通过get查询，返回0003行，f1列族、f2:say列的所有cell的值，封装到一个Result对象
        Result result = table.get(get);

        //获得Result中的所有Cell
        List<Cell> cells = result.listCells();

        //遍历Cell
        for (Cell cell : cells) {
            //cell单元格
            //获得rowkey
            byte[] rowkey_bytes = CellUtil.cloneRow(cell);
            //获得列族
            byte[] family_bytes = CellUtil.cloneFamily(cell);
            //获得列名
            byte[] qualifier_bytes = CellUtil.cloneQualifier(cell);
            //获得值
            byte[] cell_bytes = CellUtil.cloneValue(cell);

            if ("age".equals(Bytes.toString(qualifier_bytes)) || "id".equals(Bytes.toString(qualifier_bytes))) {
                System.out.println(Bytes.toString(rowkey_bytes));
                System.out.println(Bytes.toString(family_bytes));
                System.out.println(Bytes.toString(qualifier_bytes));
                System.out.println(Bytes.toInt(cell_bytes));//age或id的值是int类型，得用Bytes.toInt
            } else {
                System.out.println(Bytes.toString(rowkey_bytes));
                System.out.println(Bytes.toString(family_bytes));
                System.out.println(Bytes.toString(qualifier_bytes));
                System.out.println(Bytes.toString(cell_bytes));
            }
        }
    }

    /**
     * 不知道rowkey的具体值，我想查询rowkey范围值是0003  到0006
     * select * from myuser  where age > 30  and id < 8  and name like 'zhangsan'
     * Scan
     * scan对象的各种限制方法
     * table.getScanner(scan)
     * ResultScanner ->许多的Result对象
     *
     */
    @Test
    public void scanData() throws IOException {
        Scan scan = new Scan();//若没有指定startRow以及stopRow，则全表扫描
        //扫描f1列族
        scan.addFamily("f1".getBytes());
        //扫描 f2列族 phone列
        scan.addColumn("f2".getBytes(), "phone".getBytes());

        //设置起始结束rowkey，前闭后开
        scan.setStartRow("0003".getBytes());
        scan.setStopRow("0007".getBytes());

        //控制一次读取的数据量3个result
        scan.setCaching(3);
        //此方法设置用户获得的result中最多包含的cell个数；一行数据中，可能有n个cell；n=5，setBatch(2)，那么获得3个result，分别包含2、2、1个cell
        scan.setBatch(2);
        //将结果缓存
        scan.setCacheBlocks(true);
        //单次rpc操作最多拿到maxResultSize字节的结果(result)集
        scan.setMaxResultSize(1024);
        //获得每个cell的最新的n个版本的值
        scan.setMaxVersions(2);

        //通过getScanner查询获取到了表里面所有的数据，是多条数据
        ResultScanner scanner = table.getScanner(scan);

        //遍历ResultScanner 得到每一条数据，每一条数据都是封装在result对象里面了
        for (Result result : scanner) {
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                byte[] family_name = CellUtil.cloneFamily(cell);
                byte[] qualifier_name = CellUtil.cloneQualifier(cell);
                byte[] rowkey = CellUtil.cloneRow(cell);
                byte[] value = CellUtil.cloneValue(cell);
                //判断id和age字段，这两个字段是整形值
                if ("age".equals(Bytes.toString(qualifier_name)) || "id".equals(Bytes.toString(qualifier_name))) {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toInt(value));
                } else {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toString(value));
                }
            }
        }
    }

    /**
     * 查询所有的rowkey比0003小的所有的数据
     */
    @Test
    public void rowFilter() throws IOException {
        Table table = connection.getTable(TableName.valueOf("myuser"));
        Scan scan = new Scan();
        //获取比较对象
        BinaryComparator binaryComparator = new BinaryComparator("0003".getBytes());

        /**
         * rowFilter需要加上两个参数
         * 第一个参数就是我们的比较规则
         * 第二个参数就是我们的比较对象
         */
        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.LESS, binaryComparator);
        //为我们的scan对象设置过滤器
        scan.setFilter(rowFilter);

        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                byte[] family_name = CellUtil.cloneFamily(cell);
                byte[] qualifier_name = CellUtil.cloneQualifier(cell);
                byte[] rowkey = CellUtil.cloneRow(cell);
                byte[] value = CellUtil.cloneValue(cell);
                //判断id和age字段，这两个字段是整形值
                if ("age".equals(Bytes.toString(qualifier_name)) || "id".equals(Bytes.toString(qualifier_name))) {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toInt(value));
                } else {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toString(value));
                }
            }
        }
    }

    /**
     * 通过familyFilter来实现列族的过滤
     * 需要过滤，列族名包含f2
     * f1  f2   hello   world
     */
    @Test
    public void familyFilter() throws IOException {
        Table table = connection.getTable(TableName.valueOf("myuser"));
        Scan scan = new Scan();

        SubstringComparator substringComparator = new SubstringComparator("f2");
        //通过familyfilter来设置列族的过滤器
        FamilyFilter familyFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, substringComparator);
        scan.setFilter(familyFilter);

        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                byte[] family_name = CellUtil.cloneFamily(cell);
                byte[] qualifier_name = CellUtil.cloneQualifier(cell);
                byte[] rowkey = CellUtil.cloneRow(cell);
                byte[] value = CellUtil.cloneValue(cell);
                //判断id和age字段，这两个字段是整形值
                if ("age".equals(Bytes.toString(qualifier_name)) || "id".equals(Bytes.toString(qualifier_name))) {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toInt(value));
                } else {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toString(value));
                }
            }
        }
    }

    /**
     * 列名过滤器 只查询包含name列的值
     * 列名包含“name”
     */
    @Test
    public void qualifierFilter() throws IOException {
        Scan scan = new Scan();
        SubstringComparator substringComparator = new SubstringComparator("name");

        //定义列名过滤器，只查询列名包含name的列
        QualifierFilter qualifierFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, substringComparator);
        scan.setFilter(qualifierFilter);

        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    private void printlReult(ResultScanner scanner) {
        for (Result result : scanner) {
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                byte[] family_name = CellUtil.cloneFamily(cell);
                byte[] qualifier_name = CellUtil.cloneQualifier(cell);
                byte[] rowkey = CellUtil.cloneRow(cell);
                byte[] value = CellUtil.cloneValue(cell);
                //判断id和age字段，这两个字段是整形值
                if ("age".equals(Bytes.toString(qualifier_name)) || "id".equals(Bytes.toString(qualifier_name))) {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toInt(value));
                } else {
                    System.out.println("数据的rowkey为" + Bytes.toString(rowkey) + "======数据的列族为" + Bytes.toString(family_name) + "======数据的列名为" + Bytes.toString(qualifier_name) + "==========数据的值为" + Bytes.toString(value));
                }
            }
        }
    }

    /**
     * 查询哪些字段值  包含"8"
     */
    @Test
    public void contains8() throws IOException {
        Scan scan = new Scan();
        SubstringComparator substringComparator = new SubstringComparator("8");
        //列值过滤器，过滤列值当中包含数字8的所有的列
        ValueFilter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, substringComparator);
        scan.setFilter(valueFilter);

        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    /**
     * select  *  from  myuser where name  = '刘备'
     * 会返回我们符合条件数据的所有的字段
     * <p>
     * SingleColumnValueExcludeFilter  列值排除过滤器
     * select  *  from  myuser where name  ！= '刘备'
     */
    @Test
    public void singleColumnValueFilter() throws IOException {
        //查询 f1  列族 name  列  值为刘备的数据
        Scan scan = new Scan();
        //单列值过滤器，过滤  f1 列族  name  列  值为刘备的数据
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter("f1".getBytes(), "name".getBytes(), CompareFilter.CompareOp.EQUAL, "xiaobubu—吕布".getBytes());
        scan.setFilter(singleColumnValueFilter);

        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    @Test
    public void singleColumnValueExcludeFilter() throws IOException {
        //查询 f1  列族 name  列  值为刘备的数据
        Scan scan = new Scan();
        //单列值过滤器，过滤  f1 列族  name  列  值为刘备的数据
        SingleColumnValueExcludeFilter singleColumnValueFilter = new SingleColumnValueExcludeFilter("f1".getBytes(), "name".getBytes(), CompareFilter.CompareOp.EQUAL, "xiaobubu—吕布".getBytes());
        scan.setFilter(singleColumnValueFilter);

        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    /**
     * 查询rowkey前缀以  00开头的所有的数据
     */
    @Test
    public void prefixFilter() throws IOException {
        Scan scan = new Scan();
        //过滤rowkey以  00开头的数据
        PrefixFilter prefixFilter = new PrefixFilter("0001".getBytes());
        scan.setFilter(prefixFilter);

        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    //分页过滤器
    @Test
    public void pageFilter() throws IOException {
        //页码
        int pageNum = 3;
        //每页的大小
        int pageSize = 2;

        Scan scan = new Scan();

        if (pageNum == 1) {//获取第一页的数据
            scan.setMaxResultSize(1024);
            //hbase表中，默认的开始行键是""
            scan.setStartRow("".getBytes());
            //使用分页过滤器，实现数据的分页；参数表示每页返回多少个result
            PageFilter pageFilter = new PageFilter(pageSize);
            scan.setFilter(pageFilter);

            ResultScanner scanner = table.getScanner(scan);
            printlReult(scanner);
        } else {//如果所读分页不是第一页，先取得此分页的第一个rowkey值
            //查询(3-1) * 2 + 1
            String startRow = "";
            //扫描多少条 5
            int scanDatas = (pageNum - 1) * pageSize + 1;
            scan.setMaxResultSize(1024);
            PageFilter pageFilter = new PageFilter(scanDatas);
            scan.setFilter(pageFilter);
            ResultScanner scanner = table.getScanner(scan);
            for (Result result : scanner) {
                //获得rowkey
                byte[] row_bytes = result.getRow();
                startRow = Bytes.toString(row_bytes);
            }

            scan.setStartRow(startRow.getBytes());
            scan.setMaxResultSize(1024);
            PageFilter pageFilter1 = new PageFilter(pageSize);
            scan.setFilter(pageFilter1);

            ResultScanner scanner1 = table.getScanner(scan);
            printlReult(scanner1);
        }
    }

    /**
     * 使用多个过滤器
     * 查询  f1 列族  name列  为刘备
     * 并且rowkey 前缀以  00开头的数据
     */
    @Test
    public void filterList() throws IOException {
        Scan scan = new Scan();
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter("f1".getBytes(), "name".getBytes(), CompareFilter.CompareOp.EQUAL, "刘备".getBytes());
        PrefixFilter prefixFilter = new PrefixFilter("00".getBytes());

        FilterList filterList = new FilterList();
        filterList.addFilter(singleColumnValueFilter);
        filterList.addFilter(prefixFilter);

        scan.setFilter(filterList);
        ResultScanner scanner = table.getScanner(scan);
        printlReult(scanner);
    }

    /**
     * 删除数据
     */
    @Test
    public void deleteData() throws IOException {
        Delete delete = new Delete("0003".getBytes());
        //删除列族
        delete.addFamily("f1".getBytes());
        //删除列
        delete.addColumn("f2".getBytes(), "phone".getBytes());
        //删除指定版本的cell值
        //delete.addColumn("f2".getBytes(), "say".getBytes(), 1581434598961L);
        table.delete(delete);
    }

    /**
     * 删除表
     */
    @Test
    public void deleteTable() throws IOException {
        //获取管理员对象，用于表的删除
        Admin admin = connection.getAdmin();
        //删除一张表之前，需要先禁用表
        admin.disableTable(TableName.valueOf("myuser"));
        admin.deleteTable(TableName.valueOf("myuser"));
    }
}
