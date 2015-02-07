package it.guinea.hadoop.temperature;


import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;


public class MeanTemperatureReducer extends MapReduceBase implements Reducer<RoomDayWritable, DoubleWritable, RoomDayWritable, DoubleWritable> {

	@Override
	public void reduce(RoomDayWritable key, Iterator<DoubleWritable> values,
			OutputCollector<RoomDayWritable, DoubleWritable> output, Reporter reporter)
			throws IOException {
	
		double sum = 0;
		int counter = 0;
		while (values.hasNext()) {
			DoubleWritable dw  = values.next();
			sum += dw.get();
			++counter;
		}
		output.collect(key, new DoubleWritable(sum / counter));
		
		
	} 
}
