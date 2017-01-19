package edu.kpi.nesteruk.pzcs.model.primitives;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface Link<N> extends HasWeight, Serializable {
    String getFirst();
    String getSecond();
    boolean isIncident(HasId node);

    default String getId(boolean directed) {
        List<String> idsList = new ArrayList<>();
        idsList.add(getFirst());
        idsList.add(getSecond());
        if(!directed) {
            Collections.sort(idsList);
        }
        return idsList.stream().collect(Collectors.joining("=-="));
    }
}
