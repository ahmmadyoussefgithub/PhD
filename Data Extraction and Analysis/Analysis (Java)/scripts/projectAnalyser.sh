#!/bin/bash

export PATH=$PATH:/root/understand/scitools/bin/linux64;
export SCRIPTS_PATH=/root/scripts/;
export PROJECTS_PATH=/root/projects/;
export REPORTS_PATH=/root/reports/;
fileName=$1;

cat $fileName | while read project
do
	mkdir $PROJECTS_PATH$project
	cd $PROJECTS_PATH$project
	wget https://storage.googleapis.com/google-code-archive-source/v2/code.google.com/$project/source-archive.zip -O $project.zip
	unzip $PROJECTS_PATH$project/$project.zip
	cd $PROJECTS_PATH$project/$project
	
	und create -db $project 
        und settings -Languages Java $project.udb
        und settings -Metrics all $project.udb
        und add trunk $project.udb
        und analyze $project 
        und report $project.udb 
        und purge -db $project 

	mv $project.txt $REPORTS_PATH
	rm -rf $PROJECTS_PATH$project	
done
