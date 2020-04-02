package net.colors_wind.submitmanager;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
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
			form.finish(mainWindow);
			checkStop();
			if (Main.OPTIONS.isConvertImage()) {
				ImageOpeator image = new ImageOpeator();
				image.start(form.getStudents(), mainWindow, this);
				image.finish(mainWindow, this);
				checkStop();
			}
			Main.OPTIONS.getStrategy().resolveConflict(mainWindow, this, form);
			ConflictStrategy.finish(mainWindow, this);
			checkStop();
			FIleOperator fileOperator = new FIleOperator(dataDir);
			fileOperator.start(mainWindow, form, this);
			this.publish(100);
			fileOperator.finish(mainWindow);
		} catch (InterruptedException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mainWindow.handleTaskStop();
		}
	}
	
	public void checkStop() throws InterruptedException {
		if (thread.isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	private final AtomicInteger process = new AtomicInteger();
	public void publish(int i) {
		if (process.getAndSet(i) != i) {
			SwingUtilities.invokeLater(() -> mainWindow.progressBar.setValue(i));
		}
	}
	public static final int PROGESS_FORM = 20;
	public static final int PROGRESS_IMAGE = 25;
	public static final int PROGRESS_CONFLICT = 10;
	public static final int PROGRESS_FILE = 45;
	
	public void publishImage(int processed, int total) {
		double d = PROGRESS_IMAGE * processed / (double)total;
		publish(PROGESS_FORM + (int)d);
	}
	
	public void publishConflict(int processed, int total) {
		double d = PROGRESS_CONFLICT * processed / (double)total;
		publish(PROGESS_FORM + PROGRESS_IMAGE + (int)d);
	}
	
	public void publishFile(int processed, int total) {
		double d = PROGRESS_FILE * processed / (double)total;
		publish(PROGESS_FORM + PROGRESS_IMAGE + PROGRESS_CONFLICT + (int)d);
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
		if (PermissionRequire.checkPermission(xlsFile) && PermissionRequire.checkPermission(dataDir)) {
			thread = new Thread(this, "handleFile");
			thread.join();
			thread.start();
		}
	}
	
	public void cancel() {
		if (thread == null || !thread.isAlive()) {
			mainWindow.println("没有正在进行的工作.");
		}
		thread.interrupt();
	}


}
