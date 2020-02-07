package PlanPackage;
import java.util.List;
import java.util.*;
@SuppressWarnings("unused")
public class VM {

	int groupId;//to find vm pair connected.
	int VmOGSource;//Original location
	int VmPairSource;
	boolean isFirstVm;
	@SuppressWarnings("rawtypes")
	Dictionary<Integer,Integer> Utility = new Hashtable();
	int Frequency;
	int MigrationCost;

	public VM(int gid,int vms, int vmps, int f, int mc) {
		this.groupId = gid;
		this.VmOGSource = vms;
		this.VmPairSource = vmps;
		this.Frequency = f;
		this.MigrationCost = mc;
	}
	
	public VM() {
	}

	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	
	public boolean getVmPairSource() {
		return isFirstVm;
	}
	
	public void setVmPairSource(boolean vmNew) {
		isFirstVm = vmNew;
	}
	
	public int getVmOGSource() {
		return VmOGSource;
	}

	public void setVmOGSource(int vmOGSource) {
		VmOGSource = vmOGSource;
	}

	@SuppressWarnings("rawtypes")
	public Dictionary getUtility() {
		return Utility;
	}

	@SuppressWarnings("rawtypes")
	public void setUtility(Dictionary utility) {
		Utility = utility;
	}

	//get set vmsource
	public int getVmSource() {
		return VmOGSource;
	}

	public void setVmSource(int vmSource) {
		VmOGSource = vmSource;
	}

	public int getFrequency() {
		return Frequency;
	}

	public void setFrequency(int frequency) {
		Frequency = frequency;
	}

	public int getMigrationCost() {
		return MigrationCost;
	}
	public void setMigrationCost(int migrationCost) {
		MigrationCost = migrationCost;
	}
}
