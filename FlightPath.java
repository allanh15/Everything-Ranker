/**
 * Represents a complete flight path from origin to destination.
 * Contains the sequence of cities visited, total cost, and total time.
 */
public class FlightPath {
    private String[] cities;  // Sequence of cities in a path
    private double totalCost;  // Total cost of the flight path
    private int totalTime;     // Total time of the flight path in minutes

    /**
     * Constructs a new FlightPath with the specified route, cost, and time.
     * 
     * @param cities the sequence of cities in the flight path
     * @param totalCost the total cost of the flight path
     * @param totalTime the total time of the flight path in minutes
     */
    public FlightPath(String[] cities, double totalCost, int totalTime) {
        this.cities = cities;
        this.totalCost = totalCost;
        this.totalTime = totalTime;
    }

    /**
     * Returns the sequence of cities in this flight path.
     * 
     * @return array of city names representing the route
     */
    public String[] getCities() {
        return cities;
    }

    /**
     * Returns the total cost of this flight path.
     * 
     * @return the total cost in dollars
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Returns the total time of this flight path.
     * 
     * @return the total time in minutes
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Converts the flight path to a formatted string representation.
     * Cities are separated by " -> " to show the route sequence.
     * 
     * @return string representation of the flight path
     */
    public String pathAsString() {
        if (cities.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(cities[0]);
        
        for (int i = 1; i < cities.length; i++) {
            sb.append(" -> ").append(cities[i]);
        }
        
        return sb.toString();
    }
}
