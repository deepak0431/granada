

package keel.Algorithms.Preprocess.Instance_Selection.CHC45_3;

public class myRand{
    private boolean[] boolvect;
    private double[] doublevect;
    int totEl;
    int pos;
    public myRand (){
        this(1000);
    }
    public myRand (int tot){
        totEl=tot;
        pos=0;
        boolvect = new boolean[totEl];
        doublevect = new double[totEl];
        for (int i=0;i<totEl;i++){
            doublevect[i]=Math.random();
            if (Math.round(doublevect[i])>0.5)
                boolvect[i]=true;
            else
                boolvect[i]=false;    
        }
    }
    public boolean getBool(){
        boolean toRet = boolvect[pos];
        pos=(pos+1)%totEl;
        return toRet;
    }
    public double getDouble(){
        double toRet = doublevect[pos];
        pos=(pos+1)%totEl;
        return toRet;
    }
}
