import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {
        // write your code here

        //Create graph grid of nodes
        Graph g = new Graph(2,2);
        g.createGraph();

        Node a = g.nodes.get(0);
        Node b = g.nodes.get(1);
        Node c = g.nodes.get(2);
        Node d = g.nodes.get(3);

        //Manual add content to nodes
        a.saveContent("This is content from node A.");
        b.saveContent("This is content from node B.");
        c.saveContent("This is content from node C.");
        d.saveContent("This is content from node D.");

        //Setup Edges
        a.setEdge(b,1);
        b.setEdge(a,1);
        b.setEdge(c,1);
        c.setEdge(b,1);
        c.setEdge(d,1);
        d.setEdge(c,1);

        //test

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
        Packet ret =  Dijkstra.findContent(g.nodes.get(0),g.nodes.get(3).getContent());
        System.out.println("Data found:" + ret.data.toString() + "on Node:" + ret.referrer.nodeID);
        System.out.println("Number of hops: " + ret.hops.toString());

        //Run again to test cache
        Packet ret2 =  Dijkstra.findContent(g.nodes.get(0),g.nodes.get(3).getContent());
        System.out.println("Data found:" + ret2.data.toString() + "on Node:" + ret2.referrer.nodeID);
        System.out.println("Number of hops: " + ret2.hops.toString());


    }
}
