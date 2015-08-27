// ConvexZoneHitInfo.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneHitInfo.java,v 1.4 2013/03/27 06:59:31 vincentb1 Exp $
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



/// Code:
package jpicedt.graphic.toolkit;

import jpicedt.graphic.event.PEMouseEvent;
import java.util.ArrayList;

/**
 * Un <code>ConvexZoneHitInfo</code> comprend l'information relative à un clic
 * de souris ayant porté sur un <code>ConvexZone</code> particulier.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @version $Id: ConvexZoneHitInfo.java,v 1.4 2013/03/27 06:59:31 vincentb1 Exp $
 */
public interface ConvexZoneHitInfo{
	ConvexZoneGroup getTarget();

	/**
	 * @return l'événement souris qui déclencha ce <code>ConvexZoneHitInfo</code>
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
