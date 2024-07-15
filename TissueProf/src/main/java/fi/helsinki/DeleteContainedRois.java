package fi.helsinki;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;

@Plugin(type = Command.class, label = "Command From Macro", menuPath = "Plugins>TissueProf>Tools>DeleteContainedROIs")
public class DeleteContainedRois implements Command {

	static boolean delcanceled = false;
	static boolean delfinished = false;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (delcanceled == false) {
			
			try {
				
				RoiManager.getRoiManager();
				
				boolean RoiAdded = false;
				
				if (RoiManager.getInstance().getCount()>0) {
					RoiAdded = true;
					//System.out.println("Roi count > 0 ");
				}
			
				IJ.run("Set Measurements...", "centroid redirect=None decimal=1");
				
				System.out.println("Roi added? " + RoiAdded);
				
				while (RoiAdded == false && delcanceled == false) {
					
					int rcount = RoiManager.getInstance().getCount();
					if (rcount==0) {
					System.out.println("Count 0");
						WaitForUserDialog addRois = new WaitForUserDialog("addRois");
						addRois.addWindowListener(new WindowListener() {
							@Override
							public void windowOpened(WindowEvent e) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void windowClosing(WindowEvent e) {
								delcanceled = true;
								addRois.dispose();
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
							
						});
						addRois.show();
		
						} else if (rcount>0) {
							System.out.println("Count >0");
					}
				}
					
				if (delcanceled == true) {
					//thisCommand.cancel(true);
					Thread.currentThread().interrupt();
					
					Thread.currentThread().sleep(2);
					
				}
				
				RoiManager.getInstance().runCommand(IJ.getImage(),"Measure");
				ResultsTable thisTable = ResultsTable.getResultsTable();
				
				float[] allXmeasurements;
				float[] allYmeasurements;
				
				float[] allX = null;
				float[] allY = null;
				
				ArrayList<Integer> toDelete = new ArrayList<Integer>();
			
				System.out.println("RoiAdded? " + RoiAdded);

				toDelete = new ArrayList<Integer>(); 
	
				allXmeasurements = thisTable.getColumn(6);
				allYmeasurements = thisTable.getColumn(7);
				
				allX = new float[allXmeasurements.length];
				allY = new float[allYmeasurements.length];
				
				for (int i = 0 ; i < allXmeasurements.length ; i++) {
				
					allX[i] = (float) (allXmeasurements[i]/IJ.getImage().getCalibration().pixelWidth);
					allY[i] = (float) (allYmeasurements[i]/IJ.getImage().getCalibration().pixelHeight);
				}
				
				
					
					
				WaitForUserDialog drawRoi = new WaitForUserDialog("Draw and add a ROI that contains the ROIs that you wish to delete and press OK");
				drawRoi.addWindowListener(new WindowListener() {
					@Override
					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowClosing(WindowEvent e) {
						delcanceled = true;
						drawRoi.dispose();
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
				});
				
				drawRoi.show();
				
				if (delcanceled == true) {
					//thisCommand.cancel(true);
					Thread.currentThread().interrupt();
					
					Thread.currentThread().sleep(2);
					
				}
				
				int[] lastIndex = {RoiManager.getInstance().getCount()-1};
				
				RoiManager.getInstance().setSelectedIndexes(lastIndex);  
				
				Roi[] Rois = RoiManager.getInstance().getSelectedRoisAsArray();
				
				Roi Contour = Rois[0];
				
				RoiManager.getInstance().runCommand(IJ.getImage(),"Delete");
				
				//RoiManager.getInstance().setSelectedIndexes(lastIndex);
				
				
				
				for (int i = 0 ; i < allX.length ; i++) {
					if (Contour.containsPoint(allX[i], allY[i])){
						toDelete.add(i);
					}
				}
				
				Integer[] toDeleteIndexes = toDelete.toArray(new Integer[toDelete.size()]);
				
				int[] toDeleteints = new int[toDeleteIndexes.length];
				
				for (int i = 0 ; i < toDeleteIndexes.length ; i++) {
					toDeleteints[i] = toDeleteIndexes[i];
				}
				
				
				RoiManager.getInstance().setSelectedIndexes(toDeleteints);
				
				RoiManager.getInstance().runCommand(IJ.getImage(),"Delete");
				
				
			} catch (InterruptedException e) {
				
				System.out.println("Canceled");
				delfinished = true;
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			
		}
		
		delcanceled  = false;
		delfinished = false;

	}
	
	
	
}