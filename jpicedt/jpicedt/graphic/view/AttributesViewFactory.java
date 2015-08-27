/*
 AttributesViewFactory.java - January 15, 2007 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2007 Sylvain Reynal

 Departement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
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

//



/// Code:
package jpicedt.graphic.view;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicAttributeName;

import java.awt.Shape;
import java.awt.Paint;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import static jpicedt.graphic.model.StyleConstants.ArrowStyle;

/**
 * A factory to create objects related to Element's attributes, eg, BasicStroke's or Paint's objets.
 * This may be considered a helper class for View's implementations and their hosting ViewFactory.
 * @since jPicEdt 1.5
 * @author Sylvain Reynal
 * @version $Id: AttributesViewFactory.java,v 1.3 2013/03/27 06:55:11 vincentb1 Exp $
 *
 */
public interface AttributesViewFactory  {

	/**
	 * Returns a Stroke built from the given attributes ; null if LINE_STYLE=NONE.
	 * @param set used to fetch the LINE_STYLE attributes
	 */
	BasicStroke createStroke(PicAttributeSet set);

	/**
	 * Returns a Stroke built from the OVER_STRIKE attribute.
	 * Factory that don't overstrike should implement to return null.
	 * @param set used to fetch the OVER_STRIKE and related attributes
	 */
	BasicStroke createStrokeForOverstrike(PicAttributeSet set);

	/**
	 * Returns a Paint object from the given attributes, that is suited for painting an outline,
	 * or null if LINE_STYLE is NONE.
	 * @param set used to fetch the LINE_COLOR attributes
	 */
	Paint createPaintForOutline(PicAttributeSet set);

	/**
	 * Returns a Paint object suited for rendering shadows.
	 * Factory that don't support shadowing should implement to return null.
	 * @param set used to fetch the SHADOW and related attributes
	 */
	Paint createPaintForShadow(PicAttributeSet set);

	/**
	 * Returns a Paint object from the given attributes, that is suited for painting the interior of a shape.
	 * <p>
	 * The returned object should depend on the FILL_STYLE and FILL_COLOR attributes of the given set.
	 * @param set used to fetch the SHADOW and related attributes
	 */
	Paint createPaintForInterior(PicAttributeSet set);

	/**
	 * paint hatches (vlines,...) depending on the FILL_STYLE attributes of the given set.
	 * Factories that don't paint hatches should implement this method as an empty method.
	 * @param shape used to clip the hatch (ie the shape the calling view must render)
	 */
	void paintHatches(Graphics2D g, PicAttributeSet set, Shape shape);

	/**
	 * Returns a Shape for rendering the shadow of the given Shape,
	 * whose properties are drawn from the SHADOW attribute and related attribs.
	 * Factory that don't paint shadow should implement to return null.
	 * @param shape the Shape under which to drop a shadow
	 * @param set used to fetch the shadow attributes
	 * @return null if no shadow should be painted.
	 */
	Shape createShadow(PicAttributeSet set, Shape shape);

	/**
	 * Returns an ArrowView for rendering arrow-related attributes using the
	 * given attribute set.
	 * @param direction LEFT_ARROW or RIGHT_ARROW
	 */
	ArrowView createArrow(PicAttributeSet set, PicAttributeName<ArrowStyle> direction);

} // interface
