// Interval.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: Interval.java,v 1.4 2013/03/27 06:49:56 vincentb1 Exp $
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

// �pprofondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
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
package jpicedt.util.math;

/**
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
