import java.util.*;



public class Main {

    public static void main(String[] args) throws java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<ArrayList<PacketTracer>> allTests = new ArrayList<ArrayList<PacketTracer>>();
        //Create graph grid of nodes(length, width, cacheSize)
        Graph g = null;
        Boolean usingCache = true;

        //Run a test(graph, number of requests to perform, cache enabled, number of attackers)
        //To change the number of tests change the integer for testsize
        //Graph type. 1 = square, 2= Gnutella
        int graphType = 1;
        int testsize = 10;
        int requestsPerTest = 100000;
        Boolean useCharacteristicTimeAttack = true;
        //Not used for request rate. Using popularity distribution
        double poissonRate = .65;
        double zipfianAlpha = .65;
        double percentCustodians = .20;
        //Make this number divide into the number of custodians equally
        int numContentItems = 500;
        int AttackerRequestRate = 4;
        //Tested with square graphs of size = 25, and 100
        int graphSize = 100;

        //When this is set make sure to only test with 0,1,and 2 attackers on the square graph only.
        //This should only be used for a quick test on graphSize=25
        Boolean fixSquareGraph = false;

        //Variable to set weather to keep stats on only cache hit packets or all of the last 30% of every test
        Boolean keepCacheHitsOnly = true;

        int cacheSizesTested=0;

        //1 = LRU, 2 = FIFO, 3=Random
        int cacheType;
        //Add attack types to test
        ArrayList<Integer> attackers = new ArrayList<Integer>();


        if(graphType==2){
            graphSize=6301;
            percentCustodians = .05;
            numContentItems=2000;
            fixSquareGraph=false;
        }


        //Make sure to always start with 0 attackers
        attackers.add(0);
        if(graphType==1){
            attackers.add(1);
            attackers.add(2);
            //attackers.add(3);
            attackers.add(4);
        }
        if(graphType==2) {
            //1% Attackers
            //attackers.add((int) (graphSize * .01));
            //2% Attackers
            //attackers.add((int)(graphSize*.02));
            //5% Attackers
            attackers.add((int)(graphSize*.05));
        }


        //Loop for number of cache types (1,2,3)
        for(int c = 1; c < 3; c++) {
            cacheType = c;
            ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
            //Loop for number of unique cache sizes (0,10,20,30,40,50)
            for (int y = 0; y < 6; y++) {
                //Number of cache sizes tested. Used for stats computations
                if(c==1){
                    cacheSizesTested++;
                }
                //Testing results showed around 120% was best for attack
                int unpopPerCache = (int)((y*10)*1.2);
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
                                g = new Graph(graphType, graphSize, y * 10, zipfianAlpha, cacheType, attackers.get(a), unpopPerCache, percentCustodians, numContentItems, requestsPerTest, useCharacteristicTimeAttack, fixSquareGraph);
                                g.firstRun = true;
                                g.createGraph();
                            } else {
                                g.firstRun = false;
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
                            tests.add(Search.runTest(g, requestsPerTest, poissonRate, AttackerRequestRate, usingCache, keepCacheHitsOnly));



                        }//end for loop number of tests per attack


                    }//end for loop types of attacks


            }//end for cachesize tests
            allTests.add(tests);
        }//end for cachetype tests


        if(useCharacteristicTimeAttack){
            System.out.println("Attack metrics used use characteristic time.");
        }else {
            System.out.println("Attack metrics used DO NOT USE characteristic time. These attacks request unpopular for every attacker request.");
        }
        System.out.println("Request Rate used = "+AttackerRequestRate);
        LineChart demo = new LineChart("Average hops per request. Alpha:"+zipfianAlpha +" Nodes:25 Requests per test:"+requestsPerTest+" Number of tests:"+testsize,"Average hops per request",cacheSizesTested, allTests, attackers);
       // demo.pack();
        //demo.setVisible(true);




    }
}
