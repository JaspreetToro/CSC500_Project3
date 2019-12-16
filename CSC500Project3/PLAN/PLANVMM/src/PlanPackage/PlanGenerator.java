package PlanPackage;
import java.util.ArrayList;
import java.util.Collections;
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
	//static HashMap<Integer, List<Integer>> topoMap = new HashMap<Integer, List<Integer>>();
	public static int mbDistance;
	public static int firstMB; 
	public static int lastMB;
	public static int migration;
	public static List<VM> Vms = new ArrayList<VM>();;

	public static void main(String[] args) {
		//k,rc,vmpairs,mbs,frequency,migration
		//get inputs
		//int[] inputs = SDN.GetInput();

		SetUpPLAN(4,4,2,2,2,1);
		//k,resCap,VmPairsCount,boxes,frequecy.migration
		//SetUpPLAN(inputs[0],inputs[1],inputs[2],inputs[3],inputs[4],inputs[5]);

	}

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

		List<VM> vms = CreateObjects(vmPairLocation,frequencies, migCoe);

		

		PrintParameters(numPods, resCap,vmPairLocation,MBsLocation,frequencies,migCoe);

	}

	private static List<VM> CreateObjects(List<Integer> vms, List<Integer> fs, int m) {
		int freIndex = 0;

		List<VM> VMS = new ArrayList<VM>();
		for(int i=0; i < vms.size(); i++) {
			freIndex = (int)Math.floor((i/2));

			VM vm = new VM();
			vm.groupId = freIndex;
			vm.VmOGSource = vms.get(i);
			vm.Frequency = fs.get(freIndex);
			vm.MigrationCost = m;

			VMS.add(vm);
		}

		return VMS;
	}

	static int GetUtilityCost(int sourceVm, int newVm, int endVm) {
		int utilityCost = 0;

		utilityCost = VmCCR(sourceVm,endVm,newVm) - MigCost(endVm,newVm);

		return utilityCost;
	}

	private static int VmCCR(int sourceVm, int endVm, int newVm) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int MigCost(int vm1, int vm2) {

		return migration*SDN.ShorestDistance(k, vm1, vm2);
	}

	private static int MBDistance(List<Integer> mb) {
		int temp=0;

		for(int i =0; i < mb.size()-1;i++) {
			temp += SDN.ShorestDistance(k, mb.get(i),mb.get(i+1) );
		}

		firstMB = mb.get(0);
		lastMB = mb.get(mb.size()-1);

		return temp;
	}
	private static List<Integer> GetFrequencies(int vmPairs, int frequency) {

		List<Integer> list = new ArrayList<Integer>();

		Random r = new Random();

		for(int i = 0;i < vmPairs;i++) {

			int result = r.nextInt(frequency-1) + 1;
			list.add(result);
		}
		return list;
	}

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
