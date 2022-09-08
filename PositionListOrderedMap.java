package aed.orderedmap;

import java.util.Comparator;
import java.util.Iterator;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

public class PositionListOrderedMap<K, V> implements OrderedMap<K, V> {
	private Comparator<K> cmp;
	private PositionList<Entry<K, V>> elements;

	/* Acabar de codificar el constructor */
	public PositionListOrderedMap(Comparator<K> cmp) {
		this.cmp = cmp;
		elements = new NodePositionList<Entry<K, V>>();
	}

	/* Ejemplo de un posible método auxiliar: */

	/*
	 * If key is in the map, return the position of the corresponding entry.
	 * Otherwise, return the position of the entry which should follow that of key.
	 * If that entry is not in the map, return null. Examples: assume key = 2, and l
	 * is the list of keys in the map. For l = [], return null; for l = [1], return
	 * null; for l = [2], return a ref. to '2'; for l = [3], return a reference to
	 * [3]; for l = [0,1], return null; for l = [2,3], return a reference to '2';
	 * for l = [1,3], return a reference to '3'.
	 */

	private Position<Entry<K, V>> findKeyPlace(K key) {
		if (key == null)
			throw new IllegalArgumentException();
		boolean encontrado = false;
		Position<Entry<K, V>> res = null;
		Position<Entry<K, V>> elem = elements.first();
		while (!encontrado && elem != null) {
			if (cmp.compare(key, elem.element().getKey()) <= 0) {
				res = elem;
				encontrado = true;
			}
			elem = elements.next(elem);
		}
		return res;
	}

	/* Podéis añadir más métodos auxiliares */

	public boolean containsKey(K key) {
		boolean res;
		Position<Entry<K, V>> comparar = findKeyPlace(key);
		if (comparar == null)
			res = false;
		else
			res = cmp.compare(key, comparar.element().getKey()) == 0;
		return res;
	}

	public V get(K key) {
		V res = null;
		Position<Entry<K, V>> comparar = findKeyPlace(key);
		if (comparar != null && containsKey(key))
			res = comparar.element().getValue();
		return res;
	}

	public V put(K key, V value) {
		V res = null;
		Position<Entry<K, V>> viejo = findKeyPlace(key);
		Entry<K, V> elemNuevo = new EntryImpl<K, V>(key, value);
		if (isEmpty() || viejo == null)
			elements.addLast(elemNuevo);
		else if (containsKey(key)) {
			res = viejo.element().getValue();
			elements.set(viejo, elemNuevo);
		} else {
			elements.addBefore(viejo, elemNuevo);
		}
		return res;
	}

	public V remove(K key) {
		V res = null;
		if (containsKey(key)) {
			Position<Entry<K, V>> eliminar = findKeyPlace(key);
			res = eliminar.element().getValue();
			elements.remove(eliminar);
		}
		return res;
	}

	public int size() {
		return elements.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Entry<K, V> floorEntry(K key) {
		Position<Entry<K, V>> elem = findKeyPlace(key);
		Entry<K, V> res = null;
		if (isEmpty() || cmp.compare(key, elements.first().element().getKey()) < 0)
			res = null;
		else if (elem == null)
			res = elements.last().element();
		else if (containsKey(key))
			res = elem.element();
		else
			res = elements.prev(elem).element();
		return res;
	}

	public Entry<K, V> ceilingEntry(K key) {
		Position<Entry<K, V>> elem = findKeyPlace(key);
		Entry<K, V> res = null;
		if (isEmpty() || elem == null)
			res = null;
		else
			res = elem.element();
		return res;
	}

	public Iterable<K> keys() {
		NodePositionList<K> res = new NodePositionList<K>();
		Iterator<Entry<K, V>> it = elements.iterator();
		while (it.hasNext()) {
			res.addLast(it.next().getKey());
		}
		return res;
	}

	public String toString() {
		return elements.toString();
	}

}
