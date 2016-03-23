package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Yurii on 2016-03-13.
 */
public class DirectedLink<N extends Node> extends SimpleLink<N> implements Link<N> {

    private static final long serialVersionUID = -7222533970888717436L;

    public DirectedLink(N src, N dest, int weight) {
        super(src, dest, weight);
    }

    public DirectedLink(N src, N dest) {
        super(src, dest);
    }

    @Override
    public String toString() {
        return weight >= 0 ? String.format("(%s -(%s)-> %s", src, weight, dest) : String.format("(%s -> %s", src, dest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectedLink<?> tuple = (DirectedLink<?>) o;

        if (src != null ? !src.equals(tuple.src) : tuple.src != null) return false;
        return dest != null ? dest.equals(tuple.dest) : tuple.dest == null;

    }

    @Override
    public int hashCode() {
        int result = src != null ? src.hashCode() : 0;
        result = 31 * result + (dest != null ? dest.hashCode() : 0);
        return result;
    }
}
