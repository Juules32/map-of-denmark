package DataStructures;

import java.io.Serializable;

public class Trie<T> implements Serializable {

    TrieNode<T> root = new TrieNode<>();

    private class TrieNode<T> implements Serializable {
        T value = null;
        TrieNode<T>[] children = null;
    
    }

    //Used to the value of the key argument
    public T get(long key) {
        TrieNode<T> curr = root;

        String keyString = Long.toString(key);

        //Loops through each character in the key
        //If there is an uninitialized node along the way, returns null
        //Otherwise, returns the value of the end node
        for (int i = 0; i < keyString.length(); i++) {
            int c = Character.getNumericValue(keyString.charAt(i));
            if (curr == null || curr.children == null) return null;
            curr = curr.children[c];
        }
        if (curr == null) return null;
        return (T) curr.value;
    }

    //Used to set a key-value pair in the Trie
    public boolean set(long key, T value) {
        TrieNode<T> curr = root;
        String keyString = Long.toString(key);

        //Loops through each character in the key
        //Any unilitialized nodes are initialized along the way
        //Finally, the value of the end node is set to the value argument
        for (int i = 0; i < keyString.length(); i++) {
            int c = Character.getNumericValue(keyString.charAt(i));
            if(curr.children == null) curr.children = new TrieNode[10];
            if(curr.children[c] == null) curr.children[c] = new TrieNode<T>();
            curr = curr.children[c];
        }

        boolean result = true;
        if(curr.value != null) result = false;
        curr.value = (T) value;

        //Returns true if a value was added, not replaced
        return result;
    }
}
