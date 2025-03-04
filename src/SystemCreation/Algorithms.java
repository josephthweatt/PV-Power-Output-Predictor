package SystemCreation;
import java.util.ArrayList;

import ProductObjects.*;

public abstract class Algorithms {

	public Goal goal;

	protected ProductContainer[] containers; // this is used to evaluate
												// products

	/*
	 * ViableProductLists will contain all products that have the potential to
	 * be used in a system, it will be used to experiment with viable systems
	 * and pass them into the systemCreator
	 */
	protected ArrayList<Panel> viablePanels;
	protected ArrayList<Inverter> viableInverters;
	protected ArrayList<Racking> viableRacks;
	protected ArrayList<Battery> viableBatteries;
	protected ArrayList<BatteryController> viableBatteryControllers;
	protected ArrayList<BatteryMeter> viableBatteryMeters;
	protected ArrayList<DCACDisconnect> viableDCACDisconnects;
	protected ArrayList<BatteryWire> viableBatteryWires;
	protected ArrayList<PVWire> viablePVWires;

	protected ProductContainer viableSystems; // runAlgorithm() finds these
	protected FullSystem system; // temporary system to store new systems
	protected ImpossibleParameters parameters;

	protected final int HOURS_PER_YEAR = 8760; // assumes non-leap year

	// constructor called when the user wishes to have a specific voltage
	public Algorithms(Goal goal, ProductContainer[] containers) {
		this.goal = goal;

		this.containers = containers;

		parameters = new ImpossibleParameters();
	}

	/************************* public methods **********************************/

	// have not yet decided if this will return anything
	public void runAlgorithm() {
		viableSystems = new ProductContainer(FullSystem.class);
		// Overriding this method ought to implement 'super' to call findViables
		findViablePanels();
		findViableRacks();
		findViableBatteryControllers();
		findViableInverters();
		findViableBatteries();
		findViableDCACDisconnect();
		findViableBatteryMeters();
		findViablePVWires();
		findViableBatteryWires();

		if (impossibleParameters()) {
			return;
		}
	}

	// returns the ranked systems, the map may contain more than 10
	public FullSystem[] getSystems() {
		if (viableSystems.products.size() > 0) {
			FullSystem[] sysArray = new FullSystem[viableSystems.products
					.size()];
			for (int i = 0; i < viableSystems.products.size(); i++) {
				sysArray[i] = (FullSystem) viableSystems.products.get(i);
			}
			return sysArray;
		}
		return null;
	}

	// returns 'true' when user parameters cannot generate viable Systems
	public Boolean impossibleParameters() {
		if (parameters.valid == false) {
			return false;
		} else if (parameters.valid == true) {
			System.out.println("Restrictive input(s) found: "
					+ parameters.badParameter);
			System.out.println("No viable products found for: "
					+ parameters.restrictedProduct);
			return true;
		}
		return false;
	}

	/********************** private/protected methods **************************/
	// Checks to see if the system is worth checking against the PVWatts API,
	// then adds it to viableSystems if it is
	protected abstract void verifyAndAddSystem();

	// ranks the systems according to the goal
	protected abstract void rankSystems();

	// below algorithms look for viable products in the system

	protected abstract void findViablePanels();

	protected abstract void findViableRacks();

	protected abstract void findViableInverters();

	protected abstract void findViableBatteries();

	protected abstract void findViableBatteryControllers();

	protected abstract void findViableDCACDisconnect();

	// the following non-abstract methods will just store the cheapest product
	// to their respective list unless otherwise changed

	protected void findViableBatteryMeters() {
		viableBatteryMeters = new ArrayList<BatteryMeter>();
		containers[5].loToHi("price");
		viableBatteryMeters.add((BatteryMeter) containers[5].products.get(0));
	}

	protected void findViableBatteryWires() {
		viableBatteryWires = new ArrayList<BatteryWire>();
		containers[7].loToHi("price");
		viableBatteryWires.add((BatteryWire) containers[7].products.get(0));
	}

	protected void findViablePVWires() {
		viablePVWires = new ArrayList<PVWire>();
		containers[8].loToHi("price");
		viablePVWires.add((PVWire) containers[8].products.get(0));
	}

	// returns the top 'x' of systems in the viableSystem list
	protected ArrayList<Object> getBestSystems(int x) {
		ArrayList<Object> topX = new ArrayList<Object>();

		rankSystems();
		for (int i = 0; i < viableSystems.products.size(); i++) {
			// will not add systems with the same panel
			if (!hasPanel( topX,
					(Panel) ((FullSystem) (viableSystems.products.get(i))).panel)) {
				topX.add((FullSystem) viableSystems.products.get(i));
			}
		}
		return topX;
	}

	private Boolean hasPanel(ArrayList<Object> systems, Panel panel) {
		for (int i = systems.size() - 1; i >= 0; i--) {
			if (((FullSystem) systems.get(i)).panel == panel) {
				return true;
			}
		}
		return false;
	}

	/****************************************************************************
	 * ImpossibleParameters looks for user input that may be considered
	 * "restrictive". "Restrictive" in this case means that the algorithm cannot
	 * find an acceptable PV system to meet their requirements (e.g. the budget
	 * is too low. available space is too small)
	 ***************************************************************************/
	protected class ImpossibleParameters {
		// 'flase' means that there is at least one restrictive user input
		protected Boolean valid;

		protected String badParameter; // a string to hold the restrictive input
		protected String restrictedProduct; // products with no viable options

		public ImpossibleParameters() {
			valid = false;
			badParameter = "";
			restrictedProduct = "";
		}

		// method to acknowledge restrictive inputs
		public void badParameter(String product, String... fieldName) {
			valid = true;
			// bad Parameter can store multiple restrictive fields
			for (int i = 0; i < fieldName.length; i++) {
				if (badParameter.equals("")) {
					badParameter = fieldName[i];
				} else if (badParameter.contains(fieldName[i])) {
					// skip any parameters which have already been added
				} else {
					badParameter += " and " + fieldName[i];
				}
			}

			// makes note of which product types had no viable products
			if (restrictedProduct.equals("")) {
				restrictedProduct += product;
			} else {
				restrictedProduct = restrictedProduct.replaceAll(" and ", ", ");
				restrictedProduct += " and " + product;
			}
		}

		// called to report that no viable systems were found
		public void noViableSystems() {
			System.out.println("No viable systems. Could not find parts for: ");
			if (system.panel == null) {
				System.out.println("Panel");
			}
			if (system.inverter == null) {
				System.out.println("Inverter");
			}
			if (system.rack == null) {
				System.out.println("Rack");
			}
			if (system.battery == null) {
				System.out.println("Battery");
			}
			if (system.batteryControl == null) {
				System.out.println("Battery Control");
			}
			if (system.batteryMeter == null) {
				System.out.println("Battery Meter");
			}
			if (system.dcacDisconnect == null) {
				System.out.println("DCAC Disconnect");
			}
			if (system.batteryWire == null) {
				System.out.println("Battery Wire");
			}
			if (system.pvWire == null) {
				System.out.println("PV Wires");
			}
		}
	}
}
