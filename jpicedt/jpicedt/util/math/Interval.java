// Interval.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: Interval.java,v 1.4 2013/03/27 06:49:56 vincentb1 Exp $
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

// Àpprofondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
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

/**
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class Interval implements Cloneable
{
	double min;
	double max;

	public Interval clone(){ return new Interval(min,max); }
	public double getMin(){ return min; }
	public double getMax(){ return max; }
	public boolean isEmpty(){ return max < min; }

	public Interval(double min,double max){
			this.min=min;
			this.max=max;
	}
	public Interval(Interval x){
		min = x.min;
		max = x.max;
	}
	public Interval(double[] a){
		min= a[0];
		max= a[1];
	}

	public Interval(){
		this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	/**
	 * Affecte les bornes de l'intervalle.
	 *
	 * @param min une valeur <code>double</code> donnant la nouvelle borne min
	 * de l'intervalle
	 * @param max une valeur <code>double</code> donnant la nouvelle borne max
	 * de l'intervalle
	 */
	public void set(double min, double max){
		this.min = min;
		this.max = max;
	}

	public void setToR(){
		min = Double.NEGATIVE_INFINITY;
		max = Double.POSITIVE_INFINITY;
	}

	public void setToEmpty(){
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
	}

	public boolean equals(Interval x)
		{
			return min == x.min && max == x.max;
		}

	public boolean contains(double x)
		{
			return min <= x && x <= max;
		}

	public boolean intersects(Interval other)
		{
			return min <=  other.max && max >= other.min;
		}

	public Interval intersect(Interval other)
		{
			if(min < other.min)
				min = other.min;
			if(max > other.max)
				max = other.max;

			return this;
		}

	public Interval cloneIntersect(Interval other)
		{
			return new Interval(this).intersect(other);
		}


	public String toString()
		{
			String ret = new String(
				"[" + min + ", " + max + "]");
			return ret;
		}
}


/// Interval.java ends here
