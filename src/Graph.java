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

        //Setup the rest of the graph
        setEdges();
        createContent();
        assignZipf();
        distributeContentCustodians();
        System.out.println("Test Zipf content " + getZipfContent().contentID);
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

    public void assignZipf(){
        int totalContent = 0;
        for(int i=0; i<size;i++)
        {
           int numContentOnNode =  nodes.get(i).getContentCount();
           totalContent += numContentOnNode;
        }
        //System.out.println("Total content items in graph: " + totalContent);

        Zipf zip = new Zipf(totalContent,1);
        ArrayList<Double> ranks = new ArrayList<Double>();
        for(int j=0; j<totalContent; j++)
        {
            ranks.add(zip.getProbability(j));
        }
        //Shuffle the ranks
        Collections.shuffle(ranks);
        for(int i=0; i<size;i++) {
            nodes.get(i).content.get(0).probability = ranks.get(i);
            nodes.get(i).content.get(1).probability = ranks.get(i*2+1);
        }

        //Test Zipf probability randomization
        System.out.println("Prob of node 2 content 1 "+ nodes.get(2).content.get(1).probability);
        System.out.println("Prob of node 6 content 0 "+ nodes.get(6).content.get(0).probability);
    }

    public Content getZipfContent()
    {
        double totalSum = 0.0;
        double x = Math.random();
        Content r = new Content();
        ArrayList<Content> allContent = new ArrayList<Content>();
        for(int i=0; i<size;i++) {
            allContent.add(i * 2, nodes.get(i).content.get(0));
            allContent.add(i * 2 + 1, nodes.get(i).content.get(1));
        }
        for(Content k : allContent )
        {
            totalSum += k.probability;
            if(x <= totalSum)
            {
                r = k;
                break;
            }
        }
       return r;
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









}
