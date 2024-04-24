package group.ea.structure.helperClasses;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BatchRow {
    private SimpleIntegerProperty id;
    private SimpleStringProperty otherColumn;

    public BatchRow(int id, String otherColumn) {
        this.id = new SimpleIntegerProperty(id);
        this.otherColumn = new SimpleStringProperty(otherColumn);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getOtherColumn() {
        return otherColumn.get();
    }

    public void setOtherColumn(String otherColumn) {
        this.otherColumn.set(otherColumn);
    }
}

