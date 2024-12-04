package src;

import java.io.*;
import java.util.*;

public class MyScanner{
    private final Set<String> reserved_words = new HashSet<>();
    private final Set<String> operators = new HashSet<>();
    private final Set<String> separators = new HashSet<>();
    private final Map<String, Integer> codesForTokens = new HashMap<>();

    private final HashTable<String> symbolTable;
    private final ProgramInternalForm pif;
    private boolean lexicallyCorrect = true;
    private int lineNumber = 1;

    public MyScanner() {
        this.symbolTable = new HashTable<>();
        this.pif = new ProgramInternalForm();
        loadTokens();
    }

    public boolean getLexicallyCorrect(){
        return lexicallyCorrect;
    }

    public void scan(String program) {
        int index = 0;
        while (index < program.length()) {
            while(Character.isWhitespace(program.charAt(index))){
                if (program.charAt(index) == '\n') {
                    lineNumber++;
                }
                index++;
            }
            String token = detect(program, index);
            index += token.length();
            if (reserved_words.contains(token) || operators.contains(token) || separators.contains(token)) {
                pif.genPIF(codesForTokens.get(token), new MyPair<>(-1, -1));
            } else if (isIdentifier(token) || isConstant(token)) {
                MyPair<Integer, Integer> position = symbolTable.get(token);
                if(position == null)
                {
                    symbolTable.add(token);
                    position = symbolTable.get(token);
                }
                if(isIdentifier(token))
                    pif.genPIF(47, position);
                else if(isConstant(token))
                    pif.genPIF(48, position);
            } else {
                System.out.println("Lexical error on line " + lineNumber + ": Unrecognized token \"" + token + "\"");
                lexicallyCorrect = false;
            }
        }
    }

    private String detect(String program, int startIndex) {
        StringBuilder token = new StringBuilder();
        char ch = program.charAt(startIndex);
        if (ch == '"') {
            token.append(ch);
            startIndex++;
            while (startIndex < program.length() && program.charAt(startIndex) != '"') {
                token.append(program.charAt(startIndex));
                startIndex++;
            }
            if (startIndex < program.length() && program.charAt(startIndex) == '"') {
                token.append('"');
                return token.toString();
            } else {
                System.out.println("Lexical error on line" +lineNumber + ": Missing closing quote for string constant.");
                lexicallyCorrect = false;
                return token.toString();
            }
        }
        if (ch == '\'') {
            token.append(ch);
            startIndex++;
            if (startIndex < program.length() - 1 && program.charAt(startIndex + 1) == '\'') {
                token.append(program.charAt(startIndex));
                token.append('\'');
                return token.toString();
            } else {
                System.out.println("Lexical error on line " + lineNumber + ": Invalid character constant.");
                lexicallyCorrect = false;
                return token.toString();
            }
        }
        if (operators.contains(String.valueOf(ch))) {
            if (startIndex + 1 < program.length()) {
                String twoCharOp = "" + ch + program.charAt(startIndex + 1);
                if (operators.contains(twoCharOp)) {
                    return twoCharOp;
                }
            }
            return String.valueOf(ch);
        }
        if (separators.contains(String.valueOf(ch))) {
            return String.valueOf(ch);
        }
        while (startIndex < program.length() && !Character.isWhitespace(ch) &&
                !operators.contains(String.valueOf(ch)) && !separators.contains(String.valueOf(ch))) {
            token.append(ch);
            startIndex++;
            if (startIndex < program.length()) ch = program.charAt(startIndex);
        }
        return token.toString();
    }


    private boolean isIdentifier(String token) {
        FiniteAutomata faForIdentifiers = new FiniteAutomata("fa_for_identifiers.in");
        return faForIdentifiers.isAccepted(token);
        //return Character.isLetter(token.charAt(0)) && token.chars().allMatch(Character::isLetterOrDigit);
    }

    private boolean isConstant(String token) {
        FiniteAutomata faForStringConstants = new FiniteAutomata("fa_for_string_constants.in");
        FiniteAutomata faForNumericConstants = new FiniteAutomata("fa_for_numeric_constants.in");
        return faForStringConstants.isAccepted(token) || faForNumericConstants.isAccepted(token) || (token.startsWith("'") && token.length() == 3 && token.endsWith("'"));
//        return token.matches("[1-9]+[0-9]*") || token.equals("0") || (token.startsWith("\"") && token.endsWith("\"")) ||
//                (token.startsWith("'") && token.length() == 3 && token.endsWith("'"));
    }

    public void loadTokens() {
        try (BufferedReader reader = new BufferedReader(new FileReader("token.in"))) {
            String line;
            int numberOfLine = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (numberOfLine < 26) {
                    reserved_words.add(line);
                } else if (numberOfLine < 32) {
                    separators.add(line);
                } else {
                    operators.add(line);
                }
                codesForTokens.put(line, numberOfLine);
                numberOfLine++;
            }
        } catch (IOException e) {
            System.out.println("Error reading token file: " + e.getMessage());
        }
    }

    public String readProgramFromFile(String fileName){
        try{
            File program = new File(fileName);
            Scanner scanner = new Scanner(program);
            scanner.useDelimiter("\\A");
            String fileContent = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            return fileContent;
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void writePifToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("pif.out"))) {
            for (MyPair<Integer, MyPair<Integer, Integer>> entry : pif.get()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing PIF to file: " + e.getMessage());
        }
    }

    public void writeToSymbolTableFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("st.out"))) {
            for (String key : symbolTable.getKeys()) {
                MyPair<Integer, Integer> position = symbolTable.get(key);
                writer.write(key + " " + position.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing symbol table to file: " + e.getMessage());
        }
    }
}
