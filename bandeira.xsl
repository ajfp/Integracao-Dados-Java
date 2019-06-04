<?xml version="1.0" encoding="UTF-8"?>	
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	
    xmlns:xs="http://www.w3.org/2001/XMLSchema"	
    exclude-result-prefixes="xs"	
    version="1.0">	
    <xsl:output method="html"></xsl:output>	
	
    <xsl:template match="Paises">	
        <html><body>	
                <h2>Bandeiras</h2>	
				<table border="1">
				<tr bgcolor="#9acd32">
				<th>Nome País:</th><th>Bandeira:</th></tr>	
                <xsl:for-each select="Pais"> 
				<tr>			
					<td width="100"> <xsl:value-of select="./Nome"/>	</td>
                    <td width="100"> <img>	
                <xsl:attribute name="src">	
                    <xsl:value-of select="./Link_da_Bandeira"/>	
                </xsl:attribute>	
            </img>		</td>
                </tr> 
 </xsl:for-each> 
 </table> 
  </body></html>
  </xsl:template> 
  </xsl:stylesheet> 