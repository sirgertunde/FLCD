import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private final List<MyPair<Integer, MyPair<Integer, Integer>>> pif;

    public ProgramInternalForm() {
        this.pif = new ArrayList<>();
    }

    public List<MyPair<Integer, MyPair<Integer, Integer>>> get(){
        return pif;
    }

    public void genPIF(Integer token, MyPair<Integer, Integer> position) {
        pif.add(new MyPair<>(token, position));
    }

    @Override
    public String toString() {
        return pif.toString();
    }
}