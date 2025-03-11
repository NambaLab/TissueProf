package fi.helsinki;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GenericDialog;

public class Dialogs {
	public static class overlapDialog extends GenericDialog {
		
		Panel panel;
		Button button;
		Button button2;
		Button button3;
		Button button4;
		Button button5;
		Window wind;
		Button button1;
		OverlapRoxx OverlapRoxx;
		JRadioButton firstMode;
		JRadioButton secondMode;
		static Preferences prefs = Preferences.userNodeForPackage(TissueProf.class);
		
				
		String overTitle;
		static Frame theFrame;
		boolean canceled; 
		
		
		
		public overlapDialog(String title, Frame parent) {
			super(title, parent);
			// TODO Auto-generated constructor stub
			//Center buttons and panels properly
			
			this.setSize(500,350);	
			
		overTitle = title;
		theFrame = parent;
		
        String InputDir = prefs.get("InputDir", "");
        
        String imageName = prefs.get("imageName","");
        
        String suf = prefs.get("suffix", "");
        
        
		this.addFileField("Image File", InputDir + "/" + imageName, 25);
		this.addDirectoryField("Output Folder", prefs.get("OutputDir", imageName), 25);
		this.addStringField("File type (suffix)", prefs.get("suffix", ""));	
		
		Panel radioPanel = new Panel();
		
		Border radioBorder = BorderFactory.createLineBorder(Color.BLUE);
		
		firstMode = new JRadioButton("Co-expression Analysis");
		
		secondMode = new JRadioButton("Reanalysis");
		
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(firstMode);
		modeGroup.add(secondMode);
		
		//modeGroup.setSelected(firstMode.getModel(), true);
		
		firstMode.addActionListener(this);
		secondMode.addActionListener(this);
		
		radioPanel.add(firstMode);
		radioPanel.add(secondMode);
		radioPanel.setPreferredSize(new Dimension(360,24));
		radioPanel.setSize(new Dimension(360,24));
		
		this.addPanel(radioPanel,GridBagConstraints.EAST, new Insets(0,0,0,-183));
				
		this.addCheckbox("Channel 1", prefs.getBoolean("Channel1Selection", false));
		this.addToSameRow();
		
		this.addStringField("Channel 1 Name", prefs.get("Channel1Name", ""),4);
		this.addCheckbox("Channel 2", prefs.getBoolean("Channel2Selection", false));
		this.addToSameRow();
		this.addStringField("Channel 2 Name", prefs.get("Channel2Name", ""),4);
		this.addCheckbox("Channel 3", prefs.getBoolean("Channel3Selection", false));
		this.addToSameRow();
		this.addStringField("Channel 3 Name", prefs.get("Channel3Name", ""),4);
		this.addCheckbox("Channel 4", prefs.getBoolean("Channel4Selection", false));
		this.addToSameRow();
		this.addStringField("Channel 4 Name", prefs.get("Channel4Name", ""),4);
		
		this.addNumericField("Overlap Threshold", prefs.getDouble("overlapThreshold", 0.70), 3);
		this.addToSameRow();
		this.addNumericField("Enhance Contrast by (%)", prefs.getDouble("enhanceContrast", 0.35), 3);
		
		//Model Parameters
		this.addCheckbox("Make intensity measurements? ", true);
		
		//this.pack();
		
		//this.centerDialog(true);
		
		this.setResizable(false);
		//centerDialogOnMainScreen(this);
		setOverlapLocation(this);
		
		addWindowListener(this);
		this.setVisible(true);	
		
		}
		
		
		public void addModelParameters() {
			
			Component[] compsNow = this.getComponents();
			
			for (int i = 24 ; i < compsNow.length ; i++) {
				//System.out.println("removing... component with index " + i);
				this.remove(compsNow[i]);
			}
			
			Panel textPanel = new Panel();
			textPanel.setLayout(new BorderLayout());
			JTextField thisField = new JTextField();
			thisField.setEditable(false);
			thisField.setForeground(Color.black);
			thisField.setBackground(new Color(237,237,237));
			//Border emptyBorder = BorderFactory.createEmptyBorder();
	        	
			Border emptyBorder = BorderFactory.createEmptyBorder();
			thisField.setBorder(emptyBorder);
			thisField.setSize(new Dimension(360,25));
			
			textPanel.add(thisField, BorderLayout.EAST);
			
			Font thisFont = new Font(Font.DIALOG, Font.BOLD, 12);
			
			thisField.setFont(thisFont);
			thisField.setText("Model Parameters");
			textPanel.setSize(360,25);
			
			//this.addPanel(textPanel, GridBagConstraints.EAST, new Insets(0,0,0,-55));
			
			this.addPanel(textPanel, GridBagConstraints.EAST, new Insets(5,5,5,-60));
			
			this.addCheckbox("Use Own Models?", prefs.getBoolean("useModel", false));
			this.addToSameRow();
			this.addNumericField("Number of Tiles", prefs.getInt("nTiles", 8),0);
			
			this.addFileField("Model 1 Path", prefs.get("Model 1 Path", "Model 1 Path"), 20);
			this.addNumericField("Probability Threshold", prefs.getDouble("probs1", 0.50));
			this.addToSameRow();
			this.addNumericField("NMS Threshold", prefs.getDouble("nms1", 0.30),3);
			this.addCheckbox("Delete smaller cells?", prefs.getBoolean("deleteRoi1", false));
			this.addToSameRow();
			this.addNumericField("Delete smaller than (µm²) ->", prefs.getDouble("deleteArea1", 25),6);
			
			this.addFileField("Model 2 Path", prefs.get("Model 2 Path", "Model 2 Path"), 20);
			this.addNumericField("Probability Threshold ", prefs.getDouble("probs2", 0.50));
			this.addToSameRow();
			this.addNumericField("NMS Threshold", prefs.getDouble("nms2", 0.30),6);
			this.addCheckbox("Delete smaller cells?", prefs.getBoolean("deleteRoi2", false));
			this.addToSameRow();
			this.addNumericField("Delete smaller than (µm²) ->", prefs.getDouble("deleteArea2", 25),6);
			
			this.addFileField("Model 3 Path", prefs.get("Model 3 Path", "Model 3 Path"), 20);
			this.addNumericField("Probability Threshold ", prefs.getDouble("probs3", 0.50));
			this.addToSameRow();
			this.addNumericField("NMS Threshold", prefs.getDouble("nms3", 0.30),6);
			this.addCheckbox("Delete smaller cells?", prefs.getBoolean("deleteRoi3", false));
			this.addToSameRow();
			this.addNumericField("Delete smaller than (µm²) ->", prefs.getDouble("deleteArea3", 25),6);
			
			
			this.addFileField("Model 4 Path", prefs.get("Model 4 Path", "Model 4 Path"), 20);
			this.addNumericField("Probability Threshold ", prefs.getDouble("probs4", 0.50));
			this.addToSameRow();
			this.addNumericField("NMS Threshold", prefs.getDouble("nms4", 0.30),6);
			this.addCheckbox("Delete smaller cells?", prefs.getBoolean("deleteRoi4", false));
			this.addToSameRow();
			this.addNumericField("Delete smaller than (µm²) ->", prefs.getDouble("deleteArea4", 25),6);
			
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
			this.addPanel(panel, GridBagConstraints.EAST, new Insets(5,5,5,-58));
			
					
			//this.setSize(476,710);
			this.setSize(500,750);
			addWindowListener(this);
		}
		
		
		public void addReanalysis() {
			
			Component[] compsNow = this.getComponents();

			for (int i = 24 ; i < compsNow.length ; i++) {
				//System.out.println("removing... component with index " + i);
				this.remove(compsNow[i]);
			}
			
			Panel textPanel2 = new Panel();
			textPanel2.setSize(new Dimension(460,25));
			textPanel2.setLayout(new BorderLayout());
			JTextField thisField2 = new JTextField();
			thisField2.setEditable(false);

//			/thisField2.setBackground(new Color(237,237,237));
			//thisField2.setBackground(new Color(0,0,0));
			
			textPanel2.setForeground(Color.black);
			//textPanel2.
			//Border emptyBorder = BorderFactory.createEmptyBorder();
	        	
			Border emptyBorder = BorderFactory.createEmptyBorder();
			thisField2.setBorder(emptyBorder);
			
			Font thisFont = new Font(Font.DIALOG, Font.BOLD, 12);
			
			thisField2.setFont(thisFont);
			thisField2.setSize(500, 25);	
			thisField2.setForeground(Color.black);
			
			thisField2.setText("ROIs to Load");
			
			textPanel2.add(thisField2, BorderLayout.CENTER);
			textPanel2.setSize(new Dimension(500,25));
			textPanel2.setName("Roi message panel");
			
			this.addPanel(textPanel2, GridBagConstraints.EAST, new Insets(5,5,5,-40));

			this.addFileField("ROIs for Ch1", prefs.get("Ch1Rois", ""),25);
			this.addFileField("ROIs for Ch2", prefs.get("Ch2Rois", ""),25);
			this.addFileField("ROIs for Ch3", prefs.get("Ch3Rois", ""),25);
			this.addFileField("ROIs for Ch4", prefs.get("Ch4Rois", ""),25);
			
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
			panel.setSize(500, 25);
			panel.setName("OK Cancel panel");
			//this.addPanel(panel, GridBagConstraints.EAST, new Insets(0,0,0,-37));
			this.addPanel(panel, GridBagConstraints.EAST, new Insets(0,0,0,-58));
			this.setSize(500,550);
			this.doLayout();
			addWindowListener(this);
			
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e){
			if (e.getSource().equals(button)) {
				//System.out.println("button OK pressed ");
				this.dispose();
			}
			else if (e.getSource().equals(button3)){
				//System.out.println("This thread is making the actionListener " + Thread.currentThread().getName());
				//System.out.println("button 3 pressed ");
				TissueProf.getThreadtoStop();
				WindowManager.closeAllWindows();
				this.dispose();
			
			}
			else if (e.getSource().equals(firstMode)) {
				//this.setVisible(false);
				//System.out.println("comp count after pressing overlap analysis " + this.getComponentCount());
				//firstMode.setSelected(true);
				this.addModelParameters();
				this.setVisible(true);
			}
			else if (e.getSource().equals(secondMode)){
				//System.out.println("Comp count after pressing Reanalysis " + this.getComponentCount());
				//this.setVisible(false);
				//secondMode.setSelected(true);
				this.addReanalysis();
				this.setVisible(true);
			}
		}
		
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				//System.out.println("window closed");
				this.dispose();
				TissueProf.getThreadtoStop();
				WindowManager.closeAllWindows();
			}
		}
	}
	
	    
	    
	    
    public static class zoneNoDialog extends GenericDialog {
    	
    	Button button;
    	Button button4;
    	
    	public zoneNoDialog(String title) {
    		super(title);
    		// TODO Auto-generated constructor stub
    		//thisDialog = new GenericDialog("Number of Zones");
    		
    		this.addNumericField("Enter number of zones you would like to analyze", 1);
    		//this.setSize(400,200);
    		//this.setBounds(800, 400, 400, 150);
    		button4 = new Button("OK");
    		button4.addActionListener(this);
    		this.add(button4);
    		this.setLayout(new FlowLayout(1,2,2));
    		this.centerDialog(true);
    		
    		this.pack();
    		
    		//thisDialog.setLocation(800,400);
    		centerDialogOnMainScreen(this);
    		//this.setVisible(true);
    		this.addWindowListener(this);
    		
    		
    	}
    	
    	//@Override
    	public void actionPerformed(ActionEvent e){
    		if (e.getSource().equals(button4)) {
    			this.dispose();
    		}	
    	}
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				TissueProf.getThreadtoStop();
				WindowManager.closeAllWindows();
				this.dispose();
			
			}
		}
    }
    
    
    public static class zoneNameDialog extends GenericDialog {
    	
    	Button button5;
    	
    	public zoneNameDialog(String title, int noZones) {
    		super(title);
    		// TODO Auto-generated constructor stub
    		
    		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    		
    		this.centerDialog(true);
    		for (int i = 0 ; i < noZones ; i++) {
    			int a = i + 1;
    			this.addStringField("Zone " + a + " Name", "A", 15);
    		}
    		
    		Panel buttonPanel = new Panel();
    		buttonPanel.setSize(new Dimension(50, 20));
    		
    		button5 = new Button("OK");
    		button5.addActionListener(this);
    		//this.add(button5);
    		
    		buttonPanel.add(button5);
    		this.add(buttonPanel);
    		
    		this.centerDialog(true);
    		//this.setBounds(800,400,200,noZones*135);
    		this.pack();
    		
    		centerDialogOnMainScreen(this);
    		//this.setVisible(true);
    	
    	}
    	//@Override
    	public void actionPerformed(ActionEvent e){
    		if (e.getSource().equals(button5)) {
    			this.dispose();
    		}	
    	}
		@Override 
		public void windowClosing(WindowEvent e) {
			if (e.getSource().equals(this)) {
				this.dispose();
				TissueProf.getThreadtoStop();
				WindowManager.closeAllWindows();				
			}
		}
    }
    	
	
    public static class ProblemAreaDialog extends GenericDialog {    	
    	
		Button button6;
		Button button7;
		JPanel panel1;
		JPanel panel2; 
		String Title;
		String OutputDir; 
		String ZoneName;
		OverlapRoxx OverlapRoxx;
		String ImageName; 
		LinkedHashMap<Rox, RoiData> RoxDataMap;
		double ratio;
		
    	public ProblemAreaDialog(String Title, OverlapRoxx OverlapRoxx, String OutputDir, String ZoneName, String ImageName, 
    			LinkedHashMap<Rox, RoiData> RoxDataMap, double ratio) {
    		super(Title);
    		this.OverlapRoxx = OverlapRoxx;
    		this.Title = Title;
    		this.OutputDir = OutputDir;
    		this.ZoneName = ZoneName;
    		this.ImageName = ImageName;
    		this.RoxDataMap = RoxDataMap;
    		this.ratio = ratio;
    		
    		this.setSize(450,135);
    		//this.setLayout(new FlowLayout(1,2,2));
    		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    		panel1 = new JPanel();
    		JTextField thisText = new JTextField();
    		
    		thisText.setText("Look for possible problems with cells in the " + ZoneName + " ?");
    		
    		thisText.setEditable(false);
    		thisText.setForeground(Color.black);
    		thisText.setBackground(new Color(237,237,237));
    		//Border emptyBorder = BorderFactory.createEmptyBorder();
            	
    		Border emptyBorder = BorderFactory.createEmptyBorder();
    		thisText.setBorder(emptyBorder);
    		
    		//panel1.add(thisText, BorderLayout.CENTER);
    		panel1.add(thisText, BorderLayout.CENTER);
    		
    		this.add(panel1);
    		
    		button6 = new Button("Yes");
    		button6.addActionListener(this);
    		button7 = new Button("No");
    		button7.addActionListener(this);
    		
    		panel2 = new JPanel();
    		
    		panel2.add(button6, BorderLayout.CENTER);
    		panel2.add(button7, BorderLayout.CENTER);
    		
    		this.add(panel2);
    		
    		centerDialogOnMainScreen(this);
    		
    	}
    	
		@Override
		public void actionPerformed(ActionEvent e){
			if (e.getSource().equals(button6)) {
				this.dispose();
				System.out.println("Detecting problematic areas...");
				IJ.log("Detecting problematic areas...");
				
				ProblemAreas thisProblem = new ProblemAreas();
				thisProblem.detectProblemAreas(this.OverlapRoxx, this.OutputDir, this.ZoneName, this.ImageName, this.RoxDataMap, this.ratio);
				
			}
			else if (e.getSource().equals(button7)){
				//System.exit(ABORT);
				this.dispose();
			}	
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
    
    public static void setOverlapLocation (GenericDialog Frame) {
    	
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
    	Frame.setLocation(centerX, centerY-150);
    	
    	
    }
    
    
    
}

	    
	    
	    
	
