import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DataETL {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String s = value.toString();
			s = s.replaceAll(", ", " ");
			String[] strs = s.split(",");

			String budget = "null";
			String popularity = "null";
			String revenue = "null";
			String duration = "null";
			String vote = "null";
			
			if(strs.length > 18)
			{
				budget = strs[0];
				popularity = strs[8];
				revenue = strs[12];
				duration = strs[13];
				vote = strs[18];
				
				StringBuffer sb = new StringBuffer("");
				
				//budget: 1
				if(budget.matches("^-?\\d+$") && Double.parseDouble(budget) > 0D)
				{
					sb.append(" 1:"+ budget + " ");  //remember to keep the space before 1
				}
				else
				{
					sb.append(" ");
				}
				
				//popularity: 2
				if(popularity.matches("([0-9]*)\\.([0-9]*)") && Double.parseDouble(popularity) < 1000D && Double.parseDouble(popularity) > 0D) 
				{
					sb.append("2:" + popularity + " ");
				}
				else
				{
					sb.append(" ");
				}
				
				//revenue: 3
				if(revenue.matches("^-?\\d+$") && Double.parseDouble(revenue) < 100000000000D && Double.parseDouble(revenue) > 0D)
				{
					sb.append("3:" + revenue + " ");
				}
				else
				{
					sb.append(" ");
				}
				
				//duration: 4
				if(duration.matches("^-?\\d+$") && Double.parseDouble(duration) > 0D && Double.parseDouble(duration) < 1000D) //check is it's a valid duration
				{
					sb.append("4:" + duration+ " ");
				}
				else
				{
					sb.append(" ");
				}
				
				if(vote.matches("([0-9]*)\\.([0-9]*)") && Double.parseDouble(vote) <= 10.0D && Double.parseDouble(vote) >= 0.0D) 
				{
					context.write(new Text(vote), new Text(sb.toString()));
				}
				
			}

		}
	}

	public static class IntSumReducer extends
			Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Text value,
				Context context) throws IOException, InterruptedException {
			
			context.write(key, value);
			
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf, "cleaning and formatting data");
		job.setJarByClass(DataETL.class);
		
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
