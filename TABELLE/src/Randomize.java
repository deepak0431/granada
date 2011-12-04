
import java.util.*;

public class Randomize {
	private static long Seed;
	private static MTwister generador = new MTwister();
	public static void setSeed (long semilla) {
		Seed = semilla;
		generador.init_genrand(Seed);
	}
	// Rand computes a psuedo-random float value between 0 and 1, excluding 1  
	public static double Rand () {
		return (generador.genrand_res53());
	}
	// RandOpen computes a psuedo-random float value between 0 and 1, excluding 0 and 1  
	public static double RandOpen () {
		return (generador.genrand_real3());
	}
	// RandClosed computes a psuedo-random float value between 0 and 1 inclusive  
	public static double RandClosed () {
		return (generador.genrand_real1());
	}
	// RandGaussian generates a standardized gaussian random number  
	public static double RandGaussian () {
		return (generador.genrand_gaussian());
	}

	// Randint gives an integer value between low and high, excluding high
	public static int Randint (int low, int high) {
		return ((int) (low + (high - low) * generador.genrand_res53()));
	}
	// RandintOpen gives an integer value between low and high, excluding 0 and 1
	public static int RandintOpen (int low, int high) {
		return ((int) (low + (high - low) * generador.genrand_real3()));
	}
	// RandintClosed gives an integer value between low and high inclusive
	public static int RandintClosed (int low, int high) {
		return ((int) (low + (high - low) * generador.genrand_real1()));
	}
	// Randdouble gives an double value between low and high, excluding high
	public static double Randdouble (double low, double high) {
		return (low + (high-low) * generador.genrand_res53());
	}
	// RanddoubleOpen gives an double value between low and high, excluding low and high
	public static double RanddoubleOpen (double low, double high) {
		return (low + (high-low) * generador.genrand_real3());
	}
	// RanddoubleClosed gives an double value between low and high inclusive
	public static double RanddoubleClosed (double low, double high) {
		return (low + (high-low) * generador.genrand_real1());
	}
}


