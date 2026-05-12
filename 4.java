package cloudsimm;
import java.text.DecimalFormat;
import java.util.*;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

public class hgcjgh {
    static List<Cloudlet> cl = new ArrayList<>();
    static List<Vm> vm = new ArrayList<>();

    public static void main(String[] a) {
        Log.printLine("Starting CloudSimExample2...");
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            Datacenter d = createDC("Datacenter_0");
            DatacenterBroker b = new DatacenterBroker("Broker");
            int id = b.getId();
            vm.add(new Vm(0, id, 250, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
            vm.add(new Vm(1, id, 250, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
            b.submitVmList(vm);

            UtilizationModel u = new UtilizationModelFull();
            Cloudlet c1 = new Cloudlet(0, 250000, 1, 300, 300, u, u, u);
            c1.setUserId(id);
            Cloudlet c2 = new Cloudlet(1, 250000, 1, 300, 300, u, u, u);
            c2.setUserId(id);
            cl.add(c1);
            cl.add(c2);
            b.submitCloudletList(cl);

            b.bindCloudletToVm(0, 0);
            b.bindCloudletToVm(1, 1);
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            print(b.getCloudletReceivedList());
            d.printDebts();

            Log.printLine("CloudSimExample2 finished!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Datacenter createDC(String n) {
        List<Pe> p = new ArrayList<>();
        p.add(new Pe(0, new PeProvisionerSimple(1000)));

        List<Host> h = new ArrayList<>();
        h.add(new Host(0, new RamProvisionerSimple(2048), new BwProvisionerSimple(10000), 1000000, p, new VmSchedulerTimeShared(p)));

        DatacenterCharacteristics c = new DatacenterCharacteristics("x86", "Linux", "Xen", h, 10.0, 3.0, 0.05, 0.001, 0.0);

        try {
            return new Datacenter(n, c, new VmAllocationPolicySimple(h), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            return null;
        }
    }

    static void print(List<Cloudlet> l) {
        Log.printLine("\n========== OUTPUT ==========");
        Log.printLine("Cloudlet ID STATUS Data center ID VM ID Time Start Time Finish Time");

        DecimalFormat f = new DecimalFormat("###.##");

        for (Cloudlet c : l)
            if (c.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print(" " + c.getCloudletId() + " SUCCESS");
                Log.printLine(" " + c.getResourceId() + " " + c.getVmId() +
                        " " + f.format(c.getActualCPUTime()) +
                        " " + f.format(c.getExecStartTime()) +
                        " " + f.format(c.getFinishTime()));
            }
    }
}