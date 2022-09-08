package aed.individual4;

import java.util.Iterator;
import java.util.NoSuchElementException;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;

public class MultiSetListIterator<E> implements Iterator<E> {
	PositionList<Pair<E, Integer>> list;

	Position<Pair<E, Integer>> cursor;
	int counter;
	Position<Pair<E, Integer>> prevCursor;

	public MultiSetListIterator(PositionList<Pair<E, Integer>> list) {
		this.list = list;
		if (!list.isEmpty()) {
			cursor = list.first();
			counter = cursor.element().getRight();
		}
	}

	public boolean hasNext() {
		return cursor != null;
	}

	public E next() {
		if (cursor == null)
			throw new NoSuchElementException();
		prevCursor = cursor;
		if (counter != 1)
			counter--;
		else if (list.next(cursor) != null) {
			cursor = list.next(cursor);
			counter = cursor.element().getRight();
		} else
			cursor = list.next(cursor);
		return prevCursor.element().getLeft();
	}

	public void remove() {
		if (prevCursor == null)
			throw new IllegalStateException();
		prevCursor.element().setRight(prevCursor.element().getRight() - 1);
		if (prevCursor.element().getRight() == 0)
			list.remove(prevCursor);
		prevCursor = null;
	}

	public static <E> E get(PositionList<E> list, int n) {
		E res = null;
		if (n > 0 && n < list.size() - 1) {
			Position<E> cursor = list.first();
			for (int i = 0; i < n; i++) {
				cursor = list.next(cursor);
			}
			res = cursor.element();
		}
		return res;
	}

	public static <E> void delete(PositionList<E> list, Position<E> pos1, Position<E> pos2) {
		if (pos1.equals(pos2))
			list.remove(pos1);
		else {
			Position<E> cursor = list.first();
			while (!cursor.equals(pos1) && !cursor.equals(pos2)) {
				cursor = list.next(cursor);
			}
			cursor =list.next(cursor);
			Position<E> cursorAux = list.prev(cursor);
			while (!cursor.equals(pos1) && !cursor.equals(pos2)) {
				list.remove(cursorAux);
				cursor = list.next(cursor);
				cursorAux = list.prev(cursor);
			}
			list.remove(cursorAux);
			list.remove(cursor);
		}
	}
}
