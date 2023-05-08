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
    
    @Test void setMultipleTest1() {
        assertTrue(testTrie.set(address, testNode1));
        assertFalse(testTrie.set(address, testNode2));
    }

    @Test void setMultipleTest2() {
        assertTrue(testTrie.set(address3, testNode1));
        assertFalse(testTrie.set(address3, testNode2));
    }

    @Test void numAddressesTest() {
        testTrie.set(address, testNode1);
        testTrie.set(address3, testNode1);
        assertEquals(2, testTrie.getNumAddresses());
    }

    @Test void getValueSuccessTest() {
        testTrie.set(address, testNode1);
        assertEquals(testNode1, testTrie.get(address));
    }

    @Test void getValueUnsuccessfullyTest1() {
        testTrie.set(address3, testNode1);
        assertEquals(null, testTrie.get(address));
    }

    @Test void getValueUnsuccessfullyTest2() {
        testTrie.set(address, testNode1);
        assertEquals(null, testTrie.get(address4));
    }

    @Test void completeAddressTest() {
        assertTrue(testTrie.getRecommendations("PARKVEJ 2 3760").isEmpty());
        testTrie.set(address, testNode1);
        assertFalse(testTrie.getRecommendations("PARKVEJ 2 3760").isEmpty());
    }

    @Test void streetAndHousenumberOnlyTest() {
        assertTrue(testTrie.getRecommendations("PARKVEJ 2").isEmpty());
        testTrie.set(address, null);
        assertTrue(testTrie.getRecommendations("PARKVEJ 2").isEmpty());
        testTrie.set(address, testNode1);
        assertFalse(testTrie.getRecommendations("PARKVEJ 2").isEmpty());
    }

    @Test void streetOnlyTest() {
        assertTrue(testTrie.getRecommendations("PARKVEJ").isEmpty());
        testTrie.set(address, null);
        assertTrue(testTrie.getRecommendations("PARKVEJ").isEmpty());
        testTrie.set(address, testNode1);
        assertFalse(testTrie.getRecommendations("PARKVEJ").isEmpty());
    }

    @Test void nullAddressTest() {
        assertTrue(testTrie.getRecommendations("").isEmpty());
    }
}
