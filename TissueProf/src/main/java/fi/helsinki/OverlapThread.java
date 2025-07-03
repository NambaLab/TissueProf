package fi.helsinki;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class OverlapThread extends Thread implements Callable<OverlapRoxx> {
	
	OverlapFilter overlapFilter; 
	LinkedHashMap<Rox,RoiData> roxDataMap; 
	Rox[][] allRox;
	int NextIndex;
	Boolean[] channelSelection;
	int channelSize;
	String inputDir2;
	String OutputDir;
	String imageName;
	double overlapThreshold;
	List<List<Integer>> nullDetections;
	//OverlapRoxx overlapThis;
	
    public OverlapThread(OverlapFilter OverlapFilter, Rox[][] allRox, LinkedHashMap<Rox, RoiData> RoxDataMap, int NextIndex, Boolean[] channelSelection, int channelSize, 
    		String inputDir2, String OutputDir, String imageName, double overlapThreshold, List<List<Integer>> nullDetections) {
        this.overlapFilter = OverlapFilter;
        this.roxDataMap = RoxDataMap;
        this.allRox = allRox;
        this.NextIndex = NextIndex; 
        this.channelSelection = channelSelection;
        this.channelSize = channelSize;
        this.inputDir2 = inputDir2;
        this.imageName = imageName;
        this.OutputDir = OutputDir;
        this.overlapThreshold = overlapThreshold;
        this.nullDetections = nullDetections;
    }
	
	
    @Override
    public OverlapRoxx call() throws Exception {
    	
        OverlapRoxx overlapThis = new OverlapRoxx();
        overlapThis.overlapRoxx(overlapFilter, allRox, roxDataMap, NextIndex, channelSelection, channelSize, inputDir2, OutputDir, imageName, overlapThreshold, nullDetections);        
        //Thread.currentThread().notifyAll();
    	return overlapThis;
    	
    	
    	
    }
	
}
