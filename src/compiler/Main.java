package compiler;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import assembler.AssemblerGenerator;
import ast.ProgramNode;
import codegen.CodeGenerator;
import codegen.Instruction;
import java_cup.runtime.Symbol;
import errors.*;
import optimizer.Optimizer;
import semantic.SemanticAnalyzer;
import frontend.lexer.AnaLex;
import frontend.parser.Parser;

public class Main {

    public static void main(String[] args) {
        // 1) Entrada: fitxer passat per línia de comandes
        String inputPath;

        // Cas 1: s'executa sense arguments (IDE, Run, etc.)
        if (args.length == 0) {
            inputPath = "programa.txt";

            File defaultFile = new File(inputPath);
            if (!defaultFile.exists()) {
                System.err.println("No s'ha especificat cap fitxer d'entrada.");
                System.err.println();
                System.err.println("Opcions d'execució:");
                System.err.println("  1) Executar des de terminal:");
                System.err.println("     java -cp \"out\\production\\compilador;java-cup-11b-runtime.jar\" compiler.Main <fitxer_entrada> [directori_sortida]");
                System.err.println();
                System.err.println("  2) O bé crear un fitxer anomenat:");
                System.err.println("     programa.txt");
                System.err.println("     al directori arrel del projecte.");
                return;
            }

            // Cas 2: s'executa amb arguments
        } else {
            inputPath = args[0];
        }

        // 2) (Opcional) carpeta de sortida
        String outDir = (args.length >= 2) ? args[1] : "out";
        Path outDirPath = Paths.get(outDir);


        try {
            ErrorManager.clear();
            Files.createDirectories(outDirPath);

            // =========================
            // FRONT-END: lèxic + sintàctic
            // =========================
            AnaLex lexer = new AnaLex(new FileReader(inputPath));
            Parser parser = new Parser(lexer);

            Symbol result = parser.parse();
            ProgramNode ast = null;

            if (result != null && result.value instanceof ProgramNode) {
                ast = (ProgramNode) result.value;
            } else {
                // Si no hi ha AST, registra error sintàctic genèric
                ErrorManager.add(new CompilerError(CompilerError.TYPE.SINTACTIC,
                        "No s'ha pogut construir l'AST del programa."));
            }

            // Mostrar errors lèxics/sintàctics
            ErrorManager.printErrors();

            if (ErrorManager.hasErrors()) {
                System.out.println("S'han detectat errors (lèxics/sintàctics). S'atura la compilació.");

                ErrorManager.writeToFile(outDirPath.resolve("errors.txt"));
                return;
            }

            // =========================
            // FRONT-END: semàntica
            // =========================
            SemanticAnalyzer sem = new SemanticAnalyzer();
            sem.analyze(ast);

            // Mostrar errors semàntics
            ErrorManager.printErrors();

            if (ErrorManager.hasErrors()) {
                System.out.println("S'han detectat errors semàntics. S'atura la compilació.");

                ErrorManager.writeToFile(outDirPath.resolve("errors.txt"));
                return;
            }

            Files.writeString(outDirPath.resolve("tokens.txt"), lexer.getTokenDump());
            Files.writeString(outDirPath.resolve("taula_simbols.txt"), sem.getSymbolTableDump());

            // =========================
            // BACK-END: generació TAC
            // =========================
            assert ast != null;
            ast.generateCode();
            CodeGenerator.actualitzarProcediments();

            Files.writeString(outDirPath.resolve("taula_variables.txt"), CodeGenerator.getTaulaVariables().toFullString());

            Files.writeString(outDirPath.resolve("taula_procediments.txt"), CodeGenerator.getTaulaProcediments().toFullString());

            Files.writeString(outDirPath.resolve("tac_readable.txt"), CodeGenerator.readableCode());


            // =========================
            // BACK-END: ASM sense optimitzar
            // =========================
            List<Instruction> tacOriginal = CodeGenerator.getCode();

            AssemblerGenerator asmNoOpt = new AssemblerGenerator(tacOriginal);
            asmNoOpt.generate();
            asmNoOpt.writeToFile(outDirPath.resolve("program_no_opt").toString());

            // =========================
            // BACK-END: ASM optimitzat
            // =========================
            List<Instruction> tacOpt = Optimizer.optimize();

            AssemblerGenerator asmOpt = new AssemblerGenerator(tacOpt);
            asmOpt.generate();
            asmOpt.writeToFile(outDirPath.resolve("program_opt").toString());

            System.out.println("Compilació finalitzada correctament.");
            System.out.println("Entrada: " + inputPath);
            System.out.println("Sortida: " + outDirPath.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
