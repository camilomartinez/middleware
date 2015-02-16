package polimi.camilo.hadoop.LogAnalysis;

import java.util.regex.Matcher;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class ReferralsMapper extends BaseRegexMapper<LongWritable> {
	private final String logPattern = "^([\\d.]+) \\S+ \\S+ \\[([\\w/]+):([\\w:]+\\s[+\\-]\\d{4})\\] \"(.+?html\\s.+?)\" (\\d{3}) (\\d+) \"(http[s]?://(?:www\\.)?(.+?)/[^\"]*)\" \"([^\"]+)\"";
	private int dateGroupNumber = 2;
	private int referrerGroupNumber = 7;
	private int domainGroupNumber = 8;
	private final String noReferrer = "-";
	private int numFields = 9;
	private final Text outputKey = new Text();
	
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
		String dateString = matcher.group(dateGroupNumber);
		String[] dateSplit = dateString.split("/");
		int day = Integer.parseInt(dateSplit[0]);
		String month = dateSplit[1];
		// Simple way to check if date is between 22 of April and 30 May
		if ((month.equals("Apr") && day >= 22) || (month.equals("May") && day <= 30)) {
			String referrerUrl = matcher.group(referrerGroupNumber);
			String referrerDomain;		
			if (!referrerUrl.equals(noReferrer)) {
				// Only if there's any referrer set the domain as key
				referrerDomain = matcher.group(domainGroupNumber);
				outputKey.set(referrerDomain);
				return outputKey;
			}
		}
		return null;
	}

	@Override
	protected LongWritable MapperOutputValue(Matcher matcher) {
		return OutputOne();
	}

}
