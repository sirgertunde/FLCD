package src;

import java.util.*;

class Node {
    String name;
    List<Node> children;
    static String output = "";

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public String printNodeRecursive(String indent, boolean last, boolean first) {
        output += indent + (first ? "" : (last ? "└── " : "├── ")) + name + "\n";
        indent += first ? "" : (last ? "    " : "│   ");

        for (int i = 0; i < children.size(); i++) {
            children.get(i).printNodeRecursive(indent, i == children.size() - 1, false);
        }

        return output;
    }
}