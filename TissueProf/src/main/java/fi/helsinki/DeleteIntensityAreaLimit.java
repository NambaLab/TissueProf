package fi.helsinki;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.util.Prefs;

import fi.helsinki.DeleteIntensityAreaLimit.deleteFrame;
import fi.helsinki.ModifyRois.roiFrame2;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;


@Plugin(type = Command.class, label = "Command From Macro", menuPath = "Plugins>TissueProf>Tools>DeleteAreaIntensity")
public class DeleteIntensityAreaLimit implements Command {
	
	static double MinInt;
	static double MinArea;
	static volatile boolean incanceled = false;
	static boolean infinished = false; 
	static deleteFrame thisFrame;
	static Preferences prefsin = Preferences.userRoot().node(DeleteIntensityAreaLimit.class.getName());
    static int frames;
	
	@Override
	public void run() {
		
		while (incanceled == false) {
			try {
				
				Thread thisdeleteThread = new deleteThread();
				
				FutureTask<Boolean> futureTask = new FutureTask<Boolean>((Callable<Boolean>) thisdeleteThread);
				
				ExecutorService executor = Executors.newSingleThreadExecutor();
				
				executor.submit(futureTask);
				
				@SuppressWarnings("unused")
				Boolean deleteResult;
				
		        try {
		            deleteResult = futureTask.get() != null; 
		        } catch (InterruptedException | ExecutionException | NullPointerException e) {
					incanceled = true;
					e.printStackTrace();
		        	
		        } finally {
			        if (incanceled == true) {
			        	Thread.currentThread().interrupt();
			        	Thread.currentThread().sleep(1);
			        	//incanceled = true;
			        }
			        executor.shutdown();
		        }  

				//System.out.println("canceled here " + incanceled);
				
				//deleteRois(MinInt, MinArea);
			} catch (InterruptedException e) {
				
				incanceled = true;
				
				//System.out.println("Canceled1");
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println("canc " + incanceled);
		
		synchronized(DeleteIntensityAreaLimit.class) {
			incanceled = false;
			infinished = false;
		}
		//System.out.println("cancafter " + incanceled)
	}
	
	public static void deleteAreaIntensity(deleteFrame deleteFrame) {
		
		double[] intarea = getAreaIntensityLimit(deleteFrame);
		
		deleteRois(intarea[0], intarea[1]);
		
	}
	
	public static double[] getAreaIntensityLimit(deleteFrame deleteFrame) {
		
		MinInt = deleteFrame.getNextNumber();
		
		prefsin.putDouble("deleteInt", MinInt);
		
		MinArea = deleteFrame.getNextNumber();
		
		prefsin.putDouble("deleteArea", MinArea);
		
		double[] intarea = {(int) MinInt, (int) MinArea};
		
		return intarea;

	}
	
	
	public DeleteIntensityAreaLimit(){
		
	}
	
	public static void deleteRois (double MinInt , double MinArea){
		
		System.out.println("Minint " + MinInt + "MinArea " + MinArea);

		IJ.run("Set Measurements...", "area mean redirect=None decimal=1");
		
		RoiManager.getRoiManager();
		
		RoiManager.getInstance().setPosition(0);
	
		IJ.run("Clear Results", "" );
		RoiManager.getInstance().runCommand(IJ.getImage(),"Measure");
		
		ResultsTable thisTable = ResultsTable.getResultsTable();
		
		ArrayList<Integer> toDelete = new ArrayList<Integer>(); 
		
		float[] areas = thisTable.getColumn(0);
		
		float[] means = thisTable.getColumn(1);
		
		for (int i = 0 ; i < means.length ; i++) {
			if (means[i]<MinInt || areas[i]<MinArea) {
				toDelete.add(i);
			}
		} 
		
		if (toDelete.size()>0) {
		
		Integer[] toDeleteIndexes = toDelete.toArray(new Integer[toDelete.size()]);
		
		int[] toDeleteIndexints = new int[toDeleteIndexes.length];
		
		for (int i = 0 ; i < toDeleteIndexes.length ; i ++) {
			toDeleteIndexints[i] = toDeleteIndexes[i];
		}
		
		RoiManager.getInstance().setSelectedIndexes(toDeleteIndexints);
		System.out.println("Now selected befpre Delete!" + RoiManager.getInstance().getSelectedIndexes().length);
		RoiManager.getInstance().runCommand(IJ.getImage(),"Delete");
		}
	}
	
	
	public static class deleteFrame extends GenericDialog {
		
		Button button4;
		Button button5;
		private boolean alreadycanceled = false;
		
		
 		public deleteFrame(String name, Frame parent, int X, int Y){
			super(name, parent);
			
			this.addNumericField("Min Intensity", prefsin.getDouble("deleteInt", 0));
			
			this.addNumericField("Min Area" , prefsin.getDouble("deleteArea", 0));
			
			
			
			Panel panel = new Panel();
			
			button4 = new Button();
			button4.setLabel("OK");
			button4.setFocusable(true);
			panel.add(button4);
			button4.addActionListener(this);

			button5 = new Button();
			button5.setLabel("Cancel");
			button5.addActionListener(this);
			panel.add(button5);	
			
			panel.setPreferredSize(new Dimension(150,50));
			
			panel.setSize(new Dimension(150, 50));			
			this.addPanel(panel, GridBagConstraints.EAST, new Insets(5,5,5,-20));
			
			this.pack();
			if (frames ==0) {
				centerDialogOnMainScreen(this);
			}
			else if (frames>0){
				this.setLocation(X,Y);
			}
			this.setModalityType(Dialog.ModalityType.MODELESS);
		
			this.setVisible(true);
			
			addWindowListener(this);
		}
		
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e){
			if (e.getSource().equals(button4)) {
				deleteAreaIntensity(thisFrame);
				frames++;
                
				Point lastPoint = this.getLocationOnScreen();

				this.dispose();

				thisFrame = new deleteFrame("DeleteAreaIntensity", new Frame(), lastPoint.x, lastPoint.y);

			}
			else if (e.getSource().equals(button5)) {
				synchronized(DeleteIntensityAreaLimit.class) {incanceled = true;}
				alreadycanceled = true;
				this.dispose();
			}
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			if (!alreadycanceled) {
				alreadycanceled = true;	
				this.actionPerformed(new ActionEvent(button5, ActionEvent.ACTION_PERFORMED, "Cancel"));
			}

		}

		
		
		
		
		public static void centerDialogOnMainScreen(GenericDialog Frame) {
		
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			GraphicsDevice[] screens = ge.getScreenDevices();
			GraphicsDevice primaryScreen = screens[0]; // Assuming the first screen is the primary/main screen
			
			Dimension screenSize = primaryScreen.getDefaultConfiguration().getBounds().getSize();
		
			int centerX = (screenSize.width - Frame.getWidth()) / 2;
			int centerY = (screenSize.height - Frame.getHeight()) / 2;
			
			Frame.setLocation(centerX, centerY);
		}

	}
	
	class deleteThread extends Thread implements Callable {
		
		Boolean finish;
		
	    public deleteThread() {

	    	this.finish = infinished;

	    }
		
		
	    @Override
	    public Boolean call() throws Exception {
	    	
			thisFrame = new deleteFrame("DeleteAreaIntensity", new Frame(), 0,0);

			//saved = false; 
			
			int c = 0;
			while (incanceled == false ) {
				if(c==30) {
					//System.out.println("canceled ? " + this.finish);
				}
				c++;	
			}		
			return incanceled;
	    }
	      
	}
	

}


	

