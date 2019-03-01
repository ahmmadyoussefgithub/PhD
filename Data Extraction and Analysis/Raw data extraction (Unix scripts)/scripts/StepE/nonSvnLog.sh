#!/bin/bash
fileName=$1;
numberOfLinesToSkip=$2;

skippedLines=0;
linesProcessed=0;

cat $fileName | while read sourceCmdLine
do
	if [ $skippedLines -lt $numberOfLinesToSkip ] 
	then
		let skippedLines+=1;
	else
		if [ $(($linesProcessed%3)) -eq 0 ]
		then
			if [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "git ") }') -gt 0 ]
			then
				gitUrl=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f3);
				projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
				if [ ! -d "$projectName" ]
				then
					echo "git project: " $projectName
					cd ~/nonSvn/$projectName		
					git log >> ~/nonSvn/nonSvnLogs/$projectName;
					cd ~/nonSVN
				fi
			elif [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "hg ") }') -gt 0 ]
			then
				hgUrl=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f3);
				projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
				if [ ! -d "$projectName" ]
				then
					echo "hg project: " $projectName
					cd ~/nonSVN/$projectName		
					hg log >> ~/nonSvn/nonSvnLogs/$projectName;
					cd /root
				fi
			fi

		fi
		let linesProcessed+=1;
	fi
done

