package fi.helsinki;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;

public class RoiData {
	
	public Roi dataroi;
	int index; // IMPORTANT: have to set an index before conversion to rox
	double area = 0 ;
	double mean = 0 ; 
	double X;
	double Y;
	
	//An object to measure data of ROIs as needed. Can be passed to a Rox object to create 
	//the basic element for all overlap and intensity analysis
	
	RoiData(ImagePlus imp, Roi roi){
		RoiManager newManager = RoiManager.getInstance();

		this.dataroi = roi;
		
		//newManager.select(newManager.getRoiIndex(roi));
		imp.setRoi(this.dataroi);
		ImageStatistics roi1Pos = ImageStatistics.getStatistics(imp.getProcessor(), Measurements.CENTROID, imp.getCalibration());
		
		this.X = roi1Pos.xCentroid/imp.getCalibration().pixelWidth;
		//System.out.println("X of this roi " + X);
		this.Y= roi1Pos.yCentroid/imp.getCalibration().pixelHeight;
		
		imp.killRoi();
		
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public double setArea(ImagePlus imp) {
		imp.setRoi(this.dataroi);
		imp.getProcessor().setRoi(this.dataroi);
		double roiArea = ImageStatistics.getStatistics(imp.getProcessor(), Measurements.AREA, imp.getCalibration()).area;
		this.area = roiArea;
		imp.killRoi();
		return roiArea;
	}
	
	
	
	public void setMean(ImagePlus imp) {
		double roiMean = ImageStatistics.getStatistics(imp.getProcessor(), Measurements.MEAN, imp.getCalibration()).mean;
		this.mean = roiMean;
	}
	
	
	/*
	public Roi getDataRoi() {
		return dataroi;		
	}
	*/
		
		
}
		