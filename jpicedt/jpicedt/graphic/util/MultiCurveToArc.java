// MultiCurveToArc.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: MultiCurveToArc.java,v 1.4 2013/03/27 06:55:36 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel permet de convertir une cha�ne de courbes cubiques de B�zier de type PicMultiCurve en un groupe
// (PicGroup) d'arcs de cercle (PicEllipse) l'approximant. Lorsque le rayon d'un arc de cercle est trop grand,
// voire infini, cet arc est remplac� par un segment de droite (PicMultiCurve)
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

/// Installation:



/// Code:
package jpicedt.graphic.util;

import jpicedt.Log;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.model.*;
import jpicedt.util.math.*;

import java.util.*;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;
import static jpicedt.graphic.model.PicEllipseConstants.*;



/** Classe permettant d'approximer une AbstractCurve en un PicGroup de
 *  segments de droite (PicMultiCurve polygonale) et d'arcs de cercle
 *  (PicEllipse).<br> La methode qui fait le boulot est convert. <br> Il est
 *  important de faire la distinction entre courbe de B�zier (�l�mentaire) et
 *  cha�ne de courbe de B�zier (�galement appel�e courbe de B�zier
 *  extensible). Dans une courbe de B�zier (�l�mentaire) on n'a que 4 points
 *  de contr�les.<br>Le principe de fonctionnement est le suivant: on fait une
 *  approximation infinit�simale (ie d'�tendue initialement nulle) de la 1�re
 *  courbe de B�zier de la cha�ne au voisinage du point de d�part de cette
 *  courbe, puis on essaie d'�tendre le plus possible cette approximation
 *  tout en respectant la contrainte qu'on ne doit pas s'�loigner trop de la
 *  cha�ne de courbe de B�zier. Lorsque on ne peut plus �tendre, on repart du
 *  point final de la derni�re approximation, et ainsi de
 *  suite.<br>L'extension d'une approximation est elle-m�me un processus
 *  it�ratif, on essaie d'�tendre le plus possible en gardant la m�me
 *  position pour le centre de l'arc de cercle approximant, puis on regarde
 *  si en ajustant ce centre on ne peut pas rel�cher les contraintes, et si
 *  oui on recommence.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class MultiCurveToArc
{

	/** L'avidit� n'est pas une politique payante pour les
	 * approximations. C'est � dire que si on pousse une approximation le plus
	 * loin possible dans la tol�rance, alors il devient tr�s difficile de
	 * faire d�marrer l'approximation suivante. GREEDINESS_WEIGHT est un
	 * poids entre 0 et 1, o� 1 veut dire qu'on pousse l'approximation le
	 * plus loin possible. */
	final static double GREEDINESS_WEIGHT = .5;

	final static double PIC_ELLIPSE_MAX_FLAT_ANGLE_RAD=
		Math.toRadians(MAX_FLAT_ANGLE);
	final static double MAX_SQUARE_TAN_FLAT_ANGLE =
		Math.tan(PIC_ELLIPSE_MAX_FLAT_ANGLE_RAD)
		*Math.tan(PIC_ELLIPSE_MAX_FLAT_ANGLE_RAD);

	/**
	 * classe permettant de m�moriser les informations d�finissant un arc de
	 * cercle ou un segment de droite approximant une portion de cha�ne de
	 * courbes de B�zier.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
	 * @since jPicEdt 1.6
	 */
	static class CurveApproximation{
		// point par lequel passe l'arc de cercle ou le segment de droite
		protected PicPoint     radiusEnd;

		/** centre de l'arc de cercle approximant.
		    null dans le cas d'un segment de droite */
		protected PicPoint    center;
		/** rayon de l'arc de cercle approximant. Attention c'est sign�. Bien
		 * qu'en toute normalit� �a devrait rester positif, il se peut que le
		 * signe s'inverse au cours d'une extension d'approximation */
		protected double      radius;

		/**<math><ci>u</ci></math> est la d�riv�e normalis�e � 1 de la courbe de
		 * B�zier au point jusqu'auquel on l'a approxim�e. <math> <apply> <eq/>
		 * <apply> <fn><ci>P</ci></fn> <ci>t</ci> </apply> <apply> <plus/> <apply>
		 * <times/> <ci>pt0</ci> <msup> <apply> <minus/> <cn>1</cn> <ci>t</ci> </apply>
		 * <cn>3</cn> </msup> </apply> <apply> <times/> <cn>3</cn> <ci>pt1</ci>
		 * <ci>t</ci> <msup> <apply> <minus/> <cn>1</cn> <ci>t</ci> </apply> <cn>2</cn>
		 * </msup> </apply> <apply> <times/> <cn>3</cn> <ci>pt2</ci>
		 * <msup><ci>t</ci><cn>2</cn></msup> <apply> <minus/> <cn>1</cn> <ci>t</ci>
		 * </apply> </apply> <apply> <times/> <ci>pt3</ci> <msup> <ci>t</ci> <cn>3</cn>
		 * </msup> </apply> </apply> </apply> </math> en
		 * <math><apply><eq/><ci>t</ci><cn>0</cn></apply></math> */
		protected PicVector   u;
		/**
		 *	<math><ci>n</ci></math> est le vecteur normal au point
		 *	jusqu'auquel on a apprixim�e. Dans le cas d'un arc, n pointe vers
		 *	le centre de courbure.
		 */
		protected PicVector   n; // vecteur normal

		/** extension de l'arc de [startAngle � endAngle].
		 * sans siginification si mIsLine (i.e. segment de droite)
		 * les angles sont relatifs � PicVector.X_AXIS dans le sens trigo et
		 * sont exprim�s en radians. */
		protected double      startAngle;
		protected double      endAngle;

		/** extension du segment de droite de radiusEnd + x*u avec
		 * x dans [ 0 , endAbscisse] */
		protected double      endAbscisse;

		/** true si segment de droite, false si arc de cercle.*/
		protected boolean     mIsLine;

		/** recopie other dans this */
		protected void setTo(CurveApproximation other){
			/*PicPoint */this.radiusEnd   = new PicPoint(other.radiusEnd);
			/*PicVector*/this.u           = new PicVector(other.u);
			/*PicVector*/this.n           = new PicVector(other.n);
			/*PicPoint */this.center      = new PicPoint(other.center);
			/*double   */this.radius      = other.radius     ;
			/*double   */this.startAngle  = other.startAngle ;
			/*double   */this.endAngle    = other.endAngle   ;
			/*double   */this.endAbscisse = other.endAbscisse;
			/*boolean  */this.mIsLine     = other.mIsLine    ;
		}

		/**
		 * construit une copie de other.
		 * @since jPicEdt 1.6
		 */
		protected CurveApproximation(CurveApproximation other){
			 setTo(other);
		}
		/**
		 * @since jPicEdt 1.6
		 */
		protected CurveApproximation(){}
		/**
		 * @since jPicEdt 1.6
		 */
		public String toString(){
			StringBuffer buf = new StringBuffer();
			buf.append("[");

			buf.append("radiusEnd=");
			buf.append(radiusEnd);

			buf.append(", center=");
			buf.append(center);

			buf.append(", radius=");
			buf.append(radius);

			buf.append(", u=");
			buf.append(u);

			buf.append(", n=");
			buf.append(n);

			buf.append(", startAngle=");
			buf.append(startAngle);

			buf.append(", endAngle=");
			buf.append(endAngle);

			buf.append(", endAbscisse=");
			buf.append(endAbscisse);

			buf.append(", mIsLine=");
			buf.append(mIsLine);

			buf.append("]");
			return buf.toString();
		}

	};

	/** Classe permettant d'ajuster la position du centre d'un arc cercle
	 *  d'extension de l'approximation par arc&amp;segments. Le cercle
	 *  d'extension peut �tre soit un vrai arc de cercle, soit un segment de
	 *  droite (cas d'un cercle dont le centre est � rejet� l'infini).
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
	 * @since jPicEdt 1.6
	 */
	static class CenterAdjustment extends CurveApproximation
	{
		// points � peu pr�s sur le cercle et contraignants
		// la position du centre
		ArrayList<PicPoint> constraintPoints;

		/** au cas o� l'ajustement du centre du cercle ne donnerait pas une
		approximation d'une plus grande portion de la cha�ne de courbes de
		B�zier, prevCurveApproximation sauvegarde l'approximation pr�c�dente
		pour pouvoir revenir en arri�re. */
		CurveApproximation  prevCurveApproximation = null;

		double      minT; /* parametre de d�part de la derni�re courbe de
							 B�zier approxim�e. */
		double      t; /* param�tre final de la derni�re courbe de B�zier
						  approxim�e. */
		VecPolynomial curBezierCurve = new VecPolynomial(0,3);

		/** dernier point de la courbe de B�zier courante curBezierCurve, c'est
		 * � dire en cours d'approximation, c'est � dire
		 * curBezierCurve.eval(<b>1</b>)  */
		PicPoint      bezierEndPoint;

		/** dernier point de la courbe de B�zier courante que l'approximation
		 * en cours couvre, c'est � dire curBezierCurve.eval(<b>t</b>) */
		PicPoint bezierApproxEndPoint;

		// tol�rances : tol repr�sente la proximit� entre la coubre et
		// l'approximation par arcs&segments. maxRadius est le rayon max d'arc
		// qu'on accepte, au-del� on consid�re que c'est un segment.
		double      tol;
		double      maxRadius;


		// caract�risent la fin de l'approximation.
		PicPoint    endPoint;
		PicVector   endDir;


		/**
		 * @since jPicEdt 1.6
		 */
		public void setBezierCurve(PicPoint pt0,PicPoint pt1,PicPoint pt2,PicPoint pt3){
			PicPoint[] a={pt0,pt1,pt2,pt3};
			curBezierCurve.setALaBernstein(3,a);
			bezierEndPoint = pt3;
			minT = 0;
			t    = 0;
		}

		/**
		 * @since jPicEdt 1.6
		 */
		public VecPolynomial getBezierCurve(){
			return curBezierCurve;
		}

		/**
		 * @since jPicEdt 1.6
		 */
		public String toString(){
			StringBuffer buf = new StringBuffer();
			buf.append("[");

			buf.append(super.toString());

			buf.append(", prevCurveApproximation=");
			buf.append(prevCurveApproximation);

			buf.append(", constraintPoints=");
			buf.append(constraintPoints);

			buf.append(", curBezierCurve=");
			buf.append(curBezierCurve);

			buf.append(", minT=");
			buf.append(minT);

			buf.append(", t=");
			buf.append(t);

			buf.append(", tol=");
			buf.append(tol);

			buf.append(", maxRadius=");
			buf.append(maxRadius);

			buf.append(", endPoint=");
			buf.append(endPoint);

			buf.append(", endDir=");
			buf.append(endDir);

			buf.append(", mIsLine=");
			buf.append(mIsLine);

			buf.append(", bezierEndPoint=");
			buf.append(bezierEndPoint);

			buf.append(", bezierApproxEndPoint=");
			buf.append(bezierApproxEndPoint);

			buf.append("]");

			return buf.toString();
		}

		/**
		 * @since jPicEdt 1.6
		 */
		boolean isLine(){ return mIsLine; }

		/**
		 * @return le param�tre de la courbe de B�zier d'o� part
		 * l'approximation en cours.
		 * @since jPicEdt 1.6
		 */
		double  getBezierStartParameter(){ return minT;}

		/**
		 * @return le param�tre de la courbe de B�zier jusqu'o� va
		 * l'approximation en cours.
		 * @since jPicEdt 1.6
		 */
		double  getBezierEndParameter(){ return t;}



		/**
		 * �tend l'approximation en cours de la courbe de B�zier courante.
		 * @since jPicEdt 1.6
		 */
		public void extend(){
			boolean relaxed;
			do
			{
				if(mIsLine)
					relaxed = extendLine();
				else if(2*abs(radius) > tol)
					relaxed = extendArc();
				else
					relaxed = extendSharpArc();


			}
			while(relaxed);

		}

		/** Cette classe permet d'examiner s'il n'est pas possible de rel�cher
		 *	les contraintes en ajustant la position du centre du cercle y
		 *	compris dans le cas o� ce cercle est un segment de droite (c'est �
		 *	dire qu'en fait initialement la position du centre est rejet�e �
		 *	l'infini)
		 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
		 * @since jPicEdt 1.6
		 */
		class ProcessPoint
		{
			/** intervalle contenant toute les positions possibles du centre
			 *  qui v�rifie les contraintes. Initialement (avant d'avoir trait�
			 *  chacun des points) on suppose que toutes les positions sont
			 *  possibles.  Finalement s'il l'union d'intervalle est non vide
			 *  (et non restreinte � l'infini) cela signifie qu'on peut
			 *  rel�cher les contraintes en transformant le segment de droite
			 *  en arc de cercle.
			 *
			 *  la position du centre, telle que circonscrite par cette union
			 *  d'intervalle, est exprim�e comme une abscisse dans le rep�re �
			 *  1 dimension (radiusEnd, n), c'est � dire qu'on ajuste la
			 *  position du centre en restant sur cette demi-droite.
			 */
			IntervalUnion centerAllowedPos =
				new IntervalUnion(
					new Interval(Double.NEGATIVE_INFINITY,
								 Double.POSITIVE_INFINITY));

			double tol;
			double sqTol;

			/**
			 * @since jPicEdt 1.6
			 */
			ProcessPoint(double tol){
				this.tol = tol;
				this.sqTol = tol*tol;
			}

			/**
			 *  Traite un point dont l'�loignement par rapport � la
			 *  statisfaction des contrainte est repr�sent� par le couple
			 *  (distance, elevation), de sorte � en d�duire en fin de compte
			 *  un ajustement potentiel de la position de l'arc
			 *  d'approximation (initialement ici un segment de droite) de
			 *  nature � rel�cher les contraintes.
             *
			 * le couple (distance, elevation) sont les coordonn�es du point
			 * examin� dans le rep�re (radiusEnd, n, u)
			 *	@param distance la distance entre le point examin� et la
			 *	droite (radiusEnd , u)
			 *  @param elevation l'abscisse de la projection selon n du point
			 *  examin� sur la droite (radiusEnd, u), l'abscisse �tant
			 *  exprim�e dans le rep�re (radiusEnd, u)
			 * @return true si �a bloque (on ne peut pas plus �tendre), et
			 * false sinon.
			 * @since jPicEdt 1.6
			 */
			boolean processPoint(double distance,double elevation){
				// �limination du cas pathologique o� le point de contrainte
				// (distance, elevation) n'apporte aucune contrainte sur
				// l'abscisse du centre du cercle d'approximation
				double ptNorm2 = distance*distance  + elevation*elevation;
				if(ptNorm2 <= sqTol)
					return false;


				/**
				 * On se place dans le rep�re (radiusEnd, n, u) Soit
				 * <math><ci>pt</ci></math> le point de coordonn�es en
				 * notations complexes <math><apply><eq/><ci>pt</ci>
				 * <apply><plus/><ci>distance</ci><apply><times/>
				 * <ci>&ImaginaryI;</ci><ci>elevation</ci></apply></apply>
				 * </apply></math>, soit c le centre d'un cercle tangeant au
				 * cercle de centre pt et de rayon tol, tangeant � u au point
				 * radiusEnd. Si on d�signe par <math><ci>d</ci></math> la
				 * direction unitaire <math><apply><eq/><ci>d</ci>
				 * <apply><fn><ci>exp</ci></fn>
				 * <apply><times/><ci>&ImaginaryI;</ci>
				 * <ci>\Phi</ci></apply></apply></math> du vecteur (pt,c)
				 * alors on peut �crire:<br/> <ol> <li>\Im(c) = 0 <li>c = pt +
				 * \lambda\cdot \exp{i\Phi} <li>(|pt|^2 - tol^2 +2\lambda tol)
				 * + 2\lambda Re{ pt \cdot \exp{-i\Phi}}=0 </ol> La premi�re
				 * �quation exprime que c est sur la droite (radiusEnd,n)
				 * normale. La deuxi�me �quation exprime la d�finition de la
				 * direction d = \exp{i\Phi} en fonction de pt et c. La
				 * troisi�me �quation exprime que le cercle de centre c et
				 * tangeant en radiusEnd � u est �galement tangeant � un
				 * cercle de centre pt et de rayon tol, en fait on l'obtient
				 * en �crivant que |c - 0| = |c - (pt + tol*\exp{i\Phi})|. Le
				 * but de la manipe c'est de trouver les valeurs possibles (en
				 * tout deux) de l'abscisse \real{c} du centre c. En effet,
				 * ces abscisses donnent les bornes des intervalles dans
				 * lesquels le centre de l'arc d'approximation doit �tre
				 * compris pour remplir la contrainte impos�e par le point pt
				 * et la tol�rance tol.<br/> On proc�de en quatre
				 * temps.<ul><li> Dans un premier temps on suppose \lambda
				 * connu et on d�termine \cos\Phi et \sin\Phi en r�solvant le
				 * syst�me d'�quation, comme si \cos\Phi et \sin\Phi �taient
				 * deux variables ind�pendantes. Ce syst�me d'�quation peut
				 * s'�crire A\cdot X = B, o� A est une matrice 2\times 2, B un
				 * vecteur � 2 lignes, et X est l'inconnue X = [\cos\Phi
				 * \sin\Phi]^t. Pour la r�solution on utilise la r�gle de
				 * Cramer. Le r�sultat c'est que \cos\Phi et \sin\Phi sont
				 * d�termin�s comme deux rapport de polynomes \cos\Phi =
				 * detA0(\lambda)/ detA(\lambda) et \sin\Phi = detA1(\lambda)/
				 * detA(\lambda) o� detA0, detA1 et detA sont des polyn�mes de
				 * \lambda de degr� 1 calcul�s comme un d�terminant de matrice
				 * 2\times2. <li>Dans un deuxi�me temps on exprime que
				 * \cos^2\Phi + \sin^2\Phi = 1, �a donne une �quation
				 * polyn�miale Q(\lambda) = detA0^2 + detA1^2 - detA^2 = 0, o�
				 * Q est de degr� 4, et o� par construction 0 est une racine
				 * d'ordre 2 de Q. <li>Dans un troisi�me temps on d�termine
				 * les deux racines non nulle de cette �quation polyn�miale,
				 * �a donne 2 valeurs \lambda_1 et \lambda_2 possibles de
				 * \lambda.<li>Finalement il ne reste plus qu'� calculer les
				 * abscisses de c correspondant aux valeurs de \lambda:<br/>
				 * distance +
				 * \lambda\times\frac{detA0(\lambda)}{detA(\lambda)}</ul>
				 * @see <a
				 * href="http://fr.wikipedia.org/wiki/R%C3%A8gle_de_Cramer">R�gle
				 * de Cramer</a>
				 */

				//

				// abscisses du centre, � d�terminer
				double[] xCenter = new double[2];
				int xCenterCount = 0;

				Polynomial[][] A = new Polynomial[2][2];
				Polynomial[] B = new Polynomial[2];
				// premi�re& deuxi�me �quations : \Im c = 0, c'est � dire
				// elevation+\lambda\times \sin\Phi = 0
				// on a donc:
				// A00 = 0
				// A01 = \lambda
				// B0  = -elevation
				A[0][0] = new Polynomial();
				{
					double[] a01={1};
					A[0][1] = new Polynomial(1,1,a01);
				}
				{
					double[] b0={-elevation};
					B[0] = new Polynomial(0,0,b0);
				}
				// troisi�me �quation:
				// (|pt|^2 - tol^2 +2\lambda tol)
				// + 2\lambda Re{ pt \cdot \exp{-i\Phi}}=0
				// on a donc
				//A10 = -2*distance*\lambda
				//A11 = -2*elevation*\lambda
				//B1 = |pt|^2 - tol^2 +2\lambda tol
				{
					double[] a10={-2*distance};
					A[1][0] = new Polynomial(1,1,a10);
				}
				{
					double[] a11={-2*elevation};
					A[1][1] = new Polynomial(1,1,a11);
				}
				{
					double[] b1 = {ptNorm2 -sqTol, 2*tol};
					B[1] = new Polynomial(0,1,b1);
				}
				// maintenant on applique la r�gle de Cramer
				Polynomial detA = A[0][0].cMul(A[1][1])
					.cSub(A[0][1].cMul(A[1][0]));

				// on remplace dans detA A[:][0] par B
				Polynomial detA0 = B[0].cMul(A[1][1])
					.cSub(A[0][1].cMul(B[1]));

				// on remplace dans detA A[:][1] par B
				Polynomial detA1 = A[0][0].cMul(B[1])
					.cSub(B[0].cMul(A[1][0]));

				// maintenant on exprime que \cos^2\Phi + \sin^2\Phi = 1
				// en d'autres termes que P = detA0^2+detA1^2 - detA^2 = 0;
				Polynomial Q = detA0.cMul(detA0)
					.cAdd(detA1.cMul(detA1))
					.cSub(detA.cMul(detA));
				// Rappel: par construction Q est de degr� 4, et est un
				// multiple de \lambda^2
				double qa = Q.coeff(4);
				double qb = Q.coeff(3);
				double qc = Q.coeff(2);

				double[] lambda = new double[2];
				int lambdaCount = 0;

				if(qa != 0.0)
				{
					// calcul du discriminent
					double delta = qb*qb - 4*qa*qc;

					if(delta < 0)
					{
					}
					else if(delta == 0.0)
					{
						lambda[0] =-qb/2/qa;
						lambdaCount = 1;
					}
					else
					{
						delta = sqrt(delta);
						for(int i = 0;i< 2; ++i)
							lambda[i] = (-qb+(1-2*i)*delta)/2/qa;
						lambdaCount = 2;
					}
				}
				else if(qb != 0)
				{
					lambda[0] = -qc/qb;
					lambdaCount = 1;
				}

				for(int i = 0; i < lambdaCount; ++i)
				{
					double den = detA.eval(lambda[i]);
					if(den != 0.0)
					{
						double num = detA0.eval(lambda[i]);
						double cosPhi = num/den;
						xCenter[xCenterCount++] = cosPhi * lambda[i] + distance;
					}
				}

				if(xCenterCount == 2 && xCenter[0] > xCenter[1])
				{
					double temp = xCenter[0];
					xCenter[0]  = xCenter[1];
					xCenter[1]  = temp;
				}

				IntervalUnion contraintsIU = null;
				if(abs(distance) > tol)
				{
					if(xCenterCount == 2)
					{
						double[] constraints = { xCenter[0], xCenter[1]};
						contraintsIU = new IntervalUnion(constraints);
					}
				}
				else if(abs(distance) == tol)
				{
				}
				else
				{
					if(xCenterCount == 2)
					{
						double[] constraints = { Double.NEGATIVE_INFINITY, xCenter[0],
												 xCenter[1],Double.POSITIVE_INFINITY};
						contraintsIU = new IntervalUnion(constraints);
					}
				}

				if(contraintsIU != null)
				{
					centerAllowedPos.inter(contraintsIU);
				}
				else
				{
					// le point est hors contrainte, c'est foutu pour
					// aller plus loin.
					centerAllowedPos= new IntervalUnion();
					return true;
				}


/*				if(distance < tol && distance > -tol)
				{
					// le point est dans les tol�rances, il y a donc moyen
					// de creuser pour savoir si c'est possible de
					// rel�cher les contraintes.
					double x1 = distance +tol;
					double x2 = distance -tol;
					double e2 = elevation*elevation;
					double b1 = (e2 + x1*x1)/(2*x1);
					double b2 = (e2 + x2*x2)/(2*x2);
					if(b1 > b2)
					{
						double temp = b1;
						b1 = b2;
						b2 = temp;
					}
					double[] constraints = { Double.NEGATIVE_INFINITY,
											 b1,b2,Double.POSITIVE_INFINITY};
					IntervalUnion contraintsIU = new IntervalUnion(constraints);
					centerAllowedPos.inter(contraintsIU);
				}
				else
				{
					// le point est hors contrainte, c'est foutu pour
					// aller plus loin.
					centerAllowedPos= new IntervalUnion();
					return true;
				}
*/
				return false;
			}

			/**
			 * @since jPicEdt 1.6
			 */
			public String toString(){
			StringBuffer buf = new StringBuffer();
			buf.append("[");

			buf.append("tol=");
			buf.append(tol);

			buf.append(", centerAllowedPos=");
			buf.append(centerAllowedPos);

			buf.append("]");

			return buf.toString();
			}

		}// fin class ProcessPoint

		/** calcule la nouvelle valeur de endAngle / endAbscisse, apr�s
		 * v�rification que  t a progress�*/
		private void updateEndAngle(){
			bezierApproxEndPoint = (PicPoint)curBezierCurve.eval(t);
			if(mIsLine)
			{
				endAbscisse = new PicVector(radiusEnd,bezierApproxEndPoint).dot(u);
			}
			else
			{
				PicVector radiusVec =
					new PicVector(center,bezierApproxEndPoint);
				endAngle = PicVector.X_AXIS.angle(radiusVec);
			}

		}



		/** extension de l'approximation en cours de la courbe de B�zier
		 * courante lorsque cette approximation est faite par un segment de droite
		 * @since jPicEdt 1.6
		 */
		private boolean extendLine(){
			boolean relaxed = false;
			if(t < 0)
				return relaxed;

			double prevT = t;
			double max = 1.0;
			double subTol = tol*.1;

			double[] tVal = new double[4];
			double[] dVal = new double[4];


			VecPolynomial reBezierCurve = curBezierCurve.cSub(radiusEnd);
			Polynomial distanceToLine = reBezierCurve.dot(n);
			Polynomial dDistanceToLine = distanceToLine.cDerive();

			int tCount = 1;
			tVal[0] = minT;

			{
				ArrayList<PolynomialRealRoot> zeros =
					new ArrayList<PolynomialRealRoot>(2);
				if(abs(dDistanceToLine.coeff(2)) >= subTol)
				{
					double delta = dDistanceToLine.coeff(1)*dDistanceToLine.coeff(1)
						-4*dDistanceToLine.coeff(2)*dDistanceToLine.coeff(0);

					if(delta == 0.0)
					{
						zeros.add(new PolynomialRealRoot(-dDistanceToLine.coeff(1)
														 /2/dDistanceToLine.coeff(2),2));
					}
					else if(delta > 0.0)
					{
						double sqrtDelta = Math.sqrt(delta);
						double den = 2*dDistanceToLine.coeff(2);
						zeros.add(new PolynomialRealRoot(
									  (-dDistanceToLine.coeff(1) + sqrtDelta)/den));
						zeros.add(new PolynomialRealRoot(
									  (-dDistanceToLine.coeff(1) - sqrtDelta)/den));
					}
				}
				else if(abs(dDistanceToLine.coeff(1)) >= subTol)
				{
					zeros.add(new PolynomialRealRoot(
								  -dDistanceToLine.coeff(0)/dDistanceToLine.coeff(1)));
				}

				Collections.sort(zeros,PolynomialRealRoot.getSortByValueComparator());
				for(PolynomialRealRoot z : zeros)
				{
					if(z.getValue() > minT && z.getValue() <= max)
						tVal[tCount++] = z.getValue();
				}
			}
			if(tVal[tCount-1] < max)
				tVal[tCount++] = max;

			// les tCount valeurs dans tVal[] sont les param�tres
			// des extr�mas. Maintenant, on ne va garder que les extr�ma qui
			// sont dans la tol�rance tol.
			t = -1;
			{
				int iExcess = tCount;
				for(int i = 0;i < tCount; ++i)
				{
					dVal[i] = distanceToLine.eval(tVal[i]);
					if(abs(dVal[i]) < tol)
					{
						t = tVal[i];
					}
					else
					{
						iExcess = i;
						break;
					}
				}

				if(iExcess > 0 && iExcess < tCount)
				{
					// d�termine le point auquel tol est atteint dans
					// l'intervalle [t , tVal[iExcess]]
					double signedTol;
					int sign;
					if(dVal[iExcess] < 0)
					{
						signedTol = GREEDINESS_WEIGHT*-tol
							+(1-GREEDINESS_WEIGHT)*dVal[iExcess-1];
						sign = 1; // on veut �tre >= signedTol
					}
					else
					{
						signedTol = GREEDINESS_WEIGHT*tol
							+(1-GREEDINESS_WEIGHT)*dVal[iExcess-1];
						sign = -1; // on veut �tre <= signedTol.
					}
					Polynomial deltaDistToLine = distanceToLine.cSub(signedTol);

					double[] zero =
						deltaDistToLine.findZeroInInterval(t,tVal[iExcess],tol*.1,sign);


					if(zero == null)
						Log.error("Bogue dans MultiCurveToArc.java");
					else
					{
						t = tVal[iExcess] = zero[0];
						dVal[iExcess] =  signedTol;
						tCount = iExcess + 1;
					}
				}
			}

			// si on n'a pas avanc�, ce n'est pas la peine d'aller plus loin.
			if(t <= prevT)
			{

				t = prevT;
				if(prevCurveApproximation != null)
				{

					setTo(prevCurveApproximation);
					prevCurveApproximation = null;
				}
				return false;
			}

			/* ici on a avanc�, met � jour endAngle / endAbscisse et mIsLine
			   en cons�quence */
			updateEndAngle();

			if(t == 1.0)
			{
				// pour finaliser le traitement de la courbe de B�zier
				// courante, ont fait rentrer les points extr�maux dans la
				// liste des contraintes pass�es.
				for(int j = 0; j < tCount; ++j)
				{
					PicPoint pt = (PicPoint)curBezierCurve.eval(tVal[j]);
					constraintPoints.add(pt);
				}
				tCount = 0;
			}



			/* on a avanc�: v�rifie si on peut relaxer les contraintes en
			 ajustant le rayon et le centre de l'arc*/
			ProcessPoint processPoint = new ProcessPoint(tol);

			// constaintPoints c'est issu des points extr�me des courbes de
			// B�zier pr�c�dent qu'on a approxim� par le m�me arc/segment.
			for(PicPoint pt : constraintPoints)
			{
				PicVector radiusVec = new PicVector(radiusEnd,pt);
				double distance = radiusVec.dot(n);
				double elevation = radiusVec.dot(u);
				if(processPoint.processPoint(distance,elevation))
					break;
			}
			if(!processPoint.centerAllowedPos.isEmpty())
			{
				Polynomial elevationToLine = reBezierCurve.dot(u);
				for(int j = 0; j < tCount; ++j)
				{
					double distance  = dVal[j];
					double elevation = elevationToLine.eval(tVal[j]);
					if(processPoint.processPoint(distance,elevation))
						break;
				}

				relaxed = postExtension(processPoint.centerAllowedPos);
			}








			return relaxed;
		}

		/** extension de l'approximation lorsque cette approximation est faite
		 * par un arc de cercle suffisamment plat par rapport � la tol�rance
		 * @since jPicEdt 1.6
		 */
		private boolean extendArc(){
			boolean relaxed = false;
			if(t < 0)
				return relaxed;

			double prevT = t;
			double max = 1.0;
			double sqTol = tol*tol;
			double absRadius = abs(radius);
			double sqRadiusTolMin0 = absRadius - tol;
			sqRadiusTolMin0 *= sqRadiusTolMin0;


			double sqRadiusTolMax0 = absRadius + tol;
			sqRadiusTolMax0 *= sqRadiusTolMax0;


			double subSqTol = sqTol*.1;


			double[] tVal = new double[8];
			double[] dVal = new double[8];





			// centerP est la courbe de B�zier dans le rep�re (center, n , u)
			VecPolynomial centerP = curBezierCurve.cSub(center);
			Polynomial sqRadiusPol;

			// sqRadiusPol = ||P-center||^2
			sqRadiusPol = centerP.dot(centerP);



			Polynomial dSqRadiusPol = sqRadiusPol.cDerive();

			int tCount = 1;
			tVal[0] = minT;
			{
				ArrayList<PolynomialRealRoot> zeros =
					dSqRadiusPol.findZerosInInterval(minT,max,sqTol*(1-minT));

				Collections.sort(zeros,PolynomialRealRoot.getSortByValueComparator());
				for(PolynomialRealRoot z : zeros)
				{
					if(z.getValue() > minT)
						tVal[tCount++] = z.getValue();
				}
				if(tVal[tCount-1] < max)
					tVal[tCount++] = max;
			}

			// les tCount valeurs dans tVal[] sont les param�tres
			// des extr�mas. Maintenant, on ne va garder que les extr�ma qui
			// sont dans la tol�rance sqTol.
			t = -1;
			{
				int iExcess = tCount;
				for(int i = 0;i < tCount; ++i)
				{
					dVal[i] = sqRadiusPol.eval(tVal[i]);
					if(dVal[i] > sqRadiusTolMin0
						&& dVal[i] < sqRadiusTolMax0)
					{
						t = tVal[i];
					}
					else
					{
						iExcess = i;
						break;
					}
				}
				if(iExcess > 0  && iExcess < tCount)
				{
					// d�termine le point auquel sqRadiusPolTol est atteint dans
					// l'intervalle [t , tVal[iExcess]]
					Polynomial deltaSqDistanceToArc = new Polynomial(sqRadiusPol);
					double signedSqDtaTol;
					int sign;
					if(dVal[iExcess] <= sqRadiusTolMin0)
					{
						signedSqDtaTol = GREEDINESS_WEIGHT*sqRadiusTolMin0
							+(1-GREEDINESS_WEIGHT)*dVal[iExcess-1];
						sign = +1; // on veut �tre >= signedSqDtaTol
					}
					else
					{
						signedSqDtaTol = GREEDINESS_WEIGHT*sqRadiusTolMax0
							+(1-GREEDINESS_WEIGHT)*dVal[iExcess-1];
						sign = -1; // on veut �tre <= signedSqDtaTol
					}
					deltaSqDistanceToArc.sub(signedSqDtaTol);

					double[] zero = deltaSqDistanceToArc
						.findZeroInInterval(t,tVal[iExcess],sqTol*.1,sign);



					if(zero == null)
						Log.error("Bogue dans MultiCurveToArc.java");
					else
					{
						t = tVal[iExcess] = zero[0];
						dVal[iExcess] =  signedSqDtaTol;
						tCount = iExcess + 1;
					}
				}
			}


			// si on n'a pas avanc�, ce n'est pas la peine d'aller plus loin.
			if(t <= prevT)
			{

				t = prevT;
				if(prevCurveApproximation != null)
				{

					setTo(prevCurveApproximation);
					prevCurveApproximation = null;
				}
				return false;
			}

			// ici, on a avanc�, met donc � jour endAngle / endAbscisse,
			// et mIsLine
			updateEndAngle();
			if(t == 1.0)
			{
				// pour finaliser le traitement de la courbe de B�zier
				// courante, ont fait rentrer les points extr�maux dans la
				// liste des contraintes pass�es.
				for(int j = 0; j < tCount; ++j)
				{
					PicPoint pt = (PicPoint)curBezierCurve.eval(tVal[j]);
					constraintPoints.add(pt);
				}
				tCount = 0; /* pour ne pas traiter deux fois les m�mes points
							   en aval lorsque on recherchera � relacher les
							   contraintes. */
			}


			// on a avanc�, v�rifie maintenant si on peut relaxer les
			// contraintes en ajustant le rayon et le centre de l'centerP
			ProcessPoint processPoint = new ProcessPoint(tol);

			// constaintPoints c'est issu des points extr�me des courbes de
			// B�zier pr�c�dent qu'on a approxim� par le m�me centerP/segment.
			for(PicPoint pt : constraintPoints)
			{
				PicVector radiusVec = new PicVector(radiusEnd,pt);
				double distance = radiusVec.dot(n);
				double elevation = radiusVec.dot(u);
				if(processPoint.processPoint(distance,elevation))
					break;
			}
			if(!processPoint.centerAllowedPos.isEmpty())
			{
				PicVector antiRadiusVec = new PicVector(radiusEnd,center);
				for(int j = 0; j < tCount; ++j)
				{
					PicVector chord = centerP.eval(tVal[j]).add(antiRadiusVec);
					double distance  = chord.dot(n);
					double elevation = chord.dot(u);
					if(processPoint.processPoint(distance,elevation))
						break;
				}

				relaxed = postExtension(processPoint.centerAllowedPos);
			}
			return relaxed;

		}

		/** extension de l'approximation lorsque cette approximation est faite
		 * par un arc de cercle dont le diam�tre est inf�rieure  � la tol�rance
		 * @since jPicEdt 1.6
		 */
		private boolean extendSharpArc(){
			boolean relaxed = false;
			if(t < 0)
				return relaxed;

			double prevT = t;
			double max = 1.0;
			double sqTol = tol*tol;

			double[] tVal = new double[8];
			double[] dVal = new double[8];

			// centerP est la courbe de B�zier dans le rep�re (center, n , u)
			VecPolynomial centerP = curBezierCurve.cSub(center);
			Polynomial sqRadiusPol = centerP.dot(centerP);

			Polynomial dSqRadiusPol = sqRadiusPol.cDerive();

			int tCount = 1;
			tVal[0] = minT;

			{
				ArrayList<PolynomialRealRoot> zeros =
					dSqRadiusPol.findZerosInInterval(minT,max,sqTol*(1-minT));

				Collections.sort(zeros,PolynomialRealRoot.getSortByValueComparator());
				for(PolynomialRealRoot z : zeros)
				{
					if(z.getValue() > minT)
						tVal[tCount++] = z.getValue();
				}
				if(tVal[tCount-1] < max)
					tVal[tCount++] = max;
			}

			// les tCount valeurs dans tVal[] sont les param�tres
			// des extr�mas. Maintenant, on ne va garder que les extr�ma qui
			// sont dans la tol�rance sqTol.
			t = -1;
			{
				int iExcess = tCount;
				for(int i = 0;i < tCount; ++i)
				{
					dVal[i] = sqRadiusPol.eval(tVal[i]);
					if(dVal[i] <= sqTol)
					{
						t = tVal[i];
					}
					else
					{
						iExcess = i;
						break;
					}
				}
				if(iExcess > 0 && iExcess < tCount)
				{
					// d�termine le point auquel sqDistanceToArcTol est atteint dans
					// l'intervalle [t , tVal[iExcess]]
					Polynomial deltaSqRadius =
						new Polynomial(sqRadiusPol).sub(sqTol);

					double[] zero = deltaSqRadius
						.findZeroInInterval(t,tVal[iExcess],sqTol*.1,-1);

					if(zero == null)
						Log.error("Bogue dans MultiCurveToArc.java");
					else
					{
						t = tVal[iExcess] = zero[0];
						dVal[iExcess] =  sqTol;
						tCount = iExcess + 1;
					}
				}
			}


			// si on n'a pas avanc�, ce n'est pas la peine d'aller plus loin.
			if(t <= prevT)
			{
				t = prevT;
				return false;
			}



			/* au lieu d'appeler updateEndAngle() classique on d�termine l'arc
			 * d'approximation comme l'arc qui passe par radiusEnd en �tant
			 * tangent en u, et qui passe pas bezierApproxEndPoint en �tant
			 * tangent � la tangente � la courbe de b�zier en
			 * bezierApproxEndPoint  */
			bezierApproxEndPoint = (PicPoint)curBezierCurve.eval(t);
			if(!bezierApproxEndPoint.equals(radiusEnd))
			{
				/* on d�termine n1 comme le vercteur normale � la courbe de B�zier
				 * courante en bezierApproxEndPoint. Voir <a
				 * href="http://fr.wikipedia.org/wiki/Courbure">Courbure</a> */
				PicVector n1;
				{
					VecPolynomial dCurBezierCurve = curBezierCurve.cDerive();
					VecPolynomial d2CurBezierCurve = dCurBezierCurve.cDerive();
					PicVector u1 = dCurBezierCurve.eval(t); //  u1 = (dP/dt)(t)
					u1.normalize();
					n1 =  d2CurBezierCurve.eval(t);
					n1.add(u1,-u1.dot(n1));
					n1.normalize();
				}
				/* on r�sout radiusEnd + radius*n = bezierApproxEndPoint+r1*n1 par la
				 * m�thode de Cramer.
				 */
				{
					double den = n.det(n1);
					if(den == 0.0)
						mIsLine = true;
					else
					{
						PicVector b = new PicVector(radiusEnd,bezierApproxEndPoint);
						radius =  b.det(n1)/den;
						mIsLine  = abs(radius) >= maxRadius;
					}

					if(mIsLine)
					{
						boolean ccw = n.det(u)< 0;
						u = new PicVector(radiusEnd,bezierApproxEndPoint);
						endAbscisse = u.norm();
						u.scale(1/endAbscisse);
						if(ccw)
							n = u.cMIMul();
						else
							n = u.cIMul();
					}
					else
					{
						center = (new PicPoint(radiusEnd)).translate(n,radius);
						endAngle = PicVector.X_AXIS.angle(n1.inverse());
					}

				}
			}
			if(t == 1.0)
			{
				// pour finaliser le traitement de la courbe de B�zier
				// courante, ont fait rentrer les points extr�maux dans la
				// liste des contraintes pass�es.
				for(int j = 0; j < tCount; ++j)
				{
					PicPoint pt = (PicPoint)curBezierCurve.eval(tVal[j]);
					constraintPoints.add(pt);
				}
			}


			/* dans le cas de l'arc aigu on n'essait pas de rel�cher les
			   contraintes. Donc ici, rien... */

			return relaxed;

		}

		/**
		 * D�cide ou non de modifier la position de centre de l'arc
		 * d'approximation (ou de remplacer cet arc par un segment de droite,
		 * ou vice versa) selon le resultat du traitement par la class
		 * ProcessPoint de toutes les contraintes.
		 * @since jPicEdt 1.6
		 */
		boolean postExtension(IntervalUnion centerAllowedPos){
			boolean relaxed = false;

			Interval largestInterval = null;
			double   largestIntervalSpan = 0;
			double   newRadius = Double.NaN;
			for(Interval e : centerAllowedPos)
			{
				double span;
				if(e.getMax() < Double.POSITIVE_INFINITY
				   && e.getMin() > Double.NEGATIVE_INFINITY)
					span = e.getMax() - e.getMin();
				else
					span = Double.POSITIVE_INFINITY;
				if(span > largestIntervalSpan)
				{
					largestIntervalSpan = span;
					largestInterval = e;
				}

			}
			if(largestInterval != null)
			{
				if(largestIntervalSpan < Double.POSITIVE_INFINITY)
				{
					// relaxation des contraintes
					relaxed = largestIntervalSpan != 0;
					if(relaxed)
						newRadius = largestInterval.getMin()+.5*largestIntervalSpan;
				}
				//  v--- � partir d'ici l'une au moins des bornes est infinie
				else if(largestInterval.getMin() > Double.NEGATIVE_INFINITY)
				{
					relaxed = true;
					if(largestInterval.getMin() > 0)
					{
						// cas:  0 min max=+infini
						// on veut prendre newRadius = 2*min, en evitant que
						// multiplier min par 2 cause le d�passement de maxRadius
						newRadius = largestInterval.getMin();
						if(newRadius < maxRadius && 2*newRadius >= maxRadius)
							newRadius = newRadius*.1
								+ maxRadius*.9;
						else
							newRadius *= 2;
					}
					else
					{
						// cas:  min  0 max=+infini
						newRadius = 0.5*largestInterval.getMin();
					}
				}
				else if(largestInterval.getMax() < Double.POSITIVE_INFINITY)
				{
					relaxed = true;
					if(largestInterval.getMax() < 0)
					{
						// cas min=-infini max 0
						// on veut prendre newRadius = 2*max, en evitant que
						// multiplier min par 2 cause le d�passement de
						// maxRadius en valeur absolue
						newRadius = largestInterval.getMax();
						if(-newRadius < maxRadius && -2*newRadius >= maxRadius)
							newRadius = newRadius*.1
								+ maxRadius*-.9;
						else
							newRadius *= 2;
					}
					else
					{
						// cas min=-infini 0 max
						newRadius = .5*largestInterval.getMax();
					}
				}
				else
				{
					// les deux bornes seraient infinies
					Log.error("bogue: �a ne devrait pas passer par l�");
				}
			}

			// si on est d�j� dans le cas d'un arc de cercle, et qu'on y
			// reste, �a ne change rien, donc c'est comme si on ne
			// relaxait pas.
			boolean willBeLine = abs(newRadius) >= maxRadius;
			if(willBeLine && mIsLine)
				relaxed = false;

			if(relaxed)
			{
				/* on sauvegarde l'�tat actuel, juste au cas o� la relaxation
				   donnerait de moins bon r�sultat */
				prevCurveApproximation = new CurveApproximation(this);
				if(willBeLine)
					mIsLine = true;
				else
				{
					radius = newRadius;
					center = new PicVector(radiusEnd).add(n,radius);
				}
			}

			return relaxed;
		}

		/** m�thode pour conclure l'extension lorsque c'est un arc
		 * @return l'approximation
		 * @since jPicEdt 1.6
		 */
		protected AbstractElement getArc(){
			if(mIsLine)
			{
				Log.error("Ne pas appeler getArc dans ce contexte");
				return null;
			}
			else
			{
				// le signe `!=' est un ou-exclusif.
				boolean counterClockWise = (n.det(u) < 0) != (radius < 0);
				PicVector chord = new PicVector(radiusEnd,getEndPoint());
				if(chord.norm2() <=
					MAX_SQUARE_TAN_FLAT_ANGLE * radius * radius)
				{
					// on replace l'arc par un segment de droite.
					mIsLine  = true;
					PicMultiCurve ret = new PicMultiCurve();
					ret.addPoint(radiusEnd);
					ret.lineTo(endPoint);
					u = chord.normalize();
					if(counterClockWise)
						n = u.cMIMul();
					else
						n = u.cIMul();
					endDir = u;
					return ret;
				}
				else
				{
					double absRadius;
					/* startAngle est l'angle du rayon de d�part dans la base
					 * canonique, mais entre-temps le signe du rayon a pu
					 * s'invers� ce qui fait un saut de PI, c'est pourquoi on
					 * recalcule cet angle comme endRadiusStartAngle.
					 *
					 * On pourrait aussi aussi le recalculer comme l'angle
					 * que fait le vecteur centre~radiusEnd relativement �
					 * PicVector.X_ASIS ce qui serait plus long.
					 */
					double endRadiusStartAngle;
					if(radius < 0)
					{
						absRadius = -radius;
						if(startAngle >= 0)
							endRadiusStartAngle = startAngle - PI;
						else
							endRadiusStartAngle = startAngle + PI;
					}
					else
					{
						endRadiusStartAngle = startAngle;
						absRadius = radius;
					}


					PicPoint pt1;
					PicPoint pt2;
					PicPoint pt3;
					if(counterClockWise)
					{
						/*				 	    + pt3

						  				+ center

						  		+ pt1           + pt2

						   Si on a tourn� dans le sens trigo, alors on
						   fait en sorte que la base pt1pt2 pt2pt3 soit directe

						 */
						pt1 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,-absRadius)
							.translate(PicVector.Y_AXIS,-absRadius);
						pt2 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,absRadius)
							.translate(PicVector.Y_AXIS,-absRadius);
						pt3 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,absRadius)
							.translate(PicVector.Y_AXIS,absRadius);
					}
					else
					{
					   	/*
						  	   	+ pt1           + pt2

						  	   			+ center

							   				   	+ pt3

						   Si on a tourn� dans le sens *anti*-trigo, alors on
						   fait en sorte que la base pt1pt2 pt2pt3 soit non
						   directe
						 */
						pt1 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,-absRadius)
							.translate(PicVector.Y_AXIS,absRadius);
						pt2 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,absRadius)
							.translate(PicVector.Y_AXIS,absRadius);
						pt3 = (new PicPoint(center))
							.translate(PicVector.X_AXIS,absRadius)
							.translate(PicVector.Y_AXIS,-absRadius);
					}
					PicEllipse ret =  new PicEllipse(pt1,pt2,pt3,PicEllipse.OPEN);

					if(counterClockWise)
					{
						ret.setAngleStart(Math.toDegrees(endRadiusStartAngle));
						ret.setAngleEnd(Math.toDegrees(endAngle));
					}
					else
					{
						ret.setAngleStart(Math.toDegrees(-endRadiusStartAngle));
						ret.setAngleEnd(Math.toDegrees(-endAngle));
					}

					return ret;
				}

			}
		}

		/** m�thode pour conclure l'extension, lorsque c'est un segment de
		 * droite
		 * @return l'apprixmimation
		 * @since jPicEdt 1.6
		 */
		protected PicMultiCurve  getLine(){
			if(!mIsLine)
			{
				Log.error("Ne pas appeler getLine dans ce contexte");
				return null;
			}
			else
			{
				PicMultiCurve ret = new PicMultiCurve();
				ret.addPoint(new PicPoint(radiusEnd));
				ret.lineTo(getEndPoint());
				return ret;
			}
		}

		/** @return l'approximation, apr�s extension
		 * @since jPicEdt 1.6
		 */
		public AbstractElement getExtension(){
			if(mIsLine)
				return getLine();
			else
				return getArc();
		}


		/** @return le point final de l'approximation. On partira de ce point
		 *	pour faire l'approximation suivante
		 * @since jPicEdt 1.6
		 */
		public PicPoint getEndPoint(){
			if(endPoint == null)
			{
				if(mIsLine)
				{
					endDir   = u;
					endPoint = (new PicVector(radiusEnd)).add(u,endAbscisse);
				}
				else
				{
					double endRadiusStartAngle;
					if(radius < 0)
						endRadiusStartAngle = startAngle + PI;
					else
						endRadiusStartAngle = startAngle;

					PicVector radiusVec = (new PicVector(center,radiusEnd))
						.rotate(endAngle-endRadiusStartAngle);
					endPoint = new PicPoint(center).translate(radiusVec);
					radiusVec.normalize();
					if(n.det(u) < 0)
						/* le rep�re (n,u) est *non* direct, donc la direction tangente
						 finale est obtenu par une rotation de -pi/2 de
						 -radiusVec, soit une rotation de +pi/2 de radiusVec*/
						endDir = radiusVec.iMul();
					else
						/* le rep�re (n,u) est *direct*, donc la direction tangente
						 finale est obtenu par une rotation de +pi/2 de
						 -radiusVec*/
						endDir = radiusVec.inverse().iMul();
				}
			}
			return endPoint;
		}

		/**
		 * Initialisation de la direction tangente u, de la direction normale
		 * n au d�but d'une nouvelle approximation par arcs&amp;segments qui
		 * ne commence pas de fa�on tangente � une approximation pr�c�dente.
		 * @since jPicEdt 1.6
		 * @see <a href="http://fr.wikipedia.org/wiki/Courbure">(Wikipedia)
		 * Courbure</a>
		 */
		void finalizeCornerStart(PicPoint pt0,PicPoint pt1,PicPoint pt2,PicPoint pt3){
			constraintPoints = new ArrayList<PicPoint>();
			setBezierCurve(pt0,pt1,pt2,pt3);
			computeNewBase(pt0,pt1,pt2);
			endPoint    = null;
			endDir      = null;
		}

		/**
		 * Initialisation de la direction tangente u, de la direction normale
		 * n, ainsi que du centre et du rayon radius au d�but d'une nouvelle
		 * approximation par arcs&amp;segments.
		 * @since jPicEdt 1.6
		 */
		void computeNewBase(PicPoint pt0,PicPoint pt1,PicPoint pt2){
			prevCurveApproximation = null;

			/** On rappelle que si <math>P(t) = pt0 \times (1 - t)^3 + 3 pt1 t
 			 *  \times (1 - t)^2 + 3 pt2 t^2 (1 - t) + pt3 t^3</math><br>
 			 *  Alors:<br> <math> \frac{\mathrm{d}P}{\mathrm{d}t}(t=0)=3(pt1 -
 			 *  pt0)</math>, et<br> <math>
 			 *  \frac{\mathrm{d}^{2}P}{\mathrm{d}t^2}(t=0)=6 pt0 - 12 pt1 + 6
 			 *  pt2</math>
			 * <br/>
			 * on se base sur la formule donnant la courbure suivante:
			 * \(\frac{\mathrm{d}^2\mathbf{P}}{\mathrm{d}s^2} =
			 * \frac{1}{\left\|\dot{\mathbf{P}}(t)\right\|^2}\left
 			 *  (\ddot{\mathbf{P}}(t) -
			 * \frac{\dot{\mathbf{P}}(t)}{\left\|\dot{\mathbf{P}}(t)\right\|^2}
			 * \langle \dot{\mathbf{P}}(t)\mid \ddot{\mathbf{P}}(t)\rangle \right)\)
			 */
			u = new PicVector(pt0,pt1); // ici u = (dP/dt)(t=0) / 3
			double norm2_drdt = u.norm2() * 9; // ||(dP/dt)(t=0)||^2
			n =  new PicVector(pt1,pt2);
			n.subtract(u);// n = pt0 - 2pt1 + pt2 = (d^2P/dt^2)(t=0) / 6
			n.scale(6); // ici = n = (d^2P/dt^2)(t=0)
			u.normalize(); // u = (dP/dt)(t=0) / ||(dP/dt)(t=0)||
			n.add(u,-u.dot(n));
			n.scale(1.0/norm2_drdt); /* ici n = (d^2P/ds^2)(s=0), o� s(t) est
									  * l'abscisse curviligne */

			double    sqCurvature;

			sqCurvature = n.norm2();

			center = new PicVector(pt0);
			mIsLine = (sqCurvature * maxRadius*maxRadius <= 1);


			if(!mIsLine)
			{
				/* Ici n n'est pas encore normalis�, donc ||n||=1/R o� R est
				 * le rayon de courbure. Le centre vaut RadiusEnd + R*n', avec
				 * n'=n /||n|| la valeur de n apr�s normalisation. C'est �
				 * dire que center = RadiusEnd + (1/||n||)* (n/||n||), soit
				 * center = RadiusEnd + n/||n||^2
				 */
				double sqCurvatureRadius = 1/sqCurvature;
				center.translate(n,sqCurvatureRadius);
				PicVector radiusVec = new PicVector(center,radiusEnd);
				startAngle = PicVector.X_AXIS.angle(radiusVec);
				endAngle = startAngle; // initialement l'extension de l'arc est nulle
				n.normalize();
				radius   = -radiusVec.dot(n);
			}
			else
			{
				endAbscisse = 0; // initialement l'extention du segment est nulle.
				PicVector segmentNormalVect = u.cIMul();
				// on garde la m�me direction vers le centre de courbure, car
				// ce n'est qu'une approximation d'arc
				if(segmentNormalVect.dot(n) < 0)
					segmentNormalVect.scale(-1);
				n = segmentNormalVect;
				radius = Double.POSITIVE_INFINITY;
			}

		}

		/** re-initialisation d'une approximation par arc alors que la courbe
		 * de B�zier courante a d�j� �t� partiellement approxim�e
		 * @since jPicEdt 1.6
		 */
		void smoothRestart(PicPoint pt0,PicPoint pt1,PicPoint pt2){
			 /* on oublie les contraintes sur la derni�re approximation*/
			constraintPoints = new ArrayList<PicPoint>();
			minT = t;
			radiusEnd   = getEndPoint();
			computeNewBase(pt0,pt1,pt2);


			endPoint = null;
			endDir   = null;
		}


		/**
		 * re-initialisation d'une approximation par arc, alors que
		 * l'approximation est d�j� en cours.
		 * @since jPicEdt 1.6
		 */
		void cornerRestart(PicPoint pt0,PicPoint pt1,PicPoint pt2,PicPoint pt3){
			radiusEnd   = getEndPoint();
			finalizeCornerStart(pt0,pt1,pt2,pt3);
		}


		/**
		 * Construit un CenterAdjustment permettant d'ajuster la position du
		 * centre d'un arc de cercle approximant une courbe de B�zier.<br> �
		 * la construction la valeur initiale du centre est d�termin�e comme
		 * le centre du cercle tangeant en pt0 et de m�me rayon de courbure au
		 * point pt0 que la courbe de B�zier � appriximer
		 * @param pt0 premier point de contr�le de la coubre de B�zier � approximer
		 * @param pt1 deuxi�me point de contr�le de la coubre de B�zier � approximer
		 * @param pt2 troisi�me point de contr�le de la coubre de B�zier �
		 * approximer
		 * @param tol tol�rance sur les distances (plus c'est petit et plus on
		 * approche de pr�s la courbe de B�zier).
		 * @param maxRadius tol�rance sur les rayon (plus c'est
		 * grand et plus on accepte des arcs de cercle de grand rayon).
		 * @see <a href="http://fr.wikipedia.org/wiki/Courbe_de_B%C3%A9zier">
		 * (Wikipedia) Courbes de B�zier</a>
		 * @see <a href="http://fr.wikipedia.org/wiki/Courbure">(Wikipedia)
		 * Courbure</a>
		 * @since jPicEdt 1.6
		 */
		public CenterAdjustment(PicPoint pt0,PicPoint pt1,PicPoint pt2,PicPoint pt3,
						 double tol,double maxRadius){
			this.tol    = tol;
			this.maxRadius = maxRadius;
			radiusEnd   = new PicVector(pt0);

			finalizeCornerStart(pt0,pt1,pt2,pt3);
		}

	}
	enum MultiCurveState{
		MCS_INIT, MCS_APPROX_EXTEND, MCS_SMOOTH_ON_NEW_CURVE, MCS_SMOOTH_RESTART, MCS_CORNER_RESTART;
	};

	/** M�thode permettant de convertir une cha�ne de courbes cubiques de
	 *  B�zier en une cha�ne d'arc de cercle et de segment de droite (cha�n�s
	 *  entre eux par un conteneur PicGroup).
	 * @param multiCurve la cha�ne de courbes cubiques de B�zier dont une
	 * partie est � convertir en cha�ne d'arc de de segment de droites.
	 * @param tol tol�rance sur les distances, plus c'est petit plus
	 * l'apprixation par arc&amp;segments de droite sera proche de la courbe
	 * originale, mais aussi plus la cha�ne d'arcs&amp;segments aura de
	 * composantes.
	 * @param maxRadius plus c'est grand et plus on accepte d'avoir des
	 * cercles de grand rayon (les arcs de cercles dont le rayon est sup�rieur
	 * ou �gale � maxRadius sont remplac� par des segments de droite).
	 * @param firstCtlPtIndex index du premier point de contr�le de la partie
	 * de la cha�ne de courbes cubiques de B�zier que l'on veut approximer. Ce
	 * point doit �tre un point de subdivision.
	 * @param lastCtlPtIndex index du dernier point de contr�le de la partie
	 * de la cha�ne de courbes cubiques de B�zier que l'on veut approximer. Ce
	 * point doit �tre un point de subdivision.
	 * @return le r�sultat de la conversion, c'est � dire un PicGroup dont les
	 * �l�ments sont soit des PicEllipse arc de cercle, soit des
	 * PicMultiCurves lignes polygonales.
	 * @since jPicEdt 1.6
	 */
	public static PicGroup convert(
		AbstractCurve multiCurve,
		double tol,double maxRadius,
		int firstCtlPtIndex,
		int lastCtlPtIndex){

		PicGroup ret = new PicGroup();

		PicPoint pt0 = new PicPoint();
		PicPoint pt1 = new PicPoint();
		PicPoint pt2 = new PicPoint();
		PicPoint pt3 = new PicPoint();
		PicMultiCurve polygonalLine = null;
		CenterAdjustment centerAdjustment = null;
		VecPolynomial currentBezierCurve2 = new VecPolynomial(0,2);
		VecPolynomial currentBezierCurve1 = new VecPolynomial(0,1);

		if(multiCurve.getNearestSubdivisionPoint(firstCtlPtIndex)
		   != firstCtlPtIndex)
			Log.error("param�tre firstCtlPtIndex invalide");

		int     i = firstCtlPtIndex;
		MultiCurveState multiCurveState = MultiCurveState.MCS_INIT;
		double  t = 0;

		for(;;)
		{

			switch(multiCurveState)
			{
			case MCS_INIT:
			case MCS_SMOOTH_ON_NEW_CURVE:
			case MCS_CORNER_RESTART:
				if(i+3 > lastCtlPtIndex)
					return ret;
				pt0 = multiCurve.getBezierPt(i,pt0);
				pt1 = multiCurve.getBezierPt(i+1,pt1);
				pt2 = multiCurve.getBezierPt(i+2,pt2);
				pt3 = multiCurve.getBezierPt(i+3,pt3);
				i += 3;
				if(centerAdjustment == null)
					centerAdjustment =
						new CenterAdjustment(pt0,pt1,pt2,pt3,tol,maxRadius);
				else
					centerAdjustment.setBezierCurve(pt0,pt1,pt2,pt3);
				{

					PicPoint[] b = { pt1, pt2, pt3};
					currentBezierCurve2.setALaBernstein(2,b);
					PicPoint[] c = { pt2, pt3};
					currentBezierCurve1.setALaBernstein(1,c);

				}
				break;
			default:
				// c'est normal qu'on puisse passer par l�.
				break;
			}
			switch(multiCurveState)
			{
			case MCS_INIT:
				centerAdjustment = new CenterAdjustment(pt0,pt1,pt2,pt3,tol,maxRadius);
				multiCurveState = MultiCurveState.MCS_APPROX_EXTEND;
				break;
			case MCS_APPROX_EXTEND:
				Log.error("approx extend: On ne devrait jamais passer par l� !!");
				break;
			case MCS_SMOOTH_RESTART:
				multiCurveState = MultiCurveState.MCS_APPROX_EXTEND;
				break;
			case MCS_CORNER_RESTART:
				centerAdjustment.cornerRestart(pt0,pt1,pt2,pt3);
				multiCurveState = MultiCurveState.MCS_APPROX_EXTEND;
				break;
			case MCS_SMOOTH_ON_NEW_CURVE:
				break;
			default:
				Log.error("default: On ne devrait jamais passer par l� !!");
				break;
			}
			t = centerAdjustment.getBezierEndParameter();
			centerAdjustment.extend();


			if(centerAdjustment.getBezierEndParameter() >= 0.0)
			{


				if(centerAdjustment.getBezierEndParameter() < 1.0)
				{
					if(centerAdjustment.getBezierEndParameter() > t)
					{

						// on a un peu progress�, mais �a a bloqu� avant qu'on
						// puisse atteindre t == 1.0, alors on ajoute
						// l'approximation qu'on a pu faire et puis on
						// re-initialise
						ret.add(centerAdjustment.getExtension());
						t  = centerAdjustment.getBezierEndParameter();
						pt0 = (PicPoint)centerAdjustment.getBezierCurve().eval(t);
						pt1 = (PicPoint)currentBezierCurve2.eval(t);
						pt2 = (PicPoint)currentBezierCurve1.eval(t);

						// le red�marrage est forc�ment lisse, vu qu'on c'est
						// arr�t� au milieu d'une courbe de B�zier, donc en un
						// point parfaitement diff�rentiable.
						centerAdjustment.smoothRestart(pt0,pt1,pt2);
						multiCurveState = MultiCurveState.MCS_SMOOTH_RESTART;
					}
					else
					{
						// c'est pas fini, mais on n'arrive pas � aller plus loin
						if(t > 0.0)
							ret.add(centerAdjustment.getExtension());
						return ret;
					}
				}
				else
				{
					t = 0.0;
					// on est all� jusqu'au bout de la coubre courante
					if(i+3 <= lastCtlPtIndex)
					{

						// il reste encore des courbes de B�zier, on essaie
						// d'�tendre encore plus si le dernier point est lisse,
						// sinon on ajoute l'approximation courante, et on
						// repart en coin.
						if(multiCurve.isSmooth(multiCurve.pointToSegmentIndex(i)))
						{
							multiCurveState = MultiCurveState.MCS_SMOOTH_ON_NEW_CURVE;
						}
						else
						{
							ret.add(centerAdjustment.getExtension());
							multiCurveState = MultiCurveState.MCS_CORNER_RESTART;
						}
					}
					else
					{
						// c'est fini, on r�cup�re la mise, et on s'en va.
						ret.add(centerAdjustment.getExtension());
						return ret;
					}
				}
			}
			else
			{
				Log.error("Blocage bogueux");
				return ret;
			}
		}

	}//fin de PicGroup convert(...)

	/** fonction principale, �a ne sert qu'� faire un petit test
	 * @since jPicEdt 1.6
	 */
	public static void main(String[] argv){
		PicMultiCurve p = new PicMultiCurve();

		p.addPoint(new PicPoint());

		p.curveTo(new PicPoint(3,5),
				  new PicPoint(7,5),
				  new PicPoint(10,0)
			);
		p.scale(0,0,5,5);

		System.out.println("p="+p.toString());


		PicGroup mta = MultiCurveToArc.convert(
			p,0.01,1/100.0,
			p.getFirstPointIndex(),
			p.getLastPointIndex());

		System.out.println("mta="+mta.toString());

	}
}

/// MultiCurveToArc.java ends here
