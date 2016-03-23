package edu.kpi.nesteruk.pzcs.graph.io;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import static edu.kpi.nesteruk.util.StringUtils.isEmpty;

/**
 * Created by Yurii on 2016-03-21.
 * @deprecated Not supported
 */
@Deprecated
public class GraphDeserializer<N extends Node, L extends Link<N>> implements IOConst {

    private final Predicate<String> typeValidator;
    private final Function<String, N> nodeDeserializer;
    private final Function<String, L> linkDeserializer;

    public GraphDeserializer(Predicate<String> typeValidator, Function<String, N> nodeDeserializer, Function<String, L> linkDeserializer) {
        this.typeValidator = typeValidator;
        this.nodeDeserializer = nodeDeserializer;
        this.linkDeserializer = linkDeserializer;
    }

    public Pair<Collection<N>, Collection<L>> deserializeGraph(String serialized) {
        if(isEmpty(serialized)) {
            throw new IllegalArgumentException("Serialized data is empty");
        }
        String[] rows = serialized.split("\n");
        if(CollectionUtils.isEmpty(rows)) {
            throw new IllegalArgumentException("Serialized data has invalid format. Data = '" + serialized + "'");
        }

        try {
            checkMetadata(rows[0], rows[1]);
            String nodesInfoPrefix = NODES_INFO + DIVIDER;
            if(!rows[2].startsWith(nodesInfoPrefix)) {
                throw new IllegalArgumentException("Incorrect nodes info. Nodes info = '" + rows[2] + "'");
            }

            final int nodesStart = 2;
            int nodesCount = Integer.valueOf(rows[nodesStart].replace(nodesInfoPrefix, ""));
            ArrayList<N> nodes = new ArrayList<>(nodesCount);
            for (int nodeIndex = nodesStart; nodeIndex < nodesStart + nodesCount; nodeIndex++) {
                nodes.add(nodeDeserializer.apply(rows[nodeIndex]));
            }

            final int linksStart = nodesStart + nodesCount;
            int linksCount = Integer.valueOf(rows[linksStart]);
            ArrayList<L> links = new ArrayList<>(linksCount);
            for (int linkIndex = linksStart; linkIndex < linksStart + linksCount; linkIndex++) {
                links.add(linkDeserializer.apply(rows[linkIndex]));
            }

            return Pair.create(nodes, links);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Serialized data is not full. Data = '" + serialized + "'", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize data = '" + serialized + "'", e);
        }
    }

    private void checkMetadata(String typeInfo, String versionInfo) {
        checkTypeInfo(typeInfo);
        checkVersion(versionInfo);
    }

    private void checkVersion(String version) {
        //Do nothing
    }

    private void checkTypeInfo(String typeInfo) {
        if(isEmpty(typeInfo)) {
            throw new IllegalArgumentException("Type info is empty");
        }

        String[] typeInfoParts = typeInfo.split(DIVIDER);
        if(typeInfoParts.length != 2) {
            throw new IllegalArgumentException("Incorrect type info = '" + typeInfo + "'");
        }
        if(isEmpty(typeInfoParts[0]) || isEmpty(typeInfoParts[1])) {
            throw new IllegalArgumentException("Incorrect type info = '" + typeInfo + "'");
        }
        if(!typeValidator.test(typeInfoParts[1])) {
            throw new IllegalArgumentException("Wrong type in type info = '" + typeInfo + "'");
        }
    }

}
