public class Main {
    public static void main(String[] args) {
        MyScanner myScanner = new MyScanner();
        String fileContent = myScanner.readProgramFromFile("program1.txt");
        if(fileContent != null)
            myScanner.scan(fileContent);
        if(myScanner.getLexicallyCorrect())
            System.out.println("Lexically correct");
        myScanner.writePifToFile();
        myScanner.writeToSymbolTableFile();
    }
}