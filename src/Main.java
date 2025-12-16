import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;

import assembler.AssemblerGenerator;
import codegen.CodeGenerator;
import java_cup.runtime.Symbol;
import errors.*;
import nodes.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            AnaLex lexer = new AnaLex(new FileReader("src/valorsidproves.txt"));
            Parser parser = new Parser(lexer);

            Symbol result = parser.parse();
            ProgramNode ast = null;

            if(result == null || !(result.value instanceof ProgramNode)) {
                System.err.println("Error: no s'ha pogut construir el programa.");
            } else {
                ast = (ProgramNode) result.value;
            }

            ErrorManager.printErrors();

            if (!ErrorManager.hasErrors()) {
                SemanticAnalyzer sem = new SemanticAnalyzer();
                sem.analyze(ast);

                if (!ErrorManager.hasErrors() && ast != null) {
                    ast.generateCode();
                    CodeGenerator.actualitzarProcediments();

                    System.out.println(CodeGenerator.getTaulaVariables());
                    // Files.writeString(Path.of("variables.txt"), CodeGenerator.getTaulaVariables().toFullString());
                    System.out.println(CodeGenerator.getTaulaProcediments());
                    // Files.writeString(Path.of("procediments.txt"), CodeGenerator.getTaulaProcediments().toFullString());
                    System.out.println(CodeGenerator.readableCode());
                    // Files.writeString(Path.of("codi3a.txt"), CodeGenerator.getCode().toString());
                    // Files.writeString(Path.of("codi3a_r.txt"), CodeGenerator.readableCode());

                    AssemblerGenerator assembler = new AssemblerGenerator();
                    assembler.generate();
                    assembler.writeToFile("prova");

                    System.out.println("Compilació finalitzada correctament.");

                }

                ErrorManager.printErrors();
            } else {
                System.out.println("S'han detectat errors lèxics o sintàctics, s'atura l'anàlisi.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
