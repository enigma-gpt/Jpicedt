// MDIManager.java --- -*- coding: iso-8859-1 -*-
// January 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: MDIManager.java,v 1.30 2013/07/14 21:20:50 vincentb1 Exp $
// Keywords: MDI, Multiple Document Interface desktop manager
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
package jpicedt.ui;

import jpicedt.JPicEdt;
import jpicedt.MiscUtilities;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.event.SelectionEvent;
import jpicedt.graphic.event.SelectionListener;
import jpicedt.graphic.toolkit.DialogFactory;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.PopupMenuFactory;
import jpicedt.ui.dialog.YesNoAskMe;
import jpicedt.ui.internal.InternalFrameMDIManager;
import jpicedt.ui.internal.JFrameMDIManager;
import jpicedt.ui.internal.PEPopupMenuFactory;
import jpicedt.ui.util.PEProgressBar;
import jpicedt.widgets.MDIComponent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import static jpicedt.Localizer.*;

/**
 * An abstract class that serves as the superclass for <code>jPicEdt</code>'s Multiple Document Interface
 * desktop managers.
 * <p>
 * Concrete implementation know how to add or remove a document (e.g. a <code>PEDrawingBoard</code> or a
 * DockablePanel) to the MDI interface. Each document is actually contained in a <code>MDIComponent</code>
 * (i.e. either a <code>JFrame</code> or a <code>JInternalFrame</code>), which is created by the factory
 * method <code>createMDIComponent()</code>. The list of <code>MDIComponent</code> 's which currently belong
 * to the desktop is hold in separate hash maps, one for each family of documents.<br>
 * There are currently two kinds of documents supported:
 * <ul>
 * <li> <code>PEDrawingBoard</code>'s: they're directly wrapped into a <code>MDIComponent</code>. The
 *      "<code>boardMap</code>" hash map holds the list of boards belonging to the desktop, together with
 *      their UI delegate. The <code>addDrawingBoard()</code> and <code>removeDrawingBoard()</code> methods
 *      allows one to add or remove boards from the desktop. These methods wrap board into their UI delegate
 *      on-the-fly. Finally, the "<code>activeBoard</code>" variable maintains a reference on the currently
 *      active drawing board (or null if none is active and/or open).
 * <li> <code>DockablePanel</code>'s: there's one more layer in this case, as
 *         <ol>
 *         <li> we wish to remember UI properties (location, size, title,&hellip;)
 *      		associated with each panel
 *		   <li> this allows us to implement most methods related to <code>DockablePanel</code> management
 *      		directly in this class, instead of relying on concrete subclasses to do the job.
 *         </ol>
 *      Hence:
 *         <ol>
 *		   <li> there's a "<code>dockablePanelsMap</code>" which holds the list of
 *      		<code>DockablePanel</code>'s belonging to the desktop, together with a key which allows us to
 *      		retrieve these UI properties from <code>jPicEdt</code>'s properties.
 *		   <li> each <code>DockablePanel</code>, instead of being directly wrapped into an
 *              <code>MDIComponent</code>, rather holds a reference to it; this in particular allows us to
 *              create/hide/show/etc&hellip; <code>DockablePanel</code>'s directly in this class, since we do
 *              not need to rely on a particular implementation of <code>MDIComponent</code>.
 *         </ol>
 * </ul>
 *
 * @since jPicEdt 1.3
 * @author Sylvain Reynal
 * @version $Id: MDIManager.java,v 1.30 2013/07/14 21:20:50 vincentb1 Exp $
 */
public abstract class MDIManager implements DialogFactory {

	/** key for persistent storage */
	public static final String KEY_MDIMANAGER = "ui.mdimgr";
	/** value for persistent storage */
	public static final String MDI_CHILDFRAMES="child-frames";
	/** value for persistent storage */
	public static final String MDI_STANDALONEFRAMES="Gimp-like";


	/** <code>PropertyChangeEvent</code>'s name corresponding to a new board being activated */
	public static final String ACTIVE_BOARD_CHANGE = "active-board-change";
	/** <code>PropertyChangeEvent</code>'s name corresponding to a dockable panel change in visibility */
	public static final String DOCKABLE_PANEL_TOGGLE = "dockable-panel-toggle";

	/**
	 * a hash table used to remember dockable panels added to the desktop, with key=panel's title
	 */
	protected HashMap<String,DockablePanel> dockablePanelsMap = new HashMap<String,DockablePanel>();

	/**
	 * A hash table used to bind a <code>PEDrawingBoard</code> (the key) and its <code>MDIComponent</code>
	 * container (the value) This map plays pretty much the same role as <code>JDesktopPane</code>, but it's
	 * able to manage any desktop component implementing the <code>MDIComponent</code> interface,
	 * e.g. <code>JFrame</code>'s as well as <code>JInternalFrame</code>'s.  In particular, since this map
	 * holds the list of <code>PEDrawingBoard</code>'s together with their hosting <code>MDIComponent</code>,
	 * it's at the core of the desktop management, e.g. for adding or removing boards properly.
	 */
	protected HashMap<PEDrawingBoard,MDIComponent> boardMap = new HashMap<PEDrawingBoard,MDIComponent>(); // [SR:pending] use WeakHashMap since there are cross-references ?

	/**
	 * Delegate which notifies dockable panels when an event is triggered by a <code>PEDrawingBoard</code>
	 */
	protected BoardEventHandler boardEventHandler = new BoardEventHandler();

	/**
	 * Reference on the currently active board, or null if no board is currently open.  This reference is
	 * constantly being updated by <code>MDIDesktopManager</code> when a <code>MDIComponent</code> gets
	 * (de)activated, and it contains a <code>PEDrawingBoard</code>.
	 */
	protected PEDrawingBoard activeBoard=null;

	/////////////////////// utilities ///////////////////////

	/**
	 * Create an <code>MDIManager</code> according to the given value.
	 * @param value one of <code>MDI_CHILDFRAMES</code> or <code>MDI_STANDALONEFRAMES</code>
	 */
	public static MDIManager createMDIManager(String value, PEProgressBar progress){
		if (value.equals(MDI_CHILDFRAMES))
			return new InternalFrameMDIManager(progress);
		else
			return new JFrameMDIManager(progress);
	}

	public static String[] getInstalledMDIManagers(){
		return new String[]{MDI_CHILDFRAMES,MDI_STANDALONEFRAMES};
	}

	public static String getDefaultManagerName(){
		return MDI_CHILDFRAMES;
	}

	public abstract String getName();

	/**
	 * Factory method that creates a <code>MDIComponent</code> that acts as a wrapper for the given pane.
	 */
	public abstract MDIComponent createMDIComponent(JComponent pane);

	/**
 	 * Adds the given component to the desktop of this <code>MDIManager</code>.
	 */
	public abstract void addMDIComponent(MDIComponent c);

	// note: removing MDI components is handled directly by WindowListener/InternalFrameListener's.

	/**
	 * Returns the dimension of the desktop that hosts the components of this <code>MDIManager</code>.
	 * Depending on the implementation, this may be either the whole screen, or the size of a
	 * <code>JFrame</code> containing all the documents.
	 */
	public abstract Dimension getDesktopSize();

	////////////////////////////////////////////////////////////////////////////
	//// PREFERENCES
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates local properties from the <code>JPicEdt</code>'s preferences (e.g. GUI colours,
	 * rendering-hints,&hellip;) This implementation update the rendering hints of each currently opened
	 * <code>PEDrawingBoard</code>
	 */
	public void update(){
		// rendering hints :
		RenderingHints rh = MiscUtilities.parseRenderingHints(JPicEdt.getPreferences());
		for (PEDrawingBoard bd: getAllDrawingBoards()){
			bd.getCanvas().getRenderingHints().add(rh);
			bd.getCanvas().repaint();
		}
		// [underway]
		// updates colors (they might've been modified from inside Preference tabpane) :
		//BoardInternalFrame activeFrame = getActiveDrawingBoard();
		//if (activeFrame != null) activeFrame.getCanvas().repaint(); // Grid, text and bg colours may have changed
	}

	/**
	 * Saves UI geometry to <code>JPicEdt</code>'s preferences.<br>This implementation saves geometrical
	 * properties related to <code>DockablePanel</code> only.
	 * @see #addDockablePanel
	 * @see DockablePanel#setVisible
	 */
	public void saveGeometry(){
		// save dockable toolbars geometry :
		Properties preferences = JPicEdt.getPreferences();
		Set<String> keys = dockablePanelsMap.keySet();
		for (String key : keys){
			DockablePanel tif = dockablePanelsMap.get(key);
			if (tif == null) continue;
			Rectangle bounds = tif.getMDIDelegate().getBounds();
			preferences.setProperty("ui."+key+".x",Integer.toString(bounds.x));
			preferences.setProperty("ui."+key+".y",Integer.toString(bounds.y));
			preferences.setProperty("ui."+key+".width",Integer.toString(bounds.width));
			preferences.setProperty("ui."+key+".height",Integer.toString(bounds.height));
			preferences.setProperty("ui."+key+".visible",tif.getMDIDelegate().isVisible()?"true":"false");
		}

	}

	////////////////////////////////////////////////////////////////////////
	//// GUI components: status bar, menu bar, popup menu,...
	////////////////////////////////////////////////////////////////////////

	/**
	 * Requests that the argument string  be displayed in the status bar
	 */
	public void showStatus(String str){
		PEDrawingBoard b = getActiveDrawingBoard();
		if (b!=null) b.getStatusBar().showMessage(str);
	}

	/**
	 * Factory method to create a <code>PopupMenuFactory</code> for newly created board
	 */
	public PopupMenuFactory createPopupMenuFactory(){
		PopupMenuFactory p = new PEPopupMenuFactory();
		return p;
	}

	/**
	 * Update <code>JMenuItem</code>'s accelerators from the key/value pairs stored in <code>JPicEdt</code>'s
	 * preferences
	 */
	public abstract void updateAccelerators();

	/**
	 * Update the content (i.e. <code>PEMenu</code>'s) of the menu-bar depending on the current state of the
	 * <code>MDIManager</code>.
	 */
	public abstract void updateMenuBar();

	/**
	 * Updates the "recent files" sub-menu items after a frame was saved or opened.
	 * @since PicEdt 1.1
	 */
	public abstract void updateRecentFilesSubmenu();

	/**
	 * Updates the "script" menu items
	 * @since jpicedt 1.3.2
	 */
	public abstract void updateScriptsMenu();

	/**
	 * Updates the "fragments" menu items
	 * @since jpicedt 1.3.2
	 */
	public abstract void updateFragmentsMenu();

	/**
	 * update "undo" and "redo" text
	 * @since jpicedt 1.3.2
	 */
	public abstract void updateUndoRedoMenus(String undoName,String redoName);

	/**
	 * Updates the Window menu as soon as an internal frame is opened or closed
	 * (this menu keeps tracks of every open internal frames so that the user can activate
	 * them quickly by use of a shortcut)
	 *
	 * @since PicEdt 1.1
	 */
	public abstract void updateWindowMenu() throws MissingResourceException ;

	/**
	 * update currently "selected" menuitem in Toolkit menu
	 */
	public abstract void updateToolkitMenu(PropertyChangeEvent e);

	/**
	 * Return an array containing all the menu-items components attached to menubars
	 */
	public abstract JMenuItem[] getMenuItems();

	//////////////////////////////////////////////////////////////////////
	//// BOARDS MANAGEMENT
	//////////////////////////////////////////////////////////////////////

	/**
	 * Returns the MDIComponent that contains the given board
	 */
	public MDIComponent getHostingContainer(PEDrawingBoard board){
		return (MDIComponent)boardMap.get(board);
	}

	/**
	 * Sets the title of the frame/internal frame hosting the given board
	 * to the given string.
	 */
	public void setHostingContainerTitle(String title, PEDrawingBoard board){

		MDIComponent c = getHostingContainer(board);
		if (c==null) return; // no frame hosting this board
		c.setTitle(title);
	}

	/**
	 * Adds a new <code>DrawingBoard</code> component to the list of drawing boards. The given board gets
	 * wrapped into a <code>MDIComponent</code> beforehands.
	 */
	public void addDrawingBoard(PEDrawingBoard board){
		// !!! registering the selection-listener BEFORE adding the board to the DesktopPane allows dockable
		// toolbars (e.g. the attribute editor) which are instances of SelectionListener to be notified that a new board has been activated,
		// thus giving'em a chance to synchronize their state with that of the new active board.
		PECanvas canvas = board.getCanvas();
		EditorKit kit = canvas.getEditorKit();
		canvas.addSelectionListener(boardEventHandler);
		canvas.addUndoableEditListener(boardEventHandler);
		canvas.addPropertyChangeListener(boardEventHandler);
		kit.addPropertyChangeListener(boardEventHandler);
		board.setPopupMenuFactory(createPopupMenuFactory());
		kit.setDialogFactory(this);

		MDIComponent mdiComponent = createMDIComponent(board); // wrap "board" into a MDIComponent (e.g. a JInternalFrame)
		mdiComponent.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // the only way to close it is through a call to closeFrame()
		boardMap.put(board, mdiComponent); // remember which MDIComponent is associated with each board

		mdiComponent.setTitle(board.getTitle());
		mdiComponent.pack();
		addMDIComponent(mdiComponent);
		int nbFrame = getDrawingBoardCount();
		//int dist = f.getHeight() - f.getContentPane().getHeight(); // title bar height DOESN'T WORK !
		int dist=30;
		mdiComponent.setLocation(nbFrame * dist, nbFrame * dist);
		mdiComponent.setVisible(true);
		JPicEdt.getActionRegistry().updateActionState();
		updateMenuBar();
	}

	/**
	 * Ask the given board to close itself
	 */
	public abstract void closeBoard(PEDrawingBoard board);

	/**
	 * Remove the given drawing board from the list of open drawing boards, possibly attempting to save its
	 * content if necessary, then unregisters any previously registered listener.  Closing the associated UI
	 * (i.e. the <code>MDIComponent</code>) is the responsibility of the caller.
	 * @return false if the operation failed because the action was cancelled by the user
	 */
	protected boolean removeDrawingBoard(PEDrawingBoard board){
		// need saving ?
		if (board.isDirty()){
			int answer = showConfirmDialog(localize("msg.SaveChanges"), localize("action.ui.Close"), JOptionPane.YES_NO_CANCEL_OPTION);

			if (answer == JOptionPane.YES_OPTION) JPicEdt.saveBoard(false); // doesn't prompt user for file name if not necessary
			else if (answer == JOptionPane.CANCEL_OPTION) {
				//Log.debug("answer=cancel");
				return false;
			}
		}

		// remove listeners
		board.getCanvas().removeSelectionListener(boardEventHandler);
		board.getCanvas().removeUndoableEditListener(boardEventHandler);
		board.getCanvas().removePropertyChangeListener(boardEventHandler);
		board.getCanvas().getEditorKit().removePropertyChangeListener(boardEventHandler);

		// remove from hash map
		boardMap.remove(board); // remove cross-references (at least i hope so ;-)

		return true;
	}

	/**
	 * Returns all opened drawing boards
	 */
	public Set<PEDrawingBoard> getAllDrawingBoards(){
		return boardMap.keySet();
	}

	/**
	 * Selects the given drawing board ; concrete implementation should call <code>show()</code> or any
	 * similar method on the <code>MDIComponent</code> (aka container) hosting the given board.
	 */
	public abstract void selectDrawingBoard(PEDrawingBoard board);

	/**
	 * Returns the number of open boards
	 */
	public int getDrawingBoardCount(){
		return boardMap.size();
	}

	//////////////////////////////////////////////////////////////////////
	//// DESKTOP MANAGER
	//////////////////////////////////////////////////////////////////////

	/**
	 * Returns the currently active drawing board
	 */
	public abstract PEDrawingBoard getActiveDrawingBoard();
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
		//debug("key="+key+",pane="+pane+",map="+dockablePanelsMap);
		if (dockablePanelsMap.containsKey(key)) return null; // never add the same pane twice
		DockablePanel dp = new DockablePanel(pane,key);
		dockablePanelsMap.put(key,dp);
		// retrieve things saved last time by saveGeometry() :
		boolean vis = JPicEdt.getProperty("ui."+key+".visible",true);
		if (vis) showDockablePanel(key,true);
		return dp;
	}

	/**
	 * Toggle the visible state of the dockable pane with the given key.
	 */
	public void toggleDockablePanel(String key){
		//debug("key="+key+",vis="+visible+",map="+dockablePanelsMap);
		if (!dockablePanelsMap.containsKey(key)) return;
		DockablePanel dp = (DockablePanel)dockablePanelsMap.get(key);
		dp.toggleVisible();
	}

	/**
	 * Set the visible state of the dockable pane with the given key.
	 */
	public void showDockablePanel(String key,boolean visible){
		//debug("key="+key+",vis="+visible+",map="+dockablePanelsMap);
		if (!dockablePanelsMap.containsKey(key)) return;
		DockablePanel dp = (DockablePanel)dockablePanelsMap.get(key);
		dp.setVisible(visible);
	}

	/**
	 * Returns a set containing all keys (of class "<code>String</code>") that map to a
	 * <code>DockablePanel</code>
	 */
	public Set getDockablePanelKeys(){
		return dockablePanelsMap.keySet();
	}

	/**
	 * Return the inner pane contained in the dockable panel
	 * associated with the given key, or null if none match.
	 */
	public Container getDockablePanelFromKey(String key){
		DockablePanel dp = (DockablePanel)dockablePanelsMap.get(key);
		MDIComponent mc = dp.getMDIDelegate();
		return mc.getContentPane();
	}


	public void addDockablePanelPropertyChangeListener(PropertyChangeListener l){
		for(DockablePanel dp : dockablePanelsMap.values()){
			dp.addPropertyChangeListener(l);
		}
	}

	public void removeDockablePanelPropertyChangeListener(PropertyChangeListener l){
		for(DockablePanel dp : dockablePanelsMap.values()){
			dp.removePropertyChangeListener(l);
		}
	}

	/**
	 * A wrapper for dockable panels, e.g. the attribute editor or the toolkit palette.  The UI is rendered by
	 * a <code>MDIComponent</code> delegate (see {@link #getMDIDelegate getMDIDelegate}) that is created at
	 * init time by relying on the factory method {@link #createMDIComponent createMDIComponent}.
	 */
	public class DockablePanel {

		//private JPanel content;
		private boolean packed=false;
		private String key;
		private MDIComponent mdiComponent; // the UI delegate which renders this panel
		protected EventListenerList listenerList = new EventListenerList();


		public String getKey(){ return key; }

		/**
		 * ajoute un <code>PropertyChangeListener</code> ï¿½ la liste des auditeurs.
		 */
		public void addPropertyChangeListener(PropertyChangeListener l) {
			listenerList.add(PropertyChangeListener.class, l);
		}

		/**
		 * ï¿½te un <code>PropertyChangeListener</code> du <code>DockablePanel</code>.
		 */
		public void removePropertyChangeListener(PropertyChangeListener l) {
			listenerList.remove(PropertyChangeListener.class, l);
		}


		/**
		 * @param pane the content of this <code>DockablePanel</code>
		 * @param key used to set the title (after proper i18n'ing) of the <code>MDIComponent</code> which
		 *        renders the UI, and for building keys used to retrieve the geometry from
		 *        <code>JPicEdt</code>'s preferences.
		 */
		public DockablePanel(JPanel pane,String key){
			mdiComponent = createMDIComponent(pane);
			mdiComponent.setTitle(localize(key));
			//this.content = pane;
			this.key = key;
			addMDIComponent(mdiComponent);
		}

		/**
		 * returns the <code>MDIComponent</code> that renders the UI of this <code>DockablePanel</code>.
		 */
		public MDIComponent getMDIDelegate(){
			return mdiComponent;
		}

		/** returns true if this <code>DockablePanel</code> is currently visible
		public boolean isVisible(){
			return mdiComponent.isVisible();
		}*/

		public void toggleVisible(){
			boolean newVisibility = !mdiComponent.isVisible();
			setVisible(newVisibility);
			fireVisibilityChange(newVisibility);
		}


		void fireVisibilityChange(boolean newVisibility){
			PropertyChangeEvent e = null;
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==PropertyChangeListener.class) {
					// Lazily create the event:
					if (e == null)
						e = new PropertyChangeEvent(this,DOCKABLE_PANEL_TOGGLE,
													new Boolean(!newVisibility),new Boolean(newVisibility));
					((PropertyChangeListener)listeners[i+1]).propertyChange(e);
				}
			}
		}

		/**
		 * set the visible state of this <code>DockablePanel</code>, first trying to fetch the geometrical
		 * aspect from the Properties object given in the constructor. This is done on first call only (see
		 * "packed" flag).
		 */
		public void setVisible(boolean v){
			if (v==true && !packed && MDIManager.this!=null) {
				getMDIDelegate().pack();
				packed=true;
				// set default location :
				Dimension mainFrameSize = getDesktopSize();
				Dimension iframeSize = getMDIDelegate().getSize();
				// crop panel size to main frame size if necessary :
				if (iframeSize.height > mainFrameSize.height) iframeSize.height = mainFrameSize.height;
				if (iframeSize.width > mainFrameSize.width) iframeSize.width = mainFrameSize.width;
				int x = mainFrameSize.width - iframeSize.width;
				int y = (mainFrameSize.height - iframeSize.height) / 2;
				//debug(y="+y);
				getMDIDelegate().setLocation(x,y);
				// let's try to override it with preferences :
				x = JPicEdt.getProperty("ui."+key+".x",x);
				y = JPicEdt.getProperty("ui."+key+".y",y);
				int w = JPicEdt.getProperty("ui."+key+".width",getMDIDelegate().getSize().width);
				int h = JPicEdt.getProperty("ui."+key+".height",getMDIDelegate().getSize().height);
				// work-around for null dimension inadvertently saved in the preference file :
				if (w==0) w = getMDIDelegate().getSize().width; // i.e. as computed from pack()
				if (h==0) h = getMDIDelegate().getSize().height;
				// avoid panel sticking out of the main frame area :
				if (x+w > mainFrameSize.width) x = mainFrameSize.width - w;
				if (y+h > mainFrameSize.height) y = mainFrameSize.height - h;
				getMDIDelegate().reshape(x,y,w,h);
			}
			getMDIDelegate().setVisible(v);
			fireVisibilityChange(v);
		}

	}
	///////////////////////////////////////////////////////////////////////
	/////// MODAL DIALOG BOXES
	///////////////////////////////////////////////////////////////////////
	public int showDontAskMeAgainConfirmDialog(String message,String title,String dontAskMeAgainKey,
		int messageType){
		YesNoAskMe prop = JPicEdt.getProperty(dontAskMeAgainKey, YesNoAskMe.ASK_ME);
		if(prop == YesNoAskMe.ASK_ME){
			JCheckBox dontAskMeAgainButton= new JCheckBox(localize("button.dontAskMeAgain"));
			Object[] jOptionPaneMessage = { message, dontAskMeAgainButton};
			int ret = showConfirmDialog(jOptionPaneMessage, title,
									 JOptionPane.YES_NO_OPTION, messageType);
			if(dontAskMeAgainButton.isSelected()){
				prop = YesNoAskMe.jOptionPaneValueToYesNoAskMe(ret);
				JPicEdt.setProperty(dontAskMeAgainKey, prop);
			}
			return ret;
		}
		else
			return prop.getJOptionPaneValue();
	}


	//////////////////////////////////////////////////////////////////////
	//// EVENTS
	//////////////////////////////////////////////////////////////////////

	/**
	 * A handler for events triggered from any <code>PEDrawingBoard</code>, its <code>PECanvas</code> or its
	 * <code>EditorKit</code>.
	 */
	protected class BoardEventHandler implements SelectionListener, UndoableEditListener,
									  PropertyChangeListener {

		/**
		 * Handler for "Selection" events triggered e.g. by a click on a graphical object (from a given
		 * <code>PEDrawingBoard</code>)
		 * This takes the following actions:
		 * <ul>
		 * <li> marks the active drawing board as dirty and possibly adds a trailing "*" to the frame's title,
		 * <li> updates widgets states (ie menu items + toolbar buttons),
		 * <li> eventually notifies interested dockable panels (through the "dockablePanelsMap" hash table)
		 * 		that the state of the Drawing has changed (for instance, say there's a
		 *      graphical object filled in red and we just selected it &rarr; changes
		 * 		the current active fill colour to red in the attributes editor palette).
		 * </ul>
		 */
		public void selectionUpdate(SelectionEvent e){
			//debug("evt="+e);
			getActiveDrawingBoard().setDirty(true); // moved from PEDrawingBoard ; see note in "PEDrawingBoard.init()"
			JPicEdt.getActionRegistry().updateActionState();
			// update hosting frame title
			String title  = getActiveDrawingBoard().getTitle() + "* [" + getActiveDrawingBoard().getCanvas().getContentType().getPresentationName() + "]";
			setHostingContainerTitle(title,getActiveDrawingBoard());
			// notifies dockable panels (those which are instances of SelectionListener)
			// of possible changes in the active Drawing:
			Collection<DockablePanel> c = dockablePanelsMap.values();
			for (DockablePanel dp : c){
				Container p = dp.getMDIDelegate().getContentPane();
				if (p instanceof SelectionListener){
					((SelectionListener)p).selectionUpdate(e);
				}
			}
		}

		/**
		 * Handler for "UndoableEdit" events sourced by <code>PECanvas</code>. Updates undo/redo menu items
		 * and corresponding toolbar buttons.
		 */
		public void undoableEditHappened(UndoableEditEvent e){
			PEDrawingBoard board = getActiveDrawingBoard();
			if (board==null) return;
			String redoName = board.getCanvas().getRedoPresentationName();
			String undoName = board.getCanvas().getUndoPresentationName();
			updateUndoRedoMenus(undoName,redoName);
			JPicEdt.getActionRegistry().updateActionState();
		}

		/**
		 * Handler for "<code>PropertyChange</code>" events sources from either <code>PECanvas</code> or its
		 * <code>EditorKit</code>.  Simply forwards these events to all <code>DockablePanel</code>'s which
		 * implement the <code>PropertyChangeListener</code> interface, as well as to the attached
		 * <code>PEMenuBar</code>.
		 */
		public void propertyChange(PropertyChangeEvent e){
			//debug("evt="+e.getPropertyName()+":"+e.getOldValue()+"->"+e.getNewValue());
			Collection<DockablePanel> c = dockablePanelsMap.values();
			for (DockablePanel dp :  c){
				Container p = dp.getMDIDelegate().getContentPane();
				if (p instanceof PropertyChangeListener){
					((PropertyChangeListener)p).propertyChange(e);
				}
			}
			updateToolkitMenu(e); // update currently "selected" menuitem in Toolkit menu
			if(e.getPropertyName() == ACTIVE_BOARD_CHANGE)
				JPicEdt.getActionRegistry().updateActionState();
		}
	}

	//////////////////////////////////////////////////////////////////////
	//// GEOMETRY
	//////////////////////////////////////////////////////////////////////

	/**
	 * Rearranges all open drawing-boards by cascading them
	 */
	public abstract void cascadeDrawingBoards();

	/**
	 * Tiles all open drawing-boards horizontally
	 */
	public abstract void tileDrawingBoardsHorizontally();

	/**
	 * Tiles all open drawing-boards vertically
	 */
	public abstract void tileDrawingBoardsVertically();

} // class
