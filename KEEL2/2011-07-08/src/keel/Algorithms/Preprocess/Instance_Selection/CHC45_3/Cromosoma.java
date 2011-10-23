//
//  Cromosoma.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC45_3;

import java.util.logging.Level;
import java.util.logging.Logger;
import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Preprocess.Basic.C45.*;

import org.core.*;

public class Cromosoma implements Comparable {

  /*Cromosome data structure*/
    /* In questo caso viene implementato tramite un array di booleani che sono le
     * instanze da selezionare!
     */
  boolean cuerpo[];

  /*Useless data for cromosomes*/
  double calidad;
  boolean evaluated;
  boolean valido;
  double errorRate;
  
  /* percentual of Correct Classified */
  float correctClassPerc;
  /* number of nodes of the C45's associated Tree */
  public int numberOfNodes;
  
  //my pseudo-random circular generator
  myRand generator;

  /* Construct a random cromosome of specified size(OK) */
  public Cromosoma (int size,myRand genpassed) {

    double u;
    int i;
    generator = genpassed;
    
    cuerpo = new boolean[size];
    for (i=0; i<size; i++) {
      cuerpo[i]=generator.getBool();
    }
    evaluated = false;
    valido = true;
  }

  /*Create a copied cromosome (OK)*/
  public Cromosoma (int size, Cromosoma a, myRand genpassed) {
    generator = genpassed;
    cuerpo = new boolean[size];
    for (int i=0; i<cuerpo.length; i++)
      cuerpo[i] = a.getGen(i);
    calidad = a.getCalidad();
    evaluated = false;
    valido = true;
  }

  /*Cronstruct a cromosome from a bit array (OK)*/
  public Cromosoma (boolean datos[], myRand genpassed) {
    generator = genpassed;
    cuerpo = new boolean[datos.length];
    for (int i=0; i<datos.length; i++)
      cuerpo[i] = datos[i];
    evaluated = false;
    valido = true;
  }

  /*OK*/
  public boolean getGen (int indice) {
    return cuerpo[indice];
  }

  /*OK*/
  public double getCalidad () {
    return calidad;
  }

  /*OK*/
  public void setGen (int indice, boolean valor) {
    cuerpo[indice] = valor;
  }

  /*Function that evaluates a cromosome (OK)*/
  
  /** Function that evaluates a cromosome
   * 
   * È la funzione che mi interessa maggiormente, esegue la valutazione di un 
   * determinato cromosoma.
   * Nella versione originale tale valutazione è effettuata facendo uso del knn, 
   * nel nostro caso deve essere usato il C4.5
   *
   * @param myTree      the C45 structure used
   * @param myModel     the datasets with selected instances.
   * @param alfa        parameter used to fix the weigth of performance in 
   *                    classification and reduction importance.
   * @param nClases     number of classes.
   */
  public void evalua (C45 myTree, Dataset modelDataset,int TreeSize,float acc_test, double alfa, int nClases) {
    //M = (double)datos.length;
    double M = (double)modelDataset.numItemsets();
    double s = (double)genesActivos();
    Dataset selectedModel = modelDataset.selectDataset(cuerpo,(int)s);
        try {
            myTree.generateTree(selectedModel);
        } catch (Exception ex) {
            Logger.getLogger(Cromosoma.class.getName()).log(Level.SEVERE, null, ex);
        }
    correctClassPerc = myTree.evaluateCromosoma(); 
    numberOfNodes = myTree.root.NumberOfNodes;
    //qui lui ha i dati correttamente classificati...(aciertos)

    //calidad = correctClassPerc*alfa;
    //calidad += ((1.0 - alfa) * 100.0 * (M - s) / M);
    calidad = correctClassPerc;
    //if (calidad>=acc_test){
    //    calidad +=  100.0 * (TreeSize - numberOfNodes) / TreeSize;
    //    calidad += (0.5 * 100.0 * (M - s) / M);
    //}
    evaluated = true;
}

  /*Function that does the mutation (OK)*/
  public void mutacion (double pMutacion1to0, double pMutacion0to1) {

    int i;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) {
        if (generator.getDouble() < pMutacion1to0) {
          cuerpo[i] = false;
          evaluated = false;
        }
      } else {
        if (generator.getDouble() < pMutacion0to1) {
          cuerpo[i] = true;
          evaluated = false;
        }
      }
    }
  }

  /*Function that does the CHC diverge*/
  public void divergeCHC (double r, Cromosoma mejor, double prob) {
	  
    int i;

    for (i=0; i<cuerpo.length; i++) {
      if (generator.getDouble() < r) {
        if (generator.getDouble() < prob) {
          cuerpo[i] = true;
        } else {
          cuerpo[i] = false;
        }
      } else {
        cuerpo[i] = mejor.getGen(i);
      }
    }
    evaluated = false;
  }

  /*OK*/
  public boolean estaEvaluado () {
    return evaluated;
  }

  /*OK*/
  public int genesActivos () {
    int i, suma = 0;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) suma++;
    }

    return suma;
  }

  /*OK*/
  public boolean esValido () {
    return valido;
  }

  /*OK*/
  public void borrar () {
    valido = false;
  }

  /*Function that lets compare cromosomes to sort easily (OK)*/
  public int compareTo (Object o1) {
    if (this.calidad > ((Cromosoma)o1).calidad)
      return 1;
    else if (this.calidad < ((Cromosoma)o1).calidad)
      return -1;
    else return 0;
  }

  /*Function that inform about if a cromosome is different only in a bit, obtain the
   position of this bit. In case of have more differences, it returns -1 (OK)*/
  public int differenceAtOne (Cromosoma a) {

    int i;
    int cont = 0, pos = -1;

    for (i=0; i<cuerpo.length && cont < 2; i++)
      if (cuerpo[i] != a.getGen(i)) {
        pos = i;
        cont++;
      }

    if (cont >= 2)
      return -1;
    else return pos;
  }

  /*OK*/
  public String toString() {
	  
    int i;

    String temp = "[";

    for (i=0; i<cuerpo.length; i++)
      if (cuerpo[i])
        temp += "1";
      else
        temp += "0";
    temp += ", " + String.valueOf(calidad) + "," + String.valueOf(errorRate) + ", " + String.valueOf(genesActivos()) + "]";

    return temp;
  }
}