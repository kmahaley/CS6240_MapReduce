
*************************************************************************************************************************************************
                                                                      PRE REQUISITES
*************************************************************************************************************************************************

1. Make sure HADOOP_HOME is set.
2. R should be installed and accessible by the command line. Install all the packages required by R
3. A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar, A3Redux-0.0.1-SNAPSHOT.jar, uber-perf-0.0.1.jar and perf.txt files are in same location.
   To ensure that we have provided a folder named A3_Run with all the files necessary to run the project go to folder via terminal provide the necessary paths in makefile and run the project.
4. Make sure that the ouput directory does not exists in hdfs for hadoop run.
5. Before re-executing Makefile make sure all results.csv files is deleted.
6. Make sure you have pandoc and latex installed

*************************************************************************************************************************************************
                                                                        PERF.TXT
*************************************************************************************************************************************************

There are some changes in perf.txt when compared with the one used by professor. Make sure "hadoop.home" property points to hadoop home dir.
Details to be modified.
region = <region of your cluster> 
check.bucket = <bucket path>
check.input = <input bucket path>
check.logs = <log bucket path>
delete.output = <output bucket path>
upload.jar = <jar path in s3>
access_key = <your access key>
secret_key = <your secret key>

*************************************************************************************************************************************************
                                                              RUNNING PROGRAM USING MAKE FILE
*************************************************************************************************************************************************

Make changes in the makefile by providing the output folder path, input folder path for single thread and multi thread application as well provide folder paths for hadoop output and input folder path.

To run the project simply go to the A3_Run folder containing all the files needed to run the project from terminal and simply type make benchmarking.