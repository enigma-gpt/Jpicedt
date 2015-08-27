// ConvexZoneHalfPlaneView.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneHalfPlaneView.java,v 1.6 2013/03/27 06:55:06 vincentb1 Exp $
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
package jpicedt.graphic.view;

import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.GeneralPath;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.toolkit.ConvexZone;
import jpicedt.graphic.util.ConvexPolygonalZone;
import jpicedt.graphic.util.ConvexPolygonalZone.HalfPlane;

import static jpicedt.graphic.view.ViewConstants.BARBELL_SIZE;

/**
 * La classe <code>ConvexZoneHalfPlaneView</code> permet de visualiser
 * l'ensemble des couples (origine, direction normale) correspondant aux
 * demi-plans délimitant une zone convexe.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @version 1.0
 * @since jPicEdt 1.6
 */
public class ConvexZoneHalfPlaneView{
	boolean isHighlighted;
	ConvexZone convexZone;

	private final double TRIANGLE_H = 1.61803398875; // nombre d'or, hauteur
													 // triangle, à compter de l'origine
	private final double END_POINT_H = 2*TRIANGLE_H;
	private final double PLINTH_HW = 2*TRIANGLE_H; // demi-largeur socle
	private final double PLINTH_HH = 0.25; // demi-hauteur socle
    /// TRIANGLE_TAN_HALF_APERTURE=tan(30°), c'est à dire que le triangle est
    /// équilatéral.
	private final double TRIANGLE_TAN_HALF_APERTURE = 0.57735026919;

	Rectangle2D.Double rectBuffer = new Rectangle2D.Double();
	PicPoint           pt = new PicPoint();
	PicVector          orthoDir = new PicVector();
	ArrayList<GeneralPath>        pathBuffers;

	public ConvexZoneHalfPlaneView(ConvexZone convexZone){
		this.convexZone = convexZone;
		pathBuffers = new ArrayList<GeneralPath>(convexZone.getConvexPolygonalZone().size());
		for(ListIterator<GeneralPath> it = pathBuffers.listIterator(); it.hasNext();){
			it.next();
			it.set(new GeneralPath());
		}
	}

	/** Étend le rectangle <code>allocation</code> de sorte à prendre en compte l'épaisseur
		@param allocation rectangle à étendre.*/
	public static void barbellize(Rectangle2D allocation, double scale){
		double barbellSize = BARBELL_SIZE/scale;
		double x = allocation.getX();
		double y = allocation.getY();
		double w = allocation.getWidth();
		double h = allocation.getHeight();
		allocation.setFrame(x-barbellSize, y-barbellSize, w + 2*barbellSize, h + 2*barbellSize);
	}

	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		//debug("allocation="+allocation);
		//if (!allocation.intersects(getBounds())) return;

		double barbellSize = BARBELL_SIZE/scale;

		Paint curPaint = g.getPaint();
		ListIterator<GeneralPath> it = pathBuffers.listIterator();
		for(ConvexPolygonalZone.HalfPlane hp : convexZone.getConvexPolygonalZone()){

			GeneralPath pathBuffer;
			if(it.hasNext())
			{
				pathBuffer = it.next();
				pathBuffer.reset();
			}
			else
				it.add(pathBuffer = new GeneralPath());


			orthoDir.setCoordinates(hp.getDir());
			orthoDir.iMul();

			// sommet du triangle
			pt.setCoordinates(hp.getOrg());
			pt.translate(hp.getDir(),TRIANGLE_H*barbellSize);
			pathBuffer.moveTo(pt.getX(),pt.getY());

			// coin base à droite du triangle
			pt.setCoordinates(hp.getOrg());
			pt.translate(hp.getDir(),PLINTH_HH*barbellSize);
			pt.translate(orthoDir,((TRIANGLE_H-PLINTH_HH)*TRIANGLE_TAN_HALF_APERTURE)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin supérieur droite du socle
			pt.setCoordinates(hp.getOrg());
			pt.translate(hp.getDir(),PLINTH_HH*barbellSize);
			pt.translate(orthoDir,PLINTH_HW*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin inférieur droite du socle
			pt.translate(hp.getDir(),-(2*PLINTH_HH)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin inférieur gauche du socle
			pt.translate(orthoDir,-(2*PLINTH_HW)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin supérieur gauche du socle
			pt.translate(hp.getDir(),(2*PLINTH_HH)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin base gauche du triangle
			pt.translate(orthoDir,(PLINTH_HW-(TRIANGLE_H-PLINTH_HH)*TRIANGLE_TAN_HALF_APERTURE)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			pathBuffer.closePath();
			g.setPaint(Color.gray);
			g.fill(pathBuffer);

		}
		g.setPaint(curPaint);
	}

	public void paintHighlighter(Graphics2D g, Rectangle2D allocation, double scale){
		double barbellSize = BARBELL_SIZE/scale;

		for(ConvexPolygonalZone.HalfPlane hp : convexZone.getConvexPolygonalZone()){
			g.setPaint(Color.pink);
			pt.setCoordinates(hp.getOrg());
			rectBuffer.setRect(pt.getX() -barbellSize,pt.getY() -barbellSize,
							   2*barbellSize,2*barbellSize);
			g.fill(rectBuffer);
			pt.translate(hp.getDir(),END_POINT_H*barbellSize);
			rectBuffer.setRect(pt.getX() -barbellSize,pt.getY() -barbellSize,
							   2*barbellSize,2*barbellSize);
			g.fill(rectBuffer);
		}
	}
}


/// ConvexZoneHalfPlaneView.java ends here
