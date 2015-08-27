// SelectionHandler.java --- -*- coding: iso-8859-1 -*-
// February 15, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: SelectionHandler.java,v 1.15 2013/03/27 07:00:33 vincentb1 Exp $
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
package jpicedt.graphic;

import jpicedt.graphic.model.CtrlPtSubset;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.toolkit.ConvexZoneGroup;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

/**
 * a SelectionHandler allows to manage selection-related behaviours for a given instance of
 * {@link jpicedt.graphic.model.Drawing Drawing}.
 * Concrete implementation may generally want to store references
 * on selected {@link jpicedt.graphic.model.Element Element's} here. This may be easily carried out
 * by relying on Java's collection framework, hence we have specified some useful methods here for
 * this purpose.
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: SelectionHandler.java,v 1.15 2013/03/27 07:00:33 vincentb1 Exp $
 */
public interface SelectionHandler extends Collection<Element> {

	/**
	 * Return the selected elements wrapped in an array (may be a convenience call to asCollection)
	 */
	Element[] asArray();

	/**
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @since jPicEdt 1.6
	 */
	CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension);

	/**
	 * Select all Element's belonging to the given Drawing.
	 */
	void selectAll(Drawing d);

	/**
	 * Replace the current selection with the given element.
	 */
	void replace(Element e);


	/**
	 * Remplace l'�l�ment <code>oldE</code> par l'�l�ment <code>newE</code> lorsque <code>oldE</code>
	 * appartient � la s�lection. Sans effet si <code>newE</code> est d�j� dans la s�lection.
	 *
	 * L'ordre dans la collection n'est pas conserv�.
	 *
	 * @param oldE l'<code>Element</code> � remplacer.
	 * @param newE l'<code>Element</code> qui sert de remplacement.
	 */
	void replace(Element oldE,Element newE);

	/**
	 * Delete all selected Element's from the given Drawing
	 */
	void delete(Drawing d);

	/**
	 * Returns a list containing elements in the selection that are of the same type or
	 * inherit the given clazz.
	 */
	<T extends Element> ArrayList<T> createFilteredCollection(Class<T> clazz);

	/**
	 * Returns whether this selection-handler contains objects that are of the same type of inherit from the
	 * given clazz.
	 * @since jpicedt 1.4pre5
	 */
	boolean containsClass(Class<? extends Element> clazz);


}
