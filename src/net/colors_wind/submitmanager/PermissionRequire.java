package net.colors_wind.submitmanager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

import java.io.File;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PermissionRequire extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2500421184413532717L;
	public static final String MESSAGE = "作业管理器没有对 %file_name% 的读写权限，无法完成操作。要解决这个问题，请赋予当前用户访问该文件(夹)的权限。\r\n\r\n\r\n点击 “确定” 作业管理器将尝试获取 UAC 权限帮您完成权限配置，您只需要在稍后的对话框中同意请求；\r\n\r\n点击 “取消” 您需要手动赋予权限，并再次尝试处理文件。";
	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	private JTextPane txtArea;
	private JButton okButton;

	
	public void show(String fileName) {
		Main.centre(this);
		txtArea.setText(MESSAGE.replace("%file_name%", fileName));
		this.setVisible(true);
		this.okButton.setEnabled(UACTool.isOSCompatible());
	}

	/**
	 * Create the dialog.
	 */
	public PermissionRequire() {
		setTitle("请求权限");
		setType(Type.POPUP);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblNewLabel = new JLabel("Oops! Permission denied.");
			lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 16));
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		{
			txtArea = new JTextPane();
			txtArea.setFont(new Font("宋体", Font.PLAIN, 14));
			txtArea.setBackground(UIManager.getColor("Menu.background"));
			txtArea.setForeground(Color.BLACK);
			txtArea.setEditable(false);
			txtArea.setText("null");
		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtArea, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
					.addGap(10))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtArea, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("确定");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Main.OPTIONS.updateFileWrap(Main.mainWindow);
						File jarFile = new File(Main.getPath());
						if (!jarFile.exists()) {
							appendText("找不到程序路径, 发起UAC权限请求失败.");
							return;
						}
						boolean success = UACTool.runJarAsAdmin(jarFile, "givepermission", false);
						if (success) {
							PermissionRequire.this.setVisible(false);
						} else {
							appendText("尝试发起UAC权限请求失败.");
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("取消");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PermissionRequire.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	
	public void appendText(String text) {
		txtArea.setText(new StringBuilder(txtArea.getText()).append(text).toString());
	}
	
	public static boolean checkPermission(File file) {
		if (file.canRead() && file.canWrite()) {
			return true;
		}
		Main.permissionRequire.show(file.getAbsolutePath());
		return false;
	}
	

}
