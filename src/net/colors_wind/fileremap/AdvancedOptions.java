package net.colors_wind.fileremap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Font;

public class AdvancedOptions extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8108291943205318602L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AdvancedOptions dialog = new AdvancedOptions();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AdvancedOptions() {
		setTitle("高级选项");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setType(Type.POPUP);
		setBounds(100, 100, 410, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("180px:grow"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("180px"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		{
			JCheckBox chckbxNewCheckBox_1 = new JCheckBox("转换png");
			chckbxNewCheckBox_1.setFont(new Font("宋体", Font.PLAIN, 14));
			chckbxNewCheckBox_1.setSelected(true);
			contentPanel.add(chckbxNewCheckBox_1, "1, 2, left, top");
		}
		{
			JCheckBox chckbxNewCheckBox_6 = new JCheckBox("添加文件原始信息");
			chckbxNewCheckBox_6.setFont(new Font("宋体", Font.PLAIN, 14));
			contentPanel.add(chckbxNewCheckBox_6, "3, 2");
		}
		{
			JCheckBox chckbxNewCheckBox = new JCheckBox("转换jpg/jpeg");
			chckbxNewCheckBox.setFont(new Font("宋体", Font.PLAIN, 14));
			chckbxNewCheckBox.setSelected(true);
			contentPanel.add(chckbxNewCheckBox, "1, 4, left, top");
		}
		{
			JCheckBox chckbxNewCheckBox_5 = new JCheckBox("多个文件");
			chckbxNewCheckBox_5.setFont(new Font("宋体", Font.PLAIN, 14));
			contentPanel.add(chckbxNewCheckBox_5, "3, 4");
		}
		{
			JCheckBox chckbxNewCheckBox_2 = new JCheckBox("转换doc/docx");
			chckbxNewCheckBox_2.setSelected(true);
			chckbxNewCheckBox_2.setFont(new Font("宋体", Font.PLAIN, 14));
			contentPanel.add(chckbxNewCheckBox_2, "1, 6");
		}
		{
			JCheckBox chckbxNewCheckBox_3 = new JCheckBox("转换xls/xlsx");
			chckbxNewCheckBox_3.setSelected(true);
			chckbxNewCheckBox_3.setFont(new Font("宋体", Font.PLAIN, 14));
			contentPanel.add(chckbxNewCheckBox_3, "1, 8");
		}
		{
			JCheckBox chckbxNewCheckBox_4 = new JCheckBox("转换ppt/pptx");
			chckbxNewCheckBox_4.setSelected(true);
			chckbxNewCheckBox_4.setFont(new Font("宋体", Font.PLAIN, 14));
			contentPanel.add(chckbxNewCheckBox_4, "1, 10");
		}
		{
			JCheckBox chckbxNewCheckBox_7 = new JCheckBox("转换txt");
			chckbxNewCheckBox_7.setFont(new Font("宋体", Font.PLAIN, 14));
			chckbxNewCheckBox_7.setSelected(true);
			contentPanel.add(chckbxNewCheckBox_7, "1, 12");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("确定");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AdvancedOptions.this.setVisible(false);
					}
				});
				okButton.setActionCommand("");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("取消");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AdvancedOptions.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("取消");
				buttonPane.add(cancelButton);
			}
		}
	}

}
