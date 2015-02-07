package it.guinea.hadoop.powermonitoring;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;



public class PowerMonitoringReduce extends MapReduceBase
		implements
		Reducer<HouseIdHourKey, MeasurementRecord, Text, DoubleWritable> {

	private static long first;
	
	@Override
	public void reduce(HouseIdHourKey key, Iterator<MeasurementRecord> values,
			OutputCollector<Text, DoubleWritable> output, Reporter reporter)
			throws IOException {

				LinkedList<MeasurementRecord> list = new LinkedList<MeasurementRecord>();
				while (values.hasNext()) {
					//System.out.println();
					MeasurementRecord r = values.next();
					list.add(new MeasurementRecord(r.householdId, r.plugId, r.measure));
					
				}
				Collections.sort(list);

				Map<PlugId, LinkedList<MeasurementRecord>> map = new HashMap<PlugId, LinkedList<MeasurementRecord>>();
				for (MeasurementRecord rec : list) {
					//System.out.println("Structuring: "+rec);
					PlugId id = new PlugId(rec.householdId, rec.plugId);
					if (map.containsKey(id)) {
						//System.out.println("registering measure:"+rec);
						map.get(id).addLast(rec);
					} else {
						//System.out.println("NEW plug: "+rec.householdId+","+rec.plugId);
						//System.out.println("registering measure:"+rec);
						LinkedList<MeasurementRecord> newlist = new LinkedList<MeasurementRecord>();
						newlist.addLast(rec);
						map.put(id, newlist);
					}
				}

				int overallMedianValue = computeMedianValue(list);
				System.out.printf("OVERALL MEDIAN VALUE IS:%d\n", overallMedianValue);
				int numOfPlugs = map.entrySet().size();
				System.out.printf("NUMBER OF PLUGS IS:%d\n",numOfPlugs);
				int numOfOutliers = 0;
				for (LinkedList<MeasurementRecord> pluglist : map.values()) {
					int medianplug = computeMedianValue(pluglist);
					//System.out.println("medianplug:"+medianplug);
					if (medianplug > overallMedianValue) {
						//System.out.println("incrementing outliers...");
						numOfOutliers++;
					}
				}

				long timestamp = first + key.hourIndex * PowerMonitoringJob.SECONDS_IN_A_HOUR;
				Date date = new Date(timestamp*1000);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				
				output.collect(new Text(key.houseId + PowerMonitoringJob.INPUT_SEPARATOR + df.format(date)), new DoubleWritable((double)numOfOutliers / numOfPlugs * 100));

			}
	
			public void configure(JobConf job) {
				first = job.getLong(PowerMonitoringJob.FIRST_TIMESTAMP,0);
			}

			private int computeMedianValue(LinkedList<MeasurementRecord> values) {
				return values.size() % 2 != 0 
						? values.get(values.size() / 2).measure
						: ((values.get(values.size() / 2).measure + 
								values.get((values.size() / 2) - 1).measure) 
								/ 2);
			}

			public class PlugId {
				
				public PlugId(int householdId, int plugId) {
					super();
					this.householdId = householdId;
					this.plugId = plugId;
				}

				int householdId;

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + getOuterType().hashCode();
					result = prime * result + householdId;
					result = prime * result + plugId;
					return result;
				}

				@Override
				public boolean equals(Object obj) {
					if (this == obj)
						return true;
					if (obj == null)
						return false;
					if (getClass() != obj.getClass())
						return false;
					PlugId other = (PlugId) obj;
					if (!getOuterType().equals(other.getOuterType()))
						return false;
					if (householdId != other.householdId)
						return false;
					if (plugId != other.plugId)
						return false;
					return true;
				}

				int plugId;

				private PowerMonitoringReduce getOuterType() {
					return PowerMonitoringReduce.this;
				}
				
			}
}
