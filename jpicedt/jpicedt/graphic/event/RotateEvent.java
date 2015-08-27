/* RotateEvent.java - September 11, 2007 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal,
  2010-2011 Stephan Schulte, Benjamin Poniatowski

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

import java.util.EventObject;
import jpicedt.graphic.PECanvas;

/**
 * Class for notifications of rotation changes sourced by a PECanvas.
 * @author Stephan Schulte, Benjamin Poniatowski
 * @since jpicedt 1.5
 * @version $Id: RotateEvent.java,v 1.3 2013/03/27 07:06:12 vincentb1 Exp $
 */
public class RotateEvent extends EventObject {
	/** the current angle */
	private double angle;

	/**
	 * a new RotateEvent sourced from the given PECanvas
	 * @param source the originator of the event
	 * @param angle the current angle
	 */
	public RotateEvent(PECanvas source, double angle){
		super(source);
		this.angle = angle;
	}

	/**
	 * return the current angle
	 */
	public double getAngleValue(){
		return angle;
	}

	/**
	 * a textual representation of this event
	 */
	public String toString(){
		return "source=" + source + ", angle = " + angle;
	}
}
