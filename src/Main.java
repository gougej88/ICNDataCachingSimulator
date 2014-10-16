import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {
        // write your code here

        //Create graph grid of nodes
        Graph g = new Graph(4,4);
        g.createGraph();

        //Broadcast all content custodians to all nodes in graph
        g.distributeContentCustodians();

        //Test node for contentCustodians
        /*
        Set set = c.contentCustodians.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }


        //Test Dijkstra
        Dijkstra.ComputePaths(a);
        for (Node v : g.nodes)
        {
            System.out.println("Distance to " + v.nodeID + ": " + v.minDistance);
            List<Node> path = Dijkstra.getShortestPath(v);
            System.out.println("Path: " + path);
        }
        */

        //Search for random content starting on random node
        System.out.println("Starting test routing...");
        System.out.println();

        Packet ret =  Search.findContent(g.nodes.get(0),g.nodes.get(3).getContent(0));
        System.out.println("Data found:" + ret.data.toString() + "on Node:" + ret.referrer.nodeID);
        System.out.println("Number of hops: " + ret.hops.toString());
        System.out.println();

        //Run again to test cache
        Packet ret2 =  Search.findContent(g.nodes.get(0),g.nodes.get(3).getContent(0));
        System.out.println("Data found:" + ret2.data.toString() + "on Node:" + ret2.referrer.nodeID);
        System.out.println("Number of hops: " + ret2.hops.toString());
        System.out.println();

        Packet ret3 =  Search.findContent(g.nodes.get(0),g.nodes.get(3).getContent(1));
        System.out.println("Data found:" + ret3.data.toString() + "on Node:" + ret3.referrer.nodeID);
        System.out.println("Number of hops: " + ret3.hops.toString());
        System.out.println();

        Packet ret4 =  Search.findContent(g.nodes.get(0),g.nodes.get(15).getContent(0));
        System.out.println("Data found:" + ret4.data.toString() + "on Node:" + ret4.referrer.nodeID);
        System.out.println("Number of hops: " + ret4.hops.toString());
        System.out.println();

    }
}
