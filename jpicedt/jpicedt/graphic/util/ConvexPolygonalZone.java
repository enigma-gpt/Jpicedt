// ConvexPolygonalZone.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009 -- 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexPolygonalZone.java,v 1.10 2013/03/27 06:56:01 vincentb1 Exp $
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
import java.util.ArrayList;

import static java.lang.Math.abs;

/** Zone polygonale convexe mémorisée comme un vecteur de demi-plans et
 *  définie comme l'intersection de ces demi-plans, chaque demi plan numéro
 *  <code>i</code> est défini par un point <code>PicPoint
 *  zone.get(i).getOrg()</code> par lequelle passe sa droite frontière, et par
 *  un vecteur <code>PicVecteur zone.get(i).getDir()</code> normal à sa droite
 *  frontière, et pointant du côté du demi-plan considéré
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class ConvexPolygonalZone extends ArrayList<ConvexPolygonalZone.HalfPlane>
{

    /** Classe représentant un demi plan par un PicPoint org compris dans la
	 * droite frontière du demi-plan, et par la direction normale dir à cette
	 * droite, le demi plan considéré étant celui pointé par dir.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	public class HalfPlane{
		/**
		 * <code>org</code> est un point par lequel passe le demi-plan.
		 */
		PicPoint  org;
		/**
		 * <code>dir</code> est le vecteur normal et normé au demi-plan
		 * pointant du côté où se trouve le demi-plan.
		 */
		PicVector dir;
		/**
         *  Construit un demi-plan. <b>Attention</b> <code>org</code> et <code>dir</code> ne sont pas
		 *	dupliqués. Par ailleurs il est indispensable que <code>dir</code> soit de norme euclidienne égale
		 *	un.
		 * @param org un <code>PicPoint</code> par lequel passe la bordure du demi-plan à créer.
		 * @param dir un <code>PicVector</code> normé, normal à la bordure du demi-plan, et pointant vers
		 * l'intérieur du demi-plan à créer.
         */
		HalfPlane(PicPoint  org,PicVector dir){
			this.org = org;
			this.dir = dir;
		}

		public HalfPlane clone(){ return new HalfPlane(org,dir); }

		public HalfPlane dirCInverse(){ return new HalfPlane(org,dir.cInverse());}

		public PicPoint  getOrg(){ return org; }
		public PicVector getDir(){ return dir; }
		public String    toString(){ return "[" + org + ", "+dir+"]"; }
		public boolean   contains(PicPoint pt){ return dir.dot(new PicVector(org,pt)) >= 0;}
	};

	public ConvexPolygonalZone(){}


	public ConvexPolygonalZone(int i){
		super(i);
	}

	/**
	 * Étend la zone polygonal convexe <code>this</code> de sorte qu'elle soit l'enveloppe convexe de sa
	 * valeur actuelle et de l'ensemble de point <code>points</code>.<p><b>Attention:</b> la valeur de
	 * <code>points</code> peut être modifiée par l'appel.
	 *
	 * @param points un tableau de <code>PicPoint</code> contenant les points dont on veut l'enveloppe
	 * convexe.
	 * @param pointCount le nombre de <code>PicPoint</code> à considérer au sein de <code>points</code>, c'est
	 * à dire que seul les indexes 0, 1, &hellip; <code>pointCount</code>-1 sont pris en compte.
	 */
	public void extendByConvexHull(PicPoint[] points, int pointCount){
		// Étape N°1 : suppression des points doubles
		for(int i = 0; i < pointCount; ++i)
			for(int j = i+1; j < pointCount;){
				if(points[i].equals(points[j]))
					points[j] = points[--pointCount];
				else
					++j;
			}

		// Étape N°2 : recherche des demi-plans pertinants
		ConvexPolygonalZone.HalfPlane hp = new HalfPlane(null,null);
		for(int i = 0; i < pointCount; ++i)
			for(int j = i+1; j < pointCount;++j){
				hp.org = (new PicPoint(points[i])).middle(points[j]);
				hp.dir = new PicVector(points[i],points[j]).normalize().iMul();
				boolean first = true;
				boolean ok = true;
				int k;
				for(k = 0; k < pointCount; ++k){
					if(k != i && k != j){
						if(first){
							// on capture la direction
							if(!hp.contains(points[k]))
								hp.getDir().inverse();
							first = false;
						}
						else{
							if(!hp.contains(points[k])){
								k = pointCount;
								ok = false;
							}
						}
					}
				}
				if(ok)
					this.addHalfPlane(hp.org,hp.dir);
			}

		// Étape N°3 : suppression des demi-plans redondants
		PicVector vec = new PicVector();
		for(int i = 0; i < this.size(); ++i){
			ConvexPolygonalZone.HalfPlane hpI = this.get(i);
			for(int j = i+1; j < size();){
				ConvexPolygonalZone.HalfPlane hpJ = this.get(j);
				vec.setCoordinates(hpI.getOrg(),hpJ.getOrg());
				if(abs(hpI.getDir().dot(vec)) <= 1.0e-13*vec.norm1())
					this.remove(j);
				else
					++j;
			}
		}
	}

	public ConvexPolygonalZone clone(){ return (ConvexPolygonalZone)super.clone(); }

	public void removeAll(){
		super.removeRange(0,super.size());
	}

	public boolean contains(PicPoint pt){
		boolean ret = true;

		for(HalfPlane  hp :this)
		{
			ret = hp.contains(pt);
			if(!ret)
				return ret;
		}
		return ret;
	}

	/** ajoute un demi-plan passant par org, normal à normalDir, et du côté
	 * pointé par par normalDir. <b>Attention:</b> org et normalDir ne sont
	 * plas clonés.*/
	public void addHalfPlane(PicPoint org,PicVector normalDir){
		add(new HalfPlane(org,normalDir));
	}

	/**
	 * @see ConvexPolygonalZoneBoundary
	 * @return La frontière de la zone polygonale convexe <code>this</code>
	 */
	public ConvexPolygonalZoneBoundary getBoundary(){
		ConvexPolygonalZoneBoundaryFactory factory = new ConvexPolygonalZoneBoundaryFactory(this);

		return factory.getBoundary();
	}

	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("[");
		boolean subsequent = false;
		for(HalfPlane hp : this)
		{
			if(subsequent)
				ret.append(", ");
			else
				subsequent = true;
			ret.append(hp.toString());
		}
		ret.append("]");
		return ret.toString();
	}


};





/// ConvexPolygonalZone.java ends here
