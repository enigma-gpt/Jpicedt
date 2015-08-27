// JFrameMDIManager.java --- -*- coding: iso-8859-1 -*-
// January 2, 2005 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: JFrameMDIManager.java,v 1.16 2013/03/27 06:51:16 vincentb1 Exp $
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

import jpicedt.*;

import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.toolkit.PEToolBar;
import jpicedt.graphic.toolkit.PopupMenuFactory;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.toolkit.CustomizerDialog;

import jpicedt.ui.*;
import jpicedt.ui.util.*;
import jpicedt.ui.action.*;
import jpicedt.ui.dialog.*;

import jpicedt.widgets.MDIComponent;
import jpicedt.widgets.PEDialog;
import jpicedt.widgets.PEFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.undo.*;
import javax.swing.event.*;

/**
 * An MDI Manager implementation for <code>JFrame</code>'s
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: JFrameMDIManager.java,v 1.16 2013/03/27 06:51:16 vincentb1 Exp $
 */
public class JFrameMDIManager extends MDIManager {

	private PopupMenuFactory popupMenuFactory;
	private JFrameEventHandler jFrameHandler; // window listener

	public String getName(){
		return MDI_STANDALONEFRAMES;
	}

	/**
	 * @param progressBar The progress bar to inform of progress in the initialization ; can be null.
	 */
	public JFrameMDIManager(PEProgressBar progressBar){

		// init LAF
		jpicedt.ui.LAFManager.updateLaf();

		// add a PropertyChangeListener to the UIManager, so that
		// every change in LAF leads to an immediate repainting of
		// the whole component tree
		UIManager.addPropertyChangeListener(new PropertyChangeListener(){
			                                    public void propertyChange(PropertyChangeEvent e) {
				                                    if (e.getPropertyName().equals("lookAndFeel")) {
														Frame[] allFrames = Frame.getFrames();
														for (int i=0; i<allFrames.length; i++){
															Frame f = allFrames[i];
															if (!(f instanceof JFrame)) continue;
															JFrame jf = (JFrame)f;
															if (jf.isVisible()){
																JComponent c = (JComponent)jf.getRootPane();
																SwingUtilities.updateComponentTreeUI(c);
																c.invalidate();	c.validate(); c.repaint();
															}
														}
				                                    }}});

		jFrameHandler = new JFrameEventHandler();

		// init colours from pref.
		update();
		if (progressBar != null) {
			progressBar.increment("GUI created successfully !");
		}
	}


	/**
	 * Factory method that creates an MDIComponent which acts as a UI delegate to the given pane.
	 * This implementation returns a properly init'd <code>JFrame</code>, with the given pane
	 * added to its content-pane.
	 */
	public MDIComponent createMDIComponent(JComponent pane){
		return new JFrameMDIComponent(pane);
	}

	/**
	 * Add the given component to the desktop
	 */
	public void addMDIComponent(MDIComponent c){
		Container innerPane = c.getContentPane();
		if (innerPane instanceof PEDrawingBoard) { // add PEMenuBar and PEToolBar if it's a PEDrawingBoard
			JFrame jf = (JFrame)c;
			try {
				jf.addWindowListener(jFrameHandler);
				jf.setJMenuBar(new PEMenuBar());
				PEToolBar ptb = new PEToolBar();
				ptb.setOrientation(ptb.VERTICAL);
				jf.getContentPane().add(ptb, BorderLayout.WEST); // [pending] bug Aug' 06 cause the ContentPane is a PEDrawingBoard!
			}
			catch(MissingResourceException e){
				System.err.println("During GUI initialization, I was unable to find internationalization resource: " + e.getKey());
				System.err.println("JVM stack trace dump :");
				e.printStackTrace();
				System.err.println("In any case, you may need to check the integrity of the jpicedt.jar file you downloaded.");
				System.err.println("If it still doesn't work, please send a bug report to : syd@jpicedt.org, with this console dump included.");
				System.err.println(":-((((((((((  Exiting... sorry !");
				SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD); // close any open File Stream
				System.exit(0);
			}
			catch (Exception e) {
				System.err.println("OK, there SURE is a bug somewhere... Thank you for sending a bug report including the following stack trace to syd@jpicedt.org");
				System.err.println("Stack trace dump starts here :");
				e.printStackTrace();
				System.err.println(":-((((((((((  Exiting...sorry !");
				SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD);// close any open File Stream
				System.exit(0);
			}
		}
		// else nothing specific to be done here
	}

	public void closeBoard(PEDrawingBoard board){
		JFrameMDIComponent mdiComp = (JFrameMDIComponent)getHostingContainer(board);
		if (mdiComp==null) return;
		// simulate a click on the "close" button, it's safe because it's gives us a unified way of closing boards
		WindowEvent we = new WindowEvent(mdiComp,WindowEvent.WINDOW_CLOSING);
		jFrameHandler.windowClosing(we);
	}

	/**
	 * Returns the dimension of the desktop that hosts the components of this MDIManager.
	 * @return the screen size
	 */
	public Dimension getDesktopSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize;
	}

	//public void saveGeometry(){

	//public void update(){

	//////////////////////////////////////////////////////////////////////
	//// GUI COMPONENTS
	//////////////////////////////////////////////////////////////////////

	/**
	 * Update JMenuItem's accelerators from the key/value pairs stored in JPicEdt's preferences
	 */
	public void updateAccelerators(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateAccelerators();
		}
	}

	/**
	 * Update the content (i.e. PEMenu's) of the menu-bar depending on the current state of the MDIManager.
	 */
	 public void updateMenuBar(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateMenuBar();
		}
	 }

	/**
	 * Updates the "recent files" sub-menu items after a frame was saved or opened.
	 * @since PicEdt 1.1
	 */
	 public void updateRecentFilesSubmenu(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateRecentFilesSubmenu();
		}
	 }

	/**
	 * Updates the "script" menu items
	 * @since jpicedt 1.3.2
	 */
	 public void updateScriptsMenu(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateScriptsMenu();
		}
	 }

	/**
	 * Updates the "fragments" menu items
	 * @since jpicedt 1.3.2
	 */
	 public void updateFragmentsMenu(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateFragmentsMenu();
		}
	 }

	/**
	 * update "undo" and "redo" text
	 * @since jpicedt 1.3.2
	 */
	 public void updateUndoRedoMenus(String undoName,String redoName){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateUndoRedoMenus(undoName, redoName);
		}
	 }

	/**
	 * Updates the Window menu as soon as an internal frame is opened or closed
	 * (this menu keeps tracks of every open internal frames so that the user can activate
	 * them quickly by use of a shortcut)
	 *
	 * @since PicEdt 1.1
	 */
	 public void updateWindowMenu() throws MissingResourceException {
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.updateWindowMenu();
		}
	 }

	/**
	 * update currently "selected" menuitem in Toolkit menu
	 */
	 public void updateToolkitMenu(PropertyChangeEvent e){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			JFrame f = (JFrame)getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) bar.propertyChange(e);
		}
	 }

	/**
	 * Return an array containing all the menu-items components attached to menubars
	 */
	 public JMenuItem[] getMenuItems(){
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent f = getHostingContainer(bd);
			PEMenuBar bar = (PEMenuBar)f.getJMenuBar();
			if (bar != null) return bar.getMenuItems();
		}
		return null;
	 }


	//////////////////////////////////////////////////////////////////////
	//// BOARDS MANAGEMENT
	//////////////////////////////////////////////////////////////////////

	/**
	 * Returns the currently active drawing board ;
	 * null if there's no frame hosting a <code>PEDrawingBoard</code> in the desktop.<br>
	 */
	public PEDrawingBoard getActiveDrawingBoard(){
		return this.activeBoard; // [pending] move to superclass
	}

	/**
	 * selects the given drawing board by bringing it to front.
	 */
	public void selectDrawingBoard(PEDrawingBoard board){

		JFrame iFrame = (JFrame)getHostingContainer(board);
		if (iFrame == null) return;
		iFrame.toFront();
	}

	///////////////////////////////////////////////////////////////////////
	/////// MODAL DIALOG BOXES
	///////////////////////////////////////////////////////////////////////
	/**
	 * Wraps the given component in a JDialog, and
	 * makes it visible.
	 */
	 public MDIComponent createDialog(String title, boolean modal, JComponent p){
		 PEDialog jd = new PEDialog(getActiveRootPane(), title, modal, p);
		 jd.pack();
		 return jd;
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
		 PEDialog host = new PEDialog(getActiveRootPane(), title, modal, null); // no inner pane
		 return new CustomizerDialog(host,customizers, selected,title, buttonMask);
	 }

	/**
	 * Builds a new dialog box from a single customizer.
	 */
	 public CustomizerDialog createCustomizerDialog(AbstractCustomizer customizer, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonMask){
		PEDialog host = new PEDialog(getActiveRootPane(), "", modal, null); // no inner pane
		return new CustomizerDialog(host, customizer, buttonMask);
	 }

	 public void showMessageDialog(Object message, String title, int messageType){
		 JOptionPane.showMessageDialog(getActiveRootPane(), message, title, messageType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType){
		return JOptionPane.showConfirmDialog(getActiveRootPane(), message, title, optionType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType, int msgType){
		return JOptionPane.showConfirmDialog(getActiveRootPane(), message, title, optionType, msgType);
	 }

	 public String showInputDialog(Object message, String title, int messageType){
	 	return JOptionPane.showInputDialog(getActiveRootPane(), message, title, messageType);
	 }

	 public String showInputDialog(Object message, String title, int messageType, String initialValue){
	 	return (String)JOptionPane.showInputDialog(getActiveRootPane(), message, title, messageType,null, null, initialValue);
	 }

	public Object showInputDialog(Object message, String title, int messageType,
								  Object[] choices, Object initialChoice){
		return JOptionPane.showInputDialog(getActiveRootPane(), message, title, messageType, null,
												   choices, initialChoice);
	}


	 // return the component hosting the currently active board
	 private Frame getActiveRootPane(){
		PEDrawingBoard bd=getActiveDrawingBoard();
		if (bd!=null){
			MDIComponent mdi = getHostingContainer(bd);
			if (mdi instanceof Frame)
				return (Frame)mdi;
		}
		return null;
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
			if (!(container instanceof JFrame)) continue;
			JFrame ifr = (JFrame)container;
			if (ifr.getExtendedState()==Frame.ICONIFIED) continue;
			ifr.setExtendedState(Frame.NORMAL); // in case this one was Maximized (may fire a PropertyVetoException)
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
	}

	/**
	 * Tiles all open internal frames horizontally
	 */
	public void tileDrawingBoardsHorizontally(){

		ArrayList<JFrame> nonIconFrames = new ArrayList<JFrame>();
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent container = getHostingContainer(bd);
			if (!(container instanceof JFrame)) continue;
			JFrame jif = (JFrame)container;
			if (jif.getExtendedState()==Frame.ICONIFIED) {
				jif.setExtendedState(Frame.NORMAL);
				nonIconFrames.add(jif);
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
				JFrame ifr = (JFrame)nonIconFrames.get(nonIconNb-remaining);
				ifr.setBounds(col*colWidth, i * rowHeight , colWidth, rowHeight);
				remaining--;
			}
		}
	}

	/**
	 * Tiles all open internal frames vertically.
	 */
	public void tileDrawingBoardsVertically(){

		ArrayList<JFrame> nonIconFrames = new ArrayList<JFrame>();
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			MDIComponent container = getHostingContainer(bd);
			if (!(container instanceof JFrame)) continue;
			JFrame jif = (JFrame)container;
			if (jif.getExtendedState()==Frame.ICONIFIED) {
				jif.setExtendedState(Frame.NORMAL);
				nonIconFrames.add(jif);
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
				JFrame ifr = (JFrame)nonIconFrames.get(nonIconNb-remaining);
				ifr.setBounds(i*colWidth, rowIndex * rowHeight , colWidth, rowHeight);
				remaining--;
			}
		}
	}


	////////////////////////////////////////////////////
	/// MDIComponent -> STAND-ALONE FRAME
	/////////////////////////////////////////////////////

	/** simple adapter for <code>JFrame</code> so that they implement the <code>MDIComponent</code> interface */
	private class JFrameMDIComponent extends PEFrame {

		JFrameMDIComponent(JComponent pane){
			super(pane);
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

			PEToolKit.setAppIconToDefault(this);
		}
	}

	////////////////////////////////////////////////////
	/// DESKTOP MANAGER
	/////////////////////////////////////////////////////

	/**
	 * Handler for <code>JFrame</code> window-events.
	 * @since jPicEdt 1.4
	 */
	private class JFrameEventHandler extends WindowAdapter {

		/**
		 * Updates "activeBoard".
		 */
		public void windowActivated(WindowEvent e){
			Window w = e.getWindow();
			if (w instanceof RootPaneContainer){
				//Log.debug(f.getTitle() + " got activated...");
				RootPaneContainer rpc = (RootPaneContainer)w;
				if (rpc.getContentPane() instanceof PEDrawingBoard){
					PEDrawingBoard board = (PEDrawingBoard)rpc.getContentPane();
					activeBoard = board;
					//actionRegistry.updateActionState();

					PropertyChangeEvent propevt = new PropertyChangeEvent(this,ACTIVE_BOARD_CHANGE, null, board);
					boardEventHandler.propertyChange(propevt);

					/*
					EditorKit kit = board.getCanvas().getEditorKit();
					propevt = new PropertyChangeEvent(kit,EditorKit.EDIT_MODE_CHANGE, null, kit.getCurrentMouseTool());
					boardEventHandler.propertyChange(propevt);
					*/
				}
			}

		}

		/**
		 * Try to save the content of the board if it's dirty before it gets closed.
		 * Also updates the widget states (menubar and toolbars).
		 * (note that this method is invoked BEFORE JDesktopManager.closeFrame)
		 */
		public void windowClosing(WindowEvent e){
			Window w = e.getWindow();
			if (w instanceof RootPaneContainer){
				RootPaneContainer rpc = (RootPaneContainer)w;
				//Log.debug(f.getTitle() + " is closing...");
				if (!(rpc.getContentPane() instanceof PEDrawingBoard)) return;
				PEDrawingBoard bd = (PEDrawingBoard)rpc.getContentPane();
				if (removeDrawingBoard(bd)){ // try to save content (returns false if cancelled)
					// test if no more open board in which case we create a new empty board
					Set<PEDrawingBoard> allBoards = getAllDrawingBoards();
					if (allBoards.isEmpty()) JPicEdt.newBoard();

					w.dispose();

					activeBoard = activateNextBoard(); // ensure we activate a frame hosting a PEDrawingBoard!
					//if (activeBoard!=null)Log.debugAppendLn("New active board="+activeBoard.getTitle());
					//else Log.debugAppendLn("No active board anymore");
					PropertyChangeEvent propevt = new PropertyChangeEvent(this,ACTIVE_BOARD_CHANGE, null, activeBoard);
					boardEventHandler.propertyChange(propevt);

					// update GUI
					updateWindowMenu(); // it's fine because board was just removed from the hash

					// update Action's :
					JPicEdt.getActionRegistry().updateActionState();
					updateMenuBar();

				}
			}
		}

		private PEDrawingBoard activateNextBoard() { // copied from DefaultDesktopManager with minor changes...
			int i;
			for(PEDrawingBoard bd: getAllDrawingBoards()){
				PEDrawingBoard brd = bd;
				MDIComponent comp = getHostingContainer(brd);
				comp.toFront();
				return brd;
			}
			return null;
		}

		/**
		 * used for debugging purpose
		 */
		private String printAllFrames(){

			Frame[] jif = Frame.getFrames();
			String s = "          Desktop contains : ";
			for (int i = 0; i < jif.length; i++){
				Frame f = jif[i];
				if (f instanceof JFrame){
					s += "["+Integer.toString(i)+"]"+((JFrame)f).getTitle() + "; ";
				}
			}
			return s;
		}


	}

} // class JFrameMDIManager
