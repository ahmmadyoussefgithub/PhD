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
				gitCmd=$(echo $sourceCmdLine | cut -d"=" -f2);
				echo $projectName
				projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
				if [ ! -d "$projectName" ]
				then
					echo "git project: " $projectName	
					$gitCmd;
				fi
			elif [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "hg ") }') -gt 0 ]
			then
				hgUrl=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f3);
				projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
				if [ ! -d "$projectName" ]
				then
					echo "hg project: " $projectName	
					hg clone -U $hgUrl;

				fi
			fi
		fi
		let linesProcessed+=1;
	fi
done
































		#elif [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "git ") }') -gt 0 ]
		#then
		#	gitCmd=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f1)
		#	eval "git log" $gitCmd
		#elif [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "hg ") }') -gt 0 ]
		#then
		#	hgCmd=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f1)
		#	eval "hg log" $hgCmd
