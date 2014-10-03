import java.util.ArrayList;

/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int length;
    int width;
    ArrayList<Node> nodes = new ArrayList<Node>();

    public Graph(int length, int width) {
        this.length = length;
        this.width = width;

    }

    public void createGraph(){
        int size = length*width;
        for(int i = 0; i < size; i++)
        {
            nodes.add(i,new Node(i));
        }

    }

    public int route(Node src, Node dest)
    {
        src.getEdges();
        int test = 5;
        return test;
    }


}
