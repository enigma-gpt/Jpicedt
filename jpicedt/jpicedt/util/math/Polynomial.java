// Polynomial.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: Polynomial.java,v 1.5 2013/03/27 06:49:46 vincentb1 Exp $
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

// Installation:

/// Code:
package jpicedt.util.math;
import  jpicedt.Log;
import java.util.*;
//import ORG.netlib.math.complex.Complex;


import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/** Polyn�me r�el monovari�.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class Polynomial implements Cloneable
{
	double[] coefficients;
	int      leastDegree =0;
	int      degree = -1;


	// constructeurs --------------------------------------------------------
//-	public Polynomial(Polynomial x){
//-		degree = x.degree;
//-		leastDegree = x.leastDegree;
//-		if( ! (x.coefficients == null))
//-		{
//-			coefficients = new double [x.coefficients.length];
//-			for(int i = 0; i < x.coefficients.length; ++i)
//-				coefficients[i] = x.coefficients[i];
//-		}
//-	}
//-
	public Polynomial(Polynomial x){
		leastDegree  = x.leastDegree;
		degree       = x.degree;
		if(x.coefficients != null)
			coefficients = x.coefficients.clone();
	}

	/** Construit un polyn�me nul.
	 * @since jPicEdt 1.6
	 */
	public Polynomial(){
		degree = -1;
		leastDegree = 0;
	}
	/** Construit un polyn�me, la m�moire est allou�e, mais <b>non initialis�e</b>
     * pouvoir stocker les mon�mes de degr�e 0 � degree.  Utiliser une m�thode
     * du genre {@link #setALaBernstein(int _degree,double[] bernsteinCoefficients)} pour initialiser les coefficients.
     * @see #setALaBernstein(int _degree,double[] bernsteinCoefficients).
     * @see #Polynomial(int leastDegree,int degree).
	 * @since jPicEdt 1.6
	 */
	public Polynomial(int degree){
		this.degree = degree;
		leastDegree = 0;
		coefficients = new double[degree+1];
	}

	/** construit un polyn�me nul en allouant en interne la m�moire
	 *	pour les coefficient de leastDegree � degree. La m�moire est
	 *	initialis�e � 0, c'est � dire que le polyn�me est initialement nul.
	 * @since jPicEdt 1.6
	 */
	public Polynomial(int leastDegree,int degree){
		this.degree = degree;
		this.leastDegree = leastDegree;
		coefficients = new double[degree-leastDegree+1];
		for(int i = leastDegree; i <= degree; ++i)
			coefficients[i-leastDegree] = 0.0;
	}

	/** construit un polyn�me. <b>Attention : coefficients n'est pas clon�</b>
	 * @since jPicEdt 1.6
	 */
	public Polynomial(int leastDegree,int degree,double[] coefficients){
		this.degree = degree;
		this.leastDegree = leastDegree;
		this.coefficients = coefficients;
	}


	/**
	 * @since jPicEdt 1.6
	 */
	public void setCoeff(int degree, double coeff){
		degreeExtend(degree);
		coefficients[degree-leastDegree] = coeff;
	}


	// ----------------------------------------------------------------------
	/** �value le polyn�me this en x
	 * @since jPicEdt 1.6
	 */
	public double   eval(double x){
		// utilisation de la formule de Horner
		double ret = 0.0;
		for(int i = degree; i >= 0; --i)
		{
			ret*= x;
			if(i >= leastDegree)
				ret += coefficients[i-leastDegree];
		}
		return ret;
	}

	/** �value le polyn�me this en x
	 * @since jPicEdt 1.6
	 */
	public Complex eval(Complex x){
		Complex ret = new Complex(0.0,0.0);
		for(int i = degree; i >= 0; --i)
		{
			ret.mul(x);
			if(i >= leastDegree)
				ret.add(new Complex(coefficients[i-leastDegree],0.0));
		}
		return ret;
	}
	/**
	 * renvoie le coefficient du mon�me X^i de degr� i
	 * @since jPicEdt 1.6
	 */
	public double coeff(int i){
		if( i < leastDegree)
			return 0.0;
		else if(i > degree)
			return 0.0;
		else
			return coefficients[i-leastDegree];
	}
	/** d�rive le this, ce qui modifie this, et renvoie la nouvelle valeur de this.
	 * @since jPicEdt 1.6
	 */
	public Polynomial derive(){
		for(int i = leastDegree; i < degree; ++i)
		{
			coefficients[i-leastDegree] = coefficients[i+1-leastDegree]*(i+1);
		}
		--leastDegree;
		if(leastDegree< 0) leastDegree = 0;
		--degree;
		if(degree< -1) degree = -1;
		return this;
	}
	/** calcule la d�riv�e du this sans modifier le this
	 * @return la d�riv�e
	 * @since jPicEdt 1.6
	 */
	public Polynomial cDerive(){
		return (new Polynomial(this)).derive();
	}

	/** remplace this par -this
	 * @return this apr�s n�gation.
	 * @since jPicEdt 1.6
	 */
	public Polynomial neg(){
		int size = degree-leastDegree+1;
		for(int i = 0; i < size;++i)
			coefficients[i] = -coefficients[i];
		return this;
	}

	/** calcule -this, sans modifier this, et renvoie le r�sultat.
	 * @return -this
	 * @since jPicEdt 1.6
	 */
	public Polynomial cNeg(){
		return (new Polynomial(this)).neg();
	}

	/** renvoie un tableau des coefficients dans une plage de degr�s donn�e.
	 * @since jPicEdt 1.6
	 */
	public double[] toArray(int degMin,int degMax){
		if(degMax < degMin)
			return null;


		double[] ret = new double[degMax + 1-degMin];

		toArray(degMin,degMax,ret);

		return ret;
	}

	/**
	 * �crit un tableau des coefficients dans une plage de degr�s donn�e.
	 * @since jPicEdt 1.6
	 */
	public void toArray(int degMin,int degMax,double[] coeff){

		int c = 0;
		int max = degMax < leastDegree ? degMax : (leastDegree-1);
		int i = degMin;
		for(; i <= max; ++i)
			coeff[c++] = 0.0;
		max = degMax < degree ? degMax : degree;
		for(; i <= max; ++i)
			coeff[c++] = coefficients[i - leastDegree];
		for(; i <= degMax; ++i)
			coeff[c++] = 0.0;
	}


	/**
	 * Compose le polyn�me this avec le polym�me other, sans changer this, et
	 * renvoie le resultat.
	 * @return le polyn�me dont la fonction polyn�me associ�e est x -&gt;
	 * this(other(x))
	 * @since jPicEdt 1.6
	 */
	public Polynomial cCompose(Polynomial other){
		// this est null, this.cCompose(other) aussi.
		if(degree < leastDegree)
			return new Polynomial();


		if(other.degree < other.leastDegree){
			double c0 = coeff(0);
			if(c0 == 0.0)
				return new Polynomial();
			else
			{
				double[] a = {c0};
				return new Polynomial(0,0,a);
			}
		}

		// formule de Horner
		Polynomial ret = new Polynomial();
		for(int i = degree; i >= 0; --i)
		{
			ret.mul(other);
			if(i >= leastDegree)
				ret.add(coefficients[i-leastDegree]);
		}
		return ret;
	}


	/** multiplie le polynome this par s, ce qui modifie this.
	 * Renvoie le r�sultat, c'st � dire this apr�s modification.
	 * @since jPicEdt 1.6
	 */
	public  Polynomial mul(double s){
		for(int i = leastDegree; i < degree; ++i)
			coefficients[i-leastDegree] *= s;
		return this;
	}

	/** multiplie le polynome this par s, ce qui modifie this.
	 * @since jPicEdt 1.6
	 */
	public void scale(double s){
		mul(s);
	}

	/** ajoute c*X^d au polyn�me this, ceci modifie this
	 * @param d degr�s du mon�me ajout�
	 * @param c coefficient du mon�me ajout�
	 * @return this, par commodit�.
	 * @since jPicEdt 1.6
	 */
	public Polynomial addMonomial(int d,double c){
		degreeExtend(d);
		coefficients[d-leastDegree] += c;
		return this;
	}

	/** fait en sorte que le format interne du polynome contienne le degr�
	 * d. Suppose que d &gt;= 0.
	 * @since jPicEdt 1.6
	 */
	public void degreeExtend(int d){
		if(d < leastDegree)
		{
			if(d < 0)
				Log.error("d invalide");
			else
			{
				double[] newCoeff = new double[degree-d+1];
				int i = d;
				for(; i < leastDegree; ++i)
					newCoeff[i-d] = 0.0;
				for(; i < degree; ++i)
					newCoeff[i-d] = coefficients[i-leastDegree];
				coefficients = newCoeff;
				leastDegree = d;
			}
		}
		else if(d > degree)
		{
			double[] newCoeff = new double[d-leastDegree+1];
			int i = leastDegree;
			for(; i <= degree; ++i)
				newCoeff[i-leastDegree] = coefficients[i-leastDegree];
			for(; i < d; ++i)
				newCoeff[i-leastDegree] = 0.0;
			coefficients = newCoeff;
			degree = d;
		}
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public Polynomial add(Polynomial x){
		Polynomial r = cAdd(x);
		coefficients = r.coefficients;
		degree       = r.degree;
		leastDegree  = r.leastDegree;
		return this;
	}

	/**
	 * Ajoute x � this, et renvoie la somme. this n'est pas chang�.
	 * @since jPicEdt 1.6
	 */
	public Polynomial cAdd(Polynomial x){
		Polynomial ret = new Polynomial();
		if(degree < x.degree)
			ret.degree = x.degree;
		else
			ret.degree = degree;

		if(leastDegree > x.leastDegree)
			ret.leastDegree = x.leastDegree;
		else
			ret.leastDegree = leastDegree;

		if(ret.degree >= ret.leastDegree)
		{
			ret.coefficients = new double[1+ret.degree - ret.leastDegree];

			// copie this dans ret
			int i = ret.leastDegree;
			for(; i < leastDegree; ++i)
				ret.coefficients[i-ret.leastDegree] = 0.0;
			for(; i <= degree; ++i)
				ret.coefficients[i-ret.leastDegree] = coefficients[i-leastDegree];
			for(; i <= ret.degree; ++i)
				ret.coefficients[i-ret.leastDegree] = 0.0;

			// ajout de x
			for(i = x.leastDegree; i <= x.degree; ++i)
				ret.coefficients[i-ret.leastDegree] +=
					x.coefficients[i - x.leastDegree];
		}
		else
			ret.coefficients = null;

		return ret;
	}

	/**
	 * Soustrait x � this, ce qui modifie this, et renvoie this ainsi modifi�.
	 * @since jPicEdt 1.6
	 */
	public Polynomial sub(Polynomial x){
		Polynomial r = cSub(x);
		coefficients = r.coefficients;
		degree       = r.degree;
		leastDegree  = r.leastDegree;
		return this;
	}


	/**
	 * Soustrait x � this, et renvoie la diff�rence. this n'est pas chang�.
	 * @since jPicEdt 1.6
	 */
	public Polynomial cSub(Polynomial x){
		Polynomial ret = new Polynomial();
		if(degree < x.degree)
			ret.degree = x.degree;
		else
			ret.degree = degree;

		if(leastDegree > x.leastDegree)
			ret.leastDegree = x.leastDegree;
		else
			ret.leastDegree = leastDegree;

		if(ret.degree >= ret.leastDegree)
		{
			ret.coefficients = new double[1+ret.degree - ret.leastDegree];

			// copie this dans ret
			int i = ret.leastDegree;
			for(; i < leastDegree; ++i)
				ret.coefficients[i-ret.leastDegree] = 0.0;
			for(; i <= degree; ++i)
				ret.coefficients[i-ret.leastDegree] = coefficients[i-leastDegree];
			for(; i <= ret.degree; ++i)
				ret.coefficients[i-ret.leastDegree] = 0.0;

			// ajout de x
			for(i = x.leastDegree; i <= x.degree; ++i)
				ret.coefficients[i-ret.leastDegree] -=
					x.coefficients[i - x.leastDegree];
		}
		else
			ret.coefficients = null;

		return ret;
	}

	/**
	 * Multiplie x et this, et affecte le produit � this.
	 * @since jPicEdt 1.6
	 */
	public Polynomial mul(Polynomial x){
		Polynomial prod = cMul(x);
		degree      = prod.degree;
		leastDegree = prod.leastDegree;
		coefficients= prod.coefficients;
		return this;
	}

	/**
	 * Multiplie x et this, et renvoie le produit. this n'est pas chang�.
	 * @since jPicEdt 1.6
	 */
	public Polynomial cMul(Polynomial x){
		Polynomial ret = new Polynomial();

		if(degree < leastDegree
		   || x.degree < x.leastDegree)
			return ret;

		ret.degree = x.degree + degree;
		ret.leastDegree = x.leastDegree + leastDegree;

		ret.coefficients = new double[1+ret.degree - ret.leastDegree];

		// initialise ret � z�ro
		for(int i = ret.degree - ret.leastDegree; i >= 0; --i)
			ret.coefficients[i] = 0.0;

		// effectue le produit
		for(int i= leastDegree;i<= degree; ++i)
			for(int j = x.leastDegree;j <= x.degree; ++j)
				ret.coefficients[i+j-ret.leastDegree] +=
					coefficients[i-leastDegree]
					*x.coefficients[j-x.leastDegree];


		return ret;
	}

	/** ajoute s au polynome this. �a fait pareil que addMonomial(0,s).
	 * this est modifi� par cette op�ration.
	 * @return this
	 * @since jPicEdt 1.6
	 */
	public Polynomial add(double s){
		double p0 = coeff(0);
		p0+= s;
		if(leastDegree == 0)
		{
			if(degree < leastDegree)
			{
				degree = 0;
				coefficients = new double[1];
			}
			coefficients[0] = p0;
		}
		else if(p0 != 0.0)
		{
			if(coefficients!= null && coefficients.length >= degree+1)
			{
				int i = degree;
				for(; i >= leastDegree; --i)
					coefficients[i] = coefficients[i-leastDegree];
				for(; i >= 1; --i)
					coefficients[i] = 0;
				coefficients[0] = p0;
			}
			else
			{
				double[] newCoefficients = new double[degree+1];
				newCoefficients[0] = p0;
				for(int i = 1; i <= degree; ++i)
					newCoefficients[i] = coeff(i);
				coefficients = newCoefficients;
				leastDegree = 0;
			}
		}
		return this;
	}

	/** retranche s du polynome this, ce qui modifie this, et renvoie le r�sultat
	 * @return this -= s
	 * @since jPicEdt 1.6
	 */
	public Polynomial sub(double s){
		return add(-s);
	}


	/** ajoute s au polynome this, sans modifier this, et renvoie le r�sultat.
	 * @return this - s
	 * @since jPicEdt 1.6
	 */
	public Polynomial cSub(double s){
		return (new Polynomial(this)).sub(s);
	}


	/** Affecte au polynome le polynome de Bernstein d�fini par la formule:
	 *
	 *  <math>\sum_{i=0}^{i=d} \begin{bmatrix}i\\ d\end{bmatrix} b_{i}\times
	 *  X^i\times X^{d-i}</math>
	 *
	 *  o� <math>\begin{bmatrix}i\\ d\end{bmatrix}</math> d�signe le bim�me de
	 *  Newton
	 *
	 *@param _degree  <math>d</math>, dans la formule d�finissant le polynome
	 *de Bernstein
	 *@param bernsteinCoefficients bernsteinCoefficients[i] =
	 *<math>b_{i}</math>, dans la formule d�finissant le polynome d�finissant
	 *le polynome
	 *@return this
	 * @since jPicEdt 1.6
	 */
	public Polynomial setALaBernstein(int _degree,double[] bernsteinCoefficients){
		if(degree-leastDegree < _degree)
			coefficients = new double[_degree +1];

		for(int i = 0; i <= _degree;++i)
			coefficients[i] = 0.0;

		leastDegree = 0;
		degree = _degree;
		int topBinomial = 1;
		for(int i = 0; i <= _degree;++i)
		{
			if(i != 0)
			{
				topBinomial *= _degree+1-i;
				topBinomial /= i;
			}
			int binomial = topBinomial;
			int dMinI = _degree-i;
			for(int k = 0; k <= dMinI; ++k)
			{
				if(k != 0)
				{
					binomial *= -(dMinI+1-k);
					binomial /= k;
				}
				coefficients[i+k] += bernsteinCoefficients[i]*binomial;

			}
		}
		return this;
	}
	/** Convertit le polyn�me this en une cha�ne de caract�re � des fin de
	 * d�bogage.
	 * @return une cha�ne de caract�res repr�sentant le polyn�me this.
	 * @since jPicEdt 1.6
	 */
	public String toString(){
		String ret = new String("");
		for(int i = degree; i >= leastDegree; --i)
		{
			if(i < degree)
				ret = ret + "+";

			ret = ret + coefficients[i-leastDegree];

			if(i >=2)
				ret = ret + "*X^" + i;
			else if(i == 1)
				ret = ret + "*X";

		}
		return ret;
	}

	/** trouve un z�ro dans l'intervalle [min, max]. Cette m�thode est se base
	 * sur l'hypoth�se que le polynome est monotone dans cet intervalle.
	 *@param tol = tol�rance sur le r�sultat, abs(eval(z�ro)) &lt; tol
	 *@param sign si z�ro, pas d'exigence sur eval(z�ro), si sign=+1, la
	 *m�thode assure que eval(z�ro) &gt;= 0, si sign=-1, la m�thode assure que
	 *eval(z�ro) &lt;=0.
	 *@return null si pas touv�, {zero_trouv�} sinon
	 * @since jPicEdt 1.6
	 */
	public double[] findZeroInInterval(double min, double max,double tol,int sign){
		double minVal = eval(min);
		double maxVal = eval(max);
		double signVal;

		if(minVal <= maxVal)
		{
			signVal = 1;
		}
		else
		{
			signVal = -1;
			sign = -sign;
		}
		minVal *= signVal;
		maxVal *= signVal;

		if(minVal > 0 || maxVal < 0 || tol <= 0.0)
			return null;

		/* La m�thode utilis�e est une combinaison de la m�thode par dichotomie,
		   de la m�thode de M�ller et de la m�thode de la s�cante.
		   On ne veut pas utiliser la m�thode de M�ller toute seule, car on
		   veut pouvoir encadrer le z�ro pour �ventuellement assurer son
		   signe. */
		for(;;)
		{
			/* on commence par un petit coup de dichotomie. C'est pour assurer
			 * qu'� la fois minVal et maxVal convergent vers 0, de sorte que
			 * lorsque sign != 0, on puisse assurer le signe de eval(z�ro)
			 * (qui peut ne pas �tre tout � fait nul, et donc avoir un signe)*/


			// dichotomie
			double med = (min+max)*.5;
			double medVal = eval(med)*signVal;


			if(medVal >= 0)
			{
				max = med;
				maxVal = medVal;
			}
			else
			{
				min = med;
				minVal = medVal;

			}

			// s�cante
			med = (min*maxVal-max*minVal)/(maxVal-minVal);
			medVal = eval(med)*signVal;

			/* on sort si �a a converg� suffisamment. C'est important de
			 * tester avant M�ller, car pour que M�ller marche bien il faut
			 * que min != med et med != max.
			 */
			if(abs(medVal) < tol)
			{
				if(sign*medVal >= 0)
				{
					double[] ret = {med};
					return ret;
				}
				else
				{
					if(sign*maxVal >= 0 && abs(maxVal) <= tol)
					{
						double[] ret = {max};
						return ret;
					}
					else if(sign*minVal >= 0 && abs(minVal) <= tol)
					{
						double[] ret = {min};
						return ret;
					}
				}
			}


			// maintenant un petit coup de M�ller sur le triplet min med max
			// histoire d'ajuster med.

			{
				/* On approxime le polynome par le polynome y(x) de degr� 2

				    y = a*(x-med)^2 + b*(x-med) + c

					sachant que :

						+-----+------+
						|x    |y     |
						+-----+------+
						|min  |minVal|
						+-----+------+
						|med  |medVal|
						+-----+------+
						|max  |maxVal|
						+-----+------+

				 */

				double c = medVal;
				double dMaxMin = (maxVal-minVal)/(max-min); // a*(max+min-2*med)+b
				double dMinMed = (minVal-medVal)/(min-med); // a*(min-med)+b
				// double dMaxMed   = (MaxVal-medVal)/(max-med); // a*(max-med)+b
				double a       = (dMaxMin-dMinMed)/(max - med);
				// double a       = (dMaxMin-dMaxMed)/(min - med);
				double b       = dMaxMin - a*(max+min-2*med);

				double r   = sqrt(b*b - 4*a*c);
				double d;
				/*
				  Les deux racines de a*d^2 + b*d + c = 0 sont

				  (-b+r)/(2*a)  = -2*c/(b+r)

				  et

				  (-b-r)/(2*a)  = -2*c/(b-r)

				  on ne fait que s�lectionner la racine la plus petite en valeur
				  absolue, ce qui assure la convergence.
				 */


				if(abs(b+r) < abs(b-r))
					d = -2*c / (b-r);
				else
					d = -2*c / (b+r);

				// ici on fait la s�cante
				if(medVal >= 0)
				{
					max = med;
					maxVal = medVal;
				}
				else
				{
					min = med;
					minVal = medVal;
				}

				// ici on fait M�ller.
				med += d;
				if(min < med && med < max)
				{
					/* ici c'est le cas normal, en effet apr�s addition de d
					   med est suppos� �tre plus pr�s de z�ro qu'avant, donc
					   entre min et max. */
					medVal = eval(med)*signVal;
					if(medVal >= 0)
					{
						max = med;
						maxVal = medVal;
					}
					else
					{
						min = med;
						minVal = medVal;
					}
				}
				// else ce n'est pas normal, m'enfin bon...
			}


		}

	}

	/** trouve tous les z�ros r�els entre min et max inclus. min et max
	 * peuvent �tre infinis
	 * @return liste de PolynomialRealRoot
	 * @since jPicEdt 1.6
	 */
	public ArrayList<PolynomialRealRoot> findZerosInInterval(double min,double max,double tol){
		ArrayList<PolynomialRealRoot> ret = new ArrayList<PolynomialRealRoot>();
		if(degree <= 0)
			return ret;

		PolynomialRealRoot root = null;

		Complex x0,x1,x2,y0,y1,y2;
		Polynomial subFactor = new Polynomial(this);
		do
		{

			// d�termination d'un z�ro par la m�thode de M�ller.
			// voir http://fr.wikipedia.org/wiki/Racine_d%27un_polyn%C3%B4me
			x0 = new Complex(0.0,0.0);
			y0 = new Complex(subFactor.coeff(0),0.0);
			x1 = new Complex(1.0,0.0);
			y1 = new Complex(subFactor.eval(1.0),0.0);
			x2 = new Complex(.5,0.0);
			y2 = new Complex(subFactor.eval(.5),0.0);

			boolean firstPass = true;
			for(;;)
			{
				while(y2.norm1() > tol)
				{
					// xd01 = x0-x1
					Complex xd01 = x0.cSub(x1);
					// yd01 = (y0-y1)/(x0-x1)
					Complex yd01 = y0.cSub(y1).cDiv(xd01);
					// xd12 = x1-x2
					Complex xd12 = x1.cSub(x2);
					// yd12 = (y1-y2)/(x1-x2)
					Complex yd12 = y1.cSub(y2).cDiv(xd12);
					// xd02 = x0-x2
					Complex xd02 = x0.cSub(x2);
					// a= (yd01-yd12)/xd02
					Complex a = yd01.cSub(yd12).cDiv(xd02);
					// b = yd12 - a*xd12
					Complex b   = yd12.cSub(xd12.cMul(a));
					Complex c   = y2;
					//discriminent = sqrt(b^2 - 4*a*c)
					Complex r = b.cMul(b).sub(c.cMul(a).mul(4)).cSqrt();
					Complex d = c.cMul(-2.0);
					if(b.cAdd(r).norm1() < b.cSub(r).norm1())
						b.sub(r);
					else
						b.add(r);
					d.div(b);
					x0 = x1;
					y0 = y1;
					x1 = x2;
					y1 = y2;
					x2 = x1.cAdd(d);
					y2 = subFactor.eval(x2);


				}


				// si la racine n'est pas r�elle, mais que sa partie
				// r�elle est quasiment une racine alors on
				// relance l'algo de M�ller un coup en for�ant les parties
				// imaginaires � z�ro. De sorte � avoir une racine
				// parfaitement r�elle si une telle racine existe
				if(   x2.im() != 0.0
					  && firstPass)
				{
					double x2re = x2.re();
					double y2re = subFactor.eval(x2re);
					// on multiplie tol par 3: un fois pour y2.re(), une fois
					// pour y2.im(),et une fois pour x2re
					if(abs(y2re) <= 3*tol)
					{
						firstPass = false;
						x0 = new Complex(x0.re());
						y0 = new Complex(subFactor.eval(x0.re()));
						x1 = new Complex(x1.re());
						y1 = new Complex(subFactor.eval(x1.re()));
						x2 = new Complex(x2re);
						y2 = new Complex(y2re);
					}
					else
						break;
				}
				else
					break;
			}


			Polynomial divisor = new Polynomial();
			if(x2.im() == 0.0)
			{
				double[] coefficients = { -x2.re(), 1.0};
				divisor.leastDegree = 0;
				divisor.degree = 1;
				divisor.coefficients = coefficients;
				if(x2.re() >= min && x2.re() <= max)
				{
					root = new PolynomialRealRoot(x2.re());
					ret.add(root);
				}
				else
					root = null;
			}
			else
			{
				double[] coefficients = {
					x2.re()*x2.re()
					+x2.im()*x2.im(),
					-2*x2.re(),
					1.0};
				divisor.leastDegree = 0;
				divisor.degree = 2;
				divisor.coefficients = coefficients;
				root = null;
			}


			Polynomial[] division = subFactor.divide(divisor);
			subFactor = division[0];

			while(subFactor.refreshDegree() >= divisor.degree)
			{
				division = subFactor.divide(divisor);
				Polynomial remainder = division[1];
				Complex vx2 = remainder.eval(x2).cSub(1.0);
				Complex v1 = new Complex(remainder.eval(1.0) - 1.0);
				double v0 = remainder.coeff(0) - 1.0;
				Complex vI = remainder.eval(Complex.I).sub(1.0);
				if( abs(vx2.re()) <= tol
					&& abs(vx2.im()) <= tol
					&& abs(v1.re()) <= tol
					&& abs(v1.im()) <= tol
					&& abs(v0) <= tol
					&& abs(vI.re()) <= tol
					&& abs(vI.im()) <= tol)
				{
					subFactor = division[0];
					if(root != null)
						root.incOrder();
				}
				else
					break;


			}
		}
		while(subFactor.degree > 0);

		/* post-raffinement : du fait qu'on a proc�d� par divisions successives
		   on est peut-�tre en dehors de la tol�rance. On raffine donc les
		   racines r�elles par la m�thode de Newton */
		if(ret.size() > 1)
		{
			boolean first = true;
			Polynomial d = cDerive();
			for(PolynomialRealRoot rr : ret)
			{
				if(first)
					first = false;
				else
				{
					double z = rr.getValue();
					double a = eval(z);
					double absA = abs(a);
					for(;;)
					{
						if(absA < tol)
						{
							rr.setValue(z);
							break;
						}
						else
						{
							double p = d.eval(z);
							if(p != 0)
							{
								double zChallenger = z + -a/p;
								double aChallenger = eval(zChallenger);
								double absAChallenger = abs(aChallenger);
								if(absAChallenger < absA)
								{
									z    = zChallenger;
									a    = aChallenger;
									absA = absAChallenger;
								}
								else
								{
									rr.setValue(z);
									break;
								}
							}
							else
							{
								rr.setValue(z);
								break;
							}
						}
					}
				}
			}

		}


		return ret;
	}

	/** teste les mon�mes de degr�s les plus �lev�s et r�duit ci-possible de
	 *	degr�s jusqu'� ce que degree soit le plus faible possible
	 * @since jPicEdt 1.6
	 * @return le degr� du polyn�me this, apr�s cette op�ration.
	 */
	public int refreshDegree(){
		while(degree >= leastDegree && coefficients[degree-leastDegree] == 0.0)
		{
			--degree;
		}
		if(degree < leastDegree)
		{
			degree = -1;
			leastDegree = 0;
		}
		return degree;
	}

	/** divise this par divisor en division Euclidienne
     * @return { dividende, reste} si divisor et non nul, null sinon
	 * @since jPicEdt 1.6
	 */
	public Polynomial[] divide(Polynomial divisor){
		if(divisor.refreshDegree() < 0)
			return null;

		// most significant divisor monomial
		double mSDM = divisor.coefficients[divisor.degree - divisor.leastDegree];

		Polynomial dividend  = new Polynomial();
		Polynomial remainder = new Polynomial();
		if(refreshDegree() < 0)
		{
			Polynomial[] ret = { dividend, remainder};
			return ret;
		}

		if(degree < divisor.degree)
		{
			Polynomial[] ret = { dividend, new Polynomial(this)};
			return ret;
		}

		if(degree > divisor.degree)
			remainder.degree = degree;
		else
			remainder.degree = divisor.degree;

		if(leastDegree < divisor.leastDegree)
			remainder.leastDegree = leastDegree;
		else
			remainder.leastDegree = divisor.leastDegree;

		remainder.coefficients =
			new double [1+ remainder.degree - remainder.leastDegree];

		for(int i = remainder.leastDegree; i <= remainder.degree; ++i)
			remainder.coefficients[i - remainder.leastDegree] = coeff(i);


		dividend.degree = remainder.degree-divisor.degree;

		dividend.leastDegree =
			remainder.leastDegree-2*divisor.degree +divisor.leastDegree;
		if(dividend.leastDegree < 0)
			dividend.leastDegree = 0;

		dividend.coefficients = new double [dividend.degree
											- dividend.leastDegree+1];

		for(int i = dividend.degree; i >= dividend.leastDegree; --i)
		{
			double c = remainder.coefficients[remainder.degree
											  -remainder.leastDegree] / mSDM;
			dividend.coefficients[i-dividend.leastDegree]  = c;

			// remainder -= c X^n * divisor
			int n     = remainder.degree-divisor.degree;
			int jMin  = divisor.leastDegree+n;
			for(int j = remainder.degree-1;
				j >= jMin; --j)
			{
				remainder.coefficients[j]
					-= c * divisor.coefficients[j-n];
			}
			--remainder.degree;
		}

		Polynomial[] ret =  { dividend, remainder};
		return ret;
	}
};




/// Polynomial.java ends here
