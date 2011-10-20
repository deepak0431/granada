package writer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class MyExcelWriter {

	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times14ptBoldline;
	private WritableCellFormat times12ptBoldline;
	private WritableCellFormat times;
        private WritableCellFormat timesLborder;
        private WritableCellFormat timesRborder;
	private String inputFile;
        public  WritableSheet excelSheet;
        private String sheetName;
        WritableWorkbook workbook;
	
public void setOutputFile(String inputFile) {
	this.inputFile = inputFile;
	}

	public void create(String nameSh) throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet(nameSh, 0);
		excelSheet = workbook.getSheet(0);
		createLabel(excelSheet);
		//createContent(excelSheet);

	}
        
        public void write() throws IOException, WriteException{
                workbook.write();
		workbook.close();
        }

	private void createLabel(WritableSheet sheet)
			throws WriteException {
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		times.setWrap(true);

                // same format, but with border on the left part 
                timesLborder = new WritableCellFormat(times10pt);
                timesLborder.setWrap(true);  
                timesLborder.setBorder(Border.LEFT, BorderLineStyle.THIN);
                        
                // same format with border on the right part 
                timesRborder = new WritableCellFormat(times10pt);
                timesRborder.setWrap(true);  
                timesRborder.setBorder(Border.RIGHT, BorderLineStyle.THICK);
                
		// Create create a bold font with unterlines
		WritableFont times10ptBoldUnderline = new WritableFont(
				WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		// Lets automatically wrap the cells
		timesBoldUnderline.setWrap(true);
                
                // Create create a bold font with big character
		WritableFont times14ptBold = new WritableFont(
				WritableFont.TIMES, 14, WritableFont.BOLD, false);
		times14ptBoldline = new WritableCellFormat(times14ptBold);
                //times14ptBoldline.setAlignment(Alignment.CENTRE);
		// Lets automatically wrap the cells
		times14ptBoldline.setWrap(false);
                
                // Create create a bold font with 12pt character
		WritableFont times12ptBold = new WritableFont(
				WritableFont.TIMES, 12, WritableFont.BOLD, false);
		times12ptBoldline = new WritableCellFormat(times12ptBold);
                //times12ptBoldline.setAlignment(Alignment.CENTRE);
		// Lets automatically wrap the cells
		times12ptBoldline.setWrap(false);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesLborder);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		// Write a few headers
		//addCaption(sheet, 0, 0, "Header 1");
		//addCaption(sheet, 1, 0, "This is another header");
		

	}

	public void addCaption(int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		excelSheet.addCell(label);
	}
        
        public void addString(int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, times);
		excelSheet.addCell(label);
	}
        
        public void addStringBordL(int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesLborder);
		excelSheet.addCell(label);
	}
        
        public void addStringBig(int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, times14ptBoldline);
		excelSheet.addCell(label);
	}
        
        public void addString12pt(int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, times12ptBoldline);
		excelSheet.addCell(label);
	}

	public void addNumber(int column, int row,
			double integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		excelSheet.addCell(number);
	}
        
        public void addNumberBordR(int column, int row,
			double integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, timesRborder);
		excelSheet.addCell(number);
	}
        
        public void addNumberFirst(int column, int row,
			double integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, timesLborder);
		excelSheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
        /*
	public static void main(String[] args) throws WriteException, IOException {
		WriteExcel test = new WriteExcel();
		test.setOutputFile("c:/temp/lars.xls");
		test.write();
		System.out.println("Please check the result file under c:/temp/lars.xls ");
	}*/
}
