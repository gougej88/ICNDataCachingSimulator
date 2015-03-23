import java.util.*;



public class Main {

    public static void main(String[] args) throws java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<ArrayList<PacketTracer>> allTests = new ArrayList<ArrayList<PacketTracer>>();
        //Create graph grid of nodes(length, width, cacheSize)
        Graph g;

        //Run a test(graph, number of requests to perform, cache enabled, number of attackers)
        //To change the number of tests change the integer for testsize
        int testsize = 20;
        int requestsPerTest = 200000;
        double poissonRate = .65;
        double zipfianAlpha = .65;
        int numAttackers = 1;
        int numUnpopularItems = 50;
        int numContentItems = 250;

        //1 = LRU, 2 = FIFO, 3=Random
        int cacheType;

        //Loop for number of cache types
        for(int c = 1; c < 4; c++) {
            cacheType = c;
            ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
            //Loop for number of unique cache sizes
            for (int y = 0; y < 6; y++) {

                //Start test
                if (y == 0) {
                    for (int n = 0; n < testsize; n++) {
                        //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                        g = new Graph(5, 5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createGraph();
                        //Run without cache
                        System.out.println("Test Number: " +n);
                        tests.add(Search.runTest(g, requestsPerTest, poissonRate, false));
                    }

                } else {
                    for (int x = 0; x < testsize; x++) {
                        //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                        g = new Graph(5, 5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createGraph();
                        //Run with cache.
                        System.out.println("Test Number: " +x);
                        tests.add(Search.runTest(g, requestsPerTest, poissonRate, true));
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
