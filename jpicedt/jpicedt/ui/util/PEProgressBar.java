/*
 PEProgressBar.java - 2002 - jPicEdt 1.3.2, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
// redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
// les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les concédants successifs.
//
// À cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
// l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
// étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
// donc à des développeurs et des professionnels avertis possédant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
// besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et,
// plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
//
// Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accepté les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.ui.util;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;


/**
 * a progress bar that is (currently) used only during GUI initialization on start-up (hence messages are predefined so that
 * it makes sense during a GUI init, but probably not elsewhere).
 */
public class PEProgressBar extends JWindow {

	private int maximum, progressValue;
	private String progressMsg;
	private String title;
	private LogoPanel logoPanel;

	private static final String TITLE = jpicedt.Version.getVersion() + "-" + jpicedt.Version.getBuildDate();
	private static final String INITIAL_MSG = "Loading, please wait..."; // [pending] i18n

	private static final String LOGO_URL = "/jpicedt/images/Logo-jpicedt.png";
	private static final int LOGO_WIDTH = 600;
	private static final int LOGO_HEIGHT = 124;
	private static final float MSG_X = 0.55f * LOGO_WIDTH;
	private static final float MSG_Y = 0.5f * LOGO_HEIGHT;
	private static final Color MSG_COLOUR = Color.red;
	private static final float TITLE_X = 0.55f * LOGO_WIDTH;
	private static final float TITLE_Y = 0.3f * LOGO_HEIGHT;
	private static final Color TITLE_COLOUR = Color.black;

	/**
	 *
	 */
	public PEProgressBar(int maximum){
		this(maximum,null,null);
	}

	/**
	 * @param maximum max nb of steps
	 * @param title not used anymore
	 * @param initMsg initial message
	 */
	public PEProgressBar(int maximum, String title, String initMsg){

		this.maximum = maximum;
		this.progressMsg = initMsg;
		if (progressMsg==null) progressMsg = INITIAL_MSG;
		this.title = title;
		if (this.title==null) this.title = TITLE;
		this.progressValue = 0;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getContentPane().add(logoPanel=new LogoPanel(), BorderLayout.CENTER);
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getHeight()/2);
		setVisible(true);
		try {SwingUtilities.invokeAndWait(logoPanel);}
		catch (Exception ex){ex.printStackTrace();}
	}

	/**
	 *
	 */
	private class LogoPanel extends JPanel implements Runnable {

		Line2D.Double redLine;
		ImageIcon icon;

		LogoPanel(){
			this.setBorder(new EmptyBorder(10, 10, 10, 10));
			this.setBackground(Color.white);
			// Logo-jpicedt.gif : size = LOGO_WIDTH x LOGO_HEIGHT :
			java.net.URL u = this.getClass().getResource(LOGO_URL);
			if (u != null) icon = new ImageIcon(u);
			this.setPreferredSize(new Dimension(LOGO_WIDTH,LOGO_HEIGHT));
			redLine =  new Line2D.Double();
			redLine.y1 = redLine.y2 = LOGO_HEIGHT * 0.6;
			redLine.x1 = redLine.x2 = LOGO_WIDTH * 0.1;
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			// 1) background logo :
			icon.paintIcon(this,g2,0,0);
			// 2) title :
			g2.setPaint(TITLE_COLOUR);
			g2.drawString(title, TITLE_X, TITLE_Y);
			// 3) message :
			g2.setPaint(MSG_COLOUR);
			g2.drawString(progressMsg, MSG_X, MSG_Y);
			// 4) red horizontal line (aka progress bar) :
			double ratio = ((double)progressValue)/((double)maximum);
			GradientPaint gradient = new GradientPaint((float)redLine.x1,0,Color.white,LOGO_WIDTH,0,Color.red);
			g2.setPaint(gradient);
			g2.setStroke(new BasicStroke(5.0f));
			redLine.x2 = redLine.x1 + 0.93 * ratio * (LOGO_WIDTH - redLine.x1);
			//System.out.println("repaint : progressValue = " + progressValue + ", max = " + maximum);
			if (ratio >0) g2.draw(redLine);
		}
		public void run(){
			this.repaint();
		}
	}

	/**
	 * destroy this progress bar, disposing the hosting frame
	 */
	public void destroy(){
		//System.out.println("destroy...");
		dispose();
	}

	/**
	 * increment the state of this progress bar by one, displaying the given message at the same time
	 * @param message if null, left unchanged.
	 */
	public void increment(String message){
		if (message != null) this.progressMsg = message;
		progressValue++;
		if (progressValue > maximum) progressValue = maximum;
		//System.out.println("increment : msg = " + message + ", val = " + progressValue + ", max = " + maximum);
		try {SwingUtilities.invokeAndWait(logoPanel);}
		catch (Exception ex){ex.printStackTrace();}
	}

	/**
	 * increment the state of this progress bar by one, leaving the message unchanged
	 */
	public void increment(){
		increment(null);
	}

	/**
	 * open a dialog box with the given information message
	 */
	public void confirmMsg(String message){
		System.out.println("[confirmMsg]:"+message);
		JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE); // [todo] to localize

	}

	/**
	 * open a dialog box with the given error message
	 */
	public void recoverableError(String message){
		System.out.println("[recoverableError]:"+message);
		JOptionPane.showMessageDialog(this, message, "Recoverable error", JOptionPane.ERROR_MESSAGE); // [todo] to localize
	}

	/**
	 * open a modal dialog box with the given error message, wait for the user to close the dialog, then exit the application
	 */
	public void fatalError(String message){
		System.err.println("[fatalError]:"+message);
		JOptionPane.showMessageDialog(this, message, "Fatal error", JOptionPane.ERROR_MESSAGE); // [todo] to localize
		System.exit(0);
	}

	///////////////////////////////////////////////////////////
	/**
	 * test
	 */
	public static void main(String args[]){
		Test test = new Test();
	}
	/**
	 * test class
	 */
	public static class Test implements ActionListener {
		PEProgressBar progressBar;
		public Test(){
			int numberOfProgressSteps = 20;
			progressBar = new PEProgressBar(numberOfProgressSteps);
			javax.swing.Timer timer = new javax.swing.Timer(400, this);
			timer.start();
		}

		public void actionPerformed(ActionEvent e){
			progressBar.increment(Double.toString(Math.random()));
		}

	}
}
