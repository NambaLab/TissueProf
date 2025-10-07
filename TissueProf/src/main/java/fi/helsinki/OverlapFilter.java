package fi.helsinki;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fi.helsinki.DetectOverlap.ComboInterComposite;
import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.plugin.frame.RoiManager;

//TODO
//Multithread individual Combos and Channels and ROI groups of n inside channels.
//Decide on how to determine n, maximum number of threads, stack size and how much RAM to use
//Determine the right execution policy 
//Multithread!

public class OverlapFilter {
	
	private ArrayList<ComboRoxx> ComboRoxx;
	private Calendar date = Calendar.getInstance();
	
	OverlapFilter(Rox[][] allRox, DetectOverlap NewOverlap, Boolean[] channelSelection, int channelSize){
		//TODO 
		//Deal with static access warnings
		overlapFilter(allRox, NewOverlap, channelSelection, channelSize);
		
	}
	
	public /*static*/ void overlapFilter(Rox[][] allRox, DetectOverlap NewOverlap, Boolean[] channelSelection, int channelSize) {
		
		IJ.log("Filtering overlaps...");
		System.out.println("Filtering started : " + date.getTime().toString());
		IJ.log("Filtering started : " + date.getTime().toString());
		
		
		//Create ArrayLists to fill with filtered ROIs.
		ArrayList<ArrayList<ComboInterComposite>> InterComboComposites = NewOverlap.getDetectResults();
		ArrayList<ComboRoxx> ComboRoxxes = null;
		
		int c = 0 ;
		for (Rox [] theseRox : allRox) {
			if (channelSelection[c] == true) {
				for (ArrayList<ComboInterComposite> theseComposite : InterComboComposites) {					
					for (ComboInterComposite thisComposite : theseComposite) {
						//Add condition to make sure only relevant channels of the combo get processed
						System.out.println("Channel : " + c);
						System.out.print("Now doing this combo filtering : ");
						thisComposite.getComboIndexes().forEach(n->System.out.print(n + " "));
						System.out.print("\n");
						
						if (!thisComposite.getComboIndexes().contains(c)) {
								continue;
						}
						int f=0;
						ArrayList<Rox> thisFilteredRoxx = null;
						ComboRoxx thisComboRoxx = null; 
						ShapeRoi thisCompositeShape = thisComposite.getInterShape();
						ShapeRoi interShape;
						for (Rox thisRox : theseRox) {
							//ArrayList<ShapeRoi> InterParticipants = new ArrayList<ShapeRoi>();
							//InterParticipants.add(thisRox.shape);
							//InterParticipants.add(thisComposite.getInterShape());
							//ShapeRoi interShape = DetectOverlap.FindIntersection(InterParticipants);
							interShape = null;
							
							thisRox.setShape();
							
							/*
							System.out.println("Processing ROI shape. \n Dimensions: " + thisRox.getShape().getBounds());
							System.out.println("ROI Dimenstions: " + thisRox.getRoi().getBounds());
							RoiManager.getInstance().reset();
							//RoiManager.getInstance().addRoi(thisRox.getShape().getRois()[0]);
							RoiManager.getInstance().addRoi(thisRox.getShape());
							RoiManager.getInstance().addRoi(thisRox.getRoi());
							RoiManager.getInstance().addRoi(thisCompositeShape);
							*/
							interShape = ((ShapeRoi) thisRox.getShape().clone()).and(thisCompositeShape);
							
							
							//RoiManager.getInstance().addRoi(thisComposite.getInterShape().shapeToRoi());
							//RoiManager.getInstance().addRoi(interShape);
							
							//WaitForUserDialog seeRoxShapes = new WaitForUserDialog("See roi, shape, composite and intershape from filter");
							//seeRoxShapes.show();
							
							if (interShape!=null && (interShape.getBounds().getHeight()>0 || interShape.getBounds().getWidth()>0)) {
								/*
								RoiManager.getInstance().reset();
								RoiManager.getInstance().addRoi(thisRox.getRoi());
								RoiManager.getInstance().addRoi(thisRox.getShape());
								RoiManager.getInstance().addRoi(thisComposite.getInterShape().shapeToRoi());
								RoiManager.getInstance().addRoi(thisCompositeShape);
								RoiManager.getInstance().addRoi(interShape.shapeToRoi());
								WaitForUserDialog seeFilterRoi = new WaitForUserDialog("See roi, composite and intershape");
								seeFilterRoi.show();
								*/
								
								f++;
								if (f ==1 ) {
									thisFilteredRoxx = new ArrayList<Rox>();
									thisComboRoxx = new ComboRoxx(thisFilteredRoxx, c, thisComposite.getComboSize(), thisComposite.getComboIndexes());
									//System.out.print("f " + f + " foundFilteredRox  for combo: " );
									//thisComposite.getComboIndexes().forEach(n->System.out.print(n + " "));
									//System.out.print("\n");
								}
								//Roi interRoi = interShape.shapeToRoi();
								//RoiData interRoiData = new RoiData(IJ.getImage(), interRoi);
								//double interRoiArea = interRoiData.setArea(IJ.getImage());
								//thisRox.setInterArea(thisComposite.getComboIndexes(), interRoiArea);
								
								thisComboRoxx.addRox(thisRox);
								
								//release memory
								//interRoi = null;
								//interRoiData.clear();
								//interRoiData = null;
								//System.out.print("f " + f + " thisComboRoxx.Filtered.size " + thisComboRoxx.getFilteredRoxx().size());
								//System.out.print("  combo: ");
								//thisComposite.getComboIndexes().forEach(n->System.out.println(n + " "));
								//System.out.print("\n");
							}
							//Release shapeRoi memory
							//InterParticipants.removeAll(InterParticipants);
							//InterParticipants.clear();
							//interShape = null;
							
						}
						
						if (f>=1) {
							//System.out.println("f " + f + " Now adding comboroxx to comboroxxes");
							if (ComboRoxxes==null) {
								ComboRoxxes = new ArrayList<ComboRoxx>();
							}
							
							Collections.sort(thisComboRoxx.getFilteredRoxx(), Comparator.comparingDouble(r -> r.getPosition()[1]));
							ComboRoxxes.add(thisComboRoxx);
							//Clear local variable
							thisComboRoxx = null;
							//System.out.println("f " + f + "Comboroxxeslength after adding " + ComboRoxxes.size());
						}
						
						thisCompositeShape = null;
						
					}
				
				}
				
			}
			c++;
		}
		
		
		System.out.println("Finished combofiltering");
		//Add the single positive Roxx
		
		for (int i = 0 ; i < allRox.length ; i++) {
			if (channelSelection[i] == true) {
				List<Integer> thisChannel = new ArrayList<Integer>();
				thisChannel.add(i);
				ArrayList<Rox> thisChannelRoxx = new ArrayList<Rox>();
				for (Rox rox:allRox[i]) {
					thisChannelRoxx.add(rox);
				}
				ComboRoxx SingleRoxx = new ComboRoxx(thisChannelRoxx, i, 1, thisChannel);
				ComboRoxxes.add(SingleRoxx);
				//release memory
				SingleRoxx = null;
				thisChannelRoxx = null;
			}
		}
		
		System.out.println("SingleRoxx added");
		
		
		/*
		for (ComboRoxx thisComboRoxx : ComboRoxxes) {
			int d = 0; 	
			for (Rox thisRox : thisComboRoxx.FilteredRoxx) {
			
				String concatenated = thisComboRoxx.getComboIndexes().get(0).toString();
				
				
				int con = 0;
				for (Integer thisInt : thisComboRoxx.getComboIndexes()) {
					if (con == 0 ) {con++;continue;}
					concatenated = String.join("", concatenated, thisInt.toString());
				}
				
				
				thisRox.getRoi().setName(thisComboRoxx.getComboSize() + "combo-" + concatenated + "_" + d);
				d++;
			}
			
			if (RoiManager.getInstance()==null) {
				RoiManager.getRoiManager();
			}
			
			RoiManager.getInstance().reset();
			
			//Visualise the Roxx
			addRoxxToRoiManager((ArrayList<Rox>)thisComboRoxx.FilteredRoxx);
			
			WaitForUserDialog seeFiltered = new WaitForUserDialog("See FilteredRoxx in OverlapFilter");
			seeFiltered.show();
		}
		*/	
		//set the Roxx
		
		setRoxx(ComboRoxxes);
		
		ComboRoxxes = null;
				
		date = Calendar.getInstance();
		System.out.println("Filtering finished : " + date.getTime().toString());
		IJ.log("Filtering finished : " + date.getTime().toString());
		
	}
	
	
	
	
	public class ComboRoxx {
		
		ArrayList<Rox> FilteredRoxx;
		int Channel; 
		int ComboSize;
		List<Integer> ChIndexes;
		
		
		ComboRoxx(ArrayList<Rox> comboRoxx, int Channel, int ComboSize, List<Integer> ChIndexes){
			this.setFilteredRoxx(comboRoxx);
			this.setChannel(Channel);
			this.setComboSize(ComboSize);
			this.setChIndexes(ChIndexes);
		}
		
		private void setFilteredRoxx(ArrayList<Rox> FilteredRoxx) {
			this.FilteredRoxx = FilteredRoxx;
		}
		private void setChannel(int Channel) {
			this.Channel = Channel;
		}
		private void setComboSize(int ComboSize) {
			this.ComboSize = ComboSize;
		}
		private void setChIndexes(List<Integer> ChIndexes) {
			this.ChIndexes = ChIndexes;
		}
		private void addRox(Rox rox) {
			this.FilteredRoxx.add(rox);
		}
		
		public List<Integer> getComboIndexes(){
			return ChIndexes;
		}
		
		public int getComboSize() {
			return ComboSize;
		}
		
		public int getChannel() {
			return this.Channel;
		}
		
		public ArrayList<Rox> getFilteredRoxx(){
			return FilteredRoxx;
		}
		public void clear() {
			this.FilteredRoxx.removeAll(FilteredRoxx);
			this.FilteredRoxx.clear();
			this.Channel = 0;
			this.ChIndexes.removeAll(ChIndexes);
			this.ChIndexes.clear();
			
		}
		
	}
	
	
	public static void addRoxxToRoiManager(ArrayList<Rox> Roxx) {
		
		if (RoiManager.getInstance()==null) {
			RoiManager.getRoiManager();
		}
		
		for (Rox rox : Roxx) {
			RoiManager.getInstance().addRoi(rox.getRoi());
		}
		
	}
	
	private void setRoxx(ArrayList<ComboRoxx> ComboRoxxes) {
		this.ComboRoxx = ComboRoxxes;
	}
	
	public ArrayList<ComboRoxx> getRoxx() {
		return ComboRoxx;
	}
	
	
	
	/*
	public static void clear() {
		
		if(QuadRoxx!= null) {		
			QuadRoxx.removeAll(QuadRoxx);
			QuadRoxx.clear();
			QuadRoxx= new ArrayList<ArrayList<Rox>>();
		}
		if (TripleRoxx!= null) {
			TripleRoxx.removeAll(TripleRoxx);
			TripleRoxx.clear();
			TripleRoxx= new ArrayList<ArrayList<ArrayList<Rox>>>();
		}
		if(DoubleRoxx!=null){
			DoubleRoxx.removeAll(DoubleRoxx);
			DoubleRoxx.clear();
			DoubleRoxx= new ArrayList<ArrayList<ArrayList<Rox>>>();
		}
		if(SingleRoxx!=null) {
			SingleRoxx.removeAll(SingleRoxx);
			SingleRoxx.clear();
			SingleRoxx= new ArrayList<ArrayList<Rox>>();
		}
	
	}
	*/
	
	
}
	

