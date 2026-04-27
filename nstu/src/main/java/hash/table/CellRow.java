package hash.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CellRow {
    private final StringProperty index;
    private final StringProperty status;
    private final StringProperty key;
    private final StringProperty keyPrime;
    private final StringProperty hashIndex;
    private final StringProperty data;

    public CellRow(int index, String status, String key, String keyPrime, String hashIndex, String data) {
        this.index = new SimpleStringProperty(String.valueOf(index));
        this.status = new SimpleStringProperty(status);
        this.key = new SimpleStringProperty(key);
        this.keyPrime = new SimpleStringProperty(keyPrime);
        this.hashIndex = new SimpleStringProperty(hashIndex);
        this.data = new SimpleStringProperty(data);
    }

    public StringProperty indexProperty()     { return index; }
    public StringProperty statusProperty()    { return status; }
    public StringProperty keyProperty()       { return key; }
    public StringProperty keyPrimeProperty()  { return keyPrime; }
    public StringProperty hashIndexProperty() { return hashIndex; }
    public StringProperty dataProperty()      { return data; }
}
