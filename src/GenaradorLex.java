/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

import jflex.Main;
import jflex.exceptions.SilentExit;

/**
 *
 * @author ferri
 */
public class GenaradorLex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SilentExit {
        System.out.println("adf");
        String path_in = "src/Lexer.flex";
        String path_out = "src/";
        generarLexer(path_in, path_out);
    }


    public static void generarLexer(String path_in, String path_out) {
        String args[] = {"-d", path_out, path_in};
        try {
            Main.generate(args);
        } catch (SilentExit e) {
            throw new RuntimeException(e);
        }
    }

}
