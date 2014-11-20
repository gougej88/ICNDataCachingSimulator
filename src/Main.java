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
        //To change the number of tests change the integer for testsize
        int testsize = 100;

        for(int y=0; y <6; y++) {
            g = new Graph(5,5,y*10);
            g.createGraph();
            if(y==0) {
                for (int n = 0; n < testsize; n++)
                    //Run without cache
                    tests.add(Search.runTest(g, 1000, false));

            }else {
                for (int x = 0; x < testsize; x++) {
                    //Run with cache. Increases by 10
                    tests.add(Search.runTest(g, 1000, true));
                }//end for
            }//end else
        }


        LineChart demo = new LineChart("Average hops per request. Number of nodes:25 "+ "Number of files:"+tests.get(0).k.size()+" Number of tests: "+testsize,"Average hops per request",testsize, tests);
        demo.pack();
        demo.setVisible(true);




    }
}
