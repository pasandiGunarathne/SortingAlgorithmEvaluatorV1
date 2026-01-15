 import java.util.List;
import java.util.ArrayList;

public class Record {
    private double sortValue;
    private List<String> originalValues;

    public Record(double sortValue) {
        this.sortValue = sortValue;
        this.originalValues = new ArrayList<>();
        this.originalValues.add(Double.toString(sortValue));
    }

    public Record(List<String> values, int sortColumnIndex) throws NumberFormatException {
        this.originalValues = values;
        this.sortValue = Double.parseDouble(values.get(sortColumnIndex).trim());
    }

    public double getSortValue() { return sortValue; }
    public List<String> getOriginalValues() { return originalValues; }
    @Override
    public String toString() { return originalValues.toString(); }
}