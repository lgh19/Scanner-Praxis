#! /bin/bash

function autodetect_cams {
  # Parse the gphoto2 autodetect output
  # and assign model names to variables

  # Store `gphoto2 --autodetect` output.
  # Replace multiple spaces with tabs, select columns 1-2 only
  echo "Auto-detecting cameras:"

  # Display output for user
  gphoto2 --auto-detect

  # Load the camera model names and ports ids into variables...

  gphoto2 --auto-detect | sed 's/ \+ /\t/g' | cut -f 1-2 > gphoto2_autodetect.tmp
  
  # Camera 1
  # ...from line 3, column 1...
  CAMMODEL1=`cat gphoto2_autodetect.tmp | sed -n -e '3p' | cut -f 1`
  # e.g. "Canon EOS 600D"
  # ...from line 3, column 2...
  CAMPORT1=`cat gphoto2_autodetect.tmp | sed -n -e '3p' | cut -f 2`
  # e.g. "usb:001,058"

  # Camera 2
  # ...from line 4, column 1...
  CAMMODEL2=`cat gphoto2_autodetect.tmp | sed -n -e '4p' | cut -f 1`
  # e.g. "Canon EOS 500D"
  # ...from line 4, column 2...
  CAMPORT2=`cat gphoto2_autodetect.tmp | sed -n -e '4p' | cut -f 2`
  # e.g. "usb:001,057"

  # delete the tmp file
  rm gphoto2_autodetect.tmp
}

function manualset_cams {
  # Manually set the camera model names
  CAMMODEL1="Canon EOS 500D"
  CAMMODEL2="Canon EOS 600D"
  echo "Manually setting camera models:"
  echo $CAMMODEL1
  echo $CAMMODEL2
}

function rename_images {
  # Scan Tailor requires one directory with all the images in order -- let's do that
  # This function is called with a text parameter, once for the 'left' folder and once for the 'right' folder.
  echo "Renaming images..."
  mkdir -p ~/bookscan_$TIMESTAMP/combined

  shopt -s nullglob

  cd ~/bookscan_$TIMESTAMP/$1
  FILES=`echo *.JPG`
  if [[ -z $FILES ]]
  then
    echo "Rename: No images to rename of type: $1."
  else
    imgnum=1
    for i in *.JPG; do
      newimg=$(printf "%05d-$1.jpg" ${imgnum}) #05 pad to length of 5
      echo "${newimg} ${i}"
      cp ${i} ../combined/${newimg}
      let imgnum=imgnum+1
    done
  fi
}

function download_from_cams {
  TIMESTAMP=$(date +%Y%m%d%H%M)

  if [ -z "$CAMMODEL1" ]
  then
    echo "Download: Camera 1: not found--please check that it is on and connected."
  else
    echo "Download: $CAMMODEL1: all images..."
    mkdir -p ~/bookscan_$TIMESTAMP/left
    cd ~/bookscan_$TIMESTAMP/left
    gphoto2 --camera "$CAMMODEL1" --get-all-files
    rename_images left
    cd ..
  fi

  if [ -z "$CAMMODEL2" ]
  then
    echo "Download: Camera 2: not found--please check that it is on and connected."
  else
    echo "Download: $CAMMODEL2: all images..."   
    mkdir -p ~/bookscan_$TIMESTAMP/right
    cd ~/bookscan_$TIMESTAMP/right
    gphoto2 --camera "$CAMMODEL2" --get-all-files
    rename_images right 
   cd ..
  fi

    # OLD problem code:
    # gphoto2 processes end with -1 unexpected result even though everything seems to be fine -> hack: true gives exit status 0
    # Debugging argument 
    #   gphoto2 --debug --debug-logfile=dl_sh-cam1-debug.txt \
    # Port argument
    #   --port $GPHOTOCAM1 -P A/store_00010001/DCIM/; true
}


function download_from_model {
  # Function takes two arguments --
  # $1 - the model string for communicating with the camera
  # $2 - the folder and file identifier, e.g. 'left' 'right'
  TIMESTAMP=$(date +%Y%m%d%H%M)
  if [ -z "$1" ]
  then
    echo "Download: Camera not found--please check that it is on and connected."
  else
    echo "Download from model: $1: all images..."
    mkdir -p ~/bookscan_$TIMESTAMP/$2
    cd ~/bookscan_$TIMESTAMP/$2
    gphoto2 --camera "$1" --get-all-files
    cd ..
  fi
}

function download_from_port {
  # Function takes two arguments --
  # $1 - the port string for communicating with the camera
  # $2 - the folder and file identifier, e.g. 'left' 'right'
  TIMESTAMP=$(date +%Y%m%d%H%M)
  if [ -z "$1" ]
  then
    echo "Download: Camera not found--please check that it is on and connected."
  else
    echo "Download from port: $1: all images..."
    mkdir -p ~/bookscan_$TIMESTAMP/$2
    cd ~/bookscan_$TIMESTAMP/$2
    gphoto2 --port "$1" --get-all-files
    cd ..
  fi
}

function delete_from_model {
  # Function takes a model string as an argument
  if [ -z "$1" ]
  then
    echo "Delete: Camera not found--please check that it is on and connected."
  else
    echo "Delete: $1: all existing images from SD card"
    gphoto2 --camera "$1" --recurse --delete-all-files
  fi
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

# manualset_cams
# download_from_cams
# delete_from_cams

echo " "
echo " "
echo "Praxis Camera Deleter"
echo "Version 1.0 (dl.sh) -- transcriptions.english.ucsb.edu"
echo " "
autodetect_cams
echo " "
delete_from_port $CAMPORT1
echo " "
delete_from_port $CAMPORT2
echo " "
echo "Done."
echo " "
