public class FlightGraph {
    private City[] cities;
    private int cityCount;

    public FlightGraph(int maxCities) {
        cities = new City[maxCities];
        cityCount = 0;
    }

    //Find the index of a city by its name
    public int findCityIndex(String cityName) {
        for (int i = 0; i < cityCount; i++) {
            if (cities[i].getName().equals(cityName)) {
                return i;
            }
        }
        return -1; //city not found
    }

    //Get the index of the city, adding it if it doesn't exist
    public int getOrAddCityIndex(String cityName) {
        int index = findCityIndex(cityName);
        if (index == -1) {
            cities[cityCount] = new City(cityName);
            index = cityCount;
            cityCount++;
        }
        return index;
    }

    //Add a flight leg from origin to destination
    public void addFlightLeg(String originName, String destinationName, double cost, int time) {
        int originIndex = getOrAddCityIndex(originName);
        int destinationIndex = getOrAddCityIndex(destinationName);

        //Origin -> Destination
        FlightLeg headOrigin = cities[originIndex].getAdjecencyHead();
        FlightLeg newLeg = new FlightLeg(destinationIndex, cost, time, headOrigin);
        cities[originIndex].setAdjecencyHead(newLeg);

        //Destination -> Origin (bidirectional)
        FlightLeg headDestination = cities[destinationIndex].getAdjecencyHead();
        FlightLeg newLegReverse = new FlightLeg(originIndex, cost, time, headDestination);
        cities[destinationIndex].setAdjecencyHead(newLegReverse);
    }

    public int getCityCount() {
        return cityCount;
    }

    public String getCityName(int index) {
        if(index < 0 || index >= cityCount) {
            return null; //invalid index
        }
        return cities[index].getName();
    }

    public FlightLeg getAdjacencyHead(int cityIndex) {
        if(cityIndex < 0 || cityIndex >= cityCount) {
            return null; //invalid index
        }
        return cities[cityIndex].getAdjecencyHead();
    }
}
