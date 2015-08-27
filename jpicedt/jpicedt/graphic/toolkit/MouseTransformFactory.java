// MouseTransformFactory.java --- -*- coding: iso-8859-1 -*-
// August 23, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: MouseTransformFactory.java,v 1.10 2013/03/27 06:57:16 vincentb1 Exp $
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

import jpicedt.graphic.view.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.*;
import jpicedt.graphic.grid.*;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A factory that produces MouseTransform's to be used by the SelectionTool mouse-tool.
 * @author Sylvain Reynal
 * @since jPicEdt 1.4
 * @version $Id: MouseTransformFactory.java,v 1.10 2013/03/27 06:57:16 vincentb1 Exp $
 */
public interface MouseTransformFactory {

	/**
	 * Return a MouseTransform whose type is adequate with the given mouse-event.
	 * This can be null if no MouseTransform matches the given event.
	 */
	MouseTransform createMouseTransform(PEMouseEvent e);

	/**
	 * Allows the MouseTransformFactory to do specific graphic rendering when it's installed in a
	 * hosting SelectionTool.
	 * @since jpicedt 1.4
	 */
	void paint(Graphics2D g, Rectangle2D allocation, double scale);

	/**
	 * called when the associated SelectionTool is being activated in the hosting EditorKit.
	 * Initialization work required before any mouse-event occurs should be done here.
	 * Other initialization work may be carried out in the MouseTransform's themselves.
	 */
	void init(UserConfirmationCache ucc);

	/** called when the associated SelectionTool is being deactivated in the hosting EditorKit.
	 * This provides a way for the factory to do some final clean-up, e.g. local buffers, graphic context,...
	 */
	void flush();

} // MouseTransformFactory
