package hash.table;

@SuppressWarnings("unchecked")
public class HashTable<V> {

    private static final int[] MERSENNE = {251, 509, 1021, 2039, 4093, 8191, 16381};

    private final Cell<Integer, V>[] table;
    private final int m;
    private int count;

    private int lastKeyOriginal;
    private int lastKeyPrime;
    private int lastHashIndex;
    private int lastProbeCount;

    public HashTable(int n, double alpha) {
        int required = (int) Math.ceil(n / alpha);
        this.m = findMersenne(required);
        this.table = new Cell[m];
        for (int i = 0; i < m; i++) {
            table[i] = new Cell<>();
        }
    }

    // ── публичный интерфейс ──────────────────────────────────────────────────

    public int size()          { return m; }
    public int count()         { return count; }
    public boolean isEmpty()   { return count == 0; }
    public double loadFactor() { return (double) count / m; }

    public void clear() {
        for (int i = 0; i < m; i++) table[i] = new Cell<>();
        count = 0;
    }

    public boolean insert(int k, V data) {
        int kp = toNatural(k);
        int h0 = hash(kp);
        saveDiag(k, kp, h0);

        int probes = 0;
        int pos = -1;

        for (int i = 0; i < m; i++) {
            int j = probe(h0, i);
            probes++;
            CellStatus st = table[j].getStatus();

            if (st == CellStatus.BUSY && table[j].getKey() == k) {
                lastProbeCount = probes;
                return false; // дубликат
            }
            if (st == CellStatus.DELETED && pos == -1) pos = j;
            if (st == CellStatus.FREE) {
                if (pos == -1) pos = j;
                break;
            }
        }

        lastProbeCount = probes;
        if (pos == -1) return false; // таблица полна

        table[pos].setKey(k);
        table[pos].setData(data);
        table[pos].setStatus(CellStatus.BUSY);
        count++;
        return true;
    }

    public V search(int k) {
        int kp = toNatural(k);
        int h0 = hash(kp);
        saveDiag(k, kp, h0);

        int probes = 0;
        for (int i = 0; i < m; i++) {
            int j = probe(h0, i);
            probes++;
            CellStatus st = table[j].getStatus();

            if (st == CellStatus.BUSY && table[j].getKey() == k) {
                lastProbeCount = probes;
                return table[j].getData();
            }
            if (st == CellStatus.FREE) break;
        }

        lastProbeCount = probes;
        return null;
    }

    public boolean delete(int k) {
        int kp = toNatural(k);
        int h0 = hash(kp);
        saveDiag(k, kp, h0);

        int probes = 0;
        for (int i = 0; i < m; i++) {
            int j = probe(h0, i);
            probes++;
            CellStatus st = table[j].getStatus();

            if (st == CellStatus.BUSY && table[j].getKey() == k) {
                table[j].setStatus(CellStatus.DELETED);
                count--;
                lastProbeCount = probes;
                return true;
            }
            if (st == CellStatus.FREE) break;
        }

        lastProbeCount = probes;
        return false;
    }

    public void print() {
        for (int i = 0; i < m; i++) {
            Cell<Integer, V> cell = table[i];
            switch (cell.getStatus()) {
                case FREE    -> System.out.printf("[%3d] FREE%n", i);
                case DELETED -> System.out.printf("[%3d] DELETED%n", i);
                case BUSY    -> {
                    int k  = cell.getKey();
                    int kp = toNatural(k);
                    int h  = hash(kp);
                    System.out.printf("[%3d] BUSY    | key=%-12d | k'=%-8d | h=%d mod %d = %d | data=%s%n",
                            i, k, kp, kp, m, h, cell.getData());
                }
            }
        }
    }

    public HashTableIterator<Integer, V> begin() {
        int i = 0;
        while (i < m && table[i].getStatus() != CellStatus.BUSY) i++;
        return new HashTableIterator<>(table, i, m);
    }

    public HashTableIterator<Integer, V> end() {
        return new HashTableIterator<>(table, m, m);
    }

    // ── диагностика ──────────────────────────────────────────────────────────

    public int getLastKeyOriginal() { return lastKeyOriginal; }
    public int getLastKeyPrime()    { return lastKeyPrime; }
    public int getLastHashIndex()   { return lastHashIndex; }
    public int getLastProbeCount()  { return lastProbeCount; }

    // ── приватные методы ─────────────────────────────────────────────────────

    private int toNatural(int k)      { return KeyConverter.toNatural(k); }
    private int hash(int kp)          { return kp % m; }
    private int probe(int h0, int i)  { return (h0 + i) % m; }

    private void saveDiag(int k, int kp, int h0) {
        lastKeyOriginal = k;
        lastKeyPrime    = kp;
        lastHashIndex   = h0;
    }

    private int findMersenne(int required) {
        for (int val : MERSENNE) {
            if (val >= required) return val;
        }
        return MERSENNE[MERSENNE.length - 1];
    }
}
