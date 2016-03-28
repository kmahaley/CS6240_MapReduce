A8 Spark assignment
Authors- Chen Bai, Shakti Patro, Kartik Mahaley, Pankaj Tripathi,

*************************************************************************************************************************************************
                                                                      PRE REQUISITES
*************************************************************************************************************************************************

1. Extract files from given zip file to A8Run folder
2. Folder contains following files/folder
	1. output folder with our result
	2. job.jar
	3. makefile
	4. Readme.txt
	5. A8Report folder with our report and rmd
	6. src folder with our code
	7. pom.xml

2. Goto A8Run folder to edit makefile

3. Add following details in makefile 
	aws.bucket.name = <Name of the bucket>
	jar.name = job.jar
	aws.input.dir = <AWS path for input files>
	aws.N = <Argument for N 1 or 200 etc>
	aws.cluster.id = <AWS CLUSTER ID>
	aws.output.dir = <Output directory on AWS>

Example :
	aws.bucket.name = mapreduce
	jar.name = job.jar
	aws.input.dir = input/sample
	aws.N = 1
	aws.cluster.id = j-XXXXXXXX
	aws.output.dir = output
4. make sure EC2 keys belong to the region specified in your CLI
To check region of the CLI run command
	-aws configure
		AWS Access Key ID [****************DNRQ]: 
		AWS Secret Access Key [****************XT9K]: 
		Default region name [us-east-1]: 
		Default output format [text]:

*************************************************************************************************************************************************
                                                                      STEPS TO EXECUTE
*************************************************************************************************************************************************
1. From terminal, goto folder where all files are kept, A8Run folder
2. To upload jar to AWS, 
	- make upload
3. To make cluster
	- make cluster
4. To add step in cluster. Take id of the cluster j-XXXX which is created after make cluster command
   add that to makefile and run following command to add step to cluster
	-make steps 
5. Please wait for cluster to complete the step.Run below command to get output files from the aws.
	- make getoutput
