package codegen;

public enum OpCode {
    COPY, ADD, SUB, PROD, DIV, NEG,
    AND, OR, NOT,
    IND_VAL, IND_ASS,
    IF_LT, IF_LE, IF_EQ, IF_NE, IF_GE, IF_GT,
    GOTO, SKIP,
    PARAM_S, PARAM_C, CALL, RTN, PMB,
    WRT, READ
}
