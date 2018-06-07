package test;

import katz.KatzCentrality;
import org.apache.commons.math3.linear.*;
import org.graphstream.algorithm.measure.AbstractCentrality;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * Created by Radek2 on 19.04.2018.
 */
public class Main{
    public static void main(String[] args) {
        Graph graph = new SingleGraph("Tutorial 1");
        graph.addNode("1" );
        graph.addNode("2" );
        graph.addNode("3" );
        graph.addNode("4" );
        graph.addNode("5" );
        graph.addNode("6" );
        graph.addNode("7" );
        graph.addNode("8" );
        graph.addNode("9" );
        graph.addNode("10" );
        graph.addNode("11" );

        graph.addEdge("a", "1", "3", false);
        graph.addEdge("b", "2", "3", false);

        graph.addEdge("c", "3", "4", false);
        graph.addEdge("d", "3", "5", false);
        graph.addEdge("e", "4", "6", false);

        graph.addEdge("f", "5", "6", false);
        graph.addEdge("g", "5", "7", false);

        graph.addEdge("h", "6", "8", false);
        graph.addEdge("i", "7", "8", false);

        graph.addEdge("j", "8", "9", false);
        graph.addEdge("k", "9", "10", false);
        graph.addEdge("l", "9", "11", false);

        double alfa = 0.1;
        double beta = 1;

        int n = graph.getNodeCount();
        double adjacencyMatrix[][] = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adjacencyMatrix[i][j] = (graph.getNode(i).hasEdgeBetween(j) ? 1 : 0);





        KatzCentrality kc = new KatzCentrality("katz", AbstractCentrality.NormalizationMode.SUM_IS_1);
        kc.init(graph);
        //kc.computeCentrality();

        System.out.println();
        RealVector kcv = kc.getKatzCentralityVector();
        System.out.println(kcv);
        kc.setKatzAttribute();
        //graph = kc.getGraphWithKatzCentrality();
        int a = 0;
        for(Node node: graph) {
            node.addAttribute("ui.label", kcv.getEntry(a));
            System.out.println("KatzCentrality for node "+node.getId()+":    "+node.getAttribute("katz"));
            //node.setAttribute("katz", kcv.getEntry(a));
            a++;
        }
        graph.display();


}

}
