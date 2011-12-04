import java.io.FileInputStream;
import java.io.IOException;

public class auc {
	
	/**
	  * <p>
    * Read a file and returns the content
    * </p>
    * @param fileName Name of the file to read
    * @return A string with the content of the file
    */
	public static String readFile (String fileName) {
		String content = "";
		try {
			FileInputStream fis = new FileInputStream(fileName);
			byte[] piece = new byte[4096];
			int readBytes = 0;
			while (readBytes != -1) {
				readBytes = fis.read(piece);
				if (readBytes != -1) {
					content += new String(piece, 0, readBytes);
				}
			}
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return content;
	}
	
	public static double computeAuc (String file_name) {
		String file_content = readFile(file_name);
		String [] file_lines;
		String [] line_content;
		double tp, tn, fp, fn, tp_rate, fp_rate, auc;
		
		tp = 0;
		tn = 0;
		fp = 0;
		fn = 0;
		
		file_lines = file_content.split("\n");
		
		for (int i=0; i<file_lines.length; i++) {
			if (!file_lines[i].startsWith("@")) {
				line_content = file_lines[i].split(" ");
				
				if ((!(line_content[0].equals("positive") || line_content[0].equals("negative") || line_content[0].equals("?"))) || (!(line_content[1].equals("positive") || line_content[1].equals("negative") || line_content[1].equals("?")))) {
					System.err.println("Unknown symbol found in file " + file_name + " on line " + i + ": " + line_content);
					System.exit(-1);
				}
				
				if (line_content[0].equals("positive") && line_content[1].equals("positive")) {
					tp++;
				}
				else if ((line_content[0].equals("positive") && line_content[1].equals("negative")) || (line_content[0].equals("positive") && line_content[1].equals("?"))) {
					fn++;
				}
				else if (line_content[0].equals("negative") && line_content[1].equals("negative")) {
					tn++;
				}
				else if ((line_content[0].equals("negative") && line_content[1].equals("positive")) || (line_content[0].equals("negative") && line_content[1].equals("?"))) {
					fp++;
				}
				else {
					System.err.println("Unknown combination of symbols found in file " + file_name + " on line " + i + ": " + line_content);
					System.exit(-1);
				}
			}
		}
		
		tp_rate = (double)tp/(double)(tp+fn);
		fp_rate = (double)fp/(double)(fp+tn);
		
		auc = (1.0 + tp_rate - fp_rate)/2.0;
		
		return auc;
	}
	

	  public static void main(String[] args)
	  {
		auc mojon = new auc();
		System.out.println("AUC = "+ auc.computeAuc(args[0]));

	   }
	  
}
