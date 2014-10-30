import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.jfree.*;


public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {


        //Create graph grid of nodes
        Graph g = new Graph(5,5);
        g.createGraph();


        //Search for random content starting on random node
        System.out.println("Starting test routing...");
        System.out.println();


        //Create an arraylist of all tests, and results
        ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
        //Run a test(graph, number of requests to perform, cache enabled)
        tests.add(Search.runTest(g, 500, true));
        tests.add(Search.runTest(g, 500, false));
        tests.add(Search.runTest(g, 1000, true));
        tests.add(Search.runTest(g, 1000, false));
        tests.add(Search.runTest(g, 2000, true));
        tests.add(Search.runTest(g, 2000, false));
        tests.add(Search.runTest(g, 3000, true));
        tests.add(Search.runTest(g, 3000, false));
        tests.add(Search.runTest(g, 4000, true));
        tests.add(Search.runTest(g, 4000, false));
        tests.add(Search.runTest(g, 5000, true));
        tests.add(Search.runTest(g, 5000, false));


        LineChart demo = new LineChart("Test","This is a test:"+tests.size(), tests);
        demo.pack();
        demo.setVisible(true);




    }
}
