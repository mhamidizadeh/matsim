/* *********************************************************************** *
 * project: org.matsim.*
 * TransitEventHandler.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package playground.ikaddoura.optimization.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.AgentArrivalEvent;
import org.matsim.core.api.experimental.events.AgentDepartureEvent;
import org.matsim.core.api.experimental.events.LinkLeaveEvent;
import org.matsim.core.api.experimental.events.TransitDriverStartsEvent;
import org.matsim.core.api.experimental.events.handler.AgentArrivalEventHandler;
import org.matsim.core.api.experimental.events.handler.AgentDepartureEventHandler;
import org.matsim.core.api.experimental.events.handler.LinkLeaveEventHandler;
import org.matsim.core.events.handler.TransitDriverStartsEventHandler;

/**
 * @author ikaddoura
 *
 */
public class OperatorCostHandler implements TransitDriverStartsEventHandler, LinkLeaveEventHandler, AgentDepartureEventHandler, AgentArrivalEventHandler {
	private Network network;
	private double vehicleKm;
	private double operatingHours_excludingSlackTimes;
	
	private Map<Id, Double> ptDriverId2firstDepartureTime = new HashMap<Id, Double>();
	private Map<Id, Double> ptDriverId2lastArrivalTime = new HashMap<Id, Double>();
	private Map<Id, Double> ptDriverId2lastRouteStartTime = new HashMap<Id, Double>();
	
	private final List<Id> ptDriverIDs = new ArrayList<Id>();
	private final List<Id> ptVehicleIDs = new ArrayList<Id>();
	
	public OperatorCostHandler(Network network) {
		this.network = network;
	}

	@Override
	public void reset(int iteration) {
		this.ptDriverId2firstDepartureTime.clear();
		this.ptDriverId2lastArrivalTime.clear();
		this.ptDriverId2lastRouteStartTime.clear();
		this.ptDriverIDs.clear();
		this.ptVehicleIDs.clear();
		this.vehicleKm = 0.0;
		this.operatingHours_excludingSlackTimes = 0.0;
	}
	
	@Override
	public void handleEvent(TransitDriverStartsEvent event) {
		
		if (!this.ptDriverIDs.contains(event.getDriverId())){
			this.ptDriverIDs.add(event.getDriverId());
		}
		
		if (!this.ptVehicleIDs.contains(event.getVehicleId())){
			this.ptVehicleIDs.add(event.getVehicleId());
		}		

	}
	
	@Override
	public void handleEvent(LinkLeaveEvent event) {
		
		if (ptDriverIDs.contains(event.getPersonId())){
			this.vehicleKm = this.vehicleKm + network.getLinks().get(event.getLinkId()).getLength() / 1000.;
		}
	}
	
	public List<Id> getVehicleIDs() {
		return this.ptVehicleIDs;
	}
	
	public double getVehicleKm() {
		return this.vehicleKm;
	}

	@Override
	public void handleEvent(AgentDepartureEvent event) {
		
		if (ptDriverIDs.contains(event.getPersonId())){
			
			if (this.ptDriverId2firstDepartureTime.containsKey(event.getPersonId())){
				if (event.getTime() < this.ptDriverId2firstDepartureTime.get(event.getPersonId())){
					this.ptDriverId2firstDepartureTime.put(event.getPersonId(), event.getTime());
				}
				else {
					// not the first departure time of this public vehicle
				}
			}
			
			else {
				this.ptDriverId2firstDepartureTime.put(event.getPersonId(), event.getTime());
			}
			
			// for the operating times without slack times
			this.ptDriverId2lastRouteStartTime.put(event.getPersonId(), event.getTime());
		}
	}

	@Override
	public void handleEvent(AgentArrivalEvent event) {
		if (ptDriverIDs.contains(event.getPersonId())){
			if (this.ptDriverId2lastArrivalTime.containsKey(event.getPersonId())){
				if (event.getTime() > this.ptDriverId2lastArrivalTime.get(event.getPersonId())){
					this.ptDriverId2lastArrivalTime.put(event.getPersonId(), event.getTime());
				}
				else {
					// not the last arrival time of this public vehicle
				}
			}
			else {
				this.ptDriverId2lastArrivalTime.put(event.getPersonId(), event.getTime());
			}
			
			// for the operating times without slack times
			double routeOperatingTime = event.getTime() - this.ptDriverId2lastRouteStartTime.get(event.getPersonId());
			this.operatingHours_excludingSlackTimes = this.operatingHours_excludingSlackTimes + routeOperatingTime;
		}
	}
	
	public double getVehicleHours_includingSlackTimes() {
		double vehicleSeconds = 0;
		for (Id id : this.ptDriverId2firstDepartureTime.keySet()){
			vehicleSeconds = vehicleSeconds + ((this.ptDriverId2lastArrivalTime.get(id) - this.ptDriverId2firstDepartureTime.get(id)));
		}
		return vehicleSeconds / 3600.0;
	}

	public double getOperatingHours_excludingSlackTimes() {
		return operatingHours_excludingSlackTimes / 3600.0;
	}

}
