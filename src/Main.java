import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {
        // write your code here

        //Create graph grid of nodes
        Graph g = new Graph(4,4);
        g.createGraph();

        //Search for random content starting on random node
        System.out.println("Starting test routing...");
        System.out.println();

        Search.runTest(g,5);

    }
}
