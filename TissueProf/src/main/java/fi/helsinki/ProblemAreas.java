package fi.helsinki;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.frame.RoiManager;


public class ProblemAreas {
		
	
	ArrayList<Roi> ProblemRectangles = new ArrayList<Roi>();
	
	ArrayList<ShapeRoi> quadComposites = new ArrayList<ShapeRoi>();
	
	ArrayList<ShapeRoi> TripleChannelComposites;
	
	ArrayList<ShapeRoi> DoubleChannelComposites;
	
	ArrayList<ShapeRoi> allClassComposites = new ArrayList<ShapeRoi>();
	
	ShapeRoi quadComposite;
	
	ShapeRoi tripleComposite;
	
	ShapeRoi doubleComposite;
	
	ArrayList<Rox> singleAllOverlapRoxx = new ArrayList<Rox>();
	
	ArrayList<Rox> doubleAllOverlapRoxx = new ArrayList<Rox>();
	
	ArrayList<Rox> tripleAllOverlapRoxx = new ArrayList<Rox>();
	
	ArrayList<Rox> quadAllOverlapRoxx = new ArrayList<Rox>();
	
	
	//ShapeRoi thisComposite;
	
	public void detectProblemAreas(OverlapRoxx OverlapRoxx, String OutputDir, String ZoneName, String ImageName, 
			LinkedHashMap<Rox, RoiData> RoxDataMap, double ratio) {
		
        Random random = new Random();
  	  
        int OpenCh = random.nextInt(ProcessImage.ImageChannelNo) + 1;
        
        IJ.open(OutputDir + "/" + ImageName + "_" + "EnhancedContrast" + "_" + "ZonesOnly" + "_C" + OpenCh + ".tif");
		
		System.out.println("Detecting problematic areas...");
		
		RoiManager.getRoiManager();
		
		//Make composites of all classes of overlap: double, triple, quad
		//Make new ROI sets for each class with all combos of that class in one set.
		
		if (OverlapRoxx.QuadOverlapRoxx!=null) {
		
			if (OverlapRoxx.QuadOverlapRoxx.get(0)!=null && OverlapRoxx.QuadOverlapRoxx.get(0).size()>0) {	
				
				for (int i = 0 ; i < 4 ; i++) {
					
					quadAllOverlapRoxx.add(OverlapRoxx.QuadOverlapRoxx.get(i).get(0));
					
					Roi thisCompositeRoi = OverlapRoxx.QuadOverlapRoxx.get(i).get(0).getRoi();
					ShapeRoi thisComposite = new ShapeRoi(thisCompositeRoi);
					
					for (int j = 1 ; j < OverlapRoxx.QuadOverlapRoxx.get(i).size() ; j++) {
						quadAllOverlapRoxx.add(OverlapRoxx.QuadOverlapRoxx.get(i).get(j));
						thisComposite = ((ShapeRoi) thisComposite.clone()).or(new ShapeRoi(OverlapRoxx.QuadOverlapRoxx.get(i).get(j).getRoi()));
					}
					
					quadComposites.add(thisComposite);
				
				}
				
				
				if (TripleChannelComposites != null ) {
					if (TripleChannelComposites.size()>0) {
						quadComposite = quadComposites.get(0);
						for (int i = 1 ; i < 4 ; i ++) {
							quadComposite = ((ShapeRoi) quadComposite.clone()).or(quadComposites.get(i));
						}
						
					}
				}
				
			}
			
		}	
		
		
		TripleChannelComposites = new ArrayList<ShapeRoi>();
		
		if (OverlapRoxx.TripleOverlapRoxx!=null) {
			for (ArrayList<ArrayList<Rox>> thisCombo : OverlapRoxx.TripleOverlapRoxx) {		
				if (thisCombo.get(0).size()>0) {
					for (ArrayList<Rox> thisChannel : thisCombo) {
						
						tripleAllOverlapRoxx.add(thisChannel.get(0));
						
						ShapeRoi thisChannelComposite = new ShapeRoi(thisChannel.get(0).getRoi());
						
						for (int i = 1 ; i < thisChannel.size() ; i++) {
							tripleAllOverlapRoxx.add(thisChannel.get(i));
							thisChannelComposite = ((ShapeRoi) thisChannelComposite.clone()).or(new ShapeRoi(thisChannel.get(i).getRoi()));
						}				
						
						TripleChannelComposites.add(thisChannelComposite);
						
					}
				}
			}
			
			if (TripleChannelComposites != null ) {
				if (TripleChannelComposites.size()>0) {
					tripleComposite = TripleChannelComposites.get(0);
			
			
			for (int i = 1 ; i < TripleChannelComposites.size() ; i++) {
				tripleComposite = ((ShapeRoi) tripleComposite.clone()).or(new ShapeRoi(TripleChannelComposites.get(i)));
			}
			
				}
			}
			
		}

		DoubleChannelComposites = new ArrayList<ShapeRoi>();
		
		
		if (OverlapRoxx.DoubleOverlapRoxx!=null) {
			for (ArrayList<ArrayList<Rox>> thisCombo : OverlapRoxx.DoubleOverlapRoxx) {
				
				if (thisCombo.get(0).size()>0) {		
					for (ArrayList<Rox> thisChannel : thisCombo) {
						
						doubleAllOverlapRoxx.add(thisChannel.get(0));
						
						ShapeRoi thisChannelComposite = new ShapeRoi(thisChannel.get(0).getRoi());
						
						for (int i = 1 ; i < thisChannel.size() ; i++) {
							doubleAllOverlapRoxx.add(thisChannel.get(i));
							thisChannelComposite = ((ShapeRoi) thisChannelComposite.clone()).or(new ShapeRoi(thisChannel.get(i).getRoi()));
						}				
						
						DoubleChannelComposites.add(thisChannelComposite);

					}
				}
			}
			
			if (DoubleChannelComposites != null ) {
				if (DoubleChannelComposites.size()>0) {
			
			doubleComposite = DoubleChannelComposites.get(0);
			
			for (int i = 1 ; i < DoubleChannelComposites.size() ; i++) {
				doubleComposite = ((ShapeRoi) doubleComposite.clone()).or(new ShapeRoi(DoubleChannelComposites.get(i)));
			}
			
				}
			}
			
		}
			
			
		if (OverlapRoxx.SingleRoxx!=null) {
			
			for (int i = 0 ; i < OverlapRoxx.SingleRoxx.size() ; i++){
				if (OverlapRoxx.SingleRoxx.get(i).size()>0) {
					for (int j = 0 ; j < OverlapRoxx.SingleRoxx.get(i).size() ; j++) {
						singleAllOverlapRoxx.add(OverlapRoxx.SingleRoxx.get(i).get(j));
					}
				}
			}
				
		}
			
			
		
		//Overlap all class ROI sets with composites above the class they belong in and mark the overlaps with rectangular ROis.
		//Save the problematic area rectangle ROI set.
		
		
		if (doubleComposite!=null) {
			allClassComposites.add(doubleComposite);
		}
		
		if (tripleComposite!=null) {
			allClassComposites.add(tripleComposite);
		}
			
		if (quadComposite!=null) {
			allClassComposites.add(quadComposite);
		}
		
		
		int c = 0 ; 
		for (ShapeRoi thisComposite : allClassComposites) {	
			if (singleAllOverlapRoxx.size()>0 && thisComposite != null) {
				for (Rox thisRox : singleAllOverlapRoxx) {
					
					ShapeRoi interShape = ((ShapeRoi) thisRox.shape.clone()).and(thisComposite);
					
					if (interShape.getBounds().getHeight()>0) {
						
						double RoxArea = ((RoiData) RoxDataMap.get(thisRox)).setArea(IJ.getImage());
						
						Roi interRoi = interShape.shapeToRoi();
						RoiData interData = new RoiData(IJ.getImage(), interRoi);
						interData.setIndex(0);
						Rox interRox = new Rox(interData); 
						
						double interArea = interData.setArea(IJ.getImage());
						double thisRatio = interArea/RoxArea;
					
						if (thisRatio>ratio) {
							Roi thisRectangle = Roi.create(interRox.getPosition()[0] -50, interRox.getPosition()[1] -50, 100, 100);
							ProblemRectangles.add(thisRectangle);
							double recX = interData.X-50;
							double recY = interData.Y-50;
							double recsX = thisRox.getPosition()[0]-50;
							double recsY = thisRox.getPosition()[1]-50;
						}
					}
				}
			}
		}
		
		c = 0;
		for (ShapeRoi thisComposite : allClassComposites) {
			if(doubleAllOverlapRoxx.size()>0 && thisComposite!=null && thisComposite!=doubleComposite) {
				for (Rox thisRox : doubleAllOverlapRoxx) {
					ShapeRoi interShape = ((ShapeRoi) thisRox.shape.clone()).and(thisComposite);	
					if (interShape.getBounds().getHeight()>0) {
						double RoxArea = ((RoiData) RoxDataMap.get(thisRox)).setArea(IJ.getImage());
						
						Roi interRoi = interShape.shapeToRoi();
						RoiData interData = new RoiData(IJ.getImage(), interRoi);
						interData.setIndex(0);
						Rox interRox = new Rox(interData);
							
						double interArea = interData.setArea(IJ.getImage());
						double thisRatio = interArea/RoxArea;
						
						if (thisRatio>ratio) {
							
							Roi thisRectangle = Roi.create(interRox.getPosition()[0] -50, interRox.getPosition()[1] -50, 100, 100);
							ProblemRectangles.add(thisRectangle);
							double recX = interData.X-50;
							double recY = interData.Y-50;
							double recsX = thisRox.getPosition()[0]-50;
							double recsY = thisRox.getPosition()[1]-50;
							c++; 
						}
					}
				}
			}
		}
			
		c=0;
		for (ShapeRoi thisComposite : allClassComposites) {
			if(tripleAllOverlapRoxx.size()>0 && thisComposite!=null && thisComposite!=doubleComposite && thisComposite!=tripleComposite) {
				for (Rox thisRox : tripleAllOverlapRoxx) {
					ShapeRoi interShape = ((ShapeRoi) thisRox.shape.clone()).and(thisComposite);
					if (interShape.getBounds().getHeight()>0) {
						
						double RoxArea = ((RoiData) RoxDataMap.get(thisRox)).setArea(IJ.getImage());
						
						Roi interRoi = interShape.shapeToRoi();
						RoiData interData = new RoiData(IJ.getImage(), interRoi);
						interData.setIndex(0);
						Rox interRox = new Rox(interData);
						
						double interArea = interData.setArea(IJ.getImage());
						double thisRatio = interArea/RoxArea;
						
						if (thisRatio>ratio) {
							Roi thisRectangle = Roi.create(interRox.getPosition()[0] -50, interRox.getPosition()[1] -50, 100, 100);
							ProblemRectangles.add(thisRectangle);
							double recX = interData.X-50;
							double recY = interData.Y-50;
							double recsX = thisRox.getPosition()[0]-50;
							double recsY = thisRox.getPosition()[1]-50;
							c++;
						}
					}
				}
			}
		}
		
		//reset ROIManager and save problematic are ROIs 
		RoiManager.getInstance().reset();
		
		if (ProblemRectangles.size()>0) {
			for (Roi thisRoi :ProblemRectangles) {
				RoiManager.getInstance().addRoi(thisRoi);
			}
			runStardist.saveRois(OutputDir, "ProblematicAreas" + ZoneName);
		}
		
		ProblemRectangles = new ArrayList<Roi>();
		
	}
}
