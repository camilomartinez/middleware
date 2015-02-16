package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class PageviewsTest {
	//Some test entries
	private final String TestEntry1 = "69.10.137.199 - - [29/Apr/2003:16:45:01 -0700] \"GET /watch-info HTTP/1.0\" 200 77 \"-\" \"-\"";
	private final String TestEntry2 = "66.137.118.69 - - [29/Apr/2003:06:58:22 -0800] \"GET /archive/2003/02/01/space_sh.shtml HTTP/1.1\" 200 11966 \"http://www.google.com/search?sourceid=navclient&q=%22next+time+Columbia+flies+will+be+in+November%22\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\"";
	private final String TestEntry3 = "205.156.184.254 - - [19/Feb/2003:07:55:00 -0800] \"GET /random/text/1995_warez_bbs_list.html HTTP/1.1\" 200 529730 \"http://www.google.com/search?q=fist+punch+clipart&hl=en&lr=&ie=UTF-8&start=20&sa=N\" \"Mozilla/4.0 (compatible; MSIE 5.01; Windows 98; Marsh USA Inc. July 2000 build)\"";
	
	MapDriver<LongWritable, Text, Text, LongWritable> mapDriver;
	MapReduceDriver<LongWritable, Text, Text, LongWritable, Text, LongWritable> mapReduceDriver;
	
	@Before
	public void setup(){
		PageviewsMapper mapper =  new PageviewsMapper();
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
		mapDriver.withInput(new LongWritable(2), new Text(TestEntry2));
		mapDriver.withInput(new LongWritable(3), new Text(TestEntry3));
	    mapDriver.withOutput(new Text("29/Apr/2003"), new LongWritable(1));
	    mapDriver.withOutput(new Text("29/Apr/2003"), new LongWritable(1));
	    mapDriver.withOutput(new Text("19/Feb/2003"), new LongWritable(1));
	    mapDriver.runTest();
	}
	
	@Test
	public void testMapReduce() throws IOException{
		mapReduceDriver.withInput(new LongWritable(1), new Text(TestEntry1));
		mapReduceDriver.withInput(new LongWritable(2), new Text(TestEntry2));
		mapReduceDriver.withInput(new LongWritable(2), new Text(TestEntry2));
		mapReduceDriver.withInput(new LongWritable(3), new Text(TestEntry3));
		mapReduceDriver.addOutput(new Text("19/Feb/2003"), new LongWritable(1));
		mapReduceDriver.addOutput(new Text("29/Apr/2003"), new LongWritable(2));
		mapReduceDriver.runTest();
	}
}

