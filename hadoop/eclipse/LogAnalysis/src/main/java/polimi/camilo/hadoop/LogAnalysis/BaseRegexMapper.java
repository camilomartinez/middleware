package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public abstract class BaseRegexMapper<T> extends Mapper<LongWritable, Text, Text, T> {
	
	private final Text textOutput = new Text();
	private final LongWritable oneWritable = new LongWritable(1);
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		String logEntry = value.toString();
		Pattern pattern = Pattern.compile(LogPattern());
	    Matcher matcher = pattern.matcher(logEntry);
	    // Pattern doesn't match
	    if (!matcher.matches()) {
	    	//System.out.println("Omitting entry:");
			//System.out.println(logEntry);
			return;
		}
		if (NumberOfFields() != matcher.groupCount()) {
			//System.out.println("Bad log entry (or problem with RE?):");
			//System.out.println(logEntry);
			return;
		}
	    Text outputKey = MapperOutputKey(matcher);
	    T outputValue = MapperOutputValue(matcher);
	    // Null is used to avoid writing output
	    if (outputKey != null && outputValue != null) {
	    	context.write(outputKey, outputValue);
	    }
	}
	
	protected abstract String LogPattern();
	protected abstract int NumberOfFields();
	protected abstract Text MapperOutputKey(Matcher matcher);
	protected abstract T MapperOutputValue(Matcher matcher);
	
	// Some base implementations
	protected Text OutputGroup(Matcher matcher, int groupNumber) {
		String groupMatch = matcher.group(groupNumber);
	    textOutput.set(groupMatch);
	    return textOutput;
	}
	
	protected LongWritable OutputOne() {
		return oneWritable;
	}
}
