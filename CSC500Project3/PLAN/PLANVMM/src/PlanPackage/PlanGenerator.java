package PlanPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;
import java.io.*;

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
	public static List<VM> Vms = new ArrayList<VM>();;

	public static void main(String[] args) {
		//k,rc,vmpairs,mbs,frequency,migration
		//get inputs
		//int[] inputs = SDN.GetInput(); // Uncomment to get out of testing mode

		//testing data
		SetUpPLAN(4,4,2,2,2,1);
		//k,resCap,VmPairsCount,boxes,frequecy.migration
		//SetUpPLAN(inputs[0],inputs[1],inputs[2],inputs[3],inputs[4],inputs[5]); // Uncomment to get out of testing mode

	}

	///
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

		//manipulate the objects to produce outcome wanted
		GenerateUtilityForVMs();


		PrintParameters(numPods, resCap,vmPairLocation,MBsLocation,frequencies,migCoe);

	}

	//Generate Utility for each Vm
	private static void GenerateUtilityForVMs() {

		for(int i = 0; i < Vms.size();i++) {
			Vms.get(i).Utility = GetUtilityCost(Vms.get(i));
		}

	}

	//Calculate Utility for each possible PM
	static Dictionary GetUtilityCost(VM vm) {
		int utilityCost = 0;

		for(int i =0; i < numPms; i++) {

			if(i != vm.VmOGSource)
				vm.Utility.put(i, VmCCR(vm.VmOGSource,i,vm.VmPairSource, vm.Frequency) - MigCost(i,vm.VmPairSource,vm.MigrationCost));

		}

		return vm.Utility;
	}

	//This is the only equation that needs to be fixed.
	private static int VmCCR(int sourceVm, int endVm, int newVm,int freq) {

		SDN.ShorestDistance(k,sourceVm , endVm);
		return 0;
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
				if(Collections.frequency(list, result) <= resCap )
				{
					list.add(result);
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
