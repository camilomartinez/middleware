package polimi.camilo.hadoop.LogAnalysis;

import java.util.regex.Matcher;

import org.apache.hadoop.io.Text;

public class ReferringDomainsMapper  extends BaseRegexMapper<Text> {
	private final String logPattern = "^([\\d.]+) \\S+ \\S+ \\[([\\w/]+):([\\w:]+\\s[+\\-]\\d{4})\\] \"(.+?html\\s.+?)\" (\\d{3}) (\\d+) \"(http[s]?://(?:www\\.)?(.+?)/[^\"]*)\" \"([^\"]+)\"";
	private int dateGroupNumber = 2;
	private int referrerGroupNumber = 7;
	private int domainGroupNumber = 8;
	private final String noReferrer = "-";
	private int numFields = 9;
	private final Text outputValue = new Text();
	
	public ReferringDomainsMapper()  {
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
		return OutputGroup(matcher, dateGroupNumber);
	}

	@Override
	protected Text MapperOutputValue(Matcher matcher) {
		String referrerUrl = matcher.group(referrerGroupNumber);
		String referrerDomain;		
		if (referrerUrl.equals(noReferrer)) {
			// Default referrer when there's none
			return null;
		} else {
			// Regex already match the domain
			referrerDomain = matcher.group(domainGroupNumber);
			outputValue.set(referrerDomain);
			return outputValue;
		}
	}

}
