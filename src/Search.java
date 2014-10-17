/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static void runTest(Graph g, int numTests){
        findContent(g.nodes.get(0),g.nodes.get(3).getContent(0));
        //Run again to test cache
        findContent(g.nodes.get(0),g.nodes.get(3).getContent(0));
        findContent(g.nodes.get(0),g.getZipfContent());

        //Create searches for content on Poisson Distribution

        //Use Zipfian to see which piece of content to search for
    }

    public static void findContent(Node n, Content k){
        System.out.println("Starting node:" + n.nodeID);
        System.out.println("Search for:" + k.contentID);
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        Dijkstra.ComputePaths(p.src);
        p.route = Dijkstra.getShortestPath(p.dest);
        System.out.println("Route" + p.route);
        int i = 0;
        while(p.found == false)
        {
            if(i < p.route.size()) {
                p.next = p.route.get(i + 1);
            }
            p = p.route.get(i).sendData(p);
            i++;
        }
        System.out.println("Data found:" + p.data.toString() + " on Node:" + p.referrer.nodeID);
        System.out.println("Number of hops: " + p.hops.toString());
        System.out.println();
    }

}
