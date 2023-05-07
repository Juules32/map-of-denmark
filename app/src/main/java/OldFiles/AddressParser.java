/*
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DataStructures.Node;
import DataStructures.TrieNode;

public class AddressParser implements Serializable {
    static TrieNode trieNode;
    Node address;

    public AddressParser() {
        trieNode = new TrieNode();
    }
    
    
    public void insertAddress(Node addr) {
        var node = trieNode;
        var words = addr.getAddress().replaceAll(",", "").split(" ");
        for (var word : words) {
            if (!node.children.containsKey(word)) {
                node.children.put(word, new TrieNode());
            }
            node = node.children.get(word);
        }
        node.addresses.add(addr);
    }

    public static List<Node> searchAddresses(String query) {
        var node = trieNode;
        var words = query.replaceAll(",", "").split(" ");
        for (var word : words) {
            if (!node.children.containsKey(word)) {
                return Collections.emptyList();
            }
            node = node.children.get(word);
        }
        List<Node> result = new ArrayList<>();
        collectAddresses(node, result);
        return result;
    }
    
    private static void collectAddresses(TrieNode node, List<Node> addresses) {
        if (node == null) {
            return;
        }
    
        addresses.addAll(node.addresses);
    
        for (TrieNode childNode : node.children.values()) {
            collectAddresses(childNode, addresses);
        }
    }
}
*/