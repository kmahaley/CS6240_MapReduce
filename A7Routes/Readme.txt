A7 Routes assignment
Authors- Chen Bai, Shakti Patro, Kartik Mahaley, Pankaj Tripathi,

*******************************************************
PRE REQUISITES
*******************************************************

1. Extract files from given zip file to A7Run folder
2. Folder contains following files/folder
	1. validate folder
	2. output folder with our result
	3. job.jar
	4. makefile
	5. perf.txt
	6. Readme.txt
	7. uber-perf-0.0.1.jar
	8. A7Routing.pdf
	9. A7Routing.rmd
	10.A7Routing folder with source code

2. Goto A7Run folder to edit makefile and perf.txt to run program.

3. Add following details in makefile 
	aws.bucket.name = <Name of the bucket>
	jar.name = job.jar
	aws.input.history = <AWS path for a7 history files>
	aws.input.test = <AWS path for a7 test files>
	aws.input.request = <AWS path for a7 request files>
	aws.output.dir = <Output directory on AWS>

Example :
	aws.bucket.name = mapreduce
	jar.name = job.jar
	aws.input.history = input/a7history
	aws.input.test = input/a7test
	aws.input.request = input/a7request
	aws.output.dir = output

4. Add follwing details in perf.txt

	# AWS S3 Initialization section
  
	region = <Region name> 
	check.bucket = <Bucket name>
	check.input = <Input folder>
	check.logs = <Logs folder>
	delete.output = <Output folder>
	upload.jar = <Bucket name>
	access_key = <ACCESS KEY>
	secret_key = <SECRET KEY>
	
Example:
	region = us-east-1 
	check.bucket = s3://mapreduce
	check.input = s3://mapreduce/input
	check.logs = s3://mapreduce/logs
	delete.output = s3://mapreduce/output
	upload.jar = s3://mapreduce
	access_key = <ACCESS KEY>
	secret_key = <SECRET KEY>

# AWS EMR Cluster Configuration
	log.uri = <Log folder path on aws>
Example
	log.uri = s3://mapreduce/logs

****************************************************************
STEPS TO EXECUTE
****************************************************************
1. From terminal, goto folder where all files are kept, A7Run folder
2. To run the whole jar file without any specific steps, 
	- make runperf
3. To Build jar file from the folder 
	- make buildjar
4. To upload jar to AWS, 
	- make upload
5. To make cluster
	- make cloud
6. Please wait for cluster to complete the step.Run below command to get output files from the aws.
	- make getoutput
7. Run below command to get score 
	- make validate
   
	score.txt file which proposes hops in format < Flight1 Flight2 Duration > for a given request.
   	End of the file gives count of missed flight, count of correct predicted files and total count of flights
