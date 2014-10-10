import java.util.List;

/**
 * Created by n00430588 on 10/7/2014.
 */
public class Packet {
    Integer packetID;
    Node src;
    Node dest;
    Node referrer;
    Content search;
    Content data;
    Integer hops;
    Boolean found;
    List<Node> route;

    public Packet(Node s, Content k){
        this.src = s;
        this.search = k;
        this.hops = 0;
        this.found = false;

    }

}
