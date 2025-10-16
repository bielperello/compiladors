/**
 * Assignatura 21780 Compiladors
 * Estudis de Grau en Informàtica
 * Professor: Pere Palmer
 *
 * Compilació:
 * java -jar jflex-full-1.9.1.jar -d gen src/gramatica/Lexer.flex
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;

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
    public static void main(String []args) {
        /* if (args.length < 1) {
        System.err.println("Indica un fitxer amb les dades d'entrada");
        System.exit(0);
        }*/

        try (FileReader in = new FileReader("src/valorsidproves.txt")) {
            AnaLex lexer = new AnaLex(in);
            Symbol token;
            while ((token = lexer.next_token()).sym != ParserSym.EOF) {
                System.out.println(token);
            }
        } catch (FileNotFoundException e) {
            System.err.println("El fitxer d'entrada no existeix");;
        } catch (IOException e) {
            System.err.println("Error processant el fitxer d'entrada");
        }
    }

    /**
     Construcció d'un symbol sense atribut associat.
     **/
    private ComplexSymbol symbol(int type) {
        return new ComplexSymbol(ParserSym.terminalNames[type], type,
            new ComplexSymbolFactory.Location(yyline + 1, yycolumn + 1),
            new ComplexSymbolFactory.Location(yyline + 1, yycolumn + yylength()));
    }

    /**
     Construcció d'un symbol amb un atribut associat.
     **/
    private Symbol symbol(int type, Object value) {
        return new ComplexSymbol(ParserSym.terminalNames[type], type,
           new ComplexSymbolFactory.Location(yyline + 1, yycolumn + 1),
           new ComplexSymbolFactory.Location(yyline + 1, yycolumn + yylength()), value);
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
"["                  { return symbol(ParserSym.OBR_CORX); }
"]"                  { return symbol(ParserSym.TANC_CORX); }
","                  { return symbol(ParserSym.COMA);}
";"                  { return symbol(ParserSym.PUNT_COMA); }
"."                  { return symbol(ParserSym.PUNT); }


{op_aritmetic}       {
                          switch (yytext()) {
                              case "+" : return symbol(ParserSym.PLUS);
                              case "-" : return symbol(ParserSym.MINUS);
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
{id}                 { return symbol(ParserSym.ID, this.yytext()); }
{nombre}             { return symbol(ParserSym.ENTER, Double.parseDouble(this.yytext())); }

// ERROR
.                    {
           // Qualsevol caràcter no reconegut arriba aquí
          ErrorManager.add(new CompilerError(
              yyline + 1, yycolumn + 1,
              CompilerError.TYPE.LEXIC,
              "Símbol desconegut: '" + yytext() + "'"
          ));
          // Pots retornar null o ignorar-lo segons la teva estratègia
          return null;
      }