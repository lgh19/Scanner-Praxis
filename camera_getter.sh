# Gets the photos from the cameras and moves them to the computer

echo $1

JOBLABELARG=`echo ${1// /_}` # replace spaces with underscores
JOBDEFAULT= # default label

if [ -z "$JOBLABELARG" ]
then
  read -e -t 10 -p "Enter job label: " -i "${JOBDEFAULT}" JOBLABEL
  # on timeout no value returned, set a default
  if [ -z "${JOBLABEL}" ]
  then
    JOBLABEL="${JOBDEFAULT}"
  fi
else
  echo " Joblabel: ${JOBLABELARG}"
  JOBLABEL="${JOBLABELARG}"
fi

# Use a timestamp for the job folder name
JOBFOLDER=$1
echo "Jobfolder: ${JOBFOLDER}"

cd ${JOBFOLDER}

lefty=0
righty=0

for i in $(seq 0 9); do
  loc="[usb:002,00"
  loc2="$loc$i]"
  command='gphoto2 --port '
  command=$command$loc2
  command=$command' --get-config ownername'
  info=$($command)
  echo $command

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

function rename_images {
  # creates one directory with all the images in order
  # ...useful for PDF creation or Scan Tailor interactive GUI projects.
  # Function takes arguments --
  # $1 - a camera-specific identifier tag to append to each filename, e.g. 'left' or 'A'
  # $2 - the input path, e.g. '~/myjob/left'
  # $3 - the output path, e.g. '~/myjob/combined'
  # $4 - an image type to process, e.g. 'JPG' (camera output) or 'tif' (scantailor output)

  echo " "
  echo "Renaming images..."
  (
  mkdir -p $3
  cd $2
  shopt -s nullglob
  FILES=`echo *.$4`
  if [[ -z "$FILES" ]]
  then
    echo "Rename: No images to rename in directory: $2"
  else
    imgnum=1
    for i in *.$4; do
      newimg=$(printf "%05d-$1.$4" ${imgnum}) #05 pad to length of 5
      echo "${i} --> ${newimg}"
      mv ${i} $3/${newimg} # was cp (copy)
      let imgnum=imgnum+1
    done
  fi
  )
}

function rotate_images {
  # Function takes arguments --
  # $1 - a rotation direction, taken from the camera identifier, e.g. 'left'
  # $2 - the input path, e.g. '~/myjob/left'

  echo " "
  echo "Rotate images ($1) ($2)..."
  (
  cd $2
  shopt -s nullglob
  FILES=`echo *.JPG`
  if [[ -z "$FILES" ]]
  then
    echo "Rotate: No images to rotate in directory: $2"
  else
    case $1 in
      left )
        for i in *.JPG; do
          echo "...rotating 270: $i"
          convert "${i}" -rotate 270 "${i}"
        done
        ;;
      right )
        for i in *.JPG; do
          echo "...rotating 90: $i"
          convert "${i}" -rotate 90 "${i}"
        done
        ;;
      * )
        echo "Rotate: no rotation in job of type: $1"
        ;;
    esac
  fi
  )
}

function download_from_port {
  # Function takes two arguments --
  # $1 - the port string for communicating with the camera
  # $2 - the output path, e.g. '~/myjob/left' '~/myjob/right'
  if [ -z "$1" ]
  then
    echo "Download port not found--please check that camera is on and connected."
  else
    echo "Download from port: $1: all images..."
    (
    mkdir -p $2
    cd $2
    gphoto2 --port "$1" --get-all-files
    )
  fi
}

function verify_from_port {
    on_camera=$(gphoto2 --port $1 --list-files)
    camera_sorted=($(sort <<<"${on_camera[*]}"))
    cd $2
    shopt -s nullglob
    in_folder=`echo *.$3`
    folder_sorted=($(sort <<<"${in_folder[*]}"))
    A=${camera_sorted[@]};
    B=${folder_sorted[@]};
    echo $A
    echo $B
    if [ "$A" == "$B" ] ; then
	return 0;
    else
	return 1;
    fi;
}

function delete_from_port {
  # Function takes a port string as an argument
  if [ -z "$1" ]
  then
    echo "Delete: Camera not found--please check that it is on and connected."
  else
    echo "Delete: $1: all existing images from SD card"
    gphoto2 --port "$1" --recurse --delete-all-files
  fi
}

CAM1SIDE='left'
CAM2SIDE='right'

echo " "
echo "Downloading from cameras: "
download_from_port $leftport "$JOBFOLDER/cams/$CAM1SIDE"
download_from_port $rightport "$JOBFOLDER/cams/$CAM2SIDE"

echo " "
echo "Clearing cameras of downloaded images:"
delete_from_port $leftport
delete_from_port $rightport;

echo " "
echo "*********************************"
echo "***                           ***"
echo "***   OK to Resume Scanning   ***"
echo "***                           ***"
echo "*********************************"


echo " "
echo "Rotating images (imagemagick):"
rotate_images $CAM1SIDE "$JOBFOLDER/cams/$CAM1SIDE/"
rotate_images $CAM2SIDE "$JOBFOLDER/cams/$CAM2SIDE/"

#verification
#look in folders for images, should equal images in camera folder

x=$(verify_from_port $leftport "$JOBFOLDER/cams/$CAM1SIDE")
y=$(verify_from_port  $rightport "$JOBFOLDER/cams/$CAM2SIDE")

echo $x;
echo $y;

#if [ '$x' == 0 ] && [ '$y' == 0 ]; then
#else
#echo "Files in folders do not match files on cameras. You should retry."
#fi;


echo " "
echo "Renaming images:"
rename_images $CAM1SIDE "$JOBFOLDER/cams/$CAM1SIDE/" "$JOBFOLDER/" "JPG"
rename_images $CAM2SIDE "$JOBFOLDER/cams/$CAM2SIDE/" "$JOBFOLDER/" "JPG"


cd