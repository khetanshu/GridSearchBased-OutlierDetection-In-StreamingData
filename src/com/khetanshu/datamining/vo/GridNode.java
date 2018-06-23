package com.khetanshu.datamining.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author khetanshu
 * This class would create a mapping to find the grid
 */
public class GridNode {
	private Map<Integer, GridNode> map;
	private Integer density;


	/**
	 * Value =-1 means that there exist no value i.e. this isn't the leave node.
	 */
	public GridNode() {
		map = new HashMap<>();
		density= -1;
	}

	/**
	 * @param node
	 * @param gridId
	 * @param index (default 0)
	 * 
	 * For example say a point is (x, y , z) : (1.2 , 3.4 , 4.1)
	 * Then its grid id would be (1, 3, 4)
	 * i.e. 
	 * -> get value_x of map with key 1
	 * -> followed by with value_x(which contains map for y), get value_y with key 3
	 * -> followed by with value_y(which contains map for z), get value_z with key 4
	 * -> and value of z is the state where the counter need to be increase which means that point resides in grid id (1 ,3, 4) 
	 */
	public void savePointInRespectiveGrid(GridNode node, List<Integer> gridId, int index) {
		if(index < gridId.size()-1) {
			int key = gridId.get(index++);
			if(node.getMap().containsKey(key)) {
				/** Find and move to next node */
				savePointInRespectiveGrid(node.getMap().get(key), gridId,  index);
			}else {
				/**if the key isn't present in the mapping that means we need to create a new grid's dimension and 
				 * insert the key into it */
				GridNode newNode = new GridNode();
				node.getMap().put(key, newNode);
				savePointInRespectiveGrid(newNode, gridId,  index);
			}
		}else {
			int key = gridId.get(index);
			if(node.getMap().containsKey(key)) {
				/** Increment the density */ 
				GridNode leaveNode = node.getMap().get(key);
				leaveNode.setDensity(leaveNode.getDensity()+1);
			}else {
				GridNode leaveNode = new GridNode();
				leaveNode.setDensity(1);
				node.getMap().put(key, leaveNode);
			}
		}
	}

	public Integer getGridDensity(GridNode node, List<Integer> gridId, int index) {
		GridNode nextGridNode = node.getMap().get(gridId.get(index));
		if(nextGridNode==null) {
			/** i.e. grid not present*/
			return -1;
		}else if(nextGridNode.getMap().isEmpty()) {
			/**
			 * Return the grid density
			 * Note- grid was not found i.e. the gridId wasn't present then this would return -1 by default
			 */
			return nextGridNode.getDensity();
		}else {
			/**
			 * Recurse it until we find a point of insertion i.e. when node is NULL
			 */
			return getGridDensity(nextGridNode, gridId, ++index);
		}
	}
	
	public Integer removePointFromRespectiveGrid(GridNode node, List<Integer> gridId, int index) {
		GridNode nextGridNode = node.getMap().get(gridId.get(index));
		if(nextGridNode==null) {
			/** i.e. grid not present*/
			return -1;
		}else if(nextGridNode.getMap().isEmpty()) {
			/**
			 * Return the grid density
			 * Note- grid was not found i.e. the gridId wasn't present then this would return -1 by default
			 */
			nextGridNode.setDensity(nextGridNode.getDensity()-1);
			/*System.out.println("Reduced Grid Id Density: " + gridId + " Remaining Density = "+ nextGridNode.getDensity() );*/
			return nextGridNode.getDensity();
		}else {
			/**
			 * Recurse it until we find a point of insertion i.e. when node is NULL
			 */
			return removePointFromRespectiveGrid(nextGridNode, gridId, ++index);
		}
	}
	
	


	private Map<Integer, GridNode> getMap() {
		return map;
	}
	/*private void setMap(Map<Integer, GridNode> node) {
		this.map = node;
	}*/
	private Integer getDensity() {
		return density;
	}
	private void setDensity(Integer density) {
		this.density = density;
	}

}
