// View.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: View.java,v 1.12 2013/03/27 06:53:31 vincentb1 Exp $
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

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.highlighter.Highlighter;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * A <code>View</code> is a graphic representation of an <code>Element</code>.
 * It can be rendered using its <code>paint()</code> method.<p>
 * It provides the following capabilities :
 * <ul>
 * <li> Rendering to a <code>Graphics2D</code> context;</li>
 * <li> Highlighting through a Highlighter delegate;</li>
 * <li> Mouse-hit testing, for a View knows better than anyone how a graphic
 *      element looks on the screen, and thus where a user should click to
 *      select/whatever/&hellip; the element). As an example, consider a
 *      rectangle element that has a "fill-color" attribute set to "green",
 *      and is indeed paint in green in some View implementation, but is
 *      transparent in some other implementation : then when the user clicks
 *      inside the rectangle, the first implementation should return a
 *      "hit-ok" while the second should return "hit-fail", even though the
 *      element has the same "fill-color" in both cases.  <br>
 *      View implementation that don't implement editing capabilities may have
 *      their <code>hitTest()</code> method simply return null to signal that every
 *      mouse-click just fails, and their paintHighlighter do nothing.  <br>
 *      [pending] Another approach would be to define aka EditableView
 *      interface inheriting from View and adding the afore-mentioned methods?</li>
 *      </ul>
 *      View's belong to a tree that has the same structure as the associated
 *      Drawing, since each View is attached to an element in the tree.
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: View.java,v 1.12 2013/03/27 06:53:31 vincentb1 Exp $
 *
 */
public interface View {

	/**
 	 * Returns the element the View is responsible for rendering
	 */
	Element getElement();

	/**
 	 * set the element the View is responsible for rendering
	 */
	void setElement(Element e);

	///////////////////////////////////////////////////////////////////
	//// tree-structure
	///////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent of the view, as given by the tree-structure the
	 * associated graphic element belongs to.
	 * @return the parent, <code>null</code> if none
	 */
	View getParentView();


	/**
	 * Fetches the container hosting the view.  This is useful for
	 * things like scheduling a repaint, finding out the host
	 * components font, etc.
	 *
	 * @return the container, null if none
	 */
	PECanvas getContainer();

	/**
	 * Fetches the <code>ViewFactory</code> implementation that is feeding the view hierarchy.
	 * @return the factory, null if none
	 */
	ViewFactory getViewFactory();

	/**
	 * Fetch a <code>Graphics</code> for rendering.  This can be used to determine
	 * font characteristics, and will be different for a print view
	 * than a component view.
	 *
	 * @since 1.3
	 */
	Graphics getGraphics();

	/**
	 * Fetches the document associated with the view.
	 * @return the drawing, null if none
	 */
	Drawing getDrawing();


	//////////////////////////////////////////////
	/// EVENT HANDLING
	/////////////////////////////////////////////

	/**
	 * Give notification from the model that a change occured for an element this view is responsible
	 * for rendering.
	 */
	void changedUpdate(DrawingEvent.EventType eventType);

	////////////////////////////////////////////////////////
	//// PAINT
	////////////////////////////////////////////////////////

	/**
	 * Ask the hosting container to repaint itself.
	 * @param clip the clip rectangle in model-coordinate
	 */
	void repaint(Rectangle2D clip);

	/**
	 * Render the <code>View</code> of the underlying Element to the given graphic context.
	 * @param allocation the graphic clip
	 */
	void paint(Graphics2D g, Rectangle2D allocation);

	/**
	 * @return the bounds of this <code>View</code>.<br> This will determine the clipping rectangle passed as
	 *         a parameter to repaint.
	 */
	Rectangle2D getBounds();

	//////////////////////////////////////////////////////
	//// HIGHLIGHTER
	//////////////////////////////////////////////////////

	/**
	 * Returns the Highlighter responsible for rendering the highlighted part of this view.
	 * @return null if this view cannot be highlighted
	 */
	Highlighter getHighlighter();

	/**
	 * Sets the <code>Highlighter</code> responsible for rendering the highlighted part of this view.
	 * @param h the delegate ; null if this <code>View</code> mustn't support highlighting.
	 */
	void setHighlighter(Highlighter h);

	/**
	 * Render the <code>Highlighter</code> to the given graphic context.<br>
	 * @param allocation current clipping
	 * @param scale The current scale factor from model to screen for the
	 *        <code>Graphics2D</code> context ; this may be used to scale down line
	 *        thickess, etc&hellip; so that lines/rectangle/&hellip; appear
	 *        with the same lenght on the screen whatever the scale factor
	 *        that's set to the graphic context.
	 */
	void paintHighlighter(Graphics2D g, Rectangle2D allocation, double scale);

	/////////////////////////////////////////////////////
	//// MOUSE
	/////////////////////////////////////////////////////

	/**
	 * Returns a <code>HitInfo</code> corresponding to the given mouse-event
	 * @param e the mouse event for which a <code>HitInfo</code> is returned
	 * @param isHighlightVisible whether the receiver should include the
	 *        highlighter shapes (e.g. end-points) in the click-sensitive
	 *        area.
	 */
	HitInfo hitTest(PEMouseEvent e, boolean isHighlightVisible);

	/**
	 * If this view or its highlighter intersects the given rectangle, add
	 * the associated <code>Element</code> (or a child <code>Element</code> if it's more appropriate, for
	 * instance for composite views)  to the given list.
	 * @param isHighlightVisible whether the receiver should include the
	 *        highlighter shapes (e.g. control-points) in the intersection
	 *        area.
	 * @return whether the given rectangle intersects this view or its
	 *	  highlighter (that is, whether the given list has been modified or
	 *	  not.
	 */
	boolean intersect(Rectangle2D r, boolean isHighlightVisible, ArrayList<Element> list);

}
