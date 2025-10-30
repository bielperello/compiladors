import java.io.FileReader;
import java_cup.runtime.Symbol;
import errors.*;
import symbols.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            AnaLex lexer = new AnaLex(new FileReader("src/valorsidproves.txt"));
            Parser parser = new Parser(lexer);

            Symbol result = parser.parse();
            ProgramNode ast = (ProgramNode) result.value;

            ErrorManager.printErrors();

            if (!ErrorManager.hasErrors()) {
                SemanticAnalyzer sem = new SemanticAnalyzer();
                sem.analyze(ast);

                ErrorManager.printErrors();
            } else {
                System.out.println("S'han detectat errors lèxics o sintàctics, s'atura l'anàlisi semàntica.");
            }

        } catch (Exception e) {
            System.err.println("Error greu: " + e.getMessage());
        }
    }
}
