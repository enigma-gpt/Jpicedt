// ConvexPolygonalZoneBoundary.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexPolygonalZoneBoundary.java,v 1.5 2013/03/27 06:55:56 vincentb1 Exp $
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
package jpicedt.graphic.util;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Une classe pour représenter la frontière d'une zone polygonale convexe.
 *
 * Si la frontière est un polygone fermé, alors le segment de droite
 * correspondant à <code>halfPlanes.get(i)</code> relie les points
 * <code>subdivisionPoints.get(i)</code> et
 * <code>subdivisionPoints.get((i+1)%subdivisionPoints.size())</code>.
 *
 * Si la frontière est une ligne polygonale non fermé, en supposant que la
 * zone polygonale convexe ne soit pas réduite à une seul demi-plan, alors on
 * a <code>halfPlanes.size() = subdivisionPoints.size()+1</code>. La
 * demi-droite correspondant à <code>halfPlanes.get(0)</code> est bornée par
 * le point <code>subdivisionPoints.get(0)</code>, la demi-droite
 * correspondant à <code>halfPlanes.get(halfPlanes.size()-1)</code> est bornée
 * par le point
 * <code>subdivisionPoints.get(subdivisionPoints.size()-1)</code>, et à
 * supposer qu'il y ait plus d'un point de subdivision, alors le segment de
 * droite correspondant à un autre demi-plan quelconque
 * <code>halfPlanes.get(i)</code> relie les points
 * <code>subdivisionPoints.get(i-1)</code> et
 * <code>subdivisionPoints.get(i)</code>.
 *
 * <b>Attention</b> Les éléments de la liste <code>halfPlanes</code> ne sont
 * pas des clones de ceux de la <code>ConvexPolygonalZone</code> dont est
 * issue la frontière, mais pointent sur les mêmes exemplaires.
 *
 * @since jPicEdt 1.6
 * @see ConvexPolygonalZone
 * @see ConvexPolygonalZoneBoundaryFactory
 * @author <a href="vincentb1@users.sourceforge.net">Vincent Belaïche</a>
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
