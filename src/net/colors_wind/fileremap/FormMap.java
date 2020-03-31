package net.colors_wind.fileremap;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormMap {
	@NonNull
	private final File file;
	private final ConcurrentMap<Integer, String[]> data = new ConcurrentHashMap<>();

	public void inputFormMap() throws Exception {
		Workbook excel = WorkbookFactory.create(file);
		Sheet sheet = excel.getSheetAt(0);
		Objects.requireNonNull(sheet, "找不到工作表.");
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null)
				continue;
			String[] studentInfo = new String[row.getLastCellNum() + 1];
			for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
				Cell cell = row.getCell(cellNum);
				studentInfo[cellNum] = mapCell(cell);
			}
			try {
				int index = (int) Double.parseDouble(studentInfo[0]);
				data.put(Integer.valueOf(index), studentInfo);
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	public void inputWrap(@NonNull MainWindow mainWindow) throws InterruptedException {
		try {
			inputFormMap();
			mainWindow.printlnSafty(new StringBuilder("成功读取表格, 共计: ").append(data.size()).append(" 条数据.").toString());
		} catch (Exception e) {
			mainWindow.printlnSafty(new StringBuilder("读取表格时出现异常: ").append(e.toString()).toString());
			e.printStackTrace();
			throw new InterruptedException();
		}
	}

	public String[] getStudentInfo(@NonNull String fileName) throws IllegalArgumentException {
		int indexOf;
		if (!fileName.startsWith("序号") || (indexOf = fileName.indexOf("_")) < 0) {
			throw new IllegalArgumentException("无法识别文件名: " + fileName);
		}
		int index;
		try {
			index = Integer.parseInt(fileName.substring(2, indexOf));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("无法识别文件名: " + fileName + " " + e.toString());
		}
		return getStudentInfo(index);
	}

	public String[] getStudentInfo(int index) {
		String[] studentInfo = data.get(Integer.valueOf(index));
		if (studentInfo == null) {
			throw new NullPointerException("无法在表格文件找到序号: " + index);
		}
		return studentInfo;
	}

	public String mapCell(Cell cell) {
		if (cell == null) {
			return "null";
		}
		switch (cell.getCellType()) {
		case BLANK:
			return "empty";
		case BOOLEAN:
			return cell.getBooleanCellValue() ? "真" : "假";
		case FORMULA:
			return cell.getCellFormula();
		case NUMERIC:
			return doubleToString(cell.getNumericCellValue());
		case STRING:
			return cell.getStringCellValue();
		default:
			return "error";
		}
	}
	public static double EPS = 1E-10;
	public String doubleToString(double d) {
		if (Math.abs(d - Math.floor(d)) < EPS) {
			return Integer.toString((int) Math.floor(d));
		}
		return String.valueOf(d);
	}
}
