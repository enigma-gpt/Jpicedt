// BasicEditPointConstraint.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: BasicEditPointConstraint.java,v 1.2 2013/03/27 06:59:56 vincentb1 Exp $
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

import jpicedt.graphic.model.EditPointConstraint;
import jpicedt.ui.dialog.UserConfirmationCache;

public class BasicEditPointConstraint implements EditPointConstraint{
	private UserConfirmationCache userConfirmationCache;
	private EditPointConstraint.EditConstraint editConstraint;

	public static BasicEditPointConstraint SQUARE =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.SQUARE);
	public static BasicEditPointConstraint CENTER_FIXED =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.CENTER_FIXED);
	public static BasicEditPointConstraint DEFAULT =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.DEFAULT);
	public static BasicEditPointConstraint FREELY =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.FREELY);
	public static BasicEditPointConstraint SMOOTHNESS_SYMMETRY =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.SMOOTHNESS_SYMMETRY);
	public static BasicEditPointConstraint SMOOTHNESS =
		new BasicEditPointConstraint(EditPointConstraint.EditConstraint.SMOOTHNESS);

	public BasicEditPointConstraint(UserConfirmationCache userConfirmationCache,
									EditPointConstraint.EditConstraint editConstraint){
		this.userConfirmationCache = userConfirmationCache;
		this.editConstraint = editConstraint;
	}

	public BasicEditPointConstraint(UserConfirmationCache userConfirmationCache){
		this(userConfirmationCache,EditPointConstraint.EditConstraint.DEFAULT);
	}

	public BasicEditPointConstraint(EditPointConstraint.EditConstraint editConstraint){
		this(null,editConstraint);
	}


	public void setUserConfirmationCache(UserConfirmationCache userConfirmationCache){
		this.userConfirmationCache = userConfirmationCache;
	}

	public void setEditConstraint(EditPointConstraint.EditConstraint editConstraint){
		this.editConstraint = editConstraint;
	}

	public 	UserConfirmationCache getUserConfirmationCache(){ return userConfirmationCache; }
	public 	EditPointConstraint.EditConstraint getEditConstraint(){ return editConstraint; }

}

/// BasicEditPointConstraint.java ends here
