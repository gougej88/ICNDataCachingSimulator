import java.util.*;

/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int length;
    int width;
    ArrayList<Node> nodes = new ArrayList<Node>();
    int size;


    public Graph(int length, int width) {
        this.length = length;
        this.width = width;
        size = length*width;
    }

    public void createGraph(){

        for(int i = 0; i < size; i++)
        {
            Node a = new Node(i);
            nodes.add(i,a);


        }
        setEdges();
        createContent();
    }

    public void setEdges(){

        for(int i=0; i<size;i++)
        {

                if(i%width < width-1) {
                    //System.out.println("Set right edge:" + nodes.get(i).getNodeID());
                    nodes.get(i).setEdge(nodes.get(i+1), 1);
                } if(i < (size-width)) {
                    //System.out.println("Set bottom edge:" + nodes.get(i).getNodeID());
                    nodes.get(i).setEdge(nodes.get(i+width), 1);
                } if(i >= length)
                {
                    //System.out.println("Set top edge:" + nodes.get(i).getNodeID());
                    nodes.get(i).setEdge(nodes.get(i-length),1);
                } if(i%length > 0)
                {
                    //System.out.println("Set left edge:" + nodes.get(i).getNodeID());
                    nodes.get(i).setEdge(nodes.get(i-1),1);
                }
        }
    }

    public void createContent(){
        for(int i=0; i<size;i++)
        {
            nodes.get(i).saveContent("This is content from node "+ i);
            nodes.get(i).saveContent("This is a second piece of content on node "+i);
        }
    }

    public void distributeContentCustodians() {

        Hashtable<Content,Node> local = new Hashtable<Content, Node>();
        for (int i = 0; i < size; i++)
        {
            int kPerNode = nodes.get(i).content.size();
            for(int k = 0; k < kPerNode; k++) {
                local.put(nodes.get(i).getContent(k), nodes.get(i));
            }
        }
        for (int i = 0; i < size; i++) {
            nodes.get(i).contentCustodians = local;
        }

    }

    public Node getRandomNode(){
        int ran = new Random().nextInt(size);
        return nodes.get(ran);
    }

    public Content getRandomContent(){
        int ran = new Random().nextInt(size);
        return nodes.get(ran).getContent(0);
    }






}
