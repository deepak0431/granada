//
//  CHC.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 20-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC45_3;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Preprocess.Basic.C45.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class CHC45_3 extends Metodo {

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
  
  //my pseudo-random circular generator
  myRand generator;
  
  //number of child
  int childNum;
  
  /**
   * Richiede il file di configurazione, tale file verrà letto 
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
  public CHC45_3 (String ficheroScript) {
    // distanceEu=false;
    // Default initialization is false for the boolean variables (e comunque sta 
    // cosa sarebbe bene che la sistemassi meglio)
    super (ficheroScript);
    
    distanceEu=false;
    
    /* Making datas for the C4.5 Algorithm */
    TrainModel = new Dataset(training);
    
    //System.out.println("prima di creare il dataset test");
    TestModel = new Dataset(test);
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
    Vector confArray = new Vector();
    
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
    int nClases;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    
    //poblation of cromosomes
    Cromosoma poblacion[];
    int ev = 0;
    int pos, tmp;
    Cromosoma newPob[];
    int d = datosTrain.length / 4;
    
    Cromosoma pobTemp[];
    long tiempo = System.currentTimeMillis();
    
    generator = new myRand(1493);

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    //finding the dimension of the complete Tree
    boolean [] vectOnes=new boolean[datosTrain.length];
    for (i=0;i< datosTrain.length;i++)
        vectOnes[i]=true;       
    Cromosoma complete=new Cromosoma(vectOnes);

    int   TreeSize = complete.numberOfNodes;
    float acc_Test = complete.correctClassPerc;
    
    /* Random initialization of the poblation
     * The first element is the complete cromosome, in order to start with an 
     * element that has a good classification's accuracy.
     */
    poblacion = new Cromosoma[tamPoblacion];
    
    poblacion[0]=complete;
    for (i=1; i<tamPoblacion; i++)
      poblacion[i] = new Cromosoma (datosTrain.length);

    /*Initial evaluation of the poblation*/
    for (i=0; i<tamPoblacion; i++)
      poblacion[i].evalua(myC45, TrainModel,TreeSize,acc_Test, alfa, nClases);
      //poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);

    //variable used to count the times that the algorithm doesn't improve his performance.
    int notImproved= 0-850;
    float bestPerformance=complete.correctClassPerc;
    
    /*Until stop condition*/
    while (ev < nEval || notImproved > 150) {
    
      /*Structure recombination in C(t) constructing C'(t)*/
      Cromosoma Pob_child[] = recombinar (poblacion, d);
      
      newPob = new Cromosoma[poblacion.length];
      
      for (i=0, l=0; i<Pob_child.length; i++) {
        if (Pob_child[i].esValido()) { //the cromosome must be copied to the new poblation C'(t)
          newPob[l] = new Cromosoma (datosTrain.length, Pob_child[i]);
          l++;
        }
      }

      /*Structure evaluation in C'(t)*/
      for (i=0; i<newPob.length; i++) {
        newPob[i].evalua(myC45, TrainModel,TreeSize,acc_Test, alfa, nClases);
        //newPob[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
        ev++;        
      }

      /*Selection(s) of P(t) from C'(t) and P(t-1)*/
      Arrays.sort(poblacion);
      Arrays.sort(newPob);
      
      
      /*If the better of C' is worse than the worst of P(t-1), then there will no changes*/
      if (childNum==0 || newPob[0].getCalidad() < poblacion[tamPoblacion-1].getCalidad()) {
        d--;
      } else {
        pobTemp = new Cromosoma[tamPoblacion];
        for (i=0, j=0, k=0; i<tamPoblacion && k<childNum; i++) {
          if (poblacion[j].getCalidad() > newPob[k].getCalidad()) {
            pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
            j++;
          } else {
            pobTemp[i] = new Cromosoma (datosTrain.length, newPob[k]);
            k++;
          }
        }
        if (k == childNum) { //there are cromosomes for copying
          for (; i<tamPoblacion; i++) {
            pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
            j++;
          }
        }
        poblacion = pobTemp;
        
        if (poblacion[0].correctClassPerc > bestPerformance){
            bestPerformance=poblacion[0].correctClassPerc;
            notImproved=0;
        }else{
            notImproved++;
        }
        
      }

      /*Last step of the algorithm*/
      if (d < 0) {
        for (i=1; i<tamPoblacion; i++) {
          poblacion[i].divergeCHC (r, poblacion[0], prob0to1Div);
        }
        for (i=0; i<tamPoblacion; i++)
          if (!(poblacion[i].estaEvaluado())) {
            poblacion[i].evalua(myC45, TrainModel,TreeSize, acc_Test, alfa, nClases);
            //poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
            ev++;
          }

        /*Reinicialization of d value*/
        d = (int)(r*(1.0-r)*(double)datosTrain.length);
      }
    }

    Arrays.sort(poblacion);
    nSel = poblacion[0].genesActivos();

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

    System.out.println("CHC45_3 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /* Function that creates the new geenration of cromosomes with the childs, and 
   * some of the bests father if not enough childs are available
   * 
   * @parameter  C       The original poblation
   * @parameter  d       The distance threshold
   * 
   * @return     newPob  The new poblacion (that have to compete with the oldest 
   *                     one to survive)
   */
  private Cromosoma[] recombinar (Cromosoma C[], int d) {


    int distHamming;
    int child = 0;
    Cromosoma [] Pob_child=new Cromosoma[C.length];
    
    for (int attempt=0; attempt< C.length*5 && child < C.length;attempt++){
        distHamming=0;
        int indexFather=(int)Math.round(generator.getDouble()*C.length);
        int indexMother=(int)Math.round(generator.getDouble()*C.length);
        //check if the distHamming is bigger than threshold d
        for (int j=0; j<datosTrain.length; j++){
            if (C[indexFather].getGen(j) != C[indexMother].getGen(j))
                distHamming++;
        }
        if ((distHamming/2) > d) {
            for (int j=0; j<datosTrain.length; j++) {
                if ((C[indexFather].getGen(j) != C[indexMother].getGen(j)) && generator.getDouble() < 0.5) 
                    Pob_child[child].setGen(j, C[indexFather].getGen(j));
                else
                    Pob_child[child].setGen(j, C[indexMother].getGen(j));
            }
            // we have already created a new child
            child++;
        }        
    }
    
    /* If the algorithm end without C.lenght childs we'll add the old best 
     * cromosomes to the new poblation.
     */
    childNum = child;
    if (child<C.length){
        for (int i=0; child<C.length; i++){
            Pob_child[child]=C[i];
            child++;
        }
    }
    return Pob_child;
    
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