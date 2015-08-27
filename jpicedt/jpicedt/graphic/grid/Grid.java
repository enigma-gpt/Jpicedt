// Grid.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: Grid.java,v 1.13 2013/03/27 07:05:32 vincentb1 Exp $
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
package jpicedt.graphic.grid;

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;

import static jpicedt.Log.*;

import static java.lang.Math.floor;
import static java.lang.Math.round;


/**
 * A grid for PECanvas, defined in model-coordinate (natural unit = 1mm)<br>
 * <ul>
 * <li> Provides the ability to find a nearest-neighbour point on the grid, given a point in model-coordinate,
 *      using current snap-step value</li>
 * <li> Offers several paint schemes (grid step)</li>
 * </ul>
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.0 ; completely refactored jpicedt 1.3.2
 */
public class Grid {

	public static final String[] PREDEFINED_SNAP_STEP_STRINGS = {"0.625","1","1.25","2","2.5","5","10"};
	public static final double[] PREDEFINED_SNAP_STEPS = { 0.625, 1.0, 1.25, 2, 2.5, 5.0, 10.0};
	public static final String[] PREDEFINED_GRID_STEP_STRINGS = {"1","1.25","2","2.5","5","10","20"};
	public static final double[] PREDEFINED_GRID_STEPS = { 1.0, 1.25, 2, 2.5, 5.0, 10.0,20.0};

	//////////////// VISIBLE STATE ////////////////////////
	/** key used to fetch the visible state from the Properties object */
	public static final String KEY_VISIBLE = "grid.visible";

	/** is grid visible ? */
	private boolean isVisible;

	/** default for visible state */
	public static final	 boolean isVisibleDEFAULT=true;

	//////////////// GRID STEP ////////////////////////
	/** key used to fetch the grid step from the Properties object */
	public static final String KEY_GRID_STEP = "grid.display-step";

	/** distance b/w to consecutive grid lines */
	private double gridStep; // used by "paint".

	public static final double gridStepDEFAULT = 10; // 10 mm

	//////////////// COLOUR ////////////////////////
	/** key used to fetch the grid colour from the Properties object */
	public static final String KEY_GRID_COLOR = "grid.color";

	/** colour used to paint grid lines */
    private Color gridColor;

	/** default colour used to paint grid lines */
	public static final Color colorDEFAULT=Color.lightGray;

	//////////////// LINE STYLE ////////////////////////
	/** key used to fetch the line style from the Properties object */
	public static final String KEY_LINE_STYLE = "grid.line-style";

	/** paint grid lines using solid lines */
	public static final String SOLID = "solid";

	/** paint grid lines using dashed lines */
	public static final String DASH = "dash";

	/** style used to paint grid lines */
	private String gridLineStyle;

	/** default style used to paint grid lines */
	public static final String lineStyleDEFAULT = SOLID; // see algo GridZoomCustomizer

	public static final String[] PREDEFINED_STYLES = {SOLID, DASH};

	//////////////// SNAP ON ////////////////////////
	/** key used to fetch the snap state from the Properties object */
	public static final String KEY_SNAP_ON = "grid.snap-on";

	/** is snapping active ? */
	private boolean snapOn;

	/** is snapping active ? */
	public static final boolean snapOnDEFAULT = true;

	//////////////// SNAP STEP ////////////////////////
	/** key used to fetch the snap step from the Properties object */
	public static final String KEY_SNAP_STEP = "grid.snap-step";

	/** current snap step */
	private double snapStep;

	/** default snap step */
	public static final double snapStepDEFAULT = 5.0;


	//////////////// CACHE ////////////////////////
	/** cache used by the "paint" method */
	private Line2D.Double line = new Line2D.Double();

	/** cache for Graphics2D scale factor (used to compute BasicStroke, which is updated each time the zoom changes)
	    this includes the zoom factor and the DPMM factor */
	private double scale = 0.0;

	/** default stroke for grid lines */
	private static final float gridLineWidth = 1.0f;
	private BasicStroke lineStroke = new BasicStroke(gridLineWidth);
	private BasicStroke axisLineStroke = new BasicStroke(2.0f * gridLineWidth); // used for axis @ (0,0)

	/** draw crosses at grid points in gridStyleDash */
	private float[] dashArray = {1.0f, 9.0f}; // dash (1mm black then 4mm white) when gridStep = 10mm
	private float dashPhase = 0.5f; // dash phase => dash is shifted by 0.5mm





	/**
	 * Construct a new Grid with default values
	 */
	public Grid(){
		setVisible(isVisibleDEFAULT);
		setSnapOn(snapOnDEFAULT);
		setSnapStep(snapStepDEFAULT);
		setGridStep(gridStepDEFAULT);
		setColor(colorDEFAULT);
		setLineStyle(lineStyleDEFAULT);
	}


	/**
	 * Construct a new Grid
	 * @param isVisible whether the grid has to be displayed or not
	 * @param isSnapOn whether the snap behaviour is active or not
	 * @param snapStep step used to compute the nearest-neighbour of a click point
	 * @param gridStep distance b/w grid lines in mm
	 * @param gridColor the grid colour
	 * @param lineStyle SOLID or DASH (tells what style to apply to grid lines)
	 */
	public Grid(boolean isVisible, boolean isSnapOn, double snapStep, double gridStep, Color gridColor, String lineStyle){
		setVisible(isVisible);
		setSnapOn(isSnapOn);
		setSnapStep(snapStep);
		setGridStep(gridStep);
		setColor(gridColor);
		setLineStyle(lineStyle);
	}

	/**
	 * Construct a new Grid from the given Properties, using the following key/value pairs :
	 * <ul>
	 * <li> key = <code>KEY_VISIBLE</code>, value = true/false</li>
	 * <li> key = <code>KEY_SNAP_ON</code>, value = true/false</li>
	 * <li> key = <code>KEY_SNAP_STEP</code>, value = a double</li>
	 * <li> key = <code>KEY_GRID_COLOR</code>, value = integer (RGB) representation of the colour</li>
	 * <li> key = <code>KEY_LINE_STYLE</code>, value = one of the predefined string (SOLID or DASH)</li>
	 * </ul>
	 * @param preferences Property holding the following information:
	 * <ul>
	 * <li> <code>KEY_VISIBLE</code> whether the grid has to be displayed or not</li>
	 * <li> <code>KEY_SNAP_ON</code> whether the snap behaviour is active or not</li>
	 * <li> <code>KEY_SNAP_STEP</code> step used to compute the nearest-neighbour of a click point</li>
	 * <li> <code>KEY_GRID_COLOR</code> the grid colour</li>
	 * <li> <code>KEY_LINE_STYLE</code> SOLID or DASH (tells what style to apply to grid lines)</li>
	 * </ul>
	 */
	public Grid(Properties preferences){
		setVisible(Boolean.valueOf(preferences.getProperty(KEY_VISIBLE,new Boolean(isVisibleDEFAULT).toString())).booleanValue());
		setSnapOn(Boolean.valueOf(preferences.getProperty(KEY_SNAP_ON,new Boolean(snapOnDEFAULT).toString())).booleanValue());
		setSnapStep(Double.parseDouble(preferences.getProperty(KEY_SNAP_STEP,Double.toString(snapStepDEFAULT))));
		setGridStep(Double.parseDouble(preferences.getProperty(KEY_GRID_STEP,Double.toString(gridStepDEFAULT))));
		setColor(new Color(Integer.parseInt(preferences.getProperty(KEY_GRID_COLOR,new Integer(colorDEFAULT.getRGB()).toString()))));
		setLineStyle(preferences.getProperty(KEY_LINE_STYLE,lineStyleDEFAULT));
	}

	/**
	 * set the colour used to paint the grid
	 */
	public void setColor(Color gridColor){
		this.gridColor = gridColor;
	}

	/**
	* @return the colour used to paint the grid
	 */
	public Color getColor(){
		return gridColor;
	}

	/**
	 * set the style used to paint grid lines
	 * @param lineStyle SOLID or DASH
	 */
	public void setLineStyle(String lineStyle){
		if (SOLID.equals(lineStyle)) this.gridLineStyle = SOLID;
		else if (DASH.equals(lineStyle)) this.gridLineStyle = DASH;
		else this.gridLineStyle = this.lineStyleDEFAULT;
	}

	/**
	* @return the style used to paint grid lines
	 */
	public String getLineStyle(){
		return gridLineStyle;
	}

	/**
	 * set whether this grid is visible or not
	 */
	public void setVisible(boolean state){
		isVisible = state;
	}

	/**
	* @return whether this grid is visible or not
	 */
	public boolean isVisible(){
		return isVisible;
	}

	/**
	 * sets whether snap is active or not
	 */
	public void setSnapOn(boolean state){
		snapOn = state;
	}

	/**
	 * @return whether snap is active or not
	 */
	public boolean isSnapOn(){
		return snapOn;
	}

	/**
	 * @return the snap step
	 */
	public double getSnapStep(){
		return snapStep;
	}

	/**
	 * sets the snap-step to the given value
	 */
	public void setSnapStep(double snapStep){

		if (snapStep <= 0) throw new IllegalArgumentException(Double.toString(snapStep));
		this.snapStep = snapStep;
	}

	/**
	 * @return the grid step
	 */
	public double getGridStep(){
		return gridStep;
	}

	/**
	 * sets the grid (display)-step to the given value
	 */
	public void setGridStep(double gridStep){

		if (gridStep <= 0) throw new IllegalArgumentException(Double.toString(gridStep));
		this.gridStep = gridStep;
		dashArray[0] = (float)(0.1*gridStep); // black
		dashArray[1] = (float)(0.9 * gridStep); // white dash (1mm black then 4mm white) when gridStep = 10mm
		dashPhase = dashArray[0]*0.5f; // dash phase => dash is shifted by 0.5mm
		syncStroke(this.scale);
	}

	/**
	 * paint this grid using a grid-step of "gridstep" mm to compute line-spacing.<br>
	 * The graphic context is in user-coordinate, i.e. in model-coordinates (left to right, bottom to top, mm)
	 * @param scale the current scale factor, used to scale down line thickness so that lines are displayed
	 *        with a constant thickness, whatever AffineTransform is currently set to the graphic context.
	 */
	public void paint(Graphics2D g, Rectangle2D clip, double scale){

		if(!isVisible) return;
		//if (DEBUG) debug( "paint:clip2d="+clip);

		// possibly update zoom and lineStroke if change occured
		if (scale != this.scale){
			this.scale = scale;
			syncStroke(scale); // synchronize stroke parameters with new zoom
		}

		g.setPaint(gridColor);

		/** draw vertical lines */

		// compute the X-coord of the leftmost vertical line we'll've to repaint
		line.x2 = line.x1 = (floor(clip.getMinX() / gridStep)) * gridStep; // e.g. clip.x=17.2 => ptx=10.0
		if (gridLineStyle == SOLID) line.y1 = clip.getMinY(); // draw until the left border
		else line.y1 = (floor(clip.getMinY() / gridStep)) * gridStep;
		line.y2 = clip.getMaxY();

		while(line.x1 <= clip.getMaxX()){
			if (line.x1==0)	g.setStroke(axisLineStroke);
			else g.setStroke(lineStroke);

			g.draw(line);
			if (DEBUG) {
				Font font = new Font("SansSerif", Font.PLAIN, 6).deriveFont(AffineTransform.getScaleInstance(0.5,-0.5));
				FontRenderContext frc = g.getFontRenderContext();
				TextLayout tl = new TextLayout(Double.toString(line.x1), font, frc);
				g.setPaint(Color.black);
				tl.draw(g,(float)line.x1,0.0f);
				g.setPaint(gridColor);
			}
			line.x1 += gridStep;
			line.x2 = line.x1;
		}

		/** draw horizontal lines */

		// compute Y-coord of the bottom-most horizonal line we'll've to repaint
		line.y1 = line.y2 = (floor(clip.getMinY() / gridStep)) * gridStep; // e.g. clip.y=-13.0 => pty = -20.0
		if (gridLineStyle == SOLID) line.x1 = clip.getMinX();
		else line.x1 = (floor(clip.getMinX() / gridStep)) * gridStep;
		line.x2 = clip.getMaxX();
		while(line.y1 <= clip.getMaxY()+gridStep){
			if (line.y1==0)	g.setStroke(axisLineStroke);
			else g.setStroke(lineStroke);
			g.draw(line);
			if (DEBUG) {
				Font font = new Font("SansSerif", Font.PLAIN, 6).deriveFont(AffineTransform.getScaleInstance(0.5,-0.5));
				FontRenderContext frc = g.getFontRenderContext();
				TextLayout tl = new TextLayout(Double.toString(line.y1), font, frc);
				g.setPaint(Color.black);
				tl.draw(g,0.0f,(float)line.y1);
				g.setPaint(gridColor);
			}
			line.y1 += gridStep;
			line.y2 = line.y1;
		}
	}

	/**
	 * synchronize current strokes with AffineTransform's scale factor when zoom's changed
	 * @param scale the current scale factor of the AffineTransform attached to the graphic context
	 */
	private void syncStroke(double scale){

		// assume getScaleX == getScaleY !!!
		if (gridLineStyle == SOLID){
			lineStroke = new BasicStroke((float)(gridLineWidth/scale));
			axisLineStroke = new BasicStroke(2.0f*gridLineWidth/(float)scale);
		}
		else {
			lineStroke = new BasicStroke((float)(gridLineWidth/scale), // stroke width
	                 	           	BasicStroke.CAP_SQUARE, // decoration of the ends of the stroke
	                    			BasicStroke.JOIN_ROUND, // decoration applied where path segments meet
	                            	10.0f, // the limit to trim the miter join
	                            	dashArray, // dash (black then white)
	                            	dashPhase); // dash phase
			axisLineStroke = new BasicStroke(1.0f*gridLineWidth/(float)scale);
		}
	}

	/**
	 * Compute the nearest-neighbour of the given srcPt point on this grid, using the current snap-step value,
	 * and store the result in dstPt. <br>
	 * If snap is turned off, simply returns srcPt.<br>
	 * If dstPt is null, a new PicPoint is allocated and the result is stored in this new point.<br>
	 * If srcPt and dstPt are the same objects, the input point is correctly overwritten with the result.
	 * @param srcPt the source point in model-coordinates.
	 * @param dstPt a point that stores the result
	 * @return dstPtDst for convenience
	 */
	public PicPoint nearestNeighbour(PicPoint srcPt, PicPoint dstPt){

		if (dstPt == null) dstPt = new PicPoint();
		if (snapOn) {
			dstPt.x = round(srcPt.x/snapStep)*snapStep;
			dstPt.y = round(srcPt.y/snapStep)*snapStep;
		}
		else {
			dstPt.x = srcPt.x;
			dstPt.y = srcPt.y;
		}
		return dstPt;
	}


	/**
	 * Utilities to retrieve an index from a given snapStep in PREDEFINED_SNAP_STEPS
	 * @return index of the given snap step in array "PREDEFINED_SNAP_STEPS"
	 * returns -1 if not found.
	 */
	public static int getSnapStepIndex(double snapStep){
		for(int i=0; i<PREDEFINED_SNAP_STEPS.length; i++){
			if (PREDEFINED_SNAP_STEPS[i] == snapStep) return i;
		}
		return -1; // not found
	}

	/**
	 * Utilities to retrieve an index from a given gridStep in PREDEFINED_SNAP_STEPS
	 * @return index of the given grid step in array "PREDEFINED_GRID_STEPS"
	 * returns -1 if not found.
	 */
	public static int getGridStepIndex(double gridStep){
		for(int i=0; i<PREDEFINED_GRID_STEPS.length; i++){
			if (PREDEFINED_GRID_STEPS[i] == gridStep) return i;
		}
		return -1; // not found
	}
}
