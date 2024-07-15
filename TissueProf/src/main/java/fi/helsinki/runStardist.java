
package fi.helsinki;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
//import java.util.stream;


import ij.IJ;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.RoiEncoder;
import ij.measure.Measurements;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;



public class runStardist {
	static Roi[][] allRois = new Roi[4][4]; 
	
	static int IndexCount;
	
	public static Roi[][] runStardist(String inputDir2, String OutputDir, String imageName, Window[] imageWinds, boolean useModel, 
			Boolean[] channelSelection, String[] modelPaths, boolean[] deleteRois, double[] deleteArea, double[] probs, double[] nms, 
			int nTiles, String[] ChRoisPaths, boolean doOverlapAnalysis, boolean doReanalysis, Roi ZoneXorr){
		
		if (doOverlapAnalysis==true) {
			for (int i = 0 ; i < 4 ; i++) {
				int a= i + 1;
		        System.out.println("Running Stardist... Ch " + a);	
				 if(channelSelection[i]==false) {continue;}
				 
				 //RoiManager.getInstance().reset();
		        	//newManager.getInstance();
				 	RoiManager.getRoiManager();
				 	RoiManager.getInstance().reset();
		        	
				 	WindowManager.closeAllWindows();
				 	IJ.open(OutputDir + "/" + imageName + "_" +"EnhancedContrast" + "_" + "ZonesOnlyExpanded" + "_C" + a + ".tif");        
		        	
		        	WindowManager.getActiveWindow().setName(imageName);
		        	String segmentImage = IJ.getImage().getTitle();
		        	
		        	IJ.getImage().killRoi();
		        	
		        	String currentModel = modelPaths[i];
		        	double prob =  probs[i];
		        	double nmsi = nms[i];
		        	
		        	
		        	if (segmentImage.startsWith("\\")){
		        		segmentImage = segmentImage.replace("\\", "\\\\");
		        	}
		        	
		        
		        	IJ.log(segmentImage);
		        	
		        	if (useModel == true) {
		        		try {
		        		
		        		IJ.log("Starting Segmentation...");
		        		
				        IJ.run("Command From Macro", "command=[de.csbdresden.stardist.StarDist2D], args=['input':'"+segmentImage+"',"
				        		+ "'modelChoice':'Model (.zip) from File', 'normalizeInput':'true', 'percentileBottom':'1.0', "
				        		+ "'percentileTop':'99.49999999999999', 'probThresh':'"+prob+"', 'nmsThresh':'"+nmsi+"', 'outputType':'ROI Manager', "
				        		+ "'modelFile': '"+currentModel+"', "
				        		+ "'nTiles':'"+nTiles+"', 'excludeBoundary':'2', 'roiPosition':'Automatic', 'verbose':'false', 'showCsbdeepProgress':'true', 'showProbAndDist':'false'], "
				        		+ "process=[false]");
				        
		        		
		        		IJ.log(segmentImage);
		        		/*
				        IJ.run("Command From Macro", "command=[de.csbdresden.stardist.StarDist2D], args=['input':'"+segmentImage+"', "
				        		+ "'modelChoice':'Model (.zip) from File', 'normalizeInput':'true', 'percentileBottom':'1.0', "
				        		+ "'percentileTop':'99.49999999999999', 'probThresh':'0.6000000000000001', 'nmsThresh':'0.3', "
				        		+ "'outputType':'ROI Manager', "
				        		+ "'modelFile':'C:\\\\Users\\\\Emre\\\\Downloads\\\\stardist-master\\\\examples\\\\2D\\\\models\\\\Sox2-mouse-ncx-v3\\\\Sox2-mouse-ncx-v3.zip', "
				        		+ "'nTiles':'2', 'excludeBoundary':'2', 'roiPosition':'Automatic', 'verbose':'false', 'showCsbdeepProgress':'true', 'showProbAndDist':'false'], "
				        		+ "process=[false]");
						*/
		        			
		        		} catch (Exception e) {
				        	e.printStackTrace();
				        }
		        	}
		        	else { 
		        		
		        		RoiManager.getRoiManager();
		        		RoiManager.getInstance();
		        		
		        		IJ.log("Starting Segmentation...");
		        		IJ.run("StarDist 2D");
		        		
		        		/*
		        		try {
		    		        IJ.run("Command From Macro", "command=[de.csbdresden.stardist.StarDist2D], args=['input':'"+segmentImage+"', "
		    		        		+ "'modelChoice':'Versatile (fluorescent nuclei)', 'normalizeInput':'true', 'percentileBottom':'1.0', "
		    		        		+ "'percentileTop':'99.49999999999999', 'probThresh':'0.65', 'nmsThresh':'0.3', 'outputType':'ROI Manager', "
		    		        		+ "'modelFile':'C:\\\\Users\\\\Emre\h\\\Downloads\\\\stardist-master\\\\examples\\\\2D\\\\models\\\\Sox2-mouse-ncx-v3\\\\Sox2-mouse-ncx-v3.zip', "
		    		        		+ "'nTiles':'4', 'excludeBoundary':'2', 'roiPosition':'Automatic', 'verbose':'false', 'showCsbdeepProgress':'true', 'showProbAndDist':'false'], "
		    		        		+ "process=[false]");
		    		        } catch (Exception e) {
		    		        	e.printStackTrace();
		    		        }
		        		*/
		        		
		        	}	
			        
		        	if  (RoiManager.getInstance() == null) {
		        		RoiManager.getRoiManager();
		        	}
		        	
			        //System.out.println("Segmentation count Ch " + a + RoiManager.getInstance().getCount());
	
			        //System.out.println("Count Before Removal " + RoiManager.getInstance().getCount());
			        
			        Roi[] ChContain = RoiManager.getInstance().getRoisAsArray();
					
			        ArrayList<Roi> ChContains = new ArrayList<Roi>();
			        
			        for (Roi theRoi : ChContain) {
			        	ChContains.add(theRoi);
			        }
			        
			        //System.out.println("ChContains size " + ChContains.size());
			        
			        RoiManager.getInstance().reset();
			     
			        synchronized(ChContains) {
				        for (Roi theRoi : ChContains) {
				        	IJ.getImage().setRoi(theRoi);
				        	ImageStatistics roiPos = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.CENTROID, IJ.getImage().getCalibration());
				        	double thisRoiXX = roiPos.xCentroid/IJ.getImage().getCalibration().pixelWidth;
							double thisRoiYY= roiPos.yCentroid/IJ.getImage().getCalibration().pixelHeight;
							IJ.getImage().killRoi();
							if (ZoneXorr.containsPoint(thisRoiXX, thisRoiYY)) {
								RoiManager.getInstance().addRoi(theRoi);
							}
				        }
			        }
			        
			        if (deleteRois[i]==false) {
			        	
				        saveRois(OutputDir, imageName + "Ch" + a + "_OriginalROIs");
			        	
			        	//newManager.run("Show All");
			        	RoiManager.getInstance().run("Show All");
			        	
			        	IJ.open(OutputDir + "/" + imageName + "_SlicedAllChannels" + ".tif");
			        	IJ.runMacro("Stack.setChannel("+a+");");
			        	
			        	WaitForUserDialog ManualCorrection = new WaitForUserDialog("Manual Correction", "Add missing ROIs and delete incorrect ROIs");
			        	ManualCorrection.show();
			        	
				        Roi[] chRois = RoiManager.getInstance().getRoisAsArray();
				        		        	
			        	allRois[i] = chRois;
			        }
			        else {
				        if (deleteRois[i]==true) {	
				        	Roi[] chRois1 = RoiManager.getInstance().getRoisAsArray();
				        	ArrayList<Roi> chRois1List = new ArrayList<Roi>();
				        	
				        	synchronized(chRois1List) {	
				        		for ( int u = 0 ; u <chRois1.length ; u++) {
						        	chRois1List.add(chRois1[u]);
						        }
				        		
				        		for (Roi thisRoi : chRois1) {
					        		RoiData thisData = new RoiData(IJ.getImage(), thisRoi);
					        		IJ.getImage().killRoi();
					        		double thisArea = thisData.setArea(IJ.getImage());
					        		if (thisArea<deleteArea[i]) {
					        			chRois1List.remove(thisRoi);
					        		}	
				        		}		        
				        	}
				        	
				        	//System.out.println("chRoisList size " + chRois1List.size());
				        	//System.out.println("out of synchroized");
					        Roi[] chRois2 = (Roi[]) chRois1List.toArray(new Roi[0]);
					        //System.out.println("Count After Removal " + chRois2.length);
					        
					        RoiManager.getInstance().reset();
					        for (Roi roi : chRois2) {
					        	RoiManager.getInstance().addRoi(roi);
					        }
					        if (RoiManager.getInstance().getCount()>0) {			
					        	saveRois(OutputDir, imageName + "Ch" + a + "_OriginalROIs");
					        }
					        //newManager.run("Show All");
					        RoiManager.getInstance().run("Show All");
				        
				        	IJ.open(OutputDir + "/" + imageName + "_SlicedAllChannels" + ".tif");
					        
				        	IJ.runMacro("Stack.setChannel("+a+");");
				        	
					        WaitForUserDialog ManualCorrection = new WaitForUserDialog("Manual Correction", "Add missing ROIs and delete incorrect ROIs");
				        	ManualCorrection.show();
				        	
				        	Roi[] chRois = RoiManager.getInstance().getRoisAsArray();
				        	
					        for (int r = 0 ; r < chRois.length ; r++) {
					        						        	
					        	//System.out.println(Integer.valueOf(a).toString() + "-" + Integer.valueOf(r).toString());
					        	
					        	chRois[r].setName(Integer.valueOf(a).toString() + "-" + Integer.valueOf(r).toString());
					        	
					        	chRois[r].setPosition(0);
					        	
					        	chRois[r].setGroup(a);
					        	chRois[r].setGroupName(a, "Group " + a);
					      
					        	//chRois[r].setGro	
					        	
					        }
				        	
				        	
				        	allRois[i] = chRois;
				        	
				        	
				        	
			        	}
			        }
			        
			        int h = i+1;
			        //System.out.println("Count After Removal Ch" + h + " " + allRois[i].length );
			        		        
			        IndexCount = allRois[0].length + allRois[1].length + allRois[2].length + allRois[3].length - 1;
			        if (RoiManager.getInstance().getCount()>0) {	
			        	saveRois(OutputDir, imageName + "_Ch" + a);
			        }
			        //RoiEncoder.save(newManager.getSelectedRoisAsArray(), inputDir2 + "\\" + "thisRoi.roi");
			        //newManager.run("Select All");
			        
			        //IJ.runMacro("roiManager(\"Save \", C:/Users/Emre/Desktop/EDM025-220321/ + File.separator + \"ImageName\" + \"_/" + ""\"zonenames[i]\" + \".zip\");");
			        
			        //WaitForUserDialog seeClose = new WaitForUserDialog("see close");
			        //seeClose.show();
			        
			        IJ.selectWindow(imageName + "_SlicedAllChannels" + ".tif");
			        
			        Window imwin = IJ.getImage().getWindow();
			        
			        IJ.getImage().flush();
			        IJ.getImage().close();
			        
			        imwin.dispose();
			        imwin = null;
			        
			        //WaitForUserDialog closed = new WaitForUserDialog("First image closed? Second image there?");
			        //closed.show();
			        
			        IJ.selectWindow(imageName + "_" +"EnhancedContrast" + "_" + "ZonesOnlyExpanded" + "_C" + a + ".tif");
			        
			        Window imwin2 = IJ.getImage().getWindow();
			        
			        IJ.getImage().flush();
			        IJ.getImage().close();
			        
			        imwin2.dispose();
			        imwin2 = null;
			        
			        IJ.wait(5000);
			         
			        System.gc();
			    }
			 
			 /*
			 System.out.println("allRois0.length " + allRois[0].length);
			 System.out.println("allRois1.length " + allRois[1].length);
			 System.out.println("allRois2.length " + allRois[2].length);
			 System.out.println("allRois3.length " + allRois[3].length);
			 */
			 
		} else if (doReanalysis==true) {
			for (int i = 0 ; i < 4 ; i++ ) {
				
				int b = i + 1;
				
				if(channelSelection[i]==false) {continue;}
				
				RoiManager.getRoiManager();
				
				RoiManager.getInstance().reset();
				
				IJ.open(ChRoisPaths[i]);
				
				Roi[] chRoiss = RoiManager.getInstance().getRoisAsArray();
				
				for(int r = 0 ; r < chRoiss.length ; r ++) {
					
					int a = i + 1 ;  
					
		        	chRoiss[r].setName(Integer.valueOf(a).toString() + "-" + Integer.valueOf(r).toString());
		        	
		        	chRoiss[r].setPosition(0);
		        	
		        	chRoiss[r].setGroup(a);
		        	chRoiss[r].setGroupName(a, "Group " + a);
		        	
				}
					
				allRois[i]= chRoiss;
				
				saveRois(OutputDir, imageName + "_Ch" + b);
			}
			
			
		}	
		
		for (Window wind : Window.getWindows()){
			if (!wind.getName().matches("frame0") && !wind.getName().matches("win0") && !wind.getName().matches("frame1")) {
					wind.dispose();
					wind = null;				
			}
		}
        		
		
		if (doOverlapAnalysis == true) {
			IJ.log("Finishing segmentation...");
		}else if (doReanalysis == true) {
			IJ.log("Finishing loading of ROIs...");
		}
		
        try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
        
        IJ.wait(5000);
        
        System.gc();
         
        return allRois;

	}
	
	runStardist(){
		this.IndexCount = IndexCount;
	}																							
	
	
	
	
	  public static void saveRois(String OutputDir, String name) {
	        // Create a list of ROIs that you want to save
	        Roi[] theseRois = RoiManager.getInstance().getRoisAsArray();
	        
	        for (Roi thisRoi:theseRois) {
	        	thisRoi.setPosition(0);
	        }
	        
	        System.out.println("Rois to be Saved: " + theseRois.length);

	        // Choose a location to save the ZIP file
	        String zipRoiName = OutputDir + "/" + name + ".zip";			
	        System.out.println("zipRoiName: " + zipRoiName);

	        try (FileOutputStream fos = new FileOutputStream(zipRoiName);
	             ZipOutputStream zos = new ZipOutputStream(fos)) {

	            // Iterate over each ROI
	            for (int i = 0; i < theseRois.length; i++) {
	                Roi thisRoi = theseRois[i];

	                // Convert ROI to byte array
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                RoiEncoder re = new RoiEncoder(baos);
	                re.write(thisRoi);

	                // Create a ZIP entry for each ROI
	                ZipEntry entry = new ZipEntry(i + ".roi");
	                zos.putNextEntry(entry);

	                // Write the ROI data to the ZIP file
	                zos.write(baos.toByteArray());
	                zos.closeEntry();
	            }

	            zos.close();
	            IJ.log("ROIs saved as ZIP: " + zipRoiName);
	        } catch (IOException e) {
	            IJ.log("Error saving ROIs as ZIP: " + e.getMessage());
	        }
	    }
	
	
	public static void clear() {
		
		for (Roi[] theseRois : allRois) {
			for (Roi thisRoi : theseRois) {
				thisRoi = null;
			}
		}
		allRois = new Roi[4][4]; 
	}


}

	
	
