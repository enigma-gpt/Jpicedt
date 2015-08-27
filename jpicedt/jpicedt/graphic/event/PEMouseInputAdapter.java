// PEMouseInputAdapter.java --- -*- coding: iso-8859-1 -*-
// January 3, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PEMouseInputAdapter.java,v 1.7 2013/03/27 07:06:22 vincentb1 Exp $
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
package jpicedt.graphic.event;

/**
 * An abstract adapter class for receiving PEMouseEvent's.
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 * @author Sylvain Reynal
 * @version $Id: PEMouseInputAdapter.java,v 1.7 2013/03/27 07:06:22 vincentb1 Exp $
 *
 * @since jpicedt 1.3.2
 */
public class PEMouseInputAdapter implements PEMouseInputListener {

	public void mousePressed(PEMouseEvent e){}
	public void mouseReleased(PEMouseEvent e){}
	public void mouseClicked(PEMouseEvent e){}
	public void mouseMoved(PEMouseEvent e){}
	public void mouseDragged(PEMouseEvent e){}
	public void mouseEntered(PEMouseEvent e){}
	public void mouseExited(PEMouseEvent e){}
} // class
