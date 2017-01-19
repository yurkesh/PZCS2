package edu.kpi.nesteruk.pzcs.graph.mutating;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.building.GraphAssembler;
import edu.kpi.nesteruk.pzcs.model.common.LinkBuilder;
import edu.kpi.nesteruk.pzcs.model.common.NodeBuilder;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GetVerticesWithoutIncomingEdges;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 2017-01-09.
 */
public class SplitBranchMutatorTest {

    TasksGraphModel model;

    @BeforeMethod
    public void setUp() throws Exception {
        model = new TasksGraphModel();
        NodeBuilder nodeBuilder = model.getNodeBuilder();

        List<IdAndValue> ids = IntStream.rangeClosed(1, 5).boxed().map(index -> {
            nodeBuilder.beginBuild();
            nodeBuilder.setId(String.valueOf(index));
            nodeBuilder.setWeight(index);
            return nodeBuilder.finishBuild();
        }).map(Optional::get).collect(Collectors.toList());

        List<Pair<String, String>> connections = Lists.newArrayList(
//                Pair.create(1, 3),
//                Pair.create(3, 4),
//                Pair.create(4, 5),
//                Pair.create(1, 4),
//                Pair.create(3, 5),
                Pair.create(1, 5),
                Pair.create(1, 2),
                Pair.create(3, 2)
        ).stream()
                .map(pairOfInts -> Pair.create(String.valueOf(pairOfInts.first), String.valueOf(pairOfInts.second)))
                .collect(Collectors.toList());

        LinkBuilder linkBuilder = model.getLinkBuilder();
        connections.forEach(connection -> {
            linkBuilder.beginConnect(connection.first, connection.second);
            linkBuilder.setWeight(1);
            linkBuilder.finishConnect();
        });
    }

    @Test
    public void testMutate() throws Exception {
        TasksGraphBundle bundle = model.getBundle();
        System.out.println("BEFORE: " + bundle);
        Map<String, Task> nodesMap = bundle.getNodesMap();
        Map<String, DirectedLink<Task>> linksMap = bundle.getLinksMap();

        Collection<Task> firstTopologicalLevel = GetVerticesWithoutIncomingEdges.getFirstTopologicalLevel(
                nodesMap, linksMap
        );

        List<String> firstLevelIds = firstTopologicalLevel.stream()
                .map(Task::getId).collect(Collectors.toList());

        System.out.println("FIRST LEVEL: " + firstLevelIds);


        Map<String, List<String>> targetToSources = linksMap.values().stream().collect(Collectors.toMap(
                DirectedLink::getSecond,
                link -> {
                    List<String> toVertexes = new ArrayList<>();
                    toVertexes.add(link.getFirst());
                    return toVertexes;
                },
                (strings, strings2) -> {
                    LinkedHashSet<String> strings1 = new LinkedHashSet<>(strings);
                    strings1.addAll(strings2);
                    return new ArrayList<>(strings1);
                }
        ));
        System.out.println("T2Ss: " + targetToSources);

        Map<String, Pair<String, String>> targetsWithTwoTopSources = targetToSources.entrySet().stream()
                .filter(targetWithSourcesEntry -> targetWithSourcesEntry.getValue().size() == 2)
                .filter(targetWithTwoSourcesEntry -> firstLevelIds.containsAll(targetWithTwoSourcesEntry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<String> sources = entry.getValue();
                            return Pair.create(sources.get(0), sources.get(1));
                        }
                ));
        System.out.println("Tw2TS: " + targetsWithTwoTopSources);

        GraphAssembler<Task, DirectedLink<Task>, TasksGraphBundle> graphAssembler = model.getGraphAssembler();

        LinkedHashMap<String, Task> updatedNodes = new LinkedHashMap<>(nodesMap);
        LinkedHashMap<String, DirectedLink<Task>> updatedLinks = new LinkedHashMap<>(linksMap);

        targetsWithTwoTopSources.entrySet().forEach(targetWithTwoTopSourcesEntry -> {
            String key = targetWithTwoTopSourcesEntry.getKey();
            Task originalTask = nodesMap.get(key);
            Task copiedTask = new Task(
                    originalTask.id + "'",
                    originalTask.weight
            );
            updatedNodes.put(
                    copiedTask.id,
                    copiedTask
            );

            // забрати лінк від другого сорса до оригінаоу, зробити лінк від другого сорса до копії
            String secondSourceKey = targetWithTwoTopSourcesEntry.getValue().second;
            Task secondSource = nodesMap.get(secondSourceKey);

            String linkToRemove = bundle.getLinkBetweenNodes(secondSource.getId(), originalTask.getId());
            updatedLinks.remove(linkToRemove);

            Pair<DirectedLink<Task>, String> copiedLink = graphAssembler.linkFactory.createLink(
                    secondSource, copiedTask, linksMap.get(linkToRemove).getWeight());
            updatedLinks.put(copiedLink.second, copiedLink.first);
        });

        System.out.println("AFTER: " + new TasksGraphBundle(updatedNodes, updatedLinks));
    }

}