

public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {
        // write your code here

        //Create graph grid of nodes
        Graph g = new Graph(2,2);
        g.createGraph();
        Node a = g.nodes.get(0);
        Node b = g.nodes.get(1);
        Node c = g.nodes.get(2);
        Node d = g.nodes.get(3);

        //Manual add content to nodes
        a.saveContent("This is content from node A.");
        b.saveContent("This is content from node B.");
        c.saveContent("This is content from node C.");
        d.saveContent("This is content from node D.");

        //Setup Edges
        a.setEdge(b);
        b.setEdge(a);
        b.setEdge(c);
        c.setEdge(b);
        c.setEdge(d);
        d.setEdge(c);

        //Test to show edges
        a.getEdges();
        b.getEdges();


        //Test show the content
        a.getContent();
        b.getContent();
        c.getContent();


        //Start at outside node

        //Search grid for content custodian

        //Need to know where content found

        //Print where content is found
    }
}
