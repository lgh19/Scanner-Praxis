# Scanner-Praxis

An application used to import images from a camera, process them, and output in an OCR PDF. 

Due to a series of poor choices, the application is in java and the GUI is in javafx. 

Note: Many of the operations require JDK-9 and above.

Pre-Requirements:

ScanTailor
http://scantailor.org

Image Magick
https://www.imagemagick.org/

Tesseract OCR
https://github.com/tesseract-ocr/tesseract

PDFtk Server
https://www.pdflabs.com/tools/pdftk-server/

Mac Install:                    | Linux Install:
-----------------------------   | --------------------------
sudo port selfupdate            | sudo apt-get update
sudo port install scantailor    | sudo apt-get install scantailor
sudo port install ImageMagick   | sudo apt-get install imagemagick
sudo port install tesseract     | sudo apt-get install tesseract-ocr
sudo port install tesseract-eng | sudo apt-get install libtesseract-dev

PDFtk Server must be manually installed