import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.GenericOptionsParser;
        
public class CountTrigram {
        
 public static class Map extends Mapper<Text, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text trigram = new Text();
        
    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();

      StringTokenizer tokenizer = new StringTokenizer(line, "\t\r\n\f |,!#\"$.'%&=+-_^@`~:?<>(){}[];*/");
      if (tokenizer.countTokens() >= 3) {
        String firstToken = tokenizer.nextToken().toLowerCase();
        String secondToken = tokenizer.nextToken().toLowerCase();

        while (tokenizer.hasMoreTokens()) {
            String thirdToken = tokenizer.nextToken().toLowerCase();
            trigram.set(firstToken + " " + secondToken + " " + thirdToken);
            context.write(trigram, one);

            firstToken = secondToken;
            secondToken = thirdToken;
        }
      }
    }
 } 
        
 public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        context.write(key, new IntWritable(sum));
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    Job job = new Job(conf, "Count Trigram");
   
    job.setJarByClass(CountTrigram.class); 
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(Map.class);
    job.setCombinerClass(Reduce.class);
    job.setReducerClass(Reduce.class);

    job.setInputFormatClass(KeyValueTextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    if (!job.waitForCompletion(true))
        return;

    Configuration conf2 = new Configuration();
    Job job2 = new Job(conf2, "Top N");

    job2.setJarByClass(TopN.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(LongWritable.class);

    job2.setMapperClass(TopN.Map.class);
    job2.setReducerClass(TopN.Reduce.class);
    job2.setNumReduceTasks(1);

    job2.setInputFormatClass(KeyValueTextInputFormat.class);
    job2.setOutputFormatClass(TextOutputFormat.class);

    // input of Job2 is output of Job
    FileInputFormat.addInputPath(job2, new Path(args[1]));
    FileOutputFormat.setOutputPath(job2, new Path(args[1] + "//topN"));
    job2.getConfiguration().setInt("topN", 10);

    if (!job2.waitForCompletion(true))
        return;
 }
        
}
