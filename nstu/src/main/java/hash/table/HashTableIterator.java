package hash.table;

// итератор для обхода хэш-таблицы
// проходит только по занятым ячейкам (BUSY)
public class HashTableIterator<K, V> {

    private Cell<K, V>[] table;
    private int currentIndex;
    private int tableSize;

    public HashTableIterator(Cell<K, V>[] table, int currentIndex, int tableSize) {
        this.table = table;
        this.currentIndex = currentIndex;
        this.tableSize = tableSize;
    }

    public int getIndex() {
        return currentIndex;
    }

    // возвращает ключ текущей ячейки
    public K getKey() {
        if (currentIndex >= tableSize) {
            throw new IllegalStateException("Итератор вышел за пределы таблицы");
        }
        if (table[currentIndex].getStatus() != CellStatus.BUSY) {
            throw new IllegalStateException("Ячейка не занята");
        }
        return table[currentIndex].getKey();
    }

    // возвращает значение текущей ячейки
    public V getValue() {
        if (currentIndex >= tableSize) {
            throw new IllegalStateException("Итератор вышел за пределы таблицы");
        }
        if (table[currentIndex].getStatus() != CellStatus.BUSY) {
            throw new IllegalStateException("Ячейка не занята");
        }
        return table[currentIndex].getData();
    }

    // устанавливает значение текущей ячейки
    public void setValue(V val) {
        if (currentIndex >= tableSize) {
            throw new IllegalStateException("Итератор вышел за пределы таблицы");
        }
        if (table[currentIndex].getStatus() != CellStatus.BUSY) {
            throw new IllegalStateException("Ячейка не занята");
        }
        table[currentIndex].setData(val);
    }

    // переходим к следующей занятой ячейке
    public HashTableIterator<K, V> next() {
        int nextIndex = currentIndex + 1;
        // ищем следующую BUSY ячейку
        while (nextIndex < tableSize && table[nextIndex].getStatus() != CellStatus.BUSY) {
            nextIndex++;
        }
        return new HashTableIterator<>(table, nextIndex, tableSize);
    }

    // проверяем что итераторы не равны (для цикла)
    public boolean notEquals(HashTableIterator<K, V> other) {
        return this.currentIndex != other.currentIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HashTableIterator)) {
            return false;
        }
        HashTableIterator<?, ?> other = (HashTableIterator<?, ?>) obj;
        return this.currentIndex == other.currentIndex;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(currentIndex);
    }
}
