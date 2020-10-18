import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Sintactico<T> {
    ArrayList<Token> tokenRC;

    ArrayList<String> resultadoSintactico = new ArrayList<>();
    ArrayList<String> resultadoSemantico = new ArrayList<>();
    String tok = "", esperado = "";
    int type, contando = 0;
    String estructura = "";

    final String cadenas[] = {"class", "public", "private", "while", "int", "boolean", "char", "float", "{", "}", "=", ";", "<", ">",   //12... Aunque no se usa como tal el "!" solo, sirve para que no lance error
            "==", "<=", ">=", "!", "!=", "true", "false", "(", ")", "/", "+", "-", "*", "if"};

    final int clase = 0, publico = 1, privado = 2, whilex = 3, entero = 4, booleano = 5, caracter = 6, real = 7, llaveizq = 8,
            llaveder = 9, EQ = 10, semi = 11, menor = 12, mayor = 13, d2EQ = 14, menorEQ = 15, mayorEQ = 16, diferente = 17,
            difEQ = 18, truex = 19, falsex = 20, brackizq = 21, brackder = 22, div = 23, mas = 24,
            menos = 25, mult = 26, ifx = 27, letra = 49, num = 50, numReal = 51, ID = 52; //letra hace referencia a un caracter ('A')

    //	public Sintactico(ArrayList<String> token, ArrayList<Integer> tipo)
    public Sintactico(ArrayList<Token> tokenRC) {
        this.tokenRC = tokenRC;
//		this.token = token;
//		this.tipo = tipo; 
        try {

            this.tok = this.tokenRC.get(0).getToken();
            this.type = this.tokenRC.get(0).getTipo();
//			this.type = this..get(0);
//			this.tok = this.token.get(0);
        } catch (Exception e) {
            System.out.println("El archivo está vacío");
        }
        Programa();
    }

    public int buscaID(String id) {
        int contador = 0;

        ArrayList<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < tokenRC.size(); i++) {
            if (tokenRC.get(i).getToken().equals(id)) {
                contador++;
                posiciones.add(i);
            }
        }
        return contador > 1 ? posiciones.get(0) : -1;
    }

    public boolean buscaIDDuplicado(String ID) {
        int contador = 0;
        for (int i = 0; i < tokenRC.size(); i++) {
            if (tokenRC.get(i).getToken().equals(ID)
                    && (tokenRC.get(i - 1).getTipo() == entero
                    || tokenRC.get(i - 1).getTipo() == booleano
                    || tokenRC.get(i - 1).getTipo() == caracter
                    || tokenRC.get(i - 1).getTipo() == real)) {
                contador++;
            }
        }
        return contador > 1;
    }


    public void Advance() {
        type = tokenRC.get(contando).getTipo();
        tok = tokenRC.get(contando).getToken();
    }

    public void eat(int esperado) {
        if (type == esperado) {
            if (++contando < tokenRC.size()) {
                Advance();
            }
        } else {
            error(esperado);
        }
    }

    public void Programa() {
        if (type == publico || type == privado)
            eat(type);
        eat(clase);
        eat(ID);

        eat(llaveizq);

        while (type == publico || type == privado) {
            eat(type);
            Declaracion();
        }
        while (type == publico || type == privado || type == entero || type == booleano || type == real || type == caracter || type == ID) {
            if (type == ID) {
                int indiceID = buscaID(tokenRC.get(contando).getToken());
                if (indiceID == -1) { //SI ENTRA AQUÍ ES PORQUE NO HA SIDO DECLARADO EL ID
                    errorSemantico(-1, ID);
                    eat(ID);
                    eat(EQ);
                    eat(type);
                    eat(semi);
                } else {
                    eat(ID);
                    eat(EQ);
                    switch (tokenRC.get(indiceID - 1).getTipo()) {
                        case entero:
                            if (type != num) {
                                int tipoEncontrado = tokenRC.get(contando).getTipo();
                                errorSemantico(4, tipoEncontrado == 49 ? 6 : (tipoEncontrado == 19 || tipoEncontrado == 20) ? 5 : tipoEncontrado == 51 ? 7 : tipoEncontrado);
                            }
                            eat(type);
                            break;
                        case booleano:
                            if (type != truex && type != falsex) {
                                int tipoEncontrado = tokenRC.get(contando).getTipo();
                                errorSemantico(5, tipoEncontrado == 49 ? 6 : tipoEncontrado == 50 ? 4 : tipoEncontrado == 51 ? 7 : tipoEncontrado);
                            }
                            eat(type);
                            break;
                        case real:
                            if (type != numReal) {
                                int tipoEncontrado = tokenRC.get(contando).getTipo();
                                errorSemantico(7, tipoEncontrado == 49 ? 6 : tipoEncontrado == 50 ? 4 : (tipoEncontrado == 19 || tipoEncontrado == 20) ? 5 : tipoEncontrado);
                            }
                            eat(type);
                            break;
                        case caracter:
                            if (type != letra) {
                                int tipoEncontrado = tokenRC.get(contando).getTipo();
                                errorSemantico(6, (tipoEncontrado == 19 || tipoEncontrado == 20) ? 5 : tipoEncontrado == 50 ? 4 : tipoEncontrado == 51 ? 7 : tipoEncontrado);
                            }
                            eat(type);
                    }
                    eat(semi);
                }
            } else if (type == publico || type == privado) eat(type);
            else
                Declaracion();
        }

        if (this.type == whilex || this.type == ifx)
            Statuto();

        eat(llaveder);

        if (contando < tokenRC.size())
            error(1);
        estructura = "estructura correcta";

    }

    Hashtable<String, Integer> duplicados = new Hashtable<>();
    int contador = 0;
    public void verificaDeclaracionesDuplicadas() {
        if (duplicados.containsKey(tokenRC.get(contando).getToken())) {
            errorSemantico(-2, duplicados.get(tokenRC.get(contando).getToken()));
            return;
        }

        if (buscaIDDuplicado(tokenRC.get(contando).getToken()) && !declaracionDuplicada) {
            duplicados.put(tokenRC.get(contando).getToken(), tokenRC.get(contando).getRenglon());

            contador++;
        }
    }

    boolean declaracionDuplicada = false;
    String tokenDuplicado = "";
    int lineaPrimeraDeclaracion = 0;

    public void Declaracion() {
        String tok;
        switch (type) {
            case entero:
                eat(entero);
                tok = this.tok;

                verificaDeclaracionesDuplicadas();

                eat(ID);
                if (type == EQ) {
                    eat(EQ);
                    if (type == truex || type == falsex) {
                        errorSemantico(entero, type);
                        eat(type);
                    } else if (type == letra) {
                        errorSemantico(entero, type);
                        eat(letra);
                    } else if (type == numReal) {
                        errorSemantico(entero, type);
                        eat(numReal);
                    } else
                        eat(num);
                }
                eat(semi);
                break;
            case booleano:
                eat(booleano); //boolean
                tok = this.tok;
                verificaDeclaracionesDuplicadas();
                eat(ID); //ide
                if (type == EQ) {
                    eat(EQ); //=
                    if (type == letra) {
                        errorSemantico(booleano, type);
                        eat(letra);
                    } else if (type == numReal) {
                        errorSemantico(booleano, type);
                        eat(numReal);
                    } else if (type == num) {
                        errorSemantico(booleano, type);
                        eat(num);
                    } else
                        eat(type);
                }
                eat(semi);
                break;
            case real:
                eat(real);
                tok = this.tok;
                verificaDeclaracionesDuplicadas();
                eat(ID);
                if (type == EQ) {
                    eat(EQ);

                    if (type == letra) {
                        errorSemantico(real, type);
                        eat(letra);
                    } else if (type == num) {
                        errorSemantico(real, type);
                        eat(num);
                    } else if (type == truex || type == falsex) {
                        errorSemantico(real, type);
                        eat(type);
                    } else
                        eat(numReal);
                }
                eat(semi);
                break;
            case caracter:
                eat(caracter);
                tok = this.tok;
                verificaDeclaracionesDuplicadas();
                eat(ID);
                if (type == EQ) {
                    eat(EQ);

                    if (type == num) {
                        errorSemantico(caracter, type);
                        eat(num);
                    } else if (type == truex || type == falsex) {
                        errorSemantico(caracter, type);
                        eat(type);
                    } else if (type == numReal) {
                        errorSemantico(caracter, type);
                        eat(numReal);
                    } else
                        eat(letra);
                }
                eat(semi);
                break;
        }
    }

    public void VarDeclarator() {
        eat(EQ);

        if (type == num)
            eat(num);

        if (type == falsex)
            eat(falsex);

        if (type == truex)
            eat(truex);
    }

    public void Statuto() {
        switch (type) {
            case ifx:
                eat(ifx);
                eat(brackizq);

                TestingExp();
                eat(brackder);

                eat(llaveizq);

                while (type == whilex || type == ifx || type == ID || type == booleano || type == entero || type == real || type == caracter)
                    Statuto(); //para llamar otro statement dentro del statement
                eat(llaveder);

                break;

            case whilex:
                eat(whilex);
                eat(brackizq);

                TestingExp();
                eat(brackder);
                eat(llaveizq);
                while (type == whilex || type == ifx || type == booleano || type == entero || type == real || type == caracter || type == publico || type == privado)
                    Statuto(); //para llamar otro statement dentro del statement
                eat(llaveder);
                break;
            case ID:
                eat(ID);
                eat(EQ);

                ArithmeticExp();
                eat(semi);
                while (type == whilex || type == ifx || type == booleano || type == entero || type == real || type == caracter)
                    Statuto(); //para llamar otro statement dentro del statement
                break;
            case booleano:
            case entero:
            case real:
            case caracter:
                Declaracion();
                break;
            case publico:
                eat(publico);
                Declaracion();
                break;
            case privado:
                eat(privado);
                Declaracion();
                break;
            default:
                error();
        }
    }

    public void TestingExp() {

        switch (type) {
            case ID:
                if (type == ID) {
                    eat(ID);
                } else if (type == num)
                    eat(num);

                if (LogicSimbols())
                    if (type == ID) {
                        eat(ID);
                    } else if (type == num)
                        eat(num);
                break;
            default:
                error();
                break;
        }
    }

    public void ArithmeticExp() {

        switch (type) {
            case num:

                eat(num);

            {
                if (OperandoSimbols())
                    eat(num);
            }

            break;
            default:
                error();
                break;
        }
    }

    public void error(int type) {
        try {
            String tipo = ValoresInversos(type);
            if (type == 0)
                resultadoSintactico.add("\nError sintáctico, se esperaba una expresión **class** al comienzo");
            else if (type == 1)
                resultadoSintactico.add("\nError sintáctico en los límites, se encontró al menos un token después de la última llave cerrada, token ** " + tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna() + " **");
            else if (type == 2)
                resultadoSintactico.add("\nError sintáctico en asignación, se esperaba un operador y operando antes de ** " + tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna() + " **");
            else if (type == 3)
                resultadoSintactico.add("\nError sintáctico en validación, se esperaba un operador lógico en lugar de ** " + tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna() + " **");
            else
                resultadoSintactico.add("\nError sintáctico en token ** " + tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna() + " ** se esperaba un token ** " + tipo + " **");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void errorSemantico(int tipoEsperado, int tipoEncontrado) {
        try {
            if (tipoEsperado == -1) {
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", la variable **" + tokenRC.get(contando).getToken() + "** no ha sido declarada");
                return;
            }

            if (tipoEsperado == -2) {
                resultadoSemantico.add("\nError semántico: Se encontró variable duplicada **" + tokenRC.get(contando).getToken() + "** en la línea " + tokenRC.get(contando).getRenglon() + "\nPrimera vez declarada en línea: " + tipoEncontrado);
                return;
            }

            if (tipoEncontrado == 4 ||
                    tipoEncontrado == 5 ||
                    tipoEncontrado == 6 ||
                    tipoEncontrado == 7)
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", se esperaba un dato de tipo **" + cadenas[tipoEsperado] + "**, se está intentando asignar uno de tipo **" + cadenas[tipoEncontrado] + "**");

            if (tipoEncontrado == 49)
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", se esperaba un dato de tipo **" + cadenas[tipoEsperado] + "**, se está intentando asignar uno de tipo **char**");
            else if (tipoEncontrado == 51)
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", se esperaba un dato de tipo **" + cadenas[tipoEsperado] + "**, se está intentando asignar uno de tipo **float**");
            else if (tipoEncontrado == 50)
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", se esperaba un dato de tipo **" + cadenas[tipoEsperado] + "**, se está intentando asignar uno de tipo **int**");
            else if (tipoEncontrado == 19 || tipoEncontrado == 20)
                resultadoSemantico.add("\nError semántico de asignación en la linea " + tokenRC.get(contando).getRenglon() + ", se esperaba un dato de tipo **" + cadenas[tipoEsperado] + "**, se está intentando asignar uno de tipo **boolean**");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error() {
        resultadoSintactico.add("Error en la sintaxis, con el siguiente token ** " + tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna() + " **");
    }

    public boolean LogicSimbols() {
        if (type == menor || type == mayor || type == menorEQ || type == mayorEQ || type == d2EQ/*type == mayor || type == dobleEQ ||*/) {
            eat(type);
            return true;
        } else
            error(3);
        return false;
    }

    public boolean OperandoSimbols() {
        if (type == menos || type == mas || type == div || type == mult) {
            eat(type);
            return true;
        } else
            error(2);
        return false;
    }

    public String ValoresInversos(int type) {
        String devuelve;
        if (type == 49)
            devuelve = "caracter";
        else if (type == 50)
            devuelve = "entero";
        else if (type == 51)
            devuelve = "real";
        else if (type == 52)
            devuelve = "identificador";
        else
            devuelve = cadenas[type];

        return devuelve;
    }
}