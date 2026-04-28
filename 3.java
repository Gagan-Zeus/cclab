package cloudsimm;

import java.util.*;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.provisioners.*;

/**
 * Exp3:
 * One datacenter, one host, one VM, one cloudlet.
 */
public class hgcjgh {

	public static void main(String[] args) {
		Log.printLine("Starting Exp3...");

		try {
			// Critical: CloudSim.init() must be called before creating Datacenter/Broker/VM.
			CloudSim.init(1, Calendar.getInstance(), true);

			// Requirement: 1 datacenter -> exactly one datacenter is created here.
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			DatacenterBroker broker = new DatacenterBroker("Broker");
			int brokerId = broker.getId();

			List<Vm> vmList = new ArrayList<Vm>();
			// Requirement: 1 VM -> only one VM object is created.
			// Keep image size 100 MB so debt is 35.6 with current cost model.
			Vm vm = new Vm(0, brokerId, 1000, 1, 512, 1000, 100, "Xen", new CloudletSchedulerTimeShared());
			vmList.add(vm);
			broker.submitVmList(vmList);

			UtilizationModel full = new UtilizationModelFull();
			List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
			// Requirement: 1 cloudlet -> only one cloudlet object is created.
			Cloudlet cloudlet = new Cloudlet(0, 400000, 1, 300, 300, full, full, full);
			// Critical: if userId is wrong, broker won't manage this cloudlet.
			cloudlet.setUserId(brokerId);
			// Critical: explicit VM binding avoids accidental scheduling to another VM.
			cloudlet.setVmId(vm.getId());
			cloudletList.add(cloudlet);
			broker.submitCloudletList(cloudletList);

			CloudSim.startSimulation();
			List<Cloudlet> results = broker.getCloudletReceivedList();
			CloudSim.stopSimulation();

			printCloudletList(results);
			datacenter0.printDebts();
			Log.printLine("Exp3 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Exp3 terminated due to an unexpected error.");
		}
	}

	private static Datacenter createDatacenter(String name) throws Exception {
		List<Host> hostList = new ArrayList<Host>();
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(1000)));

		// Requirement: 1 host -> only one host is added to hostList.
		hostList.add(new Host(0, new RamProvisionerSimple(2048), new BwProvisionerSimple(10000), 1000000, peList,
				new VmSchedulerTimeShared(peList)));

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList,
				10.0, 3.0, 0.05, 0.1, 0.1);

		return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(),
				0);
	}

	private static void printCloudletList(List<Cloudlet> list) {

    System.out.println("\n========== OUTPUT ==========");
    System.out.println("ID\tSTATUS\tDC ID\tVM ID\tTIME\tSTART\tFINISH");

    for (Cloudlet c : list) {

        if (c.getCloudletStatus() == Cloudlet.SUCCESS) {

            System.out.println(
                c.getCloudletId() + "\t" +
                "SUCCESS\t" +
                c.getResourceId() + "\t" +
                c.getVmId() + "\t" +
                String.format("%.2f", c.getActualCPUTime()) + "\t" +
                String.format("%.2f", c.getExecStartTime()) + "\t" +
                String.format("%.2f", c.getFinishTime())
            );
        }
    }
}
}
