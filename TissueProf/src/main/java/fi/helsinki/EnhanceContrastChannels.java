package fi.helsinki;

import java.awt.Color;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.WaitForUserDialog;
import ij.plugin.ChannelSplitter;
import ij.plugin.Concatenator;
import ij.process.ImageProcessor;
import ij.process.LUT;

public class EnhanceContrastChannels {
	
	static ImagePlus combinedImage;
	static String ImageName;
	static ImagePlus[] channels1;
	static LUT redLUT = LUT.createLutFromColor(Color.blue);
    static LUT greenLUT = LUT.createLutFromColor(Color.green);
    static LUT blueLUT = LUT.createLutFromColor(Color.magenta);
    static LUT whiteLUT = LUT.createLutFromColor(Color.GRAY);
     
    static  LUT[] luts = {redLUT, greenLUT, blueLUT, null};
     
	public static ImagePlus EnhanceContrastChannels(ImagePlus compositeimage, String imageName , String OutputDir, double enhc) {
		
		//System.out.println("enhancing ");
		//System.out.println("Enhance Contrast by : " + enhc);
		
		CompositeImage thisComp = null;
		//Make a new composite image and set cahnnel luts 
		
		try {
		thisComp = new CompositeImage(compositeimage);
		System.out.println("Composite made ");
		} catch (Exception e ) {
			e.printStackTrace();
		}

        thisComp.setChannelLut(redLUT, 1);
        thisComp.setChannelLut(greenLUT, 2);
        thisComp.setChannelLut(blueLUT, 3);
        thisComp.setChannelLut(whiteLUT, 4);
        
        thisComp.show();
        
        channels1 = new ImagePlus[4];
        
        ImageName = imageName;
        
        //Split channels
		ImagePlus[] channels = ChannelSplitter.split(thisComp);
		System.out.println("channels length " + channels.length);
        //IJ.run("compositeImage", "Split Channels"); 
        
		for (int i = 0 ; i < channels.length ; i++) {
        	
        	//IJ.run(channels[i], "Enhance Contrast", "saturated=0.35");
        	ImageProcessor channelProcessor = channels[i].getProcessor();

        	IJ.getImage();
        	
        	WindowManager.getActiveWindow();

        	int a = i + 1 ;
        	channels[i].show();
        	
        	//Run Enhance Contrast
        	IJ.run("Enhance Contrast...", "saturated="+enhc+"");
        	System.out.println("outputDir " + OutputDir);
        	System.out.println("imageName " + ImageName);
        	//channels[i].show();
        	ImagePlus thisImage = IJ.getImage(); 
        	//Save a temporary channel duplicate before correcting channel colors 
        	IJ.saveAs(IJ.getImage(), "tiff", OutputDir + "/" + ImageName + "_" + "EnhancedContrast" + "_" + "CDUPLICATE" + "_" + "C" + a);
      
        	channelProcessor = null;
        	
        }
        
        //Free up resources
        WindowManager.closeAllWindows();
        
        String[] mergeTitles = new String[4];
        
        //Get titles of channel images before putting them into the composite image
        for (int a = 1 ; a < 5 ; a++) {
        	IJ.open(OutputDir + "/" + ImageName + "_" + "EnhancedContrast" + "_" + "CDUPLICATE" + "_" + "C" + a + ".tif");
        	
        	channels1[a-1] = IJ.getImage();
        	//mergeTitles[a-1] = WindowManager.getActiveWindow().getName();
        	String thisTitle = IJ.getImage().getTitle();
        	System.out.println("toMerge names " + thisTitle);
        	mergeTitles[a-1] = thisTitle;
        }
        
        
        //Make composite image from the individual processed channels using their titles. 
        ImagePlus newImage = IJ.createImage("Combined " , "RGB", channels[0].getWidth(), channels[0].getHeight(), 4, 1, 1);
        IJ.run("Merge Channels...", "c3="+mergeTitles[0]+" c2="+mergeTitles[1]+" c6="+mergeTitles[2]+" c4="+mergeTitles[3]+" create keep");
        IJ.run("Make Composite", "");
        IJ.getImage().setDisplayMode(IJ.COLOR);
        IJ.getImage().setProp("CompositeProjection", "null");
        IJ.run("Arrange Channels...", "new=2143");
        
        combinedImage = IJ.getImage();
        IJ.saveAs(combinedImage,  "tiff", OutputDir + "/" + imageName + "_SlicedAllChannels");
     
        
        for (ImagePlus imch : channels) {
        	imch.close();
        	imch.flush();
        	imch = null;
        }
        
        newImage.close();
        newImage.flush();
        newImage = null;
        
        if (thisComp != null) {
        		
        	thisComp.close();
        	thisComp.flush();
        	thisComp = null;
        }
        
        return combinedImage;
        	
	}

	public static ImageProcessor enhanceContrast(ImageProcessor processor, double MinValue, double MaxValue){
		
		ImageProcessor contrasted = processor.duplicate();
		contrasted.setMinAndMax(MinValue, MaxValue);
		return contrasted;
		
	}
	
	public static void clear () {
		
		//Free up resourcess
		
		combinedImage.close();
		combinedImage.flush();
		combinedImage = null;

        for (ImagePlus imthis : channels1) {
        	imthis.close();
        	imthis.flush();
        	imthis = null;
        }
       
        
        
        channels1 = null;
		
	}

}