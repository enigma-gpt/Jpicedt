/* RotateEvent.java - September 11, 2007 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal,
  2010-2011 Stephan Schulte, Benjamin Poniatowski

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
