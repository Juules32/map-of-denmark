package Utils;

import DataStructures.Node;
import DataStructures.Way;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HelperMethods {

    //Calculates euclidean distance from one Node to another
    public static double distFromTo(double fromLat, double fromLon, double toLat, double toLon) {
        return Math.sqrt(Math.pow((fromLat-toLat), 2) +
               Math.pow((fromLon-toLon), 2));
    }

    public static double distFromTo(double fromLat, double fromLon, Node toNode) {
        return Math.sqrt(Math.pow((fromLat-toNode.lat), 2) +
               Math.pow((fromLon-toNode.lon), 2));
    }

    public static double distFromTo(Node from, Node to) {
        return Math.sqrt(Math.pow((from.lat-to.lat), 2) +
               Math.pow((from.lon-to.lon), 2));
    }

    public static double metersFromTo(Node from, Node to) {
        return Math.sqrt(Math.pow((from.lat-to.lat), 2) +
               Math.pow((from.lon-to.lon), 2))*111.139;
    }

    
    //Used to draw a way (similar method exists in RTree class)
    public static void drawWay(Way way, GraphicsContext gc) {
        drawWay(way, true, "Blue", gc);
    }

    //Iterates through all nodes in a way to draw it
    public static void drawWay(Way way, boolean isLine, String theme, GraphicsContext gc) {
        if(way.nodes.length < 2) return;
        
        gc.beginPath();
        gc.moveTo(way.nodes[0].lon, way.nodes[0].lat);
        for (int i = 1; i < way.nodes.length; i++) {
            //If the current node has value null, move the pencil to next point
            if(way.nodes[i] == null && i != way.nodes.length-1) {
                gc.moveTo(way.nodes[i+1].lon, way.nodes[i+1].lat);
            }

            //Otherwise, draw a line to next point
            else if (way.nodes[i] != null) {
                gc.lineTo(way.nodes[i].lon, way.nodes[i].lat);
            }
            
        }
        
        int red = way.red;
        int green = way.green;
        int blue = way.blue;

        //Applies color changes according to current theme
        if(theme != null && !theme.equals("Default")) {
            if(theme.equals("Blue")) {
                red = 0;
                green = 0;
                blue = 255;
            }
            else if(theme.equals("High Saturation")) {
                if(red > blue && red > green) red += 50;
                if(blue > red && blue > green) blue += 50;
                if(green > red && green > blue) green += 50;

                if(red < blue && red < green) red -= 50;
                if(blue < red && blue < green) blue -= 50;
                if(green < red && green < blue) green -= 50;

                if(red < 0) red = 0;
                if(green < 0) green = 0;
                if(blue < 0) blue = 0;

                if(red > 255) red = 255;
                if(green > 255) green = 255;
                if(blue > 255) blue = 255;   
            }
            else if (theme.equals("Dimmed")) {
                red -= 50;
                if(red < 0) red = 0;
                green -= 50;
                if(green < 0) green = 0;
                blue -= 50;
                if(blue < 0) blue = 0;
            }
            else if (theme.equals("Brightened")) {
                red += 50;
                if(red > 255) red = 255;
                green += 50;
                if(green > 255) green = 255;
                blue += 50;
                if(blue > 255) blue = 255;
            }
            else if(theme.equals("Inverted")) {
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;
            }
            else if (theme.equals("Inverted and High Saturation")) {
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                if(red > blue && red > green) red += 50;
                if(blue > red && blue > green) blue += 50;
                if(green > red && green > blue) green += 50;

                if(red < blue && red < green) red -= 50;
                if(blue < red && blue < green) blue -= 50;
                if(green < red && green < blue) green -= 50;

                if(red < 0) red = 0;
                if(green < 0) green = 0;
                if(blue < 0) blue = 0;

                if(red > 255) red = 255;
                if(green > 255) green = 255;
                if(blue > 255) blue = 255;
            }
            else if (theme.equals("Inverted and Dimmed")) {
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                red -= 50;
                if(red < 0) red = 0;
                green -= 50;
                if(green < 0) green = 0;
                blue -= 50;
                if(blue < 0) blue = 0;
            }
            else if (theme.equals("Inverted and Brightened")) {
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                red += 50;
                if(red > 255) red = 255;
                green += 50;
                if(green > 255) green = 255;
                blue += 50;
                if(blue > 255) blue = 255;
            }
        }

        //If the way is a line, draw as a line
        if(isLine) {
            gc.setStroke(Color.rgb(red, green, blue));
            gc.stroke();

        //Otherwise, draw as a polygon
        } else {
            gc.setFill(Color.rgb(red, green, blue));
            gc.fill();
        }
    }
}
