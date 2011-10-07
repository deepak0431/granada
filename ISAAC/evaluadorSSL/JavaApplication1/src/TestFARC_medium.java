
import java.util.*;


//imported to use the excell library
import writer.*;
import jxl.write.WriteException;
import java.io.IOException;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public class TestFARC_medium {
    static String cadena = "";
    static StringTokenizer lineas, tokens;
    static String linea, dato1, dato2, token, timesplit[];
    static boolean algorithms = true;
    static Vector <String> algoritmos;
    static Vector <String> sel_alg;
    static Vector <String> datasets;

    static Vector <Integer> saltos;
    //static int i, j, k, l, m,n;
    static double accuracyAv[][];
    static double accuracyTra[][];
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
    
    public static void main(String[] args) throws IOException, WriteException {
		

		if (args.length != 1) {
			System.err.println("Error. Hace falta un par�metro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}
                inizializza(args);
                
                //calcolo_tempo();
		
                calcolo_accurancy();
                
                calcolo_accurancy_tra();
                
                calcolo_nodi_ant();
                
                //scrivi_tabella();
                
                write_table_excel();
                
        }
        public static void inizializza(String[] args){
            algoritmos = new Vector <String>();
            datasets = new Vector <String>();
            sel_alg = new Vector <String>();

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
            accuracyTra = new double[datasets.size()][algoritmos.size()];
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
            
            nomi_selAlg();
            
        }
        
        public static void nomi_selAlg(){
            String alg_name="";
            
            String[] vect_str;
        
            for (int i=0; i<algoritmos.size(); i++) {
                
                alAct = (String)algoritmos.elementAt(i);
                StringTokenizer st = new StringTokenizer(alAct,".");
                System.out.println(alAct);
                
                if (st.countTokens()==3){

                    st.nextToken();
                    alg_name= st.nextToken();
                    if (alg_name.startsWith("IS")){
                        st = new StringTokenizer(alg_name,"-");
                        st.nextToken();
                        alg_name=st.nextToken();
                    }   
                    else {
                        vect_str = alg_name.split("-TSS");
                        alg_name = vect_str[0];
                        }
                    /* This algorithm is called "CPruner" but does the time's print
                     * writing "Cpruner"
                     */
                    if (alg_name.compareTo("CPruner")==0)
                        alg_name="Cpruner";
                    sel_alg.addElement(new String(alg_name));                    
                }else{
                    sel_alg.addElement(new String("FARC-HD"));
                }
            }
        }
        
        public static void calcolo_tempo(){
            String alg_name="";
            String DB_name="";
            String[] vect_str;
            
            
            /*Time computation*/
            
            
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
                
                
                alAct = (String)algoritmos.elementAt(i);
                StringTokenizer st = new StringTokenizer(alAct,".");
                System.out.println(alAct);
                
                int index_str=0;
                int index_time=0;
                
                if (st.countTokens()==3){
                    cadena = Fichero.leeFichero("MEDIUM\\outFARC\\"
                                    +alAct+"\\sge_output.dat");

                    //a qsto punto dovrei avere il nome dell'algoritmo

                    System.out.println("Calculating time algorithm: " + alAct+"\n");
                    System.out.println("nome algoritmo:" + sel_alg.elementAt(i) + "\n");
                    
                    //lineas = new StringTokenizer (cadena, "\n\r");
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                        datAct = (String)datasets.elementAt(j);
                        acc = red = kappa = 0.0;
                        for (int k=0; k<10; k++) {
                            /* Filtering the databases with stranges names :-)
                             */
                            vect_str=datAct.split("-10fold");
                            datAct=vect_str[0];
                            /* The search string has the following fiormat:
                             * Alg_name datasets_neme TIMEs
                             */
                            String search_str=sel_alg.elementAt(i).concat(" ").concat(datAct);

                            //mi da problemi con questi 2 DB
                            if (datAct.compareTo("haberman")==0 || datAct.compareTo("new-thyroid")==0)
                                search_str=sel_alg.elementAt(i).concat(" ").concat("unknow");
                            
                            index_str=cadena.indexOf(search_str, index_str);
                            if (index_str==-1){
                                //non ha trovato la search string
                                System.out.println("Error searching the time of the "
                                        + "algorithm "+sel_alg.elementAt(i)+" in the "
                                        + k +"st time");
                                System.exit(1);
                                
                            }else{
                                //we have reached the alg_name's index
                                //searching the dataset name (first space)
                                index_str=cadena.indexOf(" ", index_str);
                                //searching the time (second space)
                                index_time=cadena.indexOf(" ", index_str+1);
                                //searching the end of the time (the "s" character)
                                index_str=cadena.indexOf("s", index_time);
                                //taking the time's value.
                                time[j][i]+=Double.parseDouble(cadena.substring(index_time+1, index_str));     
                            }
                            /*
                            while (cadena.indexOf(dato1, index_str)) {
                                    
                                    
                                    if (linea.startsWith(search_str)) {
                                            tokens = new StringTokenizer (linea," ");
                                            tokens.nextToken();
                                            tokens.nextToken();
                                            dato1 = tokens.nextToken(); //e qui ci sono i secondi (comprensivi della "s" finale
                                            vect_str = dato1.split("s");
                                            dato1=vect_str[0];
                                            System.out.println(dato1);
                                            time[j][i]+=Double.parseDouble(dato1);
                                            break; //devo uscire dal ciclo (ne devo contare solo 10)
                                    }
                            }*/
                        }
                    }
                }
            }   
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


                                    cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tst");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tst");
                                    }


                                    System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    System.out.println("resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tst");
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
        
        public static void calcolo_accurancy_tra(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
                    alAct = (String)algoritmos.elementAt(i);
                    System.out.println("Processing algorithm: " + alAct);
            
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                            datAct = (String)datasets.elementAt(j);
                            salAct = ((Integer)saltos.elementAt(j)).intValue();
                            acc = red = kappa = 0.0;
                            aciertos = 0;
                            total = 0;
                            for (int k=0; k<10; k++) {
                                    /*Accuracy Computation*/


                                    cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tra");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tra");
                                    }


                                    System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    System.out.println("resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tra");
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
                            accuracyTra[j][i] = acc /10;
                            
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

					
					cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0e1.txt");
					
                                        if(cadena.equals("-1")){
						cadena = Fichero.leeFichero("MEDIUM\\resultsFARC\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0e1.txt");
					}
					
					
					System.out.println(datasets.get(j));
                                        //System.out.println(cadena);
                                        System.out.println("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0e1.txt");
					//System.out.println(cadena);
					lineas = new StringTokenizer (cadena, "\n\r");
					aciertos = total = 0;
					while (lineas.hasMoreTokens()) {
						linea = lineas.nextToken();
                                                /* se nella linea sono presenti dei parametri li ignora
                                                 * se sono presenti dei dati li valuta!
                                                 */
						if ((linea.startsWith("@Number of rules:"))) {
							tokens = new StringTokenizer (linea," ");
                                                        tokens.nextToken();
                                                        tokens.nextToken();
                                                        tokens.nextToken();
							dato1 = tokens.nextToken();
                                                        System.out.println(dato1);
                                                        tokens.nextToken();
                                                        tokens.nextToken();
                                                        tokens.nextToken();
                                                        tokens.nextToken();
							tokens.nextToken();
							dato2 = tokens.nextToken();
                                                        System.out.println(dato2);
                                                        
                                                        numeroNodi[j][i]+=Integer.parseInt(dato1);
                                                        antRule[j][i]+=Double.parseDouble(dato2);
							//dato2 = tokens.nextToken();
						}
                                                /*
                                                if ((linea.startsWith("@NumberOfAntecedentsByRule"))) {
							tokens = new StringTokenizer (linea," ");
							dato1 = tokens.nextToken();
                                                        antRule[j][i]+=Double.parseDouble(tokens.nextToken());
							//dato2 = tokens.nextToken();
						}*/
                                                
					}
					
				}

			}

		}
            
        }
        public static void scrivi_tabella(){
            /*Print the results*/
                
		cadena = "\n";
		Fichero.escribeFichero("TablaFARC.txt", cadena);
		for (int i=0; i<datasets.size(); i++) {
                    //cadena = "Datasets\t\t";
                    cadena += datasets.elementAt(i) + "   \t";
                    cadena += "\tacc \t\t#size \t#ant \t\ttime(s)";
                    cadena += "\n";
			//cadena = datasets.elementAt(i) + "\t   \t   \t";
			for (int j=0; j<sel_alg.size(); j++) {
                                cadena += sel_alg.elementAt(j) + "\t\t";
				cadena += String.format("%6.4f", accuracyAv[i][j]) +
                                        "\t"+ String.format("%6.4f", (float)numeroNodi[i][j] / 10) + 
                                        "\t" + String.format("%6.4f", (float)antRule[i][j] / 10) + 
                                        "\t" + String.format("%6.4f", (float)time[i][j]);
                                cadena += "\n";
			}
			cadena += "\n\n\n";	
		}
                Fichero.AnadirtoFichero("TablaFARC.txt", cadena);
        }
        
        public static void write_table_excel() throws IOException, WriteException{
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("ExcelFARC_medium.xls");
            tabla.create("tablaFARC");
            for(int j=0;j<sel_alg.size();j++){
                tabla.addCaption(5*j+1, 0,sel_alg.elementAt(j));
                
                tabla.addCaption(5*j+1, 1, "Acc_tra");
                tabla.addCaption(5*j+2, 1, "Acc_tst");
                tabla.addCaption(5*j+3, 1, "#size");
                tabla.addCaption(5*j+4, 1, "ant");
                if (j>0)
                    tabla.addCaption(5*j+5, 1, "Time(s)");             
                
                for (int i=0; i<datasets.size(); i++) {
                    //writing content
                    tabla.addNumberFirst(5*j+1,i+2, accuracyTra[i][j]);
                    tabla.addNumber(5*j+2,i+2, accuracyAv[i][j]);
                    tabla.addNumber(5*j+3,i+2, (float)numeroNodi[i][j] / 10);
                    tabla.addNumber(5*j+4,i+2, (float)antRule[i][j] / 10);
                if (j>0)
                    tabla.addNumber(5*j+5,i+2, (float)time[i][j]);
                                        
                }
                
            }
            for (int i=0; i<datasets.size(); i++) {
                tabla.addCaption(0, 2+i, datasets.elementAt(i));
            }

            tabla.write();
            
        }
}
