import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class implements basic graph functionality, for
 * simple undirected graphs that have both a text label and a
 * value (weight) associated with each vertex in the graph.
 * <p>
 * This class provides a mapping between vertex labels (Strings)
 * and Vertex objects, which represent vertices
 * in the graph.  Each Vertex object stores a list of the
 * other vertices that it is adjacent to, so we are
 * essentially encoding the graph in an "adjacency list"
 * format, with the convenience of being able to index
 * these adjacency lists by the vertex's String label.
 */
public class LabeledValueGraph {
    private LinkedHashMap<String, Vertex> labelToVertexMap;

    /**
     * Creates a new empty graph.
     */
    public LabeledValueGraph() {
        labelToVertexMap = new LinkedHashMap<String, Vertex>();
    }

    /**
     * @param filename - the file name to load the graph from
     * @throws FileNotFoundException
     */
    public LabeledValueGraph(String filename) throws FileNotFoundException {
        loadFromFile(new Scanner(new File(filename)));
    }

    /**
     * Copy constructor (makes a deep copy of the graph)
     *
     * @param toCopy
     */
    public LabeledValueGraph(LabeledValueGraph toCopy) {
        // to copy the graph, we convert it into string format
        // and reload it. (Not the most efficient, but it's convenient, and guaranteed
        // to avoid any shallow-copy issues where vertices in the new graph
        // are still pointing to vertices in the old graph.)
        loadFromFile(new Scanner(toCopy.toString()));
    }

    private void loadFromFile(Scanner fileScanner) {
        labelToVertexMap = new LinkedHashMap<String, Vertex>();

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine().trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            String items[] = line.split("\\s+");
            if (items.length == 1) { // declaring a vertex
                addVertexIfNeededFromLabelColonValue(items[0]);
            } else if (items.length == 2) { // declaring an edge
                Vertex v = addVertexIfNeededFromLabelColonValue(items[0]);
                Vertex w = addVertexIfNeededFromLabelColonValue(items[1]);
                addEdge(v, w);
            }
        }
        fileScanner.close();
    }

    private Vertex addVertexIfNeededFromLabelColonValue(String vertexColonValue) {
        if (vertexColonValue.contains(":")) {
            String[] vertexAndValue = vertexColonValue.split(":");
            String vertexLabel = vertexAndValue[0];
            int vertexValue = Integer.parseInt(vertexAndValue[1]);
            Vertex v = labelToVertexMap.computeIfAbsent(vertexLabel, vLabel -> new Vertex(vLabel, vertexValue));
            if (v.value != vertexValue) {
                throw new IllegalArgumentException("Error reading file, found vertex \""+vertexColonValue+"\", but it's value is different from the value already set for this vertex!");
            }
            return v;
        } else {
            throw new IllegalArgumentException("Error reading file, trying to parse vertex \""+vertexColonValue+"\", but it's not in \"vertexName:value\" format!");
        }

    }

    /**
     * Adds a new isolated vertex to the graph with the specified label
     * and value.
     */
    public void addVertex(String label, int value) {
        if (labelToVertexMap.containsKey(label)) {
            throw new IllegalArgumentException("Vertex with label " + label + " already exists in the graph.");
        }
        Vertex v = new Vertex(label, value);
        labelToVertexMap.put(label, v);
    }

    public int getValueAt(String label) {
        return labelToVertexMap.get(label).value;
    }

    /**
     * Removes the vertex with the specified label from the graph
     * @param label
     */
    public void removeVertex(String label) {
        if (!labelToVertexMap.containsKey(label)) {
            throw new IllegalArgumentException("Vertex with label " + label + " does not exist in the graph.");
        }
        Vertex v = labelToVertexMap.get(label);
        for (Vertex neighbor : v.neighbors) {
            neighbor.neighbors.remove(v);
        }
        labelToVertexMap.remove(label);
    }

    /**
     * Adds an undirected edge to the graph between vertices
     * labeled v1 and v2, by updating the
     * adjacency lists for both the given vertices.
     */
    public void addEdge(String v1, String v2) {
        if (!labelToVertexMap.containsKey(v1) || !labelToVertexMap.containsKey(v2)) {
            throw new IllegalArgumentException("One or both of the vertices with labels " + v1 + " and " + v2 + " do not exist in the graph.");
        }
        addEdge(labelToVertexMap.get(v1), labelToVertexMap.get(v2));
    }
    private void addEdge(Vertex v1, Vertex v2) {
        if (!hasEdge(v1, v2)) {
            v1.neighbors.add(v2);
            v2.neighbors.add(v1);
        } else {
            throw new IllegalArgumentException("Edge between " + v1.label + " and " + v2.label + " already exists in the graph.");
        }
    }

    /** Remove one undirected edge from the graph.
     * (Note this is a O(degree(v1) + degree(v2)) operation)
     * @param v1
     * @param v2
     */
    public void removeEdge(String v1, String v2) {
        if (!labelToVertexMap.containsKey(v1) || !labelToVertexMap.containsKey(v2)) {
            throw new IllegalArgumentException("One or both of the vertices with labels " + v1 + " and " + v2 + " do not exist in the graph.");
        }
        removeEdge(labelToVertexMap.get(v1), labelToVertexMap.get(v2));
    }
    private void removeEdge(Vertex v1, Vertex v2) {
        v1.neighbors.remove(v2);
        v2.neighbors.remove(v1);
    }

    /**
     * Checks whether there is an edge between vertices labeled v1 and v2 in this graph.
     * (Note this is an O(degree(v1)) operation)
     */
    public boolean hasEdge(String v1, String v2) {
        if (!labelToVertexMap.containsKey(v1) || !labelToVertexMap.containsKey(v2)) {
            throw new IllegalArgumentException("One or both of the vertices with labels " + v1 + " and " + v2 + " do not exist in the graph.");
        }
        return hasEdge(labelToVertexMap.get(v1), labelToVertexMap.get(v2));
    }
    private boolean hasEdge(Vertex v1, Vertex v2) {
        return v1.neighbors.contains(v2);
    }

    /**
     * @return a list of the labels for ALL vertices in the graph
     */
    public List<String> getAllVertexLabels() {
        return new ArrayList<>(labelToVertexMap.keySet());
    }

    /**
     *
     * @param v - vertex name
     * @return a list of the labels of the neighbors of the vertex labeled v
     */
    public List<String> adj(String v) {
        if (!labelToVertexMap.containsKey(v)) {
            throw new IllegalArgumentException("Vertex with label " + v + " does not exist in the graph.");
        }
        List<String> neighborLabels = new LinkedList<String>();
        for (Vertex neighbor : labelToVertexMap.get(v).neighbors) {
            neighborLabels.add(neighbor.label);
        }
        return neighborLabels;
    }
    /**
     * @param v - vertex name
     * @return a list of the labels of adjacent/neighboring vertices of the vertex labeled v
     * NOTE: same as adj(v) but with a more expressive method name.  */
    public List<String> getAdjacentVertices(String v) {
        return adj(v);
    }

    /**
     * @return a string representation of the graph,
     * used for saving graphs to files, and also
     * useful for debugging/visualizing by copying into
     * https://csacademy.com/app/graph_editor/
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("//To visualize and edit small graphs, you copy/paste the following lines to/from https://csacademy.com/app/graph_editor/\n");
        for (String vLabel : labelToVertexMap.keySet()) {
            Vertex v = labelToVertexMap.get(vLabel);
            String vDebugLabel = v.label + ":" + v.value;
            sb.append(vDebugLabel);
            sb.append("\n");
        }
        for (String vLabel : labelToVertexMap.keySet()) {
            Vertex v = labelToVertexMap.get(vLabel);
            String vDebugLabel = v.label + ":" + v.value;
            for (Vertex w : v.neighbors) {
                if (v.label.compareTo(w.label) < 0) {
                    continue; // only print each edge once, not v->w and w->v
                }
                String wDebugLabel = w.label + ":" + w.value;
                sb.append(vDebugLabel);
                sb.append(" ");
                sb.append(wDebugLabel);
                sb.append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public void saveToFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(filename);
        writer.println(toString());
        writer.close();
    }

    /**
     * A simple class to represent each vertex in a LabeledValueGraph.
     */
    private class Vertex {
        private final String label;
        private int value;
        private final List<Vertex> neighbors;

        public Vertex(String label, int value) {
            this.label = label;
            this.value = value;
            neighbors = new LinkedList<Vertex>();
        }
    }


}
