import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by Jeff on 3/28/2015.
 */
public class LineGraph {
    int length;
    int cacheSize;
    ArrayList<Node> nodes = new ArrayList<Node>();
    Hashtable<Content,Node> localContentCustodians = new Hashtable<Content, Node>();
    int numContentItems;
    int numRequestsPerTest;
    double alpha;
    int cacheType;
    int numAttackers;
    int totalNodes;
    int numUnpopularItemsPerAttacker;
    ArrayList<Node> custodians = new ArrayList<Node>();
    ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();



    public LineGraph(int length, int cacheSize, double alpha, int cacheType, int numAttackers, int numUnpopularItems, int numContentItems, int numRequests) {
        this.length = length;

        this.cacheSize = cacheSize;
        this.totalNodes = length+numAttackers;
        this.numContentItems = numContentItems;
        this.alpha = alpha;
        this.cacheType = cacheType;
        this.numAttackers = numAttackers;
        this.numUnpopularItemsPerAttacker = numUnpopularItems;
        this.numRequestsPerTest = numRequests;

    }



    public void createLineGraph(){

        ArrayList<Integer> attackIndexes = new ArrayList<Integer>();
        int attackerindex = -1;

        //Assign random requester as attacker
        if(numAttackers >0) {
            for(int a = 0; a < numAttackers; a++){
                attackerindex = length+a;
                attackIndexes.add(attackerindex);
            }//end for

        }//end if



        for(int i = 0; i < totalNodes; i++)
        {
            if(attackIndexes.contains(i)) {
                AttackerNode att = new AttackerNode(i, cacheSize, cacheType, numUnpopularItemsPerAttacker,numRequestsPerTest);
                attackers.add(att);
                nodes.add(i, att);
            }else {
                Node a = new Node(i, cacheSize, cacheType);
                nodes.add(i, a);
            }//end else



        }//end for


        //Setup the rest of the graph
        //Create all edges and weights
        //setEdges();

        //Make 20% of the nodes content custodians and assign content to each one
        //createContentCustodians();

        //Pass a hashtable of all content and their respective custodian to all nodes
        //All nodes must know where content lives in order to route correctly
       // distributeContentCustodians();

        //Use Distribution to assign popularity to each piece of content in the graph
        //assignPopularityDistribution(alpha);


    }
}
