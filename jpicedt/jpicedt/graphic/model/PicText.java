// PicText.java ---  -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicText.java,v 1.38 2013/03/27 07:01:13 vincentb1 Exp $
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
package jpicedt.graphic.model;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.*;

import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.widgets.*;

import static java.lang.Math.sqrt;


/**
 * Textual elements, possibly surrounded by a box (currently, only rectangular, oval and circular shapes
 * are supported). The box size is computed from the text size (ie aka LaTeX's \\framebox command with
 * no width/height argument).
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 * @version $Id: PicText.java,v 1.38 2013/03/27 07:01:13 vincentb1 Exp $
 */

public class PicText extends TextEditable implements CustomizerFactory, NodeableElement {  // exemple : \\put(15,20){\\makebox(0,0)[bl]{texte}}

	/**
	 * An enum for allowed values for horizontal text alignment
	 */
	public static enum HorAlign {
	        LEFT("left"),
	        CENTER("center-h"),
	        RIGHT("right");

	        private String value;

	        HorAlign(String value){
		        this.value=value;
	        }
	        public String toString(){
		        return value;
	        }
	}

	/**
	 * An enum for allowed values for vertical text alignment
	 */
	public static enum VertAlign {
	        BOTTOM("bottom"),
	        BASELINE("baseline"), // for PsTricks only
	        CENTER("center-v"),
	        TOP("top");

	        private String value;

	        VertAlign(String value){
		        this.value=value;
	        }
	        public String toString(){
		        return value;
	        }
	}

	/**
	 * An enum for allowed values for frame-box styles
	 */
	public static enum FrameStyle {
	        NO_FRAME("noframe"),
	        RECTANGLE("rectangle"),
	        CIRCLE("circle"),
	        OVAL("oval");

	        private String value;

	        FrameStyle(String value){
		        this.value=value;
	        }
	        public String toString(){
		        return value;
	        }
	}

	/**
	 * 	An enum for the different text-modes
	 */
	public static enum TextMode {
			TEXT_FIELD("textfield"),
			TEXT_AREA("textarea");

			private String value;

			TextMode(String value){
				this.value=value;
			}
			public String toString(){
				return value;
			}
	}

	/**
	 * An enum for the text display options
	 * <dl>
	 *<dt><code>TEXT_MODE</code></dt><dd>In this mode the value of text is
	 *   used for display.</dd>
	 *<dt><code>ICON_MODE</code></dt><dd>In this mode an icon, when available
	 *   is used for display.</dd>
	 * </dl>
	 */
	public static enum TextIcon {
			TEXT_MODE("textmode"),
			ICON_MODE("iconmode");

			private String value;

			TextIcon(String value){
				this.value=value;
			}
			public String toString(){
				return value;
			}
	}

	////////////////////////////
	//// PUBLIC CONSTANT FIELDS
	////////////////////////////


	//////////////////////////
	//// PRIVATE FIELDS
	//////////////////////////


	/** dimensions (in latex sense) of the latex box (this does not include frame) */
	protected double width; // width of the box
	protected double height; // the height above the baseline
	protected double depth; // the depth below the baseline (depth+height=total height of the box)

	// new *************************** begin (by ss & bp)
	protected boolean areaSet = false;
	// new *************************** end (by ss & bp)

	protected Shape frame; //the frame surrounding the text


	// dimensions (in latex sense) of the frame
	private double frameWidth, frameHeight, frameDepth;  //  height= what's above the baseline
	//depth = what's below the baseline (depth+height=total height of the box)
	private double xOffset,yOffset; // coordinates of the vector BottomLeftOfText - BottomLeftOfFrame

	// TEMPORARY/ EXPERIMENTAL
	protected double frameSep=1; //to be set as an attribute. (contols amount of white space between text and frame).
	// exported in pstricks as framesep parameter
	protected double nodeSep=1; //to be set as an attribute. (contols amount of white space between frame and node-connections).
	//exported as pstricks nodesep parameter

	protected TextNodeConnectionManager nodeConnectionManager;
	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////


	/**
	 * Create a new PicText object with the whole bunch of parameters !
	 *
	 * @param ptA Anchor point (see setHorAlign() and setVertAlign() for details)
	 * @param textString the text content
	 */
	public PicText(PicPoint ptA, String textString, PicAttributeSet set) {
		super(textString,ptA,set);
		width=0; height=0; depth=0;
		updateFrame();
		// [underway] nodeConnectionManager = new TextNodeConnectionManager();
	}

	/**
	 * Creates a PicText object with the following default values :<br>
	 * <ul>
	 * <li> empty string,
	 * <li> frame and alignment according to the attribute set
	 * </ul>
	 */
	public PicText(PicPoint ptA, PicAttributeSet set) {
		super("",ptA,set);
		width=0; height=0; depth=0;
		updateFrame();
		// [underway] nodeConnectionManager = new TextNodeConnectionManager();
	}

	/**
	 * Creates a PicText object with the following default values :
	 * <ul>
	 * <li> empty string,
	 * <li> frame and alignment according to the attribute set
	 * </ul>
	 * and anchor point at (0,0)
	 */
	public PicText(PicAttributeSet set) {
		this(new PicPoint(), set);
		updateFrame();
	}

	/**
	 * Creates a PicText object with a default attribute set, and the following default values :
	 * <ul>
	 * <li> empty string,
	 * <li> no frame,
	 * <li> bottom-left alignment.
	 * </ul>
	 * and anchor point at (0,0)
	 */
	public PicText() {
		super("", new PicPoint());
		updateFrame();
		// [underway] nodeConnectionManager = new TextNodeConnectionManager();
	}

	/**
	 * cloning constructor
	 */
	public PicText(PicText text){
		super(text);
		this.width=text.width; this.height=text.height; this.depth=text.depth;
		this.frame=text.frame;
		this.frameWidth=text.frameWidth; this.frameHeight=text.frameHeight; this.frameDepth=text.frameDepth;
		// [pending] nodeConnectionManager = text.getConnectionManager().clone();
	}

	/**
	 * Override Object.clone() method
	 */
	public PicText clone(){
		return new PicText(this);
	}

	/**
	 * Return a string that represents this object's name
	 */
	public String getDefaultName(){
		return jpicedt.Localizer.currentLocalizer().get("model.Text");
	}


	//////////////////////////////////////////////////////////
	/// BOUNDING BOX
	//////////////////////////////////////////////////////////

	/**
	 * Returns the bounding box (i.e. the surrounding rectangle) in double precision
	 * Used e.g. to determine the arguments of a \\begin{picture} command.<p>
	 * This implementation compute the bb from the smallest rectangle that encompasses
	 * all specification-points.
	 * @since PicEdt 1.0
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r) {
		if (r==null)
			r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(ptAnchor, ptAnchor);
		return r;
	}

	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////

	public void setCtrlPt(int index, PicPoint pt, EditPointConstraint constraint){
		if (index != P_ANCHOR)
			throw new IndexOutOfBoundsException(new Integer(index).toString());
		ptAnchor.setCoordinates(pt);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	// rest is inherited

	/**
	 * Overriden in order to support forwarding to node connections.
	 * @since jpicedt 1.4pre5
	 */
	protected void fireChangedUpdate(DrawingEvent.EventType eventType) {
		super.fireChangedUpdate(eventType);
		//System.out.println("######leaf element changeupdate. connections="+nodeConnections);//debug
		if (nodeConnectionManager != null)
			nodeConnectionManager.fireChangedUpdate(eventType);
	}

	/////////////////////////////
	//// TRANSFORMS
	/////////////////////////////
	/**
	 * Translate this Element by (dx,dy) ; this implementation translates the specification-points,
	 * then fires a changed-update event.
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since PicEdt 1.0
	 */
	public void translate(double dx, double dy) {
		ptAnchor.translate(dx,dy);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Scale this object by <code>(sx,sy)</code> using <code>(ptOrgX,ptOrgY)</code> as the origin. This
	 * implementation simply apply a scaling transform to all specification-points.  Note that <code>sx</code>
	 * and <code>sy</code> may be negative.  This method eventually fires a changed-update event.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy, UserConfirmationCache ucc) {
		ptAnchor.scale(ptOrgX,ptOrgY,sx,sy);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle) {
		ptAnchor.rotate(ptOrg,angle);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Effectue une réflexion sur <code>this</code> relativement à l'axe
	 * défini par <code>ptOrg</code> et <code>normalVector</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de réflexion.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflexion.
	 */
	public void mirror(PicPoint ptOrg, PicVector normalVector){
		ptAnchor.mirror(ptOrg,normalVector);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Shear this Element by the given params wrt to the given origin
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc) {
		ptAnchor.shear(ptOrg, shx, shy);
		updateFrame();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	// new *************************** begin (by ss & bp)
	////////////////////////////
	//// Test functions for XML
	////////////////////////////

	public void setTextMode(TextMode textMode){
		if (textMode==null) return;
		attributeSet.setAttribute(PicAttributeName.TEXT_MODE, textMode);
	}

	public void setTextIcon(TextIcon textIcon){
		if (textIcon==null) return;
		attributeSet.setAttribute(PicAttributeName.TEXT_ICON, textIcon);
	}
	// new *************************** end (by ss & bp)

	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////

	/**
	 * Return horizontal alignment (see constant fields above)
	 */
	public HorAlign getHorAlign(){
		return attributeSet.getAttribute(PicAttributeName.TEXT_HOR_ALIGN);
	}

	/**
	 * Set the horizontal alignment (see constant fields above)
	 */
	public void setHorAlign(HorAlign horAlign){
		if (horAlign==null) return;
		attributeSet.setAttribute(PicAttributeName.TEXT_HOR_ALIGN, horAlign);
		fireChangedUpdate(DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}

	/**
	 * Return vertical alignement (see static constant above)
	 */
	public VertAlign getVertAlign(){
		return attributeSet.getAttribute(PicAttributeName.TEXT_VERT_ALIGN);
	}

	/**
	 * set the vertical alignement (see constant fields above)
	 */
	public void setVertAlign(VertAlign vertAlign){
		if (vertAlign == null) return;
		attributeSet.setAttribute(PicAttributeName.TEXT_VERT_ALIGN, vertAlign);
		fireChangedUpdate(DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}

	///////////////////////////////////////////////
	// Frame related methods
	//////////////////////////////////////////////

	/**
	 * Return TRUE if the box has to be drawn
	 */
	public boolean isFramed(){
		return (getFrameType()!= null && !getFrameType().equals(FrameStyle.NO_FRAME));
	}

	/**
	 * Set the type of frame to be put around the text.
	 */
	public void setFrameType(FrameStyle frameType){
		if (frameType==null) return;
		attributeSet.setAttribute(PicAttributeName.TEXT_FRAME, frameType);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE); // may change bounds if NO_FRAME -> RECT BOX
	}

	/**
	 * Set the frame type
	 */
	public FrameStyle getFrameType(){
		return attributeSet.getAttribute(PicAttributeName.TEXT_FRAME);
	}



	/**
	 * Returns the frame that surrounds the text, ignoring rotation (the view will rotate it)
	 */
	public Shape getShapeOfFrame(){ // refactor: getFrameShape()
		return frame;
	}


	/**
	 * First computes frame dimensions (frameWidth, etc).
	 * Then computes the frame that surrounds the text, ignoring rotation (the view will rotate it)
	 */
	public void updateFrame(){
		//[pending] frameSep should be a parameter
		if (! isFramed()){
			frameWidth=width; frameDepth=depth; frameHeight=height;
			xOffset=0; yOffset=0;
			return ;
		}
		double sep=frameSep+0.5*getAttribute(PicAttributeName.LINE_WIDTH);
		switch(getFrameType()){
		case RECTANGLE:
			frameWidth=width+2*sep;
			frameHeight=height+sep;
			frameDepth=depth+sep;
			xOffset=sep; yOffset=sep;
			if (!(frame instanceof Rectangle2D.Double)) frame = new Rectangle2D.Double();
			((Rectangle2D)frame).setFrame(getFrameLeftX(),getFrameBottomY(),frameWidth,frameHeight+frameDepth);
			return ;
		case OVAL://smallest area ellipse containing the box
			// solution pour une ellipse donnee par x^2/a^2+y^2/b^2=1 passant par un point donne (x,y) : x^2/a^2=y^2/b^2=1/2  => a=sqrt(2)*x;
			frameWidth=sqrt(2)*width+2*sep;
			xOffset=0.5*(frameWidth-width);
			yOffset=sqrt(0.5)*(height+depth)+sep-0.5*(height+depth);
			frameHeight= height +yOffset ;
			frameDepth= depth + yOffset ;
			if (!(frame instanceof Ellipse2D.Double)) frame = new Ellipse2D.Double();
			((Ellipse2D)frame).setFrame(getFrameLeftX(),getFrameBottomY(),frameWidth,frameHeight+frameDepth);
			return ;
		case CIRCLE:
			if (! (frame instanceof Ellipse2D.Double))
				frame = new Ellipse2D.Double();
			//Next formula means: increase both dimensions of the rectangle by 2*sep , and compute the distance from center to corner
			double radius=0.5*sqrt(width*width+(height+depth)*(height+depth))+sep;
			xOffset=radius-0.5*width;
			frameWidth=2*radius;
			yOffset=radius-0.5*(height+depth);
			frameHeight= height +yOffset ;
			frameDepth= depth + yOffset ;
			((Ellipse2D)frame).setFrame(getFrameLeftX(),getFrameBottomY(),frameWidth,frameHeight+frameDepth);
			return ;
		}
		return ; // should not occur
	}

	/////////////////////////////////////
	// text-mode
	////////////////////////////////////

	/**
	 * Return the textmode only
	 */
	public boolean getTextMode(){
		PicText.TextIcon mode = attributeSet.getAttribute(PicAttributeName.TEXT_ICON);
		return mode == null || mode == PicText.TextIcon.TEXT_MODE;
	}

	/**
	 * Set the textmode and updates the text
	 */
	public void setTextMode(boolean mode){
		if(mode)
			attributeSet.setAttribute(PicAttributeName.TEXT_ICON, PicText.TextIcon.TEXT_MODE);
		else
			attributeSet.setAttribute(PicAttributeName.TEXT_ICON, PicText.TextIcon.ICON_MODE);

		fireChangedUpdate(jpicedt.graphic.event.DrawingEvent.EventType.TEXT_CHANGE);
	}

	/////////////////////////////////////
	// text area
	////////////////////////////////////

	public void setAreaMode(boolean b){
		if(b)
			attributeSet.setAttribute(PicAttributeName.TEXT_MODE, PicText.TextMode.TEXT_FIELD);
		else
			attributeSet.setAttribute(PicAttributeName.TEXT_MODE, PicText.TextMode.TEXT_AREA);
	}

	public boolean getAreaSet(){
		return this.areaSet;
	}

	public boolean getAreaMode(){
		PicText.TextMode mode = attributeSet.getAttribute(PicAttributeName.TEXT_MODE);
		return mode == null || mode == PicText.TextMode.TEXT_FIELD;
	}


	/////////////////////////////////////
	// text dimensions
	////////////////////////////////////


	/**
	 * Return the width of text only
	 */
	public double getWidth(){
		return width;
	}

	/**
	 * Return the height of text only
	 */
	public double getHeight(){
		return height;
	}

	/**
	 * Return the depth of text only
	 */
	public double getDepth(){
		return depth;
	}

	/**
	 * Return x-coordinate of left boundary of text only
	 */
	public double getLeftX(){
		return getFrameLeftX()+xOffset;
	}


	/**
	 * Return x-coordinate of right boundary of text only
	 */

	public double getRightX(){
		return getLeftX()+width;
	}

	/**
	 * Return x-coordinate of center of text only
	 */

	public double getCenterX(){
		return getLeftX()+width/2;
	}




	/**
	 * Return y-coordinate of bottom boundary of text only
	 */

	public double getBottomY(){
		return getFrameBottomY()+yOffset;
	}


	/**
	 * Return y-coordinate of top boundary of text only
	 */

	public double getTopY(){
		return getBottomY()+depth+height;
	}

	/**
	 * Return y-coordinate of baseline
	 */

	public double getBaseLineY(){
		return getBottomY()+depth;
	}


	/**
	 * Return y-coordinate of center of text only
	 */

	public double getCenterY(){
		return getBottomY()+(height+depth)/2;
	}




	/////////////////////////////////////
	// Frame dimensions
	/////////////////////////////////////


	/**
	 * Return the width of the text including frame
	 */
	public double getFrameWidth(){
		return frameWidth;
	}

	/**
	 * Return the height of the text including frame
	 */
	public double getFrameHeight(){
		return frameHeight;
	}

	/**
	 * Return the depth of the text including frame
	 */
	public double getFrameDepth(){
		return frameDepth;
	}



	/**
	 * Return x-coordinate of left boundary, including frame
	 */
	public double getFrameLeftX(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		PicPoint pt = new PicPoint();
		switch (getHorAlign()){
		case LEFT:
			return ptAnchor.x;
		case RIGHT:
			return ptAnchor.x - frameWidth ;
		case CENTER: //centered
			return ptAnchor.x - 0.5*frameWidth;
		default:
			return 0;
		}
	}


	/**
	 * Return x-coordinate of right boundary
	 */
	public double getFrameRightX(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		return getFrameLeftX()+frameWidth;
	}

	/**
	 * Return x-coordinate of center
	 */

	public double getFrameCenterX(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		return getFrameLeftX()+0.5*frameWidth;
	}


	/**
	 * Return y-coordinate of top boundary
	 */

	public double getFrameTopY(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		PicPoint pt = new PicPoint();
		switch (getVertAlign()){
		case TOP:
			return ptAnchor.y;
		case BOTTOM:
			return ptAnchor.y+frameDepth+frameHeight;
		case BASELINE:
			return ptAnchor.y+frameHeight;
		case CENTER:
			return ptAnchor.y+(frameHeight+frameDepth)/2.0;
		default:
			return 0;
		}
	}


	/**
	 * Return y-coordinate of bottom boundary
	 */

	public double getFrameBottomY(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		return getFrameTopY()-frameHeight-frameDepth;
	}

	/**
	 * Return y-coordinate of baseline
	 */

	public double getFrameBaseLineY(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		return getFrameTopY()-frameHeight;
	}


	/**
	 * Return y-coordinate of center
	 */

	public double getFrameCenterY(){
		// This method is not allowed to use frame for computing the width because it is used to compute the frame in updateFrame.
		// However, it can use frame dimensions (frameWidth etc) because they are computed before accessing this method.
		return getFrameTopY()-0.5*(frameHeight+frameDepth);
	}




	/**
	 * set the width, height, and depth (of the text box, without frame). Normally, are set by the view.
	 */
	public void setDimensions(double w, double h, double d){
		this.width = w;
		this.height = h;
		this.depth = d;
		updateFrame();
		System.out.println(frameDepth+" "+frameWidth+" "+frameHeight);//debug
		// ces dimensions seront en general mises a jour par la view, donc il ne faut pas faire de fireChangeUpdate
		//		fireChangedUpdate(jpicedt.graphic.event.DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}



	/////////////////////////////////////////////////////////////////
	// NODE Stuff
	/////////////////////////////////////////////////////////////////

	public NodeConnectionManager getNodeConnectionManager(){
		return nodeConnectionManager;
	}

	public class TextNodeConnectionManager extends NodeConnectionManager {

		public TextNodeConnectionManager(){
			super(null);
			element = PicText.this; // [pending] potential bug-prone... if called from PicText<init>
		}

		/* returns the Picnode reference point.
		 * Used to compute connections: for instance, a straight connection between two nodes
		 * should be a segment in the line connecting the reference points of the two nodes.
		 */
		public PicPoint nodeReferencePoint(){
			if (! isFramed() || getFrameType()==FrameStyle.RECTANGLE)
				return ptAnchor.clone();
			else // otherwise, we always use the center
				return new PicPoint(getFrameCenterX(),getFrameCenterY());
		}


		/* Given a non-zero vector of coordinates (dx, dy), computes the origin of a connection
		 * that should start from that direction.
		 * this takes into account the nodeSep parameter (separation between the frame and the origin of the node).
		 * If nodeSep=0 for instance, this computes the intersection point of the semiline based at the reference point,
		 * and directed by (dx,dy) with the frame.
		 * About nodeSep: its effect mimics the effect in pstricks: we increase the size of the rectangle/ellipse/etc
		 * by the nodeSep amount.
		 */

		public PicPoint nodeConnectionOrigin(double dx, double dy){
			if (dx == 0 && dy==0){
				// bad value of the vector. Can occur if we try to join 2 nodes having the sams ref point...
				return nodeReferencePoint();
			}
			double refX=getNodeConnectionManager().nodeReferencePointX(); // [pending null ptr!]
			double refY=getNodeConnectionManager().nodeReferencePointY();
			if (!isFramed() || getFrameType()==FrameStyle.RECTANGLE){
				double lw; //linewidth, = 0 if no frame
				if (!isFramed())
					lw=0;
				else
					lw=getAttribute(PicAttributeName.LINE_WIDTH);

				//orientation
				// we work in coordinates where origin is at the reference point, and orientation of axes are given by signs of dx dy.
				// the relative coordinates of our vector are dx*sgnX,dy*sgnY (=abs(dx),abs(dy))

				double cornerX,cornerY; //relative coordinates of the corner of the rectangle. Should be non-negative
				double sgnX,sgnY; // +1 or -1 according to orientation;
				if (dx >= 0){
					sgnX=1;
					cornerX=getFrameRightX()-refX+0.5*lw+nodeSep;
				}
				else {
					sgnX=-1;
					cornerX=refX-getFrameLeftX()+0.5*lw+nodeSep;
				}
				if (dy >= 0){
					sgnY=1;
					cornerY=getFrameTopY()-refY+0.5*lw+nodeSep;
				}
				else {
					sgnY=-1;
					cornerY=refY-getFrameBottomY()+0.5*lw+nodeSep;
				}

				if (cornerX==0 || cornerY==0){return nodeReferencePoint();}

				double dx2=dx*sgnX; //relative coordinates of the vector
				double dy2=dy*sgnY;

				double x,y;// local coordinates of result
				// does (dx,dy) point above or below the corner ? Find out with determinant
				if (cornerX*dy2-cornerY*dx2 >=0) { // (dx2,dy2) is above.
					y=cornerY;
					x=cornerY*dx2/dy2; //dy2 is non-zero here because det >=0
				}
				else {
					x=cornerX;
					y=cornerX*dy2/dx2;  //dy is non-zero here because det <=0
				}
				//return the point in normal coordinates
				return new PicPoint(refX+x*sgnX,refY+y*sgnY);
			}
			else if (getFrameType()==FrameStyle.OVAL){//smallest area ellipse containing the box
				// solution pour une ellipse donnee par x^2/a^2+y^2/b^2=1 passant par un point donne (x,y) : x^2/a^2=y^2/b^2=1/2  => a=sqrt(2)*x;
				double lw=frameSep+getAttribute(PicAttributeName.LINE_WIDTH);
				double radiusX = 0.5*frameWidth+nodeSep+0.5*lw;
				double radiusY = 0.5*(frameHeight+frameDepth)+nodeSep+0.5*lw;
				if (radiusX==0 || radiusY==0) return nodeReferencePoint();
				double t=sqrt(dx*dx/(radiusX*radiusX) + dy*dy/(radiusY*radiusY));
				return new PicPoint(refX+dx/t,refY+dy/t);
			}
			else if (getFrameType()==FrameStyle.CIRCLE){
				double lw=frameSep+getAttribute(PicAttributeName.LINE_WIDTH);
				double radius=0.5*frameWidth+nodeSep+0.5*lw;
				// faut-il le sortir du sqrt(0.5)?
				if (radius==0) return nodeReferencePoint();
				double t=dx*dx/(radius*radius) + dy*dy/(radius*radius);
				return new PicPoint(refX+dx/t,refY+dy/t);
			}
			return null; // should not occur
		}

	}
	///////////////////////////////
	//// STRING FORMATING
	///////////////////////////////

	public String toString(){

		return "PicText@" + Integer.toHexString(hashCode()) + ", text=\""
		       + getText() + "\""
		       + " width = " + width + " frame width = " + frameWidth
		       + " height = " + height + " frame height = " + frameHeight
		       + " depth = " + depth + " frame depth = " + frameDepth
		       + " xOffset = " + xOffset + " yOffset = " + yOffset
		       + super.toString();
	}

	////////////////////////////////
	//// GUI
	////////////////////////////////

	/**
	 * Return a Customizer for geometry editing
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
		// [pending] use java.lang.ref.SoftReference
	}

	private Customizer cachedCustomizer = null;

	/**
	 * text string and geometry customizer
	 */
	class Customizer extends AbstractCustomizer implements ActionListener {

		/* widgets */
		// new *************************** begin (by ss & bp)
		private JTextArea latexTA;
		private TextMode textMode;
		// new *************************** end (by ss & bp)
		private JTextField latexTF;
		private DecimalNumberField textAnchorXTF, textAnchorYTF;
		private boolean isListenersAdded = false; // flag

		/** create a new Customizer for editing this PicText geometry and attributes */
		public Customizer(){

			super();
			JPanel p = new JPanel(new GridLayout(2,1,5,5));

			// line 1: latex string
			p.add(latexTF = new JTextField(15));

			// line 2: anchor point label
			JPanel box;
			box = new JPanel(new GridLayout(1,3,5,5));
			box.add(PEToolKit.createJLabel("attributes.TextAnchorPoint"));
			// x-coordinate of anchor point
			box.add(textAnchorXTF = new DecimalNumberField(4));
			// y-coordinate of anchor point
			box.add(textAnchorYTF = new DecimalNumberField(4));

			p.add(box);

			// cyclic focus TAB
			//textAnchorYTF.setNextFocusableComponent(latexTF);
			add(p, BorderLayout.NORTH);
			setPreferredSize(new Dimension(600,100));
		}

		/** add action listeners to widgets to reflect changes immediately */
		private void addActionListeners(){
			if (isListenersAdded) return; // already done
			latexTF.addActionListener(this);
			textAnchorXTF.addActionListener(this);
			textAnchorYTF.addActionListener(this);
			isListenersAdded = true;
		}

		private void removeActionListeners(){
			if (!isListenersAdded) return; // already done
			latexTF.removeActionListener(this);
			textAnchorXTF.removeActionListener(this);
			textAnchorYTF.removeActionListener(this);
			isListenersAdded = false;
		}

		/**
		 * (re)init widgets with Element's properties
		 */
		public void load(){

			removeActionListeners();
			// new *************************** begin (by ss & bp)
			if(this.textMode == TextMode.TEXT_AREA)
				latexTA.setText(getText());
			else
				latexTF.setText(getText());
			//latexTF.requestFocus();
			// new *************************** end (by ss & bp)

			textAnchorXTF.setValue(ptAnchor.x);
			textAnchorYTF.setValue(ptAnchor.y);

			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners(); // done the first time load is called
		}

		/**
		 * update Element's properties
		 */
		public void store(){

			setText(latexTF.getText());
			PicPoint point1Frame = new PicPoint(textAnchorXTF.getValue(),textAnchorYTF.getValue());
			setCtrlPt(P_ANCHOR,point1Frame,null);
		}

		public void actionPerformed(ActionEvent e){
			store();
		}

		/** manage focus */
		protected void activated(){
			latexTF.requestFocus();
		}

		/**
		 * @return the panel title, used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return PicText.this.getName();
		}

	}

} // PicText
