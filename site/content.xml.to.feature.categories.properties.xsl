<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:saxon="http://saxon.sf.net/" xmlns="http://www.w3.org/1999/xhtml"
	extension-element-prefixes="saxon"><xsl:output method="text" indent="no" /><xsl:template match="//units"># This file lists categories by feature in the site.
#

<!-- generate EMPTY *.categories = [list of categories per feature] -->
<xsl:for-each select="unit"><xsl:sort select="@id" /><xsl:if test="contains(@id,'.feature.jar')">
	<xsl:variable name="feat"><xsl:value-of select="substring-before(@id,'.feature.jar')"/></xsl:variable>
	<xsl:value-of select="$feat" />.categories = <xsl:for-each select="//unit"><xsl:if test="./properties/property[@name='org.eclipse.equinox.p2.type.category']"><xsl:variable name="catg"><xsl:value-of select="substring-after(./@id,'.')"/></xsl:variable><xsl:for-each select="requires/required"><xsl:if test="contains(@name,$feat)"> | <xsl:value-of select="$catg" /></xsl:if></xsl:for-each></xsl:if></xsl:for-each>
#
</xsl:if></xsl:for-each>

<!-- generate *.category.features = [list of features per category] -->
<!--
<xsl:for-each select="unit"><xsl:if test="./properties/property[@name='org.eclipse.equinox.p2.type.category']">
<xsl:for-each select="./provides/provided">
<xsl:value-of select="substring-after(@name,'.')" />.category.features = \
<xsl:for-each select="../../requires/required"><xsl:value-of select="substring-before(@name,'.feature.group')" /><xsl:if test="not(position()=last())">,\
</xsl:if>
</xsl:for-each>
#
</xsl:for-each></xsl:if></xsl:for-each>
-->

</xsl:template></xsl:stylesheet>
