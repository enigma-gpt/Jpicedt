// Highlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
//
// D�partement de Physique
// �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
// 6, avenue du Ponceau
// F-95014 CERGY CEDEX
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
//
// Author: Sylvain Reynal <reynal@users.sourceforge.net>
// Version: $Id: Highlighter.java,v 1.6 2013/03/27 06:54:21 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// �'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
//
/// Commentary:

package jpicedt.graphic.view.highlighter;

import jpicedt.graphic.view.HitInfo;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.event.PEMouseEvent;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;

/**
 * a view dedicated to painting an Element's highlighter, e.g. when the Element
 * is selected. This is a delegate for a hosting View, and is created by a dedicated
 * HighlighterFactory.<br>
 * This highlighter should provide the following capabilities :
 * <ul>
 * <li> Render highlighted parts of the given Element to a Graphics2D context ;
 * <li> Mouse-hit testing on the highlighted part only.
 * </ul>
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: Highlighter.java,v 1.6 2013/03/27 06:54:21 vincentb1 Exp $
 */
public interface Highlighter {

	/**
	 * Fetches the HighlighterFactory implementation that is feeding the view hierarchy.
	 * @return the factory, null if none
	 */
	HighlighterFactory getHighlighterFactory();

	/** returns the higlighted Element */
	Element getElement();

	//////////////////////////////////////////////
	/// EVENT HANDLING
	/////////////////////////////////////////////

	/**
	 * Give notification from the model that a change occured for an element's highlighting
	 * this highlighter is responsible for rendering.
	 */
	void changedUpdate(DrawingEvent.EventType eventType, double scale);

	////////////////////////////////////////////////////////
	//// PAINT
	////////////////////////////////////////////////////////

	/**
	 * Render the Highlighter to the given graphic context.<br>
	 * @param allocation current clipping
	 * @param scale The current scale factor from model to screen for the Graphics2D context ;
	 *        this may be used to scale down line thickess, etc... so that lines/rectangle/... appear with the
	 *        same lenght on the screen whatever the scale factor that's set to the graphic context.
	 */
	void paint(Graphics2D g, Rectangle2D allocation, double scale);

	/**
	 * Returns the bounds of this Highlighter<br>
	 *         This will determine the clipping rectangle passed as a parameter to repaint
	 *         in the hosting view
	 */
	Rectangle2D getBounds();




	/////////////////////////////////////////////////////
	//// MOUSE
	/////////////////////////////////////////////////////

	/**
	 * Returns a HitInfo corresponding to the given mouse-event on this Highlighter.
	 */
	HitInfo hitTest(PEMouseEvent e);

	/**
	 * If this highligher intersects the given rectangle, add the associated Element (or a child Element if it's more appropriate, for
	 * instance for composite views) to the given list (if non-null), and returns true.
	 */
	boolean intersect(Rectangle2D r, ArrayList<Element> list);

}
