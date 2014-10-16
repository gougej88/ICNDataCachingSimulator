/**
 * Created by Jeff on 10/15/2014.
 */
public class Search {

    public static Packet findContent(Node n, Content k){
        System.out.println("Starting node:" + n.nodeID);
        System.out.println("Search for:" + k.contentID);
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        Dijkstra.ComputePaths(p.src);
        p.route = Dijkstra.getShortestPath(p.dest);
        System.out.println("route size"+p.route.size());
        System.out.println(p.route);
        int i = 0;
        while(p.found == false)
        {
            if(i < p.route.size()) {
                p.next = p.route.get(i + 1);
            }
            p = p.route.get(i).sendData(p);
            i++;
        }

        return p;
    }

}
