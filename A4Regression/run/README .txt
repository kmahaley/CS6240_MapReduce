
*************************************************************************************************************************************************
                                                                      PRE REQUISITES
*************************************************************************************************************************************************

1. R should be installed and accessible by the command line. Install all the packages required by R
2. A4Regression-0.0.1-SNAPSHOT.jar, uber-perf-0.0.1.jar and perf.txt files are in same location.
   To ensure that we have provided a folder named A4_Run with all the files necessary to run the project go to folder via terminal provide the necessary paths in makefile and run the project.
3. Make sure you have pandoc and latex installed.

*************************************************************************************************************************************************
                                                                        PERF.TXT
*************************************************************************************************************************************************

There are some changes in perf.txt when compared with the one used by professor.
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

Make changes in the makefile by providing the output folder path, input folder path from cluster bucket where you have data files required for running the project stored.
To run the project simply go to the A4_Run folder containing all the files needed to run the project from terminal and simply type make run-mapper1.