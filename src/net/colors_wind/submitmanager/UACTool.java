package net.colors_wind.submitmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * https://github.com/kasp315b/UACTool
 * 
 * @author kasp315b
 *
 */
public class UACTool {

	// {{ Variables
	private File executable;
	private String arguments;
	private boolean showWindow;
	private boolean showPopup;
	private boolean useIntermediate;

	private File vbsFile;
	private File batFile;

	private String tempDir;
	private String wscriptLocation;

	private boolean hasPrepared;
	private boolean hasExecuted;

	private String popupMessage;

	public final static String standardMessage1 = "The following application has asked for administrator priviledges.\n\n{EXECUTABLE_PATH}\n\nIt will be launched using cmd.exe.\nPlease accept the following UAC request to continue using it.";
	public final static String standardMessage2 = "The application needs to be run as admin to function correctly.\nPlease say yes to the following UAC request.\n({EXECUTABLE_NAME})";
	// }}

	// {{ Constructors
	public UACTool() {
		if (!isOSCompatible()) {
			System.err.println("This operating system is not supported.");
			throw new UnsupportedOperationException("Incompatible Operating System");
		}

		executable = null;
		arguments = "";
		showWindow = true;
		showPopup = false;
		useIntermediate = false;
		vbsFile = null;
		batFile = null;
		tempDir = System.getProperty("java.io.tmpdir");
		wscriptLocation = System.getenv("windir") + "\\System32\\wscript.exe";
		hasPrepared = false;
		hasExecuted = false;
		popupMessage = standardMessage1;
	}

	public UACTool(File executable, String arguments, boolean showWindow, boolean useIntermediate, boolean showPopup) {
		this();
		this.setSettings(executable, arguments, showWindow, useIntermediate, showPopup);
	}

	public UACTool(File executable, String arguments, boolean showWindow) {
		this();
		this.setSettings(executable, arguments, showWindow);
	}

	public UACTool(File executable, String arguments) {
		this();
		this.setSettings(executable, arguments);
	}

	public UACTool(File executable) {
		this();
		this.setSettings(executable);
	}
	// }}

	// {{ All in One Methods
	public UACTool setSettings(File executable, String arguments, boolean showWindow, boolean useIntermediate,
			boolean showPopup) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing");
			return this;
		}
		this.setExecutable(executable);
		this.setArguments(arguments);
		this.setShowApplicationWindow(showWindow);
		this.setUseIntermediate(useIntermediate);
		this.setShowPopup(showPopup);
		return this;
	}

	public UACTool setSettings(String executablepath, String arguments, boolean showWindow, boolean useIntermediate,
			boolean showPopup) {
		return setSettings(new File(executablepath), arguments, showWindow, useIntermediate, showPopup);
	}

	public UACTool setSettings(File executable, String arguments, boolean showWindow) {
		return setSettings(executable, arguments, showWindow, useIntermediate, showPopup);
	}

	public UACTool setSettings(String executablepath, String arguments, boolean showWindow) {
		return setSettings(new File(executablepath), arguments, showWindow);
	}

	public UACTool setSettings(File executable, String arguments) {
		return setSettings(executable, arguments, showWindow);
	}

	public UACTool setSettings(String executablepath, String arguments) {
		return setSettings(new File(executablepath), arguments);
	}

	public UACTool setSettings(File executable) {
		return setSettings(executable, arguments);
	}

	public UACTool setSettings(String executablepath) {
		return setSettings(new File(executablepath));
	}
	// }}

	// {{ "Getters" and "Setters"
	public void setJarExecutable(String jarpath) {
		setJarExecutable(new File(jarpath));
	}

	public void setJarExecutable(File jarfile) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.setShowApplicationWindow(false);
		this.setShowPopup(true);
		this.setUseIntermediate(true);
		this.setExecutable(jarfile);
	}

	public void setExecutable(String executablePath) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.executable = new File(executablePath);
	}

	public void setExecutable(File executable) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.executable = executable;
	}

	public void setArguments(String arguments) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.arguments = arguments;
	}

	public void setShowApplicationWindow(boolean showWindow) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.showWindow = showWindow;
	}

	public void setShowPopup(boolean showPopup) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.showPopup = showPopup;
	}

	public void setUseIntermediate(boolean useIntermediate) {
		if (hasPrepared) {
			System.out.println("Cannot change settings after preparing.");
			return;
		}
		this.useIntermediate = useIntermediate;
	}

	public File getExecutable() {
		return executable;
	}

	public String getArguments() {
		return arguments;
	}

	public boolean getShowApplicationWindow() {
		return showWindow;
	}

	public boolean getShowPopup() {
		return showPopup;
	}

	public boolean getUseIntermediate() {
		return useIntermediate;
	}
	// }}

	// {{ Finalisation Methods
	public void prepare() throws IOException {
		if (useIntermediate) {
			String batchName = "launchbat__" + getRandomFilename(8, ".bat");
			String vbsName = "elevatevbs_" + getRandomFilename(8, ".vbs");

			batFile = new File(tempDir + batchName);
			batFile.deleteOnExit();
			vbsFile = new File(tempDir + vbsName);
			vbsFile.deleteOnExit();

			String batchScript = getBatchScript(executable.getAbsolutePath(), arguments);
			String vbsScript = getVBSScript(batFile.getName(), showWindow);

			byte[] batchBytes = batchScript.getBytes();
			byte[] vbsBytes = vbsScript.getBytes();

			FileOutputStream bout = new FileOutputStream(batFile);
			bout.write(batchBytes);
			bout.flush();
			bout.close();

			FileOutputStream vout = new FileOutputStream(vbsFile);
			vout.write(vbsBytes);
			vout.flush();
			vout.close();

			System.out.println("Created bat file: " + batchName);
			System.out.println("Created vbs file: " + vbsName);
		} else {
			if (getFiletype(executable.getName()).equals(".jar"))
				System.out.println("It is recommended to use an intermediate when launching jar files.");

			String vbsName = "elevatevbs_" + getRandomFilename(8, ".vbs");

			vbsFile = new File(tempDir + vbsName);
			vbsFile.deleteOnExit();

			String vbsScript = getVBSScript(executable.getAbsolutePath(), arguments, showWindow);

			byte[] vbsBytes = vbsScript.getBytes();

			FileOutputStream vout = new FileOutputStream(vbsFile);
			vout.write(vbsBytes);
			vout.flush();
			vout.close();

			System.out.println("Created vbs file: " + vbsName);
		}
		hasPrepared = true;
	}

	public void execute() throws IOException {
		if (!hasPrepared) { // Check if all files have been prepared.
			System.err.println("Method \"prepare()\" has to be called before \"execute()\" can be called.");
			return;
		}

		if (hasExecuted) { // Check if this method has been called already.
			System.err.println(addQuotes(executable.getName()) + " has already been started.");
			return;
		}

		if (showPopup) {
			String parsedMessage = parseMessage(popupMessage);
			JOptionPane.showMessageDialog(null, parsedMessage, "Administrator Privilege Request",
					JOptionPane.INFORMATION_MESSAGE);
		}

		String execString = wscriptLocation + " " + addQuotes(vbsFile.getAbsolutePath());

		Process process = Runtime.getRuntime().exec(execString);
		System.out.println("Launched application. (" + addQuotes(executable.getName()) + ")");
		System.out.println("Waiting for it to exit.");
		try {
			System.out.println("Exited with code: " + process.waitFor());
		} catch (InterruptedException e) {
			System.err.println("Thread interrupted, cannot wait for exit code.");
		}

		hasExecuted = true;
	}

	public void prepareAndExecute() throws IOException {
		this.prepare();
		this.execute();
	}
	// }}

	// {{ Static Execution Methods
	public static boolean runAsAdmin(File executable, String arguments, boolean showWindow, boolean useIntermediate,
			boolean showPopup) {
		boolean success = true;
		UACTool tool = new UACTool();
		tool.setExecutable(executable);
		tool.setArguments(arguments);
		tool.setShowApplicationWindow(showWindow);
		tool.setUseIntermediate(useIntermediate);
		tool.setShowPopup(showPopup);

		try {
			tool.prepare();
		} catch (IOException e) {
			success = false;
			System.err.println("UACTool: Failed to prepare for execution");
		}
		if (success) {
			try {
				tool.execute();
			} catch (IOException e) {
				success = false;
				System.err.println("UACTool: Failed to execute");
			}
		}

		return success;
	}

	public static boolean runAsAdmin(File executable, String arguments, boolean showWindow, boolean useIntermediate) {
		return runAsAdmin(executable, arguments, showWindow, useIntermediate, false);
	}

	public static boolean runAsAdmin(File executable, String arguments, boolean showWindow) {
		return runAsAdmin(executable, arguments, showWindow, false);
	}

	public static boolean runAsAdmin(File executable, String arguments) {
		return runAsAdmin(executable, arguments, true);
	}

	public static boolean runAsAdmin(File executable) {
		return runAsAdmin(executable, "");
	}

	public static boolean runJarAsAdmin(File jarfile, String arguments, boolean showPopup) {
		return runAsAdmin(jarfile, arguments, false, true, showPopup);
	}

	public static boolean runJarAsAdmin(File jarfile, String arguments) {
		return runJarAsAdmin(jarfile, arguments, true);
	}

	public static boolean runJarAsAdmin(File jarfile) {
		return runJarAsAdmin(jarfile, "");
	}

	public static boolean runAsAdmin(String executablepath, String arguments, boolean showWindow,
			boolean useIntermediate, boolean showPopup) {
		return runAsAdmin(new File(executablepath), arguments, showWindow, useIntermediate, showPopup);
	}

	public static boolean runAsAdmin(String executablepath, String arguments, boolean showWindow,
			boolean useIntermediate) {
		return runAsAdmin(new File(executablepath), arguments, showWindow, useIntermediate, false);
	}

	public static boolean runAsAdmin(String executablepath, String arguments, boolean showWindow) {
		return runAsAdmin(new File(executablepath), arguments, showWindow, false);
	}

	public static boolean runAsAdmin(String executablepath, String arguments) {
		return runAsAdmin(new File(executablepath), arguments, true);
	}

	public static boolean runAsAdmin(String executablepath) {
		return runAsAdmin(new File(executablepath), "");
	}

	public static boolean runJarAsAdmin(String jarpath, String arguments, boolean showPopup) {
		return runAsAdmin(new File(jarpath), arguments, false, true, showPopup);
	}

	public static boolean runJarAsAdmin(String jarpath, String arguments) {
		return runJarAsAdmin(new File(jarpath), arguments, true);
	}

	public static boolean runJarAsAdmin(String jarpath) {
		return runJarAsAdmin(new File(jarpath), "");
	}
	// }}

	// {{ Helper Methods and Misc.
	private static String getBatchScript(String executablePath, String arguments) {
		String batch;
		String filetype = getFiletype(executablePath);
		if (filetype.equals(".jar")) {
			batch = "cd /D %~dp0\r\n" + "javaw -jar " + addQuotes(executablePath) + " " + arguments + "\r\n";
		} else {
			batch = "cd /D %~dp0\r\n" + addQuotes(executablePath) + " " + arguments + "\r\n";
		}

		return batch;
	}

	private static String getVBSScript(String applicationpath, boolean visible) {
		return getVBSScript(applicationpath, "", visible);
	}

	private static String getVBSScript(String applicationpath, String arguments, boolean visible) {
		String vbs = "Set objShell = CreateObject(\"Wscript.Shell\")\r\n" + "strPath = Wscript.ScriptFullName\r\n"
				+ "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\r\n"
				+ "Set objFile = objFSO.GetFile(strPath)\r\n" + "strFolder = objFSO.GetParentFolderName(objFile)\r\n"
				+ "Set UAC = CreateObject(\"Shell.Application\")\r\n" + "UAC.ShellExecute " + addQuotes(applicationpath)
				+ ", " + addQuotes(arguments) + ", strFolder, \"runas\", " + (visible ? "1" : "0") + "\r\n";
		return vbs;
	}

	private static String getFiletype(String path) {
		int liod = path.lastIndexOf('.');
		if (liod == -1) {
			return "";
		} else {
			return path.substring(liod);
		}
	}

	public static void setNamegenerationCharset(char[] newCharset) {
		UACTool.charset = newCharset;
	}

	public static char[] getNamegenerationCharset() {
		return charset;
	}

	private static char[] charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	private static String getRandomFilename(int length, String extension) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(charset[(int) (Math.floor(Math.random() * charset.length))]);
		sb.append(extension);
		return sb.toString();
	}

	private static String addQuotes(String toQuotify) {
		if (toQuotify == null)
			return null;
		if (toQuotify.equals(""))
			return "\"\"";
		return (toQuotify.charAt(0) == '"' ? "" : '"') + toQuotify
				+ (toQuotify.charAt(toQuotify.length() - 1) == '"' ? "" : '"');
	}

	public static final boolean isOSCompatible() {
		boolean isSupported = System.getProperty("sun.desktop").equals("windows");
		return isSupported;
	}

	private String parseMessage(String toParse) {
		String result = toParse;
		result = result.replace("{EXECUTABLE_NAME}", executable.getName());
		result = result.replace("{EXECUTABLE_PATH}", executable.getAbsolutePath());
		return result;
	}
	// }}
}