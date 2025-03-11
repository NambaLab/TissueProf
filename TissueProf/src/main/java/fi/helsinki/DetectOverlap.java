package fi.helsinki;

import java.util.Arrays;
import java.util.LinkedHashMap;

import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.frame.RoiManager;

public class DetectOverlap {
	
	static int c;
	static int r;

	static Roi channelCompositeRoi[] = new Roi[4];
	static ShapeRoi channelCompositeShape[]= new ShapeRoi[4];
	
	static ShapeRoi QuadInterComposite;
	static ShapeRoi[] TripleInterComposites = new ShapeRoi[4];
	static ShapeRoi[] DoubleInterComposites = new ShapeRoi[6];
	
	
	public static DetectOverlap detectOverlap(Rox[][] allRox, LinkedHashMap RoiRox, Boolean[] channelSelection, int channelSize){
		
		System.out.println("detecting... ");
		IJ.log("Detecting overlap...");
		
		RoiManager.getRoiManager();
		
		RoiManager.getInstance().reset();
		
		/*
		for (Rox[] channelRox : allRox) {
			System.out.println("ChannelRox length " + channelRox.length);
		}
		*/
		
		c=0;
		for (Rox[] channelRox: allRox) {
			if (channelSelection[c]==true && channelRox.length>0) {
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

			    channelCompositeRoi[c] = channelCompositeShape[c].shapeToRoi();
			    RoiManager.getInstance().reset();
			    RoiManager.getInstance().addRoi(channelCompositeShape[c].shapeToRoi());
			}
		c++;
		}
		
		if (channelSize>3) {
			if (channelCompositeShape[0]!=null && channelCompositeShape[1]!=null && channelCompositeShape[2]!=null && channelCompositeShape[3]!=null) {
				if (((ShapeRoi) channelCompositeShape[0].clone()).and(channelCompositeShape[1]).and(channelCompositeShape[2]).and(channelCompositeShape[3]).isArea()) {
					
					QuadInterComposite = ((ShapeRoi) channelCompositeShape[0].clone()).and(channelCompositeShape[1]).and(channelCompositeShape[2]).and(channelCompositeShape[3]);
					
				}
				else {System.out.println("No quadruple intersection");
				}
			}
		}
		
		int[] allRoxLengths = new int[4];
		int c=0;
		for (Rox[] roxy:allRox) {
			if (channelSelection[c]==false) {continue;}
			allRoxLengths[c] = roxy.length;
			c++;
		}
		
		int maxLength = Arrays.stream(allRoxLengths).max().getAsInt();

		r=0;
		for (int v=0; v<2; v++) {	
			for (int w=1; w<3; w++) {
				for (int y=2; y<4; y++) {
					if (v<w && w<y) {
						if (channelSelection[v] == true && channelSelection[w] == true && channelSelection[y] == true && channelCompositeShape[v]!=null &&
								channelCompositeShape[w]!=null && channelCompositeShape[y]!=null) {
							ShapeRoi channelVClone = (ShapeRoi) channelCompositeShape[v].clone();
							if (channelVClone.and(channelCompositeShape[w]).and(channelCompositeShape[y]).getBounds().height>0){
								ShapeRoi channelVClone2 = (ShapeRoi) channelCompositeShape[v].clone();
								ShapeRoi TripleInterComposite = channelVClone2.and(channelCompositeShape[w]).and(channelCompositeShape[y]);
								TripleInterComposites[r] = TripleInterComposite;
							}
						}
					r++;
					}
				}
			}
		}
		
		r=0;
		for (int v=0; v<3; v++) {
			for (int w=1; w<4; w++) {
				if (w>v) {
					if (channelSelection[v]==true && channelSelection[w]==true && channelCompositeShape[v]!=null && channelCompositeShape[w]!=null) {
						if (((ShapeRoi) channelCompositeShape[v].clone()).and(channelCompositeShape[w]).isArea()){
							ShapeRoi DoubleInterComposite = new ShapeRoi(((ShapeRoi) channelCompositeShape[v].clone()).and(channelCompositeShape[w]));
							DoubleInterComposites[r]=DoubleInterComposite;
						}
					}
				r++;
			}
			else {continue;}
		}
			
		}
		
		DetectOverlap nowDetect = new DetectOverlap(QuadInterComposite, TripleInterComposites, DoubleInterComposites /*QuadrupleInterRox, TripleInterRox, DoubleInterRox,*/);
		
		return nowDetect;
	}
	
	DetectOverlap(ShapeRoi QuadInterComposite, ShapeRoi[] TripleInterComposites, ShapeRoi[] DoubleInterComposites){
		this.QuadInterComposite = QuadInterComposite;
		this.TripleInterComposites = TripleInterComposites;
		this.DoubleInterComposites = DoubleInterComposites;
	}
	
	public static void clear() {
		
		channelCompositeRoi = new Roi[4];
		
		for (int i = 0 ; i < channelCompositeShape.length ; i++) {
			channelCompositeShape[i] = null;
		}
		
		channelCompositeShape = new ShapeRoi[4];
		
		for (int i = 0 ; i < TripleInterComposites.length ; i++) {
			TripleInterComposites[i]= null;
		}
		
		TripleInterComposites = new ShapeRoi[4];
		
		for (int i = 0 ; i < DoubleInterComposites.length ; i++) {
			DoubleInterComposites[i] = null;
		}
		
		DoubleInterComposites = new ShapeRoi[6];
		
	}
}

		
		
		
		
	
	
	
	
		
		
		
	

	

