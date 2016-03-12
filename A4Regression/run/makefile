CLUSTER_INPUT_PATH = <PROVIDE CLUSTER INPUT PATH>
CLUSTER_OUTPUT_PATH = <PROVIDE CLUSTER OUTPUT PATH>
CLUSTER_OUTPUT_DATA_PATH = <PROVIDE PATH HERE>
S3_PATH_TO_TEXT_FILE = $(BUCKET)+R.txt

regression:
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A4Regression-0.0.1-SNAPSHOT.jar -kind=cloud -arguments="regression $(CLUSTER_INPUT_PATH) $(CLUSTER_OUTPUT_PATH) mean" -name=emr-mean
	aws s3 sync $(CLUSTER_OUTPUT_DATA_PATH) output 
	cat output/part-r-*> output/hadoop-output
	sudo Rscript -e "require(knitr); require(markdown); rmarkdown::render('R1Regression.R')"
	aws s3 cp R.txt $(S3_PATH_TO_TEXT_FILE)
	java -cp uber-perf-0.0.1.jar neu.perf.App -jar=A4Regression-0.0.1-SNAPSHOT.jar -kind=cloud -arguments="plot $(CLUSTER_INPUT_PATH) $(CLUSTER_OUTPUT_PATH) $(S3_PATH_TO_TEXT_FILE)" -name=emr-plot
	sudo Rscript -e "require(knitr); require(markdown); rmarkdown::render('R2Report.R')"

