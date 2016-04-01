#!/bin/bash
#USAGE: ./launcher.sh <java_file_to_compile> <config_file> <netid>
PROG=Process
SOURCE=$PROG".java"
CONFIG=$1
netid=$2

sed -e "s/#.*//" $CONFIG | sed -e "/^\s*$/d" > temp
echo  >> temp
REMOVE="rm bin/*.class"
$REMOVE
node_count=0
nodes_location="" #Stores a # delimited string of Location of each node
host_names=() #Stores the hostname of each node
neighbors_dict=() # Stores the Token path of each node

current_line=1
# Reading from the temp file created above
while read line; 
do
	#turn all spaces to single line spaces
	line=$(echo $line | tr -s ' ')
########Extract Number of nodes and, min and max per Active
	if [ $current_line -eq 1 ]; then
		#number of nodes
		node_count=$(echo $line | cut -f1 -d" ")
		#convert it to an integer
  		let node_count=$node_count+0 
  		
  		#minPerActive, maxPerActive
  		minPerActive=$(echo $line | cut -f2 -d" ")
  		maxPerActive=$(echo $line | cut -f3 -d" ")
  		#sendMinDelay, snapshotDelay
  		sendMinDelay=$(echo $line | cut -f4 -d" ")
  		snapshotDelay=$(echo $line | cut -f5 -d" ")
  		#maxNumber
  		maxNumber=$(echo $line | cut -f6 -d" ")
  		
  	else
#########Extract Location of each node
  		if [ $current_line -le $(expr $node_count + 1) ]; then
  			nodes_location+=$( echo -e $line"#" )	
  			node_id=$(echo $line | cut -f1 -d" ")
  			hostname=$(echo $line | cut -f2 -d" ")
  			host_names[$node_id]="$hostname"	
  		else
###########Extract Neighbors
			
			let node_id=$current_line-$node_count-2
  			neighbors=$(echo $line)
  			neighbors_dict+=(['"$node_id"']="$neighbors")
  		fi
  	fi
  	let current_line+=1
done < temp

#COMPILE ONLY ONCE. AND WAIT FOR 2s to reflect in other dc machines
COPY="cp resources/foo.out foo.out"
$COPY
PROGRAM="Process.java"
COMPILE="javac -classpath resouces/commons-math3-3.6.1.jar -sourcepath src src/$PROGRAM -d bin"
$COMPILE
sleep 2s



# iterate through the date collected above and execute on the remote servers
for node_id in $(seq 0 $(expr $node_count - 1))
do
	host=${host_names[$node_id]}
	neighbors=${neighbors_dict["$node_id"]}
	#echo $netid@$host "java $PROG $node_id '$nodes_location' '$neighbors' '$minPerActive' '$maxPerActive' \
	#'$sendMinDelay' '$snapshotDelay' '$maxNumber' '$config_file_name' " &
	COPY="cp resources/foo.out foo-"$node_id".out"
	$COPY
	COPY="cp resources/foo.out logs-"$node_id".out"
	$COPY
	ssh -o StrictHostKeyChecking=no $netid@$host "cd $(pwd);java -cp bin:resources/* Process $CONFIG $node_id '$nodes_location' '$neighbors' '$node_count' '$minPerActive' '$maxPerActive' \
	'$sendMinDelay' '$snapshotDelay' '$maxNumber' '$config_file_name' " &
      # echo $host
done
