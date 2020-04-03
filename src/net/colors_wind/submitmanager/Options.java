package net.colors_wind.submitmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.yaml.snakeyaml.Yaml;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Options {
	public static final File CONF_FILE_NAME;
	static {
		File userDir = new File(System.getProperties().getProperty("user.home"));
		CONF_FILE_NAME = new File(userDir, "submitmanager.yml");
	}
	public static final File TEMP_DIR;
	static {
		File sysTmp = new File(System.getProperty("java.io.tmpdir"));
		TEMP_DIR = new File(sysTmp, "subitmanager");
		if (!TEMP_DIR.exists()) {
			TEMP_DIR.mkdir();
		}
	}
	@NonNull
	private volatile String inputXls = "";
	@NonNull
	private volatile String inputDir = "";
	@NonNull
	private volatile String outputFile = "{8}-{7}.pdf";
	private volatile boolean addRawData = false;
	private volatile boolean tryToCombine = true;
	private volatile boolean convertImage = true;
	@NonNull
	private volatile ConflictStrategy strategy = ConflictStrategy.ADD_PREFIX;


	public void loadFromFile() throws IOException {
		if (CONF_FILE_NAME.exists()) {
			Yaml yaml = new Yaml();
			InputStream in = new FileInputStream(CONF_FILE_NAME);
			Reader reader = new InputStreamReader(in, "utf8");
			Map<String, Object> map = yaml.load(reader);
			this.setInputDir(Objects.toString(map.get("InputDir")));
			this.setInputXls(Objects.toString(map.get("InputXls")));
			this.setOutputFile(Objects.toString(map.get("OutputFile")));
			this.setAddRawData(Boolean.parseBoolean(map.get("AddRawData").toString()));
			this.setTryToCombine(Boolean.parseBoolean(Objects.toString(map.get("TryToCombine"))));
			this.setConvertImage(Boolean.parseBoolean(Objects.toString(map.get("ConvertImage"))));
			this.setStrategy(ConflictStrategy.getStrategy(Objects.toString(map.get("ConflictStrategy"))).orElse(strategy));
			reader.close();
			in.close();
		}
	}
	
	public void updateFileWrap(MainWindow mainWindow) {
		try {
			updateFile();
		} catch (IOException e) {
			mainWindow.printlnError("保存配置时出现异常", e);
			e.printStackTrace();
		}
	}
	
	public void updateFile() throws IOException {
		if (!CONF_FILE_NAME.exists()) {
			CONF_FILE_NAME.createNewFile();
		}
		Yaml yaml = new Yaml();
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("InputDir", inputDir);
		map.put("InputXls", inputXls);
		map.put("OutputFile", outputFile);
		map.put("AddRawData", addRawData);
		map.put("TryToCombine", tryToCombine);
		map.put("ConvertImage", convertImage);
		map.put("ConflictStrategy", strategy.name());
		OutputStream out = new FileOutputStream(CONF_FILE_NAME);
		Writer writer = new OutputStreamWriter(out, "utf8");
		String yamlString = yaml.dumpAsMap(map);
		writer.append(yamlString);
		writer.close();
		out.close();
	}
	
	public void loadFromWindow(MainWindow mainWindow, OutputOptions outputOption) {
		this.inputDir = mainWindow.inputDir.getText();
		this.inputXls = mainWindow.inputXls.getText();
		this.outputFile = outputOption.outputFile.getText();
		this.strategy = ConflictStrategy.getSelectConflictStrategy();
		this.addRawData = outputOption.checkboxAddRawData.isSelected();
		this.tryToCombine = outputOption.checkboxCombine.isSelected();
		this.convertImage = outputOption.checkboxImage.isSelected();
	}
	
	/**
	 * EDT Thread only
	 * @param mainWindow
	 * @param outputOption
	 */
	public void updateWindow(MainWindow mainWindow, OutputOptions outputOption) {
		mainWindow.inputDir.setText(this.inputDir);
		mainWindow.inputXls.setText(this.inputXls);
		outputOption.outputFile.setText(this.outputFile);
		strategy.setSelect(true);
		outputOption.checkboxAddRawData.setSelected(addRawData);
		outputOption.checkboxCombine.setSelected(tryToCombine);
		outputOption.checkboxImage.setSelected(convertImage);
	}
	
	public TrueTypeFont loadFont(MainWindow mainWindow) {
		File file = new File(".\\font.ttf");
		if (file.exists()) {
			try {
				return new TTFParser().parse(file);
			} catch (IOException e) {
				mainWindow.printlnError("读取自定义字体时出现异常", e);
				e.printStackTrace();
			}
		}
		InputStream in = Main.class.getResourceAsStream("/com/github/adobe_fonts/SourceHanSansSC-Medium.ttf");
		try {
			return new TTFParser().parse(in);
		} catch (NullPointerException | IOException e) {
			mainWindow.printlnError("读取内置字体时出现异常", e);
			e.printStackTrace();
		}
		return null;
	}

}
