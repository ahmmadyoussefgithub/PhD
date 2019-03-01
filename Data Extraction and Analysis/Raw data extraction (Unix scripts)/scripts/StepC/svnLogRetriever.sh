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
		if [ $(($linesProcessed%100)) -eq 0 ]
		then
			if [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "/svn/") }') -gt 0 ]
			then
				svnCmd=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f1);
				echo $projectName
				projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
				foundProjectName=$(ls "output/"$projectName | cut -d"/" -f2)
				if [ "$foundProjectName" != "$projectName" ]
				then	
					echo "not found" $projectName
					eval "svn log" $svnCmd "> ./output/"$projectName;
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
