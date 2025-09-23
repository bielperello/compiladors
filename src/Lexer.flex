/**
 * Assignatura 21780 Compiladors
 * Estudis de Grau en Informàtica
 * Professor: Pere Palmer
 *
 * Compilació:
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import token.TokenType;
import token.Yytoken.Nombre;
import token.Yytoken.TSimple;
import token.Yytoken.ValorLogic;
import token.Yytoken;
%%

%public
%class AnaLex
%unicode
%line
%column

//DECLARACIONS

espai           = [ \t\n\r]+
id              = [a-zA-Z_][a-zA-Z_0-9]*
op_aritmetic    = ("+"|"-"|"/"|"*")
op_rel          = ("<="|">="|"!="|"=="|"<"|">")
op_logic        = (i|o|no)
valor_logic     = (cert|fals)
nombre          = [0-9]+(\.[0-9]+)?([Ee][+-]?[0-9]+)?

comentari = ##.*

%{
    public static void main(String []args) {
       /* if (args.length < 1) {
            System.err.println("Indica un fitxer amb les dades d'entrada");
            System.exit(0);
        }*/
        try {
            FileReader in = new FileReader("src/valorsidproves.txt");
            AnaLex lexer = new AnaLex(in);
            Yytoken token;

            while((token = lexer.yylex()) != null) {
                System.out.println(token);
            }
        } catch (FileNotFoundException e) {
            System.err.println("El fitxer d'entrada no existeix");;
        } catch (IOException e) {
            System.err.println("Error processant el fitxer d'entrada");
        }
    }
%}

/* Patrons i accions */
%%
// ESPAIS i COMENTARIS
{espai}              {/* Ignorar */ }
{comentari}          { return new TSimple(TokenType.COMENTARI, yytext(), yyline, yycolumn); }

// OPERADORS
":="                 { return new Yytoken(TokenType.ASSIGNACIO, yyline, yycolumn); }
":"                  { return new Yytoken(TokenType.DOS_PUNTS, yyline, yycolumn); }
"("                  { return new Yytoken(TokenType.OBR_PAR, yyline, yycolumn); }
")"                  { return new Yytoken(TokenType.TANC_PAR, yyline, yycolumn); }
"["                  { return new Yytoken(TokenType.OBR_CORX, yyline, yycolumn); }
"]"                  { return new Yytoken(TokenType.TANC_CORX, yyline, yycolumn); }


{op_aritmetic}       {
                          switch (yytext()) {
                              case "+" : return new Yytoken(TokenType.PLUS, yyline, yycolumn);
                              case "-" : return new Yytoken(TokenType.MINUS, yyline, yycolumn);
                              case "*" : return new Yytoken(TokenType.TIMES, yyline, yycolumn);
                              case "/" : return new Yytoken(TokenType.DIVIDE, yyline, yycolumn);
                          }
                      }

/* Operadors relacionals */
{op_rel}             {
                          switch (yytext()) {
                              case "=="  : return new Yytoken(TokenType.EQ, yyline, yycolumn);
                              case "!=" : return new Yytoken(TokenType.NE, yyline, yycolumn);
                              case "<"  : return new Yytoken(TokenType.LT, yyline, yycolumn);
                              case "<=" : return new Yytoken(TokenType.LE, yyline, yycolumn);
                              case ">"  : return new Yytoken(TokenType.GT, yyline, yycolumn);
                              case ">=" : return new Yytoken(TokenType.GE, yyline, yycolumn);
                          }
                      }

/* Operadors lògics */
{op_logic}           {
                          switch (yytext()) {
                              case "i"  : return new Yytoken(TokenType.AND, yyline, yycolumn);
                              case "o"  : return new Yytoken(TokenType.OR, yyline, yycolumn);
                              case "no" : return new Yytoken(TokenType.NOT, yyline, yycolumn);
                          }
                      }

// PARAULES CLAU
"si"                 { return new Yytoken(TokenType.OP_IF, yyline, yycolumn); }
"llavors"            { return new Yytoken(TokenType.OP_THEN, yyline, yycolumn); }
"sino"               { return new Yytoken(TokenType.OP_ELSE, yyline, yycolumn); }
"fsi"                { return new Yytoken(TokenType.OP_ENDIF, yyline, yycolumn); }
"mentre"             { return new Yytoken(TokenType.OP_WHILE, yyline, yycolumn); }
"fer"                { return new Yytoken(TokenType.OP_DO, yyline, yycolumn); }
"fmentre"            { return new Yytoken(TokenType.OP_ENDWHILE, yyline, yycolumn); }
"repetir"            { return new Yytoken(TokenType.OP_REP, yyline, yycolumn); }
"procediment"        { return new Yytoken(TokenType.PROCEDIMENT, yyline, yycolumn); }
"funcio"             { return new Yytoken(TokenType.FUNCIO, yyline, yycolumn); }
"tornar"             { return new Yytoken(TokenType.TORNAR, yyline, yycolumn); }
"cadena"             { return new Yytoken(TokenType.OP_TIPUS_CADENA, yyline, yycolumn); }
"tupla"              { return new Yytoken(TokenType.OP_TIPUS_TUPLA, yyline, yycolumn); }
"enter"              { return new Yytoken(TokenType.OP_TIPUS_ENTER, yyline, yycolumn); }
"logic"              { return new Yytoken(TokenType.OP_TIPUS_LOGIC, yyline, yycolumn); }
"const"              { return new Yytoken(TokenType.CONST, yyline, yycolumn); }

// VALORS i IDENTIFICADORS
{valor_logic}        { return new ValorLogic(yytext().equals("cert"), yyline, yycolumn);}
{id}                 { return new TSimple(TokenType.ID, yytext(), yyline, yycolumn); }
{nombre}             { return new Nombre(Double.parseDouble(yytext()), yyline, yycolumn); }

// ERROR
.                    { return new TSimple(TokenType.ERROR, yytext(), yyline, yycolumn); }