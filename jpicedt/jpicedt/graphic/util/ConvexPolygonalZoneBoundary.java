// ConvexPolygonalZoneBoundary.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2010 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexPolygonalZoneBoundary.java,v 1.5 2013/03/27 06:55:56 vincentb1 Exp $
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
package jpicedt.graphic.util;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Une classe pour repr�senter la fronti�re d'une zone polygonale convexe.
 *
 * Si la fronti�re est un polygone ferm�, alors le segment de droite
 * correspondant � <code>halfPlanes.get(i)</code> relie les points
 * <code>subdivisionPoints.get(i)</code> et
 * <code>subdivisionPoints.get((i+1)%subdivisionPoints.size())</code>.
 *
 * Si la fronti�re est une ligne polygonale non ferm�, en supposant que la
 * zone polygonale convexe ne soit pas r�duite � une seul demi-plan, alors on
 * a <code>halfPlanes.size() = subdivisionPoints.size()+1</code>. La
 * demi-droite correspondant � <code>halfPlanes.get(0)</code> est born�e par
 * le point <code>subdivisionPoints.get(0)</code>, la demi-droite
 * correspondant � <code>halfPlanes.get(halfPlanes.size()-1)</code> est born�e
 * par le point
 * <code>subdivisionPoints.get(subdivisionPoints.size()-1)</code>, et �
 * supposer qu'il y ait plus d'un point de subdivision, alors le segment de
 * droite correspondant � un autre demi-plan quelconque
 * <code>halfPlanes.get(i)</code> relie les points
 * <code>subdivisionPoints.get(i-1)</code> et
 * <code>subdivisionPoints.get(i)</code>.
 *
 * <b>Attention</b> Les �l�ments de la liste <code>halfPlanes</code> ne sont
 * pas des clones de ceux de la <code>ConvexPolygonalZone</code> dont est
 * issue la fronti�re, mais pointent sur les m�mes exemplaires.
 *
 * @since jPicEdt 1.6
 * @see ConvexPolygonalZone
 * @see ConvexPolygonalZoneBoundaryFactory
 * @author <a href="vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version $Id: ConvexPolygonalZoneBoundary.java,v 1.5 2013/03/27 06:55:56 vincentb1 Exp $
 */
public class ConvexPolygonalZoneBoundary{

	public LinkedList<PicPoint> subdivisionPoints;
	public LinkedList<ConvexPolygonalZone.HalfPlane> halfPlanes;
	public ArrayList<ConvexPolygonalZone.HalfPlane> uselessHalfPlanes;

	public boolean isClosed(){ return subdivisionPoints.size() == halfPlanes.size(); }
	public ConvexPolygonalZoneBoundary(int i){
		subdivisionPoints  = new LinkedList<PicPoint>();
		halfPlanes         = new LinkedList<ConvexPolygonalZone.HalfPlane>();
		uselessHalfPlanes  = new ArrayList<ConvexPolygonalZone.HalfPlane>(i);
	}

	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("ConvexPolygonalZoneBoundary[subdivisionPoints=[");
		{
			Iterator<PicPoint> it = subdivisionPoints.iterator();
			if(it.hasNext())
			{
				PicPoint pt = it.next();
				for(;;)
				{

					ret.append(pt.toString());
					if(it.hasNext())
					{
						ret.append(",");
						pt = it.next();
					}
					else
						break;
				}
			}
		}
		ret.append("],halfPlanes=[");
		{
			Iterator<ConvexPolygonalZone.HalfPlane> it = halfPlanes.iterator();
			if(it.hasNext())
			{
				ConvexPolygonalZone.HalfPlane hp = it.next();
				for(;;)
				{

					ret.append(hp.toString());
					if(it.hasNext())
					{
						ret.append(",");
						hp = it.next();
					}
					else
						break;
				}
			}
		}
		ret.append("],uselessHalfPlanes=[");
		{
			Iterator<ConvexPolygonalZone.HalfPlane> it = uselessHalfPlanes.iterator();
			if(it.hasNext())
			{
				ConvexPolygonalZone.HalfPlane hp = it.next();
				for(;;)
				{

					ret.append(hp.toString());
					if(it.hasNext())
					{
						ret.append(",");
						hp = it.next();
					}
					else
						break;
				}
			}
			ret.append("]]\n");
		}
		return ret.toString();
	}
};


/// ConvexPolygonalZoneBoundary.java ends here
