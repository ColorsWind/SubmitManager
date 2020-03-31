package net.colors_wind.fileremap;

import java.io.File;

import lombok.SneakyThrows;

public class ProcessFileTask implements Runnable {
	private final MainWindow mainWindow;
	private final File xlsFile;
	private final File dataDir;
	
	public ProcessFileTask(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.xlsFile = new File(Main.OPTIONS.getInputXls());
		this.dataDir = new File(Main.OPTIONS.getInputDir());
	}

	private Thread thread;

	@Override
	public void run() {
		mainWindow.handleTaskStart();
		try {
			FormMap form = new FormMap();
			form.inputForm(xlsFile, mainWindow);
			form.inputFileList(dataDir, mainWindow);
			FileContentOperator content = new FileContentOperator();
			content.convert(form.getStudents(), mainWindow);
			FileMoveOperator move = new FileMoveOperator(dataDir);
			move.start(mainWindow, form);
			move.finish(mainWindow);
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