package org.familytree.lcasdemo;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.ImportException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.jgrapht.alg.NaiveLcaFinder;

public class Demo {
    public static void main(String[] args) throws IOException, ImportException {
        String fileName, firstPersonName, secondPersonName;
        Scanner input = new Scanner(System.in);
        fileName = input.next();
        SimpleDirectedGraph<String, DefaultEdge> graph = buildGraph(fileName);
        removeCircle(graph);
        NaiveLcaFinder<String, DefaultEdge> lcaFinder = new NaiveLcaFinder<>(graph);
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
    private static SimpleDirectedGraph buildGraph(String fileName) throws IOException, ImportException {
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

    /*
     * Remove conjugal relation or potential conjugal relationship
     */
    private static void removeCircle(SimpleDirectedGraph graph) {
        Object[] vectexs = graph.vertexSet().toArray();
        for(int i = 0; i < vectexs.length - 1; i++) {
            for(int j = i + 1; j < vectexs.length; j++) {
                if(graph.containsEdge(vectexs[i], vectexs[j]) && graph.containsEdge(vectexs[j], vectexs[i])) {
                    graph.removeEdge(vectexs[i], vectexs[j]);
                    graph.removeEdge(vectexs[j], vectexs[i]);
                }
            }
        }
    }

}

