package com.trialprogram;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TrialProgram extends Configured implements Tool {

	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("usage: [input] [output]");
			System.exit(-1);
		}
		Job job = new Job(getConf());
		job.setJobName("trialprogram");
		job.setJarByClass(getClass());

		FileSystem fs = FileSystem.get(getConf());
		// get the FileStatus list from given dir
		FileStatus[] status_list = fs.listStatus(new Path(args[0]));
		if (status_list != null) {
			for (FileStatus status : status_list) {
				// add each file to the list of inputs for the map-reduce job
				FileInputFormat.addInputPath(job, status.getPath());
			}
		}
		// FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(ProjectMapper.class);
		job.setCombinerClass(ProjectReducer.class);
		job.setReducerClass(ProjectReducer.class);

		job.setOutputKeyClass(CompositeGroupKey.class);
		job.setOutputValueClass(LongWritable.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		long start = new Date().getTime();
		int res = ToolRunner.run(new TrialProgram(), args);
		long end = new Date().getTime();
		Path path = new Path(args[1] + "/time.txt");
		FileSystem fs = FileSystem.get(new Configuration());
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fs.create(path, true)));
		br.write(String.valueOf("Time taken : "+(end - start) / 1000));
		br.close();
		System.exit(res);

	}

	public static class ProjectMapper extends Mapper<LongWritable, Text, CompositeGroupKey, LongWritable> {
		private Text word = new Text();
		private LongWritable count = new LongWritable();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// value is tab separated values: word, year, occurrences, #books,
			// #pages
			// we project out (word, occurrences) so we can sum over all years
			String[] split = value.toString().split(",");
			// word.set(split[0]);
			// System.out.println(word);
			if (split.length > 2) {
				try {
					count.set(Long.parseLong(split[2]));
					String name = split[0];
					String city = split[1];
					// context.write(word, count);
					CompositeGroupKey compo = new CompositeGroupKey(name, city);
					context.write(compo, count);
				} catch (NumberFormatException e) {
					// cannot parse - ignore
				}
			}
		}
	}
	
	public static class ProjectReducer extends Reducer<CompositeGroupKey, LongWritable, CompositeGroupKey, LongWritable> {

		private LongWritable result = new LongWritable();

		public void reduce(CompositeGroupKey key, Iterable<LongWritable> values, Context context)
				throws IOException, InterruptedException {
			long sum = 0;
			for (LongWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}

	}

}
