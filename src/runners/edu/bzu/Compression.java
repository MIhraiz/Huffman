package runners.edu.bzu;

import domain.edu.bzu.HuffmanNode;
import domain.edu.bzu.HuffmanTree;
import domain.edu.bzu.MyConverter;
import domain.edu.bzu.PriorityQueue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class Compression {

    byte[] treeBytes;
    int index = 0;

    public void compression() {


        String fileName = MainWindow.txtBrowse.getText();
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
            DefaultTableModel model = (DefaultTableModel) MainWindow.table.getModel();
            model.setRowCount(0);
            for (int i = 0; i < 256; i++) {
                model.addRow(new Object[]{huffmanTable[i].ch, huffmanTable[i].freq, huffmanTable[i].huffCode,
                        huffmanTable[i].huffCode.length()});

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
            JTextArea txtHeader = MainWindow.txtHeader;
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
                            e1.printStackTrace();
                        }
                    }
                }

                if (buffer.remaining() != 0) {
                    buffer.flip();
                    try {
                        fc.write(buffer);
                    } catch (IOException e1) {
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

}
