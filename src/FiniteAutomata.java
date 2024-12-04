package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FiniteAutomata {
    private final Set<String> Q;
    private final Set<String> sigma;
    private String q0;
    private final Set<String> F;
    private final Map<MyPair<String, String>, String> delta;

    public Set<String> getSigma() {
        return sigma;
    }

    public Set<String> getQ() {
        return Q;
    }

    public String getq0() {
        return q0;
    }

    public Set<String> getF() {
        return F;
    }

    public Map<MyPair<String, String>, String> getDelta() {
        return delta;
    }

    public FiniteAutomata(String fileName) {
        this.Q = new HashSet<>();
        this.sigma = new HashSet<>();
        this.delta = new HashMap<>();
        this.F = new HashSet<>();
        readFile(fileName);

    }

    public void readFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Q =")) {
                    String[] states = line.substring(3).trim().split("\\s+");
                    Q.addAll(Arrays.asList(states));
                } else if (line.startsWith("sigma =")) {
                    String[] symbols = line.substring(7).split(",");
                    sigma.addAll(Arrays.asList(symbols));
                } else if (line.startsWith("q0 =")) {
                    q0 = line.substring(4).trim();
                } else if (line.startsWith("F =")) {
                    String[] states = line.substring(3).trim().split("\\s+");
                    F.addAll(Arrays.asList(states));
                } else if (line.startsWith("delta =")) {
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split("->");
                        if (parts.length == 2) {
                            String[] pairParts = parts[0].trim().replaceAll("[()]", "").split(",");
                            String state = pairParts[0].trim();
                            String symbol = pairParts[1];
                            String nextState = parts[1].trim();
                            MyPair<String, String> newKey = new MyPair<>(state, symbol);
                            delta.put(newKey, nextState);

                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isAccepted(String seq) {
        String currentState = q0;
        for (char symbol : seq.toCharArray()) {
            MyPair<String, String> transitionKey = new MyPair<>(currentState, String.valueOf(symbol));
            if (delta.containsKey(transitionKey)) {
                currentState = delta.get(transitionKey);
            } else {
                return false;
            }
        }
        return F.contains(currentState);
    }


    @Override
    public String toString(){
        String s = "Q= " + Q + "\n" +
                "sigma= " + sigma + "\n" +
                "F= " + F + "\n" +
                "delta= " + delta + "\n" +
                "q0= " + q0 + "\n";
        return s;
    }

    public void printMenu(){
        System.out.println("1. Set of states (Q)");
        System.out.println("2. Alphabet (sigma)");
        System.out.println("3. Initial state (q0)");
        System.out.println("4. Set of final states (F)");
        System.out.println("5. Transition function (delta)");
        System.out.println("0. Exit");
        System.out.println("Choose an option: ");
    }

    public static void main(String[] args) {
        FiniteAutomata fa = new FiniteAutomata("fa_for_identifiers.in");
        String sequence = "0n7";
        while(true){
            fa.printMenu();
            Scanner scanner = new Scanner(System.in);
            try {
                int option = scanner.nextInt();
                if (option == 1)
                    System.out.println(fa.getQ());
                else if (option == 2)
                    System.out.println(fa.getSigma());
                else if (option == 3)
                    System.out.println(fa.getq0());
                else if (option == 4)
                    System.out.println(fa.getF());
                else if (option == 5)
                    System.out.println(fa.getDelta());
                else if (option == 0)
                    break;
                else
                    System.out.println("Invalid option");
            }catch (Exception e){
                System.out.println("Invalid option");
            }
        }
        if(fa.isAccepted(sequence))
            System.out.println("accepted");
        else
            System.out.println("not accepted");
    }
}
