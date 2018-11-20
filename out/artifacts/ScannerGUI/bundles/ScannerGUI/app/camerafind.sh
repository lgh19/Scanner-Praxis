lefty=0
righty=0

for i in $(seq 0 9); do
  loc="usb:00"
  loc2="$loc$i"
  command='gphoto2 --port '
  command=$command$loc2
  command=$command' --get-config ownername'
  info=$($command)
  echo $command
  echo $info

  #search for "Leftcamera"
  if [[ $info == *"Leftcamera"* ]]; then
          #if we find it, store port info as lefty
          leftport=$loc2;
      fi

if [[ $info == *"Rightcamera"* ]]; then
          #if we find it, store port info as righty
         rightport=$loc2
      fi
 
 #if both vars are set, the rest of the loop will continue but do nothing

done

echo $leftport
echo $rightport