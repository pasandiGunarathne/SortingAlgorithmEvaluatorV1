import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceEvaluator {
    private SortingService sorter = new SortingService();

    // D2: Execution Trigger Logic
    public Map<String, Long> runAllSorts(List<Record> originalData, int low, int high) {
        Map<String, Long> results = new HashMap<>();

        // CRUCIAL: Must create a deep copy for each run since sorting modifies the list!
        
        // --- Insertion Sort (C1) ---
        List<Record> dataCopy1 = new ArrayList<>(originalData);
        long time1 = measureTime(() -> sorter.insertionSort(dataCopy1));
        results.put("Insertion Sort", time1);

        // --- Shell Sort (C2) ---
        List<Record> dataCopy2 = new ArrayList<>(originalData);
        long time2 = measureTime(() -> sorter.shellSort(dataCopy2));
        results.put("Shell Sort", time2);

        // --- Merge Sort (C3) ---
        List<Record> dataCopy3 = new ArrayList<>(originalData);
        long time3 = measureTime(() -> sorter.mergeSort(dataCopy3));
        results.put("Merge Sort", time3);

        // --- Quick Sort (C4) ---
        List<Record> dataCopy4 = new ArrayList<>(originalData);
        // Note: Quick Sort needs the low/high indices
        long time4 = measureTime(() -> sorter.quickSort(dataCopy4, low, high));
        results.put("Quick Sort", time4);

        // --- Heap Sort (C5) ---
        List<Record> dataCopy5 = new ArrayList<>(originalData);
        long time5 = measureTime(() -> sorter.heapSort(dataCopy5));
        results.put("Heap Sort", time5);
        
        return results;
    }

    // D1: Time Measurement Utility
    private long measureTime(Runnable method) {
        long startTime = System.nanoTime();
        method.run(); // Execute the sorting method
        long endTime = System.nanoTime();
        
        // Convert nanoseconds to milliseconds
        return (endTime - startTime) / 1_000_000;
    }

    // D3: Results Display and Best Algorithm Identification
    public String getResultsSummary(Map<String, Long> results) {
        StringBuilder summary = new StringBuilder("--- Performance Results ---\n");
        String fastestAlgorithm = "";
        long shortestTime = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : results.entrySet()) {
            summary.append(String.format("%s: %d ms\n", entry.getKey(), entry.getValue()));
            
            if (entry.getValue() < shortestTime) {
                shortestTime = entry.getValue();
                fastestAlgorithm = entry.getKey();
            }
        }

        summary.append("\n=================================");
        summary.append(String.format("\nBEST PERFORMER: %s (%d ms)\n", fastestAlgorithm, shortestTime));
        summary.append("=================================");
        
        return summary.toString();
    }
}