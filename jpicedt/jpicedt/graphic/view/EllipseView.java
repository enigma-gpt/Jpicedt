// EllipseView.java --- -*- coding: iso-8859-1 -*-
// July 19, 2006 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
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
// Version: $Id: EllipseView.java,v 1.10 2013/03/27 06:54:56 vincentb1 Exp $
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
package jpicedt.graphic.view;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.ContentType;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.lang.reflect.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;
import static java.lang.Math.abs;

/**
 * a View for rendering ellipses defined by a parallelogram
 */
public class EllipseView extends LeafElementView {

	/**
	 * construct a new View for the given ellipse
	 */
	public EllipseView(PicEllipse ellipse, AttributesViewFactory f){
		super(ellipse,f);
		changedUpdate(null);
	}

	/**
	 * Synchronizes arrows' specific attributes with the model. Called from syncAttributes().
	 * This implementation creates arrows only if the model is an open arc.
	 */
	protected void syncArrowsAttributes(){
		//ellipse or closed arc or pie:
		if (((PicEllipse)element).isClosed())
			leftArrow = rightArrow = null;
		// OPEN arc:
		else {
			super.syncArrowsAttributes();
		}
	}

} // EllipseView
