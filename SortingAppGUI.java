import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.awt.geom.RoundRectangle2D;

public class SortingAppGUI extends JFrame {
    private CSVHandler csvHandler = new CSVHandler();
    private PerformanceEvaluator evaluator = new PerformanceEvaluator();
    private List<Record> dataset;

    private JComboBox<String> columnSelector;
    private JTextArea resultsArea;
    private JTable previewTable;
    private DefaultTableModel previewModel;
    private JProgressBar progressBar;

    public SortingAppGUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        setTitle("Sorting Algorithm Performance Evaluator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Controls panel (left)
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBorder(BorderFactory.createTitledBorder(null, "Controls", TitledBorder.LEFT, TitledBorder.TOP));
        controls.setPreferredSize(new Dimension(260, 400));

        JButton uploadButton = new JButton("Upload CSV");
        uploadButton.setToolTipText("Open a CSV file to evaluate sorting algorithms");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(e -> handleFileUpload());
        controls.add(Box.createVerticalStrut(8));
        controls.add(uploadButton);

        controls.add(Box.createVerticalStrut(12));
        controls.add(new JLabel("Numeric Columns:"));

        columnSelector = new JComboBox<>();
        columnSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        // (Removed built-in "Load Sample CSV" button per user request)
        columnSelector.setEnabled(false);
        columnSelector.setToolTipText("Only numeric columns are listed here");
        controls.add(columnSelector);

        controls.add(Box.createVerticalStrut(12));
        JButton runButton = new JButton("Run All Sorts");
        runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        runButton.addActionListener(e -> handleSortExecutionInBackground());
        controls.add(runButton);

        controls.add(Box.createVerticalStrut(16));
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Idle");
        controls.add(progressBar);

        add(controls, BorderLayout.WEST);

        // Right side: preview and results
        JPanel right = new JPanel(new BorderLayout(8,8));
        right.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        // Preview table
        previewModel = new DefaultTableModel();
        previewTable = new JTable(previewModel);
        previewTable.setFillsViewportHeight(true);
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane previewScroll = new JScrollPane(previewTable);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Preview (first rows)"));
        previewScroll.setPreferredSize(new Dimension(520, 180));
        right.add(previewScroll, BorderLayout.NORTH);

        // Results area
        resultsArea = new JTextArea("Upload a CSV file to begin performance evaluation...");
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Results"));
        right.add(resultsScroll, BorderLayout.CENTER);

        add(right, BorderLayout.CENTER);

        pack();
        setSize(900, 600);
        setLocationRelativeTo(null);
        // try to load default logo if present (use absolute path as requested)
        loadAndApplyLogo(new File("C:\\Users\\User\\SortingAlgorithmEvaluator\\resources\\logo.png"));
        setVisible(true);
    }

    private void handleFileUpload() {
        File selectedFile = csvHandler.selectFile(this);
        csvHandler.detectNumericColumns(selectedFile, 100);

        columnSelector.removeAllItems();
        List<String> numericHeaders = csvHandler.getNumericHeaders();

        if (numericHeaders.isEmpty()) {
            columnSelector.setEnabled(false);
            resultsArea.setText("No numeric columns detected in the uploaded file. Please upload another CSV containing numeric columns.");
            updatePreview(selectedFile);
            dataset = null;
            return;
        }

        columnSelector.setEnabled(true);
        for (String h : numericHeaders) columnSelector.addItem(h);

        // preload first numeric column for convenience
        int firstIndex = csvHandler.getNumericIndices().get(0);
        this.dataset = csvHandler.parseCSV(csvHandler.getLastFile(), firstIndex);

        resultsArea.setText("File loaded. Select a numeric column and press 'Run All Sorts'.");
        updatePreview(selectedFile);
    }

    private void updatePreview(File file) {
        previewModel.setRowCount(0);
        previewModel.setColumnCount(0);
        if (file == null) {
            previewModel.addColumn("No file");
            previewModel.addRow(new Object[]{"No preview available"});
            return;
        }

        List<String> hdrs = csvHandler.getHeaders();
        if (hdrs == null || hdrs.isEmpty()) {
            previewModel.addColumn("No headers");
            previewModel.addRow(new Object[]{"No preview available"});
            return;
        }

        for (String h : hdrs) previewModel.addColumn(h);

        List<String[]> rows = csvHandler.getPreviewRows(file, 6);
        for (String[] r : rows) {
            Object[] row = new Object[hdrs.size()];
            for (int i = 0; i < hdrs.size(); i++) {
                if (i < r.length) row[i] = r[i]; else row[i] = "";
            }
            previewModel.addRow(row);
        }
    }

    private void loadAndApplyLogo(File logoFile) {
        if (logoFile == null || !logoFile.exists()) return;
        try {
            BufferedImage img = ImageIO.read(logoFile);
            if (img == null) return;
            BufferedImage rounded = makeRounded(img, 64, 64, 14);
            setIconImage(rounded);
        } catch (IOException ex) {
            // ignore errors loading logo
        }
    }

    // Create a rounded-corner BufferedImage scaled to w x h
    private BufferedImage makeRounded(BufferedImage src, int w, int h, int arc) {
        Image tmp = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // draw transparent background
        g2.setColor(new Color(0,0,0,0));
        g2.fillRect(0,0,w,h);
        // clip to rounded rect
        g2.setClip(new RoundRectangle2D.Float(0,0,w,h,arc,arc));
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return out;
    }

    private void handleSortExecutionInBackground() {
        if (csvHandler.getLastFile() == null) {
            JOptionPane.showMessageDialog(this, "Please upload a CSV first.", "No File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int visibleIndex = columnSelector.getSelectedIndex();
        if (!columnSelector.isEnabled() || visibleIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a numeric column.", "No Column", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int columnIndex = csvHandler.getNumericIndices().get(visibleIndex);
        this.dataset = csvHandler.parseCSV(csvHandler.getLastFile(), columnIndex);
        if (this.dataset == null || this.dataset.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected column contains no numeric data.", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        progressBar.setIndeterminate(true);
        progressBar.setString("Running...");
        resultsArea.setText("Sorting algorithms running...");

        SwingWorker<Map<String, Long>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Long> doInBackground() {
                return evaluator.runAllSorts(dataset, 0, dataset.size() - 1);
            }

            @Override
            protected void done() {
                try {
                    Map<String, Long> results = get();
                    String summary = evaluator.getResultsSummary(results);
                    resultsArea.setText(summary);
                } catch (Exception ex) {
                    resultsArea.setText("Error running sorts: " + ex.getMessage());
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setString("Idle");
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortingAppGUI::new);
    }
}
