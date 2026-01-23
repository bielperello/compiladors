package errors;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ErrorManager {
    private static final List<CompilerError> errors = new ArrayList<>();

    public static void add(CompilerError error) {
        errors.add(error);
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static List<CompilerError> getErrors() {
        return errors;
    }

    public static void clear() {
        errors.clear();
    }

    /** Ordena per tipus, línia i columna (sortida consistent) */
    private static List<CompilerError> sortedErrors() {
        List<CompilerError> copy = new ArrayList<>(errors);
        copy.sort(
                Comparator.comparing(CompilerError::getType)
                        .thenComparingInt(CompilerError::getLine)
                        .thenComparingInt(CompilerError::getColumn)
        );
        return copy;
    }

    public static void printErrors() {
        for (CompilerError error : sortedErrors()) {
            System.out.println(error);
        }
    }

    /** Text complet per fitxer */
    public static String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TIPUS\t\tLINIA\t  COL\tMISSATGE\n");
        sb.append("-----\t\t-----\t  ---\t--------\n");
        for (CompilerError e : sortedErrors()) {
            sb.append(e.toFileRow()).append("\n");
        }
        return sb.toString();
    }

    public static void writeToFile(Path file) {
        try (PrintWriter writer = new PrintWriter(file.toFile())) {
            writer.print(toFileString());
        } catch (Exception e) {
            System.err.println("No s'ha pogut escriure el fitxer d'errors: " + e.getMessage());
        }
    }
}
