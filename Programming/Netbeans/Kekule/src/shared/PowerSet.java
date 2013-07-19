package shared;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class PowerSet<E> implements Iterator<Set<E>>, Iterable<Set<E>> {
	private E[] arr = null;
	private BitSet bset = null;
	private int minSize;
	private int maxSize;

	@SuppressWarnings("unchecked")
	public PowerSet(Set<E> set, int min, int max) {
		arr = (E[]) set.toArray();
		bset = new BitSet(arr.length + 1);
		this.minSize = min;
		this.maxSize = max;
	}

	@Override
	public boolean hasNext() {
		return !bset.get(arr.length);
	}

	@Override
	public Set<E> next() {
		Set<E> returnSet = new TreeSet<E>();
		// System.out.println(printBitSet());
		for (int i = 0; i < arr.length; i++) {
			if (bset.get(i)) {
				returnSet.add(arr[i]);
			}
		}

		int count;
		do {
			incrementBitSet();
			count = countBitSet();
		} while ((count < minSize) || (count > maxSize));

		return returnSet;
	}

	protected void incrementBitSet() {
		for (int i = 0; i < bset.size(); i++) {
			if (!bset.get(i)) {
				bset.set(i);
				break;
			} else
				bset.clear(i);
		}
	}

	protected int countBitSet() {
		int count = 0;
		for (int i = 0; i < bset.size(); i++) {
			if (bset.get(i)) {
				count++;
			}
		}
		return count;

	}

	protected String printBitSet() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bset.size(); i++) {
			if (bset.get(i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}
		return builder.toString();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not Supported!");
	}

	@Override
	public Iterator<Set<E>> iterator() {
		return this;
	}
}
