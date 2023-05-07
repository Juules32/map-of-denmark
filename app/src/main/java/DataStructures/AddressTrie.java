package DataStructures;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AddressTrie implements Serializable {

    //Root recursive trie node
    AddressTrieNode root = new AddressTrieNode();

    //Counts how many addresses are added
    private int numAddresses = 0;

    public int getNumAddresses() {return numAddresses;}

    //Inner class containing value and hashmap of children
    private class AddressTrieNode implements Serializable {
        Node value = null;
        HashMap<String, AddressTrieNode> children = null;
    }

    //Used to get the value at target address
    public Node get(String[] keys) {
        
        //getNode gets the node
        AddressTrieNode foundNode = getNode(keys);

        //The value is returned, if it exists
        if(foundNode != null) return foundNode.value;
        else return null;
    }

    //Used to get the Node at target address
    public AddressTrieNode getNode(String[] keys) {
        AddressTrieNode curr = root;

        //Goes through the trie, and returns null if something is undefined
        for (String key : keys) {
            if(curr.children == null) return null;
            if(key == null) return curr;
            curr = curr.children.get(key);
            if(curr == null) return null;
        }

        //If the node exists, returns the node
        return curr;
    }

    //Used to set an id at target address
    public boolean set(String[] keys, Node value) {
        AddressTrieNode curr = root;
        boolean result = true;

        //Goes through the trie, and initializes new children if they are undefined
        for (String key : keys) {
            if(curr.children == null) curr.children = new HashMap<>();

            //If no more information about the address is given, sets the value
            if(key == null) {
                if(curr.value != null) result = false;
                curr.value = value;
                ++numAddresses;
                return result;
            }

            //If a path to address has not been defined, adds link
            if(!curr.children.containsKey(key)) {
                AddressTrieNode newTrieNode = new AddressTrieNode();
                curr.children.put(key, newTrieNode);
            }

            //Updates current node
            curr = curr.children.get(key);
        }

        if(curr.value != null) result = false;
        
        //Sets value
        curr.value = value;
        ++numAddresses;
        return result;
    }

    //Used to generate recommended addresses for from and to boxes
    public List<String> getRecommendations(String query) {

        //Query is converted to list
        String[] list = stringToList(query);

        //A list of results is initialized
        List<String> results = new ArrayList<>();

        String street = list[0];
        String housenumber = list[1];
        String postcode = list[2];
        
        //If a street is given
        if(street != null) {

            //And a housenumber
            if(housenumber != null) {

                //And a postcode
                if(postcode != null) {

                    //If such an address exists
                    AddressTrieNode foundNode = getNode(list);
                    if(foundNode != null && foundNode.value != null) {

                        //Adds given string as recommendation
                        String city = findCity(postcode);
                        String[] result = {street, housenumber, postcode, city};
                        results.add(listToString(result));
                    }
                }
    
                //If only a street and housenumber is given
                //Find all options further down the AddressTrie
                else {
                    AddressTrieNode foundNode = getNode(list);
                    if(foundNode != null && foundNode.children != null) {
                        foundNode.children.forEach((key, value) -> {
                            if(value.value != null) {
                                String city = findCity(key);
                                String[] result = {street, housenumber, key, city};
                                results.add(listToString(result));
                            }
                        });
                    }
                }
            }

            //If only a street is given
            //Find all options further down the AddressTrie
            else {
                AddressTrieNode foundNode = getNode(list);
                if(foundNode != null && foundNode.children != null) {
                    foundNode.children.forEach((key, value) -> {
                        AddressTrieNode innerNode = foundNode.children.get(key);
                        if(innerNode != null && innerNode.children != null) {
                            innerNode.children.forEach((innerKey, innerValue) -> {
                                if(innerValue.value != null) {
                                    String city = findCity(innerKey);
                                    String[] result = {street, key, innerKey, city};
                                    results.add(listToString(result));
                                }
                            });
                        }
                    });
                }
            }
        }

        return results;
    }

    //Regular expression and pattern used for address parsing
    private final static String REGEX = " *(?<street>[a-zA-ZÆØÅæøå .]*[a-zA-ZÆØÅæøå.]+)([, ]+(?<housenumber>[0-9]+[a-zA-ZÆØÅæøå]*))?([, ]+(?<postcode>[0-9]{4}))?([, ]+[\\w\\Wa-zA-ZÆØÅæøå .]+)? *";
	private final static Pattern PATTERN = Pattern.compile(REGEX);

    //Used to convert query to list of address information
    public String[] stringToList(String query) {
        if(query == null || query.equals("")) {
            String[] result = {null, null, null};
            return result;
        }
        var matcher = PATTERN.matcher(query);
        String postcode = null;
        String street = null;
        String housenumber = null;

        //If the regex matcher finds matches, they are set
        if (matcher.matches()) {
            street = matcher.group("street");
            housenumber = matcher.group("housenumber");
            postcode = matcher.group("postcode");
        }

        //Converts to upper case for flexibility
        if(street != null) street = street.toUpperCase();
        if(housenumber != null) housenumber = housenumber.toUpperCase();
        if(postcode != null) postcode = postcode.toUpperCase();

        String[] result = {street, housenumber, postcode};

        return result;
    }

    //Used to convert list of address information into string
    public String listToString(String[] query) {
        String result = "";
        for (String entry : query) {result += entry != null ? (entry.substring(0,1).toUpperCase() + entry.substring(1).toLowerCase() + " ") : "";}
        return result.trim();
    }

    //Used to find city from postcode
    public String findCity(String postcode) {

        // Goes through data set and finds match
        String result = null;
        try {
            InputStream postcodesInputStream = getClass().getClassLoader().getResourceAsStream("postcodes.txt");
            Scanner sc = new Scanner(new InputStreamReader(postcodesInputStream, StandardCharsets.UTF_8));
    
            while (sc.hasNext()) {
                String[] input = sc.nextLine().split(" ", 2);
                if (input[0].equals(postcode)) {
                    result = input[1];
                    break;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Error: postcodes.txt not found in the resources folder.");
        }
    
        return result;
    }
}