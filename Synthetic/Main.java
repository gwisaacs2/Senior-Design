import java.util.Scanner;

public class Main
{
    public Waypoints [] insertWaypoints(Waypoints [] waypoints)
    {
        bool continue = true;
        bool isNumber;
        int counter = 0;
        String lat, lon;
        
        System.out.print("\nInstructions: Enter either the coordinates of the waypoint, or type ""stop"" to stop\n");
            
        while (continue)
        {
            System.out.printf("Enter the lattitude of waypoint %d: ", counter)
            lat = scanner.nextLine();
            if (isNumeric(lat))
            {
                waypoints[counter]
            }

            if (lat == "stop")
                continue = false;
            if (continue)
            {
                
            }
            counter++;   
        }
    }

    public static boolean isNumeric(String str) {
    try
    {
        Double.parseDouble(str);
        return true;
    }
    catch(NumberFormatException e)
    {
        return false;
    }
}

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Waypoints waypoints [] = new Waypoints[1];
        waypoints[0] = new Waypoints(19.656112, -70.297615);
        waypoints[0].displayNMEA();
        if (Double.parseDouble("12") == 0 )
            System.out.print("false");
        else
            System.out.print("true");
    }
    
    
}
