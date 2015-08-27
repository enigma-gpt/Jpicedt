// AbstractCurveHighlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: AbstractCurveHighlighter.java,v 1.5 2013/03/27 06:54:51 vincentb1 Exp $
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

package jpicedt.graphic.view.highlighter;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

import static jpicedt.graphic.view.ViewConstants.*;


/**
 * A <code>Highlighter</code> for any concrete implementation of
 * <code>jpicedt.graphic.model.AbstractCurve</code>.  This comprises tangents for non-straight segments, and
 * is wrapped into a <code>GeneralPath</code>.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: AbstractCurveHighlighter.java,v 1.5 2013/03/27 06:54:51 vincentb1 Exp $
 */
public class AbstractCurveHighlighter extends DefaultHighlighter {

	/**
	 * construct a new Highlighter for the given curve
	 */
	public AbstractCurveHighlighter(AbstractCurve curve,DefaultHighlighterFactory f){
		super(curve,f);
	}

	public AbstractCurve getElement(){
		return (AbstractCurve)element;
	}

	/**
	 * Synchronizes the highlighter's tangents with the model.
	 */
	protected void syncShape(double scale){

		if (shape==null) shape = new GeneralPath();
		GeneralPath path = (GeneralPath)shape;			// compute number of non-straight segments
		path.reset();

		AbstractCurve curve  = getElement();
		PicPoint pt = new PicPoint();
		for (int i=0; i<curve.getSegmentCount();i=i+1){
			if (!curve.isStraight(i)){
				pt = curve.getBezierPt(3*i, pt);
				path.moveTo((float)pt.x, (float)pt.y);
				pt = curve.getBezierPt(3*i+1, pt);
				path.lineTo((float)pt.x, (float)pt.y);
				pt = curve.getBezierPt(3*i+2, pt);
				path.moveTo((float)pt.x, (float)pt.y);
				pt = curve.getBezierPt(3*i+3, pt);
				path.lineTo((float)pt.x, (float)pt.y);
			}
		}
	}


	/**
	 * Render the Highlighter to the given graphic context.<br>
	 * Current implementation paints end-points using the highlighter color if the given
	 * allocation intersects the bounds of this view, then renders tangents,
	 * finally paint first subdivision-point in black to help user know where closed-curve
	 * start exactly.
	 * @param scale The current scale factor from model to screen for the Graphics2D context ;
	 *        this may be used to scale down line thickess, etc... so that lines/rectangle/... appear with the
	 *        same length on the screen whatever the scale factor that's set to the graphic context.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		super.paint(g, allocation, scale);

		// paint first control-point to help user know where curve start (useful for closed curves)
		double barbellSize = BARBELL_SIZE/scale/2.0;
		PicPoint pt = getElement().getCtrlPt(0,ptBuffer);
		rectBuffer.setRect(pt.x-barbellSize,pt.y-barbellSize,2*barbellSize,2*barbellSize);
		g.setPaint(Color.black);
		g.fill(rectBuffer);
	}

} // AbstractCurveHighlighter
