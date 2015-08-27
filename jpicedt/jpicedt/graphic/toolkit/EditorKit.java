// EditorKit.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EditorKit.java,v 1.74 2013/03/27 06:57:56 vincentb1 Exp $
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

package jpicedt.graphic.toolkit;

import jpicedt.graphic.ContentType;
import jpicedt.graphic.ConvexZoneSelectionHandler;
import jpicedt.graphic.DefaultContentType;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.SelectionHandler;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicSmoothPolygon;
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.model.TextEditable;

import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.event.DrawingListener;
import jpicedt.graphic.event.HelpMessageListener;
import jpicedt.graphic.event.HelpMessageEvent;

import jpicedt.graphic.view.AbstractView;
import jpicedt.graphic.view.ConvexZoneHalfPlaneView;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.ViewFactory;
import jpicedt.graphic.view.ViewConstants;
import jpicedt.graphic.view.highlighter.Highlighter;
import jpicedt.graphic.view.highlighter.HighlighterFactory;
import jpicedt.graphic.view.highlighter.DefaultHighlighterFactory;
import jpicedt.graphic.view.DefaultViewFactory;
import jpicedt.graphic.view.HitInfo;

import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.ui.MDIManager;
import jpicedt.ui.dialog.DockableConvexZoneToolBar;
import jpicedt.JPicEdt;


import javax.swing.event.EventListenerList;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.EnumSet;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;

import static jpicedt.Localizer.*;
import static jpicedt.Log.*;

/**
 * A pluggable <code>EditorKit</code> for a <code>PECanvas</code>, that mainly provides mouse-tool management,
 * e.g. zooming, selecting and drawing capabilities, together with a set of <code>Action</code>'s.
 * <br>
 * <code>Action</code>'s that make sense for this <code>EditorKit</code> are implemented as static inner
 * classes to reduce name space. They can be shared across multiple <code>PECanvas</code> through the use of
 * an <code>ActionDispatcher</code> given to the <code>Action</code> constructor.
 * <br>New mouseTool's can be registered via the registerMouseTool method ; they're backed by a hashMap,
 * and can be activated (ie set as the current mouse-tool) by using setCurrentMouseTool with the tool name as
 * the parameter string.
 * <p>
 * This <code>EditorKit</code> provides also a <code>ViewFactory</code>, which delegates most of its behaviour
 * to an underlying factory (except for the View associated with the <code>RootElement</code> of the
 * <code>Drawing</code>), and whose type depends on the current content-type, as set by setContentType. This
 * allows the "root-view" to maintain a reference to the container (which this <code>EditorKit</code> can
 * easily provide), while still allowing underlying <code>ViewFactory</code>'s to be really independant of the
 * exact structure of the <code>Drawing</code> (especially the existence of a
 * <code>RootElement</code>). Besides, the <code>EditorKit</code> can plug a new <code>ViewFactory</code>
 * on-the-fly when the content-type changed, w/o other objects having to be informed.
 * <p>
 * [todo]
 * <ul>
 * <li> provide an EditorKit abstract class + a minimal implementation for developpers who don't need
 *        all the editing stuff (e.g. setting attributes, undoing,&hellip;) when just implementing a viewer
 * </ul>
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: EditorKit.java,v 1.74 2013/03/27 06:57:56 vincentb1 Exp $
 */
public class EditorKit {

	////////////////////////////////////////////////////////////////////////////////////
	//// PUBLIC FIELDS
	////////////////////////////////////////////////////////////////////////////////////

	/** zoom mode */
	public static final String ZOOM = "action.editorkit.Zoom";
	/** selection */
	public static final String SELECT = "action.editorkit.Select";
	/** point edition tool */
	public static final String EDIT_POINT = "action.editorkit.EditBezierPoint";
	/** move */
	public static final String MOVE = "action.editorkit.Translate";
	/** scale */
	public static final String SCALE = "action.editorkit.Scale";
	/** rotate */
	public static final String ROTATE = "action.editorkit.Rotate";
	/** mirror */
	public static final String MIRROR = "action.editorkit.Mirror";
	/** shear */
	public static final String SHEAR = "action.editorkit.Shear";

	/** use by getAvailableToolNames ; add your own names here */
	private static final String[] ALL_EDIT_TOOL_NAMES=new String[]{ZOOM, SELECT, EDIT_POINT, MOVE, SCALE, ROTATE, MIRROR};

	public static final String CZ_SELECT            = ConvexZoneToolFactory.SELECT;
	public static final String CZ_EDIT              = ConvexZoneToolFactory.EDIT;
	public static final String CZ_DRAWING_TRANSLATE = ConvexZoneToolFactory.DRAWING_TRANSLATE;
	public static final String CZ_DRAWING_TRIM      = ConvexZoneToolFactory.DRAWING_TRIM;
	private static final String[] ALL_CZ_TOOL_NAMES=
		new String[]{CZ_SELECT, CZ_EDIT, CZ_DRAWING_TRANSLATE, CZ_DRAWING_TRIM};

	/** property name for edit mode change : "edit-mode" */
	public static final String EDIT_MODE_CHANGE = "edit-mode";

	////////////////////////////////////////////////////////////////////////////////////
	//// PROTECTED FIELDS
	////////////////////////////////////////////////////////////////////////////////////

	/** the PECanvas this EditorKit is installed in. null if not installed */
	protected PECanvas board;
	/** list of HelpMessageListener's and PropertyChangeListener's for this EditorKit */
	protected EventListenerList listenerList = new EventListenerList();

	////////////////////////////////////////////////////////////////////////////////////
	//// PRIVATE FIELDS
	////////////////////////////////////////////////////////////////////////////////////
	/** a key that stores the current edit mode */
	private String editMode;
	/** cette queue permet que la méthode <code>setCurrentMouseTool</code> se rappelle récursivement. Ceci
		permet que le nouvel outil-souris configuré par <code>setCurrentMouseTool</code> fasse toute son
		action sur sa méthode <code>init()</code> et change alors l'outil-souris courant en appelant
		<code>setCurrentMouseTool</code> causant ainsi un appel récursif. */
	private ArrayDeque<String> editModeQueue = new ArrayDeque(2);
	/** un mot clef stockant le mode d'édition de zones convexes courant */
	private String convexZoneEditMode;
	/** the PropertyChangeListener that gets notified of property changes in the canvas */
	private PropertyChangeHandler propertyChangeHandler = new PropertyChangeHandler();
	/** the factory that creates popup-menu on-the-fly */
	private PopupMenuFactory popupMenuFactory;
	/** the factory that creates GUI dialogs */
	private DialogFactory dialogMgr;
	/** selection handler for this kit */
	private DefaultSelectionHandler selectionHandler;
	private DefaultConvexZoneSelectionHandler convexZoneSelectionHandler;
	private boolean isConvexZoneSetShown = false;

	/** Définit si l'outil souris actuellement installé est un outil de
	 * dessin, ou un outil pour la manipulation de zones convexes */
	private MouseTool.MouseToolType currentMouseToolType = MouseTool.MouseToolType.DRAWING_MOUSE_TOOL;

	/**
	 * @return une valeur <code>MouseTool.MouseToolType</code> définissant le
	 * type de l'outil souris courant.
	 */
	public MouseTool.MouseToolType getCurrentMouseToolType(){
		return currentMouseToolType;
	}
	/**
	 * @return une valeur <code>boolean</code>, vraie si les zone convexes sont
	 * visonnées, faux si elles sont masquées.
	 */
	public boolean isConvexZoneSetShown(){
		return isConvexZoneSetShown;
	}

	public void setIsConvexZoneSetShown(boolean v){
		if(v != isConvexZoneSetShown){
			isConvexZoneSetShown = v;
			getCanvas().repaint();
		}
	}

	private class PerTypeMouseToolInfo{
		public MouseTool currentMouseTool;
		public PerTypeMouseToolInfo(){}
	}

	/** Liste type par type l'outil actuellement installé */
	private PerTypeMouseToolInfo[] perTypeMouseToolInfo;

	/** a map that stores registered mousetools */
	private HashMap<String,MouseTool> toolMap = new HashMap<String,MouseTool>();

	/** the attribute set used when instanciating a new graphic element */
	private PicAttributeSet inputAttributes;
	/** the view factory used to produce view for the drawing */
	private ViewFactory viewFactory;
	/** the FormatterFactory used to produce formatter that can write the drawing content to a writer */
	private FormatterFactory formatterFactory;
	/** the factory that produces View's Highlighters */
	private HighlighterFactory highlighterFactory;

	////////////////////////////////////////////////////////////////////////////////////
	//// PRIVATE STATIC FIELDS
	////////////////////////////////////////////////////////////////////////////////////
	// to be shared across every instance of EditorKit :
	private static Clipboard clipboard = new Clipboard("jPicEdt local clipboard");

	////////////////////////////////////////////////////////////////////////////////////
	//// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a new EditorKit with SELECT being the default initial edit-mode, and a default viewfactory.
	 */
	public EditorKit(){
		this(null,null);
	}

	/**
	 * Construct a new EditorKit with SELECT being the default initial edit-mode.
	 * @param contentType if non-null, this set the view- and formatter- factories to use for rendering the associated content ;
	 *        otherwise, default factories are created using <code>createDefaultContentType()</code>.
	 * @param oldKit if an old instance already existed, and one wishes to reuse its properties, e.g.
	 *        listener-list, popupmenu-factory, input-attributes,&hellip; except obviously for the ViewFactory
	 *        and the FormatterFactory given as arguments. May be safely be set to null.
	 */
	public EditorKit(ContentType contentType, EditorKit oldKit){
		if (DEBUG) debug("view factory delegate="+viewFactory);

		isConvexZoneSetShown = JPicEdt.getProperty("ui.dockable-panel.ConvexeZone.visible",true);

		perTypeMouseToolInfo =
			new PerTypeMouseToolInfo[MouseTool.MouseToolType.MOUSE_TOOL_COUNT.toInteger()];
		for(int i = 0; i < perTypeMouseToolInfo.length; ++i)
			perTypeMouseToolInfo[i] = new PerTypeMouseToolInfo();

		setFactoriesFromContentType(contentType);

		if (oldKit==null) this.highlighterFactory = new DefaultHighlighterFactory();
		else this.highlighterFactory = oldKit.highlighterFactory;

		this.selectionHandler = new DefaultSelectionHandler(this);
		if (DEBUG) debug("installing selection handler:"+this.selectionHandler);

		this.convexZoneSelectionHandler = new DefaultConvexZoneSelectionHandler(this);

		if (oldKit==null) this.inputAttributes = new PicAttributeSet(); // default
		else this.inputAttributes = oldKit.inputAttributes;

		MDIManager mdiManager = JPicEdt.getMDIManager();

		if (oldKit != null){
			this.listenerList = oldKit.listenerList;
			oldKit.listenerList = null;
			mdiManager.removeDockablePanelPropertyChangeListener(oldKit.propertyChangeHandler);
		}

		mdiManager.addDockablePanelPropertyChangeListener(propertyChangeHandler);

		if (oldKit != null) setPopupMenuFactory(oldKit.getPopupMenuFactory());

		if (DEBUG) debug("init completed !");
	}

	////////////////////////////////////////////////////////////////////////////
	//// PLUGGABLE BEHAVIOUR
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Called when the kit is being installed into a <code>PECanvas</code>.
	 * This registers default mouse-tools, ie :
	 * <ul>
	 * <li> tools corresponding to constant-fields SELECT, MOVE,&hellip; defined in this class ;
	 * <li> all tools available from DrawToolFactory, and which may be accessed using constant-fields defined
	 *      therein, e.g. DrawToolFactory.RECTANGLE to access the mousetool dedicated to drawing rectangles.
	 * </ul>
	 * Then registers events-listener from the attached canvas,
	 * and finally set SELECT as the default mouse-tool.
	 * @param board the PECanvas
	 */
	public void install(PECanvas board) {

		if (DEBUG) debug("Kit is being installed in "+board);
		this.board = board;
		if (DEBUG) debugAppendLn("Registering default mouse-tools");
		registerMouseTool(ZOOM,new ZoomTool(this));
		registerMouseTool(SELECT,new ElementSelectionTool(this, new EditElementMouseTransformFactory(this))); // (un)select, move elements or their end-points
		registerMouseTool(EDIT_POINT,new ElementSelectionTool(this, new EditPointMouseTransformFactory(this))); // add/remove/edit control-points features
		registerMouseTool(MOVE,new MoveTool(this,selectionHandler));
		registerMouseTool(SCALE,new ScaleTool(this,selectionHandler));
		registerMouseTool(ROTATE,new RotateTool(this,selectionHandler));
		registerMouseTool(MIRROR,new MirrorTool(this,selectionHandler));
		//registerMouseTool(SHEAR,new ShearTool(this,selectionHandler));

		DrawToolFactory dtf = new DrawToolFactory(this);
		String[][] allDrawToolNames = dtf.getAvailableToolNames();
		for (int i=0; i<allDrawToolNames.length; i++){
			for (int j=0; j<allDrawToolNames[i].length; j++){ // scan each group of related tools, e.g. DrawArcXXXX
				String toolName = allDrawToolNames[i][j];
				MouseTool mt = dtf.createDrawTool(toolName);
				registerMouseTool(toolName,mt);
			}
		}

		registerMouseTool(CZ_DRAWING_TRIM,new InConvexZoneTrimTool(this, convexZoneSelectionHandler));
		registerMouseTool(CZ_DRAWING_TRANSLATE,new InConvexZoneTranslateTool(this, convexZoneSelectionHandler));
		registerMouseTool(CZ_SELECT,new ConvexZoneSelectionTool(this, new EditConvexZoneMouseTransformFactory(this)));
		ConvexZoneToolFactory cztf = new ConvexZoneToolFactory(this);
		String[][] allConvexZoneToolNames = cztf.getAvailableToolNames();
		for (int i=0; i<allConvexZoneToolNames.length; i++){
			for (int j=0; j<allConvexZoneToolNames[i].length; j++){ // scan each group of related tools, e.g. ConvexeZoneArcXXXX
				String toolName = allConvexZoneToolNames[i][j];
				MouseTool mt = cztf.createConvexZoneTool(toolName);
				registerMouseTool(toolName,mt);
			}
		}

		setCurrentMouseTool(SELECT);
		board.addPropertyChangeListener(propertyChangeHandler); // respond to DRAWING_CHANGE events
		// install keybd bindings :
		board.setActionMap(createActionMap(new DefaultActionDispatcher(board),jpicedt.Localizer.currentLocalizer().getActionLocalizer()));
		board.setInputMap(JComponent.WHEN_FOCUSED,createInputMap());

		initDialogFactory();
	}

	/**
	 * Called when the kit is being removed from a <code>PECanvas</code>.  This is used to unregister any
	 * previously registered mouse-tool.
	 * @param board the PECanvas
	 */
	public void deinstall(PECanvas board) {
		if (DEBUG) debug("Kit is being deinstalled from "+board);
		MouseTool currentMouseTool = perTypeMouseToolInfo[currentMouseToolType.toInteger()].currentMouseTool;
		board.removePEMouseInputListener(currentMouseTool);
		board.removeKeyListener(currentMouseTool);
		board.removePropertyChangeListener(propertyChangeHandler);

		// [pending] unregister mouse tools ?
		Set<String> registered = getRegisteredMouseToolsByName();
		for (String key: registered){
			unRegisterMouseTool(key);
		}
		this.board = null;
	}

	/**
	 * Return the <code>PECanvas</code> into which this kit is installed, or null if the kit has not been
	 * installed yet.
	 */
	public PECanvas getCanvas(){
		return board;
	}

	////////////////////////////////
	/// MODEL & ATTRIBUTES
	////////////////////////////////

	/**
	 * Plugs new <code>FormatterFactory</code> and <code>ViewFactory</code> into this <code>EditorKit</code>,
	 * using the given ContentType to create them on-the-fly.
	 * @param ct if null, a default content-type is used, as returned by <code>createDefaultContentType</code>
	 */
	public void setFactoriesFromContentType(ContentType ct){
		if (ct==null) ct = createDefaultContentType();
		this.viewFactory = new ViewFactoryWrapper(ct.createViewFactory());
		this.formatterFactory = ct.createFormatter();
	}

	/**
	 * Creates a default content-type suited for this editor.
	 */
	public ContentType createDefaultContentType(){
		return new DefaultContentType();
	}

	/**
	 * Get the input attributes for the <code>PECanvas</code> attached to this <code>EditorKit</code>.
	 */
	public PicAttributeSet getInputAttributes(){
		return inputAttributes;
	}

	/**
	 * set the input attributes for the <code>PECanvas</code> attached to this <code>EditorKit</code>.
	 */
	public void setInputAttributes(PicAttributeSet set){
		inputAttributes = set;
	}

	/**
	 * @return the <code>FormatterFactory</code> used to write the content of a Drawing to a stream, according
	 *         to the type of content this kit can edit.
	 */
	public FormatterFactory getFormatterFactory(){
		return formatterFactory;
	}


	///////////////////////////////////////////
	/// MOUSE TOOL MANAGEMENT
	//////////////////////////////////////////

	/**
	 * Returns all mousetool-names registered by default with this EditorKit.
	 * @return an array of arrays of String, each sub-array containing a group of tightly related tools
	 */
	public static String[][] getAvailableToolNames(){
		String[][] allDrawToolNames = DrawToolFactory.getAvailableToolNames();
		String[][] allToolNames = new String[allDrawToolNames.length + 1][]; // one group for editing tools + as many groups as necessary for drawing tools
		allToolNames[0] = ALL_EDIT_TOOL_NAMES;
		for (int i=1; i<allToolNames.length; i++){
			allToolNames[i] = allDrawToolNames[i-1];
		}
		return allToolNames;
	}

	/**
	 * Renvoie une liste d'outils à la souris de manipulation de zone
	 * convexes, lesdits outils étant enrgistrés par défaut pour
	 * l'<code>EditorKit</code>.
	 * @return un tableau de tableau de String, chaque élément est un ensemble
	 * d'outils en relation proche les uns avec les autres.
	 */
	public static String[][] getAvailableConvexZoneToolNames(){
		String[][] allConvexZoneToolNames = ConvexZoneToolFactory.getAvailableToolNames();
		String[][] allToolNames = new String[allConvexZoneToolNames.length + 1][]; // one group for editing tools + as many groups as necessary for drawing tools
		allToolNames[0] = ALL_CZ_TOOL_NAMES;
		for (int i=1; i<allToolNames.length; i++){
			allToolNames[i] = allConvexZoneToolNames[i-1];
		}
		return allToolNames;
	}

	/**
	 * Registers the given mouse-tool by associating it with the given key in the hashtable of
	 * available mouse-tools. This name can then be fed as a parameter to
	 * {@link #setCurrentMouseTool setCurrentMouseTool()}.
	 */
	public void registerMouseTool(String key, MouseTool mt){
		if (mt==null || key==null) return; // allows the underlying factory to return null objects, if any.
		if (DEBUG) debug("Registering mouse tool " + mt + " with name " + key);
		toolMap.put(key,mt);
	}

	/**
	 * Unregisters the mouse-tool having the given key from the hashmap of available mouse-tools.
	 */
	public void unRegisterMouseTool(String key){
		if (DEBUG) debug("Unregistering mouse tool " + key);
		toolMap.remove(key);
	}

	/**
	 * @return an array containing the currently registered mouse-tools keys
	 */
	public Set<String> getRegisteredMouseToolsByName(){
		return toolMap.keySet();
	}

	/**
	 * @return an array containing the currently registered mouse-tools
	 */
	public Collection<MouseTool> getRegisteredMouseTools(){
		return toolMap.values();
	}

	/**
	 * set the current mouse tool for this editor kit
	 * <p>
	 * When setting a new edit-mode, the editor kit fire a PropertyChange Event with key
	 * "EDIT_MODE_CHANGE" and the given edit-mode as the new value.
	 * <br>
	 * This may be used by the UI to get informed that a change occured (since some
	 * mouse-tools in this kit may change the edit-mode from the inside).
	 * @param editMode a previously registered mouseTool's key
	 */
	public void setCurrentMouseTool(String editMode) {

		if (DEBUG)
			debug("Changing edit mode to " + editMode);
		if (board == null) return; // not installed
		editModeQueue.addLast(editMode);
		// si la taille de la queue est >= c'est qu'on est dans un appel recursie, alors on ne fait rien,
		// l'appelant fera le travail.
		if(editModeQueue.size() >= 2) return;
		Object oldMode = this.editMode;
		boolean noChange = true;
		MouseTool currentMouseTool = null;
		while(editModeQueue.size() != 0){
			editMode = editModeQueue.getFirst();
			// [pending] commented because startTextEditor doesn't currently set a new edit-mode
			// but instead stores the "old" edit mode when its starts, and tries to restore it
			// when it stop (hence, nothing would happend here because the old edit mode and the
			// current one would be the same.
			if (this.editMode == editMode){
				editModeQueue.removeFirst();
				continue; // no change
			}
			// [pending] currentMouseTool.deinstall() ? or stop() or...
			MouseTool newMouseTool = toolMap.get(editMode);
			if (newMouseTool == null){
				editModeQueue.removeFirst();
				continue; // [pending] throws NotRegisteredMouseToolException
			}
			noChange = false;
			this.editMode = editMode;
			currentMouseTool = perTypeMouseToolInfo[currentMouseToolType.toInteger()].currentMouseTool;
			if (currentMouseTool!=null){
				board.removePEMouseInputListener(currentMouseTool);
				board.removeKeyListener(currentMouseTool);
				currentMouseTool.flush();
			}
			currentMouseTool=newMouseTool;
			if(this.editMode != ROTATE){
				// deux précautions valent mieux qu'une, mais normalement c'est
				// inutile car le flush du RotateTool le fait déjà
				board.setRotateAngleLabelVisible(false);

			}
			// permet au nouvel outil d'initialiser quelques opérations avant qu'un événement souris se
			// produise. currentMouseTool.init() peut appeler recursivement kit.setCurrentMouseTool, ce qui a
			// pour effet de mettre le nouvel editMode en queue.
			currentMouseTool.init();
			currentMouseToolType = currentMouseTool.getMouseToolType();
			perTypeMouseToolInfo[currentMouseToolType.toInteger()].currentMouseTool = currentMouseTool;
			editModeQueue.removeFirst();
		}
		if(noChange)
			return;
		board.addPEMouseInputListener(currentMouseTool);
		board.addKeyListener(currentMouseTool);
		//board.setCursor(currentMouseTool.getCursor());
		// !!! board.unSelectAll(); // for draw_xxx only
		firePropertyChange(EDIT_MODE_CHANGE, oldMode, this.editMode);

		board.repaint(); // clean-up after mousetool
	}

	/**
	 * @return the current <code>MouseTool</code> for this <code>EditorKit</code>.
	 */
	public MouseTool getCurrentMouseTool(){
		return perTypeMouseToolInfo[currentMouseToolType.toInteger()].currentMouseTool;
	}

	/**
	 * Start a TextEditor for the given TextEditable, using the given
	 * PEMouseEvent as the starting caret location.
	 * [pending] should be implemented as an editor kit on its own, with an associated Action
	 * The point is : we must provide two ways of starting the TextEditor, one with a known
	 * textEditable and mouseEvent (current implementation), the other W/O these information
	 * (then, the TextEditor should first fetch a TextEditable in the drawing from mouse input,
	 * and start the TextEditor on it)
	 */
	/* 	protected void startTextEditor(TextEditable textEditable, PEMouseEvent me){ [pending]
			board.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			currentMouseTool = new TextEditor(textEditable, me, this);
			// [pending] 09/02/2002 set canvas's mouse-listener to it otherwise it won't work !
		} */











	/////////////////////////////////////////////////////////////////////////////
	//// MOUSE UTILITIES (package access because they're mainly accessed from MouseTool's
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Convenience for retrieving a HitInfo from a given mouse-event, by calling <code>hitTest</code>
	 * on either the selection-handler, or the drawing (non-selected element only).
	 * [SR:pending] in EditGroup mode, this should be adapted so as to look up the PicGroup instead of the
	 * drawing.
	 * @return the HitInfo for the given mouse event
	 * @param selectedElements if true, lookup selected elements only (including clicks on highlighted parts) ; otherwise, look up non-selected elts only
	 */
	/* package */ HitInfo hitTest(PEMouseEvent e, boolean selectedElements){
		// if (!EditGroupMode){
		if (selectedElements) {
			HitInfo hi = selectionHandler.hitTest(e); // include highlighter (highlighter.hitTest(), then possibly view.hitTest())
			if (DEBUG) debug( "hi (in selection) = "+hi);
			return hi;
		}
		else {
			HitInfo hi = e.getCanvas().getDrawing().getRootView().hitTest(e,false); // don't include highlighter
			if (DEBUG) debug( "hi (in drawing) = "+hi);
			return hi;
		}
		// }
		// else {
		// if (selectionOnly) return selectionHandler.hitTest(e,true);
		// takes for granted that selection-handler
		// reflects changes in the group being edited
		// else return group.getView().hitTest(e,false);
	}


	/**
	 * La méthode <code>convexZoneHitTest</code> renvoie
	 * <code>ConvexZoneHitInfo</code> décrivant l'impact d'un clic de souris
	 * sur des zones convexes.
	 *
	 * @param e le <code>PEMouseEvent</code> décrivant le clic de souris
	 * @param selectedZones un <code>boolean</code> vrai si le clic est à
	 * tester seulement sur les zones convexes sélectionnée.
	 * @return le <code>ConvexZoneHitInfo</code> décrivant l'impact du clic de souris.
	 */
	ConvexZoneHitInfo convexZoneHitTest(PEMouseEvent e, boolean selectedZones){
		if(selectedZones){
			ConvexZoneHitInfo hi = convexZoneSelectionHandler.hitTest(e,selectedZones);
			return hi;
		}
		else{
			ConvexZoneHitInfo hi = e.getCanvas().getConvexZoneSet().hitTest(e,selectedZones);
			return hi;
		}
	}

	/**
	 *
	 */
	/* package */ ArrayList<Element> intersect(Rectangle2D r, boolean selectedElements){
		ArrayList<Element> list = new ArrayList<Element>();
		if (selectedElements) {
			selectionHandler.intersect(r,list); // include highlighter (highlighter.hitTest(), then possibly view.hitTest())
		}
		else {
			getCanvas().getDrawing().getRootView().intersect(r,false,list); // don't include highlighter
		}
		return list;
	}

	///////////////////////////////////////////////
	//// EVENTS MANAGEMENT
	//////////////////////////////////////////////

	/**
	 * adds a PropertyChangeListener to the listener list
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		listenerList.add(PropertyChangeListener.class, l);
	}

	/**
	 * removes an PropertyChangeListener from the EditorKit
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		listenerList.remove(PropertyChangeListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * The event instance is lazily created using the parameters passed into the fire method.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==PropertyChangeListener.class) {
				// Lazily create the event:
				if (e == null) 	e = new PropertyChangeEvent(this,propertyName, oldValue, newValue);
				((PropertyChangeListener)listeners[i+1]).propertyChange(e);
			}
		}
	}

	/**
	 * adds a HelpMessageListener to the listener list
	 */
	public void addHelpMessageListener(HelpMessageListener l) {
		listenerList.add(HelpMessageListener.class, l);
	}

	/**
	 * removes an HelpMessageListener from the EditorKit
	 */
	public void removeHelpMessageListener(HelpMessageListener l) {
		listenerList.remove(HelpMessageListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * The event instance is lazily created using the parameters passed into the fire method.
	 */
	protected void postHelpMessage(String message){
		if (DEBUG) debug("posted msg="+message);
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		HelpMessageEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==HelpMessageListener.class) {
				// Lazily create the event:
				if (e == null) 	e = new HelpMessageEvent(this,message);
				((HelpMessageListener)listeners[i+1]).helpMessagePosted(e);
			}
		}
	}



	///////////////////////////////////////////
	/// POPUP MENU and related factories
	///////////////////////////////////////////
	/**
	 * Set the popup-menu factory that produces a <code>JPopupMenu</code> when the <code>EditorKit</code> asks
	 * for it.  <p>
	 * In this implementation, a popup-menu gets raised if :
	 * <ul>
	 * <li> a PopupTrigger mouse-event occured, (see MouseEvent.isPopupTrigger())
	 * <li> the current edit mode is "SELECT"
	 * <li> the PopupMenuFactory is not null
	 * <li> the PopupMenuFactory.createPopupMenu() method returned a non-null JPopupMenu
	 * </ul>
	 */
	public void setPopupMenuFactory(PopupMenuFactory popupMenuFactory){
		this.popupMenuFactory = popupMenuFactory;
	}

	/**
	 * @return the popup-menu factory that produces <code>JPopupMenu</code>'s for this <code>EditorKit</code>.
	 */
	public PopupMenuFactory getPopupMenuFactory(){
		return popupMenuFactory ;
	}

	/**
	 * Sets the factory that produces GUI dialogs
	 */
	public void setDialogFactory(DialogFactory dm){
		if (dm==null)
			throw new NullPointerException("Can't set a null dialog factory.");
		this.dialogMgr = dm;
	}

	/**
	 * Returns the factory that produces GUI dialogs
	 */
	public DialogFactory getDialogFactory(){
		return this.dialogMgr;
	}

	// lasy initialization
	private void initDialogFactory(){
		java.awt.Frame frame=null;
		PECanvas cv = getCanvas();
		if (cv !=null){
			java.awt.Container container = cv.getTopLevelAncestor();
			if (container instanceof java.awt.Frame)
				frame = (java.awt.Frame)container;
		}
		this.dialogMgr = new DefaultDialogFactory(frame);
	}




	//////////////////////////////////////////
	//// VIEW and VIEW FACTORY
	//////////////////////////////////////////

	/**
	 * Allow this <code>EditorKit</code> to paint shapes specific to the current tool, either directly,
	 * or through the paint method of its tools.
	 * @param scale the current scale factor b/w model-coordinates and view-coordinates ; this depends on the
	 *        <code>AffineTransform</code> being currently active in <code>PECanvas</code>'s paintComponent,
	 *        and may be used, e.g. to scale down lines thickness so that they're displayed with a constant
	 *        thickness whatever the scale factor attached to Graphics2D (this is faster than retrieving the
	 *        AffineTransform attached to the graphic context given as parameter, and computing the scale
	 *        factor by using getScaleX).
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (DEBUG) debug();
		// paint element highlighter :
		selectionHandler.paint(g, allocation, scale);

		// paint convexe zone highlighter
		if(isConvexZoneSetShown)
			convexZoneSelectionHandler.paint(g, allocation, scale);

		// paint current tool :
		MouseTool currentMouseTool = perTypeMouseToolInfo[currentMouseToolType.toInteger()].currentMouseTool;
		if (currentMouseTool != null) currentMouseTool.paint(g, allocation, scale);
		// add other paint rendering here :
	}

	/**
	 * @return the <code>ViewFactory</code> that produces View's for the kind of content-type this editor-kit
	 *         can edit or create.
	 */
	public ViewFactory getViewFactory(){
		return viewFactory;
	}

	/**
	 * Return the factory delegate for producing View's Highlighters
	 */
	public HighlighterFactory getHighlighterFactory(){
		return highlighterFactory;
	}

	/**
	 * Sets the factory delegate for producing View's Highlighters
	 */
	public void setHighlighterFactory(HighlighterFactory hf){
		this.highlighterFactory=hf;
		//[SR:pending] rebuild View tree !!!
	}

	/**
	 * This is the core ViewFactory that produces View's for the associated Drawing.
	 * It actually delegates most of its job to an underlying ViewFactory as set by
	 * <code>setContentType</code>, except for producing :
	 * <ul>
	 * <li> the root-view for the <code>Drawing</code>'s root-element ;
	 * <li> View's for <code>Element</code>'s that implement the ViewFactory interface themselves, in which
	 *      case the <code>Element</code> itself acts as the delegate ; this allow plug-ins designers willing
	 *      to implement new Element's, to provide their own ViewFactory for their element, w/o having
	 *      to rebuild a whole factory from scratch, especially if they want to base their plug-in
	 *      on the currently installed EditorKit (ie this editorkit).
	 * </ul>
	 */
	class ViewFactoryWrapper implements ViewFactory {

		private ViewFactory delegate;

		/**
		 * construct a new view factory for this editor-kit, with the given view-factory
		 * as the delegates for elements that aren't handled here.
		 */
		public ViewFactoryWrapper(ViewFactory delegate){
			if (DEBUG) debug("delegate="+delegate);
			this.delegate = delegate;
		}

		/**
		 * Return a View for the given <code>Element</code>.  If <code>element</code> is a
		 * <code>Drawing.RootElement</code>, returns a RootView, else if <code>element</code> implements the
		 * <code>ViewFactory</code> interface, it's asked to return a view for itself, otherwise delegates to
		 * the factory given as a parameter to the constructor.
		 */
		public View createView(Element element){
			if (DEBUG) debug("creating view for element : " + element);
			if (element instanceof Drawing.RootElement){
				return new RootView((Drawing.RootElement)element);
			}
			/*
			else if (element instanceof Drawing.BoundingBox){
				return new ParallelogramView((Drawing.BoundingBox)element,this);
				// [pending] highlighter???
			}*/
			else if (element instanceof ViewFactory){
				if (DEBUG) debug("delegating View creation to the element itself");
				return ((ViewFactory)element).createView(element);
			}
			else {
				if (DEBUG) debug("delegating View creation to " + delegate);
				View v = delegate.createView(element);
				if (v!=null)
					v.setHighlighter(highlighterFactory.createHighlighter(element));
				return v;
			}
		}
	}

	/**
	 * the view associated with the root-element of the drawing
	 */
	class RootView extends AbstractView {

		public RootView(Drawing.RootElement e){
			super(e);
		}

		/**
		 * Notify this View of some change in the model. This is usually called by the associated Drawing
		 * (through its <code>RootElement</code>) when its content changes (e.g. child added). Conversely,
		 * it's not called when the geometry of a child changed. [pending] quite awkward&hellip; We simply
		 * call <code>repaint</code> on the associated canvas, if this kit is installed in it.
		 */
		public void changedUpdate(DrawingEvent.EventType eventType){
			if (DEBUG) debug("eventType="+eventType);
			if (board != null) board.repaint();
		}

		/**
		 * Return a HitInfo corresponding to the given click-point in model-coordinate.
		 * This simply provides a way of testing a mouse-hit on every element of the associated drawing.
		 * @param isHighlightVisible N/A here.
		 */
		public HitInfo hitTest(PEMouseEvent me, boolean isHighlightVisible){
			return hitTest(me);
		}

		/**
		 * Returns a HitInfo corresponding to the given mouse-event.
		 * Only non-selected Element's are hit-tested.
		 */
		protected HitInfo hitTest(PEMouseEvent me){

			Drawing.RootElement e = (Drawing.RootElement)element;
			HitInfo hi=null;
			for(int i = e.size()-1; i>=0; i--){ // from top to bottom
				Element o = e.get(i);
				if (getSelectionHandler().contains(o)) continue; // look up ancestors
				View v = o.getView();
				if (v==null) continue;
				HitInfo hii = v.hitTest(me,false); // don't include highlighter
				if (hii != null) {
					if (hi==null)
						hi = hii; // init hi from hii
					else
						hi=hi.append(hii); // now hi is a HitInfo.List containing all the HitInfo's returned by elements in the drawing
				}
			}
			return hi;
		}

		/**
		 * Returns whether the non-highlighted view of some NON-selected <code>Element</code> in the
		 * <code>Drawing</code> intersects the given rectangle, and adds the Element to the given list if it's
		 * non-null.
		 * @param isHighlightVisible non-relevant here
		 */
		public boolean intersect(Rectangle2D r, boolean isHighlightVisible,  ArrayList<Element> list){
			return intersect(r,list);
		}

		/**
		* If the view of some NON-selected Element in the Drawing intersects the given rectangle, adds the Element
		* to the given list (if non-null) and returns true.
		*/
		protected boolean intersect(Rectangle2D r, ArrayList<Element> list){
			Drawing.RootElement e = (Drawing.RootElement)element;
			boolean ok = false;
			for (Element child: e){
				if (getSelectionHandler().contains(child)) continue; // look up ancestors
				View v = child.getView();
				if (v==null) continue;
				ok |= v.intersect(r,false,list); // only view, no highlighter, list modified
			}
			return ok;
		}

		/**
		 * Fetches the container hosting the view.
		 * @return the container, null if this editor-kit hasn't been installed yet.
		 */
		public PECanvas getContainer() {
			return EditorKit.this.board;
		}

		/**
			 * Fetches the <code>ViewFactory</code> implementation that is feeding the view hierarchy.
		 * @return the factory associated with this kit, according to the current content-type.
		 */
		public ViewFactory getViewFactory() {
			return viewFactory;
		}

		/**
		 * Render the View for <code>Drawing.RootElement</code> to the given graphic context. This calls
		 * "paint" on each child's view.
		 * @param a the current graphic clip
		 */
		public void paint(Graphics2D g, Rectangle2D a){
			if (DEBUG) debug();
			//if (!allocation.intersects(getBounds())) return; // not reliable, because getBounds is not always up-to-date ?
			Drawing.RootElement be = (Drawing.RootElement)element;
			Drawing.BoundingBox bb = be.getBoundingBox();
			if (bb!=null){
				View v = bb.getView();
				if (v !=null) v.paint(g,a);
			}
			for(Element e: be){
				View v  = e.getView();
				if (v !=null) v.paint(g,a);
			}
		}

		/** does nothing ; there's no hightlighter to be painted for Drawing.RootElement */
		public void paintHighlighter(Graphics2D g, Rectangle2D allocation, double scale){}
	}


	////////////////////////////////////////////////////////////////////////////////
	//// SELECTION HANDLER
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the current selection-handler that suited for the kind of content this editorkit must edit
	 */
	public SelectionHandler getSelectionHandler(){
		return selectionHandler;
	}


	public ConvexZoneSelectionHandler getConvexZoneSelectionHandler(){
		return convexZoneSelectionHandler;
	}




	//////////////////////////////////////////////////////////////
	/// EVENTS HANDLER
	//////////////////////////////////////////////////////////////

	/**
	 * Listens to changes from <code>PECanvas</code>, and dispatches to various inner classes interested in
	 * these changes.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener {

		/**
		 * called when a bound property changed in the canvas. If this is a DRAWING_CHANGE,
		 * we remove the old DrawingHandler, and register a new one for the new drawing.
		 * Currently, this only affects event dispatching to the selection-handler.
		 */
		public void propertyChange(PropertyChangeEvent e){
			if (DEBUG) debug("event:name="
				        +e.getPropertyName()+",old="+e.getOldValue()+",new="+e.getNewValue());
			if (e.getPropertyName()==PECanvas.DRAWING_CHANGE){
				// this is called e.g. when canvas.setDrawing() is invoked, as a result for instance of a read() operation.
				Drawing oldDr = (Drawing)e.getOldValue();
				Drawing newDr = (Drawing)e.getNewValue();
				selectionHandler.clear(); // security
				if (oldDr != null) oldDr.removeDrawingListener(selectionHandler);
				if (newDr != null) newDr.addDrawingListener(selectionHandler);
			}
			else if(e.getPropertyName()==MDIManager.DOCKABLE_PANEL_TOGGLE){
				if(((MDIManager.DockablePanel)e.getSource()).getKey() == DockableConvexZoneToolBar.KEY){
					Boolean visible = (Boolean)e.getNewValue();
					EditorKit kit = getCanvas().getEditorKit();
					if (kit != null)
						kit.setIsConvexZoneSetShown(visible);
				}
			}
		}
	}





	///////////////////////////////////////////
	//// ACTIONS (static inner classes)
	///////////////////////////////////////////

	// ---- select edit mode ----

	/**
	 * Sets the current mouse-tool for the editor-kit. Action commands are the same
	 * as the predefined mouse-tool's names for this kit.
	 */
	public static class SelectMouseToolAction extends PEAction {
		private String mouseToolName;
		/**
		 * Construct a new SelectMouseToolAction for the given mouse-tool name.
		 */
		public SelectMouseToolAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer, String mouseToolName){
			super(actionDispatcher, mouseToolName, localizer);
			this.mouseToolName = mouseToolName;
		}
		public void actionPerformed(ActionEvent e){
			EditorKit kit = getEditorKit();
			if (kit != null)
				kit.setCurrentMouseTool(mouseToolName);
		}
	}

	// ---- toggle edit-points-mode ----

	/**
	 * Toggles the EditorKit's edit-points-mode b/w LOCAL_MODE and GLOBAL_MODE.
	 */
	public static class ToggleEditPointsModeAction extends PEAction {

		public static final String KEY = "action.editorkit.EditPointsMode";

		/**
		 * Construct a new ToggleEditPointsModeAction.
		 */
		public ToggleEditPointsModeAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e){
			EditorKit kit = getEditorKit();
			if (kit != null && kit.getSelectionHandler() instanceof DefaultSelectionHandler){
				DefaultSelectionHandler h = (DefaultSelectionHandler)kit.getSelectionHandler();
				h.toggleHighlightingMode();
			}
		}
	}

	// ---- toggle use-convex-zone-mode ----

	/**
	 * Bascule le mode use-convex-zone-mode de l'<code>EditorKit</code> entre
	 * <code>USE</code> et <code>NOT_USE</code>.
	 */
	public static class ToggleUseConvexZoneModeAction extends PEConvexZoneAction {

		public static final String KEY = "action.convexzone.UseCzSelection";

		/**
		 * Construct a new ToggleUseConvexZoneModeAction.
		 */
		public ToggleUseConvexZoneModeAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e){
			EditorKit kit = getEditorKit();
			if (kit != null &&
				kit.getConvexZoneSelectionHandler() instanceof DefaultConvexZoneSelectionHandler){
				DefaultConvexZoneSelectionHandler h =
					(DefaultConvexZoneSelectionHandler)kit.getConvexZoneSelectionHandler();
				//h.toggleHighlightingMode();
			}
		}
	}


	public class DeleteConvexZoneAction extends PEConvexZoneAction{
		public static final String KEY = "action.editorkit.DeleteConvexZone";

		public DeleteConvexZoneAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer) {
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e) {
			PECanvas canvas = getCanvas();
			if(canvas == null)
				return;
			Rectangle2D bb = null;
			if(false)
			{
				// TODO: passer dans ce cas lorsqu'un modificateur est actif.
				/** this convex zone */
				ConvexZone cz = getSelectedObject();
				if(cz != null){
					bb = cz.getBoundingBox();
					canvas.deleteConvexZone(cz);
				}
			}
			else
			{
				/** all selection */
				ConvexZoneSelectionHandler czSelection = getEditorKit().getConvexZoneSelectionHandler();
				bb = czSelection.getBoundingBox();
				canvas.deleteConvexZoneSelection();
			}
			if(bb != null){
				ConvexZoneHalfPlaneView.barbellize(bb,canvas.getScaleFactor());
				canvas.repaintFromModelRect(bb);
			}
		}
	}
	public DeleteConvexZoneAction newDeleteConvexZoneAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
		return new DeleteConvexZoneAction(actionDispatcher, localizer);
	}



	// ---- edit disposition ----

	/**
	 * Bring one or more selected objets to back, to front, forward or backward.
	 * Latter actions work only if a single Element is selected (otherwise it 's really too tedious to work out)
	 */
	public static class EditDispositionAction extends PEAction {
		/** Action command for bringing elements to front */
		public static final String TOFRONT = "action.editorkit.BringToFront";
		/** Action command for bringing elements to back */
		public static final String TOBACK = "action.editorkit.BringToBack";
		/** Action command for moving element forward */
		public static final String FORWARD = "action.editorkit.BringForward";
		/** Action command for moving element backward */
		public static final String BACKWARD = "action.editorkit.BringBackward";
		private String type;

		/**
		 * Change the layer-disposition of one or more selected objects if there's one (and only ONE ! )
		 * at the time the action is performed.<br>
		 * @param type TOFRONT, TOBACK, FORWARD or BACKWARD
		 */
		public EditDispositionAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer,String type){
			super(actionDispatcher, type, localizer);
			this.type = type;
		}

		public void actionPerformed(ActionEvent e){

			if (getCanvas()==null) return;
			if (getCanvas().getSelectionSize()==0) return;
			if (getCanvas().getSelectionSize()>1 && (type==FORWARD||type==BACKWARD)) return;

			getCanvas().beginUndoableUpdate((String)getValue(NAME)); // i18n'd if there was a localizer provided in the contructor
			// first build a separate list of element to reorder, in order to prevent concurrent reordering of the selection:
			ArrayList<Element> selElements = new ArrayList<Element>();
			for (Element el: getCanvas().getSelectionHandler()){
				selElements.add(el);
			}
			int selElementIdx, increment;
			if (type==TOFRONT || type==FORWARD) {
				selElementIdx=0;
				increment=1;
			}
			else {
				selElementIdx=selElements.size()-1;
				increment=-1;
			}
			for (int i=0; i<selElements.size(); i++){
				Element obj1 = selElements.get(selElementIdx);
				BranchElement p = obj1.getParent(); // this will modify the disposition of this element
								// amongst its siblings (possibly inside a PicGroup)
				// for each operation, we check if it was successful ; if it's the case, we fire an undoable event.
				if (type == TOFRONT) p.bringToFront(obj1); // warning : this in turn trigger a re-ordering of the selection
				else if (type == TOBACK) p.bringToBack(obj1);
				else if (type == FORWARD) p.bringForward(obj1);
				else if (type == BACKWARD) p.bringBackward(obj1);
				// getCanvas().repaint(); // done from inside BranchElement through a fireChangedUpdate
				selElementIdx += increment;
			}
			getCanvas().endUndoableUpdate();
		}
	}

	// ---- group selection ----

	/**
	 * Group all selected objects into a new PicGroup.
	 */
	public static class GroupAction extends PEAction {

		public static final String KEY = "action.editorkit.GroupSelection";

		/** set "GroupAction" as the action's name */
		public GroupAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}
		public void undoableActionPerformed(ActionEvent e){
			getCanvas().groupSelection();
		}
	}

	// ---- join PicMultiCurveConvertable's ----

	/**
	 * Join all selected objects of type PicMultiCurveConvertable into a new PicMultiCurve.
	 * @since jpicedt 1.4pre5
	 */
	public static class JoinCurvesAction extends PEAction {

		public static final String KEY = "action.editorkit.JoinSelection";

		public JoinCurvesAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}
		public void undoableActionPerformed(ActionEvent e){
			getCanvas().joinSelection();
		}
	}


	// ---- grid/snap on/off ----

	/**
	 * Toggles grid's visible state
	 */
	public static class ShowGridAction extends PEToggleAction {

		public static final String KEY = "action.editorkit.ShowGrid";

		/** set "GridVisible" as the action's name */
		public ShowGridAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			if (e.getSource() instanceof AbstractButton){// simply toggle
				AbstractButton source = (AbstractButton)e.getSource();
				getCanvas().getGrid().setVisible(source.isSelected());
				getCanvas().repaint();
			}
			else {
				boolean b = getCanvas().getGrid().isVisible();
				getCanvas().getGrid().setVisible(!b);
				getCanvas().repaint();
			}
		}
	}

	/**
	 * Toggles grid-snap activation
	 */
	public static class ActivateSnapAction extends PEToggleAction {

		public static final String KEY = "action.editorkit.ActivateSnap";

		public ActivateSnapAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			if (e.getSource() instanceof AbstractButton){// simply toggle
				AbstractButton source = (AbstractButton)e.getSource();
				getCanvas().getGrid().setSnapOn(source.isSelected());
			}
			else {
				boolean b = getCanvas().getGrid().isSnapOn();
				getCanvas().getGrid().setSnapOn(!b);
			}
		}
	}

	// ---- edit geometry ----


	/**
	 * Open a dialog that allows the user to edit the geometry of the currently selected element
	 * from a dialog box.
	 * (N/A if the selection contains more than one element)
	 */
	public static class EditGeometryAction extends PEAction {

		public static final String KEY = "action.editorkit.EditGeometry";
		private Element target;

		/**
		 * target=currently selected object if there's one, no effect otherwise.
		 */
		public EditGeometryAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		/**
		 * target=given element
		 */
		public EditGeometryAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer, Element target){
			super(actionDispatcher,KEY,localizer);
			this.target = target;
			if (localizer!=null)
				putValue(NAME, localizer.getActionName(KEY)+" ("+target.getName()+")");
		}

		public void actionPerformed(ActionEvent e){
			Element elem = null;
			if (target==null)
				elem = getSelectedObject();
			else
				elem = target;
			if (elem != null && elem instanceof CustomizerFactory){
				getCanvas().beginUndoableUpdate((String)getValue(NAME)); // i18n'd if there was a localizer provided in the contructor
				AbstractCustomizer pane = ((CustomizerFactory)elem).createCustomizer();
				EnumSet<CustomizerDialog.ButtonMask> buttonsMask = EnumSet.of(CustomizerDialog.ButtonMask.OK_CANCEL);// & CustomizerDialog.RELOAD_BUTTON;
				DialogFactory df = getEditorKit().getDialogFactory();
				boolean modal = true;
				CustomizerDialog dlg = df.createCustomizerDialog(pane, modal, buttonsMask);
				if (elem instanceof TextEditable)
					dlg.setOkButtonClosesDialog(true);
				else
					dlg.setOkButtonClosesDialog(false);
				dlg.setVisible(true);
				boolean isCancelled = dlg.isCancelled();
				getCanvas().endUndoableUpdate();
				if (DEBUG) debug("isCancelled="+isCancelled);
				if (isCancelled) {
					try {
						if (DEBUG) debug("Undoing...");
						getCanvas().undo();
					}
					catch (javax.swing.undo.CannotUndoException ex){
						//if (DEBUG)
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Allows the user to switch the text to a small icon and back to full size.
	 */
	public static class EditTextModeAction extends PEAction {

		public static final String KEY = "action.editorkit.EditTextMode";
		private Element target;

		/**
		 * target=currently selected object if there's one, no effect otherwise.
		 */
		public EditTextModeAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		/**
		 * target=given element
		 */
		public EditTextModeAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer, Element target){
			super(actionDispatcher,KEY,localizer);
			this.target = target;
			if (localizer!=null)
				putValue(NAME, localizer.getActionName(KEY)+" ("+target.getName()+")");
		}

		/**
		 * changes the current textmode
		 */
		public void actionPerformed(ActionEvent e){
			Collection<Element> list;
			if (target==null)
				list = getCanvas().getSelectionHandler();
			else
			{
				list = new ArrayList<Element>(1);
				list.add(target);
			}
			boolean first = true;
			for(Element elem : list){
				if (elem != null && elem instanceof PicText){
					if(first){
						getCanvas().beginUndoableUpdate((String)getValue(NAME)); // i18n'd if there was a localizer provided in the contructor
						first = false;
					}
					((PicText)elem).setTextMode(!((PicText)elem).getTextMode());
				}
			}
			if(!first)
				getCanvas().endUndoableUpdate();

		}
	}

	// ---- paste ----

	/**
	 * Paste the content of the given <code>ClipBoard</code> into the target canvas.
	 */
	public static class PasteAction extends PEAction {
		private Clipboard clipboard;
		public static final String KEY_PASTE ="action.editorkit.Paste";
		public static final String KEY_PASTE_SPECIAL="action.editorkit.PasteSpecial";

		/** paste from the given clipboard */
		public PasteAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer,Clipboard clipboard){
			super(actionDispatcher,KEY_PASTE,localizer);
			this.clipboard = clipboard;
		}

		/** Paste from System clipboard ; action name = <code>PasteSpecial</code> */
		public PasteAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY_PASTE_SPECIAL,localizer);
			this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}

		public void undoableActionPerformed(ActionEvent e){
			try {
				double gridSnapStep = getCanvas().getGrid().getSnapStep();
				// translate clipboard before pasting
				PicPoint translate = new PicPoint(gridSnapStep,-gridSnapStep);
				getCanvas().paste(clipboard,translate);
			}
			catch (jpicedt.graphic.io.parser.ParserException pEx){
				DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
				df.showMessageDialog(
				    localize("exception.ParserWarning") + " :\n" + pEx,
				    localize("Paste"),
				    JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ioEx){
				DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
				df.showMessageDialog(
				    localize("exception.IOError") + "\n" + ioEx.getMessage(),
				    localize("Paste"),
				    JOptionPane.ERROR_MESSAGE);
			}
			catch (UnsupportedFlavorException ufEx){
				DialogFactory df = getCanvas().getEditorKit().getDialogFactory();
				df.showMessageDialog(
				    localize("UnsupportedDataFlavor") + "\n" + ufEx.getMessage(),
				    localize("Paste"),
				    JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// ---- copy ----

	/**
	 * Copy target's content to clipboard
	 */
	public static class CopyAction extends PEAction {
		public static final String KEY = "action.editorkit.Copy";
		private Clipboard clipboard;
		public CopyAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer,Clipboard clipboard){
			super(actionDispatcher,KEY,localizer);
			this.clipboard = clipboard;
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			getCanvas().copy(clipboard);
		}
	}

	// ---- cut ----

	/**
	 * Cut target's content to clipboard
	 */
	public static class CutAction extends PEAction {
		public static final String KEY = "action.editorkit.Cut";
		private Clipboard clipboard;
		public CutAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer,Clipboard clipboard){
			super(actionDispatcher,KEY,localizer);
			this.clipboard = clipboard;
		}

		public void undoableActionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			getCanvas().cut(clipboard);
		}
	}

	// ---- delete ----

	/**
	 * Delete the current selection
	 */
	public static class DeleteAction extends PEAction {
		public static final String KEY = "action.editorkit.Delete";
		public DeleteAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			getCanvas().deleteSelection();
		}
	}

	// ---- select all ----

	/**
	 * selects all objects in the current active internal frame
	 */
	public static class SelectAllAction extends PEAction {
		public static final String KEY = "action.editorkit.SelectAll";
		public SelectAllAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			getCanvas().selectAll();
		}
	}

	// ---- zoom in ----

	/**
	 * zoom in
	 */
	public static class ZoomInAction extends PEAction {
		public static final String KEY = "action.editorkit.ZoomIn";
		public ZoomInAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			double zoom = getCanvas().getZoomFactor();
			zoom *= 2.0;
			getCanvas().setZoomFactor(zoom);
		}
	}

	// ---- zoom out ----

	/**
	 * zoom out
	 */
	public static class ZoomOutAction extends PEAction {
		public static final String KEY = "action.editorkit.ZoomOut";
		public ZoomOutAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		public void actionPerformed(ActionEvent e){
			double zoom = getCanvas().getZoomFactor();
			zoom /= 2.0;
			getCanvas().setZoomFactor(zoom);
		}
	}

	// ---- edit drawing's bounding box ----

	/**
	 * Edit <code>Drawing</code>'s bounding box.
	 */
	public static class EditBoundingBoxAction extends PEAction {

		public static final String KEY = "action.editorkit.EditBoundingBox";

		public EditBoundingBoxAction(ActionDispatcher actionDispatcher,ActionLocalizer localizer){
			super(actionDispatcher,KEY,localizer);
		}

		public void actionPerformed(ActionEvent e){
			if (getCanvas()==null) return;
			Rectangle2D oldBB = getCanvas().getDrawing().getBoundingBox();
			// Vincent B. [pending], oldNN == null
			boolean oldIsAuto = getCanvas().getDrawing().isAutoComputeBoundingBox();
			boolean oldIsVis = getCanvas().getDrawing().isDisplayBoundingBox();

			DialogFactory df = getEditorKit().getDialogFactory();
			AbstractCustomizer customizer = getCanvas().getDrawing().getCustomizer();
			boolean modal = true;
			EnumSet<CustomizerDialog.ButtonMask> buttonMask = EnumSet.of(CustomizerDialog.ButtonMask.ALL);
			CustomizerDialog dlg = df.createCustomizerDialog(customizer,modal, buttonMask);
			dlg.setVisible(true);
			if (dlg.isCancelled()){
				getCanvas().getDrawing().setAutoComputeBoundingBox(oldIsAuto);
				getCanvas().getDrawing().setDisplayBoundingBox(oldIsVis);
				getCanvas().getDrawing().setBoundingBox(oldBB);
			}
			else
			{
				Rectangle2D newBB = getCanvas().getDrawing().getBoundingBox();
				if(!newBB.equals(oldBB))
					getCanvas().refreshPageFormatToBoundingBox();
			}
		}
	}

	// ---- Action Map ----

	/**
	 * @return an ActionMap that can be used on a PECanvas that is using a model, view and
	 *         formatter produced by this kit. ActionCommand key is created from key
	 *         ACTION_COMMAND_KEY.
	 */
	public static ActionMap createActionMap(ActionDispatcher dispatcher, ActionLocalizer localizer){

		ActionMap map = new ActionMap();
		Action a;

		// copy, cut, delete, paste, pasteSpecial, selectAll :
		a = new CopyAction(dispatcher,localizer,clipboard); // clipboard is a static variable !
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
 		a = new CutAction(dispatcher,localizer,clipboard);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
 		a = new DeleteAction(dispatcher,localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
 		a = new PasteAction(dispatcher,localizer,clipboard);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new PasteAction(dispatcher,localizer); // paste special
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
 		a = new SelectAllAction(dispatcher,localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// edit disposition :
		a = new EditDispositionAction(dispatcher, localizer, EditDispositionAction.TOBACK);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new EditDispositionAction(dispatcher, localizer, EditDispositionAction.BACKWARD);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new EditDispositionAction(dispatcher, localizer, EditDispositionAction.FORWARD);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new EditDispositionAction(dispatcher, localizer, EditDispositionAction.TOFRONT);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// toggle edit points mode :
		a = new ToggleEditPointsModeAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// registered mouse tool :
		String[][] allToolNames = getAvailableToolNames();
		for (int i=0; i<allToolNames.length; i++){
			for (int j=0; j<allToolNames[i].length; j++){
				a = new SelectMouseToolAction(dispatcher, localizer,allToolNames[i][j]);
				map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
			}
		}

		// edit attributes :
		a = new EditGeometryAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// edit text display mode (text/icon) :
		a = new EditTextModeAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// group and related : (for ungrouping, see PicGroup)
		a = new GroupAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new JoinCurvesAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		// zoom in, zoom out, grid :
		a = new ZoomInAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new ZoomOutAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);
		a = new ShowGridAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);

		// bounding box :
		a = new EditBoundingBoxAction(dispatcher, localizer);
		map.put(a.getValue(Action.ACTION_COMMAND_KEY), a);

		return map;
	}

	/////////////////////////////////////////
	//// KEYBRD EVENTS MANAGEMENT
	///////////////////////////////////////////

	/**
	 * @return an ActionMap that can be used on a PECanvas that is using a model, view and
	 *         formatter produced by this kit. ActionCommand key is created from key
	 *         ACTION_COMMAND_KEY.
	 * [todo] provide a Properties object for key-bindings
	 */
	public InputMap createInputMap(){

		InputMap map = new InputMap();
		map.put(KeyStroke.getKeyStroke('/'),EditDispositionAction.FORWARD);
		map.put(KeyStroke.getKeyStroke('*'),EditDispositionAction.BACKWARD);
		map.put(KeyStroke.getKeyStroke('+'),ZoomInAction.KEY);
		map.put(KeyStroke.getKeyStroke('-'),ZoomOutAction.KEY);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2,0),EditGeometryAction.KEY);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0),EditTextModeAction.KEY);
		// add your own key/action bindings here ...
		return map;
	}

	/////////////////////////////////////////
	//// MISC STATIC METHODS
	///////////////////////////////////////////
	/**
	 * Return a static reference to the local ClipBoard shared across all instance of this EditorKit
	 */
	public static Clipboard getClipboard(){
		return clipboard;
	}


}
