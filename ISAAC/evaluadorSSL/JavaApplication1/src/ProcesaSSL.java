
import java.util.*;




import Dataset.Attributes;
import Dataset.DatasetException;
import Dataset.HeaderFormatException;
import Dataset.InstanceAttributes;
import Dataset.InstanceSet;

public class ProcesaSSL {	
	public static void main(String[] args) throws DatasetException, HeaderFormatException {
		String cadena = "";
		StringTokenizer lineas, tokens, tokensT;
		String linea, dato1, dato2, token, timesplit[];
		boolean algorithms = true;
		Vector <String> algoritmos;
		Vector <String> datasets;

		Vector <Integer> saltos;
		int i, j, k, l, m,n;
		double accuracyAv[][];
		double accuracySD[][];
		double reductionAv[][];
		double reductionSD[][];
		double kappaAv[][];
		double kappaSD[][];
		double accredAv[][];
		double kapredAv[][];
		double time[][];
		int runs[][];
		String alAct, alAnterior;
		int configAct;
		String datAct;
		int salAct;
		double acc, red, kappa;
		int aciertos, total;
		double accV[], redV[], kappaV[];
		Vector <String> valoresClase;
		int confusionMatrix[][];
		int sumKappa, sumi, sumj;
		double accSD, redSD, kapSD;
		String tiempos;
		int postime;

		if (args.length < 2) {
			System.err.println("Error. Fichero data sets + porcentaje de etiquetados.");
			System.exit(1);
		}

		
		int partition = 10; // por defecto
		
		if(args.length == 3)
				partition = Integer.parseInt(args[2]);
		
		datasets = new Vector <String>();


		/*Lectura del fichero de configuración*/
		cadena = Fichero.leeFichero(args[0]);
		lineas = new StringTokenizer (cadena,"\n\r");
		

		
		while (lineas.hasMoreTokens()) {
			linea = lineas.nextToken();
			tokens = new StringTokenizer (linea," ");
			token = tokens.nextToken();
			datasets.addElement(new String(token));
		}
    

		System.out.println("Number of datasets "+ datasets.size());
		
		
		for (j=0; j<datasets.size(); j++) {
			
			for (k=1; k<=partition; k++) {

				// 1ª paso: modificar data sets.
				
			    Attributes.clearAll();//BUGBUGBUG
		        InstanceSet training = new InstanceSet();        
		        
		        
		        try
		        {
		            training.readSet("data//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"tra"+".dat", true); 
		            //training.print();
		            training.setAttributesAsNonStatic();
		            InstanceAttributes att = training.getAttributeDefinitions();
		            
		            Prototype.setAttributesTypes(att);  
	
		        }
		        catch(Exception e)
		        {
		            System.err.println("readPrototypeSet has failed!");
		            e.printStackTrace();
		        }
		        
		        
		        int numberOfinstances = training.getNumInstances();
		        int labeled =  (Integer.parseInt(args[1])*numberOfinstances)/100;
		        int unlabeled = numberOfinstances - labeled;
		        
		        System.out.println("NUmber of instances = "+training.getNumInstances());
		        //System.out.println("NUmber of labeled instances = "+labeled);
		        //System.out.println("NUmber of unlabeled instances = "+unlabeled);
		            

		        PrototypeSet Original =  new PrototypeSet(training);
				PrototypeSet tra = new PrototypeSet();
				PrototypeSet trs = new PrototypeSet();
			
				
			//	RandomGenerator.generateDifferentRandomIntegers(0, numberOfinstances);
				RandomGenerator.setSeed(12345678);
	            ArrayList<Integer> indexes =  RandomGenerator.generateDifferentRandomIntegers(0, numberOfinstances-1);
	            for ( i=0; i< labeled;i++){
	            	tra.add(Original.get(indexes.get(i))); // metemos las instancias etiqeutadas
	            	trs.add(Original.get(indexes.get(i)));
	            }
	            	            
	            // ¿Si falta de una clase?
	            int numberOfClass = Original.getPosibleValuesOfOutput().size();
	            
	            for(i=0; i< numberOfClass; i++){
	            	if(tra.getFromClass(i).size()==0){
	            		//System.out.println("Falta de clase = "+i);
	            		//System.out.println("Y tenemos "+Original.getFromClass(i).size()+ " de esa clase");
	            		
	            		if(Original.getFromClass(i).size()!=0){ // Tiene que haberlos en la particion original
		            		Prototype clase = (Prototype) Original.getFromClass(i).getRandom();
		            		//clase.print();
		            		tra.add(clase);
		            		labeled++;
		            		unlabeled--;	            		
	            		}
	            	}
	            }
	            
	            // Añadimos el resto sin clase.
	            for( i=labeled; i< numberOfinstances; i++){
	            	Prototype otro = Original.get(indexes.get(i));
	            	trs.add(otro);
	            	//Prototype otro2 = new Prototype(otro);
	            	//otro2.setFirstOutput(numberOfClass); // Le meto la última clase UNlABELED
	            	tra.add(otro);
	            }
				
	            //tra.print();
	            
	            // System.out.println("Y tenemos "+tra.getFromClass(1).size()+ " de esa clase");
				
	            
	            // 2 paso: Copiar cabecera y grabar data set
	            

				cadena = Fichero.leeFichero("data//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"tra"+".dat");

				// Buscar el atributo output
				
				lineas = new StringTokenizer (cadena,"\n\r");
				linea = lineas.nextToken();
				
				while (!linea.contains("@output")) {linea = lineas.nextToken();}
				String clase = new String((String) linea.subSequence(9, linea.length()-1));
				System.out.print(clase);
				
				// Ahora si sustityo en su sitio.
					lineas = new StringTokenizer (cadena,"\n\r");
					
					String CabeceraUnlabeled= new String();
					
					linea = lineas.nextToken();
					CabeceraUnlabeled = CabeceraUnlabeled.concat(linea+"\n");
				
					boolean seguir = true;
				while (!linea.contains("@data") ) {
					linea = lineas.nextToken();
									
					if (linea.contains(clase) && seguir){
						seguir = false;
						String prueba = new String(linea);
						prueba=prueba.replaceFirst("}", "");
						prueba=prueba.concat(", unlabeled}");
												
						CabeceraUnlabeled = CabeceraUnlabeled.concat(prueba+"\n");
						
						
					}else{
						CabeceraUnlabeled = CabeceraUnlabeled.concat(linea+"\n");
					}
					
					
				}
				
				/*
				while (!linea.contains("@data")) {
					linea = lineas.nextToken();
									
					if (linea.contains("@attribute class")){
						tipo1 = true;
						linea.length();
						String prueba = new String(linea);
						prueba=prueba.replaceFirst("}", "");
						prueba=prueba.concat(", unlabeled}");
												
						CabeceraUnlabeled = CabeceraUnlabeled.concat(prueba+"\n");
						
						
					}else{
						CabeceraUnlabeled = CabeceraUnlabeled.concat(linea+"\n");
					}
					
					
				}
				*/


				
				
				
				String salidaTRA = new String("dataSSL//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"tra"+".dat");
				String salidaTRS = new String("dataSSL//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"trs"+".dat");
				String salidaTST = new String("dataSSL//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"tst"+".dat");
				
				//System.out.println("Nombre fichero = "+ salidaTRA);
				
			
				Fichero.AnadirtoFichero(salidaTRS, CabeceraUnlabeled); // Le añado la cabecera al TRANSDUCTIVE
			
				
				// Añadir datos:
				
				
				lineas = new StringTokenizer (tra.asKeelDataFileString(),"\n\r");
				
				//Saltar cabecera
				
				linea = lineas.nextToken();
				while(!linea.contains("@data")){
					linea = lineas.nextToken();
				}
				
				// Quedarme solo con lo que me interesa
				
				int counter = 0;
				while(lineas.hasMoreTokens()){
					linea = lineas.nextToken();
					
					if(counter < labeled){
						CabeceraUnlabeled = CabeceraUnlabeled.concat(linea+"\n");
					}else{
						//System.out.println(linea);
						
						int index = linea.lastIndexOf(",");
						//System.out.println(linea.substring(0, index));
						String cambiado = new String(linea.substring(0, index));
						cambiado = cambiado.concat(", unlabeled");
						
						//System.out.println(cambiado);
						CabeceraUnlabeled = CabeceraUnlabeled.concat(cambiado+"\n");
					}
					
					
					counter++;
				}
				
				
				//System.out.println(CabeceraUnlabeled);

				Fichero.AnadirtoFichero(salidaTRA, CabeceraUnlabeled);  //Guardo el fichero TRA.
				

				//Añadir el transductive
				lineas = new StringTokenizer (trs.asKeelDataFileString(),"\n\r");
				
				//Saltar cabecera
				
				linea = lineas.nextToken();
				while(!linea.contains("@data")){
					linea = lineas.nextToken();
				}
				
				String transductive = new String("");
				
				while(lineas.hasMoreTokens()){
					linea = lineas.nextToken();
					
					transductive = transductive.concat(linea+"\n");
				}
				
				//System.out.println(transductive);
				
				Fichero.AnadirtoFichero(salidaTRS, transductive); // Le añado el final
				
				
				Fichero.AnadirtoFichero(salidaTST, Fichero.leeFichero("data//"+datasets.get(j)+"//"+datasets.get(j)+"-"+partition+"-"+k+"tst"+".dat"));
				
			//k=10;
			}
			//j=60;
		}
		System.out.println("The End");
}
		

}
