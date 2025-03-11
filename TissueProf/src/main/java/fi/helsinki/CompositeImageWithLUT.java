package fi.helsinki;
import java.awt.Color;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.process.LUT;

public class CompositeImageWithLUT {

	// Example of creating a custom LUT
    public static CompositeImage createCompositeImage(ImagePlus image) {
        // Create a CompositeImage from the original ImagePlus
        CompositeImage compositeImage = new CompositeImage(image, CompositeImage.COLOR);

        // Define different LUTs for different channels
        LUT redLUT = LUT.createLutFromColor(Color.blue);
        LUT greenLUT = LUT.createLutFromColor(Color.green);
        LUT blueLUT = LUT.createLutFromColor(Color.magenta);
        
        // Set LUTs for individual channels
        compositeImage.setChannelLut(redLUT, 1);   // Channel 1 (Red)
        compositeImage.setChannelLut(greenLUT, 2); // Channel 2 (Green)
        compositeImage.setChannelLut(blueLUT, 3);  // Channel 3 (Blue)

        return compositeImage;
    	}
        
	    //return compositeImage2;
	}
    




