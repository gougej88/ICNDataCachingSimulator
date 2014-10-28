import javax.swing.*;
import java.util.*;


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

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }
}
