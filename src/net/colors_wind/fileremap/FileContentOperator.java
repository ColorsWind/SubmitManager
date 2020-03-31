package net.colors_wind.fileremap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class FileContentOperator {
	public static final String PDF_FILE = ".pdf";
	public static final String[] IMG_FILE = new String[] { ".jpg", ".jpeg", ".png", ".gif" };

	public static final FilenameFilter PDF_FILTER = (dir, name) -> {
		return name.toLowerCase().endsWith(PDF_FILE);
	};

	public static final FilenameFilter IMG_FILTER = (dir, name) -> {
		name = name.toLowerCase();
		return Arrays.stream(IMG_FILE).anyMatch(name::endsWith);
	};

	public static final FilenameFilter ALL_FILTER = (dir, name) -> {
		name = name.toLowerCase();
		return name.endsWith(PDF_FILE) || Arrays.stream(IMG_FILE).anyMatch(name::endsWith);
	};
	
	public void convert(Collection<StudentInfo> students, MainWindow mainWindow) {
		students.parallelStream().forEach(studentInfo -> {
			studentInfo.getEntrySet().forEach(entry -> {
				String rawName = entry.getValue().getName(); 
				if (IMG_FILTER.accept(null, rawName)) {
					
					try {
						entry.setValue(imgToPDF(entry.getValue()));
					} catch (IOException e) {
						mainWindow.printlnError(new StringBuilder("将图片 ").append(entry.getValue().getName()).append(" 转为 PDF 时候出现异常: "), e);
						e.printStackTrace();
					}
				}
				if (Main.OPTIONS.isAddRawData() && PDF_FILTER.accept(null, entry.getValue().getName())) {
					try {
						addRawData(entry.getValue(), rawName);
					} catch (IOException e) {
						mainWindow.printlnError(new StringBuilder("将为PDF ").append(entry.getValue().getName()).append(" 添加文件原始信息时出现异常: "), e);
						e.printStackTrace();
					}
				}
			});
		});
	}
	
	private static File imgToPDF(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		PDPage pdPage = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
		PDDocument pdf = new PDDocument();
		pdf.addPage(pdPage);
		File pdfFile = new File(file.getParentFile(), new StringBuilder(file.getName()).append(".pdf").toString());
		pdf.save(pdfFile);
		pdf.close();
		return pdfFile;
	}
	
	private static void addRawData(File file, String rawName) throws IOException {
		PDDocument pdf = PDDocument.load(file);
		for(PDPage page : pdf.getPages()) {
			PDPageContentStream content = new PDPageContentStream(pdf, page);
			content.moveTo(2.0f, 2.0f);
			content.showText(rawName);
			content.close();
		}
		pdf.save(file);
	}
}
