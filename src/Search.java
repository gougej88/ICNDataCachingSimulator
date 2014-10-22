import java.util.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static void runTest(Graph g, int numTests){


        //Create searches for content on Poisson Distribution
        int p = 0;
        for(int x=0; x<numTests; x++) {
           p= Poisson.getPoisson(1);
            System.out.println("Poisson: "+ p);
        }


            //Get all nodes that are not content custodians
        ArrayList<Node> requesters = new ArrayList<Node>();
        for(int j=0; j<g.size; j++)
        {

            if(!g.localContentCustodians.contains(g.nodes.get(j)))
            {
                requesters.add(g.nodes.get(j));

            }
        }
        //Get a random requester (aka not a custodian)
        double temp = g.size * .2;
        int numCustodians = (int)temp;
        Random rand = new Random(g.size-numCustodians);
        //System.out.println("Get random requester: "+ requesters.get(rand.nextInt(requesters.size())));

        int cachehits = 0;
        double percent = 0;
        for(int x=0; x<numTests; x++) {

           Boolean r = findContent(g, g.nodes.get(requesters.get(rand.nextInt(requesters.size())).nodeID), g.getZipfContent());
            if(r)
                cachehits++;
        }
        percent = (double)cachehits/(double)numTests *100;
        System.out.println("Number of cache hits in test: "+ cachehits);
        System.out.println("Percentage of cache hits: "+ percent+"%");

    }

    public static Boolean findContent(Graph g, Node n, Content k){

        System.out.println("Starting node:" + n.nodeID);
        System.out.println("Search for:" + k.contentID);
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        System.out.println("Dest node for content: " + p.dest.nodeID);
        Dijkstra.ComputePaths(g, p.src);
        p.route = Dijkstra.getShortestPath(p.dest);
        System.out.println("Route" + p.route);
        int i = 0;
        while(p.found == false)
        {

            if(p.dest != p.src && i < p.route.size()) {
                p.next = p.route.get(i + 1);
                p = p.route.get(i).sendData(p);
            }else{
                //This should only occur if a content custodian makes a request
                System.out.println("Content is on the requesting node");
                break;
            }
                i++;
        }
        if(p.found) {

            System.out.println("Data found:" + p.data.toString() + " on Node:" + p.referrer.nodeID);
            System.out.println("Number of hops: " + p.hops.toString());
            System.out.println();
        }
        return p.cachehit;
    }

}
