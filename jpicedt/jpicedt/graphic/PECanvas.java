// PECanvas.java --- -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PECanvas.java,v 1.53 2013/03/27 07:20:46 vincentb1 Exp $
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
package jpicedt.graphic;

import jpicedt.graphic.event.SelectionListener;
import jpicedt.graphic.event.SelectionEvent;
import jpicedt.graphic.event.ZoomListener;
import jpicedt.graphic.event.PEMouseInputListener;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.PEEventMulticaster;
import jpicedt.graphic.event.RotateEvent;
import jpicedt.graphic.event.RotateListener;
import jpicedt.graphic.event.ZoomEvent;
import jpicedt.graphic.event.ConvexZoneSelectionListener;
import jpicedt.graphic.event.ConvexZoneSelectionEvent;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicMultiCurveConvertable;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.grid.Grid;
import jpicedt.graphic.toolkit.ConvexZoneGroup;
import jpicedt.graphic.toolkit.ConvexZoneSet;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.ConvexZone;
import jpicedt.graphic.toolkit.TransferableGraphic;
import jpicedt.graphic.io.formatter.CommentFormatting;
import jpicedt.graphic.io.formatter.JPICFormatter;
import jpicedt.graphic.io.parser.ExtractionParsing;
import jpicedt.graphic.io.parser.ParsedDrawing;
import jpicedt.graphic.io.parser.ParserException;
import jpicedt.graphic.io.util.JpicDocUserData;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.io.StringWriter;
import java.beans.*;
import java.awt.datatransfer.*;
import javax.swing.undo.*;

import static jpicedt.Log.*;

/**
 * <p>This is a <code>JComponent</code> on which graphic elements are drawn. It's has an underlying model (a
 * <code>Drawing</code>) to represent the content, an <code>EditorKit</code> to manipulate the content, and a
 * View responsible for rendering the content. <code>EditorKit</code>, <code>Drawing</code>, and
 * <code>View</code>'s are pluggable: the <code>EditorKit</code> is responsible for creating
 * <ol><li> a default <code>Drawing</code> and</li>
 * <li> a <code>ViewFactory</code> that will populate the View tree associated with the <code>Drawing</code>,
 * by attaching a <code>View</code> to each element in the model.</li></ol></p>
 * <p> Depending on the content type this Component is loaded with, it may plug a new <code>EditorKit</code>
 * on-the-fly that's suited for the given content type (e.g. LaTeX, Postscript, SVG-XML, etc&hellip;).
 *
 * <h1>Model to View/View to Model</h1>
 * <p>
 * Graphic objects are stored (e.g. in a <code>Drawing</code>)
 * in natural coordinates, ie LaTeX/Postscript/&hellip; coordinates, using e.g. a "1 mm" unitlength :
 * that's what we call "model coordinate". Obviously, we've to translate these coordinates to
 * screen coordinates (e.g. JViewport-coordinate or JPanel-coordinate) before rendering,
 * and this is done very simply by using an <code>AffineTransform</code> and adding it to the
 * current Graphic2D context in the body of the "paintComponent" method. The following picture
 * sums up the translation process that takes place b/w model- and view-coordinate.
 * </p>
 * <p><img src="doc-files/model2view.png"></p>
 * <p>The benefits of such an approach is to make it easy for someone willing to develop a parser/formater
 * to handle objects coordinates with the fewest possible overhead, since objects coordinates
 * are "natively" available in natural (ie from left to right and from bottom to top) coordinates, and
 * this is perfectly suited for formatting language like LaTeX, Postscript or SVG-XML. Besides,
 * this makes sense with the grid ticks marks.
 * </p>
 * <p>Margins are encapsulated in a <code>PageFormat</code> (an inner class) object. Contrary to previous
 * jpicedt releases, it's no longer necessary, as of jpicedt 1.3.2, to provide formatter methods with the
 * <code>ptOrg</code> parameter.</p>
 * <p>In addition to the standard behaviour inherited from <code>JPanel</code>,
 * <code>PropertyChangeEvent</code>'s are triggered when:
 * <ul>
 * <li>changing the page format;</li>
 * <li>changing the zoom factor;</li>
 * <li>changing the editor kit;</li>
 * </ul>
 * </p>
 * @since jpicedt 1.0
 * @author Sylvain Reynal
 * @version $Id: PECanvas.java,v 1.53 2013/03/27 07:20:46 vincentb1 Exp $ <p>
 */

public class PECanvas extends JPanel implements Scrollable {

	//////////////////////////////////// PUBLIC FIELDS /////////////////////////////////

	public static final double[] PREDEFINED_ZOOMS =     { 1.0,   2.0,  4.0,   8.0};
	public static final double ZOOM_DEFAULT = 1.0;

	public static enum SelectionBehavior { REPLACE, INCREMENTAL};

	/** key for <code>Properties</code>'s zoom value */
	public static final String KEY_ZOOM = "canvas.zoom";
	/** key for <code>Properties</code>'s content-type value */
	public static final String KEY_CONTENT_TYPE = "canvas.content-type";
	/** key for <code>Properties</code>'s nb of undoable steps value */
	public static final String KEY_UNDOABLE_STEPS = "canvas.max-undoable-steps";
	/** default undoable events to remember */
	public static final int MAX_UNDOABLE_STEPS_DEFAULT = 100; // same as UndoManager


	/** property name for drawing-change events */
	public static final String DRAWING_CHANGE = "drawing-change";

	/**
	 * nom de la propriété pour les évènement de changement d'ensemble de
	 * zones convexes.
	 */
	public static final String CONVEX_ZONE_SET_CHANGE = "convex-zone-set-change";


	/** property name for editorkit-change events */
	public static final String EDITOR_KIT_CHANGE = "editor-kit-change";

	/** property name for content-type's change events */
	public static final String CONTENT_TYPE_CHANGE = "content-type-change";

	//////////////////////////////////// PROTECTED FIELDS /////////////////////////////////

	/** the model for this canvas */
	protected Drawing drawing;

	/**
	 * Une liste de zones convexe que l'utilisateur a définies pour éditer le
	 * dessins pour cette toile à dessin  <code>PECanvas</code>.
	 */
	protected ConvexZoneSet convexZoneSet;

	/** pageFormat encapsulates board size and margin data */
	protected PageFormat pageFormat;

	/** The current content-type for this <code>PECanvas</code> (determines the <code>EditorKit</code>
		behaviour). */
	protected ContentType contentType;

	/** the <code>AffineTransform</code> used to translate from model-coordinates to view-coordinates; gets
	 *  updated each time either the zoom factor or the page format changes */
	protected AffineTransform model2ViewTransform;

	/** the <code>AffineTransform</code> used to translate from mouse-coordinates to model-coordinates;
	 *  gets updated each time model2ViewTransform changes */
	protected AffineTransform view2ModelTransform;

	/** the grid attached to this canvas */
	protected Grid grid;

	/** the current editor kit for this component */
	protected EditorKit kit;

	/** the <code>UndoableEditSupport</code> delegate for <code>UndoableEditEvent</code> firing */
	protected UndoableEditSupport undoableEditSupport;

	/** the <code>UndoManager</code> delegate for undo/redo operation */
	protected UndoManager undoManager;

	/** the <code>UndoableEdit</code> in progress */
	protected StateEdit stateEdit;

	/** A Map storing <code>RenderingHints</code> to be applied to the graphic context when rendering the
		drawing. */
	protected RenderingHints renderingHints = new RenderingHints(null);


	//////////////////////////////////// PRIVATE FIELDS /////////////////////////////////

	/** The zoom factor ; this determines the zoom factor to be applied to
	 * <code>Graphics2D</code> through an <code>AffineTransform</code>. */
	private double zoom;

	/** The total scale factor, including DPMM and zoom ; updated each time zoom changes. */
	private double scale;

	/** Chained list of <code>PEMouseInputListener</code>'s for this component. */
	private PEMouseInputListener mouseInputListener;

	/** Tmp. buffer used by <code>processMouseEvent</code> */
	private PicPoint peMousePoint = new PicPoint();

	/** Tmp. buffer used by <code>repaintFromModelRect</code> */
	private double[] tmpCoords = new double[2];
	private Rectangle repaintRectangle = new Rectangle();

	private double mult = Grid.snapStepDEFAULT;

	private JpicDocUserData   preambleUserData;
	private JpicDocUserData   postambleUserData;

	//////////////////////////////////// CONSTRUCTORS /////////////////////////////////


	/**
	 * Construct a new <code>PECanvas</code> initialized with default values.
	 */
	public PECanvas(){
		this(ZOOM_DEFAULT, new PageFormat(), new Grid(), null);
	}

	/**
	 * Construct a new <code>PECanvas</code> with the default editor-kit and drawing as content storage.
	 * <br><b>author:</b> Sylvain Reynal
	 * @param zoom initial zoom factor
	 * @param pageFormat page format (page size + page margins)
	 * @param grid grille d'aimantation
	 * @param contentType (e.g. LaTeX, PsTricks,&hellip;) ; this will determine the EditorKit for
	 *        editing the Drawing, and indirectly the ViewFactory that produces View's for the drawing,
	 *        (since the ViewFactory is obtained through the currently installed EditorKit) and the
	 *        FormatterFactory used to write the drawing to a writer.
	 *        If null, the default editor-kit/content-type is used.
	 * @since PicEdt 1.0
	 */
	public PECanvas(double zoom, PageFormat pageFormat, Grid grid, ContentType contentType){

		if (DEBUG) debug("start");
		this.zoom = zoom;
		setPageFormat(pageFormat); // need zoom to be non-null
		setZoomFactor(zoom);
		// create a default drawing, and create View's for it using
		// the ViewFactory for the given content-type
		setContentType(contentType);
		if (DEBUG) debug("Installing grid");
		this.grid = grid;
		// miscellaneous...
		// [pending] setBorder(BorderFactory.createEtchedBorder());
		setBackground(Color.white);
		// undo/redo
		this.undoableEditSupport = new UndoableEditSupport(this); // source for event = PECanvas
		this.undoManager = new UndoManager();
		setUndoLimit(MAX_UNDOABLE_STEPS_DEFAULT);
		this.undoableEditSupport.addUndoableEditListener(this.undoManager);

		if (DEBUG) debug("completed !");

		// [pending] debug (press F3 to write a comment line for lisibility)
		if (DEBUG)
			registerKeyboardAction(new ActionListener(){
			                       public void actionPerformed(ActionEvent e){
				                       System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			                       }},KeyStroke.getKeyStroke(KeyEvent.VK_F3,0),WHEN_FOCUSED);

		/**
		 * adds a keylistener to the Canvas in order to move ojects with the cursorkeys
		 */
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
					mult =	(getGrid().getSnapStepIndex(mult) < Grid.PREDEFINED_SNAP_STEPS.length - 1?
						Grid.PREDEFINED_SNAP_STEPS[(getGrid().getSnapStepIndex(mult))+1] :
						Grid.PREDEFINED_SNAP_STEPS[getGrid().getSnapStepIndex(mult)]);
				}
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					mult = 	(getGrid().getSnapStepIndex(mult) > 0 ?
						Grid.PREDEFINED_SNAP_STEPS[(getGrid().getSnapStepIndex(mult))-1] :
						Grid.PREDEFINED_SNAP_STEPS[getGrid().getSnapStepIndex(mult)]);
				}
				if(e.getKeyCode() == KeyEvent.VK_UP) // up
					for ( Iterator<Element> i = selection(); i.hasNext(); )
						i.next().translate(0, mult);
				if(e.getKeyCode() == KeyEvent.VK_DOWN) // down
					for ( Iterator<Element> i = selection(); i.hasNext(); )
						i.next().translate(0, -mult);
				if(e.getKeyCode() == KeyEvent.VK_LEFT) // left
					for ( Iterator<Element> i = selection(); i.hasNext(); )
						i.next().translate(-mult, 0);
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) // right
					for ( Iterator<Element> i = selection(); i.hasNext(); )
						i.next().translate(mult, 0);
			}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SHIFT)
					mult = getGrid().getSnapStep();
				if(e.getKeyCode() == KeyEvent.VK_CONTROL)
					mult = getGrid().getSnapStep();
			}
			public void keyTyped(KeyEvent e) {
			}
        	});

		this.addMouseMotionListener(new IconizedTextTooltipDisplayListener(this));
	}



	///////////////////////////////////////////////////////////////////////////////
	//// PAINT
	//////////////////////////////////////////////////////////////////////////////

	/**
	 * <code>paintComponent(Graphics g)</code> is called by
	 * <code>AWTEventDispatchThread</code> via "<code>paint(g)</code>"
	 * it's absolutely necessary to call <code>super.paintComponent(g)</code>
	 * so that the background gets properly painted (<code>PECanvas</code> is opaque)
	 *<p>
	 * This implementation first applies an AffineTransform to the graphic context : this transform maps in
	 * effect model-coordinates to screen-coordinates. Then invokes the paint() method in that order :
	 * <ul>
	 * <li> on the <code>Grid</code> object attached to this <code>PECanvas</code>;</li>
	 * <li> on the root-view associated with <code>Drawing.RootElement</code>.</li>
	 * <li> on the <code>EditorKit</code> in which this <code>PECanvas</code> is installed.</li>
	 * </ul>
	 *
	 */
	public void paintComponent(Graphics g){

		super.paintComponent(g); // paint background
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHints(renderingHints);
		g2.transform(model2ViewTransform); // Note : g2.setTransform(model2ViewTransform) -> bug when partial repaint !!! use g2.transform instead
		Rectangle2D allocation = g2.getClip().getBounds2D();

		if (DEBUG) {
			debug("clip=" + allocation);
			g2.setPaint(Color.green);
			g2.draw(allocation);
		}
		if (grid != null) grid.paint(g2,allocation,scale);
		if (drawing != null && drawing.getRootView()!=null)
			drawing.getRootView().paint(g2,allocation); // security : if we are just installing a new drawing and its view-tree hasn't been set yet

		if(kit != null){

			if( convexZoneSet != null
				&& kit.isConvexZoneSetShown())
				convexZoneSet.paint(g2,allocation,scale);

			kit.paint(g2,allocation,scale);
		}
	}

	/**
	 * Add the given rectangle, given in model-coordinates, to the list of dirty regions.
	 */
	public void repaintFromModelRect(Rectangle2D rect){

		if (DEBUG) debug("repaintFromModelRect");
		if (DEBUG) debugAppendLn("rect2D="+rect);
		// fetch topleft corner :
		tmpCoords[0] = rect.getX();
		tmpCoords[1] = rect.getMaxY();
		// translate it to view coord system :
		model2ViewTransform.transform(tmpCoords,0,tmpCoords,0,1);
		// set topleft corner for destination rectangle
		repaintRectangle.x = (int)tmpCoords[0];
		repaintRectangle.y = (int)tmpCoords[1];
		// fetch double-precision width and height
		tmpCoords[0] = rect.getWidth();
		tmpCoords[1] = rect.getHeight();
		// delta-translate it to view coord system :
		model2ViewTransform.deltaTransform(tmpCoords,0,tmpCoords,0,1);
		// set dimension for destination rectangle
		repaintRectangle.width = (int)tmpCoords[0];
		repaintRectangle.height = -(int)tmpCoords[1];
		// add repaintRectangle to dirty region
		if (DEBUG) debugAppendLn("repaintRect="+repaintRectangle);
		repaint(repaintRectangle);
	}

	/**
	 * Return the <code>RenderingHints</code> applied to the graphic context when rendering this component.
	 */
	public RenderingHints getRenderingHints(){
		return renderingHints;
	}




	////////////////////////////////////////////////////
	//// CONTENT HANDLING
	////////////////////////////////////////////////////

	/**
	 * @return the model, ie a <code>Drawing</code> containing only non-selected objects
	 */
	public Drawing getDrawing(){
		return drawing;
	}

	/**
	 * @return le <code>ConvexZoneSet</code> courant défini pour le dessin de
	 * ce <code>PECanvas</code>.
	 */
	public ConvexZoneSet getConvexZoneSet(){
		return this.convexZoneSet;
	}


	public void addConvexZone(ConvexZone cz){
		convexZoneSet.add(cz);
	}

	/**
	 * Set the <code>Drawing</code> model for this component. The currently registered <code>EditorKit</code>
	 * is used to build a viewtree for the drawing. A <code>PropertyChange</code> event
	 * (<code>DRAWING_CHANGE</code>) is sent to each listener.
	 */
	public void setDrawing(Drawing dr){
		if (DEBUG) debug("drawing="+dr);
		Drawing old = this.drawing;
		this.drawing = dr;
		this.drawing.setViewTree(getEditorKit().getViewFactory());
		firePropertyChange(DRAWING_CHANGE, old, this.drawing); // [pending] doesn't seem to be fired !
	}


	public void setConvexZoneSet(ConvexZoneSet czs){
		if (DEBUG) debug("convexZoneSet="+czs);
		ConvexZoneSet old = this.convexZoneSet;
		this.convexZoneSet = czs;
		firePropertyChange(CONVEX_ZONE_SET_CHANGE, old, this.convexZoneSet); // [pending] doesn't seem to be fired !
	}

	/**
	 * Fetches the currently installed kit for handling content.
	 */
	public EditorKit getEditorKit(){
		return kit;
	}

	/**
	 * Creates a default editor kit (<code>EditorKit</code>) whose factory delegates are initialized from the
	 * given content-type. If an EditorKit was already installed in this <code>PECanvas</code>, we try to
	 * reuse its properties in the new <code>EditorKit</code>, if applicable.
	 * @param contentType null if a default content-type should be used
	 * @return the editor kit
	 */
	protected EditorKit createDefaultEditorKit(ContentType contentType) {
		if (DEBUG) debug("createDefaultEditorKit");
		return new EditorKit(contentType, this.kit);
	}

	/**
	 * Sets the currently installed kit for handling content.  This is the bound property that
	 * establishes the content type of the editor. Any old kit is first deinstalled, then if kit is
	 * non-<code>null</code>, the new kit is installed. <p>
	 * A default drawing is created from it if there was no drawing set in this canvas before,
	 * otherwise the old drawing is reused : in both cases, <code>setDrawing</code> is called, but
	 * this allows the caller to change the ContentType w/o changing the Drawing if it deems it unnecessary
	 * (otherwise, it may call <code>setDrawing()</code> afterwards).
	 * A <code>PropertyChange</code> event (<code>EDITOR_KIT_CHANGE</code>) is always fired when
	 * <code>setEditorKit</code> is called.
	 * @param kit the desired editor behavior
	 * @see #getEditorKit
	 */
	public void setEditorKit(EditorKit kit) {
		if (DEBUG) debug("kit="+kit);
		EditorKit old = this.kit;
		if (old != null) {
			old.deinstall(this);
		}
		this.kit = kit;
		if (this.kit != null) {
			this.kit.install(this); // add mouse/keybrd event listeners to PECanvas
			if (drawing == null) setDrawing(new Drawing());
			else setDrawing(drawing);
			if(convexZoneSet == null)
				setConvexZoneSet(new ConvexZoneSet());
			else
				setConvexZoneSet(convexZoneSet);
		}
		firePropertyChange(EDITOR_KIT_CHANGE, old, kit);
	}




	///////////////////////////////////
	///// CONTENT TYPE
	///////////////////////////////////
	/**
	 * @return the current content-type
	 */
	public ContentType getContentType(){
		return contentType;
	}

	/**
	 * Change the current content-type: this implies plugging a new <code>ViewFactory</code> and a new
	 * <code>FormatterFactory</code> to the currently installed EditorKit, as obtained from the given
	 * <code>newContentType</code> argument.
	 * @param newContentType if null, the <code>DefaultContentType</code> is installed.
	 */
	public void setContentType(ContentType newContentType){
		ContentType old = this.contentType;
		if (old!=null && old==newContentType) return;
		this.contentType = newContentType;
		// update editor's factories:
		if (this.kit == null){ // first install a default kit
			setEditorKit(createDefaultEditorKit(newContentType));
		}
		else {
			this.kit.setFactoriesFromContentType(newContentType);
			this.drawing.setViewTree(getEditorKit().getViewFactory()); // else this was done by invoking setEditorKit()
		}
		firePropertyChange(CONTENT_TYPE_CHANGE, old, this.contentType);
	}


	///////////////////////////////////
	///// DRAWING BOARD SIZE
	///////////////////////////////////

	/**
	 * Set the size of the drawing board. Length are given in mm (this should approximately
	 * represent true mm on the screen, however this might slightly depend on the underlying platform).
	 * <br>
	 * This in turn sets the preferred size of the component. .
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void setPageFormat(PageFormat pageFormat){

		if (DEBUG) debug("page-format="+pageFormat);
		PageFormat oldFormat = this.pageFormat;
		this.pageFormat = pageFormat;
		model2ViewTransform = pageFormat.getModel2ViewTransform(zoom);
		view2ModelTransform = pageFormat.getView2ModelTransform(zoom);
		setPreferredSize(pageFormat.getSizePx(zoom));
		//firePropertyChange(PAGE_FORMAT_CHANGE, oldFormat, this.pageFormat); //[pending] bug : not fired !
		// give a chance
		// to listeners to update their layout (e.g. ScrollPane)
		invalidate();
		fireZoomUpdate(this.zoom, this.zoom, null); // force e.g. a scrollpane to update rulers
		//validate();
		repaint();
	}

	/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return the page format for this drawing board
		 * @since jPicEdt
		 */
	public PageFormat getPageFormat(){
		return pageFormat;
	}

	/**
	 * @return the pixel-coordinates of the (0,0) model origin when zoom = 1.0
	 */
	public PicPoint getSheetOrigin(){
		return pageFormat.getOrgPx(1.0);
	}

	/**
	 * @return the grid attached to this canvas
	 */
	public Grid getGrid() {
		return grid;
	}

	///////////////////////////////////
	///// Model <-> View
	///////////////////////////////////


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return an <code>AffineTransform</code> that represents the maping b/w the model-coordinate system
	 *         and the pixel coordinate system. Guaranteed not to change over time.
	 * @since jPicEdt
	 */
	public AffineTransform getModelToViewTransform(){
		return (AffineTransform)model2ViewTransform.clone();
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return an <code>AffineTransform</code> that represents the maping b/w the pixel-coordinate system
	 *         and the model-coordinate system. Guaranteed not to change over time.
	 * @since jPicEdt
	 */
	public AffineTransform getViewToModelTransform(){
		return (AffineTransform)view2ModelTransform.clone();
	}

	/**
	 * Converts a point from the model-coordinate system to the pixel-coordinate system.
	 * <br><b>author:</b> Sylvain Reynal
	 * @param src the source point in model-coordinate
	 * @param dst the destination point ; if null, a new point is allocated, and returned for convenience.
	 * @return the result (same as dst if non-null)
	 * @since jPicEdt
	 */
	public PicPoint modelToView(PicPoint src, PicPoint dst){
		if (dst==null) dst = new PicPoint();
		// fetch topleft corner :
		tmpCoords[0] = src.x;
		tmpCoords[1] = src.y;
		// translate it to view coord system :
		model2ViewTransform.transform(tmpCoords,0,tmpCoords,0,1); // transform 1 point
		dst.x = tmpCoords[0];
		dst.y = tmpCoords[1];
		return dst;
	}

	/**
	 * Converts a point from the pixel-coordinate system to the model-coordinate system.
	 * <br><b>author:</b> Sylvain Reynal
	 * @param src the source point in pixel-coordinate
	 * @param dst the destination point ; if null, a new point is allocated, and returned for convenience.
	 * @return the result (same as dst if non-null)
	 * @since jPicEdt
	 */
	public PicPoint view2Model(PicPoint src, PicPoint dst){
		if (dst==null) dst = new PicPoint();
		// fetch topleft corner :
		tmpCoords[0] = src.x;
		tmpCoords[1] = src.y;
		// translate it to view coord system :
		view2ModelTransform.transform(tmpCoords,0,tmpCoords,0,1); // transform 1 point
		dst.x = tmpCoords[0];
		dst.y = tmpCoords[1];
		return dst;
	}

	/**
	 * Converts a Shape from the model-coordinate system to the pixel-coordinate system.
	 * <br><b>author:</b> Sylvain Reynal
	 * @return A new <code>Shape</code> corresponding to the given <code>Shape</code> once transformed to the
	 * pixel-coordinate system.
	 * @param src a Shape in the model-coordinate system
	 * @since jPicEdt
	 */
	public Shape modelToView(Shape src){
		return model2ViewTransform.createTransformedShape(src);
	}

	/**
	 * Converts a Shape from the pixel-coordinate system to the model-coordinate system.
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a new Shape corresponding to the given Shape once transformed to the model-coordinate system.
	 * @param src a Shape in the pixel-coordinate system
	 * @since jPicEdt
	 */
	public Shape viewToModel(Shape src){
		return view2ModelTransform.createTransformedShape(src);
	}

	///////////////////////////////////
	///// I/O
	///////////////////////////////////
	/**
	 * Refraîchit le format de page de la toile à dessin en fonction de la
	 * boîte bornante du dessin sous-jascent.
	 *@since jPicEdt 1.5.2
	 */
	public void refreshPageFormatToBoundingBox(){
		Rectangle2D bb = drawing.getBoundingBox();
		if (bb != null && !this.pageFormat.isFitInto(bb)){
			//PageFormat newPageFormat = new PageFormat(); // [pending] clone current format and enlarge it
			//newPageFormat.fitTo(bb);
			this.pageFormat.enlargeTo(bb);
			//setPageFormat(newPageFormat);
			setPageFormat(pageFormat);
		}
	}

	/**
	 * Read drawing content from a reader and erase old one. <code>Listener</code>'s interested in
	 * <code>DrawingEvent</code>'s should register their listener anew (this can be done systematically by
	 * registering a <code>PropertyCHangeListener</code> to this canvas, and waiting for
	 * <code>DRAWING_CHANGE</code> events).
	 * @param reader the reader to read content from
	 */
	public ParsedDrawing read(Reader reader, ExtractionParsing parser) throws jpicedt.graphic.io.parser.ParserException {
		getEditorKit().getSelectionHandler().clear(); // don't fire selection event
		ParsedDrawing parsed = parser.extractAndParse(reader); // takes some time...
		Drawing dr = parsed.drawing;
		setDrawing(dr);
		refreshPageFormatToBoundingBox();
		repaint();

		preambleUserData = parsed.preambleUserData;
		postambleUserData = parsed.postambleUserData;
		return parsed;
	}

	/**
	 * Insère du contenu depuis un <code>reader</code> dans le dessin courant.
	 * @param reader le <code>reader</code> à partir duquel insérer du contenu.
	 * @param insertionPoint la position où insérer la fragement. Si <code>null</code> le fragment est inséré
	 * en (0,0).
	 */
	public void insert(Reader reader, ExtractionParsing parser,PicPoint insertionPoint) throws jpicedt.graphic.io.parser.ParserException {

		//jpicedt.graphic.io.parser.LaTeXParser latexParser = new jpicedt.graphic.io.parser.LaTeXParser(); // adapt parser to the type of content this EditorKit handles
		Drawing parsed = parser.parse(reader);
		SelectionHandler selectionHandler = kit.getSelectionHandler();
		selectionHandler.clear(); // don't fire selection event
		// add parsed content to the current drawing
		for (Element o: parsed){
			Element oo = o.clone();
			if(insertionPoint != null)
				oo.translate(insertionPoint.getX(),insertionPoint.getY());
			drawing.add(oo);
			selectionHandler.add(oo);
		}
		fireSelectionUpdate(selectionHandler.asArray(), SelectionEvent.EventType.SELECT);
		// add more non-parsed command to current drawing, if applicable :
		if (parsed.getNotparsedCommands().length()==0) return;
		String s = drawing.getNotparsedCommands();
		if (s==null || s.length()==0) drawing.setNotparsedCommands(s);
		else {
			s += "\n";
			s += parsed.getNotparsedCommands();
			drawing.setNotparsedCommands(s);
		}
	}

	/**
	 * Write drawing content to the given stream.
	 * @param writer The writer to write to
	 * @param writeSelectionOnly if true, only write selection content
	 * @exception IOException on any I/O error
	 * @since jPicEdt 1.3
	 */
	public void write(Writer writer, boolean writeSelectionOnly) throws IOException {

		StringWriter stringWriter = new StringWriter();
		CommentFormatting commentFormatter = getEditorKit().getFormatterFactory().getCommentFormatter();
		commentFormatter.setWriter(stringWriter);
		if (writeSelectionOnly){
			Drawing dr = new Drawing(getEditorKit().getSelectionHandler()); // deep copy
			// translate fragment to (0,0)
			Rectangle2D bb = dr.getBoundingBox();
			dr.getRootElement().translate(- bb.getX(), -bb.getY());
			// first write JPIC-XML text :
			stringWriter.write((new jpicedt.graphic.io.formatter.JPICFormatter(
						  commentFormatter
							   )).createFormatter(dr,null).format());
			// then write content-type specific text, except if the currently installed content-type is the
			// default since the latter already handles the JPIC-XML format... and we would get the JPIC-XML
			// text twice...
			if (!(this.contentType instanceof DefaultContentType))
				stringWriter.write(getEditorKit().getFormatterFactory().createFormatter(dr,null).format());
		}
		else {
			// Tout d'abord le prologue utilisateur
			if(preambleUserData != null){
				preambleUserData.format(commentFormatter);
			}

			// first write JPIC-XML text :
			stringWriter.write((new JPICFormatter(
						  commentFormatter
							   )).createFormatter(drawing,null).format());
			// then write content-type specific text, except if the currently installed content-type is the default
			// since the latter already handles the JPIC-XML format... and we would get the JPIC-XML text twice...
			if (!(this.contentType instanceof DefaultContentType))
				stringWriter.write(getEditorKit().getFormatterFactory().createFormatter(drawing,null).format());

			// Finalement l'épilogue utilisateur
			commentFormatter.setWriter(stringWriter);
			if(postambleUserData != null){
				commentFormatter.strongCommentFormat("User Data");
				postambleUserData.format(commentFormatter);
			}


		}
		writer.write(stringWriter.toString());
	}






	///////////////////////////////////
	///// ZOOM
	///////////////////////////////////

	/**
	 * Convenience call to <code>setZoomFactor(zoom,null)</code>.
	 */
	public void setZoomFactor(double zoom){
		setZoomFactor(zoom,null);
	}

	/**
	 * Sets the current zoom factor to the given double, then updates various
	 * properties (model &harr; view transforms, dimension,
	 * preferredSize&hellip;), finally,
	 * sources a ZoomEvent to give a chance to receiver to update their state accordingly (this may
	 * be used e.g. by a parent scrollpane to update its view port location, or by a GUI widget
	 * to reflect the new zoom value).
	 * @param zoom the new zoom factor
	 * @param ptClick this only makes sense if the parent of this component is aka ScrollPane ;
	 *        <br> Coordinates for this point are in the model-coordinate system.
	 */
	public void setZoomFactor(double zoom, PicPoint ptClick){

		if (DEBUG) debug("zoom="+zoom);
		double oldZoom = this.zoom; // store old value for further use by zoom event
		this.zoom = zoom; // [pending] test if zoom really changed !

		// update global variables according to new zoom value :
		model2ViewTransform = pageFormat.getModel2ViewTransform(zoom);
		view2ModelTransform = pageFormat.getView2ModelTransform(zoom);
		scale = model2ViewTransform.getScaleX(); // assumption : scaleX = scaleY !

		// update preferred size according to new zoom :
		setPreferredSize(pageFormat.getSizePx(zoom));
		invalidate(); // JDK's documentation says : "revalidate()"
		// give a change to a scrollpane to update the view location in the viewport and ruler sizes :
		fireZoomUpdate(oldZoom, this.zoom, ptClick);
		repaint();
	}

	/**
	 * @return the current zoom factor.
	 */
	public double getZoomFactor(){
		return zoom;
	}

	/**
	 * @return the current scale factor between model- and view-coordinates, as given by the current
	 *         <code>model2ViewTransform</code>. This is usually the product of the current zoom factor, and
	 *         the DotPerMilliMeter screen factor.
	 */
	public double getScaleFactor(){
		return scale;
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * @param oldZoom previous zoom value
	 * @param newZoom new zoom value
	 * @param ptClick the point (in model-coordinates) that is expected to be at the center of the view-port ;
	 *        can be null
	 */
	protected void fireZoomUpdate(double oldZoom, double newZoom, PicPoint ptClick){
		Object[] listeners = listenerList.getListenerList();
		ZoomEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new ZoomEvent(this,oldZoom,newZoom,ptClick);
			if (listeners[i]==ZoomListener.class) {
				((ZoomListener)listeners[i+1]).zoomUpdate(e);
			}
		}
	}

	/**
	 * Adds a <code>ZoomListener</code> to the Canvas.
	 */
	public void addZoomListener(ZoomListener l){
		listenerList.add(ZoomListener.class, l);
	}

	/**
	 * Removes a <code>ZoomListener</code> from the Canvas.
	 */
	public void removeZoomListener(ZoomListener l){
		listenerList.remove(ZoomListener.class, l);
	}

	/**
	 * utilities to retrieve the index of the given zoom in <code>PREDEFINED_ZOOMS</code> ; this may be used
	 * by GUI widgets, e.g. <code>JComboBox</code>,&hellip;
	 * @return index of the given zoom in array "<code>PREDEFINED_ZOOMS</code>" ; returns -1 if not found.
	 */
	public static int getZoomIndex(double zoom){
		for(int i=0; i<PREDEFINED_ZOOMS.length; i++){
			if (PREDEFINED_ZOOMS[i] == zoom) return i;
		}
		return -1; // not found
	}

	// new *************************** begin (by ss & bp)
	/////////////////////////////
	///// ROTATE
	/////////////////////////////

	/**
	 * Adds a <code>RotateListener</code> to the Canvas.
	 */
	public void addRotateListener(RotateListener l){
		listenerList.add(RotateListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * @param angle the current angle
	 */
	protected void fireRotateUpdate(double angle){
		Object[] listeners = listenerList.getListenerList();
		RotateEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (e==null) e = new RotateEvent(this,angle);
			if (listeners[i]==RotateListener.class) {
				((RotateListener)listeners[i+1]).rotateUpdate(e);
			}
		}
		repaint();
	}

	/**
	 * Sets the angle of the selected element.
	 */
	public void setRotateAngle(double angle){
		fireRotateUpdate(angle);
	}

	/**
	 * Toggle visibility of the angle-textfield.
	 */
	public void setRotateAngleLabelVisible(boolean b){
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==RotateListener.class) {
				((RotateListener)listeners[i+1]).setRotateAngleLabelVisible(b);
			}
		}
	}

	/////////////////////////////
	///// GridStep to KeyMover Setter
	/////////////////////////////

	/** Règle le pas horizontal et vertical par lequel les éléments sélectionnés sont déplacés par l'action
	 *	des touches « flèche vers le haut », « flèche vers le bas », « flèche vers la gauche », ou « flèche
	 *	vers la droite ».
	 */
	public void setMult(double mult) {
		this.mult = mult;
	}
	// new *************************** end (by ss & bp)

	/////////////////////////////
	///// UNDO/REDO
	/////////////////////////////

	/**
	 * Règle le nombre d'événement défaisable à mémoriser.
	 */
	public void setUndoLimit(int limit){
		this.undoManager.setLimit(limit);
	}

	/**
	 * Défait le dernier changement.
	 * @since PicEdt 1.1.3
	 */
	public void undo() throws CannotUndoException {
		unSelectAll();
		this.undoManager.undo();
	}

	/**
	 * Refait le dernier changement.
	 * @since PicEdt 1.1.3
	 */
	public void redo() throws CannotRedoException {
		unSelectAll();
		this.undoManager.redo();
	}

	/**
	 * Register an <code>UndoableEditListener</code> for the <code>Drawing</code> hosted by this canvas.
	 */
	public void addUndoableEditListener(UndoableEditListener l){
		this.undoableEditSupport.addUndoableEditListener(l);
	}

	/**
	 * Unregister an <code>UndoableEditListener</code> for the <code>Drawing</code> hosted by this canvas.
	 */
	public void removeUndoableEditListener(UndoableEditListener l){
		this.undoableEditSupport.removeUndoableEditListener(l);
	}

	/**
	 * Create a new <code>UndoableEdit</code> that holds the current state of the <code>Drawing</code>.
	 */
	public void beginUndoableUpdate(String presentationName){
		if (this.stateEdit != null) {
			endUndoableUpdate(); // if end() was not called !
		}
		this.stateEdit = new PEStateEdit(getDrawing(),presentationName);
	}

	/**
	 * Ends the current <code>UndoableEdit</code> and fire an event to registered listeners.
	 */
	public void endUndoableUpdate(){
		if (this.stateEdit==null) {
			return; // refuse two calls to end()
		}
		this.stateEdit.end();
		this.undoableEditSupport.postEdit(this.stateEdit); // notify undoableEditListener's of a new undoable-edit (this is the way UndoMgr gets informed...)
		this.stateEdit = null; // flag for begin() to know we've ended !
	}

	/**
	 * @return true if a "redo" operation would be successfull
	 */
	public boolean canRedo(){
		return this.undoManager.canRedo();
	}

	/**
	 * @return true if a "undo" operation would be successfull
	 */
	public boolean canUndo(){
		return this.undoManager.canUndo();
	}

	/**
	 * @return the presentation name of the next edit that can be redone
	 */
	public String getRedoPresentationName(){
		if (!canRedo()) return "";
		return this.undoManager.getRedoPresentationName();
	}

	/**
	 * @return the presentation name of the last edit that can be undone
	 */
	public String getUndoPresentationName(){
		if (!canUndo()) return "";
		return this.undoManager.getUndoPresentationName();
	}

	/** Overriden so as to display our own undo- and redo- presentation names */
	private class PEStateEdit extends StateEdit {

		PEStateEdit(StateEditable anObject, String name){
			super(anObject, name);
		}

		public String getUndoPresentationName() {
			return getPresentationName();
		}

		public String getRedoPresentationName() {
			return getPresentationName();
		}
	}

	//////////////////////////////////
	////// SELECTION MANAGEMENT
	//////////////////////////////////

	/**
	 * @return an <code>Iterator</code> over selected graphic elements.
	 */
	public Iterator<Element> selection(){ // [pending] change to  "getSelection"
		return getEditorKit().getSelectionHandler().iterator();
	}

	/**
	 * Return a collection of selected elements.
	 */
	public SelectionHandler getSelectionHandler(){
		return getEditorKit().getSelectionHandler();
	}

	/**
	 * @return the number of selected elements in the current selection
	 */
	public int getSelectionSize(){
		return getEditorKit().getSelectionHandler().size();
	}

	/**
	 * @return whether the given element is selected or not
	 */
	public boolean isSelected(Element e){
		return getEditorKit().getSelectionHandler().contains(e);
	}

	/**
	 * Select every object in this drawing.
	 */
	public void selectAll(){
		getEditorKit().getSelectionHandler().selectAll(drawing);
		fireSelectionUpdate(kit.getSelectionHandler().asArray(), SelectionEvent.EventType.SELECT);
	}

	/**
	 * Select the elements in the given collection (if they belong to the drawing).
	 */
	public void selectCollection(Collection<Element> c, SelectionBehavior beh){
		if (beh == SelectionBehavior.REPLACE) getEditorKit().getSelectionHandler().clear();
		ArrayList<Element> selected = new ArrayList<Element>();
		for (Element obj:c){
			if (isSelected(obj)) {
				if (DEBUG) debug("Already selected:elem="+obj);
			}
			else {
				getEditorKit().getSelectionHandler().add(obj);
				selected.add(obj);
			}
		}
		Element[] selectedArray = (Element[])selected.toArray(new Element[0]);
		fireSelectionUpdate(selectedArray, SelectionEvent.EventType.SELECT);
	}

	/**
	 * Select the given element.
	 * @param obj The <code>Element</code> to be selected.
	 * @param beh Whether to add <code>obj</code> to the selection, or to replace the selection by
	 * <code>obj</code>.
	 */
	public void select(Element obj, SelectionBehavior beh){
		switch (beh){
		case INCREMENTAL:
			if (isSelected(obj)) {
				if (DEBUG) debug("Already selected:elem="+obj);
				return; // already selected
			}
			getEditorKit().getSelectionHandler().add(obj);
			fireSelectionUpdate(obj, SelectionEvent.EventType.SELECT);
			break;
		case REPLACE:
			getEditorKit().getSelectionHandler().replace(obj);
			fireSelectionUpdate(obj, SelectionEvent.EventType.SELECT); // don't fire "unselect"
			break;
		default:
			requestFocusInWindow();
		}
	}

	/**
	 * Unselect the given graphic object. Does nothing if <code>obj</code> is not in the selection.
	 * @param obj the <code>Element</code> to be removed from selection.
	 */
	public void unSelect(Element obj){
		if (!isSelected(obj)) {
			if (DEBUG) debug("Already unselected:elem="+obj);
			return; // already unselected
		}
		getEditorKit().getSelectionHandler().remove(obj);
		fireSelectionUpdate(obj, SelectionEvent.EventType.UNSELECT);
	}

	/**
	 * Unselect the given convex zone.
	 */
	public void unSelect(ConvexZone cz){
		if (!isSelected(cz)) {
			if (DEBUG) debug("Already unselected:elem="+cz);
			return; // already unselected
		}
		getEditorKit().getConvexZoneSelectionHandler().remove(cz);
        // fireSelectionUpdate(obj, SelectionEvent.EventType.UNSELECT);
	}


	/**
	 * Unselect every object in this drawing.
	 */
	public void unSelectAll(){
		if (getEditorKit().getSelectionHandler().size()==0) return; // already empty
		Element[] deselected = getEditorKit().getSelectionHandler().asArray();
		getEditorKit().getSelectionHandler().clear();
		fireSelectionUpdate(deselected, SelectionEvent.EventType.UNSELECT);
	}

	/**
	 * Remove all selected objects from the drawing.
	 */
	public void deleteSelection(){
		getEditorKit().getSelectionHandler().delete(drawing);
	}


	/**
	 * Détruit la zone convexe <code>cz</code>.
	 *
	 * @param cz la <code>ConvexZone</code> zone convexe à détruire.
	 */
	public void deleteConvexZone(ConvexZone cz){
		getEditorKit().getConvexZoneSelectionHandler().remove(cz);
		getConvexZoneSet().remove(cz);
	}

	/** Détruit toutes les zones convexe de la selection de zones convexe.*/
	public void deleteConvexZoneSelection(){
		ConvexZoneSelectionHandler czSelection = getEditorKit().getConvexZoneSelectionHandler();
		for(ConvexZone cz : czSelection)
			getConvexZoneSet().remove(cz);
		czSelection.clear();
	}
	/**
	 * Ajout le contenu du presse-papier (<code>ClipBoard</code>) passé en argument au dessin courant, et le
	 * sélectionner ensuite. Si seulement <code>DataFlavor.stringFlavor</code> est fournit par le
	 * <code>Transferable</code>, alors on analyse la chaîne de caractère et on insère le résultat de
	 * l'analyse. Sinon, on considère comme garanti que le contenu du presse-papier (<code>ClipBoard</code>)
	 * prend en charge les données d'acception
	 * <code>jpicedt.graphic.toolkit.TransferableGraphic.JPICEDT_DATA_FLAVOR</code>.
	 * @param translate si non null, et qu'on colle vers la même planche, translate le contenu collé d'un pas
	 * de grille de sorte à ne pas cacher le contenu d'origine.
	 */
	public void paste(Clipboard clipbrd, PicPoint translate) throws ParserException, IOException, UnsupportedFlavorException {
		getEditorKit().getSelectionHandler().clear(); // don't fire selection event
		Transferable transferable = clipbrd.getContents(this); // according to doc. requestor is not used.
		if (transferable==null) return; // clipboard is empty !

		if(transferable instanceof TransferableGraphic){
			TransferableGraphic transferableGraphic =  (TransferableGraphic) transferable;
			// ne pas translater si on colle sur une autre planche.
			if(transferableGraphic.getSourceBoard() != null
			   && transferableGraphic.getSourceBoard() != this)
				translate = null;

			// juste au cas où on recollerait par dessus une seconde fois.
			transferableGraphic.setSourceBoard(this);
		}

		// first check if it's a local clipboard supporting JPICEDT_DATA_FLAVOR :
		if (transferable.isDataFlavorSupported(TransferableGraphic.JPICEDT_DATA_FLAVOR)){
			Element[] content = (Element[])transferable.getTransferData(TransferableGraphic.JPICEDT_DATA_FLAVOR);
			for (int i=0; i<content.length; i++){
				// translate clipboard so that new content doesn't hide old one
				// Warning : this translates the source !!!
				if (translate != null)
					content[i].translate(translate.getX(),translate.getY());
				// add (a copy of the) content to the current drawing
				Element element = content[i].clone();
				drawing.add(element);
				getEditorKit().getSelectionHandler().add(element);
			}
			fireSelectionUpdate(kit.getSelectionHandler().asArray(), SelectionEvent.EventType.SELECT);
		}

		// otherwise that may be the System Clipboard : only text is supported :
		else {
			StringReader reader = new StringReader(jpicedt.MiscUtilities.getClipboardStringContent(clipbrd));
			ExtractionParsing parser = jpicedt.MiscUtilities.createParser();
			PicPoint here = null;
			//if(e instanceof MouseEvent){
			//	MouseEvent me = (MouseEvent)e;
			//	here = new PicPoint();
			//	here.setCoordinates(me.getX(),me.getY());
			//	here = view2Model(here,here);
			//}
			insert(reader,parser,here);
		}

		// else does nothing
	}

	/**
	 * Add the content of the System's <code>ClipBoard</code> to the current drawing, then select it.  More
	 * specifically, we try to parse the string and insert the parsed content.
	 * @param translate if true, translate the pasted content by a grid step so that it doesn't hide old one
	 */
	public void paste(boolean translate) throws ParserException, IOException, UnsupportedFlavorException {
		PicPoint shift = null;
		if(translate)
			shift = new PicPoint(grid.getSnapStep(),-grid.getSnapStep());
		paste(Toolkit.getDefaultToolkit().getSystemClipboard(),shift);
	}

	/**
	 * Copy the content of the current selection (through a <code>TransferableGraphic</code>) to the System's
	 * clipboard (after a formatting to text), AND to the given clipboard if non-<code>null</code> (the latter
	 * can be a local clipboard supporting more data-flavors than the system clipboard)
	 * @param clipbrd the target clipboard ; can be null, in which case only the System clipboard
	 *        is modified.
	 */
	public void copy(Clipboard clipbrd) {
		// if selection buffer is empty, don't modify clipboard's content
		if (getSelectionSize() == 0) return;
		// create array of selected Elements for GraphicTransferable
		Element[] elements = new Element[getSelectionSize()];
		int counter = 0;
		for (Element e: getSelectionHandler()){
			elements[counter++] = e;
		}
		// create formatted string using "write" with selectionOnly = true :
		StringWriter writer = new StringWriter();
		try {
			write(writer,true);
			// create GraphicTransferable :
			TransferableGraphic transferable = new TransferableGraphic(elements,writer.toString(),this);
			writer.close();
			if (clipbrd!=null) clipbrd.setContents(transferable,transferable);
			Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection ss = new StringSelection(writer.toString());
			systemClipboard.setContents(ss,ss);
		} catch (IOException ioEx){ioEx.printStackTrace();}

	}

	/**
	 * Copy the content of the current selection to the
	 * System's clipboard (after a formatting to text)
	 */
	public void copy() {
		copy(null);
	}

	/**
	 * Cut the content of the current selection (through a <code>GraphicTransferable</code>) to the
	 * System clipboard, AND to the given <code>ClipBoard</code> if non-null.
	 * @param clipbrd the target clipboard ; can be null, in which case only the System clipboard
	 *        is modified.
	 */
	public void cut(Clipboard clipbrd) {
		// if selection buffer is empty, don't modify clipboard's content
		if (getSelectionSize() == 0) return;
		copy(clipbrd);
		deleteSelection();
	}

	/**
	 * Cut the content of the current selection to the System clipboard, after formatting to text.
	 */
	public void cut() {
		cut(null);
	}

	/**
	 * Group all selected objects into a new <code>PicGroup</code> and add it to the drawing.
	 * @since jPicEdt 1.2.a
	 */
	public void groupSelection(){
		PicGroup group = new PicGroup(getEditorKit().getSelectionHandler());
		getEditorKit().getSelectionHandler().delete(drawing); // delete old selected elements
		drawing.add(group);
		getEditorKit().getSelectionHandler().add(group); // select group
		fireSelectionUpdate(group, SelectionEvent.EventType.SELECT);
	}

	/**
	 * Join all selected objects of type <code>PicMultiCurveConvertable</code> into a new
	 * <code>PicMulticurve</code> and add it to the drawing.
	 * @since jPicEdt 1.4.pre5
	 */
	public void joinSelection(){
		// fetch PicMultiCurveConvertable's in selection: (note that they don't lose their parent in this process)
		ArrayList<PicMultiCurveConvertable> l = getEditorKit().getSelectionHandler().createFilteredCollection(PicMultiCurveConvertable.class);
		ArrayList<Element> lBackup = new ArrayList<Element>(l);
		if (l.size()<=1) return;
		// join along the shortest-distance path:
		PicMultiCurveConvertable c0source = l.get(0);
		PicMultiCurve c0 = c0source.convertToMultiCurve();
		l.remove(0);
		lBackup.remove(0); // this is the one to which other were joined
		if(c0 != c0source)
		{
			unSelect(c0source);
			drawing.replace(c0source,c0);
			select(c0,SelectionBehavior.INCREMENTAL);
		}
		while (l.isEmpty()==false){
			PicMultiCurveConvertable c1 = c0.fetchClosestCurve(l);
			c0.join(c1);
			l.remove(c1);
		}
		// unselect then delete old PicMultiCurveConvertable's from drawing:
		for (Element e: lBackup){
			getEditorKit().getSelectionHandler().remove(e);
			drawing.remove(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * @param element the Element that was (un)selected
	 * @param type the event type
	 */
	protected void fireSelectionUpdate(Element element, SelectionEvent.EventType type){
		Object[] listeners = listenerList.getListenerList();
		SelectionEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new SelectionEvent(this,element,type);
			if (listeners[i]==SelectionListener.class) {
				((SelectionListener)listeners[i+1]).selectionUpdate(e);
			}
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * @param elements the <code>Element</code>'s that were (un)selected
	 * @param type the event type
	 */
	protected void fireSelectionUpdate(Element[] elements, SelectionEvent.EventType type){
		Object[] listeners = listenerList.getListenerList();
		SelectionEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new SelectionEvent(this,elements,type);
			if (listeners[i]==SelectionListener.class) {
				((SelectionListener)listeners[i+1]).selectionUpdate(e);
			}
		}
	}

	/**
	 * Adds a <code>SelectionListener</code> to the Canvas.
	 */
	public void addSelectionListener(SelectionListener l){
		listenerList.add(SelectionListener.class, l);
	}

	/**
	 * Removes a <code>SelectionListener</code> from the Canvas.
	 */
	public void removeSelectionListener(SelectionListener l){
		listenerList.remove(SelectionListener.class, l);
	}


	//#######################################################################
	// GESTION DE LA SÉLECTION DE ZONES CONVEXES
	//#######################################################################
	public void select(ConvexZone cz, SelectionBehavior beh){
		switch (beh){
		case INCREMENTAL:
			if (isSelected(cz)) {
				if (DEBUG) debug("Already selected convex zone="+cz);
				return; // already selected
			}
			getEditorKit().getConvexZoneSelectionHandler().add(cz);
			fireSelectionUpdate(cz, ConvexZoneSelectionEvent.EventType.SELECT);
			break;
		case REPLACE:
			getEditorKit().getConvexZoneSelectionHandler().replace(cz);
			fireSelectionUpdate(cz, ConvexZoneSelectionEvent.EventType.SELECT); // don't fire "unselect"
			break;
		default:
		}
	}

	public void select(ConvexZoneGroup czg, SelectionBehavior beh){
		for(ConvexZone cz : czg)
			select(cz,beh);
	}

	/**
	 * @param cz une valeur <code>ConvexZone</code> dont on veut savoir si
	 * elle fait partie de la sélection de zones convexes.
	 * @return une valeur <code>boolean</code> vrai si <code>cz</code> fait
	 * fait partie de la sélection de zones convexes, faux sinon.
	 */
	public boolean isSelected(ConvexZone cz){
		return getEditorKit().getConvexZoneSelectionHandler().contains(cz);
	}

	public Iterator<ConvexZone> getConvexZoneSelection(){
		return getEditorKit().getConvexZoneSelectionHandler().iterator();
	}

	protected void fireSelectionUpdate(ConvexZone cz, ConvexZoneSelectionEvent.EventType type){
		Object[] listeners = listenerList.getListenerList();
		ConvexZoneSelectionEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new ConvexZoneSelectionEvent(this,cz,type);
			if (listeners[i]==ConvexZoneSelectionListener.class) {
				((ConvexZoneSelectionListener)listeners[i+1]).selectionUpdate(e);
			}
		}
	}


	////////////////////////////////////
	///// SCROLLABLE INTERFACE and rel.
	////////////////////////////////////

	/**
	 * @return the drawing board size (aka <code>ScrollPane</code>'s View size, as opposed to
	 * ViewPort's size)
	 */
	public Dimension getPreferredScrollableViewportSize() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension componentSize = getPageFormat().getSizePx(zoom);
		// return half screen size at maximum
		return(new Dimension((int)Math.min(componentSize.width,screenSize.width/2),
		                     (int)Math.min(componentSize.height,screenSize.height/2)));
	}

	/**
	 * @return A grid step.
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

		return (int)(grid.getSnapStep() * zoom);
	}

	/**
	 * @return the viewport size minus a grid step
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

		if (orientation == SwingConstants.HORIZONTAL)
			return (int)(visibleRect.width - grid.getSnapStep() * zoom);
		else
			return (int)(visibleRect.height - grid.getSnapStep() * zoom);
	}

	/**
	 * @return false for this implementation
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * @return false for this implementation
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}








	//////////////////////////
	//// MISC
	//////////////////////////


	/**
	 * Overriden from JComponent
	 * Signals that this component can receive focus (useful for handling keyevents)
	 */
	public boolean isRequestFocusEnabled(){
		return true;
	}





	//////////////////////////
	//// PEMOUSE LISTENER
	//////////////////////////

	/**
	 * Adds the specified mouse listener to receive mouse events from this component.
	 * If l is null, no exception is thrown and no action is performed.
	 * @param l the mouse listener.
	 */
	public synchronized void addPEMouseInputListener(PEMouseInputListener l) {
		if (l == null) {
			return;
		}
		mouseInputListener = PEEventMulticaster.add(mouseInputListener,l);
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	/**
	 * Removes the specified mouse listener so that it no longer
	 * receives mouse events from this component. This method performs
	 * no function, nor does it throw an exception, if the listener
	 * specified by the argument was not previously added to this component.
	 * If l is null, no exception is thrown and no action is performed.
	 *
	 * @param l the mouse listener.
	 */
	public synchronized void removePEMouseInputListener(PEMouseInputListener l) {
		if (l == null) {
			return;
		}
		mouseInputListener = PEEventMulticaster.remove(mouseInputListener, l);
	}

	/**
	 * Processes mouse events occurring on this component by
	 * dispatching them to any registered
	 * <code>PEMouseListener</code> objects.
	 * @param e the mouse event.
	 */
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e); // process "standard" mouse-events BEFORE (see pb with JPopupMenu for instance)
		if (mouseInputListener != null) {
			view2ModelTransform.transform(e.getPoint(),peMousePoint);
			PEMouseEvent me = new PEMouseEvent(e, this, peMousePoint);
			switch(e.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				mouseInputListener.mousePressed(me);
				break;
			case MouseEvent.MOUSE_RELEASED:
				mouseInputListener.mouseReleased(me);
				break;
			case MouseEvent.MOUSE_CLICKED:
				mouseInputListener.mouseClicked(me);
				break;
			case MouseEvent.MOUSE_EXITED:
				mouseInputListener.mouseExited(me);
				break;
			case MouseEvent.MOUSE_ENTERED:
				mouseInputListener.mouseEntered(me);
				break;
			}
		}

	}

	/**
	 * Processes mouse motion events occurring on this component by
	 * dispatching them to any registered
	 * <code>PEMouseInputListener</code> objects.
	 * @param e the mouse motion event.
	 */
	protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e); // process "standar" mouse-events
		if (mouseInputListener != null) {
			view2ModelTransform.transform(e.getPoint(),peMousePoint);
			PEMouseEvent me = new PEMouseEvent(e, this, peMousePoint);
			switch(e.getID()) {
			case MouseEvent.MOUSE_MOVED:
				mouseInputListener.mouseMoved(me);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				mouseInputListener.mouseDragged(me);
				break;
			}
		}

	}
	/**
	 * adds a <code>MouseMotionListener</code> to the canvas in order to show
	 * a tooltip on PicText elements
	 */
	public class IconizedTextTooltipDisplayListener implements MouseMotionListener{
		private PECanvas canvas;
		public IconizedTextTooltipDisplayListener(PECanvas canvas){
			super();
			this.canvas = canvas;
		}
			public void mouseDragged(MouseEvent e) {
			}
			public void mouseMoved(MouseEvent e) {
				PicPoint dst = new PicPoint();
				dst = view2Model(new PicPoint(e.getX(), e.getY()), dst);
				for (int i = 0; i < canvas.drawing.size(); i++) {
					if(canvas.drawing.get(i) instanceof PicText){
						if(((PicText)canvas.drawing.get(i)).getTextMode() == false) {
							Rectangle2D.Double rectangle = new Rectangle2D.Double(((PicText)canvas.drawing.get(i)).getFrameCenterX()-2, ((PicText)canvas.drawing.get(i)).getFrameCenterY()-3, 4, 5);
							if(rectangle.contains(dst.getX(), dst.getY())) {
              							String text = ((PicText)canvas.drawing.get(i)).getText();
              							if (!(text.equals(canvas.getToolTipText()))) {
            	  							canvas.setToolTipText(text);
              							}
              							return;
            						}
						}
					}
				}
				canvas.setToolTipText(null);
			}
    	};
} // class PECanvas
