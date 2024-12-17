package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {
    private final Grammar grammar;
    private final Map<String, List<Set<String>>> tableFirst;
    private final Map<String, Set<String>> tableFollow;
    private final Map<MyPair<String, String>, MyPair<String, Integer>> parsingTable;

    public Grammar getGrammar() {
        return grammar;
    }

    public Parser(Grammar gr) {
        grammar = gr;
        tableFirst = new HashMap<>();
        tableFollow = new HashMap<>();
        parsingTable = new HashMap<>();
        generateTableFirst();
        generateTableFollow();
        fillParsingTable();
    }

    public void generateTableFirst() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            tableFirst.put(nonTerminal, new ArrayList<>());
            tableFirst.get(nonTerminal).add(new HashSet<>());
            List<List<String>> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
            for (List<String> production : productionsForNonTerminal) {
                String firstSymbolInProduction = production.getFirst();
                if (grammar.getTerminals().contains(firstSymbolInProduction)) {
                    tableFirst.get(nonTerminal).getFirst().add(firstSymbolInProduction);
                }
            }
        }

        int i = 0;
        boolean continuing;
        do {
            i++;
            continuing = false;
            for (String nonTerminal : grammar.getNonTerminals()) {
                tableFirst.get(nonTerminal).add(new HashSet<>());
                List<List<String>> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
                boolean isEmpty = false;
                for (List<String> production : productionsForNonTerminal) {
                    String firstSymbolFromProduction = production.getFirst();
                    if(production.equals(List.of("ε")) || firstSymbolFromProduction.equals("ε")){
                        tableFirst.get(nonTerminal).get(i).add("ε");
                    }
                    if(grammar.getTerminals().contains(firstSymbolFromProduction) && !tableFirst.get(nonTerminal).get(i).contains(firstSymbolFromProduction)){
                        tableFirst.get(nonTerminal).get(i).add(firstSymbolFromProduction);
                    }
                    else {
                        for (String symbol : production) {
                            if (grammar.getNonTerminals().contains(symbol) && tableFirst.get(symbol).get(i - 1).isEmpty()) {
                                isEmpty = true;
                                break;
                            }
                        }
                        String firstSymbol = production.getFirst();
                        if (!isEmpty) {
                            for (String previousNonTerminal : tableFirst.get(nonTerminal).get(i - 1)) {
                                tableFirst.get(nonTerminal).get(i).add(previousNonTerminal);
                            }
                            if (grammar.getTerminals().contains(firstSymbol)) {
                                tableFirst.get(nonTerminal).get(i).add(firstSymbol);
                            }
                            if (grammar.getNonTerminals().contains(firstSymbol)) {
                                for (String s : tableFirst.get(firstSymbol).get(i - 1)) {
                                    tableFirst.get(nonTerminal).get(i).add(s);
                                }
                            }
                        }
                    }
                }
            }

            for (String nonTerminal : grammar.getNonTerminals()) {
                if (!tableFirst.get(nonTerminal).get(i).equals(tableFirst.get(nonTerminal).get(i - 1))) {
                    continuing = true;
                }
            }
        } while (continuing);
    }


    public void generateTableFollow() {
        // Since we generate the first table we'll be able to look ahead there
        // Idk to what extent the states of the follow table should be stored

        // Initialize...
        for (String nonTerminal : grammar.getNonTerminals()) {
            tableFollow.put(nonTerminal, new HashSet<>());
        }

        // Mark with ε eoi, this is only used in the follow
        tableFollow.get(grammar.getStartingSymbol()).add("ε");

        boolean updated;
        do {
            updated = false; // Will check for modifications.

            for (String nonTerminal : grammar.getNonTerminals()) {
                List<List<String>> productionsForNonTerminal = grammar.getProductionsForNonTerminal(nonTerminal);
                for (List<String> production : productionsForNonTerminal) {
                    for (int i = 0; i < production.size(); i++) {
                        String symbol = production.get(i);

                        if (grammar.getNonTerminals().contains(symbol)) {
                            // Find its set of the NonTerminal
                            Set<String> followSet = tableFollow.get(symbol);
                            if (i + 1 < production.size()) {

                                // Check If there is a symbol after this one
                                // Add the first set of that symbol to that symbol's follow.
                                String nextSymbol = production.get(i + 1);
                                if (grammar.getTerminals().contains(nextSymbol)) {
                                    if (followSet.add(nextSymbol)) {
                                        updated = true;
                                    }
                                } else if (grammar.getNonTerminals().contains(nextSymbol)) {

                                    // Add the FIRST set of the next NT, unless it is ε (which it won't, i think, at least for our case)
                                    for (String terminal : tableFirst.get(nextSymbol).getLast()) {
                                        if (followSet.add(terminal) && !Objects.equals(terminal, "ε")) {
                                            updated = true;
                                        } else if (Objects.equals(terminal, "ε") && followSet.addAll(tableFollow.get(nonTerminal))) {
                                            updated = true;
                                        }
                                    }
                                }
                            } else {
                                // If there is no symbol after, add the FOLLOW set of the current NT to its own FOLLOW set
                                for (String terminal : tableFollow.get(nonTerminal)) {
                                    if (followSet.add(terminal)) {
                                        updated = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (updated);

    }

    public Map<String, Set<String>> getFirst(){
        Map<String, Set<String>> first = new HashMap<>();
        for(String key: tableFirst.keySet()){
            first.put(key, tableFirst.get(key).getLast());
        }
        return first;
    }

    public Map<String, Set<String>> getFollow() {
        return tableFollow;
    }

    public void fillParsingTable(){
        parsingTable.put(new MyPair<>("$", "$"), null);
        List<MyPair<String, String>> listOfProductions = grammar.getListOfProductions();
        for(String nonTerminal: grammar.getNonTerminals()){
            for (String production: grammar.getProductions().get(nonTerminal)){
                String firstSymbol = String.valueOf(production.charAt(0));
                if(grammar.getTerminals().contains(firstSymbol)){
                    if(parsingTable.put(new MyPair<>(nonTerminal, firstSymbol), new MyPair<>(production, listOfProductions.indexOf(new MyPair<>(nonTerminal, production)))) != null){
                        throw new RuntimeException("not of type ll1, cell " + nonTerminal + " " + firstSymbol);
                    }
                }
                if(firstSymbol.equals("ε")){
                    Set<String> follow = getFollow().get(nonTerminal);
                    for(String symbol: follow){
                        if(parsingTable.put(new MyPair<>(nonTerminal, symbol), new MyPair<>(production, listOfProductions.indexOf(new MyPair<>(nonTerminal, production)))) != null){
                            throw new RuntimeException("not of type ll1, cell " + nonTerminal + " " + symbol);
                        }
                    }
                }
                if(grammar.getNonTerminals().contains(firstSymbol)){
                    Set<String> first = getFirst().get(nonTerminal);
                    for(String symbol: first){
                        if(grammar.getTerminals().contains(symbol)){
                            if(parsingTable.put(new MyPair<>(nonTerminal, symbol), new MyPair<>(production, listOfProductions.indexOf(new MyPair<>(nonTerminal, production)))) != null){
                                throw new RuntimeException("not of type ll1, cell " + nonTerminal + " " + symbol);
                            }
                        }
                        if(symbol.equals("ε")){
                            Set<String> follow = getFollow().get(nonTerminal);
                            for(String symbolInFollow: follow){
                                if(parsingTable.put(new MyPair<>(nonTerminal, symbolInFollow), new MyPair<>(production, listOfProductions.indexOf(new MyPair<>(nonTerminal, production)))) != null){
                                    throw new RuntimeException("not of type ll1, cell " + nonTerminal + " " + symbolInFollow);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void parseSimpleGrammar(String fileName){
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String sequence = reader.readLine().trim();
            Stack<String> inputStack = new Stack<>();
            Stack<String> workStack = new Stack<>();
            workStack.push(grammar.getStartingSymbol());
            Queue<Integer> outputBand = new LinkedList<>();
            for (int i = sequence.length() - 1; i >= 0; i--) {
                String s = String.valueOf(sequence.charAt(i));
                inputStack.push(s);
            }
            boolean go = true;
            String result = "";
            while(go){
                if(inputStack.empty() && !workStack.empty()){
                    while(!workStack.empty()){
                        outputBand.add(grammar.getListOfProductions().indexOf(new MyPair<>(workStack.peek(), "ε")));
                        workStack.pop();
                    }
                    result = "accept";
                    break;
                }

                MyPair<String, Integer> cellContent = parsingTable.get(new MyPair<>(workStack.peek(), inputStack.peek()));
                if(cellContent != null){
                    if(Objects.equals(cellContent.getKey(), "ε")){
                        workStack.pop();
                        outputBand.add(cellContent.getValue());
                    }else{
                        workStack.pop();
                        String[] splitArray = cellContent.getKey().split(" ");
                        List<String> splitCellContent = Arrays.asList(splitArray);
                        Collections.reverse(splitCellContent);
                        for(String symbol: splitCellContent){
                            workStack.push(symbol);
                        }
                        outputBand.add(cellContent.getValue());
                    }
                } else if(Objects.equals(inputStack.peek(), workStack.peek()) && grammar.getTerminals().contains(workStack.peek())) {
                    inputStack.pop();
                    workStack.pop();
                } else if (Objects.equals(inputStack.peek(), workStack.peek()) && Objects.equals(inputStack.peek(), "$")) {
                    go = false;
                    result = "accept";
                }else {
                    go = false;
                    result = "error";
                }
            }
            if(result.equals("accept")){
                System.out.println("Sequence accepted");
                System.out.println(outputBand);
            }else {
                System.out.println("Sequence not accepted, syntax error at " + inputStack.peek());
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args){
        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);

        System.out.println("FIRST:\n");
        System.out.println(parser.getFirst());

        System.out.println("\n\nFOLLOW:\n");
        for (Map.Entry<String, Set<String>> entry : parser.getFollow().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println(grammar.getListOfProductions());
        System.out.println(parser.parsingTable);
        parser.parseSimpleGrammar("seq.txt");
    }
}
