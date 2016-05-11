package edu.kpi.nesteruk.pzcs.planning.transfering;

import edu.kpi.nesteruk.pzcs.model.primitives.HasId;

/**
 * Created by Yurii on 2016-04-21.
 */
public interface Connections {

    Connection getConnection(String from, String to);
    default Connection getConnection(HasId from, HasId to) {
        return getConnection(from.getId(), to.getId());
    }


}
