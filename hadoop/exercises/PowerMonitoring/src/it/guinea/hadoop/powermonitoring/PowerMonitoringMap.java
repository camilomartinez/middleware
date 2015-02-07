package it.guinea.hadoop.powermonitoring;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class PowerMonitoringMap extends MapReduceBase implements Mapper<LongWritable, Text, HouseIdHourKey, MeasurementRecord> {

	private static long first;
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<HouseIdHourKey, MeasurementRecord> output,
			Reporter reporter) throws IOException {
		// TODO Auto-generated method stub
		
		//System.out.println("******************** Running Map with " + first);
		
		String[] values = value.toString().split(PowerMonitoringJob.INPUT_SEPARATOR);

		long timestamp = Long.parseLong(values[PowerMonitoringJob.TIMESTAMP_INDEX]);
		int houseId = Integer.parseInt(values[PowerMonitoringJob.HOUSE_ID_INDEX]);

		int householdId = Integer.parseInt(values[PowerMonitoringJob.HOUSEHOLD_ID_INDEX]);
		int plugId = Integer.parseInt(values[PowerMonitoringJob.PLUG_ID_INDEX]);
		int measure = Integer.parseInt(values[PowerMonitoringJob.MEASURE_INDEX]);
		
		int hourIndex = (int) ((timestamp - first) / PowerMonitoringJob.SECONDS_IN_A_HOUR);

		HouseIdHourKey mapKey = new HouseIdHourKey(houseId, hourIndex);
		MeasurementRecord record = new MeasurementRecord(householdId, plugId, measure);

		output.collect(mapKey, record);

	}
	
	public void configure(JobConf job) {
		first = job.getLong(PowerMonitoringJob.FIRST_TIMESTAMP,0);
	}

	
	

}
