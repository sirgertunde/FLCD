package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
    private final Set<String> n;
    private final Set<String> sigma;
    private String s;
    private final Map<String, List<String>> p;

    public Grammar(String fileName) {
        n = new HashSet<>();
        p = new HashMap<>();
        sigma = new HashSet<>();
        readFile(fileName);
    }

    public Set<String> getN() {
        return n;
    }

    public Set<String> getSigma() {
        return sigma;
    }

    public String getS() {
        return s;
    }

    public Map<String, List<String>> getP() {
        return p;
    }

    public void readFile(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("N =")) {
                    String[] nonterminals = line.substring(3).trim().split("\\s+");
                    n.addAll(Arrays.asList(nonterminals));
                } else if (line.startsWith("sigma =")) {
                    String[] terminals = line.substring(8).split("_");
                    sigma.addAll(Arrays.asList(terminals));
                } else if (line.startsWith("S =")) {
                    s = line.substring(3).trim();
                } else if (line.startsWith("P =")) {
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("->");
                        if (parts.length == 2) {
                            String terminalPart = parts[0].trim();
                            String productionPart = parts[1].trim();
                            if(!p.containsKey(terminalPart)){
                                p.put(terminalPart, new ArrayList<>());
                                p.get(terminalPart).add(productionPart);
                            }
                            else
                                p.get(terminalPart).add(productionPart);
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
        for(String production: p.get(nonTerminal)){
            productions.add(new ArrayList<>());
            String[] symbols = production.split(" ");
            productions.get(i).addAll(Arrays.asList(symbols));
            i++;
        }
        return productions;
    }

    public boolean checkContextFreeGrammar(){
        for(String key: p.keySet()){
            if(!n.contains(key)){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        Grammar grammar = new Grammar("g2.txt");
        System.out.println("Set of non-terminals: " + grammar.getN());
        System.out.println("Set of terminals: " + grammar.getSigma());
        System.out.println("Starting symbol: " + grammar.getS());
        System.out.println("Productions: " + grammar.getP());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give a non-terminal for which you want its productions: ");
        String givenNonTerminal = scanner.next();
        System.out.println(grammar.getProductionsForNonTerminal(givenNonTerminal));
        System.out.println(grammar.checkContextFreeGrammar());
    }
}
