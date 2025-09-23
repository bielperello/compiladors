package token;

public enum TokenType {
    // Paraules clau
    OP_IF, OP_THEN, OP_ELSE, OP_ENDIF,
    OP_WHILE, OP_DO, OP_ENDWHILE,
    OP_REP, PROCEDIMENT, FUNCIO, TORNAR,

    // Tipus
    OP_TIPUS_CADENA, OP_TIPUS_TUPLA,
    OP_TIPUS_ENTER, OP_TIPUS_LOGIC,

    // Constants / literals
    CONST, ENTER, VALOR_LOGIC, CADENA,

    // Identificadors
    ID,

    // Operadors aritmètics
    PLUS, MINUS, TIMES, DIVIDE,

    // Operadors relacionals
    EQ, NE, LT, LE, GT, GE,

    // Operadors lògics
    AND, OR, NOT,

    // Altres operadors i símbols
    ASSIGNACIO, // :=
    OBR_PAR,    // (
    TANC_PAR,   // )
    OBR_CORX,   // [
    TANC_CORX,  // ]
    DOS_PUNTS,  // :

    // Miscel·lani
    COMENTARI,
    ERROR
}
