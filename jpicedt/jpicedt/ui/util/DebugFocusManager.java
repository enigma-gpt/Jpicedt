// DebugFocusManager.java --- -*- coding: iso-8859-1 -*-
// January 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: DebugFocusManager.java,v 1.7 2013/03/27 06:50:36 vincentb1 Exp $
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
package jpicedt.ui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A subclass of Swing's DefaultFocusManager that aims at helping us debug FocusEvent related bugs<p>
 * To set this class as Swing's FocusManager, just say :<br>
 * <code>FocusManager.setCurrentManager(an_instance_of_DebugFocusManager);</code>
 *
 * @since jPicEdt
 * @author Sylvain Reynal
 */
public class DebugFocusManager extends DefaultFocusManager  {


	/**
	 * Cause the focus manager to set the focus on the next focusable component<p>
	 * Overriden so as to display information about the component.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void focusNextComponent(Component c){

		System.out.println("DebugFocusManager.focusNextComponent : " + c);
		super.focusNextComponent(c);
	}

	/**
	 * Cause the focus manager to set the focus on the previous focusable component<p>
	 * Overriden so as to display information about the component.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void focusPreviousComponent(Component c){

		System.out.println("DebugFocusManager.focusPreviousComponent : " + c);
		super.focusPreviousComponent(c);
	}

	/**
	 * Called by a JComponent when a key event occus.<p>
	 * Overriden so as to display information about the component.
	 * <br><b>author:</b> Sylvain Reynal
	 * @param c the focused component
	 * @param ke the key event
	 * @since jPicEdt
	 */
	public void processKeyEvent(Component c, KeyEvent ke){

		System.out.println("DebugFocusManager.processKeyEvent : " + ke + " on component " + c);
		super.processKeyEvent(c,ke);
	}


} // class
