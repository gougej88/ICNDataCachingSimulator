import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

/**
 * Created by n00430588 on 10/7/2014.
 */
public class Packet {
    Integer packetID;
    Node src;
    Node dest;
    Node referrer;
    Node next;
    Content search;
    Content data;
    Integer hops;
    Boolean cacheEnabled;
    Boolean cachehit;
    Boolean found;
    List<Node> route;

    public Packet(Node s, Content k){
        this.src = s;
        this.search = k;
        this.hops = 0;
        this.cachehit = false;
        this.found = false;
        this.dest = s.contentCustodians.get(k);
        this.route = Dijkstra.getShortestPath(src,dest);

    }

}
