// NestedPsCustomException.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2009/2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: NestedPsCustomException.java,v 1.3 2013/03/27 07:09:30 vincentb1 Exp $
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
package jpicedt.format.output.pstricks;
import jpicedt.format.output.util.FormatterException;
import jpicedt.graphic.model.PicGroup;
import jpicedt.ui.dialog.RadioChoiceDialog;
import jpicedt.JPicEdt;

import static jpicedt.graphic.model.BranchElement.CompoundMode.*;

public class NestedPsCustomException extends FormatterException
{
	public PstricksFormatter factory;
	public PicGroup          containedPsCustomGroup;

	NestedPsCustomException(PstricksFormatter factory,
							PicGroup containedPsCustomGroup){

		this.factory = factory;
		this.containedPsCustomGroup = containedPsCustomGroup;
	}

	public void resolve(){
		String[] alternatives = {
			"format.pstricks.NestedPsCustomException.choice.outerremove",
			"format.pstricks.NestedPsCustomException.choice.innerremove",
			"format.pstricks.NestedPsCustomException.choice.donothing"
		};
		RadioChoiceDialog dialog =
			new RadioChoiceDialog(
				JPicEdt.getMDIManager(),
				"format.pstricks.NestedPsCustomException.title",
				"["+factory.getContainerPsCustom().getName()+
				"[" + containedPsCustomGroup.getName()+"]]",
				"format.pstricks.NestedPsCustomException.prompt1",
				"format.pstricks.NestedPsCustomException.prompt2",
				alternatives,
				1
				);

		switch(dialog.getUserRadioChoice())
		{
		case 0:
			// outer remove
			factory.getContainerPsCustom().setCompoundMode(SEPARATE);
			break;
		case 1:
			// inner remove
			containedPsCustomGroup.setCompoundMode(SEPARATE);
			break;
		}

	}
};


/// NestedPsCustomException.java ends here
