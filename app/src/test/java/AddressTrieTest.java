import org.junit.jupiter.api.Test;

import DataStructures.AddressTrie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class AddressTrieTest {

    static AddressTrie testTrie;
    
    String[] address = {"PARKVEJ", "2", "3760"};
    String[] address2 = {"PARKVEJ", "2", null};
    String[] address3 = {"PARKVEJ", null, null};
    String[] address4 = {null, null, null};

    @BeforeEach void reset() {
        testTrie = new AddressTrie();
    }
    /*
    @Test void setMultiple() {
        assertTrue(testTrie.set(address, 123));
        assertFalse(testTrie.set(address, 456));
    }

    @Test void setMultiple2() {
        assertTrue(testTrie.set(address3, 123));
        assertFalse(testTrie.set(address3, 456));
    }

    @Test void numAddresses() {
        testTrie.set(address, 123);
        testTrie.set(address3, 123);
        assertEquals(2, testTrie.getNumAddresses());
    }

    @Test void getValueSuccess() {
        testTrie.set(address, 123);
        assertEquals(123, testTrie.get(address));
    }

    @Test void getValueUnsuccessfully1() {
        testTrie.set(address3, 123);
        assertEquals(-1, testTrie.get(address));
    }

    @Test void getValueUnsuccessfully2() {
        testTrie.set(address, 123);
        assertEquals(-1, testTrie.get(address4));
    }

    @Test void completeAddress() {
        testTrie.getRecommendations("PARKVEJ 2 3760");
        testTrie.set(address, 123);
        testTrie.getRecommendations("PARKVEJ 2 3760");
    }

    @Test void streetAndHousenumberOnly() {
        testTrie.getRecommendations("PARKVEJ 2");
        testTrie.set(address, -1);
        testTrie.getRecommendations("PARKVEJ 2");
        testTrie.set(address, 123);
        testTrie.getRecommendations("PARKVEJ 2");
    }

    @Test void streetOnly() {
        testTrie.getRecommendations("PARKVEJ");
        testTrie.set(address, -1);
        testTrie.getRecommendations("PARKVEJ");
        testTrie.set(address, 123);
        testTrie.getRecommendations("PARKVEJ");
    }

    @Test void nullAddress() {
        testTrie.getRecommendations("");
    }*/
}
