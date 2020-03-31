import java.util.Scanner;

public class Main
{
    /*
    private Map<Double, Double> InsertWaypoints(Map)
    {
        bool continue = true;
        int counter = 0;
        String ans;
        
        System.out.print("\nInstructions: Enter either the coordinates of the waypoint, or type ""stop"" to stop\n");
            
        while (continue)
        {
            counter++;
            System.out.printf("Enter the coordinates of waypoint %d: ", counter)
            ans = scanner.nextLine();
            if (ans == "stop")
                continue = false;
            if (continue)
            {
                
            }
                
        }
    }
    */
    
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Waypoints waypoints [] = new Waypoints[1];
        waypoints[0] = new Waypoints(19.656112, -70.297615);
        waypoints[0].displayNMEA();
    }
    
    
}
