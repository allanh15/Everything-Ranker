/*
The following is meant to determine the best routes between flights based on user requests. They are sorted by
either cost or time based on user preference.
Name: Lucas Herrera
Class: CS 3345.503
Date: 11/16/2025
Professor: Dr. Khan
*/
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        String flightDataFile = "Flights.txt";
        String requestFile = "Requests.txt";
        String outputFile = "Output.txt";

        if(args.length >= 3) {
            flightDataFile = args[0];
            requestFile = args[1];
            outputFile = args[2];
        }

        try{
            //Read flight data and build graph
            FlightGraph graph = readFlightData(flightDataFile);

            //Read requested paths
            List<RequestedFlight> requests = readRequests(requestFile);

            //create planner
            FlightPlanner planner = new FlightPlanner(graph);

            //open output writer
            PrintWriter writer = new PrintWriter(new File(outputFile));

            //Process each request
            for(int i = 0; i < requests.size(); i++){
                RequestedFlight req = requests.get(i);
                char mode = Character.toUpperCase(req.getMode());

                List<FlightPath> paths = planner.findAllPaths(req.getOrigin(), req.getDestination());

                planner.sortPaths(paths, mode);

                printResults(i + 1, req, paths, mode, writer);
            }

            writer.flush();
            writer.close();
            System.out.println("Flight planning completed. Results written to " + outputFile);
        } catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static FlightGraph readFlightData(String filename) throws Exception {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        //First line: number of records
        int n = Integer.parseInt(scanner.nextLine().trim());

        //Set the flight graph size
        FlightGraph graph = new FlightGraph(n+1);

        int count = 0;
        while(scanner.hasNextLine() && count < n){
            String line = scanner.nextLine().trim();
            if(line.isEmpty()){
                continue;
            }
            String[] parts = line.split("\\|");
            if(parts.length != 4) {
                continue; //skip invalid lines
            }

            String origin = parts[0].trim();
            String destination = parts[1].trim();
            double cost = Double.parseDouble(parts[2].trim());
            int time = Integer.parseInt(parts[3].trim());

            graph.addFlightLeg(origin, destination, cost, time);
            count++;
    }
        scanner.close();
        return graph;
}

    private static List<RequestedFlight> readRequests(String filename) throws Exception {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        int m = Integer.parseInt(scanner.nextLine().trim());
        List<RequestedFlight> requests = new ArrayList<>();

        int count = 0;
        while(scanner.hasNextLine() && count < m){
            String line = scanner.nextLine().trim();
            if(line.isEmpty()){
                continue;
            }
            String[] parts = line.split("\\|");
            if(parts.length != 3) {
                continue; //skip invalid lines
            }

            String origin = parts[0].trim();
            String destination = parts[1].trim();
            char mode = parts[2].trim().charAt(0);

            requests.add(new RequestedFlight(origin, destination, mode));
            count++;
        }
        scanner.close();
        return requests;
    }

    private static void printResults(int flightNumber, RequestedFlight r, List<FlightPath> paths, char mode, PrintWriter writer) {
        String modeStr = (mode == 'C') ? "Cost" : "Time";

        String header = "Flight " + flightNumber + ": " + r.getOrigin() + " to " + r.getDestination() + " (" + modeStr + ")\n";

        System.out.print(header);
        writer.print(header);

        if(paths == null || paths.size() == 0) {
            String noPathMsg = "No flight plan can be created from " + r.getOrigin() + " to " + r.getDestination() + ".";
            System.out.println(noPathMsg);
            writer.println(noPathMsg);
            return;
        }

        int max = Math.min(3, paths.size());
        for(int i = 0; i < max; i++){
            FlightPath p = paths.get(i);
            String line = "Path " + (i + 1) + ": " + p.pathAsString() + ". Time: " + p.getTotalTime() + " Cost: " + String.format("%.2f", p.getTotalCost()) + "\n";
            System.out.print(line);
            writer.print(line);
        }

        System.out.println();
        writer.println();
    }
}    
