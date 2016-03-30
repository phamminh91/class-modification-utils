package mp.dto;

public class Parent {
    int a;
    public Child dto;

    public Parent(int a, Child dto) {
        this.a = a;
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "Parent [a=" + a + ", dto=" + dto + "]";
    }

}
