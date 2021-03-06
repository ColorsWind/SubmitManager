package net.colors_wind.submitmanager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

public class FIleOperator {
	private final File targetDir;

	public FIleOperator(File dataDir) {
		this.targetDir = new File(dataDir, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
		targetDir.mkdirs();
	}


	public void start(MainWindow mainWindow, StudentInfo[] students, ProcessFileTask task) {
		final int total = students.length;
		final AtomicInteger count = new AtomicInteger();
		TrueTypeFont font = Main.OPTIONS.isAddRawData() ? Main.OPTIONS.loadFont(mainWindow) : null;
		Arrays.stream(students).parallel().forEach(studentInfo -> {
			try {
				File targetFile = new File(targetDir, studentInfo.getFileName());
				savePdf(targetFile, studentInfo, font);
				mainWindow.println(new StringBuilder("saved ")
						.append(targetFile.getName()).toString());
				publish(task, count.incrementAndGet(), total);
			} catch (IllegalArgumentException | IOException e) {
				mainWindow.printlnError("处理文件时发生错误", e);
				e.printStackTrace();
			}
		});
		mainWindow.println(new StringBuilder("成功保存 ").append(count.get()).append(" 个文件, 失败 ")
				.append(total - count.get()).append(" 个文件").toString());
	}
	
	private void publish(ProcessFileTask task, int process, int total) {
		task.publish(process, total);
	}
	
	private static void savePdf(File targetFile, StudentInfo studentInfo, TrueTypeFont font) throws IOException {
		if (font == null && studentInfo.getEntrySet().size() == 1) {
			Files.copy(studentInfo.getFileMap().firstEntry().getValue().toPath(), targetFile.toPath(), new CopyOption[0]);
			return;
		}
		PDDocument pdf = new PDDocument();
		List<PDDocument> toClose = new ArrayList<>(studentInfo.getEntrySet().size());
		if (font == null) {
			for(Entry<Integer, File> entry : studentInfo.getEntrySet()) {
				PDDocument sub = PDDocument.load(entry.getValue());
				for(PDPage page : sub.getPages()) {
					pdf.importPage(page);
				}
				toClose.add(sub);
			}
		} else {
			PDFont pdfFont = PDType0Font.load(pdf, font, true);
			for(Entry<Integer, File> entry : studentInfo.getEntrySet()) {
				PDDocument sub = PDDocument.load(entry.getValue());
				
				for(PDPage page : sub.getPages()) {
					PDPage operatePage = pdf.importPage(page);
					// calculate font size
					PDRectangle size = operatePage.getBBox();
					int fontSize = (int) (12 * Math.min(size.getWidth(), size.getHeight()) / PDRectangle.A4.getWidth());
					PDPageContentStream content = new PDPageContentStream(pdf, operatePage, AppendMode.APPEND, true);
					content.beginText();
					content.setFont(pdfFont, fontSize);
					content.setTextMatrix(Matrix.getTranslateInstance(2, operatePage.getBBox().getUpperRightY() - fontSize - 3));
					content.showText(entry.getValue().getName());
					content.endText();
					content.close();
				}
				toClose.add(sub);
			}	
		}
		pdf.save(targetFile);
		pdf.close();
		toClose.forEach(t -> {
			try {
				t.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void finish(MainWindow mainWindow, ProcessFileTask task) {
		mainWindow.println("-------- File Opeation Completed --------");
		Desktop desktop = Desktop.getDesktop();
		mainWindow.println(new StringBuilder("保存文件到: ").append(targetDir.getAbsolutePath()).toString());
		try {
			desktop.open(targetDir);
		} catch (IOException e) {
			mainWindow.printlnError("无法打开资源管理器, 请手动查看文件.", e);
			e.printStackTrace();
		}
		task.publish(100);
		mainWindow.println("\n\n\n\n");
	}


	public void preStart(MainWindow mainWindow, ProcessFileTask task) {
		mainWindow.println("-------- File Opeation Start --------");
		task.publish(0);
	}

}
