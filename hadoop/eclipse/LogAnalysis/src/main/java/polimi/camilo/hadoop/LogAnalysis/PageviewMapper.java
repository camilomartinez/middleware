package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageviewMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	private final IntWritable one = new IntWritable(1);
	private Text word = new Text();
	
	public PageviewMapper()  {
		System.out.println("Init WordCount Mapper");
		}

	@Override
	protected void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
	
		StringTokenizer itr = new StringTokenizer(value.toString());
		while( itr.hasMoreTokens()){
			word.set(itr.nextToken());
			context.write(word, one);
		}
		
	}

}
