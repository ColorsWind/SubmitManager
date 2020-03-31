package net.colors_wind.fileremap;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Row;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class StudentInfo {

	private final int index;
	private final String fileName;
	private final TreeMap<Integer, File> fileMap = new TreeMap<>();
 
	public static StudentInfo fromRowData(Row row)
			throws NullPointerException, NumberFormatException, ArrayIndexOutOfBoundsException {
		String fileName = Main.OPTIONS.getOutputFile();
		int index = Integer.parseInt(FormMap.mapCell(row.getCell(0)));
		for (int i = 1; i <= row.getLastCellNum(); i++) {
			fileName = fileName.replace(new StringBuilder("{").append(i + 1).append("}"),
					FormMap.mapCell(row.getCell(i)));
		}
		return new StudentInfo(index, fileName);
	}

	public void addFile(File file) {
		String[] split = file.getName().split("_");
		int num;
		try {
			num = Integer.parseInt(split[split.length - 2]);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("无法识别题序: " + file.getName());
		}
		fileMap.put(Integer.valueOf(num), file);
	}

	public static int getIndex(String fileName) {
		int indexOf;
		if (!fileName.startsWith("序号") || (indexOf = fileName.indexOf("_")) < 0) {
			throw new IllegalArgumentException("无法识别序号: " + fileName);
		}
		int index;
		try {
			index = Integer.parseInt(fileName.substring(2, indexOf));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("无法识别序号: " + fileName + " " + e.toString());
		}
		return index;
	}
	
	protected Set<Entry<Integer, File>> getEntrySet() {
		return fileMap.entrySet();
	}


}
