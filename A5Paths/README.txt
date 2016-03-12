README:

The submitted tar file contains the following:
1) Source folder
2) Run folder
3) Output folder

Steps for running the program on AWS:

Step 1: In the given Makefile, please assign values to the following:
	a)INPUT_PATH = <PROVIDE INPUT TO INPUT FILES ON CLUSTER>
	b)INTER_OUTPUT = <PROVIDE PATH TO A FOLDER TO CREATE A INTERMEDIATE OUTPUT>
	c)OUTPUT_PATH =  <PROVIDE PATH TO OUTPUT FOLDER ON CLUSTER>
	d)LOCAL_FOLDER= <PROVIDE PATH ON LOCAL SYSTEM WHERE YOU WANT TO DOWNLOAD ALL FILES>. This folder should exist before you
			run makefile.
	e)all the fields mentioned in angular brackets<> should specified by user 

Step 2: Please specify details in perf.txt file if you have specfied the folder details in make file 
	# AWS S3 Initialization
	a)bucket path 
	b)input folder
	c)log folder
	d)output folder
	d)jar folder
	e) access key and secret key
	f) arguments-

	make sure what you enter in makefile and perf.txt should be in sync.

Step 2: Make sure that you are in home directory of "run" which contains "job.jar","perf.txt","makefile","uber-perf-0.0.1.jar"

Step 3: cd to the path of the Makefile and Run the makefile using "make paths" 

Step 4: Please wait until the cluster is terminated successfully. You can check the status of the cluster execution on Amazon AWS EMR web console.



