// PEDrawingBoard.java --- -*- coding: iso-8859-1 -*-
// January 12, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: PEDrawingBoard.java,v 1.32 2013/03/27 07:20:05 vincentb1 Exp $
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
package jpicedt.ui;

import jpicedt.Localizer;
import jpicedt.MiscUtilities;
import jpicedt.graphic.*;
import jpicedt.graphic.grid.Grid;
import jpicedt.graphic.event.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.io.parser.ExtractionParsing;
import jpicedt.graphic.io.parser.Parser;
import jpicedt.graphic.io.parser.ParsedDrawing;
import jpicedt.graphic.io.parser.ParserException;
import jpicedt.ui.dialog.*;
import jpicedt.ui.util.*;
import jpicedt.widgets.*;
import jpicedt.format.output.util.FormatterException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;

import static jpicedt.Localizer.*;
import static jpicedt.Log.*;
/**
 * A drawing board based on jpicedt.graphic.PECanvas, with an embeded scrollpane, and
 * some I/O facilities (saving and loading) which <code>PECanvas</code> doesn't have.<br>
 * There are two accessors two retrieve the embedded canvas and scrollpane.
 * @since jPicEdt
 * @author Sylvain Reynal
 */
public class PEDrawingBoard extends JPanel {


	private boolean isDirty; // true if it's been modified since the last save operation or creation
	private String title; // full file name with path if saved, otherwise the adequate localization of "untitled" + index
	private boolean isSaved; // true if this board's already been saved to disk
	private PECanvas canvas;
	private PEScrollPane scrollPane;
	//private EditorKitToolBar editorKitToolbar; // deprecated, we now use DockableEditorKitToolBar
	private PopupMenuFactory popupFactory;
	private StatusBar statusBar;
	private GridZoomToolBar gridZoomToolbar;
	private DefaultActionDispatcher actionDispatcher;
	private final NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US); // for zoom

	/////////////////////////////////////////////////////
	//// CONSTRUCTORS
	/////////////////////////////////////////////////////

	/**
	 * Create a new empty drawing board with a (localized) "untitled" title
	 * <br><b>author:</b> Sylvain Reynal
	 * @param untitledIndex index appended to "untitled", e.g. "Untitled 2"
	 * @since jPicEdt
	 */
	public PEDrawingBoard(int untitledIndex,double zoom, PageFormat pageFormat, Grid grid, ContentType contentType){
		isDirty = false;
		isSaved = false;
		title =localize("misc.Noname") + untitledIndex;
		init(zoom, pageFormat, grid, contentType);
	}

	/**
	 * Create a new drawing board pre-loaded from the given path ; set title from this path.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public PEDrawingBoard(String path,double zoom, PageFormat pageFormat, Grid grid, ContentType contentType,ExtractionParsing parser){
		isDirty = false;
		isSaved = true;
		title = path;
		init(zoom, pageFormat, grid, contentType);
		load(path,parser);
	}

	/**
	 * <p>Create a new empty drawing board with a (localized) "untitled" title
	 * Init zoom, pageFormat, contentType and undoableLimit from the given Properties, using:</p><p>
	 * <ul><li> key = <code>PECanvas.KEY_ZOOM</code>, value = a double</li>
	 * <li> key = <code>PageFormat.KEY_PAGE_FORMAT</code> (see {@link
	 * jpicedt.graphic.PageFormat PageFormat} for details)</li>
	 * <li> key = <code>PECanvas.KEY_CONTENT_TYPE</code>, value = path of
	 *  <code>ContentType</code> class
	 *  (e.g. "jpicedt.format.latex.ContentType")</li>
	 * <li> key = <code>PECanvas.KEY_UNDOABLE_STEPS</code>, value = nb of
	 *   undoable events to remember suitable for instanciation by
	 *   Class.forName("xxx").newInstance().  "preferences" is also used to
	 *   init the <code>ContentType</code>, if non-<code>null</code>.</li></ul></p>
	 * <br><b>author:</b> Sylvain Reynal
	 * @param untitledIndex index appended to "untitled", e.g. "Untitled 2"
	 * @since jPicEdt
	 */
	public PEDrawingBoard(int untitledIndex, Properties preferences){
		double zoom = MiscUtilities.parseProperty(preferences, PECanvas.KEY_ZOOM, PECanvas.ZOOM_DEFAULT);
		PageFormat pf = new PageFormat(preferences);
		Grid grid = new Grid(preferences);
		ContentType ct = MiscUtilities.getContentTypeFromClassName(preferences.getProperty(PECanvas.KEY_CONTENT_TYPE));
		if (ct != null) ct.configure(preferences);
		isDirty = false;
		isSaved = false;
		title =localize("misc.Noname") + untitledIndex;
		init(zoom, pf, grid, ct);
		int undoLimit = MiscUtilities.parseProperty(preferences, PECanvas.KEY_UNDOABLE_STEPS, PECanvas.MAX_UNDOABLE_STEPS_DEFAULT);
		if (undoLimit<1) undoLimit = PECanvas.MAX_UNDOABLE_STEPS_DEFAULT;
		canvas.setUndoLimit(undoLimit);
		canvas.getRenderingHints().add(MiscUtilities.parseRenderingHints(preferences));
	}

	/**
	 * Create a new drawing board pre-loaded from the given path ; set title from this path.
	 * Init zoom, pageFormat and contentType from the given Properties, using:
	 * <ol><li>key = PECanvas.KEY_ZOOM, value = a double</li>
	 * <li>key = PageFormat.KEY_PAGE_FORMAT (see jpicedt.graphic.PageFormat for details)</li>
	 * <li>key = PECanvas.KEY_CONTENT_TYPE, value = path of ContentType class
	 *   (e.g. "jpicedt.format.latex.ContentType") suitable for instanciation by
	 *   Class.forName("xxx").newInstance().  "preferences" is also used to init the ContentType, if
	 *   non-null.</li><ol>
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public PEDrawingBoard(String path, Properties preferences,ExtractionParsing parser){
		double zoom = MiscUtilities.parseProperty(preferences, PECanvas.KEY_ZOOM, PECanvas.ZOOM_DEFAULT);
		PageFormat pf = new PageFormat(preferences);
		Grid grid = new Grid(preferences);
		ContentType ct = MiscUtilities.getContentTypeFromClassName(preferences.getProperty(PECanvas.KEY_CONTENT_TYPE));
		if (ct != null) ct.configure(preferences);
		isDirty = false;
		isSaved = true;
		title = path;
		init(zoom, pf, grid, ct);
		int undoLimit = MiscUtilities.parseProperty(preferences, PECanvas.KEY_UNDOABLE_STEPS, PECanvas.MAX_UNDOABLE_STEPS_DEFAULT);
		if (undoLimit<1) undoLimit = PECanvas.MAX_UNDOABLE_STEPS_DEFAULT;
		canvas.setUndoLimit(undoLimit);
		canvas.getRenderingHints().add(MiscUtilities.parseRenderingHints(preferences));
		load(path,parser);
	}

	/**
	 * Init component.
	 */
	private void init(double zoom, PageFormat pageFormat, Grid grid, ContentType contentType){
		this.setLayout(new BorderLayout(5,5));

		canvas = new PECanvas(zoom, pageFormat, grid, contentType);

		scrollPane = new PEScrollPane(canvas);
		actionDispatcher = new DefaultActionDispatcher(canvas);
		gridZoomToolbar = new GridZoomToolBar(actionDispatcher,jpicedt.Localizer.currentLocalizer().getActionLocalizer());
		statusBar = new StatusBar();

		add(scrollPane,BorderLayout.CENTER);
		add(gridZoomToolbar, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);

		gridZoomToolbar.install(canvas); // init widgets state

		canvas.getEditorKit().addHelpMessageListener(statusBar); // listens to edit-mode-change
		canvas.addZoomListener(gridZoomToolbar); // update zoom combo box
		canvas.addRotateListener(gridZoomToolbar); // update rotation-angle textfield
		canvas.addPEMouseInputListener(statusBar); // display mouse coords in status bar
	}

	/**
	 * Set the <code>PopupMenuFactory</code> that produces <code>JPopupMenu</code> when a popup-trigger mouse
	 * event occurs on the board.
	 */
	public void setPopupMenuFactory(PopupMenuFactory factory){
		this.popupFactory = factory;
		canvas.getEditorKit().setPopupMenuFactory(popupFactory);
	}

	/**
	 * @return the <code>PopupMenuFactory</code> that produces <code>JPopupMenu</code> when a popup-trigger
	 * mouse event occurs on the board.
	 */
	public PopupMenuFactory getPopupMenuFactory(){
		return popupFactory;
	}


	//////////////////////////////////////////////////////////////
	//// SET/GET
	//////////////////////////////////////////////////////////////


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 */
	public PECanvas getCanvas(){
		return canvas;
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 */
	public PEScrollPane getScrollPane(){
		return scrollPane;
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return Whether this board has been already saved to disk (even if it's been modified since then)
	 * This is just a convenience to know if a file path is attached to this board or not.
	 */
	public boolean isSaved(){
		return isSaved;
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return whether this board has been modified since the last time it was saved or created <p>
	 * This signal that this board need to be saved during "save", "save all",... actions.
	 */
	public boolean isDirty(){
		return isDirty;
	}

	/** Mark this board as being dirty. */
	public void setDirty(boolean dirty){
		this.isDirty = dirty;
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return This board's title, e.g. used as the title of the hosting frame.
	 * @since jPicEdt
	 */
	public String getTitle(){
		return title;
	}


	////////////////////////////////////////////////////////////
	/// I/O MANAGEMENT
	///////////////////////////////////////////////////////////

	/**
	 * Save drawing content to disk.<p>
	 * If <code>alwaysPrompt</code> is <code>false</code>, save to disk using the current title; prompt user
	 * if it's <code>null</code>, ie this board has never been saved.<br>
	 * If <code>alwaysPrompt</code> is <code>true</code>, prompt user for a file name (aka "Save as&hellip;")
	 * in any case.
	 * @return <code>false</code> if operation was cancelled by user or an I/O error occured
	 */
	public boolean save(boolean alwaysPrompt){

		String tmpPath; // temp buffer for file name
		if (!isSaved() || alwaysPrompt){
			tmpPath = PEFileChooser.getFileName(PEFileChooser.SAVEFILE, this.canvas.getContentType().getClass().getName());
			if (tmpPath == null) return false; // cancel has occured
		}
		else tmpPath = title;
		canvas.unSelectAll();
		try{
			try{
				FileWriter fileWriter = new FileWriter(tmpPath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				canvas.write(bufferedWriter, false); // no fragment
				bufferedWriter.close();
				fileWriter.close();
			}
			catch(IOException ioEx){
				DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
				df.showMessageDialog(localize("exception.IOError") + " : " + ioEx.getMessage(), localize("action.ui.Save"), JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		catch(FormatterException fmtEx)
		{
			fmtEx.resolve();
			return false;
		}
		title = tmpPath; // side-effect -> mark as saved to disk
		isSaved = true; // in case this was the first save operation
		isDirty = false; // mark as clean
		return true;
	}

	/**
	 * Save the selection content to the "fragment directory" on disk, prompting user for a file name
	 * @return false if operation was cancelled by user or I/O error occured
	 */
	public boolean saveFragment(){

		String fragmentPath = PEFileChooser.getFileName(PEFileChooser.SAVEFRAGMENT, this.canvas.getContentType().getClass().getName());
		if (fragmentPath == null) return false; // CANCEL !
		try {
			FileWriter fileWriter = new FileWriter(fragmentPath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			canvas.write(bufferedWriter, true); // save fragment only
			bufferedWriter.close();
			fileWriter.close();
		}
		catch(IOException ioEx){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			df.showMessageDialog(localize("exception.IOError") + " : " + ioEx.getMessage(),
			                             localize("action.ui.Save"),
			                              JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Save drawing or selection content the given file.
	 * @param fileName name of file to save content to
	 * @param selectionOnly whether to save the content of the selection or that of the whole the drawing
	 */
	public void save(String fileName, boolean selectionOnly) throws IOException {

		FileWriter fileWriter = new FileWriter(fileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		canvas.write(bufferedWriter, selectionOnly);
		bufferedWriter.close();
		fileWriter.close();
	}

	/**
	 * Load this board's content from the given path
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void load(String path, ExtractionParsing parser){

		/* implementation of a ProgressMonitor (underway) */
		// 	ProgressMonitorInputStream pmon = new ProgressMonitorInputStream(null,
		// 									 "Reading " + fullFileName,
		// 									 new FileInputStream(fullFileName));
		//pmon.getProgressMonitor().setMillisToDecideToPopup(10);
		//pmon.getProgressMonitor().setMillisToPopup(50);
		//	InputStreamReader reader = new InputStreamReader(pmon);

		ParsedDrawing dr = null;

		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			dr = canvas.read(bufferedReader,parser);
			bufferedReader.close();
			fileReader.close();
		}
		catch (ParserException pEx){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			df.showMessageDialog(
			   localize("exception.ParserWarning") + " :\n" + pEx,
			   localize("msg.Parsing"),
			    JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException ioEx){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			df.showMessageDialog(
			   localize("exception.IOError") + "\n" + ioEx.getMessage(),
			   localize("action.ui.Open"),
			    JOptionPane.ERROR_MESSAGE);
		}
		if(dr != null)
		{
			isDirty = dr.isDirty;
			isSaved = true;
			String notParsedCommand = canvas.getDrawing().getNotparsedCommands();
			if (notParsedCommand.length()>0){
				DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
				int res = df.showConfirmDialog(
					"The following lines were not parsed : \n" // [pending] i18n
					+ notParsedCommand
					+ "\n Would you like to DISCARD them ?",
					localize("action.ui.Open"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (res == JOptionPane.YES_OPTION) canvas.getDrawing().setNotparsedCommands("");
			}
		}

	}

	/**
	 * Insert new content from the given path into this board's drawing
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void insert(String path, ExtractionParsing parser,PicPoint insertionPoint){

		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			canvas.insert(bufferedReader,parser,insertionPoint);
			bufferedReader.close();
			fileReader.close();
		}
		catch (ParserException pEx){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			df.showMessageDialog(
			   localize("exception.ParserWarning") + " :\n" + pEx,
			   localize("msg.Parsing"),
			    JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException ioEx){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			df.showMessageDialog(
			   localize("exception.IOError") + "\n" + ioEx.getMessage(),
			   localize("action.ui.InsertFragment"),
			    JOptionPane.ERROR_MESSAGE);
		}
		String notParsedCommand = canvas.getDrawing().getNotparsedCommands();
		if (notParsedCommand.length()>0){
			DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
			int res = df.showConfirmDialog(
				"The following lines were not parsed : \n[" // [pending] i18n
				+ notParsedCommand
				+ "]\n Would you like to DISCARD them ?",
			   localize("action.ui.Open"),
			    JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if (res == JOptionPane.YES_OPTION) canvas.getDrawing().setNotparsedCommands("");
		}
		isDirty = true;
	}

	/**
	 * Reload the content of this board from its current file name if applicable ; do nothing otherwise.
	 * [pending] should raise an exception in the latter case
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void reload(ExtractionParsing parser){
		if (!isSaved()) return;
		load(title,parser);
	}

	/**
	 * Return the status bar
	 */
	public StatusBar getStatusBar(){
		return statusBar;
	}

	/////////////////////////////////// Status Bar //////////////////////////////
	/**
	 * jpicedt's Status Bar
	 *
	 * @author 	Sylvain Reynal
	 * @since       PicEdt 1.0
	 */

	public class StatusBar extends JPanel implements PEMouseInputListener, HelpMessageListener {

		private JLabel mouseCoord; // displays mouse coordinates
		private JLabel toolTip; // displays a tooltip
		private PicPoint ptBuffer=new PicPoint(); // buffer
		private DecimalFormat nf = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));

		// member methods

		/**
		 * mouseCoord | toolTip
		 */
		public StatusBar() {

			nf.setMaximumFractionDigits(2); // mouse coords formatting

			this.setBorder(BorderFactory.createEtchedBorder());

			// init panel :
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			this.setLayout(gbl);

			gbc.fill = GridBagConstraints.HORIZONTAL;

			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;

			mouseCoord = new JLabel("(0.00,0.00)");
			mouseCoord.setBorder(BorderFactory.createLoweredBevelBorder());
			mouseCoord.setFont(new Font("SansSerif", Font.PLAIN, 12));
			mouseCoord.setForeground(Color.black);
			gbl.setConstraints(mouseCoord, gbc);

			toolTip = new JLabel("     ");
			toolTip.setBorder(BorderFactory.createLoweredBevelBorder());
			toolTip.setFont(new Font("SansSerif", Font.PLAIN, 12));
			toolTip.setForeground(Color.black);
			gbc.weightx = 2.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbl.setConstraints(toolTip, gbc);

			this.add(mouseCoord);
			this.add(toolTip);
		}


		/**
		 * Displays a tooltip about current possible mouse-actions
		 * @since PicEdt 1.0
		 */
		public void showMessage(String str){
			StringBuffer buf = new StringBuffer(50);
			buf.append(" ");
			buf.append(str);
			toolTip.setText(buf.toString());
		}

		/**
		 * Displays current mouse coordinates
		 * @since PicEdt 1.0
		 */
		public void showCoord(PicPoint pt){
			StringBuffer buf = new StringBuffer(15);
			buf.append('(');
			buf.append(nf.format(pt.x));
			buf.append(',');
			buf.append(nf.format(pt.y));
			buf.append(')');
			mouseCoord.setText(buf.toString());
		}

		////////////////////////////////////////////////////
		/// EVENT
		////////////////////////////////////////////////////

		public void helpMessagePosted(HelpMessageEvent e){
			String s = e.getMessage();
			if (DEBUG) debug("Board "+getTitle()+" received msg:"+s);
			if (s==null) return;
			toolTip.setText(localize(s));
		}

		/**
		 * display mouse coords
		 */
		public void mouseDragged(PEMouseEvent e){
			mouseMoved(e);
		}

		/**
		 * Display mouse coords.
		 */
		public void mouseMoved(PEMouseEvent e){
			PicPoint ptClick = e.getPicPoint();
			e.getCanvas().getGrid().nearestNeighbour(ptClick,ptBuffer);
			showCoord(ptBuffer);
		}

		public void mousePressed(PEMouseEvent e){}
		public void mouseReleased(PEMouseEvent e){}
		public void mouseClicked(PEMouseEvent e){}
		public void mouseEntered(PEMouseEvent e){}
		public void mouseExited(PEMouseEvent e){}

	} // StatusBar


	////////////////////////////////////// GRID & ZOOM ////////////////////////////////
	/**
	 * A toolbar that allows the user to change the grid and zoom properties
	 * @author Sylvain Reynal
	 * @since jPicEdt-beta9
	 */
	public class GridZoomToolBar extends jpicedt.graphic.toolkit.PEToolBar
		implements ZoomListener, RotateListener {

		private ActionDispatcher actionDispatcher;

		// --------------- grid-on/off ------------------------
		private JToggleButton gridVisibleTB, snapOnOffTB;
		private PEToggleAction gridVisibleAction,snapOnOffAction;

		// --------------- grid- and snap-step ------------------------
		private JComboBox snapStepCB, gridStepCB;
		private GridStepAction gridStepAction;
		private SnapStepAction snapStepAction;
		// see Grid for PREDEFINED_SNAP_STEP_STRINGS and PREDEFINED_SNAP_STEPS

		// --------------- zoom ------------------------
		private ZoomAction zoomAction;
		private JSlider zoomSL;
		private JTextField zoomTF;
		// see PECanvas for PREDEFINED_ZOOMS

		// new *************************** begin (by ss & bp)
		// --------------- rotate ----------------------
		private JLabel rotateLabel;
		private JTextField rotateTF;
		//private JLabel dummy;
		private JPanel dummy;
		private DecimalFormat angleDecimalFormat = new DecimalFormat("0.00");
		// new *************************** end (by ss & bp)

		// --- class methods ------------------------------------------------

		/**
		 * Constructor
		 */
		public GridZoomToolBar(ActionDispatcher actionDispatcher, ActionLocalizer localizer) {

			this.actionDispatcher = actionDispatcher;
			this.setOrientation(HORIZONTAL);
			this.setFloatable(true);
			this.setBorder(BorderFactory.createEtchedBorder());
			
			// grid ON/OFF:
			gridVisibleTB = add(gridVisibleAction=new EditorKit.ShowGridAction(actionDispatcher, localizer));
			Dimension dimButton = gridVisibleTB.getPreferredSize(); // used later to compute other widgets maximum size
			addSeparator();

			// grid steps:
			gridStepCB = new JComboBox(Grid.PREDEFINED_GRID_STEP_STRINGS);
			gridStepCB.setEditable(true);
			gridStepCB.setAlignmentY(CENTER_ALIGNMENT);
			gridStepCB.addActionListener(gridStepAction=new GridStepAction());
			gridStepCB.setMaximumSize(new Dimension(dimButton.width * 4,dimButton.height));
			this.add(gridStepCB);
			addSeparator();

			// snap ON/OFF:
			snapOnOffTB = add(snapOnOffAction=new EditorKit.ActivateSnapAction(actionDispatcher, localizer));
			addSeparator();

			// snap steps:
			snapStepCB = new JComboBox(Grid.PREDEFINED_SNAP_STEP_STRINGS);
			snapStepCB.setEditable(true);
			snapStepCB.setAlignmentY(CENTER_ALIGNMENT);
			snapStepCB.addActionListener(snapStepAction=new SnapStepAction());
			snapStepCB.setMaximumSize(new Dimension(dimButton.width * 4,dimButton.height));
			this.add(snapStepCB);
			addSeparator();
			
			zoomTF = new JTextField();
			zoomTF.setColumns(4);
			zoomTF.setAlignmentY(CENTER_ALIGNMENT);
			zoomTF.setMinimumSize(new Dimension(40, dimButton.height));
			zoomTF.setMaximumSize(new Dimension(40, dimButton.height));
			zoomTF.addActionListener(new ZoomAction());
			zoomTF.setText("100%");
			this.add(zoomTF);
			addSeparator();
			
			zoomSL = new JSlider(); 
			zoomSL.setAlignmentY(CENTER_ALIGNMENT);
			zoomSL.setMinimumSize(new Dimension(100,dimButton.height));
			zoomSL.setMaximumSize(new Dimension(100,dimButton.height));
			zoomSL.setMajorTickSpacing(1);
			zoomSL.setMinimum(100);
			zoomSL.setMaximum(1000);
			zoomSL.setValue(100);
			zoomSL.addChangeListener(new Zoom4SliderAction());
			this.add(zoomSL);
			addSeparator();
			
			
			// rotate textfield
			rotateLabel = new JLabel(localize("action.editorkit.Rotate.Angle"));
			rotateLabel.setVisible(false);
			this.add(rotateLabel);

			rotateTF = new JTextField(5);
			rotateTF.setVisible(false);
			rotateTF.setMaximumSize(new Dimension(dimButton.width * 2,dimButton.height));
			rotateTF.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a) {
					MouseTool mt = canvas.getEditorKit().getCurrentMouseTool();
					if(mt != null && mt instanceof RotateTool){
						double angle = Double.NaN;
						try{
							Number angleNumber = angleDecimalFormat.parse(rotateTF.getText(),
																		  new ParsePosition(0));
							angle = angleNumber.doubleValue();
						}
						catch(java.lang.NumberFormatException nfEx){
						}
						if(!Double.isNaN(angle)){
							rotateTF.setText(String.valueOf(angleDecimalFormat.format(angle)));
							((RotateTool)mt).rotateTarget(angle,canvas);
						}
					}
				}
			});
			this.add(rotateTF);
			
			dummy = new JPanel() {
				   public Dimension getPreferredSize() {
				       return this.getSize();
				   };
				};
				
			this.add(dummy);
		}

		/**
		 * Called when this tool-bar is installed, and widgets values must reflect a given PECanvas state.
		 * @param canvas the source from which this toolbar updates the value or state of its widgets
		 */
		public void install(PECanvas canvas){
			// grid visibility
			gridVisibleTB.setSelected(canvas.getGrid().isVisible());
			// snap on-off
			snapOnOffTB.setSelected(canvas.getGrid().isSnapOn());
			// snap-step
			int i = Grid.getSnapStepIndex(canvas.getGrid().getSnapStep());
			if (i != -1) snapStepCB.setSelectedIndex(i);
			else { // add non-predefined snap step to list
				snapStepCB.addItem(new Double(canvas.getGrid().getSnapStep()).toString());
				snapStepCB.setSelectedIndex(snapStepCB.getItemCount()-1);
			}
			// grid-step
			i = Grid.getGridStepIndex(canvas.getGrid().getGridStep());
			if (i != -1) gridStepCB.setSelectedIndex(i);
			else { // add non-predefined grid step to list
				gridStepCB.addItem(new Double(canvas.getGrid().getGridStep()).toString());
				gridStepCB.setSelectedIndex(gridStepCB.getItemCount()-1);
			}
			// zoom
			/*i = PECanvas.getZoomIndex(canvas.getZoomFactor());
			if (i != -1) zoomCB.setSelectedIndex(i);
			else {
				NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US);
				formatPercent.setGroupingUsed(false);
				String s = formatPercent.format(canvas.getZoomFactor());
				zoomCB.addItem(s);
				zoomCB.setSelectedIndex(zoomCB.getItemCount()-1);
			}*/
		}

		/** ZoomListener interface ; called when the zoom changed in the sourcing canvas
		 *  following a ZoomTool operation -> update widget */
		public void zoomUpdate(ZoomEvent e){
			float zoom = (float)e.getNewZoomValue();
			
			NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US);
			formatPercent.setGroupingUsed(false);
			
			String percent = formatPercent.format(zoom);
			
			if(zoomTF.getText() != percent)
				zoomTF.setText(percent);
			
			if(zoomSL.getValue() != zoom)
				zoomSL.setValue((int)(zoom * 100));
		}
		/**
		 * RotateListener interface
		 */
		public void rotateUpdate(RotateEvent e){
			rotateTF.setText(String.valueOf(angleDecimalFormat.format(e.getAngleValue())));
		}

		/**
		 * toggle visibility of the textfield
		 */
		public void setRotateAngleLabelVisible(boolean b){
			rotateTF.setVisible(b);
			//dummy.setVisible(!b);
			rotateTF.setText("0");
			rotateLabel.setVisible(b);
		}

		///////////////////////////////////////////////////////////////////////////////////
		//// ACTIONS
		///////////////////////////////////////////////////////////////////////////////////

		/**
		 * Updates the grid-step in the current active frame as soon as
		 * a new item gets selected by user in the list (aka JComboBox) of available grid steps
		 *
		 * <br><b>author:</b> Sylvain Reynal
		 * @since jPicEdt 1.4
		 */
		class GridStepAction extends PEAction {

			public GridStepAction(){
				super(actionDispatcher,"GridStepList",null);
			}

			public void actionPerformed(ActionEvent e){
				int i = gridStepCB.getSelectedIndex();
				this.getCanvas().getGrid().setGridStep(Double.parseDouble((String)gridStepCB.getSelectedItem()));
				this.getCanvas().repaint();
			}
		}

		/**
		 * Updates the snap-step in the current active frame as soon as
		 * a new item gets selected by user in the list (aka JComboBox) of available grid steps
		 *
		 * <br><b>author:</b> Sylvain Reynal
		 * @since PicEdt 1.2.1
		 */
		class SnapStepAction extends PEAction {

			public SnapStepAction(){
				super(actionDispatcher,"SnapStepList",null);
			}

			public void actionPerformed(ActionEvent e){
				int i = snapStepCB.getSelectedIndex();
				this.getCanvas().getGrid().setSnapStep(Double.parseDouble((String)snapStepCB.getSelectedItem()));
				this.getCanvas().repaint();
			}
		}
		
		/**
		 * Updates zoom-factor in the current active frame as soon as
		 * a new item gets selected by user in the list of available zoom factors (e.g. 100%, 200%, ...)
		 * <br><b>author:</b> Sylvain Reynal
		 * @since PicEdt 1.2.1
		 */
		class ZoomAction extends PEAction {
			
			public ZoomAction(){
				super(actionDispatcher,"ZoomList",null);
			}
			
			public void actionPerformed(ActionEvent e) {
				String txt = zoomTF.getText();
				if(txt.endsWith("%")) txt = txt.substring(0, txt.indexOf('%'));
				Double percentage = Double.parseDouble(txt);
				
				if(percentage < 100) percentage = 100d;
				else if(percentage > 1000) percentage = 1000d;
				
				Double zoom = Double.parseDouble(txt) / 100;
				if(zoom == getCanvas().getZoomFactor()) return;
				
				try {
				
				getCanvas().setZoomFactor(zoom);
				
				}
				catch(Exception ex){
					ex.printStackTrace();
					DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
					df.showMessageDialog( localize("exception.NumberFormatError") + " : " + ex.getMessage(),
					                              localize("preferences.Zoom"),
					                              JOptionPane.ERROR_MESSAGE);
					return;
				}
				this.getCanvas().repaint();
			}
		}
		
		class Zoom4SliderAction implements ChangeListener {

			@Override
			public void stateChanged(ChangeEvent e) {
				Double zoom = new Double(zoomSL.getValue()) / 100F;
				
				if(zoom == getCanvas().getZoomFactor()) return;
					getCanvas().setZoomFactor(zoom);
			}
		}
	} // GridZoomToolBar

} // class
