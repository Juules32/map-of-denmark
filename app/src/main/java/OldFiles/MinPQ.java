package OldFiles;

/*
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
*/

public class MinPQ<Key> /*implements Iterable<Key>*/ {
    /*
    private Key[] pq;                    // store items at indices 1 to n
    private int[] pqNodes;
    private int n;                       // number of items on priority queue
    private Comparator<Key> comparator;  // optional comparator

    public MinPQ(int initCapacity) {
        pq = (Key[]) new Object[initCapacity + 1];
        pqNodes = new int[initCapacity + 1];
        n = 0;
    }

    public MinPQ() {
        this(1);
    }

    public MinPQ(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        pqNodes = new int[initCapacity + 1];
        n = 0;
    }


    public MinPQ(Comparator<Key> comparator) {
        this(1, comparator);
    }

    public MinPQ(Key[] keys) {
        n = keys.length;
        pq = (Key[]) new Object[keys.length + 1];
        for (int i = 0; i < n; i++)
            pq[i+1] = keys[i];
        for (int k = n/2; k >= 1; k--)
            sink(k);
        assert isMinHeap();
    } 

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public int min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        //return pq[1];
        return pqNodes[1];
    }

    // resize the underlying array to have the given capacity
    private void resize(int capacity) {
        //System.out.println("resize:");
        //System.out.println("(pq, pqNodes) lengths = (" + pq.length + ", " + pqNodes.length + ", " + n + ")");
        assert capacity > n;
        Key[] temp = (Key[]) new Object[capacity];
        int[] temp2 = new int[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
            temp2[i] = pqNodes[i];
        }
        pq = temp;
        pqNodes = temp2;
    }

    public void insert(Key distTo, int nodeIndex) {
        //System.out.println("Insert:");
        //System.out.println("distTo: " + distTo + ", nodeIndex: " + nodeIndex);
        //System.out.println("(pq, pqNodes, n) lengths = (" + pq.length + ", " + pqNodes.length + ", " + n + ")");
        // double size of array if necessary
        if (n == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[n+1] = distTo;
        pqNodes[++n] = nodeIndex;
        swim(n);
        assert isMinHeap();
    }

    public int delMin() {
        //System.out.println("delMin:");
        //System.out.println("(pq, pqNodes) lengths = (" + pq.length + ", " + pqNodes.length + ", " + n + ")");
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        //Key min = pq[1];
        int min = pqNodes[1];
        exch(1, n--);
        sink(1);
        pq[n+1] = null;     // to avoid loitering and help with garbage collection
        pqNodes[n+1] = -1;
        if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length / 2);
        assert isMinHeap();
        return min;
    }


    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k/2, k);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        int swap2 = pqNodes[i];
        pqNodes[i] = pqNodes[j];
        pqNodes[j] = swap2;
    }

    private boolean isMinHeap() {
        for (int i = 1; i <= n; i++) {
            if (pq[i] == null) return false;
        }
        for (int i = n+1; i < pq.length; i++) {
            if (pq[i] != null) return false;
        }
        if (pq[0] != null) return false;
        return isMinHeapOrdered(1);
    }

    private boolean isMinHeapOrdered(int k) {
        if (k > n) return true;
        int left = 2*k;
        int right = 2*k + 1;
        if (left  <= n && greater(k, left))  return false;
        if (right <= n && greater(k, right)) return false;
        return isMinHeapOrdered(left) && isMinHeapOrdered(right);
    }


    public Iterator<Key> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Key> {
        // create a new pq
        private MinPQ<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new MinPQ<Key>(size());
            else                    copy = new MinPQ<Key>(size(), comparator);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i], pqNodes[i]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Key next() {
            if (!hasNext()) throw new NoSuchElementException();
            //return copy.delMin();
            Key min = pq[1];
            copy.delMin();      // method runs to delete the first elements
            return min;
        }
    }
    */
}
