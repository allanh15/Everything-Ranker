public class RequestedFlight {
    String origin;
    String destination;
    char mode; // 'C' for cost, 'T' for time

    public RequestedFlight(String origin, String destination, char mode) {
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
    }

    //Methods to get details of the requested flight
    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public char getMode() {
        return mode;
    }
}
