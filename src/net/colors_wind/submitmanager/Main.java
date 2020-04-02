package net.colors_wind.submitmanager;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static final Options OPTIONS = new Options();
	protected static volatile MainWindow mainWindow;
	protected static volatile OutputOptions outputOptions;
	protected static volatile PermissionRequire permissionRequire;

	public static boolean addPermission() {
		File xls = new File(OPTIONS.getInputXls());
		File dir = new File(OPTIONS.getInputDir());
		boolean[] success = new boolean[4];
		if (xls.exists()) {
			success[0] = xls.setWritable(true, false);
			success[1] = xls.setReadable(true, false);
		}
		if (dir.exists()) {
			success[2] = dir.setWritable(true, false);
			success[3] = dir.setReadable(true, false);
		}
		for (boolean b : success) {
			if (!b)
				return false;
		}
		return true;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.contains("givepermission")) {
				if (addPermission()) {
					JOptionPane.showMessageDialog(null, "成功配置文件权限", "信息", JOptionPane.OK_OPTION);
				} else {
					JOptionPane.showMessageDialog(null, "配置文件权限失败, 请尝试手动配置.", "错误", JOptionPane.ERROR_MESSAGE);
				}
				System.exit(0);
				return;
			}
		}
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
				permissionRequire = new PermissionRequire();
				permissionRequire.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

	public static String getPath() {
		URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = "CANNOT GET JAR PATH";
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

}
