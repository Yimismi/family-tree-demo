package org.familytree.lcasdemo;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.ImportException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Demo {
    public static void main(String[] args) throws IOException, ImportException {
        String fileName, firstPersonName, secondPersonName;
        Scanner input = new Scanner(System.in);
        fileName = input.next();
        SimpleDirectedGraph<String, DefaultEdge> graph = buildGraph(fileName);
        MyNaiveLcaFinder<String, DefaultEdge> lcaFinder = new MyNaiveLcaFinder<>(graph);
		while (input.hasNext()) {
			firstPersonName = input.next();
        	secondPersonName = input.next();
		    Set<String> lcas = lcaFinder.findLcas(firstPersonName, secondPersonName);
			if ( lcas == null) {
				System.out.println("The two men have no common ancestor");
				continue;
			}
		    Iterator<String> it = lcas.iterator();
		    while (it.hasNext()) {
		        System.out.println(it.next());
		    }
		}
    }


    /*
     * Import graph from .dot file
     */
    static private SimpleDirectedGraph buildGraph(String fileName) throws IOException, ImportException {
        SimpleDirectedGraph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        DOTImporter<String, DefaultEdge> importer = new DOTImporter((id, attributes) -> {
            return id;
        }, (from, to, label, attributes) -> {
            return new DefaultEdge();
        });
        BufferedReader reader = null;
        try {
            importer.importGraph(graph, new BufferedReader(new FileReader(fileName)));
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        return graph;
    }
}
class MyNaiveLcaFinder<V, E> {
    private Graph<V, E> graph = null;
    public MyNaiveLcaFinder(Graph graph) {
        this.graph = graph;
    }
    public Set<V> findLcas(V a, V b) {
        return this.findLcas(Collections.singleton(a), Collections.singleton(b), new LinkedHashSet(), new LinkedHashSet());
    }
    private Set<V> findLcas(Set<V> aSet, Set<V> bSet, LinkedHashSet<V> aSeenSet, LinkedHashSet<V> bSeenSet) {
        if (aSet.size() == 0 && bSet.size() == 0) {
            return null;
        } else if (!Collections.disjoint(aSet, bSeenSet)) {
            return this.overlappingMember(aSet, bSeenSet);
        } else if (!Collections.disjoint(bSet, aSeenSet)) {
            return this.overlappingMember(bSet, aSeenSet);
        } else if (!Collections.disjoint(aSet, bSet)) {
            return this.overlappingMember(aSet, bSet);
        } else {
            aSeenSet.addAll(aSet);
            bSeenSet.addAll(bSet);
            aSet = this.allParents(aSet);
            aSet.removeAll(aSeenSet);
            bSet = this.allParents(bSet);
            bSet.removeAll(bSeenSet);
            return this.findLcas(aSet, bSet, aSeenSet, bSeenSet);
        }
    }
    private Set<V> allParents(Set<V> vertexSet) {
        HashSet<V> result = new HashSet();
        Iterator<V> var3 = vertexSet.iterator();

        while(var3.hasNext()) {
            V e = var3.next();
            Iterator<E> var5 = this.graph.incomingEdgesOf(e).iterator();

            while(var5.hasNext()) {
                E edge = var5.next();
                if (this.graph.getEdgeTarget(edge).equals(e)) {
                    result.add(this.graph.getEdgeSource(edge));
                }
            }
        }

        return result;
    }
    private Set<V> overlappingMember(Set<V> x, Set<V> y) {
        y.retainAll(x);
        return y;
    }
}
