
import java.util.*;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public class TestNodi {	
	public static void main(String[] args) {
		String cadena = "";
		StringTokenizer lineas, tokens;
		String linea, dato1, dato2, token, timesplit[];
		boolean algorithms = true;
		Vector <String> algoritmos;
		Vector <String> datasets;

		Vector <Integer> saltos;
		int i, j, k, l, m,n;
		double accuracyAv[][];
		double accuracySD[][];
		double reductionAv[][];
		double reductionSD[][];
		double kappaAv[][];
		double kappaSD[][];
		double accredAv[][];
		double kapredAv[][];
		double time[][];
                
                /* ADDED */
                int numeroNodi[][];
                double antRule[][];
                /* END ADDED */
                
		int runs[][];
		String alAct, alAnterior;
		int configAct;
		String datAct;
		int salAct;
		double acc, red, kappa;
		int aciertos, total;
		double accV[], redV[], kappaV[];
		Vector <String> valoresClase;
		int confusionMatrix[][];
		int sumKappa, sumi, sumj;
		double accSD, redSD, kapSD;
		String tiempos;
		int postime;

		if (args.length != 1) {
			System.err.println("Error. Hace falta un par�metro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}

		algoritmos = new Vector <String>();
		datasets = new Vector <String>();

		saltos = new Vector <Integer>();

		/*Lectura del fichero de configuraci�n*/
		cadena = Fichero.leeFichero(args[0]);
		lineas = new StringTokenizer (cadena,"\n\r");
		while (lineas.hasMoreTokens()) {
			linea = lineas.nextToken();
			if (linea.equalsIgnoreCase("----"))
				algorithms = false;
			else {
				if (algorithms) {
					algoritmos.addElement(new String(linea));						
				} else {
					tokens = new StringTokenizer (linea," ");
					token = tokens.nextToken();
					datasets.addElement(new String(token));
					token = tokens.nextToken();
					saltos.addElement(new Integer(token));
				}
			}
		}
    
		accuracyAv = new double[datasets.size()][algoritmos.size()];
		accuracySD = new double[datasets.size()][algoritmos.size()];
		reductionAv = new double[datasets.size()][algoritmos.size()];
		reductionSD = new double[datasets.size()][algoritmos.size()];
		kappaAv = new double[datasets.size()][algoritmos.size()];
		kappaSD = new double[datasets.size()][algoritmos.size()];
		accredAv = new double[datasets.size()][algoritmos.size()];
		kapredAv = new double[datasets.size()][algoritmos.size()];
		time = new double[datasets.size()][algoritmos.size()];
		/*ADDED*/
                numeroNodi = new int[datasets.size()][algoritmos.size()];
                antRule = new double[datasets.size()][algoritmos.size()];
                
		runs = new int[datasets.size()][algoritmos.size()];
		accV = new double[10];
		redV = new double[10];
		kappaV = new double[10];
		
		//tiempos = Fichero.leeFichero("tiempos.txt");

		System.out.println("Numbero de algoritmos "+ algoritmos.size());
		/*C�lculo del accuracy, kappa y reducci�n en KNN (TRAIN)*/
		configAct = 0;
                
                /* Istruzioni eseguite per ognuno degli algoritmi */
		for (i=0; i<algoritmos.size(); i++) {
			alAct = (String)algoritmos.elementAt(i);
			System.out.println("Processing algorithm: " + alAct);
		/*	if(i>0){
				alAnterior = (String)algoritmos.elementAt(i-1);
				//System.err.println("Algoritmo anterior = "+ alAnterior);
				if(alAct.equals(alAnterior)){
					// Si tenemos el mismo algoritmo que antes.
					System.out.println("Otro igual");
					configAct++;
				}else{
					configAct = 0;
					
				}
			
			}
			*/
                        /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
			for (j=0; j<datasets.size(); j++) {
				datAct = (String)datasets.elementAt(j);
				salAct = ((Integer)saltos.elementAt(j)).intValue();
				acc = red = kappa = 0.0;
				for (k=0; k<10; k++) {
					/*Accuracy Computation*/

					
					cadena = Fichero.leeFichero("results\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"e0.txt");
					
                                        if(cadena.equals("-1")){
						cadena = Fichero.leeFichero("results\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"e0.txt");
					}
					
					
					System.out.println(datasets.get(j));
                                        System.out.println(cadena);
                                        System.out.println("results\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"e0.txt");
					//System.out.println(cadena);
					lineas = new StringTokenizer (cadena, "\n\r");
					aciertos = total = 0;
					while (lineas.hasMoreTokens()) {
						linea = lineas.nextToken();
                                                /* se nella linea sono presenti dei parametri li ignora
                                                 * se sono presenti dei dati li valuta!
                                                 */
						if ((linea.startsWith("@TotalNumberOfNodes"))) {
							tokens = new StringTokenizer (linea," ");
							dato1 = tokens.nextToken();
                                                        numeroNodi[j][i]+=Integer.parseInt(tokens.nextToken());
							//dato2 = tokens.nextToken();
						}
                                                if ((linea.startsWith("@NumberOfAntecedentsByRule"))) {
							tokens = new StringTokenizer (linea," ");
							dato1 = tokens.nextToken();
                                                        antRule[j][i]+=Double.parseDouble(tokens.nextToken());
							//dato2 = tokens.nextToken();
						}
                                                
					}
					
				}



				//System.out.println("Acc =" +acc);

				/*Time processing*/

				/*timesplit = tiempos.split(alAct.substring(3)+" "+datAct+" ");

				for (k=1; k<timesplit.length; k++) {

					postime = timesplit[k].indexOf("s");

					time[j][i] = Double.parseDouble(timesplit[k].substring(0, postime));

					runs[j][i]++;

				}
*/
			}

		}

	
		/*Print the results*/
		cadena = "Datasets\t\t";
                cadena += "#nodes \t #antecedents";
                /*        
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
                 */
		cadena += "\n";
		Fichero.escribeFichero("TablaNodi.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", (float)numeroNodi[i][j]/20) + 
                                        "\t" + String.format("%6.4f", (float)antRule[i][j]/10)+ "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("TablaNodi.txt", cadena);
		}


    }
}
