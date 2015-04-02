import java.util.*;



public class Main {

    public static void main(String[] args) throws java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<ArrayList<PacketTracer>> allTests = new ArrayList<ArrayList<PacketTracer>>();
        //Create graph grid of nodes(length, width, cacheSize)
        LineGraph g;

        //Run a test(graph, number of requests to perform, cache enabled, number of attackers)
        //To change the number of tests change the integer for testsize
        int testsize = 5;
        int requestsPerTest = 500;
        double poissonRate = .65;
        double zipfianAlpha = .65;
        int numAttackers = 0;
        int numUnpopularItems = 1;
        int numContentItems = 2;

        //1 = LRU, 2 = FIFO, 3=Random
        int cacheType;

        //Loop for number of cache types
        for(int c = 1; c < 4; c++) {
            cacheType = c;
            ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
            //Loop for number of unique cache sizes
            //change y back to 6 for full test
            for (int y = 0; y < 2; y++) {

                //Start test
                if (y == 0) {
                    for (int n = 0; n < testsize; n++) {

                        /* SQUARE GRAPH
                        //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                        g = new Graph(5, 5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createGraph();
                        //Run without cache
                        System.out.println("Test Number: " +n);
                        tests.add(Search.runTest(g, requestsPerTest, poissonRate, false));
                        */

                        //LineGraph
                        //g = new LineGraph(5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g = new LineGraph(5, y, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createLineGraph();
                        //Run without cache
                        System.out.println("Test Number: " +n);
                        tests.add(SearchLineGraph.runTest(g, requestsPerTest, poissonRate, false));
                    }

                } else {
                    for (int x = 0; x < testsize; x++) {
                        /* SQUARE GRAPH
                        //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                        g = new Graph(5, 5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createGraph();
                        //Run with cache.
                        System.out.println("Test Number: " +x);
                        tests.add(Search.runTest(g, requestsPerTest, poissonRate, true));
                        */
                        //LineGraph
                        //g = new LineGraph(5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g = new LineGraph(5, y, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createLineGraph();
                        //Run with cache.
                        System.out.println("Test Number: " +x);
                        tests.add(SearchLineGraph.runTest(g, requestsPerTest, poissonRate, true));

                    }//end for number of tests
                }//end else
            }//end for cachesize tests
            allTests.add(tests);
        }//end for cachetype tests


        LineChart demo = new LineChart("Average hops per request. Alpha:"+zipfianAlpha +" Nodes:25 Requests per test:"+requestsPerTest+" Number of tests:"+testsize,"Average hops per request",testsize, allTests);
        demo.pack();
        demo.setVisible(true);




    }
}
