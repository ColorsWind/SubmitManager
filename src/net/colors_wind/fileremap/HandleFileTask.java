package net.colors_wind.fileremap;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class HandleFileTask implements Runnable {
	private final MainWindow mainWindow;
	private final File xlsFile;
	private final File dataDir;

	private Thread thread;

	@Override
	public void run() {
		mainWindow.handleTaskStart();
		try {
			FormMap form = new FormMap(xlsFile);
			form.inputWrap(mainWindow);
			RenameTask rename = new RenameTask(dataDir);
			rename.start(mainWindow, form);
		} catch (InterruptedException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mainWindow.handleTaskStop();
		}
	}
	
	@SneakyThrows
	public void start() {
		if (xlsFile == null || !xlsFile.exists()) {
			mainWindow.println("表格文件不存在, 请重新选择.");
			return;
		}
		if (dataDir == null || !dataDir.exists()) {
			mainWindow.println("输入文件夹不存在, 请重新选择.");
			return;
		}
		thread = new Thread(this, "handleFile");
		thread.join();
		thread.start();
	}
	
	public void cancel() {
		if (thread == null || !thread.isAlive()) {
			mainWindow.println("没有正在进行的工作.");
		}
		thread.interrupt();
	}

}
