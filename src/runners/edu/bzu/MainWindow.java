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
	private JTextField txtBrowse;
	private JTable table;
	private JTextField txtBrowseDE;
	private JTable table_1;

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

		JTextArea txtHeader = new JTextArea();
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
			byte[] treeBytes;
			int index = 0;

			public void actionPerformed(ActionEvent e) {
				String fileName = txtBrowse.getText();
				String extention = fileName.substring(fileName.lastIndexOf('.') + 1);
				byte extentionLength = (byte) extention.length();
				File file = new File(fileName);
				long[] freq = new long[256];
				byte[] fileBytes = null;
				try {
					// read all bytes in the file
					fileBytes = Files.readAllBytes(file.toPath());
					// count the chars
					for (int i = 0; i < fileBytes.length; i++) {
						if (fileBytes[i] < 0) {
							short tempNum = (short) (fileBytes[i] + 256);
							freq[tempNum]++;
						} else {
							freq[fileBytes[i]]++;
						}

					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(new JFrame(), "Wrong Entry, please choose a proper file",
							"Data Error", JOptionPane.ERROR_MESSAGE);
				}

				if (fileBytes != null) {
					PriorityQueue heap = new PriorityQueue();
					HuffmanNode[] huffmanTable = new HuffmanNode[256];

					// make Huffman table and
					// add the nodes to the heap
					short leafs = 0;
					for (short i = 0; i < 256; i++) {
						huffmanTable[i] = new HuffmanNode((char) i, freq[i]);
						if (freq[i] != 0) {
							heap.Enqueue(huffmanTable[i]);
							leafs++;
						}
					}

					int n = leafs; // Number of Huffman tree nodes
					// build Huffman tree
					for (short i = 1; i < leafs; i++) {
						HuffmanNode node = new HuffmanNode();
						HuffmanNode left = heap.Dequeue();
						HuffmanNode right = heap.Dequeue();
						node.setRight(right);
						node.setLeft(left);
						node.setFreq(left.getFreq() + right.getFreq());
						heap.Enqueue(node);
						n++;
					}
					// Assign code to each char
					HuffmanTree huffmanTree = new HuffmanTree(heap.Dequeue());
					heap = null;
					huffmanTree.buildCode();

					// Show table
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0);
					for (int i = 0; i < 256; i++) {
						model.addRow(new Object[] { huffmanTable[i].ch, huffmanTable[i].freq, huffmanTable[i].huffCode,
								huffmanTable[i].huffCode.length() });

					}

					int fileSize = fileBytes.length; // original file size in bytes

					// get the byte array that represent the tree
					short treeBytesSize = (short) ((leafs * 2) + (n - leafs));
					treeBytes = new byte[treeBytesSize];
					index = 0;
					treeBytes(huffmanTree.getRoot());

					// Create the buffer
					ByteBuffer buffer = ByteBuffer.allocate(104857600);

					// Display the header and printint it to the file;
					// Sign
					txtHeader.append("MAI\n");
					buffer.put((byte) 'M');
					buffer.put((byte) 'A');
					buffer.put((byte) 'I');

					// Extention and length
					txtHeader.append(extentionLength + " " + extention + "\n");
					buffer.put(extentionLength);
					for (int i = 0; i < extentionLength; i++) {
						buffer.put((byte) extention.charAt(i));
					}
					txtHeader.append(fileBytes.length + "\n");
					buffer.putInt(fileSize);

					// The tree and it's size
					buffer.putShort(treeBytesSize);
					txtHeader.append(treeBytesSize + "\n");
					String tree = "";
					int test = 0;
					for (int i = 0; i < treeBytesSize; i++) {
						if (treeBytes[i] == 1) {
							i++;
							if (treeBytes[i] < 0) {
								tree = tree + "1" + (char) (treeBytes[i] + 256) + "\n";
							} else {
								tree = tree + "1" + (char) treeBytes[i] + "\n";
							}
						} else {
							tree = tree + "0 ";
						}
						test = i;
					}
					txtHeader.append(tree);
					buffer.put(treeBytes);

					// creat the outfile and channel to print
					String userDir = System.getProperty("user.dir");
					String outFilePath = userDir + "\\out\\compressed\\"
							+ fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf('.')) + ".huff";
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(outFilePath);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (fos != null) {
						FileChannel fc = fos.getChannel();

						// Print the header and clear the buffer
						buffer.flip();
						try {
							fc.write(buffer);
							buffer.clear();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						String buffCode = "";
						MyConverter convert = new MyConverter();
						for (int i = 0; i < fileBytes.length; i++) {
							// Get the byte with Huffman Representation
							String charCode = null;
							if (fileBytes[i] < 0) {
								charCode = huffmanTable[fileBytes[i] + 256].huffCode;
							} else {
								charCode = huffmanTable[fileBytes[i]].huffCode;
							}

							for (int j = 0; j < charCode.length(); j++) {

								if (buffCode.length() == 8) {
									if (buffer.position() != buffer.limit()) {
										buffer.put(convert.binaryToByte(buffCode));
										buffCode = "";
									} else {
										buffer.flip();
										try {
											fc.write(buffer);
											buffer.clear();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										buffer.put(convert.binaryToByte(buffCode));
										buffCode = "";
									}

								}
								buffCode = buffCode + charCode.charAt(j);
							}
						}

						if (buffCode.length() != 0) {
							if (buffCode.length() != 8) {
								int bits = 8 - buffCode.length();
								for (int i = 0; i < bits; i++) {
									buffCode = buffCode + "0";
								}
							}

							if (buffer.position() != buffer.limit()) {
								buffer.put(convert.binaryToByte(buffCode));
								buffCode = "";
							} else {
								try {
									buffer.flip();
									fc.write(buffer);
									buffer.clear();
									buffer.put(convert.binaryToByte(buffCode));
									buffer.flip();
									fc.write(buffer);
									buffer.clear();
									buffCode = "";
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}

						if (buffer.remaining() != 0) {
							buffer.flip();
							try {
								fc.write(buffer);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							buffer.clear();
						}

					}
				}

			}

			private void treeBytes(HuffmanNode root) {
				if (root == null) {
					return;
				}

				if (root.getLeft() == null && root.getRight() == null) {
					treeBytes[index] = 1;
					index++;
					treeBytes[index] = (byte) root.getCh();
					index++;
				} else {
					treeBytes[index] = 0;
					index++;
					treeBytes(root.getLeft());
					treeBytes(root.getRight());
				}

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
			byte[] fileBytes = null;
			short treeSizeLastIndex = 0;
			int arrPosition = 0;

			public void actionPerformed(ActionEvent arg0) {
				// Getting the fila name
				String filePath = txtBrowseDE.getText();
				String fileName = null;
				String sign = "";
				try {
					// Read .huff file
					fileName = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
					File file = new File(filePath);

					// read all bytes in the file
					fileBytes = Files.readAllBytes(file.toPath());
					// Check the signature

					// read the signature
					for (int i = 0; i < 3; i++) {
						if (fileBytes[i] < 0) {
							char c = (char) (fileBytes[i] + 256);
							sign = sign + c;
						} else {
							char c = (char) fileBytes[i];
							sign = sign + c;
						}
					}

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new JFrame(), "Wrong Entry, please choose a proper file",
							"Data Error", JOptionPane.ERROR_MESSAGE);
				}

				if (sign.equals("MAI")) {
					int position = 3;

					// read the extension length
					byte extensionLength;
					position++;
					if (fileBytes[3] < 0) {
						extensionLength = (byte) (fileBytes[3] + 256);
					} else {
						extensionLength = fileBytes[3];
					}

					// The actual size index = 4 + extentionLength
					int actualSizeIndex = position + extensionLength;

					// read the extension
					String extension = "";
					for (int i = position; i < actualSizeIndex; i++) {
						if (fileBytes[i] < 0) {
							char c = (char) (fileBytes[i] + 256);
							extension = extension + c;
						} else {
							char c = (char) fileBytes[i];
							extension = extension + c;
						}
					}
					position = actualSizeIndex;

					// read the actual size
					String binarySize = "";
					for (int i = actualSizeIndex; i < actualSizeIndex + 4; i++) {
						binarySize = binarySize + MyConverter.byteToBinary(fileBytes[i]);
					}
					position += 4;
					// getting the actual size
					int actualSize = Integer.parseInt(binarySize, 2);

					binarySize = "";
					for (int i = actualSizeIndex + 4; i < actualSizeIndex + 6; i++) {
						binarySize = binarySize + MyConverter.byteToBinary(fileBytes[i]);
					}
					short treeSize = Short.parseShort(binarySize, 2);
					position += 2;
					// Where the Tree begins

					// Rebuildthe tree from the header
					treeSizeLastIndex = (short) (position + treeSize);
					arrPosition = position - 1;
					HuffmanNode root = readTree();
					HuffmanTree tree = new HuffmanTree(root);
					tree.buildCode();

					position = position + treeSize; // Point position to data
					// Display the table
					String[] codes = tree.getCodes();
					tree.setCodes(null);
					DefaultTableModel model = (DefaultTableModel) table_1.getModel();
					model.setRowCount(0);
					for (int i = 0; i < 256; i++) {
						model.addRow(new Object[] { (char) i, codes[i] });

					}

					// Create the buffer
					ByteBuffer buffer = ByteBuffer.allocate(104857600);

					// creat the outfile and channel to print
					String userDir = System.getProperty("user.dir");
					String outFilePath = userDir + "\\out\\decompressed\\" + fileName + "." + extension;
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(outFilePath);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
					if (fos != null) {
						FileChannel fc = fos.getChannel();

						HuffmanNode tempNode = tree.getRoot();
						String tempHuffCode = "";
						String temp = "";
						for (int i = 0; i < actualSize && position < fileBytes.length;position++) {
							tempHuffCode = MyConverter.byteToBinary(fileBytes[position]);
							for (int j = 0; j < tempHuffCode.length(); j++) {
								
								char c = tempHuffCode.charAt(j);
								temp = temp+c;
								byte bt;
								for(int k = 0; k < 256; k++) {
									if(temp.equals(codes[k])) {
										bt = (byte) k;
										if (buffer.position() == buffer.limit()) {
											buffer.flip();
											try {
												fc.write(buffer);
												buffer.clear();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										if(i < actualSize) {
											buffer.put(bt);
											i++;
										}
										temp = "";
										break;
									}
								}
								
								

								/*if (c == '1') {
									tempNode = tempNode.getRight();
								} else {
									tempNode = tempNode.getLeft();
								}

								if (tempNode != null) {
									if (tempNode.getLeft() == null && tempNode.getRight() == null) {
										if (buffer.position() == buffer.limit()) {
											buffer.flip();
											try {
												fc.write(buffer);
												buffer.clear();
											} catch (IOException e) {
												 TODO Auto-generated catch block
												e.printStackTrace();
											}
										}

										System.out.println(tempNode.getCh() + "     " +(byte) tempNode.getCh()+ "      " + i + "      "+ tempHuffCode+ "        " + j);
										buffer.put((byte) tempNode.getCh());

										
										tempHuffCode = tempHuffCode.substring(j+1);
										tempNode = tree.getRoot();
									} 
								}*/
							}

						}
						
						if(buffer.remaining() != 0) {
							buffer.flip();
							try {
								fc.write(buffer);
								buffer.clear();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						/*
						 * // Print the header and clear the buffer buffer.flip(); try {
						 * fc.write(buffer); buffer.clear(); } catch (IOException e1) { // TODO
						 * Auto-generated catch block e1.printStackTrace(); }
						 */

					}

				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Wrong Entry, Signature don't match", "Signature Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}

			public HuffmanNode readTree() {
				arrPosition++;
				HuffmanNode root = null;
				if (arrPosition >= treeSizeLastIndex - 1) {
					return null;
				}
				if (fileBytes[arrPosition] == 1) {
					if (fileBytes[arrPosition] < 0) {
						root = new HuffmanNode((char) (fileBytes[arrPosition + 1] + 256));
					} else {
						root = new HuffmanNode((char) fileBytes[arrPosition + 1]);
					}
					arrPosition++;
				} else {
					root = new HuffmanNode();
					root.left = readTree(); // 3 5
					root.right = readTree(); // 4 5
				}

				return root;
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
