// InternalFrameMDIManager.java --- -*- coding: iso-8859-1 -*-
// January 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: InternalFrameMDIManager.java,v 1.35 2013/03/27 06:51:21 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
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
package jpicedt.ui.internal;

import jpicedt.Version;
import jpicedt.JPicEdt;

import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.toolkit.CustomizerDialog;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.toolkit.PopupMenuFactory;

import jpicedt.ui.PEDrawingBoard;
import jpicedt.ui.MDIManager;
import jpicedt.ui.util.SystemOutUtilities;
import jpicedt.ui.util.PEProgressBar;
import jpicedt.ui.action.*;

import jpicedt.widgets.PEInternalDialog;
import jpicedt.widgets.PEInternalFrame;
import jpicedt.widgets.MDIComponent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.MissingResourceException;
import java.util.EnumSet;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ArrayList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import static jpicedt.Log.*;

/**
 * An MDI Manager implementation for "childframe" mode (ie using <code>JInternalFrame</code>'s).
 * <p>
 * <b>Layer management:</b>
 * <ul>
 * <li> <code>PEDrawingBoard</code>'s are added to <code>JLayeredPane.DEFAULT_LAYER</code> (bottom most layer)
 * <li> <code>DockablePanel</code>'s are added to <code>JLayeredPane.PALETTE_LAYER</code>
 * </ul>
 * </p>
 * @since jPicEdt 1.2
 * @author Sylvain Reynal
 * @version $Id: InternalFrameMDIManager.java,v 1.35 2013/03/27 06:51:21 vincentb1 Exp $
 */
public class InternalFrameMDIManager extends MDIManager {

	private JFrame mainFrame;
	private PEMenuBar menubar;
	private PEToolBar mainToolbar; // shared actions, e.g. "Save", "Open", ...
	private JDesktopPane desktopPane; // underway (public)
	private InternalDesktopManager desktopManager;
	private InternalFrameEventHandler internalFrameHandler;
	private PopupMenuFactory popupMenuFactory;

	public static final String KEY_GEOMETRY_X = "ui.geometry.x";
	public static final String KEY_GEOMETRY_Y = "ui.geometry.y";
	public static final String KEY_GEOMETRY_WIDTH = "ui.geometry.width";
	public static final String KEY_GEOMETRY_HEIGHT = "ui.geometry.height";

	public static final String KEY_DESKTOP_COLOR = "ui.desktop-color";
	public static final Color desktopColorDEFAULT = new Color(106,105,207); // KDE2 !!!

	public String getName(){
		return MDI_CHILDFRAMES;
	}

	/**
	 * @param progressBar The progress bar to inform of progress in the initialization ; can be null.
	 */
	public InternalFrameMDIManager(PEProgressBar progressBar){

		// init LAF
		jpicedt.ui.LAFManager.updateLaf();

		// add a PropertyChangeListener to the UIManager, so that
		// every change in LAF leads to an immediate repainting of
		// the whole component tree
		UIManager.addPropertyChangeListener(new PropertyChangeListener(){
			                                    public void propertyChange(PropertyChangeEvent e) {
				                                    if (e.getPropertyName().equals("lookAndFeel")) {
					                                    JComponent c = (JComponent)mainFrame.getRootPane();
					                                    SwingUtilities.updateComponentTreeUI(c);
					                                    c.invalidate();	c.validate(); c.repaint();
				                                    }}});

		// init the application main frame
		mainFrame = new JFrame("jPicEdt "+Version.getVersion());

		PEToolKit.setAppIconToDefault(mainFrame);

		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout());
		// allows JOptionPane to be instanciated with an implicit root frame
		JOptionPane.setRootFrame(mainFrame);

		try {
			// menubar
			menubar=new PEMenuBar();
			mainFrame.setJMenuBar(menubar);

			// main toolbar
			mainToolbar=new PEToolBar();
			mainFrame.getContentPane().add(mainToolbar, BorderLayout.NORTH);

			// test:
			//JPanel dac = new DockableAttributesCustomizer(progressBar);
			//mainFrame.getContentPane().add(dac, BorderLayout.WEST);


			// desktop
			desktopPane = new JDesktopPane();
			desktopPane.setBackground(desktopColorDEFAULT);
			desktopPane.setBorder(BorderFactory.createLineBorder(Color.black));
			//desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE); // JDK1.2 -> desktopPane.putClientProperty("JDesktopPane.dragMode","outline");
			mainFrame.getContentPane().add(desktopPane, BorderLayout.CENTER);
			int desktopPanePreferredWidth = (int)(Toolkit.getDefaultToolkit().getScreenSize().width * 0.9);
			int desktopPanePreferredHeight = (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 0.9);
			desktopPane.setPreferredSize(new Dimension(desktopPanePreferredWidth,desktopPanePreferredHeight));
			desktopManager=new InternalDesktopManager();
			desktopPane.setDesktopManager(desktopManager);
			internalFrameHandler = new InternalFrameEventHandler();
		}
		catch(MissingResourceException e){
			System.err.println("During GUI initialization, I was unable to find internationalization resource: " + e.getKey());
			System.err.println("JVM stack trace dump :");
			e.printStackTrace();
			System.err.println("In any case, you may need to check the integrity of the jpicedt.jar file you downloaded.");
			System.err.println("If it still doesn't work, please send a bug report to : reynal@ensea.fr, with this console dump included.");
			System.err.println(":-((((((((((  Exiting... sorry !");
			SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD); // close any open File Stream
			if (progressBar != null)
				progressBar.fatalError(e.getMessage() + "\nSee "+ SystemOutUtilities.getErrorLogFile() + " for details."); //[todo] to localize
			System.exit(0);
		}
		catch (Exception e) {
			System.err.println("OK, there SURE is a bug somewhere... Thank you for sending a bug report including the following stack trace to syd@jpicedt.org");
			System.err.println("Stack trace dump starts here :");
			e.printStackTrace();
			System.err.println(":-((((((((((  Exiting...sorry !");
			SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD);// close any open File Stream
			if (progressBar != null)
				progressBar.fatalError(e.getMessage() + "\nSee "+ SystemOutUtilities.getErrorLogFile() + " for details."); //[todo] to localize
			System.exit(0);
		}
		mainFrame.addWindowListener(new WindowAdapter(){
			                            public void windowClosing(WindowEvent e){
				                            new jpicedt.JPicEdt.ExitAction().actionPerformed(null);
			                            }
		                            });
		mainFrame.validate();
		// size and location on the screen :
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = JPicEdt.getProperty(KEY_GEOMETRY_X,(int)(0.05*screenSize.width));
		int y = JPicEdt.getProperty(KEY_GEOMETRY_Y,(int)(0.05*screenSize.height));
		int w = JPicEdt.getProperty(KEY_GEOMETRY_WIDTH,(int)(0.9*screenSize.width));
		int h = JPicEdt.getProperty(KEY_GEOMETRY_HEIGHT,(int)(0.9*screenSize.height));
		mainFrame.setSize(w,h);
		mainFrame.setLocation(x,y);

		// init colours from pref.
		update();
		if (progressBar != null) {
			progressBar.increment("GUI created successfully !");
			//[pending] progressBar.destroy(); // close progress bar
		}

		// setVisible "realizes" the main JFrame (i.e. create new Swing thread)
		mainFrame.setVisible(true);
	}


	/**
	 * Factory method that creates an <code>MDIComponent</code> which acts as a UI delegate to the given pane.
	 * This implementation returns a properly init'd <code>JInternalFrame</code>, with the given pane
	 * added to its content-pane.
	 */
	public MDIComponent createMDIComponent(JComponent pane){
		return new InternalMDIComponent(pane);
	}

	/**
	 * Depending on the inner pane of the given container,
	 * adds it to the <code>DEFAULT_LAYER</code> or to the <code>PALETTE_LAYER</code>.
	 */
	public void addMDIComponent(MDIComponent c){
		//if (!(c instanceof InternalMDIComponent)) return;
		Container innerPane = c.getContentPane();
		if (innerPane instanceof PEDrawingBoard) {
			desktopPane.add((InternalMDIComponent)c, JLayeredPane.DEFAULT_LAYER);
			((InternalMDIComponent)c).addInternalFrameListener(internalFrameHandler);
		}
		else if (c instanceof PEInternalDialog){
			desktopPane.add((PEInternalDialog)c, JLayeredPane.MODAL_LAYER);
		}
		else
			desktopPane.add((InternalMDIComponent)c, JLayeredPane.DEFAULT_LAYER); //
		// Dockable's layer is re-handled in addDockablePanel()

	}

	public void closeBoard(PEDrawingBoard board){
		InternalMDIComponent mdiComp = (InternalMDIComponent)getHostingContainer(board);
		if (mdiComp==null) return;
		// simulate a click on the "close" button, it's safe because it's gives us a unified way of closing boards
		try {
			mdiComp.setClosed(true);
		}
		catch (PropertyVetoException e){}
	}

	/**
	 * Returns the dimension of the desktop that hosts the components of this MDIManager.
	 */
	public Dimension getDesktopSize(){
		return desktopPane.getSize();
	}

	/**
	 * Saves UI geometry to <code>JPicEdt</code>'s preferences.
	 * @see #InternalFrameMDIManager
	 * @see #addDockablePanel
	 * @see jpicedt.ui.MDIManager.DockablePanel#setVisible
	 */
	public void saveGeometry(){
		// save desktop geometry :
		Properties preferences = JPicEdt.getPreferences();
		preferences.setProperty(KEY_GEOMETRY_X,Integer.toString(mainFrame.getX()));
		preferences.setProperty(KEY_GEOMETRY_Y,Integer.toString(mainFrame.getY()));
		preferences.setProperty(KEY_GEOMETRY_WIDTH,Integer.toString(mainFrame.getWidth()));
		preferences.setProperty(KEY_GEOMETRY_HEIGHT,Integer.toString(mainFrame.getHeight()));
		super.saveGeometry(); // save DockablePanel's geometry
	}

	/**
	 * Updates local properties from the <code>JPicEdt</code>'s preferences (e.g. GUI colours,
	 * rendering-hints,&hellip;)  This implementation calls the superclass's method, then update the desktop-pane
	 * colour.
	 */
	public void update(){
		//desktopPane = null; // test error.log
		// colours :
		Color desktopColor = JPicEdt.getProperty(KEY_DESKTOP_COLOR,desktopColorDEFAULT);
		desktopPane.setBackground(desktopColor);
		if (mainFrame.isVisible()){
			desktopPane.repaint();
		}
		super.update(); // update RenderingHints for each PEDrawingBoard
	}



	//////////////////////////////////////////////////////////////////////
	//// GUI COMPONENTS
	//////////////////////////////////////////////////////////////////////

	/**
	 * Update <code>JMenuItem</code>'s accelerators from the key/value pairs stored in <code>JPicEdt</code>'s
	 * preferences
	 */
	public void updateAccelerators(){
		 menubar.updateAccelerators();
	}

	/**
	 * Update the content (i.e. <code>PEMenu</code>'s) of the menu-bar depending on the current state of the
	 * MDIManager.
	 */
	 public void updateMenuBar(){
		 menubar.updateMenuBar();
	 }

	/**
	 * Updates the "recent files" sub-menu items after a frame was saved or opened.
	 * @since PicEdt 1.1
	 */
	 public void updateRecentFilesSubmenu(){
		 menubar.updateRecentFilesSubmenu();
	 }

	/**
	 * Updates the "script" menu items
	 * @since jpicedt 1.3.2
	 */
	 public void updateScriptsMenu(){
		 menubar.updateScriptsMenu();
	 }

	/**
	 * Updates the "fragments" menu items
	 * @since jpicedt 1.3.2
	 */
	 public void updateFragmentsMenu(){
		 menubar.updateFragmentsMenu();
	 }

	/**
	 * update "undo" and "redo" text
	 * @since jpicedt 1.3.2
	 */
	 public void updateUndoRedoMenus(String undoName,String redoName){
		 menubar.updateUndoRedoMenus(undoName, redoName);
	 }

	/**
	 * Updates the Window menu as soon as an internal frame is opened or closed
	 * (this menu keeps tracks of every open internal frames so that the user can activate
	 * them quickly by use of a shortcut)
	 *
	 * @since PicEdt 1.1
	 */
	 public void updateWindowMenu() throws MissingResourceException {
		 menubar.updateWindowMenu();
	 }

	/**
	 * update currently "selected" menuitem in Toolkit menu
	 */
	 public void updateToolkitMenu(PropertyChangeEvent e){
		 menubar.propertyChange(e);
	 }

	/**
	 * Return an array containing all the menu-items components attached to menubars
	 */
	 public JMenuItem[] getMenuItems(){
		 return menubar.getMenuItems();
	 }


	//////////////////////////////////////////////////////////////////////
	//// BOARDS MANAGEMENT
	//////////////////////////////////////////////////////////////////////

	/**
	 * Returns the currently active drawing board ;
	 * null if there's no internal frame in the <code>desktopPane</code>.<br>
	 * If there's at least one internal frame in the desktop, but it's deactivated
	 * (this should never happen however), then the policy is to arbitrarly activate one frame
	 * (namely the topmost frame on the desktop which is also the last one in the framearray)
	 */
	public PEDrawingBoard getActiveDrawingBoard(){

		return this.activeBoard; // [pending] move to superclass
		/**
		JInternalFrame jifr = desktopPane.getSelectedFrame(); // returns null if there's no frame or if none is activated
		if (jifr != null){ // there's an active frame
			InternalMDIComponent ifr = (InternalMDIComponent)jifr;
			JPanel p = ifr.getInnerPane();
			if (p instanceof PEDrawingBoard) return (PEDrawingBoard)p;
			else return null;
		}
		// else either there's no frame, or none is active
		JInternalFrame[] allFrames = desktopPane.getAllFrames();
		if (allFrames.length==0) return null;
		// we have at least one frame in the desktop,
		// but none is activated, we activate (arbitrarly) one of them :
		//debug("NO ACTIVE FRAME ! Activating top-most..");
		InternalMDIComponent ifr = (InternalMDIComponent)allFrames[allFrames.length-1]; // top-most frame
		if (ifr.isIcon()) desktopManager.deiconifyFrame(ifr);
		desktopManager.activateFrame(ifr); // same as if user clicked on the title bar
		//ifr.show();
		JPanel p = ifr.getInnerPane();
		if (p instanceof PEDrawingBoard) return (PEDrawingBoard)p;
		else return null;
		*/

	}

	/**
	 * selects the given drawing board by bringing it to front.
	 */
	public void selectDrawingBoard(PEDrawingBoard board){

		JInternalFrame iFrame = (JInternalFrame)getHostingContainer(board);
		if (iFrame == null) return;
		if (iFrame.isIcon()) desktopManager.deiconifyFrame(iFrame);
		desktopManager.activateFrame(iFrame); // simulate a user click on the title bar
	}

	//////////////////////////////////////////////////////////////////////
	//// DOCKABLE PANES
	//////////////////////////////////////////////////////////////////////

	/**
	 * Adds the given pane to the hashtable of dockable panels, then shows it.  If "pane" is an instance of
	 * <code>SelectionListener</code> (resp. <code>PropertyChangeListener</code>), it will be notified
	 * selection (resp. propery-change) events from any currently opened drawing board.
	 * @param key used to retrieve geometrical properties from the <code>JPicEdt</code>'s preferences, i.e.
	 *        "ui." + key + ".visible" for the visible state at init time.
	 * @return the <code>DockablePanel</code>, or null if it was already registered.
	 */
	public DockablePanel addDockablePanel(String key, JPanel pane){
		DockablePanel dp = super.addDockablePanel(key,pane);
		JInternalFrame c = (JInternalFrame)dp.getMDIDelegate();
		desktopPane.setLayer(c,JLayeredPane.PALETTE_LAYER);
		return dp;
	}

	///////////////////////////////////////////////////////////////////////
	/////// MODAL DIALOG BOXES
	///////////////////////////////////////////////////////////////////////
	/**
	 * Wraps the given component in a <code>PEInternalDialog</code>, and
	 * makes it visible.
	 */
	 public MDIComponent createDialog(String title,boolean modal, JComponent p){
		 PEInternalDialog mif = new PEInternalDialog(desktopPane, title, modal, p);
		 // layering handled in addMDIComponent
		 return mif;
	 }

	/**
	 * Creates a new dialog box from the given array of <code>AbstractCustomizer</code>'s, laying them out in
	 * a <code>JTabbedPane</code>.<br> By default, clicking the OK button does not close this dialog box, this
	 * must be set separately by invoking {@link
	 * jpicedt.graphic.toolkit.CustomizerDialog#setOkButtonClosesDialog(boolean b)
	 * CustomizerDialog.setOkButtonClosesDialog}.
	 * @param title the dialog title ; if null, the title of the first customizer is used.
	 * @param selected index of the selected customizer on start-up
	 * @param buttonMask buttons to be displayed : a mask computed from predefinite masks OR'd together
	 */
	 public CustomizerDialog createCustomizerDialog(ArrayList<AbstractCustomizer> customizers, int selected, String title, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonMask){
		 PEInternalDialog dlg = new PEInternalDialog(desktopPane, title, modal, null); // no inner pane
		 CustomizerDialog cd = new CustomizerDialog(dlg,customizers, selected,title, buttonMask);
		 // useless addMDIComponent(dlg);
		 return cd;
	 }

	/**
	 * Builds a new dialog box from a single customizer.
	 */
	 public CustomizerDialog createCustomizerDialog(AbstractCustomizer customizer, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonMask){
		 PEInternalDialog dlg = new PEInternalDialog(desktopPane, "", modal, null); // no title, no inner pane
		 CustomizerDialog cd = new CustomizerDialog(dlg, customizer, buttonMask);
		 // useless addMDIComponent(dlg);
		 return cd;
	 }

	 public void showMessageDialog(Object message, String title, int messageType){
		 JOptionPane.showInternalMessageDialog(desktopPane, message, title, messageType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType){
		return JOptionPane.showInternalConfirmDialog(desktopPane, message, title, optionType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType, int msgType){
		return JOptionPane.showInternalConfirmDialog(desktopPane, message, title, optionType, msgType);
	 }

	 public String showInputDialog(Object message, String title, int messageType){
	 	return JOptionPane.showInternalInputDialog(desktopPane, message, title, messageType);
	 }

	 public String showInputDialog(Object message, String title, int messageType, String initialValue){
	 	return (String)JOptionPane.showInternalInputDialog(desktopPane, message, title, messageType,null, null, initialValue);
	 }

	public Object showInputDialog(Object message, String title, int messageType,
								  Object[] choices, Object initialChoice){
		return JOptionPane.showInternalInputDialog(desktopPane,  message, title, messageType, null,
												   choices, initialChoice);
	}



	//////////////////////////////////////////////////////////////////////
	//// GEOMETRY
	//////////////////////////////////////////////////////////////////////

	/**
	 * Cascade all open internal frames
	 */
	public void cascadeDrawingBoards(){

		ArrayList nonIconFrames = new ArrayList();

		int counter=0;
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent container = getHostingContainer(bd);
			if (!(container instanceof JInternalFrame)) continue;
			JInternalFrame ifr = (JInternalFrame)container;
			//if (ifr.isIcon()) desktopManager.deiconifyFrame(ifr);
			if (ifr.isIcon()) continue;
			try {
				ifr.setMaximum(false); // in case this one was Maximized (may fire a PropertyVetoException)
				ifr.pack();
				//ifr.show(); // already visible !
				//int dist = ifr.getHeight() - ifr.getContentPane().getHeight(); // title bar height
				int dist=30;
				int x = counter * dist;
				if (x + ifr.getWidth() > getDesktopSize().width) x = 0; // wrap around at desktop edge
				int y = counter * dist;
				if (y + ifr.getHeight() > getDesktopSize().height) y = 0; // wrap around at desktop edge
				ifr.setLocation(x,y);
				counter++;
			}
		catch (PropertyVetoException ex){}
		}
	}

	/**
	 * Tiles all open internal frames horizontally
	 */
	public void tileDrawingBoardsHorizontally(){

		ArrayList<JInternalFrame> nonIconFrames = new ArrayList<JInternalFrame>();
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent container = getHostingContainer(bd);
			if (!(container instanceof JInternalFrame)) continue;
			JInternalFrame jif = (JInternalFrame)container;
			if (jif.isIcon()) {
				try {
					jif.setMaximum(false); // (may fire a PropertyVetoException)
					nonIconFrames.add(jif);
				}
				catch (PropertyVetoException ex){}
			}
		}
		int nonIconNb = nonIconFrames.size();
		if (nonIconNb == 0) return;

		// number of columns = int(sqrt(N))
		int colNb = (int)Math.floor(Math.sqrt(nonIconNb));
		int colWidth = getDesktopSize().width / colNb;

		int remaining = nonIconNb;

		for(int col = 0; col < colNb; col++){

			int numberOfFrameInThisColumn = (int)Math.floor(remaining / (colNb - col));
			int rowHeight = getDesktopSize().height / numberOfFrameInThisColumn;

			for (int i = 0; i < numberOfFrameInThisColumn; i++){
				JInternalFrame ifr = (JInternalFrame)nonIconFrames.get(nonIconNb-remaining);
				ifr.reshape(col*colWidth, i * rowHeight , colWidth, rowHeight);
				remaining--;
			}
		}
	}

	/**
	 * Tiles all open internal frames vertically.
	 */
	public void tileDrawingBoardsVertically(){

		ArrayList<JInternalFrame> nonIconFrames = new ArrayList<JInternalFrame>();
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent container = getHostingContainer(bd);
			if (!(container instanceof JInternalFrame)) continue;
			JInternalFrame jif = (JInternalFrame)container;
			if (!jif.isIcon()) {
				try {
					jif.setMaximum(false); // (may fire a PropertyVetoException)
					nonIconFrames.add(jif);
				}
				catch (PropertyVetoException ex){}
			}
		}
		int nonIconNb = nonIconFrames.size();
		if (nonIconNb == 0) return;

		// number of row = int(sqrt(N)) where N = number of non-iconified frame
		int rowNb = (int)Math.floor(Math.sqrt(nonIconNb));
		int rowHeight = getDesktopSize().height / rowNb;
		int remaining = nonIconNb;

		for(int rowIndex = 0; rowIndex < rowNb; rowIndex++){

			int numberOfFrameInThisRow = (int)Math.floor(remaining / (rowNb - rowIndex));
			int colWidth = getDesktopSize().width / numberOfFrameInThisRow;

			for (int i = 0; i < numberOfFrameInThisRow; i++){
				JInternalFrame ifr = (JInternalFrame)nonIconFrames.get(nonIconNb-remaining);
				ifr.reshape(i*colWidth, rowIndex * rowHeight , colWidth, rowHeight);
				remaining--;
			}
		}
	}


	////////////////////////////////////////////////////
	/// MDIComponent -> INTERNAL FRAME
	/////////////////////////////////////////////////////

	/** simple adapter for <code>JInternalFrame</code> so that they implement the <code>MDIComponent</code>
	 * interface */
	private class InternalMDIComponent extends PEInternalFrame {

		InternalMDIComponent(JComponent pane){
			super(null, true, true, true, true); // no title
			//this.getContentPane().add(pane);
			this.setContentPane(pane); // [underway: need test]
		}

		public void setSelected(boolean b) throws PropertyVetoException {
			//Log.debug("title="+getTitle()+", sel="+b);
			//if (!(getContentPane() instanceof PEDrawingBoard))  b=false; // never select sth else than a PEDrawingBoard
			super.setSelected(b);
		}

		// all other methods of the MDIComponent interface are already implemented by JInternalFrame...

	}

	////////////////////////////////////////////////////
	/// DESKTOP MANAGER
	/////////////////////////////////////////////////////

	/**
	 * Handler for internal frame events.
	 * @since jPicEdt 1.4
	 */
	private class InternalFrameEventHandler extends InternalFrameAdapter {

		/**
		 * Updates "activeBoard".
		 * (note that this method is invoked AFTER <code>JDesktopManager.activeFrame</code>)
		 */
		public void internalFrameActivated(InternalFrameEvent e){
			//debug(e.getInternalFrame().getTitle() + " got activated...");

			JInternalFrame jif = e.getInternalFrame();
			if (jif.getContentPane() instanceof PEDrawingBoard){
				PEDrawingBoard board = (PEDrawingBoard)jif.getContentPane();
				activeBoard = board;
				//actionRegistry.updateActionState();

				PropertyChangeEvent propevt = new PropertyChangeEvent(this,ACTIVE_BOARD_CHANGE, null, board);
				boardEventHandler.propertyChange(propevt);
			}

			/*
			EditorKit kit = board.getCanvas().getEditorKit();
			propevt = new PropertyChangeEvent(kit,EditorKit.EDIT_MODE_CHANGE, null, kit.getCurrentMouseTool());
			boardEventHandler.propertyChange(propevt);
			*/

		}

		/**
		 * Try to save the content of the board if it's dirty before it gets closed.
		 * Also updates the widget states (menubar and toolbars).
		 * (note that this method is invoked BEFORE JDesktopManager.closeFrame)
		 */
		public void internalFrameClosing(InternalFrameEvent e){
			//Log.debug(e.getInternalFrame().getTitle() + " is closing...");

			JInternalFrame jif = e.getInternalFrame();
			//boolean wasSelected = jif.isSelected(); // remember its selected state for later
			if (jif.getContentPane() instanceof PEDrawingBoard){
				PEDrawingBoard board = (PEDrawingBoard)jif.getContentPane();
				if (removeDrawingBoard(board)){ // cancelled ?
					jif.dispose(); // indirectly invokes InternalDesktopManager.closeFrame(f), and sets up a new active board
					// update GUI
					updateWindowMenu(); // it's fine because board was just removed from the hash
					// update Action's :
					JPicEdt.getActionRegistry().updateActionState();
					updateMenuBar();
				}
			}
		}
	}

	/**
	 * Handler for desktop events. This work in conjunction with <code>InternalFrameEventHandler</code>, since
	 * there are subtle chronology issues between incoming events ;-)
	 */
	private class InternalDesktopManager extends DefaultDesktopManager {

		/**
		 * Called when a frame gets activated, and before
		 * <code>InternalFrameEventHandler.internalFrameActivated()</code> is invoked.  Enforces the selected
		 * state of the given frame (this is a work-around to the presence of DockablePanel's in the same
		 * layer).
		 */
		public void activateFrame(JInternalFrame f) {
			//debug("iframe="+f.getTitle());
			super.activateFrame(f);
			if (f.getContentPane() instanceof PEDrawingBoard){
				try {f.setSelected(true);} // simulated a click on the title bar; no risk of recursion, since this has no effect on an already selected frame
				catch(PropertyVetoException e2) {}
			}
		}

		/**
		 * called when a frame gets closed, and AFTER
		 * <code>InternalFrameEventHandler.internalFrameClosing()</code> Overriden so as to update
		 * "activeBoard".
		 * @param f frame getting closed
		 */
		public void closeFrame(JInternalFrame f){
			//debug("iframe="+f.getTitle());
			boolean wasSelected = f.isSelected(); // remember its selected state for later	
			
			 // closeFrame() sometimes tries to activate a DockablePanel (which is not what we want),
			if (f.getContentPane() instanceof PEDrawingBoard){
				super.closeFrame(f);
				if (wasSelected) {
					activeBoard = activateNextBoard(); // ... so we gotta do the job ourselves...
					PropertyChangeEvent propevt = new PropertyChangeEvent(this,ACTIVE_BOARD_CHANGE, null, activeBoard);
					boardEventHandler.propertyChange(propevt);
				}
			}
			else {
				String dialogKey = getDialogKey(f);
				if(dialogKey !=  null) {
					JPicEdt.getMDIManager().toggleDockablePanel(dialogKey);
				}
			}
		}

		private PEDrawingBoard activateNextBoard() { // copied from DefaultDesktopManager with minor changes...
			int i;
			InternalMDIComponent nextFrame = null;
			for (Component comp: desktopPane.getComponents()){
				if (comp instanceof InternalMDIComponent){
					InternalMDIComponent jif = (InternalMDIComponent)comp;
					if (jif.getContentPane() instanceof PEDrawingBoard){ // skip non drawing boards...
						nextFrame = jif;
						break;
					}
				}
			}
			if (nextFrame != null) {
				try { nextFrame.setSelected(true); }
				catch (PropertyVetoException e2) { }
				nextFrame.moveToFront();
				return (PEDrawingBoard)nextFrame.getContentPane();
			}
			return null;
		}

		/**
		 * used for debugging purpose
		 */
		private String printAllFrames(){

			JInternalFrame[] jif = desktopPane.getAllFrames();
			String s = "          Desktop contains : ";
			for (int i = 0; i < jif.length; i++){
				s += jif[i].getTitle() + "; ";
			}
			return s;
		}
		
		/**
		 * uses reflection to get Panel key
		 */
		private String getDialogKey(JInternalFrame f) {
			String dialogKey = null;
			try {
				Field field = f.getContentPane().getClass().getDeclaredField("KEY");
				dialogKey = field.get(String.class).toString();
			} 
			catch(Exception ex) {
				dialogKey = null;
			}
			return dialogKey;
		}

	} // InternalDesktopManager

} // class
