package DataStructures;

import java.io.Serializable;

public class Edge implements Serializable {

    //Every edge has:
    //The id of the to Node
    public long toId;

    //A maximum speed
    public int maxSpeed;

    public Edge(long toId, int maxSpeed, boolean isCarAllowed) {
        this.toId = toId;
        this.maxSpeed = maxSpeed;
        if (this.maxSpeed == 0 && isCarAllowed) this.maxSpeed = 80;
    }
}