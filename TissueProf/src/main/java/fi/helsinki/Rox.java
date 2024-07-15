package fi.helsinki;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

public class Rox  {
	
	private Roi roi;
	private int index;
	private double area;
	private double mean;
	private double X;
	private double Y;
	private double[] position = {X,Y};
	public ShapeRoi shape;


	private double[] pos(double X, double Y) {
		double[] coors = {X,Y};
		return coors;
	}
	
	//A supra-Roi object that in addition to the Roi contains the needed data for overlap and intensity analysis
	
	Rox(RoiData roiData) {
		this.setRoi(roiData.dataroi);
		this.setIndex(roiData.index);
		this.setArea(roiData.area);
		this.setMean(roiData.mean);
		this.setPosition(roiData.X, roiData.Y);
		this.shape = new ShapeRoi(roiData.dataroi);
	}
	
	
	public Roi getRoi() {
		return roi;
	}
	
	public int getIndex() {
		//System.out.println("index = " + index);
		return index;
	}
	
	public double getArea() {
		//System.out.println("area = " + area);
		return area;
	}
	
	public double getMean() {
		//System.out.println("mean = " + mean);
		
		return mean;
	}
	
	public double[] getPosition() {

		return position;

	}
	
	public void setRoi(Roi roi) {
		this.roi = roi;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setArea(double area) {
		this.area = area;
	}
	
	public void setMean(double mean) {
		
		this.mean = mean;
	}
	
	public void setPosition(double X, double Y) {
		this.X = X;
		this.Y = Y;
		this.position = pos(X,Y);
	}

	public void setShape() {
		roi = this.roi;
		this.shape = new ShapeRoi(roi);
	}
}


