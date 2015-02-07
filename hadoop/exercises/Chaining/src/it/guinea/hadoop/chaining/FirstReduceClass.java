package it.guinea.hadoop.chaining;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class FirstReduceClass extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		
    	int count = 0;
        while (values.hasNext()) {
            values.next();
            count++; 
        }
        String countStr = String.valueOf(count);
        output.collect(key, new Text(countStr));
    	
    	
    }
   }