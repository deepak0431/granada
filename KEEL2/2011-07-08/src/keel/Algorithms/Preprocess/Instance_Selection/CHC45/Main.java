//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 20-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CHC45;

public class Main {

  public static void main (String args[]) {

    CHC45 chc45;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      chc45 = new CHC45 (args[0]);
      chc45.ejecutar();
    }
  }
}