package net.colors_wind.fileremap;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileMoveOperator {
	private final File folder;
	private final AtomicInteger countSuccess = new AtomicInteger();

	public void start(MainWindow mainWindow, FormMap form) {
		File[] files = folder.listFiles((dir, name) -> {
			return name.toLowerCase().endsWith(".pdf");
		});
		mainWindow.printlnSafty(new StringBuilder("共找到 ").append(files.length).append(" 个文件, 重命名工作即将开始.").toString());
		Arrays.stream(files).parallel().forEach(file -> {
			try {
				StudentInfo studentInfo = form.getStudentInfo(file.getName());
				File re = new File(folder, studentInfo.getFileName());
				file.renameTo(re);
				mainWindow.printlnSafty(new StringBuilder("将 ").append(file.getName()).append(" 重命名为 ").append(re.getName())
						.toString());
				countSuccess.incrementAndGet();
			} catch (IllegalArgumentException e) {
				mainWindow.printlnError("重命名文件时发送错误: ", e);
				e.printStackTrace();
			}
		});
		mainWindow.printlnSafty(new StringBuilder("成功重命名 ").append(countSuccess.get()).append(" 个文件, 失败 ")
				.append(files.length - countSuccess.get()).append(" 个文件").toString());
	}

}
