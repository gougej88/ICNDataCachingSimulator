import java.util.*;
import java.text.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class SearchLineGraph {

    public static PacketTracer runTest(LineGraph g, int numTests, double poissonRate, Boolean cacheEnabled){

        //Get all nodes that are not content custodians, thus requesters
        ArrayList<Node> requesters = new ArrayList<Node>();
        ArrayList<Node> custodians = new ArrayList<Node>();
        ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();
        for(int j=0; j<g.totalNodes; j++)
        {
            //COMPUTE ALL PATHS FROM EACH SRC ONCE
            Dijkstra.ComputePathsLineGraph(g, g.nodes.get(j));

            //Check if the node is a custodian. if not: add to requesters
            if(!g.localContentCustodians.contains(g.nodes.get(j)))
            {
                requesters.add(g.nodes.get(j));
            }else {
                custodians.add(g.nodes.get(j));
            }
        }

        attackers = g.attackers;
        for(AttackerNode att : attackers)
        {
            att.SetCustodians(custodians);
        }

        PacketTracer test = new PacketTracer(cacheEnabled);


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
        Node at = requesters.get(0);
        Node last = requesters.get(0);
        int numUnpopularKept = 0;
        int numPopularKept = 0;
        int numUnpopularTotal = 0;
        int poissonIndex = 0;
        int nodeType = 0;

        if(attackers.size() >0)
            at = attackers.get(0);

        for(int x=0; x<numTests; x++) {


            //Get the number of requests to create per time step
            jump = maxtime;

            //Mean is set here for rate at which to request content
            if(poissonIndex > 0){
                poissonIndex--;
            }else {
                p = Poisson.getPoisson(poissonRate);
                poissonIndex = p;
                nodeType = x%2;

            }
                Content k = g.getZipfContent();
                int unpopIndex = 0;

                if (attackers.size() > 0) {
                    k = attackers.get(0).unpopularContent.get(0);
                    unpopIndex = custodians.get(0).content.indexOf(k);
                }

                if (nodeType == 1 && attackers.size() > 0 && x >= numTests * .2) {
                    n = at;
                    if (((AttackerNode) n).readyToAttack && x >= startKeepingStats)
                        numUnpopularKept++;

                } else {
                    n = requesters.get(0);
                    if (attackers.size() > 0) {
                        if (unpopIndex == 1)
                            k = custodians.get(0).getContent(0);
                        else
                            k = custodians.get(0).getContent(1);
                    }
                }


                test.addToTest(jump + p, k, n);
                Packet pack = new Packet(n, k);
                pack.cacheEnabled = cacheEnabled;
                pack.time = maxtime;
                //Perform the search
                Packet r = findContent(pack, attackers);
                if (x >= startKeepingStats) {
                    if (r.cachehit)
                        cachehits++;

                    totalHops += r.hops;
                }
                maxtime += p;
                //Write each query out to text file
                //writer.write("Test:"+x+" | Time:"+maxtime+" | Source:"+r.src.nodeID+" | Content:"+r.search.contentID+" | Destination:"+r.dest.nodeID+" | Data found on:"+r.referrer.nodeID+" | Number of hops:"+r.hops+" | Cache hit?:"+r.cachehit+"\r\n");
        }//end for num tests
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
        averagehops = (double)totalHops/(double)numTestsKept;
        System.out.println("Average hops per request: "+ averagehops);
        //Set totals in packetTracer
        test.setTotals(cacheType,cacheSize,numTests,numTestsKept,numPopularKept,numUnpopularKept,totalHops,cachehits,averagehops);

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
}
