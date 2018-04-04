# Scanner-Praxis

Tools Used:
ScanTailor
http://scantailor.org

Image Magick
https://www.imagemagick.org/

Tesseract OCR
https://github.com/tesseract-ocr/tesseract


Run with:
```
./auto_scan.sh <input_folder> <output_folder>
```

Looks into the input folder, taking all jpg, JPG or jpeg files
and converts them into tif files using scan tailor and it's
build in arguments. Then it converts all the tif files into a
a multi tiff using Image Magick. This allows Tesseract to run OCR on the document before an compression occurs. Finally, a pdf is created by tesseract with a searchable OCR layer.