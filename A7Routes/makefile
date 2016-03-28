aws.bucket.name = <BUCKET NAME>
jar.name = job.jar
aws.input.history = <HISTORY FOLDER NAME>
aws.input.test = <TEST FOLDER NAME>
aws.input.request = <REQUEST FOLDER NAME>
aws.output.dir = <OUTPUT FOLDER>


# Main EMR launch.
buildjar:
	cd A7Routing && mvn clean && mvn package 
	mv A7Routing/target/A7Routes-0.0.1-SNAPSHOT-jar-with-dependencies.jar job.jar
upload:
		aws s3 cp $(jar.name) s3://${aws.bucket.name}
cloud:
	java -cp uber-perf-0.0.1.jar neu.perf.App -kind=cloud -arguments="predict s3://${aws.bucket.name}/${aws.input.history} s3://${aws.bucket.name}/${aws.input.test} s3://${aws.bucket.name}/${aws.input.request} s3://${aws.bucket.name}/${aws.output.dir} s3://${aws.bucket.name}"
getoutput:
	aws s3 sync s3://${aws.bucket.name}/${aws.output.dir} prediction
	cat prediction/part-* > merged-prediction
validate:
	java -cp ${jar.name} com.assignment.a7.App validate merged-prediction a7validate/04missed.csv.gz
runperf:
	make upload
	make cloud
	make getoutput
	make validate
