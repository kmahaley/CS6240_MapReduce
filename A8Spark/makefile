aws.bucket.name = <BUKCET NAME>
jar.name = job.jar
aws.input.dir = <INPUT FOLDER NAME>
aws.output.dir = <OUTPUT FOLDER NAME>
aws.N = 1
aws.cluster.id = <CLUSTER ID>

# Main EMR launch.
upload:
		aws s3 cp $(jar.name) s3://${aws.bucket.name}
cluster:
	aws emr create-cluster --name "SPARK" --ami-version 3.10.0 --applications Name=Spark --ec2-attributes KeyName=us-east-1 --instance-type m3.xlarge --instance-count 3 --use-default-roles
steps:
	aws emr add-steps --cluster-id ${aws.cluster.id} --steps Type=Spark,Name="Spark Model",ActionOnFailure=TERMINATE_CLUSTER,Args=[--deploy-mode,cluster,--class,com.spark.airline.SparkApp,s3://${aws.bucket.name}/${jar.name},-input=s3://${aws.bucket.name}/${aws.input.dir},-output=s3://${aws.bucket.name}/${aws.output.dir},${aws.N}]
#wait for cluster to complete steps
getoutput:
	aws s3 sync s3://${aws.bucket.name}/${aws.output.dir} prediction
	cat prediction/part-* > merged-prediction
	aws emr terminate-clusters --cluster-ids ${aws.cluster.id}
