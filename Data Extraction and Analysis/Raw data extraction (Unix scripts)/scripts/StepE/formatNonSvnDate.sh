#!/bin/bash
fileName=$1;

cat $fileName | while read logLine 
do
	
	project=$(echo $logLine | cut -d"|" -f1)
	revision=$(echo $logLine | cut -d"|" -f2)
	author=$(echo $logLine | cut -d"|" -f3)
	sourceSystem=$(echo $logLine | cut -d"|" -f5)

	dayOfMonth=$(echo $logLine | cut -d"|" -f4 | cut -d" " -f4)
	year=$(echo $logLine | cut -d"|" -f4 | cut -d" " -f6)
	monthText=$(echo $logLine | cut -d"|" -f4 | cut -d" " -f3)

	if [ "$monthText" == "Jan" ]
	then
		month=01	
	elif [ "$monthText" == "Feb" ] 
	then
		month=02
        elif [ "$monthText" == "Mar" ]
	then
		month=03
        elif [ "$monthText" == "Apr" ]
        then
		month=04
        elif [ "$monthText" == "May" ]
        then
		month=05
        elif [ "$monthText" == "Jun" ]
        then
		month=06
        elif [ "$monthText" == "Jul" ]
        then
		month=07
        elif [ "$monthText" == "Aug" ]
        then
		month=08
        elif [ "$monthText" == "Sep" ]
        then
		month=09
        elif [ "$monthText" == "Oct" ]
        then
		month=10
        elif [ "$monthText" == "Nov" ]
        then
		month=11
        elif [ "$monthText" == "Dec" ]
        then
		month=12
	fi
	echo $project "|" $revision "|" $author "|" $year-$month-$dayOfMonth "|" $sourceSystem 
done

