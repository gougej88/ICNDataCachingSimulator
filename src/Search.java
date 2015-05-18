import java.util.*;
import java.text.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static PacketTracer runTest(Graph g, int numTests, double poissonRate, int AttackerRequestRate, Boolean cacheEnabled){

        //Get all nodes that are not content custodians, thus requesters
        ArrayList<Node> requesters = new ArrayList<Node>();
        ArrayList<Node> custodians = new ArrayList<Node>();
        ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();

        if(g.graphType==2) {
            System.out.println("Starting Dijkstra's algorithm. This could take some time on large graphs");
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

        //Create a new log file of a test
        /*
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        Date d = new Date();
        String file_name = "C:\\temp\\manet\\"+dateFormat.format(d)+".txt";
        //File file = new File("C:\\temp\\manet\\");
        //file.mkdirs();
        //Writer writer = null;
        */
        PacketTracer test = new PacketTracer(cacheEnabled);
        //try {
             //writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name), "utf-8"));

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
        int p = 0;
        double jump = 0;
        double maxtime = 0;
        int cacheSize = g.cacheSize;
        int cacheType = g.cacheType;
        int startKeepingStats = (int)(numTests*.70);
        int numTestsKept = numTests - startKeepingStats;
        Node n = requesters.get(0);
        //Node last = requesters.get(0);
        int numUnpopularKept = 0;
        int numPopularKept = 0;
        int numUnpopularTotal = 0;

        if(g.graphType==2) {
            System.out.println("Starting test. Running " + numTests + " with a large graph could take some time.");
        }
        for(int x=0; x<numTests; x++) {


            //Get the number of requests to create per time step
            jump = maxtime;

            //Mean is set here for rate at which to request content
            p= Poisson.getPoisson(poissonRate);

            Content k = g.getZipfContent();

            n = g.nodes.get(getNodeByProb(requesters,totalReqPerRound).nodeID);

            /*
            if(attackers.contains(last) && ((AttackerNode)last).readyToAttack && ((AttackerNode)last).attackStatus == 1 && x >= startKeepingStats)
            {
               n = last;
                numUnpopularKept++;

            }else {
                n = g.nodes.get(requesters.get(rand.nextInt(requesters.size())).nodeID);
                last = n;
            }

            */
            Packet pack = new Packet(n,k);

            //fix to ignore the nodes that cannot route to dest
            while(pack.route.size() ==1)
            {
                //requesters.remove(n);
                //g.possibleRequesters.remove(n);
                //totalReqPerRound--;
                n = g.nodes.get(getNodeByProb(requesters,totalReqPerRound).nodeID);
                k = g.getZipfContent();
                pack = new Packet(n,k);
            }
            if(x >= startKeepingStats && attackers.contains(n))
                numUnpopularKept++;

            test.addToTest(jump+p,k,n);
            pack.cacheEnabled = cacheEnabled;
            pack.time = maxtime;
            //Perform the search
            Packet r = findContent(pack,attackers);
            if(x >= startKeepingStats && !attackers.contains(n)) {
                if (r.cachehit)
                    cachehits++;

                totalHops += r.hops;
            }
                maxtime += p;
            //Write each query out to text file
            //writer.write("Test:"+x+" | Time:"+maxtime+" | Source:"+r.src.nodeID+" | Content:"+r.search.contentID+" | Destination:"+r.dest.nodeID+" | Data found on:"+r.referrer.nodeID+" | Number of hops:"+r.hops+" | Cache hit?:"+r.cachehit+"\r\n");
        }//end for
        //Testing for number of attacks run
        if(g.attackers.size() >=1)
        {
            for(int a=0; a < attackers.size(); a++){
                int unpop = attackers.get(a).numattacks;
                numUnpopularTotal+=unpop;
            }

        }//end if

        numPopularKept = numTestsKept-numUnpopularKept;
        System.out.println("Number of unpopular requests total: "+numUnpopularTotal);
        System.out.println("Number of unpopular requests kept: "+numUnpopularKept);
        System.out.println("Number of regular requests kept: "+numPopularKept);
        //System.out.println("Maxtime: " + maxtime);
        percent = (double)cachehits/(double)numTests *100;
        System.out.println("Number of requests: "+ numTests);
        //System.out.println("Number of hops in test: "+ totalHops);
        System.out.println("Cache Size: "+ cacheSize);
        //System.out.println("Number of cache hits in test: "+ cachehits);
        System.out.println("Percentage of cache hits: "+ percent+"%");

        //Need to show just the regular reguests average
        averagehops = (double)totalHops/(double)numPopularKept;
        System.out.println("Average hops per request: "+ averagehops);
        //Set totals in packetTracer
        test.setTotals(cacheType,cacheSize,numTests,numAttackers,AttackerRequestRate,numTestsKept,numPopularKept,numUnpopularKept,totalHops,cachehits,averagehops);

        //Write output to log file
        //writer.write("Number of requests:"+numTests+" | Total number of hops:"+totalHops+" | Number of cache hits:"+cachehits+" | Percentage cache hits:"+percent+"%"+ " | Average hops per request"+ averagehops );
       // } catch (IOException ex) {
       //     System.out.println(ex);
       // } finally {
            //try {writer.close();} catch (Exception ex) {System.out.println(ex);}
       // }
    return test;
    }//end runTest

    public static Packet findContent(Packet p, ArrayList<AttackerNode> attackers){

        //System.out.println("Route" + p.route);
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
                   p = attackers.get(index).sendData(p);

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
        }

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
        int i=rand.nextInt(allRequesters.size());
        while(sum < index ) {
            sum = sum + allRequesters.get(i).requestProbability;
        }

        return allRequesters.get(Math.max(0,i));
    }//end getNodeByProb

}//end search class
