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

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.yaml.snakeyaml.Yaml;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Options {
	public static final String CONF_FILE_NAME = "./fileremap.yml";
	@NonNull
	private volatile String inputXls = "";
	@NonNull
	private volatile String inputDir = "";
	@NonNull
	private volatile String outputFile = "{7}-{8}.pdf";
	private volatile boolean addRawData = false;
	private volatile boolean tryToCombine = true;
	private volatile boolean convertImage = true;
	private volatile boolean moveInsteadCopy = true;
	@NonNull
	private volatile ConflictStrategy strategy = ConflictStrategy.COMBINE_BY_ASCEND;


	public void loadFromFile() throws IOException {
		File file = new File(CONF_FILE_NAME);
		if (file.exists()) {
			Yaml yaml = new Yaml();
			InputStream in = new FileInputStream(file);
			Reader reader = new InputStreamReader(in, "utf8");
			Map<String, Object> map = yaml.load(reader);
			this.setInputDir(map.get("InputDir").toString());
			this.setInputXls(map.get("InputXls").toString());
			this.setOutputFile(map.get("OutputFile").toString());
			this.setAddRawData(Boolean.parseBoolean(map.get("AddRawData").toString()));
			this.setTryToCombine(Boolean.parseBoolean(map.get("TryToCombine").toString()));
			this.setConvertImage(Boolean.parseBoolean(map.get("ConvertImage").toString()));
			this.setMoveInsteadCopy(Boolean.parseBoolean(map.get("MoveInsteadCopy").toString()));
			this.setStrategy(ConflictStrategy.getStrategy(map.get("ConflictStrategy").toString()).orElse(strategy));
			reader.close();
			in.close();
		}
	}
	
	public void updateFileWrap(MainWindow mainWindow) {
		try {
			updateFile();
		} catch (IOException e) {
			mainWindow.printlnError("保存配置时出现异常: ", e);
			e.printStackTrace();
		}
	}
	
	public void updateFile() throws IOException {
		File file = new File(CONF_FILE_NAME);
		if (!file.exists()) {
			file.createNewFile();
		}
		Yaml yaml = new Yaml();
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("InputDir", inputDir);
		map.put("InputXls", inputXls);
		map.put("OutputFile", outputFile);
		map.put("AddRawData", addRawData);
		map.put("TryToCombine", tryToCombine);
		map.put("ConvertImage", convertImage);
		map.put("MoveInsteadCopy", moveInsteadCopy);
		map.put("ConflictStrategy", strategy.name());
		OutputStream out = new FileOutputStream(file);
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
		this.strategy = outputOption.getConflictStrategy();
		this.addRawData = outputOption.checkboxAddRawData.isSelected();
		this.tryToCombine = outputOption.checkboxCombine.isSelected();
		this.convertImage = outputOption.checkboxImage.isSelected();
		this.moveInsteadCopy = outputOption.checkboxMove.isSelected();
	}
	
	public void updateWindow(MainWindow mainWindow, OutputOptions outputOption) {
		mainWindow.inputDir.setText(this.inputDir);
		mainWindow.inputXls.setText(this.inputXls);
		outputOption.outputFile.setText(this.outputFile);
		outputOption.setConflictStrategy(strategy);
		outputOption.checkboxAddRawData.setSelected(addRawData);
		outputOption.checkboxCombine.setSelected(tryToCombine);
		outputOption.checkboxImage.setSelected(convertImage);
		outputOption.checkboxMove.setSelected(moveInsteadCopy);
	}
	
	public TrueTypeFont loadFont(MainWindow mainWindow) {
		File file = new File(".\\font.ttf");
		if (file.exists()) {
			try {
				return new TTFParser().parse(file);
			} catch (IOException e) {
				mainWindow.printlnError("读取自定义字体时出现异常: ", e);
				e.printStackTrace();
			}
		}
		InputStream in = Main.class.getResourceAsStream("SourceHanSansSC-Medium.ttf");
		try {
			return new TTFParser().parse(in);
		} catch (IOException e) {
			mainWindow.printlnError("读取内置字体时出现异常: ", e);
			e.printStackTrace();
		}
		return null;
	}

}
