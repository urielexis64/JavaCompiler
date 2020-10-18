import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;

public class Tabla {

    final int clase = 0, publico = 1, privado = 2, whilex = 3, entero = 4, booleano = 5, caracter = 6, real = 7, llaveizq = 8,
            llaveder = 9, EQ = 10, semi = 11, menor = 12, mayor = 13, d2EQ = 14, menorEQ = 15, mayorEQ = 16, diferente = 17,
            difEQ = 18, truex = 19, falsex = 20, brackizq = 21, brackder = 22, div = 23, mas = 24,
            menos = 25, mult = 26, ifx = 27, letra = 49, num = 50, numReal = 51, ID = 52; //bool = 21,

    ArrayList<Token> tokenRC;
    ArrayList<ValoresTabla> valoresTab = new ArrayList<>();

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

    public Tabla(ArrayList<Token> tokenrc) {

//		public String rango, tipo, nombre, valor, renglon, columna;

        tokenRC = tokenrc;
        String nombre[] = new String[tokenRC.size()];
        int tipo[] = new int[tokenRC.size()];
        String nombreTipo = "";
        String renglon[] = new String[tokenRC.size()];
        String columna[] = new String[tokenRC.size()];

        //desmonto valores del arraylist en arreglos para su uso m�s f�cil

        for (int i = 0; i < tokenrc.size(); i++) {
            nombre[i] = tokenrc.get(i).getToken();
            tipo[i] = tokenrc.get(i).getTipo();
            renglon[i] = String.valueOf(tokenrc.get(i).getRenglon());
            columna[i] = String.valueOf(tokenrc.get(i).getColumna());
        }

        ArrayList<String> duplicados = new ArrayList<>();
        //Asigna valores al arraylist que desplegar� la tabla
       // boolean declaracionDuplicada = false;
        for (int i = 0; i < tokenrc.size(); i++) {

            if (tipo[i] == entero || tipo[i] == booleano || tipo[i] == real || tipo[i] == caracter) {

                if (tipo[i] == entero)
                    nombreTipo = "int";
                else if (tipo[i] == booleano)
                    nombreTipo = "boolean";
                else if (tipo[i] == real)
                    nombreTipo = "float";
                else
                    nombreTipo = "char";

                if (buscaIDDuplicado(tokenRC.get(i + 1).getToken()) )
                    continue;

                if (tipo[i - 1] == publico) {
                    if (nombreTipo.equals("int"))
                        ValoresHaciaTabla("public", nombreTipo, nombre[i + 1], "0", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("boolean"))
                        ValoresHaciaTabla("public", nombreTipo, nombre[i + 1], "false", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("float"))
                        ValoresHaciaTabla("public", nombreTipo, nombre[i + 1], "0.0", renglon[i + 1], columna[i + 1]);
                    else
                        ValoresHaciaTabla("public", nombreTipo, nombre[i + 1], "' '", renglon[i + 1], columna[i + 1]);

                } else if (tipo[i - 1] == privado) {
                    if (nombreTipo.equals("int"))
                        ValoresHaciaTabla("private", nombreTipo, nombre[i + 1], "0", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("boolean"))
                        ValoresHaciaTabla("private", nombreTipo, nombre[i + 1], "false", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("float"))
                        ValoresHaciaTabla("private", nombreTipo, nombre[i + 1], "0.0", renglon[i + 1], columna[i + 1]);
                    else
                        ValoresHaciaTabla("private", nombreTipo, nombre[i + 1], "' '", renglon[i + 1], columna[i + 1]);
                } else {
                    if (nombreTipo.equals("int"))
                        ValoresHaciaTabla("S/M", nombreTipo, nombre[i + 1], "0", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("boolean"))
                        ValoresHaciaTabla("S/M", nombreTipo, nombre[i + 1], "false", renglon[i + 1], columna[i + 1]);
                    else if (nombreTipo.equals("float"))
                        ValoresHaciaTabla("S/M", nombreTipo, nombre[i + 1], "0.0", renglon[i + 1], columna[i + 1]);
                    else
                        ValoresHaciaTabla("S/M", nombreTipo, nombre[i + 1], "' '", renglon[i + 1], columna[i + 1]);
                }

            }

        }
        duplicados.clear();
        //Aqu� a las variables declaradas se les asignan lo valores correspondientes al c�digo en el .txt
        for (int i = 0; i < tokenRC.size(); i++) {

            for (int j = 0; j < valoresTab.size(); j++) {
                if (tokenRC.get(i).getToken().equals(valoresTab.get(j).nombre))
                    if (tipo[i] == ID && tipo[i + 1] == EQ) {

                        if (tipo[i + 3] == mas || tipo[i + 3] == menos || tipo[i + 3] == div || tipo[i + 3] == mult) {
                            valoresTab.set(j, new ValoresTabla(
                                    valoresTab.get(j).rango,
                                    valoresTab.get(j).tipo,
                                    valoresTab.get(j).nombre,
                                    tokenRC.get(i + 2).getToken() + " " + tokenRC.get(i + 3).getToken() + " " + tokenRC.get(i + 4).getToken(),
                                    valoresTab.get(j).renglon,
                                    valoresTab.get(j).columna)
                            );
                        } else {

                            valoresTab.set(j, new ValoresTabla(
                                    valoresTab.get(j).rango,
                                    valoresTab.get(j).tipo,
                                    valoresTab.get(j).nombre,
                                    tokenRC.get(i + 2).getToken(),
                                    valoresTab.get(j).renglon,
                                    valoresTab.get(j).columna)
                            );
                            switch (valoresTab.get(j).tipo) {
                                case "int":
                                    if (!valoresTab.get(j).valor.matches("^[0-9]{1,9}?$"))
                                        valoresTab.remove(j);
                                    break;
                                case "boolean":
                                    if (!valoresTab.get(j).valor.matches("^true|false$"))
                                        valoresTab.remove(j);
                                    break;
                                case "float":
                                    if (!valoresTab.get(j).valor.matches("^[0-9]{0,}[.][0-9]{1,5}?$"))
                                        valoresTab.remove(j);
                                    break;
                                case "char":
                                    if (!valoresTab.get(j).valor.matches("^['][a-zA-Z0-9][']?$"))
                                        valoresTab.remove(j);
                            }

                        }
                    }

            }


        }
        //Imprime la tabla de simbolos con sus datos
        System.out.println("\n" +
                "No." + blancos("no.       ") +
                "Modificador" + blancos("modificador") +
                "Tipo" + blancos("tipo") +
                "Nombre" + blancos("nombre") +
                "Valor" + blancos("valor") +
                "Renglon" + blancos("renglon") +
                "Columna o No. de token" + blancos("columna o No. de token") +
                "\n"
        );
        for (int i = 0; i < valoresTab.size(); i++) {
            System.out.println(
                    (i + 1) + blancos(String.valueOf((i + 1 + "       "))) +
                            valoresTab.get(i).rango + blancos(valoresTab.get(i).rango) +
                            valoresTab.get(i).tipo + blancos(valoresTab.get(i).tipo) +
                            valoresTab.get(i).nombre + blancos(valoresTab.get(i).nombre) +
                            valoresTab.get(i).valor + blancos(valoresTab.get(i).valor) +
                            valoresTab.get(i).renglon + blancos(valoresTab.get(i).renglon) +
                            valoresTab.get(i).columna + blancos(valoresTab.get(i).columna)

            );
        }
        Main.modelo.setRowCount(0);
        for (int i =0; i<valoresTab.size();i++){
            Main.modelo.addRow(new String[]{(i+1)+"",valoresTab.get(i).rango,
                    valoresTab.get(i).tipo,
                    valoresTab.get(i).nombre,
                    valoresTab.get(i).valor,
                    valoresTab.get(i).renglon,
                    valoresTab.get(i).columna});
        }
    }

    public void ValoresHaciaTabla(String ran, String tip, String nom, String val, String reng, String col) {
        valoresTab.add(new ValoresTabla(ran, tip, nom, val, reng, col));
    }

    public String blancos(String cadena) {

        String blancos = "";

        for (int i = cadena.length(); i < 15; i++) {
            blancos += " ";
        }

        return blancos;
    }
}
