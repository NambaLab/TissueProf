package fi.helsinki;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.scijava.Context;
import org.scijava.platform.Platform;
import org.scijava.service.Service;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.measure.Measurements;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;
import loci.poi.util.SystemOutLogger;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.InitPreprocessor;
import org.scijava.module.process.PreprocessorPlugin;

public class OverlapRoxx {
	
	int c = 0 ;
	
	int totalOverlap = 0; 
	
	ArrayList<ArrayList<Rox>> QuadOverlapRoxx;
	ArrayList<ArrayList<ArrayList<Rox>>> TripleOverlapRoxx;
	ArrayList<ArrayList<ArrayList<Rox>>> DoubleOverlapRoxx;
	ArrayList<ArrayList<Rox>> SingleRoxx;	
	
	//ArrayList<ShapeRoi> QuadRoxxCompositeShape;
	//ArrayList<ArrayList<ArrayList<ShapeRoi>>> TripleRoxxCompositeShape;
	//ArrayList<ArrayList<ArrayList<ShapeRoi>>> DoubleRoxxCompositeShape;
	ArrayList<Rox> allOverlapRoxx;
	
	ArrayList<Rox> QuadInterRoxx; 
	ArrayList<ArrayList<Rox>> TripleInterRoxx; 
	ArrayList<ArrayList<Rox>> DoubleInterRoxx;
	
	ArrayList<Rox> allInterRoxx; 
	
	LinkedHashMap<Rox, RoiData> InterRoxDataMap;
	

	boolean done = false;
	//change return type to OverlapRoxx later after having built the constructor
	public synchronized void overlapRox(OverlapFilter OverlapFilter, Rox[][] allRox, LinkedHashMap<Rox, RoiData> RoxDataMap,
			int NextIndex, Boolean[] channelSelection, int channelSize, String inputDir2, String OutputDir, String imageName,
			double ovth){
		//Same logic as in the macro, except we will run through a smaller searchspace for interactions as we have already detected
		//all the sites for each type of interaction. While running through this searchspace we will continue using also the 
		//proximity filter built on the first Rox in the loop, using a circle and the roi.contains method
		
		IJ.log("Analyzing overlap of channel ROIs... ");
		
		System.out.println("Outputdir in overlaproxx " + OutputDir);
		System.out.println("Open this image " + OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + 1 + ".tif");
		
		IJ.open(OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + 1 + ".tif");
		
		System.out.println("Last Index " + NextIndex);
		
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
							System.out.println("i " + i + " j " + j + " k " + k + " m " + m);
							
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
								System.out.println("intersect " + i + " " + j + " " + k + " " + m);
								Roi QuadInterRoi = QuadInter.shapeToRoi();
								RoiManager.getRoiManager().reset();
								//Here setIndex, setArea and setMean for the RoiData created so that Rox class can find these and make Rox
								Rox[] QuadRoxes = {QuadRox0, QuadRox1, QuadRox2, QuadRox3};
								ShapeRoi[] QuadShapes = {shape0, shape1, shape2, shape3};
								
								double[] CoupleAreas = new double[6];
								double[] CoupleRatios = new double[12];
								
								int x = 0 ;
								for (int v=0; v<4; v++) {
									for (int w=0; w<4; w++) {
										if (w>v) {
											RoiManager.getInstance().reset();
											System.out.println("QuadRox " + v + " intersecting " + "QuadRox " + w);
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

								
								System.out.println("\n" + "Now checking overlap extent");
								if ((CoupleRatios[0]>ovth || CoupleRatios[1]>ovth) && (CoupleRatios[2]>ovth || CoupleRatios[3]>ovth) && (CoupleRatios[4]>ovth || CoupleRatios[5]>ovth) 
										&& (CoupleRatios[6]>ovth || CoupleRatios[7]>ovth) && (CoupleRatios[8]>ovth || CoupleRatios[9]>ovth) && (CoupleRatios[10]>ovth || CoupleRatios[11]>ovth)) {
									System.out.println("+70%");
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
		System.out.println("No combs " + OverlapFilter.TripleRoxx.size());
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
		System.out.println("Size of TripleCombSizes " + TripleCombSizes.size());
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
	*/
	
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
			chs.forEach(n->System.out.print("chs " +  n + " "));
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
		
		System.out.println("This abc a " + a + " " + " b " + b + " c " + c);
		 
		System.out.println("comb " + e + "channel " + a + OverlapFilter.TripleRoxx.get(e).get(a).size());
		System.out.println("comb " + e + "channel " + b + OverlapFilter.TripleRoxx.get(e).get(b).size());
		System.out.println("comb " + e + "channel " + b + OverlapFilter.TripleRoxx.get(e).get(c).size());
		
		
		System.out.println("a " + a + " b " + b + " c " + c + " r__ " + r);
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
	
	for (int i = 0 ; i < 4 ; i ++) {
		for (int j = 0 ; j < 3 ; j++) {
			//RoiManager.getInstance().reset();
			System.out.println("TripleOverlapRoxxComboSize " + TripleOverlapRoxx.get(i).get(j).size());
			for (int k = 0 ; k <TripleOverlapRoxx.get(i).get(j).size() ; k++) {
			RoiManager.getInstance().addRoi(TripleOverlapRoxx.get(i).get(j).get(k).getRoi());

			}
		}
	}
	
	
	
	////Interlude: adjusting doubleoverlap ArrayLists 
	
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
					System.out.println("This doubleroxx comb size " + DoubleCombRoxx.size());
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
						System.out.println("This comb size after " + DoubleCombRoxx.size());
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
	
	int countdownDouble = 0;
	int finalcountdownDouble = 0 ; 
	r = 0 ;
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
			//RoiManager.getInstance().reset();
			System.out.println("DoubleOverlapRoxxComboSize " + DoubleOverlapRoxx.get(i).get(j).size());
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
		ArrayList<Rox> newList = new ArrayList();
		SingleRoxx.add(newList);	
	}
	
	
	
	c=0;
	for (ArrayList<Rox> qRox :QuadOverlapRoxx) {
		//check quadoverlaproxx at this point
		//System.out.println("QuadOverlapRoxx" + c + " length " + qRox.size());
	c++;
	}
	
	c=0;
	for (ArrayList<ArrayList<Rox>> tripleList:TripleOverlapRoxx) {
		d=0;
		for (ArrayList tripleRox : tripleList) {
			//check tripleoverlaproxx at this point
			//System.out.println("TripleOverlapRoxx" + c + d + " size " + tripleRox.size());
		d++;
		}
	c++;
	}
	
	c=0;
	for (ArrayList<ArrayList<Rox>> doubleList:DoubleOverlapRoxx) {
		d=0;
		for (ArrayList doubleRox : doubleList) {
			//check doubleoverlaproxx at this point
			//System.out.println("DoubleOverlapRoxx" + c + d + " size " + doubleRox.size());
		d++;
		}
	c++;	
	}
	
	c=0;
	for (int i = 0 ; i < SingleRoxx.size(); i++) {
		//check singleroxx at this point
		//System.out.println("SingleRox" + c + " size " + SingleRoxx.get(i).size());
	c++;
	}
	
	ArrayList<Roi>allOverlapRoi = new ArrayList<Roi>();
	
	for (Rox roox : allOverlapRoxx) {
		allOverlapRoi.add(roox.getRoi());
	}
	
	//int allRoxSize = allRox.length;
	Roi[][] allRoi = new Roi[allRox.length][50];
	//Roi[][] allRoi = new Roi[2][2];
	
	//Make andfil an ArrayList for allOverlapIndexes
	
	ArrayList<Integer> allOverlapIndex = new ArrayList<Integer>();
	
	Iterator allOverIterator = allOverlapRoxx.iterator();
	
	while (allOverIterator.hasNext()) {
		allOverlapIndex.add(((Rox) allOverIterator.next()).getIndex());
	}
	
	for (int i=0; i < allRox.length ; i++) {
		if (channelSelection[i]==true) {	
			for (int j = 0 ; j < allRox[i].length ; j++) {
				//if ((!QuadOverlapRoxx.contains(allRox[i][j])) && (!TripleOverlapRoxx.contains(allRox[i][j])) && (!DoubleOverlapRoxx.contains(allRox[i][j]))){
				if (!allOverlapIndex.contains(allRox[i][j].getIndex())) {
					SingleRoxx.get(i).add(allRox[i][j]);
					System.out.println("i " + i + " j " + j);
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
		if (allOverlapRoxx!=null) {
			allOverlapRoxx.removeAll(allOverlapRoxx);
			allOverlapRoxx.clear();
		}

		totalOverlap = 0;
		
	}
			

	
	OverlapRoxx(){
		this.QuadOverlapRoxx = QuadOverlapRoxx;
		this.TripleOverlapRoxx = TripleOverlapRoxx;
		this.DoubleOverlapRoxx = DoubleOverlapRoxx;
		this.SingleRoxx = SingleRoxx;
		this.QuadInterRoxx = QuadInterRoxx;
		this.TripleInterRoxx = TripleInterRoxx;
		this.DoubleInterRoxx = DoubleInterRoxx;
		this.totalOverlap = totalOverlap;
	}	
	
}

	

