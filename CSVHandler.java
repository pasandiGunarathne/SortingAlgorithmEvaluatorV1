import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

public class CSVHandler {
    private List<String> headers = new ArrayList<>();
    private File lastFile = null;
    private List<Integer> numericIndices = new ArrayList<>();

    public File selectFile(java.awt.Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV Dataset");
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            lastFile = fileChooser.getSelectedFile();
            return lastFile;
        }
        return null;
    }

    public void detectNumericColumns(File f, int sampleRows) {
        numericIndices.clear();
        headers.clear();
        if (f == null) {
            headers.add("SampleColumn");
            numericIndices.add(0);
            lastFile = null;
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String first = br.readLine();
            if (first == null) return;
            String[] hdrs = first.split(",");
            for (String h : hdrs) headers.add(h.trim());

            int cols = headers.size();
            int[] numericCount = new int[cols];
            int rowsRead = 0;
            String row;
            while (rowsRead < sampleRows && (row = br.readLine()) != null) {
                rowsRead++;
                String[] colsArr = row.split(",");
                for (int i = 0; i < cols; i++) {
                    if (i < colsArr.length) {
                        String cell = colsArr[i].trim();
                        if (cell.isEmpty()) continue;
                        try {
                            Double.parseDouble(cell);
                            numericCount[i]++;
                        } catch (NumberFormatException nfe) {
                        }
                    }
                }
            }

            for (int i = 0; i < cols; i++) {
                if (rowsRead == 0) continue;
                if (numericCount[i] * 2 >= rowsRead) numericIndices.add(i);
            }

            lastFile = f;
        } catch (Exception ex) {
            headers.clear();
            headers.add("SampleColumn");
            numericIndices.clear();
            numericIndices.add(0);
            lastFile = null;
        }
    }

    public List<Record> parseCSV(File file, int sortColumnIndex) {
        List<Record> data = new ArrayList<>();
        lastFile = file;
        headers.clear();
        if (file == null) {
            headers.add("SampleColumn");
            for (int i = 100; i > 0; i -= 10) data.add(new Record(i));
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null) return data;
            String[] hdrs = line.split(",");
            for (String h : hdrs) headers.add(h.trim());

            String row;
            while ((row = br.readLine()) != null) {
                String[] cols = row.split(",");
                if (sortColumnIndex >= 0 && sortColumnIndex < cols.length) {
                    try {
                        double v = Double.parseDouble(cols[sortColumnIndex].trim());
                        data.add(new Record(v));
                    } catch (NumberFormatException nfe) {
                    }
                }
            }

            if (data.isEmpty()) {
                for (int i = 100; i > 0; i -= 10) data.add(new Record(i));
            }

            return data;
        } catch (Exception ex) {
            headers.clear();
            headers.add("SampleColumn");
            for (int i = 0; i < 50; i++) data.add(new Record(Math.random() * 1000));
            lastFile = null;
            return data;
        }
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<Integer> getNumericIndices() {
        return numericIndices;
    }

    public List<String> getNumericHeaders() {
        List<String> nh = new ArrayList<>();
        for (Integer idx : numericIndices) {
            if (idx >= 0 && idx < headers.size()) nh.add(headers.get(idx));
        }
        return nh;
    }

    public File getLastFile() {
        return lastFile;
    }

    // Return up to maxRows of preview (split into String arrays). Empty list if file is null or can't be read.
    public List<String[]> getPreviewRows(File file, int maxRows) {
        List<String[]> rows = new ArrayList<>();
        if (file == null || !file.exists()) return rows;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) return rows;
            String row;
            int count = 0;
            while (count < maxRows && (row = br.readLine()) != null) {
                rows.add(row.split(","));
                count++;
            }
        } catch (Exception ex) {
            // return empty preview on error
        }

        return rows;
    }
}