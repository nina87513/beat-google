public class Keyword {
    public String name;
    public int weight;

    public Keyword(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "[" + name + "," + weight + "]";
    }
}
