// EditPointConstraint.java --- -*- coding: iso-8859-1 -*-
// August 19, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal, 2011 Vincent Bela�che
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
// Version: $Id: EditPointConstraint.java,v 1.11 2013/03/27 07:02:44 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.ui.dialog.UserConfirmationCache;

/**
 * Interface pour passer des contraintes � la m�thode <code>Element.setCtrlPt()</code>. Jusqu'� pr�sent �a ne
 * sert qu'� convoyer un <code>UserConfirmationCache</code>, mais cela pourrait �tre �tendu dans le futur.
 * @since jPicEdt 1.4
 * @author <a href="mailto:reynal@ensea.fr">Sylvain Reynal</a>,
 *         <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @version $Id: EditPointConstraint.java,v 1.11 2013/03/27 07:02:44 vincentb1 Exp $
 */
public interface EditPointConstraint  {

	/** �num�r� de contraint pour <code>setCtrlPt()</code>  */
	public enum EditConstraint {
		// pour parall�logramme
		DEFAULT, SQUARE, CENTER_FIXED,
			// pour cha�ne de courbe de B�zier
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
	 * quant aux transformation non conformes. Par exemple si on d�place un point de contr�le d'un groupe et
	 * que ce groupe contient un <code>PicCircleFrom3Points</code>, faut il convertir ce cercle en
	 * <code>PicEllipse</code> pour pouvoir l'applatir, ou bien faut-il d�placer seulement ses trois points de
	 * contr�le de <code>PicCircleFrom3Points</code> et le transformer ainsi.
	 *
	 * @return le <code>UserConfirmationCache</code> donnant le contexte des voeux de l'utilisateur notamment
	 * quant aux transformation non conformes.
	 * @since jPicEdt 1.6
	 */
	UserConfirmationCache getUserConfirmationCache();

}
