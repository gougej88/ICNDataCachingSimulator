import java.util.*;


/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int length;
    int width;
    ArrayList<Node> nodes = new ArrayList<Node>();
    Hashtable<Content,Node> localContentCustodians = new Hashtable<Content, Node>();
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
        createContentCustodians();
        distributeContentCustodians();
        assignPopularityDistribution();


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

    public void createContentCustodians(){
        ArrayList<Integer> n = new ArrayList<Integer>();
        double temp = size * .2;
        int numCustodians = (int)temp;
        Random rand = new Random();
        for(int i = 0; i<numCustodians; i++)
        {
            int contentCust = rand.nextInt(size);
            if(!n.contains(contentCust)) {
                n.add(i, contentCust);
                nodes.get(contentCust).saveContent("This is content from node "+ contentCust);
                nodes.get(contentCust).saveContent("This is a second piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a third piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a fourth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a fifth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a sixth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a seventh piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a eighth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a ninth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a tenth piece of content on node "+contentCust);

            }
        }
    }

    public void assignPopularityDistribution(){
        //int totalContent = 0;
        //for(int i=0; i<size;i++)
        //{
          // int numContentOnNode =  nodes.get(i).getContentCount();
          // totalContent += numContentOnNode;
       //}
        //System.out.println("Total content items in graph: " + totalContent);
        int totalContent = localContentCustodians.size();
        System.out.println("Number of content items: " + totalContent);
        Zipf zip = new Zipf(totalContent,1);
        ArrayList<Double> ranks = new ArrayList<Double>();
        double test = 0;
        for(int j=0; j<totalContent; j++)
        {
            ranks.add(zip.getProbability(j+1));
            //System.out.print(" Zip prob:"+zip.getProbability(j+1));
            //test += zip.getProbability(j+1);
        }
        //System.out.println("Total prob sum: "+ test);
        //Shuffle the ranks
        Collections.shuffle(ranks);

        Enumeration e = localContentCustodians.keys();
        int i = 0;
        while(e.hasMoreElements()){

            Content k = (Content) e.nextElement();
            k.probability = ranks.get(i);
            i++;
            //System.out.println(k);
        }

    }

    public Content getZipfContent()
    {
        double totalSum = 0.0;
        double x = Math.random();
        Content r = new Content();
        //ArrayList<Content> allContent = new ArrayList<Content>();
        //for(int i=0; i<size;i++) {
            //allContent.add(i * 2, nodes.get(i).content.get(0));
           // allContent.add(i * 2 + 1, nodes.get(i).content.get(1));
       // }
        ArrayList<Content> allContent = new ArrayList<Content>();
        Enumeration e = localContentCustodians.keys();
        //int i =0;
        while(e.hasMoreElements()){

            Content t = (Content) e.nextElement();
            allContent.add(t);
            //System.out.println("Adding to allcontent: "+ allContent.get(i).contentID);
            //i++;
        }

        System.out.println("Random x = " + x);
        for(Content k : allContent )
        {
            totalSum += k.probability;
            //System.out.print(" k prob : "+ k.probability + "content id " + k.contentID);
            if(x <= totalSum)
            {
                r = k;
                break;
            }
        }
       return r;
    }

    public void distributeContentCustodians() {


        for (int i = 0; i < size; i++)
        {
            int kPerNode = nodes.get(i).content.size();
            for(int k = 0; k < kPerNode; k++) {
                localContentCustodians.put(nodes.get(i).getContent(k), nodes.get(i));
            }
        }
        for (int i = 0; i < size; i++) {
            nodes.get(i).contentCustodians = localContentCustodians;
        }

    }









}
