package aed.hashtable;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Arrays;

import es.upm.aedlib.Entry;
import es.upm.aedlib.EntryImpl;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.InvalidKeyException;

/**
 * A hash table implementing using open addressing to handle key collisions.
 */
public class HashTable<K, V> implements Map<K, V> {
	Entry<K, V>[] buckets;
	int size;

	public HashTable(int initialSize) {
		this.buckets = createArray(initialSize);
		this.size = 0;
	}

	/**
	 * Add here the method necessary to implement the Map api, and any auxilliary
	 * methods you deem convient.
	 */

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean containsKey(Object arg0) throws InvalidKeyException {
		if (arg0 == null)
			throw new InvalidKeyException();
		int indice = search(arg0);
		return indice != -1 && buckets[indice] != null;
	}

	@Override
	public V get(K arg0) throws InvalidKeyException {
		if (arg0 == null)
			throw new InvalidKeyException();
		V res = null;
		int indice = search(arg0);
		if (indice != -1 && buckets[indice] != null)
			res = buckets[indice].getValue();
		return res;
	}

	@Override
	public V put(K arg0, V arg1) throws InvalidKeyException {
		if (arg0 == null)
			throw new InvalidKeyException();
		V res = null;
		int indice = search(arg0);
		if (indice == -1) {
			rehash();
			indice = search(arg0);
		} else if (buckets[indice] != null)
			res = buckets[indice].getValue();
		if (buckets[indice] == null)
			size++;
		buckets[indice] = new EntryImpl<K, V>(arg0, arg1);
		return res;
	}

	@Override
	public V remove(K arg0) throws InvalidKeyException {
		if (arg0 == null)
			throw new InvalidKeyException();
		V res = null;
		int indice = search(arg0);
		if (indice != -1 && buckets[indice] != null) {
			res = buckets[indice].getValue();
			buckets[indice] = null;
			size--;
			int indiceAux = indice;
			for (int i = index(indice + 1); i != indice; i = index(i + 1)) {
				if (buckets[i] != null && search(buckets[i].getKey()) == indiceAux) {
					buckets[indiceAux] = buckets[i];
					buckets[i] = null;
					indiceAux = i;
				}
			}
		} 
		return res;
	}

	@Override
	public Iterable<K> keys() {
		K[] res = (K[]) new Integer[size];
		int contador = 0;
		for (int i = 0; i < buckets.length; i++) {
			if (buckets[i] != null) {
				res[contador] = buckets[i].getKey();
				contador++;
			}
		}
		return Arrays.asList(res);
	}

	@Override
	public Iterable<Entry<K, V>> entries() {
		Entry<K, V>[] res = createArray(size);
		int contador = 0;
		for (int i = 0; i < buckets.length; i++) {
			if (buckets[i] != null) {
				res[contador] = buckets[i];
				contador++;
			}
		}
		return Arrays.asList(res);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return entries().iterator();
	}

	// Examples of auxilliary methods: IT IS NOT REQUIRED TO IMPLEMENT THEM

	@SuppressWarnings("unchecked")
	private Entry<K, V>[] createArray(int size) {
		Entry<K, V>[] buckets = (Entry<K, V>[]) new Entry[size];
		return buckets;
	}

	// Returns the bucket index of an object
	private int index(Object obj) {
		return obj.hashCode() % buckets.length;
	}

	// Returns the index where an entry with the key is located,
	// or if no such entry exists, the "next" bucket with no entry,
	// or if all buckets stores an entry, -1 is returned.
	private int search(Object obj) {
		int res = index(obj);
		while (res != -1 && buckets[res] != null && !buckets[res].getKey().equals(obj.hashCode())) {
			res = index(res + 1);
			if (res == index(obj))
				res = -1;
		}
		return res;
	}

	// Doubles the size of the bucket array, and inserts all entries present
	// in the old bucket array into the new bucket array, in their correct
	// places. Remember that the index of an entry will likely change in
	// the new array, as the size of the array changes.
	private void rehash() {
		Entry<K, V>[] bucketsAux = buckets;
		buckets = createArray(buckets.length * 2);
		for (int i = 0; i < bucketsAux.length; i++) {
			buckets[search(bucketsAux[i].getKey())] = bucketsAux[i];
		}
	}

}
