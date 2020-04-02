package net.colors_wind.submitmanager;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.UIManager;

public class Main {
	public static final Options OPTIONS = new Options();
	protected static volatile MainWindow mainWindow;
	protected static volatile OutputOptions outputOptions;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Main.OPTIONS.loadFromFile();
			Main.OPTIONS.updateFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(() -> {
			try {
				mainWindow = new MainWindow();
				centre(mainWindow);
				outputOptions = new OutputOptions();
				outputOptions.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				centre(outputOptions);
				OPTIONS.updateWindow(mainWindow, outputOptions);
				mainWindow.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					OPTIONS.updateFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	public static void centre(Window window) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		int screenheight = screensize.height;
		int screenwidth = screensize.width;
		window.setLocation(screenwidth / 2 - window.getWidth() / 2, screenheight / 2 - window.getHeight() / 2);
	}

}
