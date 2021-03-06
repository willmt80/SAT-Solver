package DataStructures;

import java.util.*;
import java.util.stream.Collectors;

public class Graph<T> {

    private HashMap<Node<T>, HashSet<Edge<T>>> adjacencyList;
    private HashMap<Node<T>, HashSet<Edge<T>>> incidentList;

    Graph() {
        this.adjacencyList = new HashMap<>();
        this.incidentList = new HashMap<>();
    }

    public void addNode(Node<T> node) {
        if (adjacencyList.get(node) != null) {
            return;
        }

        adjacencyList.put(node, new HashSet<>());
        incidentList.put(node, new HashSet<>());
    }

    public void removeNode(Node<T> node) {
        if (adjacencyList.get(node) == null) {
            return;
        }

        HashSet<Edge<T>> edgesFromNode = new HashSet<>(getEdgesFromNode(node));
        for (Edge<T> edge: edgesFromNode) {
            removeEdge(edge);
        }

        HashSet<Edge<T>> edgesToNode = new HashSet<>(getEdgesToNode(node));
        for (Edge<T> edge: edgesToNode) {
            removeEdge(edge);
        }

        adjacencyList.remove(node);
        incidentList.remove(node);
    }

    public boolean containsNode(Node<T> node) {
        return adjacencyList.get(node) != null;
    }

    public void addEdge(Edge<T> edge) {
        if (!containsNode(edge.source)) {
            addNode(edge.source);
        }

        if (!containsNode(edge.destination)) {
            addNode(edge.destination);
        }

        HashSet<Edge<T>> outgoingEdges = adjacencyList.get(edge.source);
        outgoingEdges.add(edge);

        HashSet<Edge<T>> incomingEdges = incidentList.get(edge.destination);
        incomingEdges.add(edge);
    }

    public HashSet<Edge<T>> getEdgesToNode(Node<T> node) {
        HashSet<Edge<T>> edges = incidentList.get(node);
        edges = edges == null ? new HashSet<>() : edges;

        return edges;
    }

    public HashSet<Node<T>> getNodes() {
        return new HashSet<>(adjacencyList.keySet());
    }

    private void removeEdge(Edge<T> edge) {
        Node<T> source = edge.source;
        HashSet<Edge<T>> outgoingEdges = adjacencyList.get(source);
        if (outgoingEdges != null) {
            outgoingEdges.remove(edge);
        }

        Node<T> destination = edge.destination;
        HashSet<Edge<T>> incomingEdges = incidentList.get(destination);
        if (incomingEdges != null) {
            incomingEdges.remove(edge);
        }
    }

    private HashSet<Edge<T>> getEdgesFromNode(Node<T> node) {
        HashSet<Edge<T>> edges = adjacencyList.get(node);
        return edges == null ? new HashSet<>() : edges;
    }

    /**
     * Unused methods
     */

    public HashSet<Node<T>> getAdjacentNodes(Node<T> node) {
        HashSet<Edge<T>> edges = getEdgesFromNode(node);
        HashSet<Node<T>> adjacentNodes = new HashSet<>();

        edges.forEach(edge -> adjacentNodes.add(edge.destination));

        return adjacentNodes;
    }

    public HashSet<Node<T>> getIncidentNodes(Node<T> node) {
        HashSet<Edge<T>> edges = getEdgesToNode(node);
        HashSet<Node<T>> incidentNodes = new HashSet<>();

        edges.forEach(edge -> incidentNodes.add(edge.source));

        return incidentNodes;
    }

    public HashSet<Edge<T>> getEdges() {
        HashSet<Edge<T>> edges = new HashSet<>();
        adjacencyList.values().forEach(edges::addAll);

        return edges;
    }

    public boolean containsEdge(Edge<T> edge) {
        Node<T> source = edge.source;
        HashSet<Edge<T>> edges = adjacencyList.get(source);

        return edges != null && edges.contains(edge);
    }

    public HashSet<Edge<T>> getEdgesBetweenNodes(Node<T> firstNode, Node<T> secondNode) {
        HashSet<Edge<T>> edgesFromFirstNode = adjacencyList.get(firstNode);
        HashSet<Edge<T>> edgesFromSecondNode = adjacencyList.get(secondNode);
        HashSet<Edge<T>> edges = new HashSet<>();

        edges.addAll(edgesFromFirstNode.stream().filter(
                edge -> edge.destination.equals(secondNode)
        ).collect(Collectors.toList()));

        edges.addAll(edgesFromSecondNode.stream().filter(
                edge -> edge.destination.equals(firstNode)
        ).collect(Collectors.toList()));

        return edges;
    }

}
