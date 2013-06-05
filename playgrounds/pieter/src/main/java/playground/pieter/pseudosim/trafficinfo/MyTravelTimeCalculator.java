/**
 * 
 */
package playground.pieter.pseudosim.trafficinfo;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.AgentArrivalEvent;
import org.matsim.core.api.experimental.events.AgentDepartureEvent;
import org.matsim.core.api.experimental.events.AgentStuckEvent;
import org.matsim.core.api.experimental.events.LinkEnterEvent;
import org.matsim.core.api.experimental.events.LinkLeaveEvent;
import org.matsim.core.api.experimental.events.TransitDriverStartsEvent;
import org.matsim.core.api.experimental.events.VehicleArrivesAtFacilityEvent;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;
import org.matsim.core.trafficmonitoring.TravelTimeCalculatorConfigGroup;

import playground.pieter.pseudosim.controler.listeners.MobSimSwitcher;

/**
 * @author fouriep
 *
 */
public class MyTravelTimeCalculator extends TravelTimeCalculator {
	public MyTravelTimeCalculator(Network network,
			TravelTimeCalculatorConfigGroup ttconfigGroup) {
		super(network,ttconfigGroup.getTraveltimeBinSize(),50*3600, ttconfigGroup);
	}
	


	@Override
	public void reset(int iteration) {
		if (MobSimSwitcher.isMobSimIteration) {
			Logger.getLogger(this.getClass()).error("Calling reset on traveltimecalc");
			super.reset(iteration);
		}
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.LinkEnterEvent)
	 */
	@Override
	public void handleEvent(LinkEnterEvent e) {
		 if (MobSimSwitcher.isMobSimIteration)
		super.handleEvent(e);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.LinkLeaveEvent)
	 */
	@Override
	public void handleEvent(LinkLeaveEvent e) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(e);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.AgentDepartureEvent)
	 */
	@Override
	public void handleEvent(AgentDepartureEvent event) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(event);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.AgentArrivalEvent)
	 */
	@Override
	public void handleEvent(AgentArrivalEvent event) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(event);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.VehicleArrivesAtFacilityEvent)
	 */
	@Override
	public void handleEvent(VehicleArrivesAtFacilityEvent event) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(event);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.TransitDriverStartsEvent)
	 */
	@Override
	public void handleEvent(TransitDriverStartsEvent event) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(event);
	}



	/* (non-Javadoc)
	 * @see org.matsim.core.trafficmonitoring.TravelTimeCalculator#handleEvent(org.matsim.core.api.experimental.events.AgentStuckEvent)
	 */
	@Override
	public void handleEvent(AgentStuckEvent event) {
		if (MobSimSwitcher.isMobSimIteration)
			super.handleEvent(event);
	}
	

}
