# Scanner-Praxis

Run with  ./auto_scan.sh "input_folder" "output_folder"

Looks into the input folder, taking all jpg, JPG or jpeg files
and converts them into tif files using scan tailor and it's
build in arguments. Then it converts all the tif files into a
single PDF file using the 'convert' command. Lastly, it
adds an OCR layer to the PDF using  'pdfsandwich'

TODO
- Update pdfsandwich (requires internet connection)
	- Use debian file on desktop for version 0.1.6
- Look into problem where the PDF file becomes corrupted
	when converting from TIF files if there are too many
	JPG files in the input folder
- Change the argument to a output file name instead of
	an output folder name

