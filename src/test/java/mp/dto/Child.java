package mp.dto;

public class Child {
    public int key;
    public double value;

    public Child() {

    }

    public Child(int key, double value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Dto [key=" + key + ", value=" + value + "]";
    }
}
