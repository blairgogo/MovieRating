import java.io.IOException;
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class CommentLikeMapper
	extends Mapper<LongWritable, Text, Text, IntWritable> {
	
	@Override
	public void map(LongWritable key, Text value, Context context) 
	   throws IOException, InterruptedException {

	   		String line = value.toString();

			String[] ParsedLine = line.split(",");

			if(ParsedLine.length == 10){

		   		int firstCom = line.indexOf(",") + 1;
				int secondCom = line.indexOf(",", firstCom+1);
				int lastCom = line.lastIndexOf(",") + 1;

				String movie_name = line.substring(firstCom, secondCom);
				int comment_like = Integer.parseInt(line.substring(lastCom));
		   		/*String[] ParsedLine = line.split(",");

		   		String movie_name = ParsedLine[1];
		   		int comment_like = Integer.parseInt(ParsedLine[9]);*/

		   		context.write(new Text(movie_name), new IntWritable(comment_like));
		   	}

		}
}