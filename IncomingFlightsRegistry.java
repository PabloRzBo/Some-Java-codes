package aed.airport;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Pair;
import es.upm.aedlib.priorityqueue.*;
import es.upm.aedlib.map.*;
import es.upm.aedlib.positionlist.*;

/**
 * A registry which organizes information on airplane arrivals.
 */
public class IncomingFlightsRegistry {

	private PriorityQueue<Long, String> register;
	private Map<String, Entry<Long, String>> tower;

	/**
	 * Constructs an class instance.
	 */
	public IncomingFlightsRegistry() {
		register = new SortedListPriorityQueue<>();
		tower = new HashTableMap<>();
	}

	/**
	 * A flight is predicted to arrive at an arrival time (in seconds).
	 */
	public void arrivesAt(String flight, long time) {
		Entry<Long, String> vuelo = tower.get(flight);
		if(vuelo != null)
			register.remove(vuelo);
		tower.put(flight, register.enqueue(time, flight));
	}

	/**
	 * A flight has been diverted, i.e., will not arrive at the airport.
	 */
	public void flightDiverted(String flight) {
		Entry<Long, String> vuelo = tower.remove(flight);
		if (vuelo != null)
			register.remove(vuelo);
	}

	/**
	 * Returns the arrival time of the flight.
	 * 
	 * @return the arrival time for the flight, or null if the flight is not
	 *         predicted to arrive.
	 */
	public Long arrivalTime(String flight) {
		Long res = null;
		Entry<Long, String> vuelo = tower.get(flight);
		if (vuelo != null)
			res = vuelo.getKey();
		return res;
	}

	/**
	 * Returns a list of "soon" arriving flights, i.e., if any is predicted to
	 * arrive at the airport within nowTime+180 then adds the predicted earliest
	 * arriving flight to the list to return, and removes it from the registry.
	 * Moreover, also adds to the returned list, in order of arrival time, any other
	 * flights arriving withinfirstArrivalTime+120; these flights are also removed
	 * from the queue of incoming flights.
	 * 
	 * @return a list of soon arriving flights.
	 */
	public PositionList<FlightArrival> arriving(long nowTime) {
		PositionList<FlightArrival> res = new NodePositionList<FlightArrival>();
		if (!register.isEmpty() && register.first().getKey() <= nowTime + 180) {
			Long colisionTime = register.first().getKey() + 120;
			while (!register.isEmpty() && register.first().getKey() <= colisionTime) {
				Entry<Long, String> vuelo = register.dequeue();
				String code = vuelo.getValue();
				Long time = vuelo.getKey();
				tower.remove(code);
				res.addLast(new FlightArrival(code, time));
			}
		}
		return res;
	}

}
