package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
    private final Set<String> nonTerminals; // NonTerminals
    private final Set<String> terminals; // Terminals
    private String startingSymbol; // Starting symbol
    private final Map<String, List<String>> productions; // Productions

    public Grammar(String fileName) {
        nonTerminals = new HashSet<>();
        productions = new HashMap<>();
        terminals = new HashSet<>();
        readFile(fileName);
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }

    public List<MyPair<String, String>> getListOfProductions(){
        Map<String, List<String>> productions = getProductions();
        List<MyPair<String, String>> listOfProductions = new ArrayList<>();
        for(String key: productions.keySet()){
            for(String value: productions.get(key)){
                listOfProductions.add(new MyPair<>(key, value));
            }
        }
        return listOfProductions;
    }

    public void readFile(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("N =")) {
                    String[] nonterminals = line.substring(3).trim().split("\\s+");
                    nonTerminals.addAll(Arrays.asList(nonterminals));
                } else if (line.startsWith("sigma =")) {
                    String[] terminals = line.substring(8).split("_");
                    this.terminals.addAll(Arrays.asList(terminals));
                } else if (line.startsWith("S =")) {
                    startingSymbol = line.substring(3).trim();
                } else if (line.startsWith("P =")) {
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("->");
                        if (parts.length == 2) {
                            String terminalPart = parts[0].trim();
                            String productionPart = parts[1].trim();
                            if(!productions.containsKey(terminalPart)){
                                productions.put(terminalPart, new ArrayList<>());
                                productions.get(terminalPart).add(productionPart);
                            }
                            else
                                productions.get(terminalPart).add(productionPart);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<List<String>> getProductionsForNonTerminal(String nonTerminal){
        List<List<String>> productions = new ArrayList<>();
        int i = 0;
        for(String production: this.productions.get(nonTerminal)){
            productions.add(new ArrayList<>());
            String[] symbols = production.split(" ");
            productions.get(i).addAll(Arrays.asList(symbols));
            i++;
        }
        return productions;
    }

    public boolean checkContextFreeGrammar(){
        for(String key: productions.keySet()){
            if(!nonTerminals.contains(key)){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        Grammar grammar = new Grammar("g1.txt");
        System.out.println("Set of non-terminals: " + grammar.getNonTerminals());
        System.out.println("Set of terminals: " + grammar.getTerminals());
        System.out.println("Starting symbol: " + grammar.getStartingSymbol());
        System.out.println("Productions: " + grammar.getProductions());
        System.out.println("List of productions: " + grammar.getListOfProductions());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give a non-terminal for which you want its productions: ");
        String givenNonTerminal = scanner.next();
        System.out.println(grammar.getProductionsForNonTerminal(givenNonTerminal));
        System.out.println(grammar.checkContextFreeGrammar());
    }
}
