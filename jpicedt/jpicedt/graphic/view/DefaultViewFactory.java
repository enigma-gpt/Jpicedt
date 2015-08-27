// DefaultViewFactory.java --- -*- coding: iso-8859-1 -*-
// February 9, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: DefaultViewFactory.java,v 1.63 2013/03/27 06:55:01 vincentb1 Exp $
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

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import static jpicedt.Log.*;

/**
 * This is the default factory used to:<ol>
 * <li> create a View for a given <code>Element</code> ; it relies on a hashmap that associates
 *      <code>View</code>'s classes with <code>Element</code>'s class, the former being dynamically
 *      instanciated (through Reflection) on-the-fly when needed.</li>
 * <li> create objects for rendering Element's attributes.</li>
 * </ol>
 *
 * This factory should be used when one wants to render all <code>Element</code>'s attributes ; otherwise just
 * subclass with your own <code>ViewFactory</code> and override factory methods,
 * e.g. <code>createStroke</code>, <code>createPaintForInterior</code>, <code>createPaintForOutline</code>,
 * &hellip;: these methods are called by various <code>View</code>'s to extract rendering information from
 * <code>PicAttributeSet</code>'s when common behaviour is expected, whatever the particular
 * <code>Element</code> being actually rendered.  You may also want to add your own
 * <code>Element</code>/<code>View</code> association by calling the <code>map()</code> method. In this case,
 * your View class <b>MUST</b> have one constructor of the form <code>View(? super your_element, ? super
 * this_factory)</code> for the reflection mechanism to work properly.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: DefaultViewFactory.java,v 1.63 2013/03/27 06:55:01 vincentb1 Exp $
 */
public class DefaultViewFactory implements ViewFactory, AttributesViewFactory {

	// --- experimental support for bitmap images of PicText's ---
	//private static final boolean isExperimentalSupportForBitmap=true; // set to false to get the old behaviour
	private static final boolean isExperimentalSupportForBitmap=false; // set to false to get the old behaviour

	/** default font used to display text with TextView */
	public static Font TEXT_FONT_DEFAULT = new Font("SansSerif", Font.PLAIN, 10);
	/** key used to fetch the font family value from a Properties object */
	public static String KEY_TEXT_FONT = "view.text-font";
	/** scaling factor to be applied to the default font before rendering, so as to compensate for zoom factor */
	protected static final float TEXT_FONT_SCALE=4.0f;
	/** font used to display text strings with TextView */
	protected static Font textFont = TEXT_FONT_DEFAULT.deriveFont((float)TEXT_FONT_DEFAULT.getSize()/TEXT_FONT_SCALE);

	/** a <code>HashMap</code> that associates <code>View</code>'s classes with <code>Element</code>'s
		classes */
	protected HashMap<Class<? extends Element>,Class<? extends View>> factoryMap;


	/**
	 * For debugging purpose.
	 */
	public static void main(String[] arg){
		DefaultViewFactory f = new DefaultViewFactory();
		PicAttributeSet set = new PicAttributeSet();
		//PicRectangle elem = new PicRectangle(new PicPoint(0.0,10.0),new PicPoint(20.0,40.0),set);
		//View v = f.createView(elem);
	}

	public String toString(){
		String s = super.toString();
		s += "factoryMap:\n";
		for (Class<? extends Element> c: factoryMap.keySet()){
			s += c + " => " + factoryMap.get(c) + "\n";
		}
		return s;
	}

	// [pending] use constructor to init global parameters (text rendering,...)
	public DefaultViewFactory(){
		if (DEBUG) debug("Initializing");
		factoryMap = new HashMap<Class<? extends Element>,Class<? extends View>>();
		map(PicEllipse.class, EllipseView.class); // also PicCircleFrom3Points
		map(PicParallelogram.class, ParallelogramView.class);
		map(AbstractCurve.class, AbstractCurveView.class); // and PicMultiCurve, PicPsCurve, PicSmoothPolygon
		map(PicText.class, TextView.class);
		map(PicNodeConnection.class, PicNodeConnectionView.class);
		map(BranchElement.class, CompositeView.class);
		if (DEBUG) debug(toString());
	}

	/**
	 * Configure static fields using the given <code>Properties</code> object.
	 * @param preferences properties used to read shared parameters
	 *        If null, default values are used.
	 */
	public static void configure(Properties preferences){
		String val = preferences.getProperty(KEY_TEXT_FONT);
		if (val==null) textFont = TEXT_FONT_DEFAULT;
		else textFont = Font.decode(val);
		textFont = textFont.deriveFont((float)textFont.getSize()/TEXT_FONT_SCALE);
	}

	/**
	 * Associates the given View's class with the given <code>Element</code>'s class in the hashmap
	 * responsible for creating View's for <code>Element</code>'s.
	 * @since jpicedt 1.5
	 */
	public void map(Class<? extends Element> classElement, Class<? extends View> classView){
		factoryMap.put(classElement,classView);
	}

	/**
	 * Remove the element/view mapping for the given <code>Element</code>'s class in the hashmap responsible
	 * for creating View's for <code>Element</code>'s.
	 * @since jpicedt 1.5
	 */
	public void unmap(Class<? extends Element> classElement){
		factoryMap.remove(classElement);
	}

	/**
	 * Returns the <code>View</code>'s class associated with the given <code>Element</code>'s class in the
	 * hashmap responsible for creating <code>View</code>'s for <code>Element</code>'s.
	 * @since jpicedt 1.5
	 */
	public Class<? extends View> getMappedClass(Class<? extends Element> classElement){
		Class<? extends View> classView = factoryMap.get(classElement);
		Class superclassElement = classElement;
		while (classView == null){
			superclassElement = superclassElement.getSuperclass();
			if (Element.class.isAssignableFrom(superclassElement)){ // if superclassElement extends Element
				classView = factoryMap.get(superclassElement);
			}
			else
				break;
		}
		if (classView==null) return null;
		return (Class<? extends View>)classView; // pseudo unchecked cast, it's ok if HashMap has been populated in a proper way
	}

	/**
	 * Create a View for the given <code>Element</code> by looking up the hashmap responsible for creating
	 * <code>View</code>'s for <code>Element</code>'s.
	 * @since jpicedt 1.5
	 */
	public View createView(Element element){
		if (DEBUG) debug("Creating view for "+element);
		//if (DEBUG) debugAppendLn(toString());
		Class<? extends View> classView = getMappedClass(element.getClass());
		if (DEBUG) debugAppendLn("Mapped to:" + classView);
		if (classView==null) return null;
		try {
			// look for a constructor like View(<? extends Element> e, <? extends DefaultViewFactory> f)
			//Constructor<?>[] constructors = classView.getConstructors();
			for (Constructor<?> c:  classView.getConstructors()){
				if (DEBUG) debugAppendLn("Constructor:" + c);
				Class<?>[] params = c.getParameterTypes();
				// check if element instanceof param[0] and this instanceof params[1]
				if (params.length == 2 && params[0].isAssignableFrom(element.getClass()) && params[1].isAssignableFrom(this.getClass())){
					Constructor<? extends View> cc = classView.getConstructor(params[0],params[1]);
					View v = cc.newInstance(element,this);
					return v;
				}
			}
			//return null;
			// [test underway]
			return new LeafElementView(element, this);
		}
		catch (Exception e){
			System.err.println("View " + classView + " for element " + element.getClass() + " can't be instantiated.");
			e.printStackTrace();
			//return null;
			return new LeafElementView(element, this);
		}
	}

	/*
	 * @return a View for the given Element
	 * Current implementation returns a View appropriate for the following elements :
	 * <ul>
	 * <li> PicEllipse and any subclass -> EllipseView ;
	 * <li> BranchElement -> CompositeView ;
	 * <li> PicParallelogram -> ParallelogramView ;
	 * <li> PicEllipse / PicCircleFrom3Points -> EllipseView ;
	 * <li> PicText -> TextView ;
	 * <li> other -> null (this give a change for the receiver to provide its own View)
	 * </ul>
	 */
	/*public View _createView(Element element){
		if (DEBUG) debug("element=" + element);
		// always place daughters BEFORE superclass :
		if (element instanceof PicEllipse) return new EllipseView((PicEllipse)element);
		else if (element instanceof PicParallelogram) return new ParallelogramView((PicParallelogram)element);
		else if (element instanceof AbstractCurve) return new AbstractCurveView((AbstractCurve)element);
		else if (element instanceof PicText) {
			if (isExperimentalSupportForBitmap) return new TextViewHybrid((PicText)element);
			return new TextView((PicText)element);
		}
		else if (element instanceof PicNodeConnection) return new PicNodeConnectionView((PicNodeConnection)element);
		else if (element instanceof BranchElement) return new CompositeView((BranchElement)element);
		else {
			if (DEBUG) debug("No view available for the given element:"+element);
			return null;
		}
	}*/

	/**
	 * Returns a Stroke built from the given attributes ; <code>null</code> if
	 * <code>LINE_STYLE</code>=<code>NONE</code>.  All currently supported attributes for stroke are rendered.
	 */
	public BasicStroke createStroke(PicAttributeSet set){
		//if (DEBUG) debug(".createStroke");

		switch(set.getAttribute(LINE_STYLE)){
		case SOLID:
			return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue());

		case DASHED:
			// make sure dashes > 0
			float dashOpaque = set.getAttribute(DASH_OPAQUE).floatValue();
			if (dashOpaque>0){
				float dashTransparent = set.getAttribute(DASH_TRANSPARENT).floatValue();
				if (dashTransparent>0){
					return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue(),
					                       BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
					                       new float[]{dashOpaque, dashTransparent}, 0);
				}
			}
			// otherwise, paint as SOLID :
			return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue());

		case DOTTED:
			float dotSep = set.getAttribute(DOT_SEP).floatValue();
			// make sur dotsep > 0
			if (dotSep>0){
				float[] dotArray = {
				                           (float)(dotSep * 0.1),  // opaque 10%
				                           (float)(dotSep * 0.9)}; // transparent 90%
				return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue(),
				                       BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dotArray, 0);
			}
			// otherwise paint as SOLID
			else {
				return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue());
			}
		default:  // LINE_STYLE=NONE
			return null;
		}
	}

	/**
	 * Returns a Stroke built from the <code>OVER_STRIKE</code> attribute.  <code>Factory</code> that don't
	 * overstrike should override to return <code>null</code>.
	 * @param set used to fetch the overstrike attributes
	 */
	public BasicStroke createStrokeForOverstrike(PicAttributeSet set){

		if (set.getAttribute(OVER_STRIKE)==Boolean.FALSE)
			return null;
		return new BasicStroke(set.getAttribute(LINE_WIDTH).floatValue()+2.0f*set.getAttribute(OVER_STRIKE_WIDTH).floatValue(),
			BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);
	}

	/**
	 * Returns a Paint object from the given attributes, that is suited for painting an outline, or null if
	 * <code>LINE_STYLE</code> is <code>NONE</code>.<p>
	 * All currently supported attributes for painting an outline are rendered. It's up to subclassers to
	 * adapt this behavior to their need (probably by restricting it).
	 */
	public Paint createPaintForOutline(PicAttributeSet set){
		if (set.getAttribute(LINE_STYLE) != LineStyle.NONE)
			return set.getAttribute(LINE_COLOR);
		else return null;
	}

	/**
	 * Returns a Paint object suited for rendering shadows. Guaranteed to return a non-<code>null</code>
	 * object, even if shadowing is not supported (although a subclass may return null as well).
	 * <p>
	 * All currently supported attributes for painting a shadow are rendered. It's up to subclassers
	 * to adapt this behavior to their need (probably by restricting it, e.g., to gray-levels).
	 */
	public Paint createPaintForShadow(PicAttributeSet set){
		return set.getAttribute(SHADOW_COLOR);
	}

	/**
	 * Returns a <code>Paint</code> object from the given attributes, that is suited for painting the interior
	 * of a shape.
	 * <p>
	 * The returned object depends on the <code>FILL_STYLE</code> and <code>FILL_COLOR</code> attributes of
	 * the given set:
	 * <p>
	 * <ul>
	 * <li> <code>FILL_STYLE</code> = <code>NONE</code>: returns <code>null</code></li>
	 * <li> <code>FILL_STYLE</code> = <code>SOLID</code>, <code>VLINES</code>, <code>VLINES_FILLED</code>,
	 *      <code>HLINES</code>, <code>HLINES_FILLED</code>, <code>CROSSHATCH</code> and
	 *      <code>CROSSHATCH_FILLED</code>: returns a Color object created from <code>FILL_COLOR</code>.</li>
	 * </ul>
	 * To sum up, all currently supported attributes (as defined in
	 * <code>jpicedt.graphic.model.PicObjectConstants</code>) for painting the interior of shapes
	 * are rendered. [pending] gradient (need to add some attributes).
	 */
	public Paint createPaintForInterior(PicAttributeSet set){
		//if (DEBUG) debug(".createPaintForInterior");
		FillStyle fillStyle = set.getAttribute(FILL_STYLE);
		if (fillStyle==FillStyle.NONE)
			return null;
		else if (fillStyle==FillStyle.SOLID || fillStyle==FillStyle.HLINES_FILLED
				 || fillStyle==FillStyle.VLINES_FILLED || fillStyle==FillStyle.CROSSHATCH_FILLED)
			return set.getAttribute(FILL_COLOR);
		else if (set.getAttribute(SHADOW)==Boolean.TRUE)
			return set.getAttribute(FILL_COLOR); // if shadowing is on, fill the shape whatever the "fillstyle" parameter.
		// [SR:pending] pending patch by Herbert Voss, see the mail from Vincent 17/04/2004.
		else
			return null; // [pending] gradient
		// rem : hatches are handled by "paintHatches". Using texturePaint just barfs totally because
		// the BufferedImage used for the texture is scaled afterwards by the current Graphics2D scaler.
	}

	/**
	 * Paint hatches (vlines,&hellip;) depending on the <code>FILL_STYLE</code> attributes of the given set.
	 * Factories that don't paint hatches should override this method to do nothing.
	 * @param shape used to clip the hatch (i.e. the shape the calling view must render)
	 */
	 // (after thinking it over carefully, it's impossible to cache anything here)
	public void paintHatches(Graphics2D g, PicAttributeSet set, Shape shape){

		// [pending] bug when angle > 0 (lines are not parallel)
		FillStyle fillStyle = set.getAttribute(FILL_STYLE);
		double angle=0;
		Shape oldClip;
		switch (fillStyle){
		case VLINES:
		case VLINES_FILLED:
			angle = set.getAttribute(HATCH_ANGLE);
			oldClip = g.getClip(); // save old clip before we add our own clip
			g.clip(shape); // [pending] apparently, clipping dramatically decreases performances, maybe resort to TexturePaint ?
			paintHatchesHelper(g,set,angle,shape.getBounds2D());
			g.setClip(oldClip); // restore old clip
			break;
		case HLINES:
		case HLINES_FILLED:
			angle = set.getAttribute(HATCH_ANGLE);
			if (angle>0) angle -= 90;
			else angle += 90;
			oldClip = g.getClip(); // save old clip before we add our own clip
			g.clip(shape);
			paintHatchesHelper(g,set,angle,shape.getBounds2D());
			g.setClip(oldClip); // restore old clip
			break;
		case CROSSHATCH:
		case CROSSHATCH_FILLED:
			angle = set.getAttribute(HATCH_ANGLE);
			oldClip = g.getClip(); // save old clip before we add our own clip
			g.clip(shape);
			paintHatchesHelper(g,set,angle,shape.getBounds2D());
			if (angle>0) angle -= 90;
			else angle += 90;
			paintHatchesHelper(g,set,angle,shape.getBounds2D());
			g.setClip(oldClip); // restore old clip
			break;
		default: return;
		}

	}

	/**
	 * Helper method for <code>paintHatches()</code>.
	 * @param angle angle (in degrees) b/w lines and the vertical axis, as in <code>VLINES</code> (positive
	 * CCW)
	 * @param clip the bounding rectangle of the shape
	 */
	private void paintHatchesHelper(Graphics2D g, PicAttributeSet set, double angle, Rectangle2D clip){

		g.setStroke(new BasicStroke(set.getAttribute(HATCH_WIDTH).floatValue()));
		g.setPaint(set.getAttribute(HATCH_COLOR));
		double sep = set.getAttribute(HATCH_SEP).doubleValue();
		Line2D.Double hatch = new Line2D.Double();

		if (angle==0){ // vertical
			hatch.y1 = clip.getMinY();
			hatch.y2 = clip.getMaxY();
			final double maxX=clip.getMaxX();
			for (double x = clip.getMinX(); x<maxX;x += sep){
				hatch.x1 = hatch.x2 = x;
				g.draw(hatch);
			}
		}
		else if (angle==90){ // horizontal
			hatch.x1 = clip.getMinX();
			hatch.x2 = clip.getMaxX();
			final double maxY=clip.getMaxY();
			for (double y = clip.getMinY(); y<maxY;y += sep){
				hatch.y1 = hatch.y2 = y;
				g.draw(hatch);
			}
		}
		else { // others
			double incX = sep/cos(toRadians(angle)); // >0
			double incY = sep/sin(toRadians(angle)); //
			final double maxX;
			if (angle>0) {
				hatch.x1 = clip.getMinX();
				hatch.y1 = clip.getMinY();
				maxX = clip.getMaxX() + clip.getMaxY() * tan(toRadians(angle));
			}
			else {
				hatch.x1 = clip.getMinX();
				hatch.y1 = clip.getMaxY();
				maxX = clip.getMaxX() - clip.getMaxY() * tan(toRadians(angle));
			}
			hatch.x2 = hatch.x1;
			hatch.y2 = hatch.y1;
			while (hatch.x2 < maxX){
				hatch.x2 += incX;
				hatch.y1 += incY;
				//System.out.println("x1="+hatch.x1+"\ty1="+hatch.y1+"\tx2="+hatch.x2+"\ty2="+hatch.y2);
				g.draw(hatch);
			}
		}
	}

	/**
	 * Returns a <code>Shape</code> for rendering the shadow of the given <code>Shape</code>, whose properties
	 * are drawn from the SHADOW attribute and rel.  Factory that don't paint shadow should override to return
	 * <code>null</code>.
	 * @param shape the Shape under which to drop a shadow
	 * @param set used to fetch the shadow attributes
	 * @return null if no shadow should be painted.
	 */
	public Shape createShadow(PicAttributeSet set, Shape shape){

		if (set.getAttribute(SHADOW)==Boolean.FALSE) return null;
		// create shadow shape
		double angle = toRadians(set.getAttribute(SHADOW_ANGLE).doubleValue());
		double sep = set.getAttribute(SHADOW_SIZE).doubleValue();
		AffineTransform at = AffineTransform.getTranslateInstance(sep * cos(angle), sep * sin(angle));
		return at.createTransformedShape(shape);
	}

	/**
	 * @return an <code>ArrowView</code> from the given attribute set, or <code>null</code> if
	 * <code>ArrowStyle.NONE</code>.
	 * @param direction <code>LEFT_ARROW</code> or <code>RIGHT_ARROW</code>
	 */
	public ArrowView createArrow(PicAttributeSet set, PicAttributeName<ArrowStyle> direction){
		//if (DEBUG) debug(".createArrow");
		ArrowStyle a = set.getAttribute(direction);
		ArrowView v = ArrowView.createArrowView(a,set);
		if (v==null) // Arrow.NONE
			return null;
		return v;
	}



} // DefaultViewFactory
