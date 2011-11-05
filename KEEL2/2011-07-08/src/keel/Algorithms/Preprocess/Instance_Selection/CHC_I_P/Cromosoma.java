//
//  Cromosoma.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC_I_P;

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
  public boolean cruzado;
  boolean valido;
  double errorRate;
  
  /* percentual of Correct Classified */
  public float correctClassPerc;
  /* number of nodes of the C45's associated Tree */
  public int numberOfNodes;

  /* Construct a random cromosome of specified size(OK) */
  public Cromosoma (int size) {

    double u;
    int i;

    cuerpo = new boolean[size];
    for (i=0; i<size; i++) {
      u = Randomize.Rand();
      if (u < 0.5) {
        cuerpo[i] = false;
      } else {
        cuerpo[i] = true;
      }
    }
    cruzado = true;
    valido = true;
  }

  /*Create a copied cromosome (OK)*/
  public Cromosoma (int size, Cromosoma a) {
    int i;

    cuerpo = new boolean[size];
    for (i=0; i<cuerpo.length; i++)
      cuerpo[i] = a.getGen(i);
    calidad = a.getCalidad();
    numberOfNodes=a.numberOfNodes;
    correctClassPerc=a.correctClassPerc;
    cruzado = false;
    valido = true;
  }

  /*Cronstruct a cromosome from a bit array (OK)*/
  public Cromosoma (boolean datos[]) {
    int i;

    cuerpo = new boolean[datos.length];
    for (i=0; i<datos.length; i++)
      cuerpo[i] = datos[i];
    cruzado = true;
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
  public void evalua (C45 myTree, Dataset modelDataset,int TreeSize, double alfa) {
    //M = (double)datos.length;
    //double M = (double)modelDataset.numItemsets();
    double s = (double)genesActivos();
    Dataset selectedModel = modelDataset.selectDataset(cuerpo,(int)s);
    
        try {
            myTree.priorsProbabilities(selectedModel);
            myTree.generateTree(selectedModel);
        } catch (Exception ex) {
            Logger.getLogger(Cromosoma.class.getName()).log(Level.SEVERE, null, ex);
        }
    correctClassPerc = myTree.evaluateCromosoma(); 
    Tree.NumberOfNodes++;
    //String treeString= myTree.root.toString();
    myTree.root.calculateNodes();
    //System.out.println(treeString);
    numberOfNodes = Tree.NumberOfNodes;

    //calidad = correctClassPerc*alfa;
    //calidad += ((1.0 - alfa) * 100.0 * (M - s) / M);
    calidad = correctClassPerc*alfa;
    calidad += ((1.0 - alfa) * 100.0 * (TreeSize - numberOfNodes) / TreeSize);
    cruzado = false;
}
  
   public void evaluaComplete (C45 myTree, Dataset modelDataset,double alfa) {
    //M = (double)datos.length;
    //double M = (double)modelDataset.numItemsets();
    double s = (double)genesActivos();
    Dataset selectedModel = modelDataset.selectDataset(cuerpo,(int)s);
    
        try {
            myTree.priorsProbabilities(selectedModel);
            myTree.generateTree(selectedModel);
        } catch (Exception ex) {
            Logger.getLogger(Cromosoma.class.getName()).log(Level.SEVERE, null, ex);
        }
    correctClassPerc = myTree.evaluateCromosoma(); 
    Tree.NumberOfNodes++;
    myTree.root.calculateNodes();
    numberOfNodes = Tree.NumberOfNodes;
    
    // the tree size reduction is zero. 
    calidad = correctClassPerc*alfa;

    cruzado = false;
}

  /*Function that does the mutation (OK)*/
  public void mutacion (double pMutacion1to0, double pMutacion0to1) {

    int i;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) {
        if (Randomize.Rand() < pMutacion1to0) {
          cuerpo[i] = false;
          cruzado = true;
        }
      } else {
        if (Randomize.Rand() < pMutacion0to1) {
          cuerpo[i] = true;
          cruzado = true;
        }
      }
    }
  }

  /*Function that does the CHC diverge*/
  public void divergeCHC (double r, Cromosoma mejor, double prob) {
	  
    int i;

    for (i=0; i<cuerpo.length; i++) {
      if (Randomize.Rand() < r) {
        if (Randomize.Rand() < prob) {
          cuerpo[i] = true;
        } else {
          cuerpo[i] = false;
        }
      } else {
        cuerpo[i] = mejor.getGen(i);
      }
    }
    cruzado = true;
  }

  /*OK*/
  public boolean estaEvaluado () {
    return !cruzado;
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
      return -1;
    else if (this.calidad < ((Cromosoma)o1).calidad)
      return 1;
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