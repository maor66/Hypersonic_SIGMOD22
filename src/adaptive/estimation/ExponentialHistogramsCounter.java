package adaptive.estimation;

import java.util.ArrayList;
import java.util.List;

public class ExponentialHistogramsCounter {

	private class Bucket {
		private int size;
		private long timestamp;
		
		public Bucket(long currentTimestamp) {
			this(1, currentTimestamp);
		}
		
		protected Bucket(int size, long timestamp) {
			this.size = size;
			this.timestamp = timestamp;
		}
		
		public int getSize() {
			return size;
		}
		
		public long getTimestamp() {
			return timestamp;
		}
		
		public void merge(Bucket other) {
			size += other.size;
			timestamp = other.timestamp > timestamp ? other.timestamp : timestamp;
		}
	}
	
	private List<Bucket> buckets;
	private int sum;
	private long counter;
	private long windowSize;
	private int maxBucketsOfSameSize;
	
	public ExponentialHistogramsCounter(long windowSize, double maxError) {
		buckets = new ArrayList<Bucket>();
		sum = 0;
		counter = 0;
		this.windowSize = windowSize;
		int k = (int)Math.ceil(1.0 / maxError);
		maxBucketsOfSameSize = (k % 2 == 0) ? k / 2 + 1 : (k+1) / 2 + 1;
	}
	
	private void mergeBuckets() {
		int currentBucketSize = 1;
		int numOfBucketsOfCurrentSize = 0;
		for (int i = buckets.size() - 1; i > 0; --i) {
			Bucket currentBucket = buckets.get(i);
			if (currentBucket.getSize() == currentBucketSize) {
				if (numOfBucketsOfCurrentSize == maxBucketsOfSameSize) {
					Bucket firstBucket = buckets.remove(i);
					buckets.get(i).merge(firstBucket);
					i++;//force second traversal of this index
				}
				else {
					numOfBucketsOfCurrentSize++;
				}
			}
			else {
				currentBucketSize = currentBucket.getSize();
				numOfBucketsOfCurrentSize = 1;
			}
		}
	}
	
	public void addElement(boolean value) {
		counter++;
		validateTimeWindow();
		if (!value) {
			return;
		}
		buckets.add(new Bucket(counter));
		sum++;
		mergeBuckets();
	}
	
	private void validateTimeWindow() {
		if (buckets.isEmpty()) {
			return;
		}
		Bucket oldestBucket = buckets.get(0);
		while (counter - oldestBucket.getTimestamp() > windowSize) {
			sum -= oldestBucket.getSize();
			buckets.remove(0);
			if (buckets.isEmpty()) {
				break;
			}
			oldestBucket = buckets.get(0);
		}
	}
	
	public int getSumEstimate() {
		if (buckets.isEmpty()) {
			return sum;
		}
		return sum - buckets.get(0).getSize() / 2;
	}
	
	public double getRateEstimate() {
		if (windowSize == 0) {
			return 0.0;
		}
		return (double)getSumEstimate() / windowSize;
	}
	
	public long getNumberOfRecordedElements() {
		return counter;
	}

	public long getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(long windowSize) {
		this.windowSize = windowSize;
		validateTimeWindow();
	}

}
