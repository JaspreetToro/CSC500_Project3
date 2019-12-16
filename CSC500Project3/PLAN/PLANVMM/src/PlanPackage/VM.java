package PlanPackage;
import java.util.List;

@SuppressWarnings("unused")
public class VM {

	int groupId;//to find vm pair connected.
	int VmOGSource;//Original location
	int VmNew;
	int Frequency;
	int MigrationCost;

	public VM(int gid,int vms, int vmn, int f, int mc) {
		this.groupId = gid;
		this.VmOGSource = vms;
		this.VmNew = vmn;
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
	
	public int getVmNew() {
		return VmNew;
	}
	
	public void setVmNew(int vmNew) {
		VmNew = vmNew;
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
