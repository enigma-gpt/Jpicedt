// PopupMenuFactory.java --- -*- coding: iso-8859-1 -*-
// January 4, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PopupMenuFactory.java,v 1.10 2013/03/27 06:56:36 vincentb1 Exp $
// Keywords: main
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
package jpicedt.graphic.toolkit;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.view.HitInfo;

import javax.swing.*;

/**
 * A factory used to create a JPopupMenu for an EditorKit.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: PopupMenuFactory.java,v 1.10 2013/03/27 06:56:36 vincentb1 Exp $
 * <p>
 */
public interface PopupMenuFactory {

	/**
	 * @param board the popup-menu invoker component
	 * @param hi the HitInfo obtained from the mouse-event which triggered the popup-menu ; the general
	 *           contract is to scan the whole drawing, that is, not the selection only.  If no
	 *           <code>Element</code> was found under the cursor, use {@link JPopupMenu
	 *           createPopupMenu(PECanvas board, PEMouseEvent e)}.
	 */
	public JPopupMenu createPopupMenu(PECanvas board, HitInfo hi);

	/**
	 * Méthode à appeler lorsque aucun élément ou aucune zone convexe ne sont touchés.
	 * @param board la planche ayant invoqué la création du menu contextuel
	 * @param e l'événement souris correspondant au clic.
	 * @since jPicEdt 1.6
	 */
	public JPopupMenu createPopupMenu(PECanvas board, PEMouseEvent e);

	public JPopupMenu createPopupMenu(PECanvas board, ConvexZoneHitInfo hi);
}
