/* ZoomEvent.java - February 19, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 D�partement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
package jpicedt.graphic.event;

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;

import java.util.*;
import javax.swing.event.*;

/**
 * Class for notifications of zoom-factor changes sourced by a PECanvas.
 * @author Sylvain Reynal
 * @version $Id: ZoomEvent.java,v 1.7 2013/03/27 07:05:42 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class ZoomEvent extends EventObject {

	/** the old zoom factor */
	private double oldZoom;
	/** the new zoom factor */
	private double newZoom;
	/** the coordinate of the point that was at the center of the canvas before the zoom changed */
	private PicPoint ptCenter;

	/**
	 * a new ZoomEvent sourced from the given PECanvas
	 * @param source the originator of the event
	 * @param oldZoom the previous zoom value
	 * @param newZoom the new zoom value
	 * @param ptCenter the point (in model-coordinates) that is expected to be at the center of the
	 *        view-port. Can be null.
	 */
	public ZoomEvent(PECanvas source, double oldZoom, double newZoom, PicPoint ptCenter){
		super(source);
		this.oldZoom = oldZoom;
		this.newZoom = newZoom;
		if (ptCenter != null) this.ptCenter = new PicPoint(ptCenter);
		else this.ptCenter = null;
	}

	/**
	 * Return the old zoom factor value
	 */
	public double getOldZoomValue(){
		return oldZoom;
	}

	/**
	 * Return the new zoom factor value
	 */
	public double getNewZoomValue(){
		return newZoom;
	}

	/**
	 * Return the coordinates of the point that was at the center of the canvas before the zoom changed.
	 * This is aimed at allowing javax.swing.ViewPort, javax.swing.Scrollpane, etc... to update their view accordingly.
	 */
	public PicPoint getCenterPoint(){
		return ptCenter;
	}

	/**
	 * a textual representation of this event
	 */
	public String toString(){
		return "source=" + source
		+ ", old zoom = " + oldZoom
		+ ", new zoom = " + newZoom
		+ ", center point = " + ((ptCenter==null) ? "null" : ptCenter.toString());
	}

}
