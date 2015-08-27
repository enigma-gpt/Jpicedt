#!/usr/bin/env perl
# javaprop2texiflag.pl --- -*- coding: utf-8 -*-
# Copyright 2012 Vincent Belaïche
#
# Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
# Version: $Id: javaprop2texiflag.pl,v 1.5 2013/09/10 05:08:47 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
# redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
# les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les concédants successifs.
#
# A cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
# l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
# étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve donc
# à des développeurs et des professionnels avertis possédant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs besoins dans des
# conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et, plus généralement,
# à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
#
# Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accepté les termes.
#
## Commentary:

#

## Installation:

## Code:
use strict;
use warnings;
use feature qw(say unicode_strings);
use PerlIO;

my $version='$Id: javaprop2texiflag.pl,v 1.5 2013/09/10 05:08:47 vincentb1 Exp $';
my $error_count = 0;
my $linenb = 0;
my $inputfile;
my $verbose;
my $quiet;
my $valid_texi_key = '\A[A-Za-z0-9_-]*\Z';
my %flag_duplication = ();

sub usage
{
	my $retval = shift;
	print "This script turns a Java .properties file into a file which is readable
by texinfo. All properties are converted into corresponding \@set-tings
which can be referred by \@value{...}.

At the end of the run it prints
- a message, which file was processed and of how many lines it consists
- a message, how many \@set-tings are produced  

The returned value is the -N, where N is the error count

    Usage:
	javaprop2texiflag.pl ARGUMENTS LIST

--version           : show version and exit
-?, --help          : show this message and exit
-c, --showcomments  : Translate comments into the output
-e, --showemptylines: Translate empty lines into the output
-h, --header ARG    : Add a header ARG to the produced output
-i, --input  ARG    : Set input to ARG, otherwise it is STDIN
-o, --output ARG    : Set output to ARG, otherwise it is STDOUT
-p, --prefix ARG    : Set prefix to ARG, prefix is empty by default
                      and is prefixed to all flags
-q, --quiet         : Do not output any messages, supersedes --verbose

-s, --substitute ARG: evaluate Perl statement ARG, for each java property key
                      with $_ being the java property key of interest. Typically
                      you would use this to escape caracters that are not
                      matching /[A-Za-z0-9_-]/ which is the regexp of characters
                      allowed in texinfo flags. When this option is not set,
                      then javaprop2texiflag set it by default to: 

                       -s 's/[^A-Za-z0-9_-]/-/g'

                      which means that all non allowed characters are replaced
                      by `-'. For instance the java property defined as:

                          a\:b.c\=d_e-fàé=Some value

                      is translated to:

                      \@set a-b-c-d_e-f-- Some value

-v, --verbose       : Output some more messages
";
	exit $retval;
}

#
#
sub  jp2texif_unescape
{
	$_ = shift;
	s!\\(n(?{"\n"})|r(?{"\r"})|f(?{"\f"})|t(?{"\t"})|u([0-9A-F]{4})(?{chr hex $2})|(.)(?{$3}))!$^R!g;
	return $_;
}

# entrée: la clef non traitée, telle que lu dans le fichier d'entrée
# sortie: \: et \= remplacés par : et =.
sub jp2texif_key_unescape
{
	$_ = shift;
	s!\\([\.:])!$1!g;
	return $_;
}

sub jp2texif_key_encode
{
	$_ = shift;
	foreach my $substitute_elem (@::substitute_list){
		eval($substitute_elem);
	}
	return $_;
}

sub jp2texif_encode
{
	$_ = shift;
	s!(([\@\{\}])(?{'@'."$2"})|\n(?{'@*'}))!$^R!g;
	# Texinfo-fier les espaces de tête pour les rendre significatifs
	if(/\A([ \t]+)(.*)\Z/)
	{
		my $spaceprefix    = $1;
		my $remainder = $2;
		$_ = "\@w{" . $spaceprefix . "}" . $remainder;
	}
	# Texinfo-fier les espaces de queue pour les rendre significatifs
	if(/(.+?)([ \t]+)\Z/)
	{
		my $spacepostfix   = $2;
		my $remainder = $1;
		$_ = $remainder . "\@w{" . $spacepostfix . "}";
	}
	return $_
}

my @header = ();
our @substitute_list = ();
my $prefix         = "";
my $outputfile;
my $showcomments;
my $showemptylines;

my $i = 0;
while($i < @ARGV){
	if($i + 1 < @ARGV)
	{
		if($ARGV[$i] =~ /\A-(i|-input)\Z/)
		{
			$inputfile = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(o|-output)\Z/)
		{
			$outputfile = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(p|-prefix)\Z/)
		{
			$prefix = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(h|-header)\Z/)
		{
			$header[++$#header] = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(s|-substitute)\Z/)
		{
			$substitute_list[++$#substitute_list] = $ARGV[$i+1];
			$i = $i +2;
		}
		else
		{
			goto ONE_ARG;
		}
	}
	else
	{
	  ONE_ARG:
		{
			if($ARGV[$i] =~ /\A-(c|-showcomments)\Z/)
			{
				$showcomments = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(e|-showemptylines)\Z/)
			{
				$showemptylines = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(q|-quiet)\Z/)
			{
				$quiet = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(v|-verbose)\Z/)
			{
				$verbose = 1;
				$i ++;
			}
			elsif($ARGV[$i] eq "--version")
			{
				print "Version of javaprop2texiflag.pl = $version\n";
				exit 0;
			}
			elsif($ARGV[$i] =~ /\A-\?|--help\Z/)
			{
				usage(0);
				exit;
			}
			else
			{
				print "Invalid remaining arguments: @ARGV[$i .. $#ARGV]\n";
				usage(-1);
			}
		}
	}
}

if(!($prefix =~ m!$valid_texi_key!))
{
	$error_count ++;
	if(!$quiet)
	{
		say STDERR "Warning: invalid cararacters found in prefix.";
	}
}

if($#substitute_list == -1)
{
	$substitute_list[++$#substitute_list] = 's/[^A-Za-z0-9_-]/-/g';
}


my $in;
if($inputfile)
{ 
	open($in,  "< :encoding(ISO-8859-1)", $inputfile)  or die "Can't open $inputfile $!";
}
else
{
	$in = \*STDIN;
}
my $out;
if($outputfile)
{
	open($out, "> :encoding(UTF-8)", $outputfile) or die "Can't open $outputfile $!";
}
else
{
	$out = \*STDOUT;
}
select $out;

my $line;
if(@header)
{
	foreach(@header){
		say '@c ', $_;
	}
}

my $folded_line = 0;
my $flagnb      = 0;

my $prop_key;
my $prop_linenb;
my $propname;
my $propval;
my $nextpropval;

LINE: while(<$in>){
	$line = $_ ;
	$linenb++ ;
	if($line =~ /\A(\s*)[#!](.*)\Z/)
	{
		if($showcomments)
		{
			say "$1" , '@c ' , "$2";
		}
		next LINE;
	}
	elsif($line =~ /\A\s*\Z/)
	{
		if($showemptylines)
		{
			say "\n";
		}
		next LINE;
	}
	elsif($line =~ /\A(\s*(.*))\Z/)
	{
		if($folded_line == 0)
		{
			if($line =~ /\A\s*((?:[a-zA-Z0-9_\.-]|\\[nr=:])+)\s*[=:](.*)\Z/)
			{
				$propval = $2;
				$prop_key = $1;
				$prop_linenb = $linenb;
				$propname = jp2texif_key_encode(jp2texif_key_unescape($1));
				if(!($propname =~ m!$valid_texi_key!) && !$quiet)
				{
					say STDERR "$inputfile:$linenb: Warning invalid characters found in Texinfo flag basename: `$propname'";
				}
				if($propval =~ m!(\\+)$! && (length($1) & 1) == 1)
				{
					# nombre impair de contre-obliques en fin de ligne, c'est un repliement
					$folded_line = 1;
					$propval =~ s!.$!!;
					$propval = jp2texif_unescape($propval);

				}
				else
				{
					$flagnb ++;
					my $found = $flag_duplication{$propname};
					if($found)
					{
						if(!$quiet)
						{
							if($prop_key eq $$found[0])
							{
								say STDERR "$inputfile:$prop_linenb: Error java property key `$prop_key' is duplicated with key found at line $found->[1]. Second definition omitted";
							}
							else
							{
								say STDERR "$inputfile:$prop_linenb: Error java property key `$found->[0]' found at  at line $found->[1] translates to same Texinfo flag $propname as java property key `$prop_key' found at this line. Second definition omitted";
							}
						}
						$flagnb --;
						$error_count ++;
					}
					else
					{
						say "\@set $prefix$propname " , jp2texif_encode( jp2texif_unescape($propval));
						$flag_duplication{$propname} = [$prop_key,  $prop_linenb];
					}
				}
			}
			else
			{
				die "$inputfile:$linenb: Invalid line = $line";
			}
		}
		elsif($folded_line == 1)
		{
			$nextpropval = $2;
			if($nextpropval =~ m!(\\+)$! && (length($1) & 1) == 1)
			{
				# nombre impair de contre-obliques en fin de ligne, on reste en repliement
				$nextpropval =~ s!.$!!;
				$propval = $propval . jp2texif_unescape($nextpropval);
			}
			else
			{
				# le repliement est fini
				$folded_line = 0;
				$propval = $propval . jp2texif_unescape($nextpropval);
				say "\@set $prefix$propname " , jp2texif_encode($propval);
				$flagnb ++;
			}

		}
	    else
		{
			die "$inputfile:$linenb: javaprop2texiflag INTERNAL BUG";
		}

		next LINE;
	}
	else
	{
		die "$inputfile:$linenb: Invalid line = $line";
	}
}


if(!$quiet)
{
	if($inputfile)
	{
		$inputfile = "file \`$inputfile\'";
	}
	else
	{
		$inputfile = "standard input";
	}
	say STDOUT "\nDone: javaprop2texiflag is finished,";
	if($verbose)
	{
		use Date::Calc qw(Today_and_Now);
		my @today_now = Today_and_Now();
		printf STDOUT "\trun on %02d-%02d-%02dT%02d:%02d:%02d\n" , @today_now;
	}
	print STDOUT "\tinput was $inputfile,\n\t$linenb lines were processed,\n\t$flagnb \@set-tings were produced.\n\t$error_count error(s)\n";
}

exit -$error_count;
