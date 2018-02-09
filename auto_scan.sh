#First runs scantailor with arguments
#layout option specifies if the image scan is 1 page or 2
#0 is autodetect, 1 is one page layout


scan_tailor () {
	for f in $1* #iterate the directory
	do
		if [[ $f == *.JPG || $f == *.jpeg || $f == *.jpg ]] #only interested in .jpeg or .jpg
			then
			INPUT_FILELIST="$INPUT_FILELIST $f"
		fi
	done
	
	#scantailor command
	scantailor-cli \
		--layout=$LAYOUT_OPTION \
		--layout-direction=$LAYOUT_DIRECTION \
		--rotate=$ROTATE \
		--deskew=$DESKEW \
		--content-direction=$CONTENT_DETECTION \
		--margins=$MARGINS \
		--alignment=$ALIGNMENT \
		--dpi=$DPI \
		--output-dpi=$OUTPUT_DPI \
		--color-mode=$COLOR_MODE \
		--white-margins=$WHITE_MARGINS \
		--threshold=$THRESHOLD \
		--despeckle=$DESPECKLE \
		--dewarping=$DEWARPING \
		--depth-perception=$DEPTH_PERCEPT \
		--start-filter=$START_FILTER \
		--end-filter=$END_FILTER \
		--output-project=$OUTPUT_PROJECT \
		$INPUT_FILELIST \
		$2
}

#Edit these options for universal scantailor options
LAYOUT_OPTION=0
LAYOUT_DIRECTION="lr"
ORIENTATION="left"
ROTATE=0
DESKEW="auto"
CONTENT_DETECTION="normal"
MARGINS=1
ALIGNMENT="center"
DPI=600
OUTPUT_DPI=600
COLOR_MODE="black_and_white" #black_and_white, color_grayscale, mixed
WHITE_MARGINS=true
THRESHOLD=0
DESPECKLE="normal"
DEWARPING="off"
DEPTH_PERCEPT=2.0
START_FILTER=4
END_FILTER=6
OUTPUT_PROJECT="$2/imginfo"

INPUT_FILELIST=""

#scantailor commandline takes a list of images as input, not input directory
#need to go through all of the images of an input directory to construct the parameter
echo Beginning Tif Conversion
scan_tailor $1 $2
echo Finished Tif Conversions


#Convert all tifs in the directory into a multi tiff with Image Magick. Tiff is lossless, so this combination loses no data.
dir=`pwd`
cd $2
echo Converting tifs to multitiffs
convert *.tif output.tiff
echo Adding OCR Layer
tesseract output.tiff ../OCRoutput
#echo Performing Cleanup
#rm *.tif
#rm -r cache
#rm output.pdf
