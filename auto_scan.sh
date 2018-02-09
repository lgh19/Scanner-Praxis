#First runs scantailor with arguments

scan_tailor () {
	for f in $1* #iterate the directory
	do
		if [[ $f == *.JPG || $f == *.jpeg || $f == *.jpg ]] #only interested in .jpeg or .jpg
			then
			INPUT_FILELIST="$INPUT_FILELIST $f"
		fi
	done
	
	#scantailor options
	scantailor-cli \
		--layout=$LAYOUT_OPTION \
		--layout-direction=$LAYOUT_DIRECTION \
		--orientation=$ORIENTATION \
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
LAYOUT_OPTION="0" #<0: auto detect | 1: one page | layout 1.5: one page, layout but cutting is needed | 2: two page layout>
LAYOUT_DIRECTION="lr"
ORIENTATION="left" #<left|right|upsidedown|none>
ROTATE=0.0
DESKEW="auto" #<auto|manual> Default: auto
CONTENT_DETECTION="normal" #<cautious|normal|aggressive> Default: normal
MARGINS=25.4 #In MM
ALIGNMENT="center"
DPI=600
OUTPUT_DPI=600 #sets x and y dpi. default: 600
COLOR_MODE="black_and_white" #black_and_white, color_grayscale, mixed
WHITE_MARGINS=false #Default: false
THRESHOLD= #n<0 thinner, n>0 thicker; default: 0
DESPECKLE="normal" #Default: normal <off|cautious|normal|aggressive>
DEWARPING="off" #Default: <off|auto>
DEPTH_PERCEPT=2.0 #Default: 2.0
START_FILTER=4 #Default: 4
END_FILTER=6 #Default: 6
OUTPUT_PROJECT="$1/Project.ScanTailor"

INPUT_FILELIST=""

#scantailor commandline takes a list of images as input, not input directory
#need to go through all of the images of an input directory to construct the parameter
#Starting ScanTailor. JPGs will be converted to tif files for tesseract to run OCR on.
echo Starting ScanTailor.
scan_tailor $1 $2
echo Finished ScanTailor. Results in out folder.


#Convert all tifs in the directory into a multi tiff with Image Magick. Tiff is lossless, so this combination loses no data.
cd $2
echo Combining pictures into PDF #It's actually a multitiff, but called a PDF for the user
convert *.tif output.tiff

#With this multitiff OCR with tesseract can be run. Tesseract can only run on individual images, which is why we give it a multitiff rather than a pdf
echo Adding OCR Layer to PDF
tesseract output.tiff ../outputOCR -l eng pdf

#echo Performing Cleanup
#pwd
#cd $1
#pwd
#mkdir in
#mv *.JPG in/
#rm -r cache
