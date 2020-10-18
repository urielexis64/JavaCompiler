import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexico
{		
    int renglon = 1,columna = 1, col2 = 0, cont = 0,  contador = -1;
    int retEQ = 0, retMayEQ = 0, retMenEQ = 0, retDif = 0;
    boolean bandera = true;
    ArrayList<String> resultado = new ArrayList<>();
	ArrayList<Token> tokenRC = new ArrayList<Token>();

	public Lexico(String ruta)
	{
		analizaCodigo(ruta);
		if(bandera) /*{*/
			resultado.add("No hay errores lexicos"); 
//		for(int i=0; i<every_token.size();i++) { System.out.println(every_token.get(i));	}
	}
	public void analizaCodigo(String ruta)
	{	
		String linea = "", token = "";
		StringTokenizer tokenizer;
		try{
			FileReader file = new FileReader(ruta);
			BufferedReader archivoEntrada = new BufferedReader(file);
			linea = archivoEntrada.readLine();
			
			while (linea != null){
				columna = 0;
				linea = espacios(linea);
				tokenizer = new StringTokenizer(linea);
				while(tokenizer.hasMoreTokens()) //DENTRO DE ESTE WHILE, EN EL METODO analizadorLexico SE MANDA CADA TOKEN, CADA PALABRA
				{				
					columna++;
					token = tokenizer.nextToken();
					analizadorLexico(token);
				}
				linea=archivoEntrada.readLine();
				renglon++;
			}
			archivoEntrada.close();
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null,"No se encontro el archivo favor de checar la ruta","Alerta",JOptionPane.ERROR_MESSAGE);
		}
	}
	public void analizadorLexico(String token) 
	{
		token = Junta(token);
		if(token.equals("==") || token.equals(">=") || token.equals("<=") || token.equals("!="))
		{
			return;
		}
		String cadenas[] = {"class", "public", "private", "while","int","boolean","char","float","{","}", "=", ";","<", ">",   //12... Aunque no se usa como tal el "!" solo, sirve para que no lance error
							"==", "<=", ">=", "!", "!=","true","false", "(",")", "/", "+", "-", "*", "if"};		//14   total = 26, de 0 al 25 + nums e id --> 0 - 27
		int tipo = -1;
		for (int i = 0; i < cadenas.length; i++) 
		{
			if(token.equals(cadenas[i]))
				tipo = i;
		}

		if(token.matches("^['][a-zA-Z0-9][']?$")) { //Caracteres
			tipo =49;
		}

		if(token.matches("^[0-9]{1,9}?$")) { //enteros
			tipo = 50;
		}

		if(token.matches("^[0-9]{0,}[.][0-9]{1,5}?$")) {//Número real
			tipo = 51;
		}

		if(token.matches("^[0-9]{10,}?$")) {//error en numeros
			resultado.add("Error Léxico, se esperaba una longitud de máximo 9 dígitos en el número \"" + token +"\" en la linea "+renglon+", No. de token "+columna+" ");
			tokenRC.add(new Token(token, renglon, columna, tipo));
			bandera = false;
			return;
		}
	
		if(tipo==-1) {
			Pattern pat = Pattern.compile("^[a-zA-Z]+[0-9]{0,}+$");
			Matcher mat = pat.matcher(token);
			if(mat.find())
				tipo =52;
			else {
				resultado.add("Error léxico en la línea \""+renglon+"\" No. de token \""+columna+"\" nombre del token \""+token+"\", algunos signos no se admiten.");
				tokenRC.add(new Token(token, renglon, columna, tipo));
				bandera = false;
				return;
			}
		}
		tokenRC.add(new Token(token, renglon, columna, tipo));

	}
	public String espacios(String linea){
		for (String cadena : Arrays.asList("(", ")", "{", "}", "=", ";", "*", "-", "+", "<", "/", ">", "!"))
		{
			if(linea.contains(cadena)) {
				linea = linea.replace(cadena, " "+cadena+" ");
			}
		}
		return linea;
	}
	public String Junta(String token) { //hicimos esto ya que se buggeaba cuando trataba de poner un token tipo != <= >= ==
		contador++;
	
		if(token.equals("<"))
			retMenEQ++;
		else if(token.equals(">"))
			retMayEQ++;
		else if(token.equals("!"))
			retDif++;
		else if(token.equals("="))
		{
			retEQ++;
			retDif++;
			retMayEQ++;
			retMenEQ++;

		}
		else {
			retEQ = 0;
			retDif = 0;
			retMayEQ = 0;
			retMenEQ = 0;
		}

		if(retEQ == 2 || retMenEQ == 2 || retMayEQ == 2 || retDif == 2)
		{
			tokenRC.remove(contador-1);
			
			if(retEQ == 2)
			{	
				tokenRC.add(new Token("==", renglon, columna, 11));
				token = "==";
			}
			else if(retMayEQ == 2)
			{	
				tokenRC.add(new Token(">=", renglon, columna, 12));
				token = ">=";
			}
			else if(retMenEQ == 2)
			{	
				tokenRC.add(new Token("<=", renglon, columna, 13));
				token = "<=";
			}
			else if(retDif == 2)
			{	
				tokenRC.add(new Token("!=", renglon, columna, 14));
				token = "!=";
			}
			contador--;
		}
		return token;
	}
}