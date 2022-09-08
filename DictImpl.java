package aed.tries;

import java.util.Arrays;
import java.util.Iterator;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.tree.GeneralTree;
import es.upm.aedlib.tree.LinkedGeneralTree;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

public class DictImpl implements Dictionary {
	// A boolean because we need to know if a word ends in a node or not
	GeneralTree<Pair<Character, Boolean>> tree;

	public DictImpl() {
		tree = new LinkedGeneralTree<>();
		tree.addRoot(new Pair<Character, Boolean>(null, false));
	}

	public void add(String word) {
		if (word == null || word.length() == 0)
			throw new IllegalArgumentException();
		Position<Pair<Character, Boolean>> cursor = tree.root();
		for(int i = 0; i<word.length(); i++) {
			char letra = word.charAt(i);
			Position<Pair<Character, Boolean>> cursorAux = searchChildLabelledBy(letra, cursor);
			if (cursorAux==null)
				cursorAux = addChildAlphabetically(new Pair<Character, Boolean>(letra, false), cursor);
			cursor = cursorAux;
		}
		cursor.element().setRight(true);
	}

	public void delete(String word) {
		if (word == null || word.length() == 0)
			throw new IllegalArgumentException();
		Position<Pair<Character, Boolean>> pos = findPos(word);
		if (pos != null)
			pos.element().setRight(false);
	}

	public boolean isIncluded(String word) {
		if (word == null || word.length() == 0)
			throw new IllegalArgumentException();
		Position<Pair<Character, Boolean>> pos = findPos(word);
		return (pos != null && pos.element().getRight());
	}

	public PositionList<String> wordsBeginningWithPrefix(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException();
		PositionList<String> res = new NodePositionList<String>();
		Position<Pair<Character, Boolean>> cursor = findPos(prefix);
		if (cursor != null)
			wordsBeginningWithPrefixRec(res, cursor, prefix);
		return res;
	}

	private void wordsBeginningWithPrefixRec(PositionList<String> res, Position<Pair<Character, Boolean>> cursor,
			String prefix) {
		if (cursor.element().getRight())
			res.addLast(prefix);
		Iterator<Position<Pair<Character, Boolean>>> it = tree.children(cursor).iterator();
		while (it.hasNext()) {
			Position<Pair<Character, Boolean>> children = it.next();
			wordsBeginningWithPrefixRec(res, children, prefix + children.element().getLeft());
		}
	}

	private Position<Pair<Character, Boolean>> searchChildLabelledBy(char letra,
			Position<Pair<Character, Boolean>> pos) {
		Position<Pair<Character, Boolean>> res = null;
		Iterator<Position<Pair<Character, Boolean>>> it = tree.children(pos).iterator();
		boolean encontrado = false;
		while (it.hasNext() && !encontrado) {
			Position<Pair<Character, Boolean>> cursor = it.next();
			if (cursor.element().getLeft().equals(letra)) {
				res = cursor;
				encontrado = true;
			}
		}
		return res;
	}

	private Position<Pair<Character, Boolean>> findPos(String prefix) {
		Position<Pair<Character, Boolean>> cursor = tree.root();
		int i = 0;
		while (i < prefix.length() && cursor != null) {
			cursor = searchChildLabelledBy(prefix.charAt(i), cursor);
			i++;
		}
		return cursor;
	}

	private Position<Pair<Character, Boolean>> addChildAlphabetically(Pair<Character, Boolean> pair,
			Position<Pair<Character, Boolean>> pos) {
		Iterator<Position<Pair<Character, Boolean>>> it = tree.children(pos).iterator();
		boolean insertado = false;
		while (it.hasNext() && !insertado) {
			Position<Pair<Character, Boolean>> cursor = it.next();
			if (cursor.element().getLeft().compareTo(pair.getLeft()) > 0) {
				tree.insertSiblingBefore(cursor, pair);
				insertado = true;
			}
		}
		if (!it.hasNext() && !insertado)
			tree.addChildLast(pos, pair);
		return searchChildLabelledBy(pair.getLeft(), pos);
	}

}
