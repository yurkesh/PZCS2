package edu.kpi.nesteruk.pzcs.model.common.primitive;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CongenericLink<N extends Node> extends SimpleLink<N> implements Link<N> {

    public CongenericLink(N src, N dest, int weight) {
        super(src, dest, weight);
    }

    public CongenericLink(N src, N dest) {
        super(src, dest);
    }

    @Override
    public String toString() {
        return "(" + src + ", " + dest + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CongenericLink that = (CongenericLink) o;

        if(src.equals(that.src)) {
            return dest.equals(that.dest);
        } else if(src.equals(that.dest)) {
            return dest.equals(that.src);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return src.hashCode() + dest.hashCode();
    }
}
