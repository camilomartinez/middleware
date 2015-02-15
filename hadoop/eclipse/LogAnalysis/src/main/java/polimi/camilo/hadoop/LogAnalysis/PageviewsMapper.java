package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageviewsMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
	private final String LogPattern = "^([\\d.]+) \\S+ \\S+ \\[([\\w/]+):([\\w:]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\"";
	private int NUM_FIELDS = 8;
	
	private final LongWritable one = new LongWritable(1);
	private Text dateText = new Text();
	
	public PageviewsMapper()  {
		System.out.println("Init Pageviews Mapper");
		}

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		String logEntry = value.toString();
		Pattern pattern = Pattern.compile(LogPattern);
	    Matcher matcher = pattern.matcher(logEntry);
	    // Pattern matching error
		if (!matcher.matches() || NUM_FIELDS != matcher.groupCount()) {
			System.err.println("Bad log entry (or problem with RE?):");
			System.err.println(logEntry);
			return;
		}
	    String dateEntry = matcher.group(2);
	    dateText.set(dateEntry);
	    context.write(dateText, one);
	}

}
