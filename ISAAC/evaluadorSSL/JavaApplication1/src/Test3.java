
import java.util.*;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public class Test3 {	
	public static void main(String[] args) {
		String cadena = "";
		StringTokenizer lineas, tokens, tokensT , tokensR;
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
		double reduction[][];
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
		String tiempos, reduccion;
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
		reduction = new double[datasets.size()][algoritmos.size()];
		runs = new int[datasets.size()][algoritmos.size()];
		accV = new double[10];
		redV = new double[10];
		kappaV = new double[10];
		
		tiempos = Fichero.leeFichero("tiempos.txt");
		reduccion = Fichero.leeFichero("red.dat");
		tokensT = new StringTokenizer (tiempos,"=\n\r");
		tokensR = new StringTokenizer (reduccion,"=\n\r");
		
		System.out.println("Numbero de algoritmos "+ algoritmos.size());
		/*C�lculo del accuracy, kappa y reducci�n en KNN (TRAIN)*/
		configAct = 0;
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
	
			for (j=0; j<datasets.size(); j++) {
				datAct = (String)datasets.elementAt(j);
				salAct = ((Integer)saltos.elementAt(j)).intValue();
				acc = red = kappa = 0.0;
				for (k=0; k<10; k++) {
					/*Accuracy Computation*/
					if(configAct >0){
						cadena = Fichero.leeFichero("results//"+alAct+"."+datAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+".tra");
						if(j==0){
							cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+"0"+Integer.toString((j+salAct)*10+k)+".tra");
						}
						//System.out.println("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+".tra");
					}else{
						//System.out.println("results//Clas-KNN//"+alAct+"//result"+Integer.toString((j+salAct)*10+k)+".tra");
						cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tra");
						//cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+".tra");
					}
					
					System.out.println(datasets.get(j));
					System.out.println(k);
					lineas = new StringTokenizer (cadena, "\n\r");
					aciertos = total = 0;
					while (lineas.hasMoreTokens()) {
						linea = lineas.nextToken();
						if (!(linea.startsWith("@"))) {
							tokens = new StringTokenizer (linea," ");
							dato1 = tokens.nextToken();
							dato2 = tokens.nextToken();
							//System.out.println(dato1);
							//System.out.println(dato2);
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
					
					
					/*NO HAY REDUCCIÖN EN KEEL DEV
					 * Reduction Computation and accuracy
					if(configAct >0){
						cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+"e0.txt");
						if(j==0){
							cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+"0"+Integer.toString((j+salAct)*10+k)+"e0.txt");
						}
					}else{
						cadena = Fichero.leeFichero("results//"+alAct+".Clas-KNN."+datasets.get(j)+"//result"+Integer.toString(k)+"e0.txt");
					}
					
					cadena = Fichero.leeFichero("red.dat");
					tokens = new StringTokenizer (cadena,"=\n\r");
					
					String tok = new String(tokens.nextToken()); // en tok ya tenemos el accuracy
					accV[k]= Double.parseDouble(tok.substring(10));
					
					boolean reduction = false;
					while (tokens.hasMoreTokens() && !reduction) {
						tok = tokens.nextToken();
						//System.out.println(tok);
						if (tok.contains("Reduction"))
							reduction = true;
						else {
							reduction = false;
						}
					}
					
					dato1 = tok;
					//System.out.println(dato1.substring(22));
					 * redV[k] = Double.parseDouble(dato1.substring(22));
					 * */
					 
					
					
					
					
					/*Kappa Computation*/
					if(configAct >0){
						cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+".tra");
						if(j==0){
							cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+"0"+Integer.toString((j+salAct)*10+k)+".tra");
						}
					}else{
						cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tra");
						//cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+".tra");
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

				red = 0;

				for (l=0; l<redV.length; l++) {

					red += redV[l];

				}

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

				for (l=0; l<redV.length; l++) {

					redSD += (red - redV[l]) * (red - redV[l]); 

				}

				redSD = Math.sqrt(redSD / (double)redV.length);

				for (l=0; l<kappaV.length; l++) {

					kapSD += (kappa - kappaV[l]) * (kappa - kappaV[l]); 

				}

				kapSD = Math.sqrt(kapSD / (double)kappaV.length);

				

				/*Store the values in the main matrixes*/

				accuracyAv[j][i] = acc;
				accuracySD[j][i] = accSD;
				reductionAv[j][i] = red;
				reductionSD[j][i] = redSD;
				kappaAv[j][i] = kappa;
				kappaSD[j][i] = kapSD;
     			accredAv[j][i] = acc * red;
				kapredAv[j][i] = kappa * red;

				//System.out.println("Acc =" +acc);

				/*Time processing

				timesplit = tiempos.split(alAct.substring(3)+" "+datAct+" ");

				for (k=1; k<timesplit.length; k++) {

					postime = timesplit[k].indexOf("s");

					time[j][i] = Double.parseDouble(timesplit[k].substring(0, postime));

					runs[j][i]++;

				}*/
				
			
				for( k=0; k< 10 ;k++){
					dato1 = tokensT.nextToken();
					time[j][i] += Double.parseDouble(dato1) ;
					runs[j][i]++;
					System.out.println(dato1);
				}

				

				/*Reduction  processing*/

				for( k=0; k< 10 ;k++){
					dato1 = tokensR.nextToken();
					reduction[j][i] += Double.parseDouble(dato1) ;
					//System.out.println(dato1);
				}
				
				
			}

		}

		


		
		/*Print the results*/
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

		/*Print the results*/
	/*	cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaReduction.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", reductionAv[i][j]) + "\t" + String.format("%6.4f", reductionSD[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaReduction.txt", cadena);
		}
*/
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("reduccion.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", reduction[i][j]/(double)runs[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaReduction.txt", cadena);
		}
		
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t";
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
		Fichero.escribeFichero("tablaKappaTRA.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", kappaAv[i][j]) + "\t" + String.format("%6.4f", kappaSD[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaTRA.txt", cadena);
		}
		
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccRedTRA.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", accredAv[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccRedTRA.txt", cadena);
		}

		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaKappaRedTRA.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", kapredAv[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaRedTRA.txt", cadena);
		}

		configAct =0;
		/*C�lculo del accuracy, kappa y reducci�n en KNN (TST)*/
		for (i=0; i<algoritmos.size(); i++) {
			alAct = (String)algoritmos.elementAt(i);
			System.out.println("Processing algorithm: " + alAct);
			
			
			for (j=0; j<datasets.size(); j++) {
				salAct = ((Integer)saltos.elementAt(j)).intValue();
				acc = red = kappa = 0.0;
				for (k=0; k<10; k++) {
					/*Accuracy Computation*/
					if(configAct >0){
						cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+".tst");
					}else{
						cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tst");
						//cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+".tst");
						
						//System.out.println("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tst");
						System.out.println("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+".tst");
					}
			
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
					
					/*Reduction Computation
					if(configAct >0){
						cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+"e0.txt");
					}else{
						cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//results"+Integer.toString(k)+"e0.txt");
					}
					
					tokens = new StringTokenizer (cadena,"=\n\r");
					tokens.nextToken();
					String tok = new String();
					boolean reduction = false;
					while (tokens.hasMoreTokens() && !reduction) {
						tok = tokens.nextToken();
						//System.out.println(tok);
						if (tok.contains("Reduction"))
							reduction = true;
						else {
							reduction = false;
						}
					}
					
					dato1 = tok;
					//System.out.println(dato1.substring(22));
					redV[k] = Double.parseDouble(dato1.substring(22));
					*/
					
					/*Kappa Computation*/
					if(configAct >0){
						cadena = Fichero.leeFichero("results//Clas-KNN//"+alAct+"//result"+Integer.toString(configAct)+Integer.toString((j+salAct)*10+k)+".tst");
					}else{
						cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+"s0.tst");
						//cadena = Fichero.leeFichero("results//"+alAct+"."+datasets.get(j)+"//result"+Integer.toString(k)+".tst");
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
				red = 0;
				for (l=0; l<redV.length; l++) {
					red += redV[l];
				}
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
				for (l=0; l<redV.length; l++) {
					redSD += (red - redV[l]) * (red - redV[l]); 
				}
				redSD = Math.sqrt(redSD / (double)redV.length);
				for (l=0; l<kappaV.length; l++) {
					kapSD += (kappa - kappaV[l]) * (kappa - kappaV[l]); 
				}
				kapSD = Math.sqrt(kapSD / (double)kappaV.length);
				
				/*Store the values in the main matrixes*/
				accuracyAv[j][i] = acc;
				accuracySD[j][i] = accSD;
				reductionAv[j][i] = red;
				reductionSD[j][i] = redSD;
				kappaAv[j][i] = kappa;
				kappaSD[j][i] = kapSD;
				accredAv[j][i] = acc * red;
				kapredAv[j][i] = kappa * red;
			}
		}
		
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccuracyTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", accuracyAv[i][j]) + "\t" + String.format("%6.4f", accuracySD[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccuracyTST.txt", cadena);
		}

		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaKappaTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", kappaAv[i][j]) + "\t" + String.format("%6.4f", kappaSD[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaTST.txt", cadena);
		}
		
		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaAccRedTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", accredAv[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaAccRedTST.txt", cadena);
		}

		/*Print the results*/
		cadena = "Datasets\t\t";
		for (i=0; i<algoritmos.size(); i++) {
			cadena += algoritmos.elementAt(i) + "\t\t";
		}
		cadena += "\n";
		Fichero.escribeFichero("tablaKappaRedTST.txt", cadena);
		for (i=0; i<datasets.size(); i++) {
			cadena = datasets.elementAt(i) + "\t\t";
			for (j=0; j<algoritmos.size(); j++) {
				cadena += String.format("%6.4f", kapredAv[i][j]) + "\t";
			}
			cadena += "\n";
			Fichero.AnadirtoFichero("tablaKappaRedTST.txt", cadena);
		}	
	
}
}
