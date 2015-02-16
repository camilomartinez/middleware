package polimi.camilo.hadoop.LogAnalysis;

import java.util.regex.Matcher;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class VideoDownloadsMapper extends BaseRegexMapper<LongWritable> {
	private final String logPattern = "^([\\d.]+) \\S+ \\S+ \\[([\\w/]+):([\\w:]+\\s[+\\-]\\d{4})\\] \"(.+?Star_Wars_Kid(?:_Remix)?\\.wmv\\s.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\"";
	private int outputKeyGroupNumber = 2; 
	private int numFields = 8;
	
	public VideoDownloadsMapper()  {
		System.out.println("Init Video Downloads Mapper");
		}

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
		return OutputGroup(matcher, outputKeyGroupNumber);
	}

	@Override
	protected LongWritable MapperOutputValue(Matcher matcher) {
		return OutputOne();
	}

}
