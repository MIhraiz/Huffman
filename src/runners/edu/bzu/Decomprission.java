package runners.edu.bzu;

import domain.edu.bzu.HuffmanNode;
import domain.edu.bzu.HuffmanTree;
import domain.edu.bzu.MyConverter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class Decomprission {

    byte[] fileBytes = null;
    short treeSizeLastIndex = 0;
    int arrPosition = 0;

    public void decomprission(){


        // Getting the fila name
        String filePath = MainWindow.txtBrowseDE.getText();
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
            DefaultTableModel model = (DefaultTableModel) MainWindow.table_1.getModel();
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
                for (; position < fileBytes.length;position++) {
                    tempHuffCode = MyConverter.byteToBinary(fileBytes[position]);
                    for (int j = 0, i=0; i< actualSize && j < tempHuffCode.length(); j++) {

                        char c = tempHuffCode.charAt(j);
                        temp = temp + c;
                        byte bt;

                        if (c == '1') {
                            tempNode = tempNode.getRight();
                        } else {
                            tempNode = tempNode.getLeft();
                        }

                        if (tempNode.getLeft() == null && tempNode.getRight() == null) {
                            if (buffer.position() == buffer.limit()) {
                                buffer.flip();
                                try {
                                    fc.write(buffer);
                                    buffer.clear();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            buffer.put((byte) tempNode.getCh());
                            i++;
                            temp = "";
                            tempNode = tree.getRoot();
                        }

                    }
                }

                if(buffer.remaining() != 0) {
                    buffer.flip();
                    try {
                        fc.write(buffer);
                        buffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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

}
