// JPicEdt.java --- -*- coding: iso-8859-1 -*-
// January 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
// Copyright 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: JPicEdt.java,v 1.52 2013/10/31 23:01:28 vincentb1 Exp $
// Keywords: main
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
package jpicedt;

import jpicedt.jpicedt_env.UserSettingsDirectory;
import jpicedt.graphic.event.*;
import jpicedt.ui.*;
import jpicedt.ui.util.*;
import jpicedt.ui.util.HtmlViewer;
import jpicedt.ui.dialog.*;
import jpicedt.ui.dialog.YesNoAskMe;
import jpicedt.ui.action.*;
import jpicedt.ui.internal.*;
import jpicedt.graphic.*;
import jpicedt.graphic.toolkit.*;

import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.net.*;

import static jpicedt.Log.*;
import static jpicedt.Localizer.localize;

/**
 * Application main class (this class is not shipped in the jpicedt library)
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: JPicEdt.java,v 1.52 2013/10/31 23:01:28 vincentb1 Exp $
 *
 */
public class JPicEdt  {

	/////////////////////////////////////////////////////////////////
	/// MAIN ENTRY POINT
	/////////////////////////////////////////////////////////////////
	/**
	 * @param args Command line arguments &rarr; name of files to load into the
	 *             application on startup (there's no limitation on the number
	 *             of files to load). Any pathname, either relative, or
	 *             absolute, is valid.
	 */
	public static void main(String[] args){

		// test JDK version compatibility:
		String javaVersion = System.getProperty("java.version");
		if(javaVersion.compareTo(Version.getMinimumJdkVersion()) < 0){
			System.err.println("You are running Java version " + javaVersion + ".");
			System.err.println("jPicEdt requires Java " + Version.getMinimumJdkVersion() + " or later.");
			System.exit(1);

		}

		// parse command line arguments
		for (int i = 0; i < args.length; i++){
			if (args[i].equals("-h") || args[i].equals("--help")) {
				System.out.println("Available options:");
				System.out.println("\t--debug: do not redirect error/output stream to error.log (debug mode)");
				System.out.println("\t-d: short hand for --debug");
				System.exit(0);
			}
			else if (args[i].equals("--debug") || args[i].equals("-d") ) logRedirection = SystemOutUtilities.STANDARD;
			else fileToLoadOnStartup.add(args[i]);
		}

		// start splash screen
		/* number of progress steps =
		 * - loading preferences: 1
		 * - loading LAF: 2
		 * - init GUI (from MDIManager): 3 to 6
		 * - plus one so that 100% gets reached only after everything is completed
		 */
		int numberOfProgressSteps = 10;
		PEProgressBar progressBar = new PEProgressBar(numberOfProgressSteps);

		initPreferences(progressBar);
		initGUI(progressBar); // need action registry
		initMisc(progressBar);
	}




	/////////////////////////////////////////////////////////////////
	/// INIT
	/////////////////////////////////////////////////////////////////

	/**
	 * Init the "preferences" Properties object + the default parser.
	 * @since jPicEdt 1.3.2
	 */
	private static void initPreferences(PEProgressBar progressBar){

		if (progressBar!=null) progressBar.increment("Loading preferences..."); // step 1

		userSettingsDirectory = UserSettingsDirectory.getUserSettingsDirectory();

		// check if setting dir exists, and create missing dirs:
		createSettingDirectories(progressBar);  // guaranteed to create a dir structure, or to exit.

		// System.out and System.err redirection, depending on first arg (we must wait until user-setting dir has been created)
		SystemOutUtilities.instance().redirect(logRedirection);
		SystemOutUtilities.instance().displayDialog(false); // allows for a dialog box to be opened each time a string gets written to System.out (N/A if redir=standard)

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		try{
			preferencesFileName = userSettingsDirectory + File.separator + "preferences";
			FileInputStream fis = new FileInputStream(preferencesFileName); // throws FileNotFoundException
			preferences.load(fis); // throws IOException
			fis.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Preferences file not found: using defaults..."); // [todo] to localize
		}
		catch(Exception ex){ // can be: IOException, NumberFormatException, ArrayIndexOutOfBoundsException, MissingResourceException...
			System.err.println("The following error occured during preferences initialization from disk:");
			ex.printStackTrace(); // to System.err
			progressBar.recoverableError("Could not load preferences from disk: using defaults...\n(see "+ SystemOutUtilities.getErrorLogFile() + " for details)"); // [todo] to localize
		}

		Localizer.currentLocalizer().init(preferences); // init locale from preferences
		PEFileChooser.configure(preferences);

		progressBar.increment("Loading parser..");
		parser = MiscUtilities.createParser();
	}

	/**
	 * @since jPicEdt 1.3.2
	 */
	private static void initGUI(PEProgressBar progressBar){

		// Install new RepaintManager for debugging purpose
		if (DEBUG_PAINTING) {
			RepaintManager.setCurrentManager(new DebugRepaintManager());
		}

		// Install new FocusManager for debugging purpose
		if (DEBUG_FOCUS) {
			javax.swing.FocusManager.setCurrentManager(new DebugFocusManager());
		}

		progressBar.increment("Loading action map.."); // step 2
		actionRegistry = new ActionRegistry(); // must be done PRIOR to building the GUI !!! (cross-ref danger)

		// init MDI interface

		progressBar.increment("Loading GUI.."); // step 4
		String mdiMgrStr = getProperty(MDIManager.KEY_MDIMANAGER, MDIManager.getDefaultManagerName());
		setMDIManager(MDIManager.createMDIManager(mdiMgrStr, progressBar));
		actionRegistry.updateActionState();
		// configure shared static variables for all views that inherit from DefaultViewFactory:
		jpicedt.graphic.view.DefaultViewFactory.configure(preferences);
		// configure dockable panels, possibly "setVisible" depending on geom. stored in preferences
		// (hence it's necessary to build the MDI beforehands)
		progressBar.increment("Loading toolbars..."); // step 3 (pretty slow !!!)
		JPanel dac = new DockableAttributesCustomizer(progressBar);
		progressBar.increment(); // step 3
		JPanel dektb = new DockableEditorKitToolBar();
		JPanel dcztb = new DockableConvexZoneToolBar();
		getMDIManager().addDockablePanel(DockableAttributesCustomizer.KEY,dac);
		getMDIManager().addDockablePanel(DockableEditorKitToolBar.KEY,dektb);
		getMDIManager().addDockablePanel(DockableConvexZoneToolBar.KEY,dcztb);
		getMDIManager().updateMenuBar();

		progressBar.destroy(); // close progress bar
	}

	/**
	 * Need: logRedirection, fileToLoadOnStartup
	 * @since jPicEdt 1.3.2
	 */
	private static void initMisc(PEProgressBar progressBar){

		// preload pictures
		if (fileToLoadOnStartup.isEmpty()) {
			newBoard();
		}
		else {
			for(Iterator<String> it = fileToLoadOnStartup.iterator(); it.hasNext();){
				openBoard(it.next());
			}
		}
	}

	/**
	 * store preferences (Properties) to file, using "preferencesFileName" init'd during
	 * initPreferences
	 */
	private static void storePreferences(){

		if (DEBUG) debug(preferences.toString());
		try {
			// check that "preferences" does not contain key/value pair where value is not a String:
			for (Iterator<Object> it = preferences.keySet().iterator(); it.hasNext();){
				Object key = it.next();
				Object value = preferences.get(key);
				if ((value instanceof String)==false) it.remove(); // remove key/value from the hashmap
			}

			FileOutputStream fos = new FileOutputStream(preferencesFileName);
			preferences.store(fos, "jPicEdt Preferences");
			fos.close();
			getMDIManager().showStatus("Saved preferences to file: " + preferencesFileName); // [todo] to localize
		}
		catch(IOException ioEx){
			ioEx.printStackTrace();
			getMDIManager().showMessageDialog(
			                              localize("exception.IOError") + ioEx.getMessage(),
			                              localize("msg.SavePreferences"),
			                              JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){
			ex.printStackTrace();
			getMDIManager().showMessageDialog(
							"Unexpected error:" + ex.getMessage(), // [todo] to localize
							localize("msg.SavePreferences"),
							JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Takes for granted that "userSettingDirectory" has been properly init'd beforehands
	 * <p>
	 * So far, creates ".jpicedt", ".jpicedt/macros" and ".jpicedt/fragments" (where .jpicedt gets
	 * replaced by the proper name for the current OS).
	 * <p>
	 * Exit if creation fails at any point.
	 */
	private static void createSettingDirectories(PEProgressBar progressBar){

		File f = new File(userSettingsDirectory);
		boolean error = false;
		if (!f.exists()){
			error = error | !f.mkdirs();
			System.out.println("Creating user-settings directory structure: " + userSettingsDirectory);
		}
		File child = new File(f,"macros");
		if (!child.exists()) error = error | !child.mkdirs();
		child = new File(f,"fragments");
		if (!child.exists()) error = error | !child.mkdirs();
		if (error){
			progressBar.fatalError("I don't have enough rights to create user-settings directory structure: " + userSettingsDirectory); // [todo] to localize
		}
	}

	private static class ShutdownHook extends Thread {

		public void run(){
			//getMDIManager().saveGeometry();
			//storePreferences();
			SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD);
			// clean tmp dir from .log,.dvi,.aux etc...
			//RunExternalCommand.cleanTmpDir();
			//System.out.println("Shutting down...");
		}

	}

	/////////////////////////////////////////////////////////////////
	/// MDI Management
	/////////////////////////////////////////////////////////////////

	/**
	 * set the MDI manager that should be used for jPicEdt
	 * @since jPicEdt 1.3.2
	 */
	public static void setMDIManager(MDIManager aMdiManager){
		mdiManager = aMdiManager;
	}

	/**
	 *
	 * @return the current MDI manager for jPicEdt
	 * @since jPicEdt 1.3.2
	 */
	public static MDIManager getMDIManager(){
		return mdiManager;
	}

	/**
	 * @return the ActionDispatcher responsible for delegating action dispatching to the
	 *         currently installed MDIManager
	 */
	public static ActionDispatcher getActionDispatcher(){
		return actionDispatcher;
	}


	//////////////////////////////////////////////////////////////////////
	/// I/O MANAGEMENT
	//////////////////////////////////////////////////////////////////////

	/**
	 * Create a new drawing board and add it to the desktop
	 * @since jPicEdt 1.3.2
	 */
	public static void newBoard(){

		PEDrawingBoard newBoard = new PEDrawingBoard(numberOfNoNameBoard++,preferences); // create an "untitled" document
		getMDIManager().addDrawingBoard(newBoard);
		String title  = "JPicEdt: " + newBoard.getTitle() + " [" + newBoard.getCanvas().getContentType().getPresentationName() + "]";
		getMDIManager().setHostingContainerTitle(title, newBoard); // update frame title
		actionRegistry.updateActionState();
		getMDIManager().updateWindowMenu(); // a new "Untitled" menu item should appear in it
		getMDIManager().updateMenuBar();
	}


	/**
	 * Create a new board initialized from a file on disk, then add it to the desktop
	 * @param path the location of the file on disk ; if null, prompt user.
	 * @since jPicEdt 1.3.2
	 */
	public static void openBoard(String path){

		// first possibly fetch file name
		if (path == null){
			String defaultContentTypeClassName = preferences.getProperty(PECanvas.KEY_CONTENT_TYPE);
			path = PEFileChooser.getFileName(PEFileChooser.OPENFILE,defaultContentTypeClassName);
			if (path == null) return; // CANCEL
		}
		getMDIManager().showStatus(localize("msg.Loading") + " " + path + " ...");
		// we test if a frame with this name is already open:
		for (PEDrawingBoard bd: getMDIManager().getAllDrawingBoards()){
			if (!bd.isSaved()) continue;
			if (bd.getTitle().equals(path)){
				// if this is the case: we issue a "ReloadWarning"
				int ans = getMDIManager().showConfirmDialog(
				                  localize("exception.ReloadWarning"),
				                  localize("action.ui.Open"),
				                  JOptionPane.YES_NO_OPTION);
				if (ans == JOptionPane.NO_OPTION) return;
				// ... and possibly reload the file:
				bd.reload(parser);
				return;
			}
		}
		// ok, no board with the same path
		PEDrawingBoard newBoard = new PEDrawingBoard(path,preferences,parser);
		getMDIManager().addDrawingBoard(newBoard);
		String title  = "JPicEdt: " + newBoard.getTitle() + " [" + newBoard.getCanvas().getContentType().getPresentationName() + "]";
		getMDIManager().setHostingContainerTitle(title, newBoard); // update frame title

		// update recent file list:
		MiscUtilities.addRecentFile(preferences, newBoard.getTitle());
		// update GUI menus
		getMDIManager().updateWindowMenu(); // in case board's title has changed
		getMDIManager().updateRecentFilesSubmenu();
	}

	/**
	 * Reload the active board from disk
	 * @since jPicEdt 1.3.2
	 */
	public static void reloadBoard(){

		PEDrawingBoard boardToReload = getMDIManager().getActiveDrawingBoard();
		if (boardToReload == null) return;
		boardToReload.reload(parser);
	}

	/**
	 * Close the given board, or the currently active board if "board" is null.
	 * @return false if operation failed
	 * @since jPicEdt 1.3.2
	 */
	public static boolean closeBoard(PEDrawingBoard board){

		if (board == null) board = getMDIManager().getActiveDrawingBoard();
		if (board == null) return false; // no active board

		// simulate a click on the close button
		getMDIManager().closeBoard(board);
		return true;
	}

	/**
	 * Insère un fragment chagé du disque dans la plance active.
	 * @param path le chemin du fragment sur le disque ; si null, invite l'utilisateur à le saisir.
	 * @param insertionPoint point d'insertion du fragment ; si null, l'insertion est à (0,0).
	 * @since jPicEdt 1.3.2
	 */
	public static void insertFragment(String path,PicPoint insertionPoint){
		PEDrawingBoard targetBoard = getMDIManager().getActiveDrawingBoard();
		if (targetBoard==null) return;
		// first possibly fetch file name
		if (path == null){
			path = PEFileChooser.getFileName(PEFileChooser.OPENFRAGMENT,null);
			if (path == null) return; // CANCEL
		}
		getMDIManager().showStatus(localize("action.ui.InsertFragment") + " " + path
								   + (insertionPoint == null ? "" : (" @" + insertionPoint.toString())));
		targetBoard.insert(path,parser,insertionPoint);
	}

	/**
	 * Save the active board to disk ; prompt user if this board hasn't been saved yet, or
	 * if alwaysPromptUser is true (e.g. used as a "save as..." action)
	 * @since jPicEdt 1.3.2
	 */
	public static void saveBoard(boolean alwaysPromptUser){

		PEDrawingBoard boardToSave = getMDIManager().getActiveDrawingBoard();
		if (boardToSave == null) return;

		getMDIManager().showStatus(localize("msg.Saving")+" [" + boardToSave.getTitle() + "]");

		boolean result = boardToSave.save(alwaysPromptUser); // doesn't prompt user if this board's already been saved to disk
		if (result == false) return; // cancelled

		// update JInternalFrame/JFrame/... title by remove trailing "*" from title
		String title  = "JPicEdt: " + boardToSave.getTitle() + " [" + boardToSave.getCanvas().getContentType().getPresentationName() + "]";
		getMDIManager().setHostingContainerTitle(title, boardToSave);
		getMDIManager().showStatus(localize("msg.SaveCompleted"));

		// update list of recent files in preferences, then update menu items accordingly:
		MiscUtilities.addRecentFile(preferences, boardToSave.getTitle());

		// update GUI menus
		getMDIManager().updateWindowMenu(); // in case board's title has changed
		getMDIManager().updateRecentFilesSubmenu();

		// update actions's state
		actionRegistry.updateActionState();
		getMDIManager().updateMenuBar();
	}

	/**
	 * Save all open boards to disk, possibly prompting user for a file name for those boards which
	 * haven't been saved to disk before.
	 * @since jPicEdt 1.3.2
	 */
	public static void saveAllBoards(){

		for (PEDrawingBoard bd: getMDIManager().getAllDrawingBoards()){
			if (!bd.isDirty()) continue; // if clean, no need for saving
			bd.setVisible(true); // ok, this one is dirty, bring to front
			boolean result = bd.save(false); // dont' prompt user for file name if not necessary
			if (result == false) continue; // cancel occured or I/O error
			// update JInternalFrame/JFrame/... title by remove trailing "*" from title
			String title  = "JPicEdt: " + bd.getTitle() + " [" + bd.getCanvas().getContentType().getPresentationName() + "]";
			getMDIManager().setHostingContainerTitle(title, bd);
			getMDIManager().showStatus(localize("msg.SaveCompleted"));
			// update GUI:
			MiscUtilities.addRecentFile(preferences, bd.getTitle());
		}
		getMDIManager().updateRecentFilesSubmenu();
		getMDIManager().updateWindowMenu();
		// update actions state, according to the currently active board:
		actionRegistry.updateActionState();
		getMDIManager().updateMenuBar();
	}

	/**
	 * Save the active board selection as a fragment to disk
	 * @since jPicEdt 1.3.2
	 */
	public static void saveFragment(){

		PEDrawingBoard board = getMDIManager().getActiveDrawingBoard();
		if (board == null) return;
		getMDIManager().showStatus(localize("msg.SavingSelection"));
		board.saveFragment();
		getMDIManager().showStatus(localize("msg.SaveCompleted"));
	}






	//////////////////////////////////////////////////////////////////////
	//// ACTIONS
	//////////////////////////////////////////////////////////////////////

	/**
	 * Immutable ActionDispatcher that provides PEAction's targets by delegating to the
	 * currently installed MDIManager.
	 */
	private static class PEActionDispatcher implements ActionDispatcher {


		public PEActionDispatcher(){
		}

		/**
		 * @return the PECanvas upon which a PEAction should act.<p>
		 */
		public jpicedt.graphic.PECanvas getTarget(){
			PEDrawingBoard brd = getMDIManager().getActiveDrawingBoard();
			if (brd == null) return null;
			return brd.getCanvas();
		}
	}

	/**
	 * save a frame content to disk, performing I/O operations in a separate thread
	 * @since PicEdt 1.3.2
	 */
	public static class FileSaveAction extends PEAction {

		public static final String KEY="action.ui.Save";
		private PEDrawingBoard board;

		/**
		 * save the content of the active board.
		 */
		public FileSaveAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
			board=null;
		}

		/**
		 * save the content of the given board<p>
		 * This is useful when closing a frame, where we know the name of the frame to be saved.
		 */
		public FileSaveAction(PEDrawingBoard board){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
			this.board = board;
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.saveBoard(false); // only prompt user if necessary
			// don't use board ??? [underway]
		}
	}

	/**
	 * Save all open boards to disk. This action is not asynchronous.
	 * @since PicEdt 1.2.1
	 */
	public static class FileSaveAllAction extends PEAction {

		public static final String KEY="action.ui.SaveAll";

		public FileSaveAllAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.saveAllBoards();
		}
	}

	/**
	 * Save the active board to disk with a new file name, performing I/O operations in a separate thread
	 * @since PicEdt 1.2.1
	 */
	public static class FileSaveAsAction extends PEAction {

		public static final String KEY="action.ui.SaveAs";

		/**
		 * Save the active internal frame with a new file name
		 */
		public FileSaveAsAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.saveBoard(true); // always prompt user
		}
	}

	/**
	 * save the selection content of the active board to disk as a "fragment"
	 * @since PicEdt 1.2.1
	 */
	public static class FragmentSaveAction extends PEAction {

		public static final String KEY="action.ui.SaveSelectionAs";

		public FragmentSaveAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.saveFragment();
		}
	}

	/**
	 * Open a board from disk, performing I/O operations in a separate thread
	 * @since PicEdt 1.2.1
	 */
	public static class FileOpenAction extends PEAction {

		public static final String KEY = "action.ui.Open";
		private String fileName;

		/**
		 * load a file with the given filename
		 */
		public FileOpenAction(String fileName){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
			this.fileName=fileName;
		}

		/**
		 * prompt user
		 */
		public FileOpenAction(){
			this(null);
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.openBoard(fileName); // prompt user for path name
		}
	}

	/**
	 * Create a new board from scratch, performing operation in a separate thread.
	 * @since PicEdt 1.2.1
	 */
	public static class FileNewAction extends PEAction {

		public static final String KEY="action.ui.New";

		public FileNewAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.newBoard();
		}
	}

	/**
	 * Insert a fragment into the active board, performing I/O operation in a separate thread.
	 * <p>This action is undoable.
	 * @since PicEdt 1.2.1
	 */
	public static class FragmentInsertAction extends PEAction {

		public static final String KEY="action.ui.InsertFragment";
		private String   fileName;
		private PEPopupMenuFactory.PopupMenu poppedUpFrom;

		/**
		 * insert a fragment from the given file name
		 */
		public FragmentInsertAction(String fileName,PEPopupMenuFactory.PopupMenu poppedUpFrom){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
			this.fileName = fileName;
			this.poppedUpFrom = poppedUpFrom;
		}

		/**
		 * Insère le fragment au point (0,0)
		 */
		public FragmentInsertAction(String fileName){
			this(fileName,null);
		}

		public FragmentInsertAction(){
			this(null,null);
		}

		public void undoableActionPerformed(ActionEvent e){
			PicPoint insertionPoint = null;
			if(poppedUpFrom != null) insertionPoint = poppedUpFrom.getPicPoint();
			JPicEdt.insertFragment(fileName,insertionPoint);
		}
	}

	/**
	 * Reload the active board from disk, performing I/O operation in a separate thread.
	 * @since PicEdt 1.2.1
	 */
	public static class FileReloadAction extends PEAction {

		public static final String KEY = "action.ui.Reload";

		public FileReloadAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void undoableActionPerformed(ActionEvent e){
			JPicEdt.reloadBoard();
		}
	}

	/**
	 * Close the active board
	 * @since PicEdt 1.2.1
	 */
	public static class FileCloseAction extends PEAction {

		public static final String KEY = "action.ui.Close";

		public FileCloseAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.closeBoard(null);
		}
	}

	/**
	 * Exit jPicEdt
	 * @since PicEdt 1.2.1
	 */
	public static class ExitAction extends PEAction  {

		public static final String KEY = "action.ui.Exit";

		public ExitAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){

			for (PEDrawingBoard bd: getMDIManager().getAllDrawingBoards()){

				// if this frame hasn't been modified since its last save, we just skip it.
				if (!bd.isDirty()) continue;

				// else we raise it and ask for a confirmation:
				getMDIManager().selectDrawingBoard(bd);
				int n = getMDIManager().showConfirmDialog(
				                                      bd.getTitle() + ": " + localize("msg.SaveChanges"),
				                                      localize("action.ui.Exit") + "...",
				                                      JOptionPane.YES_NO_CANCEL_OPTION);

				if (n == JOptionPane.YES_OPTION)
					bd.save(false); // don't always prompt user
				else if (n == JOptionPane.NO_OPTION)
					continue; // NO ? OK, we don't save it and go ahead to next frame (if any).
				else
					return;  // The first CANCEL action aborts the whole process.
			}
			// now that all frames have been saved, and we save preferences to disk before really exiting...
			getMDIManager().saveGeometry();
			storePreferences();
			SystemOutUtilities.instance().redirect(SystemOutUtilities.STANDARD);
			// clean tmp dir from .log,.dvi,.aux etc...
			RunExternalCommand.cleanTmpDir();
			System.exit(0); // close any open FileStream
		}
	}

	// ---- undo/redo ----

	/**
	 * Undoes last action
	 */
	public static class UndoAction extends PEAction {

		public static final String KEY = "action.ui.Undo";

		public UndoAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			try {
				getCanvas().undo();
			}
			catch (javax.swing.undo.CannotUndoException ex){
				//if (DEBUG)
				ex.printStackTrace();
			}
			String redoName = getCanvas().getRedoPresentationName();
			String undoName = getCanvas().getUndoPresentationName();
			getMDIManager().updateUndoRedoMenus(undoName,redoName);
			actionRegistry.updateActionState();
		}
	}

	/**
	 * Redoes last action
	 * @since PicEdt 1.2.1
	 */
	public static class RedoAction extends PEAction {

		public static final String KEY = "action.ui.Redo";

		public RedoAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			try {
				getCanvas().redo();
			}
			catch (javax.swing.undo.CannotRedoException ex){
				//if (DEBUG)
				ex.printStackTrace();
			}
			String redoName = getCanvas().getRedoPresentationName();
			String undoName = getCanvas().getUndoPresentationName();
			getMDIManager().updateUndoRedoMenus(undoName,redoName);
			actionRegistry.updateActionState();
		}
	}


	/**
	 * Cascades internal frames
	 * @since PicEdt 1.2.1
	 */
	public static class WindowCascadeAction extends PEAction {

		public static final String KEY = "action.ui.Cascade";

		public WindowCascadeAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.getMDIManager().cascadeDrawingBoards();
		}
	}

	/**
	 * Tiles internal frames horizontally
	 * @since PicEdt 1.2.1
	 */
	public static class WindowTileHorAction extends PEAction {

		public static final String KEY ="action.ui.TileHorizontal";

		public WindowTileHorAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.getMDIManager().tileDrawingBoardsHorizontally();
		}
	}

	/**
	 * Tiles internal frames vertically
	 * @since PicEdt 1.2.1
	 */
	public static class WindowTileVertAction extends PEAction {

		public static final String KEY = "action.ui.TileVertical";
		public WindowTileVertAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			JPicEdt.getMDIManager().tileDrawingBoardsVertically();
		}
	}


	/**
	 * Redraws (aka repaint()) active internal frame
	 * @since PicEdt 1.2.1
	 */
	public static class RedrawAction extends PEAction {

		public static final String KEY = "action.ui.Redraw";
		public RedrawAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}
		public void actionPerformed(ActionEvent e){
			getCanvas().repaint();
		}
	}

	/**
	 * Opens the "about..." dialog box
	 * @since PicEdt 1.2.1
	 */
	public static class AboutAction extends PEAction {

		public static final String KEY = "action.ui.About";

		public AboutAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			new DialogAbout(getMDIManager());
		}
	}

	/**
	 * Set the format of the currently active board
	 * @since PicEdt 1.2.1
	 */
	public static class PageFormatAction extends PEAction {

		public static final String KEY = "action.ui.PageFormat";

		public PageFormatAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas() == null){ // change preferences
				AbstractCustomizer cz = PageFormat.createCustomizer(preferences);
				boolean modal = true;
				EnumSet<CustomizerDialog.ButtonMask> buttonMask = EnumSet.of(CustomizerDialog.ButtonMask.ALL);
				CustomizerDialog dlg = getMDIManager().createCustomizerDialog(cz,modal,buttonMask);
				dlg.setVisible(true);
			}
			else { // change current drawing board
				PageFormat pf = getCanvas().getPageFormat();
				AbstractCustomizer customizer = pf.createCustomizer(getCanvas());
				boolean modal = true;
				EnumSet<CustomizerDialog.ButtonMask> buttonMask = EnumSet.of(CustomizerDialog.ButtonMask.ALL);
				CustomizerDialog dlg = getMDIManager().createCustomizerDialog(customizer,modal,buttonMask);
				dlg.setVisible(true);
				//if (dlg.isCancelled()) return;
				//getCanvas().setPageFormat(pf); // uddate new page format iff "ok" was pressed
				// now done from inside PageFormat
			}
		}
	}

	/**
	 * Set the content-type of the currently active board
	 * @since PicEdt 1.2.1
	 */
	public static class ContentTypeAction extends PEAction {

		private int contentTypeIndex;
		/**
		 * @param contentTypeIndex index in array returned by MiscUtilities.getAvailableContentTypes()
		 */
		public ContentTypeAction(int contentTypeIndex){
			super(actionDispatcher,MiscUtilities.getAvailableContentTypesNames()[contentTypeIndex],null); // no localizer -> display content type presentation name
			this.contentTypeIndex = contentTypeIndex;
		}

		public void actionPerformed(ActionEvent e){
			// Note: lorsqu'il n'y a aucune
			PEDrawingBoard board = getMDIManager().getActiveDrawingBoard();
			if (board != null){
                // change for current drawing board
				String className = MiscUtilities.getAvailableContentTypes()[contentTypeIndex];
				jpicedt.graphic.ContentType ctype = MiscUtilities.getContentTypeFromClassName(className);
				if (ctype != null) {
					ctype.configure(preferences);
					PECanvas canvas = board.getCanvas();
					if(canvas.getContentType() != ctype)
					{
						canvas.setContentType(ctype);
						board.setDirty(true);
						// update frame/internal frame title:
						String title  = "JPicEdt: " + board.getTitle()
					                + "* [" + ctype.getPresentationName() + "]";
						getMDIManager().setHostingContainerTitle(title, board); // update frame title
					}
					actionRegistry.updateActionState();
				}
			}
		}
	}

	/**
	 * Display memory information
	 * @since PicEdt 1.2.1
	 */
	public static class MemoryMonitorAction extends PEAction {

		public static final String KEY = "action.ui.MemoryMonitor";

		public MemoryMonitorAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			Runtime r = Runtime.getRuntime();
			int free = (int)(r.freeMemory()/1024L);
			int total = (int)(r.totalMemory()/1024L);
			String message = "Free memory: " + Integer.toString(free) + " kB\n" +
			                 "Total memory: " + Integer.toString(total) + " kB";
			getMDIManager().showMessageDialog(
			                              message,
			                              localize("action.ui.MemoryMonitor"),
			                              JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Toggle the visibility of the attributes editor
	 * @since jpicedt 1.3.2-beta9
	 */
	public static class ToggleAttributesPanelAction extends PEAction {

		public static final String KEY = "action.ui.ToggleAttributesPanel";

		public ToggleAttributesPanelAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			getMDIManager().toggleDockablePanel(DockableAttributesCustomizer.KEY);
		}
	}


	/**
	 * Toggle the visibility of the toolkit panel
	 * @since jpicedt 1.4pre2
	 */
	public static class ToggleToolkitPanelAction extends PEAction {

		public static final String KEY = "action.ui.ToggleToolkitPanel";

		public ToggleToolkitPanelAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			getMDIManager().toggleDockablePanel(DockableEditorKitToolBar.KEY);
		}
	}

	/**
	 * Toggle the visibility of the convexe zone palette
	 * @since jpicedet 1.6
	 */
	public static class ToggleConvexZonePanelAction extends PEAction {

		public static final String KEY = "action.ui.ToggleConvexZonePanel";

		public ToggleConvexZonePanelAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			getMDIManager().toggleDockablePanel(DockableConvexZoneToolBar.KEY);
		}
	}

	/**
	 * Open the "preferences" dialog box
	 * @since PicEdt 1.2.1
	 */
	public static class PreferencesAction extends PEAction {

		public static final String KEY = "action.ui.Preferences";

		public PreferencesAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){

			ArrayList<AbstractCustomizer> customizersList = new ArrayList<AbstractCustomizer>();
			customizersList.add(new GeneralCustomizer(preferences));
			customizersList.add(new RenderingCustomizer(preferences));
			customizersList.add(new GridZoomCustomizer(preferences));
			customizersList.add(new UIColorCustomizer(preferences));
			customizersList.add(new DirectoriesCustomizer(preferences));
			customizersList.add(new ShortcutsCustomizer(preferences));
			customizersList.add(RunExternalCommand.createCustomizer(preferences));
			customizersList.add(PageFormat.createCustomizer(preferences));

			String[] contentTypeClassNames = MiscUtilities.getAvailableContentTypes();
			for (String contentTypeClassName : contentTypeClassNames){
				ContentType contentType = MiscUtilities.getContentTypeFromClassName(contentTypeClassName);
				if (contentType != null) {
					AbstractCustomizer cust  = contentType.createCustomizer(preferences);
					if (cust != null) customizersList.add(cust);
				}
			}
			EnumSet<CustomizerDialog.ButtonMask> buttonMask = EnumSet.of(CustomizerDialog.ButtonMask.ALL);
			boolean modal = true;
			CustomizerDialog dlg = getMDIManager().createCustomizerDialog(customizersList,0,localize("action.ui.Preferences"),modal,buttonMask);
			dlg.setVisible(true);
			getMDIManager().update();
			// update LAF:
			jpicedt.ui.LAFManager.updateLaf();
			// update ViewFactory's shared static properties
			jpicedt.graphic.view.DefaultViewFactory.configure(preferences);
			// update script menu (user script dir. may have changed):
			getMDIManager().updateScriptsMenu();
			// clean tmp dir in case tmpDir has changed
			RunExternalCommand.cleanTmpDir();
			// store preferences on disk:
			storePreferences();
		}
	} // class

	/**
	 * run an external process
	 * @since PicEdt 1.2.1
	 */
	public static class RunExternalCommandAction extends PEAction {
		private RunExternalCommand.Command command;

		/**
		 * @param command one of various availables commands, as specified in class RunExternalCommand.
		 */
		public RunExternalCommandAction(RunExternalCommand.Command command){
			super(actionDispatcher,command.getName(),Localizer.currentLocalizer().getActionLocalizer());
			this.command = command;
		}

		public void actionPerformed(ActionEvent e){
			if (getDrawing()==null) return;
			RunExternalCommand c = new RunExternalCommand(getDrawing(),getCanvas().getContentType(), command, getMDIManager());
			c.openUI();
			c.start(); // run external process in separate thread
		}
	}

	public static final String MANUAL_FORMAT_KEY="manual.format";
	public static final String MANUAL_FORMAT_HTML_ONE_FILE="HTML one file";
	public static final String MANUAL_FORMAT_HTML         ="HTML";
	public static final String MANUAL_FORMAT_PDF          ="PDF";


	/**
	 * open a frame containing the help (HTML formatted) page.
	 * @since PicEdt 1.2.1
	 */
	public static class ShowHelpAction extends PEAction {

		public static final String KEY = "action.ui.ShowHelp";

		public ShowHelpAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			Locale l = Localizer.currentLocalizer().getCurrentLocale(); // guaranteed to return a locale that is supported by jpicedt
			String langKey = l.getLanguage(); // two-letter ISO 639 code, e.g., "fr" or "en" or "pt"...

			String manualFormat = getProperty(MANUAL_FORMAT_KEY,MANUAL_FORMAT_HTML);
			int fallback = -1;
			String[] fallBacks = {MANUAL_FORMAT_HTML_ONE_FILE, MANUAL_FORMAT_HTML, MANUAL_FORMAT_PDF };
			for(int i=0; i < fallBacks.length; ++i)
			{
				if(manualFormat.equals(fallBacks[i]))
				{
					for(int j = i; j>= 1; --j)
						fallBacks[j] = fallBacks[j-1];
					fallback = 0;
					break;
				}
			}
			if(fallback == -1)
			{
				manualFormat = fallBacks[0];
				fallback = 0;
			}

			for(;;)
			{
				if(manualFormat.equals(MANUAL_FORMAT_HTML_ONE_FILE))
				{
					String helpFile = "/help-files/" + langKey + ".html";
					if(HtmlViewer.fileExits(helpFile))
					{
						HtmlViewer hv = new HtmlViewer(helpFile,localize("misc.PicEdtHelp"),getMDIManager());
						break;
					}
				}
				else if(manualFormat.equals(MANUAL_FORMAT_HTML))
				{
					String helpFile = "/help-files/" + langKey + "/index.html";
					if(HtmlViewer.fileExits(helpFile))
					{
						HtmlViewer hv = new HtmlViewer(helpFile,localize("misc.PicEdtHelp"),getMDIManager());
						break;
					}
				}
				else if(manualFormat.equals(MANUAL_FORMAT_PDF))
				{
					String helpFile = "/help-files/" + langKey + ".pdf";
					if(HtmlViewer.fileExits(helpFile))
					{
						HtmlViewer hv = new HtmlViewer(helpFile,localize("misc.PicEdtHelp"),getMDIManager());
						break;
					}
				}
				++fallback;
				if(fallback < fallBacks.length)
					manualFormat = fallBacks[fallback];
				else
					break;
			}
			if(fallback == fallBacks.length)
				getMDIManager().showMessageDialog(localize("exception.CantFindManual"),
												  localize("misc.PicEdtHelp"),
												  JOptionPane.ERROR_MESSAGE);
			else if(fallback > 0)
				getPreferences().setProperty(MANUAL_FORMAT_KEY,fallBacks[fallback]);

		}
	}

	/**
	 * open a frame containing the error.log file
	 * @since jPicEdt 1.3.2
	 */
	public static class ShowErrorLogAction extends PEAction {

		public static final String KEY = "action.ui.ShowErrorLog";

		public ShowErrorLogAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			File errorLog = new File(getUserSettingsDirectory(),"error.log");
			if (!errorLog.exists()) {
				getMDIManager().showMessageDialog("No error.log !",localize("action.ui.ShowErrorLog"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				HtmlViewer hv = new HtmlViewer(errorLog.toURI().toURL(),localize("action.ui.ShowErrorLog"),getMDIManager());
			}
			catch (MalformedURLException ex){ex.printStackTrace();}
		}
	}

	/**
	 * open a frame containing the (HTML formatted) GPL license page.
	 * [pending 2005/12] add localized versions from the FSF website, and put'em into language
	 * specific dirs inside /help-files/.
	 * @since PicEdt 1.2.1
	 */
	public static class ShowLicenseAction extends PEAction {

		public static final String KEY = "action.ui.ShowLicense";

		public ShowLicenseAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			HtmlViewer hv = new HtmlViewer("/help-files/license.html","CeCILL v2 license agreement",
										   getMDIManager());
		}
	} // class

	/**
	 * Display the LaTeX content of the active board in a Dialog Box
	 * @since PicEdt 1.2.1
	 */
	public static class ViewLaTeXFileAction extends PEAction {

		public static final String KEY = "action.ui.ViewLaTeXFile";

		/**
		 * display the whole LaTeX file in a Dialog Box
		 */
		public ViewLaTeXFileAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			new ViewFormattedStringDialog(getCanvas(),getMDIManager());
		}
	} // class

	/**
	 * Open a BeanShell console
	 * @since PicEdt 1.3.2
	 */
	public static class OpenBSHConsoleAction extends PEAction {

		public static final String KEY = "action.ui.OpenBSHConsole";

		public OpenBSHConsoleAction(){
			super(actionDispatcher,KEY,Localizer.currentLocalizer().getActionLocalizer());
		}

		public void actionPerformed(ActionEvent e){
			new jpicedt.ui.util.BeanShell(getMDIManager());
		}
	} // class


	//////////////////////////////////////////////////////////////////
	//// SOME USEFUL ACCESSOR e.g. FOR BSH MACROS
	//////////////////////////////////////////////////////////////////
	/**
	 * Return the Properties object that holds user preferences across the entire application.
	 */
	public static Properties getPreferences(){
		return preferences;
	}

	/**
	 * Return an ActionMap that holds GUI action for the entire application.
	 */
	public static ActionRegistry getActionRegistry(){
		return actionRegistry;
	}
	/**
	 * Return the default parser used for parsing standard JPicEdt's formats, ie currently LaTeX,
	 * epic/eepic, and PsTricks.
	 */
	public static jpicedt.graphic.io.parser.Parser getParser(){
		return parser;
	}

	/**
	 * Return the currently active PEDrawingBoard, or null if none is active.
	 */
	public static PEDrawingBoard getActiveDrawingBoard(){
		return getMDIManager().getActiveDrawingBoard();
	}

	/**
	 * Return the currently active PECanvas (ie hosted by the currently active PEDrawinBoard),
	 * or null if no board is currently active.
	 */
	public static jpicedt.graphic.PECanvas getActiveCanvas(){
		return actionDispatcher.getTarget();
	}

	/**
	 * Return the EditorKit attached to the currently active PECanvas (ie hosted by the currently active PEDrawinBoard),
	 * or null if no board is currently active.
	 */
	public static jpicedt.graphic.toolkit.EditorKit getActiveEditorKit(){
		jpicedt.graphic.PECanvas canvas = actionDispatcher.getTarget();
		if (canvas != null) return canvas.getEditorKit();
		else return null;
	}

	/**
	 * Return the Drawing attached to the currently active PECanvas (ie hosted by the currently active PEDrawinBoard),
	 * or null if no board is currently active.
	 */
	public static jpicedt.graphic.model.Drawing getActiveDrawing(){
		jpicedt.graphic.PECanvas canvas = actionDispatcher.getTarget();
		if (canvas != null) return canvas.getDrawing();
		else return null;
	}

	/**
	 * Return a static reference to the local ClipBoard shared across all instance of this EditorKit.
	 * This may be, for example, given as an argument to "copy()" and related methods in PECanvas.
	 * Alternatively, one can use the System's clipboard (see java.awt.Toolkit), yet some information may
	 * be lost since this later mechanism relies on formatting/parsing.
	 */
	public static Clipboard getClipboard(){
		return EditorKit.getClipboard();
	}


	//////////////////////////////////////////////////////////////////
	//// ACCESSING USER PREFERENCES
	//////////////////////////////////////////////////////////////////
	/**
	 * Convenience for retrieving a boolean property from User's preferences
	 * @return a boolean built from the value fetched from the given key,
	 * or the "def" value if the key wasn't found
	 */
	public static boolean getProperty(String key, boolean def){
		return MiscUtilities.parseProperty(preferences,key,def);
	}
	/**
	 * Convenience for retrieving a Colour property from User's preferences
	 * @return a Color built from the value fetched from the given key,
	 * or the "def" value if the key wasn't found
	 */
	public static Color getProperty(String key, Color def){
		return MiscUtilities.parseProperty(preferences,  key,  def);
	}

	/**
	 * Convenience for retrieving a double property from User's preferences
	 * @return a double parsed from the value associated with the given key in the given Properties.
	 *         returns "def" in key wasn't found, or if a parsing error occured. If "value" contains
	 *         a "%" sign, we use a <code>NumberFormat.getPercentInstance</code> to convert it to a double.
	 */
	public static double getProperty(String key, double def){
		return MiscUtilities.parseProperty(preferences,  key,  def);
	}

	/**
	 * Convenience for retrieving a int property from User's preferences
	 * @return an integer parsed from the value associated with the given key, or "def" in key wasn't found.
	 */
	public static int getProperty(String key, int def){
		return MiscUtilities.parseProperty(preferences,  key,  def);
	}

	/**
	 * Convenience for retrieving a String property from User's preferences
	 * @return a String parsed from the value associated with the given key, or "def" in key wasn't found.
	 */
	public static String getProperty(String key, String def){
		return preferences.getProperty(key,def);
	}


	/**
	 * Commodité pour retiré une propriété oui/no/me-demander des préférences utilisateur.
	 * @param key Clef identifiant la propriété
	 * @param def Valeur par défaut à utiliser si la clef n'est pas définie.
	 * @return Une valeur <code>YesNoAskMe</code>.
	 * @since jPicEdt 1.6
	 */
	public static YesNoAskMe getProperty(String key, YesNoAskMe def){
		return YesNoAskMe.toYesNoAskMe(preferences.getProperty(key,def.toString()));
	}

	public static void setProperty(String key, YesNoAskMe value){
		preferences.setProperty(key,value.toString());
	}

	/**
	 * @return the user-settings directory, for instance "~/.jpicedt" on unix.
	 */
	public static String getUserSettingsDirectory(){
		return userSettingsDirectory;
	}

	/** key used to fetch the user's temp dir from a Properties object */
	public final static String KEY_TMPDIR = "app.tmpdir";

	/** default platform tmp dir */
	public final static String DEFAULT_TMPDIR = System.getProperty("java.io.tmpdir");

	/**
	 * @return the user tmp dir, or the platform standard tmp dir if user didn't set one,
	 * or null if none is standardly defined.
	 */
	public static File getTmpDir(){
		File f=null;
		if (preferences==null) return new File(DEFAULT_TMPDIR);
		String tmpDir = getPreferences().getProperty(KEY_TMPDIR); // user tmp dir
		if (tmpDir==null)  tmpDir = DEFAULT_TMPDIR;
		if (tmpDir==null) f=null;
		else f= new File(tmpDir);
		//System.out.println("getTmpDir="+f);
		return f;
	}
	/**
	 * set the current tmp dir
	 */
	public static void setTmpDir(String location){
		getPreferences().setProperty(KEY_TMPDIR,location);
	}

	//////////////////////////////////////////////////////////////////
	//// MEMBER VARIABLES
	//////////////////////////////////////////////////////////////////

	private JPicEdt(){} // can't be instanciated

	/* DEBUG_PAINTING = true -> install our own DebugRepaintManager so that we can trace calls to paint() method, dirty regions,etc...
	 it's wise to launch jpicedt with the -debug option in this case (otherwise, you'll get messages printed out to a JDialogBox
	 which can become very annoying) */
	private static final boolean DEBUG_PAINTING = false;

	/* DEBUG_FOCUS = true -> install our own DebugFocusManager so as to debug pb linked with FocusEvent's
	   it's wise in this case to launch jpicedt with -redir=standard (see note above) */
	private static final boolean DEBUG_FOCUS = false;

	private static MDIManager mdiManager;
	private static ActionDispatcher actionDispatcher = new PEActionDispatcher();
	private static int numberOfNoNameBoard = 0; // Number of boards open but not saved yet...
	private static ActionRegistry actionRegistry; // action hash table
	private static Properties preferences = new Properties(); // new Properties(defaults) // [pending] if preferences was not loaded properly, we should provide a default properties

	// user settings:
	private static String userSettingsDirectory;
	private static String preferencesFileName;

	// global extraction/parsing engine:
	private static jpicedt.graphic.io.parser.ExtractionParsing parser;

	// result of command line parsing
	private static int logRedirection = SystemOutUtilities.FILE;
	private static ArrayList<String> fileToLoadOnStartup = new ArrayList<String>();

} // class
