package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import codec.ImageDecoder;
import codec.ImageEncoder;

public class ImageProcessing extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8513927584316178739L;
	private String titolo;
	private Container cp;
	
	private JMenuItem apri, salva, esci;
	private JMenuItem copia, codificaJpeg, 
			scalaGrigio;
	private JPanel panelSrc, panelDst;
	private JLabel imageSrc, imageDst;
	private JLabel imageSrcLbl, imageDstLbl;

	private JFileChooser fc;

	public ImageProcessing() {
		
		// JMenuItems
		this.apri = new JMenuItem("Apri immagine");
		this.salva = new JMenuItem("Salva immagine");
		this.esci = new JMenuItem("Esci");
		this.copia = new JMenuItem("Copia");
		this.codificaJpeg = new JMenuItem("Codifica JPEG");
		this.scalaGrigio = new JMenuItem("Scala di grigio");
		// ImagePanels
		this.panelSrc = new JPanel();
		this.panelDst = new JPanel();
		// ImageIcons
		this.imageSrc = new JLabel();
		this.imageDst = new JLabel();
		// JLabels
		this.imageSrcLbl = new JLabel();
		this.imageDstLbl = new JLabel();
		// JFileChooser
		this.fc = new JFileChooser();
		// Container
		this.cp = this.getContentPane();
		// create JMenuBar
		this.createJMenuBar();
		// aggiungamo ImagePanels
		this.setupJPanels();
		// aggiungiamo ActionListeners
		this.addActionListeners();
	}

	public void createJMenuBar() {
		JMenuBar mb = new JMenuBar();
		// creiamoo il JMenu for "File"
		JMenu menu = new JMenu("File");
		// aggiungiamo i JMenuItems al menu
		menu.add(this.getMiOpen());
		menu.add(this.getMiSave());
		// un separatore
		menu.addSeparator();
		// uscita
		menu.add(this.getMiExit());
		mb.add(menu); 
		// creeiamo un JMenu for  l'elaborazione
		menu = new JMenu("Elabora");
		menu.add(this.getMiCopy());
		menu.add(this.getMiJpegCodify());
		menu.add(this.getMiGrayscale());
		mb.add(menu); 
		setJMenuBar(mb);
	}

	public void addActionListeners() {
		this.getMiOpen().addActionListener(this);
		this.getMiSave().addActionListener(this);
		this.getMiSave().setEnabled(false);
		this.getMiExit().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.getMiCopy().addActionListener(this);
		this.getMiCopy().setEnabled(false);
		this.getMiJpegCodify().addActionListener(this);
		this.getMiJpegCodify().setEnabled(false);
		
	
		this.getMiGrayscale().addActionListener(this);
		this.getMiGrayscale().setEnabled(false);
	}

	public void setupJPanels() {
		// modifico il pannello dell'immagine sorgente
		this.getPanelSrc().setPreferredSize(new Dimension(512, 512));
		this.getPanelSrc().setBorder(
				BorderFactory.createTitledBorder("Nessuna Immagine"));
		this.getPanelSrc().add(this.getImageSrc());
		this.getPanelSrc().add(this.getImageSrcLbl());
		// modifico il pannello dell'immagine finale
		this.getPanelDst().setPreferredSize(new Dimension(512, 512));
		this.getPanelDst().setBorder(
				BorderFactory.createTitledBorder("Nessuna Immagine"));
		this.getPanelDst().add(this.getImageDst());
		this.getPanelDst().add(this.getImageDstLbl());
		// aggiungiamo i pannelli al layout
		this.getCp().setLayout(new FlowLayout());
		this.getCp().add(this.getPanelSrc());
		this.getCp().add(this.getPanelDst());
	}

	public void actionPerformed(ActionEvent ev) {
		String cmd = ev.getActionCommand();
		if ("Apri immagine".equals(cmd)) {
			int retval = this.getFc().showOpenDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				BufferedImage bi = this.readImage(fc.getSelectedFile());
				if (bi != null) {
					int height = bi.getHeight();
					int width = bi.getWidth();
					this.getImageSrc().setIcon(new ImageIcon(bi));
					this.setBorderTitle(this.getPanelSrc(), "Original Image");
					this.getImageSrcLbl().setText(
							String.format("Dimensioni = %d X %d", width,
									height));
					this.getPanelSrc().setPreferredSize(
							new Dimension(width + 100, height + 100));
					this.getPanelDst().setPreferredSize(
							new Dimension(width + 100, height + 100));
					this.getMiCopy().setEnabled(true);
					this.getMiJpegCodify().setEnabled(false);
			
					this.getMiGrayscale().setEnabled(false);
					this.getMiSave().setEnabled(true);
					pack();
				} else
					this.getPanelSrc().removeAll();
				this.getImageDst().setIcon(null);
				this.getImageDstLbl().setText(null);
				this.setBorderTitle(this.getPanelDst(), "Nessuna Immagine");
			}
		} else if ("Salva immagine".equals(cmd)) {
			int retval = this.getFc().showSaveDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				if (this.getImageDst().getIcon() == null)
					this.getImageDst().setIcon(this.getImageSrc().getIcon());
				try {
					ImageIO.write(
							(RenderedImage) this.getImage(this.getImageDst()),
							"png", this.getFc().getSelectedFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("Copia".equals(cmd)) {
			this.setBorderTitle(this.getPanelDst(), "Copia dell'immagine original");
			this.getImageDst().setIcon(this.getImageSrc().getIcon());
			this.getMiJpegCodify().setEnabled(true);
			
			this.getMiGrayscale().setEnabled(true);
		} else
			process(cmd);
	}

	public void process(String opName) {
		BufferedImageOp op = null;
		BufferedImage image = (BufferedImage) this.getImage(this.getImageDst());
			
		if ("Codifica JPEG".equals(opName)) {
			this.setBorderTitle(this.getPanelDst(), "Progressing - JPEG");
			this.getImageDst().setIcon(new ImageIcon(this.jpegCodify(image)));
						
		}  else if ("Scala di grigio".equals(opName)) {
			this.setBorderTitle(this.getPanelDst(), "Progressing - Gray Scale");
			op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),
					null);
			this.getImageDst()
					.setIcon(
							new ImageIcon(op.filter((BufferedImage) this
									.getImage(getImageDst()), null)));
		}
		pack();
	}

	public BufferedImage readImage(File file) {
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}

	public BufferedImage jpegCodify(BufferedImage bi) {
		// creiamo un oggetto imageEncoder 
		ImageEncoder imageEncoder = new ImageEncoder(bi);
		// prendiamo una matrice di blocchi codificati
		try {
			int[][] blocks = imageEncoder.encode();

			// otteniamo i prefissi utilizzati per i blocchi di codifica
			Map<Object, String> prefixCodes = imageEncoder
					.getHuffmanCodes(blocks);
			
			// codifichiamo i blochhi come una stringa usando i prefissi
			String data = imageEncoder.encode(blocks, prefixCodes);
			
			
			
		
			
			
			int width = bi.getWidth();
			int height = bi.getHeight();
			// creiamo un decodificatore per ricostruire l'immagine originale
			ImageDecoder imageDecoder = new ImageDecoder(width, height, data,
					prefixCodes);
			//ricostruiamo i valori RGB
			int[] rgbArray = imageDecoder.decodifica();
			// creiamo un nuovo BufferedImage con i valori RGB 
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			bi.setRGB(0, 0, width, height, rgbArray, 0, width);
			
		} catch (IllegalArgumentException e) {

			JOptionPane.showMessageDialog(null,
					"Immagine di dimensioni non conformi, riprova!");
			
			this.getImageSrc().setIcon(new ImageIcon(""));
			this.getImageSrcLbl().setText("");
			this.getPanelDst().setPreferredSize(
					new Dimension());
			this.getImageSrcLbl().setText("");
		}
		return bi;
	}

	public void setBorderTitle(JPanel panel, String title) {
		((TitledBorder) panel.getBorder()).setTitle(title);
	}

	public Image getImage(JLabel label) {
		return ((ImageIcon) label.getIcon()).getImage();
	}

	public static void main(String[] args) {
		JFrame frame = new ImageProcessing();
		frame.setTitle("Codifica JPEG");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
	}

	public String getTitle() {
		return titolo;
	}

	public void setTitle(String title) {
		this.titolo = title;
	}

	public Container getCp() {
		return cp;
	}

	public void setCp(Container cp) {
		this.cp = cp;
	}

	public JMenuItem getMiOpen() {
		return apri;
	}

	public void setMiOpen(JMenuItem miOpen) {
		this.apri = miOpen;
	}

	public JMenuItem getMiSave() {
		return salva;
	}

	public void setMiSave(JMenuItem miSave) {
		this.salva = miSave;
	}

	public JMenuItem getMiExit() {
		return esci;
	}

	public void setMiExit(JMenuItem miExit) {
		this.esci = miExit;
	}

	public JMenuItem getMiCopy() {
		return copia;
	}

	public void setMiCopy(JMenuItem miCopy) {
		this.copia = miCopy;
	}

	public JMenuItem getMiJpegCodify() {
		return codificaJpeg;
	}

	public void setMiJpegCodify(JMenuItem miJpegCodify) {
		this.codificaJpeg = miJpegCodify;
	}

	public JMenuItem getMiGrayscale() {
		return scalaGrigio;
	}

	public void setMiGrayscale(JMenuItem miGrayscale) {
		this.scalaGrigio = miGrayscale;
	}

	public JPanel getPanelSrc() {
		return panelSrc;
	}

	public void setPanelSrc(JPanel panelSrc) {
		this.panelSrc = panelSrc;
	}

	public JPanel getPanelDst() {
		return panelDst;
	}

	public void setPanelDst(JPanel panelDst) {
		this.panelDst = panelDst;
	}

	public JFileChooser getFc() {
		return fc;
	}

	public void setFc(JFileChooser fc) {
		this.fc = fc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public JLabel getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(JLabel imageSrc) {
		this.imageSrc = imageSrc;
	}

	public JLabel getImageDst() {
		return imageDst;
	}

	public void setImageDst(JLabel imageDst) {
		this.imageDst = imageDst;
	}

	public JLabel getImageSrcLbl() {
		return imageSrcLbl;
	}

	public void setImageSrcDesc(JLabel imageSrcLbl) {
		this.imageSrcLbl = imageSrcLbl;
	}

	public JLabel getImageDstLbl() {
		return imageDstLbl;
	}

	public void setImageDstLbl(JLabel imageDstLbl) {
		this.imageDstLbl = imageDstLbl;
	}

	

	

	

}