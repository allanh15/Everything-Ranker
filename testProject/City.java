public class City {
    String name;    //city name
    FlightLeg adjecencyHead; //head of linked list of flight legs

    public City(String name) {
        this.name = name;
        this.adjecencyHead = null;
    }

    //Function to get details
    public String getName() {
        return name;
    }

    public FlightLeg getAdjecencyHead() {
        return adjecencyHead;
    }

    public void setAdjecencyHead(FlightLeg adjecencyHead) {
        this.adjecencyHead = adjecencyHead;
    }
}
