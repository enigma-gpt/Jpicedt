// PEMenuBar.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
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
// Version: $Id: PEMenuBar.java,v 1.26 2013/03/27 06:51:11 vincentb1 Exp $
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

import jpicedt.Localizer;
import jpicedt.JPicEdt;
import jpicedt.MiscUtilities;
import jpicedt.ui.util.*;
import jpicedt.ui.*;
import jpicedt.ui.action.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.toolkit.PEMenu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.beans.*;

/**
 * The main menu bar. Several <code>updateXXX()</code> methods are provided, which make it easy to keep the
 * GUI state synchronized with the current content of the desktop (in terms e.g. of saved/dirty boards,
 * &hellip;)
 * <p>
 * This menubar can listen to property-change events triggered e.g. from an activated
 * <code>PEDrawingBoard</code>.
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 * @version $Id: PEMenuBar.java,v 1.26 2013/03/27 06:51:11 vincentb1 Exp $
 */
public class PEMenuBar extends JMenuBar implements PropertyChangeListener {

	private ActionRegistry actionRegistry = JPicEdt.getActionRegistry();
	/** mutable menu &amp; menuitems */
	private JMenu submenuRecentFiles; // keep tracks of last 4 opened/saved files
	private PEMenu menuFile, menuEdit, menuTools, menuCommands, menuFragments, menuScripts, menuWindow, menuHelp;
	private JMenuItem undoMenuItem, redoMenuItem;
	private String undoMenuItemText, redoMenuItemText;


	// --- class methods ------------------------------------------------

	/**
	 * Creates the application main menu bar
	 */
	public PEMenuBar() throws MissingResourceException {

		ActionRegistry actionRegistry = JPicEdt.getActionRegistry();
		//System.out.println("PEMenuBar");

		PEMenu menu;
		JMenuItem menuItem;

		add(menuFile=createFileMenu());
		add(menuEdit=createEditMenu());
		add(menuTools=createToolsMenu()); // see DockableEditorKitToolbar to track changes !
		add(menuCommands=createCommandsMenu());
		add(menuScripts=createScriptsMenu());
		add(menuFragments=createFragmentsMenu());
		add(menuWindow=createWindowMenu());
		add(Box.createHorizontalGlue());
		add(menuHelp=createHelpMenu());

		updateAccelerators(); // set accelerators from the preferences file
		updateRecentFilesSubmenu();
		updateScriptsMenu();
		updateFragmentsMenu();
	}

	/**
	 * Creates a <code>PEMenu</code> from the given String, using <code>JPicEdt</code>'s default
	 * <code>ActionLocalizer</code> to fetch the associated label (ie "name") and mnemonic ("name.mnemonic").
	 * @param name the name of the menu, used to determine :
	 * Example : createPEMenu("File") create a menu with label "File" and mnemonic = first char of "File.mnemonic"
	 *
	 */
	public static PEMenu createPEMenu(String name) throws MissingResourceException {

		jpicedt.graphic.toolkit.ActionLocalizer localizer = jpicedt.Localizer.currentLocalizer().getActionLocalizer();
		PEMenu menu = new PEMenu(localizer.getActionName(name));
		menu.setMnemonic(localizer.getActionMnemonic(name).intValue());
		return menu;
	}

	/**
	 * Return an array containing all the menu-items components attached to this menubar
	 */
	public JMenuItem[] getMenuItems(){
		ArrayList<Component> list = new ArrayList<Component>();
		for (int i=0; i<this.getMenuCount(); i++){
			JMenu menu = this.getMenu(i);
			if (menu==null) continue; // hack for Box.createHorizontalGlue()
			//debug("menu("+i+")="+menu.getText());
			for (int j=0; j<menu.getMenuComponentCount(); j++){
				Component c = menu.getMenuComponent(j);
				if (c instanceof JMenu) continue; // get rid of JMenu
				if (c instanceof JMenuItem) {
					JMenuItem mi = (JMenuItem)c;
					Action a = mi.getAction();
					if (a==null) continue; // no action => actionlistener instead => hard to manage from a "shortcuts" point of view
					if (a.getValue(Action.ACTION_COMMAND_KEY)==null) continue; // ibid.
					list.add(c);
				}
			}
		}
		JMenuItem[] miArray = list.toArray(new JMenuItem[list.size()]);
		return miArray;
	}

	// //////////////////////////////////// DEFAULT MENUS CREATION ////////////////////////

	private PEMenu createFileMenu(){

		PEMenu menu = createPEMenu("menu.File");
		JMenuItem mi;

		mi=menu.add(actionRegistry.getAction(JPicEdt.FileNewAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileOpenAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileReloadAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileSaveAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileSaveAsAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileSaveAllAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.FragmentSaveAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.FragmentInsertAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		menu.add(this.submenuRecentFiles=(JMenu)createPEMenu("menu.RecentFiles"));
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.FileCloseAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.ExitAction.KEY)); mi.setIcon(null);
		return menu;
	}

	private PEMenu createEditMenu(){

		PEMenu menu = createPEMenu("menu.Edit");
		JMenuItem mi;

		this.undoMenuItem=menu.add(actionRegistry.getAction(JPicEdt.UndoAction.KEY));
		this.undoMenuItem.setIcon(null);
		this.undoMenuItemText = this.undoMenuItem.getText(); // ie properly i18n'd
		this.redoMenuItem=menu.add(actionRegistry.getAction(JPicEdt.RedoAction.KEY));
		this.redoMenuItem.setIcon(null);
		this.redoMenuItemText = this.redoMenuItem.getText();
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(EditorKit.CutAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(EditorKit.CopyAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(EditorKit.PasteAction.KEY_PASTE)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(EditorKit.PasteAction.KEY_PASTE_SPECIAL)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(EditorKit.DeleteAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(EditorKit.SelectAllAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.PageFormatAction.KEY)); mi.setIcon(null);
		// content-type :
		PEMenu contentTypeMenu;
		menu.add(contentTypeMenu=createPEMenu("menu.SetContentType"));
		for (int i=0; i<jpicedt.MiscUtilities.getAvailableContentTypes().length; i++){
			contentTypeMenu.add(actionRegistry.getAction(MiscUtilities.getAvailableContentTypesNames()[i]));
		}

		mi=menu.add(actionRegistry.getAction(JPicEdt.PreferencesAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.RedrawAction.KEY)); mi.setIcon(null);
		return menu;
	}

	private PEMenu createToolsMenu(){
		PEMenu menu = createPEMenu("menu.Toolkit");
		ButtonGroup bg = new ButtonGroup();
		JMenuItem mi;

		// registered mouse tool :
		String[][] allToolNames = EditorKit.getAvailableToolNames();
		Action a;
		for (int i=0; i<allToolNames.length; i++){
			for (int j=0; j<allToolNames[i].length; j++){
				a = actionRegistry.getAction(allToolNames[i][j].toString());
				//a.putValue(Action.NAME,a.getValue(Action.SHORT_DESCRIPTION)); // side-effect: add text beside icons in DockableEditorKitToolBar when JFrameMDIManager is used (because a PEMenuBar gets creates each time a new sheet is opened)
				mi=menu.add(a);
				mi.setText((String)a.getValue(Action.SHORT_DESCRIPTION));
				bg.add(mi);
				if (allToolNames[i][j].equals(EditorKit.SELECT)) mi.setSelected(true);
			}
			menu.add(new JSeparator());
		}
		// local/global mode :
		a=actionRegistry.getAction(EditorKit.ToggleEditPointsModeAction.KEY);
		//a.putValue(Action.NAME,a.getValue(Action.SHORT_DESCRIPTION));
		mi=menu.add(a);
		//mi.setText(null);
		return menu;
	}

	private PEMenu createCommandsMenu(){

		PEMenu menu = createPEMenu("menu.Commands");
		JMenuItem mi;
		for (RunExternalCommand.Command c: RunExternalCommand.TeX_COMMANDS){
			Action a = actionRegistry.getAction(c.getName());
			mi=menu.add(a);
			mi.setIcon(null);
		}
		return menu;
	}

	private PEMenu createScriptsMenu(){

		PEMenu menu = createPEMenu("menu.Scripts");
		menu.add(actionRegistry.getAction(JPicEdt.OpenBSHConsoleAction.KEY));
		menu.add(new JSeparator());
		menu.add(new UpdateScriptsMenuAction());
		return menu;
	}

	private PEMenu createFragmentsMenu(){
		PEMenu menu = createPEMenu("menu.Fragments");
		menu.add(new UpdateFragmentsMenuAction());
		return menu;
	}

	private PEMenu createWindowMenu(){
		PEMenu menu = createPEMenu("menu.Window");
		JMenuItem mi;
		mi=menu.add(actionRegistry.getAction(JPicEdt.WindowCascadeAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.WindowTileHorAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.WindowTileVertAction.KEY)); mi.setIcon(null);
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.ToggleAttributesPanelAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.ToggleToolkitPanelAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.ToggleConvexZonePanelAction.KEY)); mi.setIcon(null);
		return menu;
	}

	private PEMenu createHelpMenu(){
		PEMenu menu = createPEMenu("menu.Help");
		JMenuItem mi=menu.add(actionRegistry.getAction(JPicEdt.ShowHelpAction.KEY));
		menu.add(new JSeparator());
		mi=menu.add(actionRegistry.getAction(JPicEdt.MemoryMonitorAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.ShowLicenseAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.ShowErrorLogAction.KEY)); mi.setIcon(null);
		mi=menu.add(actionRegistry.getAction(JPicEdt.AboutAction.KEY)); mi.setIcon(null);
		return menu;
	}

	///////////////////////////////////////// UPDATING SCHEME ////////////////////////////////

	/**
	 * Update JMenuItem's accelerators from the key/value pairs stored in JPicEdt's preferences
	 */
	public void updateAccelerators(){

		// fetch values for accelerators from the "preferences" file
		JMenuItem[] miArray = getMenuItems();
		Properties prefs = jpicedt.JPicEdt.getPreferences(); // same syntax as in i18_xx.properties
		for (int i=0; i<miArray.length; i++){
			// build "key":
			Action a = miArray[i].getAction();
			if (a==null) continue;
			String key = (String)a.getValue(Action.ACTION_COMMAND_KEY); // e.g. "New" or "SaveAs"
			if (key==null) continue;
			// fetch "value":
			String value = prefs.getProperty(key); // e.g. "control N" or "shift DELETE"
			if (value==null) continue;
			// turn "value" into a KeyStroke:
			if (value.equals("none")) miArray[i].setAccelerator(null); // means accelerator was removed by user
			else {
				KeyStroke ks = KeyStroke.getKeyStroke(value);
				miArray[i].setAccelerator(ks);
			}
		}
	}

	/**
	 * Update the content (i.e. <code>PEMenu</code>'s) of the menu-bar
	 * depending on the current state of the <code>MDIManager</code>.
	 */
	public void updateMenuBar(){
		// so far, we just enable/disable some PEMenu's depending on the number of currently open boards:
		Set<PEDrawingBoard> boardArray = JPicEdt.getMDIManager().getAllDrawingBoards();
		boolean enabled = !boardArray.isEmpty();
		menuFragments.setEnabled(enabled);
		menuCommands.setEnabled(enabled);
		menuWindow.setEnabled(enabled);
	}

	/**
	 * Updates the "recent files" sub-menu items after a frame was saved or opened.
	 * @since PicEdt 1.1
	 */
	public void updateRecentFilesSubmenu(){

		this.submenuRecentFiles.add(new JMenuItem("dummy")); // swing 1.1.1 bug workaround [pending] still necessary ?
		this.submenuRecentFiles.removeAll();
		ArrayList list = jpicedt.MiscUtilities.parseRecentFiles(JPicEdt.getPreferences());
		for(Iterator it = list.iterator(); it.hasNext();){
			String fileName = (String)it.next();
			JMenuItem menuItem = new JMenuItem(fileName);
			menuItem.addActionListener(new JPicEdt.FileOpenAction(fileName));
			this.submenuRecentFiles.add(menuItem);
		}
	}

	/**
	 * Updates the "script" menu items
	 * @since jpicedt 1.3.2
	 */
	public void updateScriptsMenu(){

		this.menuScripts.removeAll();
		this.menuScripts.add(JPicEdt.getActionRegistry().getAction(JPicEdt.OpenBSHConsoleAction.KEY));
		this.menuScripts.add(new JSeparator());

		JMenu macroMenu = jpicedt.ui.util.BeanShell.createMenu();
		Component[] cc = macroMenu.getMenuComponents();
		for(int i=0; i<cc.length; i++){
			this.menuScripts.add(cc[i]);
		}

		this.menuScripts.add(new JSeparator());
		this.menuScripts.add(new UpdateScriptsMenuAction());
	}

	/**
	 * Updates the "fragments" menu items
	 * @since jpicedt 1.3.2
	 */
	public void updateFragmentsMenu(){

		this.menuFragments.removeAll();

		Component[] cc = jpicedt.ui.util.Fragments.createMenu().getMenuComponents();
		for(int i=0; i<cc.length; i++){
			this.menuFragments.add(cc[i]);
		}

		this.menuFragments.add(new JSeparator());
		this.menuFragments.add(new UpdateFragmentsMenuAction());
	}

	/**
	 * update "undo" and "redo" text
	 * @since jpicedt 1.3.2
	 */
	public void updateUndoRedoMenus(String undoName,String redoName){
		this.redoMenuItem.setText(this.redoMenuItemText + " : " + redoName);
		this.undoMenuItem.setText(this.undoMenuItemText + " : " + undoName);
	}

	/**
	 * Updates the Window menu as soon as an internal frame is opened or closed
	 * (this menu keeps tracks of every open internal frames so that the user can activate
	 * them quickly by use of a shortcut)
	 *
	 * @since PicEdt 1.1
	 */
	public void updateWindowMenu() throws MissingResourceException{

		JMenuItem mi;
		this.menuWindow.removeAll();
		mi=this.menuWindow.add(JPicEdt.getActionRegistry().getAction(JPicEdt.WindowCascadeAction.KEY)); mi.setIcon(null);
		mi=this.menuWindow.add(JPicEdt.getActionRegistry().getAction(JPicEdt.WindowTileHorAction.KEY)); mi.setIcon(null);
		mi=this.menuWindow.add(JPicEdt.getActionRegistry().getAction(JPicEdt.WindowTileVertAction.KEY)); mi.setIcon(null);
		this.menuWindow.add(new JSeparator());
		mi=this.menuWindow.add(actionRegistry.getAction(JPicEdt.ToggleAttributesPanelAction.KEY)); mi.setIcon(null);
		mi=this.menuWindow.add(actionRegistry.getAction(JPicEdt.ToggleToolkitPanelAction.KEY)); mi.setIcon(null);
		mi=this.menuWindow.add(actionRegistry.getAction(JPicEdt.ToggleConvexZonePanelAction.KEY)); mi.setIcon(null);

		this.menuWindow.add(new JSeparator());

		int boardIndex=0;
		for (PEDrawingBoard bd: JPicEdt.getMDIManager().getAllDrawingBoards()){
			this.menuWindow.add(new MenuWindowFramesAction(bd, boardIndex++));
		}

	}

	/////////////////////////////////////// PROPERTY LISTENER /////////////////////////

	/**

	 * Implementation of <code>PropertyChangeListener</code> interface Allows
	 * this menu-bar to get notified of change from the target EditorKit,
	 * e.g. when a right-click switches the current EditorKit's mousetool back
	 * to "select-tool", or when a new mousetool gets selected from the
	 * "Tools" palette or from a BSH script.
	 * <p> This DockablePanel gets registered as a PropertyChangeListener
	 * (with events sourced from EditorKit's) from inside MDIManager whenever
	 * a new Board is added.
	 * @param e the PropertyChangeEvent ; only the "getNewValue()" method is used here.
	 */
	public void propertyChange(PropertyChangeEvent e){
		// update "toolsmenu"
		if (e.getPropertyName()==EditorKit.EDIT_MODE_CHANGE){
			//System.out.println("EditorKit.EDIT_MODE_CHANGE : newValue = " + e.getNewValue());
			for (int i=0; i<menuTools.getMenuComponentCount(); i++){
				Component cmp = menuTools.getMenuComponent(i);
				if (!(cmp instanceof JCheckBoxMenuItem)) continue; // filter out buttons not associated with an editor-kit mode
				JCheckBoxMenuItem c = (JCheckBoxMenuItem)cmp;
				Action a = c.getAction();
				if (e.getNewValue().equals(a.getValue(Action.ACTION_COMMAND_KEY))){ // actionCommand is used to build a PEAction
					c.setSelected(true);
				}
			}
		}
	}

	/////////////////////////////////////// ADAPTERS ///////////////////////

	/**
	 * An inner class implementing an action adapter for Window|Frames[index] submenu items.
	 * Activate frame hosting the given board, by calling the adequate method
	 * in <code>MDIManager</code>.  There's an actionListener for each open
	 * internal frame.
	 *
	 * @since PicEdt 1.1.2 (inner class as of PicEdt 1.2)
	 */
	class MenuWindowFramesAction extends AbstractAction{

		PEDrawingBoard board;

		MenuWindowFramesAction(PEDrawingBoard board, int boardIndex) {
			this.board = board;
			String str = Integer.toString(boardIndex);
			putValue(ACTION_COMMAND_KEY,null); // hack to make sure this action won't show up in ShortcutsCustomizer
			putValue(NAME, board.getTitle());
			//putValue(SHORT_DESCRIPTION, localizer.getActionTooltip(actionName));
			//putValue(LONG_DESCRIPTION, localizer.getActionHelper(actionName));
			//putValue(MNEMONIC_KEY, localizer.getActionMnemonic(actionName));
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(str.charAt(0),Event.CTRL_MASK)); // accel. don't work under Linux/WindowMaker/SUN's JDK1.2.2
			//putValue(SMALL_ICON, localizer.getActionIcon(actionName));

		}
		public void actionPerformed(ActionEvent e) {
			JPicEdt.getMDIManager().selectDrawingBoard(board);
		}
	}

	class UpdateScriptsMenuAction extends PEAction {

		public static final String KEY = "action.ui.UpdateScriptsMenu";

		public UpdateScriptsMenuAction(){
			super(JPicEdt.getActionDispatcher(),KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			updateScriptsMenu();
		}
	} // class

	class UpdateFragmentsMenuAction extends PEAction {

		public static final String KEY = "action.ui.UpdateFragmentsMenu";

		public UpdateFragmentsMenuAction(){
			super(JPicEdt.getActionDispatcher(),KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			updateFragmentsMenu();
		}
	} // class

}// class
