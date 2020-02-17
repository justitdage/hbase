package student.zhuye;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class Hbase_and_MR extends Configured implements Tool {

    @Override
    public int run(String[] strings) throws Exception {

        Job job = Job.getInstance(super.getConf());
        job.setJarByClass(Hbase_and_MR.class);
        //使用TableMapReduceUtil 工具类来初始化我们的mapper
        TableMapReduceUtil.initTableMapperJob(TableName.valueOf("myuser"), new Scan(),HbaseCopyDataMapper.class,Text.class,Put.class,job);
        //使用TableMapReduceUtil 工具类来初始化我们的reducer
        TableMapReduceUtil.initTableReducerJob( "myuser2",HbaseReducer.class,job);

        boolean b = job.waitForCompletion(true) ;
        return b?0:1;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        int result = ToolRunner.run(conf,new Hbase_and_MR(),args);
        System.exit(result);
    }

    public static class HbaseCopyDataMapper extends TableMapper<Text, Put>{
         @Override
         protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
             //获取rowkey的字节数组
             byte[] bytes = key.get();
             String rowkey = Bytes.toString(bytes);
             //构建一个put对象
             Put put = new Put(bytes);
             //获取一行中所有的cell对象
             Cell[] cells = value.rawCells();
             for (Cell cell : cells) {
                 // f1列族
                 if("f1".equals(Bytes.toString(CellUtil.cloneFamily(cell)))){
                     // name列名
                     if("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))){
                         put.add(cell);
                     }
                     // age列名
                     if("age".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))){
                         put.add(cell);
                     }
                 }
             }
             if(!put.isEmpty()){
                 context.write(new Text(rowkey),put);
             }
         }
     }

    public  static  class HbaseReducer extends TableReducer<Text,Put,ImmutableBytesWritable> {
        @Override
        protected void reduce(Text key, Iterable<Put> values, Context context) throws IOException, InterruptedException {
            ImmutableBytesWritable immutableBytesWritable = new ImmutableBytesWritable();
            immutableBytesWritable.set(key.toString().getBytes());

            for (Put put : values) {
                context.write(immutableBytesWritable,put);
            }
        }
    }


}
