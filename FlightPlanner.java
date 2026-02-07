import java.util.ArrayList;
import java.util.List;

public class FlightPlanner {

    private FlightGraph graph;

    public FlightPlanner(FlightGraph graph) {
        this.graph = graph;
    }

    //A frame on the DFS stack to represent where we are in the search
    private static class DFSFrame {
        FlightLeg nextLeg;         //next flight leg to explore
        int[] pathSoFar;          //path taken to reach this city
        int pathLength;          //length of the path so far
        double totalCost;         //total cost so far
        int totalTime;            //total time so far
    }

    //Find all simple paths from origin name to destination name using DFS
    public List<FlightPath> findAllPaths(String originName, String destinationName) {
        List<FlightPath> allPaths = new ArrayList<>();

        int originIndex = graph.findCityIndex(originName);
        int destinationIndex = graph.findCityIndex(destinationName);

        if(originIndex == -1 || destinationIndex == -1) {
            return allPaths; //one of the cities doesn't exist
        }

        int maxCities = graph.getCityCount();
        Stack<DFSFrame> stack = new Stack<DFSFrame>();

        //DFSframe setup
        DFSFrame initialFrame = new DFSFrame();
        initialFrame.nextLeg = graph.getAdjacencyHead(originIndex);
        initialFrame.pathSoFar = new int[maxCities];
        initialFrame.pathSoFar[0] = originIndex;
        initialFrame.pathLength = 1;
        initialFrame.totalCost = 0;
        initialFrame.totalTime = 0;

        stack.push(initialFrame);

        while(!stack.isEmpty()) {
            DFSFrame top = stack.peek();
            //no more legs to explore from this city, backtrack
            if (top.nextLeg == null) {
                stack.pop();
                continue;
            }

            //take next edge
            FlightLeg leg = top.nextLeg;
            top.nextLeg = leg.getNext();

            int neighborIndex = leg.getDestinationIndex();

            //check if neighbor already in path (to avoid cycles)
            if(containsCity(top.pathSoFar, top.pathLength, neighborIndex)) {
                continue; //skip this neighbor
            }

            //incremental updates
            double newTotalCost = top.totalCost + leg.getCost();
            int newTotalTime = top.totalTime + leg.getTime();

            //check if reached destination
            if(neighborIndex == destinationIndex){
                int newPathLength = top.pathLength + 1;
                String[] pathCities = new String[newPathLength];
                for(int i = 0; i < top.pathLength; i++){
                    pathCities[i] = graph.getCityName(top.pathSoFar[i]);
                }

                pathCities[newPathLength - 1] = graph.getCityName(neighborIndex);
                FlightPath fp = new FlightPath(pathCities, newTotalCost, newTotalTime);
                allPaths.add(fp);
            }
            //not destination, continue DFS
            else{
                DFSFrame child = new DFSFrame();
                child.nextLeg = graph.getAdjacencyHead(neighborIndex);
                child.pathSoFar = new int[maxCities];

                for(int i=0; i<top.pathLength; i++){
                    child.pathSoFar[i] = top.pathSoFar[i];
                }
                child.pathSoFar[top.pathLength] = neighborIndex;
                child.pathLength = top.pathLength + 1;
                child.totalCost = newTotalCost;
                child.totalTime = newTotalTime;
                stack.push(child);
            }
        }
        return allPaths;
    }

    //Helper method to check if a city index is already in the path
    private boolean containsCity(int[] path, int pathLength, int cityIndex) {
        for(int i = 0; i < pathLength; i++) {
            if(path[i] == cityIndex) {
                return true;
            }
        }
        return false;
    }

    //Sort paths by time or cost using HeapSort
    public void sortPaths(List<FlightPath> paths, char mode) {
        if(paths == null || paths.size() == 0) {
            return; //nothing to sort
        }

        FlightPath[] arr = new FlightPath[paths.size()];
        for(int i = 0; i < paths.size(); i++) {
            arr[i] = paths.get(i);
        }

        HeapSort.heapSort(arr, mode);

        paths.clear();
        for (int i=0; i < arr.length; i++) {
            paths.add(arr[i]);
        }
    }
}

