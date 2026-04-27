package hash.table;

// класс который представляет одну ячейку в хэш-таблице
public class Cell<K, V> {

    private K key;
    private V data;
    private CellStatus status;

    // по умолчанию ячейка пустая
    public Cell() {
        this.key = null;
        this.data = null;
        this.status = CellStatus.FREE;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public CellStatus getStatus() {
        return status;
    }

    public void setStatus(CellStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Cell{" + "key=" + key + ", data=" + data + ", status=" + status + '}';
    }
}
