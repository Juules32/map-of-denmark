package DataStructures;
import java.io.Serializable;

public class Node implements Serializable {

    //Every node has a latitude and longitude attribute
    public float lat, lon;
    
    //Note that latitude (y-axis) comes before longitude
    public Node(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}