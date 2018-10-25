/*

 Two inputs:
 1> Title.ID files
 2> ID Frequency files (output of TopN job)

 One output:
 - Joined output of 1> and 2> by ID

*/

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Math;

public class JoinIDTitle
{
    public static void main(String[] args) throws Exception {

      Job pass = new Job();
      Configuration conf = pass.getConfiguration();

      String titleDocID = args[0]; // main 함수의 첫번째 인자
      String docIDFreq = args[1]; // main 함수의 두번째 인자
      String outputDir = args[2]; // 출력 디렉토리

      if (outputDir == null || titleDocID == null || docIDFreq == null) {
        throw new IllegalArgumentException("Missing Parameters");
      }

      pass.setJobName("Join ID and Title");
      pass.setJarByClass(JoinIDTitle.class);

      pass.setOutputKeyClass(Text.class);
      pass.setOutputValueClass(Text.class);

      // note that this setMapper call is omitted here
      //pass.setMapperClass(MyMapper.class);
      pass.setReducerClass(MyReducer.class);

      MultipleInputs.addInputPath(pass, new Path(titleDocID), KeyValueTextInputFormat.class, MyMapper1.class);
      MultipleInputs.addInputPath(pass, new Path(docIDFreq), KeyValueTextInputFormat.class, MyMapper2.class);

      // default outputformat is TextOutputFormat 
      FileOutputFormat.setOutputPath(pass, new Path(outputDir));

      if(!pass.waitForCompletion(true))
        System.exit(1);
    }

    // title + docID input file handling
    // tag this with 1
    public static class MyMapper1 extends Mapper<Text, Text, Text, Text> {

      @Override
      protected void map(Text key, Text value, final Context context) throws IOException, InterruptedException {
        context.write(value, new Text(key + "\t" + 1));
        context.getCounter("Stats", "Number of Title+DocID").increment(1);
      }
    }

    // DocID + Number of Citation
    // tag this with 2
    public static class MyMapper2 extends Mapper<Text, Text, Text, Text> {

      @Override
      protected void map(Text key, Text value, final Context context) throws IOException, InterruptedException {
        context.write(key, new Text(value + "\t" + 2));
        context.getCounter("Stats", "DocID+Citation").increment(1);
      }
    }

    public static class MyReducer extends Reducer<Text, Text, Text, Text> {

      @Override
      public void reduce(Text key, Iterable<Text> valueIter, final Context context) throws IOException, InterruptedException {

        String title = null;
        String frequency = null;
        int count = 0;

        for(Text t: valueIter) {
          String str = t.toString();
          String []tokens = str.split("\\t");
          if (tokens[1].equals("1")) {
            title = tokens[0];
          }
          else {
            frequency = tokens[0];
          } 
          count++;
        }

        if (count == 2 && title != null && frequency != null) {
            context.write(key, new Text(title + "\t" + frequency));
            context.getCounter("Stats", "The number of pairs of matches").increment(1);
        }
      }
    }
}
