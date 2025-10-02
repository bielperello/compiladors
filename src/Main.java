import java.io.FileReader;
import java_cup.runtime.*;


public class Main {
    public static void main(String[] args) throws Exception {
        AnaLex lexer = new AnaLex(new FileReader("src/valorsidproves.txt"));
        Parser parser = new Parser(lexer);
        Symbol result = parser.parse();
        System.out.println(result.value); // haurà de mostrar SymbolP amb tot l’AST
    }
}


