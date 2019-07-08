package GUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUILogged extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField nameDocumentField;
	private JTextField sectionField;
	private JTextField numOfSectionsField;
	private JButton btnLogout;
	private JButton btnCreate;
	private JButton btnEdit;
	private JButton btnShow;
	private JButton btnList;
	private JTextField inviteNameField;
	private JTextArea textArea;
	private JButton btnInvite;
	
	public GUILogged(String username) {
		setTitle(username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 663, 524);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel lblNameDocument = new JLabel("Nome Documento");
		lblNameDocument.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNameDocument.setBounds(10, 10, 145, 33);
		contentPane.add(lblNameDocument);
		
		JLabel lblSection = new JLabel("Sezione");
		lblSection.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblSection.setBounds(10, 53, 145, 33);
		contentPane.add(lblSection);

		JLabel lblNumOfSections = new JLabel("Numero Sezioni");
		lblNumOfSections.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNumOfSections.setBounds(10, 96, 145, 33);
		contentPane.add(lblNumOfSections);
		
		nameDocumentField = new JTextField();
		nameDocumentField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		nameDocumentField.setBounds(206, 12, 156, 29);
		contentPane.add(nameDocumentField);
		nameDocumentField.setColumns(10);
		
		sectionField = new JTextField();
		sectionField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sectionField.setBounds(206, 55, 156, 29);
		contentPane.add(sectionField);
		sectionField.setColumns(10);
		
		numOfSectionsField = new JTextField();
		numOfSectionsField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		numOfSectionsField.setBounds(206, 98, 156, 29);
		contentPane.add(numOfSectionsField);
		numOfSectionsField.setColumns(10);
		
		btnLogout = new JButton("LOGOUT");
		btnLogout.setForeground(new Color(0, 0, 0));
		btnLogout.setBackground(new Color(0, 51, 255));
		btnLogout.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnLogout.setBounds(509, 444, 130, 33);
		contentPane.add(btnLogout);
		
		btnCreate = new JButton("CREATE");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnCreate.setForeground(new Color(0, 0, 0));
		btnCreate.setBackground(new Color(0, 51, 255));
		btnCreate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnCreate.setBounds(509, 177, 130, 33);
		contentPane.add(btnCreate);
		
		btnEdit = new JButton("EDIT");
		btnEdit.setForeground(new Color(0, 0, 0));
		btnEdit.setBackground(new Color(0, 51, 255));
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnEdit.setBounds(509, 220, 130, 33);
		contentPane.add(btnEdit);
		
		btnShow = new JButton("SHOW");
		btnShow.setForeground(new Color(0, 0, 0));
		btnShow.setBackground(new Color(0, 51, 255));
		btnShow.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnShow.setBounds(509, 263, 130, 33);
		contentPane.add(btnShow);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 179, 487, 298);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		btnList = new JButton("LIST");
		btnList.setForeground(new Color(0, 0, 0));
		btnList.setBackground(new Color(0, 51, 255));
		btnList.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnList.setBounds(509, 306, 130, 33);
		contentPane.add(btnList);
		
		btnInvite = new JButton("INVITE");
		btnInvite.setForeground(Color.BLACK);
		btnInvite.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnInvite.setBackground(new Color(0, 51, 255));
		btnInvite.setBounds(509, 349, 130, 33);
		contentPane.add(btnInvite);
		
		JLabel lblUtenteInvitato = new JLabel("Utente Invitato");
		lblUtenteInvitato.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblUtenteInvitato.setBounds(10, 139, 145, 33);
		contentPane.add(lblUtenteInvitato);
		
		inviteNameField = new JTextField();
		inviteNameField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		inviteNameField.setColumns(10);
		inviteNameField.setBounds(206, 143, 156, 29);
		contentPane.add(inviteNameField);
		
	}
	
	public JButton getInvite() {
		return btnInvite;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JButton getLogout() {
		return btnLogout;
	}
	
	public JButton getShow() {
		return btnShow;
	}
	
	public JButton getCreate() {
		return btnCreate;
	}
	
	public JButton getEdit() {
		return btnEdit;
	}
	
	public JButton getList() {
		return btnList;
	}
	
	public String getNameDocument() {
		return nameDocumentField.getText();
	}
	
	public String getSection() {
		return sectionField.getText();
	}
	
	public String getInviteName() {
		return inviteNameField.getText();
	}
	public String getNumOfSections() {
		return numOfSectionsField.getText();
	}
}
