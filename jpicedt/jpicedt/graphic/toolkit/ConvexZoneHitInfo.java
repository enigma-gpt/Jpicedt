// ConvexZoneHitInfo.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneHitInfo.java,v 1.4 2013/03/27 06:59:31 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import jpicedt.graphic.event.PEMouseEvent;
import java.util.ArrayList;

/**
 * Un <code>ConvexZoneHitInfo</code> comprend l'information relative � un clic
 * de souris ayant port� sur un <code>ConvexZone</code> particulier.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version $Id: ConvexZoneHitInfo.java,v 1.4 2013/03/27 06:59:31 vincentb1 Exp $
 */
public interface ConvexZoneHitInfo{
	ConvexZoneGroup getTarget();

	/**
	 * @return l'�v�nement souris qui d�clencha ce <code>ConvexZoneHitInfo</code>
	 */
	PEMouseEvent getMouseEvent();

	List append(ConvexZoneHitInfo hi);

	public class CZHitInfo implements ConvexZoneHitInfo{
		PEMouseEvent e;
		ConvexZoneGroup target;
		public CZHitInfo(ConvexZone target,PEMouseEvent e){
			this.e = e;
			this.target = new ConvexZoneGroup(target);
		}

		public PEMouseEvent      getMouseEvent(){ return e; }
		public ConvexZoneGroup   getTarget(){ return target; }
		public List              append(ConvexZoneHitInfo hi){
			return (new List(this)).append(hi);
		}
	};

	class List  extends ArrayList<ConvexZoneHitInfo> implements ConvexZoneHitInfo{
		ConvexZoneGroup target;
		public PEMouseEvent getMouseEvent(){ return get(0).getMouseEvent(); }
		public ConvexZoneGroup   getTarget(){
			return target;
		}
		public List append(ConvexZoneHitInfo czHitInfo){
			super.add(czHitInfo);
			if(target == null)
				target = czHitInfo.getTarget();
			else
				target.append(czHitInfo.getTarget());
			return this;
		}
		public List(){}
		public List(CZHitInfo czHitInfo){
			add((ConvexZoneHitInfo)czHitInfo);
			if(target == null)
				target = new ConvexZoneGroup();
			target.append(czHitInfo.getTarget());
		};
	}

	/**
	 * Represent a Hit that occured on an <code>ConvexZone</code>. It includes
	 * information as of which stroke segment was hit (this may be used by the
	 * UI to know where to add new points).
	 */
	public static class Stroke extends CZHitInfo {

		private int clickedSegment;


		/**
		 * construct a new <code>HitInfo.Stroke</code> that occured on the
		 * stroke of the given <code>Element</code>
		 * @param clickedSegment Point the index (starting from 0) of the segment of the flattened path
		 * on which the hit occured.
		 */
		public Stroke(ConvexZone clicked, int clickedSegment, PEMouseEvent mouseEvent){
			super(clicked, mouseEvent);
			this.clickedSegment = clickedSegment;
		}

		/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return return the child on which the mouse-click occured
		 * @since jPicEdt
		 */
		public int getClickedSegment(){
			return clickedSegment;
		}

		public String toString(){
			return "ConvexZoneHitInfo.Stroke : target="+getTarget()+", clickedSegment="+clickedSegment;
		}

	}

};


/// ConvexZoneHitInfo.java ends here
