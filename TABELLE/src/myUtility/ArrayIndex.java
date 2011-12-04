
package myUtility;

public class ArrayIndex{    
    
    public static ElementIndex[] createArray(double[] original){
        ElementIndex [] copy= new ElementIndex[original.length];
        for (int i=0;i<original.length;i++){
            copy[i]=new ElementIndex(original[i], i);
        }
        return copy;
    }
}
