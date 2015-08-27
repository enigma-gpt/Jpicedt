// DefaultHighlighterFactory.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: DefaultHighlighterFactory.java,v 1.7 2013/03/27 06:54:31 vincentb1 Exp $
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

package jpicedt.graphic.view.highlighter;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.reflect.*;

import static jpicedt.graphic.view.ViewConstants.*;
import static jpicedt.Log.*;

/**
 * This is the default factory used to create a Highlighter for a given Element.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: DefaultHighlighterFactory.java,v 1.7 2013/03/27 06:54:31 vincentb1 Exp $
 */
public class DefaultHighlighterFactory implements HighlighterFactory {

	public static final Color LOCAL_HIGHLIGHTING_COLOR= Color.green;
	public static final Color GLOBAL_HIGHLIGHTING_COLOR= Color.red;

	private HashMap<Class<? extends Element>,Class<? extends Highlighter>> factoryMap;

	public DefaultHighlighterFactory(){
		if (DEBUG) debug("Initializing");
		factoryMap = new HashMap<Class<? extends Element>,Class<? extends Highlighter>>();
		map(PicEllipse.class, EllipseHighlighter.class);
		map(PicCircleFrom3Points.class, CircleHighlighter.class);
		map(PicParallelogram.class, DefaultHighlighter.class);
		map(PicSmoothPolygon.class, SmoothPolygonHighlighter.class);
		map(PicPsCurve.class, PsCurveHighlighter.class);
		map(AbstractCurve.class, AbstractCurveHighlighter.class);
		map(PicText.class, DefaultHighlighter.class);
		map(PicNodeConnection.class, DefaultHighlighter.class);
		map(BranchElement.class, CompositeHighlighter.class);
		map(Element.class, DefaultHighlighter.class); // default
	}

	/**
	 *
	 * @since jpicedt 1.5
	 */
	public void map(Class<? extends Element> classElement, Class<? extends Highlighter> classHighlighter){
		factoryMap.put(classElement,classHighlighter);
	}

	/**
	 *
	 * @since jpicedt 1.5
	 */
	public void unmap(Class<? extends Element> classElement){
		factoryMap.remove(classElement);
	}

	/**
	 *
	 * @since jpicedt 1.5
	 */
	public <T extends Element> Class<? extends Highlighter> getMappedClass(Class<T> classElement){
		Class<? extends Highlighter> classHighlighter = factoryMap.get(classElement);
		Class superclassElement = classElement;
		while (classHighlighter == null){
			superclassElement = superclassElement.getSuperclass();
			if (Element.class.isAssignableFrom(superclassElement)){ // if superclassElement extends Element
				classHighlighter = factoryMap.get(superclassElement);
			}
			else
				break;
		}
		if (classHighlighter==null) return null;
		return (Class<? extends Highlighter>)classHighlighter; // pseudo unchecked cast, it's ok if HashMap has been populated in a proper way
	}

	public Highlighter createHighlighter(Element element){
		Class<? extends Highlighter> classHighlighter = getMappedClass(element.getClass());
		if (classHighlighter==null) return null;
		try {
			// look for a constructor like Highlighter(<? extends Element> e, <? extends HighlighterFactory> f)
			//Constructor<?>[] constructors = classHighlighter.getConstructors();
			for (Constructor<?> c:  classHighlighter.getConstructors()){
				Class<?>[] params = c.getParameterTypes();
				if (params.length == 2 && params[0].isAssignableFrom(element.getClass()) && params[1].isAssignableFrom(this.getClass())){
					Constructor<? extends Highlighter> cc = classHighlighter.getConstructor(params[0],params[1]);
					return cc.newInstance(element,this);
				}
			}
			return null;
		}
		catch (Exception e){
			System.err.println("Highlighter " + classHighlighter + " for element " + element.getClass() + " can't be instantiated.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return a Highlighter for the given Element
	 * Current implementation returns a Highlighter appropriate for the following elements :
	 * <ul>
	 * <li> PicEllipse -> EllipseHighlighter ;
	 * <li> PicCircleFrom3Points -> CircleHighlighter ;
	 * <li> BranchElement -> CompositeHighlighter ;
	 * <li> PicParallelogram -> ParallelogramHighlighter ;
	 * <li> PicText -> TextHighlighter ;
	 * <li> other -> DefaultHighlighter
	 * </ul>
	 */
	/*
	public Highlighter createHighlighter(Element element){ // [pending] use a HashMap

		if (DEBUG) debug("element=" + element);
		// always place daughters BEFORE superclass :
		if (element instanceof PicCircleFrom3Points) return new CircleHighlighter((PicCircleFrom3Points)element,this);
		else if (element instanceof PicEllipse) return new EllipseHighlighter((PicEllipse)element,this);
		else if (element instanceof PicParallelogram) return new DefaultHighlighter((PicParallelogram)element,this);
		else if (element instanceof PicSmoothPolygon) return new SmoothPolygonHighlighter((PicSmoothPolygon)element,this);
		else if (element instanceof PicPsCurve) return new PsCurveHighlighter((PicPsCurve)element,this);
		else if (element instanceof AbstractCurve) return new AbstractCurveHighlighter((AbstractCurve)element,this);
		else if (element instanceof PicText) return new DefaultHighlighter((PicText)element,this);
		else if (element instanceof PicNodeConnection) return new DefaultHighlighter((PicNodeConnection)element,this);
		else if (element instanceof BranchElement) return new CompositeHighlighter((BranchElement)element,this);
		else return new DefaultHighlighter(element,this);
	}*/

	/**
	 * Returns a Stroke object suited for painting hightlighter's stroke, e.g. tangents.
	 * @param scale The current scale factor from model to screen for the Graphics2D context ;
	 *        this may be used to scale down line thickess, etc... so that lines/rectangle/... appear with the
	 *        same length on the screen whatever the scale factor that's set to the graphic context.
	 */
	protected Stroke createStroke(double scale){
		//return new BasicStroke((float)(1.0/scale));
		final float[] dotArray = {(float)(5.0/scale),(float)(5.0/scale)};
		return new BasicStroke((float)(1.0/scale),BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dotArray, 0);
	}

} // DefaultHighlighterFactory
