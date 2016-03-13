package edu.kpi.nesteruk.pzcs.model.common;

import java.util.Optional;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CommonLinkBuilder implements LinkBuilder {

    private final boolean weighted;
    private final ConnectionChecker checker;
    private final LinkFactory linkFactory;

    private String srcId;
    private String destId;
    private int weight = -1;

    public CommonLinkBuilder(boolean weighted, ConnectionChecker checker, LinkFactory linkFactory) {
        this.weighted = weighted;
        this.checker = checker;
        this.linkFactory = linkFactory;
    }

    @Override
    public boolean beginConnect(String srcId, String destId) {
        this.srcId = srcId;
        this.destId = destId;
        return checker.canConnect(srcId, destId);
    }

    @Override
    public boolean needWeight() {
        return weighted;
    }

    @Override
    public void setWeight(Integer specifiedWeight) {
        if(!weighted) {
            throw new UnsupportedOperationException("Weight is not supported");
        }
        this.weight = specifiedWeight;
    }

    @Override
    public Optional<String> finishConnect() {
        if(weight < 0) {
            return Optional.empty();
        } else if(srcId == null || destId == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(linkFactory.makeLink(srcId, destId, weight));
        }
    }

    public interface ConnectionChecker {
        boolean canConnect(String srcId, String destId);
    }

    public interface LinkFactory {
        String makeLink(String srcId, String destId, int weight);
    }
}
