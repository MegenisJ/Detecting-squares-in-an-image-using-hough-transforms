# Detecting-squares-in-an-image-using-hough-transforms
Coursework to detect squares in images. Initial brief required detection of 1 square size, as an extra feature I made the program able to detect multiple square sizes.

This program uses pgm grayscale image format only as inputs although it outputs ppm colour format to highlight the squares.

Args that must be included.
 - starting image a string in "".
 - target size for the square in pixels
 - f1 (strength of highlights for candidate peaks in the hough space)
 - f2 (confidence in the square)
 - number of sizes (amount of different sized squares. Entering a value above 1 will make the program prompt you for more square sizes)

Some example program inputs:
  "java FindingSquares im1.pgm 200 0.2 0.5 1"

 "java FindingSquares multiplesizetest.pgm 173 0.25 65 3"
 when prompted:
	first input :78
	second input :283


Known bug: confidence values ranges are incorrect / inconsistent and will spike into the 60s, range should be between 0 and 1, using values between 0 and 100 can provide good results right now. 
