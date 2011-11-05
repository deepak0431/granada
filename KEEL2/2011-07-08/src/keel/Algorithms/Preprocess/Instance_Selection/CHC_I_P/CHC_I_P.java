//
//  CHC.java
//
//  Salvador Garcï¿½a Lï¿½pez
//
//  Created by Salvador Garcï¿½a Lï¿½pez 20-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC_I_P;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Preprocess.Basic.C45.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class CHC_I_P extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private int tamPoblacion;
  private int nEval;
  private double alfa;
  private double r;
  private double prob0to1Div;  
  //private int kNeigh;
  
  /* Variables added to worl in cooperation with the C4.5 Algorithm */
  protected Dataset TrainModel;
  protected Dataset ValidationModel;
  protected Dataset TestModel;
  protected boolean pruned;
  protected float confidence;
  protected int instancesPerLeaf;
  C45 myC45;
  Vector confArray;
  
  /**
   * Richiede il file di configurazione, tale file verrÃ  letto 
   *  Il file ha il seguente formato:
   *     algorithm = CHC Adaptative Search for Instance Selection
   *     inputData = "../datasets/cleveland/cleveland-10-1tra.dat" "../datasets/
   *                                    cleveland/cleveland-10-1tst.dat" 
   *     outputData = "../datasets/CHC-TSS.cleveland/CHC-TSSs0.cleveland-10-1tra.dat"
   *                                    "../datasets/CHC-TSS.cleveland/CHC-TSSs0.cleveland-10-1tst.dat" 
   *
   *     seed = 1286082570
   *     Population Size = 50
   *     Number of Evaluations = 10000
   *     Alfa Equilibrate Factor = 0.5
   *     Percentage of Change in Restart = 0.35
   *     0 to 1 Probability in Restart = 0.25
   *     0 to 1 Probability in Diverge = 0.05
   *     Number of Neighbours = 1
   *     Distance Function = Euclidean
   * 
   * La lettura del file viene effettuata dalla funzione leerConfiguracion metodo
   * astratto della classe metodo implementato nella sottoclasse CHC
   * 
   * L'inizializzazione delle variabili viene fatta, invece, servendosi delle 
   * funzioni della classe metodo.
   */
  public CHC_I_P (String ficheroScript) {
    // distanceEu=false;
    // Default initialization is false for the boolean variables (e comunque sta 
    // cosa sarebbe bene che la sistemassi meglio)
    super (ficheroScript);
    
    distanceEu=false;
    
    /* Making datas for the C4.5 Algorithm */
    //ponendo true per qualche strano movivo: NON FUNZIONA!!!
    TrainModel = new Dataset(ficheroTraining,false);
    
    //System.out.println("prima di creare il dataset test");
    //TestModel = new Dataset(test);
    TestModel = new Dataset(ficheroTraining,false);
    //System.out.println("prima del vettore di oggetti");
    
    /*
    Object []confArray = null ;
    confArray[0]=TrainModel;
    //confArray[1]=ValidationModel;
    confArray[1]=TestModel;
    confArray[2]=pruned;
    confArray[3]=confidence;
    confArray[4]=instancesPerLeaf;
    */
    confArray = new Vector();
    
    confArray.add(TrainModel);
    confArray.add(TestModel);
    confArray.add(pruned);
    confArray.add(confidence);
    confArray.add(instancesPerLeaf);
    myC45 = new C45(confArray);
  }

  public void ejecutar () {
    //variabili di appoggio	  
    int i, j, k, l;
    int numLoop = 0;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    Cromosoma poblacion[];
    int ev = 0;
    Cromosoma C[];
    int baraje[];
    int pos, tmp;
    Cromosoma newPob[];
    int d = datosTrain.length / 4;
    int tamC;
    Cromosoma pobTemp[];
    long tiempo = System.currentTimeMillis();


    //finding the dimension of the complete Tree
    boolean [] vectOnes=new boolean[datosTrain.length];
    for (i=0;i< datosTrain.length;i++)
        vectOnes[i]=true;       
    Cromosoma complete=new Cromosoma(vectOnes);
    complete.evaluaComplete(myC45, TrainModel, alfa);

    int   TreeSize = complete.numberOfNodes;
    float acc_Ref = complete.correctClassPerc;
    
    /*Random inicialization of the poblation*/
    Randomize.setSeed (semilla);
    poblacion = new Cromosoma[tamPoblacion];
    baraje = new int[tamPoblacion];
    for (i=0; i<tamPoblacion; i++)
      poblacion[i] = new Cromosoma (datosTrain.length);
    
    double bestPerformance=0;
    /*Initial evaluation of the poblation*/
    for (i=0; i<tamPoblacion; i++){
      poblacion[i].evalua(myC45, TrainModel,TreeSize, alfa);
//      System.out.println("valutando poblation di i");
//      System.out.println("la percentuale di elementi correttamente classificati è:");
//      System.out.println(poblacion[i].correctClassPerc);
//      System.out.println("la calidad è:");
//      System.out.println(poblacion[i].calidad);
//      System.out.println("il numero di nodi è:");
//      System.out.println(poblacion[i].numberOfNodes);
      
    }
      
      //poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);

    /*Until stop condition*/
    while (ev < nEval) {
      C = new Cromosoma[tamPoblacion];

      /*Selection(r) of C(t) from P(t)*/
      for (i=0; i<tamPoblacion; i++)
        baraje[i] = i;
      for (i=0; i<tamPoblacion; i++) {
        pos = Randomize.Randint (i, tamPoblacion-1);
        tmp = baraje[i];
        baraje[i] = baraje[pos];
        baraje[pos] = tmp;
      }
      for (i=0; i<tamPoblacion; i++)
        C[i] = new Cromosoma (datosTrain.length, poblacion[baraje[i]]);

      /*Structure recombination in C(t) constructing C'(t)*/
      tamC = recombinar (C, d);
      newPob = new Cromosoma[tamC];
      for (i=0, l=0; i<C.length; i++) {
        if (C[i].esValido()) { //the cromosome must be copied to the new poblation C'(t)
          newPob[l] = new Cromosoma (datosTrain.length, C[i]);
          l++;
        }
      }

      /*Structure evaluation in C'(t)*/
      for (i=0; i<newPob.length; i++) {
        newPob[i].evalua(myC45, TrainModel,TreeSize, alfa);
        //newPob[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
        ev++;        
      }

      /*Selection(s) of P(t) from C'(t) and P(t-1)*/
      Arrays.sort(poblacion);
      Arrays.sort(newPob);
      /*If the better of C' is worse than the worst of P(t-1), then there will no changes*/
      if (tamC==0 || newPob[0].getCalidad() < poblacion[tamPoblacion-1].getCalidad()) {
        d--;
      } else {
        pobTemp = new Cromosoma[tamPoblacion];
        for (i=0, j=0, k=0; i<tamPoblacion && k<tamC; i++) {
          if (poblacion[j].getCalidad() > newPob[k].getCalidad()) {
            pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
            j++;
          } else {
            pobTemp[i] = new Cromosoma (datosTrain.length, newPob[k]);
            k++;
          }
        }
        if (k == tamC) { //there are cromosomes for copying
          for (; i<tamPoblacion; i++) {
            pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
            j++;
          }
        }
        poblacion = pobTemp;
      }
      
      if (poblacion[0].calidad > bestPerformance){
          System.out.println("Loop nuumero : "+numLoop);
          System.out.println("Nuovo valore di bestPerformance: "+poblacion[0].calidad);
          System.out.println("Nuovo valore di correctClassified: "+poblacion[0].correctClassPerc);
          System.out.println("Vecchio valore valore di bestPerformance: "+bestPerformance);
          System.out.println("Valore di complete: "+complete.calidad);
          bestPerformance=poblacion[0].calidad;
          
      }else{
          
          //System.out.println("popolazione non migliorata : "+notImproved);  
      }

      /*Last step of the algorithm*/
      if (d < 0) {
        for (i=1; i<tamPoblacion; i++) {
          poblacion[i].divergeCHC (r, poblacion[0], prob0to1Div);
        }
        for (i=0; i<tamPoblacion; i++)
          if (!(poblacion[i].estaEvaluado())) {
            poblacion[i].evalua(myC45, TrainModel,TreeSize, alfa);
            //poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
            ev++;
          }

        /*Reinicialization of d value*/
        d = (int)(r*(1.0-r)*(double)datosTrain.length);
      }
      numLoop++;
    }

    Arrays.sort(poblacion);
    nSel = poblacion[0].genesActivos();
    
    System.out.println("Il cromosoma selezionato è");
    for (i=0;i<poblacion[0].cuerpo.length;i++){
        System.out.print(poblacion[0].cuerpo[i]+" ");   
    }
    System.out.println(" ");
    
    System.out.println("la percentuale di elementi correttamente classificati è:");
    System.out.println(poblacion[0].correctClassPerc);
    System.out.println("la calidad è:");
    System.out.println(poblacion[0].correctClassPerc);
    
    System.out.println("la percentuale di elementi correttamente classificati di complete è:");
    System.out.println(complete.correctClassPerc);
    System.out.println("la calidad di complete è:");
    System.out.println(complete.calidad);
    
    System.out.println("il numero di nodi dell'albero di decisione è:");
    System.out.println(poblacion[0].numberOfNodes);

    /*Construction of S set from the best cromosome*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (poblacion[0].getGen(i)) { //the instance must be copied to the solution
        for (j=0; j<datosTrain[i].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("CHC_I_P "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /*Function that determines the cromosomes who have to be crossed and the other ones who have to be removed
   It returns the number of remaining cromosomes in the poblation*/
  private int recombinar (Cromosoma C[], int d) {

    int i, j;
    int distHamming;
    int tamC = 0;

    for (i=0; i<C.length/2; i++) {
      distHamming = 0;
      for (j=0; j<datosTrain.length; j++)
        if (C[i*2].getGen(j) != C[i*2+1].getGen(j))
          distHamming++;
      if ((distHamming/2) > d) {
        for (j=0; j<datosTrain.length; j++) {
          if ((C[i*2].getGen(j) != C[i*2+1].getGen(j)) && Randomize.Rand() < 0.5) {
              if (C[i*2].getGen(j))
                  C[i*2].setGen(j,false);
              else
                  C[i*2].setGen(j,false);
          }
        }
        tamC += 2;
      } else {
        C[i*2].borrar();
        C[i*2+1].borrar();
      }
    }

    return tamC;
  }

  public void leerConfiguracion (String ficheroScript) {

    String fichero, linea, token;
    StringTokenizer lineasFichero, tokens;
    byte line[];
    int i, j;

    ficheroSalida = new String[2];

    fichero = Fichero.leeFichero (ficheroScript);
    lineasFichero = new StringTokenizer (fichero,"\n\r");

    lineasFichero.nextToken();
    linea = lineasFichero.nextToken();

    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the name of training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Obtainin the path and the base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the name of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);
    
    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

    /*Getting the size of the poblation and the number of evaluations*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nEval = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the equilibrate alfa factor and r value*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    alfa = Double.parseDouble(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    r = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the probability of change bits*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    prob0to1Div = Double.parseDouble(tokens.nextToken().substring(1));

    /*
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kNeigh = Integer.parseInt(tokens.nextToken().substring(1));
     */
    
    /*Getting the type of distance function*/
    /*
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;
    */
    
    /* Getting the parameters for the C4.5 Algorithm */
    /*Getting the pruned variable value (TRUE or FALSE)*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    pruned = Boolean.parseBoolean(tokens.nextToken().substring(1));
    
    /*Getting the confidencevalue value (TRUE or FALSE)*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    confidence = Float.parseFloat(tokens.nextToken().substring(1));
    
    /*Getting the confidencevalue value (TRUE or FALSE)*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    instancesPerLeaf = Integer.parseInt(tokens.nextToken().substring(1));
    
}
}