// Matrix.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2009/2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: Matrix.java,v 1.3 2013/03/27 06:49:51 vincentb1 Exp $
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
package jpicedt.util.math;
import  jpicedt.Log;
import static java.lang.Math.abs;


/** Matrice réelle
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class Matrix{
	int rowCount;
	int colCount;
	double[][] coefficients;

	/** Construit une matrice 0x0
	 * @since jPicEdt 1.6
	 */
	public Matrix(){
		rowCount     = 0;
		colCount     = 0;
		coefficients = null;
	}


	/** <b>Attention: coefficient n'est pas dupliqué</b>
	 * @since jPicEdt 1.6
	 */
	public Matrix(int rowCount,int colCount,double[][] coefficients){
		this.rowCount     = rowCount    ;
		this.colCount     = colCount    ;
		this.coefficients = coefficients;
	}


	/** Copie x dans this.
	 * @since jPicEdt 1.6
	 */
	public Matrix(Matrix x){
		rowCount     = x.rowCount    ;
		colCount     = x.colCount    ;
		coefficients = x.coefficients.clone();
	}

	/**
	 * Renvoie un tableau des coefficients. <b>Attention</b> this fait
	 * référence à ce tableau, ainsi le modifier modifie this.
	 * @since jPicEdt 1.6
	 */
	public double[][] getCoefficients(){
		return coefficients;
	}

	/** Produit matriciel this*x. Ceci ne modifie pas this
	 * @since jPicEdt 1.6
	 */
	public Matrix muls(Matrix x){
		if(colCount != x.rowCount){
			Log.error("Dimensions incompatibles");
			return null;
		}
		Matrix ret = new Matrix();
		ret.rowCount = rowCount;
		ret.colCount = x.colCount;
		ret.coefficients = new double[ret.rowCount][ret.colCount];
		for(int i = 0; i < rowCount; ++i)
		{
			for(int j = 0; j < x.colCount; ++j)
			{
				double a = 0;
				for(int k = 0; k < colCount; ++k)
				{
					a += coefficients[i][k] * x.coefficients[k][j];
				}
				ret.coefficients[i][j] = a;
			}
		}
		return ret;
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public boolean equals(Matrix x){
		if(rowCount != x.rowCount)
			return false;

		if(colCount != x.colCount)
			return false;

		for(int i = 0; i < rowCount; ++i)
		{
			for(int j = 0; j < rowCount; ++j)
			{
				if(coefficients[i][j] != x.coefficients[i][j])
					return false;
			}
		}
		return true;
	}
	/**
	 * Résout this * x = b, et renvoie x. La résolution utilise la méthode
	 * d'élimination de Gauss Jordan. <b>Attention</b> this et b sont
	 * corrompus lors de la résolution.
	 * @return x, tel que muls(x).equals(b)
	 * @see #gaussJordanSolve(Matrix b)
	 * @since jPicEdt 1.6
	 */
	public Matrix gaussJordanTrashSolve(Matrix b){
		if(b.rowCount != rowCount)
		{
			Log.error("Dimensions incompatibles.");
			return null;
		}

		int[] rowPermut = new int[rowCount];
		for(int i = 0; i < rowCount; ++i)
			rowPermut[i] = i;
		int[] colPermut = new int[colCount];
		for(int i = 0; i < colCount; ++i)
			colPermut[i] = i;

		int iterCount = rowCount < colCount ? rowCount : colCount;

		for(int iter = 0; iter < iterCount; ++iter)
		{
			// recherche d'un pivot
			int bestI = 0;
			int bestJ = 0;
			double bestAbsCoeff = -1;
			int iterP1 = iter+1;
			for(int i = iter; i < rowCount; ++i)
			{
				for(int j = iter; j < colCount; ++j)
				{
					double absCoeff = abs(coefficients[rowPermut[i]][colPermut[j]]);
					if(absCoeff > bestAbsCoeff)
					{
						bestAbsCoeff = absCoeff;
						bestI        = i;
						bestJ        = j;
					}
				}
			}

			// permutation du pivot
			if(bestI != iter)
			{
				int temp = rowPermut[iter];
				rowPermut[iter] = rowPermut[bestI];
				rowPermut[bestI] = temp;
			}
			if(bestJ != iter)
			{
				int temp = colPermut[iter];
				colPermut[iter] = colPermut[bestJ];
				colPermut[bestJ] = temp;
			}

			// normalisation de la ligne pivot
			int rpit = rowPermut[iter];
			int cpit = colPermut[iter];
			double c = coefficients[rpit][cpit];
			if(c == 0.0)
				break;

			c = 1/c;
			coefficients[rpit][cpit] = 1.0;
			// pas besoin d'itérer sur les colonnes précédentes
			// car c'est à 0.
			for(int j = iterP1; j < colCount; ++j)
				coefficients[rpit][colPermut[j]] *= c;

			for(int j = 0; j < b.colCount; ++j)
				b.coefficients[rpit][j] *= c;

			// élimination sur les autres lignes
			for(int i = 0; i < rowCount; ++i)
			{
				int rpi = rowPermut[i];
				if(rpi != rpit)
				{
					double d = coefficients[rpi][cpit];
					coefficients[rpi][cpit] = 0;
					for(int j = iterP1; j < colCount; ++j)
					{
						int cpj = colPermut[j];
						coefficients[rpi][cpj] -= d*coefficients[rpit][cpj];
					}
					for(int j = 0; j < b.colCount; ++j)
					{
						b.coefficients[rpi][j] -= d*b.coefficients[rpit][j];
					}
				}
			}
		}
		Matrix ret = new Matrix();
		ret.rowCount = b.rowCount;
		ret.colCount = b.colCount;
		ret.coefficients = new double[ret.rowCount][ret.colCount];
		for(int i = 0; i < ret.rowCount; ++i)
		{
			int cpi = colPermut[i];
			int rpi = rowPermut[i];
			for(int j = 0; j < ret.colCount; ++j)
				ret.coefficients[rpi][j] = b.coefficients[cpi][j];
		}
		return ret;
	}

	/**
	 * Résout this * x = b, et renvoie x. La résolution utilise la méthode
	 * d'élimination de Gauss Jordan. this et b ne sont pas altérés par la
	 * résolution.
	 * @return x, tel que muls(x).equals(b)
	 * @since jPicEdt 1.6
	 */
	public Matrix gaussJordanSolve(Matrix b){
		return new Matrix(this).gaussJordanTrashSolve(new Matrix(b));
	}

	/** Convertit la matrice this en chaîne de charactère pour débogage
	 * @since jPicEdt 1.6
	 */
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("[");
		for(int i = 0; i < rowCount; ++i)
		{
			ret.append("[");
			for(int j = 0; j < colCount; ++j)
			{
				if(j != 0)
					ret.append(" ");
				ret.append(coefficients[i][j]);
			}
			ret.append("]");
		}
		ret.append("]");
		return ret.toString();
	}

};


/// Matrix.java ends here
