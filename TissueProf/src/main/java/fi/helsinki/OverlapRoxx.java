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
				System.out.println("ThisComboRoxx size = " + thisComboRoxx.getFilteredRoxx().size());
				
				/*
				System.out.print("this chindexes = " );
				thisComboRoxx.getComboIndexes().forEach(n->System.out.print(n.toString() + " "));
				System.out.print("       ChIndexes : "  );
				ChIndexes.forEach(n->System.out.print(n.toString() + " "));
				System.out.print("\n");
				
				*/
				if (thisComboRoxx.getComboIndexes().equals(ChIndexes)) {
					theseRoxx.add(thisComboRoxx.getFilteredRoxx());
					System.out.print("Added comboroxx to theseRoxx " + "comboroxx size " + thisComboRoxx.getFilteredRoxx().size());
					for (Rox rox : thisComboRoxx.getFilteredRoxx()) {
						RoiManager.getInstance().addRoi(rox.getRoi());
					}
					
					//WaitForUserDialog seeComboRox = new WaitForUserDialog("See FilteredRox");
					//seeComboRox.show();
					
					List<Integer> thisChIndexes = thisComboRoxx.getComboIndexes();
					thisChIndexes.forEach(n->System.out.print(n + " "));
					System.out.print("\n");
					
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
			
			ComboOverlapRoxx thisComboOverlapRoxx = new ComboOverlapRoxx(theseRoxx, ChIndexes, AllOverlapRox, overlapCount, currentIndex, ovth); 
			updateAllOverlapRoxx(thisComboOverlapRoxx.getAllOverlapRox());
			updateOverlapCount(thisComboOverlapRoxx.getOverlapCount());
			updateCurrentIndex(thisComboOverlapRoxx.getCurrentIndex());
			
			ComboOverlapRoxxes.add(thisComboOverlapRoxx);
			
			//Method to take in varying no of comboroxxes and apply a submethod to do overlap analysis
			//Then return ComboOverlapRoxx
			
		}
		
		
		
		
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
		
		
		
		
		/*
		@SuppressWarnings("unused")
		int d = 0;
		for (ArrayList<Rox> theseRox : OverlapFilter.QuadRoxx) {
			int c=0;
			RoiManager.getRoiManager().reset();
			for (Rox thisRox :theseRox) {
				RoiManager.getRoiManager().add(thisRox.getRoi(), c);
				c++;
			}
			d++;
		}
		
		//QuadOverlapRoxx = (ArrayList<ArrayList<Rox>>) OverlapFilter.QuadRoxx.clone(); 
		QuadOverlapRoxx = new ArrayList<ArrayList<Rox>>(OverlapFilter.QuadRoxx.size());
		//QuadOverlapRoxx.ensureCapacity(OverlapFilter.QuadRoxx.size());
		ArrayList<Rox> QuadOverlapRoxx0 = new ArrayList<Rox>();
		ArrayList<Rox> QuadOverlapRoxx1 = new ArrayList<Rox>();
		ArrayList<Rox> QuadOverlapRoxx2 = new ArrayList<Rox>();
		ArrayList<Rox> QuadOverlapRoxx3 = new ArrayList<Rox>();
		QuadOverlapRoxx.add(QuadOverlapRoxx0);
		QuadOverlapRoxx.add(QuadOverlapRoxx1);
		QuadOverlapRoxx.add(QuadOverlapRoxx2);
		QuadOverlapRoxx.add(QuadOverlapRoxx3);
		
		allOverlapRoxx = new ArrayList<Rox>();
		
		allInterRoxx = new ArrayList<Rox>();
		
		QuadInterRoxx = new ArrayList<Rox>();
		
		//QuadRoxxCompositeShape = new ArrayList<ShapeRoi>(); 
		//TripleRoxxCompositeShape= (ArrayList<ArrayList<ArrayList<ShapeRoi>>>) OverlapFilter.TripleRoxx.clone();
		//DoubleRoxxCompositeShape = (ArrayList<ArrayList<ArrayList<ShapeRoi>>>) OverlapFilter.TripleRoxx.clone();	
		
		TripleInterRoxx = new ArrayList<ArrayList<Rox>>();
		
		for (int i = 0 ; i < 4 ; i++) {
			ArrayList<Rox> thisList = new ArrayList<Rox>();
			TripleInterRoxx.add(thisList);
		}
		
		DoubleInterRoxx = new ArrayList<ArrayList<Rox>>();
		
		for (int i = 0 ; i < 6 ; i++) {
			ArrayList<Rox> thisList = new ArrayList<Rox>();
			DoubleInterRoxx.add(thisList);
		}
		
		InterRoxDataMap = new LinkedHashMap<Rox, RoiData>();
		
	ArrayList<Roi> Ellipses = new ArrayList<Roi>();
		
	c = 0 ; 
	if (channelSize>3) {	
		for (int i = 0 ; i < OverlapFilter.QuadRoxx.get(0).size(); i++) {
			
			Rox QuadRox0 = OverlapFilter.QuadRoxx.get(0).get(i);
			
			double[] roi0Pos = QuadRox0.getPosition();
			EllipseRoi thisEllipse = new EllipseRoi(roi0Pos[0],roi0Pos[1]-100,roi0Pos[0]-100,roi0Pos[1]+100, 1);
			Ellipses.add(thisEllipse);
			//EllipseRoi thisEllipse = new EllipseRoi(QuadRox0.getPosition()[0],QuadRox0.getPosition()[1]-30,QuadRox0.getPosition()[0]-30,QuadRox0.getPosition()[1]+30, 1);
			for (int j = 0 ; j < OverlapFilter.QuadRoxx.get(1).size(); j++) {
				Rox QuadRox1 = OverlapFilter.QuadRoxx.get(1).get(j);
				if (allOverlapRoxx.contains(QuadRox0)||allOverlapRoxx.contains(QuadRox1)) {continue;}
				RoiManager.getRoiManager().reset();
				RoiManager.getRoiManager().add(QuadRox1.getRoi(), 0);
				RoiManager.getRoiManager().select(0);
				double[] quad1pos = QuadRox1.getPosition();
				boolean r0 = (thisEllipse.containsPoint(quad1pos[0], quad1pos[1]));
				if (r0==false) {continue;}
				for (int k = 0 ; k < OverlapFilter.QuadRoxx.get(2).size(); k++) {
					Rox QuadRox2 = OverlapFilter.QuadRoxx.get(2).get(k);
					if (allOverlapRoxx.contains(QuadRox0)||allOverlapRoxx.contains(QuadRox1)||allOverlapRoxx.contains(QuadRox2)) {continue;}
					RoiManager.getRoiManager().reset();
					RoiManager.getRoiManager().add(QuadRox2.getRoi(), 0);
					RoiManager.getRoiManager().select(0);
					
					//System.out.println("roi2Pos " + roi2Pos.xCentroid + " " + roi2Pos.yCentroid);
					QuadRox2.getPosition();
					//System.out.println("roi2PixelPos " + QuadRox2.getPosition + " " + roi2Pos.yCentroid);
					boolean r1 = (thisEllipse.containsPoint(QuadRox2.getPosition()[0], QuadRox2.getPosition()[1]));
					if (r1==false) {continue;}
					//if (!thisEllipse.containsPoint(roi2Pos.xCentroid, roi2Pos.yCentroid)) { continue;} else {r1=true;}
					//if (!thisEllipse.containsPoint(QuadRox2.getPosition()[0], QuadRox2.getPosition()[1])) {continue;}
					for (int m = 0 ; m < OverlapFilter.QuadRoxx.get(3).size(); m++) {
						Rox QuadRox3 = OverlapFilter.QuadRoxx.get(3).get(m);
						if (allOverlapRoxx.contains(QuadRox0) || allOverlapRoxx.contains(QuadRox1) || allOverlapRoxx.contains(QuadRox2) || allOverlapRoxx.contains(QuadRox3)){continue;}
							//ImageStatistics roi3Pos = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.CENTROID, IJ.getImage().getCalibration());
						double[] quad3pos = QuadRox3.getPosition();
						//System.out.println("roi3Pos " + roi3Pos.xCentroid + " " + roi3Pos.yCentroid);
						boolean r2 = (thisEllipse.containsPoint(quad3pos[0], quad3pos[1]));
						if (r2==false) {continue;}
						//if (!thisEllipse.containsPoint(roi3Pos.xCentroid, roi3Pos.yCentroid)) { continue;} else {r2=true;}
						//if (!thisEllipse.containsPoint(QuadRox3.getPosition()[0], QuadRox3.getPosition()[1])) {continue;}
						
						if (r0 == true && r1 == true && r2 == true) {
							ShapeRoi shape0 = new ShapeRoi(QuadRox0.getRoi());
							ShapeRoi shape1 = new ShapeRoi(QuadRox1.getRoi());
							ShapeRoi shape2 = new ShapeRoi(QuadRox2.getRoi());
							ShapeRoi shape3 = new ShapeRoi(QuadRox3.getRoi());
							
							ShapeRoi shape0clone = (ShapeRoi) shape0.clone();
							ShapeRoi shape1clone = (ShapeRoi) shape1.clone();
							ShapeRoi shape2clone = (ShapeRoi) shape2.clone();
							ShapeRoi shape3clone = (ShapeRoi) shape3.clone();
							
							ShapeRoi QuadInter = (shape0clone).and(shape1clone).and(shape2clone).and(shape3clone); 
							
							if (QuadInter.getBounds().getHeight()>0) {
								//System.out.println("Shape with area " + QuadInter.getBounds());
								//System.out.println("intersect " + i + " " + j + " " + k + " " + m);
								Roi QuadInterRoi = QuadInter.shapeToRoi();
								RoiManager.getRoiManager().reset();
								//Here setIndex, setArea and setMean for the RoiData created so that Rox class can find these and make Rox
								Rox[] QuadRoxes = {QuadRox0, QuadRox1, QuadRox2, QuadRox3};
								ShapeRoi[] QuadShapes = {shape0, shape1, shape2, shape3};
								
								//double[] CoupleAreas = new double[6];
								double[] CoupleRatios = new double[12];
								
								int x = 0 ;
								for (int v=0; v<4; v++) {
									for (int w=0; w<4; w++) {
										if (w>v) {
											RoiManager.getInstance().reset();
											//System.out.println("QuadRox " + v + " intersecting " + "QuadRox " + w);
											//ShapeRoi CoupleShape = QuadRoxes[v].shape.and(QuadRoxes[w].shape);
											ShapeRoi CoupleShape = QuadShapes[v].and(QuadShapes[w]);
											Roi CoupleRoi = CoupleShape.shapeToRoi();
											
											RoiData CoupleData = new RoiData(IJ.getImage(),CoupleRoi);
											double CoupleRoiArea = CoupleData.setArea(IJ.getImage());
	
											CoupleRatios[x] = CoupleRoiArea/RoxDataMap.get(QuadRoxes[v]).setArea(IJ.getImage());
											
											x++;
											CoupleRatios[x] = CoupleRoiArea/RoxDataMap.get(QuadRoxes[w]).setArea(IJ.getImage());
											//System.out.println("This area " + RoxDataMap.get(QuadRoxes[w]).setArea(IJ.getImage()));
											//System.out.println("This ratio " + x + " " + CoupleRoiArea/RoxDataMap.get(QuadRoxes[w]).setArea(IJ.getImage()));
											x++;
											
										}	
									}	
								}

								
								//System.out.println("\n" + "Now checking overlap extent");
								if ((CoupleRatios[0]>ovth || CoupleRatios[1]>ovth) && (CoupleRatios[2]>ovth || CoupleRatios[3]>ovth) && (CoupleRatios[4]>ovth || CoupleRatios[5]>ovth) 
										&& (CoupleRatios[6]>ovth || CoupleRatios[7]>ovth) && (CoupleRatios[8]>ovth || CoupleRatios[9]>ovth) && (CoupleRatios[10]>ovth || CoupleRatios[11]>ovth)) {
									//System.out.println("+70%");
									for (int h = 0 ; h < 4 ; h++) {
										QuadOverlapRoxx.get(h).add(QuadRoxes[h]);
										allOverlapRoxx.add(QuadRoxes[h]);
									}
									RoiData QuadInterRoiData = new RoiData(IJ.getImage(), QuadInterRoi);
									//RoiManager.getInstance().add(QuadInterRoi, 0);
									QuadInterRoiData.setArea(IJ.getImage());//For now, any image will do, but when it comes to intensity...
									//QuadInterRoiData.setMean(IJ.getImage());
									QuadInterRoiData.setIndex(NextIndex);
									NextIndex++;
									Rox QuadInterRox = new Rox(QuadInterRoiData); 
									QuadInterRoxx.add(QuadInterRox);
									allInterRoxx.add(QuadInterRox);
									InterRoxDataMap.put(QuadInterRox, QuadInterRoiData);
									totalOverlap++;
								}
							}	
						}
					}	
				}	
			}	
		}	
		
		System.out.println("Analysis of overlap finished");
		
		RoiManager.getRoiManager().reset();
		
		for (ArrayList<Rox> thisoverquad:QuadOverlapRoxx) {
			for (Rox thisRox:thisoverquad) {
				RoiManager.getRoiManager().addRoi(thisRox.getRoi());
			}
		}
	}
	
	
	RoiManager.getRoiManager().reset();
	
	ArrayList<Integer> TripleCombSizes = new ArrayList<Integer>();
	
	////Interlude
	
	
	if (channelSize>2) {
		//System.out.println("No combs " + OverlapFilter.TripleRoxx.size());
		synchronized (OverlapFilter.TripleRoxx) {
			for (ArrayList<ArrayList<Rox>> TripleRoxxList : OverlapFilter.TripleRoxx) {
				//System.out.println("No. combs " + TripleRoxxList.size());
				synchronized(TripleRoxxList) {
					for (ArrayList<Rox> TripleCombRoxx : TripleRoxxList) {
						//System.out.println("This comb size " + TripleCombRoxx.size());
						TripleCombSizes.add(TripleCombRoxx.size());
						synchronized(TripleCombRoxx) {
							Iterator<Rox> iterator = TripleCombRoxx.iterator();
							//TripleCombSizes.add(TripleCombRoxx.size());
							while (iterator.hasNext()) {
								Rox thisRox = iterator.next();
								if (allOverlapRoxx.contains(thisRox)) {
									iterator.remove(); // Remove the element using the iterator
								}
							}
							//System.out.println("This comb size after " + TripleCombRoxx.size());
						}
					}
				}
			}
		}
	}
	
	if (channelSize>2) {
		//System.out.println("Size of TripleCombSizes " + TripleCombSizes.size());
	}
	
	////Triple Overlap check for filtered ROIs
	
	
	
	TripleOverlapRoxx = new ArrayList<ArrayList<ArrayList<Rox>>>();
	
	for (int y = 0 ; y < 4 ; y++) {
		ArrayList<ArrayList<Rox>> TripleInterRox0 = new ArrayList<ArrayList<Rox>>();	
		TripleOverlapRoxx.add(TripleInterRox0);
	}
	
	for (ArrayList<ArrayList<Rox>> theseRox:TripleOverlapRoxx) {
		for (int y = 0 ; y < 4 ; y++) {	
			ArrayList<Rox> theRoxes = new ArrayList<Rox>();
		theseRox.add(theRoxes);
		}
	}
	
	//Check ArrayList sizes
	/*
	System.out.println("TripleOverlapRoxx.size " + TripleOverlapRoxx.size());
	
	for (int y = 0 ; y < TripleOverlapRoxx.size() ; y++) {
		System.out.println("TripleOverlapComboSize" + y + " " + TripleOverlapRoxx.get(y).size());
	}
	
	int countdown = 0;
	int finalcountdown = 0 ; 
	int r = 0 ;
	int counter = 0;
	for (int e = 0 ; e < OverlapFilter.TripleRoxx.size() ; e++) {
		ArrayList<Integer> chs = new ArrayList<Integer>();
		chs.add(0); chs.add(1); chs.add(2); chs.add(3);
		ArrayList<ArrayList<Rox>> thisComb = OverlapFilter.TripleRoxx.get(e);
		
		
		int v=0;
		for (ArrayList<Rox> thiscombchannel:thisComb) {
			if (thiscombchannel.size()==0 && chs.size()!=3) {
				chs.remove(v);
			}
		v++;
		}
			
					
					
		int a = chs.get(0) ;
		int b = chs.get(1) ;
		int c = chs.get(2) ;
		
		if (r == 0) { 
			a=0;
			b=1;
			c=2;
		} else {
			if (r==1) {
				a=0;
				b=1;
				c=3;
			}
		 else {
			if (r==2) {
				a=0;
				b=2;
				c=3;
			} else {
				if (r==3){
				a=1;
				b=2;
				c=3;
			}
			}
		}
		}
		
		if (channelSelection[a]==false || channelSelection[b]==false || channelSelection[c]==false) {r++; continue;}
		 
		if (chs.size()<3) {continue;}
		
		//Check combinations
		/*
		System.out.println("This abc a " + a + " " + " b " + b + " c " + c);
		
		System.out.println("comb " + e + "channel " + a + OverlapFilter.TripleRoxx.get(e).get(a).size());
		System.out.println("comb " + e + "channel " + b + OverlapFilter.TripleRoxx.get(e).get(b).size());
		System.out.println("comb " + e + "channel " + c + OverlapFilter.TripleRoxx.get(e).get(c).size());
		*/
		
		//System.out.println("a " + a + " b " + b + " c " + c + " r__ " + r);
		/*
		
		for (int i = 0 ; i < OverlapFilter.TripleRoxx.get(e).get(a).size(); i++){
			Rox Rox0 = OverlapFilter.TripleRoxx.get(e).get(a).get(i);
			double[] roi0Pos = Rox0.getPosition();
			EllipseRoi thisEllipse = new EllipseRoi(roi0Pos[0],roi0Pos[1]-100,roi0Pos[0]-100,roi0Pos[1]+100, 1);
			for (int j = 0 ; j < OverlapFilter.TripleRoxx.get(e).get(b).size() ; j++) {
				Rox Rox1 = OverlapFilter.TripleRoxx.get(e).get(b).get(j);
				if (allOverlapRoxx.contains(Rox0)||allOverlapRoxx.contains(Rox1)) {continue;}
				
				double[] roi1Pos = Rox1.getPosition();
				boolean r0 = (thisEllipse.containsPoint(roi1Pos[0], roi1Pos[1]));
				
				if(r0==false) {continue;}
				
				for (int k = 0 ; k < OverlapFilter.TripleRoxx.get(e).get(c).size() ; k++) {
					Rox Rox2 = OverlapFilter.TripleRoxx.get(e).get(c).get(k);
					if (allOverlapRoxx.contains(Rox0)||allOverlapRoxx.contains(Rox1) ||allOverlapRoxx.contains(Rox2)) {continue;}
					double[] roi2Pos = Rox2.getPosition();
					boolean r1 = (thisEllipse.containsPoint(roi2Pos[0], roi2Pos[1]));
					
					if (r1==false) {continue;}
					
					if (r0 == true && r1 == true) {
						
						ShapeRoi shape0 = new ShapeRoi(Rox0.getRoi());
						ShapeRoi shape1 = new ShapeRoi(Rox1.getRoi());
						ShapeRoi shape2 = new ShapeRoi(Rox2.getRoi());
						
						ShapeRoi shape0clone = (ShapeRoi) shape0.clone();
						ShapeRoi shape1clone = (ShapeRoi) shape1.clone();
						ShapeRoi shape2clone = (ShapeRoi) shape2.clone();
						
						
						ShapeRoi QuadInter = (shape0clone).and(shape1clone).and(shape2clone); 
						
						if (QuadInter.getBounds().getHeight()>0) {
							//Keep track of events
							//System.out.println("intersect " + "a " + a + " b " + b + " c " + c + " | " + " i " + i + " j " + j + " k " + k);
							countdown++;
							
							
							Roi TripleInterRoi = QuadInter.shapeToRoi();
							RoiManager.getRoiManager().reset();

							Rox[] TripleRoxes = {Rox0, Rox1, Rox2};
							ShapeRoi[] TripleShapes = {shape0, shape1, shape2};
							double[] TripleRatios = new double[6];
							int x = 0;
							for (int h = 0 ; h < TripleRoxes.length ; h ++) {
								for (int m = 0 ; m < TripleShapes.length ; m++) {
									if (m>h) {
										ShapeRoi rox12Shape = ((ShapeRoi) TripleShapes[h].clone()).and(TripleShapes[m]);
										Roi Couple12Roi = rox12Shape.shapeToRoi();
										RoiData Couple12Data = new RoiData(IJ.getImage(),Couple12Roi);
										double CoupleRoiArea = Couple12Data.setArea(IJ.getImage());
										
										TripleRatios[x] = CoupleRoiArea/RoxDataMap.get(TripleRoxes[h]).setArea(IJ.getImage());
										x++;
										TripleRatios[x] = CoupleRoiArea/RoxDataMap.get(TripleRoxes[m]).setArea(IJ.getImage());
										x++;
										
									}	
								}
							}
							
							
							int p=0;
							for (double ratio:TripleRatios) {
								System.out.print("ratio" + c + " " + ratio + " ");
								p++;
							}	
							
							if ((TripleRatios[0]>ovth||TripleRatios[1]>ovth) && (TripleRatios[2]>ovth||TripleRatios[3]>ovth) 
									&&(TripleRatios[4]>ovth||TripleRatios[5]>ovth)){
								//keep track of events
								//System.out.println("+70%/////////////////////");
								finalcountdown++;
								//System.out.println("final countdown " + finalcountdown);
							
								for (p = 0 ; p < 3 ; p++) {
									TripleOverlapRoxx.get(e).get(p).add(TripleRoxes[p]);
									allOverlapRoxx.add(TripleRoxes[p]);
								}
								RoiData TripleInterRoiData = new RoiData(IJ.getImage(), TripleInterRoi);
								//RoiManager.getInstance().add(QuadInterRoi, 0);
								TripleInterRoiData.setArea(IJ.getImage());//For now, any image will do, but when it comes to intensity...
								//TripleInterRoiData.setMean(IJ.getImage());
								TripleInterRoiData.setIndex(NextIndex);
								NextIndex++;
								Rox TripleInterRox = new Rox(TripleInterRoiData); 
								TripleInterRoxx.get(e).add(TripleInterRox);
								InterRoxDataMap.put(TripleInterRox, TripleInterRoiData);
								allInterRoxx.add(TripleInterRox);
								totalOverlap++;
							}
						}
						
					}
					counter++;	
				}
			}
		}
		r++;	
	}
	
	/*
	System.out.println("no of iterations " + counter);
	System.out.println("countdown " + countdown);
	System.out.println("final countdown)" + finalcountdown);
	*/
	
	
	//Checking triple overlaps
	/*
	for (int i = 0 ; i < 4 ; i ++) {
		for (int j = 0 ; j < 3 ; j++) {
			//RoiManager.getInstance().reset();
			//System.out.println("TripleOverlapRoxxComboSize " + TripleOverlapRoxx.get(i).get(j).size());
			for (int k = 0 ; k <TripleOverlapRoxx.get(i).get(j).size() ; k++) {
			RoiManager.getInstance().addRoi(TripleOverlapRoxx.get(i).get(j).get(k).getRoi());

			}
		}
	}
	*/
	
	
	////Interlude: adjusting doubleoverlap ArrayLists 
	
		/*
	ArrayList<Integer> DoubleCombSizes = new ArrayList<Integer>();
	DoubleCombSizes.ensureCapacity(24);
	
	System.out.println("Doing double overlap... ");
	
	//Checking overlap extent of double combinations of filtered ROIs.
	//Filtering out already detected ROIs from OverlapFilter results so they are not iterated over twice
	//when looking for double overlap
	
	synchronized (OverlapFilter.DoubleRoxx) {
		for (ArrayList<ArrayList<Rox>> DoubleRoxxList : OverlapFilter.DoubleRoxx) {
			//System.out.println("No. combs " + DoubleRoxxList.size());
			synchronized(DoubleRoxxList) {
				for (ArrayList<Rox> DoubleCombRoxx : DoubleRoxxList) {
					//System.out.println("This doubleroxx comb size " + DoubleCombRoxx.size());
					DoubleCombSizes.add(DoubleCombRoxx.size());
					synchronized(DoubleCombRoxx) {
						Iterator<Rox> iterator = DoubleCombRoxx.iterator();
						//DoubleCombSizes.add(DoubleCombRoxx.size());
						while (iterator.hasNext()) {
							Rox thisRox = iterator.next();
							if (allOverlapRoxx.contains(thisRox)) {	
								iterator.remove(); 
							}
						}
						//System.out.println("This comb size after " + DoubleCombRoxx.size());
					}
				}
			}
		}
	}

	
	////From the filtered ROIs associated with double-overlap areas, check overlap extent 
	//of inndividual ROIs
	
	//First adjust ArrayLists
	
	DoubleOverlapRoxx = new ArrayList<ArrayList<ArrayList<Rox>>>();
	
	for (int y = 0 ; y < 6 ; y++) {
		ArrayList<ArrayList<Rox>> DoubleInterRox0 = new ArrayList<ArrayList<Rox>>();	
		DoubleOverlapRoxx.add(DoubleInterRox0);
	}

	for (ArrayList<ArrayList<Rox>> theseRox:DoubleOverlapRoxx) {
		for (int y = 0 ; y < 2 ; y++) {	
			ArrayList<Rox> theRoxes = new ArrayList<Rox>();
				theseRox.add(theRoxes);
		}
	}
	
	//intialize counters for indexing 
	
	@SuppressWarnings("unused")
	int countdownDouble = 0;
	@SuppressWarnings("unused")
	int finalcountdownDouble = 0 ; 
	r = 0 ;
	@SuppressWarnings("unused")
	int counterDouble = 0;
	
	//check overlapfilter size
	//System.out.println("DoubleRoxx size here " + OverlapFilter.DoubleRoxx.size());
	
	for (int e = 0 ; e < OverlapFilter.DoubleRoxx.size() ; e++) {
		
			//System.out.println("r = " + r);
		
			ArrayList<Integer> chs = new ArrayList<Integer>();
			chs.add(0); chs.add(1); chs.add(2); chs.add(3);
			ArrayList<ArrayList<Rox>> thisComb = OverlapFilter.DoubleRoxx.get(e);			
			
			int a = 0;
			int b = 0;
			
			if (r==0) {
				a = chs.get(0) ; b = chs.get(1) ;
			}
			if (r==1) {
				a = chs.get(0) ; b = chs.get(2) ;
			}
			if (r==2) {
				a = chs.get(0) ; b = chs.get(3) ;
			}
			if (r==3) {
				a = chs.get(1) ; b = chs.get(2) ;
			}
			if (r==4) {
				a = chs.get(1) ; b = chs.get(3) ;
			}
			if (r==5) {
				a = chs.get(2) ; b = chs.get(3) ;
			}
			
			//if (channelSelection[a]==false && channelSelection[b]==false) {continue;}
			
			for (int i = 0 ; i < OverlapFilter.DoubleRoxx.get(e).get(a).size(); i++){
				Rox Rox0 = OverlapFilter.DoubleRoxx.get(e).get(a).get(i);
				double[] roi0Pos = Rox0.getPosition();
				//ImageStatistics roi0Pos = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.CENTROID, IJ.getImage().getCalibration());
				//System.out.println("roi0Pos " + roi0Pos);
				EllipseRoi thisEllipse = new EllipseRoi(roi0Pos[0],roi0Pos[1]-100,roi0Pos[0]-100,roi0Pos[1]+100, 1);
				for (int j = 0 ; j < OverlapFilter.DoubleRoxx.get(e).get(b).size() ; j++) {
					Rox Rox1 = OverlapFilter.DoubleRoxx.get(e).get(b).get(j);
					if (allOverlapRoxx.contains(Rox0)||allOverlapRoxx.contains(Rox1)) {continue;}
					//ImageStatistics roi1Pos = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.CENTROID, IJ.getImage().getCalibration());
					double[] roi1Pos = Rox1.getPosition();
					//System.out.println("roi1Pos " + roi1Pos.xCentroid + " " + roi1Pos.yCentroid);
					boolean r0 = (thisEllipse.containsPoint(roi1Pos[0], roi1Pos[1]));
					
					if(r0==false) {continue;}
						
						if (r0 == true) {
			
							ShapeRoi shape0 = new ShapeRoi(Rox0.getRoi());
							ShapeRoi shape1 = new ShapeRoi(Rox1.getRoi());
							
							ShapeRoi shape0clone = (ShapeRoi) shape0.clone();
							ShapeRoi shape1clone = (ShapeRoi) shape1.clone();
							
							ShapeRoi DoubleInter = (shape0clone).and(shape1clone);
							
							if (DoubleInter.getBounds().getHeight()>0) {
								//print intersection incidence information
								//System.out.println("intersect " + "a " + a + " b " + b + " c " + c + " | " + " i " + i + " j " + j);
								
								countdownDouble++;
								
								Roi DoubleInterRoi = DoubleInter.shapeToRoi();
								RoiManager.getRoiManager().reset();
								//Here setIndex, setArea and setMean for the RoiData you created so that Rox can reach these and make Rox
								Rox[] DoubleRoxes = {Rox0, Rox1};
								ShapeRoi[] DoubleShapes = {shape0, shape1};
	
								ShapeRoi rox12Shape = ((ShapeRoi) DoubleShapes[0].clone()).and(DoubleShapes[1]);
								Roi Couple12Roi = rox12Shape.shapeToRoi();
								RoiData Couple12Data = new RoiData(IJ.getImage(),Couple12Roi);
								double CoupleRoiArea = Couple12Data.setArea(IJ.getImage());
								
								double Ratio0 = CoupleRoiArea/RoxDataMap.get(DoubleRoxes[0]).setArea(IJ.getImage());
								
								double Ratio1  = CoupleRoiArea/RoxDataMap.get(DoubleRoxes[1]).setArea(IJ.getImage());
								
								//System.out.println("This area " + RoxDataMap.get(QuadRoxes[w]).setArea(IJ.getImage()));
								//System.out.println("This ratio " + x + " " + CoupleRoiArea/RoxDataMap.get(QuadRoxes[w]).setArea(IJ.getImage());
								if (Ratio0>ovth||Ratio1>ovth){
									//Keep track of events
									//System.out.println("Mark +ratio overlap incident");
									//System.out.println("+" + "+ratio+" + "%/////////////////////");
									finalcountdownDouble++;
									//System.out.println("final countdown " + finalcountdownDouble);
									for (int p = 0 ; p < 2 ; p++) {
										DoubleOverlapRoxx.get(e).get(p).add(DoubleRoxes[p]);	
										allOverlapRoxx.add(DoubleRoxes[p]);
									}
									RoiData DoubleInterRoiData = new RoiData(IJ.getImage(), DoubleInterRoi);
									//RoiManager.getInstance().add(QuadInterRoi, 0);
									DoubleInterRoiData.setArea(IJ.getImage());//For now, any image will do, but when it comes to intensity...
									DoubleInterRoiData.setMean(IJ.getImage());
									DoubleInterRoiData.setIndex(NextIndex);
									NextIndex++;
									Rox DoubleInterRox = new Rox(DoubleInterRoiData); 
									DoubleInterRoxx.get(e).add(DoubleInterRox);
									InterRoxDataMap.put(DoubleInterRox, DoubleInterRoiData);
									allInterRoxx.add(DoubleInterRox);
									totalOverlap++;
								}
							}
							
						}
						counterDouble++;	
				}
			}
			r++;	
	}
	
	//Check counter situation
	//System.out.println("no of iterations double " + counterDouble);
	//System.out.println("countdownDouble " + countdownDouble);
	//System.out.println("the final countdownDouble )" + finalcountdownDouble);
	
	RoiManager.getInstance().reset();
	
	for (int i = 0 ; i < 6 ; i ++) {
		for (int j = 0 ; j < 2 ; j++) {
			//System.out.println("DoubleOverlapRoxxComboSize " + DoubleOverlapRoxx.get(i).get(j).size());
			for (int k = 0 ; k <DoubleOverlapRoxx.get(i).get(j).size() ; k++) {
			RoiManager.getInstance().addRoi(DoubleOverlapRoxx.get(i).get(j).get(k).getRoi());
			}
		}
		//WaitForUserDialog checkover1 = new WaitForUserDialog("Check overlap");
		//checkover1.show();
	}	
	//WaitForUserDialog checkover1 = new WaitForUserDialog("Check overlap");
	//checkover1.show();
	
	//check allOverlap arraylist size to verify detections
	//System.out.println("allOverlapSize " + allOverlapRoxx.size());
	
	
	
	//Adjust SingleRoxx before starting individual overlap analysis
	
	SingleRoxx = new ArrayList<ArrayList<Rox>>();
	
	for (int i = 0 ; i < 4 ; i++) {
		ArrayList<Rox> newList = new ArrayList<Rox>();
		SingleRoxx.add(newList);	
	}
	
	ArrayList<Roi>allOverlapRoi = new ArrayList<Roi>();
	
	for (Rox roox : allOverlapRoxx) {
		allOverlapRoi.add(roox.getRoi());
	}
	
	//Roi[][] allRoi = new Roi[allRox.length][50];
	
	//Make and fill an ArrayList for allOverlapIndexes
	
	ArrayList<Integer> allOverlapIndex = new ArrayList<Integer>();
	
	Iterator<Rox> allOverIterator = allOverlapRoxx.iterator();
	
	while (allOverIterator.hasNext()) {
		allOverlapIndex.add(((Rox) allOverIterator.next()).getIndex());
	}
	
	for (int i=0; i < allRox.length ; i++) {
		if (channelSelection[i]==true) {	
			for (int j = 0 ; j < allRox[i].length ; j++) {
				//if ((!QuadOverlapRoxx.contains(allRox[i][j])) && (!TripleOverlapRoxx.contains(allRox[i][j])) && (!DoubleOverlapRoxx.contains(allRox[i][j]))){
				if (!allOverlapIndex.contains(allRox[i][j].getIndex())) {
					SingleRoxx.get(i).add(allRox[i][j]);
					totalOverlap++;
				}
				else { continue;}
			}
		}
	}
	
	c=0;
	for (int i = 0 ; i < SingleRoxx.size(); i++) {
		//Verify that SingleRoxx ArrayList has been filled 
		//System.out.println("SingleRox" + c + " size after filling List " + SingleRoxx.get(i).size());
	c++;
	}
	
	//Reset the RoiManager
	RoiManager.getInstance().reset();
	
	//Add singleRoxx Rois to be saved
	
	for (ArrayList<Rox> thisSingle:SingleRoxx) {
		for (Rox theRox : thisSingle) {
			RoiManager.getInstance().addRoi(theRox.getRoi());
		}
	}
	
	IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + "1" + ".tif");
	
	Window imwin = IJ.getImage().getWindow();
	imwin.dispose();
	imwin = null;
	IJ.getImage().flush();
	IJ.getImage().close();
	
	*/
	
	
	}
	
	
	public void clear(){
		
		if (QuadOverlapRoxx!=null) {
			QuadOverlapRoxx.removeAll(QuadOverlapRoxx);
			QuadOverlapRoxx.clear();
			this.QuadOverlapRoxx = null; 
		}
		if (TripleOverlapRoxx!=null) {
			TripleOverlapRoxx.removeAll(TripleOverlapRoxx);
			TripleOverlapRoxx.clear();
			this.TripleOverlapRoxx = null;
		}
		if (DoubleOverlapRoxx!=null) {
			DoubleOverlapRoxx.removeAll(DoubleOverlapRoxx);
			DoubleOverlapRoxx.clear();
			this.DoubleOverlapRoxx = null;
		}
		if (SingleRoxx!=null) {
			SingleRoxx.removeAll(SingleRoxx);
			SingleRoxx.clear();
			this.SingleRoxx = null;
		}
		if (QuadInterRoxx!=null) {
			QuadInterRoxx.removeAll(QuadInterRoxx);
			QuadInterRoxx.clear();
			this.QuadInterRoxx = null;
		}
		if (TripleInterRoxx!=null) {
			TripleInterRoxx.removeAll(TripleInterRoxx);
			TripleInterRoxx.clear();
			this.TripleInterRoxx = null;
		}
		if (DoubleInterRoxx!=null) {
			DoubleInterRoxx.removeAll(DoubleInterRoxx);
			DoubleInterRoxx.clear();
			this.DoubleInterRoxx = null; 
		}
		if (AllOverlapRox!=null) {
			AllOverlapRox.removeAll(AllOverlapRox);
			AllOverlapRox.clear();
		}

		overlapCount = 0;
		
	}


	
	public class ComboOverlapRoxx {
		
		ArrayList<OverlapRox> OverlapRoxx;
		List<Integer> ChIndexes;
		int ComboSize;
		ArrayList<ArrayList<Rox>> AllOverlapRox;
		int overlapCount;
		int currentIndex;
		
		
		ComboOverlapRoxx(ArrayList<ArrayList<Rox>> theseRoxx, List<Integer> ChIndexes, ArrayList<ArrayList<Rox>> allOverlapRox, int currentIndex, 
																							int overlapCount, double overlapThreshold){
			
			
			overlapComboRoxx(theseRoxx, ChIndexes, allOverlapRox, currentIndex, overlapCount, overlapThreshold);
			
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
		private void setCurrentIndex(int index) {
			this.currentIndex = index;
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
		public int getCurrentIndex() {
			return currentIndex;
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
			
			RoxCombinations.forEach(n->System.out.println("n = " + n.size()));
			
			
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
					for (Rox rox : thisRoxy) {
						allOverlapRox.get(rox.getChannelSource()).add(rox);
						thisOverlapRox.setInterIndex(currentIndex);
						allInterRox.add(thisOverlapRox.InterRox);
						currentIndex++;
						overlapCount++;
					}	
				}
			}
			
			this.setOverlapRoxx(foundOverlapRoxx);
			this.setChIndexes(ChIndexes);
			this.setComboSize(ChIndexes.size());
			this.setAllOverlapRoxx(allOverlapRox);
			this.setCount(overlapCount);
			this.setCurrentIndex(currentIndex);
			
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
			System.out.println("Roxy.size before generating combinations " + Roxy.size());
			List<List<List<Integer>>> thisCombination = combinechs.generateCombinations(Roxy.size(), 2);
			
			List<List<Integer>> pairs = new ArrayList<List<Integer>>();
			
			for (List<List<Integer>> thisListofLists: thisCombination) {
				for (List<Integer> thisList : thisListofLists) {
					
					thisList.forEach(n->System.out.print(n + " "));
					System.out.print("\n");
					
					pairs.add(thisList);
					
				}
			}
			
			Collections.sort(Roxy, Comparator.comparingDouble(r -> r.getInterArea(ChIndexes))) ;
			
			int c = 0 ;
			for (List<Integer> thisPair : pairs) {
				
				System.out.print("Checking pairwiseOverlap : ");
				thisPair.forEach(n->System.out.print(n.toString() + " "));
				System.out.print("\n");
				
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
			InterRox.setIndex(InterIndex);
			return InterRox;
		}
		
		public boolean pairwiseOverlap(Rox rox1, Rox rox2, double overlapThreshold) {
			
			boolean pairwiseOverlapped = false;
			
			ShapeRoi ShapeRoi1 = new ShapeRoi((Roi) rox1.getRoi().clone());
			ShapeRoi ShapeRoi2 = new ShapeRoi((Roi) rox2.getRoi().clone());
			ShapeRoi interShape = ((ShapeRoi) ShapeRoi1.clone()).and((ShapeRoi) ShapeRoi2.clone());
			
			Roi InterRoi = interShape.shapeToRoi();
			
			double rox1Area = rox1.getArea(); System.out.print("rox1Area = " + rox1Area);
			double rox2Area = rox2.getArea(); System.out.print("rox2Area = " + rox2Area); System.out.print("\n");
			
			if (interShape!=null && interShape.getBounds().height>0){
				
				RoiData InterData = new RoiData(IJ.getImage(), InterRoi);
				double ShapeArea = InterData.setArea(IJ.getImage());
				
				double ratio1 = ShapeArea/rox1Area;
				double ratio2 = ShapeArea/rox2Area;
				
				System.out.print("ratio1 : " + ratio1 + " ");
				System.out.print("ratio2 : " + ratio2 + " ");
				
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
			
			System.out.print("PairwiseOverlapped? " + pairwiseOverlapped);
			System.out.print("\n");
			
			
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
	                    
	                    //Also do overlap analysis here and already make the overlaproxx?
	                    //Turn this whole method into overlap method?
	                    //or pass resulting groups to overlaprox and do the overlap analysis there?
	                    //but then some sets will have differing numbers of rox.. Filter them out later according to chindexes.size()?
	                    //or delete them once there is no overlap, which serves the main point anyway
	                    //All you'll have to do is keep track of the alloverlaproxx and also filter out already detected overlaprox
	                    //Nope-- Doing overlap without making sure all the rox in the set are in vicinity of each other will increase
	                    //complexity. Go ahead with returning the proximity filtered sets to combooverlapRoxx and pick up from there.
	                    
	                }
	            }
	            
	            RoxCombinations = newCombinations;
	        }
	        
	        return RoxCombinations;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	

