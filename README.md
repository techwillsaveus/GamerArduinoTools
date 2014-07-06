DIY Gamer Arduino Tools
=======================

Contains DIY Gamer tools for the Arduino IDE

INSTALLATION
============

1. Download [tools.zip from the releases](https://github.com/twsu/GamerArduinoTools/releases/download/v0.1/tools.zip)
2. Unzip tool.zip in Documents -> Arduino (this should result in a new tools folder containing Painter and Animator folders)
3. Restart the Arduino IDE and two new awesome options should appear under Arduino > Tools.

USAGE
=====

Image Painter:
--------------

[Drawing]:
* Click & drag to paint
* Shift+Click & drag to erase (there are paint bucket and eraser icons in the top menu which will do the same)
* Press the 'i' key to invert the image
* Press the X button (or use the spacebar key) to clear the drawing

[Converting to code]:
* Press the Upload(right arrow) button to upload(be aware, it erases existing code!) (or use the 'u' key)
* Press the Insert Code(1,0,0,1) button (or 'C' key) to insert the code (but not upload). You will have the option to keep your existing code (so this is handy when adding a drawing to an existing program)
* Press the Copy Code button (or 'c' key) to give your image a name and save it to the clipboard. This is useful when inserting multiple images into the same program (to make a basic animation or interaction)
* Press the eye glasses button (or 'V' key) to view values. This will list the byte value of each pixel (which is handy to correlate with the byte array image format (e.g. B10011001 with gamer.printImage) and x/y two dimensional array index location (for use with gamer.display[xIndex][yIndex] = pixelValue(0 or 1)).

Animation Generator:
--------------------

[Drawing:]
Mostly the same as Image Painter
Clearing (a frame) is  backspace.
The X icon (or 'x' key) and it clear EVERYTHING (all frames) -> similar to creating a new document without saving the previous one

[Animating]:
* Use the + icon (or '=' key) to insert a blank/empty frame
* Use the Duplicate Current Frame button (or '+' key) to append a copy of the current frame
* Use the - icon (or '-' key) to remove the current frame
* Use Alt(on PC)/Option(on Mac)+C to copy a frame
* Use Alt(on PC)/Option(on Mac)+V to paste a frame
 
[Previewing]:
* Use the <- icon (left cursor key) to go to the previous frame
* Use the -> icon (right cursor key) to go to the next frame
* Use the spacebar key to toggle playback
* Use the delay slider to change the delay between frames (value is in milliseconds (e.g. 40ms for 25 frames per second))
[Converting to Code and saving]:
* Similar to Painter, Upload replaces all existing code(if any) and uploads the animation immediately
* Insert Code allows you to name your animation (and therefore use multiple animations in the same sketch)
* The Save icon (or 's' key) saves your current animation to a file on the computer for later editing. Give it any name/extension, but essentially it's a CSV file
* The Load icon (or 'l' key) loads an existing animation for further editing/code conversion/upload/etc. Note that on both save/load dialog you must press the Select button after choosing a file
[Using Animator as a sequencer/recorder]:
You can still draw while playing back, which makes it fun/easy to record simple animations. Here's how:
Insert a lot of blank frames (by pressing '=' repeatedly)
Press the play button (turn playback on)
Draw something while playback is on
Hint! The 'F' key adds a lot of blank frames. Use the a large delay when recording to draw bigger shapes. Have fun improvising!

Credits
=======
Illustration by  Edward Carvalho-Monaghan 
Interface design by Adam Shepard
Code by George Profenza