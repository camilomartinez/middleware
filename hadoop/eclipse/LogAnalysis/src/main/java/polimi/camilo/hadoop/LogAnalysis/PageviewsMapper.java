package polimi.camilo.hadoop.LogAnalysis;

import java.util.regex.Matcher;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class PageviewsMapper extends BaseRegexMapper<LongWritable> {
	private final String logPattern = "^([\\d.]+) \\S+ \\S+ \\[([\\w/]+):([\\w:]+\\s[+\\-]\\d{4})\\] \"(.+?html\\s.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\"";
	private int dateGroupNumber = 2; 
	private int numFields = 8;
	
	@Override
	protected String LogPattern() {
		return logPattern;
	}

	@Override
	protected int NumberOfFields() {
		return numFields;
	}

	@Override
	protected Text MapperOutputKey(Matcher matcher) {
		return OutputGroup(matcher, dateGroupNumber);
	}

	@Override
	protected LongWritable MapperOutputValue(Matcher matcher) {
		return OutputOne();
	}

}
