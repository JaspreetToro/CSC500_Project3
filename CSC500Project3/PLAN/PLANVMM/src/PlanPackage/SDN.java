package PlanPackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SDN {

	static HashMap<Integer, String> PodMap = new HashMap<Integer, String>();
	static HashMap<Integer, String> TopoMap = new HashMap<Integer, String>();

	public static int ShorestDistance(int k,int j,int i) {		
		return NumberOfHops(k,j,i);
	}	

	public static int[] GetInput() {
		boolean procceed = false;
		String temp;
		int input = -1;
		Scanner in = new Scanner(System.in);
		int[] inputs = new int[6];

		while(!procceed){
			System.out.println("Hello enter k please.");///////////////////////////////////////k

			temp = in.nextLine();

			procceed = tryParseInt(temp);

			if(procceed) {
				input = Integer.parseInt(temp);
				if(input % 2 == 0 && input > 0)
					procceed = true;
				else {
					System.out.println("Error: Must be an even number greater than 0.");			
					procceed = false;
				}
			}
			else
			{
				System.out.println("Error: Must be an even number greater than 0.");	
			}
		}		
		inputs[0] = input;

		int numPms = (int)(Math.pow(inputs[0], 3)/4);

		// input for rc
		System.out.println("Please enter the initial capacity of each PM.");
		inputs[1] = in.nextInt();	

		// input for l
		System.out.println("Please enter the number of VM pairs randomly placed. For example 1 = 2 VMs. \nMust be between 1 and " + numPms*inputs[1]);
		inputs[2] = in.nextInt();	

		// input for m
		System.out.println("Please enter the number of middleboxes in the data center.");
		inputs[3] = in.nextInt();		

		// input for lambda
		System.out.println("Please enter the communication frequency of each VM pair is a random number between [0, lambda].");
		inputs[4] = in.nextInt();	
		
		// input for migration
		System.out.println("Please enter the migration coefficient.");
		inputs[5] = in.nextInt();		

		in.close();
		return inputs;
	}
	
	@SuppressWarnings("unused")
	private static int NumberOfHops( int k,int i,int j)
	{
		int hops;//for now so we wont get a error later we wont initialize
		int start = 0;
		int tempI = i;
		int tempJ = j;


		//number of each nodes   				//if 	k = 2		k=4
		int numPms = (int)(Math.pow(k, 3)/4);	//		2 			16										
		int numEdges = (int)(Math.pow(k, 2)/2);	// 		2			8
		int numAggs = (int)(Math.pow(k, 2)/2);  //		2			8
		int numCores = (int) ( Math.pow(k, 2)/4 );//	1			4

		//start and end points of each type of node   						//if k = 2		k=4
		int pmStart = 0;													// 0			0
		int pmEnd = numPms - 1;												// 1			15
		int edgeStart = numPms;												// 2			16
		int edgeEnd = numPms + numEdges -1;									// 3			23
		int aggStart = edgeEnd +1;											// 4			24
		int aggEnd = aggStart + numAggs -1;									// 5			31
		int coreStart = aggEnd +1;											// 6			32
		int coreEnd = (int)(((5*Math.pow(k, 2)+ Math.pow(k, 3))/4) - 1);	// 7			35

		//determine the smaller and bigger one of the two ids
		i = Integer.min(tempI, tempJ);
		j = Integer.max(tempI, tempJ);

		if(i == j)//same node
		{
			hops = 0;
		}
		else if(typeOf(k,i) == "Physical Machine") { /////////// Physical Machine ///////////////////
			if(typeOf(k,j) == "Physical Machine") { // i and j are pm
				if(Math.floor((2*i)/k) == Math.floor((2*j)/k))// within same edge switch
				{
					hops = 2;
				}
				else if (Math.floor((4*i)/Math.pow(k, 2)) == Math.floor((4*j)/Math.pow(k, 2)))// within same pod
				{
					hops = 4;
				}
				else //any other
				{
					hops = 6;
				}
			}
			else if(typeOf(k,j) == "Edge Switch") {
				if (Math.floor(i / 2) == j)
				{
					hops = 1;
				}

				else if(Math.floor(i/numCores) == Math.floor(j - edgeStart)/(k/2))
				{
					hops = 3;
				}
				else
				{
					hops = 5;
				}

			}
			else if(typeOf(k,j) == "Aggregation Switch") {
				if(Math.floor(i/(numCores)) == Math.floor((j - aggStart)/(k/2))) {
					hops = 2;
				}
				else {
					hops = 4;
				}
			}
			else {//Pm and Core switch
				hops = 3;
			}
		}
		else if (typeOf(k,i) == "Edge Switch") { /////////////////Edge Switch ///////////////////
			if(typeOf(k,j) == "Edge Switch") { // j == edge
				int numEdge = (int) (Math.pow(k, 2)/2);
				int numPm = (int) (Math.pow(k, 3)/4);
				int edge_per_pod = numEdge/k;
				if(i >= numPm && j < 16 + edge_per_pod)
				{
					hops = 2;
				}
				else 
				{
					hops = 4;
				}
			}
			else if(typeOf(k,j) == "Aggregation Switch") { //j == agg
				if(Math.floor((i - edgeStart)/(k/2)) == Math.floor((j - aggStart)/(k/2))) {
					hops = 1;
				}
				else {
					hops = 3;
				}
			}
			else {//edge and Core switch j == core
				hops = 2;
			}
		}
		else if(typeOf(k,i) == "Aggregation Switch") { //////////////// Aggregation Switch ///////////////////
			if(typeOf(k,j) == "Aggregation Switch") {
				if(Math.floor((i - aggStart)/(k/2)) == Math.floor((j - aggStart)/(k/2)) || ((i - aggStart)%(k/2)) == ((j - aggStart)%(k/2))) {
					hops = 2;
				}
				else {
					hops = 4;
				}
			}
			else {//agg and Core switch
				if((i - aggStart)%(k/2) == Math.floor((j - coreStart)/(k/2))) {
					hops = 1;
				}
				else {
					hops = 3;
				}
			}
		}
		else { ///////////////// Core Switch //////////////////////
			if(Math.floor((i - coreStart)/(k/2)) == Math.floor((j - coreStart)/(k/2))) {
				hops = 2;
			}
			else {
				hops = 4;
			}
		}
		return hops;
	}

	public static boolean tryParseInt(String value) {  
		try {  
			Integer.parseInt(value);  
			return true;  
		} catch (NumberFormatException e) {  
			return false;  
		}  
	}
	
	public static String typeOf(int k, int id) {
		String type;

		if (id >= 0 && id <= (Math.pow(k, 3)/4) - 1)
		{
			type ="Physical Machine";

		}
		else if(id >= Math.pow(k, 3)/4 & id <= Math.pow(k, 3)/4 + Math.pow(k, 2)/2 - 1)
		{
			type ="Edge Switch";
		}
		else if (id >= Math.pow(k, 3)/4 + Math.pow(k, 2)/2 & id <= Math.pow(k, 3)/4 + Math.pow(k, 2)/2 + Math.pow(k, 2)/2 - 1 )
		{
			type ="Aggregation Switch";
		}
		else //if 
		{
			type ="Core Switch";
		}
		return type;
	}
	
//----------------------------------Methods I dont think we will use for project 2--------------------------
	
	@SuppressWarnings("unused")
	private static HashMap<Integer, String> CreateTopology(int k) {
		//initialize map and variables needed
		HashMap<Integer, String> map = new HashMap<Integer, String>();

		int numCores = (int) ( Math.pow(k, 2)/4 );
		int numAgg = (int) (Math.pow(k, 2)/2);
		int numEdge = (int) (Math.pow(k, 2)/2);
		int numPm = (int) (Math.pow(k, 3)/4);		
		int idNum = 0;
		int localTotal = numPm;


		//assign map
		//assign PMs 
		while(idNum < localTotal){
			map.put(idNum, "Pm");
			idNum++;
		}

		//assign Edge
		localTotal += numEdge;
		while(idNum < localTotal) {
			map.put(idNum, "Edge");
			idNum++;
		}

		//assign Aggregation
		localTotal += numAgg;
		while(idNum < localTotal) {
			map.put(idNum, "Aggregation");
			idNum++;
		}

		//assign Cores
		localTotal += numCores;
		while(idNum < localTotal) {
			map.put(idNum, "Core");
			idNum++;
		}		

		return map;		
	}
	
	@SuppressWarnings("unused")
	private static HashMap<Integer, String> CreatePodMap(int k) {

		HashMap<Integer, String> podMap = new HashMap<Integer, String>();

		//assign map
		//assign Pod 
		for(int i = 0; i < k; i++){
			podMap.put(i, "Pod");
		}

		return podMap;
	}
	
	@SuppressWarnings("unused")
	private static void PrintMap(Map<Integer, String> topoMap, boolean isPodMap) {
		
		String title = "";
		if(isPodMap) {
			title = "\n--------------------------------Pod------------------------------------";
		}
		else {      
			title = "\n------------------------Pm and all Switches----------------------------";
		}
		
		System.out.println(title);
		
		for(Map.Entry<Integer, String> entry : topoMap.entrySet()) {
			System.out.println(entry.getValue() + "Id: " + entry.getKey());
		}
		
		System.out.println("-----------------------------------------------------------------------");
	}
}
