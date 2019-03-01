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
			projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
			processNextLine="false";
			cat "output/"$projectName | while read logLine
			do
				if [ $(awk -v a="$logLine" 'BEGIN { print index(a, "-----------------------") }') -eq 1 ]
				then
					processNextLine="true";
				elif [ "$processNextLine" == "true" ]
				then
					if [ $(awk -v a="$logLine" 'BEGIN { print index(a, "r") }') -eq 1 ]
					then
						revision=$(echo $logLine | cut -d"|" -f1)
						author=$(echo $logLine | cut -d"|" -f2)
						date=$(echo $logLine | cut -d"|" -f3)
						echo $projectName "|" $revision "|" $author "|" $date "| SVN"
					fi
					processNextLine="false";
				fi
			done
                fi
                let linesProcessed+=1;
        fi
done




