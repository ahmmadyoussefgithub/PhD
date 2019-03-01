filename=$1
spawnCount=0

while [ $spawnCount -lt 3 ]
do 
	echo "spawn"
	./nonSvnCheckoutProject.sh $filename $spawnCount &
	let spawnCount+=1;
done
