package hash.table;

// основной класс хэш-таблицы с открытой адресацией (линейное зондирование)
@SuppressWarnings("unchecked")
public class HashTable<V> {

    // простые числа Мерсенна для размера таблицы (из методички)
    private static final int[] MERSENNE_PRIMES = {251, 509, 1021, 2039, 4093, 8191, 16381};

    private Cell<Integer, V>[] table;
    private int tableSize;   // реальный размер таблицы (m)
    private int elementCount; // сколько элементов сейчас в таблице

    // для диагностики последней операции
    private int lastKeyOriginal;
    private int lastKeyPrime;
    private int lastHashIndex;
    private int lastProbeCount;

    // конструктор: n - желаемое кол-во элементов, alpha - коэф заполнения
    public HashTable(int n, double alpha) {
        // вычисляем минимальный нужный размер таблицы
        int requiredSize = (int) Math.ceil((double) n / alpha);

        // выбираем ближайшее число Мерсенна
        this.tableSize = findTableSize(requiredSize);

        // инициализируем массив ячеек
        this.table = new Cell[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = new Cell<>();
        }

        this.elementCount = 0;
    }

    // вставка элемента по ключу
    // возвращает true если вставили, false если дубликат или таблица полна
    public boolean insert(int key, V data) {
        int keyPrime = KeyConverter.toNatural(key);
        int hashIndex = calculateHash(keyPrime);
        saveDiagInfo(key, keyPrime, hashIndex);

        int probes = 0;
        int insertPosition = -1; // позиция куда вставим (первый DELETED или FREE)

        for (int i = 0; i < tableSize; i++) {
            int pos = linearProbe(hashIndex, i);
            probes++;

            CellStatus currentStatus = table[pos].getStatus();

            if (currentStatus == CellStatus.BUSY) {
                // проверяем дубликат
                if (table[pos].getKey() == key) {
                    lastProbeCount = probes;
                    return false; // такой ключ уже есть
                }
            } else if (currentStatus == CellStatus.DELETED) {
                // запоминаем первое удалённое место
                if (insertPosition == -1) {
                    insertPosition = pos;
                }
            } else if (currentStatus == CellStatus.FREE) {
                // нашли свободное место
                if (insertPosition == -1) {
                    insertPosition = pos;
                }
                break; // дальше искать не нужно, там точно нет дубликатов
            }
        }

        lastProbeCount = probes;

        if (insertPosition == -1) {
            return false; // таблица полностью заполнена
        }

        // вставляем элемент
        table[insertPosition].setKey(key);
        table[insertPosition].setData(data);
        table[insertPosition].setStatus(CellStatus.BUSY);
        elementCount++;
        return true;
    }

    // поиск элемента по ключу
    // возвращает данные или null если не найдено
    public V search(int key) {
        int keyPrime = KeyConverter.toNatural(key);
        int hashIndex = calculateHash(keyPrime);
        saveDiagInfo(key, keyPrime, hashIndex);

        int probes = 0;

        for (int i = 0; i < tableSize; i++) {
            int pos = linearProbe(hashIndex, i);
            probes++;

            CellStatus currentStatus = table[pos].getStatus();

            if (currentStatus == CellStatus.BUSY) {
                if (table[pos].getKey() == key) {
                    lastProbeCount = probes;
                    return table[pos].getData(); // нашли!
                }
            } else if (currentStatus == CellStatus.FREE) {
                break; // дальше точно нет
            }
            // если DELETED - продолжаем искать
        }

        lastProbeCount = probes;
        return null; // не нашли
    }

    // удаление элемента по ключу
    // возвращает true если удалили, false если не нашли
    public boolean delete(int key) {
        int keyPrime = KeyConverter.toNatural(key);
        int hashIndex = calculateHash(keyPrime);
        saveDiagInfo(key, keyPrime, hashIndex);

        int probes = 0;

        for (int i = 0; i < tableSize; i++) {
            int pos = linearProbe(hashIndex, i);
            probes++;

            CellStatus currentStatus = table[pos].getStatus();

            if (currentStatus == CellStatus.BUSY) {
                if (table[pos].getKey() == key) {
                    // помечаем как удалённое (ленивое удаление)
                    table[pos].setStatus(CellStatus.DELETED);
                    elementCount--;
                    lastProbeCount = probes;
                    return true;
                }
            } else if (currentStatus == CellStatus.FREE) {
                break; // не нашли
            }
        }

        lastProbeCount = probes;
        return false;
    }

    // очистка таблицы
    public void clear() {
        for (int i = 0; i < tableSize; i++) {
            table[i] = new Cell<>();
        }
        elementCount = 0;
    }

    // вывод содержимого таблицы
    public void print() {
        for (int i = 0; i < tableSize; i++) {
            Cell<Integer, V> cell = table[i];
            CellStatus status = cell.getStatus();

            if (status == CellStatus.FREE) {
                System.out.printf("[%3d] FREE%n", i);
            } else if (status == CellStatus.DELETED) {
                System.out.printf("[%3d] DELETED%n", i);
            } else if (status == CellStatus.BUSY) {
                int k = cell.getKey();
                int kp = KeyConverter.toNatural(k);
                int h = calculateHash(kp);
                System.out.printf("[%3d] BUSY    | key=%-12d | k'=%-8d | h=%d mod %d = %d | data=%s%n",
                        i, k, kp, kp, tableSize, h, cell.getData());
            }
        }
    }

    // итератор на первый элемент
    public HashTableIterator<Integer, V> begin() {
        int startIndex = 0;
        // ищем первую занятую ячейку
        while (startIndex < tableSize && table[startIndex].getStatus() != CellStatus.BUSY) {
            startIndex++;
        }
        return new HashTableIterator<>(table, startIndex, tableSize);
    }

    // итератор на конец (после последнего элемента)
    public HashTableIterator<Integer, V> end() {
        return new HashTableIterator<>(table, tableSize, tableSize);
    }

    // геттеры для основных характеристик

    public int size() {
        return tableSize;
    }

    public int count() {
        return elementCount;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }

    public double loadFactor() {
        return (double) elementCount / tableSize;
    }

    public Cell<Integer, V>[] getCells() {
        return table;
    }

    // геттеры для диагностики

    public int getLastKeyOriginal() {
        return lastKeyOriginal;
    }

    public int getLastKeyPrime() {
        return lastKeyPrime;
    }

    public int getLastHashIndex() {
        return lastHashIndex;
    }

    public int getLastProbeCount() {
        return lastProbeCount;
    }

    // приватные вспомогательные методы

    private int calculateHash(int keyPrime) {
        return keyPrime % tableSize;
    }

    // линейное зондирование: h(k') + i
    private int linearProbe(int hashIndex, int i) {
        return (hashIndex + i) % tableSize;
    }

    private void saveDiagInfo(int key, int keyPrime, int hashIndex) {
        this.lastKeyOriginal = key;
        this.lastKeyPrime = keyPrime;
        this.lastHashIndex = hashIndex;
    }

    // находим подходящий размер таблицы из массива чисел Мерсенна
    private int findTableSize(int requiredSize) {
        for (int i = 0; i < MERSENNE_PRIMES.length; i++) {
            if (MERSENNE_PRIMES[i] >= requiredSize) {
                return MERSENNE_PRIMES[i];
            }
        }
        // если нужно больше чем есть в массиве - берём максимальное
        return MERSENNE_PRIMES[MERSENNE_PRIMES.length - 1];
    }
}
