import java.util.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static void runTest(Graph g, int numTests){
        //findContent(g.nodes.get(0),g.nodes.get(3).contentCustodians.keys().nextElement());
        //Run again to test cache
        //findContent(g.nodes.get(0),g.nodes.get(3).getContent(0));
        double temp = g.size * .2;
        int numCustodians = (int)temp;
        int requesters = g.size - numCustodians;
        System.out.println("Num of content custodians: "+numCustodians);
        System.out.println("Num of requesters: "+requesters);
        Random rand = new Random();
        for(int x=0; x<numTests; x++) {
            findContent(g.nodes.get(19), g.getZipfContent());
        }
        //Create searches for content on Poisson Distribution

        //Use Zipfian to see which piece of content to search for
    }

    public static void findContent(Node n, Content k){
        System.out.println("Starting node:" + n.nodeID);
        System.out.println("Search for:" + k.contentID);
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        System.out.println("Dest node for content: " + p.dest.nodeID);
        Dijkstra.ComputePaths(p.src);
        p.route = Dijkstra.getShortestPath(p.dest);
        System.out.println("Route" + p.route);
        int i = 0;
        while(p.found == false)
        {
            if(p.dest != p.src && i < p.route.size()) {
                p.next = p.route.get(i + 1);
                p = p.route.get(i).sendData(p);
            }else{
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
    }

}
