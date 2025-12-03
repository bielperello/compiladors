package assembler;

import codegen.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AssemblerGenerator {
    private final TaulaVariables TV;
    private final TaulaProcediments TP;
    private final List<Instruction> code;

    private int procActual;

    private final StringBuilder asm;

    private final String DISP_REGISTER = "A0";
    private final String BP_LOCAL = "A6";

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
        // Display Vector
        int maxDepth = CodeGenerator.np - 1;
        asm.append("DISP: DS.L ").append(maxDepth + 1).append("\n\n");

        // Heap Pointer
        asm.append("HP: DS.L 1\n\n");

        // Darrera línia de codi
        asm.append("\tEND START");
    }

    private void generateTextSection() {
        asm.append("    ORG $1000\n\n");

        asm.append("START:\n");
        procActual = 1;
        int blockSize = computeActivationBlock(procActual);

        asm.append("    LINK A6,#-").append(blockSize).append("\n");
        asm.append("    MOVE.L DISP").append(",-(A7)\n");
        asm.append("    MOVE.L A6,DISP\n");

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
                default:
                    generateInstruction(instr, procActual);
                    break;
            }
        }

        asm.append("    SIMHALT\n\n");
    }

    private int computeActivationBlock(int idProc) {
        EntradaProcediment proc = CodeGenerator.getProc(idProc);

        int locals = proc.nLocals*4;
        int temps = proc.nTemporals*4;

        int dispSave = 4;

        return locals+temps+dispSave;
    }

    private void generateProcedurePMB(int dest) {
        int blockSize = computeActivationBlock(dest);
        int profunditat = dest - 1;

        asm.append("\n    LINK A6,#-").append(blockSize).append("\n");
        asm.append("    MOVE.L DISP+").append(4*profunditat).append(",-(A7)\n");
        asm.append("    MOVE.L A6,DISP+").append(4*profunditat).append("\n\n");
    }

    private void generateProcedureRTN(int dest) {
        asm.append("\n    MOVE.L -4(A6),DISP+").append(4*(dest-1)).append("\n");
        asm.append("    UNLK A6\n");
        asm.append("    RTS\n\n");
    }

    private void generateInstruction(Instruction instr, int procId) {
        String aDest = gestAddress(instr.dest, procId);
        String aSrc1 = gestAddress(instr.arg1, procId);
        String aSrc2 = gestAddress(instr.arg2, procId);

        switch(instr.op) {
            case COPY:
                if (instr.literal != null) {
                    asm.append("    MOVE.L #").append(instr.literal).append(",").append(aDest).append("\n");
                    break;
                } else {
                    String aSrc = gestAddress(instr.arg1, procId);

                    asm.append("    MOVE.L ").append(aSrc).append(",D0\n");
                    asm.append("    MOVE.L D0,").append(aDest).append("\n");
                    break;
                }
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
            case NEG, NOT:
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
            case IND_ASS,IND_VAL:

                break;
            case PARAM_S, PARAM_C:

                break;
            case CALL:

                break;
            case WRT,READ:

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
