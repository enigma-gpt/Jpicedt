/* ZoomEvent.java - February 19, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Département de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
