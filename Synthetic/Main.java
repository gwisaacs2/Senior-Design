import java.util.Scanner;
import java.util.ArrayList;

public class Main
{
    
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Waypoints> waypoints = new ArrayList<Waypoints>();
        insertWaypoints( waypoints );
    }
    
    public static ArrayList<Waypoints> insertWaypoints(ArrayList<Waypoints> waypoints)
    {
        Scanner scanner = new Scanner(System.in);
        boolean cont = true;
        boolean accident;
        int counter = 0;
        String lat, lon;
        
        System.out.print( "\nInstructions: \nEnter either the coordinates of the waypoint, or type \"stop\" to stop\n\n");
            
        while (cont)
        {
            do
            {
                System.out.printf("Enter the lattitude of waypoint %d: ", counter + 1);
                lat = scanner.nextLine();
                if (!isNumeric(lat) && !lat.equals("stop") ) // incorrect entry
                {
                    accident = true;
                    System.out.print("\n----------- Incorrect entry ----------\n");
                }
                else if (isNumeric(lat)) // is correct number
                {
                    accident = false;
                    waypoints.add(counter, new Waypoints() );
                    waypoints.get(counter).setLattitude(Double.parseDouble(lat));
                }
                else // if stop was typed
                {
                    accident = false;
                    cont = false;
                }
            } while (accident);

            if (cont)
            {
                do
                {
                    System.out.printf("Enter the longitude of the waypoint %d: ", counter + 1);
                    lon = scanner.nextLine();
                    if (!isNumeric(lon)) // incorrect entry
                    {
                        accident = true;
                        System.out.print("\n----------- Incorrect entry -----------\nYou already entered a lattitude. You cannot stop now.\n");
                    }
                    else // is correct number
                    {
                        accident = false;
                        waypoints.get(counter).setLongitude(Double.parseDouble(lon));
                    }
                } while(accident);
            }
            System.out.println();
            counter++;   
        }
        return waypoints;
    }

    public static boolean isNumeric(String str)
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
