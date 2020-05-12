package it.polimi.ingsw.Model;

public class Pair
{
    public int getFirst() {
        return num1;
    }
    public int getSecond() {
        return num2;
    }

    private final int num1,num2;

    public Pair(int num1, int num2)
    {
        this.num1 = num1;
        this.num2 = num2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return num1 == pair.num1 &&
                num2 == pair.num2;
    }

    /*@Override
    public int hashCode() {
        return Objects.hash(num1, num2);
    }*/
}
