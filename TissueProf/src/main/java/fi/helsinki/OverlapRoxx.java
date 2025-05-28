package fi.helsinki;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import Util.Combinations;
import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import fi.helsinki.OverlapFilter.ComboRoxx;
import ij.IJ;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.plugin.frame.RoiManager;


public class OverlapRoxx {
	
	//TODO
	//Create a master method for overlaps of all kinds supporting n number of channels
	//Skip pairwise overlap of remaining pairs in an overlap if at any point one of the two 
	//pairwise overlaps falls below the overlap threshold.
	//Find any possible ways to reduce the number of steps and make the execution more efficient
	//Possibly find a better approach with less steps and less memory consumption for the whole
	//overlap analysis while maintaining the same overall logic
	
	int c = 0 ;
	
	
	ArrayList<ComboOverlapRoxx> ComboOverlapRoxxes;
	
	
	ArrayList<ArrayList<Rox>> QuadOverlapRoxx;
	ArrayList<ArrayList<ArrayList<Rox>>> TripleOverlapRoxx;
	ArrayList<ArrayList<ArrayList<Rox>>> DoubleOverlapRoxx;
	ArrayList<ArrayList<Rox>> SingleRoxx;	
	
		
	
	//ArrayList<ShapeRoi> QuadRoxxCompositeShape;
	//ArrayList<ArrayList<ArrayList<ShapeRoi>>> TripleRoxxCompositeShape;
	//ArrayList<ArrayList<ArrayList<ShapeRoi>>> DoubleRoxxCompositeShape;
	ArrayList<ArrayList<Rox>> AllOverlapRox;
	
	ArrayList<Rox> QuadInterRoxx; 
	ArrayList<ArrayList<Rox>> TripleInterRoxx; 
	ArrayList<ArrayList<Rox>> DoubleInterRoxx;
	
	ArrayList<Rox> allInterRox; 
	LinkedHashMap<Rox, RoiData> InterRoxDataMap;
	
	int overlapCount;
	int currentIndex;
	
	OverlapRoxx(){
		
		/*
		this.QuadOverlapRoxx = QuadOverlapRoxx;
		this.TripleOverlapRoxx = TripleOverlapRoxx;
		this.DoubleOverlapRoxx = DoubleOverlapRoxx;
		this.SingleRoxx = SingleRoxx;
		this.QuadInterRoxx = QuadInterRoxx;
		this.TripleInterRoxx = TripleInterRoxx;
		this.DoubleInterRoxx = DoubleInterRoxx;
		this.totalOverlap = totalOverlap; // counts the number of overlap instances
		*/
		
		
	}	
	
	
	

	boolean done = false;
	//change return type to OverlapRoxx later after having built the constructor
	public synchronized void overlapRoxx(OverlapFilter OverlapFilter, Rox[][] allRox, LinkedHashMap<Rox, RoiData> RoxDataMap,
			int NextIndex, Boolean[] channelSelection, int channelSize, String inputDir2, String OutputDir, String imageName,
			double ovth){
		
		//TODO
		//Deal with static access warnings
		//Create a master method for overlap of all kinds of different combinations with varying number of participants
		
		ArrayList<ComboRoxx> AllTheseComboRoxx = OverlapFilter.getRoxx();
		
		int x = 0;
		for (ComboRoxx thisComboRoxx : AllTheseComboRoxx) {
			
			List<Integer> thisInt = thisComboRoxx.getComboIndexes();
			thisInt.forEach(n->System.out.print(n.toString() + " "));
			System.out.print("ComboRoxx  " + x + " size = " + thisComboRoxx.getFilteredRoxx().size() );
			System.out.print("\n");
			x++;
			
		}
		
		System.out.println(AllTheseComboRoxx.size());
		ArrayList<List<Integer>> AllFilteredChIndexes = new ArrayList<List<Integer>>();
		
		Combinations combinations = new Combinations();
		
		Combinations.ChannelCombinations combinechs = combinations.new ChannelCombinations();
		
		List<List<List<Integer>>> AllChCombinations = combinechs.generateCombinations(channelSize, channelSize);
		
		List<List<Integer>> AllFilteredChIndexesOrdered = new ArrayList<List<Integer>>();
		
		
		for (List<List<Integer>> Chind : AllChCombinations) {
			
			for (List<Integer> thisind : Chind) {
				AllFilteredChIndexesOrdered.add(thisind);
				thisind.forEach(n->System.out.print(n + " "));
				System.out.print("\n");
			}
		}
		//Filter according to these ChIndexes.
		
		for (ComboRoxx thisComboRoxx : AllTheseComboRoxx) {     
			List<Integer> thisChIndexes = thisComboRoxx.getComboIndexes();
			if (!AllFilteredChIndexes.contains(thisChIndexes)) {
				AllFilteredChIndexes.add(thisChIndexes);
			}
		}
	
		synchronized(AllFilteredChIndexesOrdered) {
		    Iterator<List<Integer>> iterator = AllFilteredChIndexesOrdered.iterator();
		    while (iterator.hasNext()) {
		        List<Integer> ChIndex = iterator.next();
		        if (!AllFilteredChIndexes.contains(ChIndex)) {
		            iterator.remove();  
		        }
		    }
		}
		
		
		System.out.println("ChIndexes after filtering in proper order");
		for (List<Integer> thisind : AllFilteredChIndexesOrdered) {
			thisind.forEach(n->System.out.print(n + " "));
			System.out.print("\n");
		}
		
	
		//Find the total number of Rox from each channel, which have already been indexed. Indexing of the intersection ROIs start from the 
		//next index-- curentIndex is updated to the total number of Rox from each channel before overlap.
		
		int totalRox = 0;
		for (int i = 0 ; i < allRox.length ; i++) {
			totalRox = allRox[i].length + totalRox;
		}
		
		updateCurrentIndex(totalRox);
		System.out.println("currentIndex after first update " + currentIndex);
		//Set overlapCount to 0 before starting overlap analysis
		updateOverlapCount(0);
		//Get All Rox combinations of Rox in each other's vicinity for each overlap category
		
		allInterRox = new ArrayList<Rox>();
		
		initializeAllOverlapRox(AllOverlapRox, channelSize);
		
		if (RoiManager.getInstance()==null) {
			RoiManager.getRoiManager();
		}
					
		ComboOverlapRoxxes = new ArrayList<ComboOverlapRoxx>();
		
		for (List<Integer> ChIndexes : AllFilteredChIndexesOrdered) {
			
			ArrayList<ArrayList<Rox>> theseRoxx = new ArrayList<ArrayList<Rox>>();
			
			RoiManager.getInstance().reset();
			
			for (ComboRoxx thisComboRoxx : AllTheseComboRoxx) {
				//System.out.println("ThisComboRoxx size = " + thisComboRoxx.getFilteredRoxx().size());
				
				/*
				System.out.print("this chindexes = " );
				thisComboRoxx.getComboIndexes().forEach(n->System.out.print(n.toString() + " "));
				System.out.print("       ChIndexes : "  );
				ChIndexes.forEach(n->System.out.print(n.toString() + " "));
				System.out.print("\n");
				
				*/
				if (thisComboRoxx.getComboIndexes().equals(ChIndexes)) {
					theseRoxx.add(thisComboRoxx.getFilteredRoxx());
					//System.out.print("Added comboroxx to theseRoxx " + "comboroxx size " + thisComboRoxx.getFilteredRoxx().size());
					for (Rox rox : thisComboRoxx.getFilteredRoxx()) {
						RoiManager.getInstance().addRoi(rox.getRoi());
					}
					
					//WaitForUserDialog seeComboRox = new WaitForUserDialog("See FilteredRox");
					//seeComboRox.show();
					
					List<Integer> thisChIndexes = thisComboRoxx.getComboIndexes();
					//thisChIndexes.forEach(n->System.out.print(n + " "));
					//System.out.print("\n");
					
				}
				else {
					/*
					System.out.print("indexes don't match comboroxx indexes : ");
					thisComboRoxx.getComboIndexes().forEach(n->System.out.print(n + " "));
					System.out.print("ChIndexes " );
					ChIndexes.forEach(n->System.out.print(n + " "));
					System.out.print("\n");
					*/
				}
				
				  
			}
			
			ChIndexes.forEach(n->System.out.print(n.toString() + " "));
			System.out.print("TheseRoxx size " + theseRoxx.size());
			System.out.print("\n");
			System.out.println("BEFORE COMBOOVERLAPROXX");
			System.out.println("Index from overlapRoxx currentIndex now = " + currentIndex);
			
			
			
			
			
			ComboOverlapRoxx thisComboOverlapRoxx = new ComboOverlapRoxx(theseRoxx, ChIndexes, AllOverlapRox, this.getCurrentIndex(), overlapCount, ovth); 
			updateAllOverlapRoxx(thisComboOverlapRoxx.getAllOverlapRox());
			updateOverlapCount(thisComboOverlapRoxx.getOverlapCount());
			System.out.println("AFTER COMBOOVERLAPROXX");
			System.out.println("Updating index from overlapRoxx currentIndex = " + currentIndex);
			System.out.println("Updating index from overlapRoxx previous combocurentIndex = " + thisComboOverlapRoxx.getCurrentComboIndex());
			updateCurrentIndex(thisComboOverlapRoxx.getCurrentComboIndex());
			System.out.println("(After update) Updated index from overlapRoxx currentIndex = " + currentIndex);
			System.out.println("(After update) Updated index from overlapRoxx previous combocurentIndex = " + thisComboOverlapRoxx.getCurrentComboIndex());
			
			
			ComboOverlapRoxxes.add(thisComboOverlapRoxx);
			
			//System.out.println("AllOverlapRox Size " + thisComboOverlapRoxx.getAllOverlapRox().size());
			
			int d =0;
			for (ArrayList<Rox> roxList : thisComboOverlapRoxx.getAllOverlapRox()) {
				System.out.println(roxList.size());
				d++;
			}
			//System.out.println("AllOverlapRox as is ");
			
			d =0;
			for (ArrayList<Rox> roxList : AllOverlapRox) {
				//System.out.println(roxList.size());
				d++;
			}
			
			
			//Method to take in varying no of comboroxxes and apply a submethod to do overlap analysis
			//Then return ComboOverlapRoxx
			
			
		}
		
		//System.out.println("allOverlapRox size after making all comboroxxes " + AllOverlapRox.size());
		int d =0;
		for (ArrayList<Rox> roxList : AllOverlapRox) {
			//System.out.println(roxList.size());
			d++;
		}
		//Determine which rox from each channel are not contained in the already found overlaps and set them as OverlapRoxx, add to 
		//combooverlaproxxes
		
		addSingleRoxxes(channelSelection, allRox, ComboOverlapRoxxes, AllOverlapRox, currentIndex, overlapCount, ovth);
		
		//	private void addSingleRoxxes(int channelSize, boolean[] channelSelection, Rox[][] allRox, ArrayList<ComboOverlapRoxx> comboOverlapRoxxes, 
		//ArrayList<ArrayList<Rox>> allOverlapRox, int currentIndex, int overlapCount, int overlapThreshold)
		

		
		
		
		
		//Using the groups in allCombinedOverlapRoxx (With differing set sizes) do OverlapAnalysis
		//Looping over allCombinedOverlapRoxx,
		//First, make a simple pairwise overlap analysis method returning true or false
		//(optional) create a flexible overlap analysis method which takes variable number of ROIs and do the following:
			//Once ComboOverlapRox group is set, create ranking based on InterArea.
			//Starting with the rox with the lowest interarea, create overlap pairs 
			//Do sequential overlap analysis of the pairs, once a pair does not satisfy overlap threshold, continue to the next ComboOverlapRox
			//If all pairs satisfy overlap criteria, 
			//save the Roxes in the group into a new OverlapRox object. 
			//Add these Rox to the allOverlapRox list
			//increment the counter
			//
		
		
		IJ.log("Analyzing overlap of channel ROIs... ");
		
		IJ.open(OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + 1 + ".tif");
	
	
	
	}
	
	
	public class ComboOverlapRoxx {
		
		ArrayList<OverlapRox> OverlapRoxx;
		List<Integer> ChIndexes;
		int ComboSize;
		ArrayList<ArrayList<Rox>> AllOverlapRox;
		int overlapCount;
		int currentComboIndex;
		
		
		ComboOverlapRoxx(ArrayList<ArrayList<Rox>> theseRoxx, List<Integer> ChIndexes, ArrayList<ArrayList<Rox>> allOverlapRox, int currentIndex, 
																							int overlapCount, double overlapThreshold){
			
			this.setCurrentComboIndex(currentIndex);
			//currentComboIndex = currentIndex;
			
			System.out.println(" (Before overlap) ComboOverlapRoxx currentIndex = " +  this.getCurrentComboIndex() + " or " + currentComboIndex);
			if (theseRoxx.size()>0){
				overlapComboRoxx(theseRoxx, ChIndexes, allOverlapRox, currentIndex, overlapCount, overlapThreshold);
			}
			
			
			System.out.println(" (After overlap) ComboOverlapRoxx currentIndex = " +  this.getCurrentComboIndex() + " or " + currentComboIndex);
			
			//this.setOverlapRoxx(overlapRoxx);
			//this.setChIndexes(chIndexes);
			//this.setCount(count);
			
		}
		
		private void setOverlapRoxx(ArrayList<OverlapRox> overlapRoxx) {
			this.OverlapRoxx = overlapRoxx;
		}
		private void setChIndexes(List<Integer> chIndexes) {
			this.ChIndexes = chIndexes;
		}
		private void setComboSize(int comboSize) {
			this.ComboSize = comboSize;
		}
		private void setCount(int count) {
			this.overlapCount = count;
		}
		private void setCurrentComboIndex(int index) {
			this.currentComboIndex = index;
		}
		private void setAllOverlapRoxx(ArrayList<ArrayList<Rox>> allOverlapRox) {
			this.AllOverlapRox = allOverlapRox;
		}
		public ArrayList<OverlapRox> getOverlapRoxx(){
			return OverlapRoxx;
		}
		public List<Integer> getChIndexes(){
			return ChIndexes;
		}
		public int getComboSize() {
			return ComboSize;
		}
		public ArrayList<ArrayList<Rox>> getAllOverlapRox(){
			return AllOverlapRox;
		}
		public int getOverlapCount() {
			return overlapCount;
		}
		public int getCurrentComboIndex() {
			return currentComboIndex;
		}
		
		public void overlapComboRoxx(ArrayList<ArrayList<Rox>> theseRoxx, List<Integer> ChIndexes, ArrayList<ArrayList<Rox>> allOverlapRox,
																	int currentIndex, int overlapCount, double overlapThreshold){
			
			//Go through combinations of groupings of Rox from the Roxx
			//Group Rox, send to overlapRox
			//Return overlap results for the group
			//If positive, add to current overlapRoxx
			//If current overlapRoxx.size()>0{
				//add to overlapComboRoxx 
			//set overlapComboRoxx
			//setCount ---Also, gonna have to return the updated count 
			//set chindexes
			//Remove positive rox from their original Roxx ---Needs to be static? --neeeded at all? Just check allOverlap.contains 
			//in each iteration as usual and continue that way? Number of roxx increase exponentially with no.channels anyway...
			
			//ArrayList<ArrayList<Rox>> RoxCombinations = new ArrayList<ArrayList<Rox>>(); 
			
			//RoxCombinations = CombineRox.getRoxCombinations(theseRoxx); 
			
			
			
			CombineRox combineTheseRox = new CombineRox();
			
			ArrayList<ArrayList<Rox>> RoxCombinations = combineTheseRox.getRoxCombinations(theseRoxx);
			
			//Proceed with OverlapRox
			
			
			if (RoiManager.getInstance() == null) {
				RoiManager.getRoiManager();
			}
			
			for (ArrayList<Rox> thisRoxx : RoxCombinations) {
				RoiManager.getInstance().reset();
				
				for (Rox rox : thisRoxx) {
					RoiManager.getInstance().addRoi(rox.getRoi());
				}
				
				//WaitForUserDialog seeRois = new WaitForUserDialog("See Rois");
				//seeRois.show();
				
			}
			
			
			ArrayList<OverlapRox> foundOverlapRoxx = new ArrayList<OverlapRox>();
			
			for (ArrayList<Rox> thisRoxy : RoxCombinations) {
				
				System.out.print("thisRoxy size = " + thisRoxy.size() + " ThisRoxy indexes " );
				thisRoxy.forEach(n-> System.out.print(n.getIndex() + " "));
				System.out.print("\n");
				
				OverlapRox thisOverlapRox = new OverlapRox();
				
				if (alreadyOverlapped(thisRoxy, allOverlapRox)) {continue;}
			
				if(thisOverlapRox.isOverlapped(thisRoxy, ChIndexes, overlapThreshold)){
					foundOverlapRoxx.add(thisOverlapRox);	
					System.out.println("setting index from comboroxx index = " + String.valueOf(currentComboIndex));
					thisOverlapRox.setInterIndex(currentComboIndex);					
					thisOverlapRox.getInterRox().setIndex(currentComboIndex);
					allInterRox.add(thisOverlapRox.getInterRox());
					currentComboIndex++;
					overlapCount++;
					for (Rox rox : thisRoxy) {
						allOverlapRox.get(rox.getChannelSource()).add(rox);
					}	
				}
			}
			
			this.setOverlapRoxx(foundOverlapRoxx);
			this.setChIndexes(ChIndexes);
			this.setComboSize(ChIndexes.size());
			this.setAllOverlapRoxx(allOverlapRox);
			this.setCount(overlapCount);
			this.setCurrentComboIndex(currentComboIndex);
		}
		
		private boolean alreadyOverlapped(ArrayList<Rox> roxy, ArrayList<ArrayList<Rox>> allOverlapRox) {
			System.out.println("checking if already overlapped ");
			
			System.out.println("AllOverlapRox.size " + allOverlapRox.size());
			
			boolean alreadyOverlapped = false; 
			for (Rox rox : roxy) {
				
				if (allOverlapRox.get(rox.getChannelSource()).contains(rox)){
					alreadyOverlapped = true;
					break;
				}
				
			}
			
			System.out.println("already overlapped? " + alreadyOverlapped);
			
			return alreadyOverlapped;
			
			
			
		}
			
		
	}
	
		
	
	public class OverlapRox {
		
		ArrayList<Rox> OverlappingRoxes;
		Rox InterRox;
		ArrayList<Rox> PairwiseInterRoxes;
		int InterIndex;
		
		
		OverlapRox(){
			
			//this.setOverlappingRoxes(OverlappingRoxes);
			//this.setInterRox(InterRox);
			//this.setInterIndex(InterIndex);
			
		}
		
		private void setOverlappingRoxes(ArrayList<Rox> overlappingRoxes) {
			this.OverlappingRoxes = overlappingRoxes;
		}
		private void setInterRox(Rox interRox) {
			this.InterRox = interRox;
		}
		private void setInterIndex(int interIndex) {
			this.InterIndex = interIndex;
		}
		
		public ArrayList<Rox> getOverlappingRoxes(){
			return OverlappingRoxes;
		}
		public Rox getInterRox() {
			return InterRox;
		}
		public int getInterIndex () {
			return InterIndex;
		}
		
		/*
		public OverlapRox overlapRox(ArrayList<Rox> overlappedRoxy, List<Integer> ChIndexes) {
			
			
			
			Rox thisFinalRox = overlappedRoxy.get(0);
			
			return thisFinalRox; 
			
		}
		*/
		
		
		public boolean isOverlapped(ArrayList<Rox> Roxy, List<Integer> ChIndexes, double overlapThreshold) {
			
			System.out.print("Checking if roxy is overlapped ");
			Roxy.forEach(n->System.out.print(n.getIndex() + " "));
			System.out.print("\n");
			
			List<Integer> thisChIndexes = new ArrayList<Integer>();
			
			//Create pairs
			//Pass to pairwise overlap
			//If all pairwise overlaps==true
			//Create OverlapRox
			//Set required info
			//Return
			boolean overlapped = false;
			
			Combinations thisComb = new Combinations();
			Combinations.ChannelCombinations combinechs = thisComb.new ChannelCombinations();
			//System.out.println("Roxy.size before generating combinations " + Roxy.size());
			List<List<List<Integer>>> thisCombination = combinechs.generateCombinations(Roxy.size(), 2);
			
			List<List<Integer>> pairs = new ArrayList<List<Integer>>();
			
			for (List<List<Integer>> thisListofLists: thisCombination) {
				for (List<Integer> thisList : thisListofLists) {
					
					//thisList.forEach(n->System.out.print(n + " "));
					//System.out.print("\n");
					
					pairs.add(thisList);
					
				}
			}
			
			Collections.sort(Roxy, Comparator.comparingDouble(r -> r.getInterArea(ChIndexes))) ;
			
			int r = 0 ; 
			for (Rox rox : Roxy) {
				//System.out.println("Rox " + r + " FilterInterArea : " + rox.getInterArea(ChIndexes));
				r++;
			}
			
			
			
			
			int c = 0 ;
			for (List<Integer> thisPair : pairs) {
				
				//System.out.print("Checking pairwiseOverlap : ");
				//thisPair.forEach(n->System.out.print(n.toString() + " "));
				//System.out.print("\n");
				
				Rox rox1 = Roxy.get(thisPair.get(0));
				Rox rox2 = Roxy.get(thisPair.get(1));
				
				boolean pairwiseOverlapped = pairwiseOverlap(rox1, rox2, overlapThreshold);
				
				if(pairwiseOverlapped == false) {
					overlapped = false;
					break;
				}
				else {
					c++;
				}
			}
			
			if (c == pairs.size()) {
				overlapped = true;
				
				
				setInterRox(findInterRox(Roxy));
				setOverlappingRoxes(Roxy);
				//Take care of indexing 
				//Take care of AllOverlapRox
				
			}
			
			System.out.println("is overlapped? " + overlapped);
			
			return overlapped;
		}
		
		
		public Rox findInterRox(ArrayList<Rox> Roxy) {
			ArrayList<ShapeRoi> OverlappingRoxShapes = new ArrayList<ShapeRoi>();
			
			for (Rox rox: Roxy) {
				OverlappingRoxShapes.add((ShapeRoi)rox.shape.clone());
			}
			
			ShapeRoi InterShape = DetectOverlap.FindIntersection(OverlappingRoxShapes);
			Roi InterRoi = InterShape.shapeToRoi();
			RoiData InterData = new RoiData(IJ.getImage(), InterRoi);
			Rox InterRox = new Rox(InterData);
			System.out.println("setting index from findInterRox index = " + InterIndex + "  CurrentIndex? = " + currentIndex); 
			//InterRox.setIndex(InterIndex);
			return InterRox;
		}
		
		public boolean pairwiseOverlap(Rox rox1, Rox rox2, double overlapThreshold) {
			
			boolean pairwiseOverlapped = false;
			
			ShapeRoi ShapeRoi1 = new ShapeRoi((Roi) rox1.getRoi().clone());
			ShapeRoi ShapeRoi2 = new ShapeRoi((Roi) rox2.getRoi().clone());
			ShapeRoi interShape = ((ShapeRoi) ShapeRoi1.clone()).and((ShapeRoi) ShapeRoi2.clone());
			
			Roi InterRoi = interShape.shapeToRoi();
			
			double rox1Area = rox1.getArea(); //System.out.print("rox1Area = " + rox1Area);
			double rox2Area = rox2.getArea(); //System.out.print("rox2Area = " + rox2Area); System.out.print("\n");
			
			if (interShape!=null && interShape.getBounds().getHeight()>0){
				
				RoiData InterData = new RoiData(IJ.getImage(), InterRoi);
				double ShapeArea = InterData.setArea(IJ.getImage());
				
				double ratio1 = ShapeArea/rox1Area;
				double ratio2 = ShapeArea/rox2Area;
				
				//System.out.print("ratio1 : " + ratio1 + " ");
				//System.out.print("ratio2 : " + ratio2 + " ");
				
				if (ratio1> overlapThreshold || ratio2 > overlapThreshold) {
					pairwiseOverlapped = true;
					if(PairwiseInterRoxes == null) {
						PairwiseInterRoxes = new ArrayList<Rox>();
					}
					PairwiseInterRoxes.add(new Rox(InterData));
				}
				else {
					pairwiseOverlapped = false;
				}
				
			}
			
			//System.out.print("PairwiseOverlapped? " + pairwiseOverlapped);
			//System.out.print("\n");
			
			
			return pairwiseOverlapped;
			
		}

	}
		
	
	public class CombineRox {

	    public ArrayList<ArrayList<Rox>> getRoxCombinations(ArrayList<ArrayList<Rox>> Roxxes) {
	        ArrayList<ArrayList<Rox>> RoxCombinations = new ArrayList<>();
	        if (Roxxes.isEmpty()) {
	            RoxCombinations.add(new ArrayList<>());
	            return RoxCombinations;
	        }
	  
	        // Start with the first list's elements as initial combinations
	        ArrayList<Rox> firstRoxx = Roxxes.get(0);
	        for (Rox rox : firstRoxx) {
	            RoxCombinations.add(new ArrayList<>(Arrays.asList(rox)));
	        }
	        
	        // For each remaining list, expand the existing combinations
	        for (int i = 1; i < Roxxes.size(); i++) {
	            ArrayList<Rox> currentList = Roxxes.get(i);
	            ArrayList<ArrayList<Rox>> newCombinations = new ArrayList<>();
	            
	            for (ArrayList<Rox> combination : RoxCombinations) {
	               
	            	for (Rox rox : currentList) {
        				
	            		ArrayList<Rox> newCombination = new ArrayList<>(combination);
	                    
	                    //Add proximity filter here 
	                    Rox rox0 = combination.get(combination.size()-1);
	    				
	        			double[] roi0Pos = rox0.getPosition();
	                    EllipseRoi thisEllipse = new EllipseRoi(roi0Pos[0],roi0Pos[1]-100,roi0Pos[0]-100,roi0Pos[1]+100, 1);
	                    double[] roi1Pos = rox.getPosition();
	                   
	                    if(thisEllipse.containsPoint(roi1Pos[0], roi1Pos[1])) {
	                    	newCombination.add(rox);
	                    	newCombinations.add(newCombination);
	            
	                    }
	                    else {	
	                    	newCombination.removeAll(newCombination);
	                    	newCombination.clear();
	                    	
	                    }
	                    
	                }
	            }
	            
	            RoxCombinations = newCombinations;
	        }
	        
	        return RoxCombinations;
	    }
	}
	
	private void addSingleRoxxes(Boolean[] channelSelection, Rox[][] allRox, ArrayList<ComboOverlapRoxx> comboOverlapRoxxes, 
			ArrayList<ArrayList<Rox>> allOverlapRox, int currentIndex, int overlapCount, double overlapThreshold) {
		
		ArrayList<ArrayList<Integer>> allOverlapIndexes = new ArrayList<ArrayList<Integer>>();
		
		for (int i = 0 ; i < allRox.length ; i++) {
			allOverlapIndexes.add(new ArrayList<Integer>());
		}
		
		Iterator<ArrayList<Rox>> allOverIterator = allOverlapRox.iterator();
		synchronized( allOverlapIndexes) {
			int c = 0;
			while (allOverIterator.hasNext()) {
				Iterator<Rox> thisRoxIterator = (Iterator<Rox>) allOverIterator.next().iterator();
				synchronized(allOverlapIndexes) {
					while (thisRoxIterator.hasNext()){
						allOverlapIndexes.get(c).add(thisRoxIterator.next().getIndex());
						//	allOverIterator.next().add(((Rox) thisRoxIterator.next()).getIndex());
					}
					c++;				
				}
			}
		}
		
		
		for (int i = 0 ; i < allRox.length ; i++) {
			ComboOverlapRoxx thisSingleOverlapRoxx;
			ArrayList<OverlapRox> thisSingleRoxx;
			if (channelSelection[i]==false) {continue;}
			else {thisSingleRoxx = new ArrayList<OverlapRox>();
			ArrayList<ArrayList<Rox>> thisRoxx = new ArrayList<ArrayList<Rox>>();
			thisRoxx.add(new ArrayList<Rox>());
			List<Integer> thisChIndex = new ArrayList<Integer>();
			thisChIndex.add(i);
			
			thisSingleOverlapRoxx = new ComboOverlapRoxx(thisRoxx, thisChIndex, allOverlapRox, currentIndex, overlapCount, 
					overlapThreshold);
			
			//Make the combooverlaproxx
			
			}			
			for (int j = 0 ; j < allRox[i].length ; j++) {
				if (!allOverlapIndexes.get(i).contains(allRox[i][j].getIndex())) {
					
					OverlapRox thisSingleRox = new OverlapRox();
					ArrayList<Rox> thisSingleRoxList = new ArrayList<Rox>();
					thisSingleRoxList.add(allRox[i][j]);
					
					//make overlaprox
					thisSingleRox.setOverlappingRoxes(thisSingleRoxList);
					//add tp list of overlaproxes
					thisSingleRoxx.add(thisSingleRox);
					
				}
				
			}
		
		thisSingleOverlapRoxx.setOverlapRoxx(thisSingleRoxx);
		//add to combooverlaproxx
		comboOverlapRoxxes.add(thisSingleOverlapRoxx);
		
		}
		
		
	}
	
	private void updateAllOverlapRoxx(ArrayList<ArrayList<Rox>> allOverlapRoxx) {
		this.AllOverlapRox = allOverlapRoxx;
	}
	
	private void updateOverlapCount(int overlapCount) {
		this.overlapCount = overlapCount;
	}
	
	private void updateCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	private int getCurrentIndex() {
		return currentIndex;
	}
	
	
	private void initializeAllOverlapRox(ArrayList<ArrayList<Rox>> allOverlapRox, int chSize) {
		allOverlapRox = new ArrayList<ArrayList<Rox>>();
		for (int i = 0 ; i < chSize ; i++) {
			allOverlapRox.add(new ArrayList<Rox>());
		}
		this.AllOverlapRox = allOverlapRox;
	}
	
	private void setComboOverlapRoxxes(ArrayList<ComboOverlapRoxx> comboOverlapRoxxes) {
		this.ComboOverlapRoxxes = comboOverlapRoxxes; 
	}
	public ArrayList<ComboOverlapRoxx> getComboOverlapRoxxes(){
		return ComboOverlapRoxxes;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	

