// Fragments.java --- -*- coding: iso-8859-1 -*-
// August 11, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: Fragments.java,v 1.7 2013/03/27 06:50:26 vincentb1 Exp $
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
package jpicedt.ui.util;

import jpicedt.JPicEdt;
import jpicedt.ui.dialog.PEFileChooser;
import jpicedt.ui.internal.PEPopupMenuFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


/**
 * Fragments menu management
 * @since jPicEdt
 * @author Sylvain Reynal
 * @version $Id: Fragments.java,v 1.7 2013/03/27 06:50:26 vincentb1 Exp $
 */
public class Fragments  {

	public static JMenu createMenu(){
		return createMenu(null);
	}


	/**
	 * Return a <code>JMenu</code> containing a hierarchy of <code>JMenu</code>'s and <code>JMenuItem</code>'s
	 * built from the fragments contained, first in the installation fragment directory, then in the user
	 * fragment directory.
	 * @return an empty <code>JMenu</code> if no fragments were found in either directory.
	 */
	public static JMenu createMenu(PEPopupMenuFactory.PopupMenu poppedUpFrom){
		// 1�) scan install fragment directory :
		String jpicedtHome = jpicedt.MiscUtilities.getJPicEdtHome();
		JMenu installFragmentsMenu = null;
		if (jpicedtHome!=null) installFragmentsMenu=createMenu(new File(jpicedtHome,"fragments"),poppedUpFrom);
		// 2�) scan user fragment directory :
		JMenu userFragmentsMenu = null;
		if (JPicEdt.getUserSettingsDirectory() != null)
			userFragmentsMenu = createMenu(new File(JPicEdt.getUserSettingsDirectory(),"fragments"),
										   poppedUpFrom);
		// build array :
		JMenu menu = new JMenu("Fragments"); // string not used !
		if (installFragmentsMenu != null) {
			Component[] cc = installFragmentsMenu.getMenuComponents();
			for(int i=0; i<cc.length; i++){
				menu.add(cc[i]);
			}
		}
		menu.add(new JSeparator());
		if (userFragmentsMenu != null) {
			Component[] cc = userFragmentsMenu.getMenuComponents();
			for(int i=0; i<cc.length; i++){
				menu.add(cc[i]);
			}
		}
		return menu;
	}

	/**
	 * Return a <code>JMenu</code> containing a hierarchy of <code>JMenu</code>'s and <code>JMenuItem</code>'s
	 * built from the fragments contained in the given directory and its children.
	 * @param directory path where to look for fragments, or subdirectories.
	 * @return null if directory (or one of its children) doesn't exit, or contains no fragment.
	 */
	private static JMenu createMenu(File directory,PEPopupMenuFactory.PopupMenu poppedUpFrom){
		if (!directory.exists()) return null;
		String[] dirContent = directory.list();
		if (dirContent==null) return null; // directory is empty
		Arrays.sort(dirContent);
		// [pending] sort dir content
		JMenu menu = null; // so that we return null if nothing gets added...
		for (int i=0; i<dirContent.length; i++){
			String fileName = dirContent[i];
			File file = new File(directory, fileName); // create full path name
			// 1�) file is a subdirectory :
			if (file.isDirectory()){
				JMenu subMenu = createMenu(file,poppedUpFrom);
				if (subMenu != null) {
					if (menu==null) menu = new JMenu(directory.getName());
					menu.add(subMenu);
				}
			}
			// 2�) file is a fragment :
			else {
				int indexOfDot = fileName.indexOf(".");
				if (indexOfDot==-1) indexOfDot = fileName.length();
				String label = fileName.substring(0,indexOfDot).replace('_',' ');
				JMenuItem menuItem = new JMenuItem(label);
				menuItem.addActionListener(new JPicEdt.FragmentInsertAction(file.getPath(),poppedUpFrom));
				if (menu==null) menu = new JMenu(directory.getName());
				menu.add(menuItem);
			}
		}
		return menu;
	}

}
