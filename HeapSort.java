public class HeapSort {
    public static void heapSort(FlightPath[] array, char mode){
        int n = array.length;

        buildMaxHeap(array, n, mode);

        for(int i = n - 1; i > 0; i--){
            FlightPath temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            maxHeapify(array, 0, i, mode);
        }
    }

    private static void buildMaxHeap(FlightPath[] array, int n, char mode){
        for(int i = n / 2 - 1; i >= 0; i--){
            maxHeapify(array, i, n, mode);
        }
    }

    //Ensure the subtree rooted at index i is a max-heap
    private static void maxHeapify(FlightPath[] array, int i, int heapSize, char mode){
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;

        if(left < heapSize && compare(array[left], array[largest], mode) > 0){
            largest = left;
        }

        if(right < heapSize && compare(array[right], array[largest], mode) > 0){
            largest = right;
        }

        if(largest != i){
            FlightPath swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;

            maxHeapify(array, largest, heapSize, mode);
        }
    }

    //compare two FlightPaths based on mode
    private static int compare(FlightPath a, FlightPath b, char mode){
        if(mode == 'T'){
            return a.getTotalTime() - b.getTotalTime();
        }else{
            double costDiff = a.getTotalCost() - b.getTotalCost();
            if(costDiff < 0){
                return -1;
            }
            else if(costDiff > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
    }
}
