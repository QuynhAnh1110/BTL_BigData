import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class KMapper extends Mapper<LongWritable, Text, LongWritable, PointWritable> {

    private PointWritable[] currCentroids;
    private final LongWritable centroidId = new LongWritable();
    private final PointWritable pointInput = new PointWritable();
    private BufferedWriter writer;
    private FileSystem fs;
    private FSDataOutputStream outputStream;

    @Override
    public void setup(Context context) throws IOException {
        int nClusters = Integer.parseInt(context.getConfiguration().get("k"));
        this.currCentroids = new PointWritable[nClusters];

        // Load the centroids from configuration
        for (int i = 0; i < nClusters; i++) {
            String[] centroid = context.getConfiguration().getStrings("C" + i);
            this.currCentroids[i] = new PointWritable(centroid);
        }

        // Set up file system and open output stream once
        Configuration conf = context.getConfiguration();
        Path PathOutputData = new Path("/BTL/data_output/data_output.txt");
        this.fs = PathOutputData.getFileSystem(conf);

        if (fs.exists(PathOutputData)) {
            this.outputStream = fs.append(PathOutputData);
        } else {
            this.outputStream = fs.create(PathOutputData);
        }
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] arrPropPoint = value.toString().split(",");
        pointInput.set(arrPropPoint);

        double minDistance = Double.MAX_VALUE;
        int centroidIdNearest = 0;
        for (int i = 0; i < currCentroids.length; i++) {
            double distance = pointInput.calcDistance(currCentroids[i]);
            if (distance < minDistance) {
                centroidIdNearest = i;
                minDistance = distance;
            }
        }

        centroidId.set(centroidIdNearest);
        context.write(centroidId, pointInput);

        // Buffer the output instead of writing to file directly each time
        writer.write(pointInput.toString() + "," + centroidId.toString() + "\n");
    }

    @Override
    protected void cleanup(Context context) throws IOException {
        // Close the BufferedWriter and the output stream once map phase is done
        if (writer != null) {
            writer.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }
}