
import java.util.Hashtable;

/**
 * Created by n00430588 on 9/24/2014.
 */
public class Node {
    int nodeID;
    int batteryLifeRemaining;
    Content content = new Content();
    Hashtable cache = new Hashtable(10);
    Hashtable contentCustodians;

    public Node(int NodeID)
    {
        this.nodeID = NodeID;
        this.batteryLifeRemaining = 100;
    }

    public int getNodeID()
    {
        return nodeID;
    }

    public void receiveData(Content in, int route)
    {
        //Store content in cache then send along path if not on this node
        //Check in cache, if so sendData
        if(searchCache(in.contentID))
        {
            sendData(in, route);
            powerDrain(1);
        }else{
            addToCache(in);
            sendData(in, route);
            powerDrain(1);
        }


    }

    public void sendData(Content out, int route)
    {
        //Send content to next route

    }

    public boolean searchCache(int contentID)
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
        content.addContent(nodeID, stuff);
    }

    public void getContent(){
        String stuff =  content.showContent(nodeID);
        System.out.println(stuff);
    }
}
