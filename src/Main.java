import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.jfree.*;


public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
        //Create graph grid of nodes(length, width, cacheSize)
        Graph g;

        //Run a test(graph, number of requests to perform, cache enabled)
        for(int y=0; y <6; y++) {
            g = new Graph(5,5,y*10);
            g.createGraph();
            if(y==0) {
                for (int n = 0; n < 100; n++)
                    tests.add(Search.runTest(g, 1000, false));
            }else {
                for (int x = 0; x < 100; x++) {

                    tests.add(Search.runTest(g, 1000, true));
                }//end for
            }//end else
        }

        LineChart demo = new LineChart("Average hops per request. Number of nodes:25 "+ "Number of files:"+tests.get(0).k.size(),"Average hops per request. Number of nodes:25 "+ "Number of files:"+tests.get(0).k.size(), tests);
        demo.pack();
        demo.setVisible(true);




    }
}
