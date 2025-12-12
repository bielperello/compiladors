package assembler;

import codegen.*;
import nodes.Kind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AssemblerGenerator {
    private final TaulaVariables TV;
    private final TaulaProcediments TP;
    private final List<Instruction> code;

    private int procActual;

    private final StringBuilder asm;

    private final String DISP_REGISTER = "A0";
    private final String BP_LOCAL = "A6";
    private final int MIDA_MAX_STR = 512;

    public static final int DESP_PARAMS = 8;

    public AssemblerGenerator() {
        this.TV = CodeGenerator.getTaulaVariables();
        this.TP = CodeGenerator.getTaulaProcediments();
        this.code = CodeGenerator.getCode();
        asm = new StringBuilder();
    }

    public String generate() {
        generateTextSection();
        generateDataSection();
        return asm.toString();
    }

    private void generateDataSection() {
        // Strings Literals
        for (Map.Entry<String, String> entry : CodeGenerator.stringLiterals.entrySet()) {
            String literal = entry.getKey();
            String label = entry.getValue();

            asm.append(label).append(": DC.B '").append(literal).append("',0\n");
        }

        // Variables String
        for (EntradaVariable e : CodeGenerator.getTaulaVariables().getList()) {
            if (e.tsb == Kind.CADENA) {
                asm.append(e.bufferLabel).append(": DS.B ").append(MIDA_MAX_STR).append("\n");
            }
        }

        // Display Vector
        asm.append("\nDISP: DS.L ").append(CodeGenerator.np).append("\n\n");

        // Heap Pointer
        asm.append("HP: DS.L 1\n\n");

        // Buffer temporal per strings
        asm.append("BTS: DS.B 512\n\n");

        // Darrera línia de codi
        asm.append("\tEND START");
    }

    private void generateTextSection() {
        asm.append("    ORG $1000\n\n");

        asm.append("START:\n");
        procActual = 1;

        generateProcedurePMB(procActual);

        for (Instruction instr : code) {
            switch(instr.op) {
                case SKIP:
                    asm.append("E").append(instr.dest).append(":\n");
                    break;
                case GOTO:
                    asm.append("    BRA.W E").append(instr.dest).append("\n");

                    break;
                case PMB:
                    generateProcedurePMB(instr.dest);
                    procActual++;
                    break;
                case RTN:
                    generateProcedureRTN(instr.dest);
                    procActual--;
                    break;
                case IF_EQ, IF_NE, IF_GE, IF_GT, IF_LE, IF_LT:
                    // etiqueta a la qual botar: E + instr.dest
                    String aSrc1 = gestAddress(instr.arg1, procActual);
                    String aSrc2 = gestAddress(instr.arg2, procActual);

                    // operand 1 → D0
                    asm.append("    MOVE.L ").append(aSrc1).append(",D0\n");
                    // operand 2 → D1
                    asm.append("    MOVE.L ").append(aSrc2).append(",D1\n");

                    // compare de D1 amb D0 (D0 - D1)
                    asm.append("    CMP.L D1,D0\n");

                    if (instr.op.equals(OpCode.IF_EQ)) {
                        asm.append("    BEQ.W E").append(instr.dest).append("\n");
                    } else if (instr.op.equals(OpCode.IF_NE)) {
                        asm.append("    BNE.W E").append(instr.dest).append("\n");
                    } else if (instr.op.equals(OpCode.IF_GE)) {
                        asm.append("    BGE.W E").append(instr.dest).append("\n");
                    } else if (instr.op.equals(OpCode.IF_GT)) {
                        asm.append("    BGT.W E").append(instr.dest).append("\n");
                    } else if (instr.op.equals(OpCode.IF_LE)) {
                        asm.append("    BLE.W E").append(instr.dest).append("\n");
                    } else {
                        asm.append("    BLT.W E").append(instr.dest).append("\n");
                    }

                    break;
                case IND_VAL:
                    String aDest = gestAddress(instr.dest, procActual);
                    String aBase = gestAddress(instr.arg1, procActual);

                    asm.append("    MOVE.L -").append(instr.arg2).append("").append(aBase).append(",D0\n");
                    asm.append("    MOVE.L D0,").append(aDest).append("\n");

                    break;
                case IND_ASS:
                    String aDestBase = gestAddress(instr.dest, procActual);
                    String aSrc = gestAddress(instr.arg1, procActual);

                    asm.append("    MOVE.L ").append(aSrc).append(",D0\n");
                    asm.append("    MOVE.L D0,-").append(instr.arg2).append(aDestBase).append("\n");

                    break;
                case WRT, READ:
                    InstructionIO instrIO = (InstructionIO) instr;
                    String varDest = gestAddress(instrIO.dest, procActual);

                    if (instrIO.op == OpCode.READ) {
                        if(instrIO.tsbIO == Kind.ENTER) {
                            asm.append("    MOVE.L #4,D0\n");
                            asm.append("    TRAP #15\n");
                            asm.append("    MOVE.L D1,").append(varDest).append("\n");
                        } else if (instrIO.tsbIO == Kind.CARACTER) {
                            asm.append("    CLR.L D1\n");
                            asm.append("    MOVE.L #5,D0\n");
                            asm.append("    TRAP #15\n");
                            asm.append("    MOVE.L D1,").append(varDest).append("\n");
                        } else {
                            if (instr.dest < 0) asm.append("    MOVE.L #BTS,").append(varDest).append("\n");

                            // carregar punter al buffer associat
                            asm.append("    MOVE.L ").append(varDest).append(",A1\n");
                            asm.append("    MOVE.L #2,D0\n");
                            asm.append("    TRAP #15\n");
                        }
                    } else {
                        if (instrIO.tsbIO == Kind.ENTER) {
                            asm.append("    MOVE.L ").append(varDest).append(",D1\n");
                            asm.append("    MOVE.L #3,D0\n");
                            asm.append("    TRAP #15\n");
                        } else if (instrIO.tsbIO == Kind.CARACTER) {
                            asm.append("    MOVE.L ").append(varDest).append(",D1\n");
                            asm.append("    MOVE.L #6,D0\n");
                            asm.append("    TRAP #15\n");
                        } else {
                            asm.append("    MOVE.L ").append(varDest).append(",A1\n");
                            // sense salt de línia (TASK 13) amb salt de línia (TASK 14)
                            asm.append("    MOVE.L #13,D0\n");
                            asm.append("    TRAP #15\n");
                        }
                    }

                    break;
                default:
                    generateInstruction(instr, procActual);
                    break;
            }
        }

        asm.append("\n    SIMHALT\n");

        asm.append("\n* A0 = origen, A1 = desti\n");
        asm.append("COPY_STRING:\n");
        asm.append(".copy:\n");
        asm.append("    MOVE.B (A0)+,D0\n");
        asm.append("    MOVE.B D0,(A1)+\n");
        asm.append("    BEQ .done\n");
        asm.append("    BRA .copy\n");
        asm.append(".done:\n");
        asm.append("    RTS\n");

        asm.append("\n\n* A0 = origen, A1 = desti\n");
        asm.append("APPEND_STRING:\n");
        asm.append(".trobar_final:\n");
        asm.append("    MOVE.B (A1)+,D0\n");
        asm.append("    BNE .trobar_final\n");
        asm.append("    SUBA.L #1, A1\n");
        asm.append(".append:\n");
        asm.append("    MOVE.B (A0)+,D0\n");
        asm.append("    MOVE.B D0,(A1)+\n");
        asm.append("    BNE .append\n");
        asm.append("    RTS\n\n");
    }

    private int computeActivationBlock(int idProc) {
        EntradaProcediment proc = CodeGenerator.getProc(idProc);

        int locals = proc.nLocals*4;
        int temps = proc.nTemporals*4;

        return locals+temps;
    }

    private void generateProcedurePMB(int dest) {
        int blockSize = computeActivationBlock(dest);
        int prof = dest - 1;

        // guardar l'antic DISP[prof] com informació de control local al bloc d'activació
        asm.append("    MOVE.L DISP+").append(4*prof).append(",-(A7)\n");
        // guardar l'antic BP (que es troba a A6) del programa invocador i guardar espai per locals
        asm.append("    LINK A6,#-").append(blockSize).append("\n");
        // actualitzar el valor DISP[prof] amb el BP del bloc d'activació del programa invocat
        asm.append("    MOVE.L A6,DISP+").append(4*prof).append("\n\n");

        for(EntradaVariable v : CodeGenerator.getTaulaVariables().getList()) {
            if (v.idProc == dest && v.tsb == Kind.CADENA) {
                asm.append("    LEA ").append(v.bufferLabel).append(", A0\n");
                asm.append("    MOVE.L A0,").append(v.desp).append("(A6)\n");
            }
        }
    }

    private void generateProcedureRTN(int dest) {
        asm.append("\n    MOVE.L 4(A6),DISP+").append(4*(dest-1)).append("\n");
        asm.append("    UNLK A6\n");
        asm.append("    RTS\n\n");
    }

    private void generateInstruction(Instruction instr, int procId) {
        String aDest = gestAddress(instr.dest, procId);
        String aSrc1 = gestAddress(instr.arg1, procId);
        String aSrc2 = gestAddress(instr.arg2, procId);

        switch(instr.op) {
            case COPY:
                if (instr instanceof InstructionLiteral instrLT) {
                    // cas caràcter simple
                    if (instrLT.literal.length() == 1) {
                        char c = instrLT.literal.charAt(0);
                        asm.append("    MOVE.L #").append((int) c).append(",").append(aDest).append("\n");
                        break;
                    }

                    asm.append("    MOVE.L #").append(instrLT.literal).append(",").append(aDest).append("\n");
                    break;
                }

                if (instr.dest > 0 && instr.arg1 < 0 && CodeGenerator.getVar(instr.dest).tsb == Kind.CADENA) {
                    asm.append("    MOVE.L ").append(aDest).append(",A1\n");
                    asm.append("    MOVE.L ").append(aSrc1).append(",A0\n");
                    asm.append("    JSR COPY_STRING\n");
                    break;
                }

                /*
                asm.append("    MOVE.L ").append(aSrc1).append(",D0\n");
                asm.append("    MOVE.L D0,").append(aDest).append("\n");
                 */

                asm.append("    MOVE.L ").append(aSrc1).append(",").append(aDest).append("\n");


                break;
            case ADD,SUB,PROD,DIV:
                // operand 1 → D0
                asm.append("    MOVE.L ").append(aSrc1).append(",D0\n");
                // operand 2 → D1
                asm.append("    MOVE.L ").append(aSrc2).append(",D1\n");

                // operació
                if (instr.op.equals(OpCode.ADD)) {
                    asm.append("    ADD.L D1,D0\n");
                } else if (instr.op.equals(OpCode.SUB)) {
                    asm.append("    SUB.L D1,D0\n");
                } else if (instr.op.equals(OpCode.PROD)) {
                    asm.append("    MULS D1,D0\n");
                } else {
                    asm.append("    DIVS D1,D0\n");
                }

                // resultat → destí
                asm.append("    MOVE.L D0,").append(aDest).append("\n");

                break;
            case CONCAT:
                // punter source 1 -> A0
                asm.append("    MOVE.L ").append(aSrc1).append(",A0\n");

                if (instr.dest < 0) {
                    // buffer per temporals -> A1
                    asm.append("    LEA BTS, A1\n");
                } else {
                    // punter destí -> A1
                    asm.append("    MOVE.L ").append(aDest).append(",A1\n");
                }

                // copiar cadena 1 al destí
                asm.append("    JSR COPY_STRING\n");

                // punter source 2 -> A0
                asm.append("    MOVE.L ").append(aSrc2).append(",A0\n");

                if (instr.dest < 0) {
                    // buffer per temporals -> A1
                    asm.append("    LEA BTS, A1\n");
                } else {
                    // punter destí -> A1
                    asm.append("    MOVE.L ").append(aDest).append(",A1\n");
                }

                // concatenar cadena 2 al final del destí
                asm.append("    JSR APPEND_STRING\n");

                if (instr.dest < 0) {
                    asm.append("    MOVE.L #BTS,").append(aDest).append("\n");
                }

                break;
            case NEG, NOT:
                asm.append("    MOVE.L ").append(aSrc1).append(",").append(aDest).append("\n");
                asm.append("    MOVE.L ").append(aDest).append(",D0\n");
                asm.append("    NOT.L D0\n");
                asm.append("    MOVE.L D0,").append(aDest).append("\n");

                break;
            case AND, OR:
                // operand 1 → D0
                asm.append("    MOVE.L ").append(aSrc1).append(",D0\n");
                // operand 2 → D1
                asm.append("    MOVE.L ").append(aSrc2).append(",D1\n");

                // operació
                if (instr.op.equals(OpCode.AND)) {
                    asm.append("    AND.L D1,D0\n");
                } else {
                    asm.append("    OR.L D1,D0\n");
                }

                // resultat → destí
                asm.append("    MOVE.L D0,").append(aDest).append("\n");
                break;
            case PARAM_S:
                asm.append("    MOVE.L ").append(aDest).append(",-(A7)\n");

                break;
            case CALL:
                asm.append("    JSR E").append(instr.dest).append("\n");

                int ocupPM = CodeGenerator.getProc(instr.dest - 1).ocupPM;
                asm.append("    ADD.L #").append(ocupPM).append(",A7\n");
                break;
        }
    }

    private String gestAddress(int id, int procId) {
        // Temporals o locals: id < 0
        if (id < 0) {
            int tempIndex = -id;
            int offset = (tempIndex + TP.get(procId).nLocals) * -4;
            return offset + "(" + BP_LOCAL + ")";
        }

        EntradaVariable v = TV.get(id);
        int prof = v.idProc - 1;
        int dx = v.desp;

        if (v.idProc != procId) {
            asm.append("    MOVE.L DISP+").append(prof * 4).append("," + DISP_REGISTER +"\n");
            return dx + "(" + DISP_REGISTER + ")";
        } else {
            return dx + "(" + BP_LOCAL + ")";
        }
    }

    public void writeToFile(String filename) {
        if(!filename.endsWith(".X68")) {
            filename = filename + ".X68";
        }

        File outputFile = new File(filename);

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(asm.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error escrivint fitxer ASM: " + filename, e);
        }
    }

    public void readOffFile(String filename) {

    }
}
