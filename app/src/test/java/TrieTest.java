import org.junit.jupiter.api.Test;

import DataStructures.Trie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class TrieTest {

    static Trie<Integer> testTrie;

    @BeforeEach void reset() {
        testTrie = new Trie<>();
    }

    @Test void setMultipleTest() {
        assertTrue(testTrie.set(987, 123));
        assertFalse(testTrie.set(987, 456));
    }

    @Test void getValueSuccessTest() {
        testTrie.set(9876, 123);
        assertEquals(123, testTrie.get(9876));
    }

    @Test void getValueUnsuccessfullyTest1() {
        testTrie.set(987, 123);
        assertNull(testTrie.get(789));
    }

    @Test void getValueUnsuccessfullyTest2() {
        testTrie.set(987, 123);
        assertNull(testTrie.get(9876));
    }
}
