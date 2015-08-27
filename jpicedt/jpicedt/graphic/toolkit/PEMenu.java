// PEMenu.java --- -*- coding: iso-8859-1 -*-
// January 1, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PEMenu.java,v 1.10 2013/03/27 06:56:51 vincentb1 Exp $
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

import javax.swing.*;

/**
 * This is a subclass of <code>JMenu</code> that allows to add Swing <code>Action</code>'s using the
 * <code>ACCELERATOR_KEY</code> property (contrary to the <code>JMenu</code> implementation).
 */
public class PEMenu extends JMenu {

	/** create a <code>PEMenu</code> from the given <code>Action</code> ; this is merely used to fetch the
	 * menu mnemonic from the given <code>Action</code>'s <code>MNEMONIC_KEY</code> value*/
	public PEMenu(Action a){
		super(a);
		setMnemonic(((Integer)a.getValue(Action.MNEMONIC_KEY)).intValue());
	}

	/** create a <code>PEMenu</code> from the given string, w/o mnemonic */
	public PEMenu(String s){
		super(s);
	}
	/**
	 * set menuitem accelerator to a.getValue(ACCELERATOR_KEY)
	 * @param a Action from which a <code>JMenuItem</code> gets created.
	 */
	public JMenuItem add(Action a) {
		//debug("action="+a);
		if (a instanceof PEToggleAction){
			return add((PEToggleAction)a);
		}
		JMenuItem mi = super.add(a);
		KeyStroke ks =  (KeyStroke)a.getValue(Action.ACCELERATOR_KEY);
		if (ks != null) mi.setAccelerator(ks);
		//mi.setIcon(null);
		return mi;
	}

	/**
	 * set menuitem accelerator to a.getValue(ACCELERATOR_KEY)
	 * @param a Action from which a JMenuItem gets created.
	 */
	public JCheckBoxMenuItem add(PEToggleAction a) {
		//debug("action="+a);

		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(a);
		KeyStroke ks =  (KeyStroke)a.getValue(Action.ACCELERATOR_KEY);
		if (ks != null) cbmi.setAccelerator(ks);
		//cbmi.setIcon(null);
		add(cbmi);
		return cbmi;
	}

} // class
