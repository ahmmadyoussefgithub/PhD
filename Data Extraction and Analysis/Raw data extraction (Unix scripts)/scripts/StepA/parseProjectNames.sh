cat $1 | while read fileline
do
echo $fileline | awk '{ print $1; }'
done



