package fi.helsinki;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import java.util.LinkedHashMap;
import java.util.List;

public class Rox  {
	
	private Roi roi;
	private int index;
	private double area;
	private double mean;
	private double X;
	private double Y;
	private double[] position = {X,Y};
	public ShapeRoi shape;
	//private LinkedHashMap<List<Integer>, double> thisMap = new LinkedHashMap<List<Integer>, double>();
	private LinkedHashMap<List<Integer>, Double> ChIndexInterAreaMap = new LinkedHashMap<List<Integer>, Double>();
	private int channelSource;

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
	
	public Double getInterArea(List<Integer> ChIndexes){
		Double thisArea = ChIndexInterAreaMap.get(ChIndexes); 
		return thisArea;		
				
	}
	
	
	public int getChannelSource() {
		return channelSource;
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
	
	public void setInterArea(List<Integer> ChIndexes, double InterArea) {
		ChIndexInterAreaMap.put(ChIndexes, InterArea);	
	}
	
	public void setChannelSource(int ch) {
		this.channelSource = ch;
		
	}
	

}


