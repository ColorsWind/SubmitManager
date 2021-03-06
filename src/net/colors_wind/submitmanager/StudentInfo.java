package net.colors_wind.submitmanager;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Row;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class StudentInfo implements Comparable<StudentInfo> {
	public static final Comparator<StudentInfo> ASCEND = Comparator.comparing(StudentInfo::getIndex);
	public static final Comparator<StudentInfo> DESCEND = Collections.reverseOrder(ASCEND);
	@Getter
	private final int index;
	@Getter
	private final String fileName;
	@Getter(AccessLevel.PROTECTED)
	private final TreeMap<Integer, File> fileMap = new TreeMap<>();
	
	public String getFileNameLowerCase() {
		return fileName.toLowerCase();
	}
 
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

	public void addStudentFile(File file) {
		String[] split = file.getName().split("_");
		int num;
		try {
			num = Integer.parseInt(split[2]);
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

	@Override
	public int compareTo(@NonNull StudentInfo o) {
		return Integer.compare(this.index, o.index);
	}
	
	


}
