package edu.kpi.nesteruk.pzcs.model.common;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CommonNodeBuilder implements NodeBuilder {

    private final boolean weighted;
    private final Supplier<String> nextIdSupplier;
    private final Function<String, Boolean> idAvailabilityChecker;
    private final NodeFactory nodeFactory;

    private String id;
    private int weight = -1;

    public CommonNodeBuilder(boolean weighted, Supplier<String> nextIdSupplier, Function<String, Boolean> idAvailabilityChecker, NodeFactory nodeFactory) {
        this.weighted = weighted;
        this.nextIdSupplier = nextIdSupplier;
        this.idAvailabilityChecker = idAvailabilityChecker;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public String beginBuild() {
        return nextIdSupplier.get();
    }

    @Override
    public boolean setId(String id) {
        if(idAvailabilityChecker.apply(id)) {
            this.id = id;
            return true;
        } else {
            this.id = null;
            return false;
        }
    }

    @Override
    public boolean needWeight() {
        return weighted;
    }

    @Override
    public void setWeight(int weight) {
        if(!weighted) {
            throw new UnsupportedOperationException();
        }
        this.weight = weight;
    }

    @Override
    public Optional<String> finishBuild() {
        return id == null ? Optional.empty() : Optional.ofNullable(nodeFactory.makeNode(id, weight));
    }

    public interface NodeFactory {
        String makeNode(String id, int weight);
    }
}
