package src;

import java.util.*;

public class Parser {
    private final Grammar grammar;
    private final Map<String, List<Set<String>>> tableFirst;

    public Grammar getGrammar() {
        return grammar;
    }

    public Parser(Grammar gr) {
        grammar = gr;
        tableFirst = new HashMap<>();
        generateTableFirst();
    }

    public void generateTableFirst() {
        for (String nonTerminal : grammar.getN()) {
            tableFirst.put(nonTerminal, new ArrayList<>());
            tableFirst.get(nonTerminal).add(new HashSet<>());
            List<List<String>> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
            for (List<String> production : productionsForNonTerminal) {
                String firstSymbolInProduction = production.getFirst();
                if (grammar.getSigma().contains(firstSymbolInProduction)) {
                    tableFirst.get(nonTerminal).getFirst().add(firstSymbolInProduction);
                }
            }
        }

        int i = 0;
        boolean continuing;
        do {
            i++;
            continuing = false;
            for (String nonTerminal : grammar.getN()) {
                tableFirst.get(nonTerminal).add(new HashSet<>());
                List<List<String>> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
                boolean isEmpty = false;
                for (List<String> production : productionsForNonTerminal) {
                    String firstSymbolFromProduction = production.getFirst();
                    if(grammar.getSigma().contains(firstSymbolFromProduction) && !tableFirst.get(nonTerminal).get(i).contains(firstSymbolFromProduction)){
                        tableFirst.get(nonTerminal).get(i).add(firstSymbolFromProduction);
                    }
                    else {
                        for (String symbol : production) {
                            if (grammar.getN().contains(symbol) && tableFirst.get(symbol).get(i - 1).isEmpty()) {
                                isEmpty = true;
                            }
                            if (isEmpty) {
                                break;
                            }
                        }
                        String firstSymbol = production.getFirst();
                        if (!isEmpty) {
                            for (String s : tableFirst.get(nonTerminal).get(i - 1)) {
                                tableFirst.get(nonTerminal).get(i).add(s);
                            }
                            if (grammar.getSigma().contains(firstSymbol)) {
                                tableFirst.get(nonTerminal).get(i).add(firstSymbol);
                            }
                            if (grammar.getN().contains(firstSymbol)) {
                                for (String s : tableFirst.get(firstSymbol).get(i - 1)) {
                                    tableFirst.get(nonTerminal).get(i).add(s);
                                }
                            }
                        }
                    }
                }
            }

            for (String nonTerminal : grammar.getN()) {
                if (!tableFirst.get(nonTerminal).get(i).equals(tableFirst.get(nonTerminal).get(i - 1))) {
                    continuing = true;
                }
            }
        } while (continuing);
    }

    public Map<String, Set<String>> getFirst(){
        Map<String, Set<String>> first = new HashMap<>();
        for(String key: tableFirst.keySet()){
            first.put(key, tableFirst.get(key).getLast());
        }
        return first;
    }

    public static void main(String[] args){
        Grammar grammar = new Grammar("g2.txt");
        Parser parser = new Parser(grammar);
        System.out.println(parser.getFirst());
    }
}