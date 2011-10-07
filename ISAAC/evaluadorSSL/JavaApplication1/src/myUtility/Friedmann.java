
package myUtility;

import jsc.datastructures.MatchedData;
import jsc.relatedsamples.FriedmanTest;

public class Friedmann{    
    
    public static double[][] calculate_rank(double accuracyTst[][]){
            /* MatchedData is the datastrudture required from the Friedmann's 
             * function to work.
             */
            MatchedData array_acc = new MatchedData(accuracyTst);
            //test  = new FriedmanTest(array_acc, 0.015,true);
            FriedmanTest test  = new FriedmanTest(array_acc);
            MatchedData ranks = test.getRanks();
            
            return ranks.getData();          
        }
    
    
}
