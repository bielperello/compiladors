import java.io.FileReader;
import java_cup.runtime.Symbol;
import errors.*;

public class Main {
    public static void main(String[] args) throws Exception {
        AnaLex lexer = new AnaLex(new FileReader("src/valorsidproves.txt"));
        Parser parser = new Parser(lexer);
        Symbol result = parser.parse();
        ErrorManager.printErrors();
        // System.out.println(result.value);
    }
}
