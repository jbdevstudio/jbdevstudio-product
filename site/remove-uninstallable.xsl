<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

<xsl:template match="/">
	<xsl:apply-templates select="*"/>
</xsl:template>


<xsl:template match="*">
	<xsl:copy >
		<xsl:for-each select="@*">
			<xsl:copy />
		</xsl:for-each>
		<xsl:apply-templates />
	</xsl:copy>
</xsl:template>

<!-- remove uninstallable features/plugins based on their names/descriptions 
<properties>
<property name="df_LT.bundleName" value="... do not install ..."/>
<property name="df_LT.featureName" value="... do not install ..."/>
<property name="df_LT.description" value="... do not install ..."/>
</properties>
-->
<xsl:template match="unit[*/property[contains(@value,'do not install')]]" />

</xsl:stylesheet>