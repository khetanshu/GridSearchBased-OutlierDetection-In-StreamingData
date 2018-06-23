import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.khetanshu.datamining.algo.GridService;

/**
 * @author khetanshu
 * This class is the entry point of the project
 */
public class Solution {

	public static void main(String[] args) {
		Integer windowSize = null;
		Integer totalDimensions =null;
		String host = null;
		String port = null;
		GridService o = null;
		Socket sock= null;
		Scanner in = null;
		try {
			in = new Scanner(System.in);
			/**Read the window Size*/
			if(in.hasNextLine()) {
				String input = in.nextLine();
				if(!input.isEmpty() && input.matches("[0-9]*")){
					windowSize = Integer.parseInt(input);
				}else {
					System.out.println("Incorrect Input");
					return;
				}
			}
			/**Read the host-name and port*/
			if(in.hasNextLine()) {
				String input = in.nextLine();
				if(!input.isEmpty() && input.matches(".*:.*")){
					String hostPort[] = input.split(":");
					host = hostPort[0];
					port = hostPort[1];
				}else {
					System.out.println("Incorrect Host:Port = "+ input);
					return;
				}
			}
			if(in!=null) {
				in.close();
			}
			if(windowSize==null) {
				System.out.println("Incorrect Window size (null)");
				return;
			}
			if(host==null || port == null) {
				System.out.println("Invalid host or port");
				return;
			}
			/**Connect to Server*/
			sock = new Socket(host,Integer.parseInt(port));
			DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());
			in = new Scanner(sock.getInputStream());
			/**Read the first inputs to know the grid dimension*/	
			if(in.hasNextLine()) {
				String pointStr = in.nextLine();
				if(!pointStr.matches(".*,(-?[0-9]*.?[0-9]*,?)*")) {
					System.out.println("Incorrect Point = "+pointStr );
					return;
				}
				String[] arr = pointStr.split(",");
				List<Double> point = new ArrayList<>();
				for (int i = 1; i < arr.length; i++) {
					point.add(Double.parseDouble(arr[i]));
				}
				totalDimensions= arr.length-1;
				o = new GridService(totalDimensions,windowSize);
				o.savePoint(point);
				/*System.out.println("Saved "+pointStr);*/
				/*System.out.println(1 +" : "+pointStr);
				System.out.print( o.getGridId(point) + ": ");
				System.out.println(o.getGridDensity(o.getGridId(point)));*/
			}
			/*int outliersCnt = 0;*/
			/**Read the window-1 data as 1 is already read in the previous step*/
			for(int i=1;i<windowSize;i++) {
				String pointStr = in.nextLine();
				if(!pointStr.matches(".*,(-?[0-9]*.?[0-9]*,?)*")) {
					System.out.println("Incorrect Point = "+pointStr );
					return;
				}
				String[] arr = pointStr.split(",");
				if((arr.length-1) != totalDimensions) {
					System.out.println("Incorrect Input (Includes timestamp) : {"+pointStr+"} Total Dimensions expected was = "+ totalDimensions );
					return;
				}
				List<Double> point = new ArrayList<>();
				for (int j = 1; j < arr.length; j++) {
					point.add(Double.parseDouble(arr[j]));
				}
				o.savePoint(point);
				/*System.out.println("Saved "+pointStr);*/
				int attempts = 0;
				while(dataInputStream.available() == 0 && attempts < 1000)
				{
					attempts++;
				}
			}
			/** -> Read the subsequent point : w+1
			 *  -> Check if its an outlier if yes then print that
			 *   otherwise add that in the grid space and at the same time remove the 1st element so that total size points remains as w in the grid space 
			 */
			while(in.hasNextLine()) {
				String pointStr = in.nextLine();

				if(!pointStr.matches(".*,(-?[0-9]*.?[0-9]*,?)*")) {
					System.out.println("Incorrect Point = "+pointStr );
					return;
				}
				String[] arr = pointStr.split(",");
				if((arr.length-1) != totalDimensions) {
					System.out.println("Incorrect Input (Includes timestamp) : {"+pointStr+"} Total Dimensions expected was = "+ totalDimensions );
					return;
				}
				List<Double> point = new ArrayList<>();
				for (int j = 1; j < arr.length; j++) {
					point.add(Double.parseDouble(arr[j]));
				}
				if(o.isOutlier(point)) {
					/*outliersCnt++;*/
					System.out.println(pointStr);
				}
				o.removeFirstPoint();
				o.savePoint(point);
				/*System.out.println("Saved(New) "+pointStr);*/
				/**handles the delay/lag from server*/
				int attempts = 0;
				while(dataInputStream.available() == 0 && attempts < 1000)
				{
					attempts++;
				}
			}
			/*
			System.out.println("\nOutput Summary : ");
			System.out.println("p : "+o.totalDimensionPartitions);
			System.out.println("GridWidth  : "+o.gridWidth);
			System.out.println("Tau(Threshold Density) : "+o.thresholdDensity);
			System.out.println("Total Outliers found : "+ outliersCnt);
			*/
		} catch (Exception e) {
			System.out.println("Exception found : " + e.toString());
		}finally {
			if(in!=null) {
				in.close();
			}
			if(sock!=null) {
				try {
					sock.close();
				} catch (IOException e) {
					System.out.println("Exception found : " + e.toString());
				}
			}
		}
	}
}
