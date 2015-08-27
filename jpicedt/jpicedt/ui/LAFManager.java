// LAFManager.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: LAFManager.java,v 1.13 2013/03/27 06:50:56 vincentb1 Exp $
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
package jpicedt.ui;

import jpicedt.JPicEdt;

import javax.swing.*;
import java.util.*;

//import com.l2fprod.gui.plaf.skin.*; // doesn't work at the moment; needs UIViewport to work properly

/**
 * LAF management : maintains a list of installed LAF, and handles LAF loading.
 */
public class LAFManager {

	/** key used to fetch the LAF name from the Properties (see init) */
	public static final String KEY_LAF = "ui.look-and-feel";

	/**
	 * @return a string representing current LAF name
	 */
	public static String getCurrentLafName(){
		//return UIManager.getLookAndFeel().getName();
		String s = UIManager.getLookAndFeel().getName();
		if (s.startsWith("Mac OS X")) // bug fix: s="Mac OS X Aqua", while "Mac OS X" is expected since this the value returned by getInstalledLookAndFeels()
			return "Mac OS X";
		else
			return s;
	}

	/**
	 * @return a string representing the default LAF (ie "Metal LAF") name
	 */
	public static String getDefaultLafName(){
		String defaultLafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		for (int i=0; i<lafInfo.length; i++){
			if (lafInfo[i].getClassName().equals(defaultLafClassName))
				return lafInfo[i].getName();
		}
		return " ";
	}

	/**
	 * @return a list of installed LAF names (as returned by LookAndFeelInfo.getName())
	 *         suitable for a menu or JComboBox, e.g. "Metal,CDE/Motif,Windows"
	 */
	public static String[] getInstalledLookAndFeelsNames(){
		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		String[] lafNames = new String[lafInfo.length];
		for (int i=0; i<lafNames.length; i++){
			lafNames[i] = lafInfo[i].getName();
		}
		return lafNames;
	}

	/**
	 * Init LAF from the given Properties using key=KEY_LAF and value = LAF name (as returned
	 * by LookAndFeelInfo.getName()).
	 */
	public static void updateLaf(){
		String lafName = JPicEdt.getPreferences().getProperty(KEY_LAF);
		if (lafName != null) updateLaf(lafName);
		// else updateLafFromClassName(UIManager.getCrossPlatformLookAndFeelClassName());
		// useless since "Metal" is the default
	}

	/**
	 * set current LAF from the given LAF name
	 * @param lafName one of the predefined LAF names, e.g. "CDE/Motif" or "Metal"
	 */
	public static void updateLaf(String lafName){
		//debug(new LAFManager(),"updateLaf","lafName="+lafName);
		if (lafName.equals(getCurrentLafName())) return;  // unchanged
		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		for (int i=0; i<lafInfo.length; i++){
			if (lafName.equals(lafInfo[i].getName())){
				updateLafFromClassName(lafInfo[i].getClassName());
				return;
			}
		}
		//debug(new LAFManager(),"updateLaf","no match !");
	}

	/**
	 * Set the LAF from the given class name.
	 */
	public static void updateLafFromClassName(String className) {
		//debug(new LAFManager(),"updateLafFromClassName","className="+className);
		try  {
			UIManager.setLookAndFeel(className);
		}
		catch ( UnsupportedLookAndFeelException ue ) {
			System.err.println("An error occured during Look'n Feel loading: this Look'n Feel is probably not supported on this platform... (this may occur due to License Agreement limitation)"
			                   + "\nIf you are convinced this isn't the case, then please send a bug report including the following stack trace to reynal@ensea.fr"
			                   + "\nStack trace dump starts here :");
			ue.printStackTrace(); // to System.err
			System.err.println("Stack trace ends here");
			JPicEdt.getMDIManager().showMessageDialog(
			                              ue.getMessage(), // [todo] add a localized error description
			                              jpicedt.Localizer.currentLocalizer().get("LookAndFeel"),
			                              JOptionPane.ERROR_MESSAGE);
		}
		catch ( Exception ue ) {
			System.err.println("An error occured during Look'n Feel loading: classes for this Look'n Feel weren't found..."
			                   + "\nIf you are convinced this isn't the case, then please send a bug report including the following stack trace to reynal@ensea.fr"
			                   + "\nStack trace dump starts here :");
			ue.printStackTrace(); // to System.err
			System.err.println("Stack trace ends here");
			JPicEdt.getMDIManager().showMessageDialog(
			                              ue.getMessage(), // [todo] add a localized error description
			                              jpicedt.Localizer.currentLocalizer().get("LookAndFeel"),
			                              JOptionPane.ERROR_MESSAGE);
		}

	}

} // LAFManager
