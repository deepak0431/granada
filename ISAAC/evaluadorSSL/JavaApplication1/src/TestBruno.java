
import java.util.*;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public class TestBruno {
    static String cadena = "";
    static StringTokenizer lineas, tokens;
    static String linea, dato1, dato2, token, timesplit[];
    static boolean algorithms = true;
    static Vector <String> algoritmos;
    static Vector <String> datasets;

    static Vector <Integer> saltos;
    //static int i, j, k, l, m,n;
    static double accuracyAv[][];
    static double accuracySD[][];
    static double reductionAv[][];
    static double reductionSD[][];
    static double kappaAv[][];
    static double kappaSD[][];
    static double accredAv[][];
    static double kapredAv[][];
    static double time[][];

    /* ADDED */
    static int numeroNodi[][];
    static double antRule[][];
    /* END ADDED */

    static int runs[][];
    static String alAct, alAnterior;
    static int configAct;
    static String datAct;
    static int salAct;
    static double acc, red, kappa;
    static int aciertos, total;
    static double accV[], redV[], kappaV[];
    static Vector <String> valoresClase;
    static int confusionMatrix[][];
    static int sumKappa, sumi, sumj;
    static double accSD, redSD, kapSD;
    static String tiempos;
    static int postime;
    
    public static void main(String[] args) {
		

		if (args.length != 1) {
			System.err.println("Error. Hace falta un par�metro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}
                inizializza(args);
		
                calcolo_accurancy();
                
                calcolo_nodi_ant();
                
                scrivi_tabella();
                
        }
        public static void inizializza(String[] args){
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
            
        }
        
        public static void calcolo_accurancy(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
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
                    for (int j=0; j<datasets.size(); j++) {
                            datAct = (String)datasets.elementAt(j);
                            salAct = ((Integer)saltos.elementAt(j)).intValue();
                            acc = red = kappa = 0.0;
                            aciertos = 0;
                            total = 0;
                            for (int k=0; k<10; k++) {
                                    /*Accuracy Computation*/


                                    cadena = Fichero.leeFichero("results\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tst");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.leeFichero("results\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+".tst");
                                    }


                                    System.out.println(datasets.get(j));
                                    System.out.println(cadena);
                                    System.out.println("results\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tst");
                                    //System.out.println(cadena);
                                    lineas = new StringTokenizer (cadena, "\n\r");
                                    
                                    aciertos = 0;
                                    total = 0;
                                    while (lineas.hasMoreTokens()) {
                                            linea = lineas.nextToken();
                                            /* se nella linea sono presenti dei parametri li ignora
                                             * se sono presenti dei dati li valuta!
                                             */
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
                                    //System.out.println("Acc = "+accV[k]);
                                }
                            //acc = (double)aciertos / (double)total;
                            for (int l=0; l<accV.length; l++) {
                                    acc += accV[l];
                            }
                            accuracyAv[j][i] = acc /10;
                            
                        }
            }
        }
        
        
        public static void calcolo_nodi_ant(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
		for (int i=0; i<algoritmos.size(); i++) {
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
			for (int j=0; j<datasets.size(); j++) {
				datAct = (String)datasets.elementAt(j);
				salAct = ((Integer)saltos.elementAt(j)).intValue();
				acc = red = kappa = 0.0;
				for (int k=0; k<10; k++) {
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

			}

		}
            
        }
        public static void scrivi_tabella(){
            /*Print the results*/
                
		cadena = "\n";
		Fichero.escribeFichero("TablaNodi.txt", cadena);
		for (int i=0; i<datasets.size(); i++) {
                    //cadena = "Datasets\t\t";
                    cadena += datasets.elementAt(i) + "\t   \t  \t";
                    cadena += "\t\tacc \t\t #size \t #ant";
                    cadena += "\n";
			//cadena = datasets.elementAt(i) + "\t   \t   \t";
			for (int j=0; j<algoritmos.size(); j++) {
                                cadena += algoritmos.elementAt(j) + "\t\t";
				cadena += String.format("%6.4f", accuracyAv[i][j]) +
                                        "\t"+ String.format("%6.4f", (float)numeroNodi[i][j]/20) + 
                                        "\t" + String.format("%6.4f", (float)antRule[i][j]/10)+ "\t";
                                cadena += "\n";
			}
			cadena += "\n\n\n";	
		}
                Fichero.AnadirtoFichero("TablaNodi.txt", cadena);
        }
}
