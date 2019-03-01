fileName=$1;

revision=unset;
date=unset;
author=unset;


cat $fileName | while read sourceCmdLine
do
	if [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "git ") }') -gt 0 ]
	then
		hgUrl=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f3);
		projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
			cat /host/nonSVN/nonSvnLogs/$projectName | while read logLine
			do
				if [ "$revision" != unset -a "$date" != unset -a "$author" != unset ]
				then
					echo $projectName "|" $revision "|" $author "|" $date "|" $repository 
					revision=unset;
					date=unset;
					author=unset;
				elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "commit") }') -eq 1 ]
				then
					revision=$(echo $logLine | cut -d" " -f2-)
                                elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "Date:") }') -eq 1 ]
                                then
					date=$(echo $logLine | cut -d" " -f2-)
                                elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "Author:") }') -eq 1 ]
                                then
                                        author=$(echo $logLine | cut -d" " -f2-)
                                fi
			done
	elif [ $(awk -v a="$sourceCmdLine" 'BEGIN { print index(a, "hg ") }') -gt 0 ]
	then
		hgUrl=$(echo $sourceCmdLine | cut -d"=" -f2 | cut -d" " -f3);
		projectName=$(echo $sourceCmdLine | cut -d"=" -f1);
		if [ ! -d "$projectName" ]
		then
			cat /root/nonsvnlogs/$projectName | while read logLine
			do
				if [ "$revision" != unset -a "$date" != unset -a "$author" != unset ]
				then
					echo $projectName "|" $revision "|" $author "|" $date "| HG" 
					revision=unset;
					date=unset;
					author=unset;
				elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "changeset:") }') -eq 1 ]
				then
					revision=$(echo $logLine | cut -d" " -f2-)
                                elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "date:") }') -eq 1 ]
                                then
					date=$(echo $logLine | cut -d" " -f2-)
                                elif [ $(awk -v a="$logLine" 'BEGIN { print index(a, "user:") }') -eq 1 ]
                                then
                                        author=$(echo $logLine | cut -d" " -f2-)
                                fi
			done
		fi
	fi


done

