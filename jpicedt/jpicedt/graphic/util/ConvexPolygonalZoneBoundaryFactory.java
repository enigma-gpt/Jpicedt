// ConvexPolygonalZoneBoundaryFactory.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexPolygonalZoneBoundaryFactory.java,v 1.6 2013/06/13 20:47:12 vincentb1 Exp $
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

import java.lang.Integer;
import java.util.List;
import java.lang.Math;
import java.util.Iterator;

import jpicedt.graphic.PicVector;
import jpicedt.graphic.PicPoint;

import jpicedt.util.math.Interval;
import jpicedt.util.math.MathConstants;

import static jpicedt.Log.*;

/**
 * Une classe pour fabriquer des <code>ConvexPolygonalZoneBoundary</code>.
 *
 * @see ConvexPolygonalZoneBoundary
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version $Id: ConvexPolygonalZoneBoundaryFactory.java,v 1.6 2013/06/13 20:47:12 vincentb1 Exp $
 * @since jPicEdt 1.6
 */
public class ConvexPolygonalZoneBoundaryFactory{

	ConvexPolygonalZone convexZone;

	private Interval[] intersections;
	private Interval[] intersectionsIntersections;
	private final static double ABSOLUTE_TOLERANCE = MathConstants.DOUBLE_ABSOLUTE_TOLERANCE;
	private final static double RELATIVE_TOLERANCE = 1.0e-6;

	private void intersectIntersections(){
		if(intersectionsIntersections == null || intersectionsIntersections.length < convexZone.size())
			intersectionsIntersections = new Interval[convexZone.size()];

		for(int i = 0; i < convexZone.size(); ++i)
		{
			if(intersectionsIntersections[i] == null)
				intersectionsIntersections[i] = new Interval();
			intersectIntersection(intersectionsIntersections[i],i);
		}
	}

	private void intersectIntersection(Interval intersectionsIntersection,int hpIndex){
		int hpBaseIndex = hpIndex*convexZone.size();
		intersectionsIntersection.setToR();
		for(int i = 0; i < convexZone.size(); ++i)
		{
			if(i == hpIndex)
				continue;
			else
				intersectionsIntersection.intersect(intersections[hpBaseIndex + i]);
		}
		if(DEBUG)
			debug("intersectionsIntersection=" + intersectionsIntersection.toString()
				  + ", hpIndex=" + Integer.toString(hpIndex));
	}

	/**
	 * Calcul l'intersection deux deux demi-plan <code>hp1</code> et
	 * <code>hp2</code>. L'intersection est calcul� en termes des informations
	 * suivantes:
	 *
	 *<ol><li>un intervalle <code>h1h2</code> dterminant une droite ou une
	 * demi-doite sur la fronti�re du demi-plan <code>hp1</code>.</li>
	 * <li>un intervalle <code>h2h1</code> d�terminant une droite ou une
	 * demi-doite sur la fronti�re du demi-plan <code>hp2</code>.</li>
	 * <li>une valeur <var>ret</var> renvoy�e et d�signant l'un des cas suivants:<ol>
	 * <li>1 si le demi-plan <code>hp1</code> contient le demi-plan
	 * <code>hp2</code> et est donc inutile</li>
	 * <li>2 si le demi-plan <code>hp2</code> contient le demi-plan
	 * <code>hp1</code> et est donc inutile</li>
	 * <li> 0 sinon</li>
	 * </ol></li>
	 * </ol>
	 * @param hp1 une valeur <code>ConvexPolygonalZone.HalfPlane</code> premier membre de l'intersection
	 * @param hp2 une valeur <code>ConvexPolygonalZone.HalfPlane</code> second membre de l'intersection
	 * @param h1h2 une valeur <code>Interval</code>, d�finit la bordure de
	 * l'intersection comme sous-ensemble de la bordure de <code>hp1</code>
	 * dans la base
	 * (<code>hp1.getOrg()</code>,<code>hp1.getOrg().cMIMul()</code>).
	 * @param h2h1 une valeur <code>Interval</code>, vice versa de <code>h1h2</code>.
	 * @return une valeur <code>int</code>
	 * @see #computeIntersections
	 */
	private int computeIntersection(ConvexPolygonalZone.HalfPlane hp1,
									 ConvexPolygonalZone.HalfPlane hp2,
									 Interval h1h2,
									 Interval h2h1){
		int ret = 0;
		double det = hp1.getDir().det(hp2.getDir());
		if(det == 0.0)
		{
			// les deux demi-plans ont des fronti�res parall�les
			if(hp1.contains(hp2.getOrg()))
			{
				if(hp1.getDir().dot(hp2.getDir()) > 0)
					ret = 1;
				h1h2.setToR();
				h2h1.setToR();
			}
			else if(hp2.contains(hp1.getOrg()))
			{
				if(hp1.getDir().dot(hp2.getDir()) > 0)
					ret = 2;
				h1h2.setToR();
				h2h1.setToR();
			}
			else
			{
				// la zone convexe est vide !
				h1h2.setToEmpty();
				h2h1.setToEmpty();
			}

		}
		else
		{
			// les deux demi-plans ont des fronti�res qui se coupent

			// d�termination du point d'intersection A * intersectionPt = B
			double[][]  A = LinearAlgebra.normalVectorsToMatrix(hp1.getDir(),hp2.getDir());
			PicVector   B = new PicVector(hp1.getDir().dot(hp1.getOrg()),
										  hp2.getDir().dot(hp2.getOrg()));

			PicPoint intersectionPt = LinearAlgebra.invLinearApplication(A,B);

			double x = hp1.getDir().det(new PicVector(hp1.getOrg(),intersectionPt));
			if(det > 0)
				h1h2.set(x, Double.POSITIVE_INFINITY);
			else
				h1h2.set(Double.NEGATIVE_INFINITY, x);

			x = hp2.getDir().det(new PicVector(hp2.getOrg(),intersectionPt));
			if(det < 0)
				h2h1.set(x, Double.POSITIVE_INFINITY);
			else
				h2h1.set(Double.NEGATIVE_INFINITY, x);

		}
		return ret;
	}

	/**
	 * @param done un tableau de <code>boolean</code>, d�j� allou� � l'appel
	 * et initialis� tout � <code>false</code>, la proc�dure met �
	 * <code>true</code> les �l�ment correspondant � un demi-plan inutiles.
	 * @param uselessHalfPlanes une
	 * <code>List<ConvexPolygonalZone.HalfPlane></code> � laquelle la
	 * proc�dure ajoute les demi-plans inutiles.
	 * @see #computeIntersection
	 */
	private void computeIntersections(boolean [] done,List<ConvexPolygonalZone.HalfPlane> uselessHalfPlanes){
		if(intersections == null || intersections.length < convexZone.size()*convexZone.size())
			intersections = new Interval[convexZone.size()*convexZone.size()];

		int i1,i2;
		i1 = 0;
		for(Iterator<ConvexPolygonalZone.HalfPlane> it1 = convexZone.iterator(); it1.hasNext();){
			ConvexPolygonalZone.HalfPlane hp1 = it1.next();
			i2 = i1+1;
			for(Iterator<ConvexPolygonalZone.HalfPlane> it2 = convexZone.listIterator(i2);
				 it2.hasNext();){
				ConvexPolygonalZone.HalfPlane hp2 = it2.next();
				Interval h1h2,h2h1;
				int i1i2 = convexZone.size()*i1 + i2;
				int i2i1 = convexZone.size()*i2 + i1;

				if(intersections[i1i2] == null) intersections[i1i2] =  new Interval();
				h1h2 = intersections[i1i2];

				if(intersections[i2i1] == null) intersections[i2i1] =  new Interval();
				h2h1 = intersections[i2i1];

				int ret = computeIntersection(hp1,hp2,h1h2,h2h1);
				if(ret-- != 0)
				{
					int i = (1-ret)*i1 + ret*i2;
					done[i] = true;
					uselessHalfPlanes.add(convexZone.get(i));
				}

				++i2;
			}
			++i1;
		}
	}

	public ConvexPolygonalZoneBoundaryFactory(ConvexPolygonalZone convexZone){
		this.convexZone = convexZone;
	}

	private static boolean roughlyEquals(double x, double y){
		double distance = Math.abs(x-y);
		return distance <= ABSOLUTE_TOLERANCE
			|| distance <= RELATIVE_TOLERANCE*Math.max(Math.abs(x),Math.abs(y));
	}


	/**
	 * @return une valeur <code>ConvexPolygonalZoneBoundary</code> donnant la
	 * fronti�re de la <code>ConvexPolygonalZone</code> pass�e � la
	 * construction.
	 */
	public ConvexPolygonalZoneBoundary getBoundary(){
		boolean[] done = new boolean[convexZone.size()];
		int i;
		ConvexPolygonalZoneBoundary ret = new ConvexPolygonalZoneBoundary(convexZone.size());

		for(i=0; i < convexZone.size(); ++i)
			done[i] = false;

		computeIntersections(done, ret.uselessHalfPlanes);
		intersectIntersections();


		int goBackwardIndex = 0; // initialisation requise pour faire taire le compilo.
		int goForwardIndex;
		boolean goBackward = false;
		boolean goForward  = false;
		Interval interval = null; // initialisation requise pour faire taire le compilo.
		double boundary = 0;  // initialisation requise pour faire taire le compilo.
		int indexBase;
		ConvexPolygonalZone.HalfPlane halfPlane;

		int[] bandSides = { -1, -1};
		int bandSideCount = 0;

		// exemple:
		//		  			  sens trigonom�trique <--+
	   	//								   	   	   	   \
	   	//	          min=0          max=+inf		   +
	   	//	   max=+1 +--------------...   	   	   	   !
	    //	          v           hp0				   ^
	    //	          !
	    //	          +>
		//	          !hp1
	    //	          ^           hp2
	    //	   min=-1 +--------------...
		//            max=0          min=-inf
	   	//

		for(i=0; i < convexZone.size(); ++i){

			if(done[i])
				continue;

			interval = intersectionsIntersections[i];
			if(interval.isEmpty())
			{
				ret.uselessHalfPlanes.add(convexZone.get(i));
			}
			else
			{
				if((boundary = interval.getMin()) != Double.NEGATIVE_INFINITY)
				{
					goForward = true;
				}
				if(interval.getMax() != Double.POSITIVE_INFINITY)
				{
					goBackwardIndex = i;
					goBackward = true;
				}
				if(goForward || goBackward)
					break;
				else
				{
					if(bandSideCount == 2)
					{
						if(DEBUG)
							error("Bande � trois c�t�s.");
					}
					else
					{

						bandSides[bandSideCount ++] = i;
						done[i] = true;
					}
				}

			}
		}

		if(goForward)
		{
			goForwardIndex = i;
			halfPlane = convexZone.get(goForwardIndex);
			for(;;)
			{
				indexBase = goForwardIndex * convexZone.size();
				for(i = 0; i < convexZone.size(); ++i)
				{
					if(i == goForwardIndex)
						continue;
					else
					{
						interval = intersections[indexBase + i];
						if(boundary ==  interval.getMin())
						{
							if(done[i])
							{
								// on a cycl�
								goForwardIndex = i;
								break;
							}
							if(DEBUG)
								debug("goForwardIndex="+Integer.toString(goForwardIndex)
									  +", i="+Integer.toString(i));

							ret.subdivisionPoints.addLast(halfPlane.getDir().cIMul()
													  .scale(boundary).add( halfPlane.getOrg()));
							ret.halfPlanes.addLast(halfPlane);
							done[goForwardIndex] = true;
							goForwardIndex = i;
							break;
						}
					}
				}
				if(done[goForwardIndex])
					break;
				halfPlane = convexZone.get(goForwardIndex);
				interval = intersectionsIntersections[goForwardIndex];
				boundary = interval.getMin();
				if(boundary == Double.NEGATIVE_INFINITY)
				{
					done[goForwardIndex] = true;
					ret.halfPlanes.addLast(halfPlane);
					break;
				}
			}
		}

		if(goBackward)
		{
			halfPlane = convexZone.get(goBackwardIndex);
			interval = intersectionsIntersections[goBackwardIndex];
			boundary = interval.getMax();
			if(boundary != Double.POSITIVE_INFINITY)
			{
				if(!goForward)
				{
					// il n'y a pas eu de parcour de la fronti�re en avan�ant,
					// donc ce demi-plan n'a pas d�j� �t� ajout� � la liste
					// des demi-planq de la fronti�re.
					ret.halfPlanes.addFirst(halfPlane);
					done[goBackwardIndex] = true;
				}
				for(;;)
				{
					indexBase = goBackwardIndex*convexZone.size();
					for(i = 0; i < convexZone.size(); ++i)
					{
						if(i == goBackwardIndex)
							continue;
						else
						{
							interval = intersections[indexBase + i];
							if(boundary == interval.getMax())
							{
								if(done[i])
								{
									// on a cycl�
									goBackwardIndex = i;
									break;
								}
								if(DEBUG)
									debug("goBackwardIndex="+Integer.toString(goBackwardIndex)
										  +", i="+Integer.toString(i));


								ret.subdivisionPoints.addFirst(halfPlane.getDir().cIMul()
															   .scale(boundary).add( halfPlane.getOrg()));
								done[goBackwardIndex] = true;
								goBackwardIndex = i;
								halfPlane = convexZone.get(goBackwardIndex);
								if(!done[goBackwardIndex])
									ret.halfPlanes.addFirst(halfPlane);
								break;
							}
						}
					}
					if(done[goBackwardIndex])
						break;
					interval = intersectionsIntersections[goBackwardIndex];
					boundary = interval.getMax();
					if(boundary == Double.POSITIVE_INFINITY)
					{
						done[goBackwardIndex] = true;
						ret.halfPlanes.addFirst(halfPlane);
						break;
					}
				}
			}
		}

		if(ret.isClosed() && goBackward)
		{
			// dans le cas o� une partie d'un contour ferm� a �t� g�n�r� �
			// reculon, alors on fait une permutation circulaire sur les
			// demi-plan de fronti�re, de sorte � ce que la fronti�re puisse
			// toujours �tre exploit�e en avan�ant
			halfPlane = ret.halfPlanes.removeFirst();
			ret.halfPlanes.addLast(halfPlane);
		}

		if(bandSideCount != 0)
		{
			// cas d'une bande ou d'un demi-plan unique
			for(int j = 0; j < bandSideCount; ++j)
			{
				i = bandSides[j];
				ret.halfPlanes.addLast(convexZone.get(i));
			}
		}

		if(ret.halfPlanes.size() + ret.uselessHalfPlanes.size() < convexZone.size())
		{
			for(i = 0; i < convexZone.size(); ++i)
				if(! done[i])
				{
					ret.uselessHalfPlanes.add(convexZone.get(i));
					if(DEBUG){
						interval = intersectionsIntersections[i];
						if(! interval.isEmpty())
							error("Bande � trois c�t�s, bis.");
					}
				}
		}

		return ret;
	}
};


/// ConvexPolygonalZoneBoundaryFactory.java ends here
