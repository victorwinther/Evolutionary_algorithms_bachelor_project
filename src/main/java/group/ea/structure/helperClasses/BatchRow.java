package group.ea.structure.helperClasses;

import java.util.ArrayList;
import java.util.List;

public class BatchRow {
    private int id;
    private List<String> rowData;

    public BatchRow(int id) {
        this.id = id;
        this.rowData = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public List<String> getRowData() {
        return rowData;
    }

    public void addData(String data) {
        rowData.add(data);
    }
}
