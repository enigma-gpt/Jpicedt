// MouseTransform.java --- February 24, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: MouseTransform.java,v 1.10 2013/03/27 06:57:21 vincentb1 Exp $
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

import jpicedt.graphic.model.Element;
import jpicedt.graphic.event.PEMouseEvent;

import java.awt.*;
import java.awt.geom.*;

/**
 * An interface that specifies behaviours shared by mouse-driven object-editing actions, especially
 * when driven by <code>SelectionTool</code>.
 * Sequence order is:
 * <ol>
 * <li>mouse-press : <code>start()</code>
 * <li>mouse-dragged : <code>process()</code>
 * <li>mouse-released : <code>next()</code> ? no &rArr; <code>end()</code>
 * <li>mouse-moved : <code>process()</code>
 * <li>mouse-pressed : <code>next()</code> ? no &rArr; <code>end()</code>
 * <li>etc&hellip;
 * </ol>
 * @since jpicedt 1.3.2
 */
public interface MouseTransform {

	/**
	 * Called when the mouse is pressed. The transform should do the initialization work here.
	 * @since jpicedt 1.3.2
	 */
	public void start(PEMouseEvent e);

    /**
     * Called when the mouse is dragged/moved after the first mouse-pressed event.
	 * Element geometry update should occur here.
	 * @since jpicedt 1.3.2
     */
    public void process(PEMouseEvent e);

	/**
	 * Called when the left mouse-button changes state.
	 * @return <code>true</code> if there's another task in the sequence,
	 * <code>false</code> if mouse-transform has completed with this
	 * mouse-event.
	 */
	public boolean next(PEMouseEvent e);

	/**
	 * Return the cursor for this MouseTransform
	 * @since jpicedt 1.3.2
	 */
	public Cursor getCursor();

	/**
	 * Allows the MouseTransform to do specific graphic rendering when it's operating.
	 * @since jpicedt 1.3.2
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale);

	/**
	 * Return a help-message for the UI, that makes sense with this transform.
	 */
	public String getHelpMessage();


} // MouseTransform
