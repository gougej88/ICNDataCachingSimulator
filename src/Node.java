
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
    Map<UUID,Content> cache;
    //nodeID stored on, contentID for each
    Hashtable<Content,Node> contentCustodians = new Hashtable<Content, Node>();
    //1 = LRU, 2 = FIFO, 3=Random
    int cacheType;
    int cacheSize;


    public Node(int NodeID, final int cacheSize, int cacheType)
    {
        this.nodeID = NodeID;
        this.batteryLifeRemaining = 100;
        this.cacheType = cacheType;
        this.cacheSize = cacheSize;
        //FIFO and LRU cache
        if(cacheType == 1 || cacheType == 2) {
            cache = new LinkedHashMap<UUID, Content>(cacheSize) {
                public boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > cacheSize;
                }
            };
        }

        //Random cache
        if(cacheType == 3) {
        cache = new HashMap<UUID, Content>(cacheSize);
        }

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
        if(p.cacheEnabled && (searchCache(p.search.contentID) && !p.found))
        {
            //content found in cache send back to src
            p.found=true;
            //LRU cache move to front of hashmap since used
            if(cacheType==1) {
                cache.remove(p.search.contentID);
                addToCache(p.search);
            }
            p.hops++;
            p.referrer = this;
            p.data = cache.get(p.search.contentID);
            powerDrain(1);
            //System.out.println("Content Found in Cache!!!");
            p.cachehit = true;
            return p;
        }else{
            //Not found in cache, add to cache and forward to next hop
            //DISABLE/ENABLE CACHE HERE
            if(p.cacheEnabled)
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
        if(cacheType==1 || cacheType==2)
            cache.put(x.contentID,x);
        if(cacheType==3)
            //if random cache has room. just add it
            if(cache.size() < cacheSize) {

                cache.put(x.contentID, x);
            }else{
                Random rnd = new Random(System.currentTimeMillis());
                Object[] values = cache.values().toArray();
                Object randomValue = values[rnd.nextInt(cacheSize)];
                Content c = (Content)randomValue;
                cache.remove(c.contentID);
                cache.put(x.contentID,x);
            }
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
