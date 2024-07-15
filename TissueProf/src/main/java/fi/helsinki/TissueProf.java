/*
* To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package fi.helsinki;


import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;

import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.IJ;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import io.scif.services.DatasetIOService;
import loci.formats.FormatException;
//import ij.ImageJ;
import net.imagej.ImageJ;
import net.imagej.lut.LUTService;
import net.imagej.patcher.LegacyInjector;
		

@Plugin(type = Command.class, menuPath = "Plugins>TissueProf>TissueProf")
public class TissueProf implements PlugIn, Command {

	
	//static CompositeImage combinedenhanced;
	static OverlapRoxx OverlappedRoxx;
	
	String inputDir2;
	
	static int noZones;
	
	static ArrayList<String> zoneNames;
	
	static Roi[] zoneRois;
	
	static Roi zoneXorr;
	
	static Button button;
	
	static String[] modelPaths = new String[4];
	
	static Vector<?> modelPathsL;
	
	static boolean[] deleteRois = new boolean[4];
	
	static double[] probs = new double[4];
	
	static double[] nms = new double[4];
	
	static double[] deleteArea = new double[4];
	
	static Boolean[] channelSelection = new Boolean[4];
	
	static double overlapThreshold;
	
	static double enhanceContrast; 
	
	static boolean measureIntensity;
	
	static boolean useModel = false;
	
	static int nTiles = 8;

	static Roi[][] allRois;
	
	static Rox[][] allRox;

	static int NextIndex;
	
	static String suffix; 
	
	static String imageName;
	
	static Window[] frames;
	
	static Window[] winds; 
	
	static Window[] imageWinds = new Window[4];
	
	static RoiManager newManager;
	
	static LinkedHashMap<Roi, Rox> RoiRox;
	
	static int c;
	
	static boolean canceled = false; 
	
	static boolean finished = false;
	
	static String[] originalModelPaths = new String[4];
	
	static boolean doOverlapAnalysis = false;
	
	static boolean doReanalysis = false;
	
	static String[] OriginalChRoisPaths = new String[4];
	
	static String[] ChRoisPaths = new String[4]; 
	
	static Preferences prefs = Preferences.userNodeForPackage(TissueProf.class);
	
	static ImageJ imj = new ImageJ();
	
    @Parameter
    protected LogService log;

    @Parameter 
    protected Context context;
     
    @Parameter
    protected UIService ui;

    @Parameter
    protected CommandService command;

    @Parameter
    protected DatasetIOService datasetService;
    
    
    //@Parameter
    //protected Dataset dataset = null;
    
    //@Parameter 
    //static protected ImageJ imj;
    
    @Parameter
    protected StatusService status;
    
    @Parameter
    protected LUTService lut;
    
    @Parameter 
    protected DisplayService displayService;
    
    @Parameter 
    protected ObjectService objectService;
    
    @Parameter 
    protected IOService ioService;
    
	
	static {
		LegacyInjector.preinit();
	}
	 
	
  public void logs(String message) {
    // Just use the LogService!
    // There is no need to construct it, since the Context
    // has already provided an appropriate instance.
	  
	
  }
	
	  
	 
	
	public static void main(String[] args) throws FormatException, IOException, InterruptedException {
		
		
		
		
		Font defaultFont = new Font("Default", Font.PLAIN, 12); // Create a dummy font
        System.out.println("Default Font Family: " + defaultFont.getFamily());
        System.out.println("Default Font Name: " + defaultFont.getFontName());
			
		System.out.println("This is the first thread " + Thread.currentThread().getName().toString());
		
		//ImageJ imj = new ImageJ();
		
		imj.launch(args);
		
		System.out.println("This is the thread after imj.launch " + Thread.currentThread().getName());

		
		//imj.command().run(OverlapAnalysis5.class, true);
		
	}
	
	
	
	
	public TissueProf(){
		this.OverlappedRoxx = OverlappedRoxx;
	}
	
	
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		
		Window[] nonImage = WindowManager.getAllNonImageWindows();
		int w =0;
		for (Window win : nonImage) {
			System.out.println("nonImagewindow " + w + " :" + win.getName());
			w++;
		}
		
		w=0;
		Window[] winx = Window.getWindows();
		for (Window wi : winx ) {
			System.out.println("Regular Window " + w + " : " + wi.getName());
		}
		
		
		IJ.run("Fresh Start");
				
		System.out.println("running");
		
		IJ.wait(400);
		
		
		System.out.println("active count " + Thread.activeCount());
		
		Thread thisThreadd = Thread.currentThread();
		
		int wi = 0;
		
		for (Window wind : Window.getWindows()) {
			System.out.println("Window " + wi + wind.getName());
			wi++;
		}
		
		while (canceled==false && finished ==false) {	
			try {		
			
			Frame frame1 = new Frame();
			Frame frame2 = new Frame();

			Dialogs.zoneNoDialog thisDialog = new Dialogs.zoneNoDialog("Number of Zones");
			
			thisDialog.setVisible(true);
			
			if (canceled == true) {
				//thisCommand.cancel(true);
				Thread.currentThread().interrupt();
				
				Thread.currentThread().sleep(2);
				
			}
			
			boolean isPositiveInteger = false;
			
			while(isPositiveInteger==false) {
				try {
				noZones = (int) thisDialog.getNextNumber();
				isPositiveInteger=true;
				}
				catch (Exception e) {
					WaitForUserDialog enterNo = new WaitForUserDialog("Please enter a positive whole number");
					enterNo.show();
				}
			}
			
			System.out.println("zoneno " + noZones);	

			Dialogs.zoneNameDialog zoneNameDialog = new Dialogs.zoneNameDialog("Zone Names", noZones);
			
			zoneNameDialog.setVisible(true);

			System.out.println("Canceled = " + canceled);
			
			if (canceled == true) {
				//thisCommand.cancel(true);
				Thread.currentThread().interrupt();
				Thread.currentThread().sleep(2);
			}
			
			Vector zoneNamesv = zoneNameDialog.getStringFields();
			
			
			zoneNames = new ArrayList<String>();

			synchronized(zoneNamesv) {
			
				Iterator<TextField> iteratename = zoneNamesv.iterator();
				while(iteratename.hasNext()) {
					//System.out.println(iteratename.next());
					TextField thisNametext = iteratename.next();
					String thisName = thisNametext.getText();
					//System.out.println("from vector " + thisNametext.getText());	
					zoneNames.add(thisName);
				}
			}
			
			System.out.println("zoneName " +  zoneNames.get(0));
			
			zoneNames.forEach(n->System.out.println(n));
			
			//Create a new Dialog for parameter input
			Dialogs.overlapDialog overlapDialog = new Dialogs.overlapDialog("Parameters", frame1);
			
			//Collect inputs
			
			String[] channelNames = new String[4];
			
			System.out.println("channelName.length " + channelNames.length);
			
			System.out.println("channelSelection.length" + channelSelection.length);
			
			String filePathString = overlapDialog.getNextString();
			System.out.println("Input File from String input" + filePathString);
			
			String OutputDir = overlapDialog.getNextString();
			System.out.println("Output Directory");
			
			ArrayList<String> pathNames = new ArrayList<String>();
			pathNames.add(filePathString);
			
			suffix = overlapDialog.getNextString();
			
			Path filePath = Paths.get(filePathString);
			
			Path[] filePaths = new Path[1];
			filePaths[0] = filePath;
			
			
			//Link paths and their string representations
			LinkedHashMap<Path, String> linkedMap = new LinkedHashMap<Path, String>();
			
			for (int i = 0 ; i < filePaths.length ; i++) {
				linkedMap.put(filePaths[i], pathNames.get(i));
			}
			//Extract the image name and directory from path
			for (Entry<Path, String> entry : linkedMap.entrySet()) {
			    Path filePath1 = entry.getKey();
			    String fileName = entry.getValue();
			    System.out.println(filePath + " " + fileName);
			    
			    if (fileName.endsWith(suffix)) {
			    	imageName = filePath1.getFileName().toString();
			    	inputDir2 = filePath1.getParent().toString();
			    	//OutputDir = filePath1.getParent().toString();
			    	System.out.println("Image Name: " + imageName);
			    	System.out.println("Input Directory: " + " " + inputDir2);
			    }
			}
			
			//Verify image name 
			//System.out.println("Image Name 2: " + imageName);
			
			
			////////Channel Parameters 
			
			//Collect channel parameters
			
			for (int i=0 ; i < 4 ; i++) {
				String nowString = overlapDialog.getNextString();
				channelNames[i] = nowString;
				//System.out.println(i + "\'th string" + nowString);
				Boolean nowBool = overlapDialog.getNextBoolean();
				//System.out.println(i + "\'th channel" + nowBool );
				channelSelection[i] = nowBool;
			}
			
			//Determine the number of channels that will be used in this analysis
			
			int channelSize = 0 ;
			for (int i = 0 ; i < channelSelection.length ; i++) {
				if (channelSelection[i]==true) {
					channelSize++;
				}
			}
			
			//Check that channel names have been collected correctly
			//System.out.println("Channel Check");
			
			c=0;
			for (String channelName:channelNames) {
				System.out.println(channelNames[c]);
				c++;
			}
			
			c=0;
			//Check that check box choices were collected correctly
			//System.out.println("Checkbox Check");
			for (Boolean checkBox:channelSelection) {
				//System.out.println(channelSelection[c]);
				c++;
			}
			
			//collect overlap threshold, contrast amount, measureintensity option
			overlapThreshold = overlapDialog.getNextNumber();
			enhanceContrast = overlapDialog.getNextNumber();
			measureIntensity = overlapDialog.getNextBoolean();
			
			/////Mode Selections 
			//collect mode choice
			doOverlapAnalysis = overlapDialog.firstMode.isSelected();
			doReanalysis =	overlapDialog.secondMode.isSelected(); 
			
			//Trigger interrupted exception to exit plugin in case the window was closed, or cancel button was pressed
			//(These events set canceled to true)
			if (canceled ==true) {
				Thread.currentThread().interrupt();
				Thread.currentThread().sleep(2);
			}
			
			if (doOverlapAnalysis==true) {
			
			////Model Parameters 
			
				System.out.println("input " + inputDir2);
				System.out.println("output " + OutputDir);
				System.out.println("imageName " + imageName);
				
				////Path adjustments for StarDist
				
				if (OutputDir.endsWith("\\")) {		
					int escapeindex = OutputDir.lastIndexOf("\\");
					OutputDir = OutputDir.substring(0, escapeindex);	
				}
						
				for (int i = 0 ; i < 4 ; i++) {
					int a = i+1;
					
					String thisModel=overlapDialog.getNextString();
				
					originalModelPaths[i] = thisModel;
					
						System.out.println(thisModel);
						String thisdoublemodel = thisModel.replaceAll("\\\\", "\\\\\\\\");
						String thisdoublemodell = thisdoublemodel.replaceAll("\\\\\\\\", "\\\\\\\\\\\\\\\\");
						
						if (thisModel.startsWith("\\\\")){
							thisdoublemodell = thisdoublemodell.substring(2);
							
						}
						
						System.out.println("model " + a + " " + thisdoublemodell);
						modelPaths[i]=thisdoublemodell;
						
				}
				
				
				////Use Model?
				
				useModel = overlapDialog.getNextBoolean();
				System.out.println("useModel " + useModel);
				nTiles = (int) overlapDialog.getNextNumber();
				System.out.println("Number of Tiles " + nTiles);
				
				////DeleteROIs?
				
				for (int i = 0 ; i < 4 ; i++) {
					int a = i+1; 
					deleteRois[i]=overlapDialog.getNextBoolean();
					System.out.println("channelDeleteRois " + a + " " + deleteRois[i]);
				}
				
				
				for (int i = 0 ; i < modelPaths.length ; i++) {
					int u = i + 1; 
					System.out.println("model " + i + " " + modelPaths[i]);
				}
				 
				
				////useModel, Probability, nms and deleteCell Area limit
				
				probs = new double[4];
				nms = new double[4];
				deleteArea = new double[4];
		 		//double[] percentileTop = new double[4];
		 				
				for (int i = 0 ; i < 4 ; i++) {
					probs[i] = overlapDialog.getNextNumber();
					nms[i] = overlapDialog.getNextNumber();
					deleteArea[i] = overlapDialog.getNextNumber();
				}
				
				//verify values entered into the arrays
				for(int i = 0 ; i < 4 ; i ++ ) {
					//System.out.println("probs" + i + " " + probs[i]);
				}
				
				for(int i = 0 ; i < 4 ; i ++ ) {
					//System.out.println("nms" + i + " " + nms[i]);
				}
				
				for (int i = 0 ; i < 4 ; i++) {
					//System.out.println("deleteArea" + i + " " + deleteArea[i]);
				}
			
				//save Preferences 
				savePreferences(inputDir2, imageName, OutputDir, suffix, channelSelection, channelNames, useModel, nTiles, 
						originalModelPaths, deleteRois, deleteArea, probs, nms);
				
				//Trigger InterruptedException to exit plugin in case window was closed or cancel button was clicked. Both 
				//events set canceled = true
				if (canceled == true) {
			        Thread.currentThread().interrupt();
			        Thread.currentThread().sleep(2);
				}
				
			} else if (doReanalysis == true ) {
				for (int i = 0 ; i < 4 ; i++) {
					int a = i+1;
					//Check canceled
					if (canceled == true) {
						//thisCommand.cancel(true);
				        Thread.currentThread().interrupt();
				        Thread.currentThread().sleep(2);
					}
					
					//System.out.println("next string " + overlapDialog.getNextString());
					
					//Adjust paths for StarDist
					
					String thisChRoi=overlapDialog.getNextString();
					
					OriginalChRoisPaths[i] = thisChRoi;
					
						System.out.println(thisChRoi);
						String thisdoubleChRoi = thisChRoi.replaceAll("\\\\", "\\\\\\\\");
						String thisdoubleChRoii = thisdoubleChRoi.replaceAll("\\\\\\\\", "\\\\\\\\\\\\\\\\");
						
						if (thisChRoi.startsWith("\\\\")){
							thisdoubleChRoii = thisdoubleChRoii.substring(2);
						}
						
						System.out.println("ChRoi " + a + " " + thisdoubleChRoii);
						
						ChRoisPaths[i]=thisdoubleChRoii;
						
				
				}
				
				//Put preferences 
				prefs.put("InputDir", inputDir2);
				prefs.put("imageName", imageName);
				prefs.put("OutputDir",OutputDir);
				prefs.put("suffix", suffix);
				
				for (int i = 0 ; i < 4 ; i++) {
					int a = i+1;
					prefs.putBoolean("Channel" + a + "Selection", channelSelection[i]);
				}
				
				
				for ( int i = 0 ; i < 4 ; i++) {
					int a = i + 1;
					prefs.put("Channel" + a + "Name", channelNames[i]);
				}
			
				int q = 0;
				for (String thisRoi : ChRoisPaths) {
					System.out.println("Rois path " + q + " thisRoi");
					q++;
				}
				q=0;
			

				for (int i = 0 ; i < ChRoisPaths.length ; i++) {
					int a = i + 1;
					prefs.put("Ch" + a + "Rois", OriginalChRoisPaths[i]);
				}
				
				//Trigger InterruptedException if canceled 
				if (canceled == true) {
					//thisCommand.cancel(true);
			        Thread.currentThread().interrupt();	
			        Thread.currentThread().sleep(2);
				}
				
			}
			
			//In order to prevent class issues with ImageJ
			LegacyInjector.preinit();
			
			////Process Image
			ProcessImage NewProcess = new ProcessImage();

	    	try {
				NewProcess.processImage(inputDir2, OutputDir, imageName, suffix, doOverlapAnalysis, doReanalysis, noZones, enhanceContrast);
			} catch (FormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	//System.out.println("Done processing ");
	    	
	    	//Close unnecessary Image Windows and release memory 
	        winds = Window.getWindows();
	        w = 0;
	        for (Window wind:winds) {
	        	System.out.println("window " + w + ":" + wind.getName());
	        	if (wind.getName().startsWith("E")) {
	        		wind.dispose();
	        		wind=null;
	        	}
	        w++;
	        }
	        
	        String[] imageTitles = WindowManager.getImageTitles();
	        
	        for(String name : imageTitles) {
	        	System.out.println("image window with name " + name);
	        	//WindowManager.se
	        	IJ.selectWindow(name);
	        	Window thiswind = WindowManager.getActiveWindow();
	        	System.out.println(WindowManager.getActiveWindow().toString());
	        	thiswind.dispose();
	        	thiswind = null;
	        } 
	        
	        //Temporary Roi Array 
	        
	        allRois = new Roi[2][0];
	        
			WindowManager.closeAllWindows();
			
			zoneXorr = NewProcess.zoneXorr;
			
			////Does segmentation for Overlap Analysis, loads ROIs for Reanalysis
	        allRois = runStardist.runStardist(inputDir2, OutputDir, imageName, imageWinds, useModel, channelSelection, 
	        		modelPaths, deleteRois, deleteArea, probs, nms, nTiles, ChRoisPaths, doOverlapAnalysis, doReanalysis, zoneXorr);
			
	        
	        for (Window wind : Window.getWindows()) {
				
				if (!wind.getName().matches("frame0") && !wind.getName().matches("win0") && !wind.getName().matches("frame1")) {
					wind.dispose();
					wind= null;
	        	}
	        }
	        
	        ////Clean up resources
	        runStardist.clear();
	        
			WindowManager.closeAllWindows();
	        
			IJ.log("Preparing for ROI processing...");	
			
			////Trigger garbage collection by JVM
			
	        try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Canceled");
			}
	        
	        IJ.wait(5000);
	        
	        System.gc();
	        
	        //WaitForUserDialog newWait = new WaitForUserDialog("Memory after Stardist");
	        //newWait.show();
	        
	        IJ.log("Processing ROIs");
	        
	        IJ.open(OutputDir + "/" + imageName + "_" + "EnhancedContrast_CDUPLICATE_C" + "2" + ".tif");
	        
	        //Store ChannelROI numbers
	        int[] allRoiLengths = new int[4];
	        c=0;
	        for (Roi[] nowRois:allRois) {
		        int a = c+1;
		        System.out.println("Rois Ch" + a + nowRois.length);
		        allRoiLengths[c]=nowRois.length;
		        c++; 
	        }
	        
	        RoiManager.getRoiManager();
	        
	        RoiManager newManager = RoiManager.getInstance();
	        
	        int maxLength = Arrays.stream(allRoiLengths).max().getAsInt();
	        
			newManager.reset();
			
			//Create a 2D Array to fill with RoiData objects
			RoiData[][] allRoiData = new RoiData[imageWinds.length][];
			//Create a LinkedHashMap to link RoiData objects to the original Roi objects
			LinkedHashMap<Roi, RoiData> RoiDataMap = new LinkedHashMap<Roi, RoiData>();
			
			c=0;
			
			//Fill the allRoiData Array and the RoiDataMap
			for (int i = 0 ; i < allRois.length ; i++) {
				allRoiData[i]= new RoiData[allRois[i].length];
				for (int j = 0 ; j < allRois[i].length ; j++) {
					System.out.println("i" + i + "j " + j);
					newManager.add(allRois[i][j], c);
					newManager.select(c);
					Roi roik = allRois[i][j];				
					RoiData roikData = new RoiData(IJ.getImage(), roik);
					roikData.setIndex(c);
					allRoiData[i][j] = roikData;
					allRoiData[i][j].setIndex(c);
					RoiDataMap.put(roik, roikData);
				c++;
				}
			}
			
			//Keep track of ROI indexes
			System.out.println("AllRoi Final Index " + c);
			
	        IJ.selectWindow(imageName + "_" + "EnhancedContrast_CDUPLICATE_C" + "2" + ".tif");
	        
	        allRox = new Rox[allRoiData.length][];
	        
	        //Create LinkedHashMaps to link Rox to their RoiData and RoiData to their Rox in order to keep track of them
	        //from inside other methods
	        LinkedHashMap<RoiData, Rox> DataRoxMap = new LinkedHashMap<RoiData, Rox>();
	        LinkedHashMap<Rox, RoiData> RoxDataMap = new LinkedHashMap<Rox, RoiData>();
	        RoiRox = new LinkedHashMap<Roi, Rox>();
	        
	   
	        for (int i = 0 ; i < 4 ; i++) {
	        	RoiData[] theseData = allRoiData[i];
	        	Roi[] theseRois = allRois[i];
	        	//System.out.println("allRoiData" + i + " " + theseData.length + "allRois" + i + " " + theseRois.length);
	        	//allRoiData[i] = new RoiData[theseRois.length];
	        }
	        
	        //set necessary properties of Roxes and Fill the LinkedHashMaps
	        c=0;
	        for (int i = 0 ; i < allRoiData.length ; i++) {
	        	if (channelSelection[i]==false) {continue;}
	        	allRox[i] = new Rox[allRoiData[i].length];
	        	for (int j = 0 ; j < allRoiData[i].length ; j++) {
	        		newManager.select(c);        		
	        		RoiData thisData = allRoiData[i][j];
	        		thisData.setIndex(c);
	        		Rox thisRox = new Rox(thisData);
	        		thisRox.setIndex(c);
	        		thisRox.setShape();
	        		allRox[i][j] = thisRox;
	        		DataRoxMap.put(thisData, thisRox);
	        		RoxDataMap.put(thisRox, thisData);
	        		RoiRox.put(thisRox.getRoi(), thisRox);
	        		c++;
	        	}
	        }
	        
	        //Check if map was filled 
	        System.out.println("RoiRox size " + RoiRox.size());
	        
	        //Check if allRox was filled  
	        for (int i = 0 ; i < 4 ; i++) {
	        	if (channelSelection[i]==false) {continue;}
	        	System.out.println("allRoiLength " + allRois[i].length + "allRoiDataLength " + allRoiData[i].length + "allRoxLength " + allRox[i].length);
	        }
	        
	        zoneRois = NewProcess.zoneRois;
	        
	        System.out.println("No of ZoneRois " + zoneRois.length);
	        
	        int c = 0 ; 
	        //Classify ROIs into each zone and process each zone separately.
	        for (Roi zone : zoneRois) {
	        	
	        	RoiManager.getRoiManager();
	        	
	        	//Create an ArrayList of Rox for the current zone
	        	ArrayList<ArrayList<Rox>> thisAllRoxL = new ArrayList<ArrayList<Rox>>();
	        	for (int i = 0 ; i < 4 ; i++) {
	        		thisAllRoxL.add(new ArrayList<Rox>());
	        	}
	        	//Fill the created 2D Rox ArrayList with the Rox whose ROIs lie inside the current zone 
	        	synchronized (thisAllRoxL){
		        	for (int i = 0 ; i < 4 ; i++ ) {
		        		if (channelSelection[i]==true) {
		        			System.out.println(allRox[i].length);
			        		for(int j = 0 ; j < allRox[i].length ; j++ ) {
			        			//AllRox[i][j].getPosition();
			        			if (zone.containsPoint(allRox[i][j].getPosition()[0], allRox[i][j].getPosition()[1])) {
			        				thisAllRoxL.get(i).add(allRox[i][j]);
			        			}
			        		}
			        	}
			    	}
	        	}
	        	
	        	//Convert the filled Rox List to a 2D Array to make things easier 
	        	Rox[][] thisAllRox = new Rox[4][];
		        
		        for (int i = 0; i < thisAllRoxL.size(); i++) {
		            ArrayList<Rox> row = thisAllRoxL.get(i);
		            thisAllRox[i] = row.toArray(new Rox[thisAllRoxL.get(i).size()]);
		        }
	        
		        RoiManager.getInstance().reset();
		        
		        //add all ROIs to the ROI Manager
		        int z=0;
		        for (int i = 0 ; i < thisAllRox.length ; i++) {
		        	for( int j = 0 ; j <thisAllRox[i].length ; j++) {
		        		RoiManager.getInstance().addRoi(thisAllRox[i][j].getRoi());
		        		z++;
		        	}
		        }
		        
		        NextIndex = z;
		        
		        //Clean up Resources
		        
		        //IJ.selectWindow(imageName + "_" + "EnhancedContrast_CDUPLICATE_C" + "2" + ".tif");
		        
		        //Window imwin = IJ.getImage().getWindow();
		        
		        
		        IJ.getImage().flush();
		        IJ.getImage().close();
		        
		        //imwin.dispose();	
		        //imwin = null;
		        
		        WindowManager.closeAllWindows();
		        
		        //Open an image to be used for measurements by IJ
			    IJ.open(OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + 2 + ".tif");
		        
			    //Detect Overlap Positions 
			    DetectOverlap DetectResults = DetectOverlap.detectOverlap(thisAllRox, RoiRox, channelSelection, channelSize);
		        IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + 2 + ".tif");
		       
		        //Close windows to clean up resources
		        
		        Window thisim = IJ.getImage().getWindow();
		        
		        IJ.getImage().flush();
		        IJ.getImage().close();
		        
		        thisim.dispose();
		        thisim = null;
		        
		        //Filter ROIs from Channel ROIs that overlap with ROIs from other channels
		        OverlapFilter RoisFiltered = OverlapFilter.overlapFilter(thisAllRox, DetectResults, channelSelection, channelSize);   
		        
		        //Open a random image for measurements done by RoiData and Rox classes
		        
		        Random random = new Random();
		        
		        System.out.println("Output dir " + OutputDir);
		        
		        int OpenCh = random.nextInt(4) + 1;
		        
		        System.out.println("OpenCh " + OpenCh);
		        System.out.println("open path " + OutputDir + "/" + imageName + "_" + "EnhancedContrast" + "_" + "ZonesOnly" + "_C" + OpenCh + ".tif");
		        System.out.println("Output dir " + OutputDir);
		        
		        IJ.open(OutputDir + "/" + imageName + "_" + "EnhancedContrast" + "_" + "ZonesOnly" + "_C" + OpenCh + ".tif");
		        
		        
		        //Area Measurements for the Rox that overlap with an unknown ratio with other channel ROIs in the particular combinations.
		        for (int i = 0 ; i < RoisFiltered.QuadRoxx.size(); i++) {
		        	for (Rox rox:RoisFiltered.QuadRoxx.get(i)) {
		        		newManager.reset();
		        		newManager.add(rox.getRoi(), 0);
		        		RoxDataMap.get(rox).setArea(IJ.getImage());
		        	}
		        }
		        
		        
		        for (int i = 0 ; i < RoisFiltered.TripleRoxx.size() ; i++) {
		        	for (int j = 0 ; j < RoisFiltered.TripleRoxx.get(i).size(); j++) {
		        		for (Rox rox:RoisFiltered.TripleRoxx.get(i).get(j)) {
		        			newManager.reset();
		            		newManager.add(rox.getRoi(), 0);
		            		RoxDataMap.get(rox).setArea(IJ.getImage());
		        		}
		        	}
		        }
		        
		        for (int i = 0 ; i < RoisFiltered.DoubleRoxx.size() ; i++) {
		        	for (int j = 0 ; j < RoisFiltered.DoubleRoxx.get(i).size(); j++) {
		        		for (Rox rox:RoisFiltered.DoubleRoxx.get(i).get(j)) {
		        			newManager.reset();
		            		newManager.add(rox.getRoi(), 0);
		            		RoxDataMap.get(rox).setArea(IJ.getImage());
		        		}
		        	}
		        }
		        
		        //Clean up resources
		        
		        IJ.selectWindow(imageName + "_" + "EnhancedContrast" + "_" + "ZonesOnly" + "_C" + OpenCh + ".tif");
		        
		        Window imwin2 = IJ.getImage().getWindow();
		        
		        System.out.println("window imwin 2 selected ");
		        
		        IJ.getImage().flush();
		        IJ.getImage().close();
		        
		        imwin2.dispose();
		        imwin2 = null;
		   
		        WindowManager.closeAllWindows();
		        
		        //Create a separate thread for detailed overlap analysis of Rox that have been filtered and are known to interact
		        OverlapThread overlapThread = new OverlapThread(RoisFiltered, thisAllRox, RoxDataMap, NextIndex, channelSelection, 
		        		channelSize, inputDir2, OutputDir, imageName, overlapThreshold);
		        
		        // Create a FutureTask
		        FutureTask<OverlapRoxx> futureTask = new FutureTask<OverlapRoxx>((Callable<OverlapRoxx>) overlapThread);
		
		        // Create a thread pool
		        ExecutorService executor = Executors.newSingleThreadExecutor();
		
		        // Execute the FutureTask in the thread pool
		        executor.submit(futureTask);
		        
		        // Get the result from the FutureTask
		       
		        try {
		            OverlappedRoxx = futureTask.get();
		            
		            // Use overlapRoxxResult obtained from the overlapThread
		        } catch (InterruptedException | ExecutionException e) {

		        	e.printStackTrace();
		        	
		        } finally {
		            executor.shutdown(); // Shutdown the executor
		        }
		        
		        RoiManager.getInstance().reset();
		        
		        ////Add all ROIs and overlap ROIs to the ROI Manager and save them as the allROIs ROI sets
		        int allOverlapCount = 0;  
		        
				for (int i = 0 ; i < thisAllRox.length ; i++) {
					if (channelSelection[i]==false) {continue;}
					for (int j = 0 ; j < thisAllRox[i].length ; j++) {
						thisAllRox[i][j].setIndex(allOverlapCount);
						RoiManager.getInstance().addRoi(thisAllRox[i][j].getRoi());
						allOverlapCount++;
					}
				}
				
				System.out.println("OriginalTotalOverlapCount " + allOverlapCount);			
				
				Iterator quadIter = OverlappedRoxx.QuadInterRoxx.iterator();
				while (quadIter.hasNext()) {
					RoiManager.getInstance().addRoi(((Rox) quadIter.next()).getRoi());
					allOverlapCount++;
				}
				
				for (ArrayList<Rox> tripleRus : OverlappedRoxx.TripleInterRoxx) {
					if (tripleRus.size()>0) {
						Iterator tripleIterate = tripleRus.iterator();
						while (tripleIterate.hasNext()) {
							RoiManager.getInstance().addRoi(((Rox) tripleIterate.next()).getRoi());
							allOverlapCount++;
						}
					}
				}
				
				for (ArrayList<Rox> dubRus : OverlappedRoxx.DoubleInterRoxx) {
					if (dubRus.size()>0) {
						Iterator doubleIterate = dubRus.iterator();
						while(doubleIterate.hasNext()) {
							RoiManager.getInstance().addRoi(((Rox) doubleIterate.next()).getRoi());
							allOverlapCount++;
						}
					}
				}
				
				System.out.println("allOverlapCount with all InterRox Added " + allOverlapCount);
				
				runStardist.saveRois(OutputDir, imageName + "_" + zoneNames.get(c) + "_AllROIs");
				
				WindowManager.closeAllWindows();
				
				
				OverlapTables thisTable = new OverlapTables();
		        
		        try {
					thisTable.makeTables(OutputDir, imageName, OverlappedRoxx, inputDir2, imageName, 
							NewProcess.backgroundRois, channelSize, channelSelection, channelNames, zoneNames.get(c), measureIntensity);
				} catch (IOException e) {
					// TODO Auto-generated catch block				
					e.printStackTrace();
				}
		        
		        
		        NextIndex = 0 ;

		        Dialogs.ProblemAreaDialog thisProblem = new Dialogs.ProblemAreaDialog("Problematic Areas", OverlappedRoxx, OutputDir, zoneNames.get(c), 
		        		imageName, RoxDataMap, overlapThreshold);
				thisProblem.setVisible(true);
		        
				System.out.println("Finished working on "  + zoneNames.get(c));
				OverlappedRoxx.clear();
				OverlappedRoxx = new OverlapRoxx();
		        c++;
		       
	        }
	        
	        //Clean up resources
	        for (int i = 1 ; i < 5 ; i ++) {
	        	File deleteThis = new File(OutputDir + "/" + imageName + "_" + "EnhancedContrast_CDUPLICATE_C" + i + ".tif");
	        	deleteThis.delete();
	        }
	        
	        DataRoxMap.clear();
	        RoxDataMap.clear();
	        
	        DataRoxMap = null;
	        RoxDataMap = null;
	        
	        System.out.println("Finished");
	        
	        finished = true; 
	        
	        WindowManager.closeAllWindows();
		
			} catch (InterruptedException e ) {
				canceled = true;
				
			} catch (NullPointerException e) {
				e.printStackTrace();
				canceled = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("an unknown exception was thrown...");
			}
			
		System.out.println("finshed ? " + finished);	
		}	
		System.out.println("outside while");
		
		canceled = false;
		finished = false;
		
		//Clean-up resources
		try {
			
			
			DetectOverlap.clear();
			
			DetectOverlap.c=0;   
		
			OverlapFilter.clear();
			
			if (allRois != null) {
				for (int i = 0 ; i < allRois.length ; i++) {
					for (int j =0 ; j< allRois[i].length ; j++) {
						if (allRois[i][i]!=null) {
							allRois[i][j]=null;
						}
					}
				}
			}
			
			
			if (allRox!=null) {
				for (int i = 0 ; i < allRox.length ; i++) {
					if (channelSelection[i]==true) {
					System.out.println("allRox " + i + " length " + allRox[i].length);
					}
				}
				
				for (int i = 0 ; i < allRox.length ; i++) {
					if (channelSelection[i]==true && allRox[i].length > 0 ) {
						for (int j = 0 ; j < allRox[i].length ; j++) {
							if(allRox[i][j]!=null) {						
								allRox[i][j]=null;
							}
						}
					}
				}
			}
			
			if (frames != null) {
				for (Window fram : frames) {
					if (!fram.getName().matches("frame0") && !fram.getName().matches("win0") && !fram.getName().matches("frame1")) {
						//System.out.println("frame with name " + fram.getName() + " closing");
						fram.dispose();
						fram=null;
					}
				}
				frames = null;
			}
		
			
			if (imageWinds!=null && imageWinds.length>0) {
				for (Window imageWind : imageWinds) {
					if (imageWind!= null) {
						//System.out.println("image window with name " + imageWind.getName() + " closing");
						imageWind.dispose();
						imageWind = null;
					}
				}
				imageWinds = new Window[4];
			}
			
			if (newManager != null) {
				newManager = null;
			}
			if (RoiRox!=null) {
	        	RoiRox.clear();
			}
			if (OverlappedRoxx!=null) {
		        OverlappedRoxx.clear();
		        OverlappedRoxx = new OverlapRoxx();
			}
			
			String[] imgtitles = WindowManager.getImageTitles();
			/*
			int s = 0 ;
			for (String imgtitle : imgtitles) {
				System.out.println("image title " + s + " " + imgtitle);
				s++;
			}
			*/
			Window[] openWindows = Window.getWindows();
			
			for (Window thisWindow : openWindows) {	
				/*
				System.out.println(thisWindow.getName());
				System.out.println(thisWindow.toString());
				System.out.println("displayable? " + thisWindow.isDisplayable());
				*/
				if (!thisWindow.getName().matches("frame0") && !thisWindow.getName().matches("win0") && !thisWindow.getName().matches("frame1")) {
					thisWindow.dispose();
					thisWindow = null;
				}
			}
			
			
			if (openWindows != null) {
				openWindows = null;
			}
			
			List<Display<?>> finalList = imj.display().getDisplays();
			
			System.out.println("FinalList size " + finalList.size());
			
	    	ListIterator finalIterated = finalList.listIterator();
			
	    	int dis = 0 ; 
	    	
	    	while (finalIterated.hasNext()) {
	    		
	    		Display thisDisplay = (Display) finalIterated.next();
	    		
	    		dis++;	
	    	}
	    	
	    	finalList.removeAll(finalList);
	    	finalList.clear();
	    	finalIterated = null;
	    	WindowManager.closeAllWindows();
	    	if (finished == true) {
	    		IJ.log("Completing Analysis...");
	    	}
	    	
	        try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
	        
	        IJ.wait(5000);
	        
	    	
			System.gc();
			
			if(finished == true) {
				IJ.log("Analysis Completed");				
			}
			
		}	catch (Exception e) {
			e.printStackTrace();
		} 
		
		canceled = false;
		finished = false;
		
		
	
	}




	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		
	}
	
	private static synchronized void stopPlugin() {
		//blinker = null;
		//if (thisCommand==null) {System.out.println("CommandModule null");}
		//thisCommand.cancel(true);
		//thisCommand.cancel(true);
		canceled=true;
		//System.out.println("Command canceled " + thisCommand.isCancelled());
	}

	public static void getThreadtoStop() {
		
		stopthisPlugin();
		
	}
	
	private static void stopthisPlugin() {
		stopPlugin();
	}

	public static void savePreferences(String InputDir, String imageName, String OutputDir, String suffix, Boolean[] channelSelection, 
			String[] channelNames, boolean useModel, int nTiles, String[] originalModelPaths, boolean[] deleteRois, double[] deleteArea, 
			double[] probs, double[] nms) {	

		prefs.put("InputDir", InputDir);
		prefs.put("imageName", imageName);
		prefs.put("OutputDir",OutputDir);
		prefs.put("suffix", suffix);
		
		for (int i = 0 ; i < 4 ; i++) {
			int a = i+1;
			prefs.putBoolean("Channel" + a + "Selection", channelSelection[i]);
		}
		
		for ( int i = 0 ; i < 4 ; i++) {
			int a = i + 1;
			prefs.put("Channel" + a + "Name", channelNames[i]);
		}
		
		prefs.putDouble("overlapThreshold", overlapThreshold);
		prefs.putDouble("enhanceContrast", enhanceContrast);
		
		prefs.putBoolean("useModel", useModel);
		prefs.putInt("nTiles", nTiles);
		
		for (int i = 0 ; i < 4 ; i++) {
			int a = i + 1;
			prefs.put("Model " + a + " Path", originalModelPaths[i]);
			prefs.putDouble("probs" + a, probs[i]);
			prefs.putDouble("nms" + a, nms[i]);
			prefs.putBoolean("deleteRoi" + a , deleteRois[i] );
			prefs.putDouble("deleteArea" + a , deleteArea[i]);
		
		}
		
	}
	
}



