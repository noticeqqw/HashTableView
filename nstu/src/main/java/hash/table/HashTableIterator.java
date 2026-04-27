package hash.table;

public class HashTableIterator<K, V> {
    private final Cell<K, V>[] table;
    private final int index;
    private final int m;

    public HashTableIterator(Cell<K, V>[] table, int index, int m) {
        this.table = table;
        this.index = index;
        this.m = m;
    }

    public int getIndex() { return index; }

    public V getValue() {
        if (index >= m || table[index].getStatus() != CellStatus.BUSY) {
            throw new IllegalStateException("Iterator does not point to a BUSY cell");
        }
        return table[index].getData();
    }

    public void setValue(V val) {
        if (index >= m || table[index].getStatus() != CellStatus.BUSY) {
            throw new IllegalStateException("Iterator does not point to a BUSY cell");
        }
        table[index].setData(val);
    }

    /** Переход к следующей BUSY-ячейке. */
    public HashTableIterator<K, V> next() {
        int next = index + 1;
        while (next < m && table[next].getStatus() != CellStatus.BUSY) {
            next++;
        }
        return new HashTableIterator<>(table, next, m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashTableIterator<?, ?> other)) return false;
        return this.index == other.index;
    }

    public boolean notEquals(HashTableIterator<K, V> other) {
        return this.index != other.index;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
}
