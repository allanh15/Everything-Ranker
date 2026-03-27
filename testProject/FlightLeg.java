public class FlightLeg {
    int destinationIndex;   //index of the destination city in the FlightGraph's city array
    double cost;            //cost of the flight leg
    int time;               //time duration of the flight leg in minutes
    FlightLeg next;         //pointer to the next flight leg in the linked list

    public FlightLeg(int destinationIndex, double cost, int time, FlightLeg next) {
        this.destinationIndex = destinationIndex;
        this.cost = cost;
        this.time = time;
        this.next = next;
    }

    //Functions to get details
    public int getDestinationIndex() {
        return destinationIndex;
    }

    public double getCost() {
        return cost;
    }

    public int getTime() {
        return time;
    }

    public FlightLeg getNext() {
        return next;
    }

    public void setNext(FlightLeg next) {
        this.next = next;
    }
}
