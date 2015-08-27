// ActionLocalizer.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
