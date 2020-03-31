import java.lang.Math;

public class Waypoints
{
    
    double decimal_lat, decimal_long;
    double NMEA_lat,    NMEA_long;
    char char_lat, char_long;
    
    Waypoints(double lattitude, double longitude)
    {
        this.decimal_lat = lattitude;
        this.decimal_long = longitude;
    }
    void convertToNMEA()
    {
        double lat, lon;
        lat = Math.abs(this.decimal_lat);
        lon = Math.abs(this.decimal_long);
        
        double temp = lat - Math.floor(lat);
        this.NMEA_lat = (Math.floor(lat) * 100) + (temp * 60);
        
        temp = lon - Math.floor(lon);
        this.NMEA_long = (Math.floor(lon) * 100) + (temp * 60);
    }
    
    void displayNMEA()
    {
        convertToNMEA();
        char lat, lon;
        if (this.decimal_lat > 0)
            lat = 'N';
        else
            lat = 'S';
        if (this.decimal_long > 0)
            lon = 'E';
        else
            lon = 'W';
        
        System.out.printf("The NMEA coordinates for this waypoint are %f%c %f%c\n", this.NMEA_lat, lat, this.NMEA_long, lon);
    }
}