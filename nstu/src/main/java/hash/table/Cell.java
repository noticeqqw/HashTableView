package hash.table;

public class Cell<K, V> {
    private K key;
    private V data;
    private CellStatus status;

    public Cell() {
        this.status = CellStatus.FREE;
        this.key = null;
        this.data = null;
    }

    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }

    public V getData() { return data; }
    public void setData(V data) { this.data = data; }

    public CellStatus getStatus() { return status; }
    public void setStatus(CellStatus status) { this.status = status; }
}
