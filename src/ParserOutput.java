package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ParserOutput
{
    private final Grammar grammar;
    private final Queue<Integer> outputBand;
    private Node root;
    private String filePath;

    public ParserOutput(Grammar grammar, Queue<Integer> outputBand, String filePath)
    {
        this.grammar = grammar;
        this.outputBand = outputBand;
        this.filePath = filePath;
    }

    public void generateTree()
    {
        Map<Integer, String> productionMap = new HashMap<>();
        //Map<String, List<String>> prods = grammar.getProductions();

        // Generate productions map for index -> prod access
        for (Map.Entry<String, List<String>> entry : grammar.getProductions().entrySet())
        {
            List<String> productions = entry.getValue();
            for (int i = 0; i < productions.size(); i++)
            {
                productionMap.put(grammar.getListOfProductions().indexOf(new MyPair<>(entry.getKey(), productions.get(i))), productions.get(i));
            }
        }

        // Root is first symbol
        root = new Node(grammar.getStartingSymbol());
        Stack<Node> workStack = new Stack<>();
        workStack.push(root);

        while (!outputBand.isEmpty())
        {
            int productionIndex = outputBand.poll();
            String production = productionMap.get(productionIndex);

            if (production == null) continue;

            String[] symbols = production.split(" ");
            Collections.reverse(Arrays.asList(symbols));
            // We reverse because we want to match the order
            // of the output band, as we generate the tree,
            // we want the keep the last node to be parsed
            // for the output band's next index

            Node currentNode = workStack.pop();

            for (String symbol : symbols)
            {
                Node child = new Node(symbol);
                currentNode.addChild(child);
                if (grammar.getNonTerminals().contains(symbol))
                {
                    workStack.push(child);
                }
            }
        }
    }

    public void printTree()
    {
        if (root != null)
        {
            System.out.println("Tree representation:\n");
            String output = root.printNodeRecursive("", true, true);
            System.out.println(output);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
            {
                writer.write(output);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        } else
        {
            System.out.println("Tree is empty. Generate the tree first.");
            /*
            葉がすべて落ち
            木はそのまま、
            でも夏が来たら、
            葉はまた芽吹くか
             */
        }
    }
}