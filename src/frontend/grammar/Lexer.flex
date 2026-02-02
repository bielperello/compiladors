/**
 * Assignatura 21780 Compiladors
 * Estudis de Grau en Informàtica
 * Professor: Pere Palmer
 *
 * Compilació:
 * java -jar jflex-full-1.9.1.jar -d src/frontend/lexer src/frontend/grammar/Lexer.flex
 */
package frontend.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import frontend.parser.ParserSym;

import java_cup.runtime.*;

import errors.ErrorManager;
import errors.CompilerError;
%%

%public
%class AnaLex
%unicode
%cup
%line
%column

%eofval{
  return symbol(ParserSym.EOF);
%eofval}

//DECLARACIONS

espai           = [ \t\n\r]+
id              = [a-zA-Z_][a-zA-Z_0-9]*
op_aritmetic    = ("+"|"-"|"/"|"*")
op_rel          = ("<="|">="|"!="|"=="|"<"|">")
op_logic        = (no|i|o)
valor_logic     = (cert|fals)
nombre          = [0-9]+(\.[0-9]+)?([Ee][+-]?[0-9]+)?
cadena          =  \"[^\"]*\"
caracter        = \'([^\'\\]|\\.)\'

comentari = ##.*

%{
  private final java.util.Set<String> userTypes = new java.util.HashSet<>();

  public void addUserType(String name) {
      userTypes.add(name);
  }


  private boolean isUserType(String name) {
      return userTypes.contains(name);
  }


  // ===== Dump de tokens =====
    private final StringBuilder tokenDump = new StringBuilder();
    private boolean headerPrinted = false;
    private int tokenIndex = 0;

    public String getTokenDump() {
        return tokenDump.toString();
    }

    private void logToken(Symbol s, String lexema) {
        if (!headerPrinted) {
            tokenDump.append("--- Fitxer de Tòkens ---\n\n");
            tokenDump.append(String.format(
                    " %3s | %-18s | %-10s | %-12s | %s%n",
                    "#", "token", "posició", "lexema", "valor"
            ));
            tokenDump.append(String.format(
                    " %3s-+-%-18s-+-%-10s-+-%-12s-+-%s%n",
                    "---", "------------------", "----------", "------------", "----------------"
            ));
            headerPrinted = true;
        }

        String tokenName;
        try {
            tokenName = (s.sym >= 0 && s.sym < ParserSym.terminalNames.length)
                    ? ParserSym.terminalNames[s.sym]
                    : ("#" + s.sym);
        } catch (Throwable t) {
            tokenName = "#" + s.sym;
        }

        String valueStr = (s.value == null) ? "-" : s.value.toString();
        String lexStr = (lexema == null || lexema.isEmpty()) ? "-" : lexema;

        tokenDump.append(String.format(
                " %3d | %-18s | %3d:%-6d | %-12s | %s%n",
                ++tokenIndex,
                tokenName,
                s.left, s.right,
                lexStr,
                valueStr
        ));
    }

    /** Sense atribut associat */
    private Symbol symbol(int type) {
        Symbol s = new Symbol(type);
        s.left = yyline + 1;        // línia (1-based)
        s.right = yycolumn + 1;     // columna (1-based)

        logToken(s, yytext());      // <-- AFEGIT
        return s;
    }

    /** Amb atribut associat */
    private Symbol symbol(int type, Object value) {
        Symbol s = new Symbol(type, value);
        s.left = yyline + 1;
        s.right = yycolumn + 1;

        logToken(s, yytext());      // <-- AFEGIT
        return s;
    }
%}

/* Patrons i accions */
%%
// ESPAIS i COMENTARIS
{espai}              {/* Ignorar */ }
{comentari}          {/* Ignorar */}

// OPERADORS
":="                 { return symbol(ParserSym.ASSIGNACIO);}
":"                  { return symbol(ParserSym.DOS_PUNTS); }
"("                  { return symbol(ParserSym.OBR_PAR); }
")"                  { return symbol(ParserSym.TANC_PAR); }
"{"                  { return symbol(ParserSym.OBR_CLAU); }
"}"                  { return symbol(ParserSym.TANC_CLAU); }
","                  { return symbol(ParserSym.COMA);}
"."                  { return symbol(ParserSym.PUNT); }
"-"                  { return symbol(ParserSym.MENYS); }


{op_aritmetic}       {
                          switch (yytext()) {
                              case "+" : return symbol(ParserSym.PLUS);
                              case "*" : return symbol(ParserSym.TIMES);
                              case "/" : return symbol(ParserSym.DIVIDE);
                          }
                      }

/* Operadors relacionals */
{op_rel}             {
                          switch (yytext()) {
                              case "=="  : return symbol(ParserSym.EQ);
                              case "!=" : return symbol(ParserSym.NE);
                              case "<"  : return symbol(ParserSym.LT);
                              case "<=" : return symbol(ParserSym.LE);
                              case ">"  : return symbol(ParserSym.GT);
                              case ">=" : return symbol(ParserSym.GE);
                          }
                      }

/* Operadors lògics */
{op_logic}           {
                          switch (yytext()) {
                              case "i"  : return symbol(ParserSym.AND);
                              case "o"  : return symbol(ParserSym.OR);
                              case "no" : return symbol(ParserSym.NOT);
                          }
                      }

// PARAULES CLAU
"principal"          { return symbol(ParserSym.PRINCIPAL); }
"fprincipal"         { return symbol(ParserSym.ENDPRINCIPAL); }
"si"                 { return symbol(ParserSym.OP_IF); }
"llavors"            { return symbol(ParserSym.OP_THEN); }
"sino"               { return symbol(ParserSym.OP_ELSE); }
"fsi"                { return symbol(ParserSym.OP_ENDIF); }
"cas"                { return symbol(ParserSym.OP_SWITCH); }
"quan"               { return symbol(ParserSym.OP_CASE); }
"altre"              { return symbol(ParserSym.DEFAULT_SWITCH); }
"fcas"               { return symbol(ParserSym.OP_ENDSWITCH); }
"mentre"             { return symbol(ParserSym.OP_WHILE); }
"fer"                { return symbol(ParserSym.OP_DO); }
"fmentre"            { return symbol(ParserSym.OP_ENDWHILE); }
"repetir"            { return symbol(ParserSym.OP_REP); }
"procediment"        { return symbol(ParserSym.PROCEDIMENT); }
"fiprocediment"      { return symbol(ParserSym.ENDPROCEDIMENT);}
"funcio"             { return symbol(ParserSym.FUNCIO); }
"fifuncio"           { return symbol(ParserSym.ENDFUNCIO); }
"tornar"             { return symbol(ParserSym.TORNAR); }
"cadena"             { return symbol(ParserSym.OP_TIPUS_CADENA); }
"caracter"           { return symbol(ParserSym.OP_TIPUS_CARACTER); }
"tupla"              { return symbol(ParserSym.OP_TIPUS_TUPLA); }
"enter"              { return symbol(ParserSym.OP_TIPUS_ENTER); }
"logic"              { return symbol(ParserSym.OP_TIPUS_LOGIC); }
"const"              { return symbol(ParserSym.CONST); }
"entrada"            { return symbol(ParserSym.INPUT); }
"sortida"            { return symbol(ParserSym.OUTPUT); }
"inici"              { return symbol(ParserSym.INICI); }
"fi"                 { return symbol(ParserSym.FI); }


// VALORS i IDENTIFICADORS
{cadena}            {
                       String lexema = yytext();
                       // lleva les cometes
                       lexema = lexema.substring(1, lexema.length()-1);
                       return symbol(ParserSym.CADENA, lexema);
                     }
{caracter}           { String lexema = yytext();

                        lexema = lexema.substring(1, lexema.length() - 1);
                        char car;

                        if(lexema.length() == 1) {
                            car = lexema.charAt(0);
                        } else if (lexema.startsWith("\\")) {
                            // Seqüència escapada, com '\n', '\t', '\''
                            switch (lexema.charAt(1)) {
                                case 'n': car = '\n'; break;
                                case 't': car = '\t'; break;
                                case 'r': car = '\r'; break;
                                case '\'': car = '\''; break;
                                case '\\': car = '\\'; break;
                                default:
                                    throw new Error("Escape no reconegut: " + lexema);
                            }
                        } else {
                            throw new Error("Caràcter invàlid: " + lexema);
                        }

                        return symbol(ParserSym.CARACTER, car);
                     }
{valor_logic}        { return symbol(ParserSym.VALOR_LOGIC, this.yytext().equals("cert"));}
{id}                 {
                        String lex = yytext();
                        if (isUserType(lex)) return symbol(ParserSym.TYPE_ID, lex);
                        return symbol(ParserSym.ID, lex);
                     }
{nombre}             { return symbol(ParserSym.ENTER, Double.parseDouble(this.yytext())); }

// ERROR
. {
    ErrorManager.add(new CompilerError(
        yyline + 1, yycolumn + 1,
        CompilerError.TYPE.LEXIC,
        "Símbol desconegut: '" + yytext() + "'"
    ));

    return next_token();
}
