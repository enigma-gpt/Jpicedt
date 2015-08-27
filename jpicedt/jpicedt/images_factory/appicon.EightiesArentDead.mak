# -*- mode: makefile -*-

define JPICEDT_ICO
 -size 960x960 \
 xc:transparent \
 -fill white \
 -draw 'rectangle 385,384,960,960'\
 -draw 'rectangle 0,0,280,820'\
 -strokewidth 48 \
 -stroke 'rgb(85,131,157)' \
 -draw 'arc 0,48 280,96 5,175'\
 -draw 'arc 0,168,280,216 5,175'\
 -draw 'arc 0,288,280,336 5,175'\
 -strokewidth 0 \
 -fill red -draw 'rectangle 280,0,465,470'\
 -fill green -draw 'rectangle 465,0,650,470'\
 -fill blue -draw 'rectangle 650,0,845,470'\
 -pointsize 300 \
 -font VTC-JoeleneHand-Regular-Italic \
 -fill white  \
 -draw 'rotate -15 scale 2,3 text -30,188 j' \
 -draw 'rotate -17.5 scale 2,3 text -35,188 j' \
 -draw 'rotate -20 scale 2,3 text -40,188 j' \
 -pointsize 160 \
 -font Arial-Black \
 -fill cyan \
 -draw 'scale 1.05,3 text 291,120 P'\
 -fill  magenta \
 -draw 'scale 1.05,3 text 501,120 i'\
 -fill yellow  \
 -draw 'scale 1.05,3 text 673,120 c'\
 -repage +12.5+25 \
 \( xc:transparent \
 -pointsize 300 \
 -font VTC-JoeleneHand-Regular-Italic \
 -fill 'rgb(232,136,36)'  \
 -draw 'rotate -13 scale 2,3 text -30,188 j' \
 -draw 'rotate -15.5 scale 2,3 text -35,188 j' \
 -draw 'rotate -18 scale 2,3 text -40,188 j' \
 -pointsize 160 \
 -strokewidth 0 \
 -font Arial-Black \
 -fill white  \
 -draw 'scale 1.05,3 text 291,120 P'\
 -draw 'scale 1.05,3 text 501,120 i'\
 -draw 'scale 1.05,3 text 673,120 c'\
 -font Verdana-Bold \
 -fill black \
 -draw 'scale 1.05,3 text 402,281 E'\
 -draw 'scale 1.05,3 text 593,281 d'\
 -draw 'scale 1.05,3 text 785,281 t'\
 \) -composite \
 -resize 48x48
endef

$(OUT):
	convert $(JPICEDT_ICO) $@
