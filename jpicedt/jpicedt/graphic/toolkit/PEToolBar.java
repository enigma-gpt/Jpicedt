/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: PEToolBar.java,v 1.8 2013/03/27 06:56:41 vincentb1 Exp $
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

package jpicedt.graphic.toolkit;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.net.*;

/**
 * A subclass of JToolBar that knows how to add PEAction's.  the abstract superclass for jPicEdt toolbars ;
 * some methods have been added to/overriden from JToolBar, such as :<br>
 * <ul>
 * <li> creating JToggleButton from an Action
 * <li> adding a Button from an Action, taking i18n res file into account
 * </ul>
 * @author Sylvain Reynal
 * @since PicEdt 1.0
*/
public class PEToolBar extends JToolBar {

	/** buttons margin */
	//protected static final Insets jButtonMargin = new Insets(2,2,2,2); // top,left,bottom,right

	/**
	 * simply calls super()
	 */
	public PEToolBar(){
		super();
	}

	/**
	 * Creates a JButton from the given Action properties
	 * @param a Action from which this button gets created.
	 */
	public JButton add(Action a){
		JButton b = super.add(a);
		b.setMargin(new Insets(1,1,1,1));
		//b.setRolloverEnabled(true);
		//b.setBorderPainted(false); // [pending]
		b.setAlignmentY(CENTER_ALIGNMENT);
		b.setAlignmentX(CENTER_ALIGNMENT);
		return b;
	}

	/**
	 * Creates a JToggleButton from the given PEToggleAction's properties
	 * @param a Action from which this button gets created.
	 */
	public JToggleButton add(PEToggleAction a){

		JToggleButton b = new JToggleButton(a);
		if (b.getIcon()!=null) b.setText(null);
		b.setMargin(new Insets(2,2,2,2));
		//b.setAlignmentY(CENTER_ALIGNMENT);
		//b.setAlignmentX(CENTER_ALIGNMENT);
		b.setRolloverEnabled(true);
        add(b);
        return b;
	}
} // class PEToolBar
