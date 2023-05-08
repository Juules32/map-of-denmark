package Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import DataStructures.AddressTrie;
import DataStructures.Graph;
import DataStructures.GraphNode;
import DataStructures.Node;
import DataStructures.RTrees;
import DataStructures.Trie;
import DataStructures.Way;
import Utils.HelperMethods;
import Utils.Timer;

public class Model implements Serializable {

    //Filename used for saving and loading files
    public String filename;

    //Needed so new model load doesn't get different serial id
    private static final long serialVersionUID = 6529685098267757690L;

    //Array of all ways in dataset (is later sorted to RTree)
    public ArrayList<ArrayList<Way>> ways = new ArrayList<>(10);
    public ArrayList<ArrayList<Way>> areas = new ArrayList<>(10);

    //Lists of RTrees of ways and areas respectfully
    public RTrees wayTrees = new RTrees();
    public RTrees areaTrees = new RTrees();

    //The graph used for shortest route
    public Graph graph = new Graph();

    //The following hashmaps are later filled in by TagParser
    HashMap<String, ArrayList<String>> relevantWayKeyValues = new HashMap<>();
    HashMap<String, ArrayList<int[]>> wayColors = new HashMap<>();
    HashMap<String, ArrayList<Integer>> wayPriority = new HashMap<>();

    HashMap<String, ArrayList<String>> relevantAreaKeyValues = new HashMap<>();
    HashMap<String, ArrayList<int[]>> areaColors = new HashMap<>();
    HashMap<String, ArrayList<Integer>> areaPriority = new HashMap<>();
    
    HashMap<String, ArrayList<String>> relevantRelationKeyValues = new HashMap<>();
    HashMap<String, ArrayList<int[]>> relationColors = new HashMap<>();
    HashMap<String, ArrayList<Integer>> relationPriority = new HashMap<>();

    //Used for getting default values for roads without maxspeed tag
    HashMap<String, Integer> undefinedWaySpeeds = new HashMap<>();

    //The boundaries of the dataset
    public double minlat, maxlat, minlon, maxlon;

    //Used to define and save relations as areas
    List<Way> separatedWays;
    ArrayList<Node> nodes;
    List<Way> currOuterWays = new ArrayList<Way>();
    List<Way> currInnerWays = new ArrayList<Way>();

    //Tries for node ids, way ids and addresses, respectively
    public Trie<Node> id2node = new Trie<>();
    public Trie<Way> id2way = new Trie<>();
    public AddressTrie addresses = new AddressTrie();

    //Loads data if .obj file, else passes to constructor
    public static Model load(String filename, InputStream filenameStream) throws FileNotFoundException, IOException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
        
        //Timer used for displaying how long it takes data to load
        Timer timer = new Timer();
    
        if (filename.endsWith(".obj")) {
            try (var in = new ObjectInputStream(new BufferedInputStream(filenameStream))) {
                return (Model) in.readObject();
            }
            finally {
                timer.printTimePassed("Loaded data successfully!");
            }
        }
        try {
            return new Model(filename, filenameStream);
        }
        finally {
            timer.printTimePassed("Loaded data successfully!");
        }
    }
    
    public Model(String filename, InputStream filenameStream) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.filename = filename;

        //Fills symbol tables with data from key.txt
        InputStream waysInputStream = getClass().getClassLoader().getResourceAsStream("ways.txt");
        InputStream areasInputStream = getClass().getClassLoader().getResourceAsStream("areas.txt");
        InputStream relationsInputStream = getClass().getClassLoader().getResourceAsStream("relations.txt");
        TagParser.parse("ways.txt", waysInputStream, relevantWayKeyValues, wayColors, wayPriority, undefinedWaySpeeds);
        TagParser.parse("areas.txt", areasInputStream, relevantAreaKeyValues, areaColors, areaPriority);
        TagParser.parse("relations.txt", relationsInputStream, relevantRelationKeyValues, relationColors, relationPriority);

        //Decides which parser to use
        if (filename.endsWith(".zip")) {
            parseZIP(filenameStream);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filenameStream);
        } else {
            throw new IOException("Invalid file loaded!");
        }
    }

    //Used to save to a binary file for faster load times
    public void save(String filename) throws FileNotFoundException, IOException {
        Timer timer = new Timer();
        System.out.println("Creating .obj file...");
    
        // Create the "data" folder if it doesn't exist
        File dataFolder = new File("data/");
        if (!dataFolder.exists()) {
            System.out.println("Creating data folder...");
            dataFolder.mkdir();
        }
    
        // Save the .obj file in the "data" folder
        try (var out = new ObjectOutputStream(new FileOutputStream("data/" + filename))) {
            out.writeObject(this);
            timer.printTimePassed("Created .obj file successfully!");
        }
    }    

    //Opens zip file and parses its contents
    private void parseZIP(InputStream inputStream) throws IOException, XMLStreamException, FactoryConfigurationError {
        var input = new ZipInputStream(inputStream);
        input.getNextEntry();
        parseOSM(input);
    }

    //Parses through all lines in xml file
    private void parseOSM(InputStream inputStream) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {

        //Lists of arrayLists of ways later to be used by RTrees
        for (int i = 0; i < 10; i++) {
            ways.add(new ArrayList<Way>());
            areas.add(new ArrayList<Way>());
        }

        //The entirety of the xml contents in XMLS format
        var input = XMLInputFactory.newInstance().createXMLStreamReader(new InputStreamReader(inputStream));
        
        //Denotes whether the first way and relation has been reached
        boolean firstWay = true;
        boolean firstRelation = true;
        
        //The current found id, latitude and longitude
        long wayId = -1;
        long nodeId = -1;
        float lat = -1, lon = -1;
 
        //The current found keys and values and their indeces
        String wayKey = null;
        String wayValue = null;
        String areaKey = null;
        String areaValue = null;
        int wayValueIndex = -1;
        int areaValueIndex = -1;

        //The current found address
        String[] address = new String[3];

        //Keep track of whether the current way is a one way street
        boolean wayIsOneWay = false;

        //Used to create ways
        List<Node> nodesInCurrWay = new ArrayList<>();
        List<Long> nodeIdsInCurrWay = new ArrayList<>();

        //Used to create GraphEdges
        int speedForCurrWay = 0;
        boolean isCarAllowed = true;
        boolean waySpeedIsUndefined = false;
        String typeOfUndefinedWay = null;

        //Loops through all lines in the input
        while (input.hasNext()) {

            //The current tag
            var tagKind = input.next();

            //If the current tag is a start element
            if (tagKind == XMLStreamConstants.START_ELEMENT) {

                //The name of the current tag
                var name = input.getLocalName();
                
                //If bounds tag, sets the boundaries of the dataset
                if (name == "bounds") {
                    System.out.println("Parsing nodes...");
                    minlat = -Double.parseDouble(input.getAttributeValue(null, "minlat"));
                    maxlat = -Double.parseDouble(input.getAttributeValue(null, "maxlat"));
                    minlon = 0.56*Double.parseDouble(input.getAttributeValue(null, "minlon"));
                    maxlon = 0.56*Double.parseDouble(input.getAttributeValue(null, "maxlon"));
                }

                //If node tag, clear address and update current id, lat and lon
                else if (name == "node") {
                    address = new String[3];

                    nodeId = Long.parseLong(input.getAttributeValue(null, "id"));
                    lat = -Float.parseFloat(input.getAttributeValue(null, "lat"));
                    lon = (float) 0.56*Float.parseFloat(input.getAttributeValue(null, "lon"));
                }

                //If way tag, resets a bunch of helper variables and lists
                else if (name == "way") {
                    wayKey = null;
                    wayValue = null;
                    areaKey = null;
                    areaValue = null;
                    wayValueIndex = -1;
                    areaValueIndex = -1;
                    wayIsOneWay = false;
                    nodesInCurrWay.clear();
                    nodeIdsInCurrWay.clear();
                    speedForCurrWay = 0;
                    isCarAllowed = true;
                    waySpeedIsUndefined = true;
                    typeOfUndefinedWay = null;
                    wayId = Long.parseLong(input.getAttributeValue(null, "id"));
                    if(firstWay) {
                        firstWay = false;
                        System.out.println("Parsing ways...");
                    }
                }

                //If 'tag' tag
                else if (name == "tag") {

                    String k = input.getAttributeValue(null, "k");
                    if(k != null) {
                        String v = input.getAttributeValue(null, "v");

                        //If no relation has been found yet, update keys, values and indeces
                        if(firstRelation) {
                            if(!relevantWayKeyValues.containsKey(wayKey)) {
                                wayKey = k;
                                wayValue = v;
                                if(relevantWayKeyValues.get(wayKey) != null) {
                                    wayValueIndex = relevantWayKeyValues.get(wayKey).indexOf(wayValue);
                                }
                            }
                            if(!relevantAreaKeyValues.containsKey(areaKey)) {
                                areaKey = k;
                                areaValue = v;
                                if(relevantAreaKeyValues.get(areaKey) != null) {
                                    areaValueIndex = relevantAreaKeyValues.get(areaKey).indexOf(areaValue);
                                }
                            }

                            //Sets current way as one-directional if tag exists
                            if(k.equals("oneway") &&
                            v.equals("yes")) {
                                wayIsOneWay = true;
                            }

                            //Updates address information
                            else if (k.equals("addr:street")) {
                                address[0] = v.toUpperCase();
                            }
                            else if (k.equals("addr:housenumber")) {
                                address[1] = v.toUpperCase();
                            }
                            else if(k.equals("addr:postcode")) {
                                address[2] = v.toUpperCase();
                            }
                            else if(k.equals("maxspeed")) {
                                try {
                                    speedForCurrWay = Integer.parseInt(v.split(" ")[0]);
                                    waySpeedIsUndefined = false;
                                } catch(Exception e) {
                                    waySpeedIsUndefined = true;
                                }
                            }
                            else if(k.equals("highway")) {
                                if (v.equals("track") || v.equals("path") || v.equals("footway") || v.equals("cycleway")) isCarAllowed = false;
                                else typeOfUndefinedWay = v;
                            }
                        }

                        //Else, update areaKey/areaValue if current key/value pair is relevant
                        else {
                            if(!relevantRelationKeyValues.containsKey(areaKey)) {
                                areaKey = k;
                                areaValue = v;
                                if(relevantRelationKeyValues.get(areaKey) != null) {
                                    areaValueIndex = relevantRelationKeyValues.get(areaKey).indexOf(areaValue);
                                }
                            }
                        }
                    }
                }

                //If node in way tag, add node to current way
                else if (name == "nd") {
                    var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                    var node = id2node.get(ref);
                    nodesInCurrWay.add(node);
                    nodeIdsInCurrWay.add(ref);
                }

                //If relation tag, resets helper variables
                else if (name == "relation") {
                    wayKey = null;
                    wayValue = null;
                    areaKey = null;
                    areaValue = null;
                    wayValueIndex = -1;
                    areaValueIndex = -1;
                    if(firstRelation) {

                        //id2node is freed to save space
                        id2node = null;
                        firstRelation = false;
                        System.out.println("Parsing relations...");
                    }
                    currOuterWays.clear();
                    currInnerWays.clear();
                }

                //If way in relation tag, add way to inner or outer ways
                else if (name == "member") {
                    var type = input.getAttributeValue(null, "type");
                    if (type.equals("way")) {
                        var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                        var role = input.getAttributeValue(null, "role");
                        if (role.equals("outer")) {
                            if(id2way.get(ref) != null) {
                                currOuterWays.add(id2way.get(ref));
                            }
                        }
                        else if(role.equals("inner")) {
                            if(id2way.get(ref) != null) {
                                currInnerWays.add(id2way.get(ref));
                            }
                        }
                    }
                }
            }

            //If the current tag is an end element
            else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                
                //The name of the current tag
                var name = input.getLocalName();
                
                if(name == "way") {

                    //If way is eligible for graph, add the nodes and edges to graph
                    if(wayValueIndex != -1) {
                        if(typeOfUndefinedWay != null && waySpeedIsUndefined && undefinedWaySpeeds.containsKey(typeOfUndefinedWay)) {
                            speedForCurrWay = undefinedWaySpeeds.get(typeOfUndefinedWay);
                        }
                        
                        List<Node> graphNodes = new ArrayList<>();

                        for (int i = 0; i < nodesInCurrWay.size(); i++) {
                            long currId = nodeIdsInCurrWay.get(i);

                            if(graph.nodes.get(nodeIdsInCurrWay.get(i)) == null) {
                                graph.addNode(nodeIdsInCurrWay.get(i), new GraphNode(nodesInCurrWay.get(i)));
                            }

                            graphNodes.add(graph.nodes.get(currId));

                            if(i != 0) {
                                //If way is one-directional, add directed edges between nodes
                                if(wayIsOneWay) {
                                    graph.addDirectedEdge(nodeIdsInCurrWay.get(i-1), nodeIdsInCurrWay.get(i), speedForCurrWay, isCarAllowed);
                                }

                                //Else add undirected edges between nodes
                                else {
                                    graph.addUndirectedEdge(nodeIdsInCurrWay.get(i-1), nodeIdsInCurrWay.get(i), speedForCurrWay, isCarAllowed);
                                }
                            }
                        }

                        //Find the relevant color
                        int[] color = wayColors.get(wayKey).get(wayValueIndex);
                        int red, green, blue;
                        red = color[0];
                        green = color[1];
                        blue = color[2];
                        
                        //Find the relevant priority
                        int priority = wayPriority.get(wayKey).get(wayValueIndex);

                        //Add the way to relevant RTree and to Trie of ways
                        var newWay = new Way(graphNodes, red, green, blue);
                        ways.get(priority).add(newWay);
                        id2way.set(wayId, newWay);
                    }        

                    //If a relevant area has been found
                    else if (areaValueIndex != -1) {
                    
                        //Find the relevant color
                        int[] color = areaColors.get(areaKey).get(areaValueIndex);
                        int red, green, blue;
                        red = color[0];
                        green = color[1];
                        blue = color[2];
                        
                        //Find the relevant priority
                        int priority = areaPriority.get(areaKey).get(areaValueIndex);

                        //Add the way to relevant RTree and Trie of ways
                        var newWay = new Way(nodesInCurrWay, red, green, blue);
                        areas.get(priority).add(newWay);
                        id2way.set(wayId, newWay);
                    }

                    //Otherwise, add to Trie of ways only
                    else {
                        var newWay = new Way(nodesInCurrWay, 0, 0, 0);
                        id2way.set(wayId, newWay);
                    }
                }

                //If node tag, add to Trie of Nodes and add address if it exists
                else if(name == "node") {
                    Node newNode = new Node(lat, lon);
                    id2node.set(nodeId, newNode);

                    if(address[0] != null) {
                        addresses.set(address, newNode);
                    }
                }

                else if (name == "relation") {
                    //If relevant are value has been found
                    if(areaValueIndex >= 0) {

                        //Find the relevant color
                        int[] color = relationColors.get(areaKey).get(areaValueIndex);
                        int red, green, blue;
                        red = color[0];
                        green = color[1];
                        blue = color[2];
                        
                        //Find the relevant priority
                        int priority = relationPriority.get(areaKey).get(areaValueIndex);
                        
                        //Add the relation to areas
                        addRelationToAreas(red, green, blue, priority);
                    }
                }
            }
        }

        //RTrees are initialized
        System.out.println("Filling out and sorting RTrees...");
        for (int i = 0; i < 10; i++) {
            wayTrees.add(ways.get(i), true);
            areaTrees.add(areas.get(i), false);
        }

        //After parsing, varibles are freed to save space
        id2way = null;
    }

    //Used to add relation to areas
    private void addRelationToAreas(int red, int green, int blue, int priority) {
        separatedWays = new ArrayList<>();
        nodes = new ArrayList<>();
        
        //Add next part of relation
        addPartOfWay();

        //Until there are no more separatedWays, do the following:
        // 1. Make currOuterWays separatedWays
        // 2. Reset separatedWays
        // 3. Add next segment of the relation
        while(separatedWays.size() > 0) {
            currOuterWays = separatedWays;
            separatedWays = new ArrayList<>();
            addPartOfWay();
        }

        //Afterwards, add all the inner ways
        for (Way way : currInnerWays) {
            if(!nodes.isEmpty()) nodes.add(null);
            addAllNodes(nodes, way.nodes, 1);
        }
        
        //Then, add the relation as an area in relevant RTree
        if(!nodes.isEmpty()) {
            areas.get(priority).add(new Way(nodes, red, green, blue));
        }
    }

    //Goes through currOuterWays until a disconnected way is found
    //Then adds connected component with nulls used to tell components apart
    private void addPartOfWay() {
        if(!nodes.isEmpty()) nodes.add(null);
        if(currOuterWays.isEmpty()) return;

        Node currLastNode = currOuterWays.get(0).nodes[currOuterWays.get(0).nodes.length-1];

        addAllNodes(nodes, currOuterWays.get(0).nodes, 1);
        currOuterWays.remove(0);

        while(currOuterWays.size() > 0) {
            int[] indexAndOrder = findNearestWayInformation(currLastNode);
            if(indexAndOrder == null) continue;
            int index = indexAndOrder[0];
            int order = indexAndOrder[1];
            addAllNodes(nodes, currOuterWays.get(index).nodes, order);
            if(order == 1) {
                currLastNode = currOuterWays.get(index).nodes[currOuterWays.get(index).nodes.length-1];
            }
            else currLastNode = currOuterWays.get(index).nodes[0];
            currOuterWays.remove(index);
        }
    }

    //Used to add all nodes to larger list
    private void addAllNodes(ArrayList<Node> bigList, Node[] nodes, int order) {
        if(order == 1) {
            for (int i = 0; i < nodes.length; i++) {
                bigList.add(nodes[i]);
            }
        }
        else {
            for (int i = nodes.length-1; i >= 0; i--) {
                bigList.add(nodes[i]);
            }
        }
    }

    //Used to find the nearest way, and which end is nearest
    private int[] findNearestWayInformation(Node currLastPoint) {
        double nearestDistance = Double.POSITIVE_INFINITY;
        int indexOfNearestWay = 0;
        int isFirstNode = 0;
        
        for (int i = 0; i < currOuterWays.size(); i++) {
            Node first = currOuterWays.get(i).nodes[0];
            Node last = currOuterWays.get(i).nodes[currOuterWays.get(i).nodes.length-1];

            double distToFirst = HelperMethods.distFromTo(currLastPoint, first);
            double distToLast = HelperMethods.distFromTo(currLastPoint, last);

            if(distToFirst < nearestDistance) {
                nearestDistance = distToFirst;
                indexOfNearestWay = i;
                isFirstNode = 1;
            }
            if(distToLast < nearestDistance) {
                nearestDistance = distToLast;
                indexOfNearestWay = i;
                isFirstNode = 0;
            }
        }

        if(nearestDistance > 0) {
            separatedWays.add(currOuterWays.get(indexOfNearestWay));
            currOuterWays.remove(indexOfNearestWay);
            return null;
        }
        
        int[] result = {indexOfNearestWay, isFirstNode};
        return result;
    }
}