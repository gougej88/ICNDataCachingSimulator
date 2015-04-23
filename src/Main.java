import java.util.*;



public class Main {

    public static void main(String[] args) throws java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<ArrayList<PacketTracer>> allTests = new ArrayList<ArrayList<PacketTracer>>();
        //Create graph grid of nodes(length, width, cacheSize)
        ArrayList<ArrayList<Integer>> attacks = new ArrayList<ArrayList<Integer>>();
        //LineGraph g;
        Graph g = null;
        Boolean usingCache = true;

        //Run a test(graph, number of requests to perform, cache enabled, number of attackers)
        //To change the number of tests change the integer for testsize
        int testsize = 5;
        int requestsPerTest = 10000;
        double poissonRate = .65;
        double zipfianAlpha = .65;
        //Now done in attackers array
        int numAttackers = 2;
        //Now done as 80% of cache size
        int numUnpopularItems = 100;
        int numContentItems = 250;
        int AttackerRequestRate = 2;

        //1 = LRU, 2 = FIFO, 3=Random
        int cacheType;

        //Add attack types to test
        ArrayList<Integer> attackers = new ArrayList<Integer>();
        //Make sure to always start with 0 attackers
        attackers.add(0);
        attackers.add(1);
        attackers.add(2);
        //attackers.add(4);

        //Loop for number of cache types
        for(int c = 1; c < 4; c++) {
            cacheType = c;
            ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
            //Loop for number of unique cache sizes
            //change y back to 6 for full test
            for (int y = 0; y < 6; y++) {
                int unpopPerCache = (int)((y*10)*.8);
                if(unpopPerCache==0)
                    unpopPerCache=10;

                    //Start test

                    //Loop on types of attacks
                    for(int a = 0; a < attackers.size();a++) {

                        //Change graph to add attackers
                        if(a>0){
                            g.addAttackersToExistingGraph(attackers.get(a));

                        }//end if
                        //Loop number of tests per attack
                        for (int n = 0; n < testsize; n++) {

                            //SQUARE GRAPH
                            //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                            if (a == 0 && n == 0) {
                                g = new Graph(5, 5, y * 10, zipfianAlpha, cacheType, attackers.get(a), unpopPerCache, numContentItems, requestsPerTest);
                                g.createGraph();
                            } else {
                                g.resetGraphStats();
                            }
                            //Run without cache
                            System.out.println("Test Number: " + n);
                            double requestRateString = AttackerRequestRate;
                            if(attackers.get(a)==0)
                                requestRateString=0;
                            System.out.println("Number of attackers-rate: " + attackers.get(a) + "-" + requestRateString);
                            if(y==0){
                                usingCache=false;
                            }else{
                                usingCache=true;
                            }
                            tests.add(Search.runTest(g, requestsPerTest, poissonRate, AttackerRequestRate, usingCache));


                        /* //LineGraph
                        //g = new LineGraph(5, y * 10, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g = new LineGraph(5, y, zipfianAlpha, cacheType, numAttackers, numUnpopularItems, numContentItems, requestsPerTest);
                        g.createLineGraph();
                        //Run without cache
                        System.out.println("Test Number: " +n);
                        tests.add(SearchLineGraph.runTest(g, requestsPerTest, poissonRate, false));
                        */
                        }//end for loop number of tests per attack


                    }//end for loop types of attacks


            }//end for cachesize tests
            allTests.add(tests);
        }//end for cachetype tests


        LineChart demo = new LineChart("Average hops per request. Alpha:"+zipfianAlpha +" Nodes:25 Requests per test:"+requestsPerTest+" Number of tests:"+testsize,"Average hops per request",testsize, allTests, attackers);
       // demo.pack();
        //demo.setVisible(true);




    }
}
