import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;


public class Main
{
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        String input = args[0];
        String output = args[1];
        int heading = Integer.valueOf(args[2]);
        if (heading < 0 || heading > 360)
        {
            System.out.println("Heading not valid...");
            System.exit(0);
        }
        double radians = Math.toRadians(90 - heading); //converts heading (compass) to trig. longitude is x axis and lat is y axis

        ArrayList<Waypoints> waypoints = new ArrayList<Waypoints>(); //array containing our waypoints
        waypoints = insertWaypoints( waypoints, input + ".txt" ); //insert the first run
        waypoints = findPath(waypoints, radians);
        outputWaypoints(waypoints, output + ".txt");

    }

    public static ArrayList<Waypoints> findPath(ArrayList<Waypoints> waypoints, double radians)
    {
        System.out.println("Finding the optimal path...");
        final double CONST_VAL = 0.008; // distance each sweep from the last (from the shore)
        final int NUM_SWEEPS = 8 - 1; // number of total sweeps - 1 because we did first sweep
        double lat, lon;
        int initWaypoints = waypoints.size(); // number of initial waypoints
        int index = initWaypoints; // already did the first sweep
        int prev_index; // this is the waypoint that is in between the current waypoint and the shore
        for (int i = 0; i < initWaypoints * NUM_SWEEPS; i++)
        {
            for(int j = 0; j < initWaypoints; j++)
            {
                prev_index = index - 1 - (2 * j); // did the math here
                lat = waypoints.get(prev_index).getLattitude() + CONST_VAL * Math.sin(radians);
                lon = waypoints.get(prev_index).getLongitude() + CONST_VAL * Math.cos(radians);
                waypoints.add(index, new Waypoints(lat, lon));

                index++;
            }

        }
        return waypoints;

    }

    public static void outputWaypoints(ArrayList<Waypoints> waypoints, String output)
    {
        System.out.println("Outputting the optimal path to " + output + "...");
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            PrintStream printer = new PrintStream(fileOutputStream);

            double lat, lon;

            printer.println("type\tlatitude\tlongitude\tname\tdesc");
            for (Waypoints item : waypoints)
            {
                lat = item.getLattitude();
                lon = item.getLongitude();
                printer.printf("T\t%.7f\t%.7f\n", lat, lon);
            }
            printer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e)
        {
            System.out.println("An error occurred writing the file.");
            e.printStackTrace();
        }
    }
    
    public static ArrayList<Waypoints> insertWaypoints(ArrayList<Waypoints> waypoints, String input)
    {
        System.out.println("Reading GPS coordinates from " + input + "...");
        Scanner scanner;
        try
        {
            File file = new File(input); //opens input file
            scanner = new Scanner(file);
            scanner.nextLine(); // skips the header
            scanner.next(); // skips the T

            int counter = 0;
            waypoints.add(counter, new Waypoints()); //inputs first values
            waypoints.get(counter).setLattitude(scanner.nextDouble());
            waypoints.get(counter).setLongitude(scanner.nextDouble());

            scanner.nextLine(); // skips the distance traveled

            while(scanner.hasNext())
            {
                counter++;
                waypoints.add(counter, new Waypoints());
                scanner.next(); // skips the T
                waypoints.get(counter).setLattitude(scanner.nextDouble() );
                waypoints.get(counter).setLongitude(scanner.nextDouble() );
            }

        } catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return waypoints;
    }

    public static boolean isNumeric(String str) // checks if the string is a number or not
    {
        try
        {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e)
        {
            return false;
        }
    }
    
    
}
