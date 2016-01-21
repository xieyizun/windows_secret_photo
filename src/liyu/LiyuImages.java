package liyu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
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

import com.xieyizun.internet_action.UploadPhoto;
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
	private JButton uploadInternetButton;
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
	private DefaultMutableTreeNode rootChild;
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
		setTitle("私密相册");
		//this.setResizable(false);
		setBounds(300, 10, 800, 700);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("./res/love.png"));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "退出?", "Exit", 0);
				if (result == 0)				
					System.exit(0);
			}
		});
		contentPane = new JPanel() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("./res/background6.jpg");
				Image image = icon.getImage();
				g.drawImage(image,0,0,null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5,18,5,18));
		contentPane.setLayout(new BorderLayout(4,4));
		setContentPane(contentPane);
		JPanel bottomPane = new JPanel();
		//bottomPane.setLayout(new GridLayout(1,6,6,2));
		bottomPane.setLayout(new BorderLayout(5,5));
		bottomPane.setOpaque(false);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		//last and next Button
		last = new JButton("上一张");
		next = new JButton("下一张");
		uploadInternetButton = new JButton("上传");
		
		uploadInternetButton.setEnabled(false);
		uploadInternetButton.setPreferredSize(new Dimension(100,30));
		uploadInternetButton.setFont(new Font("", Font.ITALIC,15));
		uploadInternetButton.setForeground(Color.BLACK);
		
		last.setEnabled(false);
		last.setPreferredSize(new Dimension(100,30));
		last.setFont(new Font("", Font.ITALIC,15));
		last.setForeground(Color.BLACK);
		
		next.setEnabled(false);
		next.setPreferredSize(new Dimension(100,30));
		next.setFont(new Font("", Font.ITALIC,15));
		next.setForeground(Color.BLACK);
		//上传图片到服务器
		uploadInternetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//doUploadInternet();
				System.out.println(currentImageFile.getAbsolutePath());
				UploadPhoto.uploadPhotoToInternet(currentImageFile.getAbsolutePath());
			}
		});
		
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
		//delete button
		deleteNodeButton = new JButton("删除");
		deleteNodeButton.setEnabled(false);
		deleteNodeButton.setFont(new Font("", Font.ITALIC,15));
		deleteNodeButton.setPreferredSize(new Dimension(100,5));
		deleteNodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nodeName = selectionNode.toString();
				if (selectionNode.getAllowsChildren()) {
					File hasImages = new File("./images/"+nodeName);
					if (hasImages.listFiles().length==0) {
						int result = JOptionPane.showConfirmDialog(null, "确定删除?", null, 0);
						if (result == 0) {
							hasImages.delete();
							DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
							model.removeNodeFromParent(selectionNode);
							tree.repaint();
							deleteNodeButton.setEnabled(false);
							JOptionPane.showMessageDialog(null, "删除目录 "+nodeName+" 成功!");
						} else {
							return;
						}						
					} else {
						int result = JOptionPane.showConfirmDialog(null, "目录中的相片会一同删除，确定删除?",
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
							JOptionPane.showMessageDialog(null, "删除目录 "+nodeName+" 成功!");
						} else {
							return;
						}
					}
				} else {
					String parentNode = selectionNode.getParent().toString();
					int result=JOptionPane.showConfirmDialog(null, "确定删除相片?", null, 0);
					if (result == 0 && currentImageName != null) {
						File image = new File("./images/"+parentNode+"/"+
								currentImageName.getText());
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
							JOptionPane.showMessageDialog(null, "成功删除相片 "+nodeName);
							deleteNodeButton.setEnabled(false);
						} else {						
							try {
								currentImageFile = new File("./images/"+parentNode+"/"+
										nodeName);
								selectedImage = getToolkit().getImage(currentImageFile.toURI().toURL());
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
							JOptionPane.showMessageDialog(null, "成功删除相片 "+currentImageName.getText());	
							currentImageName.setText(nodeName);						
						}
					} else {
						return;
					}
				}
			}
		});
		JPanel bottomButtonsPane = new JPanel();
		bottomButtonsPane.setOpaque(false);
		saveimage = new JButton("保存");
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
		saveLabel = new JLabel("     名称:");
		saveLabel.setFont(new Font("", Font.ITALIC,17));
		saveLabel.setForeground(Color.BLACK);
		bottomPane.add(deleteNodeButton, BorderLayout.WEST);
		
		bottomButtonsPane.add(saveLabel);
		bottomButtonsPane.add(imageName);
		bottomButtonsPane.add(saveimage);
		bottomButtonsPane.add(last);
		bottomButtonsPane.add(next);
		bottomButtonsPane.add(uploadInternetButton);
		
		bottomPane.add(bottomButtonsPane, BorderLayout.CENTER);
		//menuBar
		JMenuBar menuBar = new JMenuBar();
		//file operator
		JMenu fileOperation = new JMenu("文件");
		JMenuItem saveAs = new JMenuItem("另存为");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JFileChooser saveChooser = new JFileChooser();
				saveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				saveChooser.setMultiSelectionEnabled(false);			
				if (selectedImage == null) {				
					JOptionPane.showMessageDialog(null, "请打开一张相片!", null, JOptionPane.WARNING_MESSAGE);
					return;
				} 
				if ("".equals(currentImageName.getText())) {
					JOptionPane.showMessageDialog(null, "没有上传相片!", null, JOptionPane.WARNING_MESSAGE);
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
						JOptionPane.showMessageDialog(null, currentImageName.getText().toString()+" 已经成功保存到 "+savePath, 
								null, JOptionPane.WARNING_MESSAGE);
					} catch(Exception e1) {
						e1.getMessage();
					}
					
				}
			}
		});
		JMenuItem exit = new JMenuItem("退出");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "确定退出?", "Exit", 0);
				if (result == 0)			
					System.exit(0);
			}
		});
		fileOperation.addSeparator();
		fileOperation.add(saveAs);
		fileOperation.addSeparator();
		fileOperation.add(exit);
		
		//help
		JMenu help = new JMenu("帮助");
		JMenuItem usage = new JMenuItem("用法");
		help.add(usage);
		//about
		JMenu about = new JMenu("关于");
		JMenuItem aboutUs = new JMenuItem("联系");
		aboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "If there are any problems arised when you use this software,"
						+"\n"+"please contact us by 1824762899@qq.com by email. Thank you!",
						"contact", JOptionPane.PLAIN_MESSAGE);
			}
		});	
		
		JMenuItem versions = new JMenuItem("版本");
		about.add(aboutUs);
		about.addSeparator();
		about.add(versions);
		
		//account
		JMenu me = new JMenu("账号");
		JMenuItem account = new JMenuItem("用户名：" + LoginFrame.username);
		me.add(account);
		
		menuBar.add(fileOperation);
		menuBar.add(help);
		menuBar.add(about);
		menuBar.add(me);
		
		setJMenuBar(menuBar);
		

				
		//scroll pane	
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(3, 3, 3, 3));
		//root nodes
		final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("相片");
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
		JMenuItem item1 = new JMenuItem("新建目录");
		JMenuItem item2 = new JMenuItem("批量导入相片");
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				//newDirName = "";
				newDirName = JOptionPane.showInputDialog("请输入目录名: ");
				if (newDirName != null && !"".equals(newDirName)) {
					
					@SuppressWarnings("unchecked")
					Enumeration<DefaultMutableTreeNode> childNodes = selectionNode.children();
					while(childNodes.hasMoreElements()) {
						DefaultMutableTreeNode node = childNodes.nextElement();
						if (newDirName.equals(node.toString())) {
							JOptionPane.showMessageDialog(null, "目录已存在，请重新命名!", "Name error", 
									JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					childDir = new DefaultMutableTreeNode(newDirName, true);
					model.insertNodeInto(childDir, rootNode, rootNode.getChildCount());
					rootNode.add(childDir);
					File mkDir = new File("./images", newDirName);
					mkDir.mkdir();
				}
			}
		});
		//upload several images once
		item2.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode root=(DefaultMutableTreeNode)model.getRoot();
				
				Enumeration<DefaultMutableTreeNode> rootChildren = root.children();
				if (rootChildren.hasMoreElements()==false) {
					JOptionPane.showMessageDialog(null, "请新建文件夹!", null, JOptionPane.WARNING_MESSAGE);
					return;
				}
				//select the directory that these images will be saved to
				String imagesDirectoryName = JOptionPane.showInputDialog("请输入文件夹名称: ");
			    //deal with cancel selection
				if (imagesDirectoryName == null) {
			    	return;
			    }
				boolean rightDirectory = false;
				while (rootChildren.hasMoreElements()) {
					rootChild = rootChildren.nextElement();
					if (rootChild.toString().equals(imagesDirectoryName)) {
						rightDirectory = true;
						break;
					}
				}
				if (rightDirectory == false) {
					JOptionPane.showMessageDialog(null, "请选择一个已经存在的目录!", null, JOptionPane.WARNING_MESSAGE);
					return;
				}
				//show file chooser so as to selected files
				JFileChooser imagesChooser = new JFileChooser();
				imagesChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				imagesChooser.setMultiSelectionEnabled(true);
				FileFilter imagesFileFilter = new FileNameExtensionFilter("image file(*gif;*jpg;*jpeg;*png",
						"gif", "jpg", "jpeg", "png");
				imagesChooser.setFileFilter(imagesFileFilter);
				
				paint = new PaintPanel();
				paint.setBorder(new BevelBorder(BevelBorder.LOWERED,null,null,null,null));
				paint.setPreferredSize(new Dimension(300,300));
				imagesChooser.setAccessory(paint);
				imagesChooser.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						preDisplayImage(e);
					}
				});	
				int result = imagesChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
				  File images[] = imagesChooser.getSelectedFiles();
				  for(int i = 0; i < images.length; i++) {
					File image = images[i];
					    //file is an image file from an outer directory
					try {
							selectedImage = getToolkit().getImage(image.toURI().toURL());
					} catch (MalformedURLException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
					}
					paintSelectedImage.setImage(selectedImage);
					paintSelectedImage.repaint();
					currentImageName.setText("");
					last.setEnabled(false);
					next.setEnabled(false);
					String imageName = JOptionPane.showInputDialog("请输入相片名: ");
					if (imageName == null) {
						if (i == images.length-1) {
							break;
						}
						continue;
					}
					while ("".equals(imageName)) {
							JOptionPane.showMessageDialog(null, "名字不能为空!", "Image save failure", JOptionPane.WARNING_MESSAGE);
							imageName = JOptionPane.showInputDialog("请输入相片的名字: ");
					}
					//avoid the same name in a directory
					Enumeration<DefaultMutableTreeNode> childNodes = rootChild.children();
					while(childNodes.hasMoreElements()) {
							DefaultMutableTreeNode node = childNodes.nextElement();
							if (node.toString().substring(0, node.toString().lastIndexOf('.')).equals(imageName)) {
								JOptionPane.showMessageDialog(null, "该名称已存在，请重新命名!", "Name error", 
										JOptionPane.WARNING_MESSAGE);
								imageName = JOptionPane.showInputDialog("请输入相片名: ");
								childNodes = rootChild.children();
							}
					}
					
					//save the image to the disk firstly
					String imagetype = "";
					try {
						Image tempImage = getToolkit().getImage(image.toURI().toURL());
						int w = tempImage.getWidth(null);
						int h = tempImage.getHeight(null);
						BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
						Graphics g = bi.getGraphics();
						g.drawImage(tempImage, 0, 0, null);
						imagetype = image.getAbsolutePath().toString().substring(image.getAbsolutePath().toString().lastIndexOf('.',
								image.getAbsolutePath().toString().length()));
						try {
							//"_" + LoginFrame.username用于在上传到数据库时标识用户
							ImageIO.write(bi, imagetype.substring(1,imagetype.length()),
									new File("./images/"+rootChild.toString()+"/"+imageName + "_" + LoginFrame.username+imagetype));
							//set the image is hidden in the directory images
							File saveOutputFile = new File("./images/"+rootChild.toString()+"/"+imageName+imagetype);
							String sets = "attrib +H \"" + saveOutputFile.getAbsolutePath() + "\""; 
							Runtime.getRuntime().exec(sets);
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//add node to the JTree
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(imageName+ "_" + LoginFrame.username+imagetype, false);
					model.insertNodeInto(newNode, rootChild, rootChild.getChildCount());
					TreeNode[] nodes = model.getPathToRoot(newNode);
					TreePath path = new TreePath(nodes);
					tree.scrollPathToVisible(path);
					tree.setSelectionPath(path);
					tree.startEditingAtPath(path);
					tree.repaint();
				  }
				}
			}
		});
		operateNodes.add(item1);
		operateNodes.add(item2);
		operateNodes.setInvoker(tree);
		tree.addMouseListener(new MouseAdapter() {						
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					if (selectedNode.isRoot()) {
						operateNodes.show(operateNodes.getInvoker(),operateNodes.getInvoker().getX()+73,
							operateNodes.getInvoker().getY());
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
							
							currentImageFile = new File("./images/"+parent.toString()+"/"+nodeName);
							selectedImage = getToolkit().getImage(currentImageFile.toURI().toURL());
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
		currentImage = new JLabel("当前图片: ");
		currentImage.setFont(new Font("",Font.ITALIC, 15));
		currentImage.setForeground(Color.BLACK);
		currentImageName = new JTextField();
		currentImageName.setColumns(10);
		currentImageName.setEditable(false);
		uploadLabel = new JLabel("导入图片: ");
		uploadLabel.setFont(new Font("", Font.ITALIC,15));
		uploadLabel.setForeground(Color.BLACK);
		uploadPath = new JTextField();
		uploadPath.setColumns(20);;
		uploadButton = new JButton("导入图片");
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
		chooser.setMultiSelectionEnabled(true);
		FileFilter filtper = new FileNameExtensionFilter("image file(*gif;*jpg;*jpeg;*png)",
				"gif", "jpg", "jpeg", "png");
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
					//上传到互联网
					uploadInternetButton.setEnabled(true);
					
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
			JOptionPane.showMessageDialog(this, "请选择一个文件夹!", null,JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (imageName.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "请设定相片名字!",null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (selectNode.isRoot()) {
			JOptionPane.showMessageDialog(this, "主文件夹无法保存相片!", null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (selectNode.toString().indexOf('.') != -1) {
			JOptionPane.showMessageDialog(this, "这是文件，不是目录!", null, JOptionPane.WARNING_MESSAGE);
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
			
			int w = selectedImage.getWidth(null);
			int h = selectedImage.getHeight(null);
			BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = bi.getGraphics();
			g.drawImage(selectedImage, 0, 0, null);
			ImageIO.write(bi, imageType, new File("./images/"+selectNode.toString()+"/"+imageName.getText()+ "_" + LoginFrame.username+"."+imageType));			
			
			//set the image is hiden in the directory images
			File saveOutputFile = new File("./images/"+selectNode.toString()+"/"+imageName.getText()+
					 "_" + LoginFrame.username + "."+imageType);
			String sets = "attrib +H \"" + saveOutputFile.getAbsolutePath() + "\""; 
			Runtime.getRuntime().exec(sets);
			
			last.setEnabled(true);
			next.setEnabled(true);
		} catch(Exception e1) {
			e1.getMessage();
		}
		//change the node infomation
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(imageName.getText()+ "_" + LoginFrame.username+"."+imageType, false);
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
			
			currentImageFile = new File("./images/"+lastNode.getParent().toString()+"/"+
					nodeName);
			selectedImage = getToolkit().getImage(currentImageFile.toURI().toURL());
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
			currentImageFile = new File("./images/"+lastNode.getParent().toString()+"/"+
					nodeName);
			selectedImage = getToolkit().getImage(currentImageFile.toURI().toURL());
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
