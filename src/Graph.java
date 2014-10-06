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

        Hashtable<Integer,Content> local = new Hashtable<Integer, Content>();
        for (int i = 0; i < size; i++)
        {
            local.put(i, nodes.get(i).getContent());
        }
        for (int i = 0; i < size; i++) {
            nodes.get(i).contentCustodians = local;
        }

    }




}
