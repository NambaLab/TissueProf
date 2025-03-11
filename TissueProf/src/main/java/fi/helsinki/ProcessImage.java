package fi.helsinki;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import ij.process.LUT;
import loci.formats.FormatException;
import loci.plugins.BF;


public class ProcessImage {
	
	static Window[] winds;
	//static Window[] imageWinds = new Window[4];
	static RoiManager newManager;
	static ShapeRoi zoneXor;
	Roi[] backgroundRois;
	Roi[] zoneRois;
	Roi zoneXorr;
	static int ImageChannelNo;
	
	public void processImage(String inputDir2, String OutputDir, String imageName, String suffix, boolean doOverlapAnalysis, 
			boolean doReanalysis, int zoneNo, double enhc) throws FormatException, IOException {
	 
	String inputDir3 = inputDir2.replaceAll("\\\\", "/");
		
	IJ.log("Opening image... " + inputDir3 + "/" + imageName);
	System.out.println("path " + inputDir3 + "/" + imageName);
	
	ImagePlus imp2 = null;
	
	ImagePlus[] images = new ImagePlus[1];
	
	@SuppressWarnings("unused")
	String DispName = null;
	
	if (doOverlapAnalysis==true) {
			
		//Open the image file using Bio-Formats
			
		images = BF.openImagePlus(inputDir3 + "/" + imageName);
			
		IJ.log("Processing image... " + inputDir3 + "/" + imageName);	
		
		System.out.println(images.length);
		
		images[0].show();

		DispName = IJ.getImage().getTitle();
		
		images[0] = IJ.getImage();
	
		images[0].show();
		imp2 = IJ.getImage();
		
		ImageChannelNo = imp2.getNChannels();
		
		System.out.println("image channels " + ImageChannelNo);
		
		
	} else if (doReanalysis==true) {
		//Verify that the input image was a 4-channel single slice .tif file
		if(!suffix.matches(".tif")) {
			IJ.log("Please try again with a 4-channel single plane .tif image file "
					+ "Such as the SlicedAllChannels from the Results Folder");
			
			WaitForUserDialog tryAgain = new WaitForUserDialog("Select an appropriate single plane 4-channel .tif image file"
					+ " (Such as the SlicedAllChannels in the Results Folder)");
			tryAgain.show();
		}
		
		//Open image using opener
		try {
			IJ.open(inputDir3 + "/" + imageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IJ.log("Processing image... " + inputDir3 + "/" + imageName);	
		
		images[0] = IJ.getImage();
		//Show image
		images[0].show();
		
		//Second reference for image
		imp2 = IJ.getImage();
		
		ImageChannelNo = imp2.getNChannels();
		
		System.out.println("image channels " + ImageChannelNo);
		
	}
	
	
	
	
	//WAIT FOR USER FOR SLICE SELECTION
	
	
	if (doOverlapAnalysis==true) {
		WaitForUserDialog waitforuser1 = new WaitForUserDialog("Slice Selection", "Select the slice you would like to analyze");
		waitforuser1.setVisible(true);
		
		int slice = IJ.getImage().getZ();
		//IJ.getImage().getZ();
		//IJ.run("Duplicate...", "duplicate channels=1-4" "slices="+slice+"");	
		System.out.println("slice no: " + slice);
		
		imp2 = new Duplicator().run(images[0], 1, ImageChannelNo, slice, slice, 1, 1);

	} else if (doReanalysis == true) {
	
		//No need for slice selection since single slice image was verified for doReanalysis mode.
	
	}
	
	//Load an instance of RoiManager
	RoiManager.getRoiManager();
    RoiManager.getInstance().reset();
    
    //Set background ROIs
    WaitForUserDialog setbackground = new WaitForUserDialog("Mark 3 regions for background measurement and add to the ROI Manager");
    setbackground.show();

    boolean correctedBackground = false;
    
    //Verify the number of background ROIs 
    while (correctedBackground==false) {    	
    	backgroundRois = RoiManager.getInstance().getRoisAsArray();
    	if (backgroundRois.length!=3) {
    		WaitForUserDialog addback = new WaitForUserDialog("Please add exactly 3 background ROIs to the ROI Manager");
    		addback.show();
    		
    	} else if (backgroundRois.length==3) {
    		correctedBackground=true;
    	}

    }
    
    //Save background ROIs
    runStardist.saveRois(OutputDir, imageName + "_backgroundROIs");
    
    //Reset ROI manager and remove lingering ROIs from image 
    RoiManager.getInstance().deselect();
    IJ.getImage().killRoi();
    
    if (images != null && images.length > 0) {		
    	
    	LUT redLUT = LUT.createLutFromColor(Color.blue);
    	LUT greenLUT = LUT.createLutFromColor(Color.green);
    	LUT blueLUT = LUT.createLutFromColor(Color.magenta);
    	//LUT whiteLUT = LUT.createLutFromColor(Color.GRAY);
    	
    	LUT[] luts = {redLUT, greenLUT, blueLUT, null};
        ImagePlus image = imp2;

        RoiManager.getInstance().deselect();
        imp2.killRoi();
        
        ImagePlus[] originalChannels = ChannelSplitter.split(imp2);
    
		//Trigger garbage collector to free up resources
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IJ.wait((5000));
		
		System.gc();
    	//Set luts to channels
        int c=0;
        for (ImagePlus originalChannel : originalChannels) {
        	originalChannel.setLut(luts[c]);
        	originalChannel.show();
        	c++;
        }
        
        winds = Window.getWindows();
        
        Window[] imageWinds = new Window[ImageChannelNo];
        
        //detect image windows and save into array
        c = 0;
        for (Window wink: winds) {
        	if (wink.toString().matches("C\\d+-.*")) {
        		imageWinds[c] = wink;
        		c++;
        	}
        }

        //save original duplicate images 
        c=0;
        for (Window imageWink: imageWinds) {
        	if (imageWink.toString().matches("C\\d+-.*")) {
        		String[] stirs = imageWink.toString().split("-");
        		IJ.selectWindow(imageWink.toString());
        		ImagePlus activeImage = IJ.getImage();
        		int a = c + 1;
        		IJ.saveAs(activeImage, "tiff", OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + a);
        		c++;
        	}
        	
        }

        
        EnhanceContrastChannels encon = new EnhanceContrastChannels();
        
        //Create a version of the image file where contrasts of all channels have been enhanced 
        ImagePlus enhanced = encon.EnhanceContrastChannels(imp2, imageName, OutputDir, enhc);
        
        //show enhanced contrast image
        enhanced.show();     
       
        //Verify channel number of enhanced contrast image
        //System.out.println("Image channels " + enhanced.getNChannels());
        
        winds = Window.getWindows();
        
        c = 0;
        for (Window wink: winds) {
        	if (wink.toString().matches("C\\d+-.*")) {
        		//System.out.println("Image Window " + wink.toString() + " " + c);
        		imageWinds[c] = wink;
        		c++;
        	}
        }
	    
	    IJ.selectWindow(imageName + "_SlicedAllChannels" + ".tif");
	    
	    //Split channels
	    ImagePlus[] channels = ChannelSplitter.split(enhanced);
	    //Save individual channels for segmentation purposes
	    for (int i = 0 ; i < ProcessImage.ImageChannelNo ; i++) {
	    	channels[i].show();
	    	int a = i + 1;
	    	IJ.saveAs(channels[i], "tiff", OutputDir + "/" + imageName + "_" + "EnhancedContrast" + "_" + "C" + a);
	    }
	    
	    
	    ////WAIT FOR USER FOR ZONE DENOTATION
	    
	    //Get ROI Manager instance and reset
	    RoiManager.getRoiManager();
	    RoiManager.getInstance().reset();
	    
	    IJ.selectWindow(imageName + "_SlicedAllChannels" + ".tif");
	    
	    //Mark Zones 
		WaitForUserDialog waitforuser = new WaitForUserDialog("Mark Zones", "Mark Zones of Interest and add to ROI Manager");
		waitforuser.setVisible(true);
		
		boolean correctedZones = false;
		
		//Make sure the number of zones corresponds to the number entered earlier
		while (correctedZones==false) {
						
			zoneRois = RoiManager.getInstance().getRoisAsArray();
			if (zoneRois.length!=zoneNo) {
				//System.out.println("zoneRois.length!=zoneNo");
				WaitForUserDialog correctZones = new WaitForUserDialog("Please add the same number of zones to the ROI Manager as the number you input at the beginning");
				correctZones.show();
			} else if (zoneRois.length==zoneNo) {
				correctedZones = true;
			}
		}

	    //Save ZoneROIs
	    runStardist.saveRois(OutputDir, imageName + "_ZoneROIs");
	   
	    ShapeRoi[] zoneShapes = new ShapeRoi[zoneRois.length];
	    
	    //Make ShapeRois to later use for ROI operations
	    c=0;
	    for (Roi zoneRoi:zoneRois) {
	    	zoneShapes[c] = new ShapeRoi(zoneRoi);
	    	c++;
	    }
	    
	    //Initialization of ZoneXor (All Zones XOR Combined) ROI
	    zoneXor = zoneShapes[0];	    
	    
	    double zoneX = 0;
	    double zoneY = 0;
	    
	    if (zoneShapes.length==1) {
	    	zoneX = zoneShapes[0].getBounds().getLocation().getX();
	    	zoneY = zoneShapes[0].getBounds().getLocation().getY();
	    }
	    
	    //verify zoneShapes array
	    //System.out.println("zoneshapes0height" + zoneShapes[0].getBounds().getHeight());
	    
	    for (int i = 1 ; i < zoneShapes.length ; i++) {
	    	zoneXor = ((ShapeRoi) zoneXor.clone()).xor(zoneShapes[i]);   
	    }
	    
	    //Prepare expanded zones
	    
	    RoiManager.getInstance().reset();
	    
	    ShapeRoi zoneXorNow = zoneXor;
	    
	    if (zoneShapes.length==1) {
	    	zoneXorr = zoneXorNow.getRois()[0];
	    	zoneXorr.setLocation(zoneX, zoneY);
	    	
	    }
	    	else {
	    		zoneXorr = zoneXorNow.shapeToRoi();
	    	}

	    RoiManager.getInstance().addRoi(zoneXorr);
	    
	    
	    Roi zoneXorExpanded = expandZones(IJ.getImage(), zoneXorr);    
		
	    RoiManager.getInstance().reset();
	   
	    RoiManager.getInstance().add(zoneXorr, 0);
	     
	    RoiManager.getInstance().add(zoneXorExpanded, 1);

	    //ApplyZone ROIs to image and clear outside to create ZonesOnly images
        for (int i = 0 ; i < imageWinds.length ; i++) {
        	int a = i + 1;
        	IJ.open(OutputDir + "/" + imageName + "_" + "EnhancedContrast" + "_" + "C" + a + ".tif");
        	ImagePlus nowImage = IJ.getImage();
        	//nowImage.setRoi(zoneXorRois, true);
        	RoiManager.getInstance().select(0);
        	IJ.setForegroundColor(0,0,0);
        	IJ.run("Clear Outside");
        	IJ.getImage().getProcessor().fillOutside(RoiManager.getInstance().getRoi(0));
        	ImagePlus zonesOnly = IJ.getImage();

        	IJ.saveAs(zonesOnly, "tiff", OutputDir + "/" + imageName + "_" +"EnhancedContrast" + "_" + "ZonesOnly" + "_C" + a);
        	nowImage.getWindow().dispose();
        	nowImage = null;
        	zonesOnly = null; 
        }
        
        //Apply ZonesExtended ROI to the channels to create ZonesOnlyExpanded images
        for (int i = 0 ; i < imageWinds.length ; i++) {
        	int a = i + 1;
        	IJ.open(OutputDir + "/" + imageName + "_" + "EnhancedContrast" + "_" + "C" + a + ".tif");
        	ImagePlus nowImage = IJ.getImage();

        	RoiManager.getInstance().select(1);
        	IJ.run("Clear Outside");
        	IJ.getImage().getProcessor().fillOutside(RoiManager.getInstance().getRoi(1));
        	ImagePlus zonesOnly = IJ.getImage();

        	IJ.saveAs(zonesOnly, "tiff", OutputDir + "/" + imageName + "_" +"EnhancedContrast" + "_" + "ZonesOnlyExpanded" + "_C" + a);
        	nowImage.getWindow().dispose();

        	nowImage = null;
        	zonesOnly = null;
        }
        
        
        //Clean up resources
        
        imp2.killStack();
        enhanced.killStack();
        for (ImagePlus channel : channels) {
        	channel.killStack();
        }
        
        imp2.close();
        images[0].close();
        enhanced.close();
        image.close();
        
        int chno = 0;
        for (ImagePlus channel : channels) {
        	
	        if (channel.getWindow()==null) {
	        	continue;
	        } else {
	        	channel.getWindow().dispose();
	        }
        chno++;
        }
        
        if (!(imp2.getWindow()==null)) {
        	imp2.getWindow().close();
        	imp2.getWindow().dispose();
        }
        
        
        if (!(enhanced.getWindow()==null)) {
        	enhanced.getWindow().close();
        	enhanced.getWindow().dispose();
        }
        
        
        encon.clear();
        
        imp2.flush();
        images[0].flush();
        enhanced.flush();
        image.flush();
        
        enhanced=null;
        images[0]=null;
        images =null;
        imp2=null;
        image =null;
        
        for(Window wind : winds) {
        	if (!wind.getName().matches("frame0") && !wind.getName().matches("win0") && !wind.getName().matches("frame1")) {	
	        		wind.dispose();
		        	wind = null;    	
        	}
        }
        
        winds = null;
        
        for (ImagePlus channel : originalChannels) {
        	channel.flush();
        	channel = null;
        }
        originalChannels = null;
        
        
        for(Window wind : imageWinds) {
        	if (!wind.getName().matches("frame0") && !wind.getName().matches("win0") && !wind.getName().matches("frame1")) {
	        		wind.dispose();
		        	wind = null;
        	}	
        }
        
        imageWinds = new Window[4];
      
        WindowManager.closeAllWindows();
        
        IJ.log("Completing image processing...");
        
        //Trigger garbage collector
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		IJ.wait((5000));
		
		System.gc();
    	
	   }
        
    
    
    
	}
	

	public Roi expandZones(ImagePlus imp, Roi zoneXOR) {
		
		//System.out.println("expanding...");
		
		imp.show();
		
		Roi zonesRoi = zoneXOR;
		
		RoiManager.getInstance().reset();
		
		RoiManager.getInstance().addRoi(zonesRoi);
		
		double thisX = (double) zonesRoi.getBounds().getLocation().getX();
		
		double thisY = (double) zonesRoi.getBounds().getLocation().getY();
		
		Rectangle boundingrect = zonesRoi.getBounds();
		
		Roi boundingrectRoi = new Roi(boundingrect);
		
		boundingrectRoi.setName("BoundingRectangle");
		boundingrectRoi.setStrokeColor(Color.white);
		
		double[] roi0Pos = {thisX, thisY};
		
		EllipseRoi thisEllipse = new EllipseRoi(roi0Pos[0]-50,roi0Pos[1]-50,roi0Pos[0]+50,roi0Pos[1]+50, 1);
		
		thisEllipse.setName("Circle");
		
		//Get all points from the created circle
		int[] circleXs = thisEllipse.getPolygon().xpoints;
		int[] circleYs = thisEllipse.getPolygon().ypoints;
		
		Polygon circlePoly = thisEllipse.getPolygon();
		ArrayList<Roi> expandRois = new ArrayList<Roi>();
		ShapeRoi thisShape = new ShapeRoi(zonesRoi);
		ShapeRoi expandedZone = thisShape;
		
		//Translate zoneROi along the created ROI and make ROIs at each point of the circle, and make an OR 
		//combination of all ROIs to create the expanded zone
		for (int i = 0 ; i < circleXs.length ; i++) {
			int a = i + 1;
			Roi thissRoi = (Roi) zonesRoi.clone();
			thissRoi.setLocation(circleXs[i], circleYs[i]);
			thissRoi.setName("Roi" + a);
			expandRois.add(thissRoi);
			
			ShapeRoi thissShape = new ShapeRoi(thissRoi);
			expandedZone = ((ShapeRoi) expandedZone.clone()).or(thissShape);
		}
				
		return expandedZone;
	}
	
	
	
	
	
	
	
	ProcessImage(){
		this.backgroundRois = backgroundRois;
		this.zoneRois = zoneRois;
	}
	
}
