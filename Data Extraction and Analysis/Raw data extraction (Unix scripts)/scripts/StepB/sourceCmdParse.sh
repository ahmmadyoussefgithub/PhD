offset="21"
start="false"

cat $1 | while read projectName 
do
	if [ -z "$2" -o "$projectName" == "$2" -o "$start" == "true" ]
	then
		start="true"
		webpage=$(wget -q -O- http://code.google.com/p/$projectName/source/checkout) 
		checkoutCmdStartIndex=$(awk -v a="$webpage" 'BEGIN { print index(a, "<tt id=\"checkoutcmd\">") }')
		offsetCheckoutCmdStartIndex=$(($checkoutCmdStartIndex+$offset))
		webpageRemainder=$(awk -v a="$webpage" -v b="$offsetCheckoutCmdStartIndex" 'BEGIN { print substr(a, b) }')
        	checkoutCmdEndIndex=$(awk -v a="$webpageRemainder" 'BEGIN { print index(a, "</tt>") }')
		unformattedCheckoutCmd=$(awk -v a="$webpageRemainder" -v b="$checkoutCmdEndIndex" 'BEGIN { print substr(a, 0, b) }')
	fi

formattedCheckoutCmd=$(echo $unformattedCheckoutCmd | sed 's/<strong>//g' | sed 's/<\/strong>//g' | sed 's/svn checkout //g' | sed 's/<em>//g' | sed 's/<\/em>//g')

echo $projectName=$formattedCheckoutCmd
done



