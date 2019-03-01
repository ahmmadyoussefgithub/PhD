filename=$1
spawnCount=0

while [ $spawnCount -lt 100 ]
do 
	./consolidateSvnLogFile.sh $filename $spawnCount &
	let spawnCount+=1;
done
