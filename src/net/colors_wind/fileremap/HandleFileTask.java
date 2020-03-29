package net.colors_wind.fileremap;

import java.io.File;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HandleFileTask implements Runnable {
	private final MainWindow mainWindow;
	private final File xlsFile;
	private final File dataDir;
	
	

	public HandleFileTask(MainWindow mainWindow, File xlsFile, File dataDir) {
		super();
		this.mainWindow = mainWindow;
		this.xlsFile = xlsFile;
		this.dataDir = dataDir;
	}



	@Override
	public void run() {
		mainWindow.handleTaskStart();
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mainWindow.handleTaskStop();
		}
	}

}
