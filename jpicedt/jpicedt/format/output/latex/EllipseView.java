// EllipseView.java --- -*- coding: iso-8859-1 -*-
// February 9, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: EllipseView.java,v 1.5 2013/03/27 07:10:10 vincentb1 Exp $
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
package jpicedt.format.output.latex;

import jpicedt.format.output.eepic.EepicViewFactory;
import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * view for ellipse specific to the <i>LaTeX picture environment</i>.
 * Only small circles can be filled.
 * [pending] bug : only OPEN arcs !
 */
public class EllipseView extends jpicedt.graphic.view.EllipseView {

	static double maxLatexDiskDiameter = DEFAULT_MAX_DISK_DIAMETER;

	public EllipseView(PicEllipse ell, LatexViewFactory f){
		super(ell,f);
	}

	/**
	 * Give notification from the model that a change occured for an element this view is responsible
	 * for rendering.<p>
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		//if geometry changed from ellipse to circle or vice-versa, and if radius becomes less or
		//equal to maxLatexDiskDiameter,  we need to *force*
		//update of interior paint attribute ; otherwise, if attribute changed,
		//no need to worry, for this is done automatically by superclass :
		if (eventType!=DrawingEvent.EventType.ATTRIBUTE_CHANGE) syncAttributes();
		super.changedUpdate(eventType); // super calls syncAttribute if ATTRIBUTE_CHANGE, it's ok.
	}

	/**
	 * Inherits from superclass, except for elliptic arcs, which are never filled.
	 */
	protected void syncAttributes(){
		super.syncAttributes(); // init cached values from default EllipseView
		outlineStroke = new BasicStroke(element.getAttribute(LINE_WIDTH).floatValue()); // dont' dash or dot
		//small disks :
		//we call getAngleExtent() instead of using variable "angleExtent" because changedUpdate()
		//(which updates 'angleExtent') is called after syncAttributes !
		PicEllipse ellipse = (PicEllipse)element;
		if (ellipse.isPlain() && ellipse.isCircular() && ellipse.getGreatAxisLength() <= maxLatexDiskDiameter) return;
		else interiorPaint = null; // forbids filling anything else
		//never paint arrows :
		// arrowStart = arrowEnd = null; // this is handled now by formatter as of 1.4
	}
}
