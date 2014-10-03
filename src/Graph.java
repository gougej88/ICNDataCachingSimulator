
/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int length;
    int width;
    Node nodes;

    public Graph(int length, int width) {
        this.length = length;
        this.width = width;

    }

    public void createGraph(){
        int size = length*width;
        for(int i = 0; i < size; i++)
        {
            Node a = new Node(i);
        }

    }


}
