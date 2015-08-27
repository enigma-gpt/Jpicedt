// DialogFactory.java --- -*- coding: iso-8859-1 -*-
// July 26, 2006 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: DialogFactory.java,v 1.9 2013/03/27 06:58:21 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import jpicedt.widgets.MDIComponent;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Provides functionalities to show a dialog box in a GUI.
 */
public interface DialogFactory {


	/**
	 * Wraps the given component in a dialog box appropriate for the current GUI.
	 */
	MDIComponent createDialog(String title, boolean modal, JComponent innerPane);

	/**
	 * Creates a new dialog box from the given array of <code>AbstractCustomizer</code>'s, laying them out in
	 * a <code>JTabbedPane</code>.<br> By default, clicking the OK button does not close this dialog box, this
	 * must be set separately by invoking {@link
	 * jpicedt.graphic.toolkit.CustomizerDialog#setOkButtonClosesDialog setOkButtonClosesDialog}.
	 * @param title the dialog title ; if <code>null</code>, the title of the first customizer is used.
	 * @param selected index of the selected customizer on start-up
	 * @param buttonMask buttons to be displayed : a mask computed from predefinite masks OR'd together
	 */
	CustomizerDialog createCustomizerDialog(ArrayList<AbstractCustomizer> customizers, int selected, String title, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonMask);

	/**
	 * Builds a new dialog box from a single customizer.
	 */
	CustomizerDialog createCustomizerDialog(AbstractCustomizer customizer, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonMask);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	void showMessageDialog(Object message, String title, int messageType);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	int showConfirmDialog(Object message, String title, int optionType);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	int showConfirmDialog(Object message, String title, int optionType, int messageType);

	/**
	 * Describe <code>showDontAskMeAgainConfirmDialog</code> method here.
	 *
	 * @param message Un message textuel.
	 * @param title voir  {@link javax.swing#JOptionPane}
	 * @param dontAskMeAgainKey une clef de propriété de type {@link jpicedt.ui.dialog#YesNoAskMe}.
	 * @param messageType voir  javax.swing.JOptionPane.
	 * @return voir {@link javax.swing#JOptionPane}, renvoie <code>JOptionPane.YES_OPTION</code> ou
	 * <code>JOptionPane.NO_OPTION</code>.
	 * @since jPicEdt 1.6
	 */
	int showDontAskMeAgainConfirmDialog(String message,String title,String dontAskMeAgainKey, int messageType);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	String showInputDialog(Object message, String title, int messageType);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	String showInputDialog(Object message, String title, int messageType, String initialValue);

	/**
	 * @see javax.swing.JOptionPane.
	 */
	Object showInputDialog(Object message, String title, int messageType,
						   Object[] choices, Object initialChoice);

}
