package comp1023.loadeddice;

public class Item {
    private String name;
    private String description;
    private String type;
    private int value;

    public Item(String name, String description, String type, int value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public void use() {
        // Logic for using the item
        System.out.println("Using item: " + name);
    }

    public void discard() {
        // Logic for discarding the item
        System.out.println("Discarding item: " + name);
    }
}
