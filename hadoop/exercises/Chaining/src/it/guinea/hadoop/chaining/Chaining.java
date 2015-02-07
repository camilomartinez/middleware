package it.guinea.hadoop.chaining;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Chaining extends Configured implements Tool {
	
	private static String intermediate = "/temp";

	@Override
	public int run(String[] arg0) throws Exception {
		
		Configuration conf = getConf();

        JobConf jobConf1 = new JobConf(conf, Chaining.class);
        jobConf1.setJobName("Job1");
        
        Path in = new Path(arg0[0]); //input/cite75_99.txt
        Path out = new Path(intermediate);//temp     
        FileInputFormat.setInputPaths(jobConf1, in);
        FileOutputFormat.setOutputPath(jobConf1, out);

        
        jobConf1.setMapperClass(FirstMapClass.class);
        jobConf1.setReducerClass(FirstReduceClass.class);

        jobConf1.setInputFormat(KeyValueTextInputFormat.class);
        jobConf1.set("key.value.separator.in.input.line", ",");
        
        jobConf1.setOutputFormat(TextOutputFormat.class);
        jobConf1.setOutputKeyClass(Text.class);
        jobConf1.setOutputValueClass(Text.class);
        
        JobConf jobConf2 = new JobConf(conf, Chaining.class);
        jobConf2.setJobName("CitationHistogram");
        
	    Path in2 = new Path(intermediate); //temp
	    Path out2 = new Path(arg0[1]); //output
	    FileInputFormat.setInputPaths(jobConf2, in2);
	    FileOutputFormat.setOutputPath(jobConf2, out2);
	   
	    jobConf2.setMapperClass(SecondMapClass.class);
	    jobConf2.setReducerClass(SecondReduceClass.class);
	    
	    jobConf2.setInputFormat(TextInputFormat.class);
	    jobConf2.setOutputFormat(TextOutputFormat.class);
	    
	    jobConf2.setOutputKeyClass(IntWritable.class);
	    jobConf2.setOutputValueClass(IntWritable.class);

        Job job1 = new Job(jobConf1);
        Job job2 = new Job(jobConf2);
        JobControl jobControl = new JobControl("chaining");
        jobControl.addJob(job1);
        jobControl.addJob(job2);
        job2.addDependingJob(job1);
        
        Thread t = new Thread(jobControl); 
        t.setDaemon(true);
        t.start(); 
                      
        while (!jobControl.allFinished()) { 
          try { 
            Thread.sleep(1000); 
          } catch (InterruptedException e) { 
            // Ignore. 
          } 
        } 
        
        return 0;
	}
	
	public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new Chaining(), args);
        System.exit(res);
    }
 

}
