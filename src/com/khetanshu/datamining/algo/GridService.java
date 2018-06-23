package com.khetanshu.datamining.algo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.khetanshu.datamining.vo.GridNode;
/**
 * @author khetanshu
 * This class act as a service layers which provides the abstract functionalities related to GRID spaces.
 * The entire GRID spaces is stored in form of multi-dimension hash-mapping (intuitive to suffix trees). 
 * This is computationally optimal. 
 * To get/check/update any grid at any point of n-dimensional space it would take O(n) running time.
 */
public class GridService {
	public GridNode grid = new GridNode();
	public Queue<List<Double>> pointsQueue = new LinkedBlockingQueue<>();
	public Integer thresholdDensity;
	public Double gridWidth;
	public Double totalDimensionPartitions;
	public List<String> neigboursPossibilities;

	public GridService(int totalDimension, int windowSize) {
		setNeigboursPossibilities(generateNeigboursPossibilities(totalDimension));
		totalDimensionPartitions=calulateTotalDimensionPartitions(totalDimension, windowSize);
		gridWidth = calculateGridWidth();
		thresholdDensity =calculateThresholdDensity();
	}

	private Integer calculateThresholdDensity() {
		return (int) Math.ceil(Math.log10(totalDimensionPartitions));
	}

	private Double calculateGridWidth() {
		return 65535.0/totalDimensionPartitions;
	}

	private Double calulateTotalDimensionPartitions(int totalDimension,int windowSize) {
		return  ((Math.pow(windowSize, (1.0/(totalDimension+1.0)))));
	}

	public void savePoint(List<Double> point) {
		/**Converting point to single relative 1st quadrant domain*/
		//point = convertToRelativeGripSpace(point);
		pointsQueue.add(point);
		List<Integer> gridId= getGridId(point);
		grid.savePointInRespectiveGrid(grid, gridId, 0);
	}

	public boolean isOutlier(List<Double> point) {
		/**Converting point to single relative 1st quadrant domain*/
		//point = convertToRelativeGripSpace(point);
		List<Integer> gridId =getGridId(point);
		if(doesTheGridOrAnyNeighbourHasSufficientDensity(gridId)) {
			return false;
		}else {
			/*System.out.println(gridId);*/
			return true;
		}
	}

	public boolean removeFirstPoint() {
		if(pointsQueue.isEmpty()) {
			return false;
		}else {
			List<Integer> gridId = getGridId(pointsQueue.remove());
			if( grid.removePointFromRespectiveGrid(grid, gridId, 0) !=-1) {
				
				return true;
			}else { 
				return false;
			}	
		}
	}
	
	public List<Double> convertToRelativeGripSpace(List<Double> point){
		for (int i = 0; i < point.size(); i++) {
			point.set(i, getRelativePostiveValue(point.get(i)));
		}
		return point;
	}
	
	
	/**FOLLOWS - INTERNAL MEMBERS */ 
	
	private boolean doesGridHaveSufficientDensity(List<Integer> gridId) {
		if(getGridDensity(gridId) >= thresholdDensity) {
			return true;
		}else {
			/*System.out.println("Neighbour "+gridId+  " Density :"+ getGridDensity(gridId) +" thresholdDensity = "+ thresholdDensity);*/
			return false;
		}
	}

	private boolean doesTheGridOrAnyNeighbourHasSufficientDensity(List<Integer> gridId) {
		for (String combination : getNeigboursPossibilities()) {
			List<Integer> newGrid = new ArrayList<>();
			newGrid.addAll(gridId);
			for (int i = 0; i < combination.length(); i++) {
				switch(combination.charAt(i)) {
				case '0':
					newGrid.set(i, newGrid.get(i));
					break;
				case '1':
					newGrid.set(i, newGrid.get(i)+1);
					break;	
				case 'x':
					newGrid.set(i, newGrid.get(i)-1);
					break;
				}
			}
			/*System.out.println(newGrid + " Density = " + getGridDensity(newGrid));*/
			if(doesGridHaveSufficientDensity(newGrid)) {
				/*System.out.println("Neighbour with Sufficient Density = "+newGrid);*/
				return true;
			}
		}
		return false;
	}

	/**
	 * @totalDimension for x and y totalDimension = 2, similarly for x,y,z totalDimension = 3 and likewise.
	 * @return truth table of all the possible neighbors like for dimension x and y 
	 * Possible neighbors would be [00, 01, 10, 11, 0-1, -10, -1-1, 1-1, -11] 
	 * i.e. (x + 0, y + 0), (x + 0, y + 1), (x + 1, y + 0),(x + 1, y + 1), (x + 0, y - 1)... and so on.
	 */
	private List<String> generateNeigboursPossibilities(int totalDimension) {
		char ways[] = {'0', '1', 'x'};           
		int rows = (int) Math.pow(3, totalDimension);
		List<String> combinations = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			String s = "";
			for (int j = totalDimension - 1; j >= 0; j--)
				s+= (ways[(i / (int) Math.pow(3, j)) % 3]);
			combinations.add(s);
		}
		return combinations;
	}

	private List<Integer> getGridId(List<Double> point){
		List<Integer> gridId = new LinkedList<>();
		for (Double dimemsion : point) {
			dimemsion/=gridWidth;
			gridId.add((int)Math.floor(dimemsion));
		}
		return gridId;
	}

	/**
	 * This function would return the relative value of all the 16-bit signed integer in terms of positive value
	 * For example
	 * -32768 --> Relative value would be --> 0 			( -32768 + 32768 {Relative factor}
	 * -32767 --> Relative value would be --> 1 			( -32767 + 32768 {Relative factor}
	 *  0     --> Relative value would be --> 32768 		(  0     + 32768 {Relative factor}
	 *  32767 --> Relative value would be --> 65535 		(  32767 + 32768 {Relative factor}
	 */
	private double getRelativePostiveValue(double value){
		double relativeFactor = 32768.0;
		return (value + relativeFactor);
	}
	
	private int getGridDensity(List<Integer> gridId) {
		return grid.getGridDensity(grid, gridId, 0);
	}


	private List<String> getNeigboursPossibilities() {
		return neigboursPossibilities;
	}


	private void setNeigboursPossibilities(List<String> neigboursPossibilities) {
		this.neigboursPossibilities = neigboursPossibilities;
	}

}
