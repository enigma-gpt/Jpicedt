// AbstractSelectionHandler.java --- -*- coding: iso-8859-1 -*-
// February 15, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: AbstractSelectionHandler.java,v 1.11 2013/03/27 07:07:37 vincentb1 Exp $
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

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.Drawing;

import java.util.*;

/**
 * Provides some basic implementation of the SelectionHandler interface.
 * This implementation is based on invokations of Collection's methods.
 * No storage mechanism is implemented in this abstract class.
 * Concrete implementation of this mechanism should be based on mutable Java Collection's, otherwise
 * selection operation will fail.
 * <br>
 * Finally, the content of the selection-handler is supposed to reflect the z-ordering of the
 * {@link jpicedt.graphic.model.Drawing Drawing} it is
 * associated with. This abstract implementation does not take care of this.
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: AbstractSelectionHandler.java,v 1.11 2013/03/27 07:07:37 vincentb1 Exp $
 */
public abstract class AbstractSelectionHandler implements SelectionHandler {

	/**
	 * Return the selected elements wrapped in an array (may be a convenience call to asCollection)
	 */
	 public Element[] asArray(){
		 return toArray(new Element[0]);
	 }

	/**
	 * Select all Element's in the given drawing.
	 */
	public void selectAll(Drawing d){
		clear(); // avoid duplicate
		addAll(d);
	}

	/**
	 * Replace the current selection with the given element.
	 */
	public void replace(Element e){
		clear();
		add(e);
	}

	/**
	 * Delete all selected Element's from the given Drawing, and remove the reference to them
	 * from this SelectionHandler.
	 */
	public void delete(Drawing d){
		for (Iterator<Element> it = iterator(); it.hasNext();){ // iterate over selected elements
			Element e = it.next();
			it.remove(); // first remove element from selection-handler to avoid concurent modification (see note below)
			d.remove(e); // then remove it from drawing => fires REMOVE DrawingEvent => calls changedUpdate => *might* raise concurrent modification exception if not taken care of properly
		}
	}
}
