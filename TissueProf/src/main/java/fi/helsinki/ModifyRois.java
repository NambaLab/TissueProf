package fi.helsinki;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
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
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;
import java.awt.event.MouseEvent;
import java.awt.LayoutManager;
import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import org.apache.commons.io.FileSystemUtils;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import com.google.api.client.util.Strings;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import ij.IJ;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.measure.ResultsTable;
import ij.plugin.Colors;
import ij.plugin.LutLoader;
import ij.plugin.frame.RoiManager;
import ij.process.LUT;
import net.imagej.display.ImageCanvas;

import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;


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
		
		System.out.println("Now running");
		
		while (modcanceled == false && modfinished == false) {
			try {
				//System.out.println("Started");
				System.out.println("Running once");
				//get current thread
				modRunThread = Thread.currentThread();
				
				boolean ModifyRois = true; 
				
				//create a ROI Manager instance
				RoiManager.getRoiManager();
				
				ColorChoice = Colors.getColors();
				
				for (String col : ColorChoice) {
					System.out.println(col);
				}
				
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
				
				System.out.println("ColorChoice length " + ColorChoice.length);
				
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
						
				
				for (Entry<String, Integer> colPair : ColorsMap.entrySet()) {
					
					System.out.print("Color " + colPair.getKey());
					System.out.print("        Group " + colPair.getValue() + "\n");
					
				}
				

				for(int i = 0 ; i < ColorNames.length ; i++) {
					//ColorNames[i]. java.awt.Color.decode();
					
					Color decoded = Colors.decode(ColorNames[i]);
					StrokeColors[i] = decoded;
					
					Groups[i] = ColorsMap.get(OriginalColorNames[i]);
					//java.lang.String.colorToString(decoded);​
					System.out.println("Color name reencoded " + i + " " + Colors.colorToString(decoded));
					
				}
				
				
				for (Color stroke : StrokeColors) {
					System.out.println("Stroke RGB " + stroke.getRGB());
		
				}
		
				
				s = 1;
				for (int group : Groups) {
					System.out.println("Group " + s + " = " + group);
					s++;
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
					int cl = 0 ;
					for (Roi roi : thisCRois) {
						
						//Get ROI groups based on their assigned names during co-expression analysis of reanalysis
						//And set new  groups based on the chosen colors. (Each color corresponding to its closest 
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
						
						//RoiManager.getInstance().addRoi(roi);
						
						//System.out.println("Roi " + cl + " Color " + roi.getStrokeColor());
						
						BoundsList.get(i).add(roi.getBounds());
						
						
					}
					
					ChRois.add(i, new ArrayList<Roi>(Arrays.asList(thisCRois)));
					
				
				}
				
				RoiManager.getInstance().reset();
				
				IJ.open(allRoisPath);
				
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
				
				int c= 0;
				for (ArrayList<Roi>thisList : ChRois) {
					System.out.println("ch " + c + " size " + ChRois.get(c).size());
					c++;
				}
				
				
				
				ModifyRoiManager();
			
				
				ChannelThread thisChannelThread = new ChannelThread();
				
				FutureTask<Boolean> futureTask = new FutureTask<Boolean>((Callable<Boolean>) thisChannelThread);
				
		        // Create a thread pool
		        ExecutorService executor = Executors.newSingleThreadExecutor();
		        
		        // Execute the FutureTask in the thread pool
		        executor.submit(futureTask);
		        
		        // Get the result from the FutureTask (blocks until the result is available)
		       
		        Boolean channelResult;
		        
		        try {
		            channelResult = futureTask.get() != null; 
		            // Use overlapRoxxResult obtained from the thread
		        } catch (InterruptedException | ExecutionException | NullPointerException e) {
		            // Handle exceptions
		        	e.printStackTrace();
		        	
		        } finally {
		        	if (saved ==true) {
		        	System.out.println("Saving...");
		        	}
		        	executor.shutdown(); // Shutdown the executor
		        }
		        
		        if (modcanceled == true) {
					
		        	Thread.currentThread().interrupt();
		        	Thread.currentThread().sleep(1);
		        	
		        }
				
				// TODO Auto-generated method stub
			
				System.out.println("still runnning");
				
	        	//Revert RoiManager to original state
				
				revertKeyListeners();
				
	        	//GlobalEventListener.removeCustomAWTListener();
				
	        	
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
				RoiManager.getInstance().close();
				
				System.out.println("Going on");
				
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
				System.out.println("reset static values");
				
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
			String running;
			
			//saved = false; 
			
			int c = 0;
			while (this.getSave() == false && modcanceled == false ) {
				if(c==30) {
					System.out.println("thisSave? " + this.save);
					System.out.println("Saved ? " + saved);
					c++;
					c--;
				
				}
				
				c++;	
			}
			
			
	    	if (saved ==true ) {
	    		
	    		System.out.println("saved and returning");
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
				System.out.println("Ch1 toggled");
				//CurrentColor = StrokeColors[0];
				CurrentGroup = Groups[0];
				
				CurrentChannel = 0;
				System.out.println("Ch1 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch2toggle)) {
				System.out.println("ch2 toggled");
				//CurrentColor = StrokeColors[1];
				CurrentGroup = Groups[1];
				
				CurrentChannel = 1;
				System.out.println("Ch2 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch3toggle)) {
				System.out.println("ch3 toggled");
				//CurrentColor = StrokeColors[2];
				CurrentGroup = Groups[2];
				
				CurrentChannel = 2;
				System.out.println("Ch3 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(ch4toggle)) {
				System.out.println("ch4 toggled");
				//CurrentColor = StrokeColors[3];
				CurrentGroup = Groups[3];
				CurrentChannel = 3;
				System.out.println("Ch4 event source " + e.getSource().toString());
			}
			else if (e.getSource().equals(saveButton)) {
				
				IJ.log("Saving Modifications...");
				System.out.println("Saving Modifications");
				
				nowSaving = true;
				
				System.out.println("mod canceled? " + modcanceled);
				
				Path filePatha = Paths.get(allRoisPath);
				String Ooriginalname = filePatha.getFileName().toString();
				String inputDirr = filePatha.getParent().toString();
				
				//System.out.println("saveButton event source " + e.getSource().toString());
				
				//WaitForUserDialog seeAllRois = new WaitForUserDialog("See all rois ");
				//seeAllRois.show();
				
				
				Roi[] allRoisEnd = RoiManager.getInstance().getRoisAsArray();
				
				ActionListener[] actListen = addButton.getActionListeners();
				//System.out.println(actListen.length);
				//System.out.println(actListen.toString());
								
				//RoiManager.getInstance().reset();
				
				//IJ.getImage().killRoi();
				
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
							System.out.println("ChRois " + p + " size " + ChList.size());
							RoiManager.getInstance().reset();
							synchronized(ChList){
								System.out.println("ChRois " + p + " size " + ChList.size());
									
								Iterator<Roi> RoiIterator = ChList.iterator();
								int count = 0 ;
								
								while (RoiIterator.hasNext()) {
	
									Roi roi = (Roi) RoiIterator.next();	 
									if (roi.getGroup()!=0) {
										
										RoiManager.getInstance().addRoi(roi);
									
									}
									else if(roi.getGroup()==0) {
										RoiManager.getInstance().addRoi(roi);
									}
								}
								//System.out.println("Count at the end of " + i + " " + count);
							}
						
						
							Path filePath = Paths.get(roiChannelPaths[p]);
							String originalname = filePath.getFileName().toString(); 
							String inputDir = filePath.getParent().toString();
							
							
							runStardist.saveRois(inputDir, originalname + "_Modified");
							System.out.println("saved : " + saved); 
							
							//Delete intermediate file
	
							File deleteTemporary = new File(inputDirr + "/" +Ooriginalname + "intermediate.zip");
				        	deleteTemporary.delete();
				        	
						}
						p++;
					}
				}			
				
				IJ.log("All ROI modifications have been saved");
				System.out.println("All ROI modifications have been saved");
				
				System.out.println("window closed");
				//this.dispose();
				saved = true;
				//OverlapAnalysis5.getThreadtoStop();
				//WindowManager.closeAllWindows();
				
			}
		}
			
			// TODO Auto-generated method stub
		
		
		
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				System.out.println("window closed");
				//modcanceled =true;
				System.out.println("modcanceled by window closing?" + modcanceled);
				//saved = true;
				if (saved == true) {					
					System.out.println("now disposing");
					this.dispose();
					System.out.println("closing after saving");
					System.out.println("modcanceled after saving? " + modcanceled);
				} 
				else if (saved == false) {
					System.out.println("Closing without saving i.e canceling mod");
					System.out.println("modcanceled without saving? " + modcanceled);
					modcanceled =true;
					//this.dispose();
				}
				
				//WindowManager.closeAllWindows();
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
			//button.setLocation
			/*button2 = new Button();
			button.setLabel("OK");
			panel.add(button2);
			button2.addActionListener(this);
			 */
			//this.addPanel
			button3 = new Button();
			button3.setLabel("Cancel");
			panel.add(button3);
			button3.addActionListener(this);
			panel.setSize(476, 25);
			panel.setName("OK Cancel panel");
			//this.addPanel(panel, GridBagConstraints.EAST, new Insets(0,0,0,-37));
			this.addPanel(panel, GridBagConstraints.EAST, new Insets(0,0,0,-90));
		//this.pack();
			//this.setSize(150,50);
			//this.setPreferredSize(new Dimension(150,50));
			
			
			this.setSize(600,300);
			this.addWindowListener(this);
			
			
			
		}
			

		@Override
		public void actionPerformed(java.awt.event.ActionEvent e){
			if (e.getSource().equals(button)) {
				System.out.println("button OK pressed ");
				this.dispose();
			}
			else if (e.getSource().equals(button3)){
				//System.out.println("This thread is making the actionListener " + Thread.currentThread().getName());
				System.out.println("button 3 pressed ");
				modcanceled = true;
				//OverlapAnalysis5.getThreadtoStop();
				this.dispose();
			}
		
		}
		
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				modcanceled = true;
				this.dispose();
				System.out.println("window closed");
				//OverlapAnalysis5.getThreadtoStop();
				//WindowManager.closeAllWindows();
			}
		}
		
	
		
		
	    public static void centerDialogOnMainScreen(Frame Frame) {
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
		
		System.out.println("now modifying roimanager");
		
		Component[] roiComps = RoiManager.getInstance().getComponents();
		
		System.out.println(
			"comp1 width = " + 
			roiComps[1].getWidth() + " comp1 height = " + 
			roiComps[1].getHeight()
		);
	
		Component Componentat = roiComps[1].getComponentAt(5,5);
		System.out.println(Componentat.getName());
		System.out.println(Componentat.toString());
		
		addButton = (Button) Componentat;

		ActionListener[] originalListeners = addButton.getActionListeners();
		
		for (ActionListener listen : originalListeners) {
			System.out.println(listen.toString());
		}
	
		cc = 0;
	
		for (int i = 0 ; i < originalListeners.length ; i++) {
			addButton.removeActionListener(originalListeners[i]);
		}
		
		
		// ActionListener[] originalListeners = addButton.getActionListeners();
		
		addButton.addActionListener(new ActionListener() {
		

	        @Override
	        public void actionPerformed(ActionEvent e) {
		        		//RoiManager.getInstance().add
	    			//ActionListener[] originalListeners = addButton.getActionListeners();
	        	

	        	System.out.println("addButton ActionListener count " + addButton.getActionListeners().length);
	        	
	        	System.out.println(this.toString());
	        	System.out.println("For the 1st event " +
	    	        	"Command " + e.getActionCommand()
	    	        	+ "ID " + e.getID()
	    	        	+ "Source " + e.getSource()
	    	        	+ "ToString " + e.toString()
	    	   
	    	        	);

	        	Roi roi = getSelectedRoi();
	        	
	        	if (e.getActionCommand()!=null) {
		        	//System.out.println("Roi Manager count before adding " + RoiManager.getInstance().getCount());
				
	        	 	//System.out.println("selected Roi bounds" + roi.getBounds().toString());
	        	 	
	        	 	if (roi != null) {
	        	 		Integer ChanName = CurrentChannel + 1;
	        	 		System.out.println(ChRois.size());
	        	 		
	        	 		for (int h = 0 ; h < ChRois.size() ; h++) {
	        	 			System.out.println("Ch Rois " + h + " size " + ChRois.get(h).size());
	        	 			System.out.println("Ch BoundsSize before " + h +" "+ BoundsList.get(h).size());
	        	 		}
	        	 		
	        	 		
	        	 		System.out.println("Current Channel " + CurrentChannel);
	        	 		Integer CurChan = ChRois.get(CurrentChannel).size() + 1;
	        	 		
	        	 		//roi.setGroup(ChanName);
	        	 		roi.setGroup(CurrentGroup);
	        	 		roi.setName(ChanName + "-" + CurChan);
	        	 
	        	 		System.out.println("Current Color " + CurrentColor);
	        	 		System.out.println("Selected current channel?" + ChannelSelect[CurrentChannel]);
	        	 		System.out.println("now  saving ?" + nowSaving);
	        	 		if (!ChannelSelect[CurrentChannel]==false && nowSaving == false ) {
	        	 			
		        	 		ChRois.get(CurrentChannel).add(roi);
			        		System.out.println("added roi to ChrRois");
		        	 		//System.out.println("Add button clicked!");
			        		//System.out.println("Roi Stroke Color Before " + roi.getStrokeColor());
			        		BoundsList.get(CurrentChannel).add(roi.getBounds());
			        		System.out.println("Added roi to boundslist");
			        		
				            cc++;
	        	 		}
	        	 		
	        	 	}
	        	 	
	        	 	System.out.println("cc " + cc);
	    			
	        	 	System.out.println("Original listener size " + originalListeners.length);
	        	 	
	        	}
	        	
	        	 	try {
		    	        for (ActionListener Listener : originalListeners) {	
		    	        	Listener.actionPerformed(e);
		    	        	
		    	        	//Get event info
		    	        	/*
		    	        	System.out.println("For the 2nd event " +
		    	        	"Command " + e.getActionCommand()
		    	        	+ "ID " + e.getID()
		    	        	+ "Source " + e.getSource()
		    	        	+ "ToString " + e.toString()
		    	        	);
		    	        	 */
		    	        }
		    	      
	        	 	} catch (Exception f) {
	        	 		f.printStackTrace();
	        	 	}
		    	    
	    	        
	    	        
	    	        System.out.println("Roi stroke color after " + roi.getStrokeColor());
					System.out.println("Roi Manager count after adding " + RoiManager.getInstance().getCount());
	    	
	    	 		for (int h = 0 ; h < ChRois.size() ; h++) {
	    	 			System.out.println("Ch Rois " + h + " size after" + ChRois.get(h).size());
	    	 			System.out.println("Ch BoundsSize after " + h + BoundsList.get(h).size());
	    	 		}
					
	        }
        	
	        public Roi getSelectedRoi() {
	        	if (IJ.getImage() != null) {
	        		return IJ.getImage().getRoi();
	        	}
	        	else {return null;}
        	}
        	
			}
			
		);									
		
		
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
                        			System.out.println("delete!");
                        			if (RoiManager.getInstance().getCount()!=0 && RoiManager.getInstance().selected()!=0 && ChannelSelect[CurrentChannel]!=false) {
                        				int index = RoiManager.getInstance().getSelectedIndex();
                        				//ChRois.get(CurrentChannel).remove(RoiManager.getInstance().getRoi(index));
                        				Rectangle thisBounds = RoiManager.getInstance().getRoi(index).getBounds();
                        				
                        				System.out.println("BoundsListSize before " + BoundsList.get(CurrentChannel).size());
                        				System.out.println("ChanneRoilSize before " + ChRois.get(CurrentChannel).size());
                        				
                        				int deleteIndex = BoundsList.get(CurrentChannel).indexOf(thisBounds);
                        				
                        				BoundsList.get(CurrentChannel).remove(deleteIndex);
                        				ChRois.get(CurrentChannel).remove(deleteIndex);
                        				
                        				System.out.println("BoundsListLength after" + BoundsList.get(CurrentChannel).size());
                        				System.out.println("Current ChannelRois Length after" + ChRois.get(CurrentChannel).size());
                        				
                        				//BoundsList.get(CurrentChannel).get(index)
                        				
                        				
                        				System.out.println("Removed ROI with index " + index + " channel index " + deleteIndex);
                        				
                        			}
                        			try {
                                	for (int i = 0 ; i < oldListeners.length ; i++) {
                                		oldListeners[i].actionPerformed(e);
                                	}
                        			} catch (Exception f) { 
                        				f.printStackTrace();
                        			}
                       
                                	
                                	System.out.println("Roi Manager after action performed " + RoiManager.getInstance().getCount());
                                	
                        		}
                        	});
                        }
                    }
            	}
            }
		}
		
		
		//RoiManagerKeyListener roiManagerListener = new RoiManagerKeyListener();    INITIALIZE HERE?
		//Button nowbutton = roiManagerListener.getAddButton();
		
		//System.out.println("Add button listener amount " + nowbutton.getActionListeners().length);

		/*
		for (int i = 0 ; i < nowbutton.getActionListeners().length ; i++) {
			System.out.println("actlisten " + i + " " + nowbutton.getActionListeners()[i]);
		}
		*/
		
		System.out.println("Add button action listener no : " + addButton.getActionListeners().length);
		
		myAdapter = new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 't') {
					// Simulate a button click
					System.out.println("t pressed ");
					addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, 1001, null));
					//java.awt.event.ActionEvent.ACTION_PERFORMED
				}
			}
		};
		
		ImageJ ij = IJ.getInstance();
		
		myFilteredListener = new FilteredKeyListener(myAdapter);
		RoiManagerKeyListener roiManagerListener = new RoiManagerKeyListener();
		RoiManagerKeyListener.GlobalEventListener.AWTListen();
		//GlobalEventListener.AWTListen();
		
		RoiManager.getInstance().setFocusable(true);
		RoiManager.getInstance().requestFocusInWindow();
		
		
	}
	
	public void revertKeyListeners() {
		//Remove GlobalEventListener
		RoiManagerKeyListener.GlobalEventListener.removeCustomAWTListener();
		
		//Remove FilteredKeyListener from each component and keep only original listener 
		ImageJ ij = IJ.getInstance();
		
		System.out.println("showing original RM Listeners");
		
		/*
		int c = 0;
		for (KeyListener ThisListener: RoiManagerKeyListener.OriginalRMKeyListeners) {
			System.out.println("c = " + c + " " + ThisListener.toString());			
			c++; 
		}
		*/
		
		RoiManagerKeyListener.revertIJKeyListeners(ij, RoiManagerKeyListener.getIJKeyListeners());
		RoiManagerKeyListener.revertRMKeyListeners();
		RoiManagerKeyListener.revertImageListener();
		
		
		System.out.println("Custom image listener : " + RoiManagerKeyListener.getCustomImageListener());
		ImagePlus.removeImageListener(RoiManagerKeyListener.getCustomImageListener());
		
		//Remove addButton custom action listener and keep only original action listener
		
		
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
	            System.out.println("FilteredListener triggered");
            	System.out.println("event source " + e.getSource());
	            ActionEvent ae = new ActionEvent(addButton, 1001, "Add [t]");
                addButton.getActionListeners()[0].actionPerformed(ae);
                e.consume();
                
	        }
	    }
	    
	    /*
	    public static KeyListener[] getKeyListeners(Component component) {
	    	KeyListener[] keyListeners = component.getKeyListeners();
	    	return keyListeners;
	    }
	   	*/
	    
	    public static void removeKeyListener(Component component, char keyChar) {
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
	    
	    private static ArrayList<KeyListener> OriginalRMKeyListeners;
	    
	    private static ImageListener CustomImageListener;
	    
	    public static KeyListener[] getIJKeyListeners() {
	    	return IJKeyListeners;
	    }
	    
	    public static ImageListener getCustomImageListener() {
	    	return CustomImageListener;
	    }
	    
	    public RoiManagerKeyListener() {
	        this.IJKeyListeners = IJKeyListeners;
	    	//this.roiManager = RoiManager.getInstance();
	        initialize();
	        
	        System.out.println("roi listener initialized");
	        
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
		            	System.out.println("image opened");
		            	ImagePlus.logImageListeners();
		                addKeyListenerToImage(imp);
		            }
		
		            @Override
		            public void imageClosed(ImagePlus imp) {
		            }
		
		            @Override
		            public void imageUpdated(ImagePlus imp) {
		            }
	        	
	        };
	        
	        IJ.log("Before adding image listener");
	        ImagePlus.logImageListeners();
	        ImagePlus.addImageListener(CustomImageListener);
	        IJ.log("After adding image listener");
	        ImagePlus.logImageListeners();
	        IJ.log("Break");
	        // Add ImageListener to detect new image openings
	        
	        /* Original imagelistener addition op
	        ImagePlus.addImageListener(new ImageListener() {
	        	
	            @Override
	            public void imageOpened(ImagePlus imp) {
	            	System.out.println("image opened");
	                addKeyListenerToImage(imp);
	            }
	
	            @Override
	            public void imageClosed(ImagePlus imp) {
	            }
	
	            @Override
	            public void imageUpdated(ImagePlus imp) {
	            }
	        });
	        */
	        
	        
	        // Add listeners to RoiManager window
	        addKeyListenersToRoiManager();
	        roiManager.setFocusable(true);
	        roiManager.requestFocus();
	        

	    }
	    
	    /*
	    private void addListenersToAllImages() {
	        int[] windowList = WindowManager.getIDList();
	        if (windowList != null) {
	            for (int id : windowList) {
	                ImagePlus imp = WindowManager.getImage(id);
	                if (imp != null) {
	                    addKeyListenerToImage(imp);
	                }
	            }
	        }
	    }
		*/
	    
	    public static void addKeyListenerToImage(ImagePlus imp) {
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
	        ImageJ ij = IJ.getInstance();
	        if (ij != null) {
	        	IJKeyListeners = ij.getKeyListeners();
	        	for (int i = 0 ; i < IJKeyListeners.length ; i++) {
	        	System.out.println("Current IJ listener " + i + ij.getKeyListeners()[i] + " " + ij.getKeyListeners()[i].hashCode());
	        	
	        	}
	        	FilteredKeyListener.removeKeyListener(ij, 't');
	            
	            ij.addKeyListener(myFilteredListener);
	            
	        	for (int i = 0 ; i < IJKeyListeners.length ; i++) {
	        	System.out.println("Current IJ listener after " + i + ij.getKeyListeners()[i] + " " + ij.getKeyListeners()[i].hashCode());
	        	
	        	}
	        }
	    }
	    
	    private static void revertIJKeyListeners(Component component, KeyListener[] KeyListeners) {
	    	//Remove GlobalKeyListener
	    	ImageJ ij = IJ.getInstance();
	    	RoiManagerKeyListener.GlobalEventListener.removeCustomAWTListener();
	    	//Remove FilteredKeyListener from each component
	    	int c = 0;
	    	for (KeyListener keyListener : IJKeyListeners) {
	    		System.out.println("IJ Key Listener before" + c + keyListener.toString() + "hash " + keyListener.hashCode());
		    	c++;
	    	}
	    	
	    	ij.removeKeyListener(myFilteredListener);
	    	c = 0;
	    	for (KeyListener keyListener : IJKeyListeners) {
		    	ij.addKeyListener(keyListener);
		    	System.out.println("IJ Key Listener after " + c + keyListener.toString() + "hash " + keyListener.hashCode());
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
	                	/* ???
	                	for (int i = 0 ; i < subComp.getKeyListeners().length ; i++) {
	                		System.out.println("i = " + i + "Component = " +subComp.getName()+ " " + subComp.getKeyListeners()[i].toString());
	                		OriginalRMKeyListeners.add(subComp.getKeyListeners()[i]);
	                	}
	                	*/
	                	
	                	FilteredKeyListener.removeKeyListener(subComp, 't');
	                    
	                	subComp.removeKeyListener(myFilteredListener);
	                	
	                    System.out.println("subcomp listener length after removing filtered listener" + subComp.getKeyListeners().length);
	                }
	            }
	        }
	    }
	    
	    private static void revertImageListener() {
	    	System.out.println("removing image listener : " + CustomImageListener.toString());
	    	ImagePlus.logImageListeners();
	    	IJ.log("Check image log ");
	    	ImagePlus.removeImageListener(CustomImageListener);
	    	ImagePlus.logImageListeners();
	    	IJ.log("Break");
	    }
	        
	        
	        
	    	
	    	
	    	
	    	
	    	
	    	//Remove addButton actionlistener
	    
	    	

	    private static ArrayList<KeyListener> saveOriginalRMKeyListeners() {
	    	System.out.println("saving original rmkeylisteners");
	    	
	    	WaitForUserDialog see = new WaitForUserDialog("see");
	    	see.show();
	    	
	        for (Component comp : roiManager.getComponents()) {
	            for (int i = 0 ; i < comp.getKeyListeners().length ; i++) {
	            	System.out.println("getting listeners ");
	            	System.out.println("coriginal comp listener length " + comp.getKeyListeners().length);
	            	//OriginalRMKeyListeners.add(comp.getKeyListeners()[i]);
	            }
	            if (comp instanceof java.awt.Panel) {
	                for (Component subComp : ((java.awt.Panel) comp).getComponents()) {
	    	            for (int i = 0 ; i < comp.getKeyListeners().length ; i++) {
	    	            	System.out.println("getting listenerss");
	    	            	System.out.println("coriginal subcomp listener length " + subComp.getKeyListeners().length);

	    	            	OriginalRMKeyListeners.add(subComp.getKeyListeners()[i]);

	    	            }
	                }
	            }
	        }
       	
	    	return OriginalRMKeyListeners;
	    	
	    }
	    
	    private static void addKeyListenersToRoiManager() {
	    	saveOriginalRMKeyListeners();
	        FilteredKeyListener.removeKeyListener(roiManager, 't');
	        roiManager.addWindowListener(new CustomWindowListener());
	        
	        roiManager.addKeyListener(myFilteredListener);
	        
	        for (Component comp : roiManager.getComponents()) {
	            FilteredKeyListener.removeKeyListener(comp, 't');
	            comp.addKeyListener(myFilteredListener);
	            		
	            if (comp instanceof java.awt.Panel) {
	                for (Component subComp : ((java.awt.Panel) comp).getComponents()) {
	                	/* ???
	                	for (int i = 0 ; i < subComp.getKeyListeners().length ; i++) {
	                		System.out.println("i = " + i + "Component = " +subComp.getName()+ " " + subComp.getKeyListeners()[i].toString());
	                		OriginalRMKeyListeners.add(subComp.getKeyListeners()[i]);
	                	}
	                	*/
	                	
	                	FilteredKeyListener.removeKeyListener(subComp, 't');
	                    
	                    subComp.addKeyListener(myFilteredListener);
	                    System.out.println("subcomp listener length" + subComp.getKeyListeners().length);
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
	            System.out.println("RoiManager window opened");
	        }

	        @Override
	        public void windowClosing(WindowEvent e) {
	            System.out.println("RoiManager window closing");
	        }

	        @Override
	        public void windowClosed(WindowEvent e) {
	            System.out.println("RoiManager window closed");
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
	            System.out.println("RoiManager window activated");
	        }

	        @Override
	        public void windowDeactivated(WindowEvent e) {
	            System.out.println("RoiManager window deactivated");
	        }
	    }
	    
	    public static class GlobalEventListener {
	        public static void AWTListen() {
	            // Add a global AWT event listener
	        	System.out.println("current awteventlisteners ");
	        	
	        	AWTEventListener[] AWTListenersNow = Toolkit.getDefaultToolkit().getAWTEventListeners();
	        	System.out.println("No of AWTListeners currently present " + AWTListenersNow.length);
	        	
	        	int awtcount = 0;
	        	for (AWTEventListener thisAWT : AWTListenersNow) {
	        		System.out.println("At index " + awtcount + " : " +thisAWT.toString());
	        		awtcount++;
	        	}
	        	
	        	System.out.println();
	        	
	        	System.out.println("adding awtevent listener:");
	        	Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
	                @Override
	                public void eventDispatched(AWTEvent event) {
	                    if (event instanceof KeyEvent) {
	                        KeyEvent ke = (KeyEvent) event;
	                        if (ke.getKeyChar() == 't' && ke.getID() == KeyEvent.KEY_PRESSED) {
	                            System.out.println("Global listener detected 't' key press: " + ke);
	                        }
	                    }
	                }
	            }, AWTEvent.KEY_EVENT_MASK);
	        	
	        	System.out.println("After AWT Listeners");
	        	AWTEventListener[] AWTListenersAfter = Toolkit.getDefaultToolkit().getAWTEventListeners();
	        	System.out.println("No of AWTListeners after addition " + AWTListenersAfter.length);
	        	
	        	awtcount = 0;
	        	for (AWTEventListener thisAWT : AWTListenersAfter) {
	        		System.out.println("At index " + awtcount + " : " +thisAWT.toString());
	        		awtcount++;
	        	}
	        }
	        
	    
	    
	        public static void removeCustomAWTListener() {
	        	
	        	System.out.println("removing custom AWTListener");
	        	
	        	AWTEventListener[] AWTListenersNow = Toolkit.getDefaultToolkit().getAWTEventListeners();
	        	System.out.println("No of AWTListeners currently present " + AWTListenersNow.length);
	        	
	        	int awtcount = 0;
	        	for (AWTEventListener thisAWT : AWTListenersNow) {
	        		System.out.println("At index " + awtcount + " : " +thisAWT.toString());
	        		awtcount++;
	        	}
	        	
	        	if (AWTListenersNow.length > 1 ) {
	        		Toolkit.getDefaultToolkit().removeAWTEventListener(AWTListenersNow[AWTListenersNow.length - 1]);
	        	}
	        	
	        	AWTEventListener[] AWTListenersAfter = Toolkit.getDefaultToolkit().getAWTEventListeners();
	        	System.out.println("No of AWListeners after removal " + AWTListenersAfter.length);
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










