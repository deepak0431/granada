
import java.util.*;

public class datos {	
	public static void main(String[] args) {
		String cadena = "";
		StringTokenizer lineas, tokens, tokensT;
		String linea, dato1, dato2, token, token2, timesplit[];
		String PK, speed, tipoAcel, defecto, mes, anio, valorDefecto;
		
		boolean algorithms = true;
		Vector <String> aceleraciones;
		Vector <String> defectosAcel;

		Vector <Integer> saltos;
		int i, j, k, l, m,n;

		if (args.length != 1) {
			System.err.println("Error. Hace falta un parámetro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}


		/*Lectura del fichero de datos*/
		cadena = Fichero.leeFichero(args[0]);
		lineas = new StringTokenizer (cadena,"\n\r");
		
		

		
		double pk, velocidad, valor_defecto;
		
		String defectos[] = new String [5];
	
		defectos[0] = "bogie";
		defectos[1] = "vgrasa1";
		defectos[2] = "vgrasa2";
		defectos[3] = "latcaja";
		defectos[4] = "vertcaja";
		
		
		int numDatos =-1;
		String mesTest = "hola";
		
		while (lineas.hasMoreTokens()) { // Hasta el final.
			
			aceleraciones = new Vector <String>();
			defectosAcel = new Vector <String>();
			
			linea = lineas.nextToken();
			
			tokens = new StringTokenizer (linea,"\t");
			
			// los dos primeros tokens son los PK.
			token = tokens.nextToken();
			token2= tokens.nextToken();
			
			if(!token2.equals("*")){
				pk = (Double.parseDouble(token) + Double.parseDouble(token2))/2;
			}else{
				pk = Double.parseDouble(token);
			}

			//System.out.println("pk = "+ pk);
			PK = String.valueOf(pk);
			
			//Velocidad
			
			speed = tokens.nextToken();
			
			// Acelaraciones.
			
			for( i=0; i<5 ;i++){
				token = tokens.nextToken();
				
				if(!token.equals("*")){
					defectosAcel.addElement(new String(defectos[i]));
					aceleraciones.addElement(new String(token));
				}
			}
			
			// Observaci—n.
			
			defecto = tokens.nextToken();
			
			if(defecto.equals("*")){
				defecto = "<null>";
			}
			// Mes y a–o
			
			mes = tokens.nextToken();
			anio = tokens.nextToken();
			
			if(!mes.equals(mesTest)){
				mesTest = mes;
				numDatos++;
			}
			for( i=0; i< aceleraciones.size(); i++){
				Fichero.AnadirtoFichero("salidaDatos.txt", PK+","+speed+","+defectosAcel.get(i)+","+defecto+","+mes+ ", "+ anio+"," + aceleraciones.get(i)+"\n");
				
				//Fichero.AnadirtoFichero("salidaDatos.txt", numDatos+","+PK+","+speed+","+defectosAcel.get(i)+
					//	
			}
		}
    
		
		System.out.println("Procesado");
	


		
		/*Print the results
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccuracyTRA.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", accuracyAv[i][j]) + "\t" + String.format("%6.4f", accuracySD[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccuracyTRA.txt", cadena);
		}
*/

	}
}
