import java.io.FileReader;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {
        AnaLex lexer = new AnaLex(new FileReader("src/valorsidproves.txt"));
        Parser parser = new Parser(lexer);
        Symbol result = parser.parse();
        System.out.println(result.value);
    }
}
