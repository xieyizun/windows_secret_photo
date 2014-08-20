package liyu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
public class LiyuImages extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTree tree;
	private JTextField uploadPath;
	private JLabel uploadLabel;
	private JButton uploadButton;
	private JPanel uploadPane;
	private Image selectedImage;
	private JButton saveimage;
	private File allDirs;
	private File[] dirs;
	private JButton last;
	private JButton next;
	private JTextField imageName;
	private JLabel saveLabel;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode childDir;
	private DefaultMutableTreeNode newNode;
	private String newDirName;
	private DefaultMutableTreeNode selectionNode;
	private DefaultMutableTreeNode lastNode;
	private DefaultMutableTreeNode nextNode;
	private JLabel currentImage;
	private JTextField currentImageName;
	private File currentImageFile;
	private JButton deleteNodeButton;
	private File tempImages;
	//draw image in the file chooser
	private class PaintPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Image image;
		public PaintPanel() {
			super();
			setOpaque(false);
			setLayout(null);
		}
		public void setImage(Image image) {
			this.image = image;
		}
		@Override
		protected void paintComponent(Graphics g) {
			if (image != null) {
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			}
			super.paintComponent(g);
		}
	}
	private PaintPanel paint;
	private PaintPanel paintSelectedImage;
	
	public LiyuImages() {
		setTitle("LiyuImages");
		this.setResizable(false);
		setBounds(350, 130, 750, 540);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		contentPane = new JPanel() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("./res/background4.jpg");
				Image image = icon.getImage();
				g.drawImage(image,0,0,null);
			}
		};
		contentPane.setBorder(new EmptyBorder(10,15,15,15));
		contentPane.setLayout(new BorderLayout(5,5));
		setContentPane(contentPane);
		JPanel bottomPane = new JPanel();
		//bottomPane.setLayout(new GridLayout(1,6,6,2));
		bottomPane.setLayout(new BorderLayout(5,5));
		bottomPane.setOpaque(false);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		//last and next Button
		last = new JButton("last");
		next = new JButton("next");
		last.setEnabled(false);
		last.setPreferredSize(new Dimension(100,30));
		last.setFont(new Font("", Font.ITALIC,15));
		last.setForeground(Color.BLACK);
		next.setEnabled(false);
		next.setPreferredSize(new Dimension(100,30));
		next.setFont(new Font("", Font.ITALIC,15));
		next.setForeground(Color.BLACK);
		last.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getLast();
			}
		});
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getNext();
			}
		});
		//save button
		deleteNodeButton = new JButton("Delete");
		deleteNodeButton.setEnabled(false);
		deleteNodeButton.setFont(new Font("", Font.ITALIC,15));
		deleteNodeButton.setPreferredSize(new Dimension(100,5));
		deleteNodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nodeName = selectionNode.toString();
				if (selectionNode.getAllowsChildren()) {
					File hasImages = new File("./images/"+nodeName);
					if (hasImages.listFiles().length==0) {
						int result = JOptionPane.showConfirmDialog(null, "This directory is empty. Are you sure to delete it?", null, 0);
						if (result == 0) {
							hasImages.delete();
							DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
							model.removeNodeFromParent(selectionNode);
							tree.repaint();
							deleteNodeButton.setEnabled(false);
							JOptionPane.showMessageDialog(null, "Delete directory "+nodeName+" successfully!");
						} else {
							return;
						}						
					} else {
						int result = JOptionPane.showConfirmDialog(null, "There are some images in this directory. Are you sure to delete it?",
								null, 0);
						if (result == 0) {
							for (File file : hasImages.listFiles()) {
								if (currentImageFile != null && currentImageFile.equals(file)) {
									currentImageFile = null;
									selectedImage = null;
									paintSelectedImage.setImage(selectedImage);
									paintSelectedImage.repaint();
									last.setEnabled(false);
									next.setEnabled(false);
									last = null;
									next = null;
								}
								file.delete();
							}
							hasImages.delete();
							DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
							model.removeNodeFromParent(selectionNode);
							tree.repaint();
							deleteNodeButton.setEnabled(false);
							JOptionPane.showMessageDialog(null, "Delete directory "+nodeName+" successfully!");
						} else {
							return;
						}
					}
				} else {
					String parentNode = selectionNode.getParent().toString();
					int result=JOptionPane.showConfirmDialog(null, "Are you sure to delete this image?", null, 0);
					if (result == 0 && currentImageName != null) {
						File image = new File("./images/"+parentNode+"/"+
								currentImageName.getText().substring(0,currentImageName.getText().lastIndexOf('.'))+".bin");
						image.delete();
						
						DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
						if (nodeName.equals(currentImageName.getText())){
							currentImageFile = null;
							selectedImage = null;
							paintSelectedImage.setImage(selectedImage);
							paintSelectedImage.repaint();
							model.removeNodeFromParent(selectionNode);
							tree.repaint();
							currentImageName.setText("");
							JOptionPane.showMessageDialog(null, "Delete image "+nodeName+" successfully!");
							deleteNodeButton.setEnabled(false);
						} else {						
							try {
								currentImageFile = new File("./images/"+parentNode+"/"+
										nodeName.substring(0, nodeName.lastIndexOf('.'))+".bin");
								//get Image from binary file, write image to temp directory temporary
								String outFilePath = "./tmp/"+nodeName;
								FileInputStream fs = new FileInputStream(currentImageFile);
								byte[] binImage = new byte[(int)currentImageFile.length()];
								fs.read(binImage, 0, (int)currentImageFile.length());
								FileOutputStream os = new FileOutputStream(outFilePath);
								os.write(binImage);
								fs.close();
								os.close();
								//get Image from temp and display it
								File tempImageFile = new File(outFilePath);
								selectedImage = getToolkit().getImage(tempImageFile.toURI().toURL());
								paintSelectedImage.setImage(selectedImage);
								paintSelectedImage.repaint();
							} catch (Exception e1) {
								e1.getMessage();
							}
							@SuppressWarnings("unchecked")
							Enumeration<DefaultMutableTreeNode> removedInNodes = selectionNode.getParent().children();
							while (removedInNodes.hasMoreElements()) {
								DefaultMutableTreeNode removedNode = removedInNodes.nextElement();
								if (removedNode.toString().equals(currentImageName.getText())) {
									model.removeNodeFromParent(removedNode);
									break;
								}
							}
							tree.repaint();
							currentImageName.setText(nodeName);
							JOptionPane.showMessageDialog(null, "Delete image "+currentImageName.getText()+" successfully!");	
						}
					} else {
						return;
					}
				}
			}
		});
		JPanel bottomButtonsPane = new JPanel();
		bottomButtonsPane.setOpaque(false);
		saveimage = new JButton("Save");
		saveimage.setPreferredSize(new Dimension(100,30));
		saveimage.setFont(new Font("", Font.ITALIC,15));
		saveimage.setForeground(Color.BLACK);
		saveimage.setEnabled(false);
		saveimage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImageToTree(e);		
			}
		});
		imageName = new JTextField();
		imageName.setFont(new Font("", Font.ITALIC, 15));
		imageName.setColumns(10);
		imageName.setText("");
		saveLabel = new JLabel("     Save Name:");
		saveLabel.setFont(new Font("", Font.ITALIC,17));
		saveLabel.setForeground(Color.BLACK);
		bottomPane.add(deleteNodeButton, BorderLayout.WEST);
		
		bottomButtonsPane.add(saveLabel);
		bottomButtonsPane.add(imageName);
		bottomButtonsPane.add(saveimage);
		bottomButtonsPane.add(last);
		bottomButtonsPane.add(next);
		bottomPane.add(bottomButtonsPane, BorderLayout.CENTER);
		//menuBar
		JMenuBar menuBar = new JMenuBar();
		//file operator
		JMenu fileOperation = new JMenu("File");
		JMenuItem save = new JMenuItem("Save As");
		save.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JFileChooser saveChooser = new JFileChooser();
				saveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				saveChooser.setMultiSelectionEnabled(false);			
				if (selectedImage == null) {				
					JOptionPane.showMessageDialog(null, "Please open an image!", null, JOptionPane.WARNING_MESSAGE);
					return;
				} 
				if ("".equals(currentImageName.getText())) {
					JOptionPane.showMessageDialog(null, "The image hasn't upload!", null, JOptionPane.WARNING_MESSAGE);
					return;
				}
				int result = saveChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File saveDir = saveChooser.getSelectedFile();
					String savePath = saveDir.getAbsolutePath();
					int w = selectedImage.getWidth(null);
					int h = selectedImage.getHeight(null);
					BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
					Graphics g = bi.getGraphics();
					g.drawImage(selectedImage, 0, 0, null);
					//change currentImageFile to selectionNode
					String imageType = currentImageName.getText().toString().substring(currentImageName.getText().toString().lastIndexOf('.')+1,
							currentImageName.getText().toString().length());
					try {
						ImageIO.write(bi, imageType, new File(savePath+"/"+currentImageName.getText().toString()));	
						JOptionPane.showMessageDialog(null, currentImageName.getText().toString()+" has been saved to "+savePath+" successfully!", 
								null, JOptionPane.WARNING_MESSAGE);
					} catch(Exception e1) {
						e1.getMessage();
					}
					
				}
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Exit", 0);
				if (result == 0)
					tempImages = new File("./tmp");
					for (File file : tempImages.listFiles()) {
						file.delete();
					}
					System.exit(0);
			}
		});
		fileOperation.addSeparator();
		fileOperation.add(save);
		fileOperation.addSeparator();
		fileOperation.add(exit);
		
		//help
		JMenu help = new JMenu("help");
		JMenuItem usage = new JMenuItem("usage");
		help.add(usage);
		//about
		JMenu about = new JMenu("about");
		JMenuItem aboutUs = new JMenuItem("contact");
		aboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "If there are any problems arised when you use this software,"
						+"\n"+"please contact us by 1824762899@qq.com by email. Thank you!",
						"contact", JOptionPane.PLAIN_MESSAGE);
			}
		});
		JMenuItem versions = new JMenuItem("version");
		about.add(aboutUs);
		about.addSeparator();
		about.add(versions);
		menuBar.add(fileOperation);
		menuBar.add(help);
		menuBar.add(about);
		setJMenuBar(menuBar);
		
		//scroll pane	
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(3, 3, 3, 3));
		//root nodes
		final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Images");
		allDirs = new File("./images");
		dirs = allDirs.listFiles();
		for (File dir : dirs) {
			newNode = new DefaultMutableTreeNode(dir.getName());
			for (File image : dir.listFiles()) {
				newNode.add(new DefaultMutableTreeNode(image.getName().substring(0, image.getName().lastIndexOf('.'))+".jpg", false));
			}
			rootNode.add(newNode);			
		}
		treeModel = new DefaultTreeModel(rootNode, true);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollPane.setViewportView(tree);
		getContentPane().add(scrollPane, BorderLayout.WEST);
		//operate nodes
		final JPopupMenu operateNodes = new JPopupMenu();
		JMenuItem item1 = new JMenuItem("new dir");
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				//newDirName = "";
				newDirName = JOptionPane.showInputDialog("please input the directory's name: ");
				if (newDirName != null && !"".equals(newDirName)) {
					childDir = new DefaultMutableTreeNode(newDirName, true);
					model.insertNodeInto(childDir, rootNode, rootNode.getChildCount());
					rootNode.add(childDir);
					File mkDir = new File("./images", newDirName);
					mkDir.mkdir();
				}
			}
		});
		operateNodes.add(item1);
		operateNodes.setInvoker(tree);
		tree.addMouseListener(new MouseAdapter() {						
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					if (selectedNode.isRoot()) {
						operateNodes.show(operateNodes.getInvoker(),operateNodes.getInvoker().getX()+30,
							operateNodes.getInvoker().getY()+20);
					}
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				JTree tree = (JTree)e.getSource();
				selectionNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if (selectionNode != null) {
					if (!selectionNode.isRoot()) {
						deleteNodeButton.setEnabled(true);
					} else {
						deleteNodeButton.setEnabled(false);
					}
					String nodeName = selectionNode.toString();
					if (selectionNode.getAllowsChildren() == false && selectionNode.isLeaf()) {
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selectionNode.getParent();
						try {
							currentImageFile = new File("./images/"+parent.toString()+"/"+nodeName.substring(0,nodeName.lastIndexOf('.'))+".bin");						
							//get Image from binary file, write image to temp directory temporary
							String outFilePath = "./tmp/"+nodeName;
							FileInputStream fs = new FileInputStream(currentImageFile);
							byte[] binImage = new byte[(int)currentImageFile.length()];
							fs.read(binImage, 0, (int)currentImageFile.length());
							FileOutputStream os = new FileOutputStream(outFilePath);
							os.write(binImage);
							fs.close();
							os.close();
							//get Image from temp and display it
							File tempImageFile = new File(outFilePath);
							selectedImage = getToolkit().getImage(tempImageFile.toURI().toURL());
							paintSelectedImage.setImage(selectedImage);
							paintSelectedImage.repaint();
							currentImageName.setText(nodeName);
							//tempImageFile.delete();
						} catch (Exception e1) {
							e1.getMessage();
						}
						if (selectionNode.getPreviousSibling() != null) {
							last.setEnabled(true);
							lastNode = selectionNode.getPreviousSibling();
						} else {
							last.setEnabled(false);
						}
						if (selectionNode.getNextSibling() != null) {
							next.setEnabled(true);
							nextNode = selectionNode.getNextSibling();
						} else {
							next.setEnabled(false);
						}
					} else {
						if (saveimage.isEnabled()==false) {
							currentImageFile = null;
							selectedImage = null;
							paintSelectedImage.setImage(selectedImage);
							paintSelectedImage.repaint();
							currentImageName.setText("");
							last.setEnabled(false);
							next.setEnabled(false);
						} else {
							last.setEnabled(false);
							next.setEnabled(false);
						}
					}
				}
			}
		});
		//upload picture
		uploadPane = new JPanel();
		uploadPane.setOpaque(false);
		getContentPane().add(uploadPane, BorderLayout.NORTH);
		currentImage = new JLabel("Current Image: ");
		currentImage.setFont(new Font("",Font.ITALIC, 15));
		currentImage.setForeground(Color.BLACK);
		currentImageName = new JTextField();
		currentImageName.setColumns(10);
		currentImageName.setEditable(false);
		uploadLabel = new JLabel("Unload Image: ");
		uploadLabel.setFont(new Font("", Font.ITALIC,15));
		uploadLabel.setForeground(Color.BLACK);
		uploadPath = new JTextField();
		uploadPath.setColumns(20);;
		uploadButton = new JButton("upload Image");
		uploadButton.setFont(new Font("", Font.ITALIC,15));
		uploadButton.setForeground(Color.BLACK);
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadImageFromDir();
			}
		});
		uploadPane.add(currentImage);
		uploadPane.add(currentImageName);
		uploadPane.add(uploadLabel);
		uploadPane.add(uploadPath);
		uploadPane.add(uploadButton);
		//display selected Image
		paintSelectedImage = new PaintPanel();
		getContentPane().add(paintSelectedImage, BorderLayout.CENTER);
		
		
		
	}
	//upload image from directory
	protected void uploadImageFromDir(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		FileFilter filtper = new FileNameExtensionFilter("image file(*gif;*jpg;*jpeg)",
				"gif", "jpg", "jpeg");
		chooser.setFileFilter(filtper);
		
		paint = new PaintPanel();
		paint.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		paint.setPreferredSize(new Dimension(300, 300));
		chooser.setAccessory(paint);
		chooser.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				preDisplayImage(e);
			}
		});
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			uploadPath.setText(file.getAbsolutePath());
			if (file != null && file.isFile()) {
				try {
					//file is an image file from an outer directory
					selectedImage = getToolkit().getImage(file.toURI().toURL());
					paintSelectedImage.setImage(selectedImage);
					paintSelectedImage.repaint();
					saveimage.setEnabled(true);
					currentImageName.setText("");
					last.setEnabled(false);
					next.setEnabled(false);
				}catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	protected void preDisplayImage(PropertyChangeEvent e) {
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY == e.getPropertyName()) {
			File picfile = (File)e.getNewValue();
			if (picfile != null && picfile.isFile()) {
				try {
					Image image = getToolkit().getImage(picfile.toURI().toURL());
					paint.setImage(image);
					paint.repaint();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	protected void saveImageToTree(ActionEvent e) {
		DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (selectNode == null) {
			JOptionPane.showMessageDialog(this, "Please select a directory!", null,JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (imageName.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "please set image name!",null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (selectNode.isRoot()) {
			JOptionPane.showMessageDialog(this, "Don't save images to the main directory!", null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (selectNode.toString().indexOf('.') != -1) {
			JOptionPane.showMessageDialog(this, "This is a file, not a directory!", null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		//avoid the same name in a directory
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> childNodes = selectNode.children();
		while(childNodes.hasMoreElements()) {
			DefaultMutableTreeNode node = childNodes.nextElement();
			if (node.toString().substring(0, node.toString().lastIndexOf('.')).equals(imageName.getText())) {
				JOptionPane.showMessageDialog(this, "An image with this name exists. Please rename it!", "Name error", 
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		String imageType = uploadPath.getText().substring(uploadPath.getText().lastIndexOf('.')+1, uploadPath.getText().length());
		//save to directory
		try {	
			/*
			int w = selectedImage.getWidth(null);
			int h = selectedImage.getHeight(null);
			BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = bi.getGraphics();
			g.drawImage(selectedImage, 0, 0, null);
			ImageIO.write(bi, imageType, new File("./images/"+selectNode.toString()+"/"+imageName.getText()+"."+imageType));			
			*/
			File toBeSavedFile = new File(uploadPath.getText());
			DataOutputStream saveOutput = new DataOutputStream(new FileOutputStream(
					"./images/"+selectNode.toString()+"/"+imageName.getText()+
					".bin"));
			FileInputStream infileToSave = new FileInputStream(toBeSavedFile);
			byte[] binImage = new byte[(int)toBeSavedFile.length()];
			infileToSave.read(binImage, 0, (int)toBeSavedFile.length());
			saveOutput.write(binImage, 0, (int)toBeSavedFile.length());
			saveOutput.close();
			infileToSave.close();
			
			last.setEnabled(true);
			next.setEnabled(true);
		} catch(Exception e1) {
			e1.getMessage();
		}
		//change the node infomation
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(imageName.getText()+"."+imageType, false);
		model.insertNodeInto(newNode, selectNode, selectNode.getChildCount());
		TreeNode[] nodes = model.getPathToRoot(newNode);
		TreePath path = new TreePath(nodes);
		tree.scrollPathToVisible(path);
		tree.setSelectionPath(path);
		tree.startEditingAtPath(path);
		tree.repaint();
		
		imageName.setText("");
		saveimage.setEnabled(false);
		
	}
	protected void getLast() {
		String nodeName = lastNode.toString();
		try {
			//currentImageFile is a binary file
			currentImageFile = new File("./images/"+lastNode.getParent().toString()+"/"+
					nodeName.substring(0, nodeName.lastIndexOf('.'))+".bin");
			//get Image from binary file, write image to temp directory temporary
			String outFilePath = "./tmp/"+nodeName;
			FileInputStream fs = new FileInputStream(currentImageFile);
			byte[] binImage = new byte[(int)currentImageFile.length()];
			fs.read(binImage, 0, (int)currentImageFile.length());
			FileOutputStream os = new FileOutputStream(outFilePath);
			os.write(binImage);
			fs.close();
			os.close();
			//get Image from temp and display it
			File tempImageFile = new File(outFilePath);
			selectedImage = getToolkit().getImage(tempImageFile.toURI().toURL());
			paintSelectedImage.setImage(selectedImage);
			paintSelectedImage.repaint();
			currentImageName.setText(nodeName);
			//tempImageFile.delete();
		} catch(Exception e) {
			e.getMessage();
		}
		if (lastNode.getNextSibling() != null) {
			nextNode = lastNode.getNextSibling();
			next.setEnabled(true);
		}
		if (lastNode.getPreviousSibling() != null) {
			lastNode = lastNode.getPreviousSibling();
			last.setEnabled(true);
		} else {
			last.setEnabled(false);
		}
	}
	protected void getNext() {
		String nodeName = nextNode.toString();
		try {
			currentImageFile = new File("./images/"+nextNode.getParent().toString()+"/"+
					nodeName.substring(0, nodeName.lastIndexOf('.'))+".bin");
			//get Image from binary file, write image to temp directory temporary
			String outFilePath = "./tmp/"+nodeName;
			FileInputStream fs = new FileInputStream(currentImageFile);
			byte[] binImage = new byte[(int)currentImageFile.length()];
			fs.read(binImage, 0, (int)currentImageFile.length());
			FileOutputStream os = new FileOutputStream(outFilePath);
			os.write(binImage);
			fs.close();
			os.close();
			//get Image from temp and display it
			File tempImageFile = new File(outFilePath);
			selectedImage = getToolkit().getImage(tempImageFile.toURI().toURL());
			paintSelectedImage.setImage(selectedImage);
			paintSelectedImage.repaint();
			currentImageName.setText(nodeName);
			//tempImageFile.delete();
		} catch(Exception e) {
			e.getMessage();
		}
		if (nextNode.getPreviousSibling() != null) {
			lastNode = nextNode.getPreviousSibling();
			last.setEnabled(true);
		}
		if (nextNode.getNextSibling() != null) {
			nextNode = nextNode.getNextSibling();
			next.setEnabled(true);
		} else {
			next.setEnabled(false);
		}
	}
	//public static void main(String[] args) {
	public static void launch() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LiyuImages().setVisible(true);
			}
		});
		
	}
}
