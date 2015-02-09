package polimi.camilo.hadoop.LogAnalysis;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {

	public WordCount() {
		System.out.println("Init WordCount");
	}

	public static void main(String[] args) throws Exception{
		//JobClient client  = new JobClient();
		Configuration conf = new Configuration();

		Job job = new Job(conf, "word count");
		String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
		job.setCombinerClass(WordCountReducer.class);
		job.setReducerClass(WordCountReducer.class);
		job.setMapperClass(WordCountMapper.class);
		job.setJarByClass(WordCount.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0: 1);
		
		
		
	}
}
