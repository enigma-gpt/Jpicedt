// CommentFormatting.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: CommentFormatting.java,v 1.4 2013/03/27 07:05:27 vincentb1 Exp $
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
package jpicedt.graphic.io.formatter;
import  java.io.Writer;
import  java.lang.*;

/**
 * Une interface pour écrire des commentaires pour un certain type de contenu,
 * de sorte à enfouir le code JPIC-XML en en-tête de ce type de contenu. Il y
 * a deux niveaux de commentaire (fort avec strongCommentFormat et faible avec
 * weakCommentFormat). Le type fort sera utilisé pour la délimitation du code
 * JPIC-XML, alors que le type faible sera pour contenir le code JPIC-XML
 * lui-même.
 * @since jpicedt 1.6
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @version $Id: CommentFormatting.java,v 1.4 2013/03/27 07:05:27 vincentb1 Exp $
 */
public interface CommentFormatting
{
	String  getContentTypeCommentFormatting();
	String  getConcreteContentType();
	void    setWriter(Writer writer);
	void    verbatimWriteLine(String s) throws java.io.IOException;
	void    strongCommentFormat(String s);
	void    weakCommentFormat  (String s);
}


/// CommentFormatting.java ends here
