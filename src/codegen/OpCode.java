package codegen;

public enum OpCode {
    COPY, COPY_RTN, READ_RTN,
    ADD, SUB, PROD, DIV, NEG,
    AND, OR, NOT, CONCAT,
    IND_VAL, IND_ASS,
    IF_LT, IF_LE, IF_EQ, IF_NE, IF_GE, IF_GT,
    GOTO, SKIP,
    PARAM_S, PARAM_C, CALL, RTN, PMB,
    WRT, READ
}
