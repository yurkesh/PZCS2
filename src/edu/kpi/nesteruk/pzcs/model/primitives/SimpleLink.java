package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Anatolii on 2016-03-13.
 */
public class SimpleLink<N extends Node> implements Link<N> {

    private static final long serialVersionUID = -4080347279345838836L;

    public final N src;
    public final N dest;
    public final int weight;

    public SimpleLink(N src, N dest, int weight) {
        if(src == null) {
            throw new NullPointerException();
        }
        if(dest == null) {
            throw new NullPointerException();
        }
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    public SimpleLink(N src, N dest) {
        this(src, dest, -1);
    }

    @Override
    public N getFirst() {
        return src;
    }

    @Override
    public N getSecond() {
        return dest;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "SimpleLink{" +
                "src=" + src +
                ", dest=" + dest +
                ", weight=" + weight +
                '}';
    }
}
