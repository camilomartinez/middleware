import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Reduce extends MapReduceBase implements Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {

	@Override
	public void reduce(IntWritable key, Iterator<IntWritable> values,
			OutputCollector<IntWritable, IntWritable> output, Reporter reporte)
			throws IOException {
		// TODO Auto-generated method stub
		int count = 0;
	    while (values.hasNext()) {
	        count += values.next().get();
	    }
	    output.collect(key, new IntWritable(count));
		
	}


}