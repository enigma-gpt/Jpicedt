// VecPolynomial.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: VecPolynomial.java,v 1.5 2013/03/27 06:55:31 vincentb1 Exp $
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
import jpicedt.util.math.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PicPoint;
import jpicedt.Log;
// import java.lang.Math.*;




/** Polynôme monovarié dans l'espace des PicVector.
 * @since jPicEdt 1.6
 */
public class VecPolynomial implements Cloneable
{
	PicVector[] coefficients;
	int      leastDegree =0;
	int      degree = -1;

	/** Clone this
	 * @since jPicEdt 1.6
	 */
	public VecPolynomial clone(){
		return new VecPolynomial(this);
	}

	/**
	 * Évalue le polynôme this en x.
	 * @param x la valeur de la variable en laquelle on évalue le polynôme
	 * this.
	 * @return le résultat de l'évaluation.
	 * @since jPicEdt 1.6
	 */
	PicVector   eval(double x)
		{
			// utilisation de la formule de Horner
			PicVector ret = new PicVector();
			for(int i = degree; i >= 0; --i)
			{
				ret.scale(x);
				if(i >= leastDegree)
					ret.add(coefficients[i-leastDegree]);
			}
			return ret;
		}

	/** Renvoie le coefficient du monôme de degré i
	 * @since jPicEdt 1.6
	 */
	public PicVector coeff(int i)
		{
			if( i < leastDegree)
				return new PicVector();
			else if(i > degree)
				return new PicVector();
			else
				return coefficients[i-leastDegree];
		}
	/** dérive le this (this est modifié) au sens du calcul différentiel.
	 * @since jPicEdt 1.6
	 */
	void derive()
		{
			for(int i = leastDegree; i < degree; ++i)
			{
				coefficients[i-leastDegree] = coefficients[i+1-leastDegree];
				coefficients[i-leastDegree].scale(i+1);
			}
			--leastDegree;
			if(leastDegree< 0) leastDegree = 0;
			--degree;
			if(degree< -1) degree = -1;
		}
	/** calcule la dérivée du this sans modifier le this
	 *@return la dérivée
	 * @since jPicEdt 1.6
	 */
	VecPolynomial cDerive(){
		VecPolynomial ret = new VecPolynomial(this);
		ret.derive();
		return ret;
	}

	/** multiplie le polynome par s
	 * @since jPicEdt 1.6
	 */
	void scale(double s){
		for(int i = leastDegree; i < degree; ++i)
			coefficients[i-leastDegree].scale(s);
	}

	/** ajoute s au polynome this, ce qui modifie this, et renvoie la somme.
	 * @since jPicEdt 1.6
	 */
	VecPolynomial add(PicPoint s){
		PicVector p0 = coeff(0);
		p0.add(s);
		if(leastDegree == 0)
			coefficients[0] = p0;
		else if(!p0.equals(new PicVector()))
		{
			if(coefficients!= null && coefficients.length >= degree+1)
			{
				int i = degree;
				for(; i >= leastDegree; --i)
					coefficients[i] = coefficients[i-leastDegree];
				for(; i >= 1; --i)
					coefficients[i] = new PicVector();
				coefficients[0] = p0;
			}
			else
			{
				PicVector[] newCoefficients = new PicVector[degree+1];
				newCoefficients[0] = p0;
				for(int i = 1; i <= degree; ++i)
					newCoefficients[i] = coeff(i);
				coefficients = newCoefficients;
				leastDegree = 0;
			}
		}
		return this;
	}

	/** retranche s au polynome this, ce qui modifie this, et renvoie la somme.
	 * @since jPicEdt 1.6
	 */
	VecPolynomial cAdd(PicPoint v){
		return (new VecPolynomial(this)).add(v);
	}


	/** retranche s au polynome this, ce modifie this, et renvoie la différence.
	 * @since jPicEdt 1.6
	 */
	VecPolynomial sub(PicPoint v){
		return add((new PicVector(v)).inverse());
	}

	/** retranche s au polynome this, sans modifier this, et renvoie la différence.
	 * @since jPicEdt 1.6
	 */
	VecPolynomial cSub(PicPoint v){
		return cAdd((new PicVector(v)).inverse());
	}


	/**
	 * Effectue le porduit scalaire entre le polynôme this et
	 * l'argument PicVector v.
	 * @return le Polynomial resultant du produit scalaire.
	 * @since jPicEdt 1.6
	 */
	public Polynomial dot(PicVector v){
		if(degree < leastDegree)
			return new Polynomial();

		int deltaDegree = degree-leastDegree;
		double[] coeff = new double[deltaDegree+1];

		for(int i = deltaDegree; i >= 0; --i)
			coeff[i] = coefficients[i].dot(v);

		return new Polynomial(leastDegree,degree,coeff);
	}


	/**
	 * Effectue le porduit scalaire entre le polynôme this et PicVector.X_AXIS
	 * de façon optimisée par rapport à l'usage de la méthode dot.
	 * @return le Polynomial resultant du produit scalaire.
	 * @see #dot(PicVector v)
	 * @see #dotYAxis()
	 * @since jPicEdt 1.6
	 */
	public Polynomial dotXAxis(){
		if(degree < leastDegree)
			return new Polynomial();

		int deltaDegree = degree-leastDegree;
		double[] coeff = new double[deltaDegree+1];

		for(int i = deltaDegree; i >= 0; --i)
			coeff[i] = coefficients[i].getX();

		return new Polynomial(leastDegree,degree,coeff);
	}

	/**
	 * Effectue le porduit scalaire entre le polynôme this et PicVector.Y_AXIS
	 * de façon optimisée par rapport à l'usage de la méthode dot.
	 * @return le Polynomial resultant du produit scalaire.
	 * @see #dot(PicVector v)
	 * @see #dotYAxis()
	 * @since jPicEdt 1.6
	 */
	public Polynomial dotYAxis(){
		if(degree < leastDegree)
			return new Polynomial();

		int deltaDegree = degree-leastDegree;
		double[] coeff = new double[deltaDegree+1];

		for(int i = deltaDegree; i >= 0; --i)
			coeff[i] = coefficients[i].getY();

		return new Polynomial(leastDegree,degree,coeff);
	}


	/** Effectue le produit scalaire avec un autre polynôme
	 * @since jPicEdt 1.6
	 */
	public Polynomial dot(VecPolynomial x){
		Polynomial ret;

		if(degree < leastDegree || x.degree < x.leastDegree)
		{
			ret  = new Polynomial();
		}
		else
		{
			int retLeastDegree = leastDegree + x.leastDegree;
			int retDegree  = degree + x.degree;
			double[] retCoeff = new double[1+retDegree-retLeastDegree];
			for(int i = retDegree-retLeastDegree; i >=0; --i)
				retCoeff[i] = 0.0;

			for(int i = leastDegree; i <= degree; ++i)
				for(int j = x.leastDegree; j<= x.degree;++j)
					retCoeff[i+j-retLeastDegree] +=
						coefficients[i-leastDegree].dot(
							x.coefficients[j-x.leastDegree]);

			ret = new Polynomial(retLeastDegree,retDegree,retCoeff);
		}
		return ret;
	}

	/** ajoute c*X^d au polynôme this. this est modifié.
	 * @since jPicEdt 1.6
	 */
	public void addMonomial(int d,PicVector c){
		if(d >= leastDegree && d <= degree)
			coefficients[d-leastDegree].translate(c);
		else
		{
			degreeExtend(d);
			coefficients[d-leastDegree].translate(c);
		}
	}

	/** fait en sorte que le format interne du polynome contienne le degré
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
				PicVector[] newCoeff = new PicVector[degree-d+1];
				for(int i = leastDegree; i < degree; ++i)
					newCoeff[i-d] = coefficients[i-leastDegree];
				coefficients = newCoeff;
				leastDegree = d;
			}
		}
		else if(d > degree)
		{
			PicVector[] newCoeff = new PicVector[d-leastDegree+1];
			for(int i = leastDegree; i <= degree; ++i)
				newCoeff[i-leastDegree] = coefficients[i-leastDegree];
			coefficients = newCoeff;
			degree = d;
		}
	}


	/** Construit un polynôme nul.
	 * @since jPicEdt 1.6
	 */
	VecPolynomial(){
		degree = -1;
		leastDegree = 0;
	}

	/** Construit un polynôme égal à x
	 * @since jPicEdt 1.6
	 */
	VecPolynomial(VecPolynomial x){
		degree = x.degree;
		leastDegree = x.leastDegree;
		if(x.coefficients == null)
			coefficients = null;
		else
		{
			coefficients = new PicVector [x.coefficients.length];
			for(int i = 0; i < x.coefficients.length; ++i)
				coefficients[i] = new PicVector(x.coefficients[i]);
		}

	}

	/** construit un polynôme nul en allouant en interne la mémoire
	 *	pour les coefficient de leastDegree à degree
	 * @since jPicEdt 1.6
	 */
	public VecPolynomial(int leastDegree,int degree){
		this.degree = degree;
		this.leastDegree = leastDegree;
		coefficients = new PicVector[degree-leastDegree+1];
		for(int i = leastDegree; i <= degree; i++)
			coefficients[i-leastDegree] = new PicVector();
	}


	/** construit un polynôme. <b>Attention : coefficients n'est pas cloné</b>
	 * @since jPicEdt 1.6
	 */
	public VecPolynomial(int leastDegree,int degree,PicVector[] coefficients){
		this.degree = degree;
		this.leastDegree = leastDegree;
		this.coefficients = coefficients;
	}



	/** Affecte au polynome le polynome de Bernstein défini par la formule:
	 *
	 *  <math>\sum_{i=0}^{i=d} \begin{bmatrix}i\\ d\end{bmatrix} b_{i}\times
	 *  X^i\times X^{d-i}</math>
	 *
	 *  où <math>\begin{bmatrix}i\\ d\end{bmatrix}</math> désigne le bimôme de
	 *  Newton
	 *
	 *@param _degree = <math>d</math>, dans la formule définissant le polynome
	 *de Bernstein
	 *@param bernsteinCoefficients[i] = <math>b_{i}</math>, dans la formule
	 *définissant le polynome définissant le polynome
	 *@return this
	 * @since jPicEdt 1.6
	 */
	VecPolynomial setALaBernstein(int _degree,PicPoint[] bernsteinCoefficients){
		if(coefficients == null || coefficients.length < _degree+1)
		{
			coefficients = new PicVector[_degree +1];
			for(int i = 0; i <= _degree;++i)
				coefficients[i] = new PicVector();
		}
		else
			for(int i = 0; i <= _degree;++i)
				coefficients[i].setCoordinates(0.0,0.0);

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
				PicVector temp = new PicVector(bernsteinCoefficients[i]);
				temp.scale(binomial);
				coefficients[i+k].add(temp);

			}
		}
		return this;
	}
	/** Convertit le polynôme this en une chaîne de caractère humainement
	 * lisible à des fin de débogage.
	 * @since jPicEdt 1.6
	 */
	public String toString(){
		String ret = new String("");
		for(int i = degree; i >= leastDegree; --i)
		{
			if(i < degree)
				ret = ret + "+";

			ret = ret + coefficients[i-leastDegree].toString();

			if(i >=2)
				ret = ret + "*X^" + i;
			else if(i == 1)
				ret = ret + "*X";

		}
		return ret;
	}

};


/// VecPolynomial.java ends here
