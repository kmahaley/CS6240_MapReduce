
HADOOP_A6HISTORY = <Provide a6history path for hadoop>
HADOOP_A6TEST = <Provide a6test path for hadoop>
HADOOP_A6VALIDATE = <Provide a6validate path for hadoop>
HADOOP_OUTPUT = <Provide output path for hadoop>

prediction:
	hadoop dfs -rmr ${HADOOP_OUTPUT}
	hadoop jar A6Prediction-0.0.1-SNAPSHOT-jar-with-dependencies.jar ${A6HISTORY} ${A6TEST} ${HADOOP_A6VALIDATE} ${HADOOP_OUTPUT}
