import java.io.*;
import java.util.*;

/**
 * QuickSort template
 */
public class QuickSort<T>{
	
	private final Comparator<T> comp;
	
	public QuickSort(Comparator<T> comp){
		this.comp = comp;
	}
	
	public void sort(T[] array){
		if(array == null) return;
		if(array.length < 2) return;
		sort(array, 0, array.length);
	}
	
	private void sort(T[] array, int start, int end){
		if(start == end) return;
		int pivot = selectPivot(array, start, end);
		pivot = partition(array, start, end, pivot, comp);
		sort(array, start, pivot);
		sort(array, pivot+1, end);
	}
	
	private void exchange(T[] array, int i1, int i2){
		T temp = array[i1];
		array[i1] = array[i2];
		array[i2] = temp;
	}
	
	/**
	 * Select a pivort from an array.
	 * @param array the array
	 * @param start the start index (inclusive)
	 * @param end the end index (exclusive)
	 * @return the index of selected pivot
	 */
	protected int selectPivot(T[] array, int start, int end){
		return (int)(start + Math.random()*(end - start));
	}
	
	/**
	 * Partition the array using a pivot.
	 * @param array the array
	 * @param start the start index (inclusive)
	 * @param end the end index (exclusive)
	 * @param pivot the index of pivot
	 * @return the position of pivot after partitioning
	 */
	protected int partition(T[] array, int start, int end, int pivot, Comparator<T> comp){
		exchange(array, start, pivot);
		int i = start+1;
		int j = end-1;
		while(i <= j){
			if(comp.compare(array[i], array[start]) > 0) 
				exchange(array, i, j--);
			else 
				i++;
		}
		exchange(array, start, j);
		return j;
	}

	
	/**
	 * Test
	 */
	public static void main(String[] args){
		
		QuickSort<Integer> sort = new QuickSort<Integer>(new Comparator<Integer>(){
			@Override
			public int compare(Integer a, Integer b){
				return a - b;
			}
		});
		
		Integer[] array = new Integer[args.length];
		for(int i=0; i<args.length; i++)
			array[i] = Integer.parseInt(args[i]);
		sort.sort(array);
		System.out.println(Arrays.toString(array));
		
	}
}