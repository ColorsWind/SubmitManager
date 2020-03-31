package net.colors_wind.fileremap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

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
			this.setStrategy(ConflictStrategy.getStrategy(map.get("ConflictStrategy").toString()).orElse(strategy));
			reader.close();
			in.close();
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
		map.put("ConflictStrategy", strategy.name());
		OutputStream out = new FileOutputStream(file);
		Writer writer = new OutputStreamWriter(out, "utf8");
		String yamlString = yaml.dumpAsMap(map);
		writer.append(yamlString);
		writer.close();
		out.close();
	}
	
	public void loadFromWindow(MainWindow mainWindow) {
		this.inputDir = mainWindow.inputFolder.getText();
		this.inputXls = mainWindow.inputXls.getText();
		AdvancedOptions aop = mainWindow.advancedOptions;
		this.outputFile = aop.outputFile.getText();
		this.addRawData = aop.checkboxAddRawData.isSelected();
		this.strategy = aop.getConflictStrategy();
	}
	
	public void updateWindow(MainWindow mainWindow) {
		mainWindow.inputFolder.setText(this.inputDir);
		mainWindow.inputXls.setText(this.inputXls);
		AdvancedOptions aop = mainWindow.advancedOptions;
		aop.checkboxAddRawData.setSelected(addRawData);
		aop.outputFile.setText(this.outputFile);
		aop.setConflictStrategy(strategy);
	}

}
