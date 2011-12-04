
import java.util.*;


//imported to use the excell library
import writer.*;
import java.io.IOException;
import jxl.write.WriteException;

// For the Friedman's test
import jsc.datastructures.MatchedData;
import jsc.relatedsamples.FriedmanTest;

// class created to have indexed arrays of doubles
import myUtility.*;

/*
 * Test para KeelDev  
 * @author Isaac Triguero and Salva Garc�a.
 */
public abstract class TestGenerico {
    String cadena = "";
    StringTokenizer lineas, tokens;
    String linea, dato1, dato2, token, timesplit[];
    boolean algorithms = true;
    Vector <String> algoritmos;
    Vector <String> sel_alg;
    Vector <String> sel_alg_largo;
    Vector <String> datasets;

    Vector <Integer> saltos;
    //static int i, j, k, l, m,n;
    double accuracyTst[][];
    double accuracyTra[][];
    
    double IS_reduction[][];
    double numberInstancesRed[][];
    
    double reductionSD[][];
    //static double kappaAv[][];
    //static double kappaSD[][];
    double accredAv[][];
    double kapredAv[][];
    double time[][];

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
    //static int sumKappa, sumi, sumj;
    //static double accSD, redSD, kapSD;
    String tiempos;
    int postime;
    
    // variables used to calculate the classificator's complexity
    double numeroNodi[][];
    double antRule[][];
    
    // variables for the Friedman's test
    double [][] ranks_matr;
    double [] ranks_vect;
    
    // Variables for the new type of table
    double RedSize[][];
    double RedSizeXtst[][];
    double RedXtst[][];

    
    // Element AVG variables
    ElementIndex elem_AvgTst[];
    ElementIndex elem_AvgTra[];
    ElementIndex elem_AvgnumeroNodi[];
    ElementIndex elem_AvgAntRule[];
    ElementIndex elem_AvgRedSize[];
    ElementIndex elem_AvgISRed[];
    ElementIndex elem_RedSizeXtst[];
    ElementIndex elem_RedXtst[];
    
    // Element Ranks variables
    ElementIndex elem_ranks_tst[];
    ElementIndex elem_ranks_Size[];
    ElementIndex elem_ranks_ISRed[];
    ElementIndex elem_ranks_RedSizeXtst[];
    ElementIndex elem_ranks_ISRedXtst[];
   
    
    public abstract void main(String[] args) throws IOException, WriteException;
            
    protected void calculate_values(){
            calcolo_tempo();
            calcolo_nodi_ant();
            calcolo_accurancy();    
            calcolo_accurancy_tra();
    
            calcolo_Red();
            calculate_friedman();
            calculate_redAVG();
        }
    
     public abstract void writeCSVs() throws IOException, WriteException;
        
        
    protected void inizializza(String[] args){
        algoritmos = new Vector <String>();
        datasets = new Vector <String>();
        sel_alg = new Vector <String>();
        sel_alg_largo = new Vector <String>();

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

        //tiempos = Fichero.leeFichero("tiempos.txt");

        System.out.println("Numbero de algoritmos "+ algoritmos.size());
        /*C�lculo del accuracy, kappa y reducci�n en KNN (TRAIN)*/
        configAct = 0;

        nomi_selAlg("FARC-HD");
    }

    protected void nomi_selAlg(String algName){
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
                sel_alg.addElement(new String(algName));
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

    public void calcolo_tempo(){
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
                cadena = Fichero.leeFichero("SMALL\\outFARC\\"
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

                    }
                }
            }
        }   
    }




    public void calcolo_accurancy(){
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


                                cadena = Fichero.leeFichero("SMALL\\resultsFARC\\"
                                        +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tst");

                                if(cadena.equals("-1")){
                                        cadena = Fichero.leeFichero("SMALL\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tst");
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
                        accuracyTst[j][i] = acc /10;

                    }
        }
    }

    private void calcolo_Red(){
        /* Istruzioni eseguite per ognuno degli algoritmi */
        for (int i=0; i<algoritmos.size(); i++) {
                alAct = (String)algoritmos.elementAt(i);
                //System.out.println("Processing algorithm: " + alAct);

                /* Per ognuno dei datasets presenti (indipendentemente dal numero di elementi del dataset)*/
                for (int j=0; j<datasets.size(); j++) {
                        datAct = (String)datasets.elementAt(j);
                        for (int k=0; k<10; k++) {

                                String directory="SMALL\\reductionFARC\\";
                                String nomeFile="Ignore-MV";

                                if (i!=0){
                                    nomeFile+="."+sel_alg_largo.elementAt(i);

                                }        
                                //nomeFile+="."+datasets.get(j);                                 
                                String nomeCompleto=directory+nomeFile+"."+datasets.get(j)
                                        +"\\"+nomeFile+"."+datasets.get(j)+"-10-"+k+"tra.dat";

                                cadena = Fichero.leeFichero(nomeCompleto);

                                if(cadena.equals("-1")){
                                    nomeCompleto=directory+nomeFile+"."+datasets.get(j)
                                            +"\\"+nomeFile+"s0."+datasets.get(j)+"-10-"+k+"tra.dat";
                                    cadena = Fichero.leeFichero(nomeCompleto);
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

    private void calcolo_accurancy_tra(){
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


                                cadena = Fichero.leeFichero("SMALL\\resultsFARC\\"
                                        +alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k)+"s0.tra");

                                if(cadena.equals("-1")){
                                        cadena = Fichero.leeFichero("SMALL\\resultsFARC\\"+alAct+"."+datasets.get(j)+"\\result"+Integer.toString(k+10)+"s0.tra");
                                }

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


    public abstract void calcolo_nodi_ant();
    
    public void scrivi_tabella(String percorso){
        /*Print the results*/

            cadena = "\n";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }

    public void write_table_excel(String percorso) throws IOException, WriteException{
        MyExcelWriter tabla = new MyExcelWriter();
        tabla.setOutputFile(percorso);
        tabla.create("tablaFARC");
        for(int j=0;j<sel_alg.size();j++){
            tabla.addCaption(6*j+1, 0,sel_alg.elementAt(j));

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
            tabla.addNumber(6*j+3,datasets.size()+2, ranks_vect[j]);

        }
        for (int i=0; i<datasets.size(); i++) {
            tabla.addCaption(0, 2+i, datasets.elementAt(i));
        }
        tabla.addCaption(0, 2+datasets.size(), "avg_Rank");

        tabla.write();
    }

    private void writeTable_Bests(String percorso)throws IOException, WriteException{
       MyExcelWriter tabla = new MyExcelWriter();
       tabla.setOutputFile(percorso);
       tabla.create("tablaFARC");

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

               int index=elem_dataset_rank[j].getIndex();
               double redSize= 1 - (numeroNodi[i][index]/numeroNodi[i][0]);
               if (redSize>0)
                   tabla.addCaption(j*4+1,1+i, sel_alg.elementAt(index));
               else
                   tabla.addString(j*4+1,1+i, sel_alg.elementAt(index));

               tabla.addNumber(j*4+2,1+i, accuracyTra[i][index]); 
               tabla.addNumber(j*4+3,1+i, numeroNodi[i][index]/20); 
               tabla.addNumber(j*4+4,1+i, redSize);
           }
        }
        tabla.write();
    }

    private void writeCSV_tst(String percorso) throws IOException, WriteException{
            cadena = "";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }
    private void writeCSV_ISRed(String percorso) throws IOException, WriteException{
            cadena = "";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }
    public void writeCSV_Size(String percorso) throws IOException, WriteException{
            cadena = "";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }

    public void writeCSV_ISRedTst(String percorso) throws IOException, WriteException{
            cadena = "";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }
    public void writeCSV_RedSizeTst(String percorso) throws IOException, WriteException{
            cadena = "";
            Fichero.escribeFichero(percorso, cadena);
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
            Fichero.AnadirtoFichero(percorso, cadena);
    }


    private double [][] calc_matrix(double [][] m){
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

    private void calculate_friedman(){
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

    private void calculteAvgRanks(){
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
    private void calculteAvg(){
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

    private void calculate_redAVG(){
        for (int i=0; i<datasets.size(); i++) {
            for(int j=0;j<sel_alg.size();j++){
                RedSize     [i][j]= 1 - (numeroNodi[i][j]/numeroNodi[i][0]); 
                RedSizeXtst [i][j]= RedSize[i][j]*accuracyTst[i][j];

                IS_reduction[i][j] = 1-(numberInstancesRed[i][j]/numberInstancesRed[i][0]);
                RedXtst [i][j]= IS_reduction[i][j]*accuracyTst[i][j];
            }   
        }
    }


    private void writeTableAvg(String percorso)throws IOException, WriteException{

        MyExcelWriter tabla = new MyExcelWriter();
        tabla.setOutputFile(percorso);
        tabla.create("tablaFARC");

        tabla.addStringBig(0, 0, "Accuracy_tra");
        tabla.addStringBig(2, 0, "Accuracy_tst");
        tabla.addStringBig(4, 0, "# Nodes");
        tabla.addStringBig(6, 0, "# Antecedents");
        tabla.addStringBig(8, 0, "IS_Reduction");
        tabla.addStringBig(10, 0, "RedSize");
        tabla.addStringBig(12, 0, "ISRed*a_tst");
        tabla.addStringBig(14, 0, "RedSz*a_tst");

        for(int j=0;j<sel_alg.size();j++){
            // Accuracy training
            tabla.addString(0,1+j, sel_alg.elementAt(elem_AvgTra[j].getIndex()));
            tabla.addNumber(1,1+j, elem_AvgTra[j].getValue());
            // Accuracy test
            tabla.addStringBordL(2,1+j, sel_alg.elementAt(elem_AvgTst[j].getIndex()));
            tabla.addNumber(3,1+j, elem_AvgTst[j].getValue());
            // size
            tabla.addStringBordL(4,1+j, sel_alg.elementAt(elem_AvgnumeroNodi[j].getIndex()));
            tabla.addNumber(5,1+j, elem_AvgnumeroNodi[j].getValue());
            // ant
            tabla.addStringBordL(6,1+j, sel_alg.elementAt(elem_AvgAntRule[j].getIndex()));
            tabla.addNumber(7,1+j, elem_AvgAntRule[j].getValue());
            // IS_Reduction
            tabla.addStringBordL(8,1+j, sel_alg.elementAt(elem_AvgISRed[j].getIndex()));
            tabla.addNumber(9,1+j, elem_AvgISRed[j].getValue());
            // redSize
            tabla.addStringBordL(10,1+j, sel_alg.elementAt(elem_AvgRedSize[j].getIndex()));
            tabla.addNumber(11,1+j, elem_AvgRedSize[j].getValue());
            // ISRed*a_tst
            tabla.addStringBordL(12,1+j, sel_alg.elementAt(elem_RedXtst[j].getIndex()));
            tabla.addNumber(13,1+j, elem_RedXtst[j].getValue());
            // RedSize*a_tst
            tabla.addStringBordL(14,1+j, sel_alg.elementAt(elem_RedSizeXtst[j].getIndex()));
            tabla.addNumber(15,1+j, elem_RedSizeXtst[j].getValue());
        }
        tabla.write();
    }


    private void writeTableAvg_rank(String percorso)throws IOException, WriteException{

        MyExcelWriter tabla = new MyExcelWriter();
        tabla.setOutputFile(percorso);
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


    private void print_redSize(){
        for (int i=0; i<datasets.size(); i++) {
            for(int j=0;j<sel_alg.size();j++){
                if (RedSize[i][j]<=0)
                    System.out.println("dataset:"+datasets.elementAt(i) +
                            " algorithm:"+sel_alg.elementAt(j) +" value:"+RedSize[i][j]);
            }
        }
    }
}
