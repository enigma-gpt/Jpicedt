// ConvexZoneHalfPlaneView.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneHalfPlaneView.java,v 1.6 2013/03/27 06:55:06 vincentb1 Exp $
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
 * demi-plans d�limitant une zone convexe.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version 1.0
 * @since jPicEdt 1.6
 */
public class ConvexZoneHalfPlaneView{
	boolean isHighlighted;
	ConvexZone convexZone;

	private final double TRIANGLE_H = 1.61803398875; // nombre d'or, hauteur
													 // triangle, � compter de l'origine
	private final double END_POINT_H = 2*TRIANGLE_H;
	private final double PLINTH_HW = 2*TRIANGLE_H; // demi-largeur socle
	private final double PLINTH_HH = 0.25; // demi-hauteur socle
    /// TRIANGLE_TAN_HALF_APERTURE=tan(30�), c'est � dire que le triangle est
    /// �quilat�ral.
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

	/** �tend le rectangle <code>allocation</code> de sorte � prendre en compte l'�paisseur
		@param allocation rectangle � �tendre.*/
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

			// coin base � droite du triangle
			pt.setCoordinates(hp.getOrg());
			pt.translate(hp.getDir(),PLINTH_HH*barbellSize);
			pt.translate(orthoDir,((TRIANGLE_H-PLINTH_HH)*TRIANGLE_TAN_HALF_APERTURE)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin sup�rieur droite du socle
			pt.setCoordinates(hp.getOrg());
			pt.translate(hp.getDir(),PLINTH_HH*barbellSize);
			pt.translate(orthoDir,PLINTH_HW*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin inf�rieur droite du socle
			pt.translate(hp.getDir(),-(2*PLINTH_HH)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin inf�rieur gauche du socle
			pt.translate(orthoDir,-(2*PLINTH_HW)*barbellSize);
			pathBuffer.lineTo(pt.getX(),pt.getY());

			// coin sup�rieur gauche du socle
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
