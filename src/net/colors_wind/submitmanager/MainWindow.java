package net.colors_wind.submitmanager;

import java.awt.BorderLayout;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class MainWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 710005323306431087L;
	private JPanel contentPane;
	protected JTextField inputDir;
	protected JTextField inputXls;
	private JProgressBar progressBar;
	private JButton buttonStart;
	private JButton buttonStop;
	private JScrollPane scrollBar;
	private JTextArea outputArea;

	


	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("作业管理器");
		setType(Type.POPUP);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 580, 433);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		progressBar = new JProgressBar();

		JButton buttonAdvanceOption = new JButton("高级选项");
		buttonAdvanceOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.outputOptions.setVisible(true);
			}
		});

		buttonStop = new JButton("停止");
		buttonStop.setEnabled(false);

		buttonStart = new JButton("开始");
		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.OPTIONS.loadFromWindow(Main.mainWindow, Main.outputOptions);
				new ProcessFileTask(MainWindow.this).start();
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup().addContainerGap()
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(buttonAdvanceOption, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(buttonStop, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(buttonStart, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_1
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(buttonStart, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(buttonStop, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
								.addComponent(buttonAdvanceOption, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
				.addGap(7)));
		panel_1.setLayout(gl_panel_1);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel labelInputXls = new JLabel("输入表格");

		JButton btnNewButton_1_1 = new JButton("浏览");
		btnNewButton_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choiceXls().ifPresent(inputXls::setText);
			}
		});

		JLabel labelInputFolder = new JLabel("输入数据");

		inputDir = new JTextField("");
		inputDir.setColumns(10);

		inputXls = new JTextField("");
		inputXls.setColumns(10);

		JButton btnNewButton_1 = new JButton("浏览");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choiceFolder().ifPresent(inputDir::setText);
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING).addGap(0, 533, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup().addContainerGap()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(labelInputFolder)
										.addComponent(labelInputXls, GroupLayout.PREFERRED_SIZE, 48,
												GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panel.createSequentialGroup()
												.addComponent(inputDir, GroupLayout.DEFAULT_SIZE, 398,
														Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnNewButton_1))
										.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
												.addComponent(inputXls, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnNewButton_1_1, GroupLayout.PREFERRED_SIZE, 57,
														GroupLayout.PREFERRED_SIZE)))
								.addGap(4)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGap(0, 71, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup().addContainerGap()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(inputXls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(labelInputXls).addComponent(btnNewButton_1_1))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(labelInputFolder)
								.addComponent(inputDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton_1))
						.addContainerGap(11, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);

		outputArea = new JTextArea();
		contentPane.add(outputArea, BorderLayout.CENTER);
		scrollBar = new JScrollPane(outputArea);
		contentPane.add(scrollBar, BorderLayout.CENTER);
	}

	public Optional<String> choiceXls() {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("表格文件(*.xls, *.xlsx)", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int result = chooser.showOpenDialog(this);
		switch (result) {
		case JFileChooser.CANCEL_OPTION:
			return Optional.empty();
		default:
			return Optional.of(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public Optional<String> choiceFolder() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(this);
		switch (result) {
		case JFileChooser.CANCEL_OPTION:
			return Optional.empty();
		default:
			return Optional.of(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void println(String msg) {
		outputArea.append(msg);
		outputArea.append("\n");
	}

	public void printlnSafty(String msg) {
		SwingUtilities.invokeLater(() -> println(msg));
	}
	
	public void printlnError(String msg) {
		printlnSafty(msg);
	}

	public void handleTaskStart() {
		this.buttonStart.setEnabled(false);
		this.buttonStop.setEnabled(true);
	}

	public void handleTaskStop() {
		this.buttonStart.setEnabled(true);
		this.buttonStop.setEnabled(false);
	}

	public void printlnError(Exception e) {
		printlnError(e.toString());
	}
	
	public void printlnError(CharSequence sequence, Exception e) {
		printlnError(sequence + ": " + e.toString());
	}

}
