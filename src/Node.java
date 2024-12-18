package src;

import java.util.*;

class Node {
    String name;
    List<Node> children;

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void printNodeRecursive(String indent, boolean last, boolean first) {
        System.out.println(indent + (first ? "" : (last ? "└── " : "├── ")) + name);
        indent += first ? "" : (last ? "    " : "│   ");

        for (int i = 0; i < children.size(); i++) {
            children.get(i).printNodeRecursive(indent, i == children.size() - 1, false);
        }
    }
}