//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 20-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC_I_P;

public class Main {

  public static void main (String args[]) {

    CHC_I_P chc45;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      chc45 = new CHC_I_P (args[0]);
      chc45.ejecutar();
    }
  }
}