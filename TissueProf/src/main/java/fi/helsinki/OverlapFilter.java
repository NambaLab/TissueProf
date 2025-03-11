package fi.helsinki;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.frame.RoiManager;

public class OverlapFilter {
	static ArrayList<ArrayList<Rox>> QuadRoxx;
	static ArrayList<ArrayList<ArrayList<Rox>>> TripleRoxx;
	static ArrayList<ArrayList<ArrayList<Rox>>> DoubleRoxx;
	static ArrayList<ArrayList<Rox>> SingleRoxx;
	
	OverlapFilter(ArrayList<ArrayList<Rox>> QuadRoxx, ArrayList<ArrayList<ArrayList<Rox>>> TripleRoxx, 
			ArrayList<ArrayList<ArrayList<Rox>>> DoubleRoxx, ArrayList<ArrayList<Rox>> SingleRoxx){
		//TODO 
		//Deal with static access warnings
		this.QuadRoxx = QuadRoxx;
		this.TripleRoxx = TripleRoxx;
		this.DoubleRoxx = DoubleRoxx;
		this.SingleRoxx = SingleRoxx;
	}
	
	public static OverlapFilter overlapFilter(Rox[][] allRox, DetectOverlap NewOverlap, Boolean[] channelSelection, int channelSize) {
		
		IJ.log("Filtering overlaps...");
		
		//Create ArrayLists to fill with filtered ROIs.
		
		QuadRoxx = new ArrayList<ArrayList<Rox>>();
		for (int i = 0 ; i < 4 ; i++) {
		ArrayList<Rox> theseRoxx = new ArrayList<Rox>();
		QuadRoxx.add(theseRoxx);
		}
		
		TripleRoxx = new ArrayList<ArrayList<ArrayList<Rox>>>();
		for (int i = 0 ; i < 4 ; i++) {
			ArrayList<ArrayList<Rox>> thisOne = new ArrayList<ArrayList<Rox>>();
			TripleRoxx.add(thisOne);
			for (int j = 0 ; j < 4 ; j++) {
				ArrayList<Rox> theseRoxx = new ArrayList<Rox>();
				TripleRoxx.get(i).add(theseRoxx);
			}
		}
		
		DoubleRoxx = new ArrayList<ArrayList<ArrayList<Rox>>>();
		for (int i = 0 ; i < 6 ; i++) {
			ArrayList<ArrayList<Rox>> thisOne1 = new ArrayList<ArrayList<Rox>>();
			DoubleRoxx.add(thisOne1);
			for (int j = 0 ; j < 4 ; j++) {
				ArrayList<Rox> theseRoxx1 = new ArrayList<Rox>();
				DoubleRoxx.get(i).add(theseRoxx1);
			
			}
		}
		
		SingleRoxx = new ArrayList<ArrayList<Rox>>();
		ArrayList<Rox> thisList= new ArrayList<Rox>();
		SingleRoxx.add(thisList);
		SingleRoxx.add(thisList);
		SingleRoxx.add(thisList);
		SingleRoxx.add(thisList);
		
		//ArrayList to put all filtered triple intersecting ROIs 
		ArrayList<ArrayList<Rox>> TripleRoxxAll = new ArrayList<ArrayList<Rox>>(); 
		TripleRoxxAll.add(thisList);
		TripleRoxxAll.add(thisList);
		TripleRoxxAll.add(thisList);
		TripleRoxxAll.add(thisList);
		
		//ArrayList to put all filtered double intersecting ROIs
		ArrayList<ArrayList<Rox>> DoubleRoxxAll = (ArrayList<ArrayList<Rox>>) TripleRoxxAll.clone();
		//ArrayList<ArrayList<Rox>> DoubleRoxxAll = new ArrayList<ArrayList<Rox>>(); 
		
		//Filter those Rox which are found to be intersecting with InterComposite ROIs which designate the whole map of
		//where intersections between certain ROIs are known to occur, for each combination.
		
		if (channelSize>3 && NewOverlap.QuadInterComposite!=null) {
			int c=0;
			for (Rox[] thisRoxy:allRox) {
				if (channelSelection[c]==true) {
					for (Rox rox:thisRoxy) {
						Roi roxRoi = rox.getRoi();
						ShapeRoi roxShape = new ShapeRoi((Roi) roxRoi.clone());
						try {
						if (roxShape.and(NewOverlap.QuadInterComposite).getBounds().height>0) {
							QuadRoxx.get(c).add(rox);
							}
						}
						catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
			c++;
			}
		}
		
		int c=0;
		for (Rox[] thisRoxy:allRox) {
			if (channelSelection[c]==true) {	
				for (Rox rox:thisRoxy) {
					int d=0;
					for(ShapeRoi interShape:NewOverlap.TripleInterComposites) {
						ShapeRoi roxShape = new ShapeRoi((Roi) rox.getRoi().clone());
						if (interShape!=null) {
							if (((ShapeRoi) roxShape.clone()).and(interShape).getBounds().height>0) {
								TripleRoxx.get(d).get(c).add(rox);
								TripleRoxxAll.get(c).add(rox);
								//System.out.println("Triple " + c + " d= " + d + "index" + rox.getIndex());
							}
						}		
					d++;	
					}
				}
			}
		c++;
		}
		
		c=0;
		for (ArrayList<ArrayList<Rox>> TripleList:TripleRoxx) {
			if (channelSelection[c]==true) {
				int d = 0;
				for(ArrayList<Rox> RoxList:TripleList) {
					if (RoxList!=null) {
						Iterator addRoiIterator = RoxList.iterator();
						RoiManager.getInstance().reset();
						while (addRoiIterator.hasNext()){
							RoiManager.getInstance().addRoi((Roi) ((Rox) addRoiIterator.next()).getRoi());
						}
						
					}
					d++;
				}
			}
			c++;
		}
		
		c=0;
		for (Rox[] thisRoxy:allRox) {
			if (channelSelection[c]==true) {
				for (Rox rox:thisRoxy) {
					int d=0;
					for(ShapeRoi interShape:NewOverlap.DoubleInterComposites) {
						if (interShape!=null) {
							ShapeRoi roxShape = new ShapeRoi(rox.getRoi());
							if (((ShapeRoi) roxShape.clone()).and(interShape).getBounds().height>0) {
								DoubleRoxx.get(d).get(c).add(rox);
								DoubleRoxxAll.get(c).add(rox);
							}	
						}	
					d++;	
					}
				}
			}	
		c++;
		}
		
		
		c=0;
		for (ArrayList<ArrayList<Rox>> DoubleList:DoubleRoxx) {
			if (DoubleList!=null) {	
				int d = 0;
				for(ArrayList<Rox> RoxList:DoubleList) {
					if (channelSelection[d]==true) {	
						Iterator addRoiIterator = RoxList.iterator();
						RoiManager.getInstance().reset();
						while (addRoiIterator.hasNext()){
							RoiManager.getInstance().addRoi((Roi) ((Rox) addRoiIterator.next()).getRoi());
						}
						
						d++;
						//WaitForUserDialog sd = new WaitForUserDialog("Check DoubleRox Combo");
						//sd.show();
					}
				}
			}
			c++;
		}
		
		c=0;
		for (Rox[] thisRoxy:allRox) {

			if (channelSelection[c]==true) {	
				for (Rox rox:thisRoxy) {
					SingleRoxx.get(c).add(rox);
				}
			}
		c++;
		}
		
		for (ArrayList<Rox> thisRoxx:QuadRoxx) {
			Collections.sort(thisRoxx, Comparator.comparingDouble(r -> r.getRoi().getBounds().getY())) ;
		}
		
		for (ArrayList<ArrayList<Rox>> RoxList:TripleRoxx) {
			for (ArrayList<Rox>thisRoxx1:RoxList) {
				Collections.sort(thisRoxx1, Comparator.comparingDouble(r -> r.getRoi().getBounds().getY())) ;
			}
		}
		
		for (ArrayList<ArrayList<Rox>> RoxList:DoubleRoxx) {
			for (ArrayList<Rox>thisRoxx2:RoxList) {
				Collections.sort(thisRoxx2, Comparator.comparingDouble(r -> r.getRoi().getBounds().getY())) ;
			}
		}
		
		//Pack the OverlapFilter object
		
		OverlapFilter thisFilter = new OverlapFilter(QuadRoxx, TripleRoxx, DoubleRoxx, SingleRoxx);
		
		TripleRoxxAll.clear();
		TripleRoxxAll = null;
		DoubleRoxxAll.clear();
		DoubleRoxxAll = null;
		
		//returned the packed OverlapFilter object
		return thisFilter;
	}

	
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
	
}
	

