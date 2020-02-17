package com.kaikeba.hbase.weibo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 建命名空间hbase
 * 建表
 *      微博内容表
 *      用户关系表
 *      收件箱表
 * 发送微博
 * 关注别人
 * 取消关注
 * 获得所关注人发表的微博
 */
public class HBaseWeibo {

    //微博内容表
    private static final byte[] WEIBO_CONTENT = "weibo:content".getBytes();
    //用户关系表
    private static final byte[] WEIBO_RELATION = "weibo:relation".getBytes();
    //收件箱表
    private static final byte[] WEIBO_RECEIVE_CONTENT_EMAIL = "weibo:receive_content_email".getBytes();

    public static void main(String[] args) throws IOException {
        HBaseWeibo hBaseWeibo = new HBaseWeibo();
        //建命名空间
//        hBaseWeibo.createNameSpace();
        //微博内容表
//        hBaseWeibo.createTableContent();
        //用户关系表
//        hBaseWeibo.createTableRelation();
        //收件箱表
//        hBaseWeibo.createTableReceiveContentEmails();
        //发送微博
//        hBaseWeibo.publishWeibo("2", "今天是20191127，寒、大风、雾霾");
        //关注别人
//        hBaseWeibo.addAttends("1", "2", "3", "M");
//        hBaseWeibo.addAttends("1", "2");
        //取消关注
//        hBaseWeibo.cancelAttends("1", "2");
        //获得所关注人发表的微博
        hBaseWeibo.getContent("1");
    }

    /**
     * 某个用户获取收件箱表内容
     * 例如A用户刷新微博，拉取他所有关注人的微博内容
     * A 从 weibo:receive_content_email  表当中获取所有关注人的rowkey
     * 通过rowkey从weibo:content表当中获取微博内容
     */
    public void getContent(String uid) throws IOException {
        //从email表获得uid行的所有的值-> 发送微博时的rowkey
        Connection connection = getConnection();
        Table weibo_email = connection.getTable(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL));

        Get get = new Get(uid.getBytes());
        get.setMaxVersions(5);

        Result result = weibo_email.get(get);
        Cell[] cells = result.rawCells();

        ArrayList<Get> gets = new ArrayList<>();

        for(Cell cell: cells) {
            //System.out.println("current cell type is delete: " + CellUtil.isDeleteType(cell));
            byte[] bytes = CellUtil.cloneValue(cell);
            Get get1 = new Get(bytes);
            gets.add(get1);
        }

        //根据这些rowkey去content表获得微博内容
        Table weibo_content = connection.getTable(TableName.valueOf(WEIBO_CONTENT));
        Result[] results = weibo_content.get(gets);
        for(Result result1: results) {
            byte[] weiboContent = result1.getValue("info".getBytes(), "content".getBytes());
            System.out.println(Bytes.toString(weiboContent));
        }
    }

    /**
     * 取消关注 A取消关注 B,C,D这三个用户
     * 其实逻辑与关注B,C,D相反即可
     * 第一步：在weibo:relation关系表当中，在attends列族当中删除B,C,D这三个列
     * 第二步：在weibo:relation关系表当中，在fans列族当中，以B,C,D为rowkey，查找fans列族当中A这个粉丝，给删除掉
     * 第三步：A取消关注B,C,D,在收件箱中，删除取关的人的微博的rowkey
     */
    public void cancelAttends(String uid, String... attends) throws IOException {
        //relation:删除关注的人
        Connection connection = getConnection();
        Table weibo_relation = connection.getTable(TableName.valueOf(WEIBO_RELATION));

        Delete delete = new Delete(uid.getBytes());
        for(String cancelAttend: attends) {
            delete.addColumn("attends".getBytes(), cancelAttend.getBytes());
        }
        weibo_relation.delete(delete);

        //relation：删除attends的粉丝uid
        for(String cancelAttend: attends) {
            Delete delete1 = new Delete(cancelAttend.getBytes());
            delete1.addColumn("fans".getBytes(), uid.getBytes());
            weibo_relation.delete(delete1);
        }

        //email：删除uid中，attends相关的列
        Table weibo_email = connection.getTable(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL));
        Delete delete1 = new Delete(uid.getBytes());
        for(String attend: attends) {
            delete1.addColumns("info".getBytes(), attend.getBytes());
        }
        weibo_email.delete(delete1);

        //释放资源
        weibo_relation.close();
        weibo_email.close();
        connection.close();

    }

    /**
     * 添加关注用户，一次可能添加多个关注用户
     * A 关注一批用户 B,C ,D
     * 第一步：A是B,C,D的关注者   在weibo:relations 当中attend列族当中以A作为rowkey，B,C,D作为列名，B,C,D作为列值，保存起来
     * weibo:relations
     *
     * 第二步：B,C,D都会多一个粉丝A  在weibo:relations 当中fans列族当中分别以B,C,D作为rowkey，A作为列名，A作为列值，保存起来
     * weibo:relations
     *
     * 第三步：获取B,C,D发布的微博rowkey
     * weibo:content
     *
     * 第四步：A需要获取B,C,D 的微博的rowkey存放到 receive_content_email 表当中去，以A作为rowkey，B,C,D作为列名，B,C,D发布的微博rowkey，放到对应的列值里面去
     * receive_content_email
     *
     * @param uid 当前用户
     * @param attends 当前用户所关注的一些其他用户
     */
    public void addAttends(String uid, String... attends) throws IOException {
        //第一：把uid关注别人的逻辑，写到relation表的attend列族下
        Connection connection = getConnection();
        Table weibo_relation = connection.getTable(TableName.valueOf(WEIBO_RELATION));

        Put put = new Put(uid.getBytes());
        for(String attend: attends) {
            //被关注人的id作为列名、单元格的值
            put.addColumn("attends".getBytes(),attend.getBytes(), attend.getBytes());
        }
        weibo_relation.put(put);

        //第二：要将attends中每人有一个粉丝uid的逻辑，添加到relation表的fans列族下
        for(String attend: attends) {
            Put put1 = new Put(attend.getBytes());
            put1.addColumn("fans".getBytes(), uid.getBytes(), uid.getBytes());
            weibo_relation.put(put1);
        }

        //第三：去content表查询attends中，每个人发布微博时的rowkey
        Table weibo_content = connection.getTable(TableName.valueOf(WEIBO_CONTENT));
        Scan scan = new Scan();
//        scan.setCaching(10);//服务器每次最多给客户端返回多少个Result，放到cache队列
        /**
         * 客户端收到的每个Result中，最多包含一行数据中的cell个数；
         * 如果某行有4个cell，设置的batch为3，那么客户端拿到2个Result，每个Result中cell个数分别为3、1
         */
//        scan.setBatch(10);
        /**
         * 设置单次RPC最多拿到的结果集的字节大小
         * 默认值2097152字节，即2M
         */
//        scan.setMaxResultSize(2097152);
        //客户端是否容忍拿到一行数据中，包含部分cell的Result
        //scan.setAllowPartialResults(true);

        ArrayList<byte[]> rowkeyBytes = new ArrayList<>();
        for(String attend: attends) {
            //attend -> 被关注人的uid -> 2 -> 2_timestamp1、2_timestamp2...
            //行键前缀过滤器
            PrefixFilter prefixFilter = new PrefixFilter((attend + "_").getBytes());
            scan.setFilter(prefixFilter);

            ResultScanner scanner = weibo_content.getScanner(scan);
            //判空：如果当前被关注人没有发送过微博的话，跳过此次循环
            if(null == scanner) {
                continue;
            }

            for(Result result: scanner) {
                byte[] rowkeyWeiboContent = result.getRow();
                rowkeyBytes.add(rowkeyWeiboContent);
            }
        }

        //第四：要将uid关注的人attends发布的微博时的rowkey写入到email表
        Table weibo_email = connection.getTable(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL));
        //判空
        if(rowkeyBytes.size() > 0) {
            Put put1 = new Put(uid.getBytes());

            for(byte[] rowkeyWeiboContent: rowkeyBytes) {
                //rowkeyWeiboContent -> 1_1758420156 uid_timestamp
                String rowkey = Bytes.toString(rowkeyWeiboContent);
                String[] split = rowkey.split("_");
                //被关注人（发送微博的人）的id作为列名；发送微博的rowkey作为cell值
                put1.addColumn("info".getBytes(), split[0].getBytes(), Long.parseLong(split[1]), rowkeyWeiboContent);
            }
            weibo_email.put(put1);
        }

        //释放资源
        weibo_content.close();
        weibo_relation.close();
        weibo_email.close();
        connection.close();
    }

    //发送微博
    /**
     *  第一步：将uid微博内容添加到content表
     *  content
     *
     *  第二步：从relation表中，获得uid的粉丝有哪些fan_uids
     *  ralation
     *
     *  第三步：fan_uids中，每个fan_uid插入数据；uid发送微博时的rowkey
     *  email
     */
    public void publishWeibo(String uid, String content) throws IOException {
        //第一步：将uid微博内容添加到content表
        Connection connection = getConnection();
        Table weibo_content = connection.getTable(TableName.valueOf(WEIBO_CONTENT));
        long timeStamp = System.currentTimeMillis();
        //put -> rowkey -> uid+timestamp
        String rowkey = uid + "_" + timeStamp;
        //put
        Put put = new Put(rowkey.getBytes());
        put.addColumn("info".getBytes(), "content".getBytes(), timeStamp, content.getBytes());
        //完成内容的添加
        weibo_content.put(put);

        //第二步：从relation表中，获得uid的粉丝有哪些fan_uids
        Table weibo_relation = connection.getTable(TableName.valueOf(WEIBO_RELATION));
        //get
        Get get = new Get(uid.getBytes());
        get.addFamily("fans".getBytes());

        Result result = weibo_relation.get(get);

        //严谨一些：判空
        if(result.isEmpty()) {
            weibo_content.close();
            weibo_relation.close();
            connection.close();
            return;
        }
        Cell[] cells = result.rawCells();

        List<byte[]> fan_uids = new ArrayList<>();
        for(Cell cell: cells) {
            byte[] fan_uid = CellUtil.cloneQualifier(cell);
            fan_uids.add(fan_uid);
        }

        //第三步：fan_uids中，每个fan_uid插入数据；uid发送微博时的rowkey
        Table weibo_email = connection.getTable(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL));

        List<Put> putList = new ArrayList<>();
        for(byte[] fan_uid: fan_uids) {
            //put
            Put put1 = new Put(fan_uid);
            put1.addColumn("info".getBytes(), uid.getBytes(), timeStamp, rowkey.getBytes());
            putList.add(put1);
        }

        weibo_email.put(putList);

        //释放资源
        weibo_content.close();
        weibo_relation.close();
        weibo_email.close();
        connection.close();
    }

    /**
     * 表结构：
     *  Table Name    weibo:receive_content_email
     *  RowKey    用户ID
     *  ColumnFamily  info
     *  ColumnLabel   被关注用户ID
     *  ColumnValue   被关注用户发微博时的RowKey
     *  Version   1000
     */
    public void createTableReceiveContentEmails() throws IOException {
        //获得连接
        Connection connection = getConnection();

        //admin
        Admin admin = connection.getAdmin();

        //创建
        if(!admin.tableExists(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL))) {
            HTableDescriptor weibo_receive_content_email = new HTableDescriptor(TableName.valueOf(WEIBO_RECEIVE_CONTENT_EMAIL));

            HColumnDescriptor info = new HColumnDescriptor("info");
            //指定最小版本、最大版本
            info.setMinVersions(1000);
            info.setMaxVersions(1000);
            info.setBlockCacheEnabled(true);

            weibo_receive_content_email.addFamily(info);
            admin.createTable(weibo_receive_content_email);
        }

        //关闭连接
        admin.close();
        connection.close();
    }

    /**
     * 创建用户关系表
     *  Table Name    weibo:relations
     *  RowKey    用户ID
     *  ColumnFamily  attends、fans
     *  ColumnLabel   关注用户ID，粉丝用户ID
     *  ColumnValue   用户ID
     *  Version   1个版本
     */
    public void createTableRelation() throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        if(!admin.tableExists(TableName.valueOf(WEIBO_RELATION))) {
            HTableDescriptor weibo_relation = new HTableDescriptor(TableName.valueOf(WEIBO_RELATION));
            //两个列族 attends
            //minversion\max\...
            HColumnDescriptor attends = new HColumnDescriptor("attends");
            attends.setMinVersions(1);
            attends.setMaxVersions(1);
            attends.setBlockCacheEnabled(true);

            //fans
            //minversion\max\...
            HColumnDescriptor fans = new HColumnDescriptor("fans");
            fans.setMinVersions(1);
            fans.setMaxVersions(1);
            fans.setBlockCacheEnabled(true);

            weibo_relation.addFamily(attends);
            weibo_relation.addFamily(fans);
            admin.createTable(weibo_relation);
        }

        admin.close();
        connection.close();
    }

    /**
     * rowkey: uid_timestamp
     * 列族：一个
     * 列名：pic, content, title
     * 版本：1个 默认
     * @throws IOException
     */
    public void createTableContent() throws IOException {
        //获得连接
        Connection connection = getConnection();

        //获得管理员对象，用于创建表
        Admin admin = connection.getAdmin();

        //创建表
        if(!admin.tableExists(TableName.valueOf(WEIBO_CONTENT))) {
            HTableDescriptor weibo_content = new HTableDescriptor(TableName.valueOf(WEIBO_CONTENT));

            HColumnDescriptor info = new HColumnDescriptor("info");
            //指定最小版本、最大版本
            info.setMinVersions(1);
            info.setMaxVersions(1);
            info.setBlockCacheEnabled(true);

            weibo_content.addFamily(info);

            admin.createTable(weibo_content);
        }

        //关闭连接
        admin.close();
        connection.close();
    }

    //建命名空间
    public void  createNameSpace() throws IOException {
        //获得连接
        Connection connection = getConnection();

        //生成Admin对象
        Admin admin = connection.getAdmin();

        //admin创建namespace
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create("weibo").addConfiguration("creator", "bruce").build();
        admin.createNamespace(namespaceDescriptor);

        //关闭连接
        admin.close();
        connection.close();
    }

    public Connection getConnection() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }
}
