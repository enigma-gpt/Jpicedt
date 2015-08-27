// ActionRegistry.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1996/2011  Sylvain Reynal
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
// Version: $Id: ActionRegistry.java,v 1.27 2013/03/27 06:52:51 vincentb1 Exp $
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

package jpicedt.ui.action;

import jpicedt.ui.*;
import jpicedt.ui.util.*;
import jpicedt.Localizer;
import jpicedt.JPicEdt;
import jpicedt.Log;
import jpicedt.graphic.toolkit.ActionLocalizer;
import jpicedt.graphic.toolkit.ActionDispatcher;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.DefaultSelectionHandler;
import jpicedt.graphic.toolkit.PEAction;
import jpicedt.graphic.toolkit.PEToggleAction;
import jpicedt.graphic.event.SelectionListener;
import jpicedt.graphic.event.SelectionEvent;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.datatransfer.*;
import static jpicedt.graphic.view.highlighter.CompositeHighlighter.*;
import static jpicedt.Log.*;

/**
 * A registry that retains all JPicEdt's actions in a map,
 * supplies them on-demand through a String key, and knows how to update actions state using
 * the currently installed MDIManager.
 * @since jPicEdt 1.3
 * @author Sylvain Reynal
 * @version $Id: ActionRegistry.java,v 1.27 2013/03/27 06:52:51 vincentb1 Exp $
 *
 */
public class ActionRegistry extends ActionMap {

	/**
	 * construct an ActionRegistry with default Actions
	 */
	public ActionRegistry(){
		this(jpicedt.Localizer.currentLocalizer().getActionLocalizer(),JPicEdt.getActionDispatcher());
	}

	/**
	 * construct an ActionRegistry with default Actions
	 */
	public ActionRegistry(ActionDispatcher dispatcher){
		this(jpicedt.Localizer.currentLocalizer().getActionLocalizer(),dispatcher);
	}

	/**
	 * construct an ActionRegistry with default Actions
	 */
	public ActionRegistry(ActionLocalizer localizer, ActionDispatcher dispatcher){

		// set EditorKit's action-map as the parent :
		ActionMap kitMap = EditorKit.createActionMap(dispatcher,localizer);
		setParent(kitMap);

		addAction(new JPicEdt.ExitAction());

		// file I/O :
		addAction(new JPicEdt.FragmentInsertAction());
		addAction(new JPicEdt.FragmentSaveAction());
		addAction(new JPicEdt.FileNewAction());
		addAction(new JPicEdt.FileOpenAction());
		addAction(new JPicEdt.FileReloadAction());
		addAction(new JPicEdt.FileSaveAction());
		addAction(new JPicEdt.FileSaveAllAction());
		addAction(new JPicEdt.FileSaveAsAction());
		addAction(new JPicEdt.FileCloseAction());

		// Editing :
		addAction(new JPicEdt.RedrawAction());
		addAction(new JPicEdt.PageFormatAction());
		addAction(new JPicEdt.ViewLaTeXFileAction());
		addAction(new JPicEdt.UndoAction());
		addAction(new JPicEdt.RedoAction());

		// BeanShell :
		addAction(new JPicEdt.OpenBSHConsoleAction());

		// Content-type :
		for (int i=0; i<jpicedt.MiscUtilities.getAvailableContentTypes().length; i++){
			addAction(new JPicEdt.ContentTypeAction(i)); // key = content-type NAME !!!
		}
		// Dockable panels :
		addAction(new JPicEdt.ToggleAttributesPanelAction());
		addAction(new JPicEdt.ToggleToolkitPanelAction());
		addAction(new JPicEdt.ToggleConvexZonePanelAction());

		// Preferences :
		addAction(new JPicEdt.PreferencesAction());

		// Help and rel.
		addAction(new JPicEdt.AboutAction());
		addAction(new JPicEdt.ShowHelpAction());
		addAction(new JPicEdt.ShowLicenseAction());
		addAction(new JPicEdt.MemoryMonitorAction());
		addAction(new JPicEdt.ShowErrorLogAction());

		// Window placement :
		addAction(new JPicEdt.WindowCascadeAction());
		addAction(new JPicEdt.WindowTileHorAction());
		addAction(new JPicEdt.WindowTileVertAction());

		//External commands :
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.LATEX));
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.DVIVIEWER));
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.DVIPS));
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.GHOSTVIEW));
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.USER1));
		addAction(new JPicEdt.RunExternalCommandAction(RunExternalCommand.Command.USER2));

		// override EditorKit.SelectMouseToolAction (which dispatch to a single PECanvas) with those in jpicedt.ui.action:
		// (reminder: map resolves to parent only if the key is not found here)
		String[][] allToolNames = EditorKit.getAvailableToolNames();
		for (int i=0; i<allToolNames.length; i++){
			for (int j=0; j<allToolNames[i].length; j++){
				addAction(new SelectEditorkitMouseToolAction(localizer,allToolNames[i][j]));
			}
		}
		// ibid. for local/global mode toggling:
		addAction(new ToggleEditorkitEditPointsModeAction(localizer));

		// actions pour la manipulation des zones convexes
		allToolNames = EditorKit.getAvailableConvexZoneToolNames();
		for (int i=0; i<allToolNames.length; i++){
			for (int j=0; j<allToolNames[i].length; j++){
				addAction(new SelectEditorkitMouseToolAction(localizer,allToolNames[i][j]));
			}
		}

		// ibid. le basculement du mode « utiliser les zones convexes »
		addAction(new ToggleUseConvexZoneModeAction(localizer));


	}


	/**
	 * @param a the Action to add
	 */
	public void addAction(Action a){
		put(a.getValue(Action.ACTION_COMMAND_KEY), a);
	}
	/**
	* @return the action whose name maps "name"
	*/
	public Action getAction(String name){
		Action a = get(name);
		if (a==null) {
			if (Log.DEBUG) Log.debug(name + " was not found!");
			return new DummyAction(name);
		}
		else return a;
	}

	/**
	 * Update <code>Action</code>'s enabled-state according to the current state of the installed
	 * <code>MDIManager</code>.  This may be called when a frame gets activated, or when a user action occured
	 * that implies updating action states, e.g. "Save", "Open", edit active board, &hellip;
	 */
	public void updateActionState(){

		// test if no more frame in the desktop
		// => we must disable all actions but those valid even if there are no frames in the desktop !
		if (jpicedt.JPicEdt.getMDIManager().getDrawingBoardCount() == 0){
			switchToNoFrameConfiguration();
			return;
		}

		// else fetch active board :
		PEDrawingBoard board = jpicedt.JPicEdt.getMDIManager().getActiveDrawingBoard();
		if (board == null) {
			switchToNoFrameConfiguration(); // should never happen
			return;
		}

		// ok, now there's at least one board in the desktop :
		switchToMinimalConfiguration();

		// update "Save" Action's state : if the frame's already been saved to disk
		// and not modified yet => disable action
		getAction(JPicEdt.FileSaveAction.KEY).setEnabled(board.isDirty() || !board.isSaved());

		// updating "Reload" Action's state : disable if the frame's hasn't yet been saved to disk,
		getAction(JPicEdt.FileReloadAction.KEY).setEnabled(board.isSaved());

		// update "SaveAll" action (very very tricky, boy !)
		updateSaveAllAction();

		// update "Undo/Redo" actions, depending on this internal frame's  UndoableBuffer state :
		if (board.getCanvas().canRedo()) getAction(JPicEdt.RedoAction.KEY).setEnabled(true);
		else getAction(JPicEdt.RedoAction.KEY).setEnabled(false);
		if (board.getCanvas().canUndo()) getAction(JPicEdt.UndoAction.KEY).setEnabled(true);
		else getAction(JPicEdt.UndoAction.KEY).setEnabled(false);

		// update Cut, Copy, Delete and SaveSelectionAs actions, depending on the size of the active selection:
		boolean b = !(board.getCanvas().getSelectionSize()==0);
		getAction(JPicEdt.FragmentSaveAction.KEY).setEnabled(b); // disabled when no active selection
		getAction(EditorKit.CopyAction.KEY).setEnabled(b);
		getAction(EditorKit.CutAction.KEY).setEnabled(b);
		getAction(EditorKit.DeleteAction.KEY).setEnabled(b);

		// content type
		jpicedt.graphic.ContentType contentType = board.getCanvas().getContentType();
		String contentTypeClassName = contentType.getClass().getName();
		for (int i=0; i<jpicedt.MiscUtilities.getAvailableContentTypes().length; i++){
			String availableClassName = jpicedt.MiscUtilities.getAvailableContentTypes()[i];
			boolean match = availableClassName.equals(contentTypeClassName);
			getAction(jpicedt.MiscUtilities.getAvailableContentTypesNames()[i]).setEnabled(!match);
		}

	}

	/**
	 * update "SaveAll" action depending on the "isDirty" flag of every currently open board
	 */
	private void updateSaveAllAction(){
		if (DEBUG) Log.error("NOT IMPLEMENTED !!! [underway]");
		/* [pending] UNDERWAY
		JInternalFrame[] allFrames = desktopPane.getAllFrames();
		for (int i = 0; i < allFrames.length; i++){
			InternalFrame ifr = (InternalFrame)allFrames[i];
			PEDrawingBoard board = ifr.getBoard();
			if (board.isDirty() || !board.isSaved()) {
				actionRegistry.getAction(JPicEdt.FileSaveAllAction.KEY).setEnabled(true);
				return;
			}
	}
		actionRegistry.getAction("SaveAll").setEnabled(false);
		*/
	}

	/**
	 * disable actions that make no sense when no frame is present in the desktop
	 */
	private void switchToNoFrameConfiguration(){

		getAction(JPicEdt.FragmentInsertAction.KEY).setEnabled(false);
		getAction(JPicEdt.FileReloadAction.KEY).setEnabled(false);
		getAction(JPicEdt.FileSaveAction.KEY).setEnabled(false);
		getAction(JPicEdt.FragmentSaveAction.KEY).setEnabled(false);
		getAction(JPicEdt.FileSaveAllAction.KEY).setEnabled(false);
		getAction(JPicEdt.FileSaveAsAction.KEY).setEnabled(false);
		getAction(JPicEdt.FileCloseAction.KEY).setEnabled(false);
		getAction(EditorKit.CopyAction.KEY).setEnabled(false);
		getAction(EditorKit.CutAction.KEY).setEnabled(false);
		getAction(EditorKit.DeleteAction.KEY).setEnabled(false);
		getAction(EditorKit.PasteAction.KEY_PASTE).setEnabled(false);
		getAction(EditorKit.SelectAllAction.KEY).setEnabled(false);
		getAction(JPicEdt.WindowCascadeAction.KEY).setEnabled(false);
		getAction(JPicEdt.WindowTileHorAction.KEY).setEnabled(false);
		getAction(JPicEdt.WindowTileVertAction.KEY).setEnabled(false);
		getAction(JPicEdt.UndoAction.KEY).setEnabled(false);
		getAction(JPicEdt.RedoAction.KEY).setEnabled(false);
		getAction(JPicEdt.RedrawAction.KEY).setEnabled(false);
		getAction("LaTeX").setEnabled(false);
		getAction("DVI").setEnabled(false);
		getAction("Dvips").setEnabled(false);
		getAction("Ghostview").setEnabled(false);
		getAction("UserProgram1").setEnabled(false);
		getAction("UserProgram2").setEnabled(false);
		// content type
		for (int i=0; i<jpicedt.MiscUtilities.getAvailableContentTypes().length; i++){
			getAction(jpicedt.MiscUtilities.getAvailableContentTypesNames()[i]).setEnabled(false);
		}
	}

	/**
	 * enable a minimal set of actions, when a new frame is created in an initially empty desktop.
	 */
	private void switchToMinimalConfiguration(){

		getAction(JPicEdt.FragmentInsertAction.KEY).setEnabled(true);
		getAction(JPicEdt.FileSaveAsAction.KEY).setEnabled(true);
		getAction(JPicEdt.FileSaveAllAction.KEY).setEnabled(true); // [pending] delete once we fix "updateSaveAllAction"
		getAction(JPicEdt.FileCloseAction.KEY).setEnabled(true);
		getAction(EditorKit.SelectAllAction.KEY).setEnabled(true);
		getAction(JPicEdt.WindowCascadeAction.KEY).setEnabled(true);
		getAction(EditorKit.PasteAction.KEY_PASTE).setEnabled(true);
		getAction(JPicEdt.WindowTileHorAction.KEY).setEnabled(true);
		getAction(JPicEdt.WindowTileVertAction.KEY).setEnabled(true);
		getAction("Redraw").setEnabled(true);
		getAction("LaTeX").setEnabled(true);
		getAction("DVI").setEnabled(true);
		getAction("Dvips").setEnabled(true);
		getAction("Ghostview").setEnabled(true);
		getAction("UserProgram1").setEnabled(true);
		getAction("UserProgram2").setEnabled(true);
	}

	// DEBUGGING : work-around for action that don't exist
	private class DummyAction extends AbstractAction {

		public DummyAction(String s){
			super(s+":NotFound!");
		}
		public void actionPerformed(ActionEvent e){}
	}

	/////////////////////////////////////////////////////////////
	/// Some actions overriden from EditorKit
	/////////////////////////////////////////////////////////////

	/**
	* Sets the current mouse-tool for every <code>EditorKit</code> attached to an open
	* <code>PEDrawingBoard</code>.  Action commands are the same as the predefined mouse-tool's names for
	* <code>EditorKit</code>.  This action dispatches to all currently active <code>EditorKit</code>'s, by
	* relying on the current <code>MDIManager</code>.
	*/
	static class SelectEditorkitMouseToolAction extends PEToggleAction {

		private String mouseToolName;

		/**
		 * Construct a new <code>SelectEditorkitMouseToolAction</code> for the given mouse-tool name.
		 * @param mouseToolName one of <code>EditorKit</code>'s predefined mouse-tool constant,
		 * e.g. <code>EditorKit.ROTATE</code>.
		 */
		public SelectEditorkitMouseToolAction(ActionLocalizer localizer,String mouseToolName){
			super(null, mouseToolName, localizer); // no dispatcher
			this.mouseToolName = mouseToolName;
		}

		/** overriden so as to dispatch to all currently open EditorKit's */
		public void actionPerformed(ActionEvent e){
			MDIManager mdiManager = JPicEdt.getMDIManager();
			//mdiManager.getToolBar().updateCurrentTool(mouseToolName); [pending]
			for(PEDrawingBoard bd: mdiManager.getAllDrawingBoards()){
				EditorKit kit = bd.getCanvas().getEditorKit();
				if (kit != null) kit.setCurrentMouseTool(mouseToolName);
			}
		}
	}

	/**
	 * Toggles the EditorKit's edit-points-mode b/w LOCAL_MODE and GLOBAL_MODE, for every
	 * EditorKit attached to an open PEDrawingBoard.
	 * This action dispatches to all currently active EditorKit's, by relying on the current MDIManager.
	 * <p>
	 * If this action is attached to an AbstractButton, then the selection state of the button is used to
	 * toggle the edit-mode between LOCAL (not-selected) and GLOBAL (selected). Otherwise, a simple toggling occurs.
	 */
	static class ToggleEditorkitEditPointsModeAction extends PEToggleAction {

		/* same KEY as the corresponding action in EditorKit */
		public final static String KEY = EditorKit.ToggleEditPointsModeAction.KEY;

		/**
		 * Construct a new SelectEditorkitMouseToolAction for the given mouse-tool name.
		 * @param mouseToolName one of EditorKit's predefined mouse-tool constant, e.g. EditorKit.ROTATE.
		 */
		public ToggleEditorkitEditPointsModeAction(ActionLocalizer localizer){
			super(null, KEY, localizer); // default, if no localizer is provided
		}

		/** overriden so as to dispatch to all currently open EditorKit's */
		public void actionPerformed(ActionEvent e){

			MDIManager mdiManager = JPicEdt.getMDIManager();

			if (e.getSource() instanceof AbstractButton){ // on=GLOBAL, off=LOCAL
				AbstractButton source = (AbstractButton)e.getSource();
				for(PEDrawingBoard bd: mdiManager.getAllDrawingBoards()){
					EditorKit kit = bd.getCanvas().getEditorKit();
					if (kit != null && kit.getSelectionHandler() instanceof DefaultSelectionHandler){
						DefaultSelectionHandler h = (DefaultSelectionHandler)kit.getSelectionHandler();
						if (source.isSelected())
							h.setHighlightingMode(HighlightingMode.GLOBAL);
						else
							h.setHighlightingMode(HighlightingMode.LOCAL);
					}
				}
			}
			else {
				for(PEDrawingBoard bd: mdiManager.getAllDrawingBoards()){
					EditorKit kit = bd.getCanvas().getEditorKit();
					if (kit != null && kit.getSelectionHandler() instanceof DefaultSelectionHandler){
						DefaultSelectionHandler h = (DefaultSelectionHandler)kit.getSelectionHandler();
						h.toggleHighlightingMode();
					}
				}
			}

		}
	}

	static class ToggleUseConvexZoneModeAction extends PEToggleAction {

		/* same KEY as the corresponding action in EditorKit */
		public final static String KEY = EditorKit.ToggleUseConvexZoneModeAction.KEY;

		/**
		 * Construuit une nouvelle actions ToggleUseConvexZoneModeAction pour
		 * le nom défini dans l'attribut <code>KEY</code>.
		 * @param localizer permet de localiser l'action correspondante dans
		 * l'<code>EditorKit</code>.
		 */
		public ToggleUseConvexZoneModeAction(ActionLocalizer localizer){
			super(null, KEY, localizer); // default, if no localizer is provided
		}

		/** surchargé de sorte à distribuer à tous est <code>EditorKit</code> ouverts. */
		public void actionPerformed(ActionEvent e){
			JOptionPane.showMessageDialog(null,"e="+e,"PROBE",JOptionPane.INFORMATION_MESSAGE); //$$PROBE

			// MDIManager mdiManager = JPicEdt.getMDIManager();

			// if (e.getSource() instanceof AbstractButton){ // on=GLOBAL, off=LOCAL
			// 	AbstractButton source = (AbstractButton)e.getSource();
			// 	for(PEDrawingBoard bd: mdiManager.getAllDrawingBoards()){
			// 		EditorKit kit = bd.getCanvas().getEditorKit();
			// 		if (kit != null && kit.getSelectionHandler() instanceof DefaultSelectionHandler){
			// 			DefaultSelectionHandler h = (DefaultSelectionHandler)kit.getSelectionHandler();
			// 			if (source.isSelected())
			// 				h.setHighlightingMode(HighlightingMode.GLOBAL);
			// 			else
			// 				h.setHighlightingMode(HighlightingMode.LOCAL);
			// 		}
			// 	}
			// }
			// else {
			// 	for(PEDrawingBoard bd: mdiManager.getAllDrawingBoards()){
			// 		EditorKit kit = bd.getCanvas().getEditorKit();
			// 		if (kit != null && kit.getSelectionHandler() instanceof DefaultSelectionHandler){
			// 			DefaultSelectionHandler h = (DefaultSelectionHandler)kit.getSelectionHandler();
			// 			h.toggleHighlightingMode();
			// 		}
			// 	}
			// }

		}
	}


} // ActionRegistry
