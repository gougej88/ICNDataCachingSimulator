import java.util.*;
import java.util.concurrent.Exchanger;


public class Main {

    public static void main(String[] args) throws java.lang.Exception {

        //Create an arraylist of all tests, and results
        ArrayList<ArrayList<PacketTracer>> allTests = new ArrayList<ArrayList<PacketTracer>>();
        //Create graph grid of nodes(length, width, cacheSize)
        Graph g = null;
        Boolean usingCache = true;
        int cacheSizesTested=0;

        //Run a test(graph, number of requests to perform, cache enabled, number of attackers)
        //To change the number of tests change the integer for testsize
        //Graph type. 1 = square, 2= Gnutella6301, 3=Gnutella8846
        int graphType = 1;
        int testsize = 10;
        int requestsPerTest = 1000;
        Boolean compareSmartAttack = false;
        double zipfianAlpha = .65;
        double percentCustodians = .20;
        //Make this number divide into the number of custodians equally
        int numContentItems = 250;
        int AttackerRequestRate = 2;
        //Tested with square graphs of size = 25, and 100
        int graphSize = 25;
        //Tested with increments of 5 and 10 cache sizes
        int cacheSizeIncrement = 5;

        //When this is set make sure to only test with 0,1,and 2 attackers on the square graph only.
        //This should only be used for a quick test on graphSize=25
        Boolean fixSquareGraph = false;

        //Variable to set weather to keep stats on only cache hit packets or all of the last 30% of every test
        Boolean keepCacheHitsOnly = true;

        //1 = LRU, 2 = FIFO, 3=Random
        int cacheType;
        int cacheTypeStart = 1;
        int cacheTypeEnd = 4;
        int cacheSizeStart = 1;
        int cacheSizeEnd = 6;
        //Add attack types to test
        ArrayList<Integer> attackers = new ArrayList<Integer>();
        double percentAttackers = .16;

        //Optional variables for running large graphs one at a time
        if(args.length > 0) {
            if (args.length > 8) {
                try {
                    graphType = Integer.parseInt(args[0]);
                    compareSmartAttack = Boolean.parseBoolean(args[1]);
                    zipfianAlpha = Double.parseDouble(args[2]);
                    AttackerRequestRate = Integer.parseInt(args[3]);
                    percentAttackers = Double.parseDouble(args[4]);
                    cacheTypeStart = Integer.parseInt(args[5]);
                    cacheTypeEnd = Integer.parseInt(args[6]);
                    cacheSizeStart = Integer.parseInt(args[7]);
                    cacheSizeEnd = Integer.parseInt(args[8]);
                } catch (Exception e) {
                    System.out.println(e.toString());
                    System.out.println("Input argument was incorrect. Please fix.");
                    System.exit(1);
                }//end catch
            }else{
                System.out.println("Please enter the correct number of input arguments or leave it empty for defaults.");
                System.exit(1);
            }//end else
        }//end if

        if(graphType==2){
            graphSize=6301;
            percentCustodians = .05;
            numContentItems=2000;
            fixSquareGraph=false;
        }
        if(graphType==3){
            graphSize=8846;
            percentCustodians = .05;
            numContentItems=2000;
            fixSquareGraph=false;
        }


        //Make sure to always start with 0 attackers
        attackers.add(0);
        if(graphType==1){
            if(graphSize==100) {
                attackers.add(2);
                attackers.add(4);
                attackers.add(8);
                attackers.add(16);
            }
            if(graphSize==25){
                attackers.add(1);
                attackers.add(2);
                attackers.add(4);
                attackers.add(8);
            }
        }
        if(graphType==2 || graphType==3) {
            //X% Attackers
            attackers.add((int)(graphSize*percentAttackers));
        }

        System.out.println("Starting simulation. Variables - GraphType:"+graphType+" CompareSmartAttack:"+compareSmartAttack+" ZipfianAlpha:"+ zipfianAlpha+" AttackerRequestRate:"+AttackerRequestRate+" PercentAttackers:"+percentAttackers);


        Boolean useCharacteristicTimeAttack = false;
        int s = 1;
        //Loop on smart attack
        if(compareSmartAttack){
            s = 2;
        }//end if
        //Loop for number of cache types (1,2,3)
        for(int c = cacheTypeStart; c < cacheTypeEnd; c++) {
            cacheType = c;
            ArrayList<PacketTracer> tests = new ArrayList<PacketTracer>();
            //Loop for number of unique cache sizes (0,10,20,30,40,50)
            for (int y = cacheSizeStart; y < cacheSizeEnd; y++) {
                //Number of cache sizes tested. Used for stats computations
                if(c==cacheTypeStart){
                    cacheSizesTested++;
                }
                //Testing results showed around 120% was best for attack
                int unpopPerCache = (int)((y*cacheSizeIncrement)*1.2);
                if(unpopPerCache==0)
                    unpopPerCache=cacheSizeIncrement;

                    //Start test
                    //Loop on types of attacks
                    for(int a = 0; a < attackers.size();a++) {

                        //Change graph to add attackers
                        if(a>0){
                            g.addAttackersToExistingGraph(attackers.get(a));

                        }//end if


                        //Loop on smart attack on and off
                        for(int l = 0; l < s; l++) {

                            if(l==1) {
                                g.useCharacteristicTimeAttack = true;
                            }//end if

                            //Loop number of tests per attack
                            for (int n = 0; n < testsize; n++) {

                                //SQUARE GRAPH
                                //Create square graph(x,y,cacheSize,alpha, cacheType, numAttackers, numUnpopularItems, numContentItems)
                                if (a == 0 && l == 0 && n == 0) {
                                    g = new Graph(graphType, graphSize, y * cacheSizeIncrement, zipfianAlpha, cacheType, attackers.get(a), unpopPerCache, percentCustodians, numContentItems, requestsPerTest, useCharacteristicTimeAttack, fixSquareGraph);
                                    g.firstRun = true;
                                    g.createGraph();
                                } else {
                                    g.firstRun = false;
                                    g.resetGraphStats();
                                }
                                //Run without cache
                                //System.out.println("Test Number: " + n);
                                double requestRateString = AttackerRequestRate;
                                if (attackers.get(a) == 0)
                                    requestRateString = 0;
                                //System.out.println("Number of attackers-rate: " + attackers.get(a) + "-" + requestRateString);
                                if (y == 0) {
                                    usingCache = false;
                                } else {
                                    usingCache = true;
                                }
                                tests.add(Search.runTest(g, requestsPerTest, AttackerRequestRate, usingCache, keepCacheHitsOnly));


                            }//end for loop number of tests per attack

                            //reset smart attack variable
                            g.useCharacteristicTimeAttack = false;
                        }//end for loop for smart attack on and off

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
        LineChart demo = new LineChart("Average hops per request. Alpha:"+zipfianAlpha +" Nodes:25 Requests per test:"+requestsPerTest+" Number of tests:"+testsize,"Average hops per request",cacheSizesTested, cacheSizeIncrement, compareSmartAttack, allTests, attackers);
       // demo.pack();
        //demo.setVisible(true);

        System.out.println("");
        System.out.println("");


    }
}
