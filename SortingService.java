
import java.util.List;
import java.util.ArrayList;

public class SortingService {

    // Insertion Sort
    public void insertionSort(List<Record> array) {
        if (array == null) return;
        for (int i = 1; i < array.size(); i++) {
            Record key = array.get(i);
            double keyVal = key.getSortValue();
            int j = i - 1;
            while (j >= 0 && array.get(j).getSortValue() > keyVal) {
                array.set(j + 1, array.get(j));
                j--;
            }
            array.set(j + 1, key);
        }
    }

    // Shell Sort
    public void shellSort(List<Record> array) {
        if (array == null) return;
        int n = array.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Record temp = array.get(i);
                double tempVal = temp.getSortValue();
                int j = i;
                while (j >= gap && array.get(j - gap).getSortValue() > tempVal) {
                    array.set(j, array.get(j - gap));
                    j -= gap;
                }
                array.set(j, temp);
            }
        }
    }

    // Merge Sort
    public void mergeSort(List<Record> array) {
        if (array == null || array.size() < 2) return;
        mergeSortHelper(array, 0, array.size() - 1);
    }

    private void mergeSortHelper(List<Record> array, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSortHelper(array, left, mid);
        mergeSortHelper(array, mid + 1, right);
        merge(array, left, mid, right);
    }

    private void merge(List<Record> array, int left, int mid, int right) {
        List<Record> leftList = new ArrayList<>();
        List<Record> rightList = new ArrayList<>();
        for (int i = left; i <= mid; i++) leftList.add(array.get(i));
        for (int i = mid + 1; i <= right; i++) rightList.add(array.get(i));

        int i = 0, j = 0, k = left;
        while (i < leftList.size() && j < rightList.size()) {
            if (leftList.get(i).getSortValue() <= rightList.get(j).getSortValue()) {
                array.set(k++, leftList.get(i++));
            } else {
                array.set(k++, rightList.get(j++));
            }
        }
        while (i < leftList.size()) array.set(k++, leftList.get(i++));
        while (j < rightList.size()) array.set(k++, rightList.get(j++));
    }

    // Quick Sort
    public void quickSort(List<Record> array, int low, int high) {
        if (array == null || array.size() == 0) return;
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(List<Record> array, int low, int high) {
        Record pivot = array.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array.get(j).getSortValue() < pivot.getSortValue()) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    // Heap Sort
    public void heapSort(List<Record> array) {
        if (array == null) return;
        int n = array.size();
        for (int i = n / 2 - 1; i >= 0; i--) heapify(array, n, i);
        for (int i = n - 1; i > 0; i--) {
            swap(array, 0, i);
            heapify(array, i, 0);
        }
    }

    private void heapify(List<Record> array, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && array.get(l).getSortValue() > array.get(largest).getSortValue()) largest = l;
        if (r < n && array.get(r).getSortValue() > array.get(largest).getSortValue()) largest = r;
        if (largest != i) {
            swap(array, i, largest);
            heapify(array, n, largest);
        }
    }

    // Utility: swap two elements in the list
    private void swap(List<Record> array, int i, int j) {
        Record tmp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, tmp);
    }
}
