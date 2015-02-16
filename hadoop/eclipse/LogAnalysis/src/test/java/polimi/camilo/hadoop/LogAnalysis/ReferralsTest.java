package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class ReferralsTest {
	//Some test entries
	private final String TestEntry1 = "69.10.137.199 - - [29/Apr/2003:16:45:01 -0700] \"GET /watch-info HTTP/1.0\" 200 77 \"-\" \"-\"";
	private final String TestEntry2 = "66.137.118.69 - - [29/Apr/2003:06:58:22 -0800] \"GET /archive/2003/02/01/space_sh.shtml HTTP/1.1\" 200 11966 \"http://www.google.com/search?sourceid=navclient&q=%22next+time+Columbia+flies+will+be+in+November%22\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\"";
	private final String TestEntry3 = "65.214.36.115 - - [29/Apr/2003:00:59:56 -0700] \"GET /archive/2002/11/15/cojag_ga.shtml HTTP/1.0\" 200 16620 \"-\" \"Mozilla/2.0 (compatible; Ask Jeeves/Teoma)\"";	
	private final String TestEntry4 = "205.156.184.254 - - [29/Apr/2003:07:55:00 -0800] \"GET /random/text/1995_warez_bbs_list.html HTTP/1.1\" 200 529730 \"http://www.cfox.com/station/geeks.cfm\" \"Mozilla/4.0 (compatible; MSIE 5.01; Windows 98; Marsh USA Inc. July 2000 build)\"";
	private final String TestEntry5 = "62.98.76.35 - - [10/Apr/2003:01:07:24 -0700] \"GET /archive/2003/03/26/hiding_s.shtml HTTP/1.1\" 200 17019 \"http://65.54.244.250/cgi-bin/linkrd?_lang=IT&lah=d3458b4f8013fab336193e0e641848a2&lat=1049960696&hm___action=http%3a%2f%2fwww%2ewaxy%2eorg%2farchive%2f2003%2f03%2f26%2fhiding_s%2eshtml%23001203\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)\"";
	private final String TestEntry6 = "62.98.76.35 - - [05/May/2003:01:07:24 -0700] \"GET /archive/2003/03/26/hiding_s.shtml HTTP/1.1\" 200 17019 \"http://65.54.244.250/cgi-bin/linkrd?_lang=IT&lah=d3458b4f8013fab336193e0e641848a2&lat=1049960696&hm___action=http%3a%2f%2fwww%2ewaxy%2eorg%2farchive%2f2003%2f03%2f26%2fhiding_s%2eshtml%23001203\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)\"";
	private final String TestEntry7 = "62.98.76.35 - - [31/May/2003:01:07:24 -0700] \"GET /archive/2003/03/26/hiding_s.shtml HTTP/1.1\" 200 17019 \"http://65.54.244.250/cgi-bin/linkrd?_lang=IT&lah=d3458b4f8013fab336193e0e641848a2&lat=1049960696&hm___action=http%3a%2f%2fwww%2ewaxy%2eorg%2farchive%2f2003%2f03%2f26%2fhiding_s%2eshtml%23001203\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)\"";
	private final String TestEntry8 = "62.98.76.35 - - [01/Feb/2003:01:07:24 -0700] \"GET /archive/2003/03/26/hiding_s.shtml HTTP/1.1\" 200 17019 \"http://65.54.244.250/cgi-bin/linkrd?_lang=IT&lah=d3458b4f8013fab336193e0e641848a2&lat=1049960696&hm___action=http%3a%2f%2fwww%2ewaxy%2eorg%2farchive%2f2003%2f03%2f26%2fhiding_s%2eshtml%23001203\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)\"";
	private final String TestEntry9 = "62.98.76.35 - - [10/Jun/2003:01:07:24 -0700] \"GET /archive/2003/03/26/hiding_s.shtml HTTP/1.1\" 200 17019 \"http://65.54.244.250/cgi-bin/linkrd?_lang=IT&lah=d3458b4f8013fab336193e0e641848a2&lat=1049960696&hm___action=http%3a%2f%2fwww%2ewaxy%2eorg%2farchive%2f2003%2f03%2f26%2fhiding_s%2eshtml%23001203\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)\"";
	
	MapDriver<LongWritable, Text, Text, LongWritable> mapDriver;
	MapReduceDriver<LongWritable, Text, Text, LongWritable, Text, LongWritable> mapReduceDriver;	
	
	@Before
	public void setup(){
		ReferralsMapper mapper =  new ReferralsMapper();
		LongSumReducer<Text> reducer = new LongSumReducer<Text>();
		
		mapDriver = new MapDriver<LongWritable, Text, Text, LongWritable>();
		mapDriver.setMapper(mapper);
		mapReduceDriver = new MapReduceDriver<LongWritable, Text, Text, LongWritable, Text, LongWritable>();
		mapReduceDriver.setMapper(mapper);
		mapReduceDriver.setReducer(reducer);
	}
	
	@Test
	public void testMapper() throws IOException{
		mapDriver.withInput(new LongWritable(1), new Text(TestEntry1));
		mapDriver.withInput(new LongWritable(2), new Text(TestEntry2));
		mapDriver.withInput(new LongWritable(3), new Text(TestEntry3));
		mapDriver.withInput(new LongWritable(4), new Text(TestEntry4));
		mapDriver.withInput(new LongWritable(5), new Text(TestEntry5));
		mapDriver.withInput(new LongWritable(6), new Text(TestEntry6));
		mapDriver.withInput(new LongWritable(7), new Text(TestEntry7));
		mapDriver.withInput(new LongWritable(8), new Text(TestEntry8));
		mapDriver.withInput(new LongWritable(9), new Text(TestEntry9));		
		mapDriver.withOutput(new Text("google.com"), new LongWritable(1));
	    mapDriver.withOutput(new Text("cfox.com"), new LongWritable(1));
	    mapDriver.withOutput(new Text("65.54.244.250"), new LongWritable(1));	    
	    mapDriver.runTest();
	}
	
	@Test
	public void testMapReduce() throws IOException{
		mapReduceDriver.withInput(new LongWritable(1), new Text(TestEntry1));
		mapReduceDriver.withInput(new LongWritable(2), new Text(TestEntry2));
		mapReduceDriver.withInput(new LongWritable(3), new Text(TestEntry2));
		mapReduceDriver.withInput(new LongWritable(4), new Text(TestEntry3));
		mapReduceDriver.withInput(new LongWritable(5), new Text(TestEntry4));
		mapReduceDriver.withInput(new LongWritable(6), new Text(TestEntry5));
		mapReduceDriver.withInput(new LongWritable(7), new Text(TestEntry6));
		mapReduceDriver.withInput(new LongWritable(8), new Text(TestEntry7));
		mapReduceDriver.withInput(new LongWritable(9), new Text(TestEntry8));
		mapReduceDriver.withInput(new LongWritable(10), new Text(TestEntry9));
		mapReduceDriver.withOutput(new Text("65.54.244.250"), new LongWritable(1));	    
	    mapReduceDriver.withOutput(new Text("cfox.com"), new LongWritable(1));
	    mapReduceDriver.withOutput(new Text("google.com"), new LongWritable(2));
		mapReduceDriver.runTest();
	}
}
