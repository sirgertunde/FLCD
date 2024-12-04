package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Grammar grammar;
    private Map<String, List<List<String>>> first;

    public Grammar getGrammar() {
        return grammar;
    }

    public Parser(Grammar gr) {
        grammar = gr;
        first = new HashMap<>();
        generateFirst();
    }

    public void generateFirst(){
        //initializare
        for(String nonTerminal: grammar.getN()){
            first.put(nonTerminal, new ArrayList<>());
            first.get(nonTerminal).add(new ArrayList<>());
            List<String> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
            for(String production: productionsForNonTerminal) {
                String firstSymbolInProduction = String.valueOf(production.charAt(0));
                if (grammar.getSigma().contains(firstSymbolInProduction) && !first.get(nonTerminal).getFirst().contains(String.valueOf(production.charAt(0)))) {
                    first.get(nonTerminal).getFirst().add(firstSymbolInProduction);
                }
            }
        }

    }
}
