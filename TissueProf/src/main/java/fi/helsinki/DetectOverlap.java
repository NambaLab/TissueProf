package fi.helsinki;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math3.util.CombinatoricsUtils;
import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.plugin.frame.RoiManager;
import Util.Combinations;
import Util.Combinations.ChannelCombinations;

public class DetectOverlap {
	
	static int c;
	static int r;

	public Roi channelCompositeRoi[] = new Roi[4];
	public ShapeRoi channelCompositeShape[]= new ShapeRoi[4];
	
	private ArrayList<ArrayList<ComboInterComposite>> ComboInterComposites; 
	
	DetectOverlap(Rox[][] allRox, LinkedHashMap<Roi, Rox> RoiRox, Boolean[] channelSelection, int channelSize){
		detectOverlap(allRox, RoiRox, channelSelection, channelSize);
	}
	
	public /*static*/ void detectOverlap(Rox[][] allRox, LinkedHashMap<Roi, Rox> RoiRox, Boolean[] channelSelection, int channelSize){
		//Make it more flexible to accomodate varying numbers of combinations and 4+ channels.
		
		System.out.println("detecting... ");
		IJ.log("Detecting overlap...");
		
		RoiManager.getRoiManager();
		
		RoiManager.getInstance().reset();
		
		/*
		for (Rox[] channelRox : allRox) {
			System.out.println("ChannelRox length " + channelRox.length);
		}
		*/
		
		//This is good and flexible ^^
		c=0;
		for (Rox[] channelRox: allRox) {
			if (channelSelection[c]==true && channelRox.length>0) {
				System.out.println("Doing chcomposites");
				//System.out.println("Channel roi length ln31 DetectOverlap" + channelRox.length);
				RoiManager.getInstance().addRoi(channelRox[0].getRoi());
				RoiManager.getInstance().run("Show All");
				channelCompositeShape[c] = new ShapeRoi(channelRox[0].getRoi());
			    for (int i = 1 ; i < channelRox.length ; i++) {
			    	Roi thisRoi = channelRox[i].getRoi();
			    	ShapeRoi channelRoiShape = new ShapeRoi(thisRoi); 
			    	//ShapeRoi channelRoiShape = new ShapeRoi(channelRox[i].getRoi()); 
			    	ShapeRoi channelCompositeClone =(ShapeRoi) channelCompositeShape[c].clone();
			    	channelCompositeShape[c] = channelCompositeClone.xor(channelRoiShape);
			    	
			    	int lengthafter = channelCompositeShape[c].getRois().length;
			    }
			    
			    //System.out.println("saving into composite array " + "Composite " + c + " " + channelCompositeShape[c].getRois().length);
			    //WaitForUserDialog seeProblem = new WaitForUserDialog("See issue");
			    //seeProblem.show();
			    RoiManager.getInstance().run("Show All");
			    channelCompositeRoi[c] = channelCompositeShape[c].shapeToRoi();
			    RoiManager.getInstance().reset();
			    RoiManager.getInstance().addRoi(channelCompositeShape[c].shapeToRoi());
			    
			}
		c++;
		}
		
		
		System.out.println("done composites now doing combocomposites");
		
		int n = channelSize;
		
		Combinations combinech = new Combinations();
		
		ChannelCombinations combinechs = combinech.new ChannelCombinations();
	
		List<List<List<Integer>>> AllChCombinations = combinechs.generateCombinations(n, n);
		
		ArrayList<ArrayList<ComboInterComposite>> ComboComposites = new ArrayList<ArrayList<ComboInterComposite>>();
		
		ArrayList<ArrayList<ComboInterComposite>> ComboInterComposites = findChComboIntersections(channelCompositeShape, AllChCombinations, ComboComposites, channelSelection);
																												
		for (ArrayList<ComboInterComposite> thisComboInterComposite : ComboInterComposites) {
			int c =0;
			for (ComboInterComposite thisComposite: thisComboInterComposite) {
				System.out.println("c " + c + " Combosize " +thisComposite.getComboSize());
				System.out.println("composite height " + thisComposite.getInterShape().getBounds().height);
				if (thisComposite/*.getInterShape().getRois()[0]*/!= null ) {
					if (thisComposite.getInterShape()!=null ) {
						if (thisComposite.getInterShape().getRois()!=null) {
							if (thisComposite.getInterShape().getRois().length>0) {
								Roi thisCompositeRoi = thisComposite.getInterShape().shapeToRoi();
								
								String concatenated = thisComposite.getComboIndexes().get(0).toString();
								
								int con = 0;
								for (Integer thisInt : thisComposite.getComboIndexes()) {
									if (con == 0 ) {con++;continue;}
									concatenated = String.join("", concatenated, thisInt.toString());
								}
								
								thisCompositeRoi.setName("n-" + thisComposite.getComboSize() + "_"  + "c-" + c + "_" + "Combo-" + concatenated);
								RoiManager.getRoiManager();
								RoiManager.getInstance().addRoi(thisCompositeRoi);
								c++;
							}
						}
					}
				}
			}
		}
		
		
		//WaitForUserDialog seeRois = new WaitForUserDialog("See combocomposites");
		//seeRois.show();
		
		setDetectResults(ComboInterComposites);
		
	
	}
		
	
	public ArrayList<ArrayList<ComboInterComposite>> getDetectResults () {
		return ComboInterComposites;
	}
	
	private void setDetectResults (ArrayList<ArrayList<ComboInterComposite>> comboInterComposites) {
		this.ComboInterComposites = comboInterComposites;
	}
	
	
	public ArrayList<ArrayList<ComboInterComposite>> findChComboIntersections(ShapeRoi[] ChannelComposites, List<List<List<Integer>>> ChannelComboRs, 
			ArrayList<ArrayList<ComboInterComposite>> ComboComposites, Boolean[] ChannelSelection) {
		
		//For each combination, select the relevant ROIs from the array storing composites according to the stored indexes.
		//Feed the selected indexes in the form of an Array to FindIntersection
		//Inside FindIntersection, sequentially find the intersection between all the composites that were fed into it 
		//Return the intersection shapeROI from FindIntersections to this method 
		//Add the returned shapeROI to its position in the ComboComposites.
		
		int n = 0;
		for (List<List<Integer>> thisComboRList : ChannelComboRs) {
			
			for (List<Integer> thisComboR : thisComboRList) {
				//List = e.g 1,2,3
				ArrayList<ShapeRoi> thisChCombo = new ArrayList<ShapeRoi>();
				boolean ChComboSelections[] = new boolean[thisComboR.size()];
				
				boolean AreSelected = false;
				
				int c = 0;
				int d = 0; 
				
				for (boolean thisSelect : ChComboSelections) {
					if (ChannelSelection[c] == true) {
						ChComboSelections[d] = true;
						d++;
					}
					c++;
				}
				
				if (d == ChComboSelections.length) {AreSelected = true;}
				
				if (AreSelected==true) {
					
					for (int i = 0 ; i < d ; i++){
						ComboComposites.add(new ArrayList<ComboInterComposite>());
					}
					
					//ArrayList<ShapeRoi> thisComboShapes = new ArrayList<ShapeRoi>();
					
					thisComboR.forEach(m->System.out.printf("index " + m + " "));
					thisComboR.forEach(m->thisChCombo.add(ChannelComposites[m]));
					
					//WaitForUserDialog seeComb = new WaitForUserDialog("See intercombo");
					//seeComb.show();
					
					
					//RoiManager.getInstance().addRoi(ChannelComposites[m].getRois()[0]);
					
					ShapeRoi thisComboIntersection = FindIntersection(thisChCombo);
					ComboInterComposite thisInterComposite = new ComboInterComposite(thisComboIntersection, thisComboR.size(), thisComboR);
					ComboComposites.get(n).add(thisInterComposite);
					thisComboIntersection = null;					
				}
			}
			n++;
		}
		return ComboComposites;
	}
	
	public static ShapeRoi FindIntersection(ArrayList<ShapeRoi> ShapeRois) {
		
		ShapeRoi CurrentIntersection  = (ShapeRoi) ShapeRois.get(0);
		
		int c = 0;
		for (ShapeRoi thisShape : ShapeRois ) {
			if (c==0) {c++; continue;}
			//System.out.println("c " + c + "CurrentIntersection height " + CurrentIntersection.getBounds().getHeight());
			if (thisShape != null) {
				CurrentIntersection = CurrentIntersection.and((ShapeRoi) thisShape);		    				
			}
			c++;
		}
		return CurrentIntersection;
	}
	
	
	public void clear() {
		
		channelCompositeRoi = new Roi[4];
		
		for (int i = 0 ; i < channelCompositeShape.length ; i++) {
			channelCompositeShape[i] = null;
		}
		
		channelCompositeShape = new ShapeRoi[4];
		
		/*
		for (int i = 0 ; i < TripleInterComposites.length ; i++) {
			TripleInterComposites[i]= null;
		}
		
		TripleInterComposites = new ShapeRoi[4];
		
		for (int i = 0 ; i < DoubleInterComposites.length ; i++) {
			DoubleInterComposites[i] = null;
		}
		
		DoubleInterComposites = new ShapeRoi[6];
		*/
	}
	
	
	public class ComboInterComposite {
		
		private ShapeRoi InterShape; 
		private int ComboSize;
		private List<Integer> ComboIndexes; 
		
		public ComboInterComposite(ShapeRoi interShape, int comboSize , List<Integer> comboIndexes) {
			this.setInterShape(interShape);
			this.setComboSize(comboSize);
			this.setComboIndexes(comboIndexes);
		}
		
		private void setInterShape(ShapeRoi interShape){
			this.InterShape = interShape;
		}
		
		private void setComboSize(int comboSize) {
			this.ComboSize = comboSize;
		}
		
		private void setComboIndexes(List<Integer> comboIndexes) {
			this.ComboIndexes = comboIndexes;
		}
		
		public ShapeRoi getInterShape() {
			return InterShape;
		}
		
		public int getComboSize() {
			return ComboSize;
		}
		
		public List<Integer> getComboIndexes(){
			return ComboIndexes;
		}
		
	}
	
	
}

		
		
		
		
	
	
	
	
		
		
		
	

	

