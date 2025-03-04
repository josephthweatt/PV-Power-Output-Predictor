import java.io.IOException;
import ProductObjects.Goal;
import SystemCreation.*;

/* 
 * Current Version created 4-20
 * doing some other debugging before putting everything in the app
 */

public class PowerInfoMain {
	
	public static void main(String[] args) throws IOException {
		// we use Tempe's coordinates for the test case
		String address = "Tempe AZ 85281";
		
		// Using product container to compute the meters squared of its panels
		SystemManager sysMan = new SystemManager();

		Goal goal = new Goal("pricing", 25000.0, 4, 35.0, address);
		sysMan.setGoal(goal);
		sysMan.setSystemsFromAlgorithm();
		
		// print systems
		for (int i = 0; i < sysMan.getSystems().length; i++) {
			System.out.println("System #" + (i + 1));
			System.out.println("\tCost: " + sysMan.getSystem(i).cost);
			System.out.println("\tYearly KWH: " + sysMan.getSystem(i).yearlyEnergy);
			System.out.println("\tSize (KW): " + (double) (sysMan.getSystem(i).panel.systemCap 
					* sysMan.getSystem(i).panel.panelCount) / 1000);
			System.out.println("\tSize (m): " + sysMan.getSystem(i).realPanelArea);
			System.out.println("\tPanel: " + sysMan.getSystem(i).panel.name);
			System.out.println("\tRack: " + sysMan.getSystem(i).rack.name);
			System.out.println("\tInverter: " + sysMan.getSystem(i).inverter.name);
			System.out.println("\tBattery: " + sysMan.getSystem(i).battery.name);
			System.out.println("\tBattery Controller: " + sysMan.getSystem(i).batteryControl.name);
			System.out.println("\tBattery Meter: " + sysMan.getSystem(i).batteryMeter.name);
			System.out.println("\tDisconnect: " + sysMan.getSystem(i).dcacDisconnect.name);
			System.out.println("\tPVWire: " + sysMan.getSystem(i).pvWire.name);
			System.out.println("\tBattery Wire: " + sysMan.getSystem(i).batteryWire.name);
			System.out.println();
		}
	}
}
