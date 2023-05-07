import org.junit.jupiter.api.Test;
import DataStructures.Node;

import DataStructures.AddressTrie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class AddressTrieTest {

    static AddressTrie testTrie;
    
    String[] address = {"PARKVEJ", "2", "3760"};
    String[] address2 = {"PARKVEJ", "2", null};
    String[] address3 = {"PARKVEJ", null, null};
    String[] address4 = {null, null, null};

    Node testNode1 = new Node(10, 10);
    Node testNode2 = new Node(20, 20);

    @BeforeEach void reset() {
        testTrie = new AddressTrie();
    }
    
    @Test void setMultiple() {
        assertTrue(testTrie.set(address, testNode1));
        assertFalse(testTrie.set(address, testNode2));
    }

    @Test void setMultiple2() {
        assertTrue(testTrie.set(address3, testNode1));
        assertFalse(testTrie.set(address3, testNode2));
    }

    @Test void numAddresses() {
        testTrie.set(address, testNode1);
        testTrie.set(address3, testNode1);
        assertEquals(2, testTrie.getNumAddresses());
    }

    @Test void getValueSuccess() {
        testTrie.set(address, testNode1);
        assertEquals(testNode1, testTrie.get(address));
    }

    @Test void getValueUnsuccessfully1() {
        testTrie.set(address3, testNode1);
        assertEquals(null, testTrie.get(address));
    }

    @Test void getValueUnsuccessfully2() {
        testTrie.set(address, testNode1);
        assertEquals(null, testTrie.get(address4));
    }

    @Test void completeAddress() {
        testTrie.getRecommendations("PARKVEJ 2 3760");
        testTrie.set(address, testNode1);
        testTrie.getRecommendations("PARKVEJ 2 3760");
    }

    @Test void streetAndHousenumberOnly() {
        testTrie.getRecommendations("PARKVEJ 2");
        testTrie.set(address, null);
        testTrie.getRecommendations("PARKVEJ 2");
        testTrie.set(address, testNode1);
        testTrie.getRecommendations("PARKVEJ 2");
    }

    @Test void streetOnly() {
        testTrie.getRecommendations("PARKVEJ");
        testTrie.set(address, null);
        testTrie.getRecommendations("PARKVEJ");
        testTrie.set(address, testNode1);
        testTrie.getRecommendations("PARKVEJ");
    }

    @Test void nullAddress() {
        testTrie.getRecommendations("");
    }
}
