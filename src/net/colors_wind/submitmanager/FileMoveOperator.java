package net.colors_wind.submitmanager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

public class FileMoveOperator {
	private final File dataDir;
	private final File target;
	private final AtomicInteger countSuccess = new AtomicInteger();

	public FileMoveOperator(File dataDir) {
		this.dataDir = dataDir;
		this.target = new File(dataDir, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
		target.mkdirs();
	}

	public void start(MainWindow mainWindow, FormMap form) {
		File[] files = dataDir.listFiles((dir, name) -> {
			return name.toLowerCase().endsWith(".pdf");
		});
		mainWindow.printlnSafty(new StringBuilder("共找到 ").append(files.length).append(" 个文件, 重命名工作即将开始.").toString());
		Arrays.stream(files).parallel().forEach(file -> {
			try {
				StudentInfo studentInfo = form.getStudentInfo(file.getName());
				File re = new File(target, studentInfo.getFileName());
				savePdf(re, studentInfo);
				mainWindow.printlnSafty(new StringBuilder("将 ").append(file.getName()).append(" 重命名为 ")
						.append(re.getName()).toString());
				countSuccess.incrementAndGet();
			} catch (IllegalArgumentException | IOException e) {
				mainWindow.printlnError("拷贝文件时发送错误: ", e);
				e.printStackTrace();
			} 
		});
		mainWindow.printlnSafty(new StringBuilder("成功重命名 ").append(countSuccess.get()).append(" 个文件, 失败 ")
				.append(files.length - countSuccess.get()).append(" 个文件").toString());
	}
	
	private static void savePdf(File file, StudentInfo studentInfo) throws IOException {
		PDDocument pdf = new PDDocument();
		PDFont formFont = PDType0Font.load(pdf, new FileInputStream(new File(".//SourceHanSansSC-Medium.ttf")), false);
		List<PDDocument> toClose = new ArrayList<>(studentInfo.getEntrySet().size());
		for(Entry<Integer, File> entry : studentInfo.getEntrySet()) {
			PDDocument sub = PDDocument.load(entry.getValue());
			for(PDPage page : sub.getPages()) {
				PDPage operatePage = pdf.importPage(page);
				if (Main.OPTIONS.isAddRawData()) {
					PDPageContentStream content = new PDPageContentStream(pdf, operatePage, AppendMode.APPEND, true);
					content.beginText();
					content.setFont(formFont, 12);
					content.setTextMatrix(Matrix.getTranslateInstance(2, operatePage.getBBox().getUpperRightY() - 15));
					content.showText(entry.getValue().getName());
					content.endText();
					content.close();
				}
			}
			toClose.add(sub);
		}
		pdf.save(file);
		pdf.close();
		toClose.forEach(t -> {
			try {
				t.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void finish(MainWindow mainWindow) {
		Desktop desktop = Desktop.getDesktop();
		mainWindow.printlnSafty(new StringBuilder("保存文件到: ").append(target.getAbsolutePath()).toString());
		try {
			desktop.open(target);
		} catch (IOException e) {
			mainWindow.printlnError("无法打开资源管理器, 请手动查看文件.", e);
			e.printStackTrace();
		}
	}

}
