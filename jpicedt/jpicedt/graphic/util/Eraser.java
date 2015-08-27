// Eraser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: Eraser.java,v 1.9 2013/10/07 19:16:27 vincentb1 Exp $
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

/// Installation:

/// Code:
package jpicedt.graphic.util;

import jpicedt.Log;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.model.*;
import jpicedt.util.math.IntervalUnion;
import jpicedt.util.math.Interval;
import jpicedt.util.math.Polynomial;

import static jpicedt.util.math.Complex.TWO_PI;
import static jpicedt.util.math.IntervalUnion.CopyIntoAllocPolicy.*;
import static jpicedt.graphic.util.AbstractEraser.ErasureStatus.*;

import java.lang.Math.*;

import static jpicedt.Log.debug;
import static java.lang.Math.PI;

/** Calcule le résultat de l'effacement par une zone convexe <var>z</var> de
 *	n'importe quel <code>Element</code> <var>e</var>. Pour faire ce calcul il
 *	suffit d'instancier
 *	<code>Eraser(</code><var>e</var><code>,</code><var>z</var><code>)</code>
 * @see AbstractEraser
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class Eraser extends AbstractEraser
{
	static final boolean DEBUG = true;

    //---------------------------------------------------------------------------
    /** Classe spécialisée pour effacer une zone d'une PicEllipse.<br>
	 * Mêmes limitation que pour {@link jpicedt.graphic.util.Eraser.CCWCircleEraser}.
	 * @see Eraser
	 * @see jpicedt.graphic.util.Eraser.CCWCircleEraser
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	public class EllipseEraser extends AbstractEraser
	{
		public EllipseEraser(PicEllipse abstractElement,ConvexPolygonalZone zone){
			super();
			double smallAxis = abstractElement.getSmallAxisLength();
			double greatAxis = abstractElement.getGreatAxisLength();
			double[][] matrix;
			double radius;
			final PicVector l2r = abstractElement.getL2RVec();
			final PicVector b2t = abstractElement.getB2TVec();

			double[][] m1 = {
				{ l2r.getX(), b2t.getX()},
				{ l2r.getY(), b2t.getY()}
			};

			final PicVector e1 = LinearAlgebra.invLinearApplication(m1,PicVector.X_AXIS);
			final PicVector e2 = LinearAlgebra.invLinearApplication(m1,PicVector.Y_AXIS);

			double[][] m2 = {
				{ e1.getX(), e2.getX()},
				{ e1.getY(), e2.getY()}
			};

			matrix = m2;
			radius = 0.5;

			PicPoint center;

			center = abstractElement
				.getCtrlPt(abstractElement.getFirstPointIndex(),null);
			{
				PicPoint pt3 = abstractElement
					.getCtrlPt(abstractElement.getFirstPointIndex()+2,null);
				center.middle(pt3);
			}
			if(DEBUG)
				debug("center="+center.toString()); //$$PROBE

			/* intervalInter contient l'intersection des secteurs angulaires qui ne sont pas dans la zone,
			   réprésentés comme des parties de
			   [0 2pi]*/
			IntervalUnion intervalInter;
			{
				double[] e = {0, TWO_PI};
				intervalInter = new IntervalUnion(e);
			}
			status = NO_ERASURE;
			for(ConvexPolygonalZone.HalfPlane hp : zone){
				PicPoint org = hp.getOrg();
				PicVector direction = LinearAlgebra.vectorLinAp(matrix,hp.getDir().cIMul())
					.normalize().miMul();
				if(DEBUG)
					debug("direction="+ direction.toString());

				double distance = direction.dot(LinearAlgebra.vectorLinAp(matrix,new PicVector(org,center)));
				if(Math.abs(distance) < radius){


					/*	   ------
   	   	   	   	   	  |	--/
					  |/						 <-_
					  +							   	\_ sens anti-horaire
				 	 /|\							  \	 des angles positifs.
				   -- |	\							   \
	              /	  |	 \
	             / 	  |	  \
	    arc hors |	  |	   \
	     du demi |	  |		\
	        plan |	  |		 \
	    interdit | org+---> direction, directionAngle = 2*pi
				 | 	  |	   	   \
				 | 	  |	  center+
				 |	  |		   /
				 | 	  <---------> distance > 0, distance < radius
				 |    |		 /    acos(-distance/radius) in [pi/2 pi]
				 |    |		/
				 | 	  |	   /
				 \ 	  |	  /
				  \	  |	 /
 				   -- |	/
   	   	   	   	     \|/
				   	  +
					  |\
					  |	--\
					  |	   -----
   	   	   	   	   	   	   	   	   	   	  |
					  	   -----------	  |
   	   	   	   	   	   	--/			  \-- |
					   /			   	 \|		 <-_
					  -	   	   	   	   	  +		   	\_ sens anti-horaire
				 	 /     	   	   	   	 /|\          \	 des angles positifs.
				   --  	 			   	/ |	--         \
	              /	   	  			   /  |	  \
	             / 	   	   	   	   	  /	  |	   \
	    arc hors |	   	    		 /	  |	   |
	     du demi |	   		 	   	/	  |	   |
	        plan |	   		  	   / 	  |	   |
	    interdit |                /    org+--->|direction, directionAngle = 2*pi
				 | 	   	   	     /	 	  |	   |
				 | 	   	  center+ 	 	  |	   |
				 |	   		     \ 	   	  |	   |
				 | 	            <---------> distance < 0, -distance < radius
				 |     		       \      | acos(-distance/radius) in [0 pi/2]
				 |     	   	 	   	\	  |	   |
				 | 	   	    		 \	  |	   |
				 \ 	   	   			  \	  |	   /
				  \	   	  			   \  |	  /
 				   --  	 			   	\ |	--
   	   	   	   	     \  			 	 \|/
				   	  -	   	   	   	   	  +
					   \			 	 /|
					   	--\			  /-- |
						   -----------	  |
										  |
										  |
 					 */

					// deltaAngle dans [0 pi]
					double deltaAngle = Math.acos(-distance/radius);

					/* directionAngle est l'angle que fait le vecteur -direction dans la base canonique
       				 * l2r b2t. À noter que -direction est la bissectrice du secteur
       				 * angulaire hors du demi-plan interdit.  directionAngle est dans
                     * [0 2pi]*/
					double directionAngle = PicVector.X_AXIS.angle(direction); // résultat dans ]-pi pi]
					if(directionAngle <= 0)
						directionAngle += TWO_PI; // dans ]0 2*pi]

					if(DEBUG)
						debug("distance="+Double.toString(distance)
							  +"\nradius="+Double.toString(radius)
							  +"\nmatrix=["
							  +Double.toString(matrix[0][0])+" "
							  +Double.toString(matrix[0][1])+"; "
							  +Double.toString(matrix[1][0])+" "
							  +Double.toString(matrix[1][1])+"]"
							  +"\ndirectionAngle="+Double.toString(directionAngle)
							  +"\ndeltaAngle="+Double.toString(deltaAngle)); //$$PROBE

					double minAngle =directionAngle + deltaAngle;
					while(minAngle > TWO_PI)
						minAngle = minAngle - TWO_PI;

					double maxAngle = directionAngle - deltaAngle;
					if(maxAngle <= 0.0)
						maxAngle = maxAngle + TWO_PI;

					if(minAngle <= maxAngle){
						double[] e = { minAngle, maxAngle};
						intervalInter.inter(e);
					}
					else{
						double[] e = { 0 ,  maxAngle, minAngle, TWO_PI};
						intervalInter.inter(e);
					}
					status = PARTIALLY_ERASED;
				}
				else if(distance > 0){
					status = TOTALLY_ERASED;
				}
			}

			switch(status)
			{
			case NO_ERASURE:
				erasedElt = abstractElement;
				break;
			case PARTIALLY_ERASED:
			{
				// maintenant on prend l'intersection de ce qui n'est pas dans la
				// zone interdite avec le contenu du cercle
				IntervalUnion circleIntervalUnion;
				if(abstractElement.isArc()) {
					double minAngle = Math.toRadians(abstractElement.getAngleStart());
					double maxAngle = Math.toRadians(abstractElement.getAngleEnd());
					// minAngle et maxAngle dans [-pi, pi], se ramener à [0 pi]
					if(minAngle < 0)
						minAngle += TWO_PI;
					if(maxAngle < 0)
						maxAngle += TWO_PI;


					if(minAngle >= maxAngle) {
						double[] e = { 0, maxAngle, minAngle,   TWO_PI};
						circleIntervalUnion = new IntervalUnion(e);
					}
					else {
						double[] e =  { minAngle ,maxAngle };
						circleIntervalUnion = new IntervalUnion(e);
					}
				}
				else {
					double[] e = { 0, TWO_PI};
					circleIntervalUnion = new IntervalUnion(e);
				}

				intervalInter.inter(circleIntervalUnion);

				intervalInter.moduloJoin(TWO_PI);
				double[] e = null;
				e = intervalInter.copyInto(e,FORCE_ALLOC);

				switch(intervalInter.componentCount())
				{
				case 0:
					status = TOTALLY_ERASED;
					erasedElt = null;
					break;
				case 1:
					e[0] = Math.toDegrees(e[0]);
					e[1] = Math.toDegrees(e[1]);
					if(e[0] != abstractElement.getAngleStart()
					   || e[1] != abstractElement.getAngleEnd()){
						PicEllipse erasedEllipse = abstractElement.clone();
						erasedEllipse.setAngleStart(e[0]);
						erasedEllipse.setAngleEnd(e[1]);
						erasedElt = erasedEllipse;
					}
					else
						status = NO_ERASURE;
					break;

				default:
				{
					PicGroup g = new PicGroup();
					for(int i =0; i < intervalInter.componentCount(); ++i)
					{
						if(e[2*i+1] > e[2*i])
						{
							PicEllipse subArc = abstractElement.clone();
							subArc.setAngleEnd(Math.toDegrees(e[2*i+1]));
							subArc.setAngleStart(Math.toDegrees(e[2*i]));
							if(i > 0){
								subArc.getAttributeSet().setAttribute(PicAttributeName.LEFT_ARROW,
																	  StyleConstants.ArrowStyle.NONE);
							}
							else if(i+1 < intervalInter.componentCount()){
								subArc.getAttributeSet().setAttribute(PicAttributeName.RIGHT_ARROW,
																	  StyleConstants.ArrowStyle.NONE);
							}

							g.add(subArc);
						}
					}
					erasedElt = g;
					break;
				}
				}
				break;
			}
			case TOTALLY_ERASED:
				erasedElt = null;
				break;
			default:
				break;
			}
		}
	};

    //-----------------------------------------------------------------------
    /** Classe spécialisée pour effacer une zone d'un PicMultiCurve.
	 * @see Eraser
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	public class MultiCurveEraser extends AbstractEraser
	{

		MultiCurveEraser(PicMultiCurve multiCurve,ConvexPolygonalZone zone){
			super();

			status         = NO_ERASURE;
			PicMultiCurve subCurve = null;
			PicGroup      g = null;

			// Ecusez-moi monsieur Bézier de ne pas mettre de
			// majuscule à votre nom...
			PicPoint[] bezierPt = new PicPoint[4];
			Polynomial p        = new Polynomial();
			Polynomial dPdt;
			int latestI = 0;
			int i;
			int noChangeCounter = 0;
			for(i = multiCurve.getFirstPointIndex();
				i+3 <=  multiCurve.getLastPointIndex(); i+= 3)
			{

				for(int j = 0; j < 4; ++j)
					bezierPt[j] = multiCurve.getCtrlPt(i+j,bezierPt[j]);


				// intervalUnion contient l'union du domaine de l'indice de
				// la courbe de Bézier compris hors de chaque demi-plan
				// dont la zone est l'intersection
				IntervalUnion intervalUnion = new IntervalUnion();
				for(ConvexPolygonalZone.HalfPlane hp: zone)
				{
					PicPoint  org = hp.getOrg();
					PicVector direction = hp.getDir();
					double tol = 1.0e-6;

					// le polynome P(t) = a[0]*(1-t)^3 + 3*a[1]*t*(1-t)^2 +
					// 3*a[2]*t^2*(1-t) + a[3]*t^3
					// est négatif hors de la zone.
					{
						double[] a = new double[4];
						for(int j=0; j < 4; ++j)
						{
							a[j] = direction.dot(new PicVector(org, bezierPt[j]));
						}
						p.setALaBernstein(3,a);
					}

					dPdt = p.cDerive();
					// x[] contient les bornes d'intervalles paritionnant
					// [0 1] et tels que le polynome P est monotone sur
					// chacun d'eux.
					double[] x = new double[4];
					x[0] = 0.0;
					int xCount = 1;

					if(Math.abs(dPdt.coeff(2)) > tol)
					{
						double delta; // discriminant de dP(t)/dt
						delta = dPdt.coeff(1)*dPdt.coeff(1)
							- 4*dPdt.coeff(2)*dPdt.coeff(0);
						if(delta < 0)
						{
							// la dérive ne s'annulle jamais, donc le polynome
							// est monotone
							x[xCount++] = 1.0;
						}
						else
						{
							double sqrtDelta = Math.sqrt(delta);
							double zMin,zMax;
							{
								double zero1 =  (-dPdt.coeff(1) - sqrtDelta)
									/ 2.0 / dPdt.coeff(2);

								double zero2 = (-dPdt.coeff(1) + sqrtDelta)
									/ 2.0 / dPdt.coeff(2);

								if(zero2 > zero1)
								{
									zMax = zero2;
									zMin = zero1;
								}
								else
								{
									zMax = zero1;
									zMin = zero2;
								}
							}

							if(zMin > 0 && zMin < 1.0)
							{
								x[xCount++] = zMin;
							}
							if(zMax > 0 && zMax < 1.0)
							{
								x[xCount++] = zMax;
							}
							x[xCount++] = 1.0;

						}// if(delta < 0)
					}
					else if(Math.abs(dPdt.coeff(1)) > tol)
					{
						// derivée de degré == 1
						double z = -dPdt.coeff(0)/dPdt.coeff(1);
						if(z > 0.0 && z < 1.0)
						{
							x[xCount++] = z;
						}
						x[xCount++] = 1.0;
					}
					else
					{
						// dérivée constante, le polynome est de degré 1, monotone
						x[xCount++] = 1.0;
					}

					for(int xI = 0; xI+1 < xCount; ++xI)
					{
						double xMin = x[xI];
						double xMax = x[xI+1];
						// le polynome est monotone sur [xMin,
						// xMax]
						double xMinVal = p.eval(xMin);
						double xMaxVal = p.eval(xMax);
						if(xMinVal <= 0 && xMaxVal <= 0)
						{
							// tout [xMin, xMax] est hors zone
							intervalUnion.union(new Interval(xMin,xMax));
						}
						else if(xMinVal > 0 && xMaxVal > 0)
						{
							// tout [xMin, xMax] est dans la zone
						}
						else
						{
							double[] zero = p.findZeroInInterval(xMin,xMax,tol,0);
							if(zero != null)
							{
								if(xMinVal < 0)
									intervalUnion
										.union(new Interval(xMin,zero[0]));
								else
									intervalUnion
										.union(new Interval(zero[0],xMax));

							}
							else
							{
								// exception
								Log.error("Zéro non trouvé sur polynome");
							}
						}
					}// for(int xI = 0; xI+1 < xCount; ++xI)

				}//for(Iterator hPlaneIt = zone.iterator();
				//hPlaneIt.hasNext();)

				if(intervalUnion.equals(new Interval(0,1)))
					++noChangeCounter;
				else
				{
					if(status == NO_ERASURE)
					{
						status = PARTIALLY_ERASED;
						erasedElt = g = new PicGroup();
						erasedElt.setAttributeSet(multiCurve.getAttributeSet());
					}

					noChangeCounter *= 3;
					if(noChangeCounter != 0)
					{
						if(subCurve == null)
						{
							subCurve = new PicMultiCurve(
								multiCurve.getCtrlPt(i-noChangeCounter,null));
						}
						do
						{
							subCurve
								.curveTo(
									multiCurve.getCtrlPt(i-noChangeCounter+1,null),
									multiCurve.getCtrlPt(i-noChangeCounter+2,null),
									multiCurve.getCtrlPt(i-noChangeCounter+3,null)
									);
						}
						while((noChangeCounter -= 3) != 0);
					}
					if(intervalUnion.componentCount() > 0)
					{
						VecPolynomial bezierPol = new VecPolynomial();
						bezierPol.setALaBernstein(3, bezierPt);
						VecPolynomial dBezierPol = bezierPol.cDerive();

						for(int intInd = 0;
							intInd < intervalUnion.componentCount();++intInd)
						{

							Interval interval = intervalUnion.get(intInd);
							double d = (interval.getMax() - interval.getMin())/3;
							double t = interval.getMin();
							PicPoint pt0 = t == 0
								? multiCurve.getCtrlPt(i,null)
								: bezierPol.eval(t);
							PicPoint pt1 = (new PicPoint(pt0)).translate(dBezierPol.eval(t),d);

							t = interval.getMax();
							PicPoint pt3 = bezierPol.eval(t);
							PicPoint pt2 = (new PicPoint(pt3)).translate(dBezierPol.eval(t),-d);

							if(!interval.contains(0) && subCurve != null){
								subCurve.setAttributeSet(multiCurve.getAttributeSet());
								g.add(subCurve);
								subCurve = null;
							}

							if(subCurve == null){
								subCurve = new PicMultiCurve(pt0);
							}

							subCurve.curveTo(pt1,pt2,pt3);


							if(!interval.contains(1))
							{
								subCurve.setAttributeSet(multiCurve.getAttributeSet());
								g.add(subCurve);
								subCurve = null;
							}
						}// end for(...)
					}
				}
			}
			if(erasedElt != null
			   && noChangeCounter != 0)
			{
				noChangeCounter *= 3;
				if(subCurve == null)
				{
					subCurve = new PicMultiCurve(
						multiCurve.getCtrlPt(
										  i - noChangeCounter,
										  null));
				}
				while(noChangeCounter != 0)
				{
					subCurve
						.curveTo(
							multiCurve.getCtrlPt(i-noChangeCounter+1,null),
							multiCurve.getCtrlPt(i-noChangeCounter+2,null),
							multiCurve.getCtrlPt(i-noChangeCounter+3,null)
							);
					noChangeCounter -= 3;
				}
			}
			if(subCurve != null)
			{
				if(subCurve.getBezierPtsCount() > 1){
					subCurve.setAttributeSet(multiCurve.getAttributeSet());
					g.add(subCurve);
				}
				subCurve = null;
			}
			if(status == PARTIALLY_ERASED && g.size() == 0)
			{
				status = TOTALLY_ERASED;
				erasedElt = null;
			}
			else if(status == NO_ERASURE)
				erasedElt = multiCurve;
			else if(g.size() == 1)
				erasedElt = g.get(0);
		}
	};

    /** Classe spécialisée pour effacer une zone d'un PicGroup.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @see Eraser
	 */
	public class GroupEraser extends AbstractEraser
	{
		GroupEraser(PicGroup picGroup,ConvexPolygonalZone zone){
			super();

			status = NO_ERASURE;

			ErasureStatus[] statusArray = new ErasureStatus[picGroup.size()];
			Element[] absEltArray = new Element[picGroup.size()];
			int i = 0;
			for(Element subAbstractElt : picGroup)
			{
				Eraser eraser  = new Eraser(subAbstractElt,zone);

				statusArray[i] = eraser.getStatus();
				switch(eraser.getStatus())
				{
				case NO_ERASURE:
					absEltArray[i] = subAbstractElt;
					++i;
					break;
				case PARTIALLY_ERASED:
					absEltArray[i] =  eraser.getErasedElt();
					status = PARTIALLY_ERASED;
					++i;
					break;
				case TOTALLY_ERASED:
					status = PARTIALLY_ERASED;
					break;
				default:
					Log.error("Eraser eraser.getStatus() inattendu");
					break;
				}

			}
			if(status == NO_ERASURE)
			{
				erasedElt = picGroup;
			}
			else
			{
				if(i != 0)
				{
					PicGroup erasedPicGroup = new PicGroup();
					for(int j = 0;j < i; ++j)
					{
						if(statusArray[j] == NO_ERASURE)
							erasedPicGroup.add(absEltArray[j].clone());
						else
							erasedPicGroup.add(absEltArray[j]);
					}
					erasedElt = erasedPicGroup;
				}
				else
				{
					status = TOTALLY_ERASED;
					erasedElt = null;
				}
			}
		}
	};

    /** Classe spécialisée pour effacer une zone d'un PicPallelogram.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @see Eraser
	 */
	public class ParallelogramEraser extends AbstractEraser
	{

		ParallelogramEraser(PicParallelogram parallelogram,ConvexPolygonalZone zone){
			super();
			PicMultiCurve c = parallelogram.convertToMultiCurve();
			MultiCurveEraser mcEraser =
				new MultiCurveEraser(c,zone);

			status = mcEraser.getStatus();
			if(status == NO_ERASURE)
				erasedElt = parallelogram;
			else
				erasedElt = mcEraser.getErasedElt();
		}

	};

	/**
	 * @param abstractElement Element dont on veut calculer le résultat de
	 * l'effacement.
	 * @param zone Zone dans laquelle tout est effacée. zone est une zone
	 * polygonal convexe.
	 */
	public Eraser(Element abstractElement,ConvexPolygonalZone zone){
		super();

		if(abstractElement instanceof PicEllipse)
		{
			EllipseEraser ellipseEraser =
				new EllipseEraser((PicEllipse)abstractElement,zone);
			status = ellipseEraser.getStatus();
			erasedElt = ellipseEraser.getErasedElt();
		}
		else if (abstractElement instanceof PicGroup)
		{
			GroupEraser groupEraser = new GroupEraser((PicGroup)abstractElement,zone);
			status = groupEraser.getStatus();
			erasedElt = groupEraser.getErasedElt();
		}
		else if (abstractElement instanceof PicParallelogram)
		{
			ParallelogramEraser parallelogramEraser =
				new ParallelogramEraser((PicParallelogram)abstractElement,zone);
			status = parallelogramEraser.getStatus();
			erasedElt = parallelogramEraser.getErasedElt();
		}
		else if (abstractElement instanceof PicMultiCurve)
		{
			MultiCurveEraser multiCurveEraser =
				new MultiCurveEraser((PicMultiCurve)abstractElement,zone);
			status = multiCurveEraser.getStatus();
			erasedElt = multiCurveEraser.getErasedElt();
		}
		else
		{
			status = NO_ERASURE;
			erasedElt = abstractElement;
			Log.warning("other class abstractElement = "+abstractElement);
		}

	}
};

/// Eraser.java ends here
