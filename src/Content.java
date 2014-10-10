import java.util.UUID;

/**
 * Created by n00430588 on 9/24/2014.
 */
public class Content {
    //right now each node can only hold 1 piece of content
    UUID contentID;
    String content;

    public Content()
    {


    }

    public Content getContent(UUID contentID)
    {
        return this;
    }

    public void addContent(UUID contentID, String stuff)
    {
        this.contentID = contentID;
        this.content = stuff;
    }


    public String showContent(int ContentID)
    {
        //Show the content stored on the node
        return content;
    }
}
