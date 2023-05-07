/*
package DataStructures;
import java.io.Serializable;
import java.util.HashMap;

public class SymbolGraph implements Serializable {
    private HashMap<Long, Integer> st;  // id -> index
    private long[] keys;           // index  -> id
    private EdgeWeightedGraph graph;             // the underlying graph

    //First step of setting up symbol graph
    public SymbolGraph() {
        st = new HashMap<Long, Integer>();
    }

    public void addNode(Long id) {
        if(!st.containsKey(id)) {
            st.put(id, st.size());
        }
    }

    //Initializes keys and graph based on found size of symbol table
    public void secondSetup() {
        keys = new long[st.size()];
        for (Long name : st.keySet()) {
            keys[st.get(name)] = name;
        }
        graph = new EdgeWeightedGraph(st.size());
    }

    

    //Does the graph contain the vertex named s?
    public boolean containsKey(Long s) {
        return st.containsKey(s);
    }

    //Returns the integer associated with the vertex named s
    public int indexOf(Long s) {
        return st.get(s);
    }

    //Returns the name of the vertex associated with the integer v
    public Long nameOf(int v) {
        validateVertex(v);
        return keys[v];
    }

    public EdgeWeightedGraph graph() {
        return graph;
    }

    //Throw an IllegalArgumentException if v is not within graph bounds
    private void validateVertex(int v) {
        int V = graph.V();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public HashMap<Long, Integer> getST(){ return st; }

    public long[] getKeys(){ return keys; }
}
*/