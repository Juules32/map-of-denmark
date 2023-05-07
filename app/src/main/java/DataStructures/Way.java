package DataStructures;

import java.io.Serializable;
import java.util.List;

public class Way implements Serializable {

    //A way contains:
    //An array of node references
    public Node[] nodes;

    //RGB color values
    public int red;

    public int green;

    public int blue;
    
    public Way(List<Node> way, int red, int green, int blue) {
        this.nodes = way.toArray(new Node[way.size()]);
        this.red = red;      
        this.green = green;      
        this.blue = blue;      
    }
}