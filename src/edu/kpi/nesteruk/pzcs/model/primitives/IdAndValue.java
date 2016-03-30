package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Anatolii on 2016-03-13.
 */
public class IdAndValue {
    public final String id;
    public final String value;

    public IdAndValue(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "IdAndValue{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAndValue that = (IdAndValue) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
