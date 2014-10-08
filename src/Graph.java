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

    }

    public void distributeContentCustodians() {

        Hashtable<Content,Node> local = new Hashtable<Content, Node>();
        for (int i = 0; i < size; i++)
        {

            local.put(nodes.get(i).getContent(), nodes.get(i));
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
        return nodes.get(ran).getContent();
    }






}
