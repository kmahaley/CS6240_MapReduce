INPUT_PATH = <Provide local input path>
OUTPUT_PATH = <Provide local output path>
HADOOP_INPUT_PATH = <Provide hadoop input path>
HADOOP_OUTPUT_PATH = <Provide hadoop output path>
CLUSTER_INPUT_PATH = <Provide AWS input path>
CLUSTER_OUTPUT_PATH = <Provide AWS output path>

benchmarking:
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=plain -main=com.mapreduce.assignment.A3ReduxMain -arguments="-s $(INPUT_PATH) $(OUTPUT_PATH) mean" -name=singlethread-mean
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=plain -main=com.mapreduce.assignment.A3ReduxMain -arguments="-s $(INPUT_PATH) $(OUTPUT_PATH) median" -name=singlethread-median
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=plain -main=com.mapreduce.assignment.A3ReduxMain -arguments="-m $(INPUT_PATH) $(OUTPUT_PATH) mean" -name=multithread-mean
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=plain -main=com.mapreduce.assignment.A3ReduxMain -arguments="-m $(INPUT_PATH) $(OUTPUT_PATH) median" -name=multithread-median
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=local -main=com.mapreduce.assignment.A3ReduxMain -arguments="-h $(HADOOP_INPUT_PATH) $(HADOOP_OUTPUT_PATH) mean" -name=hadoop-mean
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=local -main=com.mapreduce.assignment.A3ReduxMain -arguments="-h $(HADOOP_INPUT_PATH) $(HADOOP_OUTPUT_PATH) median" -name=hadoop-median
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=local -main=com.mapreduce.assignment.A3ReduxMain -arguments="-h $(HADOOP_INPUT_PATH) $(HADOOP_OUTPUT_PATH) fastmedian" -name=hadoop-fastmedian
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=cloud -arguments="-h $(CLUSTER_INPUT_PATH) $(CLUSTER_OUTPUT_PATH) mean" -name=emr-mean
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=cloud -arguments="-h $(CLUSTER_INPUT_PATH) $(CLUSTER_OUTPUT_PATH) median" -name=emr-median
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A3Redux-0.0.1-SNAPSHOT-jar-with-dependencies.jar -kind=cloud -arguments="-h $(CLUSTER_INPUT_PATH) $(CLUSTER_OUTPUT_PATH) fastmedian" -name=emr-fastmedian
	sudo apt-get install texlive-latex-extra
	sudo apt-get install pandoc
	# call to generate grapth in R
	sudo Rscript -e "install.packages('knitr', repos = c('http://yihui.name/xran', 'http://cran.rstudio.org')); install.packages('rmarkdown', repos='http://cran.rstudio.com/');install.packages('ggplot2', repos='http://cran.rstudio.com/');require(knitr); require(markdown); rmarkdown::render('A3Redux.Rmd')"
	sudo nohup xdg-open A3Redux.pdf &
