/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
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
