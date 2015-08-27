// PEComboBox.java --- -*- coding: iso-8859-1 -*-
// 2006 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: PEComboBox.java,v 1.3 2013/03/27 06:49:11 vincentb1 Exp $
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
package jpicedt.widgets;

import javax.swing.*;
import java.util.Map;
import java.util.Vector;

/**
 * An generic'ified version of JComboBox that can be initialized from a Map. This is
 * especially useful when itializing JComboBox's with ImageIcon's, when one wants to
 * perform a one-to-one mapping b/w an Object being relevant for the caller (like a member
 * of an enum, and an array of ImageIcon's.
 * K represents the typevariable to be mapped to selectable items in this combobox.
 */
 public class PEComboBox<K> extends JComboBox {

	 private Map<K,?> map; // maps relevant Object's to ImageIcon, Strings,... that are displayed in the JComboBox

	 /**
	  * Constructs a JComboBox from the given EnumMap that maps
	  * enum members to JComboBox items, e.g., String's, ImageIcon's, ...
	  */
	  public <V> PEComboBox(Map<K,V> map){
		  super(new Vector<V>(map.values())); // guaranteed to have elements sorted in the natural order of the enum
		  this.map = map;
	  }

	  /**
	   * Returns the enum member associated with the currently selected item
	   */
	   public K getSelectedKey(){
		   Object selectedItem = getSelectedItem();
		   for (K key: map.keySet()){ // for every member of the enum...
			   if (map.get(key)==selectedItem)
				   return key;
		   }
		   return null;
	   }

	   public void setSelectedKey(K key){
		   Object selectedItem = map.get(key);
		   setSelectedItem(selectedItem);
	   }



 }
