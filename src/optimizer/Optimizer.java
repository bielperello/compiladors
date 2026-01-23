package optimizer;

import codegen.*;

import java.util.ArrayList;
import java.util.List;

public class Optimizer {

    public static List<Instruction> optimize() {
        List<Instruction> code = CodeGenerator.getCode();
        List<Instruction> out = new ArrayList<>(code.size());

        int i = 0;
        while (i < code.size()) {
            Instruction a = code.get(i);

            // Brancaments adjacents:
            // IF cond -> L1 ; GOTO L2 ; SKIP L1  ==>  IF !cond -> L2 ; SKIP L1
            if (esBrancaCondicional(a) && i + 2 < code.size()) {
                Instruction b = code.get(i + 1);
                Instruction c = code.get(i + 2);

                if (b.op == OpCode.GOTO && c.op == OpCode.SKIP && a.dest == c.dest) {
                    Instruction inv = invertirBranca(a, b.dest); // mateix IF però condició invertida i dest = L2
                    out.add(inv);
                    out.add(cloneInstruction(c));
                    i += 3;
                    continue;
                }
            }


            // Jump threading: GOTO L1 ; SKIP L1 ; GOTO L2  =>  GOTO L2 ; SKIP L1 ; GOTO L2
            if (a.op == OpCode.GOTO && i + 2 < code.size()) {
                Instruction b = code.get(i + 1);
                Instruction c = code.get(i + 2);

                if (b.op == OpCode.SKIP && c.op == OpCode.GOTO && a.dest == b.dest) {
                    Instruction redirected = new Instruction(
                            OpCode.GOTO, CodeGenerator.NUL_VAL, CodeGenerator.NUL_VAL, c.dest
                    );

                    out.add(redirected);
                    out.add(cloneInstruction(b));
                    out.add(cloneInstruction(c));
                    i += 3;
                    continue;
                }
            }


            // GOTO L ; SKIP L  => eliminar GOTO
            if (a.op == OpCode.GOTO && i + 1 < code.size()) {
                Instruction b = code.get(i + 1);

                if (b.op == OpCode.SKIP && a.dest == b.dest) {
                    out.add(cloneInstruction(b));
                    i += 2;
                    continue;
                }
            }
    

            // Eliminació de codi mort després d'un GOTO
            if (a.op == OpCode.GOTO) {
                out.add(cloneInstruction(a));

                // Saltam fins al primer SKIP (que pot ser una entrada de flux)
                do {
                    i++;
                } while (i < code.size() && code.get(i).op != OpCode.SKIP);
                continue;
            }


            // Literal propagation:
            //     t = literal ; x = t  =>  x = literal   (i eliminam "t = literal" si t no s'usa més)
            if (a.op == OpCode.COPY && a instanceof InstructionLiteral && i + 1 < code.size()) {
                Instruction b = code.get(i + 1);

                if (b.op == OpCode.COPY && !(b instanceof InstructionLiteral)) {
                    int t = a.dest;

                    if (t < 0 && b.arg1 == t) {
                        InstructionLiteral il = (InstructionLiteral) a;
                        Instruction newCopy = new InstructionLiteral(OpCode.COPY, il.literal, b.dest);

                        if (isTempUsedLaterInSameProcedure(code, i + 2, t)) {
                            out.add(cloneInstruction(a));
                        }
                        out.add(newCopy);

                        i += 2;
                        continue;
                    }
                }
            }

            // Copy propagation:
            //     COPY x -> t ; COPY t -> y  =>  COPY x -> y   (i eliminam la primera si t no s'usa més)
            if (a.op == OpCode.COPY && !(a instanceof InstructionLiteral) && i + 1 < code.size()) {
                Instruction b = code.get(i + 1);

                if (b.op == OpCode.COPY && !(b instanceof InstructionLiteral)) {
                    int t = a.dest;
                    int x = a.arg1;
                    int y = b.dest;

                    if (t < 0 && b.arg1 == t) {
                        Instruction newCopy = new Instruction(OpCode.COPY, x, CodeGenerator.NUL_VAL, y);

                        if (isTempUsedLaterInSameProcedure(code, i + 2, t)) {
                            out.add(cloneInstruction(a));
                        }
                        out.add(newCopy);

                        i += 2;
                        continue;
                    }
                }
            }

            out.add(cloneInstruction(a));
            i++;
        }

        return out;
    }

    private static boolean esBrancaCondicional(Instruction in) {
        return switch (in.op) {
            case IF_EQ, IF_NE, IF_LT, IF_LE, IF_GT, IF_GE -> true;
            default -> false;
        };
    }

    private static Instruction invertirBranca(Instruction a, int nouDesti) {
        OpCode invOp = switch (a.op) {
            case IF_EQ -> OpCode.IF_NE;
            case IF_NE -> OpCode.IF_EQ;
            case IF_LT -> OpCode.IF_GE;
            case IF_LE -> OpCode.IF_GT;
            case IF_GT -> OpCode.IF_LE;
            case IF_GE -> OpCode.IF_LT;
            default -> throw new IllegalArgumentException("Not a conditional branch: " + a.op);
        };

        // Mantenim operands (arg1/arg2) i canviam op i dest
        return new Instruction(invOp, a.arg1, a.arg2, nouDesti);
    }


    private static boolean isTempUsedLaterInSameProcedure(List<Instruction> code, int fromIndex, int tX) {
        for (int i = fromIndex; i < code.size(); i++) {
            Instruction in = code.get(i);

            if (in.op == OpCode.RTN || in.op == OpCode.PMB) break;

            if (in.arg1 == tX) return true;

            // arg2 NO és operand per IND_* (és offset)
            if (in.op != OpCode.IND_VAL && in.op != OpCode.IND_ASS) {
                if (in.arg2 == tX) return true;
            }

            if (in.op == OpCode.PARAM_S && in.dest == tX) return true;
            if (in.op == OpCode.COPY_RTN && in.dest == tX) return true;
        }
        return false;
    }

    private static Instruction cloneInstruction(Instruction in) {
        if (in instanceof InstructionLiteral il) {
            return new InstructionLiteral(il.op, il.literal, il.dest);
        }
        if (in instanceof InstructionIO io) {
            return new InstructionIO(io.op, io.dest, io.tsbIO);
        }

        Instruction c = new Instruction(in.op, in.arg1, in.arg2, in.dest);
        c.literal = in.literal;
        return c;
    }
}
