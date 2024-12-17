package src;

import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private final List<MyPair<MyPair<String, Integer>, MyPair<Integer, Integer>>> pif;

    public ProgramInternalForm() {
        this.pif = new ArrayList<>();
    }

    public List<MyPair<MyPair<String, Integer>, MyPair<Integer, Integer>>> get(){
        return pif;
    }

    public void genPIF(String token, Integer tokenCode, MyPair<Integer, Integer> position) {
        pif.add(new MyPair<>(new MyPair<>(token, tokenCode), position));
    }

    @Override
    public String toString() {
        return pif.toString();
    }
}