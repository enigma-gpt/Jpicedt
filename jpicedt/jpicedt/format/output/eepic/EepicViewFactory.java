// EepicViewFactory.java--- -*- coding: iso-8859-1 -*-
// February 11, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EepicViewFactory.java,v 1.24 2013/03/27 07:10:40 vincentb1 Exp $
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
package jpicedt.format.output.eepic;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.DefaultViewFactory;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.ArrowView;

import java.awt.*;
import java.awt.geom.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;

import static jpicedt.Log.*;


/**
 * A factory to create a View for a given Element when the content type for the model is Eepic.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: EepicViewFactory.java,v 1.24 2013/03/27 07:10:40 vincentb1 Exp $
 */
public class EepicViewFactory extends DefaultViewFactory {

	protected static final Color EEPIC_STROKE = Color.BLACK;
	protected static final Color EEPIC_WHITEN = Color.WHITE;
	protected static final Color EEPIC_SHADE = Color.GRAY;
	protected static final Color EEPIC_BLACKEN = Color.BLACK;
	protected static final Color EEPIC_COLOR = Color.GRAY; // eepic doesn't support colour, hence it's a good idea to paint in grayscale

	public EepicViewFactory(){
		super();
		if (DEBUG) debug("Initializing");
		map(AbstractCurve.class, AbstractCurveView.class); // also PicCircleFrom3Points
		map(PicEllipse.class, EllipseView.class);
		map(PicText.class, TextView.class);
		if (DEBUG) debug(toString());
	}


	/**
	 * @return a View for the given Element
	 * This returns a specific Epic/Eepic view for PicEllipse, AbstractCurve and PicText.
	 * Calls superclass otherwise.
	 */
	public View createView(Element element){
		if (DEBUG) debug("Creating view for "+element);
		return super.createView(element);
		// curves : polygons only can be filled
		/*if (element instanceof AbstractCurve) return new EepicAbstractCurveView((AbstractCurve)element);
		// ellipses : only non-rotated ellipses can be filled
		else if (element instanceof PicEllipse) return new EepicEllipseView((PicEllipse)element);
		// text : rectangular boxes only
		else if (element instanceof PicText) return new EepicTextView((PicText)element);
		// parallelo can be filled (by using \\path macros) => super class
		else return super.createView(element);*/
	}

	/**
	 * @return a Stroke built from the given attributes ; null if LINE_STYLE=NONE.
	 *         Only SOLID and DASHED (with DASH_OPAQUE only) are supported by LaTeX/Eepic
	 */
	public BasicStroke createStroke(PicAttributeSet set){
		// [pending] work out the pb with ThickLine, Thinline, etc...
		if(set.getAttribute(LINE_STYLE)==LineStyle.DASHED){
			// make sure dashes > 0
			float dash = (set.getAttribute(DASH_OPAQUE).floatValue() + set.getAttribute(DASH_TRANSPARENT).floatValue())/2.0f;
			if (dash>0){
				float[] dashArray = {dash,dash};
				return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue(),
				                       BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashArray, 0);
			}
			// otherwise, paint as SOLID :
		}
		return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue());
	}

	/**
	 * @return a Paint from the given attributes, suited for painting outlines.
	 * Eepic only support painting in black.
	 */
	public Paint createPaintForOutline(PicAttributeSet set){
		return EEPIC_STROKE;
	}

	/**
	 * @return a Paint from the given attributes, suited for painting interior.
	 * Eepic support filling with black, shade (i.e. gray) and white ; return null
	 * if FILL_STYLE is NONE.
	 */
	public Paint createPaintForInterior(PicAttributeSet set){
		if (set.getAttribute(FILL_STYLE)!=FillStyle.NONE){
			if(set.getAttribute(FILL_COLOR)==EEPIC_WHITEN) return EEPIC_WHITEN;
			else if (set.getAttribute(FILL_COLOR)==EEPIC_BLACKEN) return EEPIC_BLACKEN;
			else if (set.getAttribute(FILL_COLOR)==EEPIC_SHADE) return EEPIC_SHADE;
			else return EEPIC_COLOR;
		}
		else return null;
	}

	/**
	 * paint hatches (vlines,...) depending on the FILL_STYLE attributes of the given set.
	 * Overriden to do nothing, since (so far) hatches aren't supported by eepic or LaTeX.
	 */
	public void paintHatches(Graphics2D g, PicAttributeSet set, Shape shape){
	}

	/** paint shadow ; overriden to return null, since eepic and LaTeX don't support shadowing */
	public Shape createShadow(PicAttributeSet set, Shape shape){
		return null;
	}

	/**
	 * paint overstrike ; overriden to return null.
	 */
	public BasicStroke createStrokeForOverstrike(PicAttributeSet set){
		return null;
	}

	/**
	 * @return an Arrow from the given attribute set ; Eepic supports only ArrowStyle.ARROW_HEAD
	 *         null if ArrowStyle.NONE
	 */
	public ArrowView createArrow(PicAttributeSet set, PicAttributeName<ArrowStyle> direction){
		ArrowStyle a = set.getAttribute(direction);
		if (a != ArrowStyle.NONE) {  // if there's an arrow, we draw it as a ARROW_HEAD
			a = ArrowStyle.ARROW_HEAD; // width default values => don't synchronize !
			ArrowView v = ArrowView.createArrowView(a,set);
			return v;
		}
		else return null; // consistent with superclass contract
	}
}
