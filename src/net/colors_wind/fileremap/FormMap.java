package net.colors_wind.fileremap;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
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
	private final ConcurrentMap<Integer, StudentInfo> data = new ConcurrentHashMap<>();

	private void inputForm0(@NonNull File file) throws Exception {
		Workbook excel = WorkbookFactory.create(file);
		Sheet sheet = excel.getSheetAt(0);
		Objects.requireNonNull(sheet, "找不到工作表.");
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null)
				continue;
			StudentInfo studentInfo = StudentInfo.fromRowData(row);
			this.data.put(studentInfo.getIndex(), studentInfo);
		}
		excel.close();
	}
	
	

	public void inputForm(@NonNull File file, @NonNull MainWindow mainWindow) throws InterruptedException {
		try {
			inputForm0(file);
			mainWindow.printlnSafty(new StringBuilder("成功读取表格, 共计: ").append(data.size()).append(" 条数据.").toString());
		} catch (Exception e) {
			mainWindow.printlnSafty(new StringBuilder("读取表格时出现异常: ").append(e.toString()).toString());
			e.printStackTrace();
			throw new InterruptedException();
		}
	}
	
	public void inputFileList(File dir, MainWindow mainWindow) {
		Arrays.stream(dir.listFiles(FileContentOperator.ALL_FILTER)).forEach(file -> {
			try {
				StudentInfo info = getStudentInfo(file.getName());
				info.addFile(file);
			} catch (Exception e) {
				mainWindow.printlnError(e);
				e.printStackTrace();
			}
		});
	}

	public StudentInfo getStudentInfo(@NonNull String fileName) throws IllegalArgumentException {
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

	public StudentInfo getStudentInfo(int index) {
		StudentInfo studentInfo = data.get(Integer.valueOf(index));
		if (studentInfo == null) {
			throw new NullPointerException("无法在表格文件找到序号: " + index);
		}
		return studentInfo;
	}

	public static String mapCell(Cell cell) {
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
	public static String doubleToString(double d) {
		if (Math.abs(d - Math.floor(d)) < EPS) {
			return Integer.toString((int) Math.floor(d));
		}
		return String.valueOf(d);
	}



	public Collection<StudentInfo> getStudents() {
		return data.values();
	}
}
