package PlanPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.io.*;

@SuppressWarnings("rawtypes")
public class PlanGenerator {

	//globals
	public static int k;
	public static int numPms;									
	public static int numEdges;	
	public static int numAggs;  
	public static int edgeStart;									
	public static int edgeEnd;							
	public static int aggStart;
	public static int mbDistance;
	public static int firstMB; 
	public static int lastMB;
	public static int migration;
	public static List<VM> Vms = new ArrayList<VM>();
	public static List<String> cap = new ArrayList<String>();

	public static void main(String[] args) {
		//k,rc,vmpairs,mbs,frequency,migration
		//get inputs
		//int[] inputs = SDN.GetInput();

		SetUpPLAN(4,10,10,2,100,10);
		//k,resCap,VmPairsCount,boxes,frequecy.migration
		//SetUpPLAN(inputs[0],inputs[1],inputs[2],inputs[3],inputs[4],inputs[5]);

	}

	///
	@SuppressWarnings({ "unchecked" })
	public static void SetUpPLAN(int numPods,int resCap, int vmPairs,int boxes, int frequency,int migCoe) {	
		k = numPods;
		migration = migCoe;
		numPms = (int)(Math.pow(k, 3)/4);	//		2 			16										
		numEdges = (int)(Math.pow(k, 2)/2);	// 		2			8
		numAggs = (int)(Math.pow(k, 2)/2);  //		2			8
		edgeStart = numPms ;												// 2			16
		edgeEnd = numPms + numEdges -1;									// 3			23
		aggStart = edgeEnd +1;	

		List<Integer> vmPairLocation = VMPairsLocation(vmPairs,resCap);
		List<Integer> MBsLocation = MBsLocation(boxes);
		mbDistance = MBDistance(MBsLocation);
		List<Integer> frequencies = GetFrequencies(vmPairs, frequency);

		Vms = CreateObjects(vmPairLocation,frequencies, migCoe);
		boolean procced = false;
		//manipulate the objects to produce outcome wanted
		GenerateUtilityForVMs();

		//PrintUtility("Init");
		String loop = "";
		int numberOfIterations = 1;
		do{
			for (VM vm : Vms) 
			{ 
				procced = false;
				int key = -1;
				int utility = 0;
				
				for (Enumeration k = vm.Utility.keys(); k.hasMoreElements();) 
				{ 
					int index = (int) k.nextElement();
					if(key == -1) {
						key = vm.VmOGSource;
						utility = vm.Utility.get(key);
					}
					
					int newUtility = vm.Utility.get(index);
					
					if(newUtility > utility && Collections.frequency(cap, String.valueOf(index)) < resCap) {
						//get key with the best utility in all vms.
						key = (int) index;
						procced = true;
						utility = vm.Utility.get(key);
					}				
				}
				if(procced) {
					
					//print stament before updating key and utility
					loop = loop + "Loop Iteration: " + numberOfIterations++ + "\r\n";
					loop = loop + "GroupId: " + vm.groupId + "\r\n";
					loop = loop + "Vm: " + vm.VmOGSource + " has moved to PM: " + key + "\r\n";
					loop = loop + "All Utility:"+ vm.Utility.toString() + "\r\n";
					loop = loop + "Best Utility: " + utility + "\r\n" + "\r\n";
					
					//add and remove vm from resCap list
					cap.remove(String.valueOf(vm.VmOGSource));
					cap.add(String.valueOf(key));
					
					//generate new key and utility. set VM with new data
					vm.VmOGSource = key;
					vm.Utility = GetUtilityCost(vm);
				}
				
			}

		}while(procced);

		loop = PrintOGSource(Vms, loop);
		
		if(!procced) {
			loop = "\r\n" +loop + "Final Utility \r\n";
			for (VM vm : Vms) 
			{
				loop = loop + "Vm: "+ vm.VmOGSource + "\r\n" + vm.Utility.toString() + "\r\n";
			}
		}
		
		FileOutputStream outputStream;
		try {

			outputStream = new FileOutputStream("Utility"+"Final"+".txt");
			byte[] kBytes = loop.getBytes();
			outputStream.write(kBytes);

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}	

		//PrintUtility("Final");
		PrintParameters(numPods, resCap,vmPairLocation,MBsLocation,frequencies,migCoe);

	}

	private static String PrintOGSource(List<VM> vms2,String s) {
		String temp = "---------------------------------------------------------------\r\nThe Following has the Utilities of all Vms,"
				+ " as you can see none of the possiblities are greater than zero."
				+ " \r\nFinal Vm Locations: (";
		String temp1 = "";
		for(VM vm : vms2) {
			temp1 = String.join(",", temp1, Integer.toString(vm.VmOGSource));
		}
		temp1 = temp1.replaceFirst(",", "");
		temp = temp + temp1 + ")\r\n";
		return s + temp;
	}

	private static void PrintUtility(String name) {

		FileOutputStream outputStream;
		try {

			outputStream = new FileOutputStream("Utility"+name+".txt");

			for (int i = 0; i < Vms.size();i++) {

				//k
				String k = "Vm Location: " + Vms.get(i).VmOGSource + "\r\n" 
						+ "Vm Group: "+Vms.get(i).groupId + "\r\n" 
						+ "Utility: " + Vms.get(i).Utility.get(Vms.get(i).VmOGSource) + "\r\n";
				byte[] kBytes = k.getBytes();
				outputStream.write(kBytes);
				//				
				//				String k1 = "Vm Pair Location: " + Vms.get(i+1).VmOGSource + "\r\n" + Vms.get(i+1).Utility.toString() + "\r\n";
				//				byte[] kBytes1 = k1.getBytes();
				//				outputStream.write(kBytes1);
				//				
				//				String k2 = "Utility Sum: " + (Vms.get(i).Utility.get(Vms.get(i).VmOGSource)+Vms.get(i+1).Utility.get(Vms.get(i).VmOGSource))+ "\r\n\r\n";
				//				byte[] kBytes2 = k2.getBytes();
				//				outputStream.write(kBytes2);

			}

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}	
	}



	//Generate Utility for each Vm
	private static void GenerateUtilityForVMs() {

		for(int i = 0; i < Vms.size();i++) {
			Vms.get(i).Utility = GetUtilityCost(Vms.get(i));
		}

	}

	//Calculate Utility for each possible PM
	static Dictionary GetUtilityCost(VM vm) {

		for(int i =0; i < numPms; i++) {
			int vmccr = VmCCR(vm.VmOGSource,vm.VmPairSource,i, vm.Frequency, vm.isFirstVm);
			int mig = MigCost(i,vm.VmOGSource,vm.MigrationCost);
			int utilityCost =  vmccr-mig ;
			//if(i != vm.VmOGSource)
			vm.Utility.put(i, utilityCost);

		}

		return vm.Utility;
	}

	//This is the only equation that needs to be fixed.
	private static int VmCCR(int sourceVm,int pairVm, int pm,int freq,boolean isfirst) {
		int originalCommCost = 0;
		int newCommCost = 0;

		if(isfirst) {
			originalCommCost = freq * (SDN.ShorestDistance(k, sourceVm, firstMB) + migration + (SDN.ShorestDistance(k, pairVm, lastMB)));

			//New Comm Cost
			newCommCost = freq * (SDN.ShorestDistance(k, pm, firstMB) + migration + (SDN.ShorestDistance(k, pairVm, lastMB)));

			return originalCommCost -newCommCost;
		}
		else {
			originalCommCost = freq * (migration + (SDN.ShorestDistance(k, pairVm, firstMB)) + SDN.ShorestDistance(k, sourceVm, lastMB));

			//New Comm Cost
			newCommCost = freq * (migration + (SDN.ShorestDistance(k, pairVm, firstMB)) + SDN.ShorestDistance(k, pm, lastMB));

			return originalCommCost - newCommCost;
		}
	}

	//Finds the migration cost between the old pair location and the new location.
	private static int MigCost(int vm1, int vm2, int mig) {
		return mig*SDN.ShorestDistance(k, vm1, vm2);
	}

	//Creates Objects for each vm to store all necessary information.
	private static List<VM> CreateObjects(List<Integer> vms, List<Integer> fs, int migCoe) {
		int freIndex = 0;

		List<VM> VMS = new ArrayList<VM>();
		for(int i = 0; i < vms.size(); i++) {
			freIndex = (int)Math.floor((i/2));

			VM vm = new VM();
			vm.groupId = freIndex;
			vm.VmOGSource = vms.get(i);
			vm.Frequency = fs.get(freIndex);
			vm.MigrationCost = migCoe;

			if(i % 2 == 1) {
				vm.isFirstVm = false;
				vm.VmPairSource = vms.get(i - 1);
			}
			else {
				vm.isFirstVm = true;
				vm.VmPairSource = vms.get(i + 1);
			}

			VMS.add(vm);
		}

		return VMS;
	}

	//gets distance of first middle box to last middle box and sets the first and last middle box location to a global variable.
	private static int MBDistance(List<Integer> mb) {
		int temp=0;

		for(int i =0; i < mb.size()-1;i++) {
			temp += SDN.ShorestDistance(k, mb.get(i),mb.get(i+1) );
		}

		firstMB = mb.get(0);
		lastMB = mb.get(mb.size()-1);

		return temp;
	}

	///Randomly gets the frequencies for each vmPair.
	private static List<Integer> GetFrequencies(int vmPairs, int frequency) {

		List<Integer> list = new ArrayList<Integer>();

		Random r = new Random();

		for(int i = 0;i < vmPairs;i++) {

			int result = r.nextInt(frequency-1) + 1;
			list.add(result);
		}
		return list;
	}

	///Create boxes numbered middle boxes.
	private static List<Integer> MBsLocation( int boxes) {
		//number of each nodes   				//if 	k = 2		k=4
		int numPms = (int)(Math.pow(k, 3)/4);	//		2 			16										
		int numEdges = (int)(Math.pow(k, 2)/2);	// 		2			8
		int numAggs = (int)(Math.pow(k, 2)/2);  //		2			8

		//start and end points of each type of node   						//if k = 2		k=4
		int edgeStart = numPms;												// 2			16
		int edgeEnd = numPms + numEdges -1;									// 3			23
		int aggStart = edgeEnd +1;											// 4			24
		int aggEnd = aggStart + numAggs -1;									// 5			31


		List<Integer> list = new ArrayList<Integer>();

		Random r = new Random();

		for(int i = 0;i < boxes;i++) {

			int result = r.nextInt(aggEnd-edgeStart) + edgeStart;
			list.add(result);
		}

		return list;
	}

	///Creates vmPairs*2 numbered Locations for VM pairs.
	private static List<Integer> VMPairsLocation(int vmPairs,int resCap){

		int numPms = (int)Math.pow(k, 3)/4;
		List<Integer> list = new ArrayList<Integer>();
		boolean proceed = true;
		Random r = new Random();

		for(int i = 0;i < vmPairs * 2;i++) {
			proceed = true;
			while(proceed) {	
				int result = r.nextInt(numPms-0) + 0;
				if(Collections.frequency(list, result) < resCap )
				{
					list.add(result);
					cap.add(String.valueOf(result));
					proceed = false;
				}
			}
		}

		return list;
	}

	///Create file storing all parameters
	private static void PrintParameters(int numPods, int resCap, List<Integer> vmPairLocation,
			List<Integer> mBsLocation, List<Integer> frequencies, int migCoe) {
		FileOutputStream outputStream;
		try {

			outputStream = new FileOutputStream("Parameters.txt");

			//k
			String k = "k = " + numPods+ "\r\n";
			byte[] kBytes = k.getBytes();
			outputStream.write(kBytes);

			//resCap
			String rc = "ResCap = " + resCap+ "\r\n";
			byte[] rcBytes = rc.getBytes();
			outputStream.write(rcBytes);

			//vms
			String vm = "VM Locations: " + vmPairLocation.toString()+ "\r\n";
			byte[] vmBytes = vm.getBytes();
			outputStream.write(vmBytes);

			//mbs
			String mb = "MB Locations: " + mBsLocation.toString()+ "\r\n";
			byte[] mbBytes = mb.getBytes();
			outputStream.write(mbBytes);

			//f
			String f = "Frequencies: " + frequencies.toString()+ "\r\n";
			byte[] fBytes = f.getBytes();
			outputStream.write(fBytes);

			//migCoe
			String mc = "Migration Coeficient = " + migCoe+ "\r\n";
			byte[] mcBytes = mc.getBytes();
			outputStream.write(mcBytes);

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}	
	}
}
