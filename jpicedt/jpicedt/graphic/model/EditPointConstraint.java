// EditPointConstraint.java --- -*- coding: iso-8859-1 -*-
// August 19, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal, 2011 Vincent Belaïche
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
// Version: $Id: EditPointConstraint.java,v 1.11 2013/03/27 07:02:44 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.ui.dialog.UserConfirmationCache;

/**
 * Interface pour passer des contraintes à la méthode <code>Element.setCtrlPt()</code>. Jusqu'à présent ça ne
 * sert qu'à convoyer un <code>UserConfirmationCache</code>, mais cela pourrait être étendu dans le futur.
 * @since jPicEdt 1.4
 * @author <a href="mailto:reynal@ensea.fr">Sylvain Reynal</a>,
 *         <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @version $Id: EditPointConstraint.java,v 1.11 2013/03/27 07:02:44 vincentb1 Exp $
 */
public interface EditPointConstraint  {

	/** Énuméré de contraint pour <code>setCtrlPt()</code>  */
	public enum EditConstraint {
		// pour parallélogramme
		DEFAULT, SQUARE, CENTER_FIXED,
			// pour chaîne de courbe de Bézier
			SMOOTHNESS_SYMMETRY, SMOOTHNESS, SYMMETRY, FREELY;
	}

	EditConstraint getEditConstraint();

	///**
	// * Returns true if this constraint imposes the given constraint. This is useful for constraints which
	// * may contain several AND'd constraints at once.
	// */
	//boolean imposes(EditPointConstraint c);

	/**
	 * Renvoie un contexte <code>UserConfirmationCache</code> donnant les voeux de l'utilisateur notamment
	 * quant aux transformation non conformes. Par exemple si on déplace un point de contrôle d'un groupe et
	 * que ce groupe contient un <code>PicCircleFrom3Points</code>, faut il convertir ce cercle en
	 * <code>PicEllipse</code> pour pouvoir l'applatir, ou bien faut-il déplacer seulement ses trois points de
	 * contrôle de <code>PicCircleFrom3Points</code> et le transformer ainsi.
	 *
	 * @return le <code>UserConfirmationCache</code> donnant le contexte des voeux de l'utilisateur notamment
	 * quant aux transformation non conformes.
	 * @since jPicEdt 1.6
	 */
	UserConfirmationCache getUserConfirmationCache();

}
