package runners.edu.bzu;

import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import domain.edu.bzu.HuffmanNode;
import domain.edu.bzu.HuffmanTree;
import domain.edu.bzu.MyConverter;
import domain.edu.bzu.PriorityQueue;
import domain.edu.bzu.ReverseFileNameExtensionFilter;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.table.DefaultTableModel;

public class MainWindow {

	private JFrame frame;
	static JTextField txtBrowse;
	static JTable table;
	static JTextField txtBrowseDE;
	static JTable table_1;
	static JTextArea txtHeader = new JTextArea();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 593, 651);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 557, 590);
		frame.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Compression", null, panel, null);
		panel.setLayout(null);

		txtBrowse = new JTextField();
		txtBrowse.setBounds(122, 11, 430, 27);
		panel.add(txtBrowse);
		txtBrowse.setColumns(10);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(10, 10, 102, 28);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				FileFilter filter = new ReverseFileNameExtensionFilter("Execlude Huffman", "huff");
				JFileChooser j = new JFileChooser(System.getProperty("user.dir"));
				j.setFileFilter(filter);
				int returnVal = j.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtBrowse.setText(j.getSelectedFile().getAbsolutePath());
				}

			}
		});
		panel.add(btnBrowse);

		JButton btnOut = new JButton("Open Output Folder");
		btnOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.getDesktop();
				String s = System.getProperty("user.dir") + "\\out\\compressed";
				File file = new File(s);
				try {
					desktop.open(file);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnOut.setBounds(392, 44, 160, 28);
		panel.add(btnOut);

		JLabel lblNewLabel = new JLabel("Header");
		lblNewLabel.setFont(new Font("Bungee Inline", Font.PLAIN, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(222, 66, 102, 28);
		panel.add(lblNewLabel);

		JLabel lblTable = new JLabel("Table");
		lblTable.setHorizontalAlignment(SwingConstants.CENTER);
		lblTable.setFont(new Font("Bungee Inline", Font.PLAIN, 12));
		lblTable.setBounds(222, 253, 102, 28);
		panel.add(lblTable);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 97, 542, 156);
		panel.add(scrollPane);


		scrollPane.setViewportView(txtHeader);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 284, 542, 267);
		panel.add(scrollPane_1);

		table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, },
				new String[] { "Character", "Frequency", "Huffman Code", "Length" }) {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		scrollPane_1.setViewportView(table);

		JButton btnRun_1 = new JButton("Run");
		btnRun_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				runners.edu.bzu.Compression c = new Compression();
				c.compression();

			}

		});
		btnRun_1.setBounds(10, 44, 102, 28);
		panel.add(btnRun_1);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Decompression", null, panel_1, null);
		panel_1.setLayout(null);

		txtBrowseDE = new JTextField();
		txtBrowseDE.setColumns(10);
		txtBrowseDE.setBounds(122, 29, 430, 27);
		panel_1.add(txtBrowseDE);

		JButton btnBrowseDE = new JButton("Browse");
		btnBrowseDE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileFilter filter = new FileNameExtensionFilter("Huffman File", "huff");
				JFileChooser j = new JFileChooser(System.getProperty("user.dir"));
				j.setFileFilter(filter);
				int returnVal = j.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtBrowseDE.setText(j.getSelectedFile().getAbsolutePath());
				}

			}
		});
		btnBrowseDE.setBounds(10, 28, 102, 28);
		panel_1.add(btnBrowseDE);

		JButton btnOutDE = new JButton("Open Output Folder");
		btnOutDE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				String s = System.getProperty("user.dir") + "\\out\\decompressed";
				File file = new File(s);
				try {
					desktop.open(file);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnOutDE.setBounds(314, 85, 160, 28);
		panel_1.add(btnOutDE);

		JButton btnRunDE = new JButton("Run");
		btnRunDE.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				Decomprission DE = new Decomprission();
				DE.decomprission();


			}


		});
		btnRunDE.setBounds(75, 85, 160, 28);
		panel_1.add(btnRunDE);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 167, 542, 373);
		panel_1.add(scrollPane_2);

		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(new Object[][] { { null, null }, },
				new String[] { "Character", "Huffman Code" }) {
			Class[] columnTypes = new Class[] { String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		scrollPane_2.setViewportView(table_1);

		JLabel lblTable_1 = new JLabel("Table");
		lblTable_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblTable_1.setFont(new Font("Bungee Inline", Font.PLAIN, 12));
		lblTable_1.setBounds(228, 128, 102, 28);
		panel_1.add(lblTable_1);

		JLabel lblMohammadIhraiz = new JLabel("Mohammad Ihraiz - 1182599");
		lblMohammadIhraiz.setBounds(377, 599, 200, 13);
		frame.getContentPane().add(lblMohammadIhraiz);
		lblMohammadIhraiz.setHorizontalAlignment(SwingConstants.CENTER);
		lblMohammadIhraiz.setFont(new Font("Bungee Inline", Font.PLAIN, 10));
		lblMohammadIhraiz.setBackground(SystemColor.activeCaptionBorder);

	}

}
