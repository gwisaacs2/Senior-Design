import java.util.Scanner;
import java.lang.Math;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.geom.AffineTransform;


public class Main
{
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        String input = args[0];
        String output = args[1];
        int sweeps = Integer.valueOf(args[2]);
        int heading = Integer.valueOf(args[3]);
        if (heading < 0 || heading > 360)
        {
            System.out.println("Heading not valid...");
            System.exit(0);
        }
        double radians = Math.toRadians(90 - heading); //converts heading (compass) to trig. longitude is x axis and lat is y axis

        final String input_image = "../Pictures/Bahia_de_boqueron_colored.jpg";
        final String working_image = "../Pictures/working_image.jpg";

        placeObstacles(input_image, working_image);

        ArrayList<Waypoints> waypoints = new ArrayList<Waypoints>(); //array containing our waypoints

        waypoints = insertWaypoints( waypoints, input); //insert the first run
        waypoints = findPath(waypoints, radians, sweeps); // get waypoints for the rest of the path

        getDepths(waypoints, working_image); // find the depths at each waypoint
        markObstacles(waypoints); // outputs file with flags at the obstacles

        outputWaypoints(waypoints, "../Outputs/" + output + ".txt"); //output the track of the whole path
        outputNMEA(waypoints);


    }

    public static void placeObstacles(String input_image, String output_image)
    {
        final int NUM_OBSTACLES = 7;
        System.out.println("Creating working image..");

        BufferedImage original, working, temp, ob1, ob2, ob3, ob4, ob5;
        int height, width;
        try
        {
            System.out.println("Reading image...");
            File infile = new File(input_image);
            if (!infile.canRead())
                System.out.println("Cannot read");
            FileInputStream fis = new FileInputStream(infile);
            original = ImageIO.read(fis);

            width = original.getWidth();
            height = original.getHeight();

            System.out.println("Copying original image to working image");
            working = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = working.createGraphics();
            g.drawImage(original, 0, 0, null);

            System.out.println("Loading obstacles...");

            // ob 1
            File ob1_file = new File("../Pictures/ob1.png");
            if (!ob1_file.canRead())
                System.out.println("Cannot read ob1");
            FileInputStream fis_ob1 = new FileInputStream(ob1_file);
            ob1 = ImageIO.read(fis_ob1);

            // ob2
            File ob2_file = new File("../Pictures/ob2.png");
            if (!ob2_file.canRead())
                System.out.println("Cannot read ob2");
            FileInputStream fis_ob2 = new FileInputStream(ob2_file);
            ob2 = ImageIO.read(fis_ob2);

            // ob3
            File ob3_file = new File("../Pictures/ob3.png");
            if (!ob3_file.canRead())
                System.out.println("Cannot read ob3");
            FileInputStream fis_ob3 = new FileInputStream(ob3_file);
            ob3 = ImageIO.read(fis_ob3);

            // ob4
            File ob4_file = new File("../Pictures/ob4.png");
            if (!ob4_file.canRead())
                System.out.println("Cannot read ob4");
            FileInputStream fis_ob4 = new FileInputStream(ob4_file);
            ob4 = ImageIO.read(fis_ob4);

            // ob5
            File ob5_file = new File("../Pictures/ob5.png");
            if (!ob5_file.canRead())
                System.out.println("Cannot read ob5");
            FileInputStream fis_ob5 = new FileInputStream(ob5_file);
            ob5 = ImageIO.read(fis_ob5);



            System.out.println("Placing obstacles...");
            int obstacle, xcoord, ycoord;
            double angle;
            for(int i = 0; i < NUM_OBSTACLES; i++)
            {
                angle = Math.floor(Math.random() * 360 + 1);
                xcoord = (int) (Math.random() * (1200 - 200));
                ycoord = (int) (Math.random() * (500 - 100) + 100);
                obstacle = (int) (Math.random() * 5 + 1);
                switch (obstacle)
                {
                    case 1:
                        temp = rotateImageByDegrees(ob1, angle);
                        break;
                    case 2:
                        temp = rotateImageByDegrees(ob2, angle);
                        break;
                    case 3:
                        temp = rotateImageByDegrees(ob3, angle);
                        break;
                    case 4:
                        temp = rotateImageByDegrees(ob4, angle);
                        break;
                    case 5:
                        temp = rotateImageByDegrees(ob5, angle);
                        break;
                    default:
                        temp = rotateImageByDegrees(ob2, angle);
                }
                g.drawImage(temp, xcoord, ycoord, null);

            }
            System.out.println("Writing image...");
            File outfile = new File(output_image);
            ImageIO.write(working, "jpg", outfile);

        } catch (Exception e) {System.out.println(e);}
    }

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle)
    {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }

    public static void getDepths(ArrayList<Waypoints> waypoints, String image_location)
    {
        final int MAX_DEPTH = 60;
        final int MIN_DEPTH = 0;
        final int MAX_HUE = 240;
        final int MIN_HUE = 0;

        final double LAT_MIN =  18.001498;
        final double LAT_MAX =  18.034106;
        final double LON_MIN = -67.228883;
        final double LON_MAX = -67.163433;

        System.out.println("Scanning the water for debris...");
        System.out.println("Reading from " + image_location);

        BufferedImage image;
        int height, width;
        double lat, lon;
        try
        {
            File file = new File(image_location);
            if (!file.canRead())
                System.out.println("Cannot read");
            FileInputStream fis = new FileInputStream(file);
            image = ImageIO.read(fis);

            width = image.getWidth();
            height = image.getHeight();
//            System.out.printf("\n---------------\nHeight: %d\nWidth: %d\n------------\n", height, width);

            int i, j;
            float hue;
            float [] hsb;
            double depth, ratio;
            for (Waypoints item: waypoints)
            {
                lat = item.getLattitude();
                lon = item.getLongitude();
                j = (int) Math.floor( Math.abs( (lat - LAT_MAX) / (LAT_MAX - LAT_MIN) * height) ); // subtracting from lat max because coordinates of the pixels are row & column, but the lat max is at the top of the picture
                i = (int) Math.floor( Math.abs( (lon - LON_MIN) / (LON_MAX - LON_MIN) * width) );
                Color c = new Color(image.getRGB(i,j));
                hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                hue = hsb[0];
                ratio = ( (hue * 360) - MIN_HUE ) / (MAX_HUE - MIN_HUE);
                depth = ratio * (MAX_DEPTH - MIN_DEPTH) + MIN_DEPTH;
                if (depth > MAX_DEPTH)
                    depth = MAX_DEPTH;
                if (depth < 0)
                {
                    System.out.println("Something is difeinitely wrong because you got a negative depth.");
                }

                item.setDepth( (int) depth);
//                System.out.printf("\nAt %d row and %d column or %.7f   %.7f, the hue is %.0f and the depth is %d\n", i, j, lat, lon, hue*360, (int) depth);
            }
        } catch (Exception e) {}


    }

    public static ArrayList<Waypoints> findPath(ArrayList<Waypoints> waypoints, double radians, int sweeps)
    {
        System.out.println("Finding the optimal path...");
        final double CONST_VAL = 0.0004; // distance each sweep from the last (from the shore)
        sweeps = sweeps - 1; // number of total sweeps - 1 because we did first sweep
        double lat, lon;
        int initWaypoints = waypoints.size(); // number of initial waypoints
        int index = initWaypoints; // already did the first sweep
        int prev_index; // this is the waypoint that is in between the current waypoint and the shore
        for (int i = 0; i < sweeps; i++)
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

    public static void outputNMEA(ArrayList<Waypoints> waypoints)
    {
        String output = "../Inputs/NMEA_Input.txt";
        System.out.println("Writing the NMEA input to " + output);
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            PrintStream printer = new PrintStream(fileOutputStream);

            double lat, lon;
            int depth;

            for (Waypoints item : waypoints)
            {
                lat = item.getLattitude();
                lon = item.getLongitude();
                depth = item.getDepth();
                printer.printf("%.7f\n%.7f\n%d\n", lat, lon, depth);
            }
            printer.close();
            System.out.println("Successfully wrote to the NMEA input.");
        } catch (IOException e)
        {
            System.out.println("An error occurred writing the NMEA input file.");
            e.printStackTrace();
        }
    }

    public static void markObstacles(ArrayList<Waypoints> waypoints)
    {
        final int OBSTACLE_THRESHOLD = 8;
        final String ICON = "Flag, Green";
        String output = "../Outputs/Obstacles.txt";
        System.out.println("Writing the obstacles to " + output);
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            PrintStream printer = new PrintStream(fileOutputStream);
            double lat, lon;
            int counter = 0, depth;

            printer.println("type\tlatitude\tlongitude\tname\ticon");


            for (Waypoints item : waypoints)
            {
                lat = item.getLattitude();
                lon = item.getLongitude();
                depth = item.getDepth();

                if (depth < OBSTACLE_THRESHOLD)
                {
                    counter++;
                    printer.printf("W\t%.7f\t%.7f\t\t%s\n", lat, lon, ICON);
                }
            }
            printer.close();
            if (counter < 1)
            {
                System.out.println("No obstacles found");
            }
            System.out.println("Successfully wrote to the obstacle output.");
        } catch (IOException e)
        {
            System.out.println("An error occurred writing the obstacle output file.");
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
