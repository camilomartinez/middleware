import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class MapClass extends MapReduceBase implements Mapper<Text, Text, IntWritable, IntWritable> {
        
		private final static IntWritable uno = new IntWritable(1);
        private IntWritable citationCount = new IntWritable();
        
        public void map(Text key, Text value, OutputCollector<IntWritable, IntWritable> output, Reporter reporter) throws IOException {
            citationCount.set(Integer.parseInt(value.toString()));
            output.collect(citationCount, uno);
        }
}