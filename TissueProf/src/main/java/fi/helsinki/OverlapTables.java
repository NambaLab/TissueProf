package fi.helsinki;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fi.helsinki.OverlapRoxx.ComboOverlapRoxx;
import ij.IJ;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

public class OverlapTables {
	HSSFWorkbook wb;
	String OutputDir;
	String ImageName;
	String ZoneName;

	
	OverlapTables(String OutputDir, String ImageName, String inputDir, OverlapRoxx overlapRoxx,  Roi[] backgroundRois,
			int channelSize, Boolean[] channelSelection, String[] channelNames, String zoneName, boolean measureIntensity){
		
		
		this.setOutputDir(OutputDir);
		this.setImageName(ImageName);
		this.setZoneName(zoneName);
		
		wb = new HSSFWorkbook();
		
		new CountsTable(overlapRoxx, channelNames);
		new CountsInfoTable(overlapRoxx, channelNames);
		
		
	}
	
	private String getOverlapClassName(List<Integer> chIndexes, String[] channelNames){
		
		
		System.out.println("ChannelName length in getOverlapClasName " + channelNames.length );
		System.out.println("chIndexes size " + chIndexes.size());
		
		String overlapClassName = channelNames[chIndexes.get(0)] + "+";
		
		for (int i = 1 ; i < chIndexes.size() ; i++) {
			
			String thisChannelName = channelNames[chIndexes.get(i)] + "+";
			
			overlapClassName = String.join("|", overlapClassName, thisChannelName); 
			//String.join
			
		}
		
		System.out.println("OverlapClassName = " + overlapClassName);
		
		return overlapClassName;
		
	}
	
	
	
	public void save(){
		
		FileOutputStream fileOut;
		
		boolean excelClosed = false;
		
		while (excelClosed == false) {
			try {
				System.out.println("Saving : " + OutputDir + "/" + ImageName + "_" + ZoneName + "_Results.xls" );
				fileOut = new FileOutputStream(OutputDir + "/" + ImageName + "_" + ZoneName + "_Results.xls");
				wb.write(fileOut);
				fileOut.close();
				excelClosed = true;
				
			} catch (IOException e) {
				WaitForUserDialog excelClose = new WaitForUserDialog("Close results excel files for the same image if any are open");
				excelClose.show();		
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	private void setOutputDir(String outputDir) {
		this.OutputDir = outputDir;
	}
	private void setImageName(String imageName) {
		this.ImageName = imageName;
	}
	private void setZoneName(String zoneName) {
		this.ZoneName = zoneName;
	}
	
	
	public class CountsTable {
		
		int currentColumn;
		Sheet Counts = wb.createSheet();
		
		CountsTable(OverlapRoxx overlapRoxx, String[] channelNames){
			this.setCurrentColumn(0);
			this.makeCountsTable(overlapRoxx,channelNames);
			
		}
		
		public void makeCountsTable(OverlapRoxx overlapRoxx, String[] channelNames) {
			
			
			Row[] countRows = new Row[2];	
					
			countRows[0] = Counts.createRow(0);
			countRows[1] = Counts.createRow(1);
			
			for (ComboOverlapRoxx thisCombo : overlapRoxx.getComboOverlapRoxxes()) {
				addCount(thisCombo, channelNames, currentColumn, countRows);
			}
			
			//Check if table is made properly
			String[] names = new String[currentColumn];
 			int[] counts = new int[currentColumn];
 			
			for (int i = 0 ; i < currentColumn ; i++) {
				
				names[i] = countRows[0].getCell(i).getStringCellValue(); 
				counts[i] = (int) countRows[1].getCell(i).getNumericCellValue();
				
				System.out.println("name : " + names[i] + " count : " + counts[i]);
				
			}
			
		}
	
		private void addCount(ComboOverlapRoxx comboOverlapRoxx, String[] channelNames, int currentColumn, Row[] rows) {
			
			Cell nameCell = rows[0].createCell(currentColumn); 
			Cell countCell = rows[1].createCell(currentColumn);
			
			nameCell.setCellValue(getOverlapClassName(comboOverlapRoxx.getChIndexes(), channelNames));
			countCell.setCellValue(comboOverlapRoxx.getOverlapCount());
			this.setCurrentColumn(currentColumn + 1);
		}
		
		private void setCurrentColumn(int currentColumn) {
			this.currentColumn = currentColumn;
		}
		
		private int getCurrentColumn() {
			return currentColumn;
		}
	
	}
	
	public class CountsInfoTable{
		
		int currentColumn;
		int currentRow;
		Sheet CountsInfo = wb.createSheet();
		
		CountsInfoTable(OverlapRoxx overlapRoxx, String[] channelNames){
			this.setCurrentColumn(0);
			this.setCurrentRow(0);
			this.makeCountsInfoTable(overlapRoxx,channelNames);
			
			
		}
		
		
		
		public void makeCountsInfoTable(OverlapRoxx overlapRoxx, String[] channelNames) {
			
			int[] ComboOverlapSizes = new int[overlapRoxx.getComboOverlapRoxxes().size()];
			for (int i = 0 ; i < ComboOverlapSizes.length ; i++) {
				System.out.println(overlapRoxx.getComboOverlapRoxxes().get(i).getOverlapCount());
				ComboOverlapSizes[i] = overlapRoxx.getComboOverlapRoxxes().get(i).getOverlapCount();
			}
			
	        int maxSize = Arrays.stream(ComboOverlapSizes).max().getAsInt();
	        
	        System.out.println("Max size " + maxSize);
	        
	        Row[] inRows = new Row[maxSize];
	        
	        for (int i = 0 ; i < maxSize ; i++) {
	        	inRows[i] = CountsInfo.createRow(i);
	        }
	        
	        int lastComboSize = 0;
	        for (ComboOverlapRoxx thisComboOvRoxx : overlapRoxx.getComboOverlapRoxxes() ) {

	        	System.out.println("current combo size : " + thisComboOvRoxx.getComboSize());
	        	System.out.println("last combo size : " + lastComboSize);
	        	
	        	if (lastComboSize!=0) {
		        	if (thisComboOvRoxx.getComboSize()-lastComboSize!=0) {
		        		setCurrentColumn(getCurrentColumn()+2);
		        	}
		        	else {
		        		setCurrentColumn(getCurrentColumn()+1);
		        	}
	        	}
	        	
	        	lastComboSize = thisComboOvRoxx.getComboSize();
	        	makeInfoModule(thisComboOvRoxx, channelNames, getCurrentColumn(), getCurrentRow(), inRows);
	        		
	        }
	        
		}
		
		private void makeInfoModule(ComboOverlapRoxx comboOverlapRoxx, String[] channelNames, int currentColumn, int currentRow, Row[] inRows) {
			setCurrentRow(0);
			List<Integer> ChIndexes = comboOverlapRoxx.getChIndexes();
			setModuleChNames(ChIndexes, channelNames, currentColumn, currentRow, inRows);
			//comboOverlapRoxx.getOverlapRoxx().get(0).
			
			
			
			
		}
		
		private void setModuleChNames(List<Integer> ChIndexes, String[] channelNames, int currentColumn, int currentRow, Row[] inRows) {
			

			for (Integer Ch : ChIndexes) {
				inRows[currentRow].createCell(getCurrentColumn()).setCellValue(channelNames[Ch]);
				this.setCurrentColumn(getCurrentColumn() + 1);
			}
			if (ChIndexes.size()!=1) {
				inRows[currentRow].createCell(getCurrentColumn()).setCellValue("Cell Index");	
				this.setCurrentColumn(getCurrentColumn() + 1);
			}
		}
		
		private void setCurrentColumn(int currentColumn) {
			this.currentColumn = currentColumn;
		}
		
		private int getCurrentColumn() {
			return currentColumn;
		}
		private void setCurrentRow(int currentRow) {
			this.currentRow = currentRow;
		}
		private int getCurrentRow() {
			return currentRow;
		}
		
		
		
	}

	
	public class IntensityTable{
			
		public void makeIntensityTable() {
			
		}
	}
	
	
	
	
	
	
	
	
}
