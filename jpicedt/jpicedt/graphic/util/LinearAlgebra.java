// LinearAlgebra.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: LinearAlgebra.java,v 1.5 2013/10/07 20:07:57 vincentb1 Exp $
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

/// Installation:



/// Code:
package jpicedt.graphic.util;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.model.*;

/**
 * Ensemble de fonctions statiques d'alg�bre lin�aire dans un espace vectoriel
 * r�el de dimension 2.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version $Id: LinearAlgebra.java,v 1.5 2013/10/07 20:07:57 vincentb1 Exp $
 * @since jPicEdt 1.6
 */
public class LinearAlgebra{
	 /**  Fait le produit d'une matrice 2&times;2 <code>matrix</code> et d'un vecteur <code>x</code>.
	  * @param matrix Les points dans <code>matrix</code> sont rang�s comme �a:<br>
	  *  matrix= { {matrix_x_x, matrix_x_y},{matrix_y_x,matrix_y_y}};<br>
	  *  o� le premier indice d�signe la ligne, et le second la colonne
	  * @param x objet de l'application lin�aire d�finie par matrix.
	  * @return image du point <code>x</code> par l'application lin�raire d�finie par <code>matrix</code>
	  * @since jPicEdt 1.6
	  */
	static public PicVector linearApplication(double[][] matrix,PicPoint x){
			PicVector ret = new PicVector();
			ret.setLocation(
				x.getX()*matrix[0][0] + x.getY()*matrix[0][1],
				x.getX()*matrix[1][0] + x.getY()*matrix[1][1]);
			return ret;
		}

	/**
	   Fait le produit d'une matrice 2&times;2 <code>matrix</code> et d'un vecteur <code>x</code>, affecte le
	   r�sultat � <code>x</code>, et retourne le r�sultat.
	  * @param matrix Les points dans <code>matrix</code> sont rang�s comme �a&nsbp;:<br>
	  *  matrix= { {matrix_x_x, matrix_x_y},{matrix_y_x,matrix_y_y}};<br>
	  *  o� le premier indice d�signe la ligne, et le second la colonne
	  * @param x objet de l'application lin�aire d�finie par matrix, <code>x</code> est chang� par l'appel de
	  * cette fonction.
	  * @return x
	 */
	static public PicVector vectorLinAp(double[][] matrix,PicVector x){
			x.setCoordinates(
				x.getX()*matrix[0][0] + x.getY()*matrix[0][1],
				x.getX()*matrix[1][0] + x.getY()*matrix[1][1]);
			return x;
		}


	/**
	 * Renvoie une matrice dont <code>n1</code> transpos� est la premi�re
	 * ligne, et <code>n2</code> transpos� la seconde ligne (o� l'on consid�re
	 * les <code>PicVector</code> comme des vecteurs colonne).
	 *
	 * @param n1 une valeur <code>PicVector</code> pour le premier vecteur normal
	 * @param n2 une valeur <code>PicVector</code> pour le second vecteur normal
	 * @return une valeur <code>double[][]</code> pour la matrice 2&times;2 renvoy�e
	 */
	static public double[][] normalVectorsToMatrix(PicVector n1,PicVector n2){
		double[][] ret = {{ n1.getX(), n1.getY()},{ n2.getX(), n2.getY()}};
		return ret;
	}

    /**
	 * Calcul l'image de <code>x</code> par une application lin�aire d�finie
	 * par l'inverse <code>matrix</code>.
	 *
	 * Si <code>matrix</code> n'est pas inversible, il se produit une division
	 * par 0.
	 *
	 * Le calcul est fait selon la r�gle de Cramer
	 *
	 * @see <a href="http://fr.wikipedia.org/wiki/R%C3%A8gle_de_Cramer">R�gle de Cramer</a>
	 * @param matrix une valeur <code>double</code>
	 * @param x une valeur <code>PicVector</code>
	 * @return la valeur <code>PicVector</code> image de <code>x</code> par
	 * l'inverse de <code>matrix</code>.
	 */
	static public PicVector invLinearApplication(double[][] matrix,PicPoint x){
		double invDet = 1.0 /(matrix[0][0]*matrix[1][1] - matrix[0][1]*matrix[1][0]);
		PicVector ret = new PicVector();
		ret.setCoordinates(
			(x.getX()*matrix[1][1] - x.getY()*matrix[0][1])*invDet,
			(matrix[0][0]*x.getY() - matrix[1][0]*x.getX())*invDet);
		return ret;
	}



	/** Effectue le produit des matrices 2&times;2 matrix1 et matrix2
	 * @since jPicEdt 1.6
	 */
	static public double[][] matrixProduct(double[][] matrix1,double[][] matrix2){
		double[][] ret = {{
				matrix1[0][0]*matrix2[0][0] + matrix1[0][1]*matrix2[1][0],
				matrix1[0][0]*matrix2[0][1] + matrix1[0][1]*matrix2[1][1]},{
				matrix1[1][0]*matrix2[0][0] + matrix1[1][1]*matrix2[1][0],
				matrix1[1][0]*matrix2[0][1] + matrix1[1][1]*matrix2[1][1]}};

		return ret;
	}

	/** Effectue le produit des matrices 2&times;2 matrix1 et matrix2, et l'affecte �
	 * <code>matrix1</code>. Renvoie le r�sultat.
	 * @since jPicEdt 1.6
	 */
	static public double[][] matrixMultiply(double[][] matrix1,double[][] matrix2){

		double a00 = matrix1[0][0]*matrix2[0][0] + matrix1[0][1]*matrix2[1][0];
		double a01 = matrix1[0][0]*matrix2[0][1] + matrix1[0][1]*matrix2[1][1];
		double a10 = matrix1[1][0]*matrix2[0][0] + matrix1[1][1]*matrix2[1][0];

		matrix1[1][1]= matrix1[1][0]*matrix2[0][1] + matrix1[1][1]*matrix2[1][1];

		matrix1[0][0]= a00;
		matrix1[0][1]= a01;
		matrix1[1][0]= a10;

		return matrix1;
	}


	/** Effectue la transposition de la matrice <code>matrix</code> et retourne le
		r�sultat. <code>matrix</code> est inchang� par l'op�ration.
	 */
	static public double[][] matrixTransposition(double[][] matrix){
		double[][] ret = {{matrix[0][0],matrix[1][0]},
						  {matrix[0][1],matrix[1][1]}};
		return ret;
	}

	/** Application lin�aire sur chaque point de contr�le d'un AbstractElement
	 * @param matrix matrice d�finissant l'application lin�aire
	 * @param abstElt AbstractElement objet de l'application lin�raire.
	 * @since jPicEdt 1.6
	 */
   	static public void abstractEltLinAp(double[][] matrix,Element abstElt){
		// le truc c'est qu'on passe par un tableau car si on change
		// l'un des points de contr�le �a peut avoir un effet sur les suivants.
		PicPoint[]  ctrlPoints =
			new PicPoint[abstElt.getLastPointIndex()
						 -abstElt.getFirstPointIndex()+1];
		for(int i = abstElt.getFirstPointIndex();
			i <= abstElt.getLastPointIndex(); ++i)
		{
			PicPoint ctrlPt = linearApplication(
				matrix,
				abstElt.getCtrlPt(i,null));
			ctrlPoints[i-abstElt.getFirstPointIndex()] = ctrlPt;
		}
		for(int i = abstElt.getFirstPointIndex();
			i <= abstElt.getLastPointIndex(); ++i)
		{

			abstElt.setCtrlPt(
				i,
				ctrlPoints[i-abstElt.getFirstPointIndex()]
				);
		}
	}

};


/// LinearAlgebra.java ends here
