package PlanPackage;
import java.util.List;
import java.util.*;
@SuppressWarnings("unused")
public class VM {

	int groupId;//to find vm pair connected.
	int VmOGSource;//Original location
	int VmPairSource;
	@SuppressWarnings("rawtypes")
	Dictionary Utility = new Hashtable();
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

	public int getVmPairSource() {
		return VmPairSource;
	}
	
	public void setVmPairSource(int vmNew) {
		VmPairSource = vmNew;
	}
	
	public int getVmOGSource() {
		return VmOGSource;
	}

	public void setVmOGSource(int vmOGSource) {
		VmOGSource = vmOGSource;
	}

	public Dictionary getUtility() {
		return Utility;
	}

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
