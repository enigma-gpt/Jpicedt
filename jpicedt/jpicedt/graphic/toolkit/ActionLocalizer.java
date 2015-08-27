// ActionLocalizer.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
package jpicedt.graphic.toolkit;
import javax.swing.Icon;
import javax.swing.KeyStroke;


/**
 * This interface, once properly implemented, may be used as a parameter for a <code>PEAction</code>, and can
 * feed it with localized properties (e.g. tooltip, mnemonic&hellip;). Concrete implementation may be backed,
 * for instance, by a ResourceBundle initialized from a properties file.<p>
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: ActionLocalizer.java,v 1.11 2013/03/27 07:00:01 vincentb1 Exp $
 */
public interface ActionLocalizer {

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Action name (ie Swing Action.NAME property) for the given name
	 * @param actionName a non-localized action name
	 * @since jPicEdt
	 */
	public String getActionName(String actionName);

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Action tooltip for the given name (ie Swing Action.SHORT_DESCRIPTION)
	 * @since jPicEdt
	 */
	public String getActionTooltip(String actionName);

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Action helper (ie Swing Action.LONG_DESCRIPTION)
	 * @since jPicEdt
	 */
	public String getActionHelper(String actionName);

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Action mnemonic (ie Swing Action.MNEMONIC_KEY)
	 * @since jPicEdt
	 */
	public Integer getActionMnemonic(String actionName);

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Action accelerator keystroke (ie Swing Action.ACCELERATOR_KEY)
	 * @since jPicEdt
	 */
	public KeyStroke getActionAccelerator(String actionName);

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return a localized Icon (ie Swing Action.SMALL_ICON)
	 * @since jPicEdt
	 */
	public Icon getActionIcon(String actionName);

}
