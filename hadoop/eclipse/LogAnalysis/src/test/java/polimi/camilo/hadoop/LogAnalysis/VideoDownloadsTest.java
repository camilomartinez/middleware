package polimi.camilo.hadoop.LogAnalysis;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class VideoDownloadsTest {
	//Some test entries
	private final String StarWarsPageLog = "128.206.92.71 - - [01/May/2003:13:52:33 -0700] \"GET /random/video/Star_Wars_Kid_Remix.php HTTP/1.1\" 302 13 \"-\" \"NSPlayer/9.0.0.2980 WMFSDK/9.0\"";
	private final String CommonPageLog = "66.137.118.69 - - [29/Apr/2003:06:58:22 -0800] \"GET /archive/2003/02/01/space_sh.shtml HTTP/1.1\" 200 11966 \"http://www.google.com/search?sourceid=navclient&q=%22next+time+Columbia+flies+will+be+in+November%22\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\"";
	private final String OriginalVideoLog = "66.25.214.64 - - [30/Apr/2003:17:21:24 -0700] \"GET /random/video/Star_Wars_Kid.wmv HTTP/1.1\" 302 307 \"http://www.waxy.org/archive/2003/04/29/star_war.shtml\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\"";
	private final String RemixVideoLog = "63.88.132.95 - - [02/May/2003:10:56:25 -0700] \"GET /random/video/Star_Wars_Kid_Remix.wmv HTTP/1.1\" 302 313 \"http://www.waxy.org/archive/2003/04/29/star_war.shtml\" \"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; .NET CLR 1.0.3705)\"";
	private final String OriginalVideoOtherLog = "68.65.217.50 - - [30/Apr/2003:00:19:59 -0700] \"GET /random/video/Star_Wars_Kid.wmv HTTP/1.0\" 200 100388 \"-\" \"NSPlayer/8.0.0.4487\"";
	
	MapDriver<LongWritable, Text, Text, LongWritable> mapDriver;
	MapReduceDriver<LongWritable, Text, Text, LongWritable, Text, LongWritable> mapReduceDriver;
	
	@Before
	public void setup(){
		VideoDownloadsMapper mapper =  new VideoDownloadsMapper();
		LongSumReducer<Text> reducer = new LongSumReducer<Text>();
		
		mapDriver = new MapDriver<LongWritable, Text, Text, LongWritable>();
		mapDriver.setMapper(mapper);
		mapReduceDriver = new MapReduceDriver<LongWritable, Text, Text, LongWritable, Text, LongWritable>();
		mapReduceDriver.setMapper(mapper);
		mapReduceDriver.setReducer(reducer);
	}
	
	@Test
	public void testMapper() throws IOException{
		mapDriver.withInput(new LongWritable(1), new Text(CommonPageLog));
		mapDriver.withInput(new LongWritable(2), new Text(OriginalVideoLog));
		mapDriver.withInput(new LongWritable(3), new Text(OriginalVideoOtherLog));
		mapDriver.withInput(new LongWritable(4), new Text(RemixVideoLog));
		mapDriver.withInput(new LongWritable(5), new Text(StarWarsPageLog));
	    mapDriver.withOutput(new Text("30/Apr/2003"), new LongWritable(1));
	    mapDriver.withOutput(new Text("30/Apr/2003"), new LongWritable(1));
	    mapDriver.withOutput(new Text("02/May/2003"), new LongWritable(1));
	    mapDriver.runTest();
	}
	
	
	@Test
	public void testMapReduce() throws IOException{
		mapReduceDriver.withInput(new LongWritable(1), new Text(CommonPageLog));
		mapReduceDriver.withInput(new LongWritable(2), new Text(OriginalVideoLog));
		mapReduceDriver.withInput(new LongWritable(3), new Text(OriginalVideoOtherLog));
		mapReduceDriver.withInput(new LongWritable(4), new Text(RemixVideoLog));
		mapReduceDriver.withInput(new LongWritable(5), new Text(StarWarsPageLog));
		mapReduceDriver.addOutput(new Text("02/May/2003"), new LongWritable(1));
		mapReduceDriver.addOutput(new Text("30/Apr/2003"), new LongWritable(2));
		mapReduceDriver.runTest();
	}
}