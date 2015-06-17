import java.util.*;
import java.text.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static PacketTracer runTest(Graph g, int numTests, int AttackerRequestRate, Boolean cacheEnabled, Boolean keepCacheHitsOnly){

        //Get all nodes that are not content custodians, thus requesters
        ArrayList<Node> requesters = new ArrayList<Node>();
        ArrayList<Node> custodians = new ArrayList<Node>();
        ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();
        int countRegularRequests = 0;

        if(g.graphType==2 || g.graphType==3) {
            //System.out.println("Starting Dijkstra's algorithm. This could take some time on large graphs");
        }
        for(int j=0; j<g.size; j++)
        {
            //COMPUTE ALL PATHS FROM EACH SRC ONCE
            if(g.dijkstraComputed == false) {
                Dijkstra.ComputePaths(g, g.nodes.get(j));

            }
            //Check if the node is a custodian. if not: add to requesters
            if(g.possibleRequesters.contains(g.nodes.get(j)) && !g.localContentCustodians.contains(g.nodes.get(j)))
            {
                requesters.add(g.nodes.get(j));
            }
            if(g.localContentCustodians.contains(g.nodes.get(j))){
                custodians.add(g.nodes.get(j));
            }
        }//end for

        g.dijkstraComputed=true;

        attackers = g.attackers;

        for(AttackerNode att : attackers)
        {
            att.SetCustodians(custodians);
        }

        PacketTracer test = new PacketTracer(cacheEnabled);

        //Set probability distribution for node requests based on the request rate
        int numRequesters = requesters.size();
        int numAttackers = attackers.size();
        int totalReqPerRound = (numRequesters-numAttackers)+(numAttackers*AttackerRequestRate);
        for(int n = 0; n < requesters.size(); n++)
        {
            if(attackers.contains(requesters.get(n)))
            {
                requesters.get(n).requestProbability = AttackerRequestRate;
            }
            else {
                requesters.get(n).requestProbability = 1;
            }//end else
        }//end if


        //Each numTests is a time step
        int cachehits = 0;
        int totalHops = 0;
        double percent = 0;
        double averagehops = 0;
        int p = 1;
        double jump = 0;
        double maxtime = 0;
        int cacheSize = g.cacheSize;
        int cacheType = g.cacheType;
        int startKeepingStats = (int)(numTests*.70);
        int numTestsKept = numTests - startKeepingStats;
        Node n = requesters.get(0);
        Content k = g.getZipfContent();
        Packet pack = null;
        //Node last = requesters.get(0);
        int numUnpopularKept = 0;
        int numPopularKept = 0;
        int numUnpopularTotal = 0;

        if(g.graphType==2 || g.graphType==3) {
            //System.out.println("Starting test. Running " + numTests + " with a large graph could take some time.");
        }
        for(int x=0; x<numTests; x++) {


            //Get the number of requests to create per time step
            jump = maxtime;

            //Mean is set here for rate at which to request content
            if(g.firstRun) {
                k = g.getZipfContent();
                n = g.nodes.get(getNodeByProb(requesters, totalReqPerRound).nodeID);
                pack = new Packet(n, k);
            }else{
                k = g.pattern.get(x).search;
                n = g.nodes.get((g.pattern.get(x).src).nodeID);
                pack = new Packet(n, k);
            }

            //fix to ignore the nodes that cannot route to dest
            while(pack.route.size() ==1)
            {
                if(g.firstRun) {
                    k = g.getZipfContent();
                    n = g.nodes.get(getNodeByProb(requesters, totalReqPerRound).nodeID);
                    pack = new Packet(n, k);
                }else{
                    k = g.pattern.get(x).search;
                    n = g.nodes.get((g.pattern.get(x).src).nodeID);
                    pack = new Packet(n, k);
                }
            }

            test.addToTest(jump+p,k,n);
            pack.cacheEnabled = cacheEnabled;
            pack.time = maxtime;
            //Perform the search
            Packet r = findContent(pack);

            //Send attack requests but don't track
            if(attackers.contains(r.src)){
                for(int a=0; a<AttackerRequestRate; a++) {
                    Packet b = new Packet(r.src, r.search);
                    b.cacheEnabled = cacheEnabled;
                    b.time = maxtime;
                    b = sendAttackerRequest(b, attackers);
                }//end for
            }//end if
            if(x >= startKeepingStats) {
                if (r.cachehit)
                    cachehits++;

                //Add first run cache hits to tracked data
                if(g.firstRun && r.cachehit){
                    g.patternCacheHit.add(x);
                }//end if

                //check what results we are collecting
                if (keepCacheHitsOnly) {
                    //Track only cache hits from the first run
                    if (g.patternCacheHit.contains(x)) {
                        totalHops += r.hops;
                        countRegularRequests++;
                    }//end if
                } else {
                    totalHops += r.hops;
                    countRegularRequests++;
                }//end else
            }//end if
                maxtime += p;

            if(x >= startKeepingStats && attackers.contains(r.src))
                numUnpopularKept+=AttackerRequestRate;

            //Add to pattern if first run
            if(g.firstRun) {
                g.pattern.add(r);
            }
        }//end for

        //Testing for number of attacks run
        if(g.attackers.size() >=1)
        {
            for(int a=0; a < attackers.size(); a++){
                int unpop = attackers.get(a).numattacks;
                numUnpopularTotal+=unpop;
            }
        }//end if


        numPopularKept = numTestsKept;
        //System.out.println("Number of unpopular requests total: "+numUnpopularTotal);
        //System.out.println("Number of unpopular requests sent after 70% warm up: "+numUnpopularKept);
        //System.out.println("Number of regular requests kept: "+numPopularKept);
        percent = (double)cachehits/(double)numTests *100;
        //System.out.println("Number of requests: "+ numTests);
        //System.out.println("Cache Size: "+ cacheSize);
        //System.out.println("Percentage of cache hits: "+ percent+"%");

        //Need to show just the regular reguests average
        averagehops = (double)totalHops/(double)countRegularRequests;

        //Fix for 0 cache size
        if(countRegularRequests==0){
            averagehops=0;
        }//end if

        System.out.println("Cache type:"+cacheType+" cache size:"+cacheSize+" attackers:"+numAttackers+" request rate:"+AttackerRequestRate+" Smart attack:"+g.useCharacteristicTimeAttack +"  Average hops: " + averagehops);
        //Set totals in packetTracer
        test.setTotals(cacheType,cacheSize,numTests,numAttackers,AttackerRequestRate,numTestsKept,numPopularKept,numUnpopularKept,totalHops,cachehits,averagehops,g.useCharacteristicTimeAttack);

    return test;
    }//end runTest

    public static Packet findContent(Packet p){

        int i = 0;
        while(!p.found)
        {
            if(p.dest != p.src && i < p.route.size()) {
                if(i+1 > p.route.size())
                    System.out.print("Test");
                p.next = p.route.get(i + 1);

                //send data as usual
                p = p.route.get(i).sendData(p);

            }else{
                //This should only occur if a content custodian makes a request, which should never happen
                //System.out.println("Content is on the requesting node");
                break;
            }
                i++;
        }//end while

        return p;
    }//end findContent

    public static Packet sendAttackerRequest(Packet p, ArrayList<AttackerNode> attackers){

        int i = 0;
        while(!p.found)
        {
            if(p.dest != p.src && i < p.route.size()) {
                if(i+1 > p.route.size())
                    System.out.print("Test");
                p.next = p.route.get(i + 1);

                //Check if starting node is attack node
                if(attackers.contains(p.route.get(i)))
                {
                int index = attackers.indexOf(p.route.get(i));
                p = attackers.get(index).sendDataAttack(p);

                }else {
                //else send data as usual
                p = p.route.get(i).sendData(p);
                }
            }else{
                //This should only occur if a content custodian makes a request, which should never happen
                //System.out.println("Content is on the requesting node");
                break;
            }
            i++;
        }//end while

        //Check if attacker is guessing characteristic time
        //If so and there is a cache hit, then a packet was returned from a source other than the custodian
        if(attackers.contains(p.src) && ((AttackerNode)p.src).characteristicTimeStatus ==3 && p.cachehit) {
            (((AttackerNode) p.src).allPacketsFromCustodian) = false;

        }
        return p;
    }//end findContent

    public static Node getNodeByProb(ArrayList<Node> allRequesters, int totalSum)
    {
        //This method will grab a random piece of content based on its popularity
        Random rand = new Random();
        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i=0;
        while(sum < (index+1) ) {
            sum = sum + allRequesters.get(i++).requestProbability;
        }

        return allRequesters.get(Math.max(0,i-1));
    }//end getNodeByProb

}//end search class
