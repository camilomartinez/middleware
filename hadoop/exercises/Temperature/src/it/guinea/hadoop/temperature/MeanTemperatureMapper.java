package it.guinea.hadoop.temperature;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class MeanTemperatureMapper extends MapReduceBase implements Mapper<LongWritable, Text, RoomDayWritable, DoubleWritable> {

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<RoomDayWritable, DoubleWritable> output, Reporter reporter)
			throws IOException {
	
		String line = value.toString();
		String[] parts = line.split("\\s+");
		if (parts.length < 3) {
			return;
		}
		long timestamp = Long.parseLong(parts[0]) * 1000;
		int mote = Integer.parseInt(parts[1]);
		if (mote != 18 && mote != 31) {
			return;
		}
		int temperatureInt = Integer.parseInt(parts[2]);
		double temperature = temperatureInt / 100.0;
		Date date = new Date(timestamp);
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String dateKey = df.format(date);
		output.collect(new RoomDayWritable(dateKey, mote), new DoubleWritable(temperature));
		
	}



	

}
