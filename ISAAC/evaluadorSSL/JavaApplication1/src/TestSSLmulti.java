
import java.util.*;

/*
 * Test para KeelDev:  Recibe un conjunto de directorios con distintas configuraciones para un mismo algoritmo.
 * @author Isaac Triguero and Salva Garc�a.
 */
public class TestSSLmulti {	
	public static void main(String[] args) {
		String cadena = "";
		StringTokenizer lineas, tokens;
		String linea, dato1, dato2, token, timesplit[];
		boolean algorithms = true;
		Vector <String> algoritmos;
		Vector <String> datasets;
		Vector <String> directorios;
		
		Vector <Integer> saltos;
		int i, j, k, l, m,n;
		double accuracyAv[][][];
		double accuracySD[][][];
		double reductionAv[][][];
		double reductionSD[][][];
		double kappaAv[][][];
		double kappaSD[][][];
		double accredAv[][][];
		double kapredAv[][][];
		double time[][];
		int runs[][];
		String alAct, alAnterior, dirAct;
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

		// Argumentos: fichero de config (algoritmos + datasets)  + fichero de directorios.
		if (args.length < 1) {
			System.err.println("Error. Hace falta un par�metro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}

		algoritmos = new Vector <String>();
		datasets = new Vector <String>();
		directorios = new Vector <String>();
		
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
    
		
		
		/*Lectura del fichero de DIRECTORIOS*/
		cadena = Fichero.leeFichero(args[1]);
		lineas = new StringTokenizer (cadena,"\n\r");
		while (lineas.hasMoreTokens()) {
			linea = lineas.nextToken();
			tokens = new StringTokenizer (linea," ");
			token = tokens.nextToken();
			directorios.addElement(new String(token));
		}
		
		accuracyAv = new double[datasets.size()][directorios.size()][algoritmos.size()];
		accuracySD = new double[datasets.size()][directorios.size()][algoritmos.size()];
		reductionAv = new double[datasets.size()][directorios.size()][algoritmos.size()];
		reductionSD = new double[datasets.size()][directorios.size()][algoritmos.size()];
		kappaAv = new double[datasets.size()][directorios.size()][algoritmos.size()];
		kappaSD = new double[datasets.size()][directorios.size()][algoritmos.size()];
		accredAv = new double[datasets.size()][directorios.size()][algoritmos.size()];
		kapredAv = new double[datasets.size()][directorios.size()][algoritmos.size()];
		time = new double[datasets.size()][algoritmos.size()];
		runs = new int[datasets.size()][algoritmos.size()];
		accV = new double[10];
		redV = new double[10];
		kappaV = new double[10];
		
		//tiempos = Fichero.leeFichero("tiempos.txt");

		System.out.println("Numbero de algoritmos "+ algoritmos.size());
		System.out.println("Numbero de datasets "+ datasets.size());
		System.out.println("Numbero de directorios "+ directorios.size());
		
		/*C�lculo del accuracy, kappa  en KNN (TRAIN)*/
		
		
		configAct = 0;
		for (i=0; i<algoritmos.size(); i++) {
			alAct = (String)algoritmos.elementAt(i);
			System.out.println("Processing algorithm: " + alAct);
	
			for(int z=0; z<directorios.size(); z++){
				dirAct = (String)directorios.elementAt(z);
				System.out.println("Processing directorio: " + dirAct);
				
				for (j=0; j<datasets.size(); j++) {
					datAct = (String)datasets.elementAt(j);
					salAct = ((Integer)saltos.elementAt(j)).intValue();
					acc = red = kappa = 0.0;
					boolean seguir = true;
					for (k=0; k<10 && seguir; k++) {
						/*Accuracy Computation*/
	
						//System.out.println(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tra");
						
						cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tra");
						if(cadena.equals("-1")){
							cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k+10)+"s0.tra");
						}
						
						// Si todavía sigue siendo nulo.
						
						if(cadena.equals("-1") && k==0){ // si el primero no está... malo :P
							accuracyAv[j][z][i] =0;
							accuracySD[j][z][i] = 0;
							kappaAv[j][z][i] = 0;
							kappaSD[j][z][i] = 0;
							seguir= false;
							
						}else{
						
							System.out.println(datasets.get(j)+", en el directorio = "+ dirAct);
							//System.out.println(cadena);
							lineas = new StringTokenizer (cadena, "\n\r");
							aciertos = total = 0;
							while (lineas.hasMoreTokens()) {
								linea = lineas.nextToken();
								if (!(linea.startsWith("@"))) {
									tokens = new StringTokenizer (linea," ");
									dato1 = tokens.nextToken();
									dato2 = tokens.nextToken();
									if (dato1.equalsIgnoreCase(dato2)) {
										aciertos++;
										total++;
									} else {
										total++;
									}
								}
							}
							
							accV[k] = (double)aciertos / (double)total;
							System.out.println("Acc = "+accV[k]);
							
		
							
							/*Kappa Computation*/
							
							cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tra");
							if(cadena.equals("-1")){
								cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k+10)+"s0.tra");
							}
							
							
							lineas = new StringTokenizer (cadena, "\n\r");
							aciertos = total = 0;
							valoresClase = new Vector <String>();
							while (lineas.hasMoreTokens()) {
								linea = lineas.nextToken();
								if (!(linea.startsWith("@"))) {
									tokens = new StringTokenizer (linea," ");
									dato1 = tokens.nextToken();
									if (!valoresClase.contains(dato1)) {
										valoresClase.addElement(dato1);
									}
								}
							}
							confusionMatrix = new int[valoresClase.size()][valoresClase.size()];					
		
							lineas = new StringTokenizer (cadena, "\n\r");
		
							total = 0;
		
							while (lineas.hasMoreTokens()) {
		
								linea = lineas.nextToken();
		
								if (!(linea.startsWith("@"))) {
		
									tokens = new StringTokenizer (linea," ");
		
									dato1 = tokens.nextToken();
		
									dato2 = tokens.nextToken();
		
									try {
		
										confusionMatrix[valoresClase.indexOf(dato1)][valoresClase.indexOf(dato2)]++;
		
									} catch (ArrayIndexOutOfBoundsException e) {
		
										confusionMatrix[valoresClase.indexOf(dato1)][0]++;								
		
									}
		
									total++;
		
								}
		
							}					
		
							sumKappa = 0;
		
							for (l=0; l<valoresClase.size(); l++) {
		
								sumKappa += confusionMatrix[l][l];
		
							}					
		
							kappa = total * sumKappa;
		
							sumKappa = 0;
		
							for (l=0; l<valoresClase.size(); l++) {
		
								sumi = 0;
		
								for (m=0; m<valoresClase.size(); m++) {
		
									sumi += confusionMatrix[l][m];
		
								}
		
								sumj = 0;
		
								for (m=0; m<valoresClase.size(); m++) {
		
									sumj += confusionMatrix[m][l];
		
								}
		
								sumKappa += sumi * sumj;
		
							}					
		
							kappaV[k] = (double)(kappa - sumKappa) / (double)(total*total - sumKappa);
		
							//System.out.println(accV[k]);
						}
		
						
		
						acc = 0;
		
						for (l=0; l<accV.length; l++) {
		
							acc += accV[l];
		
						}
		
						acc /= (double)accV.length;
		
		
		
						red /= (double)redV.length;
		
						kappa = 0;
		
						for (l=0; l<kappaV.length; l++) {
		
							kappa += kappaV[l];
		
						}
		
						kappa /= (double)kappaV.length;
		
						
		
						accSD = redSD = kapSD = 0;
		
						for (l=0; l<accV.length; l++) {
		
							accSD += (acc - accV[l]) * (acc - accV[l]); 
		
						}
		
						accSD = Math.sqrt(accSD / (double)accV.length);
		
		
						for (l=0; l<kappaV.length; l++) {
		
							kapSD += (kappa - kappaV[l]) * (kappa - kappaV[l]); 
		
						}
		
						kapSD = Math.sqrt(kapSD / (double)kappaV.length);
		
						
		
						/*Store the values in the main matrixes*/
		
						accuracyAv[j][z][i] = acc;
						accuracySD[j][z][i] = accSD;
						kappaAv[j][z][i] = kappa;
						kappaSD[j][z][i] = kapSD;
						
					}// End if 
	
				} // End for datasets.

			} // End for DIRECTORIOS
		}

		


		
		/*Print the results: SOLO PARA UN ALGORITMO AHORA MISMO.*/
		cadena = "Datasets\t\t";
		for (i=0; i<directorios.size(); i++) {
			cadena += directorios.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccuracyTRS.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<directorios.size(); j++) {
				cadena += String.format("%6.4f", accuracyAv[i][j][0]) + "\t" + String.format("%6.4f", accuracySD[i][j][0]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccuracyTRS.txt", cadena);
		}


		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<directorios.size(); i++) {
			cadena += directorios.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaTime.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", time[i][j]/(double)runs[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaTime.txt", cadena);
		}

		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaKappaTRS.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<directorios.size(); j++) {
				cadena += String.format("%6.4f", kappaAv[i][j][0]) + "\t" + String.format("%6.4f", kappaSD[i][j][0]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaTRS.txt", cadena);
		}
		


		configAct =0;
		/*C�lculo del accuracy, kappa y reducci�n en KNN (TST)*/
		for (i=0; i<algoritmos.size(); i++) {
			alAct = (String)algoritmos.elementAt(i);
			System.out.println("Processing algorithm: " + alAct);
			
			for(int z=0; z<directorios.size(); z++){
				dirAct = (String)directorios.elementAt(z);
				System.out.println("Processing dataset: " + dirAct);
				
				for (j=0; j<datasets.size(); j++) {
					salAct = ((Integer)saltos.elementAt(j)).intValue();
					acc = red = kappa = 0.0;
					
					System.out.println(datasets.get(j)+", en el directorio = "+ dirAct);
					boolean seguir = true;
					for (k=0; k<10 && seguir; k++) {
						/*Accuracy Computation*/
	
						
				
						cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tst");
						if(cadena.equals("-1")){
							cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k+10)+"s0.tst");
						}
						
						
						if(cadena.equals("-1") && k==0){ // si el primero no está... malo :P
							accuracyAv[j][z][i] =0;
							accuracySD[j][z][i] = 0;
							kappaAv[j][z][i] = 0;
							kappaSD[j][z][i] = 0;
							seguir= false;
							
						}else{
							
							
							lineas = new StringTokenizer (cadena, "\n\r");
							aciertos = total = 0;
							while (lineas.hasMoreTokens()) {
								linea = lineas.nextToken();
								if (!(linea.startsWith("@"))) {
									tokens = new StringTokenizer (linea," ");
									dato1 = tokens.nextToken();
									dato2 = tokens.nextToken();
									if (dato1.equalsIgnoreCase(dato2)) {
										aciertos++;
										total++;
									} else {
										total++;
									}
								}
							}
							accV[k] = (double)aciertos / (double)total;
							
							
							
							/*Kappa Computation*/
							cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tst");
							if(cadena.equals("-1")){
								cadena = Fichero.leeFichero(dirAct+"//results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k+10)+"s0.tst");
							}
							
							
							lineas = new StringTokenizer (cadena, "\n\r");
							aciertos = total = 0;
							valoresClase = new Vector <String>();
							while (lineas.hasMoreTokens()) {
								linea = lineas.nextToken();
								if (!(linea.startsWith("@"))) {
									tokens = new StringTokenizer (linea," ");
									dato1 = tokens.nextToken();
									if (!valoresClase.contains(dato1)) {
										valoresClase.addElement(dato1);
									}
								}
							}
							confusionMatrix = new int[valoresClase.size()][valoresClase.size()];					
							lineas = new StringTokenizer (cadena, "\n\r");
							total = 0;
							while (lineas.hasMoreTokens()) {
								linea = lineas.nextToken();
								if (!(linea.startsWith("@"))) {
									tokens = new StringTokenizer (linea," ");
									dato1 = tokens.nextToken();
									dato2 = tokens.nextToken();
									try {
										confusionMatrix[valoresClase.indexOf(dato1)][valoresClase.indexOf(dato2)]++;
									} catch (ArrayIndexOutOfBoundsException e) {
										confusionMatrix[valoresClase.indexOf(dato1)][0]++;								
									}
									total++;
								}
							}					
							sumKappa = 0;
							for (l=0; l<valoresClase.size(); l++) {
								sumKappa += confusionMatrix[l][l];
							}					
							kappa = total * sumKappa;
							sumKappa = 0;
							for (l=0; l<valoresClase.size(); l++) {
								sumi = 0;
								for (m=0; m<valoresClase.size(); m++) {
									sumi += confusionMatrix[l][m];
								}
								sumj = 0;
								for (m=0; m<valoresClase.size(); m++) {
									sumj += confusionMatrix[m][l];
								}
								sumKappa += sumi * sumj;
							}					
							kappaV[k] = (double)(kappa - sumKappa) / (double)(total*total - sumKappa);
						}
						
						acc = 0;
						for (l=0; l<accV.length; l++) {
							acc += accV[l];
						}
						acc /= (double)accV.length;
		
						kappa = 0;
						for (l=0; l<kappaV.length; l++) {
							kappa += kappaV[l];
						}
						kappa /= (double)kappaV.length;
						
						accSD = redSD = kapSD = 0;
						for (l=0; l<accV.length; l++) {
							accSD += (acc - accV[l]) * (acc - accV[l]); 
						}
						accSD = Math.sqrt(accSD / (double)accV.length);
		
						for (l=0; l<kappaV.length; l++) {
							kapSD += (kappa - kappaV[l]) * (kappa - kappaV[l]); 
						}
						kapSD = Math.sqrt(kapSD / (double)kappaV.length);
						
						/*Store the values in the main matrixes*/
						accuracyAv[j][z][i] = acc;
						accuracySD[j][z][i] = accSD;
						kappaAv[j][z][i] = kappa;
						kappaSD[j][z][i] = kapSD;
					} // End if
					
				}// End for j
				
			}// end for z
		}
		
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<directorios.size(); i++) {
			cadena += directorios.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccuracyTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<directorios.size(); j++) {
				cadena += String.format("%6.4f", accuracyAv[i][j][0]) + "\t" + String.format("%6.4f", accuracySD[i][j][0]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccuracyTST.txt", cadena);
		}

		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<directorios.size(); i++) {
			cadena += directorios.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaKappaTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<directorios.size(); j++) {
				cadena += String.format("%6.4f", kappaAv[i][j][0]) + "\t" + String.format("%6.4f", kappaSD[i][j][0]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaTST.txt", cadena);
		}
		



	
}
}
