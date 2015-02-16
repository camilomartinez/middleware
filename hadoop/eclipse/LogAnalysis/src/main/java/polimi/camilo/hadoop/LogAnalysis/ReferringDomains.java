package polimi.camilo.hadoop.LogAnalysis;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class ReferringDomains {

	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);
		job.setJobName("referring domains");
		job.setJarByClass(ReferringDomains.class);		
		
		job.setOutputKeyClass(Text.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setMapperClass(ReferringDomainsMapper.class);
		job.setReducerClass(ReferringDomainsReducer.class);	
		
		String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		
		System.exit(job.waitForCompletion(true) ? 0: 1);	
	}
}
