#!/bin/bash
fileName=$1;

skippedLines=0;
linesProcessed=0;

cat $fileName | while read logLine 
do
        prefix=$(echo $logLine | cut -d"|" -f1-3)
       	date=$(echo $logLine | cut -d"|" -f4 | cut -d" " -f2)
       	echo $prefix "|" $date "| SVN" 
done

