package it.guinea.hadoop.temperature;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class MeanTemperature extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		
		
		Configuration conf = getConf();
		
        JobConf job = new JobConf(conf, MeanTemperature.class);
        job.setJobName("MeanTemperature");
        
        FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        job.setMapperClass(MeanTemperatureMapper.class);
        job.setReducerClass(MeanTemperatureReducer.class);
        
        job.setInputFormat(TextInputFormat.class);
        
        job.setOutputFormat(TextOutputFormat.class);
        job.setOutputKeyClass(RoomDayWritable.class);
		job.setOutputValueClass(DoubleWritable.class);

		JobClient.runJob(job);
		
		
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Configuration(), new MeanTemperature(), args);
		System.exit(exitCode);
	}

}
