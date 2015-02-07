package it.guinea.hadoop.chaining;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SecondMapClass extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, IntWritable> {
        
		private final static IntWritable uno = new IntWritable(1);
        private IntWritable citationCount = new IntWritable();
        
        public void map(LongWritable key, Text value, OutputCollector<IntWritable, IntWritable> output, Reporter reporter) throws IOException {
            String valueStr = value.toString();
            String[] splits = valueStr.split("\\s+");
            //System.out.println("Splits (" + splits[0] +") -> " + splits[1]);
        	citationCount.set(Integer.parseInt(splits[1]));
            output.collect(citationCount, uno);
        }
}