#!/bin/bash
filename=$1
spawnCount=0

mkdir ~/nonSvn/nonSvnLogs;

while [ $spawnCount -lt 3 ]
do 
	echo "spawn"
	./nonSvnLog.sh $filename $spawnCount &
	let spawnCount+=1;
done

