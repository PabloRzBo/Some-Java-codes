package aed.delivery;

import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.set.HashTableMapSet;
import es.upm.aedlib.set.Set;
import java.util.Iterator;
import es.upm.aedlib.indexedlist.ArrayIndexedList;

public class Delivery<V> {

	private DirectedGraph<V, Integer> graph;
	private ArrayIndexedList<Vertex<V>> vertices;

	// Construct a graph out of a series of vertices and an adjacency matrix.
	// There are 'len' vertices. A negative number means no connection. A
	// non-negative
	// number represents distance between nodes.
	public Delivery(V[] places, Integer[][] gmat) {
		graph = new DirectedAdjacencyListGraph<V, Integer>();
		vertices = new ArrayIndexedList<Vertex<V>>();
		for (int i = 0; i < places.length; i++)
			vertices.add(i, graph.insertVertex(places[i]));
		for (int i = 0; i < gmat.length; i++) {
			for (int j = 0; j < gmat[i].length; j++) {
				if (gmat[i][j] != null)
					graph.insertDirectedEdge(vertices.get(i), vertices.get(j), gmat[i][j]);
			}
		}
	}

	// Just return the graph that was constructed
	public DirectedGraph<V, Integer> getGraph() {
		return graph;
	}

	// Return a Hamiltonian path for the stored graph, or null if there is noe.
	// The list containts a series of vertices, with no repetitions (even if the
	// path
	// can be expanded to a cycle).
	public PositionList<Vertex<V>> tour() {
		PositionList<Vertex<V>> res = new NodePositionList<Vertex<V>>();
		Set<Vertex<V>> visited = new HashTableMapSet<Vertex<V>>();
		Iterator<Vertex<V>> it = graph.vertices().iterator();
		while (it.hasNext() && res.size() != graph.numVertices()) {
			Vertex<V> vertice = it.next();
			res.addLast(vertice);
			visited.add(vertice);
			res = tourRec(res, visited, vertice);
		}
		if (res.size() == 1 || res.isEmpty()) 
			res = null;
		return res;
	}

	private <E> PositionList<Vertex<V>> tourRec(PositionList<Vertex<V>> res, Set<Vertex<V>> visited,
			Vertex<V> vertice) {
		Iterator<Edge<Integer>> it = graph.outgoingEdges(vertice).iterator();
		while (it.hasNext() && res.size() != graph.numVertices()) {
			Vertex<V> verticeLlegada = graph.endVertex(it.next());
			if (!visited.contains(verticeLlegada)) {
				res.addLast(verticeLlegada);
				visited.add(vertice);
				res = tourRec(res, visited, verticeLlegada);
			}
		}
		if (res.size() != graph.numVertices()) {
			res.remove(res.last());
			visited.remove(vertice);
		}
		return res;
	}

	public int length(PositionList<Vertex<V>> path) {
		int res = 0;
		if (path != null && path.size() > 1) {
			Position<Vertex<V>> actual = path.first();
			Position<Vertex<V>> siguiente = path.next(actual);
			while (siguiente != null) {
				Iterator<Edge<Integer>> caminos = graph.outgoingEdges(actual.element()).iterator();
				boolean encontrado = false;
				while (caminos.hasNext() && !encontrado) {
					Edge<Integer> camino = caminos.next();
					if (graph.endVertex(camino) == siguiente.element()) {
						res += camino.element();
						encontrado = true;
					}
				}
				actual = siguiente;
				siguiente = path.next(siguiente);
			}
		}
		return res;
	}

	public String toString() {
		return "Delivery";
	}
}
