package fi.helsinki;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;


//import loci.poi.hssf.usermodel.HSSFDataFormat;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.DataFormat;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;


public class OverlapTables {
	
	static int[] channelInts;
	
	public static void makeTables(String OutputDir, String ImageName, OverlapRoxx OverlapRoxx, String inputDir2, 
			String imageName, Roi[] backgroundRois, int channelSize, Boolean[] channelSelection, String[] channelNames, 
			String zoneName, boolean measureIntensity) throws IOException{
		
		System.out.println("Making Tables");
		IJ.log("Measuring intensities and making results tables...");
		
		HSSFWorkbook wb = new HSSFWorkbook();
		Sheet Counts = wb.createSheet("Counts");
		Sheet CountsInfo = wb.createSheet("CountsInfo");
		//Sheet Intensities = wb.createSheet("Intensities");
		
		Sheet IntensitiesBin;
		
		IntensitiesBin = wb.createSheet("Intensities");
		
		CellStyle style;
		HSSFDataFormat format1 = wb.createDataFormat();
		Row row;
		Cell cell;
		Cell cell1;
		
		System.out.println(OverlapRoxx.QuadOverlapRoxx.size());
			
		int QuadCount = OverlapRoxx.QuadOverlapRoxx.get(0).size();
			
			String[] Channels = channelNames;
			
			if (channelSize==3) {
				channelInts = new int[3];
				int n =0;
				for ( int i = 0 ; i < 4 ; i++) {
					if (channelSelection[i]==true) {
						channelInts[n]=i;
						n++;
					}
				}
			} else if (channelSize==2) {
				channelInts = new int[2];
				int n =0;
				for ( int i = 0 ; i < 4 ; i++) {
					if (channelSelection[i]==true) {
						channelInts[n]=i;
						n++;
					}
				}
			}
			
			//Keep track of counts
			/*
			System.out.println("After ChannelInts");
			System.out.println("Quad size " + QuadCount);
			System.out.println("channelsize " + channelSize);
			*/
			
			Row row1 = Counts.createRow(0);
			Row row2 = Counts.createRow(1);
			
			if (channelSize>3) {
				cell = row1.createCell(0);
				cell.setCellValue(Channels[0] + "+/" + Channels[1] + "+/" + Channels[2] + "+/" + Channels[3] + "+");
				cell1 = row2.createCell(0);
				cell1.setCellValue(QuadCount);	
			}
			
			
			ArrayList<ArrayList<String>> TripleCombCols = new ArrayList<ArrayList<String>>();
			for (int i = 0 ; i < 4 ; i ++) {
				ArrayList<String> thisList = new ArrayList<String>();
				TripleCombCols.add(thisList);
			}
			
			ArrayList<String> TripleCombColsName = new ArrayList<String>();
			ArrayList<String> DoubleCombName = new ArrayList<String>();
			
			int c = 0 ; 
			if (channelSize>3) {
				for (int v = 0 ; v < 4 ; v ++) {
					for (int w = 1 ; w < 4 ; w++ ) {
						for (int y = 2 ; y < 4 ; y++) {
							if (v<w && w<y) {
								TripleCombCols.get(c).add(Channels[v]);
								TripleCombCols.get(c).add(Channels[w]);
								TripleCombCols.get(c).add(Channels[y]);
								TripleCombColsName.add(TripleCombCols.get(c).get(0) + "+/" + TripleCombCols.get(c).get(1) + "+/" + TripleCombCols.get(c).get(2) + "+");
								//TripleCombColsName[c] = TripleCombCols[0] + "+/" + TripleCombCols[1] + "+/" + TripleCombCols[2];
								//System.out.println(TripleCombColsName[c]);
								//System.out.println(TripleCombColsName[c].getClass());
								c++;
							}
						}
					}
				}
			}
			else {
	 			if (channelSize==3) {
	 				int ix = 0 ;
	 				int p = 0 ; 
	 				int type = 0;
					for (int i = 0 ; i < 4 ; i ++) {
					if (channelSelection[i]==true && i == 0 ) {
						//TripleCombCols.get(0).add(Channels[i]);
						if (channelSelection[i+1]==true) {
							if (channelSelection[i+2]==true) {
								type = 0 ; 
							} 
							else {
								if (channelSelection[i+2]==false) {
									type = 1;
								}
							}
						}
						else {
							if (channelSelection[i+1]==false) {
								type = 2;
							}
						}
						}
						else {
							if (i!=3 && i!=0) {
								if (channelSelection[i]==true && channelSelection[i+1]==true && channelSelection[i-1] == false && i == 1) {
									//System.out.println("IIIII " + i);
									type = 3;
								}
							}
						}
						//System.out.println("TYPE " + type);
						//System.out.println("i " + i);
						if (i==3) {	
							System.out.println("About to make triplecombocols and names, i =  " + i);
							TripleCombCols.get(type).add(Channels[channelInts[p]]);
							TripleCombCols.get(type).add(Channels[channelInts[p+1]]);
							TripleCombCols.get(type).add(Channels[channelInts[p+2]]);
							TripleCombColsName.add(TripleCombCols.get(type).get(0) + "+/" + TripleCombCols.get(type).get(1) + "+/" + TripleCombCols.get(type).get(2) + "+");
						}
					}
				} 
			}
			
			//System.out.println("After TripleCombCols");

			
			try {
			if (channelSize>3) {
				for(int i = 1 ; i < 5 ; i++) {
					int a = i-1;
					cell = row1.createCell(i);
					//System.out.println(TripleCombColsName[a].getClass());
					cell.setCellValue(TripleCombColsName.get(a));	
				}
			}
			else {
				if (channelSize==3) {
					cell = row1.createCell(0);
					cell.setCellValue(TripleCombColsName.get(0));
				}
			}
			
			}catch(Exception e ) {
				e.printStackTrace();
			}
			
			//row = Counts.createRow(1);
			
			c=0;
			if (channelSize>3) {
				for (ArrayList<ArrayList<Rox>> thisCombChannel : OverlapRoxx.TripleOverlapRoxx) {
					//assert thisCombChannel.get(0).size()==thisCombChannel.get(1).size();
					cell = row2.createCell(c+1);
					cell.setCellValue(thisCombChannel.get(0).size());
					c++;
				}
			}
			else {
				if (channelSize==3){
					for (ArrayList<ArrayList<Rox>> thisCombChannel : OverlapRoxx.TripleOverlapRoxx) {
						//assert thisCombChan	nel.get(0).size()==thisCombChannel.get(1).size();
						if (thisCombChannel.size()>0 && (thisCombChannel.get(0).size()>0 || thisCombChannel.get(1).size()>0 || 
								thisCombChannel.get(2).size()>0)) {
						int thisq = 0;
						for (int q = 0 ; q < thisCombChannel.size() ; q++) {
							if (thisCombChannel.get(q).size()>0) {
								thisq=q;
								cell = row2.createCell(0);
								cell.setCellValue(thisCombChannel.get(thisq).size());
							}
						}
						c++;
						}
					}
				}
			}	
			
			if (row2.getCell(0).getNumericCellValue()>0) {c++;}
			//}
			ArrayList<ArrayList<String>> DoubleCombCols = new ArrayList<ArrayList<String>>();
			for (int i = 0 ; i < 6 ; i ++) {
				ArrayList<String> thisList = new ArrayList<String>();
				DoubleCombCols.add(thisList);
			}
				
		
			ArrayList<String> DoubleCombColsName = new ArrayList<String>();
			
			int dub=0;
			for (int v = 0 ; v < 4 ; v++) {
				for (int w = 1 ; w < 4 ; w++) { 
					if (v<w) {
						DoubleCombCols.get(dub).add(Channels[v]);
						DoubleCombCols.get(dub).add(Channels[w]);
						DoubleCombColsName.add(DoubleCombCols.get(dub).get(0) + "+/" + DoubleCombCols.get(dub).get(1) + "+");
					dub++;
					}
				}
			}
			
			//System.out.println("After double combcolnames");
			
			int dtype = 0;
			
			if (channelSize>3) {
				for(int i = 5 ; i < 11 ; i++) {
					int a = i-5;
					cell = row1.createCell(i);
					//System.out.println(TripleCombColsName[a].getClass());
					cell.setCellValue(DoubleCombColsName.get(a));	
				}
			} 
			else {
				if (channelSize==3) {
					if (channelInts[0]==0 && channelInts[1]==1 && channelInts[2]==2) {
						cell = row1.createCell(2);
						cell.setCellValue(DoubleCombColsName.get(0));
						Cell cell2 = row1.createCell(3);
						cell2.setCellValue(DoubleCombColsName.get(1));
						Cell cell3 = row1.createCell(4);
						cell3.setCellValue(DoubleCombColsName.get(3));
					} else {
						if (channelInts[0]==0 && channelInts[1]==1 && channelInts[2]==3) {
							row1.createCell(2).setCellValue(DoubleCombColsName.get(0));
							row1.createCell(3).setCellValue(DoubleCombColsName.get(2));
							row1.createCell(4).setCellValue(DoubleCombColsName.get(4));
						}
						else {
							if (channelInts[0]==0 && channelInts[1]==2 && channelInts[2]==3) {
								row1.createCell(2).setCellValue(DoubleCombColsName.get(1));
								row1.createCell(3).setCellValue(DoubleCombColsName.get(2));
								row1.createCell(4).setCellValue(DoubleCombColsName.get(5));
							}
							else {
								if (channelInts[0]==1 && channelInts[1]==2 && channelInts[2]==3) {
									row1.createCell(2).setCellValue(DoubleCombColsName.get(3));
									row1.createCell(3).setCellValue(DoubleCombColsName.get(4));
									row1.createCell(4).setCellValue(DoubleCombColsName.get(5));
								}
								
							}
						}
					}
				} else if (channelSize==2) {
					
					int q = 0 ;
					
					if (channelSelection[0]==true && channelSelection[1]==true) {
						dtype = 0; 
					}else if (channelSelection[0]==true && channelSelection[2]==true) {
						dtype = 1;
					}else if (channelSelection[0]==true && channelSelection[3]==true) {
						dtype = 2;
					}else if (channelSelection[1]==true && channelSelection[2]==true) {
						dtype = 3;
					}else if (channelSelection[1]==true && channelSelection[3]==true) {
						dtype = 4;
					}else if (channelSelection[2]==true && channelSelection[3]==true) {
						dtype = 5;
					}
					
					row1.createCell(c).setCellValue(DoubleCombColsName.get(dtype));
					
				}
			}
			
			
			c=0;
			
			//row = Counts.createRow(1);
			if (channelSize>3) {
				c=0;
				for (ArrayList<ArrayList<Rox>> thisCombChannel : OverlapRoxx.DoubleOverlapRoxx) {
					//assert thisCombChannel.get(0).size()==thisCombChannel.get(1).size();
					cell = row2.createCell(c+5);
					//System.out.println("comboChannel 0 " + thisCombChannel.get(0).size());
					//System.out.println("comboChannel 1 " + thisCombChannel.get(1).size());
					cell.setCellValue(thisCombChannel.get(0).size());
					c++;
				}
			} else {
				if (channelSize==3) {
					if (channelInts[0]==0 && channelInts[1]==1 && channelInts[2]==2) {
						row2.createCell(c+2).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(0).get(0).size());
						row2.createCell(c+3).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(1).get(0).size());
						row2.createCell(c+4).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(3).get(0).size());
					} else {
						if (channelInts[0]==0 && channelInts[1]==1 && channelInts[2]==3) {
							row2.createCell(c+2).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(0).get(0).size());
							row2.createCell(c+3).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(2).get(0).size());
							row2.createCell(c+4).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(4).get(0).size());
						}
						else {
							if (channelInts[0]==0 && channelInts[1]==2 && channelInts[2]==3) {
								row2.createCell(c+2).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(1).get(0).size());
								row2.createCell(c+3).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(2).get(0).size());
								row2.createCell(c+4).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(5).get(0).size());
							}
							else {
								if (channelInts[0]==1 && channelInts[1]==2 && channelInts[2]==3) {
									row2.createCell(c+2).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(3).get(0).size());
									row2.createCell(c+3).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(4).get(0).size());
									row2.createCell(c+4).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(5).get(0).size());
								}
							}
						}
					}
				} else if (channelSize==2) {
					row2.createCell(c).setCellValue(OverlapRoxx.DoubleOverlapRoxx.get(dtype).get(0).size());
				}
			}
			
			if (channelSize>3) {
				for (int i = 11 ; i < 15 ; i++ ) {
					int a = i-11;
					cell = row1.createCell(i);
					cell.setCellValue(Channels[a]);	
				}
			}
			else {
				if(channelSize==3) {
					int p = 0 ;
					for (int i = 0 ; i < channelSize ; i++) {
						row1.createCell(p+6).setCellValue(channelNames[channelInts[i]]);
						row2.createCell(p+6).setCellValue(OverlapRoxx.SingleRoxx.get(channelInts[i]).size());
						p++;
					}
				}
				else if (channelSize==2) {
					
					row1.createCell(3).setCellValue(channelNames[channelInts[0]]);
					row2.createCell(3).setCellValue(OverlapRoxx.SingleRoxx.get(channelInts[0]).size());
					
					row1.createCell(4).setCellValue(channelNames[channelInts[1]]);
					row2.createCell(4).setCellValue(OverlapRoxx.SingleRoxx.get(channelInts[1]).size());
					
				}
				
			}
			
			if (channelSize>3) {
				c=11;
				for (ArrayList<Rox> thisSingleChannel:OverlapRoxx.SingleRoxx) {
					cell = row2.createCell(c);
					cell.setCellValue(thisSingleChannel.size());
					c++;
				}
			}
					
		c=0;
		
		Row countRow = Counts.getRow(1);
		Iterator cellIterate = countRow.cellIterator();
		
		int[] allSizes = new int[15];
		
			
		int f = 0 ; 
		while (cellIterate.hasNext()) {
			//cellIterate.next().
			int thisSize =(int) ((Cell) cellIterate.next()).getNumericCellValue();
			 allSizes[f] = thisSize;
			 f++;
		}
			
		int maxSize = 0 ; 
		
        maxSize = Arrays.stream(allSizes).max().getAsInt();
		
		//System.out.println("Max Size " + maxSize);
		
		
		Row[] cellRows = new Row[maxSize+5];
		
		for (int i = 1 ; i < cellRows.length ; i ++) {
			cellRows[i-1] = CountsInfo.createRow(i);
		}
		
		Row nameRow = CountsInfo.createRow(0);
		
		/*
		System.out.println("Quadoverlaproxxes0 " + OverlapRoxx.QuadOverlapRoxx.get(0).size());
		System.out.println("Quadoverlaproxxes1 " + OverlapRoxx.QuadOverlapRoxx.get(1).size());
		System.out.println("Quadoverlaproxxes2 " + OverlapRoxx.QuadOverlapRoxx.get(2).size());
		System.out.println("Quadoverlaproxxes3 " + OverlapRoxx.QuadOverlapRoxx.get(3).size());
		*/
		
		if (channelSize>3) {
			if (OverlapRoxx.QuadOverlapRoxx.get(0).size()>0) {
				c=0;
				for (ArrayList<Rox> thisOverlapQuad : OverlapRoxx.QuadOverlapRoxx) {
					nameRow.createCell(c).setCellValue(Channels[c]);
					for (int i = 0 ; i < thisOverlapQuad.size(); i++) {
						cellRows[i].createCell(c).setCellValue(thisOverlapQuad.get(i).getIndex());
					}
					c++;
					for(int i = 0 ; i < thisOverlapQuad.size(); i++) {
					cellRows[i].createCell(c).setCellValue(OverlapRoxx.QuadInterRoxx.get(i).getIndex());
					}
				}
				nameRow.createCell(c).setCellValue("Cell Index");
			}
		}
		
		synchronized(OverlapRoxx.TripleOverlapRoxx) {
			int r = 0 ; 
			for (ArrayList<ArrayList<Rox>> thisCombo : OverlapRoxx.TripleOverlapRoxx) {
				synchronized(thisCombo) {
					if (r==0 && thisCombo.size()!=3) {
						thisCombo.remove(3);
					}
					else if (r==1 && thisCombo.size()!=3) {
						thisCombo.remove(2);
					}
					else if (r==2 && thisCombo.size()!=3) {
						thisCombo.remove(1);
						
					}
					else if (r==3 && thisCombo.size()!=3) {
						thisCombo.remove(0);
					}
				}
			}
		}
		
		
		
		int cnow = 0 ; 
		if (channelSize>3) {cnow = c+2;
		}
		else if (channelSize==3) {
			cnow = c;}
		else if (channelSize==2) {
			cnow = 0;
		}
		
		
		int d = 0 ; 
		int dcount = 0 ;
		int m = 0 ; 
		int t = 0 ;
		
		for (ArrayList<ArrayList<Rox>> thisCombo : OverlapRoxx.TripleOverlapRoxx) {
			//System.out.println("thisComboSize " + thisCombo.size());
			
			if (thisCombo.get(0).size()>0) {		
				int ch = 0 ; 
				System.out.println(thisCombo.get(ch).size());

				try {
					for (ArrayList<Rox> thisChannel : thisCombo) {

						nameRow.createCell(t + cnow+ch+dcount*3).setCellValue(TripleCombCols.get(d).get(ch));
						//System.out.println("d " + d + " ch " + ch);
						for (int i = 0 ; i < thisChannel.size() ; i++) {
							cellRows[i].createCell(t + cnow + ch + dcount*3).setCellValue(thisChannel.get(i).getIndex());
						}				
						ch++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					//System.out.println("ch " + ch + " " + d);
				}
				
				nameRow.createCell(t + cnow+ch+dcount*3).setCellValue("Cell Index");
				for (int i = 0 ; i < OverlapRoxx.TripleInterRoxx.get(d).size() ; i++) {
					cellRows[i].createCell(t + cnow + ch + dcount*3).setCellValue((OverlapRoxx.TripleInterRoxx.get(d).get(i)).getIndex());
				}
				dcount++;  
				t++;
				t++;
				c++;
			}
			d++;
			c++;
		}
		
		
			cnow = c + t + cnow;
			d = 0 ; 
			dcount = 0 ;
			t=0;
			
			if (channelSize==2) {
				c=0;
				cnow = 0;
			}
			
			int chdub = 0 ; 
			
			for (ArrayList<ArrayList<Rox>> thisCombo : OverlapRoxx.DoubleOverlapRoxx) {
				//System.out.println("thisComboSize " + thisCombo.size());
				
				int ch = 0 ; 
				if (thisCombo.get(0).size()>0) {
					System.out.println(thisCombo.get(ch).size());
					for (ArrayList<Rox> thisChannel : thisCombo) {

						nameRow.createCell(cnow+ch+dcount*3 + t).setCellValue(DoubleCombCols.get(chdub).get(ch));

						//System.out.println("d " + d + " ch " + ch);
						for (int i = 0 ; i < thisChannel.size() ; i++) {
							//System.out.println(thisChannel.size());
							try {
							cellRows[i].createCell(cnow + ch +dcount*3 + t).setCellValue(thisChannel.get(i).getIndex());
							} catch (NullPointerException e) {
								e.printStackTrace();
							}
							c= cnow + ch + dcount*3 + t;
						}				
						ch++;	
					}
					nameRow.createCell(cnow + ch + dcount*3 + t).setCellValue("Cell Index");
					for (int i = 0 ; i < OverlapRoxx.DoubleInterRoxx.get(chdub).size() ; i++) {
						try {
						cellRows[i].createCell(cnow + ch +dcount*3 + t).setCellValue(OverlapRoxx.DoubleInterRoxx.get(chdub).get(i).getIndex());
						//System.out.println("Cell Index " + OverlapRoxx.DoubleInterRoxx.get(chdub).get(i).getIndex());
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}	
					dcount++;
					t++;
				}
				chdub++;
				c++;
				c= cnow + dcount*3 + t;
			}
		
			
		cnow = c;
		d = 0 ; 
		dcount = 0 ;
		//OverlapRoxx.SingleRoxx.get(0).get(0).getIndex();
		
		for (ArrayList<Rox> thisSingle : OverlapRoxx.SingleRoxx) {
			if (thisSingle.size()>0) {
				nameRow.createCell(dcount + cnow + d).setCellValue(Channels[d]);
				int it = 0 ;
				for (int i = 0 ; i < thisSingle.size(); i++) {
					//Row cellRow = CountsInfo.createRow(i+1);
					Cell newcell = cellRows[i].createCell(dcount + cnow + d);
					it++;	
					newcell.setCellValue(thisSingle.get(i).getIndex());
					//thisOverlapQuad.get(i)
				}
				dcount++;
				c++;
			}                                                                       
			d++;
		}
		
		
		
		
		////Intensity Table
		for (int a = 1 ; a < 5 ; a++) {
			//IJ.open(OutputDir + "\\" + imageName + "_" + "OriginalDuplicate-" + "C" + a + ".tif");
			IJ.open(OutputDir + "/" + imageName + "_" + "OriginalDuplicate-" + "C" + a + ".tif");
			String thisName = WindowManager.getActiveWindow().getName();

			if (thisName.endsWith("\\")){
				int backindex = thisName.lastIndexOf("\\");
				thisName = thisName.substring(0, backindex);
				WindowManager.getActiveWindow().setName(thisName);
			}
			
		}
		
		if (measureIntensity == true) {
		
			Row RoiRow = IntensitiesBin.createRow(0);
		
			Row IntNamesRow = IntensitiesBin.createRow(1);
			
			int firstcol = 6;
			for (int i = 0 ; i < 2 ; i++) {
				int chc = 0 ; 
				if (i==1) {firstcol = 29;}
				int currentcol = firstcol; 
				for (int j = 0 ; j < 4 ; j++) {
					RoiRow.createCell(currentcol).setCellValue(Channels[j]);
					if (i==0) {	
						for (int k = currentcol ; k < currentcol+4 ; k++) {
							IntNamesRow.createCell(k).setCellValue(Channels[k-currentcol] + " Intensity");
						
						}
					} else if (i==1 ) {
						for (int k = currentcol ; k < currentcol+4 ; k++) {
							IntNamesRow.createCell(k).setCellValue(Channels[k-currentcol] + " -background");
						}
					}
					currentcol = currentcol + 5;
				}
			}	
			

			IntNamesRow.createCell(0).setCellValue("Cell Index");
			
			for (int i = 1 ; i < 5 ; i ++ ) {
				IntNamesRow.createCell(i).setCellValue(Channels[i-1]);
			}
			
			IntNamesRow.createCell(26).setCellValue("Backgrounds");
			
			Row[] intcellRows = new Row[OverlapRoxx.totalOverlap + 10];
				
			for (int i = 2 ; i < intcellRows.length ; i ++) {
				intcellRows[i-2] = IntensitiesBin.createRow(i);
			}
			
			double[][] backgrounds = new double[4][3];
			double[] finalbackgrounds= new double[4];
			for (int i = 0 ; i < 4 ; i++) {
				int a = i + 1;
				//inputDir2 + "\\" + 
				
				for (int j = 0 ; j < 3  ; j ++ ) {
					IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + a + ".tif");
					IJ.getImage().setRoi(backgroundRois[j]);
					backgrounds[i][j] = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
				}
				
				finalbackgrounds[i] = (backgrounds[i][0] + backgrounds[i][1] + backgrounds[i][2])/3;
				
				intcellRows[i].createCell(26).setCellValue(Channels[i] + " background");
				intcellRows[i].createCell(27).setCellValue(finalbackgrounds[i]);	
			}
			
			
			
			
			d=0;
			
			if (OverlapRoxx.QuadInterRoxx.size()>0) {
				for (Rox thisRox:OverlapRoxx.QuadInterRoxx) {
					intcellRows[d].createCell(0).setCellValue(thisRox.getIndex());
					intcellRows[d].createCell(1).setCellValue(1);
					intcellRows[d].createCell(2).setCellValue(1);
					intcellRows[d].createCell(3).setCellValue(1);
					intcellRows[d].createCell(4).setCellValue(1);
					
					for (int i = 0 ; i < 4 ; i++) {
					
						int a = i + 2;
						int cha= i +1 ;
						//6, 11, 16, 21
						if (i==0) { a = i +2;}
						if (i==1) { a = i + 6;}
						if (i==2) { a = i + 10;}
						if (i==3) { a = i + 14;}
							for (int j = 0 ; j < 4 ; j ++) {
							int w = j + 1 ;
							IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
							//ImagePlus imp = IJ.getImage();
							IJ.getImage().setRoi((Roi) OverlapRoxx.QuadOverlapRoxx.get(i).get(d).getRoi());
							double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
							intcellRows[d].createCell(a+ j + 4).setCellValue(channelMean);
							intcellRows[d].createCell(a+ j + 27).setCellValue(channelMean-finalbackgrounds[i]);
							IJ.getImage().killRoi();
							}
					}
					d++;
				}
			}
			
					
			boolean tripleExists = true;
			
			ArrayList<ArrayList<Integer>> tripleintcombs = new ArrayList<ArrayList<Integer>>();
			for (int i = 0 ; i < 4 ; i++) {
				ArrayList<Integer> thisList = new ArrayList<Integer>();
				tripleintcombs.add(thisList);
			}
			
			c=0;
			for (int v = 0 ; v < 4 ; v ++) {
				for (int w = 1 ; w < 4 ; w++ ) {
					for (int y = 2 ; y < 4 ; y++) {
						if (v<w && w<y) {
							tripleintcombs.get(c).add(v);
							tripleintcombs.get(c).add(w);
							tripleintcombs.get(c).add(y);
							c++;
						}		
					}
				}
			}
			
		
			int triplecomb = 0 ; 
			int co = 0 ;
			if (tripleExists) {
				for (ArrayList<Rox> thisChannel: OverlapRoxx.TripleInterRoxx) {
					if (thisChannel.size()>0) {
						for(Rox thisRox : thisChannel) {
							
							intcellRows[d].createCell(0).setCellValue(thisRox.getIndex());
							intcellRows[d].createCell(tripleintcombs.get(triplecomb).get(0)+1).setCellValue(1);
							intcellRows[d].createCell(tripleintcombs.get(triplecomb).get(1)+1).setCellValue(1);
							intcellRows[d].createCell(tripleintcombs.get(triplecomb).get(2)+1).setCellValue(1);
							
							
							if (triplecomb==0) {
								intcellRows[d].createCell(4).setCellValue(0);
								//for (ArrayList<ArrayList<Rox>> thisTriple : OverlapRoxx.TripleOverlapRoxx) {
									//for (ArrayList<Rox> thisRoxx : thisTriple) {
										for (int i = 0 ; i < 3 ; i++) {
											//if ( i == 3 ) {continue;}
											int a = i + 1;
											//inputDir2 + "\\" + 
											if (i==0) { a = i + 2;}
											if (i==1) { a = i + 6;}
											if (i==2) { a = i + 10;}
											//if (i==3) { a = i + 14;}
											
											for (int j = 0 ; j < 4 ; j++) {
												int w = j + 1 ;
												IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
												IJ.getImage().setRoi(((Rox) OverlapRoxx.TripleOverlapRoxx.get(0).get(i).get(co)).getRoi());
				
												//ImagePlus imp = IJ.getImage();
												double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
												
												intcellRows[d].createCell(a +  j + 4).setCellValue(channelMean);
												intcellRows[d].createCell(a + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
												IJ.getImage().killRoi();
											
											}
										}
									//}
								//}ss
							}
							
							if (triplecomb==1) { 
								co = 0 ; 
								intcellRows[d].createCell(3).setCellValue(0);
								for (int i = 0 ; i < 3 ; i++) {
									int a = i + 1;
									//inputDir2 + "\\" + 
									if (i==0) { a = i +2;}
									if (i==1) { a = i + 6;}
									if (i==2) { a = i + 15;}
									//if (i==3) { a = i + 14;}
									
									
									
									for (int j = 0 ; j < 4 ; j++) {
										int w = j + 1 ;
										IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
										
										IJ.getImage().setRoi(((Rox) OverlapRoxx.TripleOverlapRoxx.get(1).get(i).get(co)).getRoi());
										//IJ.getImage().setRoi(thisRox.getRoi());
										
										//ImagePlus imp = IJ.getImage();
										double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
										intcellRows[d].createCell(a +  j + 4).setCellValue(channelMean);
										intcellRows[d].createCell(a + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
										IJ.getImage().killRoi();
									
									}
								}
							}
							
							if (triplecomb==2) { 
								co = 0;
								intcellRows[d].createCell(2).setCellValue(0);
								for (int i = 0 ; i < 3 ; i++) {
									int a = i + 1;
									//inputDir2 + "\\" + 
									if (i==0) { a = i + 2;}
									if (i==1) { a = i + 11;}
									if (i==2) { a = i + 15;}
									//if (i==3) { a = i + 14;}
									for (int j = 0 ; j < 4 ; j++) {
										int w = j + 1 ;
										IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
										
										IJ.getImage().setRoi(((Rox) OverlapRoxx.TripleOverlapRoxx.get(2).get(i).get(co)).getRoi());
										//IJ.getImage().setRoi(thisRox.getRoi());
										
										//ImagePlus imp = IJ.getImage();
										double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
										intcellRows[d].createCell(a +  j + 4).setCellValue(channelMean);
										intcellRows[d].createCell(a + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
										IJ.getImage().killRoi();
									
									}
								}	
							}
							
							if (triplecomb==3) {
								co = 0 ;
								intcellRows[d].createCell(1).setCellValue(0);
								for (int i = 0 ; i < 3 ; i++) {
									int a = i + 1;
									//inputDir2 + "\\" + 
									if (i==0) { a = i + 7;}
									if (i==1) { a = i + 11;}
									if (i==2) { a = i + 15;}
									//if (i==3) { a = i + 14;}
									for (int j = 0 ; j < 4 ; j++) {
										int w = j + 1 ;
										IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
										
										IJ.getImage().setRoi(((Rox) OverlapRoxx.TripleOverlapRoxx.get(3).get(i).get(co)).getRoi());
										//IJ.getImage().setRoi(thisRox.getRoi());
										
										//ImagePlus imp = IJ.getImage();
										double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
										intcellRows[d].createCell(a +  j + 4).setCellValue(channelMean);
										intcellRows[d].createCell(a + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
										IJ.getImage().killRoi();
									
									}
								}
							}
							co++;
							d++;
						}
					}	
					triplecomb++;
				}
			}
								
								
			
			ArrayList<ArrayList<Integer>> doublecombints = new ArrayList<ArrayList<Integer>>();
			for (int i = 0 ; i < 6 ; i++) {
				ArrayList<Integer> thisList = new ArrayList<Integer>();
				doublecombints.add(thisList);
			}
			
			
			c=0;
			for (int v = 0 ; v < 4 ; v++) {
				for (int w = 1 ; w < 4 ; w++) { 
					if (v<w) {
					doublecombints.get(c).add(v);
					doublecombints.get(c).add(w);	
					c++;
					}
				}
			}
			
			boolean doubleExists = true ; 
			
			int doublecomb = 0 ; 
			if (doubleExists) {
				for (ArrayList<Rox> thisChannel: OverlapRoxx.DoubleInterRoxx) {
					if (thisChannel.size()>0) {
						co = 0;
						for(Rox thisRox : thisChannel) {
							intcellRows[d].createCell(0).setCellValue(thisRox.getIndex());
							for (int i = 1 ; i < 5 ; i++) {
								intcellRows[d].createCell(i).setCellValue(0);
							}
							
							intcellRows[d].getCell(doublecombints.get(doublecomb).get(0)+1).setCellValue(1);
							intcellRows[d].getCell(doublecombints.get(doublecomb).get(1)+1).setCellValue(1);
							
							
							for (int i = 0 ; i < 2 ; i++) {
								int a = i + 1;
								//inputDir2 + "\\" + 
								int b = i + 1;
								
								for (int j = 0 ; j < 4 ; j++) {
									
									int w = j + 1;
									IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
									WindowManager.toFront(WindowManager.getWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif"));					//ImagePlus imp = IJ.getImage();
									IJ.getImage().setRoi(((Rox)OverlapRoxx.DoubleOverlapRoxx.get(doublecomb).get(i).get(co)).getRoi());
									double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
									//intcellRows[d].createCell(a+5).setCellValue(channelMean);
									
									if (doublecomb == 0) { a = i + 2; b = i + 6;}
									else if (doublecomb == 1 ) { a = i + 2 ; b = i + 11;}
									else if (doublecomb == 2 ) { a = i + 2 ; b = i + 16;}
									else if (doublecomb == 3 ) { a = i + 7 ; b = i + 11;}
									else if (doublecomb == 4 ) { a = i + 7 ; b = i + 16;}
									else if (doublecomb == 5 ) { a = i + 12 ; b = i + 16;}
									
									if (i == 0) {
										intcellRows[d].createCell(a + j + 4).setCellValue(channelMean);
										intcellRows[d].createCell(a + + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
									} else if (i ==1) {
										intcellRows[d].createCell(b + j + 4 ).setCellValue(channelMean);
										intcellRows[d].createCell(b + j + 27).setCellValue(channelMean-finalbackgrounds[j]);
											
									}
									IJ.getImage().killRoi();
	
								}						
							}
							
							d++;
							co++;
						}
					
					}	
					doublecomb++;
				}
			}
			
			int s = 0 ;
			boolean singleExists = true;
			if (singleExists) {
				for (ArrayList<Rox> thisChannel : OverlapRoxx.SingleRoxx) {
					co = 0;
					if (thisChannel.size()>0) {
						for(Rox thisRox : thisChannel) {
							intcellRows[d].createCell(0).setCellValue(thisRox.getIndex());
							
							for (int i = 1 ; i < 5 ; i ++ ) {
								if (i == s+1) {
									intcellRows[d].createCell(i).setCellValue(1);	
								}
								else {
									intcellRows[d].createCell(i).setCellValue(0);
								}
							}
	
							int a = s + 2;
							
							if (s==0) { a = s +2;}
							if (s==1) { a = s + 6;}
							if (s==2) { a = s + 10;}
							if (s==3) { a = s + 14;}
						
							for (int j = 0 ; j < 4 ; j ++) {
								int w = j + 1 ;
								IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + w + ".tif");
								//ImagePlus imp = IJ.getImage();
								IJ.getImage().setRoi(thisRox.getRoi());
								double channelMean = ImageStatistics.getStatistics(IJ.getImage().getProcessor(), Measurements.MEAN, IJ.getImage().getCalibration()).mean;
								intcellRows[d].createCell(a+ j + 4).setCellValue(channelMean);
								intcellRows[d].createCell(a+ j + 27).setCellValue(channelMean-finalbackgrounds[s]);
								IJ.getImage().killRoi();
							}
							co++;
							d++;
						}
					}
					s++;
				}
			}
			
			
		} else if (measureIntensity == false ) {
			wb.removeSheetAt(2);
		}
		
		
		System.out.println("Finished making tables");
		
		FileOutputStream fileOut;
		
		boolean excelClosed = false;
		
		while (excelClosed == false) {
			try {
				fileOut = new FileOutputStream(OutputDir + "/" + ImageName + "_" + zoneName + "_Results.xls");
				wb.write(fileOut);
				fileOut.close();
				excelClosed = true;
				
			} catch (FileNotFoundException e) {
				WaitForUserDialog excelClose = new WaitForUserDialog("Close results excel files for the same image if any are open");
				excelClose.show();		
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
		for (int i = 0 ; i < 4 ; i++) {
			int a = i + 1 ;
			IJ.selectWindow(imageName + "_" + "OriginalDuplicate-" + "C" + a + ".tif");
			Window imwin = IJ.getImage().getWindow();
	
			IJ.getImage().flush();
			IJ.getImage().close();
			
			imwin.dispose();
			imwin = null;
		
		}
		
		for (Window wind : Window.getWindows()) {
        	if (!wind.getName().matches("frame0") && !wind.getName().matches("win0") && !wind.getName().matches("frame1")) {	
        		wind.dispose();
	        	wind = null;
        	}
		}
		
	}
		
	
}
