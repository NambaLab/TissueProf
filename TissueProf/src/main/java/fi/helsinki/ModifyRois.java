package fi.helsinki;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.plugin.Colors;
import ij.plugin.frame.RoiManager;
import ij.process.LUT;


@Plugin(type = Command.class, label = "Command From Macro", menuPath = "Plugins>TissueProf>Tools>ModifyROIs")
public class ModifyRois implements Command {
	
	static Preferences prefss = Preferences.userNodeForPackage(TissueProf.class);
	
	static ActionListener[] oldListeners;
	
	static Button deleteButton;
	
	static Button addButton;
		
	static String[] roiChannelPaths = new String[4];
	
	static ArrayList<ArrayList<Roi>> ChRois;
	
	static ArrayList<ArrayList<Rectangle>> BoundsList;
	
	static String[] OriginalColorNames = new String[4];
	
	static String[] ColorNames = new String[4];
	
	static boolean[] ChannelSelect = new boolean[4];
	
	static int[] ColorGroups = new int[4];
	
	static String[] ColorChoice = new String[4];	
	
	static Color[] StrokeColors = new Color[4];
	
	static String allRoisPath;
	
	static Color CurrentColor;
	
	static int CurrentGroup;
	
	static int CurrentChannel;
	
	static int GroupsColors[] = {2,3,1,235,27,87,114,4,0,186,222,76};
	
	static int Groups[] = new int[4];
	
	LUT glasbeyLut;
	
	static boolean nowSaving = false; 
	
	static Boolean saved = false;
	
	static boolean modfinished = false;
	
	static boolean modcanceled = false;
	
	static Thread modRunThread;

	static KeyAdapter myAdapter;
	
	static FilteredKeyListener myFilteredListener;
	
	static boolean alreadyAdded = false;
	
	@Override
	public void run() {
		
		//System.out.println("Now running");
		
		while (modcanceled == false && modfinished == false) {
			try {
				
				modRunThread = Thread.currentThread();

				//Create a ROI Manager instance
				RoiManager.getRoiManager();
				
				ColorChoice = Colors.getColors();
				
				ChRois = new ArrayList<ArrayList<Roi>>();
				BoundsList = new ArrayList<ArrayList<Rectangle>>();
				
				for (int i = 0 ; i < 4 ; i ++) {
					//ChRois.add(new ArrayList<Roi>());
					BoundsList.add(new ArrayList<Rectangle>());
				}
				
				ChannelRois newRois = new ChannelRois("Roi Input", new Frame());
				newRois.setVisible(true);
				
				//Check if canceled and if so interrupt thread to trigger InterruptedException
				if (modcanceled == true) {
					Thread.currentThread().interrupt();
					Thread.currentThread().sleep(1);
				}
				
				//Save input into arrays
				for (int i = 0 ; i < 4 ; i++) {
					roiChannelPaths[i] = newRois.getNextString();
					String thisOriginalColor = newRois.getNextChoice();
					OriginalColorNames[i] = thisOriginalColor;
					ColorNames[i] = thisOriginalColor.toLowerCase();
					ChannelSelect[i] = newRois.getNextBoolean();
				}
				
				for (int i = 0 ; i < 4 ; i++) {
					System.out.println("Channel " + i + " path " + roiChannelPaths[i]);
					System.out.println("Channel " + i + " color " + ColorNames[i]);
					System.out.println("Channel Select " + i + " " + ChannelSelect[i] );
				}
				
				allRoisPath = newRois.getNextString();
				//Save preferences
				savePrefs();
				
				//Create a mappping of colors chosen from the menu and the groups corresponding to the 
				//closest representations of these colors in the Glasbey LUT.
				
				LinkedHashMap<String, Integer> ColorsMap = new LinkedHashMap<String, Integer>();
				
				int s= 0;
				for (String Col : ColorChoice) {
					
					String thisColor = ColorChoice[s];
					
					switch(thisColor){
						case "Red" : ColorsMap.put(thisColor, 2);
						break;
						case "Green" : ColorsMap.put(thisColor, 3);
						break;
						case "Blue" : ColorsMap.put(thisColor, 1);
						break;
						case "Magenta" : ColorsMap.put(thisColor, 235);
						break;
						case "Cyan" : ColorsMap.put(thisColor, 27);
						break;
						case "Yellow" : ColorsMap.put(thisColor, 87);
						break;
						case "Orange" : ColorsMap.put(thisColor, 114);
						break;
						case "Black" : ColorsMap.put(thisColor, 4);
						break;
						case "White" : ColorsMap.put(thisColor, 0);
						break;
						case "Gray" : ColorsMap.put(thisColor, 186);
						break;
						case "lightGray" : ColorsMap.put(thisColor, 222);
						break;
						case "darkGray" : ColorsMap.put(thisColor, 76);
						break;
						case "Pink" : ColorsMap.put(thisColor, 13);
					}
					s++;
				}

				for(int i = 0 ; i < ColorNames.length ; i++) {
					//Set groups corresponding to the chosen colors
					Color decoded = Colors.decode(ColorNames[i]);
					StrokeColors[i] = decoded;
					Groups[i] = ColorsMap.get(OriginalColorNames[i]);
				}
				
				for (int i = 0 ; i < 4 ; i++) {
					if (ChannelSelect[i]==false) {
						ChRois.add(i, new ArrayList<Roi>());
						//BoundsList.add(i, new ArrayList<Rectangle>()); (?)
						continue;
					}
					
					RoiManager.getInstance().reset();
			
					IJ.open(roiChannelPaths[i]);
					
					Roi[] thisCRois = RoiManager.getInstance().getRoisAsArray();
					@SuppressWarnings("unused")
					int cl = 0 ;
					for (Roi roi : thisCRois) {
						
						//Get ROI groups based on their assigned names during co-expression analysis of reanalysis
						//and set new  groups based on the chosen colors. (Each color corresponding to its closest 
						//in the Glasbey LUT
						
						if (roi.getName().startsWith("1-")) {
							roi.setGroup(Groups[0]);
						}
						else if (roi.getName().startsWith("2-")) {
							//roi.setStrokeColor(StrokeColors[1]);
							roi.setGroup(Groups[1]);
						}
						else if (roi.getName().startsWith("3-")) {
							roi.setGroup(Groups[2]);
							
						}
						else if (roi.getName().startsWith("4-")) {
							roi.setGroup(Groups[3]);
						}
						
						cl++;
						
						BoundsList.get(i).add(roi.getBounds());
						
					}
					
					ChRois.add(i, new ArrayList<Roi>(Arrays.asList(thisCRois)));
					
				
				}
				
				RoiManager.getInstance().reset();
				
				IJ.open(allRoisPath);
				
				@SuppressWarnings("unused")
				int cl = 0;
					
				Roi[] AllRois = RoiManager.getInstance().getRoisAsArray();
				
				try {
					for (Roi roi : AllRois) {
						
						if (roi.getName().startsWith("1-")) {
							roi.setGroup(Groups[0]);
							//System.out.println(roi.getStrokeColor());
							//roi.setStrokeColor();
						}
						else if (roi.getName().startsWith("2-")) {
							roi.setGroup(Groups[1]);
							//System.out.println(roi.getStrokeColor());
						}
						else if (roi.getName().startsWith("3-")) {
							roi.setGroup(Groups[2]);
							//System.out.println(roi.getStrokeColor());
						}
						else if (roi.getName().startsWith("4-")) {
							roi.setGroup(Groups[3]);
							//System.out.println(roi.getStrokeColor());
						}
						else {roi.setGroup(0);}
						
						//System.out.println("Roi in AllRois" + cl + " Color " + roi.getStrokeColor());
						
						cl++;
					}
					
					
				} catch (NullPointerException e) {
					IJ.showMessage("No ROIs found in the AllROIs file");
				}
				
				
				RoiManager.getInstance().reset();
				
				for (Roi roi : AllRois){
					RoiManager.getInstance().addRoi(roi);
				}
				
				Path filePatha = Paths.get(allRoisPath);
				String Ooriginalname = filePatha.getFileName().toString();
				String inputDirr = filePatha.getParent().toString();
				
				runStardist.saveRois(inputDirr, Ooriginalname + "intermediate");
				
				RoiManager.getInstance().reset();
				
				IJ.open(inputDirr + "/" +Ooriginalname + "intermediate.zip");
			
				IJ.log("Loading ROIs and applying Colors");
				
				ModifyRoiManager();
				
				ChannelThread thisChannelThread = new ChannelThread();
				
				FutureTask<Boolean> futureTask = new FutureTask<Boolean>((Callable<Boolean>) thisChannelThread);
				
		        // Create a thread pool
		        ExecutorService executor = Executors.newSingleThreadExecutor();
		        
		        // Execute the FutureTask in the thread pool
		        executor.submit(futureTask);
		        
		        // Get the result from the FutureTask (blocks until the result is available)
		       
		        @SuppressWarnings("unused")
				Boolean channelResult;
		        
		        try {
		            channelResult = futureTask.get() != null; 
		            // Use overlapRoxxResult obtained from the thread
		        } catch (InterruptedException | ExecutionException | NullPointerException e) {
		            // Handle exceptions
		        	e.printStackTrace();
		        	
		        } finally {
		        	if (saved ==true) {
		        	System.out.println("Save complete");
		        	}
		        	executor.shutdown(); // Shutdown the executor
		        }
		        
		        if (modcanceled == true) {
					
		        	Thread.currentThread().interrupt();
		        	Thread.currentThread().sleep(1);
		        	
		        }
				
	        	//Revert RoiManager and ImageJ to original states
				
				revertKeyListeners();
	        	
				ChannelSelect = new boolean[4];
				ColorChoice = new String[4];	
				StrokeColors = new Color[4];
				allRoisPath = null;
				CurrentColor = null;
				CurrentChannel = 0;
				ChRois = new ArrayList<ArrayList<Roi>>();
				BoundsList = new ArrayList<ArrayList<Rectangle>>();
				//nowSaving = false;
				//saved = false;
				modfinished =true;
				CurrentGroup = 0;
				//RoiManager.getInstance().dispose();
				if (RoiManager.getInstance()!=null) {
					RoiManager.getInstance().close();
				}	
			} catch (InterruptedException e) {
				System.out.println("Canceled");
	    		//modcanceled = false;
				Path filePath = Paths.get(allRoisPath);
				String Ooriginalname = filePath.getFileName().toString(); 
				String inputDirr = filePath.getParent().toString();

				File deleteTemporary = new File(inputDirr + "/" +Ooriginalname + "intermediate.zip");
	        	deleteTemporary.delete();
				
	        	//Revert RoiManager to original state
	        	
	        	revertKeyListeners();
	        	
	        	//GlobalEventListener.removeCustomAWTListener();
	        	
	    		modfinished = false;
	    		saved = false;
	    		nowSaving = false;
	    		ChannelSelect = new boolean[4];
				ColorChoice = new String[4];	
				StrokeColors = new Color[4];
				allRoisPath = null;
				CurrentColor = null;
				CurrentChannel = 0;
				ChRois = new ArrayList<ArrayList<Roi>>();
				BoundsList = new ArrayList<ArrayList<Rectangle>>();
				CurrentGroup = 0;
				//System.out.println("reset static values");
				
			} 
			
		}
		
		modcanceled = false;
		modfinished = false;
		saved = false;
		nowSaving = false;
		System.out.println("run finished");
	}
	
	
	
	//Thread to call from the 
	class ChannelThread extends Thread implements Callable {
		
		//OverlapRoxx overlapThis;
	Boolean save;
		
	    public ChannelThread() {

	    	this.save = saved;
	    	
	    }
		
	    public boolean getSave() {
	    	
	    	this.save = saved;
	    	return this.save;
	    	
	    }
		
	    @Override
	    public Boolean call() throws Exception {
	    	
			roiFrame2 new2Frame = new roiFrame2();		
			
			while (this.getSave() == false && modcanceled == false ) {
				Thread.sleep(5);	
			}
			
	    	if (saved ==true ) {
	    		System.out.println("Saving...");
	    	}
	    	
	    	new2Frame.dispose();
	    	return saved;
	    	
	    }

	}

	public static class roiFrame2 extends Frame implements ActionListener, WindowListener {
		

		JRadioButton ch1toggle;
		JRadioButton ch2toggle;
		JRadioButton ch3toggle;
		JRadioButton ch4toggle;
		Button saveButton;
		
		
		roiFrame2(){
			
			Panel radioPanel = new Panel();
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			radioPanel.setLayout(new GridLayout(5,1));
			
			ch1toggle = new JRadioButton("Channel 1");
			ch2toggle = new JRadioButton("Channel 2");
			ch3toggle = new JRadioButton("Channel 3");
			ch4toggle = new JRadioButton("Channel 4");
		
			JRadioButton[] toggles = new JRadioButton[4];
			toggles[0] = ch1toggle;
			toggles[1] = ch2toggle;
			toggles[2] = ch3toggle;
			toggles[3] = ch4toggle;
			
			ButtonGroup channelGroup = new ButtonGroup();
			
			for (int i = 0 ; i < 4 ; i++ ) {
				toggles[i].addActionListener(this);
			}
		
			for (int i = 0 ; i < 4 ; i++) {
				channelGroup.add(toggles[i]);
			}

			radioPanel.add(ch1toggle);
			radioPanel.add(ch2toggle);
			radioPanel.add(ch3toggle);
			radioPanel.add(ch4toggle);
			
			
			radioPanel.setSize(new Dimension(30,100));
			radioPanel.setPreferredSize(new Dimension(30,100));
			
			this.add(radioPanel);
			
			saveButton = new Button("Save");		
			
			saveButton.addActionListener(this);
			
			Panel savePanel = new Panel();
			
			savePanel.add(saveButton);
			
			savePanel.setSize(new Dimension(30, 15));
			
			this.add(savePanel);
			
			addWindowListener(this);
			
			this.pack();
			
			centerWindowOnMainScreen(this);
			
			this.setVisible(true);
			
		}

		@Override
		public void actionPerformed(java.awt.event.ActionEvent  e) {
			
			if (e.getSource().equals(ch1toggle)) {
				//System.out.println("Ch1 toggled");
				//CurrentColor = StrokeColors[0];
				CurrentGroup = Groups[0];
				
				CurrentChannel = 0;
				//System.out.println("Ch1 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch2toggle)) {
				//System.out.println("ch2 toggled");
				//CurrentColor = StrokeColors[1];
				CurrentGroup = Groups[1];
				
				CurrentChannel = 1;
				//System.out.println("Ch2 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch3toggle)) {
				//System.out.println("ch3 toggled");
				//CurrentColor = StrokeColors[2];
				CurrentGroup = Groups[2];
				
				CurrentChannel = 2;
				//System.out.println("Ch3 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch4toggle)) {
				System.out.println("ch4 toggled");
				//CurrentColor = StrokeColors[3];
				CurrentGroup = Groups[3];
				CurrentChannel = 3;
				//System.out.println("Ch4 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(saveButton)) {
				
				IJ.log("Saving Modifications...");
				System.out.println("Saving Modifications");
				
				nowSaving = true;
				
				//System.out.println("mod canceled? " + modcanceled);
				
				Path filePatha = Paths.get(allRoisPath);
				String Ooriginalname = filePatha.getFileName().toString();
				String inputDirr = filePatha.getParent().toString();
				Roi[] allRoisEnd = RoiManager.getInstance().getRoisAsArray();

				RoiManager.getInstance().reset();

				for (int r = 0 ; r < allRoisEnd.length ; r++) {
					if (allRoisEnd[r].getGroup()!=0) {
						RoiManager.getInstance().addRoi(allRoisEnd[r]);
					} 
					else {
						RoiManager.getInstance().addRoi(allRoisEnd[r]);
					}
				}
				
				runStardist.saveRois(inputDirr,  Ooriginalname + "_Modified");				
				
				int p = 0 ;
				
				synchronized(ChRois){
					for (ArrayList<Roi> ChList : ChRois) {
						if (ChannelSelect[p]==true) {
							//System.out.println("ChRois " + p + " size " + ChList.size());
							RoiManager.getInstance().reset();
							synchronized(ChList){
								//System.out.println("ChRois " + p + " size " + ChList.size());
									
								Iterator<Roi> RoiIterator = ChList.iterator();

								while (RoiIterator.hasNext()) {
	
									Roi roi = (Roi) RoiIterator.next();	 
									if (roi.getGroup()!=0) {
										
										RoiManager.getInstance().addRoi(roi);
									
									}
									else if(roi.getGroup()==0) {
										RoiManager.getInstance().addRoi(roi);
									}
								}
							}
						
						
							Path filePath = Paths.get(roiChannelPaths[p]);
							String originalname = filePath.getFileName().toString(); 
							String inputDir = filePath.getParent().toString();
							
							
							runStardist.saveRois(inputDir, originalname + "_Modified");
							//System.out.println("saved : " + saved); 
							
							//Delete intermediate file
	
							File deleteTemporary = new File(inputDirr + "/" +Ooriginalname + "intermediate.zip");
				        	deleteTemporary.delete();
				        	
						}
						p++;
					}
				}			
				
				IJ.log("All ROI modifications have been saved");
				System.out.println("All ROI modifications have been saved");
				
				//System.out.println("window closed");
				//this.dispose();
				saved = true;
				WindowManager.closeAllWindows();
				
			}
		}
		
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				if (saved == true) {					
					this.dispose();
				} 
				else if (saved == false) {
					modcanceled =true;
					//this.dispose();
				}
				WindowManager.closeAllWindows();
			}
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		public static void centerWindowOnMainScreen(Window window) {
			// Get the default graphics environment
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			// Get the primary screen device
			GraphicsDevice primaryScreen = ge.getDefaultScreenDevice();
			
			// Get the screen size of the primary screen
			Rectangle screenBounds = primaryScreen.getDefaultConfiguration().getBounds();
			Dimension screenSize = screenBounds.getSize();
			
			// Calculate the center coordinates
			int centerX = screenBounds.x + (screenSize.width - window.getWidth()) / 2;
			int centerY = screenBounds.y + (screenSize.height - window.getHeight()) / 2;
			
			// Set the window location
			window.setLocation(centerX, centerY);
		}
	}
		
		
		
		
	public static class ChannelRois extends GenericDialog {
		
		
		Panel panel;
		Button button;
		Button button3;
			
		ChannelRois(String title, Frame frame){
			super(title, frame);
			this.addCheckbox("", prefss.getBoolean("Channel1Selectionn", false));
			this.addToSameRow();
			this.addFileField("Channel 1", prefss.get("inputRoi1", ""), 12);
			this.addToSameRow();
			this.addChoice("Color", ColorChoice, prefss.get("Color1", "magenta"));
			
			
			
			this.addCheckbox("", prefss.getBoolean("Channel2Selectionn", false));
			this.addToSameRow();
			this.addFileField("Channel 2", prefss.get("inputRoi2",""), 12);
			this.addToSameRow();
			this.addChoice("Color", ColorChoice, prefss.get("Color2", "magenta"));
			
			this.addCheckbox("", prefss.getBoolean("Channel3Selectionn", false));
			this.addToSameRow();
			this.addFileField("Channel 3", prefss.get("inputRoi3", ""), 12);
			this.addToSameRow();
			this.addChoice("Color", ColorChoice, prefss.get("Color3", "magenta"));
			
			this.addCheckbox("", prefss.getBoolean("Channel4Selectionn", false));
			this.addToSameRow();
			this.addFileField("Channel 4", prefss.get("inputRoi4", ""), 12);
			this.addToSameRow();
			this.addChoice("Color", ColorChoice, prefss.get("Color4", "magenta"));
			
			this.addFileField("All ROIs File", prefss.get("allRoisChannel", ""), 12);
			
			panel = new Panel();
			
			button = new Button();
			button.setLabel("OK");
			button.setFocusable(true);
			panel.add(button);
			button.addActionListener(this);

			button3 = new Button();
			button3.setLabel("Cancel");
			panel.add(button3);
			button3.addActionListener(this);
			panel.setSize(476, 25);
			panel.setName("OK Cancel panel");

			this.addPanel(panel, GridBagConstraints.EAST, new Insets(0,0,0,-90));
			
			this.setSize(600,300);
			this.addWindowListener(this);
			centerDialogOnMainScreen(this);
			
		}
			

		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) throws java.lang.ArrayIndexOutOfBoundsException {
			if (e.getSource().equals(button)) {
				this.dispose();	
			}
			else if (e.getSource().equals(button3)){
				modcanceled = true;
				this.dispose();
			}
		}
		
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				modcanceled = true;
				this.dispose();
			}
		}
		
		
	    public static void centerDialogOnMainScreen(GenericDialog Frame) {
	    	// Get the default graphics environment
	    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    	
	    	// Get the primary screen device
	    	GraphicsDevice[] screens = ge.getScreenDevices();
	    	GraphicsDevice primaryScreen = screens[0]; // Assuming the first screen is the primary/main screen
	    	
	    	// Get the screen size of the primary screen
	    	Dimension screenSize = primaryScreen.getDefaultConfiguration().getBounds().getSize();
	    	
	    	// Calculate the center coordinates
	    	int centerX = (screenSize.width - Frame.getWidth()) / 2;
	    	int centerY = (screenSize.height - Frame.getHeight()) / 2;
	    	
	    	// Set the dialog location
	    	Frame.setLocation(centerX, centerY);
	    }
		
	   		
			
	}
	
	
	
	int cc = 0;
	
	public void ModifyRoiManager() {
		
		//System.out.println("Now modifying the RoiManager");
		
		Component[] roiComps = RoiManager.getInstance().getComponents();
	
		Component Componentat = roiComps[1].getComponentAt(5,5);
		
		addButton = (Button) Componentat;

		ActionListener[] originalListeners = addButton.getActionListeners();
		
		cc = 0;
	
		for (int i = 0 ; i < originalListeners.length ; i++) {
			addButton.removeActionListener(originalListeners[i]);
		}

		addButton.addActionListener(new ActionListener() {
		

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	
	        	Roi roi = getSelectedRoi();
	        	
	        	if (e.getActionCommand()!=null) {
	        	 	if (roi != null) {
	        	 		Integer ChanName = CurrentChannel + 1;
	        	 		Integer CurChan = ChRois.get(CurrentChannel).size() + 1;
	        	 		roi.setGroup(CurrentGroup);
	        	 		roi.setName(ChanName + "-" + CurChan);
	        	 		if (!ChannelSelect[CurrentChannel]==false && nowSaving == false ) {
	        	 			
		        	 		ChRois.get(CurrentChannel).add(roi);
			        		BoundsList.get(CurrentChannel).add(roi.getBounds());
				            cc++;
	        	 		}
	        	 	}
	        	}
	        	
	        	 	try {
		    	        for (ActionListener Listener : originalListeners) {	
		    	        	Listener.actionPerformed(e);
		    	        }
		    	      
	        	 	} catch (Exception f) {
	        	 		f.printStackTrace();
	        	 	}					
	        }
        	
	        public Roi getSelectedRoi() {
	        	if (IJ.getImage() != null) {
	        		return IJ.getImage().getRoi();
	        	}
	        	else {return null;}
        	}
		});
			
		
		for (Component comp : RoiManager.getInstance().getComponents()) {
            if (comp instanceof Panel) {
                for (Component subComp : ((Panel) comp).getComponents()) {
                    if (subComp instanceof Button) {
                        deleteButton = (Button) subComp;
                        if ("Delete".equals(deleteButton.getLabel())) {
                        	oldListeners = deleteButton.getActionListeners();	
                        	for (int i = 0 ; i < oldListeners.length ; i++) {
                        		deleteButton.removeActionListener(oldListeners[i]);
                        	}
                        	deleteButton.addActionListener(new ActionListener(){
                        		
                        		@Override
                        		public void actionPerformed(ActionEvent e){
                        			if (RoiManager.getInstance().getCount()!=0 && RoiManager.getInstance().selected()!=0 && ChannelSelect[CurrentChannel]!=false) {
                        				int index = RoiManager.getInstance().getSelectedIndex();
                        				Rectangle thisBounds = RoiManager.getInstance().getRoi(index).getBounds();
                        				int deleteIndex = BoundsList.get(CurrentChannel).indexOf(thisBounds);
                        				
                        				try {
                        				BoundsList.get(CurrentChannel).remove(deleteIndex);
                        				ChRois.get(CurrentChannel).remove(deleteIndex);
                        				
		                        			try {
		                                	for (int i = 0 ; i < oldListeners.length ; i++) {
		                                		oldListeners[i].actionPerformed(e);
		                                	}
		                        			} catch (Exception f) { 
		                        				f.printStackTrace();
		                        			}
                        				} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        					IJ.showMessage("Please toggle the correct channel before attempting to delete the channel ROI");
                        				}
	                        			
                        			}
                        			
                       
                        		}
                        	});
                        }
                    }
            	}
            }
		}
			
		myAdapter = new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 't') {
					addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, 1001, null));
				}
			}
		};
		
		ImageJ ij = IJ.getInstance();
		
		myFilteredListener = new FilteredKeyListener(myAdapter);
		RoiManagerKeyListener roiManagerListener = new RoiManagerKeyListener();
		RoiManagerKeyListener.GlobalEventListener.AWTListen();

		RoiManager.getInstance().setFocusable(true);
		RoiManager.getInstance().requestFocusInWindow();
		
		
	}
	
	public void revertKeyListeners() {
		//Remove GlobalEventListener
		RoiManagerKeyListener.GlobalEventListener.removeCustomAWTListener();
		
		//Remove FilteredKeyListener from each component and keep only original listener 
		ImageJ ij = IJ.getInstance();
		
		RoiManagerKeyListener.revertIJKeyListeners(ij, RoiManagerKeyListener.getIJKeyListeners());
		RoiManagerKeyListener.revertRMKeyListeners();
		RoiManagerKeyListener.revertImageListener();
		
		ImagePlus.removeImageListener(RoiManagerKeyListener.getCustomImageListener());
		
		//Remove addButton custom action listener and keep only original action listener(?)
	}
	
	
	public static class FilteredKeyListener extends KeyAdapter {
	    private final KeyAdapter customKeyAdapter;

	
	    public FilteredKeyListener(KeyAdapter customKeyAdapter) {
	        this.customKeyAdapter = customKeyAdapter;
	    }
	
	    @Override
	    public void keyPressed(KeyEvent e) {
	        if (e.getKeyChar() == 't') {
	            customKeyAdapter.keyPressed(e);
	            //System.out.println("FilteredListener triggered");
            	//System.out.println("event source " + e.getSource());
	            ActionEvent ae = new ActionEvent(addButton, 1001, "Add [t]");
                addButton.getActionListeners()[0].actionPerformed(ae);
                e.consume();
                
	        }
	    }
	    
	    
	    public static void removeKeyListener(Component component, char keyChar) {
	    	//Remove KeyListener from a component 
	        KeyListener[] keyListeners = component.getKeyListeners();
	        for (KeyListener keyListener : keyListeners) {
	            if (keyListener instanceof KeyAdapter) {
	                KeyAdapter adapter = (KeyAdapter) keyListener;
	                if (adapter instanceof FilteredKeyListener) {
	                    component.removeKeyListener(keyListener);
	                }
	            }
	        }
	    }
	    
	}

		
	
	public static class RoiManagerKeyListener {
	
	    public static RoiManager roiManager;
	    private static KeyListener[] IJKeyListeners;
	    private static ImageListener CustomImageListener;
	    
	    public static KeyListener[] getIJKeyListeners() {
	    	return IJKeyListeners;
	    }
	    
	    public static ImageListener getCustomImageListener() {
	    	return CustomImageListener;
	    }
	    
	    public RoiManagerKeyListener() {
	        this.IJKeyListeners = IJKeyListeners;
	        initialize();
	    }
	
	    public void initialize() {
	        if (roiManager == null) {
	            roiManager = new RoiManager();
	        }
	        
	        addKeyListenersToImageJ();
	        
	        ImagePlus.logImageListeners();
	        CustomImageListener = new ImageListener() {
	        	  @Override
		            public void imageOpened(ImagePlus imp) {
		                addKeyListenerToImage(imp);
		            }
		
		            @Override
		            public void imageClosed(ImagePlus imp) {
		            }
		
		            @Override
		            public void imageUpdated(ImagePlus imp) {
		            }
	        	
	        };
	        //Add the created custom image listener
	        ImagePlus.addImageListener(CustomImageListener);
	        // Add listeners to RoiManager window
	        addKeyListenersToRoiManager();
	        roiManager.setFocusable(true);
	        roiManager.requestFocus();
	        

	    }
	    
	    public static void addKeyListenerToImage(ImagePlus imp) {
	    	//Add custom KeyListener to an opened image
	        ImageWindow window = imp.getWindow();  
	        if (window != null) {
	            FilteredKeyListener.removeKeyListener(window, 't');
	            for (KeyListener k : window.getKeyListeners())
	            	window.removeKeyListener(k);
        	}
            window.addKeyListener(myFilteredListener);
            ij.gui.ImageCanvas thisc = imp.getCanvas();
            if (thisc != null) {
	            FilteredKeyListener.removeKeyListener(thisc, 't');
	            
	            for (KeyListener k : thisc.getKeyListeners()) {
	            	thisc.removeKeyListener(k);
	            }
	            thisc.addKeyListener(myFilteredListener);
            }   
        }
	    
	
	    
	    private void addKeyListenersToImageJ() {
	    	//Add custom KeyListener to ImageJ
	        ImageJ ij = IJ.getInstance();
	        if (ij != null) {
	        	IJKeyListeners = ij.getKeyListeners();
	        	FilteredKeyListener.removeKeyListener(ij, 't');
	            ij.addKeyListener(myFilteredListener);
	        }
	    }
	    
	    private static void revertIJKeyListeners(Component component, KeyListener[] KeyListeners) {
	    	//Remove GlobalKeyListener
	    	ImageJ ij = IJ.getInstance();
	    	RoiManagerKeyListener.GlobalEventListener.removeCustomAWTListener();
	    	//Remove FilteredKeyListener from ImageJ
	    	ij.removeKeyListener(myFilteredListener);
	    	//Restore original KeyListeners
	    	int c = 0;
	    	for (KeyListener keyListener : IJKeyListeners) {
		    	ij.addKeyListener(keyListener);
		    	c++;
	    	}
	    }
	    
	    private static void revertRMKeyListeners() {
	    	//Remove Roi Manager custom listeners
	        for (Component comp : roiManager.getComponents()) {
	            FilteredKeyListener.removeKeyListener(comp, 't');
	            comp.addKeyListener(myFilteredListener);
	            		
	            if (comp instanceof java.awt.Panel) {
	                for (Component subComp : ((java.awt.Panel) comp).getComponents()) {
	                	FilteredKeyListener.removeKeyListener(subComp, 't');
	                	subComp.removeKeyListener(myFilteredListener);
	                }
	            }
	        }
	    }
	    
	    private static void revertImageListener() {
	    	ImagePlus.removeImageListener(CustomImageListener);
	    }
	        
	 
	    private static void addKeyListenersToRoiManager() {
	        FilteredKeyListener.removeKeyListener(roiManager, 't');
	        roiManager.addWindowListener(new CustomWindowListener());
	        roiManager.addKeyListener(myFilteredListener);
	        for (Component comp : roiManager.getComponents()) {
	            FilteredKeyListener.removeKeyListener(comp, 't');
	            comp.addKeyListener(myFilteredListener);
	            		
	            if (comp instanceof java.awt.Panel) {
	                for (Component subComp : ((java.awt.Panel) comp).getComponents()) {
	                	FilteredKeyListener.removeKeyListener(subComp, 't');
	                    subComp.addKeyListener(myFilteredListener);
	                }
	            }
	        }
   
	    }
	    
	    private static Button getAddButton() {
	        for (Component comp : roiManager.getComponents()) {
	            if (comp instanceof java.awt.Panel) {
	                for (Component subComp : ((java.awt.Panel) comp).getComponents()) {
	                    if (subComp instanceof Button) {
	                        Button button = (Button) subComp;
	                        if ("Add [t]".equals(button.getLabel())) {
	                        	return button;
	                        }
	                    }
	                }
	            }
	        }
	        return null;
	    }

	    public static class CustomWindowListener extends WindowAdapter {
	    	
	    	
	        @Override
	        public void windowOpened(WindowEvent e) {
	            //System.out.println("RoiManager window opened");
	        }

	        @Override
	        public void windowClosing(WindowEvent e) {
	            //System.out.println("RoiManager window closing");
	        }

	        @Override
	        public void windowClosed(WindowEvent e) {
	            //System.out.println("RoiManager window closed");
	        }

	        @Override
	        public void windowIconified(WindowEvent e) {
	            // No action needed
	        }

	        @Override
	        public void windowDeiconified(WindowEvent e) {
	            // No action needed
	        }

	        @Override
	        public void windowActivated(WindowEvent e) {
	            //System.out.println("RoiManager window activated");
	        }

	        @Override
	        public void windowDeactivated(WindowEvent e) {
	            //System.out.println("RoiManager window deactivated");
	        }
	    }
	    
	    public static class GlobalEventListener {
	        public static void AWTListen() {
	            // Add a global AWT event listener
	        	Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
	                @Override
	                public void eventDispatched(AWTEvent event) {
	                    if (event instanceof KeyEvent) {
	                        KeyEvent ke = (KeyEvent) event;
	                        if (ke.getKeyChar() == 't' && ke.getID() == KeyEvent.KEY_PRESSED) {
	                            //System.out.println("Global listener detected 't' key press: " + ke);
	                        }
	                    }
	                }
	            }, AWTEvent.KEY_EVENT_MASK);
	        	
	        }
	        
	    
	        public static void removeCustomAWTListener() {
	        	AWTEventListener[] AWTListenersNow = Toolkit.getDefaultToolkit().getAWTEventListeners();
	        	if (AWTListenersNow.length > 1 ) {
	        		Toolkit.getDefaultToolkit().removeAWTEventListener(AWTListenersNow[AWTListenersNow.length - 1]);
	        	}
	        }
	    
	    }
	    
	}
    
    public static void savePrefs() {
    	for (int i = 0 ; i < 4 ; i++) {
    	int a = i + 1;
    		prefss.put("inputRoi" + a, roiChannelPaths[i]);
    		//preffs.put(ColorNames[])
    		prefss.put("Color" + a, OriginalColorNames[i]);
    		//prefss.put(", allRoisPath);
    		prefss.putBoolean("Channel" + a + "Selectionn",  ChannelSelect[i]);
    	}
    	prefss.put("allRoisChannel", allRoisPath);
    	
    }
	
}










