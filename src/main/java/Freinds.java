import java.util.ArrayList;

public class Freinds {
    private ArrayList<String> likeItems = new ArrayList<>();
    private ArrayList<String> hateItems = new ArrayList<>();

    public ArrayList<String> getLikeItems() {
        return likeItems;
    }

    public void setLikeItems(ArrayList<String> likeItems) {
        this.likeItems = likeItems;
    }

    public ArrayList<String> getHateItems() {
        return hateItems;
    }

    public void setHateItems(ArrayList<String> hateItems) {
        this.hateItems = hateItems;
    }
}
