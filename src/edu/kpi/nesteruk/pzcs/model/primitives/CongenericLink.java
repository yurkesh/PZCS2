package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CongenericLink<N extends HasId> extends SimpleLink<N> {

    private static final long serialVersionUID = -2494365671091159475L;

    public CongenericLink(N src, N dest, int weight) {
        super(src, dest, weight);
    }

    public CongenericLink(N src, N dest) {
        super(src, dest);
    }

    public String getSource() {
        return getFirst();
    }

    public String getDestination() {
        return getSecond();
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
