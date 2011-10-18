
import java.util.*;


//imported to use the excell library
import writer.*;
import java.io.IOException;
import jxl.write.WriteException;

// class created to have indexed arrays of doubles
import myUtility.*;

// imported to change te precision in printing the results.
import java.math.BigDecimal;

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
    static Vector <String> sel_alg_largo;
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
    static ElementIndex elem_Time[];
    
    // Element Ranks variables
    static ElementIndex elem_ranks_tst[];
    static ElementIndex elem_ranks_Size[];
    static ElementIndex elem_ranks_ISRed[];
    static ElementIndex elem_ranks_RedSizeXtst[];
    static ElementIndex elem_ranks_ISRedXtst[];
    
    static DB_info datasets_info [];   
    
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
                System.out.println("calcolo AVG");
                calculteAvg();
                System.out.println("calcolo AVGRanks" );
                calculteAvgRanks();
                
                //WRITING THE TABLES
                System.out.println("writeTableAvg" );
                writeTableAvg();
                System.out.println("writeAvgRank" );
                writeTableAvg_rank();
                System.out.println("write_table_excel" );
                write_table_excel();
                System.out.println("writeTable_Bests" );
                writeTable_Bests();
                System.out.println("writeTable_BestsS" );
                writeTable_BestsS();
                
                //WRITING THE CSV
                System.out.println("writeCSV" );
                writeCSVs();
                
                //TEST OUTPUT
                //print_redSize();
        }
    private static void calculate_values(){
            calcolo_tempo();
            System.out.println("calcolo acc_tst");
            calcolo_accurancy();
            System.out.println("calcolo acc_tra");
            calcolo_accurancy_tra();
    
            System.out.println("calcolo red");
            calcolo_Red();
            
            System.out.println("calcolo nodi_ant");
            calcolo_nodi_ant();
            
            System.out.println("calcolo friedman");
            calculate_friedman();
            System.out.println("calcolo RedAVG");
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
            sel_alg_largo = new Vector <String>();

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
             
            datasets_info= new DB_info[datasets.size()];

            runs = new int[datasets.size()][algoritmos.size()];
            accV = new double[10];
            redV = new double[10];
            kappaV = new double[10];

            //tiempos = Fichero.myleeFichero("tiempos.txt");

            System.out.println("Numero de algoritmos "+ algoritmos.size());
            /*C�lculo del accuracy, kappa y reducci�n en KNN (TRAIN)*/
            configAct = 0;
            
            nomi_selAlg();
            generate_DBvector();
        }
        
        private static void nomi_selAlg(){
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
        
            for (int i=0; i<algoritmos.size(); i++) {
                
                alAct = (String)algoritmos.elementAt(i);
                StringTokenizer st = new StringTokenizer(alAct,".");
                
                alg_name = st.nextToken();
                if (alg_name.compareTo("Ignore-MV")==0)
                    alg_name=st.nextToken();
                sel_alg_largo.addElement(alg_name);
            }
            
        }
        
        public static void generate_DBvector(){
            
            cadena = Fichero.myleeFichero("MEDIUM\\Tablas\\datasets.txt");
            if (cadena.equals("-1")){
                System.out.println("file 'datasets.txt' not found");
                System.exit(-1);
            }
            lineas = new StringTokenizer (cadena,"\n\r");
            for (int i=0;lineas.hasMoreTokens();i++){
                linea = lineas.nextToken();
                //System.out.println(i);
                tokens = new StringTokenizer (linea,"\t");
                datasets_info[i]=new DB_info();
                
                datasets_info[i].name = tokens.nextToken();
                datasets_info[i].IS_num = tokens.nextToken();
                datasets_info[i].classNum = Integer.parseInt(tokens.nextToken());
                datasets_info[i].numericAttr = Integer.parseInt(tokens.nextToken());
                datasets_info[i].nominalAttr = Integer.parseInt(tokens.nextToken());
                datasets_info[i].IS_div_attr = Double.parseDouble(tokens.nextToken().replaceAll(",","."));
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
                //System.out.println(alAct);
                
                int index_str=0;
                int index_time=0;
                
                if (st.countTokens()==3){
                
                cadena = Fichero.myleeFichero("MEDIUM\\outFARC\\"
                                +alAct+"\\sge_output.dat");
                

                    //a qsto punto dovrei avere il nome dell'algoritmo

                    System.out.println("Calculating time algorithm: " + alAct+"\n");
                    //System.out.println("nome algoritmo:" + sel_alg.elementAt(i) + "\n");
                    
                    //lineas = new StringTokenizer (cadena, "\n\r");
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                        datAct = (String)datasets.elementAt(j);
                        acc = red = kappa = 0.0;
                        index_str = 0;
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
                                        + k +"st time"+ " for the dataset "+ datAct );
                                System.out.println("search string is: "
                                        + search_str );
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

                        }
                    }
                }
                System.out.println("END Calculating time algorithm: " + alAct+"\n");
            }   
        }
                    

        
        
        public static void calcolo_accurancy(){
            /* Istruzioni eseguite per ognuno degli algoritmi */
            for (int i=0; i<algoritmos.size(); i++) {
                    alAct = (String)algoritmos.elementAt(i);
                    //System.out.println("Acc_tst algorithm: " + alAct);
                    
                    /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                    for (int j=0; j<datasets.size(); j++) {
                            datAct = (String)datasets.elementAt(j);
                            //System.out.println("Acc_tst dataset: " + datAct);
                            salAct = ((Integer)saltos.elementAt(j)).intValue();
                            acc = red = kappa = 0.0;
                            aciertos = 0;
                            total = 0;
                            for (int k=0; k<10; k++) {
                                    /*Accuracy Computation*/


                                    cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tst");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tst");
                                    }
                                    
                                    //System.out.println("Acc_tst file: " + k);

                                    //System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    //System.out.println("resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tst");
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
                                        nomeFile+="."+sel_alg_largo.elementAt(i);
                                         
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


                                    cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"
                                            +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tra");

                                    if(cadena.equals("-1")){
                                            cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tra");
                                    }


                                    //System.out.println(datasets.get(j));
                                    //System.out.println(cadena);
                                    //System.out.println("resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tra");
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
			//System.out.println("Processing algorithm: " + alAct);
		
                        /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
			for (int j=0; j<datasets.size(); j++) {
				datAct = (String)datasets.elementAt(j);
				salAct = ((Integer)saltos.elementAt(j)).intValue();
				acc = red = kappa = 0.0;
				for (int k=0; k<10; k++) {
					/*Accuracy Computation*/

					
					cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0e1.txt");
					
                                        if(cadena.equals("-1")){
						cadena = Fichero.myleeFichero("MEDIUM\\resultsFARC\\"
                                                +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0e1.txt");
					}
					
					
					//System.out.println(datasets.get(j));
                                        //System.out.println(cadena);
                                        //System.out.println("MEDIUM\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0e1.txt");
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
                                                        //System.out.println(dato1);
                                                        tokens.nextToken();
                                                        tokens.nextToken();
                                                        tokens.nextToken();
                                                        tokens.nextToken();
							tokens.nextToken();
							dato2 = tokens.nextToken();
                                                        //System.out.println(dato2);
                                                        
                                                        numeroNodi[j][i]+=Integer.parseInt(dato1);
                                                        if (dato2.compareTo("NaN")!=0)
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
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\TablaFARC.txt", cadena);
		for (int i=0; i<datasets.size(); i++) {
                    //cadena = "Datasets\t\t";
                    cadena += datasets.elementAt(i) + "   \t";
                    cadena += "\tacc \t\t#size \t#ant \t\ttime(s)";
                    cadena += "\n";
			//cadena = datasets.elementAt(i) + "\t   \t   \t";
			for (int j=0; j<sel_alg.size(); j++) {
                                cadena += sel_alg.elementAt(j) + "\t\t";
				cadena += String.format("%6.4f", accuracyTst[i][j]) +
                                        "\t"+ String.format("%6.4f", (float)numeroNodi[i][j] / 10) + 
                                        "\t" + String.format("%6.4f", (float)antRule[i][j] / 10) + 
                                        "\t" + String.format("%6.4f", (float)time[i][j]);
                                cadena += "\n";
			}
			cadena += "\n\n\n";	
		}
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\TablaFARC.txt", cadena);
        }
        
        public static void write_table_excel() throws IOException, WriteException{
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelFARC_medium.xls");
            tabla.create("tablaFARC");
            for(int j=0;j<sel_alg.size();j++){
                tabla.addCaption(7*j+1, 0,sel_alg.elementAt(j));
                
                tabla.addCaption(7*j+1, 1, "Acc_tra");
                tabla.addCaption(7*j+2, 1, "Acc_tst");
                tabla.addCaption(7*j+3, 1, "Rank");
                tabla.addCaption(7*j+4, 1, "IS_Red");           
                tabla.addCaption(7*j+5, 1, "#size");
                tabla.addCaption(7*j+6, 1, "ant");
                if (j>0)
                    tabla.addCaption(7*j+7, 1, "Time(s)");             
                
                for (int i=0; i<datasets.size(); i++) {
                    //writing content
                    tabla.addNumberFirst(7*j+1,i+2, accuracyTra[i][j]);
                    tabla.addNumber(7*j+2,i+2, accuracyTst[i][j]);
                    tabla.addNumber(7*j+3,i+2, ranks_matr[i][j]);
                    tabla.addNumber(7*j+4,i+2, IS_reduction[i][j]);
                    tabla.addNumber(7*j+5,i+2, numeroNodi[i][j] / 20);
                    tabla.addNumber(7*j+6,i+2, antRule[i][j] / 10);
                if (j>0)
                    tabla.addNumber(7*j+7,i+2, (float)time[i][j] / 10);
                                        
                }
                tabla.addNumber(7*j+3,datasets.size()+2, ranks_vect[j]);
                
            }
            for (int i=0; i<datasets.size(); i++) {
                tabla.addCaption(0, 2+i, datasets.elementAt(i));
            }
            tabla.addCaption(0, 2+datasets.size(), "avg_Rank");

            tabla.write();
        }
            
        private static void writeTable_Bests()throws IOException, WriteException{
           MyExcelWriter tabla = new MyExcelWriter();
           tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelFARC_mediumBests.xls");
           tabla.create("tablaFARC");
           
           tabla.addString12pt(1, 0, "#IS");
           tabla.addString12pt(2, 0, "#class");
           tabla.addString12pt(3, 0, "#num");
           tabla.addString12pt(4, 0, "#nom");
           tabla.addString12pt(5, 0, "IS/(num+nom)");
           for (int i=0; i<datasets.size(); i++){
               //writing the datasets' info
               tabla.addString12pt(0, 1+i, datasets_info[i].name);
               tabla.addString(1, 1+i, datasets_info[i].IS_num);
               tabla.addNumber(2, 1+i, datasets_info[i].classNum);
               tabla.addNumber(3, 1+i, datasets_info[i].numericAttr);
               tabla.addNumber(4, 1+i, datasets_info[i].nominalAttr);
               tabla.addNumberBordR(5, 1+i, datasets_info[i].IS_div_attr);
               
               /* We are writing the values of the algorithms that perform better 
                * equal to the FARC Algorithm (without the Instance selection)
                */
               ElementIndex elem_dataset_rank[]=ArrayIndex.createArray(ranks_matr[i]);
               Arrays.sort(elem_dataset_rank);
        
               /* Still there's an algorithm with a rank equal or bigger then the 
                * FARC's algorithm continues the loop.
                */
               for(int j=0;elem_dataset_rank[j].getValue()<=ranks_matr[i][0];j++){
                   BigDecimal b; int precisionBig=3;int precision=2;
                   
                   int index=elem_dataset_rank[j].getIndex();
                   double redSize= 1 - (numeroNodi[i][index]/numeroNodi[i][0]);
                   if (redSize>0)
                       tabla.addCaption(j*4+6,1+i, sel_alg.elementAt(index));
                   else
                       tabla.addString(j*4+6,1+i, sel_alg.elementAt(index));
                   
                   b=new BigDecimal(accuracyTst[i][index]).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+7,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+7, 0, "Tst");
                   
                   b=new BigDecimal(numeroNodi[i][index]/20).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+8,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+8, 0, "#Sz");
                   
                   b=new BigDecimal(redSize).setScale(precision,BigDecimal.ROUND_HALF_UP);
                   tabla.addNumber(j*4+9,1+i, b.doubleValue()); 
                   tabla.addString12pt(j*4+9, 0, "Red");
               }
            }
            tabla.write();
        }
        
        private static void writeTable_BestsS()throws IOException, WriteException{
           MyExcelWriter tabla = new MyExcelWriter();
           tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelFARC_mediumBestsS.xls");
           tabla.create("tablaFARC");
        
           tabla.addString12pt(1, 0, "#IS");
           tabla.addString12pt(2, 0, "#class");
           tabla.addString12pt(3, 0, "#num");
           tabla.addString12pt(4, 0, "#nom");
           tabla.addString12pt(5, 0, "IS/(num+nom)");
           for (int i=0; i<datasets.size(); i++){
               //writing the datasets' info
               tabla.addString12pt(0, 1+i, datasets_info[i].name);
               tabla.addString(1, 1+i, datasets_info[i].IS_num);
               tabla.addNumber(2, 1+i, datasets_info[i].classNum);
               tabla.addNumber(3, 1+i, datasets_info[i].numericAttr);
               tabla.addNumber(4, 1+i, datasets_info[i].nominalAttr);
               tabla.addNumberBordR(5, 1+i, datasets_info[i].IS_div_attr);
               
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
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_tst.csv", cadena);
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
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_tst.csv", cadena);
        }
        private static void writeCSV_ISRed() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_ISRed.csv", cadena);
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
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_ISRed.csv", cadena);
        }
        private static void writeCSV_Size() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_Size.csv", cadena);
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
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_Size.csv", cadena);
        }
        
        private static void writeCSV_ISRedTst() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_ISRedTst.csv", cadena);
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
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_ISRedTst.csv", cadena);
        }
        private static void writeCSV_RedSizeTst() throws IOException, WriteException{
		cadena = "";
		Fichero.escribeFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_RedSizeTst.csv", cadena);
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
                Fichero.AnadirtoFichero("MEDIUM\\Tablas\\FARC\\CSV\\FARC_medium_RedSizeTst.csv", cadena);
        }
        
        
        /*
        public static void writeExcel_tst() throws IOException, WriteException{
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("ExcelFARC_medium_tst.xls");
            tabla.create("tablaFARC");
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
            double avg_Time[]       = new double[algoritmos.size()];
            
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
                    avg_Time[j]        += time[i][j];
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
                
                avg_Time[j]        /= datasets.size();
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
            
            elem_Time           =ArrayIndex.createArray(avg_Time);
            
            // Sorting the arrays
            Arrays.sort(elem_AvgTra, Collections.reverseOrder());
            Arrays.sort(elem_AvgTst, Collections.reverseOrder());
            Arrays.sort(elem_AvgnumeroNodi);
            Arrays.sort(elem_AvgAntRule);
            Arrays.sort(elem_AvgRedSize, Collections.reverseOrder());
            Arrays.sort(elem_AvgISRed, Collections.reverseOrder());
            
            Arrays.sort(elem_RedSizeXtst, Collections.reverseOrder());
            Arrays.sort(elem_RedXtst, Collections.reverseOrder());
            
            Arrays.sort(elem_Time);
            
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
            tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelFARC_avg_medium.xls");
            tabla.create("tablaFARC");
            
            tabla.addStringBig(0, 0, "Accuracy_tra");
            tabla.addStringBig(2, 0, "Accuracy_tst");
            tabla.addStringBig(4, 0, "# Nodes");
            tabla.addStringBig(6, 0, "# Antecedents");
            tabla.addStringBig(8, 0, "IS_Reduction");
            tabla.addStringBig(10, 0, "RedSize");
            tabla.addStringBig(12, 0, "ISRed*a_tst");
            tabla.addStringBig(14, 0, "RedSz*a_tst");
            tabla.addStringBig(16, 0, "Time");
            
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
                
                // Time
                b=new BigDecimal(elem_Time[j].getValue()).setScale(precision,BigDecimal.ROUND_HALF_UP);
                tabla.addStringBordL(16,1+j, sel_alg.elementAt(elem_Time[j].getIndex()));
                //tabla.addNumber(15,1+j, elem_RedSizeXtst[j].getValue());
                tabla.addNumber(17,1+j, b.doubleValue());
            }
            tabla.write();
        }
        
        
        private static void writeTableAvg_rank()throws IOException, WriteException{
            
            MyExcelWriter tabla = new MyExcelWriter();
            tabla.setOutputFile("MEDIUM\\Tablas\\FARC\\ExcelFARC_Rank_medium.xls");
            tabla.create("tablaFARC");
            
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
