// DefaultDialogFactory.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: DefaultDialogFactory.java,v 1.6 2013/03/27 06:58:36 vincentb1 Exp $
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

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import jpicedt.JPicEdt;
import jpicedt.Localizer;
import jpicedt.ui.dialog.YesNoAskMe;
import jpicedt.widgets.MDIComponent;
import jpicedt.widgets.PEDialog;

import static jpicedt.Localizer.localize;

/**
 * Provides functionalities to show a dialog box in a GUI.
 */
public class DefaultDialogFactory implements DialogFactory {

	private Frame root;

	public DefaultDialogFactory(Frame root){
		this.root = root;
	}

	public void setRootFrame(Frame root){
		this.root = root;
	}

	/**
	 * Wraps the given component in a dialog box appropriate for the current GUI.
	 */
	 public MDIComponent createDialog(String title, boolean modal, JComponent p){
		 PEDialog jd = new PEDialog(root, title, modal, p);
		 jd.pack();
		 return jd;
	 }

	/**
	 * Creates a new dialog box from the given array of <code>AbstractCustomizer</code>'s, laying them out in
	 * a <code>JTabbedPane</code>.<br>
	 * By default, clicking the OK button does not close this dialog box, this must be set separately by
	 * invoking {@link jpicedt.graphic.toolkit.CustomizerDialog#setOkButtonClosesDialog(boolean b) setOkButtonClosesDialog}.
	 * @param title the dialog title ; if null, the title of the first customizer is used.
	 * @param selected index of the selected customizer on start-up
	 * @param buttonsMask buttons to be displayed : a mask computed from predefinite masks OR'd together
	 */
	 public CustomizerDialog createCustomizerDialog(ArrayList<AbstractCustomizer> customizers, int selected,
													String title, boolean modal,
													EnumSet<CustomizerDialog.ButtonMask> buttonsMask){
		 PEDialog host = new PEDialog(root, title, modal, null); // no inner pane
		 return new CustomizerDialog(host,customizers, selected,title, buttonsMask);
	 }

	/**
	 * Builds a new dialog box from a single customizer.
	 */
	 public CustomizerDialog createCustomizerDialog(AbstractCustomizer customizer, boolean modal, EnumSet<CustomizerDialog.ButtonMask> buttonsMask){
		 PEDialog host = new PEDialog(root, "", modal, null); // no title, no inner pane
		 return new CustomizerDialog(host, customizer, buttonsMask);
	 }

	 public void showMessageDialog(Object message, String title, int messageType){
		 JOptionPane.showMessageDialog(root, message, title, messageType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType){
		return JOptionPane.showConfirmDialog(root, message, title, optionType);
	 }

	 public int showConfirmDialog(Object message, String title, int optionType, int msgType){
		return JOptionPane.showConfirmDialog(root, message, title, optionType, msgType);
	 }

	 public String showInputDialog(Object message, String title, int messageType){
	 	return JOptionPane.showInputDialog(root, message, title, messageType);
	 }

	 public String showInputDialog(Object message, String title, int messageType, String initialValue){
	 	return (String)JOptionPane.showInputDialog(root, message, title, messageType,null, null, initialValue);
	 }

	public Object showInputDialog(Object message,String title,int messageType,Object[] choices,
								  Object initialChoice){
		return JOptionPane.showInputDialog(root,message,title, messageType, null, choices, initialChoice);
	}

	public int showDontAskMeAgainConfirmDialog(String message,String title,String dontAskMeAgainKey, int messageType){
		YesNoAskMe prop = JPicEdt.getProperty(dontAskMeAgainKey, YesNoAskMe.ASK_ME);
		if(prop == YesNoAskMe.ASK_ME){
			JCheckBox dontAskMeAgainButton= new JCheckBox(localize("button.dontAskMeAgain"));
			Object[] jOptionPaneMessage = { message, dontAskMeAgainButton};
			int ret = showConfirmDialog(jOptionPaneMessage, title,
									 JOptionPane.YES_NO_OPTION, messageType);
			if(dontAskMeAgainButton.isSelected()){
				prop = YesNoAskMe.jOptionPaneValueToYesNoAskMe(ret);
				JPicEdt.setProperty(dontAskMeAgainKey, prop);
			}
			return ret;
		}
		else
			return prop.getJOptionPaneValue();
	}

}
