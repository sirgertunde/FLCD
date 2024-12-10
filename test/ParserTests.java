package test;

import org.junit.jupiter.api.Assertions;
import src.Grammar;
import src.Parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParserTests {
    @org.junit.Test
    public void testFirst(){
        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);
        Map<String, Set<String>> first = parser.getFirst();
        Map<String, Set<String>> expectedFirst = new HashMap<>();
        expectedFirst.put("B", new HashSet<>());
        expectedFirst.get("B").add("b");
        expectedFirst.put("S", new HashSet<>());
        expectedFirst.get("S").add("a");
        expectedFirst.put("C", new HashSet<>());
        expectedFirst.get("C").add("c");
        Assertions.assertEquals(first, expectedFirst);
    }

    @org.junit.Test
    public void testFollow(){
        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);
        Map<String, Set<String>> follow = parser.getFollow();
        Map<String, Set<String>> expectedFollow = new HashMap<>();
        expectedFollow.put("B", new HashSet<>());
        expectedFollow.get("B").add("ε");
        expectedFollow.put("S", new HashSet<>());
        expectedFollow.get("S").add("ε");
        expectedFollow.put("C", new HashSet<>());
        expectedFollow.get("C").add("ε");
        Assertions.assertEquals(follow, expectedFollow);
    }
}
