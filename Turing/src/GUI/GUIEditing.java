package GUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUIEditing extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnEndEdit;
	private JTextField textField;
	private JButton btnSend;
	private JTextArea textArea;

	public GUIEditing() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 663, 524);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnEndEdit = new JButton("END EDIT");
		btnEndEdit.setForeground(new Color(0, 0, 0));
		btnEndEdit.setBackground(new Color(0, 51, 255));
		btnEndEdit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnEndEdit.setBounds(509, 444, 130, 33);
		contentPane.add(btnEndEdit);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 629, 299);
		contentPane.add(textArea);
		
		textField = new JTextField();
		textField.setBounds(10, 319, 489, 115);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnSend = new JButton("SEND");
		btnSend.setForeground(new Color(0, 0, 0));
		btnSend.setBackground(new Color(0, 51, 255));
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSend.setBounds(509, 319, 130, 115);
		contentPane.add(btnSend);
	}

	public JButton getEndEdit() {
		return btnEndEdit;
	}

	public JButton getSend() {
		return btnSend;
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}
	public JTextField getTextField() {
		return textField;
	}
	
}
