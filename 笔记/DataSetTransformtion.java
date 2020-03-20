package com.example.demo.test;

import ch.qos.logback.core.db.dialect.DBUtil;
import org.apache.commons.dbutils.DbUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

public class DataSetTransformtion {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();
        //mapFunction(executionEnvironment);
        //filterFunction(executionEnvironment);
       // firstFunction(executionEnvironment);
        //flatMapFunction(executionEnvironment);
        distinctFunction(executionEnvironment);
    }

    /**
     * 对集合元素操作
     *
     * @param executionEnvironment
     * @throws Exception
     */
    private static void mapFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        DataSource<Integer> integerDataSource = executionEnvironment.fromCollection(list);
        integerDataSource.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {
                //遍历list中元素 +1
                return value + 1;
            }
        }).print();
    }

    /**
     * 过滤元素
     *
     * @param executionEnvironment
     * @throws Exception
     */
    private static void filterFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        DataSource<Integer> integerDataSource = executionEnvironment.fromCollection(list);
        integerDataSource.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {
                //遍历list中元素 +1
                return value + 1;
            }
        }).filter(new FilterFunction<Integer>() {
            @Override
            public boolean filter(Integer value) throws Exception {
                //过滤掉小于5的元素
                return value > 5;
            }
        }).print();
    }

    /**
     * 数据写入数据库
     *
     * @param executionEnvironment
     * @throws Exception
     */
    private static void mapPartitionFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("student: " + i);
        }
        //设置并行度6
        DataSource<String> integerDataSource = executionEnvironment.fromCollection(list).setParallelism(6);
//        integerDataSource.map(new MapFunction<String, String>() {  //输入 String 输出String
//            @Override
//            public String map(String value) throws Exception {
//                //模拟写入数据库
//                String connection = DBUtils.getConnection();
//                System.out.println("connection = [" + value + " ]");
//                DBUtils.returnConnection(connection);
//                return value;
//            }
//        }).print();
//        integerDataSource.mapPartition(new MapPartitionFunction<String, Object>() {
//            @Override
//            public void mapPartition(Iterable<String> values, Collector<Object> out) throws Exception {
//                String connection= DBUtils.getConnetion();
//                System.out.println("connection = ["+connection+" ]");
//                DBUtils.returnConnection(connection);
//            }
//        }).print();
    }

    /**
     * 取集合中前几个值
     *
     * @param executionEnvironment
     * @throws Exception
     */
    private static void firstFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<Tuple2<Integer, String>> list = new ArrayList<>();
        list.add(new Tuple2<>(1, "Hadoop"));
        list.add(new Tuple2<>(1, "Spark"));
        list.add(new Tuple2<>(1, "Flink"));
        list.add(new Tuple2<>(2, "Java"));
        list.add(new Tuple2<>(2, "Spring Boot"));
        list.add(new Tuple2<>(3, "Linux"));
        list.add(new Tuple2<>(4, "VUE"));

        //设置并行度6
        DataSource<Tuple2<Integer, String>> integerDataSource = executionEnvironment.fromCollection(list).setParallelism(1);
        //按照顺序取集合中前3个打印
        integerDataSource.first(3).print();
        //(1,Hadoop)
        //(1,Spark)
        //(1,Flink)
        System.out.println(" - - - - - ");

        //先根据 Integer分组，然后取分组后前两个
        integerDataSource.groupBy(0).first(2).print();
        //(3,Linux)
        //(1,Hadoop)
        //(1,Spark)
        //(2,Java)
        //(2,Spring Boot)
        //(4,VUE)
        System.out.println(" --------");
        // 分组后，降序排列，取前两个
        integerDataSource.groupBy(0).sortGroup(1, Order.DESCENDING).first(2).print();

        //(3,Linux)
        //(1,Spark)
        //(1,Hadoop)
        //(2,Spring Boot)
        //(2,Java)
        //(4,VUE)
        System.out.println(" -    -  ");
    }

    private static void flatMapFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("hadoop,spark");
        list.add("hadoop,flink");
        list.add("flink,flink");
        DataSource<String> integerDataSource = executionEnvironment.fromCollection(list);
        integerDataSource.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                //分割数据
                String[] split = value.split(",");
                for (String s : split) {
                    //将分割后元素放到集合中
                    out.collect(s);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            //输入string ,输出tuple
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                // 给每个单词赋值1
                return new Tuple2<>(value, 1);
            }
        }).groupBy(0).sum(1).print(); //按照单词分组，记总和
        //(hadoop,2)
        //(flink,3)
        //(spark,1)
    }

    private static void distinctFunction(ExecutionEnvironment executionEnvironment) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("hadoop,spark");
        list.add("hadoop,flink");
        list.add("flink,flink");
        DataSource<String> integerDataSource = executionEnvironment.fromCollection(list);
        integerDataSource.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                //分割数据
                String[] split = value.split(",");
                for (String s : split) {
                    //将分割后元素放到集合中
                    out.collect(s);
                }
            }
        }).distinct().print(); //去重输出
        //hadoop
        //flink
        //spark
    }

}
