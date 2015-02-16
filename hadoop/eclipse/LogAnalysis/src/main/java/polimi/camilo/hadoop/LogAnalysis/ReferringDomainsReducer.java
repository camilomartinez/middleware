package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReferringDomainsReducer extends Reducer<Text, Text, Text, IntWritable> {

	private IntWritable result = new IntWritable();
	
	@Override
	protected void reduce(Text word, Iterable<Text> domains, Context context)
			throws IOException, InterruptedException {
		HashSet<String> uniqueDomains = new HashSet<String>();
		for(Text domain: domains) {
			uniqueDomains.add(domain.toString());
		}
		int numberOfDomains = uniqueDomains.size();
		result.set(numberOfDomains);
		context.write(word, result);
	}

}
