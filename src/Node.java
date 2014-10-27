
import java.util.*;

/**
 * Created by n00430588 on 9/24/2014.
 */
public class Node {
    int nodeID;
    int batteryLifeRemaining;
    double minDistance = Double.POSITIVE_INFINITY;
    Hashtable<Node,Node> previous = new Hashtable<Node, Node>();
    //Node previous;
    ArrayList<Edge> edges = new ArrayList<Edge>();
    ArrayList<Content> content = new ArrayList<Content>();
    //index, ContentID
    LinkedHashMap<UUID,Content> cache = new LinkedHashMap<UUID,Content>(10) {public boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 10;
    }};
    //nodeID stored on, contentID for each
    Hashtable<Content,Node> contentCustodians = new Hashtable<Content, Node>();
    //nodeID to go to for next hop, and contentID
    //WILL I NEED?
    //Hashtable<Integer,Content> nextHop = new Hashtable<Integer, Content>();

    public Node(int NodeID)
    {
        this.nodeID = NodeID;
        this.batteryLifeRemaining = 100;
    }


    public int getNodeID()
    {
        return nodeID;
    }

    public void setEdge(Node n, Integer weight) {
        edges.add(new Edge(n, weight));

    }


    public ArrayList<Node> getAllEdges() {
        int size = edges.size();
        ArrayList<Node> ret = new ArrayList<Node>();
        for (int i = 0; i < size; i++)
        {
            ret.add(i, edges.get(i).target);
            System.out.println("Node ID: "+ ret.get(i).getNodeID());
        }
        return ret;
    }
    public Packet receiveData(Packet p)
    {
        //If this node is the dest
        if(p.dest.nodeID == this.nodeID)
        {
            p.hops++;
            p.referrer = this;
            UUID s = p.search.contentID;
            int f = content.indexOf(p.search);
            p.data = content.get(f);
            p.found=true;
            powerDrain(1);
            return p;
        }
        //Store content in cache then send along path if not on this node
        //Check in cache, if so sendData
        if(searchCache(p.search.contentID) && p.found==false)
        {
            //content found in cache send back to src
            p.found=true;
            p.hops++;
            p.referrer = this;
            p.dest = p.src;
            p.data = cache.get(p.search.contentID);
            powerDrain(1);
            System.out.println("Content Found in Cache!!!");
            p.cachehit = true;
            return p;
        }else{
            //Not found in cache, add to cache and forward to next hop
            //DISABLE/ENABLE CACHE HERE
            addToCache(p.search);
            p.hops++;

            powerDrain(1);
            return p;
        }


    }

    public Packet sendData(Packet p)
    {
        //Send content to next route
        p = p.next.receiveData(p);
        return p;
    }

    public boolean searchCache(UUID contentID)
        {
        if(cache.containsKey(contentID))
        {
            return true;
        }else{
            return false;
        }
    }

    public void addToCache(Content x)
    {
        //Add content to cache when new content is received
        this.cache.put(x.contentID,x);
    }

    public int powerDrain(int drain)
    {
        //Drain an element of the power
        batteryLifeRemaining = batteryLifeRemaining-drain;
        return batteryLifeRemaining;
    }

    public void saveContent(String stuff)
    {
        //Make this node a content custodian. Should this be done as each node saves only 1 content item?
        UUID contentID = UUID.randomUUID();
        Content t = new Content();
        t.addContent(contentID, stuff);
        content.add(t);



    }

    public Content getContent(int index){
        //String stuff =  content.showContent(nodeID);
        //System.out.println(stuff);
        return content.get(index);
    }

    public int getContentCount(){
        return content.size();
    }


}
