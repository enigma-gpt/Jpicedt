// PEMouseInputListener.java --- -*- coding: iso-8859-1 -*-
// January 3, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PEMouseInputListener.java,v 1.7 2013/03/27 07:06:17 vincentb1 Exp $
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
package jpicedt.graphic.event;

import java.util.EventListener;

/**
 * The listener for receiving mouse and mouse motion events. In comparison with
 * {@link javax.swing.event.MouseInputListener javax.swing.event.MouseInputListener},
 * the benefit of using this listener is that MouseEvent's are translated into PEMouseEvent's, and the latter
 * contain enhanced data e.g. model-coordinates in addition to usual screen-coordinates.
 * @author Sylvain Reynal
 * @version $Id: PEMouseInputListener.java,v 1.7 2013/03/27 07:06:17 vincentb1 Exp $
 * @see jpicedt.graphic.event.PEMouseEvent
 * @since jpicedt 1.3.2
 */
public interface PEMouseInputListener extends EventListener {

	/** Invoked when a mouse button has been pressed on a component. */
	public void mousePressed(PEMouseEvent e);

	/** Invoked when a mouse button has been released on a component. */
	public void mouseReleased(PEMouseEvent e);

	/** Invoked when the mouse button has been clicked (pressed and released) on a component. */
	public void mouseClicked(PEMouseEvent e);

	/**  Invoked when the mouse button has been moved on a component (with no buttons down). */
	public void mouseMoved(PEMouseEvent e);

	/** Invoked when a mouse button is pressed on a component and then dragged. */
	public void mouseDragged(PEMouseEvent e);

	/** Invoked when the mouse enters a component. */
	public void mouseEntered(PEMouseEvent e);

	/** nvoked when the mouse exits a component. */
	public void mouseExited(PEMouseEvent e);

} // interface
