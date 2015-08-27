/*
 AbstractCurveView.java - July 19, 2006 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
package jpicedt.graphic.view;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.ContentType;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.lang.reflect.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;

/**
 * a View for rendering any concrete implementation of jpicedt.graphic.model.AbstractCurve.
 * The added value wrt to LeafElementView is related to the painting of dots.
 */
public class AbstractCurveView extends LeafElementView {

	// The following are set by syncDotsShape(), and used to draw polygon's dots, if applicable:
	protected Shape dotShape=null; // null if no dots to be painted
	protected ArrayList<Shape> dotShapeList=null;
	protected Stroke dotStroke=null; // different from outlineStroke !
	protected Paint dotFillPaint=null, dotDrawPaint=null; // may be different from outlinePaint or interiorPaint

	/**
	 * construct a new View for the given curve
	 */
	public AbstractCurveView(AbstractCurve curve, AttributesViewFactory f){
		super(curve,f);
		changedUpdate(null);
	}

	/**
	 * Synchronizes arrows' specific attributes with the model. Called from syncAttributes().
	 * This implementation creates arrows only if the model is an open curve.
	 */
	protected void syncArrowsAttributes(){
		// closed curve:
		if (((AbstractCurve)element).isClosed())
			leftArrow = rightArrow = null;
		// open curve:
		else {
			super.syncArrowsAttributes();
		}
	}

	/**
	 * Inherits from superclass, except where painting dots is concerned, where this method delegates
	 * to {@link #syncDotShapeAttributes syncDotShapeAttributes()} to create the appropriate Shape.
	 */
	protected void syncAttributes(){
		super.syncAttributes();
		syncDotShapeAttributes();
	}

	/**
	 * Inherits from superclass, except where updating dot's geometry is concerned, where this method delegates
	 * to {@link #syncDotShapeList syncDotShapeList()} to create the appropriate Shape.
	 */
	protected void syncShape(){
		super.syncShape();
		syncDotShapeList();
	}

	/**
	 * Computes the Shape (centered at 0,0) used for drawing polygon's dots, built from the current attributes;
	 * set this.dotShape to null if POLYDOTS_STYLE==NONE or if dotting is not supported by the factory.<p>
	 * This method gets invoked by {@link #syncAttributes syncAttributes} as part of the delegation mechanism,
	 * when dot's style has changed.
	 */
	protected void syncDotShapeAttributes(){
		PolydotsStyle dotStyle = element.getAttribute(POLYDOTS_STYLE);
		float dotWidth, dotHeight;
		AffineTransform rotAt;
		GeneralPath pth;
		boolean isDotFilled=false;

		dotWidth = (element.getAttribute(LINE_WIDTH).floatValue() * element.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE).floatValue()
		                  + element.getAttribute(POLYDOTS_SIZE_MINIMUM_MM).floatValue())/2.0f;
		this.dotStroke = new BasicStroke(dotWidth/5.0f); // [SR:pending] ask D.Guirou for exact data
		dotHeight = dotWidth * element.getAttribute(POLYDOTS_SCALE_V).floatValue();
		dotWidth *= element.getAttribute(POLYDOTS_SCALE_H).floatValue();
		if (dotWidth<0) dotWidth=0;
		if (dotHeight<0) dotHeight=0;

		rotAt = AffineTransform.getRotateInstance(element.getAttribute(POLYDOTS_ANGLE).doubleValue()*Math.PI/180.0);

		switch (dotStyle){
		case POLYDOTS_DISK:
			this.dotShape = new Ellipse2D.Double(-dotWidth,-dotHeight,2.*dotWidth,2.*dotHeight);
			isDotFilled=true;
			// rotating is useless here ;-)
			break;
		case POLYDOTS_CIRCLE:
			this.dotShape = new Ellipse2D.Double(-dotWidth,-dotHeight,2.*dotWidth,2.*dotHeight);
			isDotFilled=false;
			// ibid.
			break;
		case POLYDOTS_PLUS:
			pth = new GeneralPath();
			pth.moveTo(0,dotHeight);
			pth.lineTo(0,-dotHeight);
			pth.moveTo(-dotWidth,0);
			pth.lineTo(dotWidth,0);
			this.dotShape = rotAt.createTransformedShape(pth);
			isDotFilled=false;
			break;
		case POLYDOTS_TRIANGLE:
		case POLYDOTS_TRIANGLE_FILLED:
			pth = new GeneralPath();
			pth.moveTo(-dotWidth,-dotHeight);
			pth.lineTo(dotWidth,-dotHeight);
			pth.lineTo(0,dotHeight);
			pth.closePath();
			this.dotShape = rotAt.createTransformedShape(pth);
			isDotFilled=(dotStyle==PolydotsStyle.POLYDOTS_TRIANGLE_FILLED);
			break;
		case POLYDOTS_SQUARE:
		case POLYDOTS_SQUARE_FILLED:
			pth = new GeneralPath();
			pth.moveTo(-dotWidth,-dotHeight);
			pth.lineTo(dotWidth,-dotHeight);
			pth.lineTo(dotWidth,dotHeight);
			pth.lineTo(-dotWidth,dotHeight);
			pth.closePath();
			this.dotShape = rotAt.createTransformedShape(pth);
			isDotFilled=(dotStyle==PolydotsStyle.POLYDOTS_SQUARE_FILLED);
			break;
		case POLYDOTS_PENTAGON:
		case POLYDOTS_PENTAGON_FILLED:
			pth = new GeneralPath();
			pth.moveTo(0,dotHeight);
			for (int i=1; i<5; i++){
				double angle = (0.4*i+.5)*Math.PI;
				double x = dotWidth * Math.cos(angle); // don't worry about performances here, syncDotShape is never called
				double y = dotHeight * Math.sin(angle); // when updating an Element's geometry
				pth.lineTo((float)x,(float)y);
			}
			pth.closePath();
			this.dotShape = rotAt.createTransformedShape(pth);
			isDotFilled=(dotStyle==PolydotsStyle.POLYDOTS_PENTAGON_FILLED);
			break;
			// [SR:pending] Tbar dots ?
		default:
			this.dotShape=null;
		}

		// dot draw/fill attributes:
		if (isDotFilled){ // e.g. "square*" => stroked and filled with "linecolor", and "fillcolor" is not used
			if (outlinePaint==null)
				this.dotDrawPaint = this.dotFillPaint = Color.black; // don't paint multicurve, yet paint dots !
			else
				this.dotDrawPaint = this.dotFillPaint = outlinePaint;
		}
		else {// e.g. "triangle" => filled with "fillcolor", stroked with "linecolor"
			this.dotDrawPaint = (outlinePaint==null ? Color.black : outlinePaint);
			this.dotFillPaint = (interiorPaint==null ? Color.white : interiorPaint);
		}
	}

	/**
	 * sync the ArrayList containing the dot's shapes to be painted, with the current model
	 * geometry. Called by changedUpdate() in response to a GEOMETRY_CHANGE event.
	 * Assume syncDotShapeAttributes() has been called before.
	 */
	protected void syncDotShapeList(){

		if (this.dotShapeList==null)
			this.dotShapeList = new ArrayList<Shape>();

		if (this.dotShape != null) {
			AbstractCurve curve = (AbstractCurve)element;
			this.dotShapeList.clear();
			final int N = curve.getNumberOfSubdivisionPoints();
			for (int subDivIdx = 0; subDivIdx < N; subDivIdx++){
				int i = curve.segmentToPointIndex(subDivIdx, AbstractCurve.PointType.SUBDIVISION_POINT);
				AffineTransform trans = AffineTransform.getTranslateInstance(curve.getBezierPtX(i),curve.getBezierPtY(i));
				this.dotShapeList.add(trans.createTransformedShape(this.dotShape));
			}
		}
		else
			this.dotShapeList.clear();


	}

	/**
	 * Render the View to the given graphic context.
	 */
	public void paint(Graphics2D g,Rectangle2D a){
		if (shape == null && this.dotShape==null) return;
		if (!a.intersects(getBounds())) return;

		// paint curve if no dots or if dots+superimpose
		if (shape != null && (this.dotShape==null || (this.dotShape!=null && element.getAttribute(POLYDOTS_SUPERIMPOSE)))){
			super.paint(g,a); //paint shape, shadow and arrows
		}

		// paint dots:
		g.setStroke(this.dotStroke);
		for (Shape dot: this.dotShapeList){
			g.setPaint(this.dotFillPaint);
			g.fill(dot);
			g.setPaint(this.dotDrawPaint);
			g.draw(dot);
		}
	}

} // AbstractCurveView
