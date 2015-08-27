// PEToggleAction.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PEToggleAction.java,v 1.7 2013/03/27 06:56:46 vincentb1 Exp $
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

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import javax.swing.*;
import java.util.*;


/**
 * A subclass of PEAction suited for toggle-able AbstractButton's, e.g. JToggleButton,
 * JCheckBox, JCheckBoxMenuItem and JRadioButton.<br>
 * The main point is that PEMenu and PEToolBar know how to add a PEToggleAction and
 * to create the adequate widget (either JCheckBoxMenuItem or JToggleButton).<br>
 * Concrete implementation of the <code>actionPerformed</method> should use
 * <code>((AbstractButton)e.getSource()).isSelected()</code> to fetch the current widget state.
 * @since jPicEdt
 * @author Sylvain Reynal
 */
public abstract class PEToggleAction extends PEAction {

	/**
	 * @param actionDispatcher the ActionDispatcher that provide a target when this Action gets invoked.
	 * @param actionName the actionName of this Action that serve to build this Action's properties
	 * @param localizer (can be null) the ActionLocalizer that feeds this Action with localized properties ;
	 *        if null, the Action's NAME property is set to actionName, but other
	 *        properties are not set (this allow a subclass to set properties manually).
	 * @since jPicEdt 1.3.2
	 */
	public PEToggleAction(ActionDispatcher actionDispatcher, String actionName, ActionLocalizer localizer){
		super(actionDispatcher,actionName,localizer);
	}
} // class
