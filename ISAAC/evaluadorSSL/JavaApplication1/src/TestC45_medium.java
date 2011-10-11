
import java.util.*;

// To write on the excell' table
import writer.*;
import java.io.IOException;
import jxl.write.WriteException;

// For the Friedman's test
import jsc.datastructures.MatchedData;
import jsc.relatedsamples.FriedmanTest;

// class created to have indexed arrays of doubles
import myUtility.*;

// imported to change te precision in printing the results.
import java.math.BigDecimal;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public class TestC45_medium {
    static String cadena = "";
    static StringTokenizer lineas, tokens;
    static String linea, dato1, dato2, token, timesplit[];
    static boolean algorithms = true;
    static Vector <String> algoritmos;
    static Vector <String> sel_alg;
    static Vector <String> datasets;

    static Vector <Integer> saltos;
    //static int i, j, k, l, m,n;
    static double accuracyTst[][];
    static double accuracyTra[][];
    
    static double IS_reduction[][];
    static double numberInstancesRed[][];
    
    static double reductionSD[][];
    //static double kappaAv[][];
    //static double kappaSD[][];
    static double accredAv[][];
    static double kapredAv[][];
    static double time[][];

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
    //static int sumKappa, sumi, sumj;
    //static double accSD, redSD, kapSD;
    static String tiempos;
    static int postime;
    
    // variables used to calculate the classificator's complexity
    static double numeroNodi[][];
    static double antRule[][];
    
    // variables for the Friedman's test
    static double [][] ranks_matr;
    static double [] ranks_vect;
    
    // Variables for the new type of table
    static double RedSize[][];
    static double RedSizeXtst[][];
    static double RedXtst[][];

    
    // Element AVG variables
    static ElementIndex elem_AvgTst[];
    static ElementIndex elem_AvgTra[];
    static ElementIndex elem_AvgnumeroNodi[];
    static ElementIndex elem_AvgAntRule[];
    static ElementIndex elem_AvgRedSize[];
    static ElementIndex elem_AvgISRed[];
    static ElementIndex elem_RedSizeXtst[];
    static ElementIndex elem_RedXtst[];
    
    // Element Ranks variables
    static ElementIndex elem_ranks_tst[];
    static ElementIndex elem_ranks_Size[];
    static ElementIndex elem_ranks_ISRed[];
    static ElementIndex elem_ranks_RedSizeXtst[];
    static ElementIndex elem_ranks_ISRedXtst[];
   
    
    public static void main(String[] args) throws IOException, WriteException {
		

		if (args.length != 1) {
			System.err.println("Error. Hace falta un par�metro: Fichero de algoritmos y datasets.");
			System.exit(1);
		}
                // inizialization
                inizializza(args);
                
                // calcultating the performance's parameters&values
                calculate_values();
                
                // calcultating average and averageRanks
                calculteAvg();
                calculteAvgRanks();
                
                //WRITING THE TABLES
                writeTableAvg();
                writeTableAvg_rank();
                write_table_excel();
                writeTable_Bests();
                writeTable_BestsS();
                
                //WRITING THE CSV
                writeCSVs();
                
                //TEST OUTPUT
                //print_redSize();
        }
        private static void calculate_values(){
            calcolo_accurancy();
            calcolo_accurancy_tra();

            calcolo_Red();
            calcolo_nodi_ant();
            calculate_friedman();
            calculate_redAVG();
        }
        
        private static void writeCSVs() throws IOException, WriteException{
            writeCSV_tst();
            writeCSV_Size();
            writeCSV_ISRed();
            writeCSV_RedSizeTst();
            writeCSV_ISRedTst();
        }
        
        
        private static void inizializza(String[] args){
            algoritmos = new Vector <String>();
            datasets = new Vector <String>();
            sel_alg = new Vector <String>();

            saltos = new Vector <Integer>();

            /*Lectura del fichero de configuraci�n*/
            cadena = Fichero.myleeFichero(args[0]);
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

            accuracyTst = new double[datasets.size()][algoritmos.size()];
            accuracyTra = new double[datasets.size()][algoritmos.size()];
            
            IS_reduction = new double[datasets.size()][algoritmos.size()];
            numberInstancesRed = new double[datasets.size()][algoritmos.size()];
            
            reductionSD = new double[datasets.size()][algoritmos.size()];
            //kappaAv = new double[datasets.size()][algoritmos.size()];
            //kappaSD = new double[datasets.size()][algoritmos.size()];
            accredAv = new double[datasets.size()][algoritmos.size()];
            kapredAv = new double[datasets.size()][algoritmos.size()];
            time = new double[datasets.size()][algoritmos.size()];
            
            //new variables
            numeroNodi = new double[datasets.size()][algoritmos.size()];
            antRule    = new double[datasets.size()][algoritmos.size()];
            RedSize    = new double[datasets.size()][algoritmos.size()];
            RedSizeXtst= new double[datasets.size()][algoritmos.size()];
            RedXtst= new double[datasets.size()][algoritmos.size()];
             

            runs = new int[datasets.size()][algoritmos.size()];
            accV = new double[10];
            redV = new double[10];
            kappaV = new double[10];

            //tiempos = Fichero.myleeFichero("tiempos.txt");

            System.out.println("Numbero de algoritmos "+ algoritmos.size());
            /*C�lculo del accuracy, kappa y reducci�n en KNN (TRAIN)*/
            configAct = 0;
            

            
            nomi_selAlg(); 
        }
        
        private static void nomi_selAlg(){
            String alg_name="";
        
            for (int i=0; i<algoritmos.size(); i++) {
                
                alAct = (String)algoritmos.elementAt(i);
                StringTokenizer st = new StringTokenizer(alAct,".");
                System.out.println(alAct);
                alg_name = st.nextToken();
                if (alg_name.compareTo("Ignore-MV")==0)
                    alg_name=st.nextToken();
                sel_alg.addElement(alg_name);
            }
        }
        
        
        private static void calcolo_accurancy(){
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


                                    cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tst");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+".tst");
                                    }


                                    //System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    //System.out.println("MEDIUM\\resultsC45\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tst");
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
                            accuracyTst[j][i] = acc /10;
                            
                        }
            }
        }
        
        private static void calcolo_Red(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
                    alAct = (String)algoritmos.elementAt(i);
                    //System.out.println("Processing algorithm: " + alAct);
            
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                            datAct = (String)datasets.elementAt(j);
                            for (int k=0; k<10; k++) {

                                    String directory="MEDIUM\\reductionFARC\\";
                                    String nomeFile="Ignore-MV";
                                    
                                    if (i!=0){
                                        nomeFile+="."+sel_alg.elementAt(i);
                                         
                                    }        
                                    //nomeFile+="."+datasets.get(j);                                 
                                    String nomeCompleto=directory+nomeFile+"."+datasets.get(j)
                                            +"\\"+nomeFile+"."+datasets.get(j)+"-10-"+k+"tra.dat";
 
                                    cadena = Fichero.myleeFichero(nomeCompleto);
                                    
                                    if(cadena.equals("-1")){
                                        nomeCompleto=directory+nomeFile+"."+datasets.get(j)
                                                +"\\"+nomeFile+"s0."+datasets.get(j)+"-10-"+k+"tra.dat";
                                        cadena = Fichero.myleeFichero(nomeCompleto);
                                    }
                                    
                                    //System.out.println(nomeCompleto);
                                    //System.out.println(nomeFile);
                                    lineas = new StringTokenizer (cadena, "\n\r");

                                    while (lineas.hasMoreTokens()) {
                                        linea = lineas.nextToken();
                                        //linea = lineas.nextToken();
                                        if (!(linea.startsWith("@"))) {
                                            //System.out.println(linea);
                                            numberInstancesRed[j][i]+=1;           
                                            
                                        }
                                    }
                                }
                            //System.out.println(alAct);
                            //System.out.println(numberInstancesRed[j][i]);
                            numberInstancesRed[j][i] = (numberInstancesRed[j][i]/81)*10;
                        }
            }
        }
        
        private static void calcolo_accurancy_tra(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
                    alAct = (String)algoritmos.elementAt(i);
                    //System.out.println("Processing algorithm: " + alAct);
            
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                            datAct = (String)datasets.elementAt(j);
                            salAct = ((Integer)saltos.elementAt(j)).intValue();
                            acc = red = kappa = 0.0;
                            aciertos = 0;
                            total = 0;
                            for (int k=0; k<10; k++) {
                                    /*Accuracy Computation*/


                                    cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tra");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+".tra");
                                    }


                                    //System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    //System.out.println("MEDIUM\\resultsC45\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+".tra");
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
        
        
        private static void calcolo_nodi_ant(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
		for (int i=0; i<algoritmos.size(); i++) {
			alAct = (String)algoritmos.elementAt(i);
			//System.out.println("Processing algorithm: " + alAct);
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

					
					cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"e0.txt");
					
                                        if(cadena.equals("-1")){
						cadena = Fichero.myleeFichero("MEDIUM\\resultsC45\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"e0.txt");
					}
					
					
					//System.out.println(datasets.get(j));
                                        //System.out.println(cadena);
                                        //System.out.println("resultsC45\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"e0.txt");
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
        private static void scrivi_tabella(){
            /*Print the results*/
                
		cadena = "\n";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\TablaC45.txt", cadena);
		for (int i=0; i<datasets.size(); i++) {
                    //cadena = "Datasets\t\t";
                    cadena += datasets.elementAt(i) + "\t   \t  \t";
                    cadena += "\t\tacc_tra \tacc \t\t #size \t #ant";
                    cadena += "\n";
			//cadena = datasets.elementAt(i) + "\t   \t   \t";
			for (int j=0; j<algoritmos.size(); j++) {
                                cadena += algoritmos.elementAt(j) + "\t\t";
				cadena += String.format("%6.4f", accuracyTra[i][j]) +
                                        "\t"+ String.format("%6.4f", accuracyTst[i][j]) + 
                                        "\t"+ String.format("%6.4f", numeroNodi[i][j]/20) + 
                                        "\t" + String.format("%6.4f",antRule[i][j]/10)+ "\t";
                                cadena += "\n";
			}
			cadena += "\n\n\n";	
		}
                Fichero.AnadirtoFichero("TablaC45.txt", cadena);
        }
        
        private static void write_table_excel() throws IOException, WriteException{
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("MEDIUM\\Tablas\\C45\\ExcelC45_medium.xls");
            tabla.create("tablaC45");
            for(int j=0;j<sel_alg.size();j++){
                tabla.addCaption(6*j+1, 0,sel_alg.elementAt(j));
                
                tabla.addCaption(6*j+1, 1, "Acc_tra");
                tabla.addCaption(6*j+2, 1, "Acc_tst");
                tabla.addCaption(6*j+3, 1, "Rank");
                tabla.addCaption(6*j+4, 1, "IS_Red");           
                tabla.addCaption(6*j+5, 1, "#size");
                tabla.addCaption(6*j+6, 1, "ant");
                //if (j>0)
                //    tabla.addCaption(5*j+5, 1, "Time(s)");             
                
                for (int i=0; i<datasets.size(); i++) {
                    //writing content
                    tabla.addNumberFirst(6*j+1,i+2, accuracyTra[i][j]);
                    tabla.addNumber(6*j+2,i+2, accuracyTst[i][j]);
                    tabla.addNumber(6*j+3,i+2, ranks_matr[i][j]);
                    tabla.addNumber(6*j+4,i+2, IS_reduction[i][j]);
                    tabla.addNumber(6*j+5,i+2, numeroNodi[i][j] / 20);
                    tabla.addNumber(6*j+6,i+2, antRule[i][j] / 10);
                //if (j>0)
                //    tabla.addNumber(5*j+5,i+2, (float)time[i][j]);
                                        
                }
                tabla.addNumber(6*j+3,datasets.size()+2, ranks_vect[j]);
                
            }
            for (int i=0; i<datasets.size(); i++) {
                tabla.addCaption(0, 2+i, datasets.elementAt(i));
            }
            tabla.addCaption(0, 2+datasets.size(), "avg_Rank");

            tabla.write();
        }
        
        private static void writeTable_Bests()throws IOException, WriteException{
           MyExcelWriter tabla = new MyExcelWriter();
           tabla.setOutputFile("MEDIUM\\Tablas\\C45\\ExcelC45_mediumBests.xls");
           tabla.create("tablaC45");
           
           for (int i=0; i<datasets.size(); i++){
               //writing the names of the datasets
               tabla.addString12pt(0, 1+i, datasets.elementAt(i));
               /* We are writing the values of the algorithms that perform better 
                * equal to the C45 Algorithm (without the Instance selection)
                */
               ElementIndex elem_dataset_rank[]=ArrayIndex.createArray(ranks_matr[i]);
               Arrays.sort(elem_dataset_rank);
               
               /* Still there's an algorithm with a rank equal or bigger then the 
                * C45's algorithm continues the loop.
                */
               for(int j=0;elem_dataset_rank[j].getValue()<=ranks_matr[i][0];j++){
                   BigDecimal b; int precisionBig=3;int precision=2;
                   
                   int index=elem_dataset_rank[j].getIndex();
                   double redSize= 1 - (numeroNodi[i][index]/numeroNodi[i][0]);
                   if (redSize>0)
                       tabla.addCaption(j*4+1,1+i, sel_alg.elementAt(index));
                   else
                       tabla.addString(j*4+1,1+i, sel_alg.elementAt(index));
                   
                   b=new BigDecimal(accuracyTst[i][index]).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+2,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+2, 0, "Tst");
                   
                   b=new BigDecimal(numeroNodi[i][index]/20).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+3,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+3, 0, "#Sz");
                   
                   b=new BigDecimal(redSize).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+4,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+4, 0, "Red");
               }
            }
            tabla.write();
        }
        
        private static void writeTable_BestsS()throws IOException, WriteException{
           MyExcelWriter tabla = new MyExcelWriter();
           tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelC45_smallBestsS.xls");
           tabla.create("tablaC45");
        
           for (int i=0; i<datasets.size(); i++){
               //writing the names of the datasets
               tabla.addString12pt(0, 1+i, datasets.elementAt(i));
               /* We are writing the values of the algorithms that perform better 
                * equal to the FARC Algorithm (without the Instance selection)
                */
               ElementIndex elem_dataset_rank[]=ArrayIndex.createArray(ranks_matr[i]);
               Arrays.sort(elem_dataset_rank);
        
               /* Still there's an algorithm with a rank equal or bigger then the 
                * FARC's algorithm continues the loop.
                */
               for(int j=0;elem_dataset_rank[j].getValue()<=ranks_matr[i][0];j++){
                   BigDecimal b; 
                   int index=elem_dataset_rank[j].getIndex();
                   double redSize= 1 - (numeroNodi[i][index]/numeroNodi[i][0]);
                   if (redSize>0)
                       tabla.addCaption(j*1+6,1+i, sel_alg.elementAt(index));
                   else
                       tabla.addString(j*1+6,1+i, sel_alg.elementAt(index));
               }
            }
            tabla.write();
        }
        
        private static void writeCSV_tst() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_tst.csv", cadena);
                cadena = "Test";
                for(int j=0;j<sel_alg.size();j++){
                cadena += "," + sel_alg.elementAt(j);
                }
                for (int i=0; i<datasets.size(); i++) {
                    cadena += "\n" +datasets.elementAt(i);
                    for (int j=0; j<algoritmos.size(); j++){
                        //cadena +=","+String.format("%6.4f", accuracyTst[i][j]);
                        cadena +="," +accuracyTst[i][j];
                    }                       
                }
                cadena += "\n";
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_tst.csv", cadena);
        }
        private static void writeCSV_ISRed() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_ISRed.csv", cadena);
                cadena = "ISRed";
                for(int j=0;j<sel_alg.size();j++){
                cadena += "," + sel_alg.elementAt(j);
                }
                for (int i=0; i<datasets.size(); i++) {
                    cadena += "\n" +datasets.elementAt(i);
                    for (int j=0; j<algoritmos.size(); j++){
                        //cadena +=","+String.format("%6.4f", accuracyTst[i][j]);
                        cadena +="," +IS_reduction[i][j];
                    }                       
                }
                cadena += "\n";
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_ISRed.csv", cadena);
        }
        private static void writeCSV_Size() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_Size.csv", cadena);
                cadena = "Size";
                for(int j=0;j<sel_alg.size();j++){
                cadena += "," + sel_alg.elementAt(j);
                }
                for (int i=0; i<datasets.size(); i++) {
                    cadena += "\n" +datasets.elementAt(i);
                    for (int j=0; j<algoritmos.size(); j++){
                        //cadena +=","+String.format("%6.4f", accuracyTst[i][j]);
                        cadena +="," +numeroNodi[i][j]/20;
                    }                       
                }
                cadena += "\n";
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_Size.csv", cadena);
        }
        
        private static void writeCSV_ISRedTst() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_ISRedTst.csv", cadena);
                cadena = "ISRedTst";
                for(int j=0;j<sel_alg.size();j++){
                cadena += "," + sel_alg.elementAt(j);
                }
                for (int i=0; i<datasets.size(); i++) {
                    cadena += "\n" +datasets.elementAt(i);
                    for (int j=0; j<algoritmos.size(); j++){
                        //cadena +=","+String.format("%6.4f", accuracyTst[i][j]);
                        cadena +="," +RedXtst[i][j];
                    }                       
                }
                cadena += "\n";
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_ISRedTst.csv", cadena);
        }
        private static void writeCSV_RedSizeTst() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_RedSizeTst.csv", cadena);
                cadena = "RedSizeTst";
                for(int j=0;j<sel_alg.size();j++){
                cadena += "," + sel_alg.elementAt(j);
                }
                for (int i=0; i<datasets.size(); i++) {
                    cadena += "\n" +datasets.elementAt(i);
                    for (int j=0; j<algoritmos.size(); j++){
                        //cadena +=","+String.format("%6.4f", accuracyTst[i][j]);
                        cadena +="," +RedSizeXtst[i][j];
                    }                       
                }
                cadena += "\n";
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\C45\\CSV\\C45_medium_RedSizeTst.csv", cadena);
        }
        
        
        /*
        public static void writeExcel_tst() throws IOException, WriteException{
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("ExcelC45_medium_tst.xls");
            tabla.create("tablaC45");
            for(int j=0;j<sel_alg.size();j++){
                tabla.addCaption(j+1, 0,sel_alg.elementAt(j));
                
                //tabla.addCaption(j+1, 1, "Acc_tst");
                
                for (int i=0; i<datasets.size(); i++) {
                    //writing content
                    tabla.addNumberFirst(j+1,i+1, accuracyTst[i][j]);
                }          
            }
            for (int i=0; i<datasets.size(); i++) {
                tabla.addCaption(0, 1+i, datasets.elementAt(i));
            }
            
            tabla.write();
        }
        */
        
        private static double [][] calc_matrix(double [][] m){
          int r = m.length;
          int c = m[0].length;
          double [][] t = new double[r][c];
          for(int i = 0; i < r; ++i){
            for(int j = 0; j < c; ++j){
              t[i][j] = 1 - m[i][j];
            }
          }
          return t;
        }
        
        private static void calculate_friedman(){
            /* calc_matrix is used 'cause the datas are reversed in order by the
             * Friedmann's function.
             * The function calculate_rank returns the Friedmann's ranks.
             */
            ranks_matr= Friedmann.calculate_rank(calc_matrix(accuracyTst));
            ranks_vect= new double [sel_alg.size()];
            
            for (int i=0;i<datasets.size();i++){
                for (int j=0; j<sel_alg.size();j++){
                    ranks_vect[j]+=ranks_matr[i][j]/datasets.size();
                } 
            }           
        }
        
        private static void calculteAvgRanks(){
            double ranks_tst[][]        = Friedmann.calculate_rank(calc_matrix(accuracyTst));
            double ranks_Size[][]       = Friedmann.calculate_rank(numeroNodi);
            double ranks_ISRed[][]      = Friedmann.calculate_rank(calc_matrix(IS_reduction));
            double ranks_RedSizeXtst[][]= Friedmann.calculate_rank(calc_matrix(RedSizeXtst));
            double ranks_RedXtst[][]    = Friedmann.calculate_rank(calc_matrix(RedXtst));
            

            double avg_ranksTst[]        = new double[algoritmos.size()];
            double avg_ranksSize[]       = new double[algoritmos.size()];
            double avg_ranksISRed[]       = new double[algoritmos.size()];
            double avg_ranksRedSizeXtst[]= new double[algoritmos.size()];
            double avg_ranksRedXtst[]    = new double[algoritmos.size()];

            for (int j=0; j<algoritmos.size(); j++) {

                for (int i=0; i<datasets.size(); i++) {
                    avg_ranksTst[j]         += ranks_tst[i][j];
                    avg_ranksSize[j]        += ranks_Size[i][j];
                    avg_ranksISRed[j]       += ranks_ISRed[i][j];
                    avg_ranksRedSizeXtst[j] += ranks_RedSizeXtst[i][j];
                    avg_ranksRedXtst[j]     += ranks_RedXtst[i][j];
                }
                
                avg_ranksTst[j]         /= datasets.size();
                avg_ranksSize[j]        /= datasets.size();
                avg_ranksISRed[j]       /= datasets.size();
                avg_ranksRedSizeXtst[j] /= datasets.size();
                avg_ranksRedXtst[j]     /= datasets.size();

            }
            // Creating the arrays with indeces
            elem_ranks_tst        =ArrayIndex.createArray(avg_ranksTst);
            elem_ranks_Size       =ArrayIndex.createArray(avg_ranksSize);
            elem_ranks_ISRed      =ArrayIndex.createArray(avg_ranksISRed);
            elem_ranks_RedSizeXtst=ArrayIndex.createArray(avg_ranksRedSizeXtst);
            elem_ranks_ISRedXtst  =ArrayIndex.createArray(avg_ranksRedXtst);
            
            // Sorting the arrays
            Arrays.sort(elem_ranks_tst);
            Arrays.sort(elem_ranks_Size);
            Arrays.sort(elem_ranks_ISRed);
            Arrays.sort(elem_ranks_RedSizeXtst);
            Arrays.sort(elem_ranks_ISRedXtst);

        
        }
        private static void calculteAvg(){
            // Creating AVG vectors;
            double avg_accuracyTst[]= new double[algoritmos.size()];
            double avg_accuracyTra[]= new double[algoritmos.size()];
            double avg_numeroNodi[] = new double[algoritmos.size()];
            double avg_antRule[]    = new double[algoritmos.size()];
            double avg_RedSize[]    = new double[algoritmos.size()];
            double avg_IS_Red[]     = new double[algoritmos.size()];
            
            double avg_RedSizeXtst[]= new double[algoritmos.size()];
            double avg_RedXtst[]    = new double[algoritmos.size()];
            
            for (int j=0; j<algoritmos.size(); j++) {
                for (int i=0; i<datasets.size(); i++) {
                    avg_accuracyTra[j] += accuracyTra[i][j];
                    avg_accuracyTst[j] += accuracyTst[i][j];
                    avg_numeroNodi[j]  += numeroNodi[i][j];
                    avg_antRule[j]     += antRule[i][j];
                    avg_RedSize[j]     += RedSize[i][j];
                    avg_IS_Red[j]      += IS_reduction[i][j];
                    
                    avg_RedSizeXtst[j] += RedSizeXtst[i][j];
                    avg_RedXtst[j]     += RedXtst[i][j];
                }
                avg_accuracyTra[j] /= datasets.size();
                avg_accuracyTst[j] /= datasets.size();
                
                avg_numeroNodi[j]  /= datasets.size();
                avg_numeroNodi[j]  /= 20;
                
                avg_antRule[j]     /= datasets.size();
                avg_antRule[j]     /= 10;
                
                avg_RedSize[j]     /= datasets.size();
                avg_IS_Red[j]      /= datasets.size();
                
                avg_RedSizeXtst[j] /= datasets.size();
                avg_RedXtst[j]     /= datasets.size();
                // product redSize*acc_test
                //avg_RedSizeXtst[j]  = avg_RedSize[j] * avg_accuracyTst[j];
            }
            // Creating the arrays with indeces
            elem_AvgTra         =ArrayIndex.createArray(avg_accuracyTra);
            elem_AvgTst         =ArrayIndex.createArray(avg_accuracyTst);
            elem_AvgnumeroNodi  =ArrayIndex.createArray(avg_numeroNodi);
            elem_AvgAntRule     =ArrayIndex.createArray(avg_antRule);
            elem_AvgRedSize     =ArrayIndex.createArray(avg_RedSize);
            elem_AvgISRed       =ArrayIndex.createArray(avg_IS_Red);
            
            elem_RedSizeXtst    =ArrayIndex.createArray(avg_RedSizeXtst);
            elem_RedXtst        =ArrayIndex.createArray(avg_RedXtst);
            
            // Sorting the arrays
            Arrays.sort(elem_AvgTra, Collections.reverseOrder());
            Arrays.sort(elem_AvgTst, Collections.reverseOrder());
            Arrays.sort(elem_AvgnumeroNodi);
            Arrays.sort(elem_AvgAntRule);
            Arrays.sort(elem_AvgRedSize, Collections.reverseOrder());
            Arrays.sort(elem_AvgISRed, Collections.reverseOrder());
            
            Arrays.sort(elem_RedSizeXtst, Collections.reverseOrder());
            Arrays.sort(elem_RedXtst, Collections.reverseOrder());
        
        }
        
        private static void calculate_redAVG(){
            for (int i=0; i<datasets.size(); i++) {
                for(int j=0;j<sel_alg.size();j++){
                    RedSize     [i][j]= 1 - (numeroNodi[i][j]/numeroNodi[i][0]); 
                    RedSizeXtst [i][j]= RedSize[i][j]*accuracyTst[i][j];
                    
                    IS_reduction[i][j] = 1-(numberInstancesRed[i][j]/numberInstancesRed[i][0]);
                    RedXtst [i][j]= IS_reduction[i][j]*accuracyTst[i][j];
                }   
            }
        }
        
        
        private static void writeTableAvg()throws IOException, WriteException{
            
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("MEDIUM\\Tablas\\C45\\ExcelC45_avg_medium.xls");
            tabla.create("tablaC45");
            
            tabla.addStringBig(0, 0, "Accuracy_tra");
            tabla.addStringBig(2, 0, "Accuracy_tst");
            tabla.addStringBig(4, 0, "# Nodes");
            tabla.addStringBig(6, 0, "# Antecedents");
            tabla.addStringBig(8, 0, "IS_Reduction");
            tabla.addStringBig(10, 0, "RedSize");
            tabla.addStringBig(12, 0, "ISRed*a_tst");
            tabla.addStringBig(14, 0, "RedSz*a_tst");
            
            for(int j=0;j<sel_alg.size();j++){
                BigDecimal b; int precisionBig=3;int precision=2;
                // Accuracy training
                b=new BigDecimal(elem_AvgTra[j].getValue()).setScale(precisionBig,BigDecimal.ROUND_HALF_UP);
                tabla.addString(0,1+j, sel_alg.elementAt(elem_AvgTra[j].getIndex()));
                //tabla.addNumber(1,1+j, elem_AvgTra[j].getValue());
                tabla.addNumber(1,1+j, b.doubleValue());
                
                // Accuracy test
                b=new BigDecimal(elem_AvgTst[j].getValue()).setScale(precisionBig,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(2,1+j, sel_alg.elementAt(elem_AvgTst[j].getIndex()));
                //tabla.addNumber(3,1+j, elem_AvgTst[j].getValue());
                tabla.addNumber(3,1+j, b.doubleValue());
                
                // size
                b=new BigDecimal(elem_AvgnumeroNodi[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(4,1+j, sel_alg.elementAt(elem_AvgnumeroNodi[j].getIndex()));
                //tabla.addNumber(5,1+j, elem_AvgnumeroNodi[j].getValue());
                tabla.addNumber(5,1+j, b.doubleValue());
                
                // ant
                b=new BigDecimal(elem_AvgAntRule[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(6,1+j, sel_alg.elementAt(elem_AvgAntRule[j].getIndex()));
                //tabla.addNumber(7,1+j, elem_AvgAntRule[j].getValue());
                tabla.addNumber(7,1+j,  b.doubleValue());
                
                // IS_Reduction
                b=new BigDecimal(elem_AvgISRed[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(8,1+j, sel_alg.elementAt(elem_AvgISRed[j].getIndex()));
                //tabla.addNumber(9,1+j, elem_AvgISRed[j].getValue());
                tabla.addNumber(9,1+j, b.doubleValue());
                
                // redSize
                b=new BigDecimal(elem_AvgRedSize[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(10,1+j, sel_alg.elementAt(elem_AvgRedSize[j].getIndex()));
                //tabla.addNumber(11,1+j, elem_AvgRedSize[j].getValue());
                tabla.addNumber(11,1+j,  b.doubleValue());
                
                // ISRed*a_tst
                b=new BigDecimal(elem_RedXtst[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(12,1+j, sel_alg.elementAt(elem_RedXtst[j].getIndex()));
                //tabla.addNumber(13,1+j, elem_RedXtst[j].getValue());
                tabla.addNumber(13,1+j, b.doubleValue());
                
                // RedSize*a_tst
                b=new BigDecimal(elem_RedSizeXtst[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(14,1+j, sel_alg.elementAt(elem_RedSizeXtst[j].getIndex()));
                //tabla.addNumber(15,1+j, elem_RedSizeXtst[j].getValue());
                tabla.addNumber(15,1+j, b.doubleValue());
                
            }
            tabla.write();
        }
        
        
        private static void writeTableAvg_rank()throws IOException, WriteException{
            
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("MEDIUM\\Tablas\\C45\\ExcelC45_Rank_medium.xls");
            tabla.create("tablaC45");
            
            tabla.addString12pt(0, 0, "AvgRank_test");
            tabla.addString12pt(2, 0, "AvgRank_ISRed");
            tabla.addString12pt(4, 0, "AvgRank_Size");
            tabla.addString12pt(6, 0, "AvgR_ISRed*tst");
            tabla.addString12pt(8, 0, "AvgR_RedSize*tst");

            
            for(int j=0;j<sel_alg.size();j++){
                // Accuracy training
                tabla.addString(0,1+j, sel_alg.elementAt(elem_ranks_tst[j].getIndex()));
                tabla.addNumber(1,1+j, elem_ranks_tst[j].getValue());
                // Accuracy test
                tabla.addStringBordL(2,1+j, sel_alg.elementAt(elem_ranks_ISRed[j].getIndex()));
                tabla.addNumber(3,1+j, elem_ranks_ISRed[j].getValue()); 
                // Accuracy test
                tabla.addStringBordL(4,1+j, sel_alg.elementAt(elem_ranks_Size[j].getIndex()));
                tabla.addNumber(5,1+j, elem_ranks_Size[j].getValue()); 
                // AvgRank_ISRedXtst
                tabla.addStringBordL(6,1+j, sel_alg.elementAt(elem_ranks_ISRedXtst[j].getIndex()));
                tabla.addNumber(7,1+j, elem_ranks_ISRedXtst[j].getValue());
                // AvgRank_RedSize*tst
                tabla.addStringBordL(8,1+j, sel_alg.elementAt(elem_ranks_RedSizeXtst[j].getIndex()));
                tabla.addNumber(9,1+j, elem_ranks_RedSizeXtst[j].getValue()); 
            }
            tabla.write();
        }
        
        
        private static void print_redSize(){
            for (int i=0; i<datasets.size(); i++) {
                for(int j=0;j<sel_alg.size();j++){
                    if (RedSize[i][j]<=0)
                        System.out.println("dataset:"+datasets.elementAt(i) +
                                " algorithm:"+sel_alg.elementAt(j) +" value:"+RedSize[i][j]);
                }
            }
        }
}
