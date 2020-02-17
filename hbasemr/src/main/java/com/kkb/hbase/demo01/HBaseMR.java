package com.kkb.hbase.demo01;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HBaseMR extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(super.getConf());
        job.setJarByClass(HBaseMR.class);

        /**
         * TableName table,
         *       Scan scan,
         *       Class<? extends TableMapper> mapper,
         *       Class<?> outputKeyClass,
         *       Class<?> outputValueClass,
         *       Job job
         */
        TableMapReduceUtil.initTableMapperJob(TableName.valueOf("myuser"), new Scan(), HBaseReaderMap.class, Text.class, Put.class, job);

        /**
         * String table,
         *     Class<? extends TableReducer> reducer, Job job
         */
        TableMapReduceUtil.initTableReducerJob("myuser2", HBaseWriterReduce.class, job);

        return job.waitForCompletion(true)? 0: 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");

        int run = ToolRunner.run(configuration, new HBaseMR(), args);
        System.exit(run);
    }
}
